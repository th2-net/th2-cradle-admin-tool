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

package com.exactpro.th2.cradle.adm.http.params;

import com.exactpro.th2.cradle.adm.params.RemovePageParams;

import java.util.Map;

public class RemovePageParamsBuilder extends HttpParamBuilder<RemovePageParams> {

	@Override
	public RemovePageParams fromMap(Map<String, String> request) {
		RemovePageParams arg = new RemovePageParams();
		arg.setBookId(request.get(HttpParamConst.BOOK_ID));
		arg.setPageName(request.get(HttpParamConst.PAGE_NAME));
		return arg;
	}

	@Override
	protected String[] mandatoryOptions() {
		return new String[] {HttpParamConst.BOOK_ID, HttpParamConst.PAGE_NAME};
	}

	@Override
	protected String header() {
		return "removing page parameters";
	}
	
}
