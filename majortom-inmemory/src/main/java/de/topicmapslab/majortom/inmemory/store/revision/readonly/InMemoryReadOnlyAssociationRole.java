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
package de.topicmapslab.majortom.inmemory.store.revision.readonly;

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociation;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociationRole;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryReadOnlyAssociationRole extends ReadOnlyAssociationRole {

	private final String reifierId;
	private final String typeId, playerId;
	private String parentId;

	/*
	 * cached values
	 */
	private Topic cachedPlayer, cachedType;
	private Topic cachedReifier;
	private Set<Locator> itemIdentifiers = HashUtil.getHashSet();
	private IAssociation cachedParent;

	/**
	 * @param clone
	 */
	public InMemoryReadOnlyAssociationRole(IAssociationRole clone) {
		super(clone);
		this.parentId = clone.getParent().getId();

		for (Locator itemIdentifier : clone.getItemIdentifiers()) {
			itemIdentifiers.add(new LocatorImpl(itemIdentifier.getReference()));
		}
		typeId = clone.getType().getId();
		playerId = clone.getPlayer().getId();
		if (clone.getReifier() != null) {
			reifierId = clone.getReifier().getId();
		} else {
			reifierId = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		return itemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation getParent() {
		if (cachedParent != null) {
			return cachedParent;
		}
		IAssociation parent = (IAssociation) getTopicMap().getStore().doRead(getTopicMap(), TopicMapStoreParameterType.BY_ID, parentId);
		if (parent instanceof ReadOnlyAssociation) {
			cachedParent = parent;
		}
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getPlayer() {
		if (cachedPlayer != null) {
			return cachedPlayer;
		}
		Topic player = (Topic) getTopicMap().getConstructById(playerId);
		if (player instanceof InMemoryReadOnlyTopic) {
			cachedPlayer = player;
		}
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		if (cachedType != null) {
			return cachedType;
		}
		Topic type = (Topic) getTopicMap().getConstructById(typeId);
		if (type instanceof InMemoryReadOnlyTopic) {
			cachedType = type;
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		if (reifierId == null) {
			return null;
		}
		if (cachedReifier != null) {
			return cachedReifier;
		}
		Topic reifier = (Topic) getTopicMap().getStore().doRead(getTopicMap(), TopicMapStoreParameterType.BY_ID, reifierId);
		if (reifier instanceof InMemoryReadOnlyTopic) {
			cachedReifier = reifier;
		}
		return reifier;
	}

}
