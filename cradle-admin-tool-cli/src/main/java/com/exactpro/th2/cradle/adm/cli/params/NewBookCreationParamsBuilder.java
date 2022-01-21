/*******************************************************************************
 * Copyright 2021-2022 Exactpro (Exactpro Systems Limited)
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

import com.exactpro.th2.cradle.adm.params.NewBookCreationParams;
import com.exactpro.th2.cradle.adm.params.ParamUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.BOOK_NAME;
import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.CREATED_TIME;
import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.DESC;
import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.FIRST_PAGE_COMMENT;
import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.FIRST_PAGE_NAME;
import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.FULL_NAME;

public class NewBookCreationParamsBuilder extends CommandLineBuilder<NewBookCreationParams> {

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
		arg.setName(commandLine.getOptionValue(BOOK_NAME));
		arg.setCreated(ParamUtils.parseInstant(commandLine.getOptionValue(CREATED_TIME)));
		arg.setFirstPageName(commandLine.getOptionValue(FIRST_PAGE_NAME));
		arg.setFullName(commandLine.getOptionValue(FULL_NAME));
		arg.setDesc(commandLine.getOptionValue(DESC));
		arg.setFirstPageComment(commandLine.getOptionValue(FIRST_PAGE_COMMENT));
		
		return arg;
	}

	@Override
	public String header() {
		return "new book creation parameters";
	}

	@Override
	public String[] mandatoryOptions() {
		return new String[] {BOOK_NAME};
	}
}
