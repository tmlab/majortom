package de.topicmapslab.majortom.testsuite.readonly;

import de.topicmapslab.majortom.model.core.ITopicMap;

public abstract class AbstractTest {
	
	protected static ITopicMap map;
	
	public static void setTopicMap(ITopicMap topicMap){
		map = topicMap;
	}
}
