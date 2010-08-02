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

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TopicInUseException;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;

/**
 * Read only implementation of a construct
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyConstruct implements IConstruct {

	private String id;
	private ITopicMap topicMap;

	/**
	 * constructor
	 * 
	 * @param clone
	 *            the construct to clone
	 */
	protected ReadOnlyConstruct(IConstruct clone) {
		this.id = clone.getId();
		this.topicMap = clone.getTopicMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap() {
		return topicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(boolean cascade) throws TopicInUseException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void addItemIdentifier(Locator arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItemIdentifier(Locator arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IConstruct o) {
		return id.compareTo(o.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IConstruct) {
			return ((IConstruct) obj).getId().equalsIgnoreCase(getId());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRemoved() {
		return false;
	}
	
	
}
