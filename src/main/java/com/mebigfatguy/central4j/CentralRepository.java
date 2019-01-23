/*
 * central4j - an api for accessing maven central
 * Copyright 2016-2019 MeBigFatGuy.com
 * Copyright 2016-2019 Dave Brosius
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mebigfatguy.central4j.internal.CentralURLs;

public class CentralRepository implements Iterable<Artifact> {

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    @Override
    public Iterator<Artifact> iterator() {
        return new ArtifactIterator();
    }

    public Iterable<Artifact> subGroupIterable(final String startingGroupPrefix) {
        return new Iterable<Artifact>() {
            @Override
            public Iterator<Artifact> iterator() {
                return new ArtifactIterator(startingGroupPrefix);
            }
        };
    }

    public List<Artifact> getArtifactsByGroupId(String groupId) throws IOException {

        Document doc = Jsoup.connect(CentralURLs.DOWNLOAD_URL + "/" + groupId.replace('.', '/')).get();

        Elements artifactElements = doc.getElementsByTag("a");
        List<Artifact> artifacts = new ArrayList<>();

        for (Element artifactElement : artifactElements) {
            String artifactId = artifactElement.attr("title");
            if (artifactId.endsWith("/")) {
                artifactId = artifactId.substring(0, artifactId.length() - 1);
            }
            if (!artifactId.isEmpty()) {
                artifacts.add(new Artifact(groupId, artifactId, ""));
            }
        }

        return artifacts;

    }

    public List<Artifact> getArtifactsByArtifactId(String artifactId) throws IOException {
        URL u = new URL(CentralURLs.SEARCH_URL + "?q=a:" + artifactId + "&start=0&rows=100");
        URLConnection c = createSearchConnection(u);

        c.connect();

        boolean isGZipped = "gzip".equalsIgnoreCase(c.getContentEncoding());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(isGZipped ? new GZIPInputStream(c.getInputStream()) : c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<Artifact> artifacts = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");
            for (Object d : docs) {
                JSONObject jsonArtifact = (JSONObject) d;
                artifacts.add(new Artifact(jsonArtifact.getString("g"), jsonArtifact.getString("a"), jsonArtifact.getString("latestVersion")));
            }

            return artifacts;
        }
    }

    public List<Artifact> getArtifactsByClassName(String className) throws IOException {
        URL u = new URL(CentralURLs.SEARCH_URL + "?q=c:" + className + "&start=0&rows=100");
        URLConnection c = createSearchConnection(u);

        c.connect();

        boolean isGZipped = "gzip".equalsIgnoreCase(c.getContentEncoding());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(isGZipped ? new GZIPInputStream(c.getInputStream()) : c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<Artifact> artifacts = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");
            for (Object d : docs) {
                JSONObject jsonArtifact = (JSONObject) d;
                artifacts.add(new Artifact(jsonArtifact.getString("g"), jsonArtifact.getString("a"), jsonArtifact.getString("v")));
            }

            return artifacts;
        }
    }

    public List<String> getVersions(String groupId, String artifactId) throws IOException {

        Document doc = Jsoup.connect(CentralURLs.DOWNLOAD_URL + "/" + groupId.replace('.', '/') + "/" + artifactId).get();
        Elements versionElements = doc.getElementsByTag("a");

        List<String> versions = new ArrayList<>(versionElements.size());
        for (Element versionElement : versionElements) {
            String version = versionElement.attr("title");
            if (version.endsWith("/")) {
                version = version.substring(0, version.length() - 1);
            }
            if (!version.isEmpty() && !version.contains("maven-metadata")) {
                versions.add(version);
            }
        }

        return versions;

    }

    public String getLatestVersion(String groupId, String artifactId) throws IOException {

        List<String> versions = getVersions(groupId, artifactId);
        if (versions.isEmpty()) {
            return null;
        }

        return versions.get(versions.size() - 1);
    }

    public String getPom(String groupId, String artifactId, String version) throws IOException {

        URL u = new URL(
                CentralURLs.DOWNLOAD_URL + '/' + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".pom");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

            return readerToString(br);
        }
    }

    public String getSHA1Hash(String groupId, String artifactId, String version) throws IOException {
        URL u = new URL(
                CentralURLs.DOWNLOAD_URL + '/' + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version + ".jar.sha1");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

            return readerToString(br);
        }
    }

    public InputStream getArtifact(String groupId, String artifactId, String version) throws IOException {
        return getArtifact(groupId, artifactId, version, null);
    }

    public InputStream getArtifact(String groupId, String artifactId, String version, String classifier) throws IOException {

        URL u = new URL(CentralURLs.DOWNLOAD_URL + '/' + groupId.replace('.', '/') + '/' + artifactId + '/' + version + '/' + artifactId + '-' + version
                + ((classifier != null) ? ("-" + classifier) : "") + ".jar");
        return u.openStream();
    }

    private String readerToString(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();

        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }

        return sb.toString();
    }

    private URLConnection createSearchConnection(URL u) throws IOException {
        URLConnection c = u.openConnection();
        c.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        c.setRequestProperty("Accept-Encoding", "gzip, deflate");
        c.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        c.setRequestProperty("Cache-Control", "max-age=0");
        c.setRequestProperty("Upgrade-Insecure-Requests", "1");
        c.setRequestProperty("User-Agent", " Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:57.0) Gecko/20100101 Firefox/57.0");
        c.setRequestProperty("Referer", CentralURLs.SEARCH_BASE_URL);

        List<String> cookies = getCookies();
        for (String cookie : cookies) {
            c.addRequestProperty("Cookie", cookie);
        }
        return c;
    }

    private List<String> getCookies() throws IOException {
        URL u = new URL(CentralURLs.SEARCH_BASE_URL);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.connect();

        try {
            Map<String, List<String>> headers = c.getHeaderFields();
            List<String> cookies = headers.get("Cookie");
            if (cookies == null) {
                return Collections.emptyList();
            }

            return cookies;
        } finally {
            c.disconnect();
        }
    }

}
