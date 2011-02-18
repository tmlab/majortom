package de.topicmapslab.majortom.redis.store;

import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;

public class RedisStoreIdentity implements ITopicMapStoreIdentity {

	private static final long serialVersionUID = 1L;
	private String id;

	public RedisStoreIdentity(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public long longId() {
		return 0;
	}

	public void setId(long id) {
		throw new UnsupportedOperationException();
	}

	public void setId(String id) {
		this.id = id;
	}

}
