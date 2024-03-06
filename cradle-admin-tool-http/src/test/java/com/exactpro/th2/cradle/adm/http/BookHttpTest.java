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

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.BookToAdd;
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookHttpTest extends AbstractHttpTest{

    @Test
    public void addBookTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "test_01_underscores";
        HttpTester.Response response = this.executeGet("/new-book?book-name=" + bookName);
        assertEquals(200, response.getStatus());
        PlainResponse plainResponse = new PlainResponse(response.getContent());
        assertEquals("Success", plainResponse.status);
        assertTrue(plainResponse.comment.startsWith("Book created name = " + bookName));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        assertNotNull(this.storage.getBook(new BookId(bookName)));
    }

    @Test
    public void addBookTestWithParams() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "test_02_underscores";
        String pageName = "PAGE1";
        String fullName = "FILL_BOOK_NAME";
        String desc = "description0001";
        Instant time = Instant.now();
        HttpTester.Response response = this.executeGet(String.format("/new-book?book-name=%s&created-time=%s" +
                "&first-page-name=%s&full-name=%s&desc=%s", bookName, time.toString(), pageName, fullName, desc));
        assertEquals(200, response.getStatus());
        PlainResponse plainResponse = new PlainResponse(response.getContent());
        assertEquals("Success", plainResponse.status);
        assertTrue(plainResponse.comment.startsWith("Book created name = " + bookName));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        BookInfo createdBook = this.storage.getBook(new BookId(bookName));
        assertNotNull(createdBook);
        assertEquals(time.truncatedTo(MILLIS), createdBook.getCreated());
        assertEquals(fullName, createdBook.getFullName());
        assertEquals(desc, createdBook.getDesc());
        assertEquals(0, createdBook.getPages().size());
    }

    @Test
    public void testEscapingSymbols() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "test_03_underscores";
        String pageName = "PAG%$%_=dE1";
        String fullName = "FILL   ;596-*/30 ddd.. BOOK_NAME";
        String desc = "descriptor333345678900987635i><\ton0dd001";
        Instant time = Instant.now();
        String url = String.format("/new-book?book-name=%s&created-time=%s" +
                        "&first-page-name=%s&full-name=%s&desc=%s",
                bookName, time.toString(),
                URLEncoder.encode(pageName, StandardCharsets.UTF_8),
                URLEncoder.encode(fullName, StandardCharsets.UTF_8),
                URLEncoder.encode(desc, StandardCharsets.UTF_8));

        HttpTester.Response response = this.executeGet(url);
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true,"Book created name = " + bookName);
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        BookInfo createdBook = this.storage.getBook(new BookId(bookName));
        assertNotNull(createdBook);
        assertEquals(time.truncatedTo(MILLIS), createdBook.getCreated());
        assertEquals(fullName, createdBook.getFullName());
        assertEquals(desc, createdBook.getDesc());
        assertEquals(0, createdBook.getPages().size());
    }

    @Test
    public void addExistedBook() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "addExistedBook";
        this.addBook(new BookToAdd(bookName, Instant.now()));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());

        HttpTester.Response response = this.executeGet("/new-book?book-name=" + bookName);
        this.checkPlainResponseContains(response.getContent(), false,
                String.format("Query to insert book '%s' was not applied. Probably, book already exists",
                bookName.toLowerCase()));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
    }

}
