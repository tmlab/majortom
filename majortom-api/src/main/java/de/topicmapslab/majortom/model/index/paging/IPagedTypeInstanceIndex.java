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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;

/**
 * Special {@link ITypeInstanceIndex} supporting paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedTypeInstanceIndex extends ITypeInstanceIndex {

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic types within the given range
	 */
	public Collection<Topic> getTopicTypes(int offset, int limit);

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the topic types within the given range
	 */
	public Collection<Topic> getTopicTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all topic instances of the given topic type within the given
	 * range.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the topic within the given range
	 */
	public Collection<Topic> getTopics(Topic type, int offset, int limit);

	/**
	 * Returns all topic instances of the given topic type within the given
	 * range.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic within the given range
	 */
	public Collection<Topic> getTopics(Topic type, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all instances of at least one of given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of the instances typed by at least one of the given
	 *         types within the given range
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, int offset, int limit);

	/**
	 * Returns all instances of at least one of given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of the instances typed by at least one of the given
	 *         types within the given range
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every
	 *            given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all instances typed by at least one or every of
	 *         the given types within the given range
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit);

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every
	 *            given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all instances typed by at least one or every of
	 *         the given types within the given range
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the association types within the given range
	 */
	public Collection<Topic> getAssociationTypes(int offset, int limit);

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the association types within the given range
	 */
	public Collection<Topic> getAssociationTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return all associations of the given type within the given range
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations of the type within the given range
	 */
	public Collection<Association> getAssociations(Topic type, int offset, int limit);

	/**
	 * Return all associations of the given type within the given range
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations of the type within the given range
	 */
	public Collection<Association> getAssociations(Topic type, int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all association items typed by one of the given
	 *         types within the given range
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit);

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all association items typed by one of the given
	 *         types within the given range
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator);

	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all role types of the topic map within the given range.
	 */
	public Collection<Topic> getRoleTypes(int offset, int limit);

	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all role types of the topic map within the given range.
	 */
	public Collection<Topic> getRoleTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return all roles of the given type within the given range.
	 * 
	 * @param type
	 *            the role type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given type within the given range
	 */
	public Collection<Role> getRoles(Topic type, int offset, int limit);

	/**
	 * Return all roles of the given type within the given range.
	 * 
	 * @param type
	 *            the role type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given type within the given range
	 */
	public Collection<Role> getRoles(Topic type, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all association roles typed by one of the given
	 *         types within the given range
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types, int offset, int limit);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all association roles typed by one of the given
	 *         types within the given range
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all types within the given range
	 */
	public Collection<Topic> getCharacteristicTypes(int offset, int limit);

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all types within the given range
	 */
	public Collection<Topic> getCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all characteristics being typed by the given of topic type.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all characteristics typed by the given type
	 *         within the given range
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, int offset, int limit);

	/**
	 * Returns all characteristics being typed by the given of topic type.
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
	 * @return a collection of all characteristics typed by the given type
	 *         within the given range
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator);

	/**
	 * Returns all characteristics typed by one of given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all characteristics typed by one of the given
	 *         types within the given range
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit);

	/**
	 * Returns all characteristics typed by one of given types.
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
	 * @return a collection of all characteristics typed by one of the given
	 *         types within the given range
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator);

	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all name types within the given range
	 */
	public Collection<Topic> getNameTypes(int offset, int limit);

	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all name types within the given range
	 */
	public Collection<Topic> getNameTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return all names of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names of the given type within the given range.
	 */
	public Collection<Name> getNames(Topic type, int offset, int limit);

	/**
	 * Return all names of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names of the given type within the given range.
	 */
	public Collection<Name> getNames(Topic type, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all names typed by one of the given types within
	 *         the given range
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types, int offset, int limit);

	/**
	 * Returns all names typed by one of the given types.
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
	 * @return a collection of all names typed by one of the given types within
	 *         the given range
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrence types within the given range
	 */
	public Collection<Topic> getOccurrenceTypes(int offset, int limit);

	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrence types within the given range
	 */
	public Collection<Topic> getOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return all occurrences of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences of the given type within the given range.
	 */
	public Collection<Occurrence> getOccurrences(Topic arg0, int offset, int limit);

	/**
	 * Return all occurrences of the given type within the given range.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given type within the given range.
	 */
	public Collection<Occurrence> getOccurrences(Topic arg0, int offset, int limit, Comparator<Occurrence> comparator);

	/**
	 * Returns all occurrences typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all occurrences typed by one of the given types
	 *         within the given range
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit);

	/**
	 * Returns all occurrences typed by one of the given types.
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
	 * @return a collection of all occurrences typed by one of the given types
	 *         within the given range
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator);
}
