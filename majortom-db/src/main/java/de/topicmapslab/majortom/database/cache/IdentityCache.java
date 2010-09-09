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
package de.topicmapslab.majortom.database.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of a store object containing all identity informations
 * 
 * @author Sven Krosse
 * 
 */
class IdentityCache implements ITopicMapListener {

	/**
	 * enumeration used as key for internal storage
	 */
	enum Key {
		ITEM_IDENTIFIER,

		SUBJECT_IDENTIFIER,

		SUBJEC_LOCATOR
	}

	/**
	 * Map to store IDs and the corresponding constructs
	 */
	private BidiMap ids;

	/**
	 * Map to store the all identities contained by the topic map instance
	 */
	private Map<Key, Set<ILocator>> identities;

	/**
	 * storage map of the reference-locator mapping of the topic map
	 */
	private Map<String, ILocator> locators;

	/**
	 * item-identifier mapping of the topic map engine
	 */
	private Map<ILocator, IConstruct> itemIdentifiers;

	/**
	 * construct to item-identifiers mapping
	 */
	private Map<IConstruct, Set<ILocator>> constructItemIdentifiers;

	/**
	 * subject-identifier mapping of the topic map engine
	 */
	private Map<ILocator, ITopic> subjectIdentifiers;

	/**
	 * topic to subject-identifiers mapping
	 */
	private Map<ITopic, Set<ILocator>> topicSubjectIdentifiers;

	/**
	 * subject-locator mapping of the topic map engine
	 */
	private Map<ILocator, ITopic> subjectLocators;

	/**
	 * topic to subject-locators mapping
	 */
	private Map<ITopic, Set<ILocator>> topicSubjectLocators;

