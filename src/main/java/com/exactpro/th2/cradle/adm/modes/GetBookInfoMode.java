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

package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.cradle.adm.params.GetBookInfoParams;
import com.exactpro.th2.cradle.adm.results.BooksListInfo;
import com.exactpro.th2.cradle.adm.results.ResultBookDetailedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


public class GetBookInfoMode extends AbstractMode<GetBookInfoParams, BooksListInfo> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetBookInfoMode.class);

	@Override
	public BooksListInfo execute() {

		cacheBooks();

		BooksListInfo resBooks = new BooksListInfo();
		
		cradleStorage.getBooks().stream().filter(bookInfo
				-> param.getBookIds().contains(bookInfo.getId())).map(bookInfo ->
		{

			var resultBookInfo = ResultConverter.fromBookInfo(bookInfo, new ResultBookDetailedInfo());

			if (param.isWithPages()) {
				Collection<PageInfo> pages = param.shouldLoadRemovedPages() ? loadAllPages(bookInfo) : bookInfo.getPages();

				for (PageInfo page : pages) {
					resultBookInfo.addPages(ResultConverter.fromPageInfo(page));
				}
			}
			
			return resultBookInfo;
			
		}).forEach(resBooks::addBook);

		if (resBooks.getBooks() == null || resBooks.getBooks().isEmpty()) {
			resBooks.failed("No books found by given params");
		}

		return resBooks;

	}

	protected boolean requiredParams() {
		return true;
	}

	/**
	 * Loads and caches all specified books in Cradle's storage. Books that cannot be loaded are skipped.
	 */
	private void cacheBooks() {
		for(var book : param.getBookIds()) {
			try {
				cradleStorage.refreshBook(book.getName());
			} catch (CradleStorageException e) {
				LOGGER.info("Could not load book '{}'. Error: '{}'.", book.getName(), e.getMessage());
			}
		}
	}

	/**
	 * Loads all pages for the specified book, including removed ones.
	 */
	private Collection<PageInfo> loadAllPages(BookInfo bookInfo) {
		try {
			return cradleStorage.getAllPages(bookInfo.getId());
		} catch (CradleStorageException e) {
			LOGGER.info("Could not load all pages for book '{}'. Error: '{}'", bookInfo.getId().getName(), e.getMessage());
			return bookInfo.getPages();
		}
	}
}
