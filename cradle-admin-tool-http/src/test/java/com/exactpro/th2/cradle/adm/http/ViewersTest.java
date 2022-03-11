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
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.PageToAdd;
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class ViewersTest extends AbstractHttpTest {

    public static final BookToAdd BOOK_1 = new BookToAdd("book_1", Instant.now().minusSeconds(60*20), "page1");
    static {
        BOOK_1.setDesc("book1-description");
        BOOK_1.setFullName("Book1 full name. Test");
        BOOK_1.setFirstPageComment("Comment123");
    }
    public static final BookToAdd BOOK_2 = new BookToAdd("book_2",
            BOOK_1.getCreated().minusSeconds(60*20), "page2");
    public static final BookToAdd BOOK_3 = new BookToAdd("book_3",
            BOOK_1.getCreated().minusSeconds(60*30), "page3");

    public static final PageToAdd BOOK_3_PAGE = new PageToAdd("page3/2", Instant.now().plusSeconds(60), "comment");


    private void createData() throws Exception {
        addBook(BOOK_1);
        addBook(BOOK_2);
        addBook(BOOK_3);
        addPage(new BookId(BOOK_3.getName()), BOOK_3_PAGE);
        Assertions.assertEquals(3, this.storage.getBooksCount());
    }

    @Test
    public void getBookInfoTest() throws Exception {
        createData();
        HttpTester.Response response = this.executeGet("/get-all-books");
        Assertions.assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        //TODO books are in incorrect order should be fixed in CradleAPI
        String expected = String.format("[{\"bookId\":\"book_2\",\"bookFullName\":null,\"bookDesc\":null," +
                "\"bookCreatedTime\":\"%s\"},{\"bookId\":\"book_1\"," +
                "\"bookFullName\":\"Book1 full name. Test\",\"bookDesc\":\"book1-description\"," +
                "\"bookCreatedTime\":\"%s\"},{\"bookId\":\"book_3\",\"bookFullName\":null," +
                "\"bookDesc\":null,\"bookCreatedTime\":\"%s\"}]", BOOK_2.getCreated(), BOOK_1.getCreated(),
                BOOK_3.getCreated());
        Assertions.assertEquals(expected, content);
    }

    @Test
    public void getAllBooksTest() throws Exception {
        createData();
        HttpTester.Response response = this.executeGet("/get-book-info?book-id=" + BOOK_3.getName());
        Assertions.assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        String expected = String.format("[{\"bookId\":\"book_3\",\"bookFullName\":null,\"bookDesc\":null," +
                        "\"bookCreatedTime\":\"%s\",\"pages\":[{\"pageId\":\"page3\"," +
                        "\"comment\":null,\"started\":\"%s\"," +
                        "\"ended\":\"%s\",\"removed\":null},{\"pageId\":\"page3/2\"," +
                        "\"comment\":\"comment\",\"started\":\"%s\"," +
                        "\"ended\":null,\"removed\":null}]}]", BOOK_3.getCreated(), BOOK_3.getCreated(),
                BOOK_3_PAGE.getStart(), BOOK_3_PAGE.getStart());
        Assertions.assertEquals(expected, content);
    }

    @Test
    public void getBookInfoMultistreamNoPagesTest() throws Exception {
        createData();
        HttpTester.Response response = this.executeGet("/get-book-info?with-pages=false&book-id=" + BOOK_3.getName()
            + "&book-id=" + BOOK_2.getName());
        Assertions.assertEquals(200, response.getStatus());
        String content = response.getContent().trim();
        String expected = String.format("[{\"bookId\":\"book_2\",\"bookFullName\":null,\"bookDesc\":null," +
                        "\"bookCreatedTime\":\"%s\",\"pages\":[]},{\"bookId\":\"book_3\"," +
                        "\"bookFullName\":null,\"bookDesc\":null,\"bookCreatedTime\":\"%s\"," +
                        "\"pages\":[]}]", BOOK_2.getCreated(), BOOK_3.getCreated());
        Assertions.assertEquals(expected, content);
    }

}
