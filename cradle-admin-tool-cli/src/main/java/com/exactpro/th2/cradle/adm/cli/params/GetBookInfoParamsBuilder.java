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

import com.exactpro.th2.cradle.adm.params.GetBookInfoParams;
import com.exactpro.th2.cradle.adm.params.ParamUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Arrays;

import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.*;

public class GetBookInfoParamsBuilder extends CommandLineBuilder<GetBookInfoParams> {

	public static void getOptions(Options options) {
		options.addOption(Option.builder(BOOK_ID).hasArg(true).required(false).build());
		options.addOption(Option.builder(WITH_PAGES).hasArg(true).required(false).build());
		options.addOption(Option.builder(LOAD_REMOVED_PAGES).hasArg(true).required(false).build());
	}

	@Override
	public GetBookInfoParams fromCommandLine(CommandLine commandLine) {
		GetBookInfoParams arg = new GetBookInfoParams();

		Arrays.stream(commandLine.getOptionValues(BOOK_ID)).forEach(arg::addBookId);
		arg.setWithPages(ParamUtils.getBoolean(commandLine.getOptionValue(WITH_PAGES), true));
		arg.setLoadRemovedPages(ParamUtils.getBoolean(commandLine.getOptionValue(LOAD_REMOVED_PAGES), false));

		return arg;
	}

	@Override
	public String header() {
		return "get book info parameters";
	}

	@Override
	public String[] mandatoryOptions() {
		return new String[] {BOOK_ID};
	}
}
