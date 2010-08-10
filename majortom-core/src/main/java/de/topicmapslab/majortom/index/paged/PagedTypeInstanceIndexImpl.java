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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the in-memory {@link IPagedTypeInstanceIndex} supporting
 * paging
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedTypeInstanceIndexImpl<E extends ITopicMapStore> extends PagedIndexImpl<E, ITypeInstanceIndex> implements IPagedTypeInstanceIndex {

	public enum Key {

		ASSOCIATION,

		CHARACTERISTICS,

		NAME,

		OCCURRENCE,

		ROLE,

		TOPIC,

		TOPIC_MATCHING_ALL
	}

	/**
	 * internal cache for type paging without comparator
	 */
	private Map<Key, List<Topic>> cachedTypes;
	/**
	 * internal cache for type paging with comparator
	 */
	private Map<Key, Map<Comparator<Topic>, List<Topic>>> cachedComparedTypes;

	/**
	 * internal cache method for type-construct mappings
	 */
	private Map<Key, Map<Topic, List<? extends Construct>>> cachedConstructs;

	/**
	 * internal cache method for type-construct mappings
	 */
	private Map<Key, Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructs;

	/**
	 * internal cache method for types-construct mappings
	 */
	private Map<Key, Map<Collection<? extends Topic>, List<? extends Construct>>> cachedConstructsMultipleTypes;

	/**
	 * internal cache method for types-construct mappings
	 */
	private Map<Key, Map<Collection<? extends Topic>, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsMultipleTypes;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 * @param parentIndex
	 *            the parent {@link ITypeInstanceIndex}
	 */
	public PagedTypeInstanceIndexImpl(E store, ITypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.ASSOCIATION);
		if (list == null) {
			return doGetAssociationTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.ASSOCIATION, comparator);
		if (list == null) {
			return doGetAssociationTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> list = read(Key.ASSOCIATION, type);
		if (list == null) {
			return doGetAssociations(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> list = read(Key.ASSOCIATION, type, comparator);
		if (list == null) {
			return doGetAssociations(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> list = read(Key.ASSOCIATION, types);
		if (list == null) {
			return doGetAssociations(types, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> list = read(Key.ASSOCIATION, types, comparator);
		if (list == null) {
			return doGetAssociations(types, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.CHARACTERISTICS);
		if (list == null) {
			return doGetCharacteristicTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.CHARACTERISTICS, comparator);
		if (list == null) {
			return doGetCharacteristicTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> list = read(Key.CHARACTERISTICS, type);
		if (list == null) {
			return doGetCharacteristics(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> list = read(Key.CHARACTERISTICS, type, comparator);
		if (list == null) {
			return doGetCharacteristics(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> list = read(Key.CHARACTERISTICS, types);
		if (list == null) {
			return doGetCharacteristics(types, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> list = read(Key.CHARACTERISTICS, types, comparator);
		if (list == null) {
			return doGetCharacteristics(types, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.NAME);
		if (list == null) {
			return doGetNameTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.NAME, comparator);
		if (list == null) {
			return doGetNameTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> list = read(Key.NAME, type);
		if (list == null) {
			return doGetNames(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic type, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> list = read(Key.NAME, type, comparator);
		if (list == null) {
			return doGetNames(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> list = read(Key.NAME, types);
		if (list == null) {
			return doGetNames(types, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> list = read(Key.NAME, types, comparator);
		if (list == null) {
			return doGetNames(types, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.OCCURRENCE);
		if (list == null) {
			return doGetOccurrenceTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.OCCURRENCE, comparator);
		if (list == null) {
			return doGetOccurrenceTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> list = read(Key.OCCURRENCE, type);
		if (list == null) {
			return doGetOccurrences(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> list = read(Key.OCCURRENCE, type, comparator);
		if (list == null) {
			return doGetOccurrences(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> list = read(Key.OCCURRENCE, types);
		if (list == null) {
			return doGetOccurrences(types, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> list = read(Key.OCCURRENCE, types, comparator);
		if (list == null) {
			return doGetOccurrences(types, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.ROLE);
		if (list == null) {
			return doGetRoleTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.ROLE, comparator);
		if (list == null) {
			return doGetRoleTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> list = read(Key.ROLE, type);
		if (list == null) {
			return doGetRoles(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Topic type, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> list = read(Key.ROLE, type, comparator);
		if (list == null) {
			return doGetRoles(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> list = read(Key.ROLE, types);
		if (list == null) {
			return doGetRoles(types, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Role> list = read(Key.ROLE, types, comparator);
		if (list == null) {
			return doGetRoles(types, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.TOPIC);
		if (list == null) {
			return doGetTopicTypes(offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.TOPIC, comparator);
		if (list == null) {
			return doGetTopicTypes(offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.TOPIC, type);
		if (list == null) {
			return doGetTopics(type, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(Key.TOPIC, type, comparator);
		if (list == null) {
			return doGetTopics(type, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopics(types, false, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopics(types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(all ? Key.TOPIC_MATCHING_ALL : Key.TOPIC, types);
		if (list == null) {
			return doGetTopics(types, all, offset, limit);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> list = read(all ? Key.TOPIC_MATCHING_ALL : Key.TOPIC, types, comparator);
		if (list == null) {
			return doGetTopics(types, all, offset, limit, comparator);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearTopicDependentCache();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		Object dependingObject = null;
		/*
		 * switch by event
		 */
		switch (event) {
		case ASSOCIATION_REMOVED:
		case NAME_REMOVED:
		case OCCURRENCE_REMOVED:
		case VARIANT_REMOVED:
		case ROLE_REMOVED:
		case TOPIC_REMOVED: {
			/*
			 * change depends on the old value
			 */
			dependingObject = oldValue;
		}
			break;
		case ASSOCIATION_ADDED: {
			clearAssociationDependentCache();
			return;
		}
		case TOPIC_ADDED: {
			clearTopicDependentCache();
			return;
		}
		case NAME_ADDED: {
			clearNameDependentCache();
			return;
		}
		case OCCURRENCE_ADDED: {
			clearOccurrenceDependentCache();
			return;
		}
		case ROLE_ADDED: {
			clearRoleDependentCache();
			return;
		}
		default: {
			/*
			 * change depends on the notifier
			 */
			dependingObject = notifier;
		}
		}

		/*
		 * switch by depending object
		 */
		if (dependingObject instanceof Topic) {
			clearTopicDependentCache();
		} else if (dependingObject instanceof Association) {
			clearAssociationDependentCache();
		} else if (dependingObject instanceof Role) {
			clearRoleDependentCache();
		} else if (dependingObject instanceof Name) {
			clearNameDependentCache();
		} else if (dependingObject instanceof Occurrence) {
			clearOccurrenceDependentCache();
		}
	}

	/**
	 * Method clears all caches
	 */
	private final void clearTopicDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.clear();
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.clear();
		}
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedConstructsMultipleTypes != null) {
			cachedConstructsMultipleTypes.clear();
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.clear();
		}
		if (cachedComparedConstructsMultipleTypes != null) {
			cachedComparedConstructsMultipleTypes.clear();
		}
	}

	/**
	 * Method clears all caches depending on an association, its types or its
	 * roles
	 */
	private final void clearAssociationDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(Key.ASSOCIATION);
			cachedTypes.remove(Key.ROLE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(Key.ASSOCIATION);
			cachedTypes.remove(Key.ROLE);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.ASSOCIATION);
			cachedComparedConstructs.remove(Key.ROLE);
		}
		if (cachedConstructsMultipleTypes != null) {
			cachedConstructsMultipleTypes.remove(Key.ASSOCIATION);
			cachedConstructsMultipleTypes.remove(Key.ROLE);
		}
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.ASSOCIATION);
			cachedConstructs.remove(Key.ROLE);
		}
		if (cachedComparedConstructsMultipleTypes != null) {
			cachedComparedConstructsMultipleTypes.remove(Key.ASSOCIATION);
			cachedComparedConstructsMultipleTypes.remove(Key.ROLE);
		}
	}

	/**
	 * Method clears all caches depending on a name or its types
	 */
	private final void clearNameDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(Key.NAME);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(Key.NAME);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.NAME);
		}
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.NAME);
		}
		if (cachedConstructsMultipleTypes != null) {
			cachedConstructsMultipleTypes.remove(Key.NAME);
		}
		if (cachedComparedConstructsMultipleTypes != null) {
			cachedComparedConstructsMultipleTypes.remove(Key.NAME);
		}
	}

	/**
	 * Method clears all caches depending on an occurrence or its types
	 */
	private final void clearOccurrenceDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(Key.OCCURRENCE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(Key.OCCURRENCE);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.OCCURRENCE);
		}
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.OCCURRENCE);
		}
		if (cachedConstructsMultipleTypes != null) {
			cachedConstructsMultipleTypes.remove(Key.OCCURRENCE);
		}
		if (cachedComparedConstructsMultipleTypes != null) {
			cachedComparedConstructsMultipleTypes.remove(Key.OCCURRENCE);
		}
	}

	/**
	 * Method clears all caches depending on a role or its types
	 */
	private final void clearRoleDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(Key.ROLE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(Key.ROLE);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Key.ROLE);
		}
		if (cachedConstructs != null) {
			cachedConstructs.remove(Key.ROLE);
		}
		if (cachedConstructsMultipleTypes != null) {
			cachedConstructsMultipleTypes.remove(Key.ROLE);
		}
		if (cachedComparedConstructsMultipleTypes != null) {
			cachedComparedConstructsMultipleTypes.remove(Key.ROLE);
		}
	}

	/**
	 * Method reads the internal types if they are still contained.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @return the types or <code>null</code> if the key-pairs is unknown
	 */
	private List<Topic> read(Key type) {
		if (cachedTypes == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return cachedTypes.get(type);
	}

	/**
	 * Method store the types to the internal cache.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @param values
	 *            the values to store
	 */
	protected void store(Key type, List<Topic> values) {
		if (cachedTypes == null) {
			cachedTypes = HashUtil.getWeakHashMap();
		}
		/*
		 * store cached types
		 */
		cachedTypes.put(type, values);
	}

	/**
	 * Method reads the types from cache.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @param comparator
	 *            the comparator
	 * @return types or <code>null</code> if the key-pair is unknown
	 */
	private List<Topic> read(Key type, Comparator<Topic> comparator) {
		if (cachedComparedTypes == null) {
			return null;
		}
		/*
		 * get cached map comparator-types
		 */
		Map<Comparator<Topic>, List<Topic>> comparedTypes = cachedComparedTypes.get(type);
		if (comparedTypes == null) {
			return null;
		}
		/*
		 * get types mapped to the comparator
		 */
		return comparedTypes.get(comparator);
	}

	/**
	 * Method store the given values.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected void store(Key type, Comparator<Topic> comparator, List<Topic> values) {
		if (cachedComparedTypes == null) {
			cachedComparedTypes = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached map comparator-types
		 */
		Map<Comparator<Topic>, List<Topic>> comparedTypes = cachedComparedTypes.get(type);
		if (comparedTypes == null) {
			/*
			 * create map and store to parent map
			 */
			comparedTypes = HashUtil.getWeakHashMap();
			cachedComparedTypes.put(type, comparedTypes);
		}
		/*
		 * store types mapped to the comparator
		 */
		comparedTypes.put(comparator, values);
	}

	/**
	 * Method reads the constructs by their type from internal cache
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param type
	 *            the topic type
	 * @return the types or <code>null</code> if the key-pairs is unknown
	 */
	@SuppressWarnings("unchecked")
	private <T extends Construct> List<T> read(Key key, Topic type) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}

		/*
		 * get type-construct mappings
		 */
		Map<Topic, List<? extends Construct>> map = cachedConstructs.get(key);
		if (map == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return (List<T>) map.get(type);
	}

	/**
	 * Method store the types to the internal cache.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param type
	 *            the topic type
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void store(Key key, Topic type, List<T> values) {
		/*
		 * initialize main cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getHashMap();
		}
		/*
		 * get type-construct mappings
		 */
		Map<Topic, List<? extends Construct>> map = cachedConstructs.get(key);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructs.put(key, map);
		}
		/*
		 * store cached types
		 */
		map.put(type, values);
	}

	/**
	 * Method reads the constructs from cache.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param type
	 *            the topic type
	 * @param comparator
	 *            the comparator
	 * @return constructs or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private <T extends Construct> List<T> read(Key key, Topic type, Comparator<T> comparator) {
		/*
		 * check main main cache
		 */
		if (cachedComparedConstructs == null) {
			return null;
		}
		/*
		 * get type-construct mappings
		 */
		Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>> comparedMap = cachedComparedConstructs.get(key);
		if (comparedMap == null) {
			return null;
		}
		/*
		 * get comparator-construct mappings
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = comparedMap.get(type);
		if (map == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return (List<T>) map.get(comparator);
	}

	/**
	 * Method store the given values.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param type
	 *            the topic type
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void store(Key key, Topic type, Comparator<T> comparator, List<T> values) {
		/*
		 * initialize main cache
		 */
		if (cachedComparedConstructs == null) {
			cachedComparedConstructs = HashUtil.getHashMap();
		}
		/*
		 * get type-construct mappings
		 */
		Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>> comparedMap = cachedComparedConstructs.get(key);
		if (comparedMap == null) {
			comparedMap = HashUtil.getWeakHashMap();
			cachedComparedConstructs.put(key, comparedMap);
		}
		/*
		 * get comparator-construct mappings
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = comparedMap.get(type);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			comparedMap.put(type, map);
		}
		/*
		 * store cached types
		 */
		map.put(comparator, values);
	}

	/**
	 * Method reads the constructs by their types from internal cache
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param types
	 *            the topic types
	 * @return the constructs or <code>null</code> if the key-pairs is unknown
	 */
	@SuppressWarnings("unchecked")
	private <T extends Construct> List<T> read(Key key, Collection<? extends Topic> types) {
		/*
		 * check main cache
		 */
		if (cachedConstructsMultipleTypes == null) {
			return null;
		}

		/*
		 * get type-construct mappings
		 */
		Map<Collection<? extends Topic>, List<? extends Construct>> map = cachedConstructsMultipleTypes.get(key);
		if (map == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return (List<T>) map.get(types);
	}

	/**
	 * Method store the constructs to the internal cache.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param types
	 *            the topic types
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void store(Key key, Collection<? extends Topic> types, List<T> values) {
		/*
		 * initialize main cache
		 */
		if (cachedConstructsMultipleTypes == null) {
			cachedConstructsMultipleTypes = HashUtil.getHashMap();
		}
		/*
		 * get type-construct mappings
		 */
		Map<Collection<? extends Topic>, List<? extends Construct>> map = cachedConstructsMultipleTypes.get(key);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsMultipleTypes.put(key, map);
		}
		/*
		 * store cached types
		 */
		map.put(types, values);
	}

	/**
	 * Method reads constructs by their types from cache.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param types
	 *            the topic types
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private <T extends Construct> List<T> read(Key key, Collection<? extends Topic> types, Comparator<T> comparator) {
		/*
		 * check main main cache
		 */
		if (cachedComparedConstructsMultipleTypes == null) {
			return null;
		}
		/*
		 * get type-construct mappings
		 */
		Map<Collection<? extends Topic>, Map<Comparator<? extends Construct>, List<? extends Construct>>> comparedMap = cachedComparedConstructsMultipleTypes
				.get(key);
		if (comparedMap == null) {
			return null;
		}
		/*
		 * get comparator-construct mappings
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = comparedMap.get(types);
		if (map == null) {
			return null;
		}
		/*
		 * get cached types
		 */
		return (List<T>) map.get(comparator);
	}

	/**
	 * Method store the given values.
	 * 
	 * @param key
	 *            the argument indicates the type of constructs
	 * @param type
	 *            the topic type
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected <T extends Construct> void store(Key key, Collection<? extends Topic> types, Comparator<T> comparator, List<T> values) {
		/*
		 * initialize main cache
		 */
		if (cachedComparedConstructsMultipleTypes == null) {
			cachedComparedConstructsMultipleTypes = HashUtil.getHashMap();
		}
		/*
		 * get type-construct mappings
		 */
		Map<Collection<? extends Topic>, Map<Comparator<? extends Construct>, List<? extends Construct>>> comparedMap = cachedComparedConstructsMultipleTypes
				.get(key);
		if (comparedMap == null) {
			comparedMap = HashUtil.getWeakHashMap();
			cachedComparedConstructsMultipleTypes.put(key, comparedMap);
		}
		/*
		 * get comparator-construct mappings
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = comparedMap.get(types);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			comparedMap.put(types, map);
		}
		/*
		 * store cached types
		 */
		map.put(comparator, values);
	}

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic types within the given range
	 */
	protected List<Topic> doGetTopicTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopicTypes());
		store(Key.TOPIC, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the topic types within the given range
	 */
	protected List<Topic> doGetTopicTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopicTypes());
		Collections.sort(list, comparator);
		store(Key.TOPIC, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all topic instances of the given topic type within the given
	 * range.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the topic within the given range
	 */
	protected List<Topic> doGetTopics(Topic type, int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(type));
		store(Key.TOPIC, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all topic instances of the given topic type within the given
	 * range.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic within the given range
	 */
	protected List<Topic> doGetTopics(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(type));
		Collections.sort(list, comparator);
		store(Key.TOPIC, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every
	 *            given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all instances typed by at least one or every of the
	 *         given types within the given range
	 */
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(types, all));
		store(all ? Key.TOPIC_MATCHING_ALL : Key.TOPIC, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every
	 *            given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all instances typed by at least one or every of the
	 *         given types within the given range
	 */
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(types, all));
		Collections.sort(list, comparator);
		store(all ? Key.TOPIC_MATCHING_ALL : Key.TOPIC, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the association types within the given range
	 */
	protected List<Topic> doGetAssociationTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getAssociationTypes());
		store(Key.ASSOCIATION, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the association types within the given range
	 */
	protected List<Topic> doGetAssociationTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getAssociationTypes());
		Collections.sort(list, comparator);
		store(Key.ASSOCIATION, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all associations of the given type within the given range
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations of the type within the given range
	 */
	protected List<Association> doGetAssociations(Topic type, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(type));
		store(Key.ASSOCIATION, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all associations of the given type within the given range
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations of the type within the given range
	 */
	protected List<Association> doGetAssociations(Topic type, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(type));
		Collections.sort(list, comparator);
		store(Key.ASSOCIATION, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items typed by one of the given types
	 *         within the given range
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(types));
		store(Key.ASSOCIATION, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association items typed by one of the given types
	 *         within the given range
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(types));
		Collections.sort(list, comparator);
		store(Key.ASSOCIATION, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all role types of the topic map within the given range.
	 */
	protected List<Topic> doGetRoleTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getRoleTypes());
		store(Key.ROLE, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all role types of the topic map within the given range.
	 */
	protected List<Topic> doGetRoleTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getRoleTypes());
		Collections.sort(list, comparator);
		store(Key.ROLE, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all roles of the given type within the given range.
	 * 
	 * @param type
	 *            the role type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given type within the given range
	 */
	protected List<Role> doGetRoles(Topic type, int offset, int limit) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(type));
		store(Key.ROLE, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all roles of the given type within the given range.
	 * 
	 * @param type
	 *            the role type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given type within the given range
	 */
	protected List<Role> doGetRoles(Topic type, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(type));
		Collections.sort(list, comparator);
		store(Key.ROLE, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association roles typed by one of the given types
	 *         within the given range
	 */
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(types));
		store(Key.ROLE, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association roles typed by one of the given types
	 *         within the given range
	 */
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(types));
		Collections.sort(list, comparator);
		store(Key.ROLE, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all types within the given range
	 */
	protected List<Topic> doGetCharacteristicTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getCharacteristicTypes());
		store(Key.CHARACTERISTICS, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all types within the given range
	 */
	protected List<Topic> doGetCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getCharacteristicTypes());
		Collections.sort(list, comparator);
		store(Key.CHARACTERISTICS, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics being typed by the given of topic type.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all characteristics typed by the given type within the
	 *         given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(type));
		store(Key.CHARACTERISTICS, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics being typed by the given of topic type.
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
	 * @return a list of all characteristics typed by the given type within the
	 *         given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(type));
		Collections.sort(list, comparator);
		store(Key.CHARACTERISTICS, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics typed by one of given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all characteristics typed by one of the given types
	 *         within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(types));
		store(Key.CHARACTERISTICS, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics typed by one of given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all characteristics typed by one of the given types
	 *         within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(types));
		Collections.sort(list, comparator);
		store(Key.CHARACTERISTICS, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all name types within the given range
	 */
	protected List<Topic> doGetNameTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getNameTypes());
		store(Key.NAME, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all name types within the given range
	 */
	protected List<Topic> doGetNameTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getNameTypes());
		Collections.sort(list, comparator);
		store(Key.NAME, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all names of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names of the given type within the given range.
	 */
	protected List<Name> doGetNames(Topic type, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(type));
		store(Key.NAME, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all names of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names of the given type within the given range.
	 */
	protected List<Name> doGetNames(Topic type, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(type));
		Collections.sort(list, comparator);
		store(Key.NAME, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all names typed by one of the given types within the
	 *         given range
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(types));
		store(Key.NAME, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all names typed by one of the given types within the
	 *         given range
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(types));
		Collections.sort(list, comparator);
		store(Key.NAME, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrence types within the given range
	 */
	protected List<Topic> doGetOccurrenceTypes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getOccurrenceTypes());
		store(Key.OCCURRENCE, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrence types within the given range
	 */
	protected List<Topic> doGetOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getOccurrenceTypes());
		Collections.sort(list, comparator);
		store(Key.OCCURRENCE, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences of the given type within the given range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic type, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(type));
		store(Key.OCCURRENCE, type, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given type within the given range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic type, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(type));
		Collections.sort(list, comparator);
		store(Key.OCCURRENCE, type, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all occurrences typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all occurrences typed by one of the given types within
	 *         the given range
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(types));
		store(Key.OCCURRENCE, types, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all occurrences typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all occurrences typed by one of the given types
	 *         within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(types));
		Collections.sort(list, comparator);
		store(Key.OCCURRENCE, types, comparator, list);
		return secureSubList(list, offset, limit);
	}

}
