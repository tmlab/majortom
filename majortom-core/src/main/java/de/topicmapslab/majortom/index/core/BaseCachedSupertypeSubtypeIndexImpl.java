/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.index.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedSupertypeSubtypeIndexImpl<T extends ITopicMapStore> extends BaseCachedIndexImpl<T> implements ITopicMapListener {

	/**
	 * enumeration to specify the type of type-hierarchy
	 * 
	 * @author Sven Krosse
	 * 
	 */
	public enum Type {

		ALL_SUPERTYPES,

		ALL_SUBTYPES,

		SUPERTYPE,

		DIRECT_SUPERTYPE,

		SUBTYPE,

		DIRECT_SUBTYPE
	}

	private Map<SupertypeSubtypeCacheKey, Collection<Topic>> cache;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the internal topic map store
	 */
	public BaseCachedSupertypeSubtypeIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * a topic was removed
		 */
		if (event == TopicMapEventType.TOPIC_REMOVED || event == TopicMapEventType.MERGE || event == TopicMapEventType.TOPIC_ADDED) {
			clearCache();
		}
		/*
		 * a super type relation was changed
		 */
		else if (event == TopicMapEventType.SUPERTYPE_ADDED || event == TopicMapEventType.SUPERTYPE_REMOVED) {
			clearCache();
		}
	}

	/**
	 * Clear all caches
	 */
	protected final void clearCache() {
		if (cache != null) {
			cache.clear();
		}
	}

	/**
	 * Internal method to read the type-hierarchy topics of the given type from
	 * cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the
	 *            result should match all types
	 * @return the type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	protected final Collection<Topic> read(Type type, Object filter, Boolean multiMatch) {
		return read(type, filter, multiMatch, null, null, null);
	}

	/**
	 * Internal method to read the type-hierarchy topics of the given type from
	 * cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the
	 *            result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	protected final Collection<Topic> read(Type type, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<Topic> comparator) {
		/*
		 * check main cache for compared type hierarchy
		 */
		if (cache == null) {
			return null;
		}
		return cache.get(generateCacheKey(type, filter, multiMatch, offset, limit, comparator));
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the
	 *            result should match all types
	 * @param values
	 *            the values to store
	 */
	protected final void cache(Type type, Object filter, Boolean multiMatch, Collection<Topic> values) {
		cache(type, filter, multiMatch, null, null, null, values);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the
	 *            result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final void cache(Type type, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<Topic> comparator, Collection<Topic> values) {
		/*
		 * initialize cache for compared type hierarchy
		 */
		if (cache == null) {
			cache = HashUtil.getWeakHashMap();
		}
		/*
		 * store compared list
		 */
		cache.put(generateCacheKey(type, filter, multiMatch, offset, limit, comparator), values);
	}

	/**
	 * Generates a key for internal caching
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the
	 *            result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated cache key;
	 */
	private SupertypeSubtypeCacheKey generateCacheKey(Type type, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<Topic> comparator) {
		return new SupertypeSubtypeCacheKey(type, filter, multiMatch, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		getStore().addTopicMapListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		getStore().removeTopicMapListener(this);
		super.close();
	}

	/**
	 * Removed any cached content from internal cache
	 */
	public void clear() {
		clearCache();
	}

}

class SupertypeSubtypeCacheKey {

	BaseCachedSupertypeSubtypeIndexImpl.Type type;
	Object filter;
	Boolean multiMatch;
	Integer offset;
	Integer limit;
	Comparator<Topic> comparator;

	/**
	 * constructor
	 * 
	 * @param type
	 *            the type of type-hierarchy
	 * @param filter
	 *            a filter criteria (a type or a collection of type)
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if types
	 *            should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public SupertypeSubtypeCacheKey(BaseCachedSupertypeSubtypeIndexImpl.Type type, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<Topic> comparator) {
		this.type = type;
		this.offset = offset;
		this.limit = limit;
		this.filter = filter;
		this.multiMatch = multiMatch;
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof SupertypeSubtypeCacheKey) {
			SupertypeSubtypeCacheKey key = (SupertypeSubtypeCacheKey) obj;
			boolean result = key.type.equals(type);
			result &= (multiMatch == null) ? key.multiMatch == null : multiMatch.equals(key.multiMatch);
			result &= (filter == null) ? key.filter == null : filter.equals(key.filter);
			result &= (comparator == null) ? key.comparator == null : comparator.equals(key.comparator);
			result &= (offset == null) ? key.offset == null : offset.equals(key.offset);
			result &= (limit == null) ? key.limit == null : limit.equals(key.limit);
			return result;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hashCode = type.hashCode();
		hashCode |= (multiMatch == null) ? 0 : multiMatch.hashCode();
		hashCode |= (filter == null) ? 0 : filter.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}

}
