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
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.cradle.adm.TestBookPageBuilder;
import com.exactpro.th2.test.annotations.Th2AppFactory;
import com.exactpro.th2.test.annotations.Th2TestFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("integration")
public class ViewersTest extends AbstractCliTest {
    public static final String DEFAULT_REMOVED_TIME = "+292278994-08-17T07:12:55.807Z";
    private static final String BOOK_1 = "BOOK_1";
    private static final String BOOK_2 = "BOOK_2";
    private static final String BOOK_3 = "BOOK_3";
    private static final String PAGE_1 = "PAGE_1";
    private static final String PAGE_2 = "PAGE_2";

    @Test
    public void printUnknownBookTest(@Th2AppFactory CommonFactory appFactory) {
        Application.run(new String[]{"-c=stub/", "--getBookInfo", "-bookId", "unknown_book"}, args -> appFactory);

        checkOutput(false, "No books found by given params");
    }

    @Test
    public void printBookTest(@Th2AppFactory CommonFactory appFactory,
                              @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        DBMetadata result = getInitData(cradleStorage, "printBookTest");

        BookMetadata book1 = result.books.get(BOOK_1);
        Application.run(new String[]{"-c=stub/", "--getBookInfo", "-bookId", book1.name}, args -> appFactory);

        PageMetadata page1 = book1.pages.get(PAGE_1);
        PageMetadata page2 = book1.pages.get(PAGE_2);
        String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                "Started with arguments: [-c=stub/, --getBookInfo, -bookId, %s]\n" +
                "Success\n" +
                "\n" +
                "book #1\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n" +
                "\tPage #1\n" +
                "\t\tPageId: %s\n" +
                "\t\tStarted: %s\n" +
                "\t\tEnded: %s\n" +
                "\t\tUpdated: %s\n" +
                "\t\tRemoved: %s\n" +
                "\tPage #2\n" +
                "\t\tPageId: %s\n" +
                "\t\tStarted: %s\n" +
                "\t\tUpdated: %s\n" +
                "\t\tRemoved: %s\n",
                book1.name,
                book1.name.toLowerCase(),
                book1.start.truncatedTo(ChronoUnit.MILLIS),
                page1.name,
                page1.start,
                page1.end,
                page1.start.truncatedTo(ChronoUnit.MILLIS),
                DEFAULT_REMOVED_TIME,
                page2.name,
                page2.start,
                page2.start.truncatedTo(ChronoUnit.MILLIS),
                DEFAULT_REMOVED_TIME);
        assertEquals(expected, this.outContent.toString());
    }

    @Test
    public void printBookTest2(@Th2AppFactory CommonFactory appFactory,
                               @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        DBMetadata result = getInitData(cradleStorage, "printBookTest2");

        BookMetadata book2 = result.books.get(BOOK_2);
        BookMetadata book3 = result.books.get(BOOK_3);
        Application.run(new String[]{"-c=stub/", "--getBookInfo", "-bookId", book2.name, "-bookId", book3.name}, args -> appFactory);

        String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                "Started with arguments: [-c=stub/, --getBookInfo, -bookId, %s, -bookId, %s]\n" +
                "Success\n" +
                "\n" +
                "book #1\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n" +
                "\n" +
                "book #2\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n",
                book2.name,
                book3.name,
                book3.name.toLowerCase(),
                book3.start.truncatedTo(ChronoUnit.MILLIS),
                book2.name.toLowerCase(),
                book2.start.truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected, this.outContent.toString());
    }

    @Test
    public void printBookTestWithRemovedPages(@Th2AppFactory CommonFactory appFactory,
                                      @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        DBMetadata result = getInitData(cradleStorage, "printBookTestWithRemovedPages");

        BookMetadata book1 = result.books.get(BOOK_1);
        PageMetadata page1 = book1.pages.get(PAGE_1);
        PageMetadata page2 = book1.pages.get(PAGE_2);

        BookId book1Id = new BookId(book1.name);
        BookInfo bookInfo = cradleStorage.getBook(book1Id);
        assertNotNull(bookInfo);
        assertEquals(2, bookInfo.getPages().size());
        PageInfo pageInfo2 = getPageInfo(cradleStorage, book1Id, page1.name);
        cradleStorage.removePage(pageInfo2.getId());

        Application.run(new String[]{"-c=stub/", "--getBookInfo", "-bookId", book1.name, "-loadRemovedPages"},
                args -> appFactory);
        String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                "Started with arguments: [-c=stub/, --getBookInfo, -bookId, %s, -loadRemovedPages]\n" +
                "Success\n" +
                "\n" +
                "book #1\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n" +
                "\tPage #1\n" +
                "\t\tPageId: %s\n" +
                "\t\tStarted: %s\n" +
                "\t\tUpdated: %s\n" +
                "\t\tRemoved: %s\n",
                book1.name,
                book1.name.toLowerCase(),
                book1.start.truncatedTo(ChronoUnit.MILLIS),
                page2.name,
                page2.start,
                page2.start.truncatedTo(ChronoUnit.MILLIS),
                DEFAULT_REMOVED_TIME);
        String outContent = this.outContent.toString();
        assertEquals(expected, outContent);
    }

    @Test
    public void printBookTestWithoutPages(@Th2AppFactory CommonFactory appFactory,
                                          @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        DBMetadata result = getInitData(cradleStorage, "printBookTestWithoutPages");

        BookMetadata book1 = result.books.get(BOOK_1);

        Application.run(new String[]{"-c=stub/", "--getBookInfo", "-bookId", book1.name, "-withPages", "false"},
                args -> appFactory);

        String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                "Started with arguments: [-c=stub/, --getBookInfo, -bookId, %s, -withPages, false]\n" +
                "Success\n" +
                "\n" +
                "book #1\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n",
                book1.name,
                book1.name.toLowerCase(),
                book1.start.truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected, this.outContent.toString());
    }

    @NotNull
    private static DBMetadata getInitData(CradleStorage cradleStorage, String baseName) throws Exception {
        String book1Name = baseName + "Book1";
        Instant book1Start = Instant.now();
        String page1Name = baseName + "Page1";
        Instant page1Time = book1Start.plus(20, ChronoUnit.MINUTES);
        String page2Name = baseName + "Page2";
        Instant page2Time = page1Time.plus(20, ChronoUnit.MINUTES);
        String book2Name = baseName + "Book2";
        Instant book2Start = Instant.now().minus(20, ChronoUnit.MINUTES);
        String book3Name = baseName + "Book3";
        Instant book3Start = Instant.now().minus(40, ChronoUnit.MINUTES);
        TestBookPageBuilder.builder().addBookIds(book1Name, book1Start)
                .addPageIds(book1Name, page1Name, page1Time, null)
                .addPageIds(book1Name, page2Name, page2Time, null)
                .addBookIds(book2Name, book2Start)
                .addBookIds(book3Name, book3Start)
                .exec(cradleStorage);
        return new DBMetadata(
            Map.of(
                BOOK_1, new BookMetadata(book1Name, book1Start, Map.of(
                        PAGE_1, new PageMetadata(page1Name, page1Time, page2Time),
                        PAGE_2, new PageMetadata(page2Name, page2Time))),
                BOOK_2, new BookMetadata(book2Name, book2Start),
                BOOK_3, new BookMetadata(book3Name, book3Start)
            )
        );
    }

    @NotNull
    private static PageInfo getPageInfo(CradleStorage cradleStorage, BookId bookId, String pageName) throws CradleStorageException {
        return cradleStorage.getAllPages(bookId).stream()
                .filter(page -> Objects.equals(pageName, page.getName()))
                .findFirst()
                .orElseThrow();
    }

    private static class PageMetadata {
        public final String name;
        public final Instant start;
        public final Instant end;

        private PageMetadata(String name, Instant start, Instant end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
        private PageMetadata(String name, Instant start) {
            this(name, start, null);
        }
    }
    private static class BookMetadata {
        public final String name;
        public final Instant start;
        public final Map<String, PageMetadata> pages;

        private BookMetadata(String name, Instant start, Map<String, PageMetadata> pages) {
            this.name = name;
            this.start = start;
            this.pages = pages;
        }
        private BookMetadata(String name, Instant start) {
            this(name, start, Collections.emptyMap());
        }
    }
    private static class DBMetadata {
        public final Map<String, BookMetadata> books;

        private DBMetadata(Map<String, BookMetadata> books) {
            this.books = books;
        }
    }
}
