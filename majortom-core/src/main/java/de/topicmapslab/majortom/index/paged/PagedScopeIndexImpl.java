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
import de.topicmapslab.majortom.model.core.ITopic;
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
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(Collection<IScope> scopes) {
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
			return doGetNumberOfAssociations(scopes);
		}
		long results = readNumberOfConstructs(IAssociation.class, scopes, false);
		if (results == -1) {
			results = doGetNumberOfAssociations(scopes);
			cacheNumberOfConstructs(IAssociation.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(IScope scope) {
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
			return doGetNumberOfAssociations(scope);
		}
		long results = readNumberOfConstructs(IAssociation.class, scope, false);
		if (results == -1) {
			results = doGetNumberOfAssociations(scope);
			cacheNumberOfConstructs(IAssociation.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(Topic theme) {
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
			return doGetNumberOfAssociations(theme);
		}
		long results = readNumberOfConstructs(IAssociation.class, theme, false);
		if (results == -1) {
			results = doGetNumberOfAssociations(theme);
			cacheNumberOfConstructs(IAssociation.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociations(Topic[] themes, boolean all) {
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
			return doGetNumberOfAssociations(themes, all);
		}
		long results = readNumberOfConstructs(IAssociation.class, themes, all);
		if (results == -1) {
			results = doGetNumberOfAssociations(themes, all);
			cacheNumberOfConstructs(IAssociation.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfAssociationScopes();
		}
		long results = readNumberOfConstructs(IAssociation.class, IScope.class, false);
		if (results == -1) {
			results = doGetNumberOfAssociationScopes();
			cacheNumberOfConstructs(IAssociation.class, IScope.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfAssociationThemes();
		}
		long results = readNumberOfConstructs(IAssociation.class, ITopic.class, false);
		if (results == -1) {
			results = doGetNumberOfAssociationThemes();
			cacheNumberOfConstructs(IAssociation.class, ITopic.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Collection<IScope> scopes) {
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
			return doGetNumberOfNames(scopes);
		}
		long results = readNumberOfConstructs(IName.class, scopes, false);
		if (results == -1) {
			results = doGetNumberOfNames(scopes);
			cacheNumberOfConstructs(IName.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(IScope scope) {
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
			return doGetNumberOfNames(scope);
		}
		long results = readNumberOfConstructs(IName.class, scope, false);
		if (results == -1) {
			results = doGetNumberOfNames(scope);
			cacheNumberOfConstructs(IName.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Topic theme) {
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
			return doGetNumberOfNames(theme);
		}
		long results = readNumberOfConstructs(IName.class, theme, false);
		if (results == -1) {
			results = doGetNumberOfNames(theme);
			cacheNumberOfConstructs(IName.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNames(Topic[] themes, boolean all) {
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
			return doGetNumberOfNames(themes, all);
		}
		long results = readNumberOfConstructs(IName.class, themes, all);
		if (results == -1) {
			results = doGetNumberOfNames(themes, all);
			cacheNumberOfConstructs(IName.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfNameScopes();
		}
		long results = readNumberOfConstructs(IName.class, IScope.class, false);
		if (results == -1) {
			results = doGetNumberOfNameScopes();
			cacheNumberOfConstructs(IName.class, IScope.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfNameThemes();
		}
		long results = readNumberOfConstructs(IName.class, ITopic.class, false);
		if (results == -1) {
			results = doGetNumberOfNameThemes();
			cacheNumberOfConstructs(IName.class, ITopic.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Collection<IScope> scopes) {
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
			return doGetNumberOfOccurrences(scopes);
		}
		long results = readNumberOfConstructs(IOccurrence.class, scopes, false);
		if (results == -1) {
			results = doGetNumberOfOccurrences(scopes);
			cacheNumberOfConstructs(IOccurrence.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(IScope scope) {
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
			return doGetNumberOfOccurrences(scope);
		}
		long results = readNumberOfConstructs(IOccurrence.class, scope, false);
		if (results == -1) {
			results = doGetNumberOfOccurrences(scope);
			cacheNumberOfConstructs(IOccurrence.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Topic theme) {
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
			return doGetNumberOfOccurrences(theme);
		}
		long results = readNumberOfConstructs(IOccurrence.class, theme, false);
		if (results == -1) {
			results = doGetNumberOfOccurrences(theme);
			cacheNumberOfConstructs(IOccurrence.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrences(Topic[] themes, boolean all) {
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
			return doGetNumberOfOccurrences(themes, all);
		}
		long results = readNumberOfConstructs(IOccurrence.class, themes, all);
		if (results == -1) {
			results = doGetNumberOfOccurrences(themes, all);
			cacheNumberOfConstructs(IOccurrence.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfOccurrencesScopes();
		}
		long results = readNumberOfConstructs(IOccurrence.class, IScope.class, false);
		if (results == -1) {
			results = doGetNumberOfOccurrencesScopes();
			cacheNumberOfConstructs(IOccurrence.class, IScope.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfOccurrenceThemes();
		}
		long results = readNumberOfConstructs(IOccurrence.class, ITopic.class, false);
		if (results == -1) {
			results = doGetNumberOfOccurrenceThemes();
			cacheNumberOfConstructs(IOccurrence.class, ITopic.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled() || isOnTransactionContext(scope)) {
			return doGetNumberOfScopables(scope);
		}
		long results = readNumberOfConstructs(IScopable.class, scope, false);
		if (results == -1) {
			results = doGetNumberOfScopables(scope);
			cacheNumberOfConstructs(IScopable.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(Collection<IScope> scopes) {
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
			return doGetNumberOfVariants(scopes);
		}
		long results = readNumberOfConstructs(IVariant.class, scopes, false);
		if (results == -1) {
			results = doGetNumberOfVariants(scopes);
			cacheNumberOfConstructs(IVariant.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(IScope scope) {
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
			return doGetNumberOfVariants(scope);
		}
		long results = readNumberOfConstructs(IVariant.class, scope, false);
		if (results == -1) {
			results = doGetNumberOfVariants(scope);
			cacheNumberOfConstructs(IVariant.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(Topic theme) {
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
			return doGetNumberOfVariants(theme);
		}
		long results = readNumberOfConstructs(IVariant.class, theme, false);
		if (results == -1) {
			results = doGetNumberOfVariants(theme);
			cacheNumberOfConstructs(IVariant.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants(Topic[] themes, boolean all) {
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
			return doGetNumberOfVariants(themes, all);
		}
		long results = readNumberOfConstructs(IVariant.class, themes, all);
		if (results == -1) {
			results = doGetNumberOfVariants(themes, all);
			cacheNumberOfConstructs(IVariant.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfVariantScopes();
		}
		long results = readNumberOfConstructs(IVariant.class, IScope.class, false);
		if (results == -1) {
			results = doGetNumberOfVariantScopes();
			cacheNumberOfConstructs(IVariant.class, IScope.class, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetNumberOfVariantThemes();
		}
		long results = readNumberOfConstructs(IVariant.class, ITopic.class, false);
		if (results == -1) {
			results = doGetNumberOfVariantThemes();
			cacheNumberOfConstructs(IVariant.class, ITopic.class, false, results);
		}
		return results;
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
	 * @return a list of all constructs within the given range scoped by the given scope
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
	 * @return a list of all constructs within the given range scoped by the given scope
	 */
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		List<Scoped> list = HashUtil.getList(getParentIndex().getScopables(scope));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an association item. Default implementation only calls the parent
	 * index.
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
	 * Returns all scope objects used as scope of an association item. Default implementation only calls the parent
	 * index.
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
	 * Returning all themes contained by at least one association scope. Default implementation only calls the parent
	 * index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one association scope.
	 */
	protected List<Topic> doGetAssociationThemes(int offset, int limit) {
		List<Topic> list = HashUtil.getList(getParentIndex().getAssociationThemes());
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one association scope. Default implementation only calls the parent
	 * index.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one association scope.
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
	 * @return a list of all association items within the given range scoped by the given scope
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
	 * @return a list of all association items within the given range scoped by the given scope
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
	 * @return a list of all association items within the given range scoped by one of the given scopes
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
	 * @return a list of all association items within the given range scoped by one of the given scopes
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
	 * @return a list of all characteristics within the given range scoped by the given scope
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
	 * @return a list of all characteristics within the given range scoped by the given scope
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
	 * @return all themes within the given range contained by at least one occurrence scope.
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
	 * @return all themes within the given range contained by at least one occurrence scope.
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
	 * @return all occurrences scoped by the given scope object within the given range
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
	 * @return all occurrences scoped by the given scope object within the given range
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
	 * @return all occurrences within the given range scoped by one of the given scope objects
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
	 * @return all occurrences within the given range scoped by one of the given scope objects
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
	 * @return all themes within the given range contained by at least one name scope.
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
	 * @return all themes within the given range contained by at least one name scope.
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
	 * @return all names within the given range scoped by one of the given scope objects
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
	 * @return all themes within the given range contained by at least one variant scope.
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
	 * @return all themes within the given range contained by at least one variant scope.
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
	 * @return all variants within the given range scoped by the given scope object
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
	 * @return all variants within the given range scoped by the given scope object
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
	 * @return all variants within the given range scoped by one of the given scope objects
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
	 * @return all variants within the given range scoped by one of the given scope objects
	 */
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(scopes));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
	}

	/**
	 * Returns the number of associations scoped by the given scopes
	 * 
	 * @param scopes
	 *            the scopes
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(Collection<IScope> scopes) {
		return getParentIndex().getAssociations(scopes).size();
	}

	/**
	 * Returns the number of associations scoped by the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(IScope scope) {
		return getParentIndex().getAssociations(scope).size();
	}

	/**
	 * Returns the number of associations scoped by the given theme
	 * 
	 * @param theme
	 *            the theme
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(Topic theme) {
		return getParentIndex().getAssociations(theme).size();
	}

	/**
	 * Returns the number of associations scoped by the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates full or partial match
	 * @return the number
	 */
	protected long doGetNumberOfAssociations(Topic[] themes, boolean all) {
		return getParentIndex().getAssociations(themes, all).size();
	}

	/**
	 * Returns the number of association scopes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfAssociationScopes() {
		return getParentIndex().getAssociationScopes().size();
	}

	/**
	 * Returns the number of association themes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfAssociationThemes() {
		return getParentIndex().getAssociationThemes().size();
	}

	/**
	 * Returns the number of names scoped by the given scopes
	 * 
	 * @param scopes
	 *            the scopes
	 * @return the number
	 */
	protected long doGetNumberOfNames(Collection<IScope> scopes) {
		return getParentIndex().getNames(scopes).size();
	}

	/**
	 * Returns the number of names scoped by the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the number
	 */
	protected long doGetNumberOfNames(IScope scope) {
		return getParentIndex().getNames(scope).size();
	}

	/**
	 * Returns the number of names scoped by the given theme
	 * 
	 * @param theme
	 *            the theme
	 * @return the number
	 */
	protected long doGetNumberOfNames(Topic theme) {
		return getParentIndex().getNames(theme).size();
	}

	/**
	 * Returns the number of names scoped by the given themes
	 * 
	 * @param themes
	 *            the scopes
	 * @param all
	 *            the flag of partial or full match
	 * @return the number
	 */
	protected long doGetNumberOfNames(Topic[] themes, boolean all) {
		return getParentIndex().getNames(themes, all).size();
	}

	/**
	 * Returns the number of name scopes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfNameScopes() {
		return getParentIndex().getNameScopes().size();
	}

	/**
	 * Returns the number of name themes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfNameThemes() {
		return getParentIndex().getNameThemes().size();
	}

	/**
	 * Returns the number of occurrences scoped by the given scopes
	 * 
	 * @param scopes
	 *            the scopes
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(Collection<IScope> scopes) {
		return getParentIndex().getOccurrences(scopes).size();
	}

	/**
	 * Returns the number of occurrences scoped by the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(IScope scope) {
		return getParentIndex().getOccurrences(scope).size();
	}

	/**
	 * Returns the number of occurrences scoped by the given theme
	 * 
	 * @param theme
	 *            the theme
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(Topic theme) {
		return getParentIndex().getOccurrences(theme).size();
	}

	/**
	 * Returns the number of occurrences scoped by the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            the flag of partial or full match
	 * @return the number
	 */
	protected long doGetNumberOfOccurrences(Topic[] themes, boolean all) {
		return getParentIndex().getOccurrences(themes, all).size();
	}

	/**
	 * 
	 * Returns the number of occurrences scopes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfOccurrencesScopes() {
		return getParentIndex().getOccurrenceScopes().size();
	}

	/**
	 * Returns the number of occurrence themes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfOccurrenceThemes() {
		return getParentIndex().getOccurrenceThemes().size();
	}

	/**
	 * Returns the number of constructs scoped by the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the number
	 */
	protected long doGetNumberOfScopables(IScope scope) {
		return getParentIndex().getScopables(scope).size();
	}

	/**
	 * Returns the number of variants scoped by the given scopes
	 * 
	 * @param scopes
	 *            the scopes
	 * @return the number
	 */
	protected long doGetNumberOfVariants(IScope scope) {
		return getParentIndex().getVariants(scope).size();
	}

	/**
	 * Returns the number of variants scoped by the given theme
	 * 
	 * @param theme
	 *            the theme
	 * @return the number
	 */
	protected long doGetNumberOfVariants(Topic theme) {
		return getParentIndex().getVariants(theme).size();
	}

	/**
	 * Returns the number of variants scoped by the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            the flag of partial or full match
	 * @return the number
	 */
	protected long doGetNumberOfVariants(Topic[] themes, boolean all) {
		return getParentIndex().getVariants(themes, all).size();
	}

	/**
	 * Returns the number of variant scopes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfVariantScopes() {
		return getParentIndex().getVariantScopes().size();
	}

	/**
	 * Returns the number of variants scoped by the given scopes
	 * 
	 * @param scopes
	 *            the scopes
	 * @return the number
	 */
	protected long doGetNumberOfVariants(Collection<IScope> scopes) {
		return getParentIndex().getVariants(scopes).size();
	}

	/**
	 * Returns the number of variant themes
	 * 
	 * @return the number
	 */
	protected long doGetNumberOfVariantThemes() {
		return getParentIndex().getVariantThemes().size();
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
