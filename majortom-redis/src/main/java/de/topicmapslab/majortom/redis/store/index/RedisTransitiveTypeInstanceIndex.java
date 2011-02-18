package de.topicmapslab.majortom.redis.store.index;

import de.topicmapslab.majortom.index.nonpaged.CachedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class RedisTransitiveTypeInstanceIndex extends CachedTransitiveTypeInstanceIndex<RedisTopicMapStore> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public RedisTransitiveTypeInstanceIndex(RedisTopicMapStore store) {
		super(store);
	}

}
