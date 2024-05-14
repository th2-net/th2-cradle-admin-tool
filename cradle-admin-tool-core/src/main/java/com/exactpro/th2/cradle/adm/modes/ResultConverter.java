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

import com.exactpro.th2.cradle.adm.results.ResultBookInfo;
import com.exactpro.th2.cradle.adm.results.ResultPageInfo;
import com.exactpro.cradle.BookInfo;
import com.exactpro.cradle.PageInfo;

public class ResultConverter {


	public static ResultPageInfo fromPageInfo(PageInfo page) {
		if (page == null)
			return null;
		ResultPageInfo result = new ResultPageInfo();
		result.setPageId(page.getId() == null ? null : page.getId().getName());
		result.setComment(page.getComment());
		result.setStarted(page.getStarted());
		result.setEnded(page.getEnded());
		result.setUpdated(page.getUpdated());
		result.setRemoved(page.getRemoved());
		return result;
	}

	public static <T extends ResultBookInfo> T fromBookInfo(BookInfo bookInfo, T resultBookInfo) {
		resultBookInfo.setBookId(bookInfo.getId().getName());
		resultBookInfo.setBookFullName(bookInfo.getFullName());
		resultBookInfo.setBookDesc(bookInfo.getDesc());
		resultBookInfo.setBookCreatedTime(bookInfo.getCreated());

		return resultBookInfo;
	}
	
}
