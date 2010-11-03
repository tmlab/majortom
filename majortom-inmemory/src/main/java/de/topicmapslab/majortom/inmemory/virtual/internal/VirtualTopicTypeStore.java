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
package de.topicmapslab.majortom.inmemory.virtual.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.internal.TopicTypeStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualTopicTypeStore<T extends VirtualTopicMapStore> extends TopicTypeStore {

	private Map<String, Set<String>> removedInstances;
	private Map<String, Set<String>> removedTypes;
	private Map<String, Set<String>> removedSupertypes;
	private Map<String, Set<String>> removedSubtypes;

	/**
	 * @param store
	 */
	public VirtualTopicTypeStore(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (removedInstances != null) {
			removedInstances.clear();
		}

		if (removedTypes != null) {
			removedTypes.clear();
		}

		if (removedSupertypes != null) {
			removedSupertypes.clear();
		}

		if (removedSubtypes != null) {
			removedSubtypes.clear();
		}
	}

	/**
	 * Internal method to access the virtual-identity store
	 * 
	 * @return the virtual identity store
	 */
	@SuppressWarnings("unchecked")
	protected VirtualIdentityStore<T> getVirtualIdentityStore() {
		return ((VirtualIdentityStore<T>) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	@SuppressWarnings("unchecked")
	protected T getStore() {
		return (T) super.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addType(ITopic t, ITopic type) {
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		super.addType(getVirtualIdentityStore().asVirtualConstruct(t),
				getVirtualIdentityStore().asVirtualConstruct(type));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSupertype(ITopic type, ITopic supertype) {
		if (getVirtualIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		super.addSupertype(getVirtualIdentityStore().asVirtualConstruct(type), getVirtualIdentityStore()
				.asVirtualConstruct(supertype));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeType(ITopic t, ITopic type) {
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		storeIsaRelation(t, type);
		super.removeType(t, type);
	}

	/**
	 * Store the old is-instance-of relation between the given topics
	 * 
	 * @param t
	 *            the instance
	 * @param type
	 *            the type
	 */
	private void storeIsaRelation(ITopic t, ITopic type) {
		/*
		 * store relation instance -> type
		 */
		if (removedTypes == null) {
			removedTypes = HashUtil.getHashMap();
		}
		Set<String> set = removedTypes.get(t.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
			removedTypes.put(t.getId(), set);
		}
		set.add(type.getId());

		/*
		 * store relation type -> instance
		 */
		if (removedInstances == null) {
			removedInstances = HashUtil.getHashMap();
		}
		set = removedInstances.get(type.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
			removedInstances.put(type.getId(), set);
		}
		set.add(t.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSupertype(ITopic type, ITopic supertype) {
		if (getVirtualIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		storeAkoRelation(type, supertype);
		super.removeSupertype(type, supertype);
	}

	/**
	 * Store the old a-kind-of relation between the given topics
	 * 
	 * @param type
	 *            the type
	 * @param supertype
	 *            the supertype
	 */
	private void storeAkoRelation(ITopic type, ITopic supertype) {
		/*
		 * store relation subtype -> supertype
		 */
		if (removedSubtypes == null) {
			removedSubtypes = HashUtil.getHashMap();
		}
		Set<String> set = removedSubtypes.get(supertype.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
			removedSubtypes.put(supertype.getId(), set);
		}
		set.add(type.getId());

		/*
		 * store relation supertype -> subtype
		 */
		if (removedSupertypes == null) {
			removedSupertypes = HashUtil.getHashMap();
		}
		set = removedSupertypes.get(type.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
			removedSupertypes.put(type.getId(), set);
		}
		set.add(supertype.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getTypes() {
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getTopicTypes()) {
			ITopic type = (ITopic) t;
			if (getVirtualIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getInstances(type).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(type));
			}
		}
		set.addAll(super.getTypes());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ITopic> getDirectTypes(ITopic instance) {
		Set<ITopic> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(instance)) {
			Set<ITopic> types = (Set<ITopic>) getStore().getRealStore().doRead(instance,
					TopicMapStoreParameterType.TYPE);
			for (ITopic type : types) {
				if (getVirtualIdentityStore().isRemovedConstruct(type)) {
					continue;
				}
				if (removedTypes == null || !removedTypes.containsKey(instance.getId())
						|| !removedTypes.get(instance.getId()).contains(type.getId())) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(type));
				}
			}
		}
		set.addAll(super.getDirectTypes(instance));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getInstances() {
		Set<ITopic> set = HashUtil.getHashSet();
		for (ITopic type : getTypes()) {
			for (ITopic instance : getDirectInstances(type)) {
				if (getVirtualIdentityStore().isRemovedConstruct(instance)) {
					continue;
				}
				set.add(getVirtualIdentityStore().asVirtualConstruct(instance));
			}
		}
		set.addAll(super.getInstances());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectInstances(ITopic type) {
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(type)) {
			/*
			 * get all instances of the given type
			 */
			for (Topic inst : index.getTopics(type)) {
				ITopic instance = (ITopic) inst;
				/*
				 * check if instance is removed
				 */
				if (getVirtualIdentityStore().isRemovedConstruct(instance)) {
					continue;
				}
				/*
				 * check that is-instance-of relation was not removed in the current transaction context
				 */
				if (removedInstances == null || !removedInstances.containsKey(type.getId())
						|| !removedInstances.get(type.getId()).contains(instance.getId())) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(instance));
				}
			}
		}
		set.addAll(super.getDirectInstances(type));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getSupertypes() {
		ISupertypeSubtypeIndex index = getStore().getRealStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getSupertypes()) {
			ITopic type = (ITopic) t;
			if (getVirtualIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getSubtypes(type).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(type));
			}
		}
		set.addAll(super.getSupertypes());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectSupertypes(ITopic subtype) {
		ISupertypeSubtypeIndex index = getStore().getRealStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(subtype)) {
			for (Topic t : index.getDirectSupertypes(subtype)) {
				ITopic supertype = (ITopic) t;
				if (getVirtualIdentityStore().isRemovedConstruct(supertype)) {
					continue;
				}
				/*
				 * check that a-kind-of relation was not removed in the current transaction context
				 */
				if (removedSupertypes == null || !removedSupertypes.containsKey(subtype.getId())
						|| !removedSupertypes.get(subtype.getId()).contains(supertype.getId())) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(supertype));
				}
			}
		}
		set.addAll(super.getDirectSupertypes(subtype));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getSubtypes() {
		ISupertypeSubtypeIndex index = getStore().getRealStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getSubtypes()) {
			ITopic type = (ITopic) t;
			if (getVirtualIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getSupertypes(type).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(type));
			}
		}
		set.addAll(super.getSubtypes());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectSubtypes(ITopic type) {
		ISupertypeSubtypeIndex index = getStore().getRealStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(type)) {
			for (Topic t : index.getDirectSubtypes(type)) {
				ITopic subtype = (ITopic) t;
				if (getVirtualIdentityStore().isRemovedConstruct(subtype)) {
					continue;
				}
				/*
				 * check that a-kind-of relation was not removed in the current transaction context
				 */
				if (removedSubtypes == null || !removedSubtypes.containsKey(type.getId())
						|| !removedSubtypes.get(type.getId()).contains(subtype.getId())) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(subtype));
				}
			}
		}
		set.addAll(super.getDirectSubtypes(type));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

}
