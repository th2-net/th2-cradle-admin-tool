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

import com.exactpro.th2.cradle.adm.params.NewBookCreationParams;
import com.exactpro.th2.cradle.adm.params.ParamUtils;

import java.util.Map;

import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.BOOK_NAME;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.CREATED_TIME;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.DESC;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.FIRST_PAGE_COMMENT;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.FIRST_PAGE_NAME;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.FULL_NAME;

public class NewBookCreationParamsBuilder extends HttpParamBuilder<NewBookCreationParams> {

	@Override
	public NewBookCreationParams fromMap(Map<String, String> request) {
		NewBookCreationParams arg = new NewBookCreationParams();
		arg.setName(request.get(BOOK_NAME));
		arg.setCreated(ParamUtils.parseInstant(request.get(CREATED_TIME)));
		arg.setFirstPageName(request.get(FIRST_PAGE_NAME));
		arg.setFullName(request.get(FULL_NAME));
		arg.setDesc(request.get(DESC));
		arg.setFirstPageComment(request.get(FIRST_PAGE_COMMENT));

		return arg;
	}

	@Override
	protected String[] mandatoryOptions() {
		return new String[] {BOOK_NAME, FIRST_PAGE_NAME};
	}

	@Override
	protected String header() {
		return "new book creation parameters";
	}
}
