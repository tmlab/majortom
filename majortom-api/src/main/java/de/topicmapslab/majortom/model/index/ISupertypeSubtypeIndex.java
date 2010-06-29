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
package de.topicmapslab.majortom.model.index;

import java.util.Collection;

import org.tmapi.core.Topic;
import org.tmapi.index.Index;

/**
 * Interface definition of an index handling the supertype-subtype relations of
 * the current topic map.
 * 
 * @author Sven Krosse
 * 
 */
public interface ISupertypeSubtypeIndex extends Index {

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @return a collection of all supertypes
	 */
	public Collection<Topic> getSupertypes();

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a collection of all supertypes of the given type
	 */
	public Collection<Topic> getSupertypes(Topic type);

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a collection of all supertypes of the given type
	 */
	public Collection<Topic> getDirectSupertypes(Topic type);

	/**
	 * Returns all topic types being a supertype of at least one given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @return a collection of all supertypes of at least one given type
	 */
	public Collection<Topic> getSupertypes(Topic... types);

	/**
	 * Returns all topic types being a supertype of at least one given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @return a collection of all supertypes of at least one given type
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types);

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * @return a collection of all supertypes of at least one of the given type
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all);

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * @return a collection of all subtypes
	 */
	public Collection<Topic> getSubtypes();

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a collection of all subtypes of the given type
	 */
	public Collection<Topic> getSubtypes(Topic type);

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a collection of all subtypes of the given type
	 */
	public Collection<Topic> getDirectSubtypes(Topic type);

	/**
	 * Returns all topic types being a subtype of at least one given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @return a collection of all subtypes of at least one given type
	 */
	public Collection<Topic> getSubtypes(Topic... types);

	/**
	 * Returns all topic types being a subtype of at least one given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @return a collection of all subtypes of at least one given type
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types);

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * @return a collection of all subtypes of at least one of the given type
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all);
}
