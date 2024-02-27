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

package com.exactpro.th2.cradle.adm;

import com.exactpro.cradle.BookCache;
import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TestBookCache implements BookCache {

    Map<BookId, BookInfo> books;

    TestBookCache () {
        books = new ConcurrentHashMap<>();
    }

    @Override
    public BookInfo getBook(BookId bookId) throws CradleStorageException {
        if (!books.containsKey(bookId)) {
            throw new CradleStorageException(String.format("Book %s is unknown", bookId.getName()));
        }
        return books.get(bookId);
    }

    @Override
    public boolean checkBook(BookId bookId) {
        return books.containsKey(bookId);
    }

    @Override
    public Collection<PageInfo> loadPageInfo(BookId bookId, boolean loadRemoved) throws CradleStorageException {
        throw new CradleStorageException("Method not supported");
    }

    @Override
    public Collection<BookInfo> getCachedBooks() {
        return Collections.unmodifiableCollection(books.values());
    }
}

