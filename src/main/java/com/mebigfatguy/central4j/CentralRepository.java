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
package com.mebigfatguy.central4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mebigfatguy.central4j.internal.CentralURLs;

public class CentralRepository implements Iterable<Artifact> {

    @Override
    public Iterator<Artifact> iterator() {
        return new ArtifactIterator();
    }

    public List<Artifact> getArtifactsByGroupId(String groupId) throws IOException {

        URL u = new URL(CentralURLs.SEARCH_URL + "?q=g:\"" + groupId + "\"&rows=100&wt=json");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

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
        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

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

    public List<String> getVersions(String groupId, String artifactId) throws IOException {

        URL u = new URL(CentralURLs.SEARCH_URL + "?q=g:\"" + groupId + "\"+AND+a:\"" + artifactId + "\"&core=gav&rows=100&wt=json");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), StandardCharsets.UTF_8))) {

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
        return null;
    }

    public InputStream getArtifact(String groupId, String artifactId, String version) throws IOException {
        return null;
    }

    public InputStream getArtifact(String groupId, String artifactId, String version, String classifier) throws IOException {
        return null;
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

}
