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

import java.util.Objects;

public class Artifact implements Comparable<Artifact> {

    private String groupId;
    private String artifactId;
    private String version;

    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Artifact)) {
            return false;
        }

        Artifact that = (Artifact) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(artifactId, that.artifactId) && Objects.equals(version, that.version);
    }

    @Override
    public int compareTo(Artifact a) {
        int cmp = groupId.compareTo(a.groupId);
        if (cmp != 0) {
            return cmp;
        }

        cmp = artifactId.compareTo(a.artifactId);
        if (cmp != 0) {
            return cmp;
        }

        return version.compareTo(a.version);
    }

    @Override
    public String toString() {
        return "Artifact [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
    }
}
