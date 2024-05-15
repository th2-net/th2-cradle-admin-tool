package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.th2.cradle.adm.results.BookSchemaInfo;
import com.exactpro.cradle.BookListEntry;
import com.exactpro.th2.cradle.adm.params.NoParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ListAllBookSchemasMode extends AbstractMode<NoParams, BookSchemaInfo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListAllBookSchemasMode.class);


    @Override
    public BookSchemaInfo execute() {
        Collection<BookListEntry> bookList = cradleStorage.listBooks();
        BookSchemaInfo res = new BookSchemaInfo();
        for(BookListEntry currBookScheme : bookList){
            res.addBookSchema(currBookScheme);
        }
        return res;
    }
}
