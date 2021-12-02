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
import com.exactpro.th2.cradle.adm.cli.params.NewPageParams;

public class NewPageCreationMode extends AbstractMode<NewPageParams> {

	@Override
	public void execute() throws Exception {
		checkInit();
		addInfo.accept(String.format("Creating new page: bookId(%s) name(%s) pageStart(%s) comment(%s)",
				param.getBookId(), param.getPageName(), param.getPageStart(), param.getPageComment()));
		this.cradleStorage.addPage(param.getBookId(), param.getPageName(), param.getPageStart(), param.getPageComment());
		addInfo.accept("Page is successfully created");
	}

	@Override
	public AbstractBuilder<NewPageParams> createBuilder() {
		return new NewPageParams.Builder();
	}
}