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
package de.topicmapslab.majortom.revision.core;

import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyAssociation extends ReadOnlyScopable implements IAssociation {

	private final String typeId;
	private final Set<String> roleIds = HashUtil.getHashSet();

	/*
	 * cached values
	 */
	private Topic cachedType;
	private Set<Role> cachedRoles;

	/**
	 * @param clone
	 */
	public ReadOnlyAssociation(IAssociation clone) {
		super(clone);

		typeId = clone.getType().getId();
		for (Role role : clone.getRoles()) {
			roleIds.add(role.getId());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getParent() {
		return (ITopicMap) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public Role createRole(Topic arg0, Topic arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getRoleTypes() {
		Set<Topic> set = new HashSet<Topic>();
		for (Role r : getRoles()) {
			set.add(r.getType());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRoles() {
		Set<Role> roles = new HashSet<Role>();
		if (cachedRoles != null) {
			roles.addAll(cachedRoles);
		}
		if (!roleIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(roleIds);
			for (String id : ids) {
				Role r = (Role) getTopicMap().getConstructById(id);
				if (r instanceof ReadOnlyAssociationRole) {
					if (cachedRoles == null) {
						cachedRoles = HashUtil.getHashSet();
					}
					roleIds.remove(id);
					cachedRoles.add(r);
				}
				roles.add(r);
			}
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRoles(Topic arg0) {
		Set<Role> roles = new HashSet<Role>();
		for (Role r : getRoles()) {
			if (r.getType().equals(arg0)) {
				roles.add(r);
			}
		}
		return roles;
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
