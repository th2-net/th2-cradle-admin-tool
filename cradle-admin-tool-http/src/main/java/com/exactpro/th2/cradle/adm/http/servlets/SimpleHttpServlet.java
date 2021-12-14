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
import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.modes.HttpMode;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class SimpleHttpServlet extends HttpServlet {

	private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServlet.class);
	
	protected final CradleStorage storage;

	protected SimpleHttpServlet(CradleStorage storage) {
		this.storage = storage;
	}

	protected abstract AbstractMode<?, ?> createMode();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		AbstractMode<?, ?> mode = createMode();
		SimpleResult execute;
		try {
			if (mode instanceof HttpMode) {
				((HttpMode)mode).initParams(req);	
			}
			
			mode.init(this.storage);
			execute = mode.execute();
		} catch (InvalidConfigurationException e) {
			execute = new SimpleResult(e);
		}

		this.writeResult(execute, resp);
	}
	

	protected void writeResult(SimpleResult result, HttpServletResponse response) {
		response.setContentType(ServletUtils.PLAIN_TEXT);
		response.setStatus(HttpStatus.OK_200);
		try (PrintWriter writer = response.getWriter()){
			writer.println(result.isSuccess() ? "Success" : "Failed");
			if (result.getInfo() != null) {
				writer.println(result.getInfo());
			}
			if (result.getError() != null) {
				result.getError().printStackTrace(writer);
			}
		} catch (IOException e) {
			logger.error("Cannot send result", e);
		}
	}
	
}
