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
package de.topicmapslab.majortom.inmemory.virtual.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.inmemory.store.internal.IdentityStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualIdentityStore<T extends VirtualTopicMapStore> extends IdentityStore {

	private Set<String> removedIds;
	private Set<String> virtualConstructs;

	/**
	 * @param store
	 */
	public VirtualIdentityStore(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected T getStore() {
		return (T) super.getStore();
	}

	// SUBJECT LOCATOR

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectLocator(ITopic t, ILocator locator) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getSubjectLocators(t);
		super.addSubjectLocator(t, locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectLocator(ITopic t, ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getSubjectLocators(t);
		super.removeSubjectLocator(t, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ILocator> getSubjectLocators(ITopic t) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * check if already copied to internal cache
		 */
		if (!containsSubjectLocators(t) && !isVirtual(t)) {
			/*
			 * copy to internal store
			 */
			for (ILocator locator : (Collection<ILocator>) getStore().getRealStore().doRead(t,
					TopicMapStoreParameterType.SUBJECT_LOCATOR)) {
				super.addSubjectLocator(t, locator);
			}
		}
		Set<ILocator> set = super.getSubjectLocators(t);
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic bySubjectLocator(ILocator l) {
		/*
		 * check if construct is part of this
		 */
		ITopic topic = super.bySubjectLocator(l);
		if (topic == null) {
			topic = (ITopic) getStore().getRealStore().doRead(getStore().getTopicMap(),
					TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, l);
			if (topic != null && !isRemovedConstruct(topic)) {
				topic = asVirtualConstruct(topic);
			} else {
				return null;
			}
		}
		if (isRemovedConstruct(topic)) {
			return null;
		}
		return topic;
	}

	// SUBJECT IDENTIFIER

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectIdentifier(ITopic t, ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getSubjectIdentifiers(t);
		super.addSubjectIdentifier(t, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectIdentifier(ITopic t, ILocator identifier) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getSubjectIdentifiers(t);
		super.removeSubjectIdentifier(t, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ILocator> getSubjectIdentifiers(ITopic t) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		/*
		 * check if already copied to internal cache
		 */
		if (!containsSubjectIdentifiers(t) && !isVirtual(t)) {
			/*
			 * copy to internal store
			 */
			for (ILocator locator : (Collection<ILocator>) getStore().getRealStore().doRead(t,
					TopicMapStoreParameterType.SUBJECT_IDENTIFIER)) {
				super.addSubjectIdentifier(t, locator);
			}
		}
		Set<ILocator> set = super.getSubjectIdentifiers(t);
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic bySubjectIdentifier(ILocator l) {
		/*
		 * check if construct is part of this
		 */
		ITopic topic = super.bySubjectIdentifier(l);
		if (topic == null) {
			topic = (ITopic) getStore().getRealStore().doRead(getStore().getTopicMap(),
					TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, l);
			if (topic != null && !isRemovedConstruct(topic)) {
				topic = asVirtualConstruct(topic);
			} else {
				return null;
			}
		}
		if (isRemovedConstruct(topic)) {
			return null;
		}
		return topic;
	}

	// ITEM IDENTIFIERS

	/**
	 * {@inheritDoc}
	 */
	public void addItemIdentifer(IConstruct c, ILocator identifier) {
		if (isRemovedConstruct(c)) {
			throw new ConstructRemovedException(c);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getItemIdentifiers(c);
		super.addItemIdentifer(c, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItemIdentifer(IConstruct c, ILocator identifier) {
		if (isRemovedConstruct(c)) {
			throw new ConstructRemovedException(c);
		}
		/*
		 * copy lazy to internal store if not done before
		 */
		getItemIdentifiers(c);
		super.removeItemIdentifer(c, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ILocator> getItemIdentifiers(IConstruct c) {
		if (isRemovedConstruct(c)) {
			throw new ConstructRemovedException(c);
		}
		/*
		 * check if already copied to internal cache
		 */
		if (!containsItemIdentifiers(c) && !isVirtual(c)) {
			/*
			 * copy to internal store
			 */
			for (ILocator locator : (Collection<ILocator>) getStore().getRealStore().doRead(c,
					TopicMapStoreParameterType.ITEM_IDENTIFIER)) {
				super.addItemIdentifer(c, locator);
			}
		}
		Set<ILocator> set = super.getItemIdentifiers(c);
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct byItemIdentifier(ILocator l) {
		/*
		 * check if construct is part of this
		 */
		IConstruct construct = super.byItemIdentifier(l);
		if (construct == null) {

			construct = (IConstruct) getStore().getRealStore().doRead(getStore().getTopicMap(),
					TopicMapStoreParameterType.BY_ITEM_IDENTIFER, l);
			if (construct != null && !isRemovedConstruct(construct)) {
				construct = asVirtualConstruct(construct);
			} else {
				return null;
			}
		}
		if (isRemovedConstruct(construct)) {
			return null;
		}
		return construct;
	}

	// REMOVAGE

	/**
	 * {@inheritDoc}
	 */
	public void removeTopic(ITopic t) {
		super.removeTopic(t);
		if (removedIds == null) {
			removedIds = HashUtil.getHashSet();
		}
		removedIds.add(t.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeConstruct(IConstruct c) {
		if (containsConstruct(c)) {
			super.removeConstruct(c);
		}
		if (removedIds == null) {
			removedIds = HashUtil.getHashSet();
		}
		removedIds.add(c.getId());
	}

	// OTHER IDENTITY

	/**
	 * {@inheritDoc}
	 */
	public IConstruct byId(String id) {
		/*
		 * check if construct is part of this
		 */
		IConstruct construct = super.byId(id);
		if (construct == null) {
			construct = (IConstruct) getStore().getRealStore().doRead(getStore().getTopicMap(),
					TopicMapStoreParameterType.BY_ID, id);
			if (construct != null && !isRemovedConstruct(construct)) {
				construct = asVirtualConstruct(construct);
			} else {
				return null;
			}
		} else if (isRemovedConstruct(construct)) {
			return null;
		}
		return construct;
	}

	/**
	 * Creates a virtual stub of the given construct
	 * 
	 * @param c
	 *            the construct
	 * @return the virtual stub
	 * @throws ConstructRemovedException
	 *             thrown if the id of the given construct is marked as removed
	 */
	public <X extends IConstruct> X asVirtualConstruct(X c) throws ConstructRemovedException {
		return c;
	}

	/**
	 * Creates a virtual stub of the given scope
	 * 
	 * @param scope
	 *            the scope
	 * @return the virtual stub
	 * @throws ConstructRemovedException
	 *             thrown if at least one contained theme is marked as removed
	 */
	public IScope asVirtualScope(IScope scope) throws ConstructRemovedException {
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (removedIds != null) {
			removedIds.clear();
		}
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ITopic> getTopics() {
		Set<ITopic> topics = HashUtil.getHashSet();
		for (ITopic topic : (Collection<ITopic>) getStore().getRealStore().doRead(getStore().getTopicMap(),
				TopicMapStoreParameterType.TOPIC)) {
			if (!isRemovedConstruct(topic)) {
				topics.add(asVirtualConstruct(topic));
			}
		}
		topics.addAll(super.getTopics());
		return topics;
	}

	/**
	 * Checks if the given construct was deleted by the virtual layer.
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if the construct was deleted by the virtual layer, <code>false</code> otherwise.
	 */
	public boolean isRemovedConstruct(IConstruct c) {
		return c != null && removedIds != null && removedIds.contains(c.getId());
	}

	/**
	 * Checks if at least one theme of the given scope was deleted by the virtual layer.
	 * 
	 * @param scope
	 *            the scope
	 * @return <code>true</code> if at least one theme of the given scope was deleted by the virtual layer,
	 *         <code>false</code> otherwise.
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
	 * {@inheritDoc}
	 */
	public void setId(IConstruct c, String id) {
		if (virtualConstructs == null) {
			virtualConstructs = HashUtil.getHashSet();
		}
		virtualConstructs.add(id);
		super.setId(c, id);
	}

	/**
	 * Checks if the construct is only a part of the virtual memory layer
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if the construct is virtual, <code>false</code> otherwise
	 */
	public boolean isVirtual(Construct c) {
		if (virtualConstructs != null && virtualConstructs.contains(c.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the scope is only a part of the virtual memory layer
	 * 
	 * @param scope
	 *            the scope
	 * @return <code>true</code> if the scope is virtual, <code>false</code> otherwise
	 */
	public boolean isVirtual(IScope scope) {
		if (virtualConstructs != null && virtualConstructs.contains(scope.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * Specify the given scope as virtual
	 * 
	 * @param scope
	 *            the virtual scope
	 */
	public void setVirtual(IScope scope) {
		if (virtualConstructs == null) {
			virtualConstructs = HashUtil.getHashSet();
		}
		virtualConstructs.add(scope.getId());
	}

	/**
	 * Removes the given construct from the virtual layer
	 * 
	 * @param c
	 *            the construct
	 */
	public void removeVirtualConstruct(IConstruct c) {
		if (virtualConstructs != null) {
			virtualConstructs.remove(c.getId());
			super.removeConstruct(c);
		}
	}

}
