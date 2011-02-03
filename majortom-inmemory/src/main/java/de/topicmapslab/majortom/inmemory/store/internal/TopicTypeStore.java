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
package de.topicmapslab.majortom.inmemory.store.internal;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tmapi.core.Association;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of topic and topic type relations
 * 
 * @author Sven Krosse
 * 
 */
public class TopicTypeStore implements IDataStore {

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
	 * the parent store
	 */
	private final InMemoryTopicMapStore store;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the parent store
	 */
	public TopicTypeStore(final InMemoryTopicMapStore store) {
		this.store = store;
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
	}

	/**
	 * Return all direct-instances of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct instances
	 */
	public Set<ITopic> getDirectInstances(ITopic type) {

		Set<ITopic> set = HashUtil.getHashSet();
		if (instances != null && instances.containsKey(type)) {
			set.addAll(instances.get(type));
		}

		if (store.recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectInstancesByAssociation(type));
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmTypeInstanceAssociationType()) {
			ITopic assocType = store.getTmdmTypeInstanceAssociationType();
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION, assocType);
			for (Association association : associations) {
				try {
					if (association.getRoles(store.getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(store.getTmdmInstanceRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		Set<ITopic> set = HashUtil.getHashSet();
		if (types != null && types.containsKey(instance)) {
			set.addAll(types.get(instance));
		}
		if (store.recognizingTypeInstanceAssociation()) {
			set.addAll(getDirectTypesByAssociation(instance));
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmTypeInstanceAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(instance, TopicMapStoreParameterType.ASSOCIATION,
					store.getTmdmTypeInstanceAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(store.getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)) {
						set.add((ITopic) association.getRoles(store.getTmdmTypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return all transitive instances of the topic map;
	 * 
	 * @return the instances
	 */
	public Set<ITopic> getInstances() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (instances != null) {
			for (Set<ITopic> s : instances.values()) {
				set.addAll(s);
			}
		}
		if (store.recognizingTypeInstanceAssociation()) {
			set.addAll(getInstancesByAssociation());
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmTypeInstanceAssociationType()) {
			for (Association association : store.getTopicMap().getAssociations(store.getTmdmTypeInstanceAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(store.getTmdmInstanceRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return all transitive types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getTypes() {

		Set<ITopic> set = HashUtil.getHashSet();
		if (types != null) {
			for (Set<ITopic> s : types.values()) {
				set.addAll(s);
			}
		}
		if (store.recognizingTypeInstanceAssociation()) {
			set.addAll(getTypesByAssociation());
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmTypeInstanceAssociationType()) {
			for (Association association : store.getTopicMap().getAssociations(store.getTmdmTypeInstanceAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(store.getTmdmTypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return all direct super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getDirectSupertypes(ITopic type) {
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		if (supertypes != null && supertypes.containsKey(type)) {
			set.addAll(supertypes.get(type));
		}
		if (store.recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSupertypesByAssociation(type));
		}

		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					store.getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(store.getTmdmSubtypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(store.getTmdmSupertypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model assocaition!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
	protected final Set<ITopic> getSupertypes(ITopic type, Set<ITopic> known) {
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
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return all super types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSupertypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (supertypes != null) {
			for (Set<ITopic> s : supertypes.values()) {
				set.addAll(s);
			}
		}
		if (store.recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSupertypesByAssociation());
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : store.getTopicMap().getAssociations(store.getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(store.getTmdmSupertypeRoleType()).iterator().next().getPlayer());
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
		/*
		 * create result set
		 */
		Set<ITopic> set = HashUtil.getHashSet();
		if (subtypes != null && subtypes.containsKey(type)) {
			set.addAll(subtypes.get(type));
		}
		if (store.recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getDirectSubtypesByAssociation(type));
		}

		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmSupertypeSubtypeAssociationType()) {
			Set<IAssociation> associations = (Set<IAssociation>) getStore().doRead(type, TopicMapStoreParameterType.ASSOCIATION,
					store.getTmdmSupertypeSubtypeAssociationType());
			for (Association association : associations) {
				try {
					if (association.getRoles(store.getTmdmSupertypeRoleType()).iterator().next().getPlayer().equals(type)) {
						set.add((ITopic) association.getRoles(store.getTmdmSubtypeRoleType()).iterator().next().getPlayer());
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model association!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return all sub types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSubtypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		if (subtypes != null) {
			for (Set<ITopic> s : subtypes.values()) {
				set.addAll(s);
			}
		}
		if (store.recognizingSupertypeSubtypeAssociation()) {
			set.addAll(getSubtypesByAssociation());
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
		if (store.existsTmdmSupertypeSubtypeAssociationType()) {
			for (Association association : store.getTopicMap().getAssociations(store.getTmdmSupertypeSubtypeAssociationType())) {
				try {
					set.add((ITopic) association.getRoles(store.getTmdmSubtypeRoleType()).iterator().next().getPlayer());
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model association!", e);
				}
			}
		}
		if ( set.isEmpty()){
			return Collections.emptySet();
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
			return;
		}
		/*
		 * get types of the topic item
		 */
		Set<ITopic> set = types.get(t);
		if (!set.contains(type)) {
			return;
		}
		/*
		 * remove type
		 */
		set.remove(type);
		if ( set.isEmpty()){
			types.remove(t);
		}
		/*
		 * get instances of the topic item
		 */
		set = instances.get(type);
		
		if (!set.contains(t)) {
			return;
		}
		/*
		 * remove type
		 */
		
		set.remove(t);
		if ( set.isEmpty()){
			instances.remove(type);	
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
	public void removeSupertype(ITopic type, ITopic supertype) {
		/*
		 * check if type is known by the storage
		 */
		if (supertypes == null || !supertypes.containsKey(type)) {
			return;
		}
		/*
		 * get super types of the type
		 */
		Set<ITopic> set = supertypes.get(type);
		if (!set.contains(supertype)) {
			return;
		}
		/*
		 * remove super type
		 */
		set.remove(supertype);
		if ( set.isEmpty()){
			supertypes.remove(type);
		}

		/*
		 * get sub types of the super type
		 */
		set = subtypes.get(supertype);
		/*
		 * remove sub type
		 */
		set.remove(type);
		if ( set.isEmpty()){
			subtypes.remove(supertype);
		}
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
		if (types == null) {
			types = HashUtil.getHashMap();
		}
		/*
		 * get types of the topic item
		 */
		Set<ITopic> set = types.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
			types.put(t, set);
		}
		/*
		 * add type if not contained
		 */
		set.add(type);

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
			instances.put(type, set);
		}
		/*
		 * add instance if not contained
		 */
		set.add(t);
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
		if (supertypes == null) {
			supertypes = HashUtil.getHashMap();
		}
		/*
		 * get super types of the type
		 */
		Set<ITopic> set = supertypes.get(type);
		if (set == null) {
			set = HashUtil.getHashSet();
			supertypes.put(type, set);
		}
		/*
		 * add super type if not contained
		 */
		set.add(supertype);

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
			subtypes.put(supertype, set);
		}
		/*
		 * add sub type if not contained
		 */
		set.add(type);
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
		if ( removed.isEmpty()){
			return Collections.emptySet();
		}
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
			/*
			 * store revision
			 */
			if (revision != null) {
				store.storeRevision(revision, TopicMapEventType.TYPE_REMOVED, instance, null, topic);
				store.storeRevision(revision, TopicMapEventType.TYPE_ADDED, instance, replacement, null);
			}
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
			/*
			 * store revision
			 */
			if (revision != null) {
				store.storeRevision(revision, TopicMapEventType.TYPE_REMOVED, topic, null, type);
				store.storeRevision(revision, TopicMapEventType.TYPE_ADDED, replacement, type, null);
			}
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
			/*
			 * store revision
			 */
			if (revision != null) {
				store.storeRevision(revision, TopicMapEventType.SUPERTYPE_REMOVED, topic, null, supertype);
				store.storeRevision(revision, TopicMapEventType.SUPERTYPE_ADDED, replacement, supertype, null);
			}
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
			/*
			 * store revision
			 */
			if (revision != null) {
				store.storeRevision(revision, TopicMapEventType.SUPERTYPE_REMOVED, t, null, topic);
				store.storeRevision(revision, TopicMapEventType.SUPERTYPE_ADDED, t, replacement, null);
			}
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
	protected ITopicMapStore getStore() {
		return store;
	}
	
	protected Map<ITopic, Set<ITopic>> getSupertypesMap(){
		return supertypes;
	}
	/**
	 * internal storage of the type-instance relations
	 */
	protected Map<ITopic, Set<ITopic>> getInstancesMap(){
		return instances;
	}
	/**
	 * internal storage of the instance-types relations
	 */
	protected Map<ITopic, Set<ITopic>> getTypesMap(){
		return types;
	}
	/**
	 * internal storage of the type-subtypes relations
	 */
	protected Map<ITopic, Set<ITopic>> getSubtypesMap(){
		return subtypes;
	}
}
