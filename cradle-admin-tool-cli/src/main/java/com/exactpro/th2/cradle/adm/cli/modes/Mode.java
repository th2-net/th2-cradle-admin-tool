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

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.cli.params.*;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Mode {

	public static void getModeOpts(Options options) {
		options.addOption(Option.builder().longOpt(CmdParams.ALL_BOOKS_L).hasArg(false)
				.required(false).desc("Getting info about books").build());
		options.addOption(Option.builder().longOpt(CmdParams.ALL_BOOKS_SCHEMA_L).hasArg(false)
				.required(false).desc("Getting nae and schema about books").build());
		GetAllBooksParamBuilder.getOptions(options);
		options.addOption(Option.builder().longOpt(CmdParams.BOOK_INFO_L).hasArg(false)
				.required(false).desc("Getting info about chosen books").build());
		GetBookInfoParamsBuilder.getOptions(options);
		options.addOption(Option.builder(CmdParams.MODE_BOOK_S).longOpt(CmdParams.MODE_BOOK_L).hasArg(false)
				.required(false).desc("Create new book mode").build());
		NewBookCreationParamsBuilder.getOptions(options);
		options.addOption(Option.builder(CmdParams.MODE_PAGE_S).longOpt(CmdParams.MODE_PAGE_L).hasArg(false)
				.required(false).desc("Add a page to existed book mode").build());
		NewPageParamsBuilder.getOptions(options);
		options.addOption(Option.builder().longOpt(CmdParams.REMOVE_PAGE_L).hasArg(false)
				.required(false).desc("Removing a page from existed book mode").build());
		RemovePageParamsBuilder.getOptions(options);
		options.addOption(Option.builder().longOpt(CmdParams.UPDATE_PAGE).hasArg(false)
				.required(false).desc("Update page name/comment").build());
		UpdatePageParamBuilder.getOptions(options);

	}

	public static AbstractMode<?, ?> getMode(CommandLine cmdLine) throws InvalidConfigurationException {

		if (cmdLine.hasOption(CmdParams.MODE_BOOK_S)) {
			return new NewBookCreationCliMode();
		} else if (cmdLine.hasOption(CmdParams.MODE_PAGE_S)) {
			return new NewPageCreationCliMode();
		} else if (cmdLine.hasOption(CmdParams.ALL_BOOKS_L)) {
			return new GetAllBooksCreationCliMode();
		} else if (cmdLine.hasOption(CmdParams.ALL_BOOKS_SCHEMA_L)){
			return new ListAllBookSchemasCliMode();
		} else if (cmdLine.hasOption(CmdParams.REMOVE_PAGE_L)) {
			return new RemovePageCliMode();
		} else if (cmdLine.hasOption(CmdParams.BOOK_INFO_L)) {
			return new GetBookInfoCliMode();
		} else if (cmdLine.hasOption(CmdParams.UPDATE_PAGE)) {
			return new UpdatePageCliMode();
		} else {
			throw new InvalidConfigurationException("Mode for application didn't specified. (--%s or -%s supported)",
					CmdParams.MODE_BOOK_L, CmdParams.MODE_PAGE_L);
		}
	}

}
