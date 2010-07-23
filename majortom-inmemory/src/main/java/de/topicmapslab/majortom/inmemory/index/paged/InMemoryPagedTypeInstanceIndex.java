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

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the in-memory {@link IPagedTypeInstanceIndex} supporting
 * paging
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedTypeInstanceIndex extends InMemoryPagedIndex<ITypeInstanceIndex> implements IPagedTypeInstanceIndex {

	/**
	 * internal cache for type paging without comparator
	 */
	private Map<TopicMapStoreParameterType, List<Topic>> cachedTypes;
	/**
	 * internal cache for type paging with comparator
	 */
	private Map<TopicMapStoreParameterType, Map<Comparator<Topic>, List<Topic>>> cachedComparedTypes;
	/**
	 * internal cache for type-association paging without comparator
	 */
	private Map<Topic, List<Association>> cachedAssociations;
	/**
	 * internal cache for type-association paging with comparator
	 */
	private Map<Topic, Map<Comparator<Association>, List<Association>>> cachedComparedAssociations;
	/**
	 * internal cache for type-characteristic paging without comparator
	 */
	private Map<Topic, List<ICharacteristics>> cachedCharacteristics;
	/**
	 * internal cache for type-characteristic paging with comparator
	 */
	private Map<Topic, Map<Comparator<ICharacteristics>, List<ICharacteristics>>> cachedComparedCharacteristics;
	/**
	 * internal cache for type-name paging without comparator
	 */
	private Map<Topic, List<Name>> cachedNames;
	/**
	 * internal cache for type-name paging with comparator
	 */
	private Map<Topic, Map<Comparator<Name>, List<Name>>> cachedComparedNames;
	/**
	 * internal cache for type-occurrence paging without comparator
	 */
	private Map<Topic, List<Occurrence>> cachedOccurrences;
	/**
	 * internal cache for type-occurrence paging with comparator
	 */
	private Map<Topic, Map<Comparator<Occurrence>, List<Occurrence>>> cachedComparedOccurrences;
	/**
	 * internal cache for type-role paging without comparator
	 */
	private Map<Topic, List<Role>> cachedRoles;
	/**
	 * internal cache for type-role paging with comparator
	 */
	private Map<Topic, Map<Comparator<Role>, List<Role>>> cachedComparedRoles;
	/**
	 * internal cache for type-topics paging without comparator
	 */
	private Map<Topic, List<Topic>> cachedTopics;
	/**
	 * internal cache for type-topics paging with comparator
	 */
	private Map<Topic, Map<Comparator<Topic>, List<Topic>>> cachedComparedTopics;
	/**
	 * internal cache for type-topics paging with multiple types (matching all)
	 * and without comparator
	 */
	private Map<Collection<Topic>, List<Topic>> cachedMatchingAllTopics;
	/**
	 * internal cache for type-topics paging with multiple types (matching at
	 * least one) and without comparator
	 */
	private Map<Collection<Topic>, List<Topic>> cachedMatchingTopics;
	/**
	 * internal cache for type-topics paging with multiple types (matching all)
	 * and with comparator
	 */
	private Map<Collection<Topic>, Map<Comparator<Topic>, List<Topic>>> cachedComparedMatchingAllTopics;
	/**
	 * internal cache for type-topics paging with multiple types (matching at
	 * least one) and with comparator
	 */
	private Map<Collection<Topic>, Map<Comparator<Topic>, List<Topic>>> cachedComparedMatchingTopics;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 * @param parentIndex
	 *            the parent {@link ITypeInstanceIndex}
	 */
	public InMemoryPagedTypeInstanceIndex(InMemoryTopicMapStore store, ITypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.ASSOCIATION, "getAssociationTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.ASSOCIATION, "getAssociationTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedAssociations == null) {
			cachedAssociations = HashUtil.getWeakHashMap();
		}
		List<Association> associations = cachedAssociations.get(type);
		if (associations == null) {
			associations = HashUtil.getList(getParentIndex().getAssociations(type));
			cachedAssociations.put(type, associations);
		}
		return secureSubList(associations, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedAssociations == null) {
			cachedComparedAssociations = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Association>, List<Association>> compared = cachedComparedAssociations.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedAssociations.put(type, compared);
		}
		List<Association> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getAssociations(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedAssociations == null) {
			cachedAssociations = HashUtil.getWeakHashMap();
		}
		List<Association> associations = HashUtil.getList();
		for (Topic type : types) {
			List<Association> list = cachedAssociations.get(type);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getAssociations(type));
				cachedAssociations.put(type, list);
			}
			associations.addAll(list);
		}
		return secureSubList(associations, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedAssociations == null) {
			cachedComparedAssociations = HashUtil.getWeakHashMap();
		}
		List<Association> result = HashUtil.getList();
		for (Topic type : types) {
			Map<Comparator<Association>, List<Association>> compared = cachedComparedAssociations.get(type);
			if (compared == null) {
				compared = HashUtil.getWeakHashMap();
				cachedComparedAssociations.put(type, compared);
			}
			List<Association> list = compared.get(comparator);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getAssociations(type));
				Collections.sort(list, comparator);
				compared.put(comparator, list);
			}
			result.addAll(list);
		}
		Collections.sort(result, comparator);
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.CHARACTERISTICS, "getCharacteristicTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.CHARACTERISTICS, "getCharacteristicTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedCharacteristics == null) {
			cachedCharacteristics = HashUtil.getWeakHashMap();
		}
		List<ICharacteristics> list = cachedCharacteristics.get(type);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getCharacteristics(type));
			cachedCharacteristics.put(type, list);
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
		if (cachedComparedCharacteristics == null) {
			cachedComparedCharacteristics = HashUtil.getWeakHashMap();
		}
		Map<Comparator<ICharacteristics>, List<ICharacteristics>> compared = cachedComparedCharacteristics.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedCharacteristics.put(type, compared);
		}
		List<ICharacteristics> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getCharacteristics(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedCharacteristics == null) {
			cachedCharacteristics = HashUtil.getWeakHashMap();
		}
		List<ICharacteristics> result = HashUtil.getList();
		for (Topic type : types) {
			List<ICharacteristics> list = cachedCharacteristics.get(type);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getCharacteristics(type));
				cachedCharacteristics.put(type, list);
			}
			result.addAll(list);
		}
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedCharacteristics == null) {
			cachedComparedCharacteristics = HashUtil.getWeakHashMap();
		}
		List<ICharacteristics> result = HashUtil.getList();
		for (Topic type : types) {
			Map<Comparator<ICharacteristics>, List<ICharacteristics>> compared = cachedComparedCharacteristics.get(type);
			if (compared == null) {
				compared = HashUtil.getWeakHashMap();
				cachedComparedCharacteristics.put(type, compared);
			}
			List<ICharacteristics> list = compared.get(comparator);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getCharacteristics(type));
				Collections.sort(list, comparator);
				compared.put(comparator, list);
			}
			result.addAll(list);
		}
		Collections.sort(result, comparator);
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.NAME, "getNameTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.NAME, "getNameTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedNames == null) {
			cachedNames = HashUtil.getWeakHashMap();
		}
		List<Name> list = cachedNames.get(type);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getNames(type));
			cachedNames.put(type, list);
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
		if (cachedComparedNames == null) {
			cachedComparedNames = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Name>, List<Name>> compared = cachedComparedNames.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedNames.put(type, compared);
		}
		List<Name> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getNames(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedNames == null) {
			cachedNames = HashUtil.getWeakHashMap();
		}
		List<Name> result = HashUtil.getList();
		for (Topic type : types) {
			List<Name> list = cachedNames.get(type);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getNames(type));
				cachedNames.put(type, list);
			}
			result.addAll(list);
		}
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedNames == null) {
			cachedComparedNames = HashUtil.getWeakHashMap();
		}
		List<Name> result = HashUtil.getList();
		for (Topic type : types) {
			Map<Comparator<Name>, List<Name>> compared = cachedComparedNames.get(type);
			if (compared == null) {
				compared = HashUtil.getWeakHashMap();
				cachedComparedNames.put(type, compared);
			}
			List<Name> list = compared.get(comparator);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getNames(type));
				Collections.sort(list, comparator);
				compared.put(comparator, list);
			}
			result.addAll(list);
		}
		Collections.sort(result, comparator);
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.OCCURRENCE, "getOccurrenceTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.OCCURRENCE, "getOccurrenceTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedOccurrences == null) {
			cachedOccurrences = HashUtil.getWeakHashMap();
		}
		List<Occurrence> list = cachedOccurrences.get(type);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getOccurrences(type));
			cachedOccurrences.put(type, list);
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
		if (cachedComparedOccurrences == null) {
			cachedComparedOccurrences = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Occurrence>, List<Occurrence>> compared = cachedComparedOccurrences.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedOccurrences.put(type, compared);
		}
		List<Occurrence> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getOccurrences(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedOccurrences == null) {
			cachedOccurrences = HashUtil.getWeakHashMap();
		}
		List<Occurrence> result = HashUtil.getList();
		for (Topic type : types) {
			List<Occurrence> list = cachedOccurrences.get(type);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getOccurrences(type));
				cachedOccurrences.put(type, list);
			}
			result.addAll(list);
		}
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedOccurrences == null) {
			cachedComparedOccurrences = HashUtil.getWeakHashMap();
		}
		List<Occurrence> result = HashUtil.getList();
		for (Topic type : types) {
			Map<Comparator<Occurrence>, List<Occurrence>> compared = cachedComparedOccurrences.get(type);
			if (compared == null) {
				compared = HashUtil.getWeakHashMap();
				cachedComparedOccurrences.put(type, compared);
			}
			List<Occurrence> list = compared.get(comparator);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getOccurrences(type));
				Collections.sort(list, comparator);
				compared.put(comparator, list);
			}
			result.addAll(list);
		}
		Collections.sort(result, comparator);
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.ROLE, "getRoleTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.ROLE, "getRoleTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedRoles == null) {
			cachedRoles = HashUtil.getWeakHashMap();
		}
		List<Role> list = cachedRoles.get(type);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getRoles(type));
			cachedRoles.put(type, list);
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
		if (cachedComparedRoles == null) {
			cachedComparedRoles = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Role>, List<Role>> compared = cachedComparedRoles.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedRoles.put(type, compared);
		}
		List<Role> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getRoles(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedRoles == null) {
			cachedRoles = HashUtil.getWeakHashMap();
		}
		List<Role> result = HashUtil.getList();
		for (Topic type : types) {
			List<Role> list = cachedRoles.get(type);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getRoles(type));
				cachedRoles.put(type, list);
			}
			result.addAll(list);
		}
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedRoles == null) {
			cachedComparedRoles = HashUtil.getWeakHashMap();
		}
		List<Role> result = HashUtil.getList();
		for (Topic type : types) {
			Map<Comparator<Role>, List<Role>> compared = cachedComparedRoles.get(type);
			if (compared == null) {
				compared = HashUtil.getWeakHashMap();
				cachedComparedRoles.put(type, compared);
			}
			List<Role> list = compared.get(comparator);
			if (list == null) {
				list = HashUtil.getList(getParentIndex().getRoles(type));
				Collections.sort(list, comparator);
				compared.put(comparator, list);
			}
			result.addAll(list);
		}
		Collections.sort(result, comparator);
		return secureSubList(result, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.TOPIC, "getTopicTypes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTypes(TopicMapStoreParameterType.TOPIC, "getTopicTypes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedTopics == null) {
			cachedTopics = HashUtil.getWeakHashMap();
		}
		List<Topic> list = cachedTopics.get(type);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getTopics(type));
			cachedTopics.put(type, list);
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
		if (cachedComparedTopics == null) {
			cachedComparedTopics = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Topic>, List<Topic>> compared = cachedComparedTopics.get(type);
		if (compared == null) {
			compared = HashUtil.getWeakHashMap();
			cachedComparedTopics.put(type, compared);
		}
		List<Topic> list = compared.get(comparator);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getTopics(type));
			Collections.sort(list, comparator);
			compared.put(comparator, list);
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
		if (cachedMatchingTopics == null) {
			cachedMatchingTopics = HashUtil.getWeakHashMap();
		}
		List<Topic> list = cachedMatchingTopics.get(types);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getTopics(types));
			cachedMatchingTopics.put(types, list);
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (cachedComparedMatchingTopics == null) {
			cachedComparedMatchingTopics = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Topic>, List<Topic>> map = cachedComparedMatchingTopics.get(types);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedComparedMatchingTopics.put(types, map);
		}
		List<Topic> topics = map.get(comparator);
		if (topics == null) {
			topics = HashUtil.getList(getParentIndex().getTopics(types));
			Collections.sort(topics, comparator);
			map.put(comparator, topics);
		}
		return secureSubList(topics, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (!all) {
			return getTopics(types, offset, limit);
		}
		if (cachedMatchingAllTopics == null) {
			cachedMatchingAllTopics = HashUtil.getWeakHashMap();
		}
		List<Topic> list = cachedMatchingAllTopics.get(types);
		if (list == null) {
			list = HashUtil.getList(getParentIndex().getTopics(types, all));
			cachedMatchingAllTopics.put(types, list);
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
		if (!all) {
			return getTopics(types, offset, limit);
		}
		if (cachedComparedMatchingAllTopics == null) {
			cachedComparedMatchingAllTopics = HashUtil.getWeakHashMap();
		}
		Map<Comparator<Topic>, List<Topic>> map = cachedComparedMatchingAllTopics.get(types);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedComparedMatchingAllTopics.put(types, map);
		}
		List<Topic> topics = map.get(comparator);
		if (topics == null) {
			topics = HashUtil.getList(getParentIndex().getTopics(types, all));
			Collections.sort(topics, comparator);
			map.put(comparator, topics);
		}
		return secureSubList(topics, offset, limit);
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
		if (cachedAssociations != null) {
			cachedAssociations.clear();
		}
		if (cachedComparedAssociations != null) {
			cachedComparedAssociations.clear();
		}
		if (cachedCharacteristics != null) {
			cachedCharacteristics.clear();
		}
		if (cachedComparedCharacteristics != null) {
			cachedComparedCharacteristics.clear();
		}
		if (cachedNames != null) {
			cachedNames.clear();
		}
		if (cachedComparedNames != null) {
			cachedComparedNames.clear();
		}
		if (cachedOccurrences != null) {
			cachedOccurrences.clear();
		}
		if (cachedComparedOccurrences != null) {
			cachedComparedOccurrences.clear();
		}
		if (cachedRoles != null) {
			cachedRoles.clear();
		}
		if (cachedComparedRoles != null) {
			cachedComparedRoles.clear();
		}
		if (cachedTopics != null) {
			cachedTopics.clear();
		}
		if (cachedComparedTopics != null) {
			cachedComparedTopics.clear();
		}
		if (cachedMatchingAllTopics != null) {
			cachedMatchingAllTopics.clear();
		}
		if (cachedMatchingTopics != null) {
			cachedMatchingTopics.clear();
		}
		if (cachedComparedMatchingAllTopics != null) {
			cachedComparedMatchingAllTopics.clear();
		}
		if (cachedComparedMatchingTopics != null) {
			cachedComparedMatchingTopics.clear();
		}
	}

	/**
	 * Method clears all caches depending on an association, its types or its
	 * roles
	 */
	private final void clearAssociationDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(TopicMapStoreParameterType.ASSOCIATION);
			cachedTypes.remove(TopicMapStoreParameterType.ROLE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(TopicMapStoreParameterType.ASSOCIATION);
			cachedTypes.remove(TopicMapStoreParameterType.ROLE);
		}
		if (cachedAssociations != null) {
			cachedAssociations.clear();
		}
		if (cachedComparedAssociations != null) {
			cachedComparedAssociations.clear();
		}
		if (cachedRoles != null) {
			cachedRoles.clear();
		}
		if (cachedComparedRoles != null) {
			cachedComparedRoles.clear();
		}
	}

	/**
	 * Method clears all caches depending on a name or its types
	 */
	private final void clearNameDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(TopicMapStoreParameterType.NAME);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(TopicMapStoreParameterType.NAME);
		}
		if (cachedCharacteristics != null) {
			cachedCharacteristics.clear();
		}
		if (cachedComparedCharacteristics != null) {
			cachedComparedCharacteristics.clear();
		}
		if (cachedNames != null) {
			cachedNames.clear();
		}
		if (cachedComparedNames != null) {
			cachedComparedNames.clear();
		}
	}

	/**
	 * Method clears all caches depending on an occurrence or its types
	 */
	private final void clearOccurrenceDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(TopicMapStoreParameterType.OCCURRENCE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(TopicMapStoreParameterType.OCCURRENCE);
		}
		if (cachedCharacteristics != null) {
			cachedCharacteristics.clear();
		}
		if (cachedComparedCharacteristics != null) {
			cachedComparedCharacteristics.clear();
		}
		if (cachedOccurrences != null) {
			cachedOccurrences.clear();
		}
		if (cachedComparedOccurrences != null) {
			cachedComparedOccurrences.clear();
		}
	}

	/**
	 * Method clears all caches depending on a role or its types
	 */
	private final void clearRoleDependentCache() {
		if (cachedTypes != null) {
			cachedTypes.remove(TopicMapStoreParameterType.ROLE);
		}
		if (cachedComparedTypes != null) {
			cachedComparedTypes.remove(TopicMapStoreParameterType.ROLE);
		}
		if (cachedRoles != null) {
			cachedRoles.clear();
		}
		if (cachedComparedRoles != null) {
			cachedComparedRoles.clear();
		}
	}

	/**
	 * Method reads the internal types and store them to the cache if they are
	 * not still contained.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @param redirectMethodName
	 *            the method to get the set containing the types
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return types within the given range
	 */
	@SuppressWarnings("unchecked")
	private List<Topic> getTypes(TopicMapStoreParameterType type, String redirectMethodName, int offset, int limit) {
		try {
			if (cachedTypes == null) {
				cachedTypes = HashUtil.getWeakHashMap();
			}
			/*
			 * get cached types
			 */
			List<Topic> types = cachedTypes.get(type);
			if (types == null) {
				/*
				 * call method to get types of the specific type
				 */
				Method method = getParentIndex().getClass().getMethod(redirectMethodName);
				Collection<Topic> col = (Collection<Topic>) method.invoke(getParentIndex());
				/*
				 * convert as list and store to parent map
				 */
				types = HashUtil.getList(col);
				cachedTypes.put(type, types);
			}
			/*
			 * create unmodifiable sub list
			 */
			return secureSubList(types, offset, limit);
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

	/**
	 * Method reads the internal types and store them to the cache if they are
	 * not still contained.
	 * 
	 * @param type
	 *            the argument indicates the type of constructs
	 * @param redirectMethodName
	 *            the method to get the set containing the types
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return types within the given range
	 */
	@SuppressWarnings("unchecked")
	private List<Topic> getTypes(TopicMapStoreParameterType type, String redirectMethodName, int offset, int limit, Comparator<Topic> comparator) {
		try {
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
			 * get types mapped to the comparator
			 */
			List<Topic> types = comparedTypes.get(comparator);
			if (types == null) {
				/*
				 * call method to get all types of the specific type
				 */
				Method method = getParentIndex().getClass().getMethod(redirectMethodName);
				Collection<Topic> col = (Collection<Topic>) method.invoke(getParentIndex());
				/*
				 * convert as list
				 */
				types = HashUtil.getList(col);
				/*
				 * sort and add to map
				 */
				Collections.sort(types, comparator);
				comparedTypes.put(comparator, types);
			}
			/*
			 * create unmodifiable sub list
			 */
			return secureSubList(types, offset, limit);
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
}
