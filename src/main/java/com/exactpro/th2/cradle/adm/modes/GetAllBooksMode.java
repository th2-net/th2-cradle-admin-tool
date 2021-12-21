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
import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.cradle.adm.params.NoParams;
import com.exactpro.th2.cradle.adm.results.BooksListInfo;
import com.exactpro.th2.cradle.adm.results.BooksListInfo.ResultBookInfo;
import com.exactpro.th2.cradle.adm.results.BooksListInfo.ResultPageInfo;

import java.util.Collection;


public class GetAllBooksMode extends AbstractMode<NoParams, BooksListInfo> {

	private ResultPageInfo printPage(PageInfo page) {
		if (page == null)
			return null;
		ResultPageInfo result = new ResultPageInfo();
		result.setPageId(page.getId() == null ? null: page.getId().getName());
		result.setActive(page.isActive());
		result.setComment(page.getComment());
		result.setStarted(page.getStarted());
		result.setEnded(page.getEnded());
		return result;
	}
	
	@Override
	public BooksListInfo execute() {

		Collection<BookInfo> books = cradleStorage.getBooks();
		BooksListInfo resBooks = new BooksListInfo(); 
		for (BookInfo book : books) {
			ResultBookInfo resultBookInfo = new ResultBookInfo();
			resultBookInfo.setBookId(book.getId().getName());
			resultBookInfo.setBookFullName(book.getFullName());
			resultBookInfo.setBookDesc(book.getDesc());
			resultBookInfo.setBookCreatedTime(book.getCreated());
			
			resultBookInfo.setBookFirstPage(printPage(book.getFirstPage()));
			resultBookInfo.setBookLastPage(printPage(book.getLastPage()));
			resBooks.addBook(resultBookInfo);
		}
		return resBooks;
		
	}

	protected boolean requiredParams() {
		return false;
	}
	
}
