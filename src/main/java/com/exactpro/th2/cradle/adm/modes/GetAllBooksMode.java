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
import com.exactpro.th2.cradle.adm.params.GetAllBooksParams;
import com.exactpro.th2.cradle.adm.results.BooksListInfo;
import com.exactpro.th2.cradle.adm.results.ResultBookInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class GetAllBooksMode extends AbstractMode<GetAllBooksParams, BooksListInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetAllBooksMode.class);

	@Override
	public BooksListInfo execute() {

		try {
			cacheAllBooks();
		} catch (CradleStorageException e) {
			return new BooksListInfo(e);
		}

		List<BookInfo> books = new ArrayList<>(cradleStorage.getBooks());

		books = books.stream().filter(el ->
				el.getCreated().isAfter(param.getFrom())
				&& el.getCreated().isBefore(param.getTo())).collect(Collectors.toList());

		books.sort((Comparator) (o1, o2) -> {
			BookInfo info1 = (BookInfo) o1;
			BookInfo info2 = (BookInfo) o2;

			Instant ins1 = info1.getCreated();
			Instant ins2 = info2.getCreated();

			String name1 = info1.getId().getName();
			String name2 = info2.getId().getName();

			if (!param.getCreationSort().equals(GetAllBooksParams.SortType.NONE)) {
				if (!info1.getCreated().equals(info2.getCreated())) {
					switch (param.getCreationSort()) {
						case ASC:
							return ins1.compareTo(ins2);
						case DESC:
							return ins2.compareTo(ins1);
					}
				}
			}

			switch (param.getNameSort()) {
				case ASC:
					return name1.compareTo(name2);
				case DESC:
					return name2.compareTo(name1);
			}

			return 0;
		});

		BooksListInfo resBooks;
		if (books.isEmpty()) {
			resBooks = new BooksListInfo("No books for given interval");
		} else {
			resBooks = new BooksListInfo();
		}
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
