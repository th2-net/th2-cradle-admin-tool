package com.exactpro.th2.cradle.adm.modes;

import com.exactpro.cradle.PageInfo;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;
import com.exactpro.th2.cradle.adm.results.SimpleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdatePageMode extends AbstractMode<UpdatePageParams, SimpleResult> {
    private static final Logger logger = LoggerFactory.getLogger(UpdatePageMode.class);
    @Override
    public SimpleResult execute() {
        PageInfo pageInfo = null;

        if (param.getUpdatedComment() != null) {
            try {
                pageInfo = cradleStorage.updatePageComment(param.getBookId(), param.getPageName(), param.getUpdatedComment());
                logger.info("page comment updated to {}", param.getUpdatedComment());
            } catch (Exception e) {
                return new SimpleResult(e);
            }
        }

        if (param.getUpdatedName() != null) {
            try {
                pageInfo = cradleStorage.updatePageName(param.getBookId(), param.getPageName(), param.getUpdatedName());
                logger.info("page name updated to {}", param.getUpdatedName());
            } catch (Exception e) {
                return new SimpleResult(e);
            }
        }

        if (pageInfo == null) {
            return new SimpleResult(new RuntimeException("Page was not updated, either update comment or update name should be specified"));
        }

        StringBuilder sb = new StringBuilder("Page updated ");
        fillPageParams(sb, pageInfo);
        return new SimpleResult(sb.toString());
    }

    private void fillPageParams(StringBuilder sb, PageInfo pageInfo) {
        sb.append("bookId = ").append(pageInfo.getId().getBookId().getName());
        sb.append(",pageName = ").append(pageInfo.getId().getName());
        sb.append(",pageStart = ").append(pageInfo.getStarted());

        if (pageInfo.getComment() != null) {
            sb.append(",pageComment = ").append(pageInfo.getComment());
        }

        sb.append(",updated = ").append(pageInfo.getUpdated());
    }
}
