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
import java.util.Set;

import org.tmapi.core.Typed;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of type-typed relations
 * 
 * @author Sven Krosse
 */
public class TypedStore implements IDataStore {

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
	public TypedStore(final InMemoryTopicMapStore store) {
		this.store = store;
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
	 * Remove the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the removed type
	 */
	public ITopic removeType(IName typed) {
		if (nameTypes != null && nameTypes.containsKey(typed)) {
			ITopic t = nameTypes.remove(typed);
			Set<IName> set = typedNames.get(t);
			if (set != null) {
				set.remove(typed);
				if (set.isEmpty()) {
					typedNames.remove(t);
				}
			}
			return t;
		}
		return null;
	}

	/**
	 * Remove the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the removed type
	 */
	public ITopic removeType(IOccurrence typed) {
		if (occurrenceTypes != null && occurrenceTypes.containsKey(typed)) {
			ITopic t = occurrenceTypes.remove(typed);
			Set<IOccurrence> set = typedOccurrences.get(t);
			if (set != null) {
				set.remove(typed);
				if (set.isEmpty()) {
					typedOccurrences.remove(t);
				}
			}
			return t;
		}
		return null;
	}

	/**
	 * Remove the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the removed type
	 */
	public ITopic removeType(IAssociation typed) {
		if (associationTypes != null && associationTypes.containsKey(typed)) {
			ITopic t = associationTypes.remove(typed);
			Set<IAssociation> set = typedAssociations.get(t);
			if (set != null) {
				set.remove(typed);
				if (set.isEmpty()) {
					typedAssociations.remove(t);
				}
			}
			return t;
		}
		return null;
	}

	/**
	 * Remove the given type for the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the removed type
	 */
	public ITopic removeType(IAssociationRole typed) {
		if (roleTypes != null && roleTypes.containsKey(typed)) {
			ITopic t = roleTypes.remove(typed);
			Set<IAssociationRole> set = typedRoles.get(t);
			if (set != null) {
				set.remove(typed);
				if (set.isEmpty()) {
					typedRoles.remove(t);
				}
			}
			return t;
		}
		return null;
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
	public void setType(IName typed, ITopic t) {
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
			typedNames.put(t, set);
		}
		set.add(typed);
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
			typedOccurrences.put(t, set);
		}
		set.add(typed);
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
			typedAssociations.put(t, set);
		}
		set.add(typed);
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
			typedRoles.put(t, set);
		}
		set.add(typed);
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
	public ITopic getType(IName typed) {
		if (nameTypes == null || !nameTypes.containsKey(typed)) {
			throw new TopicMapStoreException("The type of the given typed item does not exit.");
		}
		return nameTypes.get(typed);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IOccurrence typed) {
		if (occurrenceTypes == null || !occurrenceTypes.containsKey(typed)) {
			throw new TopicMapStoreException("The type of the given typed item does not exit.");
		}
		return occurrenceTypes.get(typed);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IAssociation typed) {
		if (associationTypes == null || !associationTypes.containsKey(typed)) {
			throw new TopicMapStoreException("The type of the given typed item does not exit.");
		}
		return associationTypes.get(typed);
	}

	/**
	 * Return the type of the typed attribute
	 * 
	 * @param typed
	 *            the typed item
	 * @return the type
	 */
	public ITopic getType(IAssociationRole typed) {
		if (roleTypes == null || !roleTypes.containsKey(typed)) {
			throw new TopicMapStoreException("The type of the given typed item does not exit.");
		}
		return roleTypes.get(typed);
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
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
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
		if (typedAssociations != null && typedAssociations.containsKey(t)) {
			return typedAssociations.get(t);
		}
		return Collections.emptySet();
	}

	/**
	 * Returns the typed association role items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed association role items
	 */
	public Set<IAssociationRole> getTypedRoles(ITopic t) {
		if (typedRoles != null && typedRoles.containsKey(t)) {
			return typedRoles.get(t);
		}
		return Collections.emptySet();
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
		if (set.isEmpty()) {
			return Collections.emptySet();
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
	public Set<IName> getTypedNames(ITopic t) {
		if (typedNames != null && typedNames.containsKey(t)) {
			return typedNames.get(t);
		}
		return Collections.emptySet();
	}

	/**
	 * Returns the typed names items of the given type.
	 * 
	 * @param t
	 *            the type
	 * @return a set containing all typed characteristics
	 */
	public Set<IOccurrence> getTypedOccurrences(ITopic t) {
		if (typedOccurrences != null && typedOccurrences.containsKey(t)) {
			return typedOccurrences.get(t);
		}
		return Collections.emptySet();
	}

	/**
	 * Removing the given topic as type of each stored typed. The typed objects will be removed too.
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
		if (removed.isEmpty()) {
			return Collections.emptySet();
		}
		return removed;
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		/*
		 * replace as name type
		 */
		if (typedNames != null && typedNames.containsKey(topic)) {
			Set<IName> names = typedNames.get(topic);
			for (IName n : names) {
				nameTypes.put(n, replacement);
				/*
				 * store revision
				 */
				store.storeRevision(revision, TopicMapEventType.TYPE_SET, n, replacement, topic);
			}
			if (typedNames.containsKey(replacement)) {
				names.addAll(typedNames.get(replacement));
			}
			typedNames.put(replacement, names);
			typedNames.remove(topic);
		}

		/*
		 * replace as occurrence type
		 */
		if (typedOccurrences != null && typedOccurrences.containsKey(topic)) {
			Set<IOccurrence> occurrences = typedOccurrences.get(topic);
			for (IOccurrence o : occurrences) {
				occurrenceTypes.put(o, replacement);
				/*
				 * store revision
				 */
				store.storeRevision(revision, TopicMapEventType.TYPE_SET, o, replacement, topic);
			}
			if (typedOccurrences.containsKey(replacement)) {
				occurrences.addAll(typedOccurrences.get(replacement));
			}
			typedOccurrences.put(replacement, occurrences);
			typedOccurrences.remove(topic);
		}

		/*
		 * replace as association type
		 */
		if (typedAssociations != null && typedAssociations.containsKey(topic)) {
			Set<IAssociation> associations = typedAssociations.get(topic);
			for (IAssociation a : associations) {
				associationTypes.put(a, replacement);
				/*
				 * store revision
				 */
				store.storeRevision(revision, TopicMapEventType.TYPE_SET, a, replacement, topic);
			}
			if (typedAssociations.containsKey(replacement)) {
				associations.addAll(typedAssociations.get(replacement));
			}
			typedAssociations.put(replacement, associations);
			typedAssociations.remove(topic);
		}

		/*
		 * replace as role type
		 */
		if (typedRoles != null && typedRoles.containsKey(topic)) {
			Set<IAssociationRole> roles = typedRoles.get(topic);
			for (IAssociationRole r : roles) {
				roleTypes.put(r, replacement);
				/*
				 * store revision
				 */
				store.storeRevision(revision, TopicMapEventType.TYPE_SET, r, replacement, topic);
			}
			if (typedRoles.containsKey(replacement)) {
				roles.addAll(typedRoles.get(replacement));
			}
			typedRoles.put(replacement, roles);
			typedRoles.remove(topic);
		}
	}

	/**
	 * Return a set containing all topic types used as type of a characteristics item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getCharacteristicTypes() {
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(getNameTypes());
		set.addAll(getOccurrenceTypes());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of a role item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getRoleTypes() {
		if (typedRoles == null) {
			return Collections.emptySet();
		}
		return HashUtil.getHashSet(typedRoles.keySet());
	}

	/**
	 * Return a set containing all topic types used as type of an association item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getAssociationTypes() {
		if (typedAssociations == null) {
			return Collections.emptySet();
		}
		return HashUtil.getHashSet(typedAssociations.keySet());
	}

	/**
	 * Return a set containing all topic types used as type of a name item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getNameTypes() {
		if (typedNames == null) {
			return Collections.emptySet();
		}
		return HashUtil.getHashSet(typedNames.keySet());
	}

	/**
	 * Return a set containing all topic types used as type of an occurrence item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getOccurrenceTypes() {
		if (typedOccurrences == null) {
			return Collections.emptySet();
		}
		return HashUtil.getHashSet(typedOccurrences.keySet());
	}

	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	protected InMemoryTopicMapStore getStore() {
		return store;
	}
}
