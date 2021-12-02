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

package com.exactpro.th2.cradle.adm.cli.modes;

import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.cradle.adm.cli.params.AbstractBuilder;
import com.exactpro.th2.cradle.adm.cli.params.NoParams;


public class GetAllBooksMode extends AbstractMode<NoParams> {

	private void printPage(PageInfo page) {
		addInfo.accept("\tPageId: " + page.getId().getName());
		addInfo.accept("\tIsActive: " + page.isActive());
		addInfo.accept("\tComment: " + page.getComment());
		addInfo.accept("\tStarted: " + page.getStarted());
		addInfo.accept("\tEnded: " + page.getEnded());
	}
	
	@Override
	public void execute() throws Exception {

		for (BookInfo book : cradleStorage.getBooks()) {
			addInfo.accept("Book Id");
			addInfo.accept("\tBook Name: " + book.getId().getName());
			addInfo.accept("Book Full name: " + book.getFullName());
			addInfo.accept("Book Desc: " + book.getDesc());
			addInfo.accept("Book Created time: " + book.getCreated());
			addInfo.accept("Book First page");
			printPage(book.getFirstPage());
			addInfo.accept("Book Last page");
			printPage(book.getLastPage());
			addInfo.accept("");
			addInfo.accept("");
		}
		
		
	}

	@Override
	public AbstractBuilder<NoParams> createBuilder() {
		return new NoParams.Builder();
	}

	protected boolean requiredParams() {
		return false;
	}
}
