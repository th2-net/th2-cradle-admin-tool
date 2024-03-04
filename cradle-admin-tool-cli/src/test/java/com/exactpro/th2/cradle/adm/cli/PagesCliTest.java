/*
 * Copyright 2022-2024 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.cli;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.test.annotations.Th2AppFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class PagesCliTest extends AbstractCliTest {

    @Test
    public void addPageBeforeTest(@Th2AppFactory CommonFactory appFactory,
                                  CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addPageBeforeTest";
        BookId bookId = new BookId(bookName);
        String pageName = "addPageBeforeTest";
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());
        assertSame(bookInfo, cradleStorage.addPage(bookId, pageName, Instant.now().plusSeconds(30), "test"));
        assertEquals(1, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{"-c=stub/", "--page",
                        "-pageName", "page123",
                        "-bookId", bookName,
                        "-pageStart", Instant.parse("2022-01-22T00:10:00Z").toString()},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        assertEquals(1, cradleStorage.getBook(bookId).getPages().size());
        checkOutput(false, "You can only create pages which start more than");
    }

    @Test
    public void addPageCurrentTimeTest(@Th2AppFactory CommonFactory appFactory,
                                       CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addPageCurrentTimeTest";
        BookId bookId = new BookId(bookName);
        String pageName = "addPageCurrentTimeTest";
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());
        assertSame(bookInfo, cradleStorage.addPage(bookId, pageName, Instant.now().plusSeconds(30), "test"));
        assertEquals(1, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{"-c=stub/", "--page",
                        "-pageName", "page123",
                        "-bookId", bookName,
                        "-pageStart", Instant.now().toString()},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        assertEquals(1, bookInfo.getPages().size());
        checkOutput(false, "You can only create pages which start more than");
    }

    @Test
    public void addPageCorrectTest(@Th2AppFactory CommonFactory appFactory,
                                   CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addPageCorrectTest";
        BookId bookId = new BookId(bookName);
        String pageName = "addPageCorrectTest";
        Instant pageStart = Instant.now().plus(1, ChronoUnit.MINUTES);
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{"-c=stub/", "--page",
                        "-pageName", pageName,
                        "-bookId", bookName,
                        "-pageStart", pageStart.toString()},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        assertEquals(1, bookInfo.getPages().size());
        PageInfo newPage = bookInfo.getPage(new PageId(bookId, pageStart, pageName));
        assertNotNull(newPage);
        assertNull(newPage.getEnded());

        checkOutput(true, null);
    }

    @Test
    public void addPageWithoutNameTest(@Th2AppFactory CommonFactory appFactory,
                                      CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addPageWithoutNameTest";
        BookId bookId = new BookId(bookName);
        Instant pageStart = Instant.now().plus(1, ChronoUnit.MINUTES);
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{
                        "-c=stub/", "--page",
                        "-bookId", bookName,
                        "-pageStart", pageStart.toString()},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        assertEquals(1, bookInfo.getPages().size());
        PageInfo lastPage = bookInfo.getLastPage();
        assertNotNull(lastPage);
        assertTrue(UUID_REGEX.matcher(lastPage.getName()).find());

        checkOutput(true, null);
    }

    @Test
    public void addPageWithParamsTest(@Th2AppFactory CommonFactory appFactory,
                                      CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addPageWithParamsTest";
        BookId bookId = new BookId(bookName);
        String pageName = "addPageWithParamsTest";
        String pageComment = "This PAGE is created in addPageWithParamsTest for unit test purposes";
        Instant pageStart = Instant.now().plus(20, ChronoUnit.MINUTES);
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{"-c=stub/", "--page",
                        "-bookId", bookName,
                        "-pageStart", pageStart.toString(),
                        "-pageName", pageName,
                        "-pageComment", pageComment},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        PageInfo createdPage = bookInfo.getPage(new PageId(bookId, pageStart, pageName));
        assertEquals(pageName, createdPage.getName());
        assertEquals(pageComment, createdPage.getComment());
        assertNull(createdPage.getEnded());
        assertEquals(pageStart, createdPage.getStarted());

        checkOutput(true, null);
    }

    @Test
    public void addExistedPageTest(@Th2AppFactory CommonFactory appFactory,
                                   CradleManager manager) throws Exception {
        CradleStorage cradleStorage = manager.getStorage();

        String bookName = "addExistedPageTest";
        BookId bookId = new BookId(bookName);
        String pageName = "addExistedPageTest";
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertNotNull(bookInfo);
        assertEquals(0, cradleStorage.getBook(bookId).getPages().size());
        assertSame(bookInfo, cradleStorage.addPage(bookId, pageName, Instant.now().plusSeconds(30), "test"));
        assertEquals(1, cradleStorage.getBook(bookId).getPages().size());

        Application.run(
                new String[]{"-c=stub/", "--page",
                        "-pageName", pageName,
                        "-bookId", bookName,
                        "-pageStart", Instant.now().plus(3, ChronoUnit.MINUTES).toString()},
                args -> appFactory);

        cradleStorage.refreshBook(bookName);
        assertEquals(1, bookInfo.getPages().size());
        checkOutput(false, String.format(
                "Query to insert page '%s' book '%s' was not applied. Probably, page already exists",
                pageName,
                bookName.toLowerCase()));
    }

}
