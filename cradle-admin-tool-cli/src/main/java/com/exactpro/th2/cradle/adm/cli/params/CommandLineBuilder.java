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

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandLineBuilder<T> {

	public abstract T fromCommandLine(CommandLine commandLine) ;
	protected abstract String header() ;
	protected String[] mandatoryOptions() { return new String[0]; }

	public void checkMandatoryOptions(CommandLine cmdLine) throws InvalidConfigurationException {
		List<String> missingOpts = null;
		for (String opt : this.mandatoryOptions()) {
			if (!cmdLine.hasOption(opt)) {
				if (missingOpts == null) {
					missingOpts = new ArrayList<>();
				}
				missingOpts.add(opt);
			}
		}

		if (missingOpts != null) {
			throw new InvalidConfigurationException("These options is mandatory for " + header()
					+ " and should be specified: " + missingOpts);
		}
	}
	
}
