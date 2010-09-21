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

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedLiteralIndexImpl<X extends ITopicMapStore> extends BaseCachedIndexImpl<X> implements ITopicMapListener {

	private Map<LiteralCacheKey, Collection<? extends Construct>> cachedConstructs;
	private Map<LiteralCacheKey, Collection<? extends Construct>> cachedLiterals;

	/**
	 * @param store
	 * @param parentIndex
	 */
	public BaseCachedLiteralIndexImpl(X store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * construct was removed
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED || event == TopicMapEventType.NAME_REMOVED || event == TopicMapEventType.OCCURRENCE_REMOVED || event == TopicMapEventType.TOPIC_REMOVED
				|| event == TopicMapEventType.ASSOCIATION_REMOVED || event == TopicMapEventType.ROLE_REMOVED) {
			clearCache();
		}
		/*
		 * new construct
		 */
		else if (event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.NAME_ADDED || event == TopicMapEventType.VARIANT_ADDED) {
			clearCache();
		}
		/*
		 * data type or value modified
		 */
		else if (event == TopicMapEventType.DATATYPE_SET || event == TopicMapEventType.VALUE_MODIFIED) {
			clearCache();
		}
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	protected final <T extends Construct> Collection<T> readConstructs(Class<T> clazz) {
		return readConstructs(clazz, null, null, null, null, null);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria - the datatype or <code>null</code>
	 * @param datatype
	 *            the datatype
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	protected final <T extends Construct> Collection<T> readConstructs(Class<T> clazz, Object filter, Object datatype) {
		return readConstructs(clazz, filter, datatype, null, null, null);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	protected final <T extends Construct> Collection<T> readConstructs(Class<T> clazz, Integer offset, Integer limit, Comparator<T> comparator) {
		return readConstructs(clazz, null, null, offset, limit, comparator);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria - the datatype or <code>null</code>
	 * @param datatype
	 *            the datatype
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends Construct> Collection<T> readConstructs(Class<T> clazz, Object filter, Object datatype, Integer offset, Integer limit, Comparator<T> comparator) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		/*
		 * get cached constructs by type
		 */
		return (Collection<T>) cachedConstructs.get(generateCacheKey(clazz, filter, datatype, offset, limit, comparator));
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param values
	 *            the values to store
	 */
	protected final <T extends Construct> void cacheConstructs(Class<T> clazz, Collection<T> values) {
		cacheConstructs(clazz, null, null, null, null, null, values);
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria - the datatype or <code>null</code>
	 * @param datatype
	 *            the datatype
	 * @param values
	 *            the values to store
	 */
	protected final <T extends Construct> void cacheConstructs(Class<T> clazz, Object filter, Object datatype, Collection<T> values) {
		cacheConstructs(clazz, filter, datatype, null, null, null, values);
	}

	/**
	 * Internal method to add constructs of the given type to internal store. *
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final <T extends Construct> void cacheConstructs(Class<T> clazz, Integer offset, Integer limit, Comparator<T> comparator, Collection<T> values) {
		cacheConstructs(clazz, null, null, offset, limit, comparator, values);
	}

	/**
	 * Internal method to add constructs of the given type to internal store. *
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria - the datatype or <code>null</code>
	 * @param datatype
	 *            the datatype
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final <T extends Construct> void cacheConstructs(Class<T> clazz, Object filter, Object datatype, Integer offset, Integer limit, Comparator<T> comparator, Collection<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * store cached constructs by type
		 */
		cachedConstructs.put(generateCacheKey(clazz, filter, datatype, offset, limit, comparator), values);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (e.g. regular expression)
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	protected final <T extends IConstruct> Collection<T> read(Class<?> clazz, Object filter, Object deviance) {
		return read(clazz, filter, deviance, null, null, null);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (e.g. regular expression)
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	protected final <T extends IConstruct> Collection<T> read(Class<?> clazz, Object filter, Object deviance, Integer offset, Integer limit, Comparator<?> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedLiterals == null) {
			return null;
		}
		/*
		 * get cached constructs by comparator
		 */
		return (Collection<T>) cachedLiterals.get(generateCacheKey(clazz, filter, deviance, offset, limit, comparator));
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (e.g. regular expression)
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param values
	 *            the values to store
	 */
	protected final void cache(Class<?> clazz, Object filter, Object deviance, Collection<? extends Construct> values) {
		cache(clazz, filter, deviance, null, null, null, values);
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (e.g. regular expression)
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final void cache(Class<?> clazz, Object filter, Object deviance, Integer offset, Integer limit, Comparator<?> comparator, Collection<? extends Construct> values) {
		/*
		 * initialize cache
		 */
		if (cachedLiterals == null) {
			cachedLiterals = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached constructs by comparator
		 */
		cachedLiterals.put(generateCacheKey(clazz, filter, deviance, offset, limit, comparator), values);
	}

	/**
	 * Internal method to clear all caches
	 */
	protected final void clearCache() {
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedLiterals != null) {
			cachedLiterals.clear();
		}
	}

	/**
	 * Generates a key for internal caching
	 * 
	 * @param clazz
	 *            the class
	 * @param filter
	 *            the filter criteria or <code>null</code>
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated cache key;
	 */
	private LiteralCacheKey generateCacheKey(Class<?> clazz, Object filter, Object deviance, Integer offset, Integer limit, Comparator<?> comparator) {
		LiteralCacheKey key = new LiteralCacheKey(clazz, filter, deviance, offset, limit, comparator);
		return key;
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

class LiteralCacheKey {

	Comparator<?> comparator;
	Class<?> clazz;
	Integer offset;
	Integer limit;
	Object filter;
	Object deviance;

	/**
	 * constructor
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (e.g. regular expression)
	 * @param deviance
	 *            a deviance criteria (e.g. numeric value)
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public LiteralCacheKey(Class<?> clazz, Object filter, Object deviance, Integer offset, Integer limit, Comparator<?> comparator) {
		this.clazz = clazz;
		this.deviance = deviance;
		this.offset = offset;
		this.limit = limit;
		this.filter = filter;
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof LiteralCacheKey) {
			LiteralCacheKey key = (LiteralCacheKey) obj;
			boolean result = key.clazz.equals(clazz);
			result &= (filter == null) ? key.filter == null : filter.equals(key.filter);
			result &= (deviance == null) ? key.deviance == null : deviance.equals(key.deviance);
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
		hashCode |= (filter == null) ? 0 : filter.hashCode();
		hashCode |= (deviance == null) ? 0 : deviance.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}
}
