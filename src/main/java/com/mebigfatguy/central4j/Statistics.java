package com.mebigfatguy.central4j;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class Statistics {

    private ZonedDateTime lastIndexTime;
    private long artifactCount;
    private long uniqueArtifactCount;
    private long repositorySize;
    private List<Artifact> topDownloads;

    public Statistics(ZonedDateTime lastIndexTime, long artifactCount, long uniqueArtifactCount, long repositorySize, List<Artifact> topDownloads) {
        this.lastIndexTime = lastIndexTime;
        this.artifactCount = artifactCount;
        this.uniqueArtifactCount = uniqueArtifactCount;
        this.repositorySize = repositorySize;
        this.topDownloads = topDownloads;
    }

    public ZonedDateTime getLastIndexTime() {
        return lastIndexTime;
    }

    public long getArtifactCount() {
        return artifactCount;
    }

    public long getUniqueArtifactCount() {
        return uniqueArtifactCount;
    }

    public long getRepositorySize() {
        return repositorySize;
    }

    public List<Artifact> getTopDownloads() {
        return topDownloads;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastIndexTime, artifactCount, uniqueArtifactCount, repositorySize, topDownloads);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Statistics)) {
            return false;
        }

        Statistics that = (Statistics) obj;

        return Objects.equals(lastIndexTime, that.lastIndexTime) && Objects.equals(artifactCount, that.artifactCount)
                && Objects.equals(uniqueArtifactCount, that.uniqueArtifactCount) && Objects.equals(repositorySize, that.repositorySize)
                && Objects.equals(topDownloads, that.topDownloads);

    }

    @Override
    public String toString() {
        return "Statistics [lastIndexTime=" + lastIndexTime + ", artifactCount=" + artifactCount + ", uniqueArtifactCount=" + uniqueArtifactCount
                + ", repositorySize=" + repositorySize + ", topDownloads=" + topDownloads + "]";
    }

}
