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
package de.topicmapslab.majortom.inmemory.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of the {@link ISupertypeSubtypeIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemorySupertypeSubtypeIndex extends InMemoryIndex implements ISupertypeSubtypeIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the in-memory store
	 */
	public InMemorySupertypeSubtypeIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTopicTypeStore().getSubtypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			set.addAll(getStore().getIdentityStore().getTopics());
			set.removeAll(getSupertypes());
		} else {
			set.addAll(getStore().getTopicTypeStore().getDirectSubtypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			set.addAll(getStore().getIdentityStore().getTopics());
			set.removeAll(getSupertypes());
		} else {
			set.addAll(getStore().getTopicTypeStore().getSubtypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTopicTypeStore().getSubtypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		return Collections.unmodifiableCollection(getSubtypes(types, false));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getStore().getTopicTypeStore().getSubtypes((ITopic) type));
			} else {
				set.retainAll(getStore().getTopicTypeStore().getSubtypes((ITopic) type));
			}
			if (all && set.isEmpty()) {
				break;
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTopicTypeStore().getSupertypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			set.addAll(getStore().getIdentityStore().getTopics());
			set.removeAll(getSubtypes());
		} else {
			set.addAll(getStore().getTopicTypeStore().getDirectSupertypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			set.addAll(getStore().getIdentityStore().getTopics());
			set.removeAll(getSubtypes());
		} else {
			set.addAll(getStore().getTopicTypeStore().getSupertypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTopicTypeStore().getSupertypes((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		return Collections.unmodifiableCollection(getSupertypes(types, false));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null.");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getStore().getTopicTypeStore().getSupertypes((ITopic) type));
			} else {
				set.retainAll(getStore().getTopicTypeStore().getSupertypes((ITopic) type));
			}
			if (all && set.isEmpty()) {
				break;
			}
		}
		return Collections.unmodifiableCollection(set);
	}

}
