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

        Assert.assertEquals(10, artifacts.size());
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
    public void testGetVersions() throws IOException {

        CentralRepository r = new CentralRepository();

        List<String> versions = r.getVersions("com.mebigfatguy.yank", "yank");

        Assert.assertEquals("0.2.0", versions.get(versions.size() - 1));
    }

    @Test
    public void testGetLatestVersion() throws IOException {

        CentralRepository r = new CentralRepository();

        String version = r.getLatestVersion("com.mebigfatguy.yank", "yank");

        Assert.assertEquals("1.6.1", version);
    }

    @Test
    public void testGetPom() throws IOException {

        CentralRepository r = new CentralRepository();

        String pom = r.getPom("com.mebigfatguy.fb-delta", "fb-delta", "0.2.0");

        Assert.assertNotNull(pom);
        Assert.assertEquals(5991, pom.length());
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
}
