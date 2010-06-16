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
package de.topicmapslab.majortom.inMemory.store.revision.core;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyAssociationRole extends ReadOnlyReifiable implements IAssociationRole {

	private final String typeId, playerId;

	/*
	 * cached values
	 */
	private Topic cachedPlayer, cachedType;

	/**
	 * @param clone
	 */
	public ReadOnlyAssociationRole(IAssociationRole clone) {
		super(clone);
		typeId = clone.getType().getId();
		playerId = clone.getPlayer().getId();
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation getParent() {
		return (IAssociation) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getPlayer() {
		if (cachedPlayer != null) {
			return cachedPlayer;
		}
		Topic player = (Topic) getTopicMap().getConstructById(playerId);
		if (player instanceof ReadOnlyTopic) {
			cachedPlayer = player;
		}
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPlayer(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		if (cachedType != null) {
			return cachedType;
		}
		Topic type = (Topic) getTopicMap().getConstructById(typeId);
		if (type instanceof ReadOnlyTopic) {
			cachedType = type;
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

}
