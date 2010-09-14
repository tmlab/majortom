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
package de.topicmapslab.majortom.model.index.paging;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.index.IIndex;

/**
 * Special supertype-subtype index supports paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedSupertypeSubtypeIndex extends IIndex {

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes within the given range
	 */
	public List<Topic> getSupertypes(int offset, int limit);

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes within the given range
	 */
	public List<Topic> getSupertypes(int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit);

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit);

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a supertype of at least one given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all supertypes of at least one given type within the
	 *         given range
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types,
			int offset, int limit);

	/**
	 * Returns all topic types being a supertype of at least one given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all supertypes of at least one given type within the
	 *         given range
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types,
			boolean all, int offset, int limit);

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types,
			boolean all, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes within the given range
	 */
	public List<Topic> getSubtypes(int offset, int limit);

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all subtypes within the given range
	 */
	public List<Topic> getSubtypes(int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit);

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit);

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit,
			Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a subtype of at least one given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes of at least one given type within the
	 *         given range
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types,
			int offset, int limit);

	/**
	 * Returns all topic types being a subtype of at least one given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all subtypes of at least one given type within the
	 *         given range
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes of at least one of the given type within
	 *         the given range
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types,
			boolean all, int offset, int limit);

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all subtypes of at least one of the given type
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types,
			boolean all, int offset, int limit, Comparator<Topic> comparator);

}
