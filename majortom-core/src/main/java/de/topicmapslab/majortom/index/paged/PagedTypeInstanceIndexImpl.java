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
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the in-memory {@link IPagedTypeInstanceIndex} supporting paging
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedTypeInstanceIndexImpl<E extends ITopicMapStore> extends BaseCachedTypeInstanceIndexImpl<E> implements IPagedTypeInstanceIndex {

	private final ITypeInstanceIndex parentIndex;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 * @param parentIndex
	 *            the parent {@link ITypeInstanceIndex}
	 */
	public PagedTypeInstanceIndexImpl(E store, ITypeInstanceIndex parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * @return the parentIndex
	 */
	public ITypeInstanceIndex getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociationTypes(offset, limit);
		}
		Collection<Topic> types = read(IAssociation.class, offset, limit, null);
		if (types == null) {
			types = doGetAssociationTypes(offset, limit);
			cache(IAssociation.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociationTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(IAssociation.class, offset, limit, comparator);
		if (types == null) {
			types = doGetAssociationTypes(offset, limit, comparator);
			cache(IAssociation.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfAssociationTypes();
		}
		long number = readNumberOfConstructs(IAssociation.class, null, false);
		if (number == -1) {
			number = doGetNumberOfAssociationTypes();
			cacheNumberOfConstructs(IAssociation.class, null, false, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetAssociations(type, offset, limit);
		}
		Collection<Association> results = read(IAssociation.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetAssociations(type, offset, limit);
			cache(IAssociation.class, type, false, offset, limit, null, results);
		}
		return (List<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic type, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetAssociations(type, offset, limit, comparator);
		}
		Collection<Association> results = read(IAssociation.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetAssociations(type, offset, limit, comparator);
			cache(IAssociation.class, type, false, offset, limit, comparator, results);
		}
		return (List<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfAssociations(type);
		}
		long value = readNumberOfConstructs(IAssociation.class, type, false);
		if (value == -1) {
			value = doGetNumberOfAssociations(type);
			cacheNumberOfConstructs(IAssociation.class, type, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetAssociations(types, offset, limit);
		}
		Collection<Association> results = read(IAssociation.class, types, false, offset, limit, null);
		if (results == null) {
			results = doGetAssociations(types, offset, limit);
			cache(IAssociation.class, types, false, offset, limit, null, results);
		}
		return (List<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetAssociations(types, offset, limit, comparator);
		}
		Collection<Association> results = read(IAssociation.class, types, false, offset, limit, comparator);
		if (results == null) {
			results = doGetAssociations(types, offset, limit, comparator);
			cache(IAssociation.class, types, false, offset, limit, comparator, results);
		}
		return (List<Association>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfAssociations(types);
		}
		long value = readNumberOfConstructs(IAssociation.class, types, false);
		if (value == -1) {
			value = doGetNumberOfAssociations(types);
			cacheNumberOfConstructs(IAssociation.class, types, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetCharacteristicTypes(offset, limit);
		}
		Collection<Topic> types = read(ICharacteristics.class, offset, limit, null);
		if (types == null) {
			types = doGetCharacteristicTypes(offset, limit);
			cache(ICharacteristics.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetCharacteristicTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(ICharacteristics.class, offset, limit, comparator);
		if (types == null) {
			types = doGetCharacteristicTypes(offset, limit, comparator);
			cache(ICharacteristics.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfCharacteristicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfCharacteristicTypes();
		}
		long number = readNumberOfConstructs(ICharacteristics.class, null, false);
		if (number == -1) {
			number = doGetNumberOfCharacteristicTypes();
			cacheNumberOfConstructs(ICharacteristics.class, null, false, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetCharacteristics(type, offset, limit);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(type, offset, limit);
			cache(ICharacteristics.class, type, false, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetCharacteristics(type, offset, limit, comparator);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(type, offset, limit, comparator);
			cache(ICharacteristics.class, type, false, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfCharacteristics(type);
		}
		long value = readNumberOfConstructs(ICharacteristics.class, type, false);
		if (value == -1) {
			value = doGetNumberOfCharacteristics(type);
			cacheNumberOfConstructs(ICharacteristics.class, type, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetCharacteristics(types, offset, limit);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, types, false, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(types, offset, limit);
			cache(ICharacteristics.class, types, false, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetCharacteristics(types, offset, limit, comparator);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, types, false, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(types, offset, limit, comparator);
			cache(ICharacteristics.class, types, false, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfCharacteristics(types);
		}
		long value = readNumberOfConstructs(ICharacteristics.class, types, false);
		if (value == -1) {
			value = doGetNumberOfCharacteristics(types);
			cacheNumberOfConstructs(ICharacteristics.class, types, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNameTypes(offset, limit);
		}
		Collection<Topic> types = read(IName.class, offset, limit, null);
		if (types == null) {
			types = doGetNameTypes(offset, limit);
			cache(IName.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNameTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(IName.class, offset, limit, comparator);
		if (types == null) {
			types = doGetNameTypes(offset, limit, comparator);
			cache(IName.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfNameTypes();
		}
		long number = readNumberOfConstructs(IName.class, null, false);
		if (number == -1) {
			number = doGetNumberOfNameTypes();
			cacheNumberOfConstructs(IName.class, null, false, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNames(type, offset, limit);
		}
		Collection<Name> results = read(IName.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetNames(type, offset, limit);
			cache(IName.class, type, false, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic type, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNames(type, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(type, offset, limit, comparator);
			cache(IName.class, type, false, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfNames(type);
		}
		long value = readNumberOfConstructs(IName.class, type, false);
		if (value == -1) {
			value = doGetNumberOfNames(type);
			cacheNumberOfConstructs(IName.class, type, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNames(types, offset, limit);
		}
		Collection<Name> results = read(IName.class, types, false, offset, limit, null);
		if (results == null) {
			results = doGetNames(types, offset, limit);
			cache(IName.class, types, false, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNames(types, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, types, false, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(types, offset, limit, comparator);
			cache(IName.class, types, false, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfNames(types);
		}
		long value = readNumberOfConstructs(IName.class, types, false);
		if (value == -1) {
			value = doGetNumberOfNames(types);
			cacheNumberOfConstructs(IName.class, types, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetOccurrenceTypes(offset, limit);
		}
		Collection<Topic> types = read(IOccurrence.class, offset, limit, null);
		if (types == null) {
			types = doGetOccurrenceTypes(offset, limit);
			cache(IOccurrence.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetOccurrenceTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(IOccurrence.class, offset, limit, comparator);
		if (types == null) {
			types = doGetOccurrenceTypes(offset, limit, comparator);
			cache(IOccurrence.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfOccurrenceTypes();
		}
		long number = readNumberOfConstructs(IOccurrence.class, null, false);
		if (number == -1) {
			number = doGetNumberOfOccurrenceTypes();
			cacheNumberOfConstructs(IOccurrence.class, null, false, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetOccurrences(type, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(type, offset, limit);
			cache(IOccurrence.class, type, false, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetOccurrences(type, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(type, offset, limit, comparator);
			cache(IOccurrence.class, type, false, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfOccurrences(type);
		}
		long value = readNumberOfConstructs(IOccurrence.class, type, false);
		if (value == -1) {
			value = doGetNumberOfOccurrences(type);
			cacheNumberOfConstructs(IOccurrence.class, type, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetOccurrences(types, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, types, false, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(types, offset, limit);
			cache(IOccurrence.class, types, false, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetOccurrences(types, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, types, false, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(types, offset, limit, comparator);
			cache(IOccurrence.class, types, false, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfOccurrences(types);
		}
		long value = readNumberOfConstructs(IOccurrence.class, types, false);
		if (value == -1) {
			value = doGetNumberOfOccurrences(types);
			cacheNumberOfConstructs(IOccurrence.class, types, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetRoleTypes(offset, limit);
		}
		Collection<Topic> types = read(IAssociationRole.class, offset, limit, null);
		if (types == null) {
			types = doGetRoleTypes(offset, limit);
			cache(IAssociationRole.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getRoleTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetRoleTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(IAssociationRole.class, offset, limit, comparator);
		if (types == null) {
			types = doGetRoleTypes(offset, limit, comparator);
			cache(IAssociationRole.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfRoleTypes();
		}
		long number = readNumberOfConstructs(IAssociationRole.class, null, false);
		if (number == -1) {
			number = doGetNumberOfRoleTypes();
			cacheNumberOfConstructs(IAssociationRole.class, null, false, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetRoles(type, offset, limit);
		}
		Collection<Role> results = read(IAssociationRole.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetRoles(type, offset, limit);
			cache(IAssociationRole.class, type, false, offset, limit, null, results);
		}
		return (List<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Topic type, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetRoles(type, offset, limit, comparator);
		}
		Collection<Role> results = read(IAssociationRole.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetRoles(type, offset, limit, comparator);
			cache(IAssociationRole.class, type, false, offset, limit, comparator, results);
		}
		return (List<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfRoles(type);
		}
		long value = readNumberOfConstructs(IAssociationRole.class, type, false);
		if (value == -1) {
			value = doGetNumberOfRoles(type);
			cacheNumberOfConstructs(IAssociationRole.class, type, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetRoles(types, offset, limit);
		}
		Collection<Role> results = read(IAssociationRole.class, types, false, offset, limit, null);
		if (results == null) {
			results = doGetRoles(types, offset, limit);
			cache(IAssociationRole.class, types, false, offset, limit, null, results);
		}
		return (List<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetRoles(types, offset, limit, comparator);
		}
		Collection<Role> results = read(IAssociationRole.class, types, false, offset, limit, comparator);
		if (results == null) {
			results = doGetRoles(types, offset, limit, comparator);
			cache(IAssociationRole.class, types, false, offset, limit, comparator, results);
		}
		return (List<Role>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfRoles(types);
		}
		long value = readNumberOfConstructs(IAssociationRole.class, types, false);
		if (value == -1) {
			value = doGetNumberOfRoles(types);
			cacheNumberOfConstructs(IAssociationRole.class, types, false, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicTypes(offset, limit);
		}
		Collection<Topic> types = read(ITopic.class, offset, limit, null);
		if (types == null) {
			types = doGetTopicTypes(offset, limit);
			cache(ITopic.class, offset, limit, null, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicTypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicTypes(offset, limit, comparator);
		}
		Collection<Topic> types = read(ITopic.class, offset, limit, comparator);
		if (types == null) {
			types = doGetTopicTypes(offset, limit, comparator);
			cache(ITopic.class, offset, limit, comparator, types);
		}
		return (List<Topic>) types;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfTopicTypes();
		}
		long number = readNumberOfConstructs(ITopic.class, null, null);
		if (number == -1) {
			number = doGetNumberOfTopicTypes();
			cacheNumberOfConstructs(ITopic.class, null, null, number);
		}
		return number;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetTopics(type, offset, limit);
		}
		Collection<Topic> results = read(ITopic.class, type, false, offset, limit, null);
		if (results == null) {
			results = doGetTopics(type, offset, limit);
			cache(ITopic.class, type, false, offset, limit, null, results);
		}
		return (List<Topic>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetTopics(type, offset, limit, comparator);
		}
		Collection<Topic> results = read(ITopic.class, type, false, offset, limit, comparator);
		if (results == null) {
			results = doGetTopics(type, offset, limit, comparator);
			cache(ITopic.class, type, false, offset, limit, comparator, results);
		}
		return (List<Topic>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(type)) {
			return doGetNumberOfTopics(type);
		}
		long results = readNumberOfConstructs(ITopic.class, type, false);
		if (results == -1) {
			results = doGetNumberOfTopics(type);
			cacheNumberOfConstructs(ITopic.class, type, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
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
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		return getTopics(types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		return getNumberOfTopics(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetTopics(types, all, offset, limit);
		}
		Collection<Topic> results = read(ITopic.class, types, all, offset, limit, null);
		if (results == null) {
			results = doGetTopics(types, all, offset, limit);
			cache(ITopic.class, types, all, offset, limit, null, results);
		}
		return (List<Topic>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetTopics(types, all, offset, limit, comparator);
		}
		Collection<Topic> results = read(ITopic.class, types, all, offset, limit, comparator);
		if (results == null) {
			results = doGetTopics(types, all, offset, limit, comparator);
			cache(ITopic.class, types, all, offset, limit, comparator, results);
		}
		return (List<Topic>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(types)) {
			return doGetNumberOfTopics(types, all);
		}
		long value = readNumberOfConstructs(ITopic.class, types, all);
		if (value == -1) {
			value = doGetNumberOfTopics(types, all);
			cacheNumberOfConstructs(ITopic.class, types, all, value);
		}
		return value;
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of topic types
	 * 
	 * @return the number of topic types
	 */
	protected long doGetNumberOfTopicTypes() {
		return getParentIndex().getTopicTypes().size();
	}

	/**
	 * Returns all topic instances of the given topic type within the given range.
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all topic instances of the given topic type within the given range.
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of all topic instances of the given topic type within the given range.
	 * 
	 * @param type
	 *            the type
	 * @return the number
	 */
	protected long doGetNumberOfTopics(Topic type) {
		return getParentIndex().getTopics(type).size();
	}

	/**
	 * Returns all instances of at least one given type or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all instances typed by at least one or every of the given types within the given range
	 */
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(types, all));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all instances of at least one given type or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all instances typed by at least one or every of the given types within the given range
	 */
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getTopics(types, all));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of instances of at least one given type or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @return the number
	 */
	protected long doGetNumberOfTopics(Collection<Topic> types, boolean all) {
		return getParentIndex().getTopics(types, all).size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of association types
	 * 
	 * @return the number of association types
	 */
	protected long doGetNumberOfAssociationTypes() {
		return getParentIndex().getAssociationTypes().size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of associations typed by the given topic
	 * 
	 * @param type
	 *            the type
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(Topic type) {
		return getParentIndex().getAssociations(type).size();
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
	 * @return a list of all association items typed by one of the given types within the given range
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(types));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a list of all association items typed by one of the given types within the given range
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(types));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of associations typed by one of the given topics
	 * 
	 * @param types
	 *            the topic types
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(Collection<? extends Topic> types) {
		return getParentIndex().getAssociations(types).size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of role types
	 * 
	 * @return the number of role types
	 */
	protected long doGetNumberOfRoleTypes() {
		return getParentIndex().getRoleTypes().size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of roles typed by the given topic
	 * 
	 * @param type
	 *            the role type
	 * @return the number
	 */
	protected long doGetNumberOfRoles(Topic type) {
		return getParentIndex().getRoles(type).size();
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
	 * @return a list of all association roles typed by one of the given types within the given range
	 */
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(types));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a list of all association roles typed by one of the given types within the given range
	 */
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		List<Role> list = HashUtil.getList(getParentIndex().getRoles(types));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of roles typed by the given topics
	 * 
	 * @param types
	 *            the role types
	 * @return the number
	 */
	protected long doGetNumberOfRoles(Collection<? extends Topic> types) {
		return getParentIndex().getRoles(types).size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of characteristic types
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfCharacteristicTypes() {
		return getParentIndex().getCharacteristicTypes().size();
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
	 * @return a list of all characteristics typed by the given type within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(type));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a list of all characteristics typed by the given type within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(type));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of all characteristics being typed by the given of topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @return the number
	 */
	protected long doGetNumberOfCharacteristics(Topic type) {
		return getParentIndex().getCharacteristics(type).size();
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
	 * @return a list of all characteristics typed by one of the given types within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(types));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a list of all characteristics typed by one of the given types within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(types));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of all characteristics being typed by the given of topic types.
	 * 
	 * @param types
	 *            the topic types
	 * @return the number
	 */
	protected long doGetNumberOfCharacteristics(Collection<? extends Topic> types) {
		return getParentIndex().getCharacteristics(types).size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of name types
	 * 
	 * @return the number of name types
	 */
	protected long doGetNumberOfNameTypes() {
		return getParentIndex().getNameTypes().size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of all names of the given type.
	 * 
	 * @param type
	 *            the type
	 * @return the number
	 */
	protected long doGetNumberOfNames(Topic type) {
		return getParentIndex().getNames(type).size();
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
	 * @return a list of all names typed by one of the given types within the given range
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(types));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a list of all names typed by one of the given types within the given range
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(types));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of all names of the given types.
	 * 
	 * @param types
	 *            the types
	 * @return the number
	 */
	protected long doGetNumberOfNames(Collection<? extends Topic> types) {
		return getParentIndex().getNames(types).size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of occurrence types
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfOccurrenceTypes() {
		return getParentIndex().getOccurrenceTypes().size();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of occurrences typed by the given topic type
	 * 
	 * @param type
	 *            the type
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(Topic type) {
		return getParentIndex().getOccurrences(type).size();
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
	 * @return a list of all occurrences typed by one of the given types within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(types));
		return HashUtil.secureSubList(list, offset, limit);
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
	 * @return a collection of all occurrences typed by one of the given types within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(types));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of occurrences typed by the given topic types
	 * 
	 * @param types
	 *            the types
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(Collection<? extends Topic> types) {
		return getParentIndex().getOccurrences(types).size();
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		if (!parentIndex.isOpen()) {
			parentIndex.open();
		}
		super.open();
	}

}
