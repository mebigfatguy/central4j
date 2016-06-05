package com.mebigfatguy.central4j;

import java.io.IOException;
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
}
