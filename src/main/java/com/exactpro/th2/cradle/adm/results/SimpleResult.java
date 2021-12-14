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

package com.exactpro.th2.cradle.adm.results;

public class SimpleResult {
	
	private boolean isSuccess;
	private String info;
	private Throwable error;

	public SimpleResult() {
		this.isSuccess = true;
	}

	public SimpleResult(Throwable error) {
		this.isSuccess = false;
		this.error = error;
	}

	public SimpleResult(String info) {
		this.isSuccess = true;
		this.info = info;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public String getInfo() {
		return info;
	}

	public Throwable getError() {
		return error;
	}
}
