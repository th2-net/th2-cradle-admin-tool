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

package com.exactpro.th2.cradle.adm.cli.params;

import com.exactpro.th2.cradle.adm.params.RemovePageParams;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class RemovePageParamsBuilder extends CommandLineBuilder<RemovePageParams> {

	public static void getOptions(Options options) {
		options.addOption(Option.builder(CmdParams.BOOK_ID).required(false).hasArg(true).build());
		options.addOption(Option.builder(CmdParams.PAGE_NAME).required(false).hasArg(true).build());
	}

	public RemovePageParams fromCommandLine(CommandLine commandLine) {
		RemovePageParams arg = new RemovePageParams();
		arg.setBookId(commandLine.getOptionValue(CmdParams.BOOK_ID));
		arg.setPageName(commandLine.getOptionValue(CmdParams.PAGE_NAME));
		return arg;
	}

	@Override
	public String header() {
		return "removing page params";
	}

	@Override
	public String[] mandatoryOptions() {
		return new String[] {CmdParams.BOOK_ID, CmdParams.PAGE_NAME};
	}
}
