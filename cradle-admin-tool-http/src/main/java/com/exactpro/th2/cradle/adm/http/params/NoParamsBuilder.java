package com.exactpro.th2.cradle.adm.http.params;

import com.exactpro.th2.cradle.adm.params.NoParams;

import java.util.Map;

public class NoParamsBuilder extends HttpParamBuilder<NoParams> {

    @Override
    public NoParams fromMap(Map<String, String> request) {
        return null;
    }

    @Override
    protected String header() {
        return "";
    }
}
