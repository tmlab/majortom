package de.topicmapslab.majortom.redis.store.index;

import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.STAR;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBTYPES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUPERTYPES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TOPICS_OF_TOPICMAP;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.redis.store.RedisStoreIdentity;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.HashUtil;

public class RedisSupertypeSubtypeIndex extends IndexImpl<RedisTopicMapStore> implements ISupertypeSubtypeIndex {

	private RedisHandler redis;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the redis topic map store
	 */
	public RedisSupertypeSubtypeIndex(RedisTopicMapStore store) {
		super(store);
		redis = getTopicMapStore().getRedis();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		Set<String> keys = redis.list(STAR + COLON + SUBTYPES);
		for (String key : keys) {
			int index = key.lastIndexOf(COLON + SUBTYPES);
			set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key.substring(0, index)),
					getTopicMapStore().getTopicMap()));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (type == null) {
			return getDirectSupertypes(null);
		}
		Set<Topic> known = HashUtil.getHashSet();
		return getSupertypes(type, known);
	}

	/**
	 * Utility method to handle traversal super-type-sub-type relation
	 * 
	 * @param type
	 *            the type
	 * @param known
	 *            list known super types to avoid cycles
	 * @return the set of found super types
	 */
	private Collection<Topic> getSupertypes(Topic type, Set<Topic> known) {
		Collection<Topic> result = HashUtil.getHashSet();
		for (Topic type_ : getDirectSupertypes(type)) {
			if (known.contains(type_)) {
				continue;
			}
			known.add(type_);
			result.add(type_);
			result.addAll(getSupertypes(type_, known));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			Set<String> ids = redis.smembers(TOPICS_OF_TOPICMAP);
			Set<String> keys = redis.list(STAR + COLON + SUPERTYPES);
			for (String id : ids) {
				if (!keys.contains(id + COLON + SUPERTYPES)) {
					set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap()));
				}
			}
		} else {
			for (String id : redis.smembers(type.getId() + COLON + SUPERTYPES)) {
				set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap()));
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic... types) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		return getSupertypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		return getSupertypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		boolean first = true;
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (first || !all) {
				set.addAll(getSupertypes(type));
				first = false;
			} else {
				set.retainAll(getSupertypes(type));
			}
			if (all && set.isEmpty()) {
				return Collections.emptySet();
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		Set<String> keys = redis.list(STAR + COLON + SUPERTYPES);
		for (String key : keys) {
			int index = key.lastIndexOf(COLON + SUPERTYPES);
			set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key.substring(0, index)),
					getTopicMapStore().getTopicMap()));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic type) {
		if (type == null) {
			return getDirectSubtypes(null);
		}
		Set<Topic> known = HashUtil.getHashSet();
		return getSubtypes(type, known);
	}

	/**
	 * Utility method to handle traversal super-type-sub-type relation
	 * 
	 * @param type
	 *            the type
	 * @param known
	 *            list known sub types to avoid cycles
	 * @return the set of found sub types
	 */
	private Collection<Topic> getSubtypes(Topic type, Set<Topic> known) {
		Collection<Topic> result = HashUtil.getHashSet();
		for (Topic type_ : getDirectSubtypes(type)) {
			if (known.contains(type_)) {
				continue;
			}
			known.add(type_);
			result.add(type_);
			result.addAll(getSubtypes(type_, known));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			Set<String> ids = redis.smembers(TOPICS_OF_TOPICMAP);
			Set<String> keys = redis.list(STAR + COLON + SUBTYPES);
			for (String id : ids) {
				if (!keys.contains(id + COLON + SUBTYPES)) {
					set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap()));
				}
			}
		} else {
			for (String id : redis.smembers(type.getId() + COLON + SUBTYPES)) {
				set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap()));
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic... types) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		return getSubtypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		return getSubtypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Given parameter cannot be null!");
		}
		boolean first = true;
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (first || !all) {
				set.addAll(getSubtypes(type));
				first = false;
			} else {
				set.retainAll(getSubtypes(type));
			}
			if (all && set.isEmpty()) {
				return Collections.emptySet();
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

}
