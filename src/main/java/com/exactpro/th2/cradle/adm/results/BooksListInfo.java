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

package com.exactpro.th2.cradle.adm.results;

import java.util.ArrayList;
import java.util.List;

public class BooksListInfo extends SimpleResult {
	
	private List<ResultBookInfo> books;

	public BooksListInfo() {
		super();
	}

	public BooksListInfo(Throwable error) {
		super(error);
	}

	public BooksListInfo(String info) {
		super(info);
	}

	public List<ResultBookInfo> getBooks() {
		return books;
	}

	public void addBook(ResultBookInfo book) {
		if (this.books == null) {
			this.books = new ArrayList<>();
		}
		this.books.add(book);
	}

	
}
