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
package de.topicmapslab.majortom.database.cache;

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of topic and topic type relations
 * 
 * @author Sven Krosse
 * 
 */
class TopicTypeCache implements ITopicMapListener {

	/**
	 * enumeration specify the key for internal maps
	 */
	enum Key {
		TYPE,

		INSTANCE,

		SUBTYPE,

		SUPERTYPE
	}

	/**
	 * Map storing the type-hierarchy
	 */
	private Map<Key, Set<ITopic>> typeHierarchy;
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
	 * internal storage of the type-supertypes relations
	 */
	private Map<ITopic, Set<ITopic>> transitiveSupertypes;
	/**
	 * internal storage of the type-instance relations
	 */
	private Map<ITopic, Set<ITopic>> transitiveInstances;
	/**
	 * internal storage of the instance-types relations
	 */
	private Map<ITopic, Set<ITopic>> transitiveTypes;
	/**
	 * internal storage of the type-subtypes relations
	 */
	private Map<ITopic, Set<ITopic>> transitiveSubtypes;

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
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
		if (transitiveTypes != null) {
			transitiveTypes.clear();
		}
		if (transitiveSupertypes != null) {
			transitiveSupertypes.clear();
		}
		if (transitiveSubtypes != null) {
			transitiveSubtypes.clear();
		}
		if (transitiveInstances != null) {
			transitiveInstances.clear();
		}
		if (typeHierarchy != null) {
			typeHierarchy.clear();
		}
	}

	/**
	 * Secure extraction of the values from the given map.
	 * 
	 * @param map
	 *            the map
	 * @param key
	 *            the key
	 * @return the values or <code>null</code> if the map does not contains the
	 *         given key or is <code>null</code>
	 */
	public Set<ITopic> getTypeHierarchy(Map<ITopic, Set<ITopic>> map, ITopic key) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * Return all direct-instances of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the direct instances
	 */
	public Set<ITopic> getDirectInstances(ITopic type) {
		return getTypeHierarchy(instances, type);
	}

	/**
	 * Return all direct-types of the given topic item
	 * 
	 * @param instance
	 *            the topic item
	 * @return the direct types
	 */
	public Set<ITopic> getDirectTypes(ITopic instance) {
		return getTypeHierarchy(types, instance);
	}

	/**
	 * Return all transitive instances of the given topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @return the instances
	 */
	public Set<ITopic> getInstances(ITopic type) {
		return getTypeHierarchy(transitiveInstances, type);
	}

	/**
	 * Return all transitive types of the given topic item.
	 * 
	 * @param instance
	 *            the topic item
	 * @return the types
	 */
	public Set<ITopic> getTypes(ITopic instance) {
		return getTypeHierarchy(transitiveTypes, instance);
	}

	/**
	 * Return all direct super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getDirectSupertypes(ITopic type) {
		return getTypeHierarchy(supertypes, type);
	}

	/**
	 * Return all super types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getSupertypes(ITopic type) {
		return getTypeHierarchy(transitiveSupertypes, type);
	}

	/**
	 * Return all direct sub types of the given topic type
	 * 
	 * @param type
	 *            the topic type
	 * @return the super types
	 */
	public Set<ITopic> getDirectSubtypes(ITopic type) {
		return getTypeHierarchy(subtypes, type);
	}

	/**
	 * Return all sub types of the given topic type.
	 * 
	 * @param type
	 *            the topic type
	 * @return the sub types
	 */
	public Set<ITopic> getSubtypes(ITopic type) {
		return getTypeHierarchy(transitiveSubtypes, type);
	}

	/**
	 * Returns the type-hierarchy of the specified type ( instances, types,
	 * subtypes or supertypes ) of the topic map.
	 * 
	 * @param type
	 *            the key
	 * @return the type-hierarchy
	 */
	public Set<ITopic> getTypeHierarchy(Key type) {
		if (typeHierarchy == null || !typeHierarchy.containsKey(type)) {
			return null;
		}
		return typeHierarchy.get(type);
	}

	/**
	 * Return all transitive instances of the topic map;
	 * 
	 * @return the instances
	 */
	public Set<ITopic> getInstances() {
		return getTypeHierarchy(Key.INSTANCE);
	}

	/**
	 * Return all transitive types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getTypes() {
		return getTypeHierarchy(Key.TYPE);
	}

	/**
	 * Return all super types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSupertypes() {
		return getTypeHierarchy(Key.SUPERTYPE);
	}

	/**
	 * Return all sub types of the topic map.
	 * 
	 * @return the types
	 */
	public Set<ITopic> getSubtypes() {
		return getTypeHierarchy(Key.SUBTYPE);
	}

	/**
	 * Cache the given types of the topic to the internal cache
	 * 
	 * @param topic
	 *            the topic
	 * @param set
	 *            the types
	 */
	public void cacheTypes(ITopic topic, Set<ITopic> set) {
		if (types == null) {
			types = HashUtil.getHashMap();
		}
		types.put(topic, set);
	}

	/**
	 * Cache the given supertypes of the topic to the internal cache
	 * 
	 * @param topic
	 *            the topic
	 * @param set
	 *            the supertypes
	 */
	public void cacheSupertypes(ITopic topic, Set<ITopic> set) {
		if (supertypes == null) {
			supertypes = HashUtil.getHashMap();
		}
		supertypes.put(topic, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * topic or association (potential TMDM association) was removed
		 */
		if (event == TopicMapEventType.TOPIC_REMOVED
				|| event == TopicMapEventType.ASSOCIATION_REMOVED
				|| event == TopicMapEventType.ROLE_REMOVED) {
			// XXX check type of association before and handle specific
			clear();
		}
		/*
		 * association was created
		 */
		else if (event == TopicMapEventType.ASSOCIATION_ADDED
				|| event == TopicMapEventType.PLAYER_MODIFIED) {
			// XXX check type of association before and handle specific
			clear();
		}
		/*
		 * type was removed
		 */
		else if (event == TopicMapEventType.TYPE_REMOVED
				|| event == TopicMapEventType.TYPE_ADDED) {
			ITopic instance = (ITopic) notifier;
			ITopic type = null;
			if (event == TopicMapEventType.TYPE_REMOVED) {
				type = (ITopic) oldValue;
			} else {
				type = (ITopic) newValue;
			}
			// remove transitive types -> does not know the supertypes of type
			if (transitiveTypes != null
					&& transitiveInstances.containsKey(instance)) {
				transitiveTypes.remove(instance);
			}
			// remove transitive instances -> does not know the supertypes of
			// type
			if (transitiveInstances != null) {
				transitiveInstances.clear();
			}
			if (types != null && types.containsKey(instance)) {
				// remove old type
				if (event == TopicMapEventType.TYPE_REMOVED) {
					types.get(instance).remove(type);
				}
				// add new type
				else {
					types.get(instance).add(type);
				}
			}
			if (instances != null && instances.containsKey(type)) {
				// remove old instance
				if (event == TopicMapEventType.TYPE_REMOVED) {
					instances.get(type).remove(instance);
				}
				// add new instance
				else {
					instances.get(type).add(instance);
				}
			}
		}
		/*
		 * supertype was removed
		 */
		else if (event == TopicMapEventType.SUPERTYPE_REMOVED
				|| event == TopicMapEventType.SUPERTYPE_ADDED) {
			ITopic subtype = (ITopic) notifier;
			ITopic supertype = null;
			if (event == TopicMapEventType.SUPERTYPE_REMOVED) {
				supertype = (ITopic) oldValue;
			} else {
				supertype = (ITopic) newValue;
			}
			// transitive type hierarchy changed -> clear
			if (transitiveTypes != null) {
				transitiveTypes.clear();
			}
			// transitive type hierarchy changed -> clear
			if (transitiveInstances != null) {
				transitiveInstances.clear();
			}
			// transitive type hierarchy changed -> clear
			if (transitiveSupertypes != null) {
				transitiveSupertypes.clear();
			}
			// transitive type hierarchy changed -> clear
			if (transitiveSubtypes != null) {
				transitiveSubtypes.clear();
			}
			if (supertypes != null && supertypes.containsKey(subtype)) {
				// remove old supertype
				if (event == TopicMapEventType.SUPERTYPE_REMOVED) {
					supertypes.get(subtype).remove(supertype);
				}
				// add new supertype
				else {
					supertypes.get(subtype).add(supertype);
				}
			}
			if (subtypes != null && subtypes.containsKey(supertype)) {
				// remove old subtype
				if (event == TopicMapEventType.SUPERTYPE_REMOVED) {
					subtypes.get(supertype).remove(subtype);
				}
				// add new subtype
				else {
					subtypes.get(supertype).add(subtype);
				}
			}
		}

	}
}