	/**
	 * map to store the best labels of a topic
	 */
	private Map<ITopic, String> bestLabels;

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (identities != null) {
			identities.clear();
		}
		if (locators != null) {
			locators.clear();
		}
		if (itemIdentifiers != null) {
			itemIdentifiers.clear();
		}
		if (subjectIdentifiers != null) {
			subjectIdentifiers.clear();
		}
		if (subjectLocators != null) {
			subjectLocators.clear();
		}
		if (constructItemIdentifiers != null) {
			constructItemIdentifiers.clear();
		}
		if (topicSubjectIdentifiers != null) {
			topicSubjectIdentifiers.clear();
		}
		if (topicSubjectLocators != null) {
			topicSubjectLocators.clear();
		}
		if (ids != null) {
			ids.clear();
		}
	}

	/**
	 * Return the construct identified by the given id.
	 * 
	 * @param id
	 *            the id
	 * @return the construct or <code>null</code>
	 */
	public IConstruct byId(final String id) {
		if (ids == null) {
			return null;
		}
		return (IConstruct) ids.get(id);
	}

	/**
	 * Secure extraction of a construct by its identity from the given map.
	 * 
	 * @param <T>
	 *            the generic type of construct to extract
	 * @param map
	 *            the map
	 * @param l
	 *            the locator
	 * @return the construct or <code>null</code> if the given map is
	 *         <code>null</code> or does not contain the given key.
	 */
	public <T extends IConstruct> T byIdentity(Map<ILocator, T> map, ILocator l) {
		if (map == null) {
			return null;
		}
		return map.get(l);
	}

	/**
	 * Return the construct identified by the given item-identifier.
	 * 
	 * @param l
	 *            the item-identifier
	 * @return the construct or <code>null</code>
	 */
	public IConstruct byItemIdentifier(final ILocator l) {
		return byIdentity(itemIdentifiers, l);
	}

	/**
	 * Return the topic identified by the given subject-identifier.
	 * 
	 * @param l
	 *            the subject-identifier
	 * @return the topic or <code>null</code>
	 */
	public ITopic bySubjectIdentifier(final ILocator l) {
		return byIdentity(subjectIdentifiers, l);
	}

	/**
	 * Return the topic identified by the given subject-locator.
	 * 
	 * @param l
	 *            the subject-locator
	 * @return the topic or <code>null</code>
	 */
	public ITopic bySubjectLocator(final ILocator l) {
		return byIdentity(subjectLocators, l);
	}

	/**
	 * Cache the mapping between the id and the construct into internal store.
	 * 
	 * @param id
	 *            the id
	 * @param c
	 *            the construct
	 */
	public void cacheId(final String id, IConstruct c) {
		if (ids == null) {
			ids = new TreeBidiMap();
		}
		ids.put(id, c);
	}

	/**
	 * Cache the mapping between the item-identifier and the construct into
	 * internal store.
	 * 
	 * @param l
	 *            the item-identifier
	 * @param c
	 *            the construct
	 */
	public void cacheItemIdentifier(final ILocator l, IConstruct c) {
		if (itemIdentifiers == null) {
			itemIdentifiers = HashUtil.getHashMap();
		}
		itemIdentifiers.put(l, c);
	}

	/**
	 * Cache the mapping between the subject-identifier and the topic into
	 * internal store.
	 * 
	 * @param l
	 *            the subject-identifier
	 * @param t
	 *            the topic
	 */
	public void cacheSubjectIdentifier(final ILocator l, ITopic t) {
		if (subjectIdentifiers == null) {
			subjectIdentifiers = HashUtil.getHashMap();
		}
		subjectIdentifiers.put(l, t);
	}

	/**
	 * Cache the mapping between the subject-locator and the topic into internal
	 * store.
	 * 
	 * @param l
	 *            the subject-locator
	 * @param t
	 *            the topic
	 */
	public void cacheSubjectLocator(final ILocator l, ITopic t) {
		if (subjectLocators == null) {
			subjectLocators = HashUtil.getHashMap();
		}
		subjectLocators.put(l, t);
	}

	/**
	 * Secure extraction of the identities of the given construct from the given
	 * map.
	 * 
	 * @param <T>
	 *            the generic type of construct
	 * @param map
	 *            the map
	 * @param construct
	 *            the construct
	 * @return the identities or <code>null</code> if the given map is
	 *         <code>null</code> or does not contain the given key.
	 */
	public <T extends IConstruct> Set<ILocator> getIdentities(
			Map<T, Set<ILocator>> map, T construct) {
		if (map == null || !map.containsKey(construct)) {
			return null;
		}
		return map.get(construct);
	}

	/**
	 * Return all item-identifiers of the given construct.
	 * 
	 * @param c
	 *            the construct
	 * @return the identifiers
	 */
	public Set<ILocator> getItemIdentifiers(IConstruct c) {
		return getIdentities(constructItemIdentifiers, c);
	}

	/**
	 * Return all subject-identifiers of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the identifiers
	 */
	public Set<ILocator> getSubjectIdentifiers(ITopic t) {
		return getIdentities(topicSubjectIdentifiers, t);
	}

	/**
	 * Return all subject-locator of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the locators
	 */
	public Set<ILocator> getSubjectLocators(ITopic t) {
		return getIdentities(topicSubjectLocators, t);
	}

	/**
	 * Cache the item-identifiers of the given construct to the internal store.
	 * 
	 * @param c
	 *            the construct
	 * @param identifiers
	 *            the item-identifiers
	 */
	public void cacheItemIdentifiers(IConstruct c, Set<ILocator> identifiers) {
		if (constructItemIdentifiers == null) {
			constructItemIdentifiers = HashUtil.getHashMap();
		}
		constructItemIdentifiers.put(c, identifiers);
	}

	/**
	 * Cache the subject-identifiers of the given topic to the internal store.
	 * 
	 * @param t
	 *            the topic
	 * @param identifiers
	 *            the subject-identifiers
	 */
	public void cacheSubjectIdentifiers(ITopic t, Set<ILocator> identifiers) {
		if (topicSubjectIdentifiers == null) {
			topicSubjectIdentifiers = HashUtil.getHashMap();
		}
		topicSubjectIdentifiers.put(t, identifiers);
	}

	/**
	 * Cache the subject-locators of the given topic to the internal store.
	 * 
	 * @param t
	 *            the topic
	 * @param identifiers
	 *            the subject-locators
	 */
	public void cacheSubjectLocators(ITopic t, Set<ILocator> identifiers) {
		if (topicSubjectLocators == null) {
			topicSubjectLocators = HashUtil.getHashMap();
		}
		topicSubjectLocators.put(t, identifiers);
	}

	/**
	 * Return all internal stored identifiers.
	 * 
	 * @return all identifiers
	 */
	public Set<ILocator> getIdentifiers() {
		Set<ILocator> ii = getItemIdentifiers();
		Set<ILocator> si = getSubjectIdentifiers();
		Set<ILocator> sl = getSubjectLocators();
		if (ii == null || si == null || sl == null) {
			return null;
		}
		Set<ILocator> set = HashUtil.getHashSet();
		set.addAll(ii);
		set.addAll(si);
		set.addAll(sl);
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Extract the identities contained by the current topic map of the
	 * specified type.
	 * 
	 * @param key
	 *            the type of identities ( item-identifiers,
	 *            subject-identifiers, subject-locators )
	 * @return the identifiers or <code>null</code>
	 */
	public Set<ILocator> getIdentities(Key key) {
		if (identities == null || !identities.containsKey(key)) {
			return null;
		}
		return identities.get(key);
	}

	/**
	 * Return all internal stored item-identifiers.
	 * 
	 * @return all item-identifiers
	 */
	public Set<ILocator> getItemIdentifiers() {
		return getIdentities(Key.ITEM_IDENTIFIER);
	}

	/**
	 * Return all internal stored subject-identifiers.
	 * 
	 * @return all subject-identifiers
	 */
	public Set<ILocator> getSubjectIdentifiers() {
		return getIdentities(Key.SUBJECT_IDENTIFIER);
	}

	/**
	 * Return all internal stored subject-locators.
	 * 
	 * @return all subject-locators
	 */
	public Set<ILocator> getSubjectLocators() {
		return getIdentities(Key.SUBJEC_LOCATOR);
	}

	/**
	 * Cache the given identities to the internal cache
	 * 
	 * @param key
	 *            the key declare the type of identities
	 * @param locators
	 *            the locators
	 */
	public void cacheIdentities(Key key, Set<ILocator> locators) {
		if (identities == null) {
			identities = HashUtil.getHashMap();
		}
		identities.put(key, locators);
	}

	/**
	 * Returns the best label for the given topic
	 * 
	 * @param t
	 *            the topic
	 * @return the best label
	 */
	public String getBestLabel(ITopic t) {
		if (bestLabels == null || !bestLabels.containsKey(t)) {
			return null;
		}
		return bestLabels.get(t);
	}

	/**
	 * Cache the best label of the given topic to the internal store.
	 * 
	 * @param t
	 *            the topic
	 * @param bestLabel
	 *            the best label
	 */
	public void cacheBestLabel(ITopic t, String bestLabel) {
		if (bestLabels == null) {
			bestLabels = HashUtil.getHashMap();
		}
		bestLabels.put(t, bestLabel);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * topic was removed
		 */
		if (event == TopicMapEventType.TOPIC_REMOVED) {
			ITopic topic = (ITopic) oldValue;
			/*
			 * clear subject-identifiers
			 */
			if (topicSubjectIdentifiers != null
					&& topicSubjectIdentifiers.containsKey(oldValue)) {
				if (subjectIdentifiers != null) {
					for (ILocator si : topicSubjectIdentifiers.get(topic)) {
						subjectIdentifiers.remove(si);
					}
				}
				topicSubjectIdentifiers.remove(topic);
			}
			/*
			 * clear subject-locators
			 */
			if (topicSubjectLocators != null
					&& topicSubjectLocators.containsKey(oldValue)) {
				if (subjectLocators != null) {
					for (ILocator si : topicSubjectLocators.get(topic)) {
						subjectLocators.remove(si);
					}
				}
				topicSubjectLocators.remove(topic);
			}
			/*
			 * clear item-identifiers
			 */
			if (constructItemIdentifiers != null
					&& constructItemIdentifiers.containsKey(oldValue)) {
				if (itemIdentifiers != null) {
					for (ILocator si : constructItemIdentifiers.get(topic)) {
						itemIdentifiers.remove(si);
					}
				}
				constructItemIdentifiers.remove(topic);
			}
			/*
			 * clear id
			 */
			if (ids != null) {
				ids.remove(topic.getId());
			}
			/*
			 * remove best label
			 */
			if (bestLabels != null) {
				bestLabels.remove(topic);
			}
			/*
			 * clear identities
			 */
			identities.clear();
		}
		/*
		 * construct was removed
		 */
		else if (event == TopicMapEventType.NAME_REMOVED
				|| event == TopicMapEventType.ROLE_REMOVED
				|| event == TopicMapEventType.OCCURRENCE_REMOVED
				|| event == TopicMapEventType.VARIANT_REMOVED
				|| event == TopicMapEventType.ASSOCIATION_REMOVED) {
			IConstruct construct = (IConstruct) oldValue;
			/*
			 * clear item-identifiers
			 */
			if (constructItemIdentifiers != null
					&& constructItemIdentifiers.containsKey(oldValue)) {
				if (itemIdentifiers != null) {
					for (ILocator si : constructItemIdentifiers.get(construct)) {
						itemIdentifiers.remove(si);
					}
				}
				constructItemIdentifiers.remove(construct);
			}
			/*
			 * clear id
			 */
			if (ids != null) {
				ids.remove(construct.getId());
			}
			/*
			 * clear identities
			 */
			identities.remove(Key.ITEM_IDENTIFIER);
		}
		/*
		 * subject-identifier added
		 */
		else if ( event == TopicMapEventType.SUBJECT_IDENTIFIER_ADDED ){
			ITopic topic = (ITopic) notifier;
			ILocator locator = (ILocator) newValue;
			if ( topicSubjectIdentifiers != null && topicSubjectIdentifiers.containsKey(topic)){
				topicSubjectIdentifiers.get(topic).add(locator);
			}
			cacheSubjectIdentifier(locator, topic);
			if ( identities != null && identities.containsKey(Key.SUBJECT_IDENTIFIER)){
				identities.get(Key.SUBJECT_IDENTIFIER).add(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(topic);
			}
		}
		/*
		 * subject-locator added
		 */
		else if ( event == TopicMapEventType.SUBJECT_LOCATOR_ADDED ){
			ITopic topic = (ITopic) notifier;
			ILocator locator = (ILocator) newValue;
			if ( topicSubjectLocators != null && topicSubjectLocators.containsKey(topic)){
				topicSubjectLocators.get(topic).add(locator);
			}
			cacheSubjectLocator(locator, topic);
			if ( identities != null && identities.containsKey(Key.SUBJEC_LOCATOR)){
				identities.get(Key.SUBJEC_LOCATOR).add(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(topic);
			}
		}
		/*
		 * item-identifier added
		 */
		else if ( event == TopicMapEventType.ITEM_IDENTIFIER_ADDED ){
			IConstruct construct = (IConstruct) notifier;
			ILocator locator = (ILocator) newValue;
			if ( constructItemIdentifiers != null && constructItemIdentifiers.containsKey(construct)){
				constructItemIdentifiers.get(construct).add(locator);
			}
			cacheItemIdentifier(locator, construct);
			if ( identities != null && identities.containsKey(Key.ITEM_IDENTIFIER)){
				identities.get(Key.ITEM_IDENTIFIER).add(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(construct);
			}
		}
		/*
		 * subject-identifier removed
		 */
		else if ( event == TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED ){
			ITopic topic = (ITopic) notifier;
			ILocator locator = (ILocator) oldValue;
			if ( topicSubjectIdentifiers != null && topicSubjectIdentifiers.containsKey(topic)){
				topicSubjectIdentifiers.get(topic).remove(locator);
			}
			if ( subjectIdentifiers != null ){
				subjectIdentifiers.remove(locator);
			}
			if ( identities != null && identities.containsKey(Key.SUBJECT_IDENTIFIER)){
				identities.get(Key.SUBJECT_IDENTIFIER).remove(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(topic);
			}
		}
		/*
		 * subject-locator removed
		 */
		else if ( event == TopicMapEventType.SUBJECT_LOCATOR_REMOVED ){
			ITopic topic = (ITopic) notifier;
			ILocator locator = (ILocator) oldValue;
			if ( topicSubjectLocators != null && topicSubjectLocators.containsKey(topic)){
				topicSubjectLocators.get(topic).remove(locator);
			}
			if ( subjectLocators != null ){
				subjectLocators.remove(locator);
			}
			if ( identities != null && identities.containsKey(Key.SUBJEC_LOCATOR)){
				identities.get(Key.SUBJEC_LOCATOR).remove(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(topic);
			}
		}
		/*
		 * item-identifier removed
		 */
		else if ( event == TopicMapEventType.ITEM_IDENTIFIER_REMOVED ){
			IConstruct construct = (IConstruct) notifier;
			ILocator locator = (ILocator) oldValue;
			if ( constructItemIdentifiers != null && constructItemIdentifiers.containsKey(construct)){
				constructItemIdentifiers.get(construct).remove(locator);
			}
			if ( itemIdentifiers != null ){
				itemIdentifiers.remove(locator);
			}
			if ( identities != null && identities.containsKey(Key.ITEM_IDENTIFIER)){
				identities.get(Key.ITEM_IDENTIFIER).remove(locator);
			}
			if ( bestLabels != null ){
				bestLabels.remove(construct);
			}
		}
		
	}
}
