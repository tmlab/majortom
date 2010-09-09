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
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
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
public abstract class PagedScopeIndexImpl<T extends ITopicMapStore> extends PagedIndexImpl<T, IScopedIndex> implements IPagedScopedIndex {

	/**
	 * enumeration of cache keys
	 */
	public enum Param {
		SCOPED,

		ASSOCIATION,

		OCCURRENCE,

		NAME,

		VARIANT,

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
	 * Cache containing all sorted constructs valid in a scope containing a
	 * theme
	 */
	private Map<Param, Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructsSingleTheme;
	/**
	 * Cache containing all constructs valid in a scope containing at least or
	 * all themes
	 */
	private Map<Param, Map<Topic[], List<? extends Construct>>> cachedConstructsMultipleThemes;
	/**
	 * Cache containing all sorted constructs valid in a scope containing at
	 * least or all themes
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
	public PagedScopeIndexImpl(T store, IScopedIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.ASSOCIATION);
		if (cache == null) {
			return doGetAssociationScopes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getAssociationScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.ASSOCIATION, comparator);
		if (cache == null) {
			return doGetAssociationScopes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.ASSOCIATION);
		if (cache == null) {
			return doGetAssociationThemes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getAssociationThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.ASSOCIATION, comparator);
		if (cache == null) {
			return doGetAssociationThemes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, theme);
		if (cache == null) {
			return doGetAssociations(theme, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, theme, comparator);
		if (cache == null) {
			return doGetAssociations(theme, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, themes, all);
		if (cache == null) {
			return doGetAssociations(themes, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, themes, all, comparator);
		if (cache == null) {
			return doGetAssociations(themes, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, scope);
		if (cache == null) {
			return doGetAssociations(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, scope, comparator);
		if (cache == null) {
			return doGetAssociations(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, scopes);
		if (cache == null) {
			return doGetAssociations(scopes, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Association> getAssociations(Collection<IScope> scopes, int offset, int limit, Comparator<Association> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Association> cache = read(Param.ASSOCIATION, scopes, comparator);
		if (cache == null) {
			return doGetAssociations(scopes, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.CHARACTERISTICS, scope);
		if (cache == null) {
			return doGetCharacteristics(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.CHARACTERISTICS, scope, comparator);
		if (cache == null) {
			return doGetCharacteristics(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.NAME);
		if (cache == null) {
			return doGetNameScopes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getNameScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.NAME, comparator);
		if (cache == null) {
			return doGetNameScopes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.NAME);
		if (cache == null) {
			return doGetNameThemes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getNameThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.NAME, comparator);
		if (cache == null) {
			return doGetNameThemes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, theme);
		if (cache == null) {
			return doGetNames(theme, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic theme, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, theme, comparator);
		if (cache == null) {
			return doGetNames(theme, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, themes, all);
		if (cache == null) {
			return doGetNames(themes, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, themes, all, comparator);
		if (cache == null) {
			return doGetNames(themes, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, scope);
		if (cache == null) {
			return doGetNames(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(IScope scope, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, scope, comparator);
		if (cache == null) {
			return doGetNames(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, scopes);
		if (cache == null) {
			return doGetNames(scopes, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit, Comparator<Name> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Name> cache = read(Param.NAME, scopes, comparator);
		if (cache == null) {
			return doGetNames(scopes, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.OCCURRENCE);
		if (cache == null) {
			return doGetOccurrenceScopes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.OCCURRENCE, comparator);
		if (cache == null) {
			return doGetOccurrenceScopes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.OCCURRENCE);
		if (cache == null) {
			return doGetOccurrenceThemes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.OCCURRENCE, comparator);
		if (cache == null) {
			return doGetOccurrenceThemes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, theme);
		if (cache == null) {
			return doGetOccurrences(theme, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, theme, comparator);
		if (cache == null) {
			return doGetOccurrences(theme, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, themes, all);
		if (cache == null) {
			return doGetOccurrences(themes, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, themes, all, comparator);
		if (cache == null) {
			return doGetOccurrences(themes, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, scope);
		if (cache == null) {
			return doGetOccurrences(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, scope, comparator);
		if (cache == null) {
			return doGetOccurrences(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, scopes);
		if (cache == null) {
			return doGetOccurrences(scopes, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes, int offset, int limit, Comparator<Occurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Occurrence> cache = read(Param.OCCURRENCE, scopes, comparator);
		if (cache == null) {
			return doGetOccurrences(scopes, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Scoped> cache = read(Param.SCOPED, scope);
		if (cache == null) {
			return doGetScopables(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Scoped> cache = read(Param.SCOPED, scope, comparator);
		if (cache == null) {
			return doGetScopables(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.VARIANT);
		if (cache == null) {
			return doGetVariantScopes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IScope> getVariantScopes(int offset, int limit, Comparator<IScope> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IScope> cache = readScopes(Param.VARIANT, comparator);
		if (cache == null) {
			return doGetVariantScopes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.VARIANT);
		if (cache == null) {
			return doGetVariantThemes(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getVariantThemes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = readThemes(Param.VARIANT, comparator);
		if (cache == null) {
			return doGetVariantThemes(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, theme);
		if (cache == null) {
			return doGetVariants(theme, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, theme, comparator);
		if (cache == null) {
			return doGetVariants(theme, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, themes, all);
		if (cache == null) {
			return doGetVariants(themes, all, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, themes, all, comparator);
		if (cache == null) {
			return doGetVariants(themes, all, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, scope);
		if (cache == null) {
			return doGetVariants(scope, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, scope, comparator);
		if (cache == null) {
			return doGetVariants(scope, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, scopes);
		if (cache == null) {
			return doGetVariants(scopes, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Variant> cache = read(Param.VARIANT, scopes, comparator);
		if (cache == null) {
			return doGetVariants(scopes, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
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
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearNameCache() {
		clearDependenCache(Param.NAME);
		clearDependenCache(Param.CHARACTERISTICS);
		clearDependenCache(Param.VARIANT);
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearVariantCache() {
		clearDependenCache(Param.VARIANT);
	}

	/**
	 * Clear all caches depending on associations
	 */
	private final void clearOccurrenceCache() {
		clearDependenCache(Param.OCCURRENCE);
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
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	private final List<IScope> readScopes(Param param) {
		/*
		 * check main cache
		 */
		if (cachedScopes == null) {
			return null;
		}
		/*
		 * get cached scopes of the specific type
		 */
		return cachedScopes.get(param);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param valus
	 *            the values to store
	 */
	protected final void storeScopes(Param param, List<IScope> values) {
		/*
		 * initialize cache
		 */
		if (cachedScopes == null) {
			cachedScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * store scopes of the specific type
		 */
		cachedScopes.put(param, values);
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param comparator
	 *            the comparator
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	private final List<IScope> readScopes(Param param, Comparator<IScope> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedScopes == null) {
			return null;
		}
		/*
		 * get map of cached sorted scopes
		 */
		Map<Comparator<IScope>, List<IScope>> map = cachedComparedScopes.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached scopes of the specific type
		 */
		return map.get(param);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param comparator
	 *            the comparator
	 * @param valus
	 *            the values to store
	 */
	protected final void storeScopes(Param param, Comparator<IScope> comparator, List<IScope> values) {
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
		 * store scopes of the specific type
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read the themes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @return the themes or <code>null</code> if the key is unknown
	 */
	private final List<Topic> readThemes(Param param) {
		/*
		 * check main cache
		 */
		if (cachedThemes == null) {
			return null;
		}
		/*
		 * get cached themes of the specific type
		 */
		return cachedThemes.get(param);
	}

	/**
	 * Internal method to add the themes of the specified construct type to
	 * internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param valus
	 *            the values to store
	 */
	protected final void storeThemes(Param param, List<Topic> values) {
		/*
		 * initialize cache
		 */
		if (cachedThemes == null) {
			cachedThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * store themes of the specific type
		 */
		cachedThemes.put(param, values);
	}

	/**
	 * Internal method to read the themes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param comparator
	 *            the comparator
	 * @return the themes or <code>null</code> if the key is unknown
	 */
	private final List<Topic> readThemes(Param param, Comparator<Topic> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedThemes == null) {
			return null;
		}
		/*
		 * get map of cached sorted themes of the specific type
		 */
		Map<Comparator<Topic>, List<Topic>> map = cachedComparedThemes.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached sorted themes by type
		 */
		return map.get(param);
	}

	/**
	 * Internal add to read the themes of the specified construct type to
	 * internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param comparator
	 *            the comparator
	 * @param valus
	 *            the values to store
	 */
	protected final void storeThemes(Param param, Comparator<Topic> comparator, List<Topic> values) {
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
		 * store sorted themes by type
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param theme
	 *            the theme
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Topic theme) {
		/*
		 * check main cache
		 */
		if (cachedConstructsSingleTheme == null) {
			return null;
		}
		/*
		 * get map of theme-dependent constructs by type
		 */
		Map<Topic, List<? extends Construct>> map = cachedConstructsSingleTheme.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached theme-dependent constructs by theme
		 */
		return (List<X>) map.get(theme);
	}

	/**
	 * Internal method to add the constructs valid in the given theme of the
	 * specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param theme
	 *            the theme
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Topic theme, List<X> values) {
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
		 * store theme-dependent constructs by theme
		 */
		map.put(theme, values);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param theme
	 *            the theme
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Topic theme, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructsSingleTheme == null) {
			return null;
		}
		/*
		 * get map of cached sorted theme-dependent constructs by type
		 */
		Map<Topic, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsSingleTheme.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get map of cached sorted constructs by theme
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(theme);
		if (map == null) {
			return null;
		}
		/*
		 * get cached sorted constructs by comparator
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to add the constructs valid in the given theme of the
	 * specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param theme
	 *            the theme
	 * @param comparator
	 *            the comparator
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Topic theme, Comparator<X> comparator, List<X> values) {
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
		 * store sorted constructs by comparator
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read the constructs valid in the given scope of the
	 * specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param scope
	 *            the scope
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, IScope scope) {
		/*
		 * check main cache
		 */
		if (cachedConstructsSingleScope == null) {
			return null;
		}
		/*
		 * get map of cached scope-dependent constructs by type
		 */
		Map<IScope, List<? extends Construct>> map = cachedConstructsSingleScope.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached scope-dependent constructs by scope
		 */
		return (List<X>) map.get(scope);
	}

	/**
	 * Internal method to add the constructs valid in the given scope of the
	 * specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param scope
	 *            the scope
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, IScope scope, List<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructsSingleScope == null) {
			cachedConstructsSingleScope = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached scope-dependent constructs by type
		 */
		Map<IScope, List<? extends Construct>> map = cachedConstructsSingleScope.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructsSingleScope.put(param, map);
		}
		/*
		 * store scope-dependent constructs by scope
		 */
		map.put(scope, values);
	}

	/**
	 * Internal method to read the constructs valid in the given scope of the
	 * specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param scope
	 *            the scope
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, IScope scope, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructsSingleScope == null) {
			return null;
		}
		/*
		 * get map of cached sorted scope-dependent constructs by type
		 */
		Map<IScope, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsSingleScope.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get map of sorted constructs by scope
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(scope);
		if (map == null) {
			return null;
		}
		/*
		 * get cached sorted constructs by comparator
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to add the constructs valid in the given scope of the
	 * specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param scope
	 *            the scope
	 * @param comparator
	 *            the comparator
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, IScope scope, Comparator<X> comparator, List<X> values) {
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
		 * store sorted constructs by comparator
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read the constructs valid in one of the given scopes
	 * of the specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param scopes
	 *            the scopes
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Collection<IScope> scopes) {
		/*
		 * check main cache
		 */
		if (cachedConstructsMultipleScopes == null) {
			return null;
		}
		/*
		 * get map of cached scope-dependent constructs by type
		 */
		Map<Collection<IScope>, List<? extends Construct>> map = cachedConstructsMultipleScopes.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached scope-dependent by scopes
		 */
		return (List<X>) map.get(scopes);
	}

