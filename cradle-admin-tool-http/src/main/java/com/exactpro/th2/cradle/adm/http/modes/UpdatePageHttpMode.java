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

package com.exactpro.th2.cradle.adm.http.modes;

import com.exactpro.th2.cradle.adm.InvalidConfigurationException;
import com.exactpro.th2.cradle.adm.http.params.HttpParamBuilder;
import com.exactpro.th2.cradle.adm.http.params.UpdatePageParamsBuilder;
import com.exactpro.th2.cradle.adm.modes.UpdatePageMode;
import com.exactpro.th2.cradle.adm.params.UpdatePageParams;

import jakarta.servlet.http.HttpServletRequest;

public class UpdatePageHttpMode extends UpdatePageMode implements HttpMode<UpdatePageParams> {
    @Override
    public HttpParamBuilder<UpdatePageParams> createParamsBuilder() {
        return new UpdatePageParamsBuilder();
    }

    @Override
    public void initParams(HttpServletRequest req) throws InvalidConfigurationException {
        this.param = getParams(req);
    }
}
