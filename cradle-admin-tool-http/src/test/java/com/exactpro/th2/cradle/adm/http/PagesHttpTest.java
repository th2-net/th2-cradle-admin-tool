/*
* Copyright 2022-2025 Exactpro (Exactpro Systems Limited)
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
import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.PageToAdd;
import org.eclipse.jetty.http.HttpTester;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.exactpro.cradle.utils.EscapeUtils.escape;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PagesHttpTest extends AbstractHttpTest {

    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$");

    @Test
    public void addPageTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "addPageTest";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageName, time.toString()));
        assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookName.toLowerCase(), pageName, time));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        BookId bookId = new BookId(bookName);
        BookInfo bookIdObj = this.storage.getBook(bookId);
        assertNotNull(bookIdObj.getPage(new PageId(bookId, time, pageName)));
    }

    @Test
    public void addPageTestMillis() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "addPageTestMillis";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MILLIS);
        addBook(new BookToAdd(bookName, Instant.now()));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageName, time.toEpochMilli()));
        assertEquals(200, response.getStatus());
        Instant instant = Instant.ofEpochMilli(time.toEpochMilli()); // reset nanos
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookName.toLowerCase(), pageName, instant));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        BookId bookId = new BookId(bookName);
        BookInfo bookIdObj = this.storage.getBook(bookId);
        assertNotNull(bookIdObj.getPage(new PageId(bookId, time, pageName)));
    }

    @Test
    public void addPageWithNoNameTest() throws Exception {

        String bookName = "addPageWithNoNameTest";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-start=%s",
                bookName, time.toString()));
        assertEquals(200, response.getStatus());
        assertEquals(1, storage.listBooks().size());
        BookId bookId = new BookId(bookName);
        BookInfo bookIdObj = this.storage.getBook(bookId);
        assertEquals(1, bookIdObj.getPages().size());
        String pageName = bookIdObj.findPage(time).getId().getName();
        this.checkPlainResponseContains(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookName.toLowerCase(), pageName, time));
        assertTrue(UUID_REGEX.matcher(pageName).find());
    }

    @Test
    public void addPageWithParamsTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "addPageWithParamsTest";
        String pageName = "page2";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        String comment = "text_comment_1234567890_text_comment_1234567890_text_comment_1234567890_text_comment_1234567890";
        addBook(new BookToAdd(bookName, Instant.now()));

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s&page-comment=%s",
                bookName, pageName, time.toString(), comment));
        assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s,pageComment = %s", bookName.toLowerCase(), pageName, time, comment));
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        BookId bookId = new BookId(bookName);
        BookInfo bookIdObj = this.storage.getBook(bookId);
        PageInfo page = bookIdObj.getPage(new PageId(bookId, time, pageName));
        assertNotNull(page);
        assertEquals(time, page.getStarted());
        assertNull(page.getEnded());
        assertEquals(comment, page.getComment());
    }

    @Test
    public void updatePageCommentTest() throws Exception {
        String bookName = "updatePageCommentTest";
        String pageName = "page2";
        String pageComment = "update-page-comment-test";
        Instant time = Instant.now().minus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        BookId bookId = new BookId(bookName);
        PageId pageId = new PageId(bookId, time, pageName);
        BookInfo bookInfo = this.storage.getBook(bookId);

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageName, time));
        assertEquals(200, response.getStatus());

        bookInfo.refresh();
        PageInfo pageInfo = bookInfo.getPage(pageId);
        assertNotNull(pageInfo);
        assertNull(pageInfo.getComment());

        response = this.executeGet(String.format("/update-page?book-id=%s&page-name=%s&new-page-comment=%s",
                bookName, pageName, pageComment));
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true,
                String.format("Page updated bookId = %s,pageName = %s,pageStart = %s,pageComment = %s,updated = ", bookName.toLowerCase(), pageName, time, pageComment));

        bookInfo.refresh();
        pageInfo = bookInfo.getPage(pageId);
        assertNotNull(pageInfo);
        assertEquals(pageComment, pageInfo.getComment());
    }

    @Test
    public void updatePageNameTest() throws Exception {
        String bookName = "updatePageNameTest";
        String pageNameFirst = "page-first";
        String pageNameSecond = "page-second";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        BookId bookId = new BookId(bookName);
        PageId pageIdFirst = new PageId(bookId, time, pageNameFirst);
        PageId pageIdSecond = new PageId(bookId, time, pageNameSecond);
        BookInfo bookInfo = this.storage.getBook(bookId);

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageNameFirst, time));
        assertEquals(200, response.getStatus());

        bookInfo.refresh();
        assertNotNull(bookInfo.getPage(pageIdFirst));
        assertNull(bookInfo.getPage(pageIdSecond));

        response = this.executeGet(String.format("/update-page?book-id=%s&page-name=%s&new-page-name=%s",
                bookName, pageNameFirst, pageNameSecond));
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true,
                String.format("Page updated bookId = %s,pageName = %s,pageStart = %s,updated = ", bookName.toLowerCase(), pageIdSecond.getName(), time));

        bookInfo.refresh();
        assertNull(bookInfo.getPage(pageIdFirst));
        assertNotNull(bookInfo.getPage(pageIdSecond));
    }

    @Test
    public void updatePageNameInPastTest() throws Exception {
        String bookName = "updatePageNameInPastTest";
        String pageNameFirst = "page-first";
        String pageNameSecond = "page-second";
        Instant time = Instant.now().minus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        BookId bookId = new BookId(bookName);
        PageId pageIdFirst = new PageId(bookId, time, pageNameFirst);
        PageId pageIdSecond = new PageId(bookId, time, pageNameSecond);
        BookInfo bookInfo = this.storage.getBook(bookId);

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageNameFirst, time));
        assertEquals(200, response.getStatus());

        bookInfo.refresh();
        assertNotNull(bookInfo.getPage(pageIdFirst));
        assertNull(bookInfo.getPage(pageIdSecond));

        response = this.executeGet(String.format("/update-page?book-id=%s&page-name=%s&new-page-name=%s",
                bookName, pageNameFirst, pageNameSecond));
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), false,
                String.format("You can only rename pages which start more than 200 ms in future: pageStart - %s, now + threshold - ", time));

        bookInfo.refresh();
        assertNotNull(bookInfo.getPage(pageIdFirst));
        assertNull(bookInfo.getPage(pageIdSecond));
    }

    @Test
    public void updatePageTest() throws Exception {
        String bookName = "updatePageTest";
        String pageNameFirst = "page-first";
        String pageNameSecond = "page-second";
        String pageComment = "update-page-comment-test";
        Instant time = Instant.now().plus(2, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now()));

        BookId bookId = new BookId(bookName);
        PageId pageIdFirst = new PageId(bookId, time, pageNameFirst);
        PageId pageIdSecond = new PageId(bookId, time, pageNameSecond);
        BookInfo bookInfo = this.storage.getBook(bookId);

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageNameFirst, time));
        assertEquals(200, response.getStatus());

        bookInfo.refresh();
        PageInfo pageInfo = bookInfo.getPage(pageIdFirst);
        assertNotNull(pageInfo);
        assertNull(bookInfo.getPage(pageIdSecond));
        assertNull(pageInfo.getComment());

        response = this.executeGet(String.format("/update-page?book-id=%s&page-name=%s&new-page-name=%s&new-page-comment=%s",
                bookName, pageNameFirst, pageNameSecond, pageComment));
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true,
                String.format("Page updated bookId = %s,pageName = %s,pageStart = %s,pageComment = %s,updated = ", bookName.toLowerCase(), pageIdSecond.getName(), time, pageComment));

        bookInfo.refresh();
        pageInfo = bookInfo.getPage(pageIdSecond);
        assertNull(bookInfo.getPage(pageIdFirst));
        assertNotNull(pageInfo);
        assertEquals(pageComment, pageInfo.getComment());
    }

    @Test
    public void removePageTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "removePageTest";
        String old_page = "old_page";
        Instant old_page_start = Instant.now().plus(5, ChronoUnit.MILLIS);
        String new_page = "new_page";
        Instant new_page_start = Instant.now().plus(3, ChronoUnit.MINUTES);
        addBook(new BookToAdd(bookName, Instant.now().minus(20, ChronoUnit.SECONDS)));
        addPage(new BookId(bookName), new PageToAdd(old_page, old_page_start, "should be deleted in this scenario"));
        addPage(new BookId(bookName), new PageToAdd(new_page, new_page_start, "should not be deleted in this scenario"));

        BookId bookId = new BookId(bookName);
        BookInfo bookIdObj = this.storage.getBook(bookId);
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        assertEquals(2, bookIdObj.getPages().size());

        Thread.sleep(30); //to start new page
        HttpTester.Response response = this.executeGet(String.format("/remove-page?book-id=%s&page-name=%s",
                bookName, old_page));
        bookIdObj = this.storage.getBook(bookId);
        assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page removed %s:%s:%s", bookName.toLowerCase(), escape(old_page_start.toString()), old_page));
        assertEquals(1, bookIdObj.getPages().size());

        assertNotNull(bookIdObj.getPage(new PageId(bookId, new_page_start, new_page)));
    }

    @Test
    public void pageGapTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "pageGapTest";
        String pageToRemove = "page2";
        Instant time = Instant.now();
        BookId bookId = new BookId(bookName);

        Instant page1Start = time.plus(10, ChronoUnit.MINUTES);
        Instant page2Start = time.plus(20, ChronoUnit.MINUTES);
        Instant page3Start = time.plus(30, ChronoUnit.MINUTES);

        addBook(new BookToAdd(bookName, time.minus(20, ChronoUnit.SECONDS)));
        addPage(bookId, new PageToAdd("page1", page1Start, null));
        addPage(bookId, new PageToAdd(pageToRemove, page2Start, null));
        addPage(bookId, new PageToAdd("page3", page3Start, null));


        BookInfo bookInfo = this.storage.getBook(bookId);
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());
        assertEquals(3, bookInfo.getPages().size());

        HttpTester.Response response = this.executeGet(String.format("/remove-page?book-id=%s&page-name=%s",
                bookName, pageToRemove));
        this.storage.refreshPages(bookId);
        assertEquals(200, response.getStatus());
        this.checkPlainResponseContains(response.getContent(), true, "Page removed");
        assertEquals(2, bookInfo.getPages().size());

        PageInfo firstPage = bookInfo.getFirstPage();
        assertNotNull(firstPage);
        PageInfo lastPage = bookInfo.getLastPage();
        assertNotNull(lastPage);

        assertEquals(page2Start, firstPage.getEnded());
        assertEquals(page3Start, lastPage.getStarted());
    }

    @Test
    public void insertIntoGageGapTest() throws Exception {
        int initNumberOfBooks = storage.listBooks().size();
        String bookName = "insertIntoGageGapTest";
        String pageToRemove = "page2";
        String pageToInsert = "page4";
        Instant time = Instant.now();
        BookId bookId = new BookId(bookName);

        Instant page1Start = time.plus(10, ChronoUnit.MINUTES);
        Instant page2Start = time.plus(20, ChronoUnit.MINUTES);
        Instant page3Start = time.plus(30, ChronoUnit.MINUTES);

        addBook(new BookToAdd(bookName, time.minus(20, ChronoUnit.SECONDS)));
        BookInfo bookInfo = this.storage.getBook(bookId);
        assertEquals(initNumberOfBooks + 1, storage.listBooks().size());

        addPage(bookId, new PageToAdd("page1", page1Start, null));
        addPage(bookId, new PageToAdd(pageToRemove, page2Start, null));
        addPage(bookId, new PageToAdd("page3", page3Start, null));
        this.storage.refreshPages(bookId);
        assertEquals(3, bookInfo.getPages().size());

        removePage(new PageId(bookId, page2Start, pageToRemove));
        this.storage.refreshPages(bookId);
        assertEquals(2, bookInfo.getPages().size());

        HttpTester.Response response = this.executeGet(String.format("/new-page?book-id=%s&page-name=%s&page-start=%s",
                bookName, pageToInsert, page2Start));
        assertEquals(200, response.getStatus());
        this.checkPlainResponse(response.getContent(), true,
                String.format("Page created bookId = %s,pageName = %s,pageStart = %s", bookName.toLowerCase(), pageToInsert, page2Start));

        this.storage.refreshPages(bookId);
        List<PageInfo> pages = new ArrayList<>(bookInfo.getPages());
        assertEquals(3, pages.size());

        assertEquals(page1Start, pages.get(0).getStarted());
        assertEquals(page2Start, pages.get(0).getEnded());
        assertEquals(page2Start, pages.get(1).getStarted());
        assertEquals(page3Start, pages.get(1).getEnded());
        assertEquals(page3Start, pages.get(2).getStarted());
        assertNull(pages.get(2).getEnded());
    }
}
