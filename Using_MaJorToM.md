# How to Use the MaJorToM Topic Maps engine #

To use the MaJorToM Topic Maps Engine the following steps are necessary:

1) Download the MaJorToM in-Memory Jar
2a) Add Jar to classpath
or
2b) Use the Maven Dependency Management

3) Using the TMAPI Factory

```
TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
TopicMapSystem system = factory.newTopicMapSystem();
TopicMap topicMap = system.createTopicMap("http://psi.example.org/");
```

or

```
TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
// unchecked cast could fail if other engines are located in the classpath
ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
TopicMap topicMap = system.createTopicMap("http://engine.topicmapslab.de/", new InMemoryTopicMapStore(system));
```

4) Cast to MaJorToM Topic Map Interface to enable the usage of additional features

```
ITopicMap majortomTopicMap = (ITopicMap) topicMap;
```