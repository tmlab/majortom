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
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.index.core.BaseCachedScopeIndexImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedScopedIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedScopedIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedScopeIndexImpl<T extends ITopicMapStore> extends BaseCachedScopeIndexImpl<T> implements IPagedScopedIndex {

	private IScopedIndex parentIndex;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index
	 */
	public PagedScopeIndexImpl(T store, IScopedIndex parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * @return the parentIndex
	 */
	public IScopedIndex getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociationScopes(offset, limit);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IAssociation.class, offset, limit, null);
		if (scopes == null) {
			scopes = doGetAssociationScopes(offset, limit);
			cacheScopes(IAssociation.class, offset, limit, null, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit, Comparator<IScope> comparator) {
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
			return doGetAssociationScopes(offset, limit, comparator);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IAssociation.class, offset, limit, comparator);
		if (scopes == null) {
			scopes = doGetAssociationScopes(offset, limit, comparator);
			cacheScopes(IAssociation.class, offset, limit, comparator, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetAssociationThemes(offset, limit);
		}
		List<Topic> themes = (List<Topic>) readThemes(IAssociation.class, offset, limit, null);
		if (themes == null) {
			themes = doGetAssociationThemes(offset, limit);
			cacheThemes(IAssociation.class, offset, limit, null, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit, Comparator<Topic> comparator) {
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
			return doGetAssociationThemes(offset, limit, comparator);
		}
		List<Topic> themes = (List<Topic>) readThemes(IAssociation.class, offset, limit, comparator);
		if (themes == null) {
			themes = doGetAssociationThemes(offset, limit, comparator);
			cacheThemes(IAssociation.class, offset, limit, comparator, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetAssociations(theme, offset, limit);
		}
		Collection<Association> associations = read(IAssociation.class, theme, false, offset, limit, null);
		if (associations == null) {
			associations = doGetAssociations(theme, offset, limit);
			cache(IAssociation.class, theme, false, offset, limit, null, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetAssociations(theme, offset, limit, comparator);
		}
		Collection<Association> associations = read(IAssociation.class, theme, false, offset, limit, comparator);
		if (associations == null) {
			associations = doGetAssociations(theme, offset, limit, comparator);
			cache(IAssociation.class, theme, false, offset, limit, comparator, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetAssociations(themes, all, offset, limit);
		}
		Collection<Association> associations = read(IAssociation.class, themes, all, offset, limit, null);
		if (associations == null) {
			associations = doGetAssociations(themes, all, offset, limit);
			cache(IAssociation.class, themes, all, offset, limit, null, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetAssociations(themes, all, offset, limit, comparator);
		}
		Collection<Association> associations = read(IAssociation.class, themes, all, offset, limit, comparator);
		if (associations == null) {
			associations = doGetAssociations(themes, all, offset, limit, comparator);
			cache(IAssociation.class, themes, all, offset, limit, comparator, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetAssociations(scope, offset, limit);
		}
		Collection<Association> associations = read(IAssociation.class, scope, false, offset, limit, null);
		if (associations == null) {
			associations = doGetAssociations(scope, offset, limit);
			cache(IAssociation.class, scope, false, offset, limit, null, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetAssociations(scope, offset, limit, comparator);
		}
		Collection<Association> associations = read(IAssociation.class, scope, false, offset, limit, comparator);
		if (associations == null) {
			associations = doGetAssociations(scope, offset, limit, comparator);
			cache(IAssociation.class, scope, false, offset, limit, comparator, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetAssociations(scopes, offset, limit);
		}
		Collection<Association> associations = read(IAssociation.class, scopes, false, offset, limit, null);
		if (associations == null) {
			associations = doGetAssociations(scopes, offset, limit);
			cache(IAssociation.class, scopes, false, offset, limit, null, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetAssociations(scopes, offset, limit, comparator);
		}
		Collection<Association> associations = read(IAssociation.class, scopes, false, offset, limit, comparator);
		if (associations == null) {
			associations = doGetAssociations(scopes, offset, limit, comparator);
			cache(IAssociation.class, scopes, false, offset, limit, comparator, associations);
		}
		return (List<Association>) associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetCharacteristics(scope, offset, limit);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, scope, false, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(scope, offset, limit);
			cache(ICharacteristics.class, scope, false, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetCharacteristics(scope, offset, limit, comparator);
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, scope, false, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(scope, offset, limit, comparator);
			cache(ICharacteristics.class, scope, false, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNameScopes(offset, limit);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IName.class, offset, limit, null);
		if (scopes == null) {
			scopes = doGetNameScopes(offset, limit);
			cacheScopes(IName.class, offset, limit, null, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit, Comparator<IScope> comparator) {
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
			return doGetNameScopes(offset, limit, comparator);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IName.class, offset, limit, comparator);
		if (scopes == null) {
			scopes = doGetNameScopes(offset, limit, comparator);
			cacheScopes(IName.class, offset, limit, comparator, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNameThemes(offset, limit);
		}
		List<Topic> themes = (List<Topic>) readThemes(IName.class, offset, limit, null);
		if (themes == null) {
			themes = doGetNameThemes(offset, limit);
			cacheThemes(IName.class, offset, limit, null, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit, Comparator<Topic> comparator) {
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
			return doGetNameThemes(offset, limit, comparator);
		}
		List<Topic> themes = (List<Topic>) readThemes(IName.class, offset, limit, comparator);
		if (themes == null) {
			themes = doGetNameThemes(offset, limit, comparator);
			cacheThemes(IName.class, offset, limit, comparator, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetNames(theme, offset, limit);
		}
		Collection<Name> results = read(IName.class, theme, false, offset, limit, null);
		if (results == null) {
			results = doGetNames(theme, offset, limit);
			cache(IName.class, theme, false, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetNames(theme, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, theme, false, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(theme, offset, limit, comparator);
			cache(IName.class, theme, false, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetNames(themes, all, offset, limit);
		}
		Collection<Name> results = read(IName.class, themes, all, offset, limit, null);
		if (results == null) {
			results = doGetNames(themes, all, offset, limit);
			cache(IName.class, themes, all, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetNames(themes, all, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, themes, all, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(themes, all, offset, limit, comparator);
			cache(IName.class, themes, all, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetNames(scope, offset, limit);
		}
		Collection<Name> results = read(IName.class, scope, false, offset, limit, null);
		if (results == null) {
			results = doGetNames(scope, offset, limit);
			cache(IName.class, scope, false, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetNames(scope, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, scope, false, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(scope, offset, limit, comparator);
			cache(IName.class, scope, false, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetNames(scopes, offset, limit);
		}
		Collection<Name> results = read(IName.class, scopes, false, offset, limit, null);
		if (results == null) {
			results = doGetNames(scopes, offset, limit);
			cache(IName.class, scopes, false, offset, limit, null, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetNames(scopes, offset, limit, comparator);
		}
		Collection<Name> results = read(IName.class, scopes, false, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(scopes, offset, limit, comparator);
			cache(IName.class, scopes, false, offset, limit, comparator, results);
		}
		return (List<Name>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetOccurrenceScopes(offset, limit);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IOccurrence.class, offset, limit, null);
		if (scopes == null) {
			scopes = doGetOccurrenceScopes(offset, limit);
			cacheScopes(IOccurrence.class, offset, limit, null, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit, Comparator<IScope> comparator) {
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
			return doGetOccurrenceScopes(offset, limit, comparator);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IOccurrence.class, offset, limit, comparator);
		if (scopes == null) {
			scopes = doGetOccurrenceScopes(offset, limit, comparator);
			cacheScopes(IOccurrence.class, offset, limit, comparator, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetOccurrenceThemes(offset, limit);
		}
		List<Topic> themes = (List<Topic>) readThemes(IOccurrence.class, offset, limit, null);
		if (themes == null) {
			themes = doGetOccurrenceThemes(offset, limit);
			cacheThemes(IOccurrence.class, offset, limit, null, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit, Comparator<Topic> comparator) {
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
			return doGetOccurrenceThemes(offset, limit, comparator);
		}
		List<Topic> themes = (List<Topic>) readThemes(IOccurrence.class, offset, limit, comparator);
		if (themes == null) {
			themes = doGetOccurrenceThemes(offset, limit, comparator);
			cacheThemes(IOccurrence.class, offset, limit, comparator, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetOccurrences(theme, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, theme, false, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(theme, offset, limit);
			cache(IOccurrence.class, theme, false, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetOccurrences(theme, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, theme, false, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(theme, offset, limit, comparator);
			cache(IOccurrence.class, theme, false, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetOccurrences(themes, all, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, themes, all, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(themes, all, offset, limit);
			cache(IOccurrence.class, themes, all, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetOccurrences(themes, all, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, themes, all, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(themes, all, offset, limit, comparator);
			cache(IOccurrence.class, themes, all, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetOccurrences(scope, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, scope, false, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(scope, offset, limit);
			cache(IOccurrence.class, scope, false, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetOccurrences(scope, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, scope, false, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(scope, offset, limit, comparator);
			cache(IOccurrence.class, scope, false, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetOccurrences(scopes, offset, limit);
		}
		Collection<Occurrence> results = read(IOccurrence.class, scopes, false, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(scopes, offset, limit);
			cache(IOccurrence.class, scopes, false, offset, limit, null, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetOccurrences(scopes, offset, limit, comparator);
		}
		Collection<Occurrence> results = read(IOccurrence.class, scopes, false, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(scopes, offset, limit, comparator);
			cache(IOccurrence.class, scopes, false, offset, limit, comparator, results);
		}
		return (List<Occurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetScopables(scope, offset, limit);
		}
		Collection<Scoped> results = read(IScopable.class, scope, false, offset, limit, null);
		if (results == null) {
			results = doGetScopables(scope, offset, limit);
			cache(IScopable.class, scope, false, offset, limit, null, results);
		}
		return (List<Scoped>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetScopables(scope, offset, limit, comparator);
		}
		Collection<Scoped> results = read(IScopable.class, scope, false, offset, limit, comparator);
		if (results == null) {
			results = doGetScopables(scope, offset, limit, comparator);
			cache(IScopable.class, scope, false, offset, limit, comparator, results);
		}
		return (List<Scoped>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetVariantScopes(offset, limit);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IVariant.class, offset, limit, null);
		if (scopes == null) {
			scopes = doGetVariantScopes(offset, limit);
			cacheScopes(IVariant.class, offset, limit, null, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit, Comparator<IScope> comparator) {
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
			return doGetVariantScopes(offset, limit, comparator);
		}
		List<IScope> scopes = (List<IScope>) readScopes(IVariant.class, offset, limit, comparator);
		if (scopes == null) {
			scopes = doGetVariantScopes(offset, limit, comparator);
			cacheScopes(IVariant.class, offset, limit, comparator, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetVariantThemes(offset, limit);
		}
		List<Topic> themes = (List<Topic>) readThemes(IVariant.class, offset, limit, null);
		if (themes == null) {
			themes = doGetVariantThemes(offset, limit);
			cacheThemes(IVariant.class, offset, limit, null, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit, Comparator<Topic> comparator) {
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
			return doGetVariantThemes(offset, limit, comparator);
		}
		List<Topic> themes = (List<Topic>) readThemes(IVariant.class, offset, limit, comparator);
		if (themes == null) {
			themes = doGetVariantThemes(offset, limit, comparator);
			cacheThemes(IVariant.class, offset, limit, comparator, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetVariants(theme, offset, limit);
		}
		Collection<Variant> results = read(IVariant.class, theme, false, offset, limit, null);
		if (results == null) {
			results = doGetVariants(theme, offset, limit);
			cache(IVariant.class, theme, false, offset, limit, null, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(theme)) {
			return doGetVariants(theme, offset, limit, comparator);
		}
		Collection<Variant> results = read(IVariant.class, theme, false, offset, limit, comparator);
		if (results == null) {
			results = doGetVariants(theme, offset, limit, comparator);
			cache(IVariant.class, theme, false, offset, limit, comparator, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetVariants(themes, all, offset, limit);
		}
		Collection<Variant> results = read(IVariant.class, themes, all, offset, limit, null);
		if (results == null) {
			results = doGetVariants(themes, all, offset, limit);
			cache(IVariant.class, themes, all, offset, limit, null, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(themes)) {
			return doGetVariants(themes, all, offset, limit, comparator);
		}
		Collection<Variant> results = read(IVariant.class, themes, all, offset, limit, comparator);
		if (results == null) {
			results = doGetVariants(themes, all, offset, limit, comparator);
			cache(IVariant.class, themes, all, offset, limit, comparator, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetVariants(scope, offset, limit);
		}
		Collection<Variant> results = read(IVariant.class, scope, false, offset, limit, null);
		if (results == null) {
			results = doGetVariants(scope, offset, limit);
			cache(IVariant.class, scope, false, offset, limit, null, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetVariants(scope, offset, limit, comparator);
		}
		Collection<Variant> results = read(IVariant.class, scope, false, offset, limit, comparator);
		if (results == null) {
			results = doGetVariants(scope, offset, limit, comparator);
			cache(IVariant.class, scope, false, offset, limit, comparator, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetVariants(scopes, offset, limit);
		}
		Collection<Variant> results = read(IVariant.class, scopes, false, offset, limit, null);
		if (results == null) {
			results = doGetVariants(scopes, offset, limit);
			cache(IVariant.class, scopes, false, offset, limit, null, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scopes)) {
			return doGetVariants(scopes, offset, limit, comparator);
		}
		Collection<Variant> results = read(IVariant.class, scopes, false, offset, limit, comparator);
		if (results == null) {
			results = doGetVariants(scopes, offset, limit, comparator);
			cache(IVariant.class, scopes, false, offset, limit, comparator, results);
		}
		return (List<Variant>) results;
	}

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all constructs within the given range scoped by the
	 *         given scope
	 */
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit) {
		List<Scoped> list = HashUtil.getList(getParentIndex().getScopables(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all constructs within the given range scoped by the
	 *         given scope
	 */
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		List<Scoped> list = HashUtil.getList(getParentIndex().getScopables(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an association item. Default
	 * implementation only calls the parent index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of scope objects within the given range
	 */
	protected List<IScope> doGetAssociationScopes(int offset, int limit) {
		List<IScope> list = HashUtil.getList(getParentIndex().getAssociationScopes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an association item. Default
	 * implementation only calls the parent index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of scope objects within the given range
	 */
	protected List<IScope> doGetAssociationScopes(int offset, int limit, Comparator<IScope> comparator) {
		List<IScope> list = HashUtil.getList(getParentIndex().getAssociationScopes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one association scope. Default
	 * implementation only calls the parent index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         association scope.
	 */
	protected List<Topic> doGetAssociationThemes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getAssociationThemes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one association scope. Default
	 * implementation only calls the parent index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one
	 *         association scope.
	 */
	protected List<Topic> doGetAssociationThemes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getAssociationThemes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all associations in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic theme, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(theme));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all associations in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic theme, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(theme));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all associations in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(themes, all));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all associations in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(themes, all));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items within the given range scoped by
	 *         the given scope
	 */
	protected List<Association> doGetAssociations(IScope scope, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association items within the given range scoped by
	 *         the given scope
	 */
	protected List<Association> doGetAssociations(IScope scope, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items within the given range scoped by
	 *         one of the given scopes
	 */
	protected List<Association> doGetAssociations(Collection<IScope> scopes, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(scopes));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association items within the given range scoped by
	 *         one of the given scopes
	 */
	protected List<Association> doGetAssociations(Collection<IScope> scopes, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(scopes));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all characteristics within the given range scoped by
	 *         the given scope
	 */
	protected List<ICharacteristics> doGetCharacteristics(IScope scope, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all characteristics within the given range scoped by
	 *         the given scope
	 */
	protected List<ICharacteristics> doGetCharacteristics(IScope scope, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of scope objects within the given range
	 */
	protected List<IScope> doGetOccurrenceScopes(int offset, int limit) {
		List<IScope> list = HashUtil.getList(getParentIndex().getOccurrenceScopes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of scope objects within the given range
	 */
	protected List<IScope> doGetOccurrenceScopes(int offset, int limit, Comparator<IScope> comparator) {
		List<IScope> list = HashUtil.getList(getParentIndex().getOccurrenceScopes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one occurrence scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         occurrence scope.
	 */
	protected List<Topic> doGetOccurrenceThemes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getOccurrenceThemes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one occurrence scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one
	 *         occurrence scope.
	 */
	protected List<Topic> doGetOccurrenceThemes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getOccurrenceThemes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all occurrences in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic theme, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(theme));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all occurrences in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic theme, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(theme));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all occurrences in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(themes, all));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all occurrences in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(themes, all));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences scoped by the given scope object within the given
	 *         range
	 */
	protected List<Occurrence> doGetOccurrences(IScope scope, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences scoped by the given scope object within the given
	 *         range
	 */
	protected List<Occurrence> doGetOccurrences(IScope scope, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range scoped by one of the given
	 *         scope objects
	 */
	protected List<Occurrence> doGetOccurrences(Collection<IScope> scopes, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(scopes));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range scoped by one of the given
	 *         scope objects
	 */
	protected List<Occurrence> doGetOccurrences(Collection<IScope> scopes, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(scopes));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of scope objects within the given range
	 */
	protected List<IScope> doGetNameScopes(int offset, int limit) {
		List<IScope> list = HashUtil.getList(getParentIndex().getNameScopes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of scope objects within the given range
	 */
	protected List<IScope> doGetNameScopes(int offset, int limit, Comparator<IScope> comparator) {
		List<IScope> list = HashUtil.getList(getParentIndex().getNameScopes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one name scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one name
	 *         scope.
	 */
	protected List<Topic> doGetNameThemes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getNameThemes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one name scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one name
	 *         scope.
	 */
	protected List<Topic> doGetNameThemes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getNameThemes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all names in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range
	 */
	protected List<Name> doGetNames(Topic theme, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(theme));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all names in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range
	 */
	protected List<Name> doGetNames(Topic theme, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(theme));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all names in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(themes, all));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all names in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(themes, all));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range scoped by the given scope object
	 */
	protected List<Name> doGetNames(IScope scope, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range scoped by the given scope object
	 */
	protected List<Name> doGetNames(IScope scope, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names scoped by one of the given scope objects
	 */
	protected List<Name> doGetNames(Collection<IScope> scopes, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(scopes));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range scoped by one of the given scope
	 *         objects
	 */
	protected List<Name> doGetNames(Collection<IScope> scopes, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(scopes));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a collection of scope objects within the given range
	 */
	protected List<IScope> doGetVariantScopes(int offset, int limit) {
		List<IScope> list = HashUtil.getList(getParentIndex().getVariantScopes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a collection of scope objects within the given range
	 */
	protected List<IScope> doGetVariantScopes(int offset, int limit, Comparator<IScope> comparator) {
		List<IScope> list = HashUtil.getList(getParentIndex().getVariantScopes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one variant scope.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         variant scope.
	 */
	protected List<Topic> doGetVariantThemes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getVariantThemes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one variant scope.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return all themes within the given range contained by at least one
	 *         variant scope.
	 */
	protected List<Topic> doGetVariantThemes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> list = HashUtil.getList(getParentIndex().getVariantThemes());
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all variants in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic theme, int offset, int limit) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(theme));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all variants in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic theme, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(theme));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all variants in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(themes, all));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all variants in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(themes, all));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range scoped by the given scope
	 *         object
	 */
	protected List<Variant> doGetVariants(IScope scope, int offset, int limit) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(scope));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range scoped by the given scope
	 *         object
	 */
	protected List<Variant> doGetVariants(IScope scope, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range scoped by one of the given
	 *         scope objects
	 */
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(scopes));
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range scoped by one of the given
	 *         scope objects
	 */
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(scopes));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		if (!parentIndex.isOpen()) {
			parentIndex.open();
		}
	}

}
