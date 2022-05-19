package com.exactpro.th2.cradle.adm.http.servlets;

import com.exactpro.cradle.CradleStorage;
import com.exactpro.th2.cradle.adm.http.modes.ListAllBookSchemasHttpMode;
import com.exactpro.th2.cradle.adm.modes.AbstractMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListAllBookSchemasServlet extends SimpleHttpServlet{
    private static final Logger logger = LoggerFactory.getLogger(ListAllBookSchemasServlet.class);

    public ListAllBookSchemasServlet(CradleStorage storage) {
        super(storage);
    }

    @Override
    protected AbstractMode<?, ?> createMode() {
        return new ListAllBookSchemasHttpMode();
    }
}
