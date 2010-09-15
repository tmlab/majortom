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

package de.topicmapslab.majortom.core;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of {@link ITopicMap}
 * 
 * @author Sven Krosse
 * 
 */
public class TopicMapImpl extends ReifiableImpl implements ITopicMap {

	/**
	 * the reference to the topic map store
	 */
	private ITopicMapStore store;

	/**
	 * the locator of the topic map
	 */
	private final Locator locator;

	/**
	 * the parent topic map system
	 */
	private final ITopicMapSystem topicMapSystem;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the parent topic map system
	 * @param locator
	 *            the locator of the topicmap
	 */
	public TopicMapImpl(ITopicMapSystem topicMapSystem, Locator locator) {
		super(null, null, null);
		this.locator = locator;
		this.topicMapSystem = topicMapSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTag(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name attribute cannot be null.");
		}
		getStore().doModify(this, TopicMapStoreParameterType.TAG, name);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTag(String name, Calendar timestamp) {
		if (name == null) {
			throw new IllegalArgumentException("Name attribute cannot be null.");
		}
		if (timestamp == null) {
			throw new IllegalArgumentException("Timestamp attribute cannot be null.");
		}
		getStore().doModify(this, TopicMapStoreParameterType.TAG, name, timestamp);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTopicMapListener(ITopicMapListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener attribute cannot be null.");
		}
		getStore().addTopicMapListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociations(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Association type filter cannot be null.");
		}
		return Collections.unmodifiableCollection((Collection<T>) getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, type));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociations(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Association scope filter cannot be null.");
		}
		return Collections.unmodifiableCollection((Collection<T>) getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociations(Topic type, IScope scope) {
		if (type == null) {
			throw new IllegalArgumentException("Association type filter cannot be null.");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Association scope filter cannot be null.");
		}
		return Collections.unmodifiableCollection((Collection<T>) getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, type, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMapStore getStore() {
		if (store == null) {
			throw new RuntimeException("Store is null!");
		}
		return store;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void setStore(final ITopicMapStore store) {
		if (store == null) {
			throw new IllegalArgumentException("Store argument cannot be null.");
		}
		if (this.store != null) {
			this.store.close();
		}
		this.store = store;
		((TopicMapStoreImpl)this.store).setTopicMap(this);
		this.store.connect();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Topic> Collection<T> getTopics(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Topic type filter cannot be null.");
		}
		return Collections.unmodifiableCollection((Collection<T>) getStore().doRead(this, TopicMapStoreParameterType.TOPIC, type));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTopicMapListener(ITopicMapListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener argument cannot be null.");
		}
		getStore().removeTopicMapListener(listener);

	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		getStore().close();
	}

	/**
	 * {@inheritDoc}
	 */
	public Association createAssociation(Topic type, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (Association) getStore().doCreate(this, TopicMapStoreParameterType.ASSOCIATION, type, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Association createAssociation(Topic type, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null");
		}
		if (!type.getParent().equals(getTopicMap())) {
			System.out.println(type.getParent().getId()+" = " + getTopicMap().getId());
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (Association) getStore().doCreate(this, TopicMapStoreParameterType.ASSOCIATION, type, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator createLocator(String ref) throws MalformedIRIException {
		return (Locator) getStore().doCreate(this, TopicMapStoreParameterType.LOCATOR, ref);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic createTopic() {
		return (Topic) getStore().doCreate(this, TopicMapStoreParameterType.TOPIC);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic createTopicByItemIdentifier(Locator identifier) throws IdentityConstraintException, ModelConstraintException {
		if (identifier == null) {
			throw new ModelConstraintException(this, "Item-identifier cannot be null");
		}
		Construct c = getConstructByItemIdentifier(identifier);
		if (c == null) {
			Topic topic = getTopicBySubjectIdentifier(identifier);
			if (topic == null) {
				return (Topic) getStore().doCreate(this, TopicMapStoreParameterType.BY_ITEM_IDENTIFER, identifier);
			}
			topic.addItemIdentifier(identifier);
			return topic;
		} else if (c instanceof Topic) {
			return (Topic) c;
		} else {
			c = getConstructByItemIdentifier(identifier);
			
			throw new IdentityConstraintException(c, null, identifier, "Item-Identifier already used by a construct which is not a topic!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic createTopicBySubjectIdentifier(Locator identifier) throws ModelConstraintException {
		if (identifier == null) {
			throw new ModelConstraintException(this, "Subject-identifier cannot be null");
		}
		Topic topic = getTopicBySubjectIdentifier(identifier);
		if (topic != null) {
			return topic;
		}
		Construct c = getConstructByItemIdentifier(identifier);
		if (c == null) {
			return (Topic) getStore().doCreate(this, TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, identifier);
		} else if (c instanceof Topic) {
			topic = (Topic) c;
			topic.addSubjectIdentifier(identifier);
			return topic;
		}
		throw new IdentityConstraintException(c, null, identifier, "Item-Identifier already used by a construct which is not a topic!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic createTopicBySubjectLocator(Locator locator) throws ModelConstraintException {
		if (locator == null) {
			throw new ModelConstraintException(this, "Subject-locator cannot be null");
		}
		Topic topic = getTopicBySubjectLocator(locator);
		return topic != null ? topic : (Topic) getStore().doCreate(this, TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, locator);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Association> getAssociations() {
		return Collections.unmodifiableSet((Set<Association>) getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION));
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructById(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Id cannot be null.");
		}
		return (Construct) getStore().doRead(this, TopicMapStoreParameterType.BY_ID, id);
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(Locator identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Item-identifier cannot be null.");
		}
		return (Construct) getStore().doRead(this, TopicMapStoreParameterType.BY_ITEM_IDENTIFER, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public <I extends Index> I getIndex(Class<I> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Index-clazz cannot be null.");
		}
		return (I) getStore().getIndex(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator getLocator() {
		return locator == null ? (Locator) getStore().doRead(this, TopicMapStoreParameterType.LOCATOR) : locator;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(Locator identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("Subject-identifier cannot be null.");
		}
		return (Topic) getStore().doRead(this, TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(Locator locator) {
		if (locator == null) {
			throw new IllegalArgumentException("Subject-locator cannot be null.");
		}
		return (Topic) getStore().doRead(this, TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, locator);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Topic> getTopics() {
		return Collections.unmodifiableSet((Set<Topic>) getStore().doRead(this, TopicMapStoreParameterType.TOPIC));
	}

	/**
	 * {@inheritDoc}
	 */
	public void mergeIn(TopicMap topicMap) throws ModelConstraintException {
		if (topicMap == null) {
			throw new ModelConstraintException(this, "Other topic map cannot be null.");
		} else if (!topicMap.equals(this)) {
			getStore().doMerge(this, topicMap);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(boolean cascade) throws TopicInUseException {
		getStore().doRemove(this, cascade);
		/*
		 * set store to null
		 */
		this.store = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return (String) getStore().doRead(this, TopicMapStoreParameterType.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction createTransaction() {
		if (!getStore().isTransactable()) {
			throw new UnsupportedOperationException("The current topic map store does not support transactions.");
		}
		return getStore().createTransaction();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope createScope(Collection<Topic> themes) {
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null.");
		}
		Collection<ITopic> topics = HashUtil.getHashSet();
		for (Topic theme : themes) {
			if (theme instanceof ITopic && theme.getParent().equals(this)) {
				topics.add((ITopic) theme);
			} else {
				throw new IllegalArgumentException("At least one theme is not a topic of this topic map.");
			}
		}
		return (IScope) getStore().doCreate(this, TopicMapStoreParameterType.SCOPE, topics);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope createScope(Topic... themes) {
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null.");
		}
		Collection<ITopic> topics = HashUtil.getHashSet();
		for (Topic theme : themes) {
			if (theme instanceof ITopic && theme.getParent().equals(this)) {
				topics.add((ITopic) theme);
			} else {
				throw new IllegalArgumentException("At least one theme is not a topic of this topic map.");
			}
		}
		return (IScope) getStore().doCreate(this, TopicMapStoreParameterType.SCOPE, topics);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Topic-Map{Base-Locator:" + getLocator().toExternalForm() + "}";
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		getStore().removeDuplicates();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		getStore().clear();
	}
}
