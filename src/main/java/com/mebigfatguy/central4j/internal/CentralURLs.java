/*
 * central4j - an api for accessing maven central
 * Copyright 2016 MeBigFatGuy.com
 * Copyright 2016 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package com.mebigfatguy.central4j.internal;

import java.net.URL;

public class CentralURLs {

    public static final URL ITERATION_URL;
    public static final URL SEARCH_URL;

    static {
        URL iu;
        URL su;
        try {
            iu = new URL("http://repo1.maven.org/maven2/");
            su = new URL("http://search.maven.org/solrsearch/select");
        } catch (Exception e) {
            iu = null;
            su = null;
        }

        ITERATION_URL = iu;
        SEARCH_URL = su;

    }
}
