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
package de.topicmapslab.majortom.index.paged;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedSupertypeSubtypeIndexImpl<T extends ITopicMapStore> extends PagedIndexImpl<T, ISupertypeSubtypeIndex> implements
		IPagedSupertypeSubtypeIndex {

	public enum Param {

		SUPERTYPE_ALL,

		SUPERTYPE,

		DIRECT_SUPERTYPE_ALL,

		DIRECT_SUPERTYPE,

		SUBTYPE_ALL,

		SUBTYPE,

		DIRECT_SUBTYPE_ALL,

		DIRECT_SUBTYPE
	}

	private Map<Param, List<Topic>> cachedFullHierarchy;
	private Map<Param, Map<Comparator<Topic>, List<Topic>>> cachedComparedFullHierarchy;

	private Map<Param, Map<Topic, List<Topic>>> cachedHierarchy;
	private Map<Param, Map<Topic, Map<Comparator<Topic>, List<Topic>>>> cachedComparedHierarchy;
	private Map<Param, Map<Collection<? extends Topic>, List<Topic>>> cachedHierarchyMultipleTopics;
	private Map<Param, Map<Collection<? extends Topic>, Map<Comparator<Topic>, List<Topic>>>> cachedComparedHierarchyMultipleTopics;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the internal topic map store
	 * @param parentIndex
	 *            the parent index
	 */
	public PagedSupertypeSubtypeIndexImpl(T store, ISupertypeSubtypeIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.DIRECT_SUBTYPE, type);
		if (cache == null) {
			return doGetDirectSubtypes(type, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.DIRECT_SUBTYPE, type, comparator);
		if (cache == null) {
			return doGetDirectSubtypes(type, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.DIRECT_SUPERTYPE, type);
		if (cache == null) {
			return doGetDirectSupertypes(type, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.DIRECT_SUPERTYPE, type, comparator);
		if (cache == null) {
			return doGetDirectSupertypes(type, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE);
		if (cache == null) {
			return doGetSubtypes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE, comparator);
		if (cache == null) {
			return doGetSubtypes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE, type);
		if (cache == null) {
			return doGetSubtypes(type, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE, type, comparator);
		if (cache == null) {
			return doGetSubtypes(type, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE, types, false);
		if (cache == null) {
			return doGetSubtypes(types, false, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBTYPE, types, false, comparator);
		if (cache == null) {
			return doGetSubtypes(types, false, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Param param = all ? Param.SUBTYPE_ALL : Param.SUBTYPE;
		List<Topic> cache = read(param, types, all);
		if (cache == null) {
			return doGetSubtypes(types, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Param param = all ? Param.SUBTYPE_ALL : Param.SUBTYPE;
		List<Topic> cache = read(param, types, all, comparator);
		if (cache == null) {
			return doGetSubtypes(types, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE);
		if (cache == null) {
			return doGetSupertypes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE, comparator);
		if (cache == null) {
			return doGetSupertypes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE, type);
		if (cache == null) {
			return doGetSupertypes(type, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE, type, comparator);
		if (cache == null) {
			return doGetSupertypes(type, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE, types, false);
		if (cache == null) {
			return doGetSupertypes(types, false, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUPERTYPE, types, false, comparator);
		if (cache == null) {
			return doGetSupertypes(types, false, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Param param = all ? Param.SUPERTYPE_ALL : Param.SUPERTYPE;
		List<Topic> cache = read(param, types, all);
		if (cache == null) {
			return doGetSupertypes(types, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Param param = all ? Param.SUPERTYPE_ALL : Param.SUPERTYPE;
		List<Topic> cache = read(param, types, all, comparator);
		if (cache == null) {
			return doGetSupertypes(types, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * a topic was removed
		 */
		if (event == TopicMapEventType.TOPIC_REMOVED) {
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
	private final void clearCache() {
		if (cachedComparedFullHierarchy != null) {
			cachedComparedFullHierarchy.clear();
		}
		if (cachedComparedHierarchy != null) {
			cachedComparedHierarchy.clear();
		}
		if (cachedComparedHierarchyMultipleTopics != null) {
			cachedComparedHierarchyMultipleTopics.clear();
		}
		if (cachedFullHierarchy != null) {
			cachedFullHierarchy.clear();
		}
		if (cachedHierarchy != null) {
			cachedHierarchy.clear();
		}
		if (cachedHierarchyMultipleTopics != null) {
			cachedHierarchyMultipleTopics.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		super.close();
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param type
	 *            the topic type
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param, Topic type) {
		/*
		 * check main cache for type hierarchy
		 */
		if (cachedHierarchy == null) {
			return null;
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Topic, List<Topic>> cached = cachedHierarchy.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get types list
		 */
		return cached.get(type);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @see #read(Param, Topic)
	 * @param param
	 *            the hierarchy type
	 * @param type
	 *            the topic type
	 * @param values
	 *            the values to store
	 */
	protected final void store(Param param, Topic type, List<Topic> values) {
		/*
		 * initialize cache for type hierarchy
		 */
		if (cachedHierarchy == null) {
			cachedHierarchy = HashUtil.getWeakHashMap();
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Topic, List<Topic>> cached = cachedHierarchy.get(param);
		if (cached == null) {
			/*
			 * create specific hierarchy mappings and store it
			 */
			cached = HashUtil.getWeakHashMap();
			cachedHierarchy.put(param, cached);
		}
		/*
		 * store types list
		 */
		cached.put(type, values);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param type
	 *            the topic type
	 * @param comparator
	 *            the comparator
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param, Topic type, Comparator<Topic> comparator) {
		/*
		 * check main cache for compared type hierarchy
		 */
		if (cachedComparedHierarchy == null) {
			return null;
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Topic, Map<Comparator<Topic>, List<Topic>>> cachedCompared = cachedComparedHierarchy.get(param);
		if (cachedCompared == null) {
			return null;
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedCompared.get(type);
		if (cached == null) {
			return null;
		}
		/*
		 * get compared list
		 */
		return cached.get(comparator);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @see #read(Param, Topic, Comparator)
	 * @param param
	 *            the hierarchy type
	 * @param type
	 *            the topic type
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final void store(Param param, Topic type, Comparator<Topic> comparator, List<Topic> values) {
		/*
		 * initialize cache for compared type hierarchy
		 */
		if (cachedComparedHierarchy == null) {
			cachedComparedHierarchy = HashUtil.getWeakHashMap();
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Topic, Map<Comparator<Topic>, List<Topic>>> cachedCompared = cachedComparedHierarchy.get(param);
		if (cachedCompared == null) {
			/*
			 * create specific hierarchy mappings and store it
			 */
			cachedCompared = HashUtil.getWeakHashMap();
			cachedComparedHierarchy.put(param, cachedCompared);
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedCompared.get(type);
		if (cached == null) {
			/*
			 * create mapping between comparator and compared-lists and store it
			 */
			cached = HashUtil.getWeakHashMap();
			cachedCompared.put(type, cached);
		}
		/*
		 * store compared list
		 */
		cached.put(comparator, values);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param types
	 *            the topic types
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param, Collection<? extends Topic> types, boolean all) {
		/*
		 * check main cache for type hierarchy
		 */
		if (cachedHierarchyMultipleTopics == null) {
			return null;
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Collection<? extends Topic>, List<Topic>> cached = cachedHierarchyMultipleTopics.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get types list
		 */
		return cached.get(types);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @see #read(Param, Collection, boolean)
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param types
	 *            the topic types
	 * @param values
	 *            the values to store
	 * 
	 */
	protected final void store(Param param, Collection<? extends Topic> types, boolean all, List<Topic> values) {
		/*
		 * initialize cache for type hierarchy
		 */
		if (cachedHierarchyMultipleTopics == null) {
			cachedHierarchyMultipleTopics = HashUtil.getWeakHashMap();
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Collection<? extends Topic>, List<Topic>> cached = cachedHierarchyMultipleTopics.get(param);
		if (cached == null) {
			/*
			 * create specific hierarchy mappings and store it
			 */
			cached = HashUtil.getWeakHashMap();
			cachedHierarchyMultipleTopics.put(param, cached);
		}
		/*
		 * store types list
		 */
		cached.put(types, values);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param types
	 *            the topic types
	 * @param comparator
	 *            the comparator
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param, Collection<? extends Topic> types, boolean all, Comparator<Topic> comparator) {
		/*
		 * check main cache for compared type hierarchy
		 */
		if (cachedComparedHierarchyMultipleTopics == null) {
			return null;
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Collection<? extends Topic>, Map<Comparator<Topic>, List<Topic>>> cachedCompared = cachedComparedHierarchyMultipleTopics.get(param);
		if (cachedCompared == null) {
			return null;
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedCompared.get(types);
		if (cached == null) {
			return null;
		}
		/*
		 * get compared list
		 */
		return cached.get(comparator);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @see #read(Param, Collection, boolean, Comparator)
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param types
	 *            the topic types
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final void store(Param param, Collection<? extends Topic> types, boolean all, Comparator<Topic> comparator, List<Topic> values) {
		/*
		 * initialize cache for compared type hierarchy
		 */
		if (cachedComparedHierarchyMultipleTopics == null) {
			cachedComparedHierarchyMultipleTopics = HashUtil.getWeakHashMap();
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		Map<Collection<? extends Topic>, Map<Comparator<Topic>, List<Topic>>> cachedCompared = cachedComparedHierarchyMultipleTopics.get(param);
		if (cachedCompared == null) {
			/*
			 * create specific hierarchy mappings and store it
			 */
			cachedCompared = HashUtil.getWeakHashMap();
			cachedComparedHierarchyMultipleTopics.put(param, cachedCompared);
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedCompared.get(types);
		if (cached == null) {
			/*
			 * create mapping between comparator and compared-lists and store it
			 */
			cached = HashUtil.getWeakHashMap();
			cachedCompared.put(types, cached);
		}
		/*
		 * store compared list
		 */
		cached.put(comparator, values);
	}

	/**
	 * Internal method to read the all type-hierarchy topics of the given type
	 * from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param) {
		/*
		 * check main cache for type hierarchy
		 */
		if (cachedFullHierarchy == null) {
			return null;
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		return cachedFullHierarchy.get(param);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param values
	 *            the values to store
	 */
	protected final void store(Param param, List<Topic> values) {
		/*
		 * initialize cache for type hierarchy
		 */
		if (cachedFullHierarchy == null) {
			cachedFullHierarchy = HashUtil.getWeakHashMap();
		}
		/*
		 * store specific hierarchy mappings (sub or super type)
		 */
		cachedFullHierarchy.put(param, values);
	}

	/**
	 * Internal method to read the all type-hierarchy topics of the given type
	 * from cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the direct type-hierarchy topics of the given type from cache or
	 *         <code>null</code> if key is unknown
	 */
	private final List<Topic> read(Param param, Comparator<Topic> comparator) {
		/*
		 * check main cache for compared type hierarchy
		 */
		if (cachedComparedFullHierarchy == null) {
			return null;
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedComparedFullHierarchy.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get compared list
		 */
		return cached.get(comparator);
	}

	/**
	 * Add the given values to the internal cache.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final void store(Param param, Comparator<Topic> comparator, List<Topic> values) {
		/*
		 * initialize cache for compared type hierarchy
		 */
		if (cachedComparedFullHierarchy == null) {
			cachedComparedFullHierarchy = HashUtil.getWeakHashMap();
		}
		/*
		 * get mapping between comparator and compared-lists
		 */
		Map<Comparator<Topic>, List<Topic>> cached = cachedComparedFullHierarchy.get(param);
		if (cached == null) {
			/*
			 * create mapping between comparator and compared-lists and store it
			 */
			cached = HashUtil.getWeakHashMap();
			cachedComparedFullHierarchy.put(param, cached);
		}
		/*
		 * store compared list
		 */
		cached.put(comparator, values);
	}

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes within the given range
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes());
		store(Param.SUPERTYPE, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes within the given range
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes());
		Collections.sort(cache, comparator);
		store(Param.SUPERTYPE, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(type));
		store(Param.SUPERTYPE, type, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(type));
		Collections.sort(cache, comparator);
		store(Param.SUPERTYPE, type, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSupertypes(type));
		store(Param.DIRECT_SUPERTYPE, type, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSupertypes(type));
		Collections.sort(cache, comparator);
		store(Param.DIRECT_SUPERTYPE, type, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(types, all));
		store(all ? Param.SUPERTYPE_ALL : Param.SUPERTYPE, types, all, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(types, all));
		Collections.sort(cache, comparator);
		store(all ? Param.SUPERTYPE_ALL : Param.SUPERTYPE, types, all, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes within the given range
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes());
		store(Param.SUBTYPE, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all subtypes within the given range
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes());
		Collections.sort(cache, comparator);
		store(Param.SUBTYPE, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(type));
		store(Param.SUBTYPE, type, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(type));
		Collections.sort(cache, comparator);
		store(Param.SUBTYPE, type, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSubtypes(type));
		store(Param.DIRECT_SUBTYPE, type, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSubtypes(type));
		Collections.sort(cache, comparator);
		store(Param.DIRECT_SUBTYPE, type, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(types, all));
		store(all ? Param.SUBTYPE_ALL : Param.SUBTYPE, types, all, cache);
		return secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all subtypes of at least one of the given type
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(types, all));
		Collections.sort(cache, comparator);
		store(all ? Param.SUBTYPE_ALL : Param.SUBTYPE, types, all, comparator, cache);
		return secureSubList(cache, offset, limit);
	}

}
