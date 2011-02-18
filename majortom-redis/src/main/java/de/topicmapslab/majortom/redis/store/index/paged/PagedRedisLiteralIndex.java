package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedLiteralIndexImpl;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisLiteralIndex extends PagedLiteralIndexImpl<RedisTopicMapStore> {

	public PagedRedisLiteralIndex(RedisTopicMapStore store, ILiteralIndex parentIndex) {
		super(store, parentIndex);
	}

}
