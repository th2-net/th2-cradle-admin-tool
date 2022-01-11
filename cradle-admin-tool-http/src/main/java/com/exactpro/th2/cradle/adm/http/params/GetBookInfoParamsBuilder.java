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

import com.exactpro.th2.cradle.adm.params.GetBookInfoParams;
import com.exactpro.th2.cradle.adm.params.ParamUtils;
import com.exactpro.th2.cradle.adm.params.RemovePageParams;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

public class GetBookInfoParamsBuilder extends HttpParamBuilder<GetBookInfoParams> {

	@Override
	public GetBookInfoParams fromHttpRequest(HttpServletRequest request) {
		GetBookInfoParams arg = new GetBookInfoParams();
		Arrays.stream(request.getParameterValues(HttpParamConst.BOOK_ID)).forEach(arg::addBookId);
		arg.setWithPages(ParamUtils.getBoolean(request.getParameter(HttpParamConst.WITH_PAGES), true));
		return arg;
	}
	
	@Override
	public GetBookInfoParams fromMap(Map<String, String> request) {
		GetBookInfoParams arg = new GetBookInfoParams();
		arg.addBookId(request.get(HttpParamConst.BOOK_ID));
		arg.setWithPages(ParamUtils.getBoolean(request.get(HttpParamConst.WITH_PAGES), true));
		return arg;
	}

	@Override
	protected String header() {
		return "get book info parameters";
	}

	@Override
	protected String[] mandatoryOptions() {
		return new String[] {HttpParamConst.BOOK_ID} ;
	}
}
