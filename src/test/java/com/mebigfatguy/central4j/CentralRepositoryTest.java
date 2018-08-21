package com.mebigfatguy.central4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CentralRepositoryTest {

    @Test
    public void testIterate() {
        CentralRepository r = new CentralRepository();

        List<Artifact> artifacts = new ArrayList<>();
        for (Artifact a : r.subGroupIterable("com.mebigfatguy")) {
            artifacts.add(a);
        }

        Assert.assertEquals(15, artifacts.size());
    }

    @Test
    public void testGetArtifactsByGroupId() throws IOException {

        CentralRepository r = new CentralRepository();

        List<Artifact> artifacts = r.getArtifactsByGroupId("com.mebigfatguy.fb-contrib");

        Assert.assertEquals(1, artifacts.size());
        Assert.assertEquals("fb-contrib", artifacts.get(0).getArtifactId());
    }

    @Test
    public void testGetArtifactsByArtifactId() throws IOException {

        CentralRepository r = new CentralRepository();

        List<Artifact> artifacts = r.getArtifactsByArtifactId("yank");

        Assert.assertEquals(3, artifacts.size());

        Collections.sort(artifacts, new Comparator<Artifact>() {

            @Override
            public int compare(Artifact o1, Artifact o2) {
                return o1.getGroupId().compareTo(o2.getGroupId());
            }

        });

        Assert.assertEquals("com.mebigfatguy.yank", artifacts.get(0).getGroupId());
        Assert.assertEquals("com.xeiam", artifacts.get(1).getGroupId());
        Assert.assertEquals("org.knowm", artifacts.get(2).getGroupId());
    }

    @Test
    public void testGetArtifactsByClassName() throws IOException {

        CentralRepository r = new CentralRepository();

        List<Artifact> artifacts = r.getArtifactsByClassName("ArtifactIterator");

        Collections.sort(artifacts, new Comparator<Artifact>() {

            @Override
            public int compare(Artifact o1, Artifact o2) {
                return o1.getGroupId().compareTo(o2.getGroupId());
            }

        });

        Artifact expectedArtifact = new Artifact("com.mebigfatguy.central4j", "central4j", "0.2.0");
        Assert.assertTrue(artifacts.contains(expectedArtifact));
    }

    @Test
    public void testGetVersions() throws IOException {

        CentralRepository r = new CentralRepository();

        List<String> versions = r.getVersions("com.mebigfatguy.yank", "yank");

        Assert.assertEquals(14, versions.size());
    }

    @Test
    public void testGetLatestVersion() throws IOException {

        CentralRepository r = new CentralRepository();

        String version = r.getLatestVersion("com.mebigfatguy.yank", "yank");

        Assert.assertEquals("2.0.0", version);
    }

    @Test
    public void testGetPom() throws IOException {

        CentralRepository r = new CentralRepository();

        String pom = r.getPom("com.mebigfatguy.fb-delta", "fb-delta", "0.2.0");

        Assert.assertNotNull(pom);
        Assert.assertEquals(5991, pom.length());
    }

    @Test
    public void testGetSHA1() throws IOException {

        CentralRepository r = new CentralRepository();

        String sha1 = r.getSHA1Hash("com.mebigfatguy.fb-delta", "fb-delta", "0.2.0");

        Assert.assertNotNull(sha1);
        Assert.assertEquals("2c21934b6e0feb4aa3a98c315159a7ad5c03a66e", sha1);
    }

    @Test
    public void testGetArtifact() throws IOException {

        CentralRepository r = new CentralRepository();

        try (InputStream is = r.getArtifact("com.mebigfatguy.fb-delta", "fb-delta", "0.2.0"); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            long total = 0;
            while (len >= 0) {
                total += len;
                baos.write(buffer, 0, len);
                len = is.read(buffer);
            }

            Assert.assertEquals(8561, total);
        }
    }

    @Test
    public void testGetClassifierArtifact() throws IOException {

        CentralRepository r = new CentralRepository();

        try (InputStream is = r.getArtifact("com.mebigfatguy.fb-delta", "fb-delta", "0.2.0", "sources");
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            long total = 0;
            while (len >= 0) {
                total += len;
                baos.write(buffer, 0, len);
                len = is.read(buffer);
            }

            Assert.assertEquals(8305, total);
        }
    }

    @Test
    public void testGetStatistics() throws IOException {
        CentralRepository r = new CentralRepository();

        Statistics statistics = r.getStatistics();
        Assert.assertNotNull(statistics);

        Artifact junit = new Artifact("junit", "junit", null);
        Assert.assertTrue(statistics.getTopDownloads().contains(junit));
    }
}
