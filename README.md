central4j
=========

an api for accessing maven central


Available at maven central with coordinates

         GroupId: com.mebigfatguy.central4j
      ArtifactId: central4j
         Version: 0.2.0


== Usage ==

The CentralRepository class holds all the access methods, just create an instance, and use its methods.

```java
    Iterable<Artifact> subGroupIterable(final String startingGroupPrefix);
		// Iterates over the artifacts under a particular group prefix
    	
    List<Artifact> getArtifactsByGroupId(String groupId)
    	// returns a list of artifacts by group
    	
    List<Artifact> getArtifactsByArtifactId(String artifactId)
    	// returns a list of artifacts by artifact id
    	
    List<Artifact> getArtifactsByClassName(String className)
    	// returns a list of artifacts by class name
    	
    List<String> getVersions(String groupId, String artifactId)
    	// returns a list of available versions for an artifact
    	
    String getLatestVersion(String groupId, String artifactId)
    	// returns the latest version of an artifact
    	
    String getPom(String groupId, String artifactId, String version)
    	// returns the pom for an artifact
    	
    InputStream getArtifact(String groupId, String artifactId, String version)
    	// returns the bytes of an artifact
    	
    ```
    	
    
    	
    
