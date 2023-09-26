/*
* Copyright 2021-2023 Exactpro (Exactpro Systems Limited)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class PageManager implements AutoCloseable, Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(PageManager.class);
    static final String AUTO_PAGE_COMMENT = "auto-page";
    static final String AUTO_BOOK_DESCRIPTION = "auto-book";

    private final CradleStorage storage;
    private final long pageActionRejectionThreshold;
    private final ScheduledExecutorService executorService;
    private final Map<String, AutoPageInfo> books;

    public PageManager(
            CradleStorage storage,
            boolean autoBooks,
            Map<String, AutoPageConfiguration> autoPages,
            int pageRecheckInterval,
            long pageActionRejectionThreshold
    ) {
        if (autoPages == null || autoPages.isEmpty()) {
            LOGGER.info("auto-page configuration is not provided, pages will not be generated automatically");
            this.storage = null;
            this.pageActionRejectionThreshold = 0;
            this.executorService = null;
            this.books = Collections.emptyMap();
            return;
        }

        Map<String, String> normalisedBookName = autoPages.keySet().stream()
                .collect(Collectors.toMap(
                        PageManager::normaliseBookName,
                        Function.identity()
                ));

        if (normalisedBookName.size() != autoPages.size()) {
            throw new IllegalArgumentException("Some of books have the same name after normalization" +
                    ", origin: " + autoPages.keySet() +
                    ", normalized: " + normalisedBookName.keySet());
        }

        books = normalisedBookName.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> new AutoPageInfo(autoPages.get(entry.getValue()), getOrCreateBook(storage, entry.getKey(), autoBooks))
                ));

        this.storage = storage;
        this.pageActionRejectionThreshold = pageActionRejectionThreshold;

        LOGGER.info("Managing pages for books {} every {} sec", books.keySet().toArray(), pageRecheckInterval);
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this, 0, pageRecheckInterval, TimeUnit.SECONDS);
    }

    @NotNull
    private static String normaliseBookName(String origin) {
        String bookName = lowerCase(trim(origin));
        if (bookName == null || bookName.isEmpty()) {
            throw new IllegalArgumentException("One of book is null or empty");
        }
        return bookName;
    }


    private BookInfo checkBook(BookInfo book, AutoPageConfiguration autoPageConfiguration) throws Exception {
        Instant pageStartBase = autoPageConfiguration.getPageStartTime();
        Duration pageDuration = autoPageConfiguration.getPageDuration();
        Instant nowPlusThreshold = Instant.now().plusMillis(pageActionRejectionThreshold);

        PageInfo pageInfo = book.getLastPage();

        if (pageInfo == null) {
            return createAutoPage(storage, book, nowPlusThreshold);
        }

        Instant lastPageStart = pageInfo.getStarted();
        if (lastPageStart.isBefore(nowPlusThreshold)) {
            int comparison = nowPlusThreshold.compareTo(pageStartBase);
            if (comparison < 0) {
                return createAutoPage(storage, book, pageStartBase);
            } else if (comparison > 0) {
                Duration diff = Duration.between(pageStartBase, nowPlusThreshold);
                Instant nextMark = pageStartBase.plus(pageDuration.multipliedBy(diff.dividedBy(pageDuration) + 1));
                return createAutoPage(storage, book, nextMark);
            } else {
                return createAutoPage(storage, book, pageStartBase.plus(pageDuration));
            }
        }

        return book;
    }

    @Override
    public void close() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                List<Runnable> tasks = executorService.shutdownNow();
                LOGGER.warn("Executor can't stop during 5 seconds, " + tasks + " tasks that never commenced execution");
            }
        }
    }

    @Override
    public void run() {
        books.forEach((bookName, autoPageInfo) -> {
            try {
                autoPageInfo.setBookInfo(checkBook(autoPageInfo.getBookInfo(), autoPageInfo.getAutoPageConfiguration()));
            } catch (Exception e) {
                LOGGER.error("Exception processing book {}", bookName, e);
            }
        });
    }

    private static BookInfo getOrCreateBook(@NotNull CradleStorage storage, String bookName, boolean autoBook) {
        try {
            BookInfo storageBookInfo = storage.refreshBook(bookName);

            if (storageBookInfo != null) {
                return storageBookInfo;
            }

            if (!autoBook) {
                throw new IllegalStateException("Storage doesn't contain the '" + bookName + "' book. Create book manually or enable auto-book functionality in configuration");
            }

            BookToAdd bookToAdd = new BookToAdd(
                    bookName,
                    Instant.now().minus(1, ChronoUnit.DAYS)
            );
            bookToAdd.setFullName(bookName);
            bookToAdd.setDesc(AUTO_BOOK_DESCRIPTION);

            BookInfo bookInfo = storage.addBook(bookToAdd);

            LOGGER.info("Created '{}' book, time: {}, full name: {}, description: {}",
                    bookName,
                    bookToAdd.getCreated(),
                    bookToAdd.getFullName(),
                    bookToAdd.getDesc());

            createAutoPage(storage, bookInfo, bookInfo.getCreated());
            LOGGER.info("Added first page, book: {}, time: {}", bookInfo.getId().getName(), bookInfo.getCreated());
            return bookInfo;
        } catch (Exception e) {
            throw new RuntimeException("Book with name '" + bookName + "' can't be created", e);
        }
    }

    private static BookInfo createAutoPage(CradleStorage storage, BookInfo book, Instant nowPlusThreshold) throws CradleStorageException, IOException {
        long nowMillis =  nowPlusThreshold.toEpochMilli();
        return storage.addPage(book.getId(), "auto-page-" + nowMillis, nowPlusThreshold, AUTO_PAGE_COMMENT);
    }
}
