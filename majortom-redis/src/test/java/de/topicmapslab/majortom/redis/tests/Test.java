package de.topicmapslab.majortom.redis.tests;

import junit.framework.TestCase;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

public class Test extends TestCase{
	
	public void testname() throws Exception {
		TopicMap tm = TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap("http://hallo.txt");
		
	}
	
}
