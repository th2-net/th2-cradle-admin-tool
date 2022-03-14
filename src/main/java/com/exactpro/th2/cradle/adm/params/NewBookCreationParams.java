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

package com.exactpro.th2.cradle.adm.params;

import com.exactpro.cradle.BookToAdd;

import java.time.Instant;

public class NewBookCreationParams {

	private String name;
	private Instant created;
	private String firstPageName;
	
	private String fullName;
	private String desc;
	private String firstPageComment;

	public NewBookCreationParams() {
	}

	public String getName() {
		return name;
	}

	public Instant getCreated() {
		return created;
	}

	public String getFirstPageName() {
		return firstPageName;
	}

	public String getFullName() {
		return fullName;
	}

	public String getDesc() {
		return desc;
	}

	public String getFirstPageComment() {
		return firstPageComment;
	}

	public BookToAdd toBookToAdd() {
		BookToAdd bookToAdd = new BookToAdd(name, this.created, firstPageName);
		if (fullName != null) {
			bookToAdd.setFullName(fullName);
		}

		if (desc != null) {
			bookToAdd.setDesc(desc);
		}

		if (firstPageComment != null) {
			bookToAdd.setFirstPageComment(firstPageComment);
		}
		return bookToAdd;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public void setFirstPageName(String firstPageName) {
		this.firstPageName = firstPageName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setFirstPageComment(String firstPageComment) {
		this.firstPageComment = firstPageComment;
	}
}
