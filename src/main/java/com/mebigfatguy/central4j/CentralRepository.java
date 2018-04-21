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

        URL u = new URL(CentralURLs.SEARCH_URL + "?q=g:\"" + groupId + "\"&rows=100&wt=json");
        URLConnection c = createSearchConnection(u);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<Artifact> artifacts = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");

            for (int i = 0; i < count; i++) {
                JSONObject jsonArtifact = docs.getJSONObject(i);

                artifacts.add(new Artifact(jsonArtifact.getString("g"), jsonArtifact.getString("a"), jsonArtifact.getString("latestVersion")));
            }

            return artifacts;
        }
    }

    public List<Artifact> getArtifactsByArtifactId(String artifactId) throws IOException {
        URL u = new URL(CentralURLs.SEARCH_URL + "?q=a:\"" + artifactId + "\"&rows=100&wt=json");
        URLConnection c = createSearchConnection(u);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<Artifact> artifacts = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");

            for (int i = 0; i < count; i++) {
                JSONObject jsonArtifact = docs.getJSONObject(i);

                artifacts.add(new Artifact(jsonArtifact.getString("g"), jsonArtifact.getString("a"), jsonArtifact.getString("latestVersion")));

            }

            return artifacts;
        }
    }

    public List<Artifact> getArtifactsByClassName(String className) throws IOException {
        URL u = new URL(CentralURLs.SEARCH_URL + "?q=c:\"" + className + "\"&rows=100&wt=json");
        URLConnection c = createSearchConnection(u);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<Artifact> artifacts = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");

            for (int i = 0; i < count; i++) {
                JSONObject jsonArtifact = docs.getJSONObject(i);

                artifacts.add(new Artifact(jsonArtifact.getString("g"), jsonArtifact.getString("a"), jsonArtifact.getString("v")));

            }

            return artifacts;
        }
    }

    public List<String> getVersions(String groupId, String artifactId) throws IOException {

        URL u = new URL(CentralURLs.SEARCH_URL + "?q=g:\"" + groupId + "\"+AND+a:\"" + artifactId + "\"&core=gav&rows=100&wt=json");
        URLConnection c = createSearchConnection(u);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");
            int count = jo.getInt("numFound");

            List<String> versions = new ArrayList<>(count);

            JSONArray docs = jo.getJSONArray("docs");

            for (int i = 0; i < count; i++) {
                JSONObject jsonArtifact = docs.getJSONObject(i);

                versions.add(jsonArtifact.getString("v"));

            }

            return versions;
        }
    }

    public String getLatestVersion(String groupId, String artifactId) throws IOException {
        URL u = new URL(CentralURLs.SEARCH_URL + "?q=g:\"" + groupId + "\"+AND+a:\"" + artifactId + "\"+AND+p:jar&core=gav&rows=1&wt=json");
        URLConnection c = createSearchConnection(u);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);
            jo = jo.getJSONObject("response");

            int count = jo.getInt("numFound");

            if (count <= 1) {
                throw new IOException("Artifact with groupId: " + groupId + " and artifactId: " + artifactId + " not found");
            }

            JSONArray docs = jo.getJSONArray("docs");

            JSONObject jsonArtifact = docs.getJSONObject(0);

            return jsonArtifact.getString("v");

        }
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

    public Statistics getStatistics() throws IOException {

        ZonedDateTime lastIndexTime = null;
        long artifactCount = 0;
        long uniqueArtifactCount = 0;
        long repositorySize = 0;
        List<Artifact> topDownloads = new ArrayList<>();

        URL u = new URL(CentralURLs.STATISTICS_URL);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

            String result = readerToString(br);

            JSONObject jo = new JSONObject(result);

            lastIndexTime = ZonedDateTime.parse(jo.getString("dateModified"), FORMATTER);
            artifactCount = Long.parseLong(jo.getString("gavNumber").replaceAll("(,|\\.)", ""));
            uniqueArtifactCount = Long.parseLong(jo.getString("gaNumber").replaceAll("(,|\\.)", ""));
            repositorySize = Long.parseLong(jo.getString("repoSize").replaceAll("(,|\\.)", ""));
        }

        Document doc = Jsoup.connect(CentralURLs.TOP_DOWNLOADS_URL).get();
        Elements dls = doc.getElementsByTag("dl");
        Element dl = dls.get(1);

        String groupId = null;
        for (Element child : dl.children()) {
            if ("dt".equals(child.nodeName())) {
                groupId = child.text();
            } else {
                topDownloads.add(new Artifact(groupId, child.text(), null));
            }
        }

        return new Statistics(lastIndexTime, artifactCount, uniqueArtifactCount, repositorySize, topDownloads);
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
