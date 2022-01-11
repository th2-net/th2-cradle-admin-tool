/*******************************************************************************
 * Copyright 2022-2022 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.utils.CradleStorageException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class HttpModesUtils {
	
	public static void refreshBookPages(CradleStorage cradleStorage, final BookId bookId) throws IOException, CradleStorageException {
		refreshBookPages(cradleStorage, Collections.singleton(bookId));
	}

	public static void refreshBookPages(CradleStorage cradleStorage, Collection<BookId> bookIds) throws IOException, CradleStorageException {

		HashSet<BookId> bookSet = new HashSet<>(bookIds);
		var iterator = cradleStorage.getBooks().iterator();
		while (!bookSet.isEmpty() && iterator.hasNext()) {
			bookSet.remove(iterator.next().getId());
		}

		//books don't exist
		if (!bookSet.isEmpty())  {
			cradleStorage.refreshBooks();
		}

		for (BookId bookId : bookIds) {
			cradleStorage.refreshPages(bookId);
		}
	}

	public static void refreshAllBookPages(CradleStorage cradleStorage) throws IOException, CradleStorageException {
		Collection<BookInfo> oldBooks = cradleStorage.getBooks();
		for (BookInfo oldBook : oldBooks) {
			cradleStorage.refreshPages(oldBook.getId());
		}
		
		//load new books
		cradleStorage.refreshBooks();
	}
	
}
