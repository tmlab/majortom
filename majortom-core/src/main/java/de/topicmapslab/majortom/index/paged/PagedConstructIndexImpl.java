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
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.index.core.BaseCachedIndexImpl;
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
public abstract class PagedConstructIndexImpl<T extends ITopicMapStore> extends BaseCachedIndexImpl<T> implements IPagedConstructIndex, ITopicMapListener {

	enum Type {
		SUPERTYPES,

		TYPES,

		NAMES,

		OCCURRENCES,

		VARIANTS,

		ROLES,

		ASSOCIATION,

		TOPIC
	}

	private Map<ConstructCacheKey, Long> cachedNumbersOfChildren;
	private Map<ConstructCacheKey, List<? extends Construct>> cachedConstructs;
	private Map<Object, Set<ConstructCacheKey>> dependentKeys;

	/**
	 * @param store
	 */
	public PagedConstructIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociations(offset, limit);
		}
		List<Association> cache = readConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap(), offset, limit);
		if (cache == null) {
			cache = doGetAssociations(offset, limit);
			cacheConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap(), offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociations(offset, limit);
		}
		List<Association> cache = readConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap(), offset, limit, comparator);
		if (cache == null) {
			cache = doGetAssociations(offset, limit, comparator);
			cacheConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap(), offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfAssociations();
		}
		Long noc = readNumberOfConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap());
		if (noc == null) {
			noc = doGetNumberOfAssociations();
			cacheNumberOfConstructs(Type.ASSOCIATION, getTopicMapStore().getTopicMap(), noc);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetAssociationsPlayed(topic, offset, limit);
		}
		List<Association> cache = readConstructs(Type.ASSOCIATION, topic, offset, limit);
		if (cache == null) {
			cache = doGetAssociationsPlayed(topic, offset, limit);
			cacheConstructs(Type.ASSOCIATION, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetAssociationsPlayed(topic, offset, limit, comparator);
		}
		List<Association> cache = readConstructs(Type.ASSOCIATION, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetAssociationsPlayed(topic, offset, limit, comparator);
			cacheConstructs(Type.ASSOCIATION, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociationsPlayed(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfAssociationsPlayed(topic);
		}
		Long noc = readNumberOfConstructs(Type.ASSOCIATION, topic);
		if (noc == null) {
			noc = doGetNumberOfAssociationsPlayed(topic);
			cacheNumberOfConstructs(Type.ASSOCIATION, topic, noc);
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
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNames(topic, offset, limit);
		}
		List<Name> cache = readConstructs(Type.NAMES, topic, offset, limit);
		if (cache == null) {
			cache = doGetNames(topic, offset, limit);
			cacheConstructs(Type.NAMES, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic topic, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNames(topic, offset, limit, comparator);
		}
		List<Name> cache = readConstructs(Type.NAMES, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetNames(topic, offset, limit, comparator);
			cacheConstructs(Type.NAMES, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfNames(topic);
		}
		Long noc = readNumberOfConstructs(Type.NAMES, topic);
		if (noc == null) {
			noc = doGetNumberOfNames(topic);
			cacheNumberOfConstructs(Type.NAMES, topic, noc);
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
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetOccurrences(topic, offset, limit);
		}
		List<Occurrence> cache = readConstructs(Type.OCCURRENCES, topic, offset, limit);
		if (cache == null) {
			cache = doGetOccurrences(topic, offset, limit);
			cacheConstructs(Type.OCCURRENCES, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetOccurrences(topic, offset, limit, comparator);
		}
		List<Occurrence> cache = readConstructs(Type.OCCURRENCES, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetOccurrences(topic, offset, limit, comparator);
			cacheConstructs(Type.OCCURRENCES, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfOccurrences(topic);
		}
		Long noc = readNumberOfConstructs(Type.OCCURRENCES, topic);
		if (noc == null) {
			noc = doGetNumberOfOccurrences(topic);
			cacheNumberOfConstructs(Type.OCCURRENCES, topic, noc);
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
		if (association == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(association)) {
			return doGetRoles(association, offset, limit);
		}
		List<Role> cache = readConstructs(Type.ROLES, association, offset, limit);
		if (cache == null) {
			cache = doGetRoles(association, offset, limit);
			cacheConstructs(Type.ROLES, association, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Association association, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (association == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(association)) {
			return doGetRoles(association, offset, limit, comparator);
		}
		List<Role> cache = readConstructs(Type.ROLES, association, offset, limit, comparator);
		if (cache == null) {
			cache = doGetRoles(association, offset, limit, comparator);
			cacheConstructs(Type.ROLES, association, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoles(Association association) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (association == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(association)) {
			return doGetNumberOfRoles(association);
		}
		Long noc = readNumberOfConstructs(Type.ROLES, association);
		if (noc == null) {
			noc = doGetNumberOfRoles(association);
			cacheNumberOfConstructs(Type.ROLES, association, noc);
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
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetRolesPlayed(topic, offset, limit);
		}
		List<Role> cache = readConstructs(Type.ROLES, topic, offset, limit);
		if (cache == null) {
			cache = doGetRolesPlayed(topic, offset, limit);
			cacheConstructs(Type.ROLES, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetRolesPlayed(topic, offset, limit, comparator);
		}
		List<Role> cache = readConstructs(Type.ROLES, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetRolesPlayed(topic, offset, limit, comparator);
			cacheConstructs(Type.ROLES, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRolesPlayed(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfRolesPlayed(topic);
		}
		Long noc = readNumberOfConstructs(Type.ROLES, topic);
		if (noc == null) {
			noc = doGetNumberOfRolesPlayed(topic);
			cacheNumberOfConstructs(Type.ROLES, topic, noc);
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
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetSupertypes(topic, offset, limit);
		}
		List<Topic> cache = readConstructs(Type.SUPERTYPES, topic, offset, limit);
		if (cache == null) {
			cache = doGetSupertypes(topic, offset, limit);
			cacheConstructs(Type.SUPERTYPES, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetSupertypes(topic, offset, limit, comparator);
		}
		List<Topic> cache = readConstructs(Type.SUPERTYPES, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetSupertypes(topic, offset, limit, comparator);
			cacheConstructs(Type.SUPERTYPES, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfSupertypes(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfSupertypes(topic);
		}
		Long noc = readNumberOfConstructs(Type.SUPERTYPES, topic);
		if (noc == null) {
			noc = doGetNumberOfSupertypes(topic);
			cacheNumberOfConstructs(Type.SUPERTYPES, topic, noc);
		}
		return noc;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopics(offset, limit);
		}
		List<Topic> cache = readConstructs(Type.TOPIC, getTopicMapStore().getTopicMap(), offset, limit);
		if (cache == null) {
			cache = doGetTopics(offset, limit);
			cacheConstructs(Type.TOPIC, getTopicMapStore().getTopicMap(), offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopics(offset, limit, comparator);
		}
		List<Topic> cache = readConstructs(Type.TOPIC, getTopicMapStore().getTopicMap(), offset, limit, comparator);
		if (cache == null) {
			cache = doGetTopics(offset, limit, comparator);
			cacheConstructs(Type.TOPIC, getTopicMapStore().getTopicMap(), offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTopics() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfTopics();
		}
		Long noc = readNumberOfConstructs(Type.TOPIC, getTopicMapStore().getTopicMap());
		if (noc == null) {
			noc = doGetNumberOfTopics();
			cacheNumberOfConstructs(Type.TOPIC, getTopicMapStore().getTopicMap(), noc);
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
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetTypes(topic, offset, limit);
		}
		List<Topic> cache = readConstructs(Type.TYPES, topic, offset, limit);
		if (cache == null) {
			cache = doGetTypes(topic, offset, limit);
			cacheConstructs(Type.TYPES, topic, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetTypes(topic, offset, limit, comparator);
		}
		List<Topic> cache = readConstructs(Type.TYPES, topic, offset, limit, comparator);
		if (cache == null) {
			cache = doGetTypes(topic, offset, limit, comparator);
			cacheConstructs(Type.TYPES, topic, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTypes(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(topic)) {
			return doGetNumberOfTypes(topic);
		}
		Long noc = readNumberOfConstructs(Type.TYPES, topic);
		if (noc == null) {
			noc = doGetNumberOfTypes(topic);
			cacheNumberOfConstructs(Type.TYPES, topic, noc);
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
		if (name == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(name)) {
			return doGetVariants(name, offset, limit);
		}
		List<Variant> cache = readConstructs(Type.VARIANTS, name, offset, limit);
		if (cache == null) {
			cache = doGetVariants(name, offset, limit);
			cacheConstructs(Type.VARIANTS, name, offset, limit, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Name name, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (name == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(name)) {
			return doGetVariants(name, offset, limit, comparator);
		}
		List<Variant> cache = readConstructs(Type.VARIANTS, name, offset, limit, comparator);
		if (cache == null) {
			cache = doGetVariants(name, offset, limit, comparator);
			cacheConstructs(Type.VARIANTS, name, offset, limit, comparator, cache);
		}
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(Name name) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (name == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * is caching is disabled redirect to topic map store
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(name)) {
			return doGetNumberOfVariants(name);
		}
		Long noc = readNumberOfConstructs(Type.VARIANTS, name);
		if (noc == null) {
			noc = doGetNumberOfVariants(name);
			cacheNumberOfConstructs(Type.VARIANTS, name, noc);
		}
		return noc;
	}

	/**
	 * Internal method to read the number of contained construct.
	 * 
	 * @param type
	 *            the class
	 * @param context
	 *            the context
	 * @return dependent constructs from cache
	 */
	protected final Long readNumberOfConstructs(Type type, Construct context) {
		/*
		 * check main cache
		 */
		if (cachedNumbersOfChildren == null) {
			return null;
		}
		/*
		 * get constructs by key
		 */
		return cachedNumbersOfChildren.get(generateCacheKey(type, context, null, null, null));
	}

	/**
	 * Internal method to read the number of contained construct.
	 * 
	 * @param type
	 *            the class
	 * @param context
	 *            the context
	 * @param noc
	 *            the number of constructs to cache
	 * @return dependent constructs from cache
	 */
	protected final void cacheNumberOfConstructs(Type type, Construct context, Long noc) {
		/*
		 * check main cache
		 */
		if (cachedNumbersOfChildren == null) {
			cachedNumbersOfChildren = HashUtil.getHashMap();
		}
		/*
		 * add to internal cache
		 */
		cachedNumbersOfChildren.put(generateCacheKey(type, context, null, null, null), noc);
	}

	/**
	 * Internal method to read dependent constructs of the given parent construct from cache.
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * 
	 * @param type
	 *            the type
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @return dependent constructs from cache
	 */
	protected final <X extends Construct> List<X> readConstructs(Type type, Construct context, Integer offset, Integer limit) {
		return readConstructs(type, context, offset, limit, null);
	}

	/**
	 * Internal method to read dependent constructs of the given parent construct from cache.
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * 
	 * @param type
	 *            the type
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return dependent constructs from cache
	 */
	@SuppressWarnings("unchecked")
	protected final <X extends Construct> List<X> readConstructs(Type type, Construct context, Integer offset, Integer limit, Comparator<?> comparator) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		/*
		 * get constructs by key
		 */
		return (List<X>) cachedConstructs.get(generateCacheKey(type, context, offset, limit, comparator));
	}

	/**
	 * Internal method to add dependent constructs of the given parent construct to internal cache
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * 
	 * @param type
	 *            the type
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final <X extends Construct> void cacheConstructs(Type type, Construct context, Integer offset, Integer limit, List<X> values) {
		cacheConstructs(type, context, offset, limit, null, values);
	}

	/**
	 * Internal method to add dependent constructs of the given parent construct to internal cache
	 * 
	 * @param <X>
	 *            the type of dependent constructs
	 * 
	 * @param type
	 *            the type
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final <X extends Construct> void cacheConstructs(Type type, Construct context, Integer offset, Integer limit, Comparator<X> comparator, List<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getHashMap();
		}
		/*
		 * add to internal cache
		 */
		cachedConstructs.put(generateCacheKey(type, context, offset, limit, comparator), values);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		Set<ConstructCacheKey> keys = HashUtil.getHashSet();

		/*
		 * topic removed or merged
		 */
		if (event == TopicMapEventType.TOPIC_REMOVED || event == TopicMapEventType.MERGE) {
			clearCache();
		}
		/*
		 * variant added or removed
		 */
		else if (event == TopicMapEventType.VARIANT_ADDED || event == TopicMapEventType.VARIANT_REMOVED) {
			keys.addAll(getDependentKeys(Type.VARIANTS));
			keys.retainAll(getDependentKeys(notifier));
		}
		/*
		 * name added or removed
		 */
		else if (event == TopicMapEventType.NAME_ADDED || event == TopicMapEventType.NAME_REMOVED) {
			keys.addAll(getDependentKeys(Type.NAMES));
			keys.retainAll(getDependentKeys(notifier));
		}
		/*
		 * occurrence added or removed
		 */
		else if (event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.OCCURRENCE_REMOVED) {
			keys.addAll(getDependentKeys(Type.OCCURRENCES));
			keys.retainAll(getDependentKeys(notifier));
		}
		/*
		 * topic role added or removed
		 */
		else if (event == TopicMapEventType.ROLE_ADDED || event == TopicMapEventType.ROLE_REMOVED) {
			// played roles
			keys.addAll(getDependentKeys(Type.ROLES));
			// played association
			keys.addAll(getDependentKeys(Type.ASSOCIATION));
		}
		/*
		 * type changed or removed
		 */
		else if (event == TopicMapEventType.TYPE_ADDED || event == TopicMapEventType.TYPE_REMOVED) {
			keys.addAll(getDependentKeys(Type.TYPES));
			keys.retainAll(getDependentKeys(notifier));
		}
		/*
		 * supertype changed or removed
		 */
		else if (event == TopicMapEventType.SUPERTYPE_ADDED || event == TopicMapEventType.SUPERTYPE_REMOVED) {
			keys.addAll(getDependentKeys(Type.SUPERTYPES));
			keys.retainAll(getDependentKeys(notifier));
		}
		/*
		 * player modified or removed
		 */
		else if (event == TopicMapEventType.PLAYER_MODIFIED) {
			// played roles
			keys.addAll(getDependentKeys(Type.ROLES));
			// played association
			keys.addAll(getDependentKeys(Type.ASSOCIATION));
			// old and new player
			Collection<ConstructCacheKey> others = HashUtil.getHashSet(getDependentKeys(newValue));
			others.addAll(getDependentKeys(oldValue));
			// intersection
			keys.retainAll(others);
		}

		/*
		 * clear cache for dependent keys
		 */
		for (ConstructCacheKey key : keys) {
			if (cachedNumbersOfChildren != null) {
				cachedNumbersOfChildren.remove(key);
			}
			if (cachedConstructs != null) {
				cachedConstructs.remove(key);
			}
		}
	}

	/**
	 * Internal method to clear the cache
	 */
	public final void clearCache() {
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedNumbersOfChildren != null) {
			cachedNumbersOfChildren.clear();
		}
		if (dependentKeys != null) {
			dependentKeys.clear();
		}
	}

	/**
	 * Generates a key for internal caching
	 * 
	 * @param type
	 *            the type
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated cache key;
	 */
	private ConstructCacheKey generateCacheKey(Type type, Construct context, Integer offset, Integer limit, Comparator<?> comparator) {
		ConstructCacheKey key = new ConstructCacheKey(type, context, offset, limit, comparator);
		/*
		 * store dependent keys for clearing cache
		 */
		if (dependentKeys == null) {
			dependentKeys = HashUtil.getHashMap();
		}
		Set<ConstructCacheKey> keys = dependentKeys.get(type);
		if (keys == null) {
			keys = HashUtil.getHashSet();
			dependentKeys.put(type, keys);
		}
		keys.add(key);

		keys = dependentKeys.get(context);
		if (keys == null) {
			keys = HashUtil.getHashSet();
			dependentKeys.put(context, keys);
		}
		keys.add(key);
		return key;
	}

	/**
	 * Returns a set of cache keys dependents on the given criteria
	 * 
	 * @param criteria
	 *            the criteria
	 * @return all dependent keys
	 */
	private Set<ConstructCacheKey> getDependentKeys(Object criteria) {
		if (dependentKeys == null || !dependentKeys.containsKey(criteria)) {
			return Collections.emptySet();
		}
		return dependentKeys.get(criteria);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all types of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the given topic as a sorted list within the given range.
	 */
	protected List<Topic> doGetTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getTypes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return noc;
	}

	/**
	 * Returns all supetypes of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all supetypes of the given topic as a list within the given range.
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getSupertypes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all supetypes of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all supetypes of the given topic as a sorted list within the given range.
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(((ITopic) topic).getSupertypes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all names of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all names of the given topic as a sorted list within the given range.
	 */
	protected List<Name> doGetNames(Topic topic, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(((ITopic) topic).getNames());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return noc;
	}

	/**
	 * Returns all occurrences of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all occurrences of the given topic as a list within the given range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(((ITopic) topic).getOccurrences());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all occurrences of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given topic as a sorted list within the given range.
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(((ITopic) topic).getOccurrences());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all variants of the given name as a sorted list within the given range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all variants of the given name as a sorted list within the given range.
	 */
	protected List<Variant> doGetVariants(Name name, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(name.getVariants());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return noc;
	}

	/**
	 * Returns all roles of the given association as a list within the given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles of the given association as a list within the given range.
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit) {
		List<Role> list = HashUtil.getList(association.getRoles());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all roles of the given association as a sorted list within the given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given association as a sorted list within the given range.
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(association.getRoles());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return noc;
	}

	/**
	 * Returns all associations of the topic map as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations as a list within the given range.
	 */
	protected List<Association> doGetAssociations(int offset, int limit) {
		List<Association> list = HashUtil.getList(getTopicMapStore().getTopicMap().getAssociations());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all associations of the topic map as a sorted list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations as a sorted list within the given range.
	 */
	protected List<Association> doGetAssociations(int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getTopicMapStore().getTopicMap().getAssociations());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of associations of the topic map
	 * 
	 * @return the number of associations
	 */
	protected long doGetNumberOfAssociations() {
		long noc = getTopicMapStore().getTopicMap().getAssociations().size();
		return noc;
	}

	/**
	 * Returns all associations played by given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations played by given topic as a list within the given range.
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit) {
		List<Association> list = HashUtil.getList(((ITopic) topic).getAssociationsPlayed());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all associations played by given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations played by given topic as a sorted list within the given range.
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(((ITopic) topic).getAssociationsPlayed());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of played associations of the topic
	 * 
	 * @param topic
	 *            the topic whose number of played associations should be returned
	 * @return the number of played associations
	 */
	protected long doGetNumberOfAssociationsPlayed(Topic topic) {
		long noc = ((ITopic) topic).getAssociationsPlayed().size();
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all roles played by given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles played by given topic as a sorted list within the given range.
	 */
	protected List<Role> doGetRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(topic.getRolesPlayed());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return noc;
	}

	/**
	 * Returns all topics of the topic map as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all topic as a list within the given range.
	 */
	protected List<Topic> doGetTopics(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getTopicMapStore().getTopicMap().getTopics());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all topics of the topic map within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all topic as a sorted list within the given range.
	 */
	protected List<Topic> doGetTopics(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getTopicMapStore().getTopicMap().getTopics());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return the number of topics
	 * 
	 * @return the number of topics
	 */
	protected long doGetNumberOfTopics() {
		long noc = getTopicMapStore().getTopicMap().getTopics().size();
		return noc;
	}

	/**
	 * Removed any cached content from internal cache
	 */
	public void clear() {
		clearCache();
	}

}

/**
 * Class defining a cache key for {@link IPagedConstructIndex}
 * 
 * @author Sven Krosse
 * 
 */
class ConstructCacheKey {
	PagedConstructIndexImpl.Type type;
	Construct context;
	Integer offset;
	Integer limit;
	Comparator<?> comparator;

	/**
	 * constructor
	 * 
	 * @param type
	 *            the type of constructs
	 * @param context
	 *            the context
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public ConstructCacheKey(PagedConstructIndexImpl.Type type, Construct context, Integer offset, Integer limit, Comparator<?> comparator) {
		this.type = type;
		this.context = context;
		this.offset = offset;
		this.limit = limit;
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ConstructCacheKey) {
			ConstructCacheKey key = (ConstructCacheKey) obj;
			boolean result = key.type.equals(type);
			result &= context.equals(key.context);
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
		hashCode |= context.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}

}