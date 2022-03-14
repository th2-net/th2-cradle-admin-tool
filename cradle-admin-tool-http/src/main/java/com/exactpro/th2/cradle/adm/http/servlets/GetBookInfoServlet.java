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

package com.exactpro.th2.cradle.adm.http.servlets;

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.http.modes.GetAllBooksHttpMode;
import com.exactpro.th2.cradle.adm.http.modes.GetBookInfoHttpMode;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;
import com.exactpro.th2.cradle.adm.modes.GetAllBooksMode;
import com.exactpro.th2.cradle.adm.results.BooksListInfo;
import com.exactpro.th2.cradle.adm.results.ResultBookInfo;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetBookInfoServlet extends GetBookServlet {

	private static final Logger logger = LoggerFactory.getLogger(GetBookInfoServlet.class);

	public GetBookInfoServlet(CradleStorage storage) {
		super(storage);
	}

	@Override
	protected AbstractMode<?, ?> createMode() {
		return new GetBookInfoHttpMode();
	}
}
