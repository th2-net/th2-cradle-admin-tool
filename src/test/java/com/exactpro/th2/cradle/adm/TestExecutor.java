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

package com.exactpro.th2.cradle.adm;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.PageToAdd;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestExecutor {

    public List<BookToAdd> booksToAdd;
    public List<Pair<BookId, PageToAdd>> pageIds;

    public TestExecutor addBookIds(BookToAdd bookToAdd) {
        if (this.booksToAdd == null)
            this.booksToAdd = new ArrayList<>();
        this.booksToAdd.add(bookToAdd);
        return this;
    }

    public TestExecutor addBookIds(String name, Instant created) {
        return this.addBookIds(new BookToAdd(name, created));
    }

    public TestExecutor addPageIds(BookId bookId, PageToAdd pageToAdd) {
        if (this.pageIds == null)
            this.pageIds = new ArrayList<>();
        this.pageIds.add(new ImmutablePair<>(bookId, pageToAdd));
        return this;
    }

    public TestExecutor addPageIds(BookId bookId, String name, Instant start, String comment) {
        return this.addPageIds(bookId, new PageToAdd(name, start, comment));
    }

    public TestExecutor addPageIds(String bookId, String name, Instant start, String comment) {
        return this.addPageIds(new BookId(bookId), name, start, comment);
    }

    public void execTest(TestExec func) throws Exception {

        TestCradleManager testCradleManager = new TestCradleManager();
        TestCradleStorage storage = testCradleManager.getStorage();
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

        TestCommonFactory testCommonFactory = new TestCommonFactory(testCradleManager);
        try (MockedStatic<CommonFactory> commonFactory = Mockito.mockStatic(CommonFactory.class)) {
            commonFactory.when(() -> CommonFactory.createFromArguments(ArgumentMatchers.any())).then(action -> testCommonFactory);
            func.apply(storage);
        }
    }

    public interface TestExec {
        void apply(TestCradleStorage storage) throws Exception;
    }

}
