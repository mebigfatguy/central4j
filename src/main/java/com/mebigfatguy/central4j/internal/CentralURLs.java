/*
 * central4j - an api for accessing maven central
 * Copyright 2016-2018 MeBigFatGuy.com
 * Copyright 2016-2018 Dave Brosius
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

public class CentralURLs {

    public static final String ITERATION_URL = "http://repo1.maven.org/maven2";
    public static final String DOWNLOAD_URL = "http://repo1.maven.org/maven2";
    public static final String SEARCH_BASE_URL = "http://search.maven.org";
    public static final String SEARCH_URL = SEARCH_BASE_URL + "/solrsearch/select";
    public static final String STATISTICS_URL = SEARCH_BASE_URL + "/quickstats";
    public static final String TOP_DOWNLOADS_URL = SEARCH_BASE_URL + "/content/stats.html";
}
