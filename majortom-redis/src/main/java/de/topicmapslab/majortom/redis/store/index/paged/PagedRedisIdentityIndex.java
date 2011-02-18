package de.topicmapslab.majortom.redis.store.index.paged;

import de.topicmapslab.majortom.index.paged.PagedIdentityIndexImpl;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;

public class PagedRedisIdentityIndex extends PagedIdentityIndexImpl<RedisTopicMapStore> {

	public PagedRedisIdentityIndex(RedisTopicMapStore store, IIdentityIndex parentIndex) {
		super(store, parentIndex);
		// TODO Auto-generated constructor stub
	}

}
