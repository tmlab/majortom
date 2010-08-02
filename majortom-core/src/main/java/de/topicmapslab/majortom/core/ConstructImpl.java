/*******************************************************************************
 * Copyright 2010, Topic Maps Lab ( http://www.topicmapslab.de )
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
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TopicInUseException;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link IConstruct}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ConstructImpl implements IConstruct {
	/**
	 * removed flag
	 */
	private boolean removed = false;

	/**
	 * the topic map
	 */
	private final ITopicMap topicMap;

	/**
	 * the parent of this construct;
	 */
	private final IConstruct parent;

	/**
	 * the identity of this construct
	 */
	private final ITopicMapStoreIdentity identity;

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param topicMap
	 *            the containing topic map
	 * @param parent
	 *            the parent construct
	 */
	public ConstructImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap, IConstruct parent) {
		this.topicMap = topicMap;
		this.parent = parent;
		this.identity = identity;
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
	public void addItemIdentifier(Locator identifier) throws ModelConstraintException {
		if (isRemoved()) {
			throw new ConstructRemovedException(this);
		}
		if (identifier == null) {
			throw new ModelConstraintException(this, "Item identifier cannot be null.");
		}
		if (!getItemIdentifiers().contains(identifier)) {
			getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.ITEM_IDENTIFIER, identifier);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return (String) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Locator> getItemIdentifiers() {
//		if (isRemoved()) {
//			throw new ConstructRemovedException(this);
//		}
		return Collections.unmodifiableSet((Set<Locator>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ITEM_IDENTIFIER));
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		if (isRemoved()) {
			throw new ConstructRemovedException(this);
		}
		remove(false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(boolean cascade) throws TopicInUseException {
		if (isRemoved()) {
			throw new ConstructRemovedException(this);
		}
		getTopicMap().getStore().doRemove(this, cascade);
		removed = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeItemIdentifier(Locator identifier) {
		if (isRemoved()) {
			throw new ConstructRemovedException(this);
		}
		if (identifier != null && getItemIdentifiers().contains(identifier)) {
			getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.ITEM_IDENTIFIER, identifier);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getParent() {
		return parent;
	}

	/**
	 * Returns the identity of this construct
	 * 
	 * @return the identity the {@link ITopicMapStoreIdentity}
	 */
	public ITopicMapStoreIdentity getIdentity() {
		return identity;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IConstruct o) {
		return o.getId().compareTo(getId());
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
		return removed;
	}

	/**
	 * Modify the internal state of deletion.
	 */
	public void setRemoved() {
		this.removed = true;
	}
}
