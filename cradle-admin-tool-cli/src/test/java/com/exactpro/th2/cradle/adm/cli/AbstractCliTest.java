/*
 * Copyright 2022-2024 Exactpro (Exactpro Systems Limited)
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
 */

package com.exactpro.th2.cradle.adm.cli;

import com.exactpro.cradle.CradleManager;
import com.exactpro.th2.cradle.adm.TestBookPageBuilder;
import com.exactpro.th2.test.annotations.Th2IntegrationTest;
import com.exactpro.th2.test.spec.CradleSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.regex.Pattern;

@Th2IntegrationTest
public class AbstractCliTest {
    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$");
    protected ByteArrayOutputStream outContent;
    protected ByteArrayOutputStream errContent;

    protected TestBookPageBuilder testBookPageBuilder;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @SuppressWarnings("unused")
    public final CradleSpec cradleSpec = CradleSpec.Companion.create()
            .disableAutoPages()
            .reuseKeyspace();

    @BeforeAll
    public static void initStorage(CradleManager manager) {
        // init database schema
        manager.getStorage();
    }

    @BeforeEach
    public void before() {
        testBookPageBuilder = TestBookPageBuilder.builder();

        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;

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

        String statusInLogs =  "Empty Log Status";
        statusInLogs = Arrays.asList(separated).contains("Success") ? "Success" : statusInLogs;
        statusInLogs = Arrays.asList(separated).contains("Failed") ? "Failed" : statusInLogs;
        Assertions.assertEquals(status ? "Success": "Failed", statusInLogs);

        if (exceptionText != null) {
            boolean isExceptionPresent = Arrays.stream(separated).anyMatch(el -> el.contains(exceptionText));

            Assertions.assertTrue(isExceptionPresent, () ->
                    String.format("Could not find following desired exception in logs: %s", exceptionText));
        }

    }

}
