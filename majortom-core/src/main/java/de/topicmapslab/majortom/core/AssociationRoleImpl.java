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

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link IAssociationRole}
 * 
 * @author Sven Krosse
 * 
 */
public class AssociationRoleImpl extends ReifiableImpl implements IAssociationRole {

	private static final long serialVersionUID = -7838350394983745862L;

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param parent
	 *            the parent construct
	 */
	protected AssociationRoleImpl(ITopicMapStoreIdentity identity, IAssociation parent) {
		super(identity, parent.getTopicMap(), parent);
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
		return (Topic) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.PLAYER);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPlayer(Topic player) {
		if (player == null) {
			throw new ModelConstraintException(this, "Player cannot be null.");
		}
		if (!player.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(player, "Player has to be a topic of the same topic map.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.PLAYER, player);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(Topic type) {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (!type.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.TYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		return (Topic) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		try{
		Topic type = getType();
		Topic player = getPlayer();
		return "Association-Role{Type:" + (type == null ? "null" : type.toString()) + ";Player:" + (player == null ? "null" : player.toString() + "}");
		}catch (Exception e) {
			return "Association-Role{Id:" + getId() +"}";
		}
	}

}
