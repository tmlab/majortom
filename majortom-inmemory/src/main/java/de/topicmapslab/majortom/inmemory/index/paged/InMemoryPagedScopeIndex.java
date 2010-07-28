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
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedScopedIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedScopedIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedScopeIndex extends InMemoryPagedIndex<IScopedIndex> implements IPagedScopedIndex {

	/**
	 * enumeration of cache keys
	 */
	enum Param {
		SCOPED,

		ASSOCIATION,

		ASSOCIATION_ALL,

		OCCURRENCE,

		OCCURRENCE_ALL,

		NAME,

		NAME_ALL,

		VARIANT,

		VARIANT_ALL,

		CHARACTERISTICS
	}

	/**
	 * Cache containing the scopes of specific constructs
	 */
	private Map<Param, List<IScope>> cachedScopes;
	/**
	 * Cache containing the sorted scopes of specific constructs
	 */
	private Map<Param, Map<Comparator<IScope>, List<IScope>>> cachedComparedScopes;
	/**
	 * Cache containing the themes of specific constructs
	 */
	private Map<Param, List<Topic>> cachedThemes;
	/**
	 * Cache containing the sorted themes of specific constructs
	 */
	private Map<Param, Map<Comparator<Topic>, List<Topic>>> cachedComparedThemes;
	/**
	 * Cache containing all constructs valid in a scope containing a theme
	 */
	private Map<Param, Map<Topic, List<? extends Construct>>> cachedConstructsSingleTheme;
	/**
	 * Cache containing all sorted constructs valid in a scope containing a theme
	 */
	private Map<Param, Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsSingleTheme;
	/**
	 * Cache containing all constructs valid in a scope containing at least or all themes
	 */
	private Map<Param, Map<Topic[], List<? extends Construct>>> cachedConstructsMultipleThemes;
	/**
	 * Cache containing all sorted constructs valid in a scope containing at least or all themes
	 */
	private Map<Param, Map<Topic[], Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsMultipleThemes;
	/**
	 * Cache containing all constructs valid in the scope
	 */
	private Map<Param, Map<IScope, List<? extends Construct>>> cachedConstructsSingleScope;
	/**
	 * Cache containing all sorted constructs valid in the scope
	 */
	private Map<Param, Map<IScope, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsSingleScope;
	/**
	 * Cache containing all constructs valid in one of the scopes
	 */
	private Map<Param, Map<Collection<IScope>, List<? extends Construct>>> cachedConstructsMultipleScopes;
	/**
	 * Cache containing all sorted constructs valid in one of the scopes
	 */
	private Map<Param, Map<Collection<IScope>, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsMultipleScopes;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index
	 */
	public InMemoryPagedScopeIndex(InMemoryTopicMapStore store, IScopedIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.ASSOCIATION, "getAssociationScopes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.ASSOCIATION, "getAssociationScopes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.ASSOCIATION, "getAssociationThemes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.ASSOCIATION, "getAssociationThemes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", theme, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", themes, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", scopes, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ASSOCIATION, "getAssociations", scopes, offset, limit, comparator);
	}
		
	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.CHARACTERISTICS, "getCharacteristics", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.CHARACTERISTICS, "getCharacteristics", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.NAME, "getNameScopes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.NAME, "getNameScopes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.NAME, "getNameThemes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.NAME, "getNameThemes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", theme, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", themes, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", scopes, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.NAME, "getNames", scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.OCCURRENCE, "getOccurrenceScopes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.OCCURRENCE, "getOccurrenceScopes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.OCCURRENCE, "getOccurrenceThemes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.OCCURRENCE, "getOccurrenceThemes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", theme, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", themes, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", scopes, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.OCCURRENCE, "getOccurrences", scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.SCOPED, "getScopables", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.SCOPED, "getScopables", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.VARIANT, "getVariantScopes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getScopes(Param.VARIANT, "getVariantScopes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.VARIANT, "getVariantThemes", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getThemes(Param.VARIANT, "getVariantThemes", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", theme, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", themes, all, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", scope, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", scopes, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.VARIANT, "getVariants", scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * construct was removed -> clear dependent caches
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED) {
			clearVariantCache();
		} else if (event == TopicMapEventType.NAME_REMOVED) {
			clearNameCache();
		} else if (event == TopicMapEventType.OCCURRENCE_REMOVED) {
			clearOccurrenceCache();
		} else if (event == TopicMapEventType.ASSOCIATION_REMOVED) {
			clearAssociationCache();
		}
		/*
		 * scope was modified -> clear dependent caches
		 */
		else if (event == TopicMapEventType.SCOPE_MODIFIED) {
			if (notifier instanceof Association) {
				clearAssociationCache();
			} else if (notifier instanceof Name) {
				clearNameCache();
			} else if (notifier instanceof Occurrence) {
				clearOccurrenceCache();
			} else if (notifier instanceof Variant) {
				clearVariantCache();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		super.close();
	}

	/**
	 * Clear all caches
	 */
	private final void clearCache() {
		if (cachedComparedConstructsMultipleScopes != null) {
			cachedComparedConstructsMultipleScopes.clear();
		}

		if (cachedConstructsMultipleScopes != null) {
			cachedConstructsMultipleScopes.clear();
		}

		if (cachedComparedConstructsMultipleThemes != null) {
			cachedComparedConstructsMultipleThemes.clear();
		}

		if (cachedConstructsMultipleThemes != null) {
			cachedConstructsMultipleThemes.clear();
		}

		if (cachedComparedConstructsSingleScope != null) {
			cachedComparedConstructsSingleScope.clear();
		}

		if (cachedConstructsSingleScope != null) {
			cachedConstructsSingleScope.clear();
		}

		if (cachedComparedConstructsSingleTheme != null) {
			cachedComparedConstructsSingleTheme.clear();
		}

		if (cachedConstructsSingleTheme != null) {
			cachedConstructsSingleTheme.clear();
		}
		if (cachedComparedScopes != null) {
			cachedComparedScopes.clear();
		}
		if (cachedScopes != null) {
			cachedScopes.clear();
		}

		if (cachedComparedThemes != null) {
			cachedComparedThemes.clear();
		}

		if (cachedThemes != null) {
			cachedThemes.clear();
		}
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearAssociationCache() {
		clearDependenCache(Param.ASSOCIATION);
		clearDependenCache(Param.ASSOCIATION_ALL);
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearNameCache() {
		clearDependenCache(Param.NAME);
		clearDependenCache(Param.NAME_ALL);
		clearDependenCache(Param.CHARACTERISTICS);
		clearDependenCache(Param.VARIANT);
		clearDependenCache(Param.VARIANT_ALL);
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearVariantCache() {
		clearDependenCache(Param.VARIANT);
		clearDependenCache(Param.VARIANT_ALL);
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearOccurrenceCache() {
		clearDependenCache(Param.OCCURRENCE);
		clearDependenCache(Param.OCCURRENCE_ALL);
		clearDependenCache(Param.CHARACTERISTICS);
	}

	/**
	 * Clear all dependent caches of the given type
	 * 
	 * @param param
	 *            the type
	 */
	private final void clearDependenCache(Param param) {
		if (cachedComparedConstructsMultipleScopes != null) {
			cachedComparedConstructsMultipleScopes.remove(param);
		}

		if (cachedConstructsMultipleScopes != null) {
			cachedConstructsMultipleScopes.remove(param);
		}

		if (cachedComparedConstructsMultipleThemes != null) {
			cachedComparedConstructsMultipleThemes.remove(param);
		}

		if (cachedConstructsMultipleThemes != null) {
			cachedConstructsMultipleThemes.remove(param);
		}

		if (cachedComparedConstructsSingleScope != null) {
			cachedComparedConstructsSingleScope.remove(param);
		}

		if (cachedConstructsSingleScope != null) {
			cachedConstructsSingleScope.remove(param);
		}

		if (cachedComparedConstructsSingleTheme != null) {
			cachedComparedConstructsSingleTheme.remove(param);
		}

		if (cachedConstructsSingleTheme != null) {
			cachedConstructsSingleTheme.remove(param);
		}
		if (cachedComparedScopes != null) {
			cachedComparedScopes.remove(param);
		}
		if (cachedScopes != null) {
			cachedScopes.remove(param);
		}

		if (cachedComparedThemes != null) {
			cachedComparedThemes.remove(param);
		}

		if (cachedThemes != null) {
			cachedThemes.remove(param);
		}
	}

	/**
	 * Internal method to read the scopes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the scopes within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<IScope> getScopes(Param param, String methodName, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedScopes == null) {
			cachedScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached scopes of the specific type
		 */
		List<IScope> list = cachedScopes.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<IScope>) method.invoke(getParentIndex()));
				cachedScopes.put(param, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the scopes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the scopes within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<IScope> getScopes(Param param, String methodName, int offset, int limit, Comparator<IScope> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedScopes == null) {
			cachedComparedScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted scopes
		 */
		Map<Comparator<IScope>, List<IScope>> map = cachedComparedScopes.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedComparedScopes.put(param, map);
		}
		/*
		 * get cached scopes of the specific type
		 */
		List<IScope> list = map.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<IScope>) method.invoke(getParentIndex()));
				/*
				 * sort list and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the themes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the themes within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getThemes(Param param, String methodName, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedThemes == null) {
			cachedThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached themes of the specific type
		 */
		List<Topic> list = cachedThemes.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex()));
				cachedThemes.put(param, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the themes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the themes within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Topic> getThemes(Param param, String methodName, int offset, int limit, Comparator<Topic> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedThemes == null) {
			cachedComparedThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted themes of the specific type
		 */
		Map<Comparator<Topic>, List<Topic>> map = cachedComparedThemes.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedComparedThemes.put(param, map);
		}
		/*
		 * get cached sorted themes by type
		 */
		List<Topic> list = map.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Topic>) method.invoke(getParentIndex()));
				/*
				 * sort list and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param theme
	 *            the theme
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Topic theme, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedConstructsSingleTheme == null) {
			cachedConstructsSingleTheme = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of theme-dependent constructs by type
		 */
		Map<Topic, List<? extends Construct>> map = cachedConstructsSingleTheme.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsSingleTheme.put(param, map);
		}
		/*
		 * get cached theme-dependent constructs by theme
		 */
		List<T> list = (List<T>) map.get(theme);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), theme));
				map.put(theme, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param theme
	 *            the theme
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Topic theme, int offset, int limit, Comparator<T> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructsSingleTheme == null) {
			cachedComparedConstructsSingleTheme = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted theme-dependent constructs by type
		 */
		Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsSingleTheme.get(param);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructsSingleTheme.put(param, cached);
		}
		/*
		 * get map of cached sorted constructs by theme
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(theme);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cached.put(theme, map);
		}
		/*
		 * get cached sorted constructs by comparator
		 */
		List<T> list = (List<T>) map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), theme));
				/*
				 * sort and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in the given scope of the
	 * specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param scope
	 *            the scope
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, IScope scope, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedConstructsSingleScope == null) {
			cachedConstructsSingleScope = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached scope-dependent constructs  by type
		 */
		Map<IScope, List<? extends Construct>> map = cachedConstructsSingleScope.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsSingleScope.put(param, map);
		}
		/*
		 * get cached scope-dependent constructs by scope
		 */
		List<T> list = (List<T>) map.get(scope);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, IScope.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), scope));
				map.put(scope, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in the given scope of the
	 * specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param scope
	 *            the scope
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, IScope scope, int offset, int limit, Comparator<T> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructsSingleScope == null) {
			cachedComparedConstructsSingleScope = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted scope-dependent constructs by type
		 */
		Map<IScope, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsSingleScope.get(param);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructsSingleScope.put(param, cached);
		}
		/*
		 * get map of sorted constructs by scope
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(scope);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cached.put(scope, map);
		}
		/*
		 * get cached sorted constructs by comparator
		 */
		List<T> list = (List<T>) map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, IScope.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), scope));
				/*
				 * sort and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in one of the given scopes
	 * of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param scopes
	 *            the scopes
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Collection<IScope> scopes, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedConstructsMultipleScopes == null) {
			cachedConstructsMultipleScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached scope-dependent constructs by type
		 */
		Map<Collection<IScope>, List<? extends Construct>> map = cachedConstructsMultipleScopes.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsMultipleScopes.put(param, map);
		}
		/*
		 * get cached scope-dependent by scopes
		 */
		List<T> list = (List<T>) map.get(scopes);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Collection.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), scopes));
				map.put(scopes, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in one of the given scope of
	 * the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param scopes
	 *            the scopes
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Collection<IScope> scopes, int offset, int limit,
			Comparator<T> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructsMultipleScopes == null) {
			cachedComparedConstructsMultipleScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted scope-dependent constructs by type
		 */
		Map<Collection<IScope>, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsMultipleScopes.get(param);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructsMultipleScopes.put(param, cached);
		}
		/*
		 * get map of cached sorted constructs by scopes
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(scopes);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cached.put(scopes, map);
		}
		/*
		 * get cached scope-dependent by comparator
		 */
		List<T> list = (List<T>) map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Collection.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), scopes));
				/*
				 * sort and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in one or all of the given
	 * themes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Topic[] themes, boolean all, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedConstructsMultipleThemes == null) {
			cachedConstructsMultipleThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached themes-dependent constructs by type
		 */
		Map<Topic[], List<? extends Construct>> map = cachedConstructsMultipleThemes.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsMultipleThemes.put(param, map);
		}
		/*
		 * get cached constructs by themes
		 */
		List<T> list = (List<T>) map.get(themes);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic[].class, boolean.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), themes, all));
				map.put(themes, list);
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
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read the constructs valid in one or all of the given
	 * themes of the specified construct type.
	 * 
	 * @param param
	 *            the construct type
	 * @param methodName
	 *            the method name to call if list is missing
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator
	 * @return the constructs within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Topic[] themes, boolean all, int offset, int limit,
			Comparator<T> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructsMultipleThemes == null) {
			cachedComparedConstructsMultipleThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached sorted themes-dependent constructs by type
		 */
		Map<Topic[], Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsMultipleThemes.get(param);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructsMultipleThemes.put(param, cached);
		}
		/*
		 * get map of cached sorted constructs by themes
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(themes);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cached.put(themes, map);
		}
		/*
		 * get cached constructs by comparator
		 */
		List<T> list = (List<T>) map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get scopes
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Topic[].class, boolean.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), themes, all));
				/*
				 * sort and store it
				 */
				Collections.sort(list, comparator);
				map.put(comparator, list);
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
		return secureSubList(list, offset, limit);
	}

}
