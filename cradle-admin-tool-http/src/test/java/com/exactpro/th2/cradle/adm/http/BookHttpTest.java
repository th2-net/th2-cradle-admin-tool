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
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class BookHttpTest extends AbstractHttpTest{

    @Test
    public void addBookTest() throws Exception {
        Assertions.assertEquals(0, this.storage.getBooksCount());
        HttpTester.Response response = this.executeGet("/new-book?book-name=test_01_underscores");
        Assertions.assertEquals(200, response.getStatus());
        PlainResponse plainResponse = new PlainResponse(response.getContent());
        Assertions.assertEquals("Success", plainResponse.status);
        Assertions.assertTrue(plainResponse.comment.startsWith("Book created name = test_01_underscores"));
        Assertions.assertEquals(1, this.storage.getBooksCount());
        Assertions.assertNotNull(this.storage.getBook(new BookId("test_01_underscores")));
    }

    @Test
    public void addBookTestWithParams() throws Exception {
        Assertions.assertEquals(0, this.storage.getBooksCount());
        String bookName = "test_01_underscores";
        String pageName = "PAGE1";
        String fullname = "FILLBOOKNAME";
        String desc = "description0001";
        Instant time = Instant.now();
        HttpTester.Response response = this.executeGet(String.format("/new-book?book-name=%s&created-time=%s" +
                "&first-page-name=%s&full-name=%s&desc=%s", bookName, time.toString(), pageName, fullname, desc));
        Assertions.assertEquals(200, response.getStatus());
        PlainResponse plainResponse = new PlainResponse(response.getContent());
        Assertions.assertEquals("Success", plainResponse.status);
        Assertions.assertTrue(plainResponse.comment.startsWith("Book created name = test_01_underscores"));
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookInfo createdBook = this.storage.getBook(new BookId("test_01_underscores"));
        Assertions.assertNotNull(createdBook);
        Assertions.assertEquals(time, createdBook.getCreated());
        Assertions.assertEquals(fullname, createdBook.getFullName());
        Assertions.assertEquals(desc, createdBook.getDesc());
        Assertions.assertEquals(0, createdBook.getPages().size());
        //Assertions.assertEquals(pageName, createdBook.getFirstPage().getId().getName());
    }

    @Test
    public void testEscapingSymbols() throws Exception {
        Assertions.assertEquals(0, this.storage.getBooksCount());
        String bookName = "test_01_underscores";
        String pageName = "PAG%$%_=dE1";
        String fullname = "FILL   ;596-*/30 ddd.. BOOKNAME";
        String desc = "descript333345678900987635i><\ton0dd001";
        Instant time = Instant.now();
        String url = String.format("/new-book?book-name=%s&created-time=%s" +
                        "&first-page-name=%s&full-name=%s&desc=%s",
                bookName, time.toString(),
                URLEncoder.encode(pageName, StandardCharsets.UTF_8),
                URLEncoder.encode(fullname, StandardCharsets.UTF_8),
                URLEncoder.encode(desc, StandardCharsets.UTF_8));

        HttpTester.Response response = this.executeGet(url);
        PlainResponse plainResponse = new PlainResponse(response.getContent());
        Assertions.assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true,"Book created name = test_01_underscores");
        Assertions.assertEquals(1, this.storage.getBooksCount());
        BookInfo createdBook = this.storage.getBook(new BookId("test_01_underscores"));
        Assertions.assertNotNull(createdBook);
        Assertions.assertEquals(time, createdBook.getCreated());
        Assertions.assertEquals(fullname, createdBook.getFullName());
        Assertions.assertEquals(desc, createdBook.getDesc());
        Assertions.assertEquals(0, createdBook.getPages().size());
        //Assertions.assertEquals(pageName, createdBook.getFirstPage().getId().getName());
    }

    @Test
    public void addExistedBook() throws Exception {

        String bookName = "testBook";
        this.addBook(new BookToAdd(bookName, Instant.now()));
        Assertions.assertEquals(1, this.storage.getBooksCount());

        HttpTester.Response response = this.executeGet("/new-book?book-name=" + bookName);
        this.checkPlainResponseContains(response.getContent(), false, String.format("Book '%s' is already present in storage",
                bookName.toLowerCase()));
        Assertions.assertEquals(1, this.storage.getBooksCount());
    }

}
