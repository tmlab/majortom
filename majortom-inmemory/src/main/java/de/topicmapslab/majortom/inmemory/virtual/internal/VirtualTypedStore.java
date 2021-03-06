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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inmemory.store.internal.TypedStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualTypedStore<T extends VirtualTopicMapStore> extends TypedStore implements IVirtualStore {

	private Set<ITypeable> modifiedConstructs;
	private Map<ITopic, Set<ITypeable>> changedTypes;

	/**
	 * @param store
	 */
	public VirtualTypedStore(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IAssociation typed) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
		} catch (TopicMapStoreException e) {
			if (!getVirtualIdentityStore().isVirtual(typed)) {
				try {
					type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
				} catch (TopicMapStoreException ex) {
					// THROWN IF CONSTRUCT IS NOT CREATED YET
				}
			}
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IAssociationRole typed) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
		} catch (TopicMapStoreException e) {
			if (!getVirtualIdentityStore().isVirtual(typed)) {
				try {
					type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
				} catch (TopicMapStoreException ex) {
					// THROWN IF CONSTRUCT IS NOT CREATED YET
				}
			}
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IName typed) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
		} catch (TopicMapStoreException e) {
			if (!getVirtualIdentityStore().isVirtual(typed)) {
				try {
					type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
				} catch (TopicMapStoreException ex) {
					// THROWN IF CONSTRUCT IS NOT CREATED YET
				}
			}
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IOccurrence typed) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
		} catch (TopicMapStoreException e) {
			if (!getVirtualIdentityStore().isVirtual(typed)) {
				try {
					type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
				} catch (TopicMapStoreException ex) {
					// THROWN IF CONSTRUCT IS NOT CREATED YET
				}
			}
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> getTypedAssociations(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction context
		 */
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociation> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			/*
			 * get index of real store instance
			 */
			ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
			if (!index.isOpen()) {
				index.open();
			}
			/*
			 * get association typed by given type
			 */
			for (Association a : index.getAssociations(t)) {
				if (getVirtualIdentityStore().isRemovedConstruct((IAssociation) a)) {
					continue;
				}
				set.add(getVirtualIdentityStore().asVirtualConstruct((IAssociation) a));
			}

			/*
			 * remove all old type relations
			 */
			if (changedTypes != null && changedTypes.containsKey(t)) {
				set.removeAll(changedTypes.get(t));
			}
		}

		/*
		 * add internal information of transaction context
		 */
		set.addAll(super.getTypedAssociations(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> getTypedRoles(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction context
		 */
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			/*
			 * get index of real store instance
			 */
			ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
			if (!index.isOpen()) {
				index.open();
			}

			/*
			 * get roles typed by given type
			 */
			for (Role r : index.getRoles(t)) {
				if (getVirtualIdentityStore().isRemovedConstruct((IAssociationRole) r)) {
					continue;
				}
				set.add(getVirtualIdentityStore().asVirtualConstruct((IAssociationRole) r));
			}

			/*
			 * remove all old type relations
			 */
			if (changedTypes != null && changedTypes.containsKey(t)) {
				set.removeAll(changedTypes.get(t));
			}
		}

		/*
		 * add internal information of transaction context
		 */
		set.addAll(super.getTypedRoles(t));

		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> getTypedNames(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction context
		 */
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IName> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			/*
			 * get index of real store instance
			 */
			ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
			if (!index.isOpen()) {
				index.open();
			}

			/*
			 * get names typed by given type
			 */
			for (Name n : index.getNames(t)) {
				if (getVirtualIdentityStore().isRemovedConstruct((IName) n)) {
					continue;
				}
				set.add(getVirtualIdentityStore().asVirtualConstruct((IName) n));
			}

			/*
			 * remove all old type relations
			 */
			if (changedTypes != null && changedTypes.containsKey(t)) {
				set.removeAll(changedTypes.get(t));
			}
		}

		/*
		 * add internal information of transaction context
		 */
		set.addAll(super.getTypedNames(t));

		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> getTypedOccurrences(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction context
		 */
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			/*
			 * get index of real store instance
			 */
			ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
			if (!index.isOpen()) {
				index.open();
			}

			/*
			 * get occurrences typed by given type
			 */
			for (Occurrence o : index.getOccurrences(t)) {
				if (getVirtualIdentityStore().isRemovedConstruct((IOccurrence) o)) {
					continue;
				}
				set.add(getVirtualIdentityStore().asVirtualConstruct((IOccurrence) o));
			}

			/*
			 * remove all old type relations
			 */
			if (changedTypes != null && changedTypes.containsKey(t)) {
				set.removeAll(changedTypes.get(t));
			}
		}

		/*
		 * add internal information of transaction context
		 */
		set.addAll(super.getTypedOccurrences(t));

		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IAssociation typed, ITopic t) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IAssociationRole typed, ITopic t) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IName typed, ITopic t) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IOccurrence typed, ITopic t) {
		if (getVirtualIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IAssociation typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type != null) {
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// thrown because the typed construct is not known by the lazy store
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IAssociationRole typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type != null) {
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// thrown because the typed construct is not known by the lazy store
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IName typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type != null) {
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// thrown because the typed construct is not known by the lazy store
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IOccurrence typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type != null) {
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParameterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// thrown because the typed construct is not known by the lazy store
		}
		return getVirtualIdentityStore().asVirtualConstruct(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getAssociationTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		/*
		 * get types by index
		 */
		for (Topic type : index.getAssociationTypes()) {
			ITopic t = (ITopic) type;
			/*
			 * check if typed object was deleted by the current transaction context and is really a type
			 */
			if (!getVirtualIdentityStore().isRemovedConstruct(t) && !getTypedAssociations(t).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(t));
			}
		}

		/*
		 * add internal stored types
		 */
		set.addAll(super.getAssociationTypes());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getRoleTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		/*
		 * get types by index
		 */
		for (Topic type : index.getRoleTypes()) {
			ITopic t = (ITopic) type;
			/*
			 * check if typed object was deleted by the current transaction context and is really a type
			 */
			if (!getVirtualIdentityStore().isRemovedConstruct(t) && !getTypedRoles(t).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(t));
			}
		}

		/*
		 * add internal stored types
		 */
		set.addAll(super.getRoleTypes());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getNameTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		/*
		 * get types by index
		 */
		for (Topic type : index.getNameTypes()) {
			ITopic t = (ITopic) type;
			/*
			 * check if typed object was deleted by the current transaction context and is really a type
			 */
			if (!getVirtualIdentityStore().isRemovedConstruct(t) && !getTypedNames(t).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(t));
			}
		}

		/*
		 * add internal stored types
		 */
		set.addAll(super.getNameTypes());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getOccurrenceTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getStore().getRealStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		/*
		 * get types by index
		 */
		for (Topic type : index.getOccurrenceTypes()) {
			ITopic t = (ITopic) type;
			/*
			 * check if typed object was deleted by the current transaction context and is really a type
			 */
			if (!getVirtualIdentityStore().isRemovedConstruct(t) && !getTypedOccurrences(t).isEmpty()) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(t));
			}
		}

		/*
		 * add internal stored types
		 */
		set.addAll(super.getOccurrenceTypes());
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		super.close();
		if (changedTypes != null) {
			changedTypes.clear();
		}
		if (modifiedConstructs != null) {
			modifiedConstructs.clear();
		}
	}

	/**
	 * Method checks if the type change of the given construct is the first type change in the transaction context. If
	 * it is the first, the old relation stored to filter them later.
	 * 
	 * @param typed
	 *            the typed construct
	 */
	protected void storeOldRelation(ITypeable typed) {
		/*
		 * first type change in transaction context
		 */
		if (modifiedConstructs == null || !modifiedConstructs.contains(typed)) {
			/*
			 * store as first type change
			 */
			if (modifiedConstructs == null) {
				modifiedConstructs = HashUtil.getHashSet();
			}
			modifiedConstructs.add(typed);
			/*
			 * check if type is known by other type changes
			 */
			if (changedTypes == null) {
				changedTypes = HashUtil.getHashMap();
			}
			ITopic oldType = (ITopic) typed.getType();
			if (oldType != null) {
				/*
				 * add new type change to type specific list
				 */
				Set<ITypeable> set = changedTypes.get(oldType);
				if (set == null) {
					set = HashUtil.getHashSet();
					changedTypes.put(oldType, set);
				}
				set.add(typed);
			}
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
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected T getStore() {
		return (T) super.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeVirtualConstruct(IConstruct construct, IConstruct newConstruct) {
		if (construct instanceof ITypeable) {
			if (construct instanceof IAssociation) {
				removeVirtualConstruct((IAssociation) construct, (IAssociation) newConstruct);
			} else if (construct instanceof IName) {
				removeVirtualConstruct((IName) construct, (IName) newConstruct);
			} else if (construct instanceof IOccurrence) {
				removeVirtualConstruct((IOccurrence) construct, (IOccurrence) newConstruct);
			} else if (construct instanceof IAssociationRole) {
				removeVirtualConstruct((IAssociationRole) construct, (IAssociationRole) newConstruct);
			}
			if ( modifiedConstructs != null && modifiedConstructs.contains(construct)){
				modifiedConstructs.remove(construct);
				modifiedConstructs.add((ITypeable)newConstruct);
			}
		}
		/*
		 * construct is a topic
		 */
		else if ( construct instanceof ITopic ){
			ITopic newTopic = (ITopic) newConstruct;
			/*
			 * replace as association type
			 */
			Map<ITopic, Set<IAssociation>> typedAssociations = getTypedAssociationsMap();
			if ( typedAssociations != null && typedAssociations.containsKey(construct)){
				Set<IAssociation> set = typedAssociations.remove(construct);
				typedAssociations.put(newTopic, set);
				for ( IAssociation a : set){
					Map<IAssociation, ITopic> typed = getAssociationTypesMap();
					if (typed != null && typed.containsKey(a)) {
						typed.put(a, newTopic);
					}
				}
			}
			/*
			 * replace as role type
			 */
			Map<ITopic, Set<IAssociationRole>> typedRoles = getTypedRolesMap();
			if ( typedRoles != null && typedRoles.containsKey(construct)){
				Set<IAssociationRole> set = typedRoles.remove(construct);
				typedRoles.put(newTopic, set);
				for ( IAssociationRole r : set){
					Map<IAssociationRole, ITopic> typed = getRoleTypesMap();
					if (typed != null && typed.containsKey(r)) {
						typed.put(r, newTopic);
					}
				}
			}
			/*
			 * replace as name type
			 */
			Map<ITopic, Set<IName>> typedNames = getTypedNamesMap();
			if ( typedNames != null && typedNames.containsKey(construct)){
				Set<IName> set = typedNames.remove(construct);
				typedNames.put(newTopic, set);
				for ( IName n : set){
					Map<IName, ITopic> typed = getNameTypesMap();
					if (typed != null && typed.containsKey(n)) {
						typed.put(n, newTopic);
					}
				}
			}
			/*
			 * replace as occurrence type
			 */
			Map<ITopic, Set<IOccurrence>> typedOccurrences = getTypedOccurrencesMap();
			if ( typedOccurrences != null && typedOccurrences.containsKey(construct)){
				Set<IOccurrence> set = typedOccurrences.remove(construct);
				typedOccurrences.put(newTopic, set);
				for ( IOccurrence o : set){
					Map<IOccurrence, ITopic> typed = getOccurrenceTypesMap();
					if (typed != null && typed.containsKey(o)) {
						typed.put(o, newTopic);
					}
				}
			}
			/*
			 * copy modification knowledge
			 */
			if ( changedTypes != null && changedTypes.containsKey(construct)){
				changedTypes.put(newTopic, changedTypes.remove(construct));
			}			
		}
	}

	private void removeVirtualConstruct(IAssociation construct, IAssociation newConstruct) {
		Map<IAssociation, ITopic> associationTypes = getAssociationTypesMap();
		if (associationTypes != null && associationTypes.containsKey(construct)) {
			ITopic type = associationTypes.remove(construct);
			associationTypes.put(newConstruct, type);
			Map<ITopic, Set<IAssociation>> typedAssociations = getTypedAssociationsMap();
			if (typedAssociations != null && typedAssociations.containsKey(type)) {
				typedAssociations.get(type).remove(construct);
				typedAssociations.get(type).add(newConstruct);
			}
		}
	}

	private void removeVirtualConstruct(IAssociationRole construct, IAssociationRole newConstruct) {
		Map<IAssociationRole, ITopic> associationRoleTypes = getRoleTypesMap();
		if (associationRoleTypes != null && associationRoleTypes.containsKey(construct)) {
			ITopic type = associationRoleTypes.remove(construct);
			associationRoleTypes.put(newConstruct, type);
			Map<ITopic, Set<IAssociationRole>> typedAssociationRoles = getTypedRolesMap();
			if (typedAssociationRoles != null && typedAssociationRoles.containsKey(type)) {
				typedAssociationRoles.get(type).remove(construct);
				typedAssociationRoles.get(type).add(newConstruct);
			}
		}
	}

	private void removeVirtualConstruct(IName construct, IName newConstruct) {
		Map<IName, ITopic> nameTypes = getNameTypesMap();
		if (nameTypes != null && nameTypes.containsKey(construct)) {
			ITopic type = nameTypes.remove(construct);
			nameTypes.put(newConstruct, type);
			Map<ITopic, Set<IName>> typedNames = getTypedNamesMap();
			if (typedNames != null && typedNames.containsKey(type)) {
				typedNames.get(type).remove(construct);
				typedNames.get(type).add(newConstruct);
			}
		}
	}

	private void removeVirtualConstruct(IOccurrence construct, IOccurrence newConstruct) {
		Map<IOccurrence, ITopic> occurrenceTypes = getOccurrenceTypesMap();
		if (occurrenceTypes != null && occurrenceTypes.containsKey(construct)) {
			ITopic type = occurrenceTypes.remove(construct);
			occurrenceTypes.put(newConstruct, type);
			Map<ITopic, Set<IOccurrence>> typedOccurrences = getTypedOccurrencesMap();
			if (typedOccurrences != null && typedOccurrences.containsKey(type)) {
				typedOccurrences.get(type).remove(construct);
				typedOccurrences.get(type).add(newConstruct);
			}
		}
	}
}
