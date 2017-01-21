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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

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
            throw new IllegalStateException("Iterator is exhausted");
        }

        return browseResults.removeFirst();
    }

    private void populateBrowse(String u, String startingGroup) {
        try (InputStream is = new BufferedInputStream(new URL(u).openStream())) {

            XMLReader r = XMLReaderFactory.createXMLReader();
            r.setContentHandler(new BrowseHandler(startingGroup));
            r.parse(new InputSource(is));
        } catch (FoundArtifactsException e) {
            int slash = startingGroup.lastIndexOf("/");
            String groupId = startingGroup.substring(0, slash).replace('/', '.');
            String artifactId = startingGroup.substring(slash + 1);
            Artifact a = new Artifact(groupId, artifactId, null);
            browseResults.addLast(a);
            currentPageLinks.clear();
            return;
        } catch (IOException |

                SAXException e) {
            // we just won't return results
        }

        browseToBeProcessed.addAll(currentPageLinks);
        currentPageLinks.clear();
    }

    class BrowseHandler extends DefaultHandler {

        private String root;

        public BrowseHandler(String rootPath) {
            root = rootPath + '/';
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("a".equals(localName)) {
                String href = attributes.getValue("href");
                if (href.endsWith("/")) {
                    href = href.substring(0, href.length() - 1);
                }
                if (href.equals(MAVEN_META_DATA)) {
                    throw new FoundArtifactsException();
                }

                int dotPos = href.lastIndexOf('.');

                String extension = dotPos < 0 ? "ok" : href.substring(dotPos + 1);

                if (!IGNORED_EXTENSIONS.contains(extension)) {
                    currentPageLinks.add(root + href);
                }
            }
        }
    }

    static class FoundArtifactsException extends RuntimeException {

        private static final long serialVersionUID = -6743806019037154885L;

    }
}
