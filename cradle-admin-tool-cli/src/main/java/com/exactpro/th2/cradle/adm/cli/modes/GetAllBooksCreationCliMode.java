package com.exactpro.th2.cradle.adm.cli.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.cli.params.CommandLineBuilder;
import com.exactpro.th2.cradle.adm.cli.params.GetAllBooksParamBuilder;
import com.exactpro.th2.cradle.adm.modes.GetAllBooksMode;
import com.exactpro.th2.cradle.adm.params.GetAllBooksParams;
import org.apache.commons.cli.CommandLine;

public class GetAllBooksCreationCliMode extends GetAllBooksMode implements CliMode<GetAllBooksParams> {
    @Override
    public CommandLineBuilder<GetAllBooksParams> createParamsBuilder() {
        return new GetAllBooksParamBuilder();
    }

    @Override
    public boolean initParams(CommandLine commandLine) throws InvalidConfigurationException {
        this.param = getParams(commandLine);
        return true;
    }
}
