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

import java.util.Arrays;
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
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of in memory type-instance index.
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryTypeInstanceIndex extends InMemoryIndex implements ITypeInstanceIndex {

	/**
	 * constructor
	 * 
	 * @param store the in-memory-store
	 */
	public InMemoryTypeInstanceIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getAssociationTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return Collections.unmodifiableCollection(getAssociations(Arrays.asList(types)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedAssociations((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic arg0) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Association> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getTypedAssociations((ITopic) arg0));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getCharacteristicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getCharacteristicTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getTypedCharacteristics((ITopic) type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedCharacteristics((ITopic) type));
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
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedCharacteristics((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getRoleTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedRoles((ITopic) type));
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
		Set<Role> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getTypedRoles((ITopic) type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedRoles((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getNameTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Name> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getTypedNames((ITopic) type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedNames((ITopic) type));
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
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedNames((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getOccurrenceTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		set.addAll(getStore().getTypedStore().getTypedOccurrences((ITopic) type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedOccurrences((ITopic) type));
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
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTypedStore().getTypedOccurrences((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		set.addAll(getStore().getTopicTypeStore().getTypes());
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
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
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			set.addAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
			} else {
				set.retainAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
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
		Set<Topic> set = HashUtil.getHashSet();
		if (type == null) {
			set.addAll(getStore().getIdentityStore().getTopics());
			set.removeAll(getStore().getTopicTypeStore().getInstances());
		} else {
			set.addAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic[] types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
			} else {
				set.retainAll(getStore().getTopicTypeStore().getDirectInstances((ITopic) type));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

}
