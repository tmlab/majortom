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
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;

import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TypedCache implements IDataStore {

	/**
	 * storage map of the type-name relation
	 */
	private Map<ITopic, Set<IName>> typedNames;
	/**
	 * storage map of the name-type relation
	 */
	private Map<IName, ITopic> nameTypes;

	/**
	 * storage map of the type-occurrence relation
	 */
	private Map<ITopic, Set<IOccurrence>> typedOccurrences;
	/**
	 * storage map of the occurrence-type relation
	 */
	private Map<IOccurrence, ITopic> occurrenceTypes;

	/**
	 * storage map of the type-association relation
	 */
	private Map<ITopic, Set<IAssociation>> typedAssociations;
	/**
	 * storage map of the association-type relation
	 */
	private Map<IAssociation, ITopic> associationTypes;

	/**
	 * storage map of the type-role relation
	 */
	private Map<ITopic, Set<IAssociationRole>> typedRoles;
	/**
	 * storage map of the role-type relation
	 */
	private Map<IAssociationRole, ITopic> roleTypes;
	private Set<ITypeable> modifiedConstructs;
	private Map<ITopic, Set<ITypeable>> changedTypes;

	private final TransactionTopicMapStore topicMapStore;

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the transaction topic map store
	 */
	public TypedCache(TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(Typed typed) {
		if (typed instanceof IName) {
			return getType((IName) typed);
		} else if (typed instanceof IOccurrence) {
			return getType((IOccurrence) typed);
		} else if (typed instanceof IAssociation) {
			return getType((IAssociation) typed);
		} else if (typed instanceof IAssociationRole) {
			return getType((IAssociationRole) typed);
		} else {
			throw new TopicMapStoreException("Unsupported parameter type '" + typed.getClass() + "'.");
		}
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IAssociation typed) {
		if (isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		if (associationTypes != null && associationTypes.containsKey(typed)) {
			type = associationTypes.get(typed);
		}
		if (type == null) {
			type = (ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE);
		}
		return getTransactionStore().getIdentityStore().createLazyStub(type);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IAssociationRole typed) {
		if (isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		if (roleTypes != null && roleTypes.containsKey(typed)) {
			type = roleTypes.get(typed);
		}
		if (type == null) {
			type = (ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE);
		}
		return getTransactionStore().getIdentityStore().createLazyStub(type);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IName typed) {
		if (isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		if (nameTypes != null && nameTypes.containsKey(typed)) {
			type = nameTypes.get(typed);
		}
		if (type == null) {
			type = (ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE);
		}
		return getTransactionStore().getIdentityStore().createLazyStub(type);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IOccurrence typed) {
		if (isRemovedConstruct(typed)) {
			throw new ConstructRemovedException(typed);
		}
		ITopic type = null;
		if (occurrenceTypes != null && occurrenceTypes.containsKey(typed)) {
			type = occurrenceTypes.get(typed);
		}
		if (type == null) {
			type = (ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE);
		}
		return getTransactionStore().getIdentityStore().createLazyStub(type);
	}

	/**
	 * Returns the typed items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed attributes
	 */
	public Set<ITypeable> getTyped(ITopic t) {
		Set<ITypeable> set = HashUtil.getHashSet();
		set.addAll(getTypedAssociations(t));
		set.addAll(getTypedRoles(t));
		set.addAll(getTypedNames(t));
		set.addAll(getTypedOccurrences(t));
		return set;
	}

	/**
	 * Returns the typed items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed attributes
	 */
	public Set<IAssociation> getTypedAssociations(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociation> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		/*
		 * get association typed by given type
		 */
		for (Association a : index.getAssociations(t)) {
			if (isRemovedConstruct((IAssociation) a)) {
				continue;
			}
			set.add(getTransactionStore().getIdentityStore().createLazyStub((IAssociation) a));
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
		if (typedAssociations != null && typedAssociations.containsKey(t)) {
			set.addAll(typedAssociations.get(t));
		}
		return set;
	}

	/**
	 * Returns the typed association role items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed association role items
	 */
	public Set<IAssociationRole> getTypedRoles(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		/*
		 * get roles typed by given type
		 */
		for (Role r : index.getRoles(t)) {
			if (isRemovedConstruct((IAssociationRole) r)) {
				continue;
			}
			set.add(getTransactionStore().getIdentityStore().createLazyStub((IAssociationRole) r));
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
		if (typedRoles != null && typedRoles.containsKey(t)) {
			set.addAll(typedRoles.get(t));
		}

		return set;
	}

	/**
	 * Returns the typed names items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed characteristics
	 */
	public Set<ICharacteristics> getTypedCharacteristics(ITopic t) {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getTypedNames(t));
		set.addAll(getTypedOccurrences(t));
		return set;
	}

	/**
	 * Returns the typed names items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed characteristics
	 */
	public Set<IName> getTypedNames(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IName> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		/*
		 * get names typed by given type
		 */
		for (Name n : index.getNames(t)) {
			if (isRemovedConstruct((IName) n)) {
				continue;
			}
			set.add(getTransactionStore().getIdentityStore().createLazyStub((IName) n));
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
		if (typedNames != null && typedNames.containsKey(t)) {
			set.addAll(typedNames.get(t));
		}

		return set;
	}

	/**
	 * Returns the typed names items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed characteristics
	 */
	public Set<IOccurrence> getTypedOccurrences(ITopic t) {
		/*
		 * check if type attribute was deleted within the current transaction
		 * context
		 */
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		/*
		 * get occurrences typed by given type
		 */
		for (Occurrence o : index.getOccurrences(t)) {
			if (isRemovedConstruct((IOccurrence) o)) {
				continue;
			}
			set.add(getTransactionStore().getIdentityStore().createLazyStub((IOccurrence) o));
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
		if (typedOccurrences != null && typedOccurrences.containsKey(t)) {
			set.addAll(typedOccurrences.get(t));
		}

		return set;
	}

	/**
	 * Store the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @param t
	 *            the topic
	 */
	public void setType(Typed typed, ITopic t) {
		if (typed instanceof IName) {
			setType((IName) typed, t);
		} else if (typed instanceof IOccurrence) {
			setType((IOccurrence) typed, t);
		} else if (typed instanceof IAssociation) {
			setType((IAssociation) typed, t);
		} else if (typed instanceof IAssociationRole) {
			setType((IAssociationRole) typed, t);
		} else {
			throw new TopicMapStoreException("Unsupported parameter type '" + typed.getClass() + "'.");
		}
	}

	/**
	 * Store the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @param t
	 *            the topic
	 */
	public void setType(IAssociation typed, ITopic t) {
		if (isRemovedConstruct(typed) || isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		if (associationTypes == null) {
			associationTypes = HashUtil.getHashMap();
		}
		associationTypes.put(typed, t);

		if (typedAssociations == null) {
			typedAssociations = HashUtil.getHashMap();
		}
		Set<IAssociation> set = typedAssociations.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(typed);
		typedAssociations.put(t, set);
	}

	/**
	 * Store the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @param t
	 *            the topic
	 */
	public void setType(IAssociationRole typed, ITopic t) {
		if (isRemovedConstruct(typed) || isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		if (roleTypes == null) {
			roleTypes = HashUtil.getHashMap();
		}
		roleTypes.put(typed, t);

		if (typedRoles == null) {
			typedRoles = HashUtil.getHashMap();
		}
		Set<IAssociationRole> set = typedRoles.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(typed);
		typedRoles.put(t, set);
	}

	/**
	 * Store the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @param t
	 *            the topic
	 */
	public void setType(IName typed, ITopic t) {
		if (isRemovedConstruct(typed) || isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		if (nameTypes == null) {
			nameTypes = HashUtil.getHashMap();
		}
		nameTypes.put(typed, t);

		if (typedNames == null) {
			typedNames = HashUtil.getHashMap();
		}
		Set<IName> set = typedNames.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(typed);
		typedNames.put(t, set);
	}

	/**
	 * Store the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @param t
	 *            the topic
	 */
	public void setType(IOccurrence typed, ITopic t) {
		if (isRemovedConstruct(typed) || isRemovedConstruct(t)) {
			throw new ConstructRemovedException(typed);
		}
		storeOldRelation(typed);
		if (occurrenceTypes == null) {
			occurrenceTypes = HashUtil.getHashMap();
		}
		occurrenceTypes.put(typed, t);

		if (typedOccurrences == null) {
			typedOccurrences = HashUtil.getHashMap();
		}
		Set<IOccurrence> set = typedOccurrences.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(typed);
		typedOccurrences.put(t, set);
	}

	/**
	 * Remove the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the removed type
	 */
	public ITopic removeType(Typed typed) {
		if (typed instanceof IName) {
			return removeType((IName) typed);
		} else if (typed instanceof IOccurrence) {
			return removeType((IOccurrence) typed);
		} else if (typed instanceof IAssociation) {
			return removeType((IAssociation) typed);
		} else if (typed instanceof IAssociationRole) {
			return removeType((IAssociationRole) typed);
		} else {
			throw new TopicMapStoreException("Unsupported parameter type '" + typed.getClass() + "'.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IAssociation typed) {		
		ITopic type = null;
		if (associationTypes != null && associationTypes.containsKey(typed)) {
			type = associationTypes.remove(typed);
			Set<IAssociation> set = typedAssociations.get(type);
			set.remove(typed);
			if (set.isEmpty()) {
				typedAssociations.remove(type);
			} else {
				typedAssociations.put(type, set);
			}
		}
		if (type == null) {
			storeOldRelation(typed);
			type = getTransactionStore().getIdentityStore().createLazyStub((ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE));
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IAssociationRole typed) {
		ITopic type = null;
		if (roleTypes != null && roleTypes.containsKey(typed)) {
			type = roleTypes.remove(typed);
			Set<IAssociationRole> set = typedRoles.get(type);
			set.remove(typed);
			if (set.isEmpty()) {
				typedRoles.remove(type);
			} else {
				typedRoles.put(type, set);
			}
		}
		if (type == null) {
			storeOldRelation(typed);
			type = getTransactionStore().getIdentityStore().createLazyStub((ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE));
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IName typed) {
		ITopic type = null;
		if (nameTypes != null && nameTypes.containsKey(typed)) {
			type = nameTypes.remove(typed);
			Set<IName> set = typedNames.get(type);
			set.remove(typed);
			if (set.isEmpty()) {
				typedNames.remove(type);
			} else {
				typedNames.put(type, set);
			}
		}
		if (type == null) {
			storeOldRelation(typed);
			type = getTransactionStore().getIdentityStore().createLazyStub((ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE));
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic removeType(IOccurrence typed) {
		ITopic type = null;
		if (occurrenceTypes != null && occurrenceTypes.containsKey(typed)) {
			type = occurrenceTypes.remove(typed);
			Set<IOccurrence> set = typedOccurrences.get(type);
			set.remove(typed);
			if (set.isEmpty()) {
				typedOccurrences.remove(type);
			} else {
				typedOccurrences.put(type, set);
			}
		}
		if (type == null) {
			storeOldRelation(typed);
			type = getTransactionStore().getIdentityStore().createLazyStub((ITopic) getTopicMapStore().doRead(typed, TopicMapStoreParameterType.TYPE));
		}
		return type;
	}

	/**
	 * Return a set containing all topic types used as type of an association
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getAssociationTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
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
			if (!isRemovedConstruct(t) && !getTypedAssociations(t).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(t));
			}
		}

		/*
		 * add internal stored types
		 */
		if (typedAssociations != null) {
			set.addAll(typedAssociations.keySet());
		}
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of a role item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getRoleTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
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
			if (!isRemovedConstruct(t) && !getTypedRoles(t).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(t));
			}
		}

		/*
		 * add internal stored types
		 */
		if (typedRoles != null) {
			set.addAll(typedRoles.keySet());
		}
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of a characteristics
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getCharacteristicTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(getNameTypes());
		set.addAll(getOccurrenceTypes());
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of a name item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getNameTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
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
			if (!isRemovedConstruct(t) && !getTypedNames(t).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(t));
			}
		}

		/*
		 * add internal stored types
		 */
		if (typedNames != null) {
			set.addAll(typedNames.keySet());
		}
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of an occurrence
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getOccurrenceTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * get index of real store instance
		 */
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
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
			if (!getTransactionStore().getIdentityStore().isRemovedConstruct(t) && !getTypedOccurrences(t).isEmpty()) {
				set.add(getTransactionStore().getIdentityStore().createLazyStub(t));
			}
		}

		/*
		 * add internal stored types
		 */
		if (typedOccurrences != null) {
			set.addAll(typedOccurrences.keySet());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (typedNames != null) {
			typedNames.clear();
		}
		if (nameTypes != null) {
			nameTypes.clear();
		}
		if (typedOccurrences != null) {
			typedOccurrences.clear();
		}
		if (occurrenceTypes != null) {
			occurrenceTypes.clear();
		}
		if (typedAssociations != null) {
			typedAssociations.clear();
		}
		if (associationTypes != null) {
			associationTypes.clear();
		}
		if (typedRoles != null) {
			typedRoles.clear();
		}
		if (roleTypes != null) {
			roleTypes.clear();
		}
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
			ITopic oldType = getType(typed);
			if (oldType != null) {
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
		 * replace as name type
		 */
		Set<IName> names = HashUtil.getHashSet(getTypedNames(topic));
		for (IName n : names) {
			setType(n, replacement);
		}

		/*
		 * replace as occurrence type
		 */
		Set<IOccurrence> occurrences = HashUtil.getHashSet(getTypedOccurrences(topic));
		for (IOccurrence o : occurrences) {
			setType(o, replacement);
		}

		/*
		 * replace as association type
		 */
		Set<IAssociation> associations = HashUtil.getHashSet(getTypedAssociations(topic));
		for (IAssociation a : associations) {
			setType(a, replacement);
		}

		/*
		 * replace as role type
		 */
		Set<IAssociationRole> roles = HashUtil.getHashSet(getTypedRoles(topic));
		for (IAssociationRole r : roles) {
			setType(r, replacement);
		}
	}

	/**
	 * Removing the given topic as type of each stored typed. The typed objects
	 * will be removed too.
	 * 
	 * @param type
	 *            the type
	 * @return the removed typed items
	 */
	public Set<ITypeable> removeType(ITopic type) {
		Set<ITypeable> removed = HashUtil.getHashSet();
		/*
		 * remove all associations
		 */
		for (IAssociation a : getTypedAssociations(type)) {
			associationTypes.remove(a);
			removed.add(a);
		}
		if (typedAssociations != null) {
			typedAssociations.remove(type);
		}
		/*
		 * remove all roles
		 */

		for (IAssociationRole r : getTypedRoles(type)) {
			roleTypes.remove(r);
			removed.add(r);
		}
		if (typedRoles != null) {
			typedRoles.remove(type);
		}
		/*
		 * remove all name
		 */
		for (IName c : getTypedNames(type)) {
			nameTypes.remove(c);
			removed.add(c);
		}
		if (typedNames != null) {
			typedNames.remove(type);
		}
		/*
		 * remove all occurrence
		 */
		for (IOccurrence c : getTypedOccurrences(type)) {
			occurrenceTypes.remove(c);
			removed.add(c);
		}
		if (typedOccurrences != null) {
			typedOccurrences.remove(type);
		}
		return removed;
	}
}
