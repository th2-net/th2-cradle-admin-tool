/*******************************************************************************
 * Copyright 2021-2023 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.http.servlets.*;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServer implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);
	
	private final Configuration configuration;
	private final CradleStorage storage;
	protected Server server;

	public HttpServer(Configuration configuration, CradleStorage storage) {
		this.configuration = configuration;
		this.storage = storage;
	}

	protected void createServer() {
		this.server = new Server();

		ServerConnector serverConnector = new ServerConnector(this.server);
		serverConnector.setHost(configuration.getIp());
		serverConnector.setPort(configuration.getPort());

		this.server.setConnectors(new Connector[]{serverConnector});
	}

	public void run() throws Exception {
		this.createServer();
		ServletHandler servletHandler = new ServletHandler();
		server.setHandler(servletHandler);
		
		servletHandler.addServletWithMapping(new ServletHolder(new GetBookServlet(storage)), "/get-all-books");
		servletHandler.addServletWithMapping(new ServletHolder(new ListAllBookSchemasServlet(storage)), "/list-all-book-schemas");
		servletHandler.addServletWithMapping(new ServletHolder(new NewBookServlet(storage)), "/new-book");
		servletHandler.addServletWithMapping(new ServletHolder(new NewPageServlet(storage)), "/new-page");
		servletHandler.addServletWithMapping(new ServletHolder(new RemovePageServlet(storage)), "/remove-page");
		servletHandler.addServletWithMapping(new ServletHolder(new GetBookInfoServlet(storage)), "/get-book-info");
		servletHandler.addServletWithMapping(new ServletHolder(new UpdatePageServlet(storage)), "/update-page");
		
		server.start();
		logger.info("server started: http://{}:{}/", configuration.getIp(), configuration.getPort());
	}

	@Override
	public void close() throws Exception {
		server.stop();
	}
}
