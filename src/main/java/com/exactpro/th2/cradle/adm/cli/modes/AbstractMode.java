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

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.cli.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.cli.params.AbstractBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMode<T> {
	
	protected CradleStorage cradleStorage;
	protected T param;
	protected Consumer<String> addInfo;
	
	public void init(CradleStorage cradleStorage, Consumer<String> addInfo) {
		this.addInfo = addInfo;
		this.cradleStorage = cradleStorage;		
	}

	public boolean initParams(CommandLine cmdLine) throws ParseException, InvalidConfigurationException {
		this.param = this.createParams(cmdLine);
		return !requiredParams() || param != null;
	}
	
	public abstract void execute() throws Exception;

	public abstract AbstractBuilder<T> createBuilder();
	
	protected T createParams(CommandLine cmdLine) throws InvalidConfigurationException {
		AbstractBuilder<T> builder = createBuilder();
		checkMandatoryOptions(builder, cmdLine);
		return builder.fromCommandLine(cmdLine);
	}
	
	protected void checkMandatoryOptions(AbstractBuilder<T> builder, CommandLine cmdLine) throws InvalidConfigurationException {
		List<String> missingOpts = null;
		for (String opt : builder.mandatoryOptions()) {
			if (!cmdLine.hasOption(opt)) {
				if (missingOpts == null) {
					missingOpts = new ArrayList<>();
				}
				missingOpts.add(opt);
			}
		}

		if (missingOpts != null) {
			throw new InvalidConfigurationException("These options is mandatory for " + builder.header() 
					+ " and should be specified: " + missingOpts);
		}
	}
	
	protected boolean requiredParams() {
		return true;
	}
	
	protected void checkInit() throws InitException {
		if (cradleStorage == null) {
			throw new InitException("Cradle storage is not initialized");
		}
		if (requiredParams() && param == null) {
			throw new InitException("Params is not read");
		}
	}

	public boolean prepareStorage() {
		return false;
	}
	
	
}
