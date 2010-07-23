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
package de.topicmapslab.majortom.inmemory.index.paged;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Construct;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedSupertypeSubtypeIndex extends InMemoryPagedIndex<ISupertypeSubtypeIndex> implements IPagedSupertypeSubtypeIndex {

	enum Param {

		SUPERTYPE_ALL,

		SUPERTYPE,

		DIRECT_SUPERTYPE_ALL,

		DIRECT_SUPERTYPE,

		SUBTYPE_ALL,

		SUBTYPE,

		DIRECT_SUBTYPE_ALL,

		DIRECT_SUBTYPE
	}

	private Map<TopicMapStoreParameterType, List<Topic>> cachedFullHierarchy;
	private Map<TopicMapStoreParameterType, Map<Comparator<Topic>, List<Topic>>> cachedComparedFullHierarchy;

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
	public InMemoryPagedSupertypeSubtypeIndex(InMemoryTopicMapStore store, ISupertypeSubtypeIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.DIRECT_SUBTYPE, "getDirectSubtypes", type, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.DIRECT_SUBTYPE, "getDirectSubtypes", type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.DIRECT_SUPERTYPE, "getDirectSupertypes", type, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.DIRECT_SUPERTYPE, "getDirectSupertypes", type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(TopicMapStoreParameterType.SUBTYPE, "getSubtypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(TopicMapStoreParameterType.SUBTYPE, "getSubtypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUBTYPE, "getSubtypes", type, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUBTYPE, "getSubtypes", type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUBTYPE, "getSubtypes", types, false, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}

		return getHierarchy(Param.SUBTYPE, "getSubtypes", types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (all) {
			return getHierarchy(Param.SUBTYPE_ALL, "getSubtypes", types, all, offset, limit);
		}
		return getHierarchy(Param.SUBTYPE, "getSubtypes", types, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (all) {
			return getHierarchy(Param.SUBTYPE_ALL, "getSubtypes", types, all, offset, limit, comparator);
		}
		return getHierarchy(Param.SUBTYPE, "getSubtypes", types, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(TopicMapStoreParameterType.SUPERTYPE, "getSupertypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(TopicMapStoreParameterType.SUPERTYPE, "getSupertypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUPERTYPE, "getSupertypes", type, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUPERTYPE, "getSupertypes", type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUPERTYPE, "getSupertypes", types, false, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getHierarchy(Param.SUPERTYPE, "getSupertypes", types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (all) {
			return getHierarchy(Param.SUPERTYPE_ALL, "getSupertypes", types, all, offset, limit);
		}
		return getHierarchy(Param.SUPERTYPE_ALL, "getSupertypes", types, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (all) {
			return getHierarchy(Param.SUPERTYPE_ALL, "getSupertypes", types, all, offset, limit, comparator);
		}
		return getHierarchy(Param.SUPERTYPE_ALL, "getSupertypes", types, all, offset, limit, comparator);
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
	 * type within the given range.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param type
	 *            the topic type
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(Param param, String methodName, Topic type, int offset, int limit) {
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
		 * get types list
		 */
		List<Topic> list = cached.get(type);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics and store it
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic.class);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex(), type));
				cached.put(type, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type within the given range.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param type
	 *            the topic type
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(Param param, String methodName, Topic type, int offset, int limit, Comparator<Topic> comparator) {
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
		 * get compared list
		 */
		List<Topic> list = cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic.class);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex(), type));
				/*
				 * sort list and store them
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type within the given range.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(Param param, String methodName, Collection<? extends Topic> types, boolean all, int offset, int limit) {
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
		 * get types list
		 */
		List<Topic> list = cached.get(types);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics and store it
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Collection.class, boolean.class);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex(), types, all));
				cached.put(types, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the direct type-hierarchy topics of the given
	 * type within the given range.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(Param param, String methodName, Collection<? extends Topic> types, boolean all, int offset, int limit,
			Comparator<Topic> comparator) {
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
		 * get compared list
		 */
		List<Topic> list = cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Collection.class, boolean.class);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex(), types, all));
				/*
				 * sort list and store them
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the all type-hierarchy topics of the given type
	 * within the given range.
	 * 
	 * @param param
	 *            the hierarchy type
	 * @param methodName
	 *            the name of the method to retrieve missing information
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(TopicMapStoreParameterType param, String methodName, int offset, int limit) {
		/*
		 * initialize cache for type hierarchy
		 */
		if (cachedFullHierarchy == null) {
			cachedFullHierarchy = HashUtil.getWeakHashMap();
		}
		/*
		 * get specific hierarchy mappings (sub or super type)
		 */
		List<Topic> list = cachedFullHierarchy.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics and store it
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex()));
				cachedFullHierarchy.put(param, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the all type-hierarchy topics of the given type
	 * within the given range.
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
	 * @return the direct type-hierarchy topics of the given type within the
	 *         given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getHierarchy(TopicMapStoreParameterType param, String methodName, int offset, int limit, Comparator<Topic> comparator) {
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
		 * get compared list
		 */
		List<Topic> list = cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get type-hierarchy topics
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex()));
				/*
				 * sort list and store them
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

}
