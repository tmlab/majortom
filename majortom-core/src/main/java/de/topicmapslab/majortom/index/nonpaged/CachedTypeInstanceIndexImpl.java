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

import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.core.BaseCachedTypeInstanceIndexImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Implementation of the in-memory {@link ITypeInstanceIndex} supporting paging
 * 
 * @author Sven Krosse
 * 
 */
public abstract class CachedTypeInstanceIndexImpl<E extends ITopicMapStore> extends BaseCachedTypeInstanceIndexImpl<E> implements ITypeInstanceIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public CachedTypeInstanceIndexImpl(E store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociationTypes();
		}
		Collection<Topic> types = read(IAssociation.class);
		if (types == null) {
			types = doGetAssociationTypes();
			cache(IAssociation.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetAssociations(type);
		}
		Collection<Association> results = read(IAssociation.class, type, false);
		if (results == null) {
			results = doGetAssociations(type);
			cache(IAssociation.class, type, false, results);
		}
		return (Collection<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetAssociations(types);
		}
		Collection<Association> results = read(IAssociation.class, types, false);
		if (results == null) {
			results = doGetAssociations(types);
			cache(IAssociation.class, types, false, results);
		}
		return (Collection<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getCharacteristicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetCharacteristicTypes();
		}
		Collection<Topic> types = read(ICharacteristics.class);
		if (types == null) {
			types = doGetCharacteristicTypes();
			cache(ICharacteristics.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( type == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetCharacteristics(type);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, type, false);
		if (results == null) {
			results = doGetCharacteristics(type);
			cache(ICharacteristics.class, type, false, results);
		}
		return (Collection<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetCharacteristics(types);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, types, false);
		if (results == null) {
			results = doGetCharacteristics(types);
			cache(ICharacteristics.class, types, false, results);
		}
		return (Collection<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNameTypes();
		}
		Collection<Topic> types = read(IName.class);
		if (types == null) {
			types = doGetNameTypes();
			cache(IName.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( type == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNames(type);
		}
		Collection<Name> results = read(IName.class, type, false);
		if (results == null) {
			results = doGetNames(type);
			cache(IName.class, type, false, results);
		}
		return (Collection<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNames(types);
		}
		Collection<Name> results = read(IName.class, types, false);
		if (results == null) {
			results = doGetNames(types);
			cache(IName.class, types, false, results);
		}
		return (Collection<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetOccurrenceTypes();
		}
		Collection<Topic> types = read(IOccurrence.class);
		if (types == null) {
			types = doGetOccurrenceTypes();
			cache(IOccurrence.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( type == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetOccurrences(type);
		}
		Collection<Occurrence> results = read(IOccurrence.class, type, false);
		if (results == null) {
			results = doGetOccurrences(type);
			cache(IOccurrence.class, type, false, results);
		}
		return (Collection<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetOccurrences(types);
		}
		Collection<Occurrence> results = read(IOccurrence.class, types, false);
		if (results == null) {
			results = doGetOccurrences(types);
			cache(IOccurrence.class, types, false, results);
		}
		return (Collection<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetRoleTypes();
		}
		Collection<Topic> types = read(IAssociationRole.class);
		if (types == null) {
			types = doGetRoleTypes();
			cache(IAssociationRole.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( type == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetRoles(type);
		}
		Collection<Role> results = read(IAssociationRole.class, type, false);
		if (results == null) {
			results = doGetRoles(type);
			cache(IAssociationRole.class, type, false, results);
		}
		return (Collection<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetRoles(types);
		}
		Collection<Role> results = read(IAssociationRole.class, types, false);
		if (results == null) {
			results = doGetRoles(types);
			cache(IAssociationRole.class, types, false, results);
		}
		return (Collection<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicTypes();
		}
		Collection<Topic> types = read(ITopic.class);
		if (types == null) {
			types = doGetTopicTypes();
			cache(ITopic.class, types);
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetTopics(type);
		}
		Collection<Topic> results = read(ITopic.class, type, false);
		if (results == null) {
			results = doGetTopics(type);
			cache(ITopic.class, type, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopics(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( types == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetTopics(types, all);
		}
		Collection<Topic> results = read(ITopic.class, types, all);
		if (results == null) {
			results = doGetTopics(types, all);
			cache(ITopic.class, types, all, results);
		}
		return results;
	}

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @return the topic types within the given range
	 */
	protected abstract Collection<Topic> doGetTopicTypes();

	/**
	 * Returns all topic instances of the given topic type within the given
	 * range.
	 * 
	 * @param type
	 *            the type
	 * @return the topic within the given range
	 */
	protected abstract Collection<Topic> doGetTopics(Topic type);

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every
	 *            given type
	 * @return a Collection of all instances typed by at least one or every of
	 *         the given types within the given range
	 */
	protected abstract Collection<Topic> doGetTopics(Collection<Topic> types, boolean all);

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @return the association types within the given range
	 */
	protected abstract Collection<Topic> doGetAssociationTypes();

	/**
	 * Return all associations of the given type within the given range
	 * 
	 * @param type
	 *            the type
	 * @return all associations of the type within the given range
	 */
	protected abstract Collection<Association> doGetAssociations(Topic type);

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @return a Collection of all association items typed by one of the given
	 *         types within the given range
	 */
	protected abstract Collection<Association> doGetAssociations(Collection<? extends Topic> types);

	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @return all role types of the topic map within the given range.
	 */
	protected abstract Collection<Topic> doGetRoleTypes();

	/**
	 * Return all roles of the given type within the given range.
	 * 
	 * @param type
	 *            the role type
	 * @return all roles of the given type within the given range
	 */
	protected abstract Collection<Role> doGetRoles(Topic type);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @return a Collection of all association roles typed by one of the given
	 *         types within the given range
	 */
	protected abstract Collection<Role> doGetRoles(Collection<? extends Topic> types);

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @return a Collection of all types within the given range
	 */
	protected abstract Collection<Topic> doGetCharacteristicTypes();

	/**
	 * Returns all characteristics being typed by the given of topic type.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a Collection of all characteristics typed by the given type
	 *         within the given range
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(Topic type);

	/**
	 * Returns all characteristics typed by one of given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @return a Collection of all characteristics typed by one of the given
	 *         types within the given range
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types);

	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @return all name types within the given range
	 */
	protected abstract Collection<Topic> doGetNameTypes();

	/**
	 * Return all names of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * @return all names of the given type within the given range.
	 */
	protected abstract Collection<Name> doGetNames(Topic type);

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @return a Collection of all names typed by one of the given types within
	 *         the given range
	 */
	protected abstract Collection<Name> doGetNames(Collection<? extends Topic> types);

	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @return all occurrence types within the given range
	 */
	protected abstract Collection<Topic> doGetOccurrenceTypes();

	/**
	 * Return all occurrences of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return all occurrences of the given type within the given range.
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Topic type);

	/**
	 * Returns all occurrences typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @return a Collection of all occurrences typed by one of the given types
	 *         within the given range
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Collection<? extends Topic> types);

}
