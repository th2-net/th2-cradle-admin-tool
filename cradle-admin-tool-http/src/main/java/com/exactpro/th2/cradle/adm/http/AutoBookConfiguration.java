/*
 * Copyright 2023 Exactpro (Exactpro Systems Limited)
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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@SuppressWarnings("FieldMayBeFinal")
public class AutoBookConfiguration {
    @JsonProperty("book-full-name")
    private String bookFullName = null;
    @JsonProperty("book-description")
    private String bookDescription = null;
    @JsonProperty("book-creation-time")
    private Instant bookCreationTime = null;

    public String getBookFullName() {
        return bookFullName;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public Instant getBookCreationTime() {
        return bookCreationTime;
    }

    @Override
    public String toString() {
        return "AutoBookConfiguration{" +
                "bookFullName='" + bookFullName + '\'' +
                ", bookDescription='" + bookDescription + '\'' +
                ", bookCreationTime=" + bookCreationTime +
                '}';
    }
}
