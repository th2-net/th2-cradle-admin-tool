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
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.test.annotations.Th2AppFactory;
import com.exactpro.th2.test.annotations.Th2TestFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
public class BookCliTest extends AbstractCliTest {

    @Test
    public void addBookTest(@Th2AppFactory CommonFactory appFactory,
                            @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        String bookName = "addBookTest";

        Application.run(new String[]{"-c=stub/", "--book", "-bookName", bookName}, args -> appFactory);

        assertEquals(0, cradleStorage.getBooks().size());
        BookInfo bookInfo = cradleStorage.getBook(new BookId(bookName));
        assertEquals(1, cradleStorage.getBooks().size());
        assertEquals(0, bookInfo.getPages().size());

        checkOutput(true, null);
    }

    @Test
    public void addBookWithoutTimeTest(@Th2AppFactory CommonFactory appFactory,
                                       @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        Instant i1 = Instant.now();
        String bookName = "addBookWithoutTimeTest";
        Application.run(new String[]{"-c=stub/", "--book", "-bookName", bookName}, args -> appFactory);

        assertEquals(0, cradleStorage.getBooks().size());
        BookInfo bookInfo = cradleStorage.getBook(new BookId(bookName));
        assertEquals(1, cradleStorage.getBooks().size());
        assertNotNull(bookInfo);
        assertNotNull(bookInfo.getCreated());
        assertTrue(bookInfo.getCreated().isAfter(i1));
        assertTrue(bookInfo.getCreated().isBefore(Instant.now()));

        checkOutput(true, null);
    }

    @Test
    public void addBookWithParamsTest(@Th2AppFactory CommonFactory appFactory,
                                      @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        Instant created = Instant.now().minus(20, ChronoUnit.MINUTES);
        String bookName = "addBookWithParamsTest";
        BookId bookId = new BookId(bookName);
        String bookFullName = "This book is created in addBookWithParamsTest for unit test purposes";
        String bookDesc = "book DESC123";

        Application.run(new String[]{"-c=stub/", "--book", "-bookName", bookName,
                "-createdTime", created.toString(),
                "-desc", bookDesc, "-fullName", bookFullName},
                args -> appFactory);


        assertEquals(0, cradleStorage.getBooks().size());
        BookInfo bookInfo = cradleStorage.getBook(bookId);
        assertEquals(1, cradleStorage.getBooks().size());
        assertNotNull(bookInfo);
        assertEquals(bookFullName, bookInfo.getFullName());
        assertEquals(bookDesc, bookInfo.getDesc());
        assertEquals(created.truncatedTo(ChronoUnit.MILLIS), bookInfo.getCreated());
        assertEquals(0, bookInfo.getPages().size());

        checkOutput(true, null);
    }

    @Test
    public void addExistedBookTest(@Th2AppFactory CommonFactory appFactory,
                                   @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();
        String bookName = "addExistedBookTest";
        BookId bookId = new BookId(bookName);
        assertEquals(0, cradleStorage.getBooks().size());
        testBookPageBuilder.addBookIds(bookName, Instant.now())
                .exec(cradleStorage);
        assertEquals(1, cradleStorage.getBooks().size());

        Application.run(new String[]{"-c=stub/", "--book", "-bookName", bookName}, args -> appFactory);

        assertNotNull(cradleStorage.getBook(bookId));
        checkOutput(false, String.format("Query to insert book '%s' was not applied. Probably, book already exists", bookName.toLowerCase()));
    }
}
