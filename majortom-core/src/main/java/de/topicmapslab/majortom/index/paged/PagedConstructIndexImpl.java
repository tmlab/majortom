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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedConstructIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedConstructIndexImpl<T extends ITopicMapStore> extends IndexImpl<T> implements IPagedConstructIndex, ITopicMapListener {

	/**
	 * enumeration of map keys
	 */
	public enum Key {
		TYPES,

		SUPERTYPE,

		NAMES,

		OCCURRENCES,

		VARIANTS,

		ROLES,

		ASSOCIATIONS_PLAYED,

		ROLES_PLAYED
	}

	private Map<Key, Map<Construct, Long>> cachedNumbersOfChildren;
	private Map<Key, Map<Construct, List<? extends Construct>>> cachedConstructs;
	private Map<Key, Map<Construct, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructs;

	/**
	 * @param store
	 */
	public PagedConstructIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Key.ASSOCIATIONS_PLAYED, topic);
		if (cache == null) {
			return doGetAssociationsPlayed(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Key.ASSOCIATIONS_PLAYED, topic, comparator);
		if (cache == null) {
			return doGetAssociationsPlayed(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociationsPlayed(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.ASSOCIATIONS_PLAYED, topic);
		if (noc == null) {
			return doGetNumberOfAssociationsPlayed(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Key.NAMES, topic);
		if (cache == null) {
			return doGetNames(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic topic, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Key.NAMES, topic, comparator);
		if (cache == null) {
			return doGetNames(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.NAMES, topic);
		if (noc == null) {
			return doGetNumberOfNames(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Key.OCCURRENCES, topic);
		if (cache == null) {
			return doGetOccurrences(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Key.OCCURRENCES, topic, comparator);
		if (cache == null) {
			return doGetOccurrences(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.OCCURRENCES, topic);
		if (noc == null) {
			return doGetNumberOfOccurrences(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Association association, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> cache = read(Key.ROLES, association);
		if (cache == null) {
			return doGetRoles(association, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Association association, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> cache = read(Key.ROLES, association, comparator);
		if (cache == null) {
			return doGetRoles(association, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoles(Association association) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.ROLES, association);
		if (noc == null) {
			return doGetNumberOfRoles(association);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> cache = read(Key.ROLES_PLAYED, topic);
		if (cache == null) {
			return doGetRolesPlayed(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> cache = read(Key.ROLES_PLAYED, topic, comparator);
		if (cache == null) {
			return doGetRolesPlayed(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRolesPlayed(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.ROLES_PLAYED, topic);
		if (noc == null) {
			return doGetNumberOfRolesPlayed(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Key.SUPERTYPE, topic);
		if (cache == null) {
			return doGetSupertypes(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Key.SUPERTYPE, topic, comparator);
		if (cache == null) {
			return doGetSupertypes(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfSupertypes(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.SUPERTYPE, topic);
		if (noc == null) {
			return doGetNumberOfSupertypes(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Key.TYPES, topic);
		if (cache == null) {
			return doGetTypes(topic, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Key.TYPES, topic, comparator);
		if (cache == null) {
			return doGetTypes(topic, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTypes(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.TYPES, topic);
		if (noc == null) {
			return doGetNumberOfTypes(topic);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Name name, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Key.VARIANTS, name);
		if (cache == null) {
			return doGetVariants(name, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Name name, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Key.VARIANTS, name, comparator);
		if (cache == null) {
			return doGetVariants(name, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(Name name) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Long noc = readNumberOfChildren(Key.VARIANTS, name);
		if (noc == null) {
			return doGetNumberOfVariants(name);
		}
		return noc;
	}

	/**
	 * Internal method to read the stored number of children from cache.
	 * 
	 * @param key
	 *            the children type
	 * @param construct
	 *            the parent construct
	 * @return the number of children or <code>null</code>
	 */
	private Long readNumberOfChildren(Key key, Construct construct) {
		/*
		 * check main cache
		 */
		if (cachedNumbersOfChildren == null) {
			return null;
		}
		/*
		 * get mapping of parent construct
		 */
		Map<Construct, Long> map = cachedNumbersOfChildren.get(key);
		if (map == null) {
			return null;
		}
		/*
		 * return number of children
		 */
		return map.get(construct);
	}

	/**
	 * Internal method to add the number of children to internal cache
	 * 
	 * @param key
	 *            the children type
	 * @param construct
	 *            the parent construct
	 * @param numberOfChildren
	 *            the number of children
	 */
	protected final void storeNumberOfChildren(Key key, Construct construct, Long numberOfChildren) {
		/*
		 * check main cache
		 */
		if (cachedNumbersOfChildren == null) {
			cachedNumbersOfChildren = HashUtil.getHashMap();
		}
		/*
		 * get mapping of parent construct
		 */
		Map<Construct, Long> map = cachedNumbersOfChildren.get(key);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedNumbersOfChildren.put(key, map);
		}
		/*
		 * store number of children
		 */
		map.put(construct, numberOfChildren);
	}

	/**
	 * Internal method to read dependent constructs of the given parent
	 * construct from cache.
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * @param key
	 *            the key of dependent constructs
	 * @param parent
	 *            the parent construct
	 * @return dependent constructs of the given parent construct or
	 *         <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Key key, Construct parent) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		/*
		 * get mapping of cached constructs
		 */
		Map<Construct, List<? extends Construct>> map = cachedConstructs.get(key);
		if (map == null) {
			return null;
		}
		/*
		 * get constructs
		 */
		return (List<X>) map.get(parent);
	}

	/**
	 * Internal method to add dependent constructs of the given parent construct
	 * to cache.
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * @param key
	 *            the key of dependent constructs
	 * @param parent
	 *            the parent construct
	 * @param values
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Key key, Construct parent, List<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getHashMap();
		}
		/*
		 * get mapping of cached constructs
		 */
		Map<Construct, List<? extends Construct>> map = cachedConstructs.get(key);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructs.put(key, map);
		}
		/*
		 * store constructs
		 */
		map.put(parent, values);
	}

	/**
	 * Internal method to read dependent constructs of the given parent
	 * construct from cache.
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * @param key
	 *            the key of dependent constructs
	 * @param parent
	 *            the parent construct
	 * @param comparator
	 *            the comparators
	 * @return dependent constructs of the given parent construct or
	 *         <code>null</code> if key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Key key, Construct parent, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructs == null) {
			return null;
		}
		/*
		 * get mapping of parent to cached compared constructs by key
		 */
		Map<Construct, Map<Comparator<? extends Construct>, List<? extends Construct>>> compared = cachedComparedConstructs.get(key);
		if (compared == null) {
			return null;
		}
		/*
		 * get mapping of cached compared constructs by parent
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = compared.get(parent);
		if (map == null) {
			return null;
		}
		/*
		 * get constructs by comparator
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to add dependent constructs of the given parent construct
	 * to internal cache
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * @param key
	 *            the key of dependent constructs
	 * @param parent
	 *            the parent construct
	 * @param comparator
	 *            the comparators
	 * @param values
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Key key, Construct parent, Comparator<X> comparator, List<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructs == null) {
			cachedComparedConstructs = HashUtil.getHashMap();
		}
		/*
		 * get mapping of parent to cached compared constructs by key
		 */
		Map<Construct, Map<Comparator<? extends Construct>, List<? extends Construct>>> compared = cachedComparedConstructs.get(key);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedConstructs.put(key, compared);
		}
		/*
		 * get mapping of cached compared constructs by parent
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = compared.get(parent);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			compared.put(parent, map);
		}
		/*
		 * store constructs by comparator
		 */
		map.put(comparator, values);
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
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		getStore().addTopicMapListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * construct was removed
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED) {
			clearVariantCache();
		} else if (event == TopicMapEventType.NAME_REMOVED) {
			clearNameCache();
		} else if (event == TopicMapEventType.OCCURRENCE_REMOVED) {
			clearOccurrenceCache();
		} else if (event == TopicMapEventType.TOPIC_REMOVED) {
			clearTopicCache();
		} else if (event == TopicMapEventType.ASSOCIATION_REMOVED) {
			clearAssociationCache();
		} else if (event == TopicMapEventType.ROLE_REMOVED) {
			clearRoleCache();
		}

		/*
		 * variant added
		 */
		else if (event == TopicMapEventType.VARIANT_ADDED) {
			clearVariantCache();
		}
		/*
		 * name added
		 */
		else if (event == TopicMapEventType.NAME_ADDED) {
			clearNameCache();
		}
		/*
		 * occurrence added
		 */
		else if (event == TopicMapEventType.OCCURRENCE_ADDED) {
			clearOccurrenceCache();
		}
		/*
		 * topic role added
		 */
		else if (event == TopicMapEventType.ROLE_ADDED) {
			clearRoleCache();
		}
		/*
		 * type changed
		 */
		else if (event == TopicMapEventType.TYPE_ADDED || event == TopicMapEventType.TYPE_REMOVED) {
			clearTypesCache();
		}
		/*
		 * supertype changed
		 */
		else if (event == TopicMapEventType.SUPERTYPE_ADDED || event == TopicMapEventType.SUPERTYPE_REMOVED) {
			clearSupertypesCache();
		}
		/*
		 * player modified
		 */
		else if (event == TopicMapEventType.PLAYER_MODIFIED) {
			clearAssociationCache();
		}
	}

	/**
	 * Internal method to clear the cache
	 */
	private final void clearCache() {
		if (cachedConstructs != null) {
			cachedConstructs.clear();
			cachedConstructs = null;
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.clear();
			cachedComparedConstructs = null;
		}
	}

	/**
	 * Internal method to clear the cache depends on names
	 */
	private final void clearNameCache() {
		clearVariantCache();
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.NAMES);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.NAMES);
		}
	}

	/**
	 * Internal method to clear the cache depends on occurrences
	 */
	private final void clearOccurrenceCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.OCCURRENCES);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.OCCURRENCES);
		}
	}

	/**
	 * Internal method to clear the cache depends on topic types
	 */
	private final void clearTopicCache() {
		clearTypesCache();
		clearSupertypesCache();
		clearAssociationCache();
		clearNameCache();
		clearOccurrenceCache();
	}

	/**
	 * Internal method to clear the cache depends on topic types
	 */
	private final void clearTypesCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.TYPES);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.TYPES);
		}
	}

	/**
	 * Internal method to clear the cache depends on topic supertypes
	 */
	private final void clearSupertypesCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.SUPERTYPE);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.SUPERTYPE);
		}
	}

	/**
	 * Internal method to clear the cache depends on variants
	 */
	private final void clearVariantCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.VARIANTS);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.VARIANTS);
		}
	}

	/**
	 * Internal method to clear the cache depends on roles
	 */
	private final void clearRoleCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.ROLES);
			cachedConstructs.remove(Key.ROLES_PLAYED);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.ROLES_PLAYED);
		}
	}

	/**
	 * Internal method to clear the cache depends on assocaition
	 */
	private final void clearAssociationCache() {
		clearRoleCache();
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.ASSOCIATIONS_PLAYED);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.ASSOCIATIONS_PLAYED);
		}
	}

	/**
	 * Clears the indexes in context to the given list, to avoid indexes out of
	 * range.
	 * 
	 * @param list
	 *            the list
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return an two-
	 */
	protected final <E> List<E> secureSubList(List<E> list, int offset, int limit) {
		int from = offset;
		if (from < 0 || list.isEmpty()) {
			from = 0;
		} else if (from >= list.size()) {
			from = list.size() - 1;
		}
		int to = offset + limit;
		if (to < 0) {
			to = 0;
		} else if (to > list.size()) {
			to = list.size();
		}
		return Collections.unmodifiableList(list.subList(from, to));
	}

	/**
	 * Returns all types of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all types of the given topic as a list within the given range.
	 */
	protected List<Topic> doGetTypes(Topic topic, int offset, int limit) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getTypes());
		store(Key.TYPES, topic, list);
		return secureSubList(list, offset, limit);
	}


	/**
	 * Returns all types of the given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the given topic as a sorted list within the given
	 *         range.
	 */
	protected List<Topic> doGetTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getTypes());
		Collections.sort(list, comparator);
		store(Key.TYPES, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of types of the topic
	 * 
	 * @param topic
	 *            the topic whose number of types should be returned
	 * @return the number of types
	 */
	protected long doGetNumberOfTypes(Topic topic) {
		long noc = ((ITopic) topic).getTypes().size();
		storeNumberOfChildren(Key.TYPES, topic, noc);
		return noc;
	}

	/**
	 * Returns all supetypes of the given topic as a list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all supetypes of the given topic as a list within the given
	 *         range.
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getSupertypes());
		store(Key.SUPERTYPE, topic, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all supetypes of the given topic as a sorted list within the
	 * given range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all supetypes of the given topic as a sorted list within the
	 *         given range.
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getSupertypes());
		Collections.sort(list, comparator);
		store(Key.SUPERTYPE, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of supetypes of the topic
	 * 
	 * @param topic
	 *            the topic whose number of supertypes should be returned
	 * @return the number of supertypes
	 */
	protected long doGetNumberOfSupertypes(Topic topic) {
		long noc = ((ITopic) topic).getSupertypes().size();
		storeNumberOfChildren(Key.SUPERTYPE, topic, noc);
		return noc;
	}

	/**
	 * Returns all names of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all names of the given topic as a list within the given range.
	 */
	protected List<Name> doGetNames(Topic topic, int offset, int limit) {
		List<Name> list = HashUtil.getList(((ITopic) topic).getNames());
		store(Key.NAMES, topic, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all names of the given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all names of the given topic as a sorted list within the given
	 *         range.
	 */
	protected List<Name> doGetNames(Topic topic, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(((ITopic) topic).getNames());
		Collections.sort(list, comparator);
		store(Key.NAMES, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of names of the topic
	 * 
	 * @param topic
	 *            the topic whose number of names should be returned
	 * @return the number of names
	 */
	protected long doGetNumberOfNames(Topic topic) {
		long noc = ((ITopic) topic).getNames().size();
		storeNumberOfChildren(Key.NAMES, topic, noc);
		return noc;
	}

	/**
	 * Returns all occurrences of the given topic as a list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all occurrences of the given topic as a list within the given
	 *         range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(((ITopic) topic).getOccurrences());
		store(Key.OCCURRENCES, topic, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all occurrences of the given topic as a sorted list within the
	 * given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given topic as a sorted list within the
	 *         given range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(((ITopic) topic).getOccurrences());
		Collections.sort(list, comparator);
		store(Key.OCCURRENCES, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of occurrences of the topic
	 * 
	 * @param topic
	 *            the topic whose number of occurrences should be returned
	 * @return the number of occurrences
	 */
	protected long doGetNumberOfOccurrences(Topic topic) {
		long noc = ((ITopic) topic).getOccurrences().size();
		storeNumberOfChildren(Key.OCCURRENCES, topic, noc);
		return noc;
	}

	/**
	 * Returns all variants of the given name as a list within the given range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all variants of the given name as a list within the given range.
	 */
	protected List<Variant> doGetVariants(Name name, int offset, int limit) {
		List<Variant> list = HashUtil.getList(name.getVariants());
		store(Key.VARIANTS, name, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all variants of the given name as a sorted list within the given
	 * range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all variants of the given name as a sorted list within the given
	 *         range.
	 */
	protected List<Variant> doGetVariants(Name name, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(name.getVariants());
		Collections.sort(list, comparator);
		store(Key.VARIANTS, name, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of variants of the name
	 * 
	 * @param name
	 *            the name whose number of variants should be returned
	 * @return the number of variants
	 */
	protected long doGetNumberOfVariants(Name name) {
		long noc = name.getVariants().size();
		storeNumberOfChildren(Key.VARIANTS, name, noc);
		return noc;
	}

	/**
	 * Returns all roles of the given association as a list within the given
	 * range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles of the given association as a list within the given
	 *         range.
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit) {
		List<Role> list = HashUtil.getList(association.getRoles());
		store(Key.ROLES, association, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all roles of the given association as a sorted list within the
	 * given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given association as a sorted list within the
	 *         given range.
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(association.getRoles());
		Collections.sort(list, comparator);
		store(Key.ROLES, association, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of roles of the association
	 * 
	 * @param association
	 *            the association whose number of roles should be returned
	 * @return the number of roles
	 */
	protected long doGetNumberOfRoles(Association association) {
		long noc = association.getRoles().size();
		storeNumberOfChildren(Key.ROLES, association, noc);
		return noc;
	}

	/**
	 * Returns all associations played by given topic as a list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations played by given topic as a list within the given
	 *         range.
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit) {
		List<Association> list = HashUtil.getList(((ITopic) topic).getAssociationsPlayed());
		store(Key.ASSOCIATIONS_PLAYED, topic, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all associations played by given topic as a sorted list within
	 * the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations played by given topic as a sorted list within
	 *         the given range.
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(((ITopic) topic).getAssociationsPlayed());
		Collections.sort(list, comparator);
		store(Key.ASSOCIATIONS_PLAYED, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of played associations of the topic
	 * 
	 * @param topic
	 *            the topic whose number of played associations should be
	 *            returned
	 * @return the number of played associations
	 */
	protected long doGetNumberOfAssociationsPlayed(Topic topic) {
		long noc = ((ITopic) topic).getAssociationsPlayed().size();
		storeNumberOfChildren(Key.ASSOCIATIONS_PLAYED, topic, noc);
		return noc;
	}

	/**
	 * Returns all roles played by given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles played by given topic as a list within the given range.
	 */
	protected List<Role> doGetRolesPlayed(Topic topic, int offset, int limit) {
		List<Role> list = HashUtil.getList(topic.getRolesPlayed());
		store(Key.ROLES_PLAYED, topic, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all roles played by given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles played by given topic as a sorted list within the given
	 *         range.
	 */
	protected List<Role> doGetRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(topic.getRolesPlayed());
		Collections.sort(list, comparator);
		store(Key.ROLES_PLAYED, topic, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of played roles of the topic
	 * 
	 * @param topic
	 *            the topic whose number of played roles should be returned
	 * @return the number of played roles
	 */
	protected long doGetNumberOfRolesPlayed(Topic topic) {
		long noc = topic.getRolesPlayed().size();
		storeNumberOfChildren(Key.ROLES_PLAYED, topic, noc);
		return noc;
	}
	
	
}
