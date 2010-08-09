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
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of topic and topic type relations
 * 
 * @author Sven Krosse
 * 
 */
public class TopicTypeCache implements IDataStore {

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

	private Set<IConstruct> removedConstructs;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the parent store
	 */
	public TopicTypeCache(final TransactionTopicMapStore store) {
		this.topicMapStore = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
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
		if (removedConstructs != null) {
			removedConstructs.clear();
		}
	}

	/**
	 * Return all direct-instances of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct instances
	 */
	public Set<ITopic> getDirectInstances(ITopic type) {
		if (isRemovedConstruct(type)) {
			throw new TopicMapStoreException("Topic is already marked as removed.");
		}

		Set<ITopic> set = HashUtil.getHashSet();
		if (instances != null && instances.containsKey(type)) {
			set.addAll(instances.get(type));
		} else {
			set.addAll(redirectGetDirectInstance(type));
		}
		if (getStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectInstancesByAssociation(type));
		}
		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @param type
	 *            the type
	 * @return the instances of the given type
	 */
	protected Set<ITopic> redirectGetDirectInstance(ITopic type) {
		ITypeInstanceIndex index = getStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic topic : index.getTopics(type)) {
			topics.add((ITopic) topic);
		}

		if (instances == null) {
			instances = HashUtil.getHashMap();
		}
		instances.put(type, topics);
		return topics;
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
		if (getStore().existsTmdmTypeInstanceAssociationType()) {
			ITopic assocType = getStore().getTmdmTypeInstanceAssociationType();
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION, assocType);
			for (Association association : associations) {
				try {
					if (association.getRoles(getStore().getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getStore().getTmdmInstanceRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all direct-types of the given topic item
	 * 
	 * @param instance
	 *            the topic item
	 * @return the direct types
	 */
	public Set<ITopic> getDirectTypes(ITopic instance) {
		if (isRemovedConstruct(instance)) {
			return HashUtil.getHashSet();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		if (types != null && types.containsKey(instance)) {
			set.addAll(types.get(instance));
		}else{
			set.addAll(redirectGetDirectTypes(instance));
		}
		if (getStore().recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectTypesByAssociation(instance));
		}
		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @param instance
	 *            the instance
	 * @return the types of the given instance
	 */
	@SuppressWarnings("unchecked")
	protected Set<ITopic> redirectGetDirectTypes(ITopic instance) {
		Set<ITopic> topics = (Set<ITopic>) getStore().doRead(instance, TopicMapStoreParameterType.TYPE);
		if (types == null) {
			types = HashUtil.getHashMap();
		}
		types.put(instance, topics);
		return topics;
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
		if (getStore().existsTmdmTypeInstanceAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(instance, TopicMapStoreParameterType.ASSOCIATION,
					getStore().getTmdmTypeInstanceAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getStore().getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)) {
						set.add((ITopic) association.getRoles(getStore().getTmdmTypeRoleType()).iterator().next().getPlayer());
					}
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
		if (isRemovedConstruct(type)) {
			return HashUtil.getHashSet();
		}
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
		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Return all transitive instances of the topic map;
	 * 
	 * @return the instances
	 */
	public Set<ITopic> getInstances() {
		Set<ITopic> topics = redirectGetInstances();
		if (instances != null) {
			for (Set<ITopic> set : instances.values()) {
				topics.addAll(set);
			}
		}
		return topics;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @return all instances of the topic map
	 */
	protected Set<ITopic> redirectGetInstances() {
		ITypeInstanceIndex index = getStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic type : index.getTopicTypes()) {
			topics.addAll(getDirectInstances((ITopic) type));
		}
		return topics;
	}

	/**
	 * Return all transitive types of the given topic item.
	 * 
	 * @param instance
	 *            the topic item
	 * @return the types
	 */
	public Set<ITopic> getTypes(ITopic instance) {
		if (isRemovedConstruct(instance)) {
			return HashUtil.getHashSet();
		}
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
	 * Return all transitive types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getTypes() {
		Set<ITopic> topics = redirectGetTypes();
		if (types != null) {
			for (Set<ITopic> set : types.values()) {
				topics.addAll(set);
			}
		}
		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : topics) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @return all types of the topic map
	 */
	protected Set<ITopic> redirectGetTypes() {
		ITypeInstanceIndex index = getStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic type : index.getTopicTypes()) {
			topics.add((ITopic) type);
		}
		return topics;
	}

	/**
	 * Return all direct super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getDirectSupertypes(ITopic type) {
		if (isRemovedConstruct(type)) {
			return HashUtil.getHashSet();
		}
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		if (supertypes != null && supertypes.containsKey(type)) {
			set.addAll(supertypes.get(type));
		} else {
			set.addAll(redirectGetDirectSupertypes(type));
		}
		if (getStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSupertypesByAssociation(type));
		}

		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @param type
	 *            the type
	 * @return the supertypes of the given type
	 */
	@SuppressWarnings("unchecked")
	protected Set<ITopic> redirectGetDirectSupertypes(ITopic type) {
		Set<ITopic> topics = (Set<ITopic>) getStore().doRead(type, TopicMapStoreParameterType.SUPERTYPE);
		if (supertypes == null) {
			supertypes = HashUtil.getHashMap();
		}
		supertypes.put(type, topics);
		return topics;
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
		if (getStore().existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					getStore().getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer());
					}
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
		if (isRemovedConstruct(type)) {
			return HashUtil.getHashSet();
		}
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
		if (isRemovedConstruct(type)) {
			return HashUtil.getHashSet();
		}
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
	 * Return all super types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSupertypes() {
		Set<ITopic> set = HashUtil.getHashSet(redirectGetSupertypes());
		if (supertypes != null) {
			for (Set<ITopic> s : supertypes.values()) {
				set.addAll(s);
			}
		}
		if (getStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSupertypesByAssociation());
		}
		return set;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @return all supertypes of the topic map
	 */
	protected Set<ITopic> redirectGetSupertypes() {
		ISupertypeSubtypeIndex index = getStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic type : index.getSupertypes()) {
			topics.add((ITopic) type);
		}
		return topics;
	}

	/**
	 * Return all supertypes of the topic map by using the internal topic map
	 * data model associations
	 * 
	 * @return the supertypes
	 */
	private Set<ITopic> getSupertypesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getStore().existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : getStore().getTopicMap().getAssociations(getStore().getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Return all direct sub types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getDirectSubtypes(ITopic type) {
		if (isRemovedConstruct(type)) {
			return HashUtil.getHashSet();
		}
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		if (subtypes != null && subtypes.containsKey(type)) {
			set.addAll(subtypes.get(type));
		} else {
			set.addAll(redirectGetDirectSubtypes(type));
		}
		if (getStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSubtypesByAssociation(type));
		}

		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @param type
	 *            the supertypes
	 * @return all subtypes of the given type
	 */
	protected Set<ITopic> redirectGetDirectSubtypes(ITopic type) {
		ISupertypeSubtypeIndex index = getStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic sub : index.getSubtypes(type)) {
			topics.add((ITopic) sub);
		}
		if ( subtypes == null ){
			subtypes = HashUtil.getHashMap();
		}
		subtypes.put(type, topics);
		return topics;
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
		if (getStore().existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					getStore().getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(getStore().getTmdmSupertypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(getStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer());
					}
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
	 * Return all sub types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSubtypes() {
		Set<ITopic> set = HashUtil.getHashSet(redirectGetSubtypes());
		if (subtypes != null) {
			for (Set<ITopic> s : subtypes.values()) {
				set.addAll(s);
			}
		}
		if (getStore().recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSubtypesByAssociation());
		}

		Set<ITopic> results = HashUtil.getHashSet();
		for (ITopic topic : set) {
			if (!isRemovedConstruct(topic)) {
				results.add(topic);
			}
		}
		return results;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result.
	 * 
	 * @return all subtypes of the topic map
	 */
	protected Set<ITopic> redirectGetSubtypes() {
		ISupertypeSubtypeIndex index = getStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> topics = HashUtil.getHashSet();
		for (Topic type : index.getSubtypes()) {
			topics.add((ITopic) type);
		}
		return topics;
	}

	/**
	 * Return all sub types of the topic map by using the internal topic map
	 * data model associations
	 * 
	 * @return the subtypes
	 */
	private Set<ITopic> getSubtypesByAssociation() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (getStore().existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : getStore().getTopicMap().getAssociations(getStore().getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(getStore().getTmdmSubtypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		return set;
	}

	/**
	 * Removing a type-instance-relation between the given topics.
	 * 
	 * @param t
	 *            the topic item
	 * @param type
	 *            the type
	 */
	public void removeType(ITopic t, ITopic type) {
		/*
		 * check if instance is known by the storage
		 */
		if (types == null || !types.containsKey(t)) {
			redirectGetDirectTypes(t);
		}
		/*
		 * get types of the topic item
		 */
		Set<ITopic> set = types.get(t);
		if (!set.contains(type)) {
			throw new TopicMapStoreException("ITopic type is not related to the given topic item");
		}
		/*
		 * remove type
		 */
		set.remove(type);
		types.put(t, set);
		/*
		 * get instances of the topic item
		 */
		set = instances.get(type);
		if (!set.contains(t)) {
			redirectGetDirectInstance(type);
		}
		/*
		 * remove type
		 */
		set.remove(t);
		instances.put(type, set);
	}

	/**
	 * Removing a super-type-sub-type-relation between the given types.
	 * 
	 * @param type
	 *            the topic type
	 * @param supertype
	 *            the super type
	 */
	public void removeSupertype(ITopic type, ITopic supertype) {
		/*
		 * check if type is known by the storage
		 */
		if (supertypes == null || !supertypes.containsKey(type)) {
			redirectGetDirectSupertypes(type);
		}
		/*
		 * get super types of the type
		 */
		Set<ITopic> set = supertypes.get(type);
		if (!set.contains(supertype)) {
			throw new TopicMapStoreException("Super type is not related to the given topic type");
		}
		/*
		 * remove super type
		 */
		set.remove(supertype);
		supertypes.put(type, set);

		/*
		 * get subtypes of the type
		 */
		if (subtypes == null || !subtypes.containsKey(supertype)) {
			redirectGetDirectSubtypes(supertype);
		}
		set = subtypes.get(supertype);
		/*
		 * remove sub type
		 */
		set.remove(type);
		subtypes.put(supertype, set);
	}

	/**
	 * Add a type-instance-relation between the given topics.
	 * 
	 * @param t
	 *            the topic item
	 * @param type
	 *            the type
	 */
	public void addType(ITopic t, ITopic type) {
		/*
		 * check if the map is instantiate
		 */
		getDirectTypes(t);
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
		getDirectInstances(type);
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
	 * Removing a super-type-sub-type-relation between the given types.
	 * 
	 * @param type
	 *            the topic type
	 * @param supertype
	 *            the super type
	 */
	public void addSupertype(ITopic type, ITopic supertype) {
		/*
		 * check if super types map is instantiated
		 */
		getDirectSupertypes(type);
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
		getDirectSubtypes(supertype);
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
		if (removedConstructs == null) {
			removedConstructs = HashUtil.getHashSet();
		}
		removedConstructs.addAll(removed);
		removedConstructs.add(topic);
		/*
		 * return dependent removed topics
		 */
		return removed;
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
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected TopicMapStoreImpl getStore() {
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
}
