/*******************************************************************************
 * Copyright 2021-2021 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.cradle.adm.params.NoParams;
import com.exactpro.th2.cradle.adm.results.BooksListInfo;
import com.exactpro.th2.cradle.adm.results.ResultBookInfo;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


public class GetAllBooksMode extends AbstractMode<NoParams, BooksListInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetAllBooksMode.class);

	@Override
	public BooksListInfo execute() {

		try {
			cacheAllBooks();
		} catch (CradleStorageException e) {
			return new BooksListInfo(e);
		}

		Collection<BookInfo> books = cradleStorage.getBooks();
		BooksListInfo resBooks = new BooksListInfo(); 
		for (BookInfo book : books) {
			resBooks.addBook(ResultConverter.fromBookInfo(book, new ResultBookInfo()));
		}
		return resBooks;
	}

	protected boolean requiredParams() {
		return false;
	}

	/**
	 * Loads and caches all books in Cradle's storage.
	 */
	private void cacheAllBooks() throws CradleStorageException {
		for (var bookEntry : cradleStorage.listBooks()) {
			try {
				cradleStorage.refreshBook(bookEntry.getName());
			} catch (CradleStorageException e) {
				LOGGER.info("Could not load book '{}'. Error: '{}'.", bookEntry.getName(), e.getMessage());
			}
		}
	}
}
