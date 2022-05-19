package com.exactpro.th2.cradle.adm.cli.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.cli.params.CommandLineBuilder;
import com.exactpro.th2.cradle.adm.cli.params.NoParamsBuilder;
import com.exactpro.th2.cradle.adm.modes.ListAllBookSchemasMode;
import com.exactpro.th2.cradle.adm.params.NoParams;
import org.apache.commons.cli.CommandLine;


public class ListAllBookSchemasCliMode extends ListAllBookSchemasMode implements CliMode<NoParams> {
    @Override
    public CommandLineBuilder<NoParams> createParamsBuilder() {
        return new NoParamsBuilder();
    }

    @Override
    public boolean initParams(CommandLine commandLine) throws InvalidConfigurationException {
        this.param = getParams(commandLine);
        return true;
    }
}
