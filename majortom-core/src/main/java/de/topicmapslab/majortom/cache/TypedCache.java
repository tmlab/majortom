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
package de.topicmapslab.majortom.cache;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Typed;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of type-typed relations
 * 
 * @author Sven Krosse
 */
class TypedCache implements ITopicMapListener {

	/**
	 * map containing all known types
	 */
	private Map<Class<? extends ITypeable>, Set<ITopic>> types;

	/**
	 * storage map of the name-type relation
	 */
	private Map<IName, ITopic> nameTypes;
	/**
	 * storage map of the occurrence-type relation
	 */
	private Map<IOccurrence, ITopic> occurrenceTypes;
	/**
	 * storage map of the association-type relation
	 */
	private Map<IAssociation, ITopic> associationTypes;
	/**
	 * storage map of the role-type relation
	 */
	private Map<IAssociationRole, ITopic> roleTypes;

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		if (nameTypes != null) {
			nameTypes.clear();
		}
		if (occurrenceTypes != null) {
			occurrenceTypes.clear();
		}
		if (associationTypes != null) {
			associationTypes.clear();
		}
		if (roleTypes != null) {
			roleTypes.clear();
		}
		if (types != null) {
			types.clear();
		}
	}

	/**
	 * Secure extraction of the type of the given typed attribute from the given
	 * map
	 * 
	 * @param <T>
	 *            the generic type of the typed
	 * @param map
	 *            the map containing the type
	 * @param typed
	 *            the typed
	 * @return the type or <code>null</code> if the key is not contained by the
	 *         given map or the map is <code>null</code>
	 */
	public <T extends ITypeable> ITopic getType(Map<T, ITopic> map, T typed) {
		if (map == null || !map.containsKey(typed)) {
			return null;
		}
		return map.get(typed);
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
			return getType(nameTypes, (IName) typed);
		} else if (typed instanceof IOccurrence) {
			return getType(occurrenceTypes, (IOccurrence) typed);
		} else if (typed instanceof IAssociation) {
			return getType(associationTypes, (IAssociation) typed);
		} else if (typed instanceof IAssociationRole) {
			return getType(roleTypes, (IAssociationRole) typed);
		} else {
			throw new TopicMapStoreException("Unsupported parameter type '"
					+ typed.getClass() + "'.");
		}
	}

	/**
	 * Return a set containing all topic types used as type of a characteristics
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getCharacteristicTypes() {
		Set<ITopic> nt = getNameTypes();
		Set<ITopic> ot = getOccurrenceTypes();
		if (nt == null || ot == null) {
			return null;
		}
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(nt);
		set.addAll(ot);
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Return a set containing all topic types used as type of constructs of the
	 * given class
	 * 
	 * @param clazz
	 *            the class of constructs
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getTypes(Class<? extends ITypeable> clazz) {
		if (types == null || !types.containsKey(clazz)) {
			return null;
		}
		return types.get(clazz);
	}

	/**
	 * Return a set containing all topic types used as type of a role item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getRoleTypes() {
		return getTypes(IAssociationRole.class);
	}

	/**
	 * Return a set containing all topic types used as type of an association
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getAssociationTypes() {
		return getTypes(IAssociation.class);
	}

	/**
	 * Return a set containing all topic types used as type of a name item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getNameTypes() {
		return getTypes(IName.class);
	}

	/**
	 * Return a set containing all topic types used as type of an occurrence
	 * item.
	 * 
	 * @return a set of all types
	 */
	public Set<ITopic> getOccurrenceTypes() {
		return getTypes(IOccurrence.class);
	}

	/**
	 * Cache the type of the given typed construct to internal cache.
	 * 
	 * @param typeable
	 *            the typed construct
	 * @param type
	 *            the type
	 */
	public void cacheType(ITypeable typeable, ITopic type) {
		if (typeable instanceof IName) {
			cacheNameType((IName) typeable, type);
		} else if (typeable instanceof IOccurrence) {
			cacheOccurrenceType((IOccurrence) typeable, type);
		} else if (typeable instanceof IAssociation) {
			cacheAssociationType((IAssociation) typeable, type);
		} else if (typeable instanceof IAssociationRole) {
			cacheRoleType((IAssociationRole) typeable, type);
		}
	}

	/**
	 * Cache the type of the given association to internal cache.
	 * 
	 * @param association
	 *            the association
	 * @param type
	 *            the type
	 */
	public void cacheAssociationType(IAssociation association, ITopic type) {
		if (associationTypes == null) {
			associationTypes = HashUtil.getHashMap();
		}
		associationTypes.put(association, type);
	}

	/**
	 * Cache the type of the given name to internal cache.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public void cacheNameType(IName name, ITopic type) {
		if (nameTypes == null) {
			nameTypes = HashUtil.getHashMap();
		}
		nameTypes.put(name, type);
	}

	/**
	 * Cache the type of the given occurrence to internal cache.
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param type
	 *            the type
	 */
	public void cacheOccurrenceType(IOccurrence occurrence, ITopic type) {
		if (occurrenceTypes == null) {
			occurrenceTypes = HashUtil.getHashMap();
		}
		occurrenceTypes.put(occurrence, type);
	}

	/**
	 * Cache the type of the given role to internal cache.
	 * 
	 * @param role
	 *            the role
	 * @param type
	 *            the type
	 */
	public void cacheRoleType(IAssociationRole role, ITopic type) {
		if (roleTypes == null) {
			roleTypes = HashUtil.getHashMap();
		}
		roleTypes.put(role, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * name was removed
		 */
		if (event == TopicMapEventType.NAME_REMOVED && nameTypes != null) {
			nameTypes.remove(oldValue);
		}
		/*
		 * occurrence was removed
		 */
		else if (event == TopicMapEventType.OCCURRENCE_REMOVED
				&& occurrenceTypes != null) {
			occurrenceTypes.remove(oldValue);
		}
		/*
		 * role was removed
		 */
		else if (event == TopicMapEventType.ROLE_REMOVED && roleTypes != null) {
			roleTypes.remove(oldValue);
		}
		/*
		 * association was removed
		 */
		else if (event == TopicMapEventType.ASSOCIATION_REMOVED
				&& associationTypes != null) {
			associationTypes.remove(oldValue);
		}
		/*
		 * topic was removed -> potential theme
		 */
		else if (event == TopicMapEventType.TOPIC_REMOVED) {
			ITopic topic = (ITopic) oldValue;
			if (associationTypes != null) {
				for (Entry<IAssociation, ITopic> entry : HashUtil
						.getHashSet(associationTypes.entrySet())) {
					if (entry.equals(topic)) {
						associationTypes.remove(entry.getKey());
					}
				}
			}
			if (roleTypes != null) {
				for (Entry<IAssociationRole, ITopic> entry : HashUtil
						.getHashSet(roleTypes.entrySet())) {
					if (entry.equals(topic)) {
						roleTypes.remove(entry.getKey());
					}
				}
			}
			if (occurrenceTypes != null) {
				for (Entry<IOccurrence, ITopic> entry : HashUtil
						.getHashSet(occurrenceTypes.entrySet())) {
					if (entry.equals(topic)) {
						occurrenceTypes.remove(entry.getKey());
					}
				}
			}
			if (nameTypes != null) {
				for (Entry<IName, ITopic> entry : HashUtil.getHashSet(nameTypes
						.entrySet())) {
					if (entry.equals(topic)) {
						nameTypes.remove(entry.getKey());
					}
				}
			}
		}
		/*
		 * type was modified
		 */
		else if (event == TopicMapEventType.TYPE_SET) {
			cacheType((ITypeable) notifier, (ITopic) newValue);
		}
	}
}
