/*
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
*/

package com.exactpro.th2.cradle.adm.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
public class Configuration {
	
	public static final String DEFAULT_IP = "0.0.0.0";
	public static final int DEFAULT_PORT = 8080;
	public static final int DEFAULT_PAGE_RECHECK_INTERVAL_SEC = 60;

	@JsonProperty("ip")
	private String ip = DEFAULT_IP;

	@JsonProperty("port")
	private int port = DEFAULT_PORT;

	@JsonProperty("page-recheck-interval")
	private int pageRecheckInterval = DEFAULT_PAGE_RECHECK_INTERVAL_SEC;

	@JsonProperty("auto-books")
	private Map<String, AutoBookConfiguration> autoBooks = Collections.emptyMap();

	@JsonProperty("auto-pages")
	private Map<String, AutoPageConfiguration> autoPages = Collections.emptyMap();

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public Map<String, AutoBookConfiguration> getAutoBooks() {
		return autoBooks;
	}

	public Map<String, AutoPageConfiguration> getAutoPages() {
		return autoPages;
	}

	public int getPageRecheckInterval() {
		return pageRecheckInterval;
	}
}
