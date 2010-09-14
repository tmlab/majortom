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

import java.util.Collection;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.core.BaseCachedIdentityIndexImpl;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Implementation of {@link IIdentityIndex}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class CachedIdentityIndexImpl<T extends ITopicMapStore> extends BaseCachedIdentityIndexImpl<T> implements IIdentityIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public CachedIdentityIndexImpl(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByIdentifier(Pattern.compile(regExp));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetConstructsByIdentifier(regExp);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp);
		if (constructs == null) {
			constructs = doGetConstructsByIdentifier(regExp);
			cache(BaseCachedIdentityIndexImpl.Type.IDENTIFIER, regExp, constructs);
		}
		return constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructsByItemIdentifier(Pattern.compile(regExp));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetConstructsByItemIdentifier(regExp);
		}
		Collection<Construct> constructs = read(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp);
		if (constructs == null) {
			constructs = doGetConstructsByItemIdentifier(regExp);
			cache(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, regExp, constructs);
		}
		return constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getItemIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetItemIdentifiers();
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER);
		if (locators == null) {
			locators = doGetItemIdentifiers();
			cacheLocators(BaseCachedIdentityIndexImpl.Type.ITEM_IDENTIFIER, locators);
		}
		return locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetSubjectIdentifiers();
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER);
		if (locators == null) {
			locators = doGetSubjectIdentifiers();
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, locators);
		}
		return locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectLocators() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetSubjectLocators();
		}
		Collection<Locator> locators = readLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR);
		if (locators == null) {
			locators = doGetSubjectLocators();
			cacheLocators(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, locators);
		}
		return locators;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectIdentifier(Pattern.compile(regExp));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetTopicsBySubjectIdentifier(regExp);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp);
		if (topics == null) {
			topics = doGetTopicsBySubjectIdentifier(regExp);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_IDENTIFIER, regExp, topics);
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicsBySubjectLocator(Pattern.compile(regExp));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		/*
		 * redirect to real store if caching is disabled
		 */
		if ( !getStore().isCachingEnabled()){
			return doGetTopicsBySubjectLocator(regExp);
		}
		Collection<Topic> topics = read(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp);
		if (topics == null) {
			topics = doGetTopicsBySubjectLocator(regExp);
			cache(BaseCachedIdentityIndexImpl.Type.SUBJECT_LOCATOR, regExp, topics);
		}
		return topics;
	}

	/**
	 * Returning all constructs using an identifier matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the constructs
	 */
	protected abstract Collection<Construct> doGetConstructsByIdentifier(Pattern regExp);

	/**
	 * Returning all constructs using an item-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the constructs
	 */
	protected abstract Collection<Construct> doGetConstructsByItemIdentifier(Pattern regExp);

	/**
	 * Returning all topics using a subject-identifier matching the given
	 * regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the topics
	 */
	protected abstract Collection<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp);

	/**
	 * Returning all topics using a subject-locator matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the topics
	 */
	protected abstract Collection<Topic> doGetTopicsBySubjectLocator(Pattern regExp);

	/**
	 * Return all item-identifiers used by any construct of the topic map.
	 * 
	 * @return the locators
	 */
	protected abstract Collection<Locator> doGetItemIdentifiers();

	/**
	 * Return all subject-identifiers used by any topic of the topic map.
	 * 
	 * @return the locators
	 */
	protected abstract Collection<Locator> doGetSubjectIdentifiers();

	/**
	 * Return all subject-locators used by any topic of the topic map.
	 * 
	 * @return the locators
	 */
	protected abstract Collection<Locator> doGetSubjectLocators();

}
