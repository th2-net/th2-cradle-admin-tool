package com.exactpro.th2.cradle.adm.cli.params;

import com.exactpro.cradle.BookId;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import static com.exactpro.th2.cradle.adm.cli.params.CmdParams.*;

public class UpdatePageParamBuilder extends CommandLineBuilder<UpdatePageParams>{

    public static void getOptions(Options options) {
        options.addOption(Option.builder().longOpt(UPDATE_PAGE_BOOK_ID).desc("Book of page to be updated").hasArg(true).required(false).build());
        options.addOption(Option.builder().longOpt(UPDATE_PAGE_NAME).desc("Name of page to update").hasArg(true).required(false).build());
        options.addOption(Option.builder().longOpt(UPDATE_PAGE_NAME_NEW).desc("New name for the updated page").hasArg(true).required(false).build());
        options.addOption(Option.builder().longOpt(UPDATE_PAGE_COMMENT_NEW).desc("New comment for the updated page").hasArg(true).required(false).build());
    }

    @Override
    public UpdatePageParams fromCommandLine(CommandLine commandLine) {
        return new UpdatePageParams(
                new BookId(commandLine.getOptionValue(UPDATE_PAGE_BOOK_ID)),
                commandLine.getOptionValue(UPDATE_PAGE_NAME),
                commandLine.getOptionValue(UPDATE_PAGE_NAME_NEW),
                commandLine.getOptionValue(UPDATE_PAGE_COMMENT_NEW));
    }

    @Override
    protected String header() {
        return "update page name/comment";
    }
}
