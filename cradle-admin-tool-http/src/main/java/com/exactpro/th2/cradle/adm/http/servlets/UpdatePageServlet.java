package com.exactpro.th2.cradle.adm.http.servlets;

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.http.modes.UpdatePageHttpMode;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;

public class UpdatePageServlet extends SimpleHttpServlet {

    public UpdatePageServlet(CradleStorage storage) {
        super(storage);
    }

    @Override
    protected AbstractMode<?, ?> createMode() {
        return new UpdatePageHttpMode();
    }
}
