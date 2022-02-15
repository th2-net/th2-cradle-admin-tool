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
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.cradle.adm.TestExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class PagesCliTest extends AbstractCliTest {

    @Test
    public void addPageBeforeTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {

                            Application.main(new String[]{"-c=stub/", "--page", "-pageName", "page123", "-bookId", INITIAL_BOOK, "-pageStart", "2022-01-22T00:10:00.000000Z"});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_5 = cradleStorage.getBook(INITIAL_BOOK);
                            Assertions.assertEquals(1, dev_test_5.getPages().size());
                            Assertions.assertNotNull(dev_test_5.getPage(new PageId(dev_test_5.getId(), INITIAL_PAGE)));

                            checkOutput(false, "Timestamp of new page start must be after current timestamp");
                        }
                );
    }

    @Test
    public void addPageCorrectTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {

                            Application.main(new String[]{"-c=stub/", "--page", "-pageName", "page123", "-bookId", INITIAL_BOOK, "-pageStart", Instant.now().plus(1, ChronoUnit.MINUTES).toString()});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_5 = cradleStorage.getBook(INITIAL_BOOK);
                            Assertions.assertEquals(2, dev_test_5.getPages().size());
                            PageInfo newPage = dev_test_5.getPage(new PageId(dev_test_5.getId(), "page123"));
                            PageInfo oldPage = dev_test_5.getPage(new PageId(dev_test_5.getId(), INITIAL_PAGE));
                            Assertions.assertNotNull(newPage);
                            Assertions.assertNotNull(oldPage);

                            Assertions.assertNotNull(oldPage.getStarted());
                            Assertions.assertNotNull(oldPage.getEnded());
                            //todo incorrect logic now
//                            Assertions.assertTrue(oldPage.isActive());

                            Assertions.assertNotNull(newPage.getStarted());
                            Assertions.assertNull(newPage.getEnded());
//                            Assertions.assertFalse(newPage.isActive());

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addPageWithoutNameTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {

                            Application.main(new String[]{"-c=stub/", "--page", "-bookId", INITIAL_BOOK, "-pageStart", Instant.now().plus(1, ChronoUnit.MINUTES).toString()});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_5 = cradleStorage.getBook(INITIAL_BOOK);
                            Assertions.assertEquals(2, dev_test_5.getPages().size());
                            Assertions.assertEquals(dev_test_5.getFirstPage().getId().getName(), INITIAL_PAGE);
                            Assertions.assertTrue(UUID_REGEX.matcher(dev_test_5.getLastPage().getId().getName()).find());

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addPageWithParamsTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {
                            Instant pageStart = Instant.now().plus(20, ChronoUnit.MINUTES);
                            String pageName = "test_page_1234";
                            String pageComment = "This PAGE is created in addPageWithParamsTest for unit test purposes";
                            Application.main(new String[]{"-c=stub/", "--page", "-bookId", INITIAL_BOOK,
                                    "-pageStart", pageStart.toString(), "-pageName", pageName, "-pageComment", pageComment});

                            PageInfo createdPage = cradleStorage.getPage(new PageId(new BookId(INITIAL_BOOK), pageName));
                            Assertions.assertEquals(pageName, createdPage.getId().getName());
                            Assertions.assertEquals(pageComment, createdPage.getComment());
                            Assertions.assertTrue(createdPage.isActive());
                            Assertions.assertNull(createdPage.getEnded());
                            Assertions.assertEquals(pageStart, createdPage.getStarted());

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addExistedPageTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {
                            Application.main(new String[]{"-c=stub/", "--page", "-pageName", INITIAL_PAGE, "-bookId", INITIAL_BOOK, "-pageStart", Instant.now().plus(1, ChronoUnit.MINUTES).toString()});
                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            checkOutput(false, String.format("Page '%s' is already present in book '%s'", INITIAL_PAGE, INITIAL_BOOK));
                        }
                );
    }

}
