/*******************************************************************************
 * Copyright 2022 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.exactpro.th2.cradle.adm.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AbstractCliTest {

    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$");

    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    public static final String INITIAL_BOOK = "init_book";
    public static final String INITIAL_PAGE = "init_page";

    @BeforeEach
    public void before() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    protected void checkOutput(boolean status, String exceptionText) {
        String out = outContent.toString();
        String[] separated = out.split("\n");

        Assertions.assertTrue((exceptionText == null ? 3: 4) <= separated.length);
        String statusInLogs =  "Empty Log Status";
        statusInLogs = Arrays.asList(separated).contains("Success") ? "Success" : statusInLogs;
        statusInLogs = Arrays.asList(separated).contains("Failed") ? "Failed" : statusInLogs;
        Assertions.assertEquals(status ? "Success": "Failed", statusInLogs);

        if (exceptionText != null)
            Assertions.assertTrue(separated[3].contains(exceptionText), () ->
                    String.format("Actual value (%s) should contain: %s", separated[3], exceptionText));
    }

}
