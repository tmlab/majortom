If you use [Maven](http://maven.apache.org/) or  [Apache Ivy](http://ant.apache.org/ivy/) in your [Ant](http://ant.apache.org/)-based project, you can include the following dependencies:

### Include the MaJorToM API ###

```
  <repositories>
      <repository>
          <id>TMLab Public Repository</id>
          <url>http://maven.topicmapslab.de/public/</url>
      </repository>
  </repositories>

  <dependencies>
      <dependency>
          <groupId>de.topicmapslab.majortom</groupId>
          <artifactId>majortom-model</artifactId>
          <version>1.2.0</version>
      </dependency>
  </dependencies>
```

### Include the MaJorToM In-Memory store ###

```
  <repositories>
      <repository>
          <id>TMLab Public Repository</id>
          <url>http://maven.topicmapslab.de/public/</url>
      </repository>
  </repositories>

  <dependencies>
      <dependency>
          <groupId>de.topicmapslab.majortom</groupId>
          <artifactId>majortom-inMemory</artifactId>
          <version>1.2.0</version>
      </dependency>
  </dependencies>
```