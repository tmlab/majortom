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

/**
 * Implementation of {@link IPagedScopedIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class CachedScopeIndexImpl<T extends ITopicMapStore> extends BaseCachedScopeIndexImpl<T> implements IScopedIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public CachedScopeIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getScope(Arrays.asList(themes));
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<? extends Topic> themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		IScope scope = readScope(themes);
		if (scope == null) {
			scope = doGetScope(themes);
			cacheScope(themes, scope);
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getScopes(Arrays.asList(themes), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getScopes(Arrays.asList(themes), matchAll);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		Collection<IScope> scopes = readScopes(IScopable.class, themes, matchAll);
		if (scopes == null) {
			scopes = doGetScopes(themes, matchAll);
			cacheScopes(IScopable.class, themes, matchAll, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> scopes = readScopes(IAssociation.class);
		if (scopes == null) {
			scopes = doGetAssociationScopes();
			cacheScopes(IAssociation.class, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> themes = readThemes(IAssociation.class);
		if (themes == null) {
			themes = doGetAssociationThemes();
			cacheThemes(IAssociation.class, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> associations = read(IAssociation.class, theme, false);
		if (associations == null) {
			associations = doGetAssociations(theme);
			cache(IAssociation.class, theme, false, associations);
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> associations = read(IAssociation.class, themes, all);
		if (associations == null) {
			associations = doGetAssociations(themes, all);
			cache(IAssociation.class, themes, all, associations);
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> associations = read(IAssociation.class, scope, false);
		if (associations == null) {
			associations = doGetAssociations(scope);
			cache(IAssociation.class, scope, false, associations);
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope... scopes) {
		return getAssociations(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> associations = read(IAssociation.class, scopes, false);
		if (associations == null) {
			associations = doGetAssociations(scopes);
			cache(IAssociation.class, scopes, false, associations);
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(ICharacteristics.class, scope, false);
		if (results == null) {
			results = doGetCharacteristics(scope);
			cache(ICharacteristics.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> col = Arrays.asList(scopes);
		Collection<ICharacteristics> results = read(ICharacteristics.class, col, false);
		if (results == null) {
			results = doGetCharacteristics(col);
			cache(ICharacteristics.class, col, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> scopes = readScopes(IName.class);
		if (scopes == null) {
			scopes = doGetNameScopes();
			cacheScopes(IName.class, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> themes = readThemes(IName.class);
		if (themes == null) {
			themes = doGetNameThemes();
			cacheThemes(IName.class, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> results = read(IName.class, theme, false);
		if (results == null) {
			results = doGetNames(theme);
			cache(IName.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> results = read(IName.class, themes, all);
		if (results == null) {
			results = doGetNames(themes, all);
			cache(IName.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> results = read(IName.class, scope, false);
		if (results == null) {
			results = doGetNames(scope);
			cache(IName.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope... scopes) {
		return getNames(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> results = read(IName.class, scopes, false);
		if (results == null) {
			results = doGetNames(scopes);
			cache(IName.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> scopes = readScopes(IOccurrence.class);
		if (scopes == null) {
			scopes = doGetOccurrenceScopes();
			cacheScopes(IOccurrence.class, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> themes = readThemes(IOccurrence.class);
		if (themes == null) {
			themes = doGetOccurrenceThemes();
			cacheThemes(IOccurrence.class, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> results = read(IOccurrence.class, theme, false);
		if (results == null) {
			results = doGetOccurrences(theme);
			cache(IOccurrence.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> results = read(IOccurrence.class, themes, all);
		if (results == null) {
			results = doGetOccurrences(themes, all);
			cache(IOccurrence.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> results = read(IOccurrence.class, scope, false);
		if (results == null) {
			results = doGetOccurrences(scope);
			cache(IOccurrence.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		return getOccurrences(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> results = read(IOccurrence.class, scopes, false);
		if (results == null) {
			results = doGetOccurrences(scopes);
			cache(IOccurrence.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Scoped> results = read(IScopable.class, scope, false);
		if (results == null) {
			results = doGetScopables(scope);
			cache(IScopable.class, scope, false, results);
		}
		return results;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> col = Arrays.asList(scopes);
		Collection<Scoped> results = read(IScopable.class, col, false);
		if (results == null) {
			results = doGetScopables(col);
			cache(IScopable.class, col, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IScope> scopes = readScopes(IVariant.class);
		if (scopes == null) {
			scopes = doGetVariantScopes();
			cacheScopes(IVariant.class, scopes);
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> themes = readThemes(IVariant.class);
		if (themes == null) {
			themes = doGetVariantThemes();
			cacheThemes(IVariant.class, themes);
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Variant> results = read(IVariant.class, theme, false);
		if (results == null) {
			results = doGetVariants(theme);
			cache(IVariant.class, theme, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Variant> results = read(IVariant.class, themes, all);
		if (results == null) {
			results = doGetVariants(themes, all);
			cache(IVariant.class, themes, all, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Variant> results = read(IVariant.class, scope, false);
		if (results == null) {
			results = doGetVariants(scope);
			cache(IVariant.class, scope, false, results);
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope... scopes) {
		return getVariants(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Variant> results = read(IVariant.class, scopes, false);
		if (results == null) {
			results = doGetVariants(scopes);
			cache(IVariant.class, scopes, false, results);
		}
		return results;
	}

	/**
	 * Returns the scope of the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @return the scope object and never <code>null</code>
	 */
	protected abstract IScope doGetScope(Collection<? extends Topic> themes);

	/**
	 * Returns the scopes of the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @param matchingAll
	 *            flag indicates if the returned scopes has to contain all
	 *            themes
	 * @return the set of scope object and never <code>null</code>
	 */
	protected abstract Collection<IScope> doGetScopes(Collection<? extends Topic> themes, boolean matchingAll);

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * @return a Collection of all constructs scoped by the given scope
	 */
	protected abstract Collection<Scoped> doGetScopables(IScope scope);
	
	/**
	 * Returns all constructs scoped by one of the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * @return a Collection of all constructs scoped by  one of the given scope object.
	 */
	protected abstract Collection<Scoped> doGetScopables(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of an association item. Default
	 * implementation only calls the parent index.
	 * 
	 * 
	 * @return a Collection of scope objects
	 */
	protected abstract Collection<IScope> doGetAssociationScopes();

	/**
	 * Returning all themes contained by at least one association scope. Default
	 * implementation only calls the parent index.
	 * 
	 * @return all themes contained by at least one association scope.
	 */
	protected abstract Collection<Topic> doGetAssociationThemes();

	/**
	 * Returning all associations in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * @return all associations
	 */
	protected abstract Collection<Association> doGetAssociations(Topic theme);

	/**
	 * Returning all associations in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return all associations
	 */
	protected abstract Collection<Association> doGetAssociations(Topic[] themes, boolean all);

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * @return a collection of all association items scoped by the given scope
	 */
	protected abstract Collection<Association> doGetAssociations(IScope scope);

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * @return a Collection of all association items scoped by one of the given
	 *         scopes
	 */
	protected abstract Collection<Association> doGetAssociations(Collection<IScope> scopes);

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * @return a Collection of all characteristics scoped by the given scope
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(IScope scope);

	/**
	 * Returns all characteristics scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * @return a Collection of all characteristics scoped by one of the given
	 *         scope objects.
	 */
	protected abstract Collection<ICharacteristics> doGetCharacteristics(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * @return a Collection of scope objects
	 */
	protected abstract Collection<IScope> doGetOccurrenceScopes();

	/**
	 * Returning all themes contained by at least one occurrence scope.
	 * 
	 * @return all themes contained by at least one occurrence scope.
	 */
	protected abstract Collection<Topic> doGetOccurrenceThemes();

	/**
	 * Returning all occurrences in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * @return all occurrences
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Topic theme);

	/**
	 * Returning all occurrences in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return all occurrences
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Topic[] themes, boolean all);

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * @return all occurrences scoped by the given scope object within the given
	 *         range
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(IScope scope);

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * @return all occurrences scoped by one of the given scope objects
	 */
	protected abstract Collection<Occurrence> doGetOccurrences(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * @return a collection of scope objects
	 */
	protected abstract Collection<IScope> doGetNameScopes();

	/**
	 * Returning all themes contained by at least one name scope.
	 * 
	 * @return all themes contained by at least one name scope.
	 */
	protected abstract Collection<Topic> doGetNameThemes();

	/**
	 * Returning all names in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * @return all names
	 */
	protected abstract Collection<Name> doGetNames(Topic theme);

	/**
	 * Returning all names in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return all names
	 */
	protected abstract Collection<Name> doGetNames(Topic[] themes, boolean all);

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * @return all names scoped by the given scope object
	 */
	protected abstract Collection<Name> doGetNames(IScope scope);

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * @return all names scoped by one of the given scope objects
	 */
	protected abstract Collection<Name> doGetNames(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @return a collection of scope objects
	 */
	protected abstract Collection<IScope> doGetVariantScopes();

	/**
	 * Returning all themes contained by at least one variant scope.
	 * 
	 * @return all themes contained by at least one variant scope.
	 */
	protected abstract Collection<Topic> doGetVariantThemes();

	/**
	 * Returning all variants in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * @return all variants
	 */
	protected abstract Collection<Variant> doGetVariants(Topic theme);

	/**
	 * Returning all variants in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return all variants
	 */
	protected abstract Collection<Variant> doGetVariants(Topic[] themes, boolean all);

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * @return all variants scoped by the given scope object
	 */
	protected abstract Collection<Variant> doGetVariants(IScope scope);

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * @return all variants scoped by one of the given scope objects
	 */
	protected abstract Collection<Variant> doGetVariants(Collection<IScope> scopes);

}
