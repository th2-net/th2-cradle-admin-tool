/*
 * Copyright 2021-2024 Exactpro (Exactpro Systems Limited)
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
 */

package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.params.HttpParamBuilder;
import jakarta.servlet.http.HttpServletRequest;


public interface HttpMode<T> {

	HttpParamBuilder<T> createParamsBuilder();
	void initParams(HttpServletRequest commandLine) throws InvalidConfigurationException;

	default T getParams(HttpServletRequest commandLine) throws InvalidConfigurationException {
		var builder = createParamsBuilder();
		builder.checkMandatoryOptions(commandLine);
		return builder.fromHttpRequest(commandLine);
	}

	
}
