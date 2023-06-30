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
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.cradle.adm.cli.modes.CliMode;
import com.exactpro.th2.cradle.adm.cli.modes.Mode;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;
import com.exactpro.th2.cradle.adm.params.CmdParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.StreamSupport;

public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static final String IMPLEMENTATION_TITLE = "Cradle TH2 Admin tool (CLI)";
	public static final String RUN_COMMAND = "java -jar th2-cradle-admin-cli.jar";
	private static String VERSION;
	private static String BUILD_DATE;
	

	public static void main(String[] args) throws Exception {

		initApplication(args);
		Options options = buildOptions();
		
		CommandLine cmdLine = new DefaultParser().parse(options, args);

		try (CommonFactory commonFactory = CommonFactory.createFromArguments(buildSchemaParams(cmdLine))) {
			
			AbstractMode<?, ?> mode = Mode.getMode(cmdLine);
			if (mode instanceof CliMode && !((CliMode<?>)mode).initParams(cmdLine)) {
				return;
			}

			try (CradleManager mngr = commonFactory.getCradleManager()) {

				CradleStorage storage = mngr.getStorage();
				mode.init(storage);
				SimpleResult result = mode.execute();
				ResultPrinter.printToCmd(result);

			}
		} catch (Exception e) {
			logger.error("Exception {}", e);
			printHelp(options);
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

			Spliterator<URL> urlSpliterator = Spliterators.spliteratorUnknownSize(urls.asIterator(), Spliterator.ORDERED);
			Optional<Attributes> attributes = StreamSupport.stream(urlSpliterator, false)
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
