package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedTypeInstanceIndexImpl;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.store.index.RedisTypeInstanceIndex;

public class PagedRedisTypeInstanceIndex extends PagedTypeInstanceIndexImpl<RedisTopicMapStore> {

	public PagedRedisTypeInstanceIndex(RedisTopicMapStore store, RedisTypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

}
