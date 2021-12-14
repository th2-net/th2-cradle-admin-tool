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

package com.exactpro.th2.cradle.adm.http.params;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.servlets.ServletUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class HttpParamBuilder<T> {

	public T fromHttpRequest(HttpServletRequest request)  {
		return fromMap(ServletUtils.getOneValueParameters(request));
	}

	public abstract T fromMap(Map<String, String> request);
	protected String[] mandatoryOptions() { return new String[0]; }
	protected abstract String header() ;

	public void checkMandatoryOptions(Map<String, String> map) throws InvalidConfigurationException {
		checkMandatoryOptions(s -> map.get(s) == null);
	}

	public void checkMandatoryOptions(HttpServletRequest request) throws InvalidConfigurationException {
		checkMandatoryOptions(s -> request.getParameter(s) == null);
	}

	private void checkMandatoryOptions(Function<String, Boolean> isAbsent) throws InvalidConfigurationException {
		List<String> missingOpts = null;
		for (String opt : this.mandatoryOptions()) {
			if (isAbsent.apply(opt)) {
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
