package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.params.HttpParamBuilder;
import com.exactpro.th2.cradle.adm.http.params.UpdatePageParamsBuilder;
import com.exactpro.th2.cradle.adm.modes.UpdatePageMode;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;

import javax.servlet.http.HttpServletRequest;

public class UpdatePageHttpMode extends UpdatePageMode implements HttpMode<UpdatePageParams> {
    @Override
    public HttpParamBuilder<UpdatePageParams> createParamsBuilder() {
        return new UpdatePageParamsBuilder();
    }

    @Override
    public boolean initParams(HttpServletRequest req) throws InvalidConfigurationException {
        this.param = getParams(req);
        return true;
    }
}
