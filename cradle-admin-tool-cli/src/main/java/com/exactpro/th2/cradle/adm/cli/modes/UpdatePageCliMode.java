package com.exactpro.th2.cradle.adm.cli.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.cli.params.CommandLineBuilder;
import com.exactpro.th2.cradle.adm.cli.params.UpdatePageParamBuilder;
import com.exactpro.th2.cradle.adm.modes.UpdatePageMode;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;
import org.apache.commons.cli.CommandLine;

public class UpdatePageCliMode extends UpdatePageMode implements CliMode<UpdatePageParams> {
    @Override
    public CommandLineBuilder<UpdatePageParams> createParamsBuilder() {
        return new UpdatePageParamBuilder();
    }

    @Override
    public boolean initParams(CommandLine commandLine) throws InvalidConfigurationException {
        this.param = getParams(commandLine);
        return true;
    }
}
