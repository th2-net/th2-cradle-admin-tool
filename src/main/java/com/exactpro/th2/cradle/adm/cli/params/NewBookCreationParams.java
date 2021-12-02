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

import com.exactpro.cradle.BookToAdd;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.time.Instant;

import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.*;

public class NewBookCreationParams {

	private String name;
	private Instant created;
	private String firstPageName;
	
	private String fullName;
	private String desc;
	private String firstPageComment;

	private NewBookCreationParams() {
	}

	public String getName() {
		return name;
	}

	public Instant getCreated() {
		return created;
	}

	public String getFirstPageName() {
		return firstPageName;
	}

	public String getFullName() {
		return fullName;
	}

	public String getDesc() {
		return desc;
	}

	public String getFirstPageComment() {
		return firstPageComment;
	}

	public BookToAdd toBookToAdd() {
		BookToAdd bookToAdd = new BookToAdd(name, created, firstPageName);
		bookToAdd.setFullName(fullName);
		bookToAdd.setFullName(desc);
		bookToAdd.setFullName(firstPageComment);
		return bookToAdd;
	}

	public static class Builder implements AbstractBuilder<NewBookCreationParams> {
		
		public static void getOptions(Options options) {
			options.addOption(Option.builder(BOOK_NAME).hasArg(true).required(false).build());
			options.addOption(Option.builder(CREATED_TIME).hasArg(true).required(false).build());
			options.addOption(Option.builder(FIRST_PAGE_NAME).hasArg(true).required(false).build());
			options.addOption(Option.builder(FULL_NAME).hasArg(true).required(false).build());
			options.addOption(Option.builder(DESC).hasArg(true).required(false).build());
			options.addOption(Option.builder(FIRST_PAGE_COMMENT).hasArg(true).required(false).build());
		}

		@Override
		public NewBookCreationParams fromCommandLine(CommandLine commandLine) {
			NewBookCreationParams arg = new NewBookCreationParams();
			arg.name = commandLine.getOptionValue(BOOK_NAME);
			arg.created = ParamUtils.parseInstant(commandLine.getOptionValue(CREATED_TIME));
			arg.firstPageName = commandLine.getOptionValue(FIRST_PAGE_NAME);
			arg.fullName = commandLine.getOptionValue(FULL_NAME);
			arg.desc = commandLine.getOptionValue(DESC);
			arg.firstPageComment = commandLine.getOptionValue(FIRST_PAGE_COMMENT);
			
			return arg;
		}

		@Override
		public String header() {
			return "new book creation parameters";
		}

		@Override
		public String[] mandatoryOptions() {
			return new String[] {BOOK_NAME, CREATED_TIME, FIRST_PAGE_NAME};
		}
	}
	
}
