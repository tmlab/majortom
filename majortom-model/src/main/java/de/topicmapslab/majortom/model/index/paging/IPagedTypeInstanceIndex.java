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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.index.IIndex;

/**
 * Special type-instance index supporting paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedTypeInstanceIndex extends IIndex {

	/**
	 * Returns all topic types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic types within the given range
	 */
	public List<Topic> getTopicTypes(int offset, int limit);

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
	public List<Topic> getTopicTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of all topic types
	 * 
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfTopicTypes();

	/**
	 * Returns all topic instances of the given topic type within the given range.
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
	public List<Topic> getTopics(Topic type, int offset, int limit);

	/**
	 * Returns all topic instances of the given topic type within the given range.
	 * 
	 * @param type
	 *            the type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the topic within the given range
	 */
	public List<Topic> getTopics(Topic type, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of all topic of this type
	 * 
	 * @param type
	 *            the topic type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfTopics(Topic type);

	/**
	 * Returns all instances of at least one of given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of the instances typed by at least one of the given types within the given range
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit);

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
	 * @return a list of the instances typed by at least one of the given types within the given range
	 */
	public List<Topic> getTopics(Collection<Topic> types, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of all topic of at least one of this types
	 * 
	 * @param types
	 *            the topic types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfTopics(Collection<Topic> types);

	/**
	 * Returns all instances of at least one given type or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all instances typed by at least one or every of the given types within the given range
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit);

	/**
	 * Returns all instances of at least one given type or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all instances typed by at least one or every of the given types within the given range
	 */
	public List<Topic> getTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of all topic of at least one of this types or of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found instances should be typed by every given type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfTopics(Collection<Topic> types, boolean all);

	/**
	 * Returns all association types of the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the association types within the given range
	 */
	public List<Topic> getAssociationTypes(int offset, int limit);

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
	public List<Topic> getAssociationTypes(int offset, int limit, Comparator<Topic> comparator);
	
	/**
	 * Returns the number of association types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociationTypes();

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
	public List<Association> getAssociations(Topic type, int offset, int limit);

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
	public List<Association> getAssociations(Topic type, int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns the number of associations typed by the given type.
	 * @param type the type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(Topic type);
	
	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items typed by one of the given types within the given range
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit);

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
	 * @return a list of all association items typed by one of the given types within the given range
	 */
	public List<Association> getAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns the number of associations typed by one of the given types
	 * @param types the types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(Collection<? extends Topic> types);
	
	/**
	 * Return all role types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all role types of the topic map within the given range.
	 */
	public List<Topic> getRoleTypes(int offset, int limit);

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
	public List<Topic> getRoleTypes(int offset, int limit, Comparator<Topic> comparator);
	
	/**
	 * Returns the number of role types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfRoleTypes();

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
	public List<Role> getRoles(Topic type, int offset, int limit);

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
	public List<Role> getRoles(Topic type, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Returns the number of roles typed by the given type
	 * @param type the type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfRoles(Topic type);
	
	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types
	 *            the topic types
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association roles typed by one of the given types within the given range
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit);

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
	 * @return a list of all association roles typed by one of the given types within the given range
	 */
	public List<Role> getRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Returns the number of roles typed by one of the given types
	 * @param types the types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfRoles(Collection<? extends Topic> types);
	
	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all types within the given range
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit);

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all types within the given range
	 */
	public List<Topic> getCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of characteristic types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfCharacteristicTypes();
	
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
	 * @return a list of all characteristics typed by the given type within the given range
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit);

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
	 * @return a list of all characteristics typed by the given type within the given range
	 */
	public List<ICharacteristics> getCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator);

	/**
	 * Returns the number of characteristics typed by the given type
	 * @param type the type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfCharacteristics(Topic type);
	
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
	 * @return a list of all characteristics typed by one of the given types within the given range
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit);

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
	 * @return a list of all characteristics typed by one of the given types within the given range
	 */
	public List<ICharacteristics> getCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator);

	/**
	 * Returns the number of characteristics typed by one of the given types
	 * @param types the types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfCharacteristics(Collection<? extends Topic> types);
	
	/**
	 * Return all name types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all name types within the given range
	 */
	public List<Topic> getNameTypes(int offset, int limit);

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
	public List<Topic> getNameTypes(int offset, int limit, Comparator<Topic> comparator);
	
	/**
	 * Returns the number of name types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNameTypes();

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
	public List<Name> getNames(Topic type, int offset, int limit);

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
	public List<Name> getNames(Topic type, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Returns the number of names typed by the given type
	 * @param type the type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(Topic type);
	
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
	 * @return a list of all names typed by one of the given types within the given range
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit);

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
	 * @return a list of all names typed by one of the given types within the given range
	 */
	public List<Name> getNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Returns the number of names typed by one of the given types
	 * @param types the types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(Collection<? extends Topic> types);
	
	/**
	 * Return all occurrence types of the topic map within the given range.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrence types within the given range
	 */
	public List<Topic> getOccurrenceTypes(int offset, int limit);

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
	public List<Topic> getOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of occurrence types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrenceTypes();
	
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
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit);

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
	public List<Occurrence> getOccurrences(Topic type, int offset, int limit, Comparator<Occurrence> comparator);

	/**
	 * Returns the number of occurrences of the given type.
	 * @param type the type
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(Topic type);
	
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
	 * @return a list of all occurrences typed by one of the given types within the given range
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit);

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
	 * @return a collection of all occurrences typed by one of the given types within the given range
	 */
	public List<Occurrence> getOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator);
	
	/**
	 * Returns the number of occurrences typed by one of the given types.
	 * @param types the types
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(Collection<? extends Topic> types);
}
