/*
 * Copyright 2024 Exactpro (Exactpro Systems Limited)
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

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.cradle.adm.TestBookPageBuilder;
import com.exactpro.th2.test.annotations.Th2AppFactory;
import com.exactpro.th2.test.annotations.Th2TestFactory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
public class ViewersAllBooksTest extends AbstractCliTest {
    @Test
    public void printAllBooksTest(@Th2AppFactory CommonFactory appFactory,
                                  @Th2TestFactory CommonFactory testFactory) throws Exception {
        CradleStorage cradleStorage = testFactory.getCradleManager().getStorage();

        String book1Name = "Book1";
        Instant book1Start = Instant.now();
        String book2Name = "Book2";
        Instant book2Start = Instant.now().minus(20, ChronoUnit.MINUTES);
        String book3Name = "Book3";
        Instant book3Start = Instant.now().minus(40, ChronoUnit.MINUTES);
        TestBookPageBuilder.builder()
                .addBookIds(book1Name, book1Start)
                .addBookIds(book2Name, book2Start)
                .addBookIds(book3Name, book3Start)
                .exec(cradleStorage);

        Application.run(new String[]{"-c=stub/", "--getAllBooks"}, args -> appFactory);

        String expected = String.format("Cradle TH2 Admin tool (CLI), version null, build-date null\n" +
                "Started with arguments: [-c=stub/, --getAllBooks]\n" +
                "Success\n" +
                "\n" +
                "book #1\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n" +
                "\n" +
                "book #2\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n" +
                "\n" +
                "book #3\n" +
                "\tBookId: %s\n" +
                "\tBookCreatedTime: %s\n",
                book3Name.toLowerCase(),
                book3Start.truncatedTo(MILLIS),
                book2Name.toLowerCase(),
                book2Start.truncatedTo(MILLIS),
                book1Name.toLowerCase(),
                book1Start.truncatedTo(MILLIS)
        );

        assertEquals(expected, this.outContent.toString());
    }
}
