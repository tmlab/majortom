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
package de.topicmapslab.majortom.index.nonpaged;

import java.util.Arrays;
import java.util.Collection;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.core.BaseCachedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class CachedSupertypeSubtypeIndexImpl<T extends ITopicMapStore> extends BaseCachedSupertypeSubtypeIndexImpl<T> implements ISupertypeSubtypeIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the internal topic map store
	 */
	public CachedSupertypeSubtypeIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetDirectSubtypes(type);
		}
		Collection<Topic> topics = read(Type.DIRECT_SUBTYPE, type, false);
		if (topics == null) {
			topics = doGetDirectSubtypes(type);
			cache(Type.DIRECT_SUBTYPE, type, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetDirectSupertypes(type);
		}
		Collection<Topic> topics = read(Type.DIRECT_SUPERTYPE, type, false);
		if (topics == null) {
			topics = doGetDirectSupertypes(type);
			cache(Type.DIRECT_SUPERTYPE, type, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetSubtypes();
		}
		Collection<Topic> topics = read(Type.ALL_SUBTYPES, null, false);
		if (topics == null) {
			topics = doGetSubtypes();
			cache(Type.ALL_SUBTYPES, null, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetSubtypes(type);
		}
		Collection<Topic> topics = read(Type.ALL_SUBTYPES, type, false);
		if (topics == null) {
			topics = doGetSubtypes(type);
			cache(Type.ALL_SUBTYPES, type, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Type array null is not allowed!");
		}
		return getSubtypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSubtypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetSubtypes(types, all);
		}
		Collection<Topic> topics = read(Type.SUBTYPE, types, all);
		if (topics == null) {
			topics = doGetSubtypes(types, all);
			cache(Type.SUBTYPE, types, all, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled()) {
			return doGetSupertypes();
		}
		Collection<Topic> topics = read(Type.ALL_SUPERTYPES, null, false);
		if (topics == null) {
			topics = doGetSupertypes();
			cache(Type.ALL_SUPERTYPES, null, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetSupertypes(type);
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, type, false);
		if (topics == null) {
			topics = doGetSupertypes(type);
			cache(Type.SUPERTYPE, type, false, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Type array null is not allowed!");
		}
		return getSupertypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSupertypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetSupertypes(types, all);
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, types, all);
		if (topics == null) {
			topics = doGetSupertypes(types, all);
			cache(Type.SUPERTYPE, types, all, topics);
		}
		return topics;
	}

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @return a Collection of all supertypes within the given range
	 */
	protected abstract Collection<Topic> doGetSupertypes();

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a Collection of all supertypes of the given type within the given
	 *         range
	 */
	protected abstract Collection<Topic> doGetSupertypes(Topic type);

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * 
	 * @return a Collection of all supertypes of the given type within the given
	 *         range
	 */
	protected abstract Collection<Topic> doGetDirectSupertypes(Topic type);

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
	 * @return a Collection of all supertypes of at least one of the given type
	 *         within the given range
	 */
	protected abstract Collection<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all);

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * @return a Collection of all subtypes within the given range
	 */
	protected abstract Collection<Topic> doGetSubtypes();

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a Collection of all subtypes of the given type within the given
	 *         range
	 */
	protected abstract Collection<Topic> doGetSubtypes(Topic type);

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a Collection of all subtypes of the given type within the given
	 *         range
	 */
	protected abstract Collection<Topic> doGetDirectSubtypes(Topic type);

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * @return a Collection of all subtypes of at least one of the given type
	 *         within the given range
	 */
	protected abstract Collection<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all);
}
