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
package de.topicmapslab.majortom.database.transaction.cache;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.transaction.LazyStubCreator;
import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of a store object containing all identity informations
 * 
 * @author Sven Krosse
 * 
 */
public class IdentityCache implements IDataStore {

	/**
	 * cache of lazy constructs
	 */
	private Map<String, IConstruct> lazyStubs;
	private Set<IScope> lazyScopes;

	/**
	 * storage map of id-construct relation of the topic map engine
	 */
	private BidiMap ids;

	/**
	 * storage map of the reference-locator mapping of the topic map
	 */
	private Map<String, ILocator> locators;

	/**
	 * item-identifier mapping of the topic map engine
	 */
	private Map<ILocator, IConstruct> itemIdentitiers;

	/**
	 * removed item-identifiers
	 */
	private Set<ILocator> removedItemIdentifiers;

	/**
	 * construct to item-identifiers mapping
	 */
	private Map<IConstruct, Set<ILocator>> constructItemIdentitiers;

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
	 * a set containing all topics
	 */
	private Set<ITopic> topics;

	/**
	 * reference to the underlying topic map store
	 */
	private final TransactionTopicMapStore topicMapStore;

	private Set<IConstruct> removedConstructs;

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the real store
	 */
	public IdentityCache(TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (ids != null) {
			ids.clear();
		}
		if (locators != null) {
			locators.clear();
		}
		if (itemIdentitiers != null) {
			itemIdentitiers.clear();
		}
		if (subjectIdentifiers != null) {
			subjectIdentifiers.clear();
		}
		if (subjectLocators != null) {
			subjectLocators.clear();
		}
		if (constructItemIdentitiers != null) {
			constructItemIdentitiers.clear();
		}
		if (topicSubjectIdentifiers != null) {
			topicSubjectIdentifiers.clear();
		}
		if (topicSubjectLocators != null) {
			topicSubjectLocators.clear();
		}
		if (topics != null) {
			topics.clear();
		}
		if (removedConstructs != null) {
			removedConstructs.clear();
		}
		if (removedItemIdentifiers != null) {
			removedItemIdentifiers.clear();
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
		IConstruct c = null;
		if (ids == null) {
			c = redirectById(id);
		} else {
			c = (IConstruct) ids.get(id);
		}
		if (!isRemovedConstruct(c)) {
			return c;
		}
		return null;
	}

	/**
	 * Internal method to redirect the call to the underlying store and cache
	 * the result.
	 * 
	 * @param id
	 *            the id
	 * @return the construct or <code>null</code>
	 */
	protected IConstruct redirectById(final String id) {
		IConstruct c = (IConstruct) getTopicMapStore().doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.BY_ID, id);
		if (ids == null) {
			ids = new TreeBidiMap();
		}
		ids.put(id, c);
		return c;
	}

	/**
	 * Return the construct identified by the given item-identifier.
	 * 
	 * @param l
	 *            the item-identifier
	 * @return the construct or <code>null</code>
	 */
	public IConstruct byItemIdentifier(final ILocator l) {
		if (removedItemIdentifiers != null && removedItemIdentifiers.contains(l)) {
			return null;
		}
		IConstruct c = null;
		if (itemIdentitiers == null) {
			c = redirectByItemIdentifier(l);
		} else {
			c = itemIdentitiers.get(l);
		}
		if (!isRemovedConstruct(c)) {
			return c;
		}
		return null;
	}

