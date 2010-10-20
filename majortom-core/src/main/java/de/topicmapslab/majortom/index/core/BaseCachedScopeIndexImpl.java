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
package de.topicmapslab.majortom.index.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.paging.IPagedScopedIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedScopedIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedScopeIndexImpl<T extends ITopicMapStore> extends BaseCachedIndexImpl<T> implements ITopicMapListener {

	/**
	 * Cache containing the scopes of specific constructs
	 */
	private Map<ScopesCacheKey, Collection<IScope>> cachedScopes;
	/**
	 * Cache containing the scope of a set of themes
	 */
	private Map<Collection<? extends Topic>, IScope> scopesByThemes;
	/**
	 * Cache containing the themes of specific constructs
	 */
	private Map<ScopesCacheKey, Collection<Topic>> cachedThemes;
	/**
	 * Cache containing all constructs
	 */
	private Map<ScopesCacheKey, Collection<? extends Construct>> cachedConstructs;

	/**
	 * Caching map for all cache keys depends on the given class
	 */
	private Map<Class<? extends IScopable>, Set<ScopesCacheKey>> cacheKeys;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public BaseCachedScopeIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * construct was removed -> clear dependent caches
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED || event == TopicMapEventType.VARIANT_ADDED) {
			clearDependentCache(IName.class, IVariant.class);
		} else if (event == TopicMapEventType.NAME_REMOVED || event == TopicMapEventType.NAME_ADDED) {
			clearDependentCache(IName.class, IVariant.class);
		} else if (event == TopicMapEventType.OCCURRENCE_REMOVED || event == TopicMapEventType.OCCURRENCE_ADDED) {
			clearDependentCache(IOccurrence.class);
		} else if (event == TopicMapEventType.ASSOCIATION_REMOVED || event == TopicMapEventType.ASSOCIATION_ADDED) {
			clearDependentCache(IAssociation.class);
		}
		/*
		 * scope was modified -> clear dependent caches
		 */
		else if (event == TopicMapEventType.SCOPE_MODIFIED) {
			if (notifier instanceof Association) {
				clearDependentCache(IAssociation.class);
			} else if (notifier instanceof Name) {
				clearDependentCache(IName.class, IVariant.class);
			} else if (notifier instanceof Occurrence) {
				clearDependentCache(IOccurrence.class);
			} else if (notifier instanceof Variant) {
				clearDependentCache(IName.class, IVariant.class);
			}
		}
		/*
		 * topic removed or merging
		 */
		else if (event == TopicMapEventType.MERGE || event == TopicMapEventType.TOPIC_REMOVED) {
			clearCache();
		}
	}

	/**
	 * Clear all caches
	 */
	protected final void clearCache() {
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedScopes != null) {
			cachedScopes.clear();
		}
		if (cachedThemes != null) {
			cachedThemes.clear();
		}
		if (cacheKeys != null) {
			cacheKeys.clear();
		}
		if (scopesByThemes != null) {
			scopesByThemes.clear();
		}
	}

	/**
	 * Clear all caches depending on given classes
	 * 
	 * @param classes
	 *            the classes the cache depends on
	 */
	private final void clearDependentCache(Class<? extends IScopable>... classes) {
		for (Class<? extends IScopable> clazz : classes) {
			for (ScopesCacheKey key : getDependentKeys(clazz)) {
				if (cachedConstructs != null) {
					cachedConstructs.remove(key);
				}
				if (cachedScopes != null) {
					cachedScopes.remove(key);
				}
				if (cachedThemes != null) {
					cachedThemes.remove(key);
				}
			}
		}
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param themes
	 *            the themes
	 * @return the scope or <code>null</code> if the key is unknown
	 */
	protected final IScope readScope(Collection<? extends Topic> themes) {
		if (scopesByThemes == null) {
			return null;
		}
		return scopesByThemes.get(themes);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param themes
	 *            the themes
	 * @param scope
	 *            the scope to store
	 */
	protected final void cacheScope(Collection<? extends Topic> themes, IScope scope) {
		if (scopesByThemes == null) {
			scopesByThemes = HashUtil.getHashMap();
		}
		scopesByThemes.put(themes, scope);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param values
	 *            the values to store
	 */
	protected final void cacheScopes(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Collection<IScope> values) {
		cacheScopes(clazz, filter, multiMatch, null, null, null, values);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param values
	 *            the values to store
	 */
	protected final void cacheScopes(Class<? extends IScopable> clazz, Collection<IScope> values) {
		cacheScopes(clazz, null, false, null, null, null, values);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final void cacheScopes(Class<? extends IScopable> clazz, Integer offset, Integer limit, Comparator<IScope> comparator, Collection<IScope> values) {
		cacheScopes(clazz, null, false, offset, limit, comparator, values);
	}

	/**
	 * Internal method to add the scopes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param values
	 *            the values to store
	 */
	protected final void cacheScopes(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit, Comparator<IScope> comparator, Collection<IScope> values) {
		/*
		 * initialize cache
		 */
		if (cachedScopes == null) {
			cachedScopes = HashUtil.getWeakHashMap();
		}
		/*
		 * store scopes of the specific type
		 */
		cachedScopes.put(generateScopesCacheKey(clazz, filter, multiMatch, offset, limit, comparator), values);
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	protected final Collection<IScope> readScopes(Class<? extends IScopable> clazz, Object filter, boolean multiMatch) {
		return readScopes(clazz, filter, multiMatch, null, null, null);
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	protected final Collection<IScope> readScopes(Class<? extends IScopable> clazz) {
		return readScopes(clazz, null, false, null, null, null);
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	protected final Collection<IScope> readScopes(Class<? extends IScopable> clazz, Integer offset, Integer limit, Comparator<IScope> comparator) {
		return readScopes(clazz, null, false, offset, limit, comparator);
	}

	/**
	 * Internal method to read the scopes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the scopes or <code>null</code> if the key is unknown
	 */
	protected final Collection<IScope> readScopes(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit, Comparator<IScope> comparator) {
		/*
		 * check main cache
		 */
		if (cachedScopes == null) {
			return null;
		}
		ScopesCacheKey key = generateScopesCacheKey(clazz, filter, multiMatch, offset, limit, comparator);
		return cachedScopes.get(key);
	}

	/**
	 * Internal method to read the themes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @return the themes or <code>null</code> if the key is unknown
	 */
	protected final Collection<Topic> readThemes(Class<? extends IScopable> clazz) {
		return readThemes(clazz, null, null, null);
	}

	/**
	 * Internal method to read the themes of the specified construct type from
	 * the internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the themes or <code>null</code> if the key is unknown
	 */
	protected final Collection<Topic> readThemes(Class<? extends IScopable> clazz, Integer offset, Integer limit, Comparator<Topic> comparator) {
		/*
		 * check main cache
		 */
		if (cachedThemes == null) {
			return null;
		}
		/*
		 * get cached themes of the specific type
		 */
		return cachedThemes.get(generateScopesCacheKey(clazz, null, false, offset, limit, comparator));
	}

	/**
	 * Internal method to add the themes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param valus
	 *            the values to store
	 */
	protected final void cacheThemes(Class<? extends IScopable> clazz, Collection<Topic> values) {
		cacheThemes(clazz, null, null, null, values);
	}

	/**
	 * Internal method to add the themes of the specified construct type to
	 * internal cache.
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param valus
	 *            the values to store
	 */
	protected final void cacheThemes(Class<? extends IScopable> clazz, Integer offset, Integer limit, Comparator<Topic> comparator, Collection<Topic> values) {
		/*
		 * initialize cache
		 */
		if (cachedThemes == null) {
			cachedThemes = HashUtil.getWeakHashMap();
		}
		/*
		 * store themes of the specific type
		 */
		cachedThemes.put(generateScopesCacheKey(clazz, null, false, offset, limit, comparator), values);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type from the internal cache.
	 * 
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	protected final <X extends Construct> Collection<X> read(Class<? extends IScopable> clazz, Object filter, boolean multiMatch) {
		return read(clazz, filter, multiMatch, null, null, null);
	}

	/**
	 * Internal method to read the constructs valid in the given theme of the
	 * specified construct type from the internal cache.
	 * 
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the constructs or <code>null</code> if the key is unknown
	 */
	@SuppressWarnings("unchecked")
	protected final <X extends Construct> Collection<X> read(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		return (Collection<X>) cachedConstructs.get(generateScopesCacheKey(clazz, filter, multiMatch, offset, limit, comparator));
	}

	/**
	 * Internal method to add the constructs valid in the given theme of the
	 * specified construct type to internal cache
	 * 
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void cache(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Collection<X> values) {
		cache(clazz, filter, multiMatch, null, null, null, values);
	}

	/**
	 * Internal method to add the constructs valid in the given theme of the
	 * specified construct type to internal cache
	 * 
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @param valus
	 *            the values to store
	 */
	protected final <X extends Construct> void cache(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit, Comparator<X> comparator, Collection<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * store theme-dependent constructs by theme
		 */
		cachedConstructs.put(generateScopesCacheKey(clazz, filter, multiMatch, offset, limit, comparator), values);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz) {
		return generateScopesCacheKey(clazz, null, false, null, null, null);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz, Integer offset, Integer limit) {
		return generateScopesCacheKey(clazz, null, false, offset, limit, null);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz, Integer offset, Integer limit, Comparator<?> comparator) {
		return generateScopesCacheKey(clazz, null, false, offset, limit, comparator);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz, Object filter, boolean multiMatch) {
		return generateScopesCacheKey(clazz, filter, multiMatch, null, null, null);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit) {
		return generateScopesCacheKey(clazz, filter, multiMatch, offset, limit, null);
	}

	/**
	 * Generate a cache key for internal cache
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 * @return the generated key
	 */
	protected ScopesCacheKey generateScopesCacheKey(Class<? extends IScopable> clazz, Object filter, boolean multiMatch, Integer offset, Integer limit, Comparator<?> comparator) {
		ScopesCacheKey key = new ScopesCacheKey(clazz, filter, multiMatch, offset, limit, comparator);
		if (cacheKeys == null) {
			cacheKeys = HashUtil.getHashMap();
		}
		Set<ScopesCacheKey> keys = cacheKeys.get(clazz);
		if (keys == null) {
			keys = HashUtil.getHashSet();
			cacheKeys.put(clazz, keys);
		}
		keys.add(key);
		return key;
	}

	/**
	 * Returns a set containing all dependent cache keys for the given class
	 * 
	 * @param clazz
	 *            the class
	 * @return a set of depending cache keys
	 */
	private Set<ScopesCacheKey> getDependentKeys(Class<? extends IScopable> clazz) {
		if (cacheKeys == null) {
			return Collections.emptySet();
		}
		Set<ScopesCacheKey> keys = HashUtil.getHashSet();
		if (cacheKeys.containsKey(clazz)) {
			keys.addAll(cacheKeys.get(clazz));
		}
		if (ICharacteristics.class.isAssignableFrom(clazz) && cacheKeys.containsKey(ICharacteristics.class)) {
			keys.addAll(cacheKeys.get(ICharacteristics.class));
		}
		if (cacheKeys.containsKey(IScopable.class)) {
			keys.addAll(cacheKeys.get(IScopable.class));
		}
		return keys;
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
	 * Removed any cached content from internal cache
	 */
	public void clear() {
		clearCache();
	}

}

/**
 * internal cache key class for scope cache keys
 * 
 * @author Sven Krosse
 * 
 */
class ScopesCacheKey {
	Comparator<?> comparator;
	Class<? extends IScopable> clazz;
	Integer offset;
	Integer limit;
	Object filter;
	Boolean multiMatch;

	/**
	 * constructor
	 * 
	 * @param clazz
	 *            the type of construct
	 * @param filter
	 *            a filter criteria (a theme, a collection, a scope )
	 * @param multiMatch
	 *            flag indicates if the filter is a collection of themes if
	 *            themes should matching all
	 * @param offset
	 *            the offset or <code>null</code>
	 * @param limit
	 *            the limit or <code>null</code>
	 * @param comparator
	 *            the comparator or <code>null</code>
	 */
	public ScopesCacheKey(Class<? extends IScopable> clazz, Object filter, Boolean multiMatch, Integer offset, Integer limit, Comparator<?> comparator) {
		this.clazz = clazz;
		this.offset = offset;
		this.limit = limit;
		this.filter = filter;
		this.multiMatch = multiMatch;
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ScopesCacheKey) {
			ScopesCacheKey key = (ScopesCacheKey) obj;
			boolean result = key.clazz.equals(clazz);
			result &= multiMatch == key.multiMatch;
			result &= (filter == null) ? key.filter == null : filter.equals(key.filter);
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
		int hashCode = clazz.hashCode();
		hashCode |= multiMatch.hashCode();
		hashCode |= (filter == null) ? 0 : filter.hashCode();
		hashCode |= (comparator == null) ? 0 : comparator.hashCode();
		hashCode |= (offset == null) ? 0 : offset.hashCode();
		hashCode |= (limit == null) ? 0 : limit.hashCode();
		return hashCode;
	}
}