package com.exactpro.th2.cradle.adm.params;

import com.exactpro.cradle.BookId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdatePageParams {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePageParams.class);


    private final BookId bookId;
    private final String pageName;
    private final String updatedName;
    private final String updatedComment;

    public UpdatePageParams (BookId bookId, String pageName, String updatedName, String updatedComment) {
        if (bookId == null || pageName == null) {
            throw new RuntimeException(String.format("bookId (%s) or pageName (%s) was not specified, can not update page",
                    bookId,
                    pageName));
        }

        this.bookId = bookId;
        this.pageName = pageName;
        this.updatedName = updatedName;
        this.updatedComment = updatedComment;
    }

    public BookId getBookId() {
        return bookId;
    }

    public String getPageName() {
        return pageName;
    }

    public String getUpdatedName() {
        return updatedName;
    }

    public String getUpdatedComment() {
        return updatedComment;
    }
}
