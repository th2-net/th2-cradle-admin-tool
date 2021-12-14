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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BooksListInfo extends SimpleResult {
	
	private List<ResultBookInfo> books;

	public List<ResultBookInfo> getBooks() {
		return books;
	}

	public void addBook(ResultBookInfo book) {
		if (this.books == null) {
			this.books = new ArrayList<>();
		}
		this.books.add(book);
	}

	public static class ResultBookInfo {
		private String bookId;
		private String bookFullName;
		private String bookDesc;
		private Instant bookCreatedTime;
		private ResultPageInfo bookFirstPage;
		private ResultPageInfo bookLastPage;


		public String getBookId() {
			return bookId;
		}

		public ResultBookInfo setBookId(String bookId) {
			this.bookId = bookId;
			return this;
		}

		public String getBookFullName() {
			return bookFullName;
		}

		public ResultBookInfo setBookFullName(String bookFullName) {
			this.bookFullName = bookFullName;
			return this;
		}

		public String getBookDesc() {
			return bookDesc;
		}

		public ResultBookInfo setBookDesc(String bookDesc) {
			this.bookDesc = bookDesc;
			return this;
		}

		public Instant getBookCreatedTime() {
			return bookCreatedTime;
		}

		public ResultBookInfo setBookCreatedTime(Instant bookCreatedTime) {
			this.bookCreatedTime = bookCreatedTime;
			return this;
		}

		public ResultPageInfo getBookFirstPage() {
			return bookFirstPage;
		}

		public ResultBookInfo setBookFirstPage(ResultPageInfo bookFirstPage) {
			this.bookFirstPage = bookFirstPage;
			return this;
		}

		public ResultPageInfo getBookLastPage() {
			return bookLastPage;
		}

		public ResultBookInfo setBookLastPage(ResultPageInfo bookLastPage) {
			this.bookLastPage = bookLastPage;
			return this;
		}
	}

	public static class ResultPageInfo {
		private String pageId;
		private boolean isActive;
		private String comment;
		private Instant started;
		private Instant ended;

		public String getPageId() {
			return pageId;
		}

		public ResultPageInfo setPageId(String pageId) {
			this.pageId = pageId;
			return this;
		}

		public boolean isActive() {
			return isActive;
		}

		public ResultPageInfo setActive(boolean active) {
			isActive = active;
			return this;
		}

		public String getComment() {
			return comment;
		}

		public ResultPageInfo setComment(String comment) {
			this.comment = comment;
			return this;
		}

		public Instant getStarted() {
			return started;
		}

		public ResultPageInfo setStarted(Instant started) {
			this.started = started;
			return this;
		}

		public Instant getEnded() {
			return ended;
		}

		public ResultPageInfo setEnded(Instant ended) {
			this.ended = ended;
			return this;
		}
	}
}
