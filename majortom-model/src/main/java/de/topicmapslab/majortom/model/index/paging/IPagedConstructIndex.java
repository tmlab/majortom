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

import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.index.IIndex;

/**
 * Interface definition of an index supporting paging. The index provides paged access to children or values of a
 * specific topic map construct.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedConstructIndex extends IIndex {

	/**
	 * Returns all topics of the topic map within the given range
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return a list of the selected topics but never <code>null</code>
	 * @since 1.2.0
	 */
	public List<Topic> getTopics(int offset, int limit);

	/**
	 * Returns all topics of the topic map within the given range and sorted by the given comparator.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return a list of the selected topics
	 * @since 1.2.0
	 */
	public List<Topic> getTopics(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns the number of topics.
	 * 
	 * @return the number of topics
	 * @since 1.2.0
	 */
	public long getNumberOfTopics();

	/**
	 * Returns all associations of the topic map within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return a list of associations within the given range
	 * @since 1.2.0
	 */
	public List<Association> getAssociations(int offset, int limit);

	/**
	 * Returns all associations of the topic map within the given range sorted by the given comparator.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return a list of associations within the given range
	 * @since 1.2.0
	 */
	public List<Association> getAssociations(int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns the number of associations.
	 * 
	 * @return the number of associations
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations();

	/**
	 * Returns all types of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all types of the given topic as a list within the given range.
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit);

	/**
	 * Returns all types of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the given topic as a sorted list within the given range.
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return the number of types of the topic
	 * 
	 * @param topic
	 *            the topic whose number of types should be returned
	 * @return the number of types
	 */
	public long getNumberOfTypes(Topic topic);

	/**
	 * Returns all supetypes of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all supetypes of the given topic as a list within the given range.
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit);

	/**
	 * Returns all supetypes of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose supetypes should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all supetypes of the given topic as a sorted list within the given range.
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Return the number of supetypes of the topic
	 * 
	 * @param topic
	 *            the topic whose number of supertypes should be returned
	 * @return the number of supertypes
	 */
	public long getNumberOfSupertypes(Topic topic);

	/**
	 * Returns all names of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all names of the given topic as a list within the given range.
	 */
	public List<Name> getNames(Topic topic, int offset, int limit);

	/**
	 * Returns all names of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all names of the given topic as a sorted list within the given range.
	 */
	public List<Name> getNames(Topic topic, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Return the number of names of the topic
	 * 
	 * @param topic
	 *            the topic whose number of names should be returned
	 * @return the number of names
	 */
	public long getNumberOfNames(Topic topic);

	/**
	 * Returns all occurrences of the given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all occurrences of the given topic as a list within the given range.
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit);

	/**
	 * Returns all occurrences of the given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given topic as a sorted list within the given range.
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator);

	/**
	 * Return the number of occurrences of the topic
	 * 
	 * @param topic
	 *            the topic whose number of occurrences should be returned
	 * @return the number of occurrences
	 */
	public long getNumberOfOccurrences(Topic topic);

	/**
	 * Returns all variants of the given name as a list within the given range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all variants of the given name as a list within the given range.
	 */
	public List<Variant> getVariants(Name name, int offset, int limit);

	/**
	 * Returns all variants of the given name as a sorted list within the given range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all variants of the given name as a sorted list within the given range.
	 */
	public List<Variant> getVariants(Name name, int offset, int limit, Comparator<Variant> comparator);

	/**
	 * Return the number of variants of the name
	 * 
	 * @param name
	 *            the name whose number of variants should be returned
	 * @return the number of variants
	 */
	public long getNumberOfVariants(Name name);

	/**
	 * Returns all roles of the given association as a list within the given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles of the given association as a list within the given range.
	 */
	public List<Role> getRoles(Association association, int offset, int limit);

	/**
	 * Returns all roles of the given association as a sorted list within the given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given association as a sorted list within the given range.
	 */
	public List<Role> getRoles(Association association, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Return the number of roles of the association
	 * 
	 * @param association
	 *            the association whose number of roles should be returned
	 * @return the number of roles
	 */
	public long getNumberOfRoles(Association association);

	/**
	 * Returns all associations played by given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations played by given topic as a list within the given range.
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit);

	/**
	 * Returns all associations played by given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations played by given topic as a sorted list within the given range.
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator);

	/**
	 * Return the number of played associations of the topic
	 * 
	 * @param topic
	 *            the topic whose number of played associations should be returned
	 * @return the number of played associations
	 */
	public long getNumberOfAssociationsPlayed(Topic topic);

	/**
	 * Returns all roles played by given topic as a list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles played by given topic as a list within the given range.
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit);

	/**
	 * Returns all roles played by given topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles played by given topic as a sorted list within the given range.
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Return the number of played roles of the topic
	 * 
	 * @param topic
	 *            the topic whose number of played roles should be returned
	 * @return the number of played roles
	 */
	public long getNumberOfRolesPlayed(Topic topic);

}
