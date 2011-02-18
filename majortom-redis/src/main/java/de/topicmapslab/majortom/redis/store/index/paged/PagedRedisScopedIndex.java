package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedScopeIndexImpl;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisScopedIndex extends PagedScopeIndexImpl<RedisTopicMapStore> {

	public PagedRedisScopedIndex(RedisTopicMapStore store, IScopedIndex parentIndex) {
		super(store, parentIndex);		
	}

}
