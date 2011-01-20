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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the in-memory {@link IPagedTypeInstanceIndex} supporting paging
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedTypeInstanceIndexImpl<E extends ITopicMapStore> extends BaseCachedIndexImpl<E> implements ITopicMapListener {

	private Map<Class<?>, Set<TypeInstanceCacheKey>> dependentCacheKeys;

	/**
	 * internal cache for typed constructs
	 */
	private Map<TypeInstanceCacheKey, Collection<?>> cachedConstructs;
	/**
	 * internal cache for number of typed constructs
	 */
	private Map<TypeInstanceCacheKey, Long> cachedNumberOfConstructs;

	/**
	 * internal cache for types of constructs
	 */
	private Map<TypeInstanceCacheKey, Collection<Topic>> cachedTypes;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public BaseCachedTypeInstanceIndexImpl(E store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * association added or removed
		 */
		if (event == TopicMapEventType.ASSOCIATION_ADDED || event == TopicMapEventType.ASSOCIATION_REMOVED) {
			clearDependentCache(IAssociation.class);
		}
		/*
		 * role added or removed
		 */
		else if (event == TopicMapEventType.ROLE_ADDED || event == TopicMapEventType.ROLE_REMOVED) {
			clearDependentCache(IAssociationRole.class);
		}
		/*
		 * name added or removed
		 */
		else if (event == TopicMapEventType.NAME_ADDED || event == TopicMapEventType.NAME_REMOVED) {
			clearDependentCache(IName.class, ICharacteristics.class);
		}
		/*
		 * occurrence added or removed
		 */
		else if (event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.OCCURRENCE_REMOVED) {
			clearDependentCache(IOccurrence.class, ICharacteristics.class);
		}
		/*
		 * topic merged or removed
		 */
		else if (event == TopicMapEventType.TOPIC_REMOVED || event == TopicMapEventType.MERGE) {
			clearCache();
		}
		/*
		 * type modified
		 */
		else if (event == TopicMapEventType.TYPE_SET) {
			if (notifier instanceof IAssociation) {
				clearDependentCache(IAssociation.class);
			} else if (notifier instanceof IAssociationRole) {
				clearDependentCache(IAssociationRole.class);
			} else if (notifier instanceof IName) {
				clearDependentCache(IName.class, ICharacteristics.class);
			} else {
				clearDependentCache(IOccurrence.class, ICharacteristics.class);
			}
		}
		/*
		 * type added or removed
		 */
		else if (event == TopicMapEventType.TYPE_ADDED || event == TopicMapEventType.TYPE_REMOVED || event == TopicMapEventType.TOPIC_ADDED) {
			clearDependentCache(ITopic.class);
		}
	}

	/**
	 * clear all internal caches
	 */
	public final void clearCache() {
		if (cachedTypes != null) {
			cachedTypes.clear();
			cachedTypes = HashUtil.getHashMap();
		}
		if (cachedConstructs != null) {
			cachedConstructs.clear();
			cachedConstructs = HashUtil.getHashMap();
		}
		if (dependentCacheKeys != null) {
			dependentCacheKeys.clear();
			dependentCacheKeys = HashUtil.getHashMap();
		}
		if (cachedNumberOfConstructs != null) {
			cachedNumberOfConstructs.clear();
		}
	}

	/**
	 * Method clears all caches depending on one of the given classes
	 * 
	 * @param classes
	 *            the classes
	 */
	public final void clearDependentCache(Class<? extends IConstruct>... classes) {
		for (Class<? extends IConstruct> clazz : classes) {
			for (TypeInstanceCacheKey key : getDependentKeys(clazz)) {
				if (cachedTypes != null) {
					cachedTypes.remove(key);
				}
				if (cachedConstructs != null) {
					cachedConstructs.remove(key);
				}
				if (cachedNumberOfConstructs != null) {
					cachedNumberOfConstructs.remove(key);
				}
			}
		}
	}

	/**
	 * Reads the number of matching construct from cache if it exists
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter
	 * @param matchAll
	 *            the match all flag
	 * @return the number of constructs or <code> -1 </code>
	 */
	protected long readNumberOfConstructs(Class<? extends Construct> clazz, Object filter, Boolean matchAll) {
		/*
		 * check main cache
		 */
		if (cachedNumberOfConstructs == null) {
			return -1;
		}
		/*
		 * get cached types
		 */
		Long value = cachedNumberOfConstructs.get(generateCacheKey(clazz, filter, matchAll, null, null, null));
		return value == null ? -1 : value;
	}

	/**
	 * Method reads the constructs by their types from internal cache
	 * 
	 * @param clazz
	 *            the class
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the types or <code>null</code> if the key-pairs is unknown
	 */
	protected Collection<Topic> read(Class<? extends Construct> clazz) {
		return read(clazz, null, null, null);
	}

	/**
	 * Method reads the constructs by their types from internal cache
	 * 
	 * @param clazz
	 *            the class
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the types or <code>null</code> if the key-pairs is unknown
	 */
	protected Collection<Topic> read(Class<? extends Construct> clazz, Integer offset, Integer limit, Comparator<Topic> comparator) {
		/*
		 * check main cache
		 */
		if (cachedTypes == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return cachedTypes.get(generateCacheKey(clazz, null, false, offset, limit, comparator));
	}

	/**
	 * Store the number of matching construct into cache
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter
	 * @param matchAll
	 *            the match all flag
	 * @param number
	 *            the number of constructs
	 */
	protected void cacheNumberOfConstructs(Class<? extends Construct> clazz, Object filter, Boolean matchAll, long number) {
		/*
		 * check main cache
		 */
		if (cachedNumberOfConstructs == null) {
			cachedNumberOfConstructs = HashUtil.getHashMap();
		}
		/*
		 * get cached types
		 */
		cachedNumberOfConstructs.put(generateCacheKey(clazz, filter, matchAll, null, null, null), number);
	}

	/**
	 * Method store the types to the internal cache.
	 * 
	 * @param clazz
	 *            the class
	 * @param values
	 *            the values to store
	 */
	protected void cache(Class<? extends IConstruct> clazz, Collection<Topic> values) {
		cache(clazz, null, null, null, values);
	}

	/**
	 * Method store the types to the internal cache.
	 * 
	 * @param clazz
	 *            the class
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected void cache(Class<? extends IConstruct> clazz, Integer offset, Integer limit, Comparator<Topic> comparator, Collection<Topic> values) {
		/*
		 * initialize main cache
		 */
		if (cachedTypes == null) {
			cachedTypes = HashUtil.getHashMap();
		}
		/*
		 * store cached types
		 */
		cachedTypes.put(generateCacheKey(clazz, null, false, offset, limit, comparator), values);
	}

	/**
	 * Method reads constructs by their types from cache.
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the result should match all types
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	protected <T extends Construct> Collection<T> read(Class<? extends IConstruct> clazz, Object filter, Boolean multiMatch) {
		return read(clazz, filter, multiMatch, null, null, null);
	}

	/**
	 * Method reads constructs by their types from cache.
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	protected <T extends Construct> Collection<T> read(Class<? extends IConstruct> clazz, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<? extends Construct> comparator) {
		/*
		 * check main main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		return (Collection<T>) cachedConstructs.get(generateCacheKey(clazz, filter, multiMatch, offset, limit, comparator));
	}

	/**
	 * Method store the given values.
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the result should match all types
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void cache(Class<? extends IConstruct> clazz, Object filter, Boolean multiMatch, Collection<T> values) {
		cache(clazz, filter, multiMatch, null, null, null, values);
	}

	/**
	 * Method store the given values.
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void cache(Class<? extends IConstruct> clazz, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<? extends Construct> comparator,
			Collection<T> values) {
		/*
		 * initialize main cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getHashMap();
		}
		/*
		 * store cached types
		 */
		cachedConstructs.put(generateCacheKey(clazz, filter, multiMatch, offset, limit, comparator), values);
	}

	/**
	 * Generates a key for internal caching
	 * 
	 * @param clazz
	 *            the clazz
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if the result should match all types
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated cache key;
	 */
	private TypeInstanceCacheKey generateCacheKey(Class<?> clazz, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<?> comparator) {
		TypeInstanceCacheKey key = new TypeInstanceCacheKey(clazz, filter, multiMatch, offset, limit, comparator);
		/*
		 * store dependent keys for clearing cache
		 */
		if (dependentCacheKeys == null) {
			dependentCacheKeys = HashUtil.getHashMap();
		}
		Set<TypeInstanceCacheKey> keys = dependentCacheKeys.get(clazz);
		if (keys == null) {
			keys = HashUtil.getHashSet();
			dependentCacheKeys.put(clazz, keys);
		}
		keys.add(key);
		return key;
	}

	/**
	 * Returns a set of cache keys dependents on the given type
	 * 
	 * @param type
	 *            the type
	 * @return all dependent keys
	 */
	private Set<TypeInstanceCacheKey> getDependentKeys(Class<?> clazz) {
		if (dependentCacheKeys == null || !dependentCacheKeys.containsKey(clazz)) {
			return Collections.emptySet();
		}
		return dependentCacheKeys.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		getTopicMapStore().addTopicMapListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		getTopicMapStore().removeTopicMapListener(this);
		super.close();
	}

	/**
	 * Removed any cached content from internal cache
	 */
	public void clear() {
		clearCache();
	}

}

class TypeInstanceCacheKey {

	Class<?> clazz;
	Object filter;
	Boolean multiMatch;
	Integer offset;
	Integer limit;
	Comparator<?> comparator;

	/**
	 * constructor
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a type or a collection of type)
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of types if types should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public TypeInstanceCacheKey(Class<?> clazz, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<?> comparator) {
		this.clazz = clazz;
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
		if (obj instanceof TypeInstanceCacheKey) {
			TypeInstanceCacheKey key = (TypeInstanceCacheKey) obj;
			boolean result = key.clazz.equals(clazz);
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
		int hashCode = clazz.hashCode();
		hashCode |= (multiMatch == null) ? 0 : multiMatch.hashCode();
		hashCode |= (filter == null) ? 0 : filter.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}

}
