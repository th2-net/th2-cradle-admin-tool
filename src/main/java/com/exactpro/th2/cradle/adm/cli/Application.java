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

package com.exactpro.th2.cradle.adm.cli;

import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.cassandra.CassandraCradleManager;
import com.exactpro.cradle.cassandra.CassandraStorageSettings;
import com.exactpro.cradle.cassandra.connection.CassandraConnectionSettings;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.common.schema.cradle.CradleConfidentialConfiguration;
import com.exactpro.th2.common.schema.cradle.CradleConfiguration;
import com.exactpro.th2.common.schema.cradle.CradleNonConfidentialConfiguration;
import com.exactpro.th2.common.schema.exception.CommonFactoryException;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.cradle.adm.cli.modes.AbstractMode;
import com.exactpro.th2.cradle.adm.cli.params.CmdParams;
import com.exactpro.th2.cradle.adm.cli.params.Mode;
import com.google.common.collect.Streams;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.exactpro.cradle.cassandra.CassandraStorageSettings.DEFAULT_CONSISTENCY_LEVEL;
import static com.exactpro.cradle.cassandra.CassandraStorageSettings.DEFAULT_TIMEOUT;

public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static final String IMPLEMENTATION_TITLE = "Cradle TH2 Admin tool";
	public static final String RUN_COMMAND = "java -jar th2-cradle-admin-cli.jar";
	private static String VERSION;
	private static String BUILD_DATE;


	private static CradleManager createCradleManager(CradleConfiguration cradleConfiguration, boolean prepareStorage) {
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
					DEFAULT_CONSISTENCY_LEVEL,
					DEFAULT_CONSISTENCY_LEVEL
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
	

	public static void main(String[] args) throws Exception {

		initApplication(args);
		Options options = buildOptions();
		
		CommandLine cmdLine = new DefaultParser().parse(options, args);

		CradleManager mngr = null;
		try (CommonFactory commonFactory = CommonFactory.createFromArguments(buildSchemaParams(cmdLine))) {
			CradleConfiguration cradleConfiguration = commonFactory.getCradleConfiguration();
			
			AbstractMode<?> mode = Mode.getMode(cmdLine);
			if (!mode.initParams(cmdLine)) {
				return;
			}
			mngr = createCradleManager(cradleConfiguration, mode.prepareStorage());
			CradleStorage storage = mngr.getStorage();
			mode.init(storage, System.out::println);
			mode.execute();
			
		} catch (InvalidConfigurationException e) {
			System.out.println("Error:");
			System.out.println(e.getDescription());
			printHelp(options);
		} finally {
			if (mngr != null) {
				mngr.close();
			}
		}
	}
	
	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(new Option(CmdParams.COMMON_CFG_SHORT, CmdParams.COMMON_CFG_LONG, true, null));
		Mode.getModeOpts(options);
		return options;
	}

	private static String[] buildSchemaParams(CommandLine line) {
		if (line.hasOption(CmdParams.COMMON_CFG_LONG)) {
			return new String[] {"-c", line.getOptionValue(CmdParams.COMMON_CFG_LONG)};
		} else {
			return new String[0];	
		}
	}

	private static void printHelp(Options options) {
		new HelpFormatter().printHelp(RUN_COMMAND, options);
	}

	private static void initApplication(String[] args)
	{
		PropertyConfigurator.configureAndWatch("log.properties");
		initMetadata();
		System.out.printf("%s, version %s, build-date %s%n", IMPLEMENTATION_TITLE, VERSION, BUILD_DATE);
		System.out.println("Started with arguments: " + Arrays.toString(args));
	}


	private static void initMetadata()
	{
		try
		{
			ClassLoader cl = Application.class.getClassLoader();
			Enumeration<URL> urls = cl.getResources(JarFile.MANIFEST_NAME);
			Optional<Attributes> attributes = Streams.stream(urls.asIterator())
					.map(url ->
					{
						try (InputStream stream = url.openStream())
						{
							return new Manifest(stream);
						}
						catch (IOException e)
						{
							logger.warn("Manifest '{}' loading failure", url, e);
							return null;
						}
					})
					.filter(Objects::nonNull)
					.map(Manifest::getMainAttributes)
					.filter(attrs -> IMPLEMENTATION_TITLE.equals(attrs.getValue(Attributes.Name.IMPLEMENTATION_TITLE)))
					.findFirst();
			
			if (attributes.isPresent())
			{
				Attributes attrs = attributes.get();
				VERSION = attrs.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
				BUILD_DATE = attrs.getValue("Implementation-Version-Date");
			}
		}
		catch (IOException e)
		{
			logger.warn("Manifest searching failure", e);
		}
	}


}
