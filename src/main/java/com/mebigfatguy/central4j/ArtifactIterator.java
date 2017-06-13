/*
 * central4j - an api for accessing maven central
 * Copyright 2016-2017 MeBigFatGuy.com
 * Copyright 2016-2017 Dave Brosius
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
package com.mebigfatguy.central4j;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mebigfatguy.central4j.internal.CentralURLs;

public class ArtifactIterator implements Iterator<Artifact> {

    private static final Set<String> IGNORED_EXTENSIONS;
    private static final String MAVEN_META_DATA = "maven-metadata.xml";

    static {
        Set<String> ie = new HashSet<>();
        ie.add("");
        ie.add("/");
        ie.add("asc");
        ie.add("html");
        ie.add("md5");
        ie.add("pom");
        ie.add("sha1");
        ie.add("xml");

        IGNORED_EXTENSIONS = Collections.unmodifiableSet(ie);
    }

    private Deque<String> browseToBeProcessed;
    private List<String> currentPageLinks;
    private Deque<Artifact> browseResults;

    public ArtifactIterator() {
        browseToBeProcessed = new ArrayDeque<>(200);
        currentPageLinks = new ArrayList<>();
        browseResults = new ArrayDeque<>(200);
        populateBrowse(CentralURLs.ITERATION_URL, "");
    }

    public ArtifactIterator(String startingGroupPrefix) {
        browseToBeProcessed = new ArrayDeque<>(100);
        currentPageLinks = new ArrayList<>();
        browseResults = new ArrayDeque<>(100);
        String startingGroupURL = startingGroupPrefix.replace('.', '/');
        populateBrowse(CentralURLs.ITERATION_URL + '/' + startingGroupURL, startingGroupURL);
    }

    @Override
    public boolean hasNext() {
        if (!browseResults.isEmpty()) {
            return true;
        }
        while (!browseToBeProcessed.isEmpty()) {
            String startingGroupURL = browseToBeProcessed.removeFirst();
            populateBrowse(CentralURLs.ITERATION_URL + '/' + startingGroupURL, startingGroupURL);

            if (!browseResults.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Artifact next() {
        if (browseResults.isEmpty()) {
            throw new NoSuchElementException("Iterator is exhausted");
        }

        return browseResults.removeFirst();
    }

    private void populateBrowse(String u, String startingGroup) {
        try {

            Document doc = Jsoup.connect(u).get();
            Elements links = doc.getElementsByTag("a");
            for (Element link : links) {
                String href = link.attr("href");
                if (href.endsWith("/")) {
                    href = href.substring(0, href.length() - 1);
                }

                if (MAVEN_META_DATA.equals(href)) {
                    int slash = startingGroup.lastIndexOf('/');
                    String groupId = startingGroup.substring(0, slash).replace('/', '.');
                    String artifactId = startingGroup.substring(slash + 1);
                    Artifact a = new Artifact(groupId, artifactId, null);
                    browseResults.addLast(a);
                    currentPageLinks.clear();
                    break;
                }

                int dotPos = href.lastIndexOf('.');
                String extension = dotPos < 0 ? "ok" : href.substring(dotPos + 1);

                if (!IGNORED_EXTENSIONS.contains(extension)) {
                    currentPageLinks.add(startingGroup + '/' + href);
                }
            }
        } catch (IOException e) {
            // just don't return these results
        }

        browseToBeProcessed.addAll(currentPageLinks);
        currentPageLinks.clear();
    }

    @Override
    public String toString() {
        return "ArtifactIterator [browseToBeProcessed=" + browseToBeProcessed + ", currentPageLinks=" + currentPageLinks + ", browseResults=" + browseResults
                + "]";
    }
}
