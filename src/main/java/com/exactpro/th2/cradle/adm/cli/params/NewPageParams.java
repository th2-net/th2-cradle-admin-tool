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

package com.exactpro.th2.cradle.adm.cli.params;

import com.exactpro.cradle.BookId;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.time.Instant;

public class NewPageParams {
	
	private String bookId;
	
	private String pageName;
	private Instant pageStart;
	private String pageComment;

	private NewPageParams() {
	}

	public BookId getBookId() {
		return new BookId(bookId);
	}

	public String getPageName() {
		return pageName;
	}

	public Instant getPageStart() {
		return pageStart;
	}

	public String getPageComment() {
		return pageComment;
	}

	public static class Builder implements AbstractBuilder<NewPageParams> {

		public static void getOptions(Options options) {
			options.addOption(Option.builder(CmdParams.BOOK_ID).required(false).hasArg(true).build());
			options.addOption(Option.builder(CmdParams.PAGE_NAME).required(false).hasArg(true).build());
			options.addOption(Option.builder(CmdParams.PAGE_START_TIME).required(false).hasArg(true).build());
			options.addOption(Option.builder(CmdParams.PAGE_COMMENT).required(false).hasArg(true).build());
		}

		public NewPageParams fromCommandLine(CommandLine commandLine) {
			NewPageParams arg = new NewPageParams();
			arg.bookId = commandLine.getOptionValue(CmdParams.BOOK_ID);
			arg.pageName = commandLine.getOptionValue(CmdParams.PAGE_NAME);
			arg.pageStart = ParamUtils.parseInstant(commandLine.getOptionValue(CmdParams.PAGE_START_TIME));
			arg.pageComment = commandLine.getOptionValue(CmdParams.PAGE_COMMENT);
			return arg;
		}

		@Override
		public String header() {
			return "new page creation params";
		}

		@Override
		public String[] mandatoryOptions() {
			return new String[] {CmdParams.BOOK_ID, CmdParams.PAGE_NAME, CmdParams.PAGE_START_TIME};
		}
	}
}
