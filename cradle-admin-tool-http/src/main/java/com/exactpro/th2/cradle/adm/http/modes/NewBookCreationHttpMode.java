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

package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.params.HttpParamBuilder;
import com.exactpro.th2.cradle.adm.http.params.NewBookCreationParamsBuilder;
import com.exactpro.th2.cradle.adm.modes.NewBookCreationMode;
import com.exactpro.th2.cradle.adm.params.NewBookCreationParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class NewBookCreationHttpMode extends NewBookCreationMode implements HttpMode<NewBookCreationParams> {

	@Override
	public HttpParamBuilder<NewBookCreationParams> createParamsBuilder() {
		return new NewBookCreationParamsBuilder();
	}
	
	@Override
	public boolean initParams(HttpServletRequest req) throws InvalidConfigurationException {
		this.param = getParams(req);
		return true;
	}
}
