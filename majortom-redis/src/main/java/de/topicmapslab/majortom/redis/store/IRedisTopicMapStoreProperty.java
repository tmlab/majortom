package de.topicmapslab.majortom.redis.store;

import de.topicmapslab.majortom.store.TopicMapStoreProperty;

public interface IRedisTopicMapStoreProperty extends TopicMapStoreProperty{

	public static final String REDIS_PREFIX = PREFIX + ".redis";
	
	public static final String REDIS_HOST = REDIS_PREFIX + ".host";
	
	public static final String REDIS_PORT = REDIS_PREFIX + ".port";
	
	public static final String REDIS_PASSWORD = REDIS_PREFIX + ".password";
	
	public static final String REDIS_DATABASE = REDIS_PREFIX + ".database";
	
}
