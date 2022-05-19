package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.params.HttpParamBuilder;
import com.exactpro.th2.cradle.adm.http.params.NoParamsBuilder;
import com.exactpro.th2.cradle.adm.modes.ListAllBookSchemasMode;
import com.exactpro.th2.cradle.adm.params.NoParams;
import com.exactpro.th2.cradle.adm.results.BookSchemaInfo;

import javax.servlet.http.HttpServletRequest;

public class ListAllBookSchemasHttpMode extends ListAllBookSchemasMode implements HttpMode<NoParams> {

    @Override
    public BookSchemaInfo execute() {
        return super.execute();
    }

    @Override
    public HttpParamBuilder<NoParams> createParamsBuilder() {
        return new NoParamsBuilder();
    }

    @Override
    public boolean initParams(HttpServletRequest commandLine) throws InvalidConfigurationException {
        this.param = getParams(commandLine);
        return true;
    }
}
