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

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.PageToAdd;
import com.exactpro.th2.cradle.adm.TestCradleStorage;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;

public class AbstractHttpTest {

    protected TestHttpServer testHttpServer;
    protected LocalConnector connector;
    protected TestCradleStorage storage;

    @BeforeEach
    public void init() throws Exception {
        this.storage = new TestCradleStorage();
        this.testHttpServer = new TestHttpServer(new CustomConfiguration(), this.storage);
        this.testHttpServer.run();
        this.connector = testHttpServer.getLocalConnector();
        this.testHttpServer.run();

        storage.init(false);
    }

    @AfterEach
    public void dispose() throws Exception {
        if (testHttpServer != null)
            testHttpServer.close();
    }

    protected HttpTester.Response executeGet(String query) throws Exception {
        HttpTester.Request request = HttpTester.newRequest();
        request.setURI(query);
        request.setHeader("Host", "");
        return HttpTester.parseResponse(HttpTester.from(connector.getResponse(request.generate())));
    }

    protected void checkPlainResponse(String str, boolean status, String test) {
        PlainResponse rsp = new PlainResponse(str.trim());
        Assertions.assertEquals(status ? "Success": "Failed", rsp.status);
        Assertions.assertEquals(test, rsp.comment);
    }

    protected void checkPlainResponseContains(String body, boolean expected, String test) {
        PlainResponse rsp = new PlainResponse(body.trim());
        Assertions.assertEquals(expected ? "Success": "Failed", rsp.status);
        Assertions.assertTrue(rsp.comment.contains(test), () -> String.format("Comment should contain text: %s but actual text is %s", test, rsp.comment));
    }

    protected void addBook(BookToAdd bookToAdd) throws Exception {
        this.storage.addBook(bookToAdd);
    }

    protected void addPage(BookId bookId, PageToAdd page) throws Exception {
        this.storage.addPages(bookId, Collections.singletonList(page));
    }


    protected static class PlainResponse {
        public String status;
        public String comment;

        protected PlainResponse(String str) {
            String[] spl = str.split("\n", 2);
            status =  spl[0];
            comment = spl.length > 1 ? spl[1] : null;
        }
    }

}
