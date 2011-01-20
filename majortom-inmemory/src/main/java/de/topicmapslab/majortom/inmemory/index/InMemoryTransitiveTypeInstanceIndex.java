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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryTransitiveTypeInstanceIndex extends InMemoryTypeInstanceIndex implements ITransitiveTypeInstanceIndex {

	/**
	 * constructor
	 * 
	 * @param store the parent in-memory topic map store
	 */
	public InMemoryTransitiveTypeInstanceIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getAssociations(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getAssociations(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getAssociations(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getAssociations(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		set.addAll(super.getAssociations(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
		set.addAll(super.getAssociations(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(super.getCharacteristics(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
		set.addAll(super.getCharacteristics(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getCharacteristics(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getCharacteristics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getCharacteristics(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getCharacteristics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getRoles(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getRoles(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		set.addAll(super.getRoles(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
		set.addAll(super.getRoles(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getRoles(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getRoles(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		set.addAll(super.getNames(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
		set.addAll(super.getNames(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getNames(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getNames(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getNames(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getNames(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		set.addAll(super.getOccurrences(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
		set.addAll(super.getOccurrences(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getOccurrences(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getOccurrences(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(super.getOccurrences(getTopicMapStore().getTopicTypeStore().getSubtypes((ITopic) type)));
			set.addAll(super.getOccurrences(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getTopicMapStore().getTopicTypeStore().getInstances((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getTopicMapStore().getTopicTypeStore().getInstances((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getTopics(type));
			} else {
				set.retainAll(getTopics(type));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			return super.getTopics(type);
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getTopicMapStore().getTopicTypeStore().getInstances((ITopic) type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic[] types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getTopics(type));
			} else {
				set.retainAll(getTopics(type));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

}
