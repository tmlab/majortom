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
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedIdentityIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IIdentityIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedIdentityIndex extends InMemoryPagedIndex<IIdentityIndex> implements IPagedIdentityIndex {

	/**
	 * enumeration representing the map keys
	 */
	enum Param {
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
	public InMemoryPagedIdentityIndex(InMemoryTopicMapStore store, IIdentityIndex parentIndex) {
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
		return getConstructs(Param.IDENTIFIER, "getConstructsByIdentifier", regExp, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.IDENTIFIER, "getConstructsByIdentifier", regExp, offset, limit, comparator);
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
		return getConstructs(Param.ITEM_IDENTIFIER, "getConstructsByItemIdentifier", regExp, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.ITEM_IDENTIFIER, "getConstructsByItemIdentifier", regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.ITEM_IDENTIFIER, "getItemIdentifiers", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.ITEM_IDENTIFIER, "getItemIdentifiers", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.SUBJECT_IDENTIFIER, "getSubjectIdentifiers", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.SUBJECT_IDENTIFIER, "getSubjectIdentifiers", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.SUBJECT_LOCATOR, "getSubjectLocators", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getIdentifiers(Param.SUBJECT_LOCATOR, "getSubjectLocators", offset, limit, comparator);
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
		return getConstructs(Param.SUBJECT_IDENTIFIER, "getTopicsBySubjectIdentifier", regExp, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.SUBJECT_IDENTIFIER, "getTopicsBySubjectIdentifier", regExp, offset, limit, comparator);
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
		return getConstructs(Param.SUBJECT_LOCATOR, "getTopicsBySubjectLocator", regExp, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Param.SUBJECT_LOCATOR, "getTopicsBySubjectLocator", regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * construct was removed
		 */
		if (event == TopicMapEventType.CONSTRUCT_REMOVED) {
			/*
			 * topic was removed -> clear cache
			 */
			if (oldValue instanceof Topic) {
				clearCache();
			}
			/*
			 * any other construct -> remove item-identifier cache
			 */
			else {
				clearItemIdentifierCache();
			}
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
	 * Internal method to read all identifiers of a specific type within the
	 * given range.
	 * 
	 * @param param
	 *            the identifier type
	 * @param methodName
	 *            the method name which should be called to extract missing
	 *            values
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the identifiers within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Locator> getIdentifiers(Param param, String methodName, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedIdentifiers == null) {
			cachedIdentifiers = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached identifiers by type
		 */
		List<Locator> list = cachedIdentifiers.get(param);
		if (list == null) {
			try {
				/*
				 * call method to get identifiers
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Locator>) method.invoke(getParentIndex()));
				cachedIdentifiers.put(param, list);
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
	 * Internal method to read all identifiers of a specific type within the
	 * given range.
	 * 
	 * @param param
	 *            the identifier type
	 * @param methodName
	 *            the method name which should be called to extract missing
	 *            values
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return the identifiers within the given range
	 */
	@SuppressWarnings("unchecked")
	private final List<Locator> getIdentifiers(Param param, String methodName, int offset, int limit, Comparator<Locator> comparator) {
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
		 * get cached identifiers by type
		 */
		List<Locator> list = map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get identifiers
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<Locator>) method.invoke(getParentIndex()));
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
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern within the given range.
	 * 
	 * @param param
	 *            the identifier type
	 * @param methodName
	 *            the method name which should be called to extract missing
	 *            values
	 * @param pattern
	 *            the pattern to match
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the identifiers within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Pattern pattern, int offset, int limit) {
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
		 * get cached constructs by pattern
		 */
		List<T> list = (List<T>) map.get(pattern);
		if (list == null) {
			try {
				/*
				 * call method to get identifiers
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Pattern.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), pattern));
				map.put(pattern, list);
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
	 * Internal method to read all identifiers of a specific type matching the
	 * given pattern within the given range.
	 * 
	 * @param param
	 *            the identifier type
	 * @param methodName
	 *            the method name which should be called to extract missing
	 *            values
	 * @param pattern
	 *            the pattern to match
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return the identifiers within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <T extends Construct> List<T> getConstructs(Param param, String methodName, Pattern pattern, int offset, int limit, Comparator<T> comparator) {
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
		/*
		 * get cached constructs by type
		 */
		List<T> list = (List<T>) map.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get identifiers
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, Pattern.class);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), pattern));
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
