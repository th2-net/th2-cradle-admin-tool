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

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.cradle.adm.FactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
		
		var resources = new ArrayList<AutoCloseable>();
		configureShutdownHook(resources);

		try {
			CommonFactory factory = CommonFactory.createFromArguments(args);
			resources.add(factory);
			CradleManager cradleManager = FactoryUtils.createCradleManager(factory, true);
			resources.add(cradleManager);

			CradleStorage storage = cradleManager.getStorage();

			HttpServer httpServer = new HttpServer(factory.getCustomConfiguration(CustomConfiguration.class), storage);
			httpServer.run();
			resources.add(httpServer);
		} catch (Exception e) {
			logger.error("{}", e.getMessage(), e);
			System.exit(-1);
		}
	}
	
	public static void configureShutdownHook(ArrayList<AutoCloseable> resources) {

		Runtime.getRuntime().addShutdownHook(new Thread(
				() -> {
					logger.info("Executing shutdown hook");
					ArrayList<AutoCloseable> revs = new ArrayList<>(resources);
					Collections.reverse(revs);
					for (AutoCloseable resource : revs) {
						try {
							logger.info("Closing {}", resource);
							resource.close();
						} catch (Exception e) {
							logger.error("Cannot close resource {}", resource, e);
						}
					}
					logger.info("Shutdown complete");
				}
				));
	}
	
}
