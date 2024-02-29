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
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageToAdd;
import com.exactpro.cradle.utils.CradleStorageException;
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewersTest extends AbstractHttpTest {

    public static final BookToAdd BOOK_1;
    public static final String DEFAULT_REMOVED_TIME = "+292278994-08-17T07:12:55.807Z";

    static {
        BookToAdd book = new BookToAdd("book_1", Instant.now().minusSeconds(60*20));
        book.setDesc("book1-description");
        book.setFullName("Book1 full name. Test");
        book.setFirstPageComment("Comment123");

        BOOK_1 = book;
    }
    public static final BookToAdd BOOK_2 = new BookToAdd("book_2",
            BOOK_1.getCreated().minusSeconds(60*20));
    public static final BookToAdd BOOK_3 = new BookToAdd("book_3",
            BOOK_1.getCreated().minusSeconds(60*30));

    public static final PageToAdd BOOK_3_PAGE = new PageToAdd("page3/2", Instant.now().plusSeconds(60), "comment");

    @BeforeAll
    public static void createData(CradleManager manager) throws CradleStorageException, IOException {
        CradleStorage storage = manager.getStorage();
        storage.addBook(BOOK_1);
        storage.addBook(BOOK_2);
        storage.addBook(BOOK_3);
        storage.addPage(new BookId(BOOK_3.getName()), BOOK_3_PAGE.getName(), BOOK_3_PAGE.getStart(), BOOK_3_PAGE.getComment());
    }

    @Test
    public void getBookInfoTest() throws Exception {
        HttpTester.Response response = this.executeGet("/get-all-books");
        assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        //TODO books are in incorrect order should be fixed in CradleAPI
        String expected = String.format("[{" +
                    "\"bookId\":\"book_2\"," +
                    "\"bookFullName\":null," +
                    "\"bookDesc\":null," +
                    "\"bookCreatedTime\":\"%s\"" +
                "},{" +
                    "\"bookId\":\"book_1\"," +
                    "\"bookFullName\":\"Book1 full name. Test\"," +
                    "\"bookDesc\":\"book1-description\"," +
                    "\"bookCreatedTime\":\"%s\"" +
                "},{" +
                    "\"bookId\":\"book_3\"," +
                    "\"bookFullName\":null," +
                    "\"bookDesc\":null,\"" +
                    "bookCreatedTime\":\"%s\"" +
                "}]",
                BOOK_2.getCreated().truncatedTo(MILLIS),
                BOOK_1.getCreated().truncatedTo(MILLIS),
                BOOK_3.getCreated().truncatedTo(MILLIS));
        assertEquals(expected, content);
    }

    @Test
    public void getAllBooksTest() throws Exception {
        HttpTester.Response response = this.executeGet("/get-book-info?book-id=" + BOOK_3.getName());
        assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        String expected = String.format("[{" +
                    "\"bookId\":\"book_3\"," +
                    "\"bookFullName\":null," +
                    "\"bookDesc\":null," +
                    "\"bookCreatedTime\":\"%s\"," +
                    "\"pages\":[{" +
                        "\"pageId\":\"page3/2\"," +
                        "\"comment\":\"comment\"," +
                        "\"started\":\"%s\"," +
                        "\"ended\":null," +
                        "\"updated\":\"%s\"," +
                        "\"removed\":\"%s\"" +
                    "}]" +
                "}]",
                BOOK_3.getCreated().truncatedTo(MILLIS),
                BOOK_3_PAGE.getStart(),
                BOOK_3_PAGE.getStart().truncatedTo(MILLIS),
                DEFAULT_REMOVED_TIME
        );
        assertEquals(expected, content);
    }

    @Test
    public void getBookInfoMultiStreamNoPagesTest() throws Exception {
        HttpTester.Response response = this.executeGet("/get-book-info?with-pages=false&book-id=" + BOOK_3.getName()
            + "&book-id=" + BOOK_2.getName());
        assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        String expected = String.format("[{" +
                    "\"bookId\":\"book_2\"," +
                    "\"bookFullName\":null," +
                    "\"bookDesc\":null," +
                    "\"bookCreatedTime\":\"%s\"," +
                    "\"pages\":[]" +
                "},{" +
                    "\"bookId\":\"book_3\"," +
                    "\"bookFullName\":null," +
                    "\"bookDesc\":null," +
                    "\"bookCreatedTime\":\"%s\"," +
                    "\"pages\":[]" +
                "}]",
                BOOK_2.getCreated().truncatedTo(MILLIS),
                BOOK_3.getCreated().truncatedTo(MILLIS));
        assertEquals(expected, content);
    }

    @Test
    public void getBookInfoWithPagesTest() throws Exception {
        HttpTester.Response response = this.executeGet("/get-book-info?with-pages=true&book-id=" + BOOK_3.getName()
                + "&book-id=" + BOOK_2.getName());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void getBookInfoWithRemovedPagesTest() throws Exception {
        HttpTester.Response response = this.executeGet("/get-book-info?with-pages=true&load-removed-pages" +
                "&book-id=" + BOOK_3.getName() + "&book-id=" + BOOK_2.getName());
        assertEquals(200, response.getStatus());
    }
}
