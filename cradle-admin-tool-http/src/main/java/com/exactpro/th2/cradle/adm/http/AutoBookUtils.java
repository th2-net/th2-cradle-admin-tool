/*
 * Copyright 2023 Exactpro (Exactpro Systems Limited)
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
import com.exactpro.cradle.CradleStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;

public class AutoBookUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoBookUtils.class);
    public static final String AUTO_BOOK_DESCRIPTION = "auto-book";

    public static void createBooks(@NotNull CradleStorage storage, @Nullable Map<String, AutoBookConfiguration> autoBooks) {
        requireNonNull(storage, "Cradle storage can't be null");

        if (autoBooks == null || autoBooks.isEmpty()) {
            LOGGER.info("Auto book configuration is empty");
            return;
        }

        Set<String> existedBooks = storage.getBooks().stream()
                .map(BookInfo::getId)
                .map(BookId::getName)
                .collect(Collectors.toSet());

        autoBooks.forEach((bookName, config) -> {
            try {
                bookName = lowerCase(trim(bookName));
                if (bookName == null || bookName.isEmpty()) {
                    LOGGER.warn("book with null or empty name can't be created");
                }

                if (existedBooks.contains(bookName)) {
                    return;
                }

                BookToAdd bookToAdd;
                if (config == null) {
                    bookToAdd = new BookToAdd(bookName);
                    bookToAdd.setFullName(bookName);
                    bookToAdd.setDesc(AUTO_BOOK_DESCRIPTION);
                } else {
                    bookToAdd = new BookToAdd(
                            bookName,
                            config.getBookCreationTime() != null ? config.getBookCreationTime() : Instant.now()
                    );
                    bookToAdd.setFullName(trim(defaultIfBlank(config.getBookFullName(), bookName)));
                    bookToAdd.setDesc(trim(defaultIfBlank(config.getBookDescription(), AUTO_BOOK_DESCRIPTION)));
                }

                storage.addBook(bookToAdd);

                LOGGER.info("Created '{}' book, time: {}, full name: {}, description: {}",
                        bookName,
                        bookToAdd.getCreated(),
                        bookToAdd.getFullName(),
                        bookToAdd.getDesc());
            } catch (Exception e) {
                throw new RuntimeException("Book with name '" + bookName + "' and config " + config + " can't be created", e);
            }
        });
    }
}
