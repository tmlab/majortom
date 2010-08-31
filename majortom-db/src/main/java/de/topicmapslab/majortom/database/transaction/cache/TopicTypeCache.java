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
package de.topicmapslab.majortom.database.transaction.cache;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TopicTypeCache implements IDataStore {

	private Map<String, Set<String>> removedInstances;
	private Map<String, Set<String>> removedTypes;
	private Map<String, Set<String>> removedSupertypes;
	private Map<String, Set<String>> removedSubtypes;
	/**
	 * internal storage of the type-supertypes relations
	 */
	private Map<ITopic, Set<ITopic>> supertypes;
	/**
	 * internal storage of the type-instance relations
	 */
	private Map<ITopic, Set<ITopic>> instances;
	/**
	 * internal storage of the instance-types relations
	 */
	private Map<ITopic, Set<ITopic>> types;
	/**
	 * internal storage of the type-subtypes relations
	 */
	private Map<ITopic, Set<ITopic>> subtypes;
	/**
	 * reference to the underlying topic map store
	 */
	private final TransactionTopicMapStore topicMapStore;

	/**
	 * @param store
	 */
	public TopicTypeCache(final TransactionTopicMapStore store) {
		this.topicMapStore = store;
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
		if (types != null) {
			types.clear();
		}
		if (supertypes != null) {
			supertypes.clear();
		}
		if (subtypes != null) {
			subtypes.clear();
		}
		if (instances != null) {
			instances.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addType(ITopic t, ITopic type) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		internalAddType(getTransactionStore().getIdentityStore().createLazyStub(t), getTransactionStore().getIdentityStore().createLazyStub(type));
	}

	/**
	 * Internal method to add a type-instance-relation between the given topics.
	 * 
	 * @param t
	 *            the topic item
	 * @param type
	 *            the type
	 */
	private void internalAddType(ITopic t, ITopic type) {
		/*
		 * check if the map is instantiate
		 */
		if (types == null) {
			types = HashUtil.getHashMap();
		}
		/*
		 * get types of the topic item
		 */
		Set<ITopic> set = types.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		/*
		 * add type if not contained
		 */
		if (!set.contains(type)) {
			set.add(type);
			types.put(t, set);
		}

		/*
		 * check if instances map is instantiated
		 */
		if (instances == null) {
			instances = HashUtil.getHashMap();
		}
		/*
		 * get instances of the given type
		 */
		set = instances.get(type);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		/*
		 * add instance if not contained
		 */
		if (!set.contains(t)) {
			set.add(t);
			instances.put(type, set);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSupertype(ITopic type, ITopic supertype) {
		if (isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		internalAddSupertype(getTransactionStore().getIdentityStore().createLazyStub(type), getTransactionStore().getIdentityStore().createLazyStub(supertype));
	}

	/**
	 * Removing a super-type-sub-type-relation between the given types.
	 * 
	 * @param type
	 *            the topic type
	 * @param supertype
	 *            the super type
	 */
	private void internalAddSupertype(ITopic type, ITopic supertype) {
		/*
		 * check if super types map is instantiated
		 */
		if (supertypes == null) {
			supertypes = HashUtil.getHashMap();
		}
		/*
		 * get super types of the type
		 */
		Set<ITopic> set = supertypes.get(type);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		/*
		 * add super type if not contained
		 */
		if (!set.contains(supertype)) {
			set.add(supertype);
			supertypes.put(type, set);
		}

		/*
		 * check if sub type map is instantiated
		 */
		if (subtypes == null) {
			subtypes = HashUtil.getHashMap();
		}
		/*
		 * get sub types of the super type
		 */
		set = subtypes.get(supertype);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		/*
		 * add sub type if not contained
		 */
		if (!set.contains(type)) {
			set.add(type);
			subtypes.put(supertype, set);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeType(ITopic t, ITopic type) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		storeIsaRelation(t, type);
		/*
		 * check if instance is known by the storage
		 */
		if (types != null && types.containsKey(t)) {
			Set<ITopic> set = types.get(t);
			if (set.contains(type)) {
				/*
				 * remove type
				 */
				set.remove(type);
				types.put(t, set);
			}
		}
		if (instances != null && instances.containsKey(type)) {
			/*
			 * get instances of the topic item
			 */
			Set<ITopic> set = instances.get(type);
			if (set.contains(t)) {
				/*
				 * remove instance
				 */
				set.remove(t);
				instances.put(type, set);
			}
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
		if (isRemovedConstruct(type)) {
			throw new ConstructRemovedException(type);
		}
		if (isRemovedConstruct(supertype)) {
			throw new ConstructRemovedException(supertype);
		}
		storeAkoRelation(type, supertype);
		/*
		 * check if type is known by the storage
		 */
		if (supertypes != null && supertypes.containsKey(type)) {
			/*
			 * get super types of the type
			 */
			Set<ITopic> set = supertypes.get(type);
			if (set.contains(supertype)) {
				/*
				 * remove supertype
				 */
				set.remove(supertype);
				supertypes.put(type, set);
			}
		}
		/*
		 * check if supertype is known by the storage
		 */
		if (subtypes != null && subtypes.containsKey(supertype)) {
			/*
			 * get sub types of the super type
			 */
			Set<ITopic> set = subtypes.get(supertype);
			if (set.contains(type)) {
				/*
				 * remove sub type
				 */
				set.remove(type);
				subtypes.put(supertype, set);
			}
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
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getTopicTypes()) {
			ITopic type = (ITopic) t;
			if (isRemovedConstruct(type)) {
				continue;
			}
			if (!getInstances(type).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(type));
			}
		}
		/*
		 * get cached types
		 */
		if (types != null) {
			for (Set<ITopic> s : types.values()) {
				set.addAll(s);
			}
		}
		/*
		 * get types by association
		 */
		if (getTopicMapStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getTypesByAssociation());
		}
		return set;
	}

	/**
	 * Return all types of the topic map by using the internal topic map data
	 * model associations
	 * 
	 * @return the types
	 */
	private Set<ITopic> getTypesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmTypeInstanceAssociationType()) {
			for (Association association : getTransactionStore().getTopicMap().getAssociations(getTransactionStore().getTmdmTypeInstanceAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getTransactionStore().getTmdmTypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all transitive types of the given topic item.
	 * 
	 * @param instance
	 *            the topic item
	 * @return the types
	 */
	public Set<ITopic> getTypes(ITopic instance) {
		Set<ITopic> set = HashUtil.getHashSet();

		/*
		 * iterate over all types
		 */
		for (ITopic t : getDirectTypes(instance)) {
			/*
			 * add all super types of current type
			 */
			set.addAll(getSupertypes(t));
			set.add(t);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<ITopic> getDirectTypes(ITopic instance) {
		Set<ITopic> set = HashUtil.getHashSet();
		Set<ITopic> types = (Set<ITopic>) getTopicMapStore().doRead(instance, TopicMapStoreParameterType.TYPE);
		for (ITopic type : types) {
			if (isRemovedConstruct(type)) {
				continue;
			}
			if (removedTypes == null || !removedTypes.containsKey(instance.getId()) || !removedTypes.get(instance.getId()).contains(type.getId())) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(type));
			}
		}
		if (this.types != null && this.types.containsKey(instance)) {
			set.addAll(this.types.get(instance));
		}
		if (getTopicMapStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectTypesByAssociation(instance));
		}
		return set;
	}

	/**
	 * Return all direct-types of the given topic type by using the internal
	 * topic map data model associations
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct types
	 */
	@SuppressWarnings("unchecked")
	private Set<ITopic> getDirectTypesByAssociation(ITopic instance) {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmTypeInstanceAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getTransactionStore().doRead(instance, TopicMapStoreParameterType.ASSOCIATION,
					getTransactionStore().getTmdmTypeInstanceAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getTransactionStore().getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)) {
						set.add((ITopic) association.getRoles(getTransactionStore().getTmdmTypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
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
				if (isRemovedConstruct(instance)) {
					continue;
				}
				set.add(getTransactionStore().getIdentityStore().createLazyStub(instance));
			}
		}
		if (instances != null) {
			for (Set<ITopic> s : instances.values()) {
				set.addAll(s);
			}
		}
		if (getTopicMapStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getInstancesByAssociation());
		}
		return set;
	}

	/**
	 * Return all instances of the topic map by using the internal topic map
	 * data model associations
	 * 
	 * @return the instances
	 */
	private Set<ITopic> getInstancesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmTypeInstanceAssociationType()) {
			for (Association association : getTransactionStore().getTopicMap().getAssociations(getTransactionStore().getTmdmTypeInstanceAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getTransactionStore().getTmdmInstanceRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all transitive instances of the given topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @return the instances
	 */
	public Set<ITopic> getInstances(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(getDirectInstances(type));

		/*
		 * iterate over all sub types
		 */
		for (ITopic t : getSubtypes(type)) {
			/*
			 * add all instances of current type
			 */
			set.addAll(getDirectInstances(t));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectInstances(ITopic type) {
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
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
			if (isRemovedConstruct(instance)) {
				continue;
			}
			/*
			 * check that is-instance-of relation was not removed in the current
			 * transaction context
			 */
			if (removedInstances == null || !removedInstances.containsKey(type.getId()) || !removedInstances.get(type.getId()).contains(instance.getId())) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(instance));
			}
		}
		if (instances != null && instances.containsKey(type)) {
			set.addAll(instances.get(type));
		}

		if (getTopicMapStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectInstancesByAssociation(type));
		}
		return set;
	}

	/**
	 * Return all direct-instances of the given topic type by using the internal
	 * topic map data model associations
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct instances
	 */
	@SuppressWarnings("unchecked")
	private Set<ITopic> getDirectInstancesByAssociation(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmTypeInstanceAssociationType()) {
			ITopic assocType = getTransactionStore().getTmdmTypeInstanceAssociationType();
			Set<IAssociation> associations = (Set<IAssociation>) getTransactionStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION, assocType);
			for (Association association : associations) {
				try {
					if (association.getRoles(getTransactionStore().getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getTransactionStore().getTmdmInstanceRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getSupertypes() {
		ISupertypeSubtypeIndex index = getTopicMapStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getSupertypes()) {
			ITopic type = (ITopic) t;
			if (isRemovedConstruct(type)) {
				continue;
			}
			if (!getSubtypes(type).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(type));
			}
		}
		if (supertypes != null) {
			for (Set<ITopic> s : supertypes.values()) {
				set.addAll(s);
			}
		}
		if (getTopicMapStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSupertypesByAssociation());
		}
		return set;
	}

	/**
	 * Return all supertypes of the topic map by using the internal topic map
	 * data model associations
	 * 
	 * @return the supertypes
	 */
	private Set<ITopic> getSupertypesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : getTransactionStore().getTopicMap().getAssociations(getTransactionStore().getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getTransactionStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getSupertypes(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		return getSupertypes(type, set);
	}

	/**
	 * Return all super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @param known
	 *            a set containing all known type to enable cycle detection
	 * @return the super types
	 */
	protected Set<ITopic> getSupertypes(ITopic type, Set<ITopic> known) {
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(getDirectSupertypes(type));
		/*
		 * create set containing new added super types
		 */
		Set<ITopic> newAdded = HashUtil.getHashSet();
		newAdded.addAll(set);

		/*
		 * check if there is a new super typed added at last iteration
		 */
		while (!newAdded.isEmpty()) {
			Set<ITopic> temp = HashUtil.getHashSet();
			/*
			 * iterate over new added super types
			 */
			for (ITopic t : newAdded) {
				if (known.contains(t)) {
					continue;
				}
				known.add(t);
				/*
				 * get super types of current type
				 */
				for (ITopic t_ : getSupertypes(t, known)) {
					/*
					 * is already known as super type?
					 */
					if (!set.contains(t_)) {
						temp.add(t_);
						set.add(t_);
					}
				}
			}
			newAdded.clear();
			newAdded.addAll(temp);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectSupertypes(ITopic subtype) {
		ISupertypeSubtypeIndex index = getTopicMapStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getDirectSupertypes(subtype)) {
			ITopic supertype = (ITopic) t;
			if (isRemovedConstruct(supertype)) {
				continue;
			}
			/*
			 * check that a-kind-of relation was not removed in the current
			 * transaction context
			 */
			if (removedSupertypes == null || !removedSupertypes.containsKey(subtype.getId())
					|| !removedSupertypes.get(subtype.getId()).contains(supertype.getId())) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(supertype));
			}
		}
		if (supertypes != null && supertypes.containsKey(subtype)) {
			set.addAll(supertypes.get(subtype));
		}
		if (getTopicMapStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSupertypesByAssociation(subtype));
		}

		return set;
	}

	/**
	 * Return all direct-supertypes of the given topic type by using the
	 * internal topic map data model associations
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct supertypes
	 */
	@SuppressWarnings("unchecked")
	private Set<ITopic> getDirectSupertypesByAssociation(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getTransactionStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					getTransactionStore().getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getTransactionStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getTransactionStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getSubtypes() {
		ISupertypeSubtypeIndex index = getTopicMapStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getSubtypes()) {
			ITopic type = (ITopic) t;
			if (isRemovedConstruct(type)) {
				continue;
			}
			if (!getSupertypes(type).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(type));
			}
		}
		if (subtypes != null) {
			for (Set<ITopic> s : subtypes.values()) {
				set.addAll(s);
			}
		}
		if (getTopicMapStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSubtypesByAssociation());
		}
		return set;
	}

	/**
	 * Return all sub types of the topic map by using the internal topic map
	 * data model associations
	 * 
	 * @return the subtypes
	 */
	private Set<ITopic> getSubtypesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : getTransactionStore().getTopicMap().getAssociations(getTransactionStore().getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getTransactionStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all sub types of the given topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @return the sub types
	 */
	public Set<ITopic> getSubtypes(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		return getSubtypes(type, set);
	}

	/**
	 * Return all sub types of the given topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @param known
	 *            a set containing all known types, to enable cycle detection
	 * @return the sub types
	 */
	protected Set<ITopic> getSubtypes(ITopic type, Set<ITopic> known) {
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(getDirectSubtypes(type));
		/*
		 * create set containing new added sub types
		 */
		Set<ITopic> newAdded = HashUtil.getHashSet();
		newAdded.addAll(set);

		/*
		 * check if there is a new sub typed added at last iteration
		 */
		while (!newAdded.isEmpty()) {
			Set<ITopic> temp = HashUtil.getHashSet();
			/*
			 * iterate over new added sub types
			 */
			for (ITopic t : newAdded) {
				if (known.contains(t)) {
					continue;
				}
				known.add(t);
				/*
				 * get sub types of current type
				 */
				for (ITopic t_ : getSubtypes(t, known)) {
					/*
					 * is already known as sub type?
					 */
					if (!set.contains(t_)) {
						temp.add(t_);
						set.add(t_);
					}
				}
			}
			newAdded.clear();
			newAdded.addAll(temp);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getDirectSubtypes(ITopic type) {
		ISupertypeSubtypeIndex index = getTopicMapStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : index.getDirectSubtypes(type)) {
			ITopic subtype = (ITopic) t;
			if (isRemovedConstruct(subtype)) {
				continue;
			}
			/*
			 * check that a-kind-of relation was not removed in the current
			 * transaction context
			 */
			if (removedSubtypes == null || !removedSubtypes.containsKey(type.getId()) || !removedSubtypes.get(type.getId()).contains(subtype.getId())) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(subtype));
			}
		}
		if (subtypes != null && subtypes.containsKey(type)) {
			set.addAll(subtypes.get(type));
		}
		if (getTopicMapStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSubtypesByAssociation(type));
		}

		return set;
	}
	
	/**
	 * Return all direct-subtypes of the given topic type by using the internal
	 * topic map data model associations
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct subtypes
	 */
	@SuppressWarnings("unchecked")
	private Set<ITopic> getDirectSubtypesByAssociation(ITopic type) {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getTransactionStore().existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getTransactionStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					getTransactionStore().getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getTransactionStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getTransactionStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected TopicMapStoreImpl getTopicMapStore() {
		return topicMapStore.getRealStore();
	}

	/**
	 * @return the topicMapStore
	 */
	public TransactionTopicMapStore getTransactionStore() {
		return topicMapStore;
	}

	/**
	 * Redirect method call to identity store and check if construct is marked
	 * as removed.
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if the construct was marked as removed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isRemovedConstruct(IConstruct c) {
		return getTransactionStore().getIdentityStore().isRemovedConstruct(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		/*
		 * replace as type
		 */
		for (ITopic instance : getInstances(topic)) {
			removeType(instance, topic);
			addType(instance, replacement);
		}
		if (instances != null) {
			instances.remove(topic);
		}

		/*
		 * replace as instances
		 */
		for (ITopic type : getTypes(topic)) {
			removeType(topic, type);
			addType(replacement, type);
		}
		if (types != null) {
			types.remove(topic);
		}

		/*
		 * replace as sub type
		 */
		for (ITopic supertype : getSupertypes(topic)) {
			removeSupertype(topic, supertype);
			addSupertype(replacement, supertype);
		}
		if (supertypes != null) {
			supertypes.remove(topic);
		}

		/*
		 * replace as super type
		 */
		for (ITopic t : getSubtypes(topic)) {
			removeSupertype(t, topic);
			addSupertype(t, replacement);
		}
		if (subtypes != null) {
			subtypes.remove(topic);
		}
	}
	
	/**
	 * Removing the given topic from the internal store and all dependent
	 * relations.
	 * 
	 * @param topic
	 *            the topic
	 * @return the removed dependent topics
	 */
	public Set<ITopic> removeTopic(ITopic topic) {
		Set<ITopic> removed = HashUtil.getHashSet();
		/*
		 * remove as instance
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTypes(topic));
		for (ITopic type : types) {
			removeType(topic, type);
		}
		/*
		 * remove as type
		 */
		Set<ITopic> instances = HashUtil.getHashSet(getInstances(topic));
		for (ITopic instance : instances) {
			/*
			 * remove instance and all typed topics
			 */
			removed.addAll(removeTopic(instance));
		}

		/*
		 * remove all sub-types
		 */
		Set<ITopic> subtypes = HashUtil.getHashSet(getSubtypes(topic));
		for (ITopic subtype : subtypes) {
			/*
			 * remove sub-type and all instances
			 */
			removed.addAll(removeTopic(subtype));
		}

		/*
		 * remove as sub-type
		 */
		Set<ITopic> supertypes = HashUtil.getHashSet(getSupertypes(topic));
		for (ITopic supertype : supertypes) {
			removeSupertype(topic, supertype);
		}

		/*
		 * remove topic itself
		 */
		if (this.subtypes != null) {
			this.subtypes.remove(topic);
		}
		if (this.supertypes != null) {
			this.supertypes.remove(topic);
		}
		if (this.instances != null) {
			this.instances.remove(topic);
		}
		if (this.types != null) {
			this.types.remove(topic);
		}
		/*
		 * return dependent removed topics
		 */
		return removed;
	}

}