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

package com.exactpro.th2.cradle.adm;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.cassandra.CassandraCradleManager;
import com.exactpro.cradle.cassandra.CassandraStorageSettings;
import com.exactpro.cradle.cassandra.connection.CassandraConnectionSettings;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.common.schema.cradle.CradleConfiguration;
import com.exactpro.th2.common.schema.exception.CommonFactoryException;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static com.exactpro.cradle.cassandra.CassandraStorageSettings.DEFAULT_CONSISTENCY_LEVEL;
import static com.exactpro.cradle.cassandra.CassandraStorageSettings.DEFAULT_TIMEOUT;

public class FactoryUtils {

	public static CradleManager createCradleManager(CommonFactory commonFactory, boolean prepareStorage) {
		CradleConfiguration cradleConfiguration = commonFactory.getCradleConfiguration();
		return createCradleManager(cradleConfiguration, prepareStorage);
	}

	public static CradleManager createCradleManager(CradleConfiguration cradleConfiguration, boolean prepareStorage) {
		try {
			CassandraConnectionSettings cassandraConnectionSettings = new CassandraConnectionSettings(
					cradleConfiguration.getHost(),
					cradleConfiguration.getPort(),
					cradleConfiguration.getDataCenter()
			);
			if (StringUtils.isNotEmpty(cradleConfiguration.getUsername())) {
				cassandraConnectionSettings.setUsername(cradleConfiguration.getUsername());
			}
			if (StringUtils.isNotEmpty(cradleConfiguration.getPassword())) {
				cassandraConnectionSettings.setPassword(cradleConfiguration.getPassword());
			}

			CassandraStorageSettings cassandraStorageSettings = new CassandraStorageSettings(
					null,
					cradleConfiguration.getTimeout() > 0
							? cradleConfiguration.getTimeout()
							: DEFAULT_TIMEOUT,
					ConsistencyLevel.LOCAL_ONE,
					ConsistencyLevel.LOCAL_ONE
			);
			if (cradleConfiguration.getPageSize() > 0) {
				cassandraStorageSettings.setResultPageSize(cradleConfiguration.getPageSize());
			}
			if (cradleConfiguration.getCradleMaxMessageBatchSize() > 0) {
				cassandraStorageSettings.setMaxMessageBatchSize(cradleConfiguration.getCradleMaxMessageBatchSize());
			}
			if (cradleConfiguration.getCradleMaxEventBatchSize() > 0) {
				cassandraStorageSettings.setMaxTestEventBatchSize(cradleConfiguration.getCradleMaxEventBatchSize());
			}

			return new CassandraCradleManager(
					cassandraConnectionSettings,
					cassandraStorageSettings,
					prepareStorage
			);
		} catch (CradleStorageException | RuntimeException | IOException e) {
			throw new CommonFactoryException("Cannot create Cradle manager", e);
		}
	}
	
}
