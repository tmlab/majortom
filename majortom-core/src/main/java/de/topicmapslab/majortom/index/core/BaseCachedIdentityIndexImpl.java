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
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IIdentityIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedIdentityIndexImpl<T extends ITopicMapStore> extends IndexImpl<T> implements ITopicMapListener {

	/**
	 * enumeration representing the map keys
	 */
	public enum Type {
		IDENTIFIER,

		ITEM_IDENTIFIER,

		SUBJECT_IDENTIFIER,

		SUBJECT_LOCATOR
	}

	private Map<IdentityCacheKey, Collection<Locator>> cachedIdentifiers;
	private Map<IdentityCacheKey, Collection<? extends Construct>> cachedConstructs;
	private Map<Type, Set<IdentityCacheKey>> dependentCacheKeys;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public BaseCachedIdentityIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {

		/*
		 * a construct was removed or created
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED || event == TopicMapEventType.VARIANT_ADDED || event == TopicMapEventType.NAME_REMOVED || event == TopicMapEventType.NAME_ADDED
				|| event == TopicMapEventType.OCCURRENCE_REMOVED || event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.ASSOCIATION_REMOVED
				|| event == TopicMapEventType.ASSOCIATION_ADDED || event == TopicMapEventType.ROLE_REMOVED || event == TopicMapEventType.ROLE_ADDED) {
			clearDependentCache(Type.IDENTIFIER, Type.ITEM_IDENTIFIER);
		}
		/*
		 * a topic was created, removed or merged
		 */
		else if (event == TopicMapEventType.TOPIC_REMOVED || event == TopicMapEventType.TOPIC_ADDED || event == TopicMapEventType.MERGE) {
			clearCache();
		}
		/*
		 * subject-identifier was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.SUBJECT_IDENTIFIER_ADDED || event == TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED) {
			clearDependentCache(Type.IDENTIFIER, Type.SUBJECT_IDENTIFIER);
		}

		/*
		 * subject-locator was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.SUBJECT_LOCATOR_ADDED || event == TopicMapEventType.SUBJECT_LOCATOR_REMOVED) {
			clearDependentCache(Type.IDENTIFIER, Type.SUBJECT_LOCATOR);
		}

		/*
		 * item-identifier was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.ITEM_IDENTIFIER_ADDED || event == TopicMapEventType.ITEM_IDENTIFIER_REMOVED) {
			clearDependentCache(Type.IDENTIFIER, Type.ITEM_IDENTIFIER);
		}
	}

	/**
	 * Clear all caches
	 */
	private void clearCache() {
		if (cachedIdentifiers != null) {
			cachedIdentifiers.clear();
		}
		if (dependentCacheKeys != null) {
			dependentCacheKeys.clear();
		}
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
	}

	/**
	 * Clear all caches depend on the given types
	 * 
	 * @param types
	 *            the types
	 */
	private void clearDependentCache(Type... types) {
		for (Type type : types) {
			for (IdentityCacheKey key : getDependentKeys(type)) {
				if (cachedIdentifiers != null) {
					cachedIdentifiers.remove(key);
				}
				if (dependentCacheKeys != null) {
					dependentCacheKeys.remove(key);
				}
				if (cachedConstructs != null) {
					cachedConstructs.remove(key);
				}
			}
		}
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param type
	 *            the type
	 * @return the identifiers or <code>null</code> if key is unknown.
	 */
	protected final Collection<Locator> readLocators(Type type) {
		return readLocators(type, null, null, null, null);
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the identifiers or <code>null</code> if key is unknown.
	 */
	protected final Collection<Locator> readLocators(Type type, Integer offset, Integer limit, Comparator<Locator> comparator) {
		return readLocators(type, null, offset, limit, comparator);
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @return the identifiers or <code>null</code> if key is unknown.
	 */
	protected final Collection<Locator> readLocators(Type type, Pattern filter) {
		return readLocators(type, filter, null, null, null);
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the identifiers or <code>null</code> if key is unknown.
	 */
	protected final Collection<Locator> readLocators(Type type, Pattern filter, Integer offset, Integer limit, Comparator<Locator> comparator) {
		/*
		 * check main cache
		 */
		if (cachedIdentifiers == null) {
			return null;
		}
		/*
		 * store cached identifiers by type
		 */
		return cachedIdentifiers.get(generateCacheKey(type, filter, offset, limit, comparator));
	}

	/**
	 * Store the given values into internal cache.
	 * 
	 * @param type
	 *            the type
	 * @param values
	 *            the locators to store
	 */
	protected final void cacheLocators(Type type, Collection<Locator> values) {
		cacheLocators(type, null, null, null, null, values);
	}

	/**
	 * Store the given values into internal cache.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the locators to store
	 */
	protected final void cacheLocators(Type type, Integer offset, Integer limit, Comparator<Locator> comparator, Collection<Locator> values) {
		cacheLocators(type, null, offset, limit, comparator, values);
	}

	/**
	 * Store the given values into internal cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param values
	 *            the locators to store
	 */
	protected final void cacheLocators(Type type, Pattern filter, Collection<Locator> values) {
		cacheLocators(type, filter, null, null, null, values);
	}

	/**
	 * Store the given values into internal cache.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the locators to store
	 */
	protected final void cacheLocators(Type type, Pattern filter, Integer offset, Integer limit, Comparator<Locator> comparator, Collection<Locator> values) {
		/*
		 * initialize cache
		 */
		if (cachedIdentifiers == null) {
			cachedIdentifiers = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached identifiers by type
		 */
		cachedIdentifiers.put(generateCacheKey(type, filter, offset, limit, comparator), values);
	}

	/**
	 * Store the given values into the internal cache using the given key
	 * values.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the value to cache
	 */
	protected final <X extends Construct> void cache(Type type, Pattern filter, Collection<X> constructs) {
		cache(type, filter, null, null, null, constructs);
	}

	/**
	 * Store the given values into the internal cache using the given key
	 * values.
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the value to cache
	 */
	protected final <X extends Construct> void cache(Type type, Pattern filter, Integer offset, Integer limit, Comparator<X> comparator, Collection<X> constructs) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		cachedConstructs.put(generateCacheKey(type, filter, offset, limit, comparator), constructs);
	}

	/**
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern.
	 * 
	 * @param type
	 *            the type
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	protected final <X extends Construct> Collection<X> read(Type type, Pattern filter) {
		return read(type, filter, null, null, null);
	}

	/**
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	protected final <X extends Construct> Collection<X> read(Type type, Pattern filter, Integer offset, Integer limit, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		return (Collection<X>) cachedConstructs.get(generateCacheKey(type, filter, offset, limit, comparator));
	}

	/**
	 * Generates a key for internal caching
	 * 
	 * @param type
	 *            the type
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated cache key;
	 */
	private IdentityCacheKey generateCacheKey(Type type, Object filter, Integer offset, Integer limit, Comparator<?> comparator) {
		IdentityCacheKey key = new IdentityCacheKey(type, filter, offset, limit, comparator);
		/*
		 * store dependent keys for clearing cache
		 */
		if (dependentCacheKeys == null) {
			dependentCacheKeys = HashUtil.getHashMap();
		}
		Set<IdentityCacheKey> keys = dependentCacheKeys.get(type);
		if (keys == null) {
			keys = HashUtil.getHashSet();
			dependentCacheKeys.put(type, keys);
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
	private Set<IdentityCacheKey> getDependentKeys(Type type) {
		if (dependentCacheKeys == null || !dependentCacheKeys.containsKey(type)) {
			return Collections.emptySet();
		}
		return dependentCacheKeys.get(type);
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

}

class IdentityCacheKey {

	Comparator<?> comparator;
	BaseCachedIdentityIndexImpl.Type type;
	Integer offset;
	Integer limit;
	Object filter;

	/**
	 * constructor
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (regular expression)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public IdentityCacheKey(BaseCachedIdentityIndexImpl.Type type, Object filter, Integer offset, Integer limit, Comparator<?> comparator) {
		this.type = type;
		this.offset = offset;
		this.limit = limit;
		this.filter = filter;
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IdentityCacheKey) {
			IdentityCacheKey key = (IdentityCacheKey) obj;
			boolean result = key.type.equals(type);
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
		hashCode |= (filter == null) ? 0 : filter.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}
}
