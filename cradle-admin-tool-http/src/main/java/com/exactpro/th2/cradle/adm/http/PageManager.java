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
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageManager implements AutoCloseable, Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final String AUTO_PAGE_COMMENT = "auto-page";

    private final CradleStorage storage;
    private final long pageActionRejectionThreshold;
    private final ScheduledExecutorService executorService;
    private final Map<String, AutoPageInfo> books;

    public PageManager(
            CradleStorage storage,
            Map<String, AutoPageConfiguration> autoPages,
            int pageRecheckInterval,
            long pageActionRejectionThreshold
    ) throws CradleStorageException {
        if (autoPages == null || autoPages.isEmpty()) {
            logger.info("auto-page configuration is not provided, pages will not be generated automatically");
            this.storage = null;
            this.pageActionRejectionThreshold = 0;
            this.executorService = null;
            this.books = Collections.emptyMap();
            return;
        }

        this.storage = storage;
        this.pageActionRejectionThreshold = pageActionRejectionThreshold;

        books = new HashMap<>();
        for (String bookName : autoPages.keySet()) {
            books.put(bookName, new AutoPageInfo(autoPages.get(bookName), storage.refreshBook(bookName)));
        }

        logger.info("Managing pages for books {} every {} sec", books.keySet().toArray(), pageRecheckInterval);
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this, 0, pageRecheckInterval, TimeUnit.SECONDS);
    }


    private BookInfo checkBook(BookInfo book, AutoPageConfiguration autoPageConfiguration) throws Exception {
        Instant pageStartBase = autoPageConfiguration.getPageStartTime();
        Duration pageDuration = autoPageConfiguration.getPageDuration();
        Instant nowPlusThreshold = Instant.now().plusMillis(pageActionRejectionThreshold);
        long nowMillis =  nowPlusThreshold.toEpochMilli();

        PageInfo pageInfo = book.getLastPage();

        if (pageInfo == null) {
            return storage.addPage(book.getId(), "auto-page-" + nowMillis, nowPlusThreshold, AUTO_PAGE_COMMENT);
        }

        Instant lastPageStart = pageInfo.getStarted();
        if (lastPageStart.isBefore(nowPlusThreshold)) {
            int comparison = nowPlusThreshold.compareTo(pageStartBase);
            if (comparison < 0) {
                return storage.addPage(book.getId(), "auto-page-" + nowMillis, pageStartBase, AUTO_PAGE_COMMENT);
            } else if (comparison > 0) {
                Duration diff = Duration.between(pageStartBase, nowPlusThreshold);
                Instant nextMark = pageStartBase.plus(pageDuration.multipliedBy(diff.dividedBy(pageDuration) + 1));
                return storage.addPage(book.getId(), "auto-page-" + nowMillis, nextMark, AUTO_PAGE_COMMENT);
            } else {
                return storage.addPage(book.getId(), "auto-page-" + nowMillis, pageStartBase.plus(pageDuration), AUTO_PAGE_COMMENT);
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
                logger.warn("Executor can't stop during 5 seconds, " + tasks + " tasks that never commenced execution");
            }
        }
    }

    @Override
    public void run() {
        books.forEach((bookName, autoPageInfo) -> {
            try {
                autoPageInfo.setBookInfo(checkBook(autoPageInfo.getBookInfo(), autoPageInfo.getAutoPageConfiguration()));
            } catch (Exception e) {
                logger.error("Exception processing book {}", bookName, e);
            }
        });
    }
}
