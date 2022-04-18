package com.exactpro.th2.cradle.adm.cli.params;

import com.exactpro.th2.cradle.adm.params.GetAllBooksParams;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.*;

public class GetAllBooksParamBuilder extends CommandLineBuilder<GetAllBooksParams> {
    public static void getOptions(Options options) {
        options.addOption(Option.builder().longOpt(NAME_SORT).desc("sorting order by names (ASC/DESC)").hasArg(true).argName("abc").argName("xyz").required(false).build());
        options.addOption(Option.builder().longOpt(CREATION_SORT).desc("sorting order by creation datetime (ASC/DESC)").hasArg(true).required(false).build());
        options.addOption(Option.builder().longOpt(DATE_FROM).desc("date filter from yyyy-mm-dd (inclusive)").hasArg(true).required(false).build());
        options.addOption(Option.builder().longOpt(DATE_TO).desc("date filter to yyyy-mm-dd (exclusive)").hasArg(true).required(false).build());
    }

    @Override
    public GetAllBooksParams fromCommandLine(CommandLine commandLine) {
        String fromStr = commandLine.getOptionValue(DATE_FROM);
        String toStr = commandLine.getOptionValue(DATE_TO);
        Instant from = null, to = null;

        if (fromStr != null) {
            from = LocalDate.parse(fromStr).atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        if (toStr != null) {
            to = LocalDate.parse(toStr).atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        return new GetAllBooksParams(
                commandLine.getOptionValue(NAME_SORT),
                commandLine.getOptionValue(CREATION_SORT),
                from,
                to
        );
    }

    @Override
    protected String header() {
        return "get all books parameters";
    }
}
