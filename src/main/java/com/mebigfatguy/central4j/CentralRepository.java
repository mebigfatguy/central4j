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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CentralRepository implements Iterable<Artifact> {

    @Override
    public Iterator<Artifact> iterator() {
        return new ArtifactIterator();
    }

    public List<Artifact> getArtifactsByGroupId(String groupId) throws IOException {
        return Collections.emptyList();
    }

    public List<Artifact> getArtifactsByArtifactId(String groupId) throws IOException {
        return Collections.emptyList();
    }

    public List<String> getVersions(String groupId, String artifactId) throws IOException {
        return Collections.emptyList();
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

}
