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
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PageManagerTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void autoPage(boolean baseTimeBefore) throws Exception {
        Instant now = Instant.now();

        Duration duration = Duration.of(100, ChronoUnit.SECONDS);
        long threshold = duration.dividedBy(2).toMillis();

        BookInfo longTimeBefore = createBookInfo(now.minus(duration.multipliedBy(2)), "a long time before");
        BookInfo before = createBookInfo(now.minus(duration), "before");
        BookInfo equal = createBookInfo(now, "equal");
        BookInfo after = createBookInfo(now.plus(duration), "after");

        CradleStorage mockStorage = mock(CradleStorage.class);
        when(mockStorage.refreshBook(same(longTimeBefore.getFullName()))).thenReturn(longTimeBefore);
        when(mockStorage.refreshBook(same(before.getId().getName()))).thenReturn(before);
        when(mockStorage.refreshBook(same(equal.getId().getName()))).thenReturn(equal);
        when(mockStorage.refreshBook(same(after.getId().getName()))).thenReturn(after);
        when(mockStorage.addPage(same(longTimeBefore.getId()), any(), any(), any())).thenReturn(longTimeBefore);
        when(mockStorage.addPage(same(before.getId()), any(), any(), any())).thenReturn(before);
        when(mockStorage.addPage(same(equal.getId()), any(), any(), any())).thenReturn(equal);
        when(mockStorage.addPage(same(after.getId()), any(), any(), any())).thenReturn(after);

        AutoPageConfiguration autoPageConfig = mock(AutoPageConfiguration.class);
        when(autoPageConfig.getPageDuration()).thenReturn(duration);
        when(autoPageConfig.getPageStartTime()).thenReturn(baseTimeBefore ? now.minus(duration) : now.plus(duration));


        Map<String, AutoPageConfiguration> mapping = Map.of(
                longTimeBefore.getId().getName(), autoPageConfig,
                before.getId().getName(), autoPageConfig,
                equal.getId().getName(), autoPageConfig,
                after.getId().getName(), autoPageConfig
        );
        try (PageManager ignored = new PageManager(mockStorage, mapping, 60*60*12, threshold)) {
            verify(mockStorage).refreshBook(longTimeBefore.getId().getName());
            verify(mockStorage).refreshBook(before.getId().getName());
            verify(mockStorage).refreshBook(equal.getId().getName());
            verify(mockStorage).refreshBook(after.getId().getName());

            verify(mockStorage, timeout(1000))
                    .addPage(same(longTimeBefore.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage)
                    .addPage(same(before.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage)
                    .addPage(same(equal.getId()), any(), eq(now.plus(duration)), any());
            verify(mockStorage, never())
                    .addPage(same(after.getId()), any(), any(), any());

            verifyNoMoreInteractions(mockStorage);
        }
    }

    private BookInfo createBookInfo(Instant lastStarted, String fullName) {
        PageInfo pageInfoMock = mock(PageInfo.class);
        when(pageInfoMock.getStarted()).thenReturn(lastStarted);

        BookInfo bookInfoMock = mock(BookInfo.class);
        when(bookInfoMock.getId()).thenReturn(new BookId(fullName));
        when(bookInfoMock.getLastPage()).thenReturn(pageInfoMock);
        when(bookInfoMock.getFullName()).thenReturn(fullName);
        return bookInfoMock;
    }
}
