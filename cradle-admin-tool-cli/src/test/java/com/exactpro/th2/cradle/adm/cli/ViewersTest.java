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

package com.exactpro.th2.cradle.adm.cli;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageId;
import com.exactpro.th2.cradle.adm.TestExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

public class ViewersTest extends AbstractCliTest {

    private final Instant book1Start = Instant.now();
    private final String page2Name = "page2";
    private final Instant page2Time = book1Start.plus(20, ChronoUnit.MINUTES);
    private final String page3Name = "page3";
    private final Instant page3Time = page2Time.plus(20, ChronoUnit.MINUTES);

    private final String book1Str = this.book1Start.toString();
    private final String page2Str = this.page2Time.toString();
    private final String page3Str = this.page3Time.toString();

    private final Instant book2Start = Instant.now().minus(20, ChronoUnit.MINUTES);
    private final Instant book3Start = Instant.now().minus(40, ChronoUnit.MINUTES);

    private final String book2StartStr = this.book2Start.toString();
    private final String book3StartStr = this.book3Start.toString();

    private final String book2 = "test_book2";
    private final String book3 = "test_book3";

    private TestExecutor createDataExecutor() {
        return new TestExecutor().addBookIds(INITIAL_BOOK, book1Start)
                .addPageIds(INITIAL_BOOK, page2Name, page2Time, null)
                .addPageIds(INITIAL_BOOK, page3Name, page3Time, null)
                .addBookIds(book2, book2Start)
                .addBookIds(book3, book3Start);
    }

    @Test
    public void printUnknownBookTest() throws Exception {
        createDataExecutor().execTest(
                        (cradleStorage) -> {
                            Application.main(new String[]{"-c=stub/", "--getBookInfo", "-bookId", "unknown_book"});
                            checkOutput(false, "No books found by given params");
                        }
                );
    }

    @Test
    public void printBookTest() throws Exception {

        createDataExecutor().execTest(
                (cradleStorage) -> {
                    Application.main(new String[]{"-c=stub/", "--getBookInfo", "-bookId", INITIAL_BOOK});
                    String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                            "Started with arguments: [-c=stub/, --getBookInfo, -bookId, init_book]\n" +
                            "Success\n" +
                            "\n" +
                            "book #1\n" +
                            "\tBookId: init_book\n" +
                            "\tBookCreatedTime: %s\n" +
                            "\tPage #1\n" +
                            "\t\tPageId: page2\n" +
                            "\t\tStarted: %s\n" +
                            "\t\tEnded: %s\n" +
                            "\tPage #2\n" +
                            "\t\tPageId: page3\n" +
                            "\t\tStarted: %s\n", book1Str, page2Str, page3Str, page3Str);
                    Assertions.assertEquals(expected, this.outContent.toString());
                }
        );
    }

    @Test
    public void printBookTest2() throws Exception {

        createDataExecutor().execTest(
                (cradleStorage) -> {
                    Application.main(new String[]{"-c=stub/", "--getBookInfo", "-bookId", book2, "-bookId", book3});
                    String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                            "Started with arguments: [-c=stub/, --getBookInfo, -bookId, test_book2, -bookId, test_book3]\n" +
                            "Success\n" +
                            "\n" +
                            "book #1\n" +
                            "\tBookId: test_book3\n" +
                            "\tBookCreatedTime: %s\n" +
                            "\n" +
                            "book #2\n" +
                            "\tBookId: test_book2\n" +
                            "\tBookCreatedTime: %s\n"
                            , book3StartStr,  book2StartStr);
                    Assertions.assertEquals(expected, this.outContent.toString());
                }
        );
    }

    @Test
    public void printBookTestWithRemovedPages() throws Exception {

        createDataExecutor().execTest(
                (cradleStorage) -> {
                    Instant removeInstant = Instant.now().plus(1, ChronoUnit.MINUTES);
                    Assertions.assertEquals(2,cradleStorage.getBook(new BookId(INITIAL_BOOK)).getPages().size()  );

                    cradleStorage.setNextRemovedTime(removeInstant);
                    cradleStorage.removePage(new PageId(new BookId(INITIAL_BOOK), page2Name));

                    Application.main(new String[]{"-c=stub/", "--getBookInfo", "-bookId", INITIAL_BOOK, "-loadRemovedPages"});
                    String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                            "Started with arguments: [-c=stub/, --getBookInfo, -bookId, init_book, -loadRemovedPages]\n" +
                            "Success\n" +
                            "\n" +
                            "book #1\n" +
                            "\tBookId: init_book\n" +
                            "\tBookCreatedTime: %s\n" +
                            "\tPage #1\n" +
                            "\t\tPageId: page2\n" +
                            "\t\tStarted: %s\n" +
                            "\t\tEnded: %s\n" +
                            "\t\tRemoved: %s\n" +
                            "\tPage #2\n" +
                            "\t\tPageId: page3\n" +
                            "\t\tStarted: %s\n", book1Str, page2Str, page3Str, removeInstant, page3Str);
                    Assertions.assertEquals(expected, this.outContent.toString());
                }
        );
    }

    @Test
    public void printBookTestWithoutPages() throws Exception {

        createDataExecutor().execTest(
                (cradleStorage) -> {
                    Application.main(new String[]{"-c=stub/", "--getBookInfo", "-bookId", INITIAL_BOOK, "-withPages", "false"});
                    String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                            "Started with arguments: [-c=stub/, --getBookInfo, -bookId, init_book, -withPages, false]\n" +
                            "Success\n" +
                            "\n" +
                            "book #1\n" +
                            "\tBookId: init_book\n" +
                            "\tBookCreatedTime: %s\n", book1Str);
                    Assertions.assertEquals(expected, this.outContent.toString());
                }
        );
    }

    @Test
    public void printAllBooksTest() throws Exception {

        createDataExecutor().execTest(
                (cradleStorage) -> {
                    Application.main(new String[]{"-c=stub/", "--getAllBooks"});
                    String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                            "Started with arguments: [-c=stub/, --getAllBooks]\n" +
                            "Success\n" +
                            "\n" +
                            "book #1\n" +
                            "\tBookId: test_book3\n" +
                            "\tBookCreatedTime: %s\n" +
                            "\n" +
                            "book #2\n" +
                            "\tBookId: init_book\n" +
                            "\tBookCreatedTime: %s\n" +
                            "\n" +
                            "book #3\n" +
                            "\tBookId: test_book2\n" +
                            "\tBookCreatedTime: %s\n", book3StartStr, book1Str, book2StartStr);
                    Assertions.assertEquals(expected, this.outContent.toString());
                }
        );
    }

}
