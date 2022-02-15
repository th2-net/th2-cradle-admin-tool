/*******************************************************************************
 * Copyright 2022 Exactpro (Exactpro Systems Limited)
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

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.PageToAdd;
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class PagesHttpTest extends AbstractHttpTest {

    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$");

    @Test
    public void addPageTest() throws Exception {

        String bookId = "testBook";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookId, Instant.now(), "page1"));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookId, pageName, time.toString()));
        Assertions.assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookId, pageName, time));
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookId bookIdkey = new BookId(bookId);
        BookInfo bookIdObj = this.storage.getBook(bookIdkey);
        Assertions.assertNotNull(bookIdObj.getPage(new PageId(bookIdkey, pageName)));
    }

    @Test
    public void addPageTestMillis() throws Exception {

        String bookId = "testBook";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookId, Instant.now(), "page1"));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookId, pageName, time.toEpochMilli()));
        Assertions.assertEquals(200, response.getStatus());
        Instant instant = Instant.ofEpochMilli(time.toEpochMilli()); // reset nanos
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookId, pageName, instant));
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookId bookIdkey = new BookId(bookId);
        BookInfo bookIdObj = this.storage.getBook(bookIdkey);
        Assertions.assertNotNull(bookIdObj.getPage(new PageId(bookIdkey, pageName)));
    }

    @Test
    public void addPageWithNoNameTest() throws Exception {

        String bookId = "testBook";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookId, Instant.now(), "page1"));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-start=%s",
                bookId, time.toString()));
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookId bookIdkey = new BookId(bookId);
        BookInfo bookIdObj = this.storage.getBook(bookIdkey);
        Assertions.assertEquals(2, bookIdObj.getPages().size());
        String pageName = bookIdObj.findPage(time).getId().getName();
        this.checkPlainResponseContains(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookId, pageName, time));
        Assertions.assertTrue(UUID_REGEX.matcher(pageName).find());
    }

    @Test
    public void addPageWithParamsTest() throws Exception {

        String bookId = "testBook";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        String comment = "text_comment_1234567890_text_comment_1234567890_text_comment_1234567890_text_comment_1234567890";
        addBook(new BookToAdd(bookId, Instant.now(), "page1"));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s&page-comment=%s",
                bookId, pageName, time.toString(), comment));
        Assertions.assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s,pageComment = %s", bookId, pageName, time, comment));
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookId bookIdkey = new BookId(bookId);
        BookInfo bookIdObj = this.storage.getBook(bookIdkey);
        PageInfo page = bookIdObj.getPage(new PageId(bookIdkey, pageName));
        Assertions.assertNotNull(page);
        Assertions.assertEquals(time, page.getStarted());
        Assertions.assertNull(page.getEnded());
        Assertions.assertEquals(comment, page.getComment());
    }

    @Test
    public void removePageTest() throws Exception {

        String bookId = "testBook";
        String old_page = "old_page";
        String new_page = "new_page";
        addBook(new BookToAdd(bookId, Instant.now().minus(20, ChronoUnit.SECONDS), old_page));
        addPage(new BookId(bookId), new PageToAdd("new_page", Instant.now().plus(10, ChronoUnit.MILLIS), "should not be deleted in this scenario"));

        BookId bookIdkey = new BookId(bookId);
        BookInfo bookIdObj = this.storage.getBook(bookIdkey);
        Assertions.assertEquals(1, this.storage.getBooksCount());
        Assertions.assertEquals(2, bookIdObj.getPages().size());

        Thread.sleep(30); //to start new page
        HttpTester.Response response = this.executeGet(String.format("/remove-page?book-id=%s&page-name=%s",
                bookId, old_page));
        bookIdObj = this.storage.getBook(bookIdkey);
        Assertions.assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page removed testBook:%s", old_page));
        Assertions.assertEquals(1, bookIdObj.getPages().size());

        Assertions.assertNotNull(bookIdObj.getPage(new PageId(bookIdkey, new_page)));
    }

}
