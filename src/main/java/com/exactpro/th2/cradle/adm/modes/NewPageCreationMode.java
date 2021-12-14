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

package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.th2.cradle.adm.params.NewPageParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewPageCreationMode extends AbstractMode<NewPageParams, SimpleResult> {

	private static final Logger logger = LoggerFactory.getLogger(NewPageCreationMode.class);

	@Override
	public SimpleResult execute() {
		try {
			checkInit();
			logger.info("Creating new page: bookId({}}) name({}) pageStart({}) comment({})", param.getBookId(), param.getPageName(), param.getPageStart(), param.getPageComment());
			this.cradleStorage.addPage(param.getBookId(), param.getPageName(), param.getPageStart(), param.getPageComment());
			logger.info("Page is successfully created");
			StringBuilder pageSB = new StringBuilder("Page created ");
			fillPageParams(pageSB);
			return new SimpleResult(pageSB.toString());
		} catch (Exception e) {
			logger.error("Error creating page", e);
			return new SimpleResult(e);
		}

	}

	private void fillPageParams(StringBuilder sb) {
		sb.append("bookId = ").append(param.getBookId());
		sb.append(",pageName = ").append(param.getPageName());
		sb.append(",pageStart = ").append(param.getPageStart());

		if (param.getPageComment() != null) {
			sb.append(",pageCommit = ").append(param.getPageComment());
		}
	}
	
}