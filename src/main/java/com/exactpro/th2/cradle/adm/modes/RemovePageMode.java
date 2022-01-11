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

package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.th2.cradle.adm.params.RemovePageParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemovePageMode extends AbstractMode<RemovePageParams, SimpleResult> {

	private static final Logger logger = LoggerFactory.getLogger(RemovePageMode.class);
	
	@Override
	public SimpleResult execute() {
		try {
			checkInit();
			logger.info("Removing page: pageId({}})", param.getPageId());
			this.cradleStorage.removePage(param.getPageId());
			logger.info("Page is successfully removed");
			return new SimpleResult("Page removed " + param.getPageId());
		} catch (Exception e) {
			logger.error("Error creating page", e);
			return new SimpleResult(e);
		}
	}
}
