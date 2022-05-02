package com.exactpro.th2.cradle.adm.http.params;

import com.exactpro.cradle.BookId;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.*;
import java.util.Map;


public class UpdatePageParamsBuilder extends HttpParamBuilder<UpdatePageParams>{
    @Override
    public UpdatePageParams fromMap(Map<String, String> request) {
        return new UpdatePageParams(
                new BookId(request.get(UPDATE_PAGE_BOOK_ID)),
                request.get(UPDATE_PAGE_NAME),
                request.get(UPDATE_PAGE_NAME_NEW),
                request.get(UPDATE_PAGE_COMMENT_NEW));
    }

    @Override
    protected String header() {
        return "update page name/comment";
    }
}
