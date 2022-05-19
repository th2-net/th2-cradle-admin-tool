package com.exactpro.th2.cradle.adm.results;

import com.exactpro.cradle.BookListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BookSchemaInfo extends SimpleResult{
    private static final Logger LOGGER = LoggerFactory.getLogger(BookSchemaInfo.class);


    private List<BookListEntry> bookSchemaList;

    public BookSchemaInfo() {
        super();
    }

    public BookSchemaInfo(Throwable error) {
        super(error);
    }

    public BookSchemaInfo(String info) {
        super(info);
    }

    public List<BookListEntry> getBookSchemas() {
        return bookSchemaList;
    }

    public void addBookSchema(BookListEntry book) {
        if (this.bookSchemaList == null) {
            this.bookSchemaList = new ArrayList<>();
        }
        this.bookSchemaList.add(book);
    }

    public void failed(String text) {
        this.isSuccess = false;
        this.info = text;
    }

    @Override
    public String getInfo() {
        this.info = "";
        if(bookSchemaList != null && !bookSchemaList.isEmpty()){
            int count = 1;
            for(BookListEntry bk : bookSchemaList){
                info += "\n";
                info += "book #" + count + " Name: " + bk.getName() + ", Schema Version: " + bk.getSchemaVersion() + "\n";
                count++;
            }
        }
        return this.info;
    }
}