	/**
	 * Internal method to redirect the call to the underlying store and cache
	 * the result.
	 * 
	 * @param l
	 *            the item-identifier
	 * @return the construct or <code>null</code>
	 */
	protected IConstruct redirectByItemIdentifier(final ILocator l) {
		IConstruct c = (IConstruct) getTopicMapStore().doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, l);
		if (itemIdentitiers == null) {
			itemIdentitiers = HashUtil.getHashMap();
		}
		itemIdentitiers.put(l, c);
		return c;
	}

	/**
	 * Return the topic identified by the given subject-identifier.
	 * 
	 * @param l
	 *            the subject-identifier
	 * @return the topic or <code>null</code>
	 */
	public ITopic bySubjectIdentifier(final ILocator l) {
		ITopic t = null;
		if (subjectIdentifiers == null) {
			t = redirectBySubjectIdentifier(l);
		} else {
			t = subjectIdentifiers.get(l);
		}
		if (!isRemovedConstruct(t)) {
			return t;
		}
		return null;
	}

	/**
	 * Internal method to redirect the call to the underlying store and cache
	 * the result.
	 * 
	 * @param l
	 *            the subject-identifier
	 * @return the topic or <code>null</code>
	 */
	protected ITopic redirectBySubjectIdentifier(final ILocator l) {
		ITopic t = (ITopic) getTopicMapStore().doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, l);
		if (subjectIdentifiers == null) {
			subjectIdentifiers = HashUtil.getHashMap();
		}
		subjectIdentifiers.put(l, t);
		return t;
	}

	/**
	 * Return the topic identified by the given subject-locator.
	 * 
	 * @param l
	 *            the subject-locator
	 * @return the topic or <code>null</code>
	 */
	public ITopic bySubjectLocator(final ILocator l) {
		ITopic t = null;
		if (subjectLocators == null) {
			t = redirectBySubjectLocator(l);
		} else {
			t = subjectLocators.get(l);
		}
		if (!isRemovedConstruct(t)) {
			return t;
		}
		return null;
	}

	/**
	 * Internal method to redirect the call to the underlying store and cache
	 * the result.
	 * 
	 * @param l
	 *            the subject-locator
	 * @return the topic or <code>null</code>
	 */
	protected ITopic redirectBySubjectLocator(final ILocator l) {
		ITopic t = (ITopic) getTopicMapStore().doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, l);
		if (subjectLocators == null) {
			subjectLocators = HashUtil.getHashMap();
		}
		subjectLocators.put(l, t);
		return t;
	}

	/**
	 * Return all item-identifiers of the given construct.
	 * 
	 * @param c
	 *            the construct
	 * @return the identifiers
	 */
	public Set<ILocator> getItemIdentifiers(IConstruct c) {
		if (isRemovedConstruct(c)) {
			throw new TopicMapStoreException("Construct is already marked as removed!");
		}
		if (constructItemIdentitiers == null || !constructItemIdentitiers.containsKey(c)) {
			return redirectGetItemIdentifiers(c);
		}
		return constructItemIdentitiers.get(c);
	}

	/**
	 * Internal method to redirect the call to the underlying topic map store
	 * and cache the result.
	 * 
	 * @param c
	 *            the construct
	 * @return all item identifiers
	 */
	@SuppressWarnings("unchecked")
	protected Set<ILocator> redirectGetItemIdentifiers(IConstruct c) {
		Set<ILocator> locators = (Set<ILocator>) getTopicMapStore().doRead(c, TopicMapStoreParameterType.ITEM_IDENTIFIER);
		if (constructItemIdentitiers == null) {
			constructItemIdentitiers = HashUtil.getHashMap();
		}
		constructItemIdentitiers.put(c, locators);
		return locators;
	}

	/**
	 * Return all subject-identifiers of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the identifiers
	 */
	public Set<ILocator> getSubjectIdentifiers(ITopic t) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		if (topicSubjectIdentifiers == null || !topicSubjectIdentifiers.containsKey(t)) {
			return redirectGetSubjectIdentifiers(t);
		}
		return topicSubjectIdentifiers.get(t);
	}

	/**
	 * Internal method to redirect the call to the underlying topic map store
	 * and cache the result.
	 * 
	 * @param t
	 *            the topic
	 * @return all subject identifiers
	 */
	@SuppressWarnings("unchecked")
	protected Set<ILocator> redirectGetSubjectIdentifiers(ITopic t) {
		Set<ILocator> locators = (Set<ILocator>) getTopicMapStore().doRead(t, TopicMapStoreParameterType.SUBJECT_IDENTIFIER);
		if (topicSubjectIdentifiers == null) {
			topicSubjectIdentifiers = HashUtil.getHashMap();
		}
		topicSubjectIdentifiers.put(t, locators);
		return locators;
	}

	/**
	 * Return all subject-locator of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the locators
	 */
	public Set<ILocator> getSubjectLocators(ITopic t) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		if (topicSubjectLocators == null || !topicSubjectLocators.containsKey(t)) {
			return redirectGetSubjectLocators(t);
		}
		return topicSubjectLocators.get(t);
	}

	/**
	 * Internal method to redirect the call to the underlying topic map store
	 * and cache the result.
	 * 
	 * @param t
	 *            the topic
	 * @return all subject locators
	 */
	@SuppressWarnings("unchecked")
	protected Set<ILocator> redirectGetSubjectLocators(ITopic t) {
		Set<ILocator> locators = (Set<ILocator>) getTopicMapStore().doRead(t, TopicMapStoreParameterType.SUBJECT_LOCATOR);
		if (topicSubjectLocators == null) {
			topicSubjectLocators = HashUtil.getHashMap();
		}
		topicSubjectLocators.put(t, locators);
		return locators;
	}

	/**
	 * Register the id for the given construct
	 * 
	 * @param c
	 *            the construct
	 * @param id
	 *            the id
	 */
	public void setId(final IConstruct c, final String id) {
		if (ids == null) {
			ids = new TreeBidiMap();
		}
		if (c instanceof ITopic) {
			if (topics == null) {
				topics = HashUtil.getHashSet();
			}
			topics.add((ITopic) c);
		}
		this.ids.put(id, c);
	}

	/**
	 * Register a item-identifier for the given construct
	 * 
	 * @param c
	 *            the construct
	 * @param identifier
	 *            the item-identifier
	 */
	public void addItemIdentifer(final IConstruct c, final ILocator identifier) {
		if (isRemovedConstruct(c)) {
			throw new TopicMapStoreException("Construct is already marked as removed!");
		}
		/*
		 * remove marked item-identifier from deletion list
		 */
		if (removedItemIdentifiers != null) {
			removedItemIdentifiers.remove(identifier);
		}
		if (itemIdentitiers == null) {
			itemIdentitiers = HashUtil.getHashMap();
		}
		this.itemIdentitiers.put(identifier, c);

		/*
		 * store backward relation
		 */
		if (constructItemIdentitiers == null || !constructItemIdentitiers.containsKey(c)) {
			redirectGetItemIdentifiers(c);
		}

		Set<ILocator> set = constructItemIdentitiers.get(c);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(identifier);
		constructItemIdentitiers.put(c, set);
	}

	/**
	 * Register a subject-identifier for the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param identifier
	 *            the subject-identifier
	 */
	public void addSubjectIdentifier(final ITopic t, final ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		if (subjectIdentifiers == null) {
			subjectIdentifiers = HashUtil.getHashMap();
		}
		this.subjectIdentifiers.put(identifier, t);

		/*
		 * store backward relation
		 */
		if (topicSubjectIdentifiers == null || !topicSubjectIdentifiers.containsKey(t)) {
			redirectGetSubjectIdentifiers(t);
		}

		Set<ILocator> set = topicSubjectIdentifiers.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(identifier);
		topicSubjectIdentifiers.put(t, set);
	}

	/**
	 * Register a subject-locator for the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param locator
	 *            the subject-locator
	 */
	public void addSubjectLocator(final ITopic t, final ILocator locator) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		if (subjectLocators == null) {
			subjectLocators = HashUtil.getHashMap();
		}
		this.subjectLocators.put(locator, t);

		/*
		 * store backward relation
		 */
		if (topicSubjectLocators == null || !topicSubjectLocators.containsKey(t)) {
			redirectGetSubjectLocators(t);
		}

		Set<ILocator> set = topicSubjectLocators.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(locator);
		topicSubjectLocators.put(t, set);
	}

	/**
	 * Unregister the id of the construct.
	 * 
	 * @param c
	 *            the construct
	 * @param id
	 *            the id
	 */
	public void removeId(final IConstruct c, final String id) {
		if (ids == null) {
			throw new TopicMapStoreException("Id is unknown and cannot remove.");
		}
		this.ids.remove(id);
	}

	/**
	 * Unregister a item-identifier for the given construct
	 * 
	 * @param c
	 *            the construct
	 * @param identifier
	 *            the item-identifier
	 */
	public void removeItemIdentifer(final IConstruct c, final ILocator identifier) {
		if (isRemovedConstruct(c)) {
			throw new TopicMapStoreException("Construct is already marked as removed!");
		}
		redirectByItemIdentifier(identifier);
		if (itemIdentitiers == null) {
			throw new TopicMapStoreException("Identifier is unknown and cannot remove.");
		}
		this.itemIdentitiers.remove(identifier);

		/*
		 * remove backward relation
		 */
		if (constructItemIdentitiers == null || !constructItemIdentitiers.containsKey(c)) {
			redirectGetItemIdentifiers(c);
		}
		Set<ILocator> set = constructItemIdentitiers.get(c);
		set.remove(identifier);
		constructItemIdentitiers.put(c, set);

		/*
		 * mark item-identifier as removed
		 */
		if (removedItemIdentifiers == null) {
			removedItemIdentifiers = HashUtil.getHashSet();
		}
		removedItemIdentifiers.add(identifier);
	}

	/**
	 * Unregister a subject-identifier for the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param identifier
	 *            the subject-identifier
	 */
	public void removeSubjectIdentifier(final ITopic t, final ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		redirectBySubjectIdentifier(identifier);
		if (subjectIdentifiers == null) {
			throw new TopicMapStoreException("Identifier is unknown and cannot remove.");
		}
		this.subjectIdentifiers.remove(identifier);

		/*
		 * remove backward relation
		 */
		if (topicSubjectIdentifiers == null || !topicSubjectIdentifiers.containsKey(t)) {
			redirectGetSubjectIdentifiers(t);
		}
		Set<ILocator> set = topicSubjectIdentifiers.get(t);
		set.remove(identifier);
		topicSubjectIdentifiers.put(t, set);
	}

	/**
	 * Unregister a subject-locator for the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param locator
	 *            the subject-locator
	 */
	public void removeSubjectLocator(final ITopic t, final ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new TopicMapStoreException("Topic is already marked as removed!");
		}
		redirectBySubjectLocator(identifier);
		if (subjectLocators == null) {
			throw new TopicMapStoreException("Identifier is unknown and cannot remove.");
		}
		this.subjectLocators.remove(identifier);

		/*
		 * remove backward relation
		 */
		if (topicSubjectLocators == null || !topicSubjectLocators.containsKey(t)) {
			redirectGetSubjectLocators(t);
		}
		Set<ILocator> set = topicSubjectLocators.get(t);
		set.remove(identifier);
		topicSubjectLocators.put(t, set);
	}

	/**
	 * Remove the construct from the internal store
	 * 
	 * @param c
	 *            the construct
	 */
	public void removeConstruct(IConstruct c) {
		if (c instanceof ITopic) {
			removeTopic((ITopic) c);
		} else {
			/*
			 * Remove item identifiers
			 */
			if (constructItemIdentitiers != null && constructItemIdentitiers.containsKey(c)) {
				for (ILocator l : constructItemIdentitiers.get(c)) {
					itemIdentitiers.remove(l);
				}
				constructItemIdentitiers.remove(c);
			}
			/*
			 * remove id
			 */
			if (ids != null) {
				ids.removeValue(c);
			}
			if (removedConstructs == null) {
				removedConstructs = HashUtil.getHashSet();
			}
			removedConstructs.add(c);
		}
	}

	/**
	 * Removing the given topic from internal store
	 * 
	 * @param t
	 *            the topic
	 */
	public void removeTopic(ITopic t) {
		/*
		 * Remove item identifiers
		 */
		if (constructItemIdentitiers != null && constructItemIdentitiers.containsKey(t)) {
			for (ILocator l : constructItemIdentitiers.get(t)) {
				itemIdentitiers.remove(l);
			}
			constructItemIdentitiers.remove(t);
		}
		/*
		 * remove subject-identifiers
		 */
		if (topicSubjectIdentifiers != null && topicSubjectIdentifiers.containsKey(t)) {
			for (ILocator l : topicSubjectIdentifiers.get(t)) {
				subjectIdentifiers.remove(l);
			}
			topicSubjectIdentifiers.remove(t);
		}
		/*
		 * remove subject-locators
		 */
		if (topicSubjectLocators != null && topicSubjectLocators.containsKey(t)) {
			for (ILocator l : topicSubjectLocators.get(t)) {
				subjectLocators.remove(l);
			}
			topicSubjectLocators.remove(t);
		}

		/*
		 * remove topic
		 */
		if (topics != null) {
			topics.remove(t);
		}

		/*
		 * remove id
		 */
		if (ids != null) {
			ids.removeValue(t);
		}
		if (removedConstructs == null) {
			removedConstructs = HashUtil.getHashSet();
		}
		removedConstructs.add(t);
	}

	/**
	 * Creates a new locator instance of the reference is unknown or return the
	 * stored instance
	 * 
	 * @param reference
	 *            the reference
	 * @return the locator
	 */
	public ILocator createLocator(final String reference) {
		if (locators == null) {
			locators = HashUtil.getHashMap();
		} else if (locators.containsKey(reference)) {
			return locators.get(reference);
		}
		ILocator l = new LocatorImpl(reference);
		locators.put(reference, l);
		return l;
	}

	/**
	 * Returns all topics of the internal store
	 * 
	 * @return all topics
	 */
	public Set<ITopic> getTopics() {
		Set<ITopic> set = HashUtil.getHashSet();
		for (ITopic t : redirectGetTopics()) {
			if (!isRemovedConstruct(t)) {
				set.add(t);
			}
		}
		if (this.topics != null) {
			set.addAll(this.topics);
		}
		return set;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the results.
	 * 
	 * @return all topics
	 */
	@SuppressWarnings("unchecked")
	protected Set<ITopic> redirectGetTopics() {
		return (Set<ITopic>) getTopicMapStore().doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.TOPIC);
	}

	/**
	 * Create a random item-identifier.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @return the item-identifier
	 */
	public ILocator createItemIdentifier(ITopicMap topicMap) {
		String itemIdentifier = topicMap.getLocator().getReference() + "/" + UUID.randomUUID().toString();
		return createLocator(itemIdentifier);
	}

	public <T extends IConstruct> void replace(T construct, T replacement) {
		if (construct instanceof ITopic) {
			replace((ITopic) construct, (ITopic) replacement);
		} else {
			getItemIdentifiers(construct);
			getItemIdentifiers(replacement);
			/*
			 * move all item-identifier
			 */
			for (ILocator itemIdentifier : getItemIdentifiers(construct)) {
				removeItemIdentifer(construct, itemIdentifier);
				addItemIdentifer(replacement, itemIdentifier);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		Set<ILocator> itemIdentifiers = HashUtil.getHashSet(getItemIdentifiers(topic));
		Set<ILocator> subjectIdentifiers = HashUtil.getHashSet(getSubjectIdentifiers(topic));
		Set<ILocator> subjectLocators = HashUtil.getHashSet(getSubjectLocators(topic));

		/*
		 * move all item-identifier
		 */
		for (ILocator itemIdentifier : itemIdentifiers) {
			removeItemIdentifer(topic, itemIdentifier);
			addItemIdentifer(replacement, itemIdentifier);
		}

		/*
		 * move all subject-identifier
		 */
		for (ILocator subjectIdentifier : subjectIdentifiers) {
			removeSubjectIdentifier(topic, subjectIdentifier);
			addSubjectIdentifier(replacement, subjectIdentifier);
		}
		/*
		 * move all subject-locator
		 */
		for (ILocator subjectLocator : subjectLocators) {
			removeSubjectLocator(topic, subjectLocator);
			addSubjectLocator(replacement, subjectLocator);
		}
	}

	/**
	 * Return all internal stored identifiers.
	 * 
	 * @return all identifiers
	 */
	public Set<ILocator> getIdentifiers() {
		Set<ILocator> set = HashUtil.getHashSet();
		set.addAll(getItemIdentifiers());
		set.addAll(getSubjectIdentifiers());
		set.addAll(getSubjectLocators());
		return set;
	}

	/**
	 * Return all internal stored item-identifiers.
	 * 
	 * @return all item-identifiers
	 */
	public Set<ILocator> getItemIdentifiers() {
		IIdentityIndex index = getTopicMapStore().getIndex(IIdentityIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ILocator> set = HashUtil.getHashSet();
		for (Locator l : index.getItemIdentifiers()) {
			if (removedItemIdentifiers == null || !removedItemIdentifiers.contains(l)) {
				if (!isRemovedConstruct(byItemIdentifier((ILocator) l))) {
					set.add((ILocator) l);
				}
			}
		}
		if (itemIdentitiers != null) {
			set.addAll(itemIdentitiers.keySet());
		}
		return set;
	}

	/**
	 * Return all internal stored subject-identifiers.
	 * 
	 * @return all subject-identifiers
	 */
	public Set<ILocator> getSubjectIdentifiers() {
		Set<ILocator> set = HashUtil.getHashSet();
		for (ITopic t : getTopics()) {
			set.addAll(getSubjectIdentifiers(t));
		}
		return set;
	}

	/**
	 * Return all internal stored subject-locators.
	 * 
	 * @return all subject-locators
	 */
	public Set<ILocator> getSubjectLocators() {
		Set<ILocator> set = HashUtil.getHashSet();
		for (ITopic t : getTopics()) {
			set.addAll(getSubjectLocators(t));
		}
		return set;
	}

	/**
	 * Checks if the given locator is used as identifier.
	 * 
	 * @param locator
	 *            the identifier
	 * @return <code>true</code> if the locator is used as identifier,
	 *         <code>false</code> otherwise.
	 */
	public boolean containsIdentifier(ILocator locator) {
		return containsItemIdentifier(locator) || containsSubjectIdentifier(locator) || containsSubjectLocator(locator);
	}

	/**
	 * Checks if the given locator is used as item-identifier.
	 * 
	 * @param locator
	 *            the item-identifier
	 * @return <code>true</code> if the locator is used as item-identifier,
	 *         <code>false</code> otherwise.
	 */
	public boolean containsItemIdentifier(ILocator locator) {
		return getItemIdentifiers().contains(locator);
	}

	/**
	 * Checks if the given locator is used as subject-identifier.
	 * 
	 * @param locator
	 *            the subject-identifier
	 * @return <code>true</code> if the locator is used as subject-identifier,
	 *         <code>false</code> otherwise.
	 */
	public boolean containsSubjectIdentifier(ILocator locator) {
		return getSubjectIdentifiers().contains(locator);
	}

	/**
	 * Checks if the given locator is used as subject-locator.
	 * 
	 * @param locator
	 *            the subject-locator
	 * @return <code>true</code> if the locator is used as subject-locator,
	 *         <code>false</code> otherwise.
	 */
	public boolean containsSubjectLocator(ILocator locator) {
		return getSubjectLocators().contains(locator);
	}

	/**
	 * Return the internal stored id of the given construct
	 * 
	 * @param construct
	 *            the construct
	 * @return the internal id and never <code>null</code>. If the construct is
	 *         unknown an exception will be thrown.
	 */
	public String getId(IConstruct construct) {
		if (ids == null || !ids.containsValue(construct)) {
			throw new TopicMapStoreException("Unkown construct instance.");
		}
		return (String) ids.getKey(construct);
	}

	/**
	 * @return the topicMapStore
	 */
	public TopicMapStoreImpl getTopicMapStore() {
		return topicMapStore.getRealStore();
	}

	/**
	 * Method checks if the given constructs is stored as removed.
	 * 
	 * @param construct
	 *            the construct
	 * @return <code>true</code> if the construct was removed before,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isRemovedConstruct(IConstruct construct) {
		if (removedConstructs != null && removedConstructs.contains(construct)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the store know at least one subject locator for the given topic
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if at least one subject locator is store for
	 *         the given topic, <code>false</code> otherwise.
	 */
	protected boolean containsSubjectLocators(ITopic topic) {
		if (topicSubjectLocators == null) {
			return false;
		}
		return topicSubjectLocators.containsKey(topic);
	}

	/**
	 * Checks if the store know at least one subject identifier for the given
	 * topic
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if at least one subject identifier is store for
	 *         the given topic, <code>false</code> otherwise.
	 */
	protected boolean containsSubjectIdentifiers(ITopic topic) {
		if (topicSubjectIdentifiers == null) {
			return false;
		}
		return topicSubjectIdentifiers.containsKey(topic);
	}

	/**
	 * Checks if the store know at least one item identifier for the given
	 * construct
	 * 
	 * @param construct
	 *            the construct
	 * @return <code>true</code> if at least one item identifier is store for
	 *         the given construct, <code>false</code> otherwise.
	 */
	protected boolean containsItemIdentifiers(IConstruct construct) {
		if (constructItemIdentitiers == null) {
			return false;
		}
		return constructItemIdentitiers.containsKey(construct);
	}

	/**
	 * Checks if the given construct is stored by the identity store.
	 * 
	 * @param construct
	 *            the construct
	 * @return <code>true</code> if the id is known by the store,
	 *         <code>false</code> otherwise.
	 */
	protected final boolean containsConstruct(IConstruct construct) {
		return ids != null && ids.containsKey(construct.getId());
	}

	/**
	 * Creates a lazy stub of the given construct
	 * 
	 * @param c
	 *            the construct
	 * @return the lazy stub
	 * @throws ConstructRemovedException
	 *             thrown if the id of the given construct is marked as removed
	 */
	@SuppressWarnings("unchecked")
	public <T extends IConstruct> T createLazyStub(T c) throws ConstructRemovedException {
		if (lazyStubs == null) {
			lazyStubs = HashUtil.getHashMap();
		}
		if (c == null) {
			return null;
		}
		if (removedConstructs != null && removedConstructs.contains(c)) {
			throw new ConstructRemovedException(c);
		}
		if (!lazyStubs.containsKey(c.getId())) {
			T construct = LazyStubCreator.createLazyStub(c, topicMapStore.getTransaction());
			lazyStubs.put(c.getId(), construct);
			return construct;
		}
		return (T) lazyStubs.get(c.getId());
	}
	


	/**
	 * Checks if at least one theme of the given scope was deleted by the
	 * current transaction context.
	 * 
	 * @param scope
	 *            the scope
	 * @return <code>true</code> if at least one theme of the given scope was
	 *         deleted by the current transaction, <code>false</code> otherwise.
	 */
	public boolean isRemovedScope(IScope scope) {
		if (scope != null) {
			for (ITopic theme : scope.getThemes()) {
				if (isRemovedConstruct(theme)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Creates a lazy stub of the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the lazy stub
	 * @throws ConstructRemovedException
	 *             thrown if at least one contained theme is marked as removed
	 */
	public IScope createLazyStub(IScope scope) throws ConstructRemovedException {
		if (lazyScopes == null) {
			lazyScopes = HashUtil.getHashSet();
		}
		if (scope == null) {
			return null;
		}
		if (!lazyScopes.contains(scope)) {
			Set<ITopic> themes = HashUtil.getHashSet();
			for (ITopic theme : scope.getThemes()) {
				themes.add(createLazyStub(theme));
			}
			return new ScopeImpl(themes);
		}
		return scope;
	}
}
