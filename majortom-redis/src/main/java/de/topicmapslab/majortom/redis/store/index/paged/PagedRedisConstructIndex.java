package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedConstructIndexImpl;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisConstructIndex extends PagedConstructIndexImpl<RedisTopicMapStore> implements IPagedConstructIndex {

	public PagedRedisConstructIndex(RedisTopicMapStore store) {
		super(store);
	}

}
