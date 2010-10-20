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
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.core.BaseCachedIdentityIndexImpl;
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
public abstract class PagedIdentityIndexImpl<T extends ITopicMapStore> extends BaseCachedIdentityIndexImpl<T> implements IPagedIdentityIndex {

	private final IIdentityIndex parentIndex;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index
	 */
	public PagedIdentityIndexImpl(T store, IIdentityIndex parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * @return the parentIndex
	 */
	public IIdentityIndex getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetConstructsByIdentifier(regExp, offset, limit);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp, offset, limit, null);
		if (constructs == null) {
			constructs = doGetConstructsByIdentifier(regExp, offset, limit);
			cache(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp, offset, limit, null, constructs);
		}
		return (List<Construct>) constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetConstructsByIdentifier(regExp, offset, limit, comparator);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp, offset, limit, comparator);
		if (constructs == null) {
			constructs = doGetConstructsByIdentifier(regExp, offset, limit, comparator);
			cache(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp, offset, limit, comparator, constructs);
		}
		return (List<Construct>) constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetConstructsByItemIdentifier(regExp, offset, limit);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp, offset, limit, null);
		if (constructs == null) {
			constructs = doGetConstructsByItemIdentifier(regExp, offset, limit);
			cache(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp, offset, limit, null, constructs);
		}
		return (List<Construct>) constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Construct> getConstructsByItemIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetConstructsByItemIdentifier(regExp, offset, limit, comparator);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp, offset, limit, comparator);
		if (constructs == null) {
			constructs = doGetConstructsByItemIdentifier(regExp, offset, limit, comparator);
			cache(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp, offset, limit, comparator, constructs);
		}
		return (List<Construct>) constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetItemIdentifiers(offset, limit);
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, offset, limit, null);
		if (locators == null) {
			locators = doGetItemIdentifiers(offset, limit);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, offset, limit, null, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetItemIdentifiers(offset, limit, comparator);
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, offset, limit, comparator);
		if (locators == null) {
			locators = doGetItemIdentifiers(offset, limit, comparator);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, offset, limit, comparator, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetSubjectIdentifiers(offset, limit);
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, offset, limit, null);
		if (locators == null) {
			locators = doGetSubjectIdentifiers(offset, limit);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, offset, limit, null, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetSubjectIdentifiers(offset, limit, comparator);
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, offset, limit, comparator);
		if (locators == null) {
			locators = doGetSubjectIdentifiers(offset, limit, comparator);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, offset, limit, comparator, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetSubjectLocators(offset, limit);
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, offset, limit, null);
		if (locators == null) {
			locators = doGetSubjectLocators(offset, limit);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, offset, limit, null, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Locator> getSubjectLocators(int offset, int limit, Comparator<Locator> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetSubjectLocators(offset, limit, comparator);
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, offset, limit, comparator);
		if (locators == null) {
			locators = doGetSubjectLocators(offset, limit, comparator);
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, offset, limit, comparator, locators);
		}
		return (List<Locator>) locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicsBySubjectIdentifier(regExp, offset, limit);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp, offset, limit, null);
		if (topics == null) {
			topics = doGetTopicsBySubjectIdentifier(regExp, offset, limit);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicsBySubjectIdentifier(regExp, offset, limit, comparator);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp, offset, limit, comparator);
		if (topics == null) {
			topics = doGetTopicsBySubjectIdentifier(regExp, offset, limit, comparator);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
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
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicsBySubjectLocator(regExp, offset, limit);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp, offset, limit, null);
		if (topics == null) {
			topics = doGetTopicsBySubjectLocator(regExp, offset, limit);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getTopicsBySubjectLocator(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if ( regExp == null ){
			throw new IllegalArgumentException("Argument cannot be null.");
		}
		if ( comparator == null ){
			throw new IllegalArgumentException("Comparator cannot be null.");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if (!getTopicMapStore().isCachingEnabled()) {
			return doGetTopicsBySubjectLocator(regExp, offset, limit, comparator);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp, offset, limit, comparator);
		if (topics == null) {
			topics = doGetTopicsBySubjectLocator(regExp, offset, limit, comparator);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(constructs, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
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
		return HashUtil.secureSubList(locators, offset, limit);
	}

}
