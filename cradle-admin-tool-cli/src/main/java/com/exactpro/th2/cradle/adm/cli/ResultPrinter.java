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

package com.exactpro.th2.cradle.adm.cli;

import com.exactpro.cradle.BookListEntry;
import com.exactpro.th2.cradle.adm.results.*;

import java.util.List;

public class ResultPrinter {
	
	public static void printToCmd(SimpleResult r) {
		System.out.println(r.isSuccess() ? "Success" : "Failed");
		if (r.getInfo() != null)
			System.out.println(r.getInfo());
		if (r.getError() != null)
			r.getError().printStackTrace(System.out);
		if (r instanceof BooksListInfo) {
			int count = 1;
			BooksListInfo bookListInfo = (BooksListInfo) r;
			List<ResultBookInfo> books = bookListInfo.getBooks();
			if (books != null && !books.isEmpty()) {
				for (ResultBookInfo book : books) {
					System.out.println();
					System.out.println("book #" + count);
					printBookToCmd(book);
					count++;
				}
			}
		}
	}
	
	private static void printBookToCmd(ResultBookInfo bookInfo) {
		System.out.println("\tBookId: " + bookInfo.getBookId());
		System.out.println("\tBookCreatedTime: " + bookInfo.getBookCreatedTime());
		if (bookInfo.getBookDesc() != null)
			System.out.println("\tBookDesc: " + bookInfo.getBookDesc());
		if (bookInfo.getBookFullName() != null)
			System.out.println("\tBookFullName: " + bookInfo.getBookFullName());
		if (bookInfo instanceof ResultBookDetailedInfo) {
			List<ResultPageInfo> pages = ((ResultBookDetailedInfo) bookInfo).getPages();
			int i = 1;
			for (ResultPageInfo page : pages) {
				System.out.println("\tPage #" + i++);
				printPageToCmd(page, "\t\t");
			}
		}
	}

	private static void printPageToCmd(ResultPageInfo pageInfo, String prefix) {
		System.out.println(prefix + "PageId: " + pageInfo.getPageId());
		if (pageInfo.getComment() != null)
			System.out.println(prefix + "Comment: " + pageInfo.getComment());
		System.out.println(prefix + "Started: " + pageInfo.getStarted());
		if (pageInfo.getEnded() != null)
			System.out.println(prefix + "Ended: " + pageInfo.getEnded());
		if (pageInfo.getUpdated() != null)
			System.out.println(prefix + "Updated: " + pageInfo.getUpdated());
		if (pageInfo.getRemoved() != null) {
			System.out.println(prefix + "Removed: " + pageInfo.getRemoved());
		}
	}
}
