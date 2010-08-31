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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedIdentityIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IIdentityIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedIdentityIndexImpl<T extends ITopicMapStore> extends PagedIndexImpl<T, IIdentityIndex> implements IPagedIdentityIndex {

	/**
	 * enumeration representing the map keys
	 */
	protected enum Param {
		IDENTIFIER,

		ITEM_IDENTIFIER,

		SUBJECT_IDENTIFIER,

		SUBJECT_LOCATOR
	}

	private Map<Param, List<Locator>> cachedIdentifiers;
	private Map<Param, Map<Comparator<Locator>, List<Locator>>> cachedComparedIdentifiers;
	private Map<Param, Map<Pattern, List<? extends Construct>>> cachedConstructs;
	private Map<Param, Map<Pattern, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedConstructs;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index
	 */
	public PagedIdentityIndexImpl(T store, IIdentityIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByIdentifier(Pattern.compile(regExp), offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(String regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByIdentifier(Pattern.compile(regExp), offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(Pattern regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Construct> cache = read(Param.IDENTIFIER, regExp);
		if (cache == null) {
			return doGetConstructsByIdentifier(regExp, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Construct> cache = read(Param.IDENTIFIER, regExp, comparator);
		if (cache == null) {
			return doGetConstructsByIdentifier(regExp, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByItemIdentifier(Pattern.compile(regExp), offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(String regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByItemIdentifier(Pattern.compile(regExp), offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(Pattern regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Construct> cache = read(Param.ITEM_IDENTIFIER, regExp);
		if (cache == null) {
			return doGetConstructsByItemIdentifier(regExp, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Construct> cache = read(Param.ITEM_IDENTIFIER, regExp, comparator);
		if (cache == null) {
			return doGetConstructsByItemIdentifier(regExp, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.ITEM_IDENTIFIER);
		if (cache == null) {
			return doGetItemIdentifiers(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.ITEM_IDENTIFIER, comparator);
		if (cache == null) {
			return doGetItemIdentifiers(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.SUBJECT_IDENTIFIER);
		if (cache == null) {
			return doGetSubjectIdentifiers(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.SUBJECT_IDENTIFIER, comparator);
		if (cache == null) {
			return doGetSubjectIdentifiers(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.SUBJECT_LOCATOR);
		if (cache == null) {
			return doGetSubjectLocators(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Locator> cache = read(Param.SUBJECT_LOCATOR, comparator);
		if (cache == null) {
			return doGetSubjectLocators(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectIdentifier(Pattern.compile(regExp), offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(String regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectIdentifier(Pattern.compile(regExp), offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBJECT_IDENTIFIER, regExp);
		if (cache == null) {
			return doGetTopicsBySubjectIdentifier(regExp, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBJECT_IDENTIFIER, regExp, comparator);
		if (cache == null) {
			return doGetTopicsBySubjectIdentifier(regExp, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectLocator(Pattern.compile(regExp), offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(String regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectLocator(Pattern.compile(regExp), offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(Pattern regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBJECT_LOCATOR, regExp);
		if (cache == null) {
			return doGetTopicsBySubjectLocator(regExp, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<Topic> cache = read(Param.SUBJECT_LOCATOR, regExp, comparator);
		if (cache == null) {
			return doGetTopicsBySubjectLocator(regExp, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {

		if (event == TopicMapEventType.VARIANT_REMOVED) {
			clearItemIdentifierCache();
		} else if (event == TopicMapEventType.NAME_REMOVED) {
			clearItemIdentifierCache();
		} else if (event == TopicMapEventType.OCCURRENCE_REMOVED) {
			clearItemIdentifierCache();
		} else if (event == TopicMapEventType.TOPIC_REMOVED) {
			clearCache();
		} else if (event == TopicMapEventType.ASSOCIATION_REMOVED) {
			clearItemIdentifierCache();
		} else if (event == TopicMapEventType.ROLE_REMOVED) {
			clearItemIdentifierCache();
		}
		/*
		 * subject-identifier was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.SUBJECT_IDENTIFIER_ADDED || event == TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED) {
			clearSubjectIdentifierCache();
		}

		/*
		 * subject-locator was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.SUBJECT_LOCATOR_ADDED || event == TopicMapEventType.SUBJECT_LOCATOR_REMOVED) {
			clearSubjectLocatorCache();
		}

		/*
		 * item-identifier was changed -> clear dependent cache
		 */
		else if (event == TopicMapEventType.ITEM_IDENTIFIER_ADDED || event == TopicMapEventType.ITEM_IDENTIFIER_REMOVED) {
			clearItemIdentifierCache();
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
	private void clearCache() {
		if (cachedIdentifiers != null) {
			cachedIdentifiers.clear();
		}
		if (cachedComparedIdentifiers != null) {
			cachedComparedIdentifiers.clear();
		}
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.clear();
		}
	}

	/**
	 * clear caches depend on subject-locators
	 */
	private void clearSubjectLocatorCache() {
		clearDependentCache(Param.IDENTIFIER);
		clearDependentCache(Param.SUBJECT_LOCATOR);
	}

	/**
	 * clear caches depend on subject-identifiers
	 */
	private void clearSubjectIdentifierCache() {
		clearDependentCache(Param.IDENTIFIER);
		clearDependentCache(Param.SUBJECT_IDENTIFIER);
	}

	/**
	 * clear caches depend on item-identifiers
	 */
	private void clearItemIdentifierCache() {
		clearDependentCache(Param.IDENTIFIER);
		clearDependentCache(Param.ITEM_IDENTIFIER);
	}

	/**
	 * Clear all caches depend on the given type
	 * 
	 * @param param
	 *            the type
	 */
	private void clearDependentCache(Param param) {
		if (cachedIdentifiers != null) {
			cachedIdentifiers.remove(param);
		}
		if (cachedComparedIdentifiers != null) {
			cachedComparedIdentifiers.remove(param);
		}
		if (cachedConstructs != null) {
			cachedConstructs.remove(param);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(param);
		}
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param param
	 *            the identifier type
	 * @return the identifiers or <code>null</code> if key is unknown.
	 */
	private final List<Locator> read(Param param) {
		/*
		 * check main cache
		 */
		if (cachedIdentifiers == null) {
			return null;
		}
		/*
		 * store cached identifiers by type
		 */
		return cachedIdentifiers.get(param);
	}

	/**
	 * Store the given values into internal cache.
	 * <p>
	 * <b>Hint:</b> Store the whole results, not only the range between offset
	 * and limit.
	 * </p>
	 * 
	 * @param param
	 *            the identifier type
	 * @param values
	 *            the locators to store
	 */
	protected final void store(Param param, List<Locator> values) {
		/*
		 * initialize cache
		 */
		if (cachedIdentifiers == null) {
			cachedIdentifiers = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached identifiers by type
		 */
		cachedIdentifiers.put(param, values);
	}

	/**
	 * Internal method to read all identifiers of a specific type from cache.
	 * 
	 * @param param
	 *            the identifier type
	 * @param comparator
	 *            the comparator
	 * @return the identifiers or <code>null</code> if key-pair is unknown
	 */
	private final List<Locator> read(Param param, Comparator<Locator> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedIdentifiers == null) {
			return null;
		}
		/*
		 * get map of cached compared identifiers
		 */
		Map<Comparator<Locator>, List<Locator>> map = cachedComparedIdentifiers.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get cached identifiers by type
		 */
		return map.get(comparator);
	}

	/**
	 * Store the given values into the internal cache.
	 * <p>
	 * <b>Hint:</b> Store the whole results, not only the range between offset
	 * and limit.
	 * </p>
	 * 
	 * @param param
	 *            the identifier type
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the locators to store
	 */
	protected final void store(Param param, Comparator<Locator> comparator, List<Locator> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedIdentifiers == null) {
			cachedComparedIdentifiers = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached compared identifiers
		 */
		Map<Comparator<Locator>, List<Locator>> map = cachedComparedIdentifiers.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedComparedIdentifiers.put(param, map);
		}
		/*
		 * store identifiers by type
		 */
		map.put(comparator, values);
	}

	/**
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern form cache.
	 * 
	 * @param param
	 *            the identifier type
	 * @param methodName
	 *            the method name which should be called to extract missing
	 *            values
	 * @param pattern
	 *            the pattern to match
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Pattern pattern) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		/*
		 * get map of cached pattern-dependent constructs
		 */
		Map<Pattern, List<? extends Construct>> map = cachedConstructs.get(param);
		if (map == null) {
			return null;
		}
		return (List<X>) map.get(pattern);
	}

	/**
	 * Store the given values into the internal cache using the given key
	 * values.
	 * <p>
	 * <b>Hint:</b> Store the whole results, not only the range between offset
	 * and limit.
	 * </p>
	 * 
	 * @param param
	 *            the identifier type
	 * @param pattern
	 *            the pattern to match
	 * @param constructs
	 *            the constructs to cache
	 */
	protected final <X extends Construct> void store(Param param, Pattern pattern, List<X> constructs) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * get map of cached pattern-dependent constructs
		 */
		Map<Pattern, List<? extends Construct>> map = cachedConstructs.get(param);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cachedConstructs.put(param, map);
		}
		/*
		 * store constructs by pattern
		 */
		map.put(pattern, constructs);
	}

	/**
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern.
	 * 
	 * @param param
	 *            the identifier type
	 * @param pattern
	 *            the pattern to match
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <X extends Construct> List<X> read(Param param, Pattern pattern, Comparator<X> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedConstructs == null) {
			return null;
		}
		/*
		 * get cached pattern-dependent
		 */
		Map<Pattern, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructs.get(param);
		if (cached == null) {
			return null;
		}
		/*
		 * get map of cached compared constructs
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(pattern);
		if (map == null) {
			return null;
		}
		/*
		 * get cached constructs by type
		 */
		return (List<X>) map.get(comparator);
	}

	/**
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern.
	 * <p>
	 * <b>Hint:</b> Store the whole results, not only the range between offset
	 * and limit.
	 * </p>
	 * 
	 * @param param
	 *            the identifier type
	 * @param pattern
	 *            the pattern to match
	 * @param comparator
	 *            the comparator
	 * @return the constructs or <code>null</code> if the key-pair is unknown
	 */
	protected final <X extends Construct> void store(Param param, Pattern pattern, Comparator<X> comparator, List<X> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructs == null) {
			cachedComparedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached pattern-dependent
		 */
		Map<Pattern, Map<Comparator<? extends Construct>, List<? extends Construct>>> cached = cachedComparedConstructs.get(param);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructs.put(param, cached);
		}
		/*
		 * get map of cached compared constructs
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> map = cached.get(pattern);
		if (map == null) {
			map = HashUtil.getWeakHashMap();
			cached.put(pattern, map);
		}
		map.put(comparator, values);
	}

	/**
	 * Returning all constructs using an identifier matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs
	 */
	protected List<Construct> doGetConstructsByIdentifier(Pattern regExp, int offset, int limit) {
		List<Construct> constructs = HashUtil.getList(getParentIndex().getConstructsByIdentifier(regExp));
		store(Param.IDENTIFIER, regExp, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all constructs using an identifier matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the constructs
	 */
	protected List<Construct> doGetConstructsByIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		List<Construct> constructs = HashUtil.getList(getParentIndex().getConstructsByIdentifier(regExp));
		Collections.sort(constructs, comparator);
		store(Param.IDENTIFIER, regExp, comparator, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all constructs using an item-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the constructs
	 */
	protected List<Construct> doGetConstructsByItemIdentifier(Pattern regExp, int offset, int limit) {
		List<Construct> constructs = HashUtil.getList(getParentIndex().getConstructsByItemIdentifier(regExp));
		store(Param.ITEM_IDENTIFIER, regExp, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all constructs using an item-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the constructs
	 */
	protected List<Construct> doGetConstructsByItemIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		List<Construct> constructs = HashUtil.getList(getParentIndex().getConstructsByItemIdentifier(regExp));
		Collections.sort(constructs, comparator);
		store(Param.ITEM_IDENTIFIER, regExp, comparator, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all topics using a subject-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the topics
	 */
	protected List<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit) {
		List<Topic> constructs = HashUtil.getList(getParentIndex().getTopicsBySubjectIdentifier(regExp));
		store(Param.SUBJECT_IDENTIFIER, regExp, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all topics using a subject-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the topics
	 */
	protected List<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> constructs = HashUtil.getList(getParentIndex().getTopicsBySubjectIdentifier(regExp));
		Collections.sort(constructs, comparator);
		store(Param.SUBJECT_IDENTIFIER, regExp, comparator, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all topics using a subject-locator matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @return the topics
	 */
	protected List<Topic> doGetTopicsBySubjectLocator(Pattern regExp, int offset, int limit) {
		List<Topic> constructs = HashUtil.getList(getParentIndex().getTopicsBySubjectLocator(regExp));
		store(Param.SUBJECT_LOCATOR, regExp, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Returning all topics using a subject-locator matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the topics
	 */
	protected List<Topic> doGetTopicsBySubjectLocator(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> constructs = HashUtil.getList(getParentIndex().getTopicsBySubjectLocator(regExp));
		Collections.sort(constructs, comparator);
		store(Param.SUBJECT_LOCATOR, regExp, comparator, constructs);
		return secureSubList(constructs, offset, limit);
	}

	/**
	 * Return all item-identifiers used by any construct of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @return the locators
	 */
	protected List<Locator> doGetItemIdentifiers(int offset, int limit) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getItemIdentifiers());
		store(Param.ITEM_IDENTIFIER, locators);
		return secureSubList(locators, offset, limit);
	}

	/**
	 * Return all item-identifiers used by any construct of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the locators
	 */
	protected List<Locator> doGetItemIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getItemIdentifiers());
		Collections.sort(locators, comparator);
		store(Param.ITEM_IDENTIFIER, comparator, locators);
		return secureSubList(locators, offset, limit);
	}

	/**
	 * Return all subject-identifiers used by any topic of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @return the locators
	 */
	protected List<Locator> doGetSubjectIdentifiers(int offset, int limit) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getSubjectIdentifiers());
		store(Param.SUBJECT_IDENTIFIER, locators);
		return secureSubList(locators, offset, limit);
	}

	/**
	 * Return all subject-identifiers used by any topic of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the locators
	 */
	protected List<Locator> doGetSubjectIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getSubjectIdentifiers());
		Collections.sort(locators, comparator);
		store(Param.SUBJECT_IDENTIFIER, comparator, locators);
		return secureSubList(locators, offset, limit);
	}

	/**
	 * Return all subject-locators used by any topic of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @return the locators
	 */
	protected List<Locator> doGetSubjectLocators(int offset, int limit) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getSubjectLocators());
		store(Param.SUBJECT_LOCATOR, locators);
		return secureSubList(locators, offset, limit);
	}

	/**
	 * Return all subject-locators used by any topic of the topic map.
	 * 
	 * @param offset
	 *            the offset value
	 * @param limit
	 *            the limit value
	 * 
	 * @param comparator
	 *            the comparator to sort the values
	 * @return the locators
	 */
	protected List<Locator> doGetSubjectLocators(int offset, int limit, Comparator<Locator> comparator) {
		List<Locator> locators = HashUtil.getList(getParentIndex().getSubjectLocators());
		Collections.sort(locators, comparator);
		store(Param.SUBJECT_LOCATOR, comparator, locators);
		return secureSubList(locators, offset, limit);
	}

}