	/**
	 * Internal method to add the constructs valid in one of the given scopes of
	 * the specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param scopes
	 *            the scopes
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Collection<IScope> scopes, List<X> values) {
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
		 * store scope-dependent by scopes
		 */
		map.put(scopes, values);
	}

	/**
	 * Internal method to read the constructs valid in one of the given scope of
	 * the specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param scopes
	 *            the scopes
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Collection<IScope> scopes, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructsMultipleScopes == null) {
			return null;
		}
		/*
		 * get map of cached sorted scope-dependent constructs by type
		 */
		Map<Collection<IScope>, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsMultipleScopes.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get map of cached sorted constructs by scopes
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(scopes);
		if (map == null) {
			return null;
		}
		/*
		 * get cached scope-dependent by comparator
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to add the constructs valid in one of the given scope of
	 * the specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param scopes
	 *            the scopes
	 * @param comparator
	 *            the comparator
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Collection<IScope> scopes, Comparator<X> comparator, List<X> values) {
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
		 * store scope-dependent by comparator
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read the constructs valid in one or all of the given
	 * themes of the specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Topic[] themes, boolean all) {
		/*
		 * check main cache
		 */
		if (cachedConstructsMultipleThemes == null) {
			return null;
		}
		/*
		 * get map of cached themes-dependent constructs by type
		 */
		Map<Topic[], List<? extends Construct>> map = cachedConstructsMultipleThemes.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached constructs by themes
		 */
		return (List<X>) map.get(themes);
	}

	/**
	 * Internal method to add the constructs valid in one or all of the given
	 * themes of the specified construct type to internal cache
	 * 
	 * @param param
	 *            the construct type
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Topic[] themes, boolean all, List<X> values) {
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
		 * store constructs by themes
		 */
		map.put(themes, values);
	}

	/**
	 * Internal method to read the constructs valid in one or all of the given
	 * themes of the specified construct type from the internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Topic[] themes, boolean all, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructsMultipleThemes == null) {
			return null;
		}
		/*
		 * get map of cached sorted themes-dependent constructs by type
		 */
		Map<Topic[], Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructsMultipleThemes.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get map of cached sorted constructs by themes
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(themes);
		if (map == null) {
			return null;
		}
		/*
		 * get cached constructs by comparator
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to add the constructs valid in one or all of the given
	 * themes of the specified construct type to internal cache.
	 * 
	 * @param param
	 *            the construct type
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if the construct is valid within a scope
	 *            containing all or at least on of the given themes
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final <X extends Construct> void store(Param param, Topic[] themes, boolean all, Comparator<X> comparator, List<X> values) {
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
		 * store constructs by comparator
		 */
		map.put(comparator, values);
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
		store(Param.SCOPED, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.SCOPED, scope, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an association item.
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
		storeScopes(Param.ASSOCIATION, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all scope objects used as scope of an association item.
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
		storeScopes(Param.ASSOCIATION, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one association scope.
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
		storeThemes(Param.ASSOCIATION, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returning all themes contained by at least one association scope.
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
		storeThemes(Param.ASSOCIATION, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, theme, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, theme, comparator, list);
		return secureSubList(list, offset, limit);
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
	 *@return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(themes, all));
		store(Param.ASSOCIATION, themes, all, list);
		return secureSubList(list, offset, limit);
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
	 *@return all associations within the given range
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		List<Association> list = HashUtil.getList(getParentIndex().getAssociations(themes, all));
		Collections.sort(list, comparator);
		store(Param.ASSOCIATION, themes, all, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scope, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scopes, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scopes, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.ASSOCIATION, scope, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.OCCURRENCE, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.OCCURRENCE, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.OCCURRENCE, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.OCCURRENCE, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, theme, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, theme, comparator, list);
		return secureSubList(list, offset, limit);
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
	 *@return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(themes, all));
		store(Param.OCCURRENCE, themes, all, list);
		return secureSubList(list, offset, limit);
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
	 *@return all occurrences within the given range
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		List<Occurrence> list = HashUtil.getList(getParentIndex().getOccurrences(themes, all));
		Collections.sort(list, comparator);
		store(Param.OCCURRENCE, themes, all, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, scope, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, scopes, list);
		return secureSubList(list, offset, limit);
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
		store(Param.OCCURRENCE, scopes, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.NAME, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.NAME, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.NAME, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.NAME, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, theme, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, theme, comparator, list);
		return secureSubList(list, offset, limit);
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
	 *@return all names within the given range
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(themes, all));
		store(Param.NAME, themes, all, list);
		return secureSubList(list, offset, limit);
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
	 *@return all names within the given range
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		List<Name> list = HashUtil.getList(getParentIndex().getNames(themes, all));
		Collections.sort(list, comparator);
		store(Param.NAME, themes, all, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, scope, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, scopes, list);
		return secureSubList(list, offset, limit);
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
		store(Param.NAME, scopes, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.VARIANT, list);
		return secureSubList(list, offset, limit);
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
		storeScopes(Param.VARIANT, comparator, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.VARIANT, list);
		return secureSubList(list, offset, limit);
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
		storeThemes(Param.VARIANT, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, theme, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, theme, comparator, list);
		return secureSubList(list, offset, limit);
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
	 *@return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(themes, all));
		store(Param.VARIANT, themes, all, list);
		return secureSubList(list, offset, limit);
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
	 *@return all variants within the given range
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		List<Variant> list = HashUtil.getList(getParentIndex().getVariants(themes, all));
		Collections.sort(list, comparator);
		store(Param.VARIANT, themes, all, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, scope, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, scope, comparator, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, scopes, list);
		return secureSubList(list, offset, limit);
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
		store(Param.VARIANT, scopes, comparator, list);
		return secureSubList(list, offset, limit);
	}

}
