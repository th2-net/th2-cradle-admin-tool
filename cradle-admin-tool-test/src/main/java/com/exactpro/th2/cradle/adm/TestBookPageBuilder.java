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

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageToAdd;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestBookPageBuilder {
    public List<BookToAdd> booksToAdd;
    public List<Pair<BookId, PageToAdd>> pageIds;

    private TestBookPageBuilder() { }

    public TestBookPageBuilder addBookIds(BookToAdd bookToAdd) {
        if (this.booksToAdd == null)
            this.booksToAdd = new ArrayList<>();
        this.booksToAdd.add(bookToAdd);
        return this;
    }

    public TestBookPageBuilder addBookIds(String name, Instant created) {
        return this.addBookIds(new BookToAdd(name, created));
    }

    public TestBookPageBuilder addPageIds(BookId bookId, PageToAdd pageToAdd) {
        if (this.pageIds == null)
            this.pageIds = new ArrayList<>();
        this.pageIds.add(new ImmutablePair<>(bookId, pageToAdd));
        return this;
    }

    public TestBookPageBuilder addPageIds(BookId bookId, String name, Instant start, String comment) {
        return this.addPageIds(bookId, new PageToAdd(name, start, comment));
    }

    public TestBookPageBuilder addPageIds(String bookId, String name, Instant start, String comment) {
        return this.addPageIds(new BookId(bookId), name, start, comment);
    }

    public void exec(CradleStorage storage) throws Exception {
        if (this.booksToAdd != null) {
            for (BookToAdd bta : this.booksToAdd) {
                storage.addBook(bta);
            }
        }
        if (this.pageIds != null) {
            for (Pair<BookId, PageToAdd> pta : this.pageIds) {
                storage.addPages(pta.getKey(), Collections.singletonList(pta.getRight()));
            }
        }
    }

    public static TestBookPageBuilder builder() {
        return new TestBookPageBuilder();
    }
}
