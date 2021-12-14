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

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.results.SimpleResult;


public abstract class AbstractMode<T, K extends SimpleResult> {
	
	protected CradleStorage cradleStorage;
	protected T param;
	
	public void init(CradleStorage cradleStorage) {
		this.cradleStorage = cradleStorage;
	}
	
	public abstract K execute();
	
	protected boolean requiredParams() {
		return true;
	}
	
	protected void checkInit() throws InitException {
		if (cradleStorage == null) {
			throw new InitException("Cradle storage is not initialized");
		}
		if (requiredParams() && param == null) {
			throw new InitException("Params is not read");
		}
	}

	public boolean prepareStorage() {
		return false;
	}
	
	
}
