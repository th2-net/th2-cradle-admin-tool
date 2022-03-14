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

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.cradle.adm.TestExecutor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class BookCliTest extends AbstractCliTest{


    @Test
    public void addBookTest() throws Exception {

        new TestExecutor()
                .execTest(
                        (cradleStorage) -> {

                            Application.main(new String[]{"-c=stub/", "--book", "-bookName", "dev_test_6"});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_6 = cradleStorage.getBook("dev_test_6");
                            Assertions.assertEquals(1, dev_test_6.getPages().size());
                            String name = dev_test_6.getFirstPage().getId().getName();
                            Assertions.assertTrue(UUID_REGEX.matcher(name).find());

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addBookWithoutTimeTest() throws Exception {

        new TestExecutor()
                .execTest(
                        (cradleStorage) -> {

                            Instant i1 = Instant.now();
                            Application.main(new String[]{"-c=stub/", "--book", "-bookName", "dev_test_6"});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_5 = cradleStorage.getBook("dev_test_6");

                            Assertions.assertNotNull(dev_test_5);
                            Assertions.assertNotNull(dev_test_5.getCreated());
                            Assertions.assertTrue(dev_test_5.getCreated().isAfter(i1));
                            Assertions.assertTrue(dev_test_5.getCreated().isBefore(Instant.now()));

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addBookWithParamsTest() throws Exception {

        new TestExecutor()
                .execTest(
                        (cradleStorage) -> {
                            Instant created = Instant.now().minus(20, ChronoUnit.MINUTES);
                            String bookName = "dev_test_6";
                            String bookFullName = "This book is created in addBookWithParamsTest for unit test purposes";
                            String pageName = "pageInit";
                            String pageComment = "page comment 12345678901234567890123456789012345678901234567890";
                            String bookDesc = "book DESC123";
                            Application.main(new String[]{"-c=stub/", "--book", "-bookName", bookName,
                                    "-createdTime", created.toString(), "-firstPageName", pageName,
                                    "-firstPageComment", pageComment, "-desc", bookDesc, "-fullName", bookFullName});

                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            BookInfo dev_test_6 = cradleStorage.getBook("dev_test_6");
                            Assertions.assertEquals(bookFullName, dev_test_6.getFullName());
                            Assertions.assertEquals(bookDesc, dev_test_6.getDesc());
                            Assertions.assertEquals(created, dev_test_6.getCreated());
                            Assertions.assertEquals(1, dev_test_6.getPages().size());
                            PageInfo firstPage = dev_test_6.getFirstPage();
                            Assertions.assertEquals(pageName, firstPage.getId().getName());
                            Assertions.assertEquals(pageComment, firstPage.getComment());
                            Assertions.assertNull(firstPage.getEnded());
                            Assertions.assertEquals(created, firstPage.getStarted());

                            checkOutput(true, null);
                        }
                );
    }

    @Test
    public void addExistedBookTest() throws Exception {

        new TestExecutor().addBookIds(INITIAL_BOOK, Instant.now(), INITIAL_PAGE)
                .execTest(
                        (cradleStorage) -> {
                            Application.main(new String[]{"-c=stub/", "--book", "-bookName", INITIAL_BOOK});
                            Assertions.assertEquals(1, cradleStorage.getBooksCount());
                            checkOutput(false, String.format("Book '%s' is already present in storage", INITIAL_BOOK));
                        }
                );
    }

}
