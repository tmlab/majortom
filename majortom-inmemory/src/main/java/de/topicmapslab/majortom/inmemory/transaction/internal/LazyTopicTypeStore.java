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
package de.topicmapslab.majortom.inmemory.transaction.internal;

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.internal.TopicTypeStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyTopicTypeStore extends TopicTypeStore {

	private Map<String, Set<String>> removedInstances;
	private Map<String, Set<String>> removedTypes;
	private Map<String, Set<String>> removedSupertypes;
	private Map<String, Set<String>> removedSubtypes;

	/**
	 * @param store
	 */
	public LazyTopicTypeStore(InMemoryTopicMapStore store) {
		super(store);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if ( removedInstances != null ){
			removedInstances.clear();
		}
		
		if ( removedTypes != null ){
			removedTypes.clear();
		}
		
		if ( removedSupertypes != null ){
			removedSupertypes.clear();
		}
		
		if ( removedSubtypes != null ){
			removedSubtypes.clear();
		}
	}

	/**
	 * Internal method to access the lazy-identity store of this transaction
	 * context
	 * 
	 * @return the lazy identity store
	 */
	protected LazyIdentityStore getLazyIdentityStore() {
		return ((LazyIdentityStore) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected InMemoryTransactionTopicMapStore getStore() {
		return (InMemoryTransactionTopicMapStore) super.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addType(ITopic t, ITopic type) {
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getLazyIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		super.addType(getLazyIdentityStore().createLazyStub(t), getLazyIdentityStore().createLazyStub(type));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSupertype(ITopic type, ITopic supertype) {
		if (getLazyIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (getLazyIdentityStore().isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		super.addSupertype(getLazyIdentityStore().createLazyStub(type), getLazyIdentityStore().createLazyStub(supertype));
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeType(ITopic t, ITopic type) {
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getLazyIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		storeIsaRelation(t, type);
		try {
			super.removeType(t, type);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO -> is-instance-of relation is out of transaction
			// context
		}
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
		}
		set.add(type.getId());
		removedTypes.put(t.getId(), set);

		/*
		 * store relation type -> instance
		 */
		if (removedInstances == null) {
			removedInstances = HashUtil.getHashMap();
		}
		set = removedInstances.get(type.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(t.getId());
		removedInstances.put(type.getId(), set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSupertype(ITopic type, ITopic supertype) {
		if (getLazyIdentityStore().isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (getLazyIdentityStore().isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		storeAkoRelation(type, supertype);
		try {
			super.removeSupertype(type, supertype);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO -> a-kind-of relation is out of transaction
			// context
		}
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
		}
		set.add(type.getId());
		removedSubtypes.put(supertype.getId(), set);

		/*
		 * store relation supertype -> subtype
		 */
		if (removedSupertypes == null) {
			removedSupertypes = HashUtil.getHashMap();
		}
		set = removedSupertypes.get(type.getId());
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(supertype.getId());
		removedSupertypes.put(type.getId(), set);
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
			if (getLazyIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getInstances(type).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(type));
			}
		}
		set.addAll(super.getTypes());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ITopic> getDirectTypes(ITopic instance) {
		Set<ITopic> set = HashUtil.getHashSet();
		Set<ITopic> types = (Set<ITopic>) getStore().getRealStore().doRead(instance, TopicMapStoreParameterType.TYPE);
		for (ITopic type : types) {
			if (getLazyIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (removedTypes == null || !removedTypes.containsKey(instance.getId()) || !removedTypes.get(instance.getId()).contains(type.getId())) {
				set.add(getLazyIdentityStore().createLazyStub(type));
			}
		}
		set.addAll(super.getDirectTypes(instance));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getInstances() {
		Set<ITopic> set = HashUtil.getHashSet();
		for (ITopic type : getTypes()) {
			for (ITopic instance : getDirectInstances(type)) {
				if (getLazyIdentityStore().isRemovedConstruct(instance)) {
					continue;
				}
				set.add(getLazyIdentityStore().createLazyStub(instance));
			}
		}
		set.addAll(super.getInstances());
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
		/*
		 * get all instances of the given type
		 */
		for (Topic inst : index.getTopics(type)) {
			ITopic instance = (ITopic) inst;
			/*
			 * check if instance is removed
			 */
			if (getLazyIdentityStore().isRemovedConstruct(instance)) {
				continue;
			}
			/*
			 * check that is-instance-of relation was not removed in the current
			 * transaction context
			 */
			if (removedInstances == null || !removedInstances.containsKey(type.getId()) || !removedInstances.get(type.getId()).contains(instance.getId())) {
				set.add(getLazyIdentityStore().createLazyStub(instance));
			}
		}
		set.addAll(super.getDirectInstances(type));
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
			if (getLazyIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getSubtypes(type).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(type));
			}
		}
		set.addAll(super.getSupertypes());
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
		for (Topic t : index.getDirectSupertypes(subtype)) {
			ITopic supertype = (ITopic) t;
			if (getLazyIdentityStore().isRemovedConstruct(supertype)) {
				continue;
			}
			/*
			 * check that a-kind-of relation was not removed in the current
			 * transaction context
			 */
			if (removedSupertypes == null || !removedSupertypes.containsKey(subtype.getId())
					|| !removedSupertypes.get(subtype.getId()).contains(supertype.getId())) {
				set.add(getLazyIdentityStore().createLazyStub(supertype));
			}
		}
		set.addAll(super.getDirectSupertypes(subtype));
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
			if (getLazyIdentityStore().isRemovedConstruct(type)) {
				continue;
			}
			if (!getSupertypes(type).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(type));
			}
		}
		set.addAll(super.getSubtypes());
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
		for (Topic t : index.getDirectSubtypes(type)) {
			ITopic subtype = (ITopic) t;
			if (getLazyIdentityStore().isRemovedConstruct(subtype)) {
				continue;
			}
			/*
			 * check that a-kind-of relation was not removed in the current
			 * transaction context
			 */
			if (removedSubtypes == null || !removedSubtypes.containsKey(type.getId()) || !removedSubtypes.get(type.getId()).contains(subtype.getId())) {
				set.add(getLazyIdentityStore().createLazyStub(subtype));
			}
		}
		set.addAll(super.getDirectSubtypes(type));
		return set;
	}

}
