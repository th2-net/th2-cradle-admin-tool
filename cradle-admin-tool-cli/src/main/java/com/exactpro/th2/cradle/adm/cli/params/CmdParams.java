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

package com.exactpro.th2.cradle.adm.cli.params;

public class CmdParams {

	public static final String MODE_BOOK_L			= "book";
	public static final String MODE_BOOK_S			= "b";
	public static final String MODE_PAGE_L			= "page";
	public static final String MODE_PAGE_S			= "p";
	public static final String INIT_KEYSPACE_L		= "initKeyspace";
	public static final String ALL_BOOKS_L			= "getAllBooks";
	public static final String BOOK_INFO_L			= "getBookInfo";
	public static final String REMOVE_PAGE_L		= "removePage";

	public static final String BOOK_NAME			= "bookName";
	public static final String CREATED_TIME			= "createdTime";
	public static final String FIRST_PAGE_NAME		= "firstPageName";
	public static final String FULL_NAME			= "fullName";
	public static final String DESC					= "desc";
	public static final String FIRST_PAGE_COMMENT	= "firstPageComment";

	public static final String BOOK_ID				= "bookId";
	public static final String PAGE_NAME			= "pageName";
	public static final String PAGE_START_TIME		= "pageStart";
	public static final String PAGE_COMMENT			= "pageComment";

	public static final String WITH_PAGES			= "withPages";
	public static final String LOAD_REMOVED_PAGES   = "loadRemovedPages";

	public static final String COMMON_CFG_SHORT = "c";
	public static final String COMMON_CFG_LONG = "configs";

	public static final String NAME_SORT = "nameSort";
	public static final String CREATION_SORT = "creationSort";
	public static final String DATE_FROM = "dateFrom";
	public static final String DATE_TO = "dateTo";

	public static final String UPDATE_PAGE = "updatePage";
	public static final String UPDATE_PAGE_BOOK_ID = "bookId";
	public static final String UPDATE_PAGE_NAME = "pageName";
	public static final String UPDATE_PAGE_NAME_NEW = "newPageName";
	public static final String UPDATE_PAGE_COMMENT_NEW = "newPageComment";

}
