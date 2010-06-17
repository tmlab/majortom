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
package de.topicmapslab.majortom.inMemory.transaction.internal;

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.inMemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inMemory.store.internal.TypedStore;
import de.topicmapslab.majortom.inMemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParamterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyTypedStore extends TypedStore {

	private Set<ITypeable> modifiedConstructs;
	private Map<ITopic, Set<ITypeable>> changedTypes;

	/**
	 * @param store
	 */
	public LazyTypedStore(InMemoryTopicMapStore store) {
		super(store);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IAssociation typed) {
		if (getLazyIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}		
		return getLazyIdentityStore().createLazyStub(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IAssociationRole typed) {
		if (getLazyIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		return getLazyIdentityStore().createLazyStub(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IName typed) {
		if (getLazyIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		return getLazyIdentityStore().createLazyStub(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getType(IOccurrence typed) {
		if (getLazyIdentityStore().isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		try {
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		return getLazyIdentityStore().createLazyStub(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> getTypedAssociations(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociation> set = HashUtil.getHashSet();
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
			if (getLazyIdentityStore().isRemovedConstruct((IAssociation) a)) {
				continue;
			}
			set.add(getLazyIdentityStore().createLazyStub((IAssociation) a));
		}

		/*
		 * remove all old type relations
		 */
		if (changedTypes != null && changedTypes.containsKey(t)) {
			set.removeAll(changedTypes.get(t));
		}

		/*
		 * add internal information of transaction context
		 */
		set.addAll(super.getTypedAssociations(t));

		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> getTypedRoles(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
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
			if (getLazyIdentityStore().isRemovedConstruct((IAssociationRole) r)) {
				continue;
			}
			set.add(getLazyIdentityStore().createLazyStub((IAssociationRole) r));
		}

		/*
		 * remove all old type relations
		 */
		if (changedTypes != null && changedTypes.containsKey(t)) {
			set.removeAll(changedTypes.get(t));
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
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IName> set = HashUtil.getHashSet();
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
			if (getLazyIdentityStore().isRemovedConstruct((IName) n)) {
				continue;
			}
			set.add(getLazyIdentityStore().createLazyStub((IName) n));
		}

		/*
		 * remove all old type relations
		 */
		if (changedTypes != null && changedTypes.containsKey(t)) {
			set.removeAll(changedTypes.get(t));
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
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
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
			if (getLazyIdentityStore().isRemovedConstruct((IOccurrence) o)) {
				continue;
			}
			set.add(getLazyIdentityStore().createLazyStub((IOccurrence) o));
		}

		/*
		 * remove all old type relations
		 */
		if (changedTypes != null && changedTypes.containsKey(t)) {
			set.removeAll(changedTypes.get(t));
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
		if (getLazyIdentityStore().isRemovedConstruct(typed) || getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IAssociationRole typed, ITopic t) {
		if (getLazyIdentityStore().isRemovedConstruct(typed) || getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IName typed, ITopic t) {
		if (getLazyIdentityStore().isRemovedConstruct(typed) || getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		super.setType(typed, t);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(IOccurrence typed, ITopic t) {
		if (getLazyIdentityStore().isRemovedConstruct(typed) || getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
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
			if ( type != null ){
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}		
		return getLazyIdentityStore().createLazyStub(type);		
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IAssociationRole typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if ( type != null ){
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}		
		return getLazyIdentityStore().createLazyStub(type);		
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IName typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if ( type != null ){
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}		
		return getLazyIdentityStore().createLazyStub(type);		
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IOccurrence typed) {
		ITopic type = null;
		try {
			type = super.getType(typed);
			if ( type != null ){
				return super.removeType(typed);
			}
			type = super.getType(typed);
			if (type == null) {
				type = (ITopic) getStore().getRealStore().doRead(typed, TopicMapStoreParamterType.TYPE);
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}		
		return getLazyIdentityStore().createLazyStub(type);		
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
			 * check if typed object was deleted by the current transaction
			 * context and is really a type
			 */
			if (!getLazyIdentityStore().isRemovedConstruct(t) && !getTypedAssociations(t).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(t));
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
			 * check if typed object was deleted by the current transaction
			 * context and is really a type
			 */
			if (!getLazyIdentityStore().isRemovedConstruct(t) && !getTypedRoles(t).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(t));
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
			 * check if typed object was deleted by the current transaction
			 * context and is really a type
			 */
			if (!getLazyIdentityStore().isRemovedConstruct(t) && !getTypedNames(t).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(t));
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
			 * check if typed object was deleted by the current transaction
			 * context and is really a type
			 */
			if (!getLazyIdentityStore().isRemovedConstruct(t) && !getTypedOccurrences(t).isEmpty()) {
				set.add(getLazyIdentityStore().createLazyStub(t));
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
	 * Method checks if the type change of the given construct is the first type
	 * change in the transaction context. If it is the first, the old relation
	 * stored to filter them later.
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
			/*
			 * add new type change to type specific list
			 */
			Set<ITypeable> set = changedTypes.get(oldType);
			if (set == null) {
				set = HashUtil.getHashSet();
			}
			set.add(typed);
			changedTypes.put(oldType, set);
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
	 * {@inheritDoc}
	 */
	protected InMemoryTransactionTopicMapStore getStore() {
		return (InMemoryTransactionTopicMapStore) super.getStore();
	}
}
