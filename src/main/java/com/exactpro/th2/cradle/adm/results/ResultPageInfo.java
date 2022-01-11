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

package com.exactpro.th2.cradle.adm.results;

import java.time.Instant;

public class ResultPageInfo {

	private String pageId;
	private boolean isActive;
	private String comment;
	private Instant started;
	private Instant ended;

	public String getPageId() {
		return pageId;
	}

	public ResultPageInfo setPageId(String pageId) {
		this.pageId = pageId;
		return this;
	}

	public boolean isActive() {
		return isActive;
	}

	public ResultPageInfo setActive(boolean active) {
		isActive = active;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public ResultPageInfo setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Instant getStarted() {
		return started;
	}

	public ResultPageInfo setStarted(Instant started) {
		this.started = started;
		return this;
	}

	public Instant getEnded() {
		return ended;
	}

	public ResultPageInfo setEnded(Instant ended) {
		this.ended = ended;
		return this;
	}
	
}
