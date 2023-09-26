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
import com.exactpro.cradle.BookListEntry;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PageManagerTest {

    private static final String SCHEMA_VERSION = "test-schema-version";

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void autoPage(boolean baseTimeBefore) throws Exception {
        Instant now = Instant.now();

        Duration duration = Duration.of(100, ChronoUnit.SECONDS);
        long threshold = duration.dividedBy(2).toMillis();

        BookInfo longTimeBefore = createBookInfoWithPage(now.minus(duration.multipliedBy(2)), "a long time before");
        BookInfo before = createBookInfoWithPage(now.minus(duration), "before");
        BookInfo equal = createBookInfoWithPage(now, "equal");
        BookInfo after = createBookInfoWithPage(now.plus(duration), "after");

        CradleStorage mockStorage = mock(CradleStorage.class);
        List<BookListEntry> listEntries = List.of(
                new BookListEntry(longTimeBefore.getId().getName(), SCHEMA_VERSION),
                new BookListEntry(before.getId().getName(), SCHEMA_VERSION),
                new BookListEntry(equal.getId().getName(), SCHEMA_VERSION),
                new BookListEntry(after.getId().getName(), SCHEMA_VERSION)
        );
        when(mockStorage.listBooks()).thenReturn(listEntries);
        when(mockStorage.refreshBook(same(longTimeBefore.getFullName()))).thenReturn(longTimeBefore);
        when(mockStorage.refreshBook(same(before.getId().getName()))).thenReturn(before);
        when(mockStorage.refreshBook(same(equal.getId().getName()))).thenReturn(equal);
        when(mockStorage.refreshBook(same(after.getId().getName()))).thenReturn(after);
        when(mockStorage.addPage(same(longTimeBefore.getId()), any(), any(), any())).thenReturn(longTimeBefore);
        when(mockStorage.addPage(same(before.getId()), any(), any(), any())).thenReturn(before);
        when(mockStorage.addPage(same(equal.getId()), any(), any(), any())).thenReturn(equal);
        when(mockStorage.addPage(same(after.getId()), any(), any(), any())).thenReturn(after);

        AutoPageConfiguration autoPageConfig = new AutoPageConfiguration();
        autoPageConfig.setPageDuration(duration);
        autoPageConfig.setPageStartTime(baseTimeBefore ? now.minus(duration) : now.plus(duration));


        Map<String, AutoPageConfiguration> mapping = Map.of(
                longTimeBefore.getId().getName(), autoPageConfig,
                before.getId().getName(), autoPageConfig,
                equal.getId().getName(), autoPageConfig,
                after.getId().getName(), autoPageConfig
        );
        try (PageManager ignored = new PageManager(mockStorage, false, mapping, 60*60*12, threshold)) {
            verify(mockStorage, times(4)).listBooks();
            verify(mockStorage).refreshBook(longTimeBefore.getId().getName());
            verify(mockStorage).refreshBook(before.getId().getName());
            verify(mockStorage).refreshBook(equal.getId().getName());
            verify(mockStorage).refreshBook(after.getId().getName());

            verify(mockStorage, timeout(1000))
                    .addPage(same(longTimeBefore.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage, timeout(1000))
                    .addPage(same(before.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage, timeout(1000))
                    .addPage(same(equal.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage, never())
                    .addPage(same(after.getId()), any(), any(), any());

            verifyNoMoreInteractions(mockStorage);
        }
    }

    @Test
    public void emptyStorageAndAutoBookFalse() {
        Instant now = Instant.now();

        Duration duration = Duration.of(100, ChronoUnit.SECONDS);
        long threshold = duration.dividedBy(2).toMillis();

        CradleStorage mockStorage = mock(CradleStorage.class);

        AutoPageConfiguration autoPageConfig = new AutoPageConfiguration();
        autoPageConfig.setPageDuration(duration);
        autoPageConfig.setPageStartTime(now.plus(duration));

        String book = "test-new-book";
        Map<String, AutoPageConfiguration> mapping = Map.of(
                book, autoPageConfig
        );
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> new PageManager(mockStorage, false, mapping, 60*60*12, threshold).close());
        assertEquals("Book with name '" + book + "' can't be created", runtimeException.getMessage());
    }

    @Test
    public void createAutoBook() throws Exception {
        Instant now = Instant.now();

        Duration duration = Duration.of(100, ChronoUnit.SECONDS);
        long threshold = duration.dividedBy(2).toMillis();

        BookInfo existedBook = createBookInfoWithPage(now.minus(duration), "test-existed-book");
        BookInfo newBook = createBookInfo("test-new-book", now.minus(1, ChronoUnit.DAYS));

        CradleStorage mockStorage = mock(CradleStorage.class);
        List<BookListEntry> listEntries = List.of(new BookListEntry(existedBook.getId().getName(), SCHEMA_VERSION));
        when(mockStorage.listBooks()).thenReturn(listEntries);
        when(mockStorage.refreshBook(same(existedBook.getId().getName()))).thenReturn(existedBook);
        when(mockStorage.addBook(argThat((bookToAdd) -> newBook.getId().getName().equals(bookToAdd.getName())
            && Instant.now().minus(1, ChronoUnit.DAYS).isAfter(bookToAdd.getCreated())
        ))).thenReturn(newBook);
        when(mockStorage.addPage(same(existedBook.getId()), any(), any(), any())).thenReturn(existedBook);
        when(mockStorage.addPage(same(newBook.getId()), any(), any(), any())).thenReturn(newBook);

        AutoPageConfiguration autoPageConfig = new AutoPageConfiguration();
        autoPageConfig.setPageDuration(duration);
        autoPageConfig.setPageStartTime(now.plus(duration));

        Map<String, AutoPageConfiguration> mapping = Map.of(
                existedBook.getId().getName(), autoPageConfig,
                newBook.getId().getName(), autoPageConfig
        );
        try (PageManager ignored = new PageManager(mockStorage, true, mapping, 60*60*12, threshold)) {
            verify(mockStorage, times(2)).listBooks();
            verify(mockStorage).refreshBook(existedBook.getId().getName());

            verify(mockStorage).addBook(argThat((bookToAdd) -> newBook.getId().getName().equals(bookToAdd.getName())));

            verify(mockStorage)
                    .addPage(same(newBook.getId()), anyString(), eq(now.minus(1, ChronoUnit.DAYS)), anyString());

            verify(mockStorage, timeout(1000))
                    .addPage(eq(existedBook.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage, timeout(1000))
                    .addPage(eq(newBook.getId()), any(), argThat((pageStart) ->
                            Instant.now().plus(duration).isAfter(pageStart) && Instant.now().isBefore(pageStart)
                    ), any());

            verifyNoMoreInteractions(mockStorage);
        }
    }

    private BookInfo createBookInfoWithPage(Instant lastStarted, String fullName) {
        PageInfo pageInfoMock = mock(PageInfo.class);
        when(pageInfoMock.getStarted()).thenReturn(lastStarted);

        BookInfo bookInfoMock = createBookInfo(fullName, lastStarted);
        when(bookInfoMock.getLastPage()).thenReturn(pageInfoMock);
        return bookInfoMock;
    }

    @NotNull
    private static BookInfo createBookInfo(String fullName, Instant created) {
        BookInfo bookInfoMock = mock(BookInfo.class);
        when(bookInfoMock.getId()).thenReturn(new BookId(fullName));
        when(bookInfoMock.getCreated()).thenReturn(created);
        when(bookInfoMock.getFullName()).thenReturn(fullName);
        return bookInfoMock;
    }
}
