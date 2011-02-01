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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.paged.IPagedAssociation;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link IAssociation}
 * 
 * @author Sven Krosse
 * 
 */
public class AssociationImpl extends ScopeableImpl implements IAssociation, IPagedAssociation {

	private static final long serialVersionUID = -7045605297867030798L;

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param topicMap
	 *            the topic map
	 */
	protected AssociationImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap) {
		super(identity, topicMap, topicMap);
	}

	/**
	 * {@inheritDoc}
	 */
	public Role createRole(Topic type, Topic player) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (player == null) {
			throw new ModelConstraintException(this, "Player cannot be null.");
		}
		if (!type.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map.");
		}
		if (!player.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(player, "Player has to be a topic of the same topic map.");
		}
		return (Role) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.ROLE, type, player);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getParent() {
		return getTopicMap();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Topic> getRoleTypes() {
		return Collections.unmodifiableSet((Set<Topic>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE_TYPES));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRoles() {
		return Collections.unmodifiableSet((Set<Role>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRoles(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null.");
		}
		return Collections.unmodifiableSet((Set<Role>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE, type));
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
		try {
			Topic type = getType();
			return "Association{Type:" + (type == null ? "null" : type.toString()) + ";Roles:" + getRoles().toString() + "}";
		} catch (Exception e) {
			return "Association{ID:" + getId() + "}";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(int offset, int limit) {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getRoles(this, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Role> getRoles(int offset, int limit, Comparator<Role> comparator) {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getRoles(this, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfRoles() {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getNumberOfRoles(this);
	}
}
