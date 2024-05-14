/*******************************************************************************
 * Copyright 2021-2022 Exactpro (Exactpro Systems Limited)
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

import com.exactpro.th2.cradle.adm.results.SimpleResult;
import com.exactpro.th2.cradle.adm.params.NewBookCreationParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class NewBookCreationMode extends AbstractMode<NewBookCreationParams, SimpleResult> {

	private static final Logger logger = LoggerFactory.getLogger(NewBookCreationMode.class);
	
	@Override
	public SimpleResult execute() {
		try {
			checkInit();
			Instant createdTime = param.getCreated();
			if (createdTime == null) {
				createdTime = Instant.now();
				param.setCreated(createdTime);
				logger.info("'Created' book time is not specified. Generated value: {}", createdTime);
			}

			logger.info("Creating new book: name({}}) created({}})", param.getName(), createdTime);
			logger.info("full name({}) desc({}})", param.getFullName(), param.getDesc());
		
			this.cradleStorage.addBook(this.param.toBookToAdd());
			logger.info("Book is successfully created");
			StringBuilder sb = new StringBuilder("Book created ");
			fillBookParams(sb);
			return new SimpleResult(sb.toString());
		} catch (Exception e) {
			logger.error("Error creating new book", e);
			return new SimpleResult(e);
		}
	}
	
	private void fillBookParams(StringBuilder sb) {
		sb.append("name = ").append(param.getName());
		sb.append(",created = ").append(param.getCreated());

		if (param.getFullName() != null) {
			sb.append(",fullName = ").append(param.getFullName());
		}
		if (param.getDesc() != null) {
			sb.append(",desc = ").append(param.getDesc());
		}
	}
	
}
