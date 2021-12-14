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

import com.exactpro.th2.cradle.adm.params.NewPageParams;
import com.exactpro.th2.cradle.adm.params.ParamUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class NewPageParamsBuilder extends CommandLineBuilder<NewPageParams> {

	public static void getOptions(Options options) {
		options.addOption(Option.builder(CmdParams.BOOK_ID).required(false).hasArg(true).build());
		options.addOption(Option.builder(CmdParams.PAGE_NAME).required(false).hasArg(true).build());
		options.addOption(Option.builder(CmdParams.PAGE_START_TIME).required(false).hasArg(true).build());
		options.addOption(Option.builder(CmdParams.PAGE_COMMENT).required(false).hasArg(true).build());
	}

	public NewPageParams fromCommandLine(CommandLine commandLine) {
		NewPageParams arg = new NewPageParams();
		arg.setBookId(commandLine.getOptionValue(CmdParams.BOOK_ID));
		arg.setPageName(commandLine.getOptionValue(CmdParams.PAGE_NAME));
		arg.setPageStart(ParamUtils.parseInstant(commandLine.getOptionValue(CmdParams.PAGE_START_TIME)));
		arg.setPageComment(commandLine.getOptionValue(CmdParams.PAGE_COMMENT));
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
