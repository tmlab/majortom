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

/**
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyAssociation extends ReadOnlyScopable implements IAssociation {

	/**
	 * @param clone
	 */
	public ReadOnlyAssociation(IAssociation clone) {
		super(clone);
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
	public void setType(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

}
