package com.mebigfatguy.central4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class CentralRepositoryTest {

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
}
