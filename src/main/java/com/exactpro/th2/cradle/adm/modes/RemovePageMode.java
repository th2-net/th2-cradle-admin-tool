/*
 * Copyright 2022-2024 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageInfo;
import com.exactpro.cradle.counters.Interval;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.cradle.adm.params.RemovePageParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Iterator;
import java.util.Objects;

public class RemovePageMode extends AbstractMode<RemovePageParams, SimpleResult> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemovePageMode.class);
	private static final Interval INTERVAL = new Interval(Instant.MIN, Instant.MAX);
	
	@Override
	public SimpleResult execute() {
		try {
			checkInit();
			LOGGER.info("Search page: {} in book: {}", param.getBookId(), param.getPageName());
			PageId pageId = search();
			if (pageId == null) {
				throw new IllegalStateException(
						"Book '" + param.getBookId().getName() + "' doesn't contain page '" + param.getPageName() + "'"
				);
			}
			LOGGER.info("Removing page: pageId({}})", pageId);
			this.cradleStorage.removePage(pageId);
			LOGGER.info("Page is successfully removed");
			return new SimpleResult("Page removed " + pageId);
		} catch (Exception e) {
			LOGGER.error("Error creating page", e);
			return new SimpleResult(e);
		}
	}

	private PageId search() throws CradleStorageException {
		Iterator<PageInfo> iterator = this.cradleStorage.getPages(param.getBookId(), INTERVAL);
		while (iterator.hasNext()) {
			PageInfo pageInfo = iterator.next();
			if (Objects.equals(pageInfo.getName(), param.getPageName())) {
				return pageInfo.getId();
			}
		}
		return null;
	}
}
