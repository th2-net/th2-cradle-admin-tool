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

package com.exactpro.th2.cradle.adm.cli.modes;

import com.exactpro.th2.cradle.adm.cli.params.AbstractBuilder;
import com.exactpro.th2.cradle.adm.cli.params.NewBookCreationParams;


public class NewBookCreationMode extends AbstractMode<NewBookCreationParams> {
	
	@Override
	public void execute() throws Exception {
		checkInit();
		addInfo.accept(String.format("Creating new book: name(%s) created(%s) first page(%s)",
				param.getName(), param.getCreated(), param.getFirstPageName()));
		addInfo.accept(String.format("full name(%s) desc(%s) firstPage(%s)",
				param.getFullName(), param.getDesc(), param.getFirstPageComment()));
		this.cradleStorage.addBook(this.param.toBookToAdd());
		addInfo.accept("Book is successfully created");
	}

	@Override
	public AbstractBuilder<NewBookCreationParams> createBuilder() {
		return new NewBookCreationParams.Builder();
	}
}
