package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisSupertypeSubtypeIndex extends PagedSupertypeSubtypeIndexImpl<RedisTopicMapStore> {

	public PagedRedisSupertypeSubtypeIndex(RedisTopicMapStore store, ISupertypeSubtypeIndex parentIndex) {
		super(store, parentIndex);
	}

}
