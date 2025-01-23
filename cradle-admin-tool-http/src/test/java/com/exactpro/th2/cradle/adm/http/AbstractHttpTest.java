/*
 * Copyright 2022-2025 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.cradle.adm.http;

import com.exactpro.cradle.BookId;
import com.exactpro.cradle.BookToAdd;
import com.exactpro.cradle.CradleManager;
import com.exactpro.cradle.CradleStorage;
import com.exactpro.cradle.PageId;
import com.exactpro.cradle.PageToAdd;
import com.exactpro.cradle.utils.CradleStorageException;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.test.annotations.Th2AppFactory;
import com.exactpro.th2.test.annotations.Th2IntegrationTest;
import com.exactpro.th2.test.spec.CradleSpec;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@Th2IntegrationTest
public class AbstractHttpTest {

    @SuppressWarnings("unused")
    public final CradleSpec cradleSpec = CradleSpec.Companion.create()
            .disableAutoPages()
            .reuseKeyspace();
    protected TestHttpServer testHttpServer;
    protected LocalConnector connector;
    protected CradleStorage storage;

    @BeforeAll
    public static void initStorage(CradleManager manager) {
        // init database schema
        manager.getStorage();
    }

    @BeforeEach
    public void init(@Th2AppFactory CommonFactory appFactory,
                     CradleManager manager) throws Exception {
        this.storage = manager.getStorage();
        this.testHttpServer = new TestHttpServer(new Configuration(), appFactory.getCradleManager().getStorage());
        this.testHttpServer.run();
        this.connector = testHttpServer.getLocalConnector();
        this.testHttpServer.run();
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
        assertAll(
                () -> assertEquals(status ? "Success" : "Failed", rsp.status, rsp.toString()),
                () -> assertEquals(test, rsp.comment)
        );
    }

    protected void checkPlainResponseContains(String body, boolean status, String test) {
        PlainResponse rsp = new PlainResponse(body.trim());
        assertAll(
                () -> assertEquals(status ? "Success" : "Failed", rsp.status, rsp.toString()),
                () -> assertTrue(rsp.comment.contains(test), () -> String.format("Comment should contain text: %s but actual text is %s", test, rsp.comment))
        );
    }

    protected void addBook(BookToAdd bookToAdd) throws Exception {
        this.storage.addBook(bookToAdd);
    }

    protected void addPage(BookId bookId, PageToAdd page) throws Exception {
        this.storage.addPages(bookId, Collections.singletonList(page));
    }

    protected void removePage(PageId pageId) throws CradleStorageException, IOException {
        this.storage.removePage(pageId);
    }

    protected static class PlainResponse {
        public String status;
        public String comment;

        protected PlainResponse(String str) {
            String[] spl = str.split("\n", 2);
            status =  spl[0];
            comment = spl.length > 1 ? spl[1] : null;
        }

        @Override
        public String toString() {
            return "PlainResponse{" +
                    "status='" + status + '\'' +
                    ", comment='" + comment + '\'' +
                    '}';
        }
    }

}
