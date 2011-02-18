package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedTransitiveTypeInstanceIndexImpl;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisTransitiveTypeInstanceIndex extends PagedTransitiveTypeInstanceIndexImpl<RedisTopicMapStore> {

	public PagedRedisTransitiveTypeInstanceIndex(RedisTopicMapStore store, ITransitiveTypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

}
