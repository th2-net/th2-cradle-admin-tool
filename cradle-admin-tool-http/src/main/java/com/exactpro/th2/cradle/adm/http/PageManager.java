/*******************************************************************************
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
 ******************************************************************************/

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PageManager implements AutoCloseable, Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Map<String, Duration> autoPages;
    private CradleStorage storage;
    private ScheduledExecutorService executorService;
    private Map<String, BookInfo> books;

    public PageManager(CradleStorage storage, Map<String, Duration> autoPages, int pageRecheckInterval) throws CradleStorageException {
        if (autoPages == null || autoPages.isEmpty()) {
            logger.info("auto-page configuration is not provided, pages will not be generated automatically");
            return;
        }

        this.storage = storage;
        this.autoPages = autoPages;


        books = new HashMap<>();
        for (String bookName : autoPages.keySet()) {
            books.put(bookName, storage.refreshBook(bookName));
        }

        logger.info("Managing pages for books {} every {} sec", books.keySet().toArray(), pageRecheckInterval);
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this, 0, pageRecheckInterval, TimeUnit.SECONDS);
    }


    private BookInfo checkBook(BookInfo book, Duration pageDuration) throws Exception {

        long pageDurationMillis = pageDuration.toMillis();
        Instant now = Instant.now();
        long nowMillis =  now.toEpochMilli();

        PageInfo pageInfo = book.getLastPage();

        if (pageInfo == null) {
            return storage.addPage(book.getId(), "auto-page-" + nowMillis, now, null);
        }

        long nextMark = (nowMillis + pageDurationMillis - 1) / pageDurationMillis * pageDurationMillis;
        if (pageInfo.getStarted().isBefore(now)) {
            return storage.addPage(book.getId(), "auto-page-" + nowMillis, Instant.ofEpochMilli(nextMark), null);
        }

        return book;
    }

    @Override
    public void close() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void run() {

        for (String book: books.keySet()) {
            try {
                books.put(book, checkBook(books.get(book), autoPages.get(book)));
            } catch (Exception e) {
                logger.error("Exception processing book {}", book, e);
            }
        }
    }
}