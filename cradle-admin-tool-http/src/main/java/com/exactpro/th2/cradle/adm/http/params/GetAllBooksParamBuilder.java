package com.exactpro.th2.cradle.adm.http.params;

import com.exactpro.th2.cradle.adm.params.GetAllBooksParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import static com.exactpro.th2.cradle.adm.http.params.HttpParamConst.*;

public class GetAllBooksParamBuilder extends HttpParamBuilder<GetAllBooksParams> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllBooksParamBuilder.class);

    @Override
    public GetAllBooksParams fromMap(Map<String, String> request) {
        String nameSort = null;
        String creationSort = null;
        String fromStr = request.get(DATE_FROM);
        String toStr = request.get(DATE_TO);
        Instant from = null, to = null;

        if (fromStr != null) {
            from = LocalDate.parse(fromStr).atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        if (toStr != null) {
            to = LocalDate.parse(toStr).atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        try {
            nameSort = request.get(NAME_SORT);
            creationSort = request.get(CREATION_SORT);
        } catch (Exception e) {
            LOGGER.warn("Could not parse request parameters, {}", e.getMessage());
        }

        return new GetAllBooksParams(nameSort, creationSort, from, to);
    }

    @Override
    protected String header() {
        return "get all books parameters";
    }
}
