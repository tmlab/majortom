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
import org.tmapi.index.Index;

/**
 * Interface definition of an index supporting paging. The index provides paged
 * access to children or values of a specific topic map construct.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedConstructIndex extends Index {

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
	 * Returns all types of the given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the given topic as a sorted list within the given
	 *         range.
	 */
	public List<Topic> getTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator);

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
	public List<Topic> getSupertypes(Topic topic, int offset, int limit);

	/**
	 * Returns all types of the given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose types should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the given topic as a sorted list within the given
	 *         range.
	 */
	public List<Topic> getSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator);

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
	 * Returns all names of the given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all names of the given topic as a sorted list within the given
	 *         range.
	 */
	public List<Name> getNames(Topic topic, int offset, int limit, Comparator<Name> comparator);

	/**
	 * Returns all occurrences of the given topic as a list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all occurrences of the given topic as a list within the given
	 *         range.
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit);

	/**
	 * Returns all occurrences of the given topic as a sorted list within the
	 * given range.
	 * 
	 * @param topic
	 *            the topic whose occurrences should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the given topic as a sorted list within the
	 *         given range.
	 */
	public List<Occurrence> getOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator);

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
	 * Returns all variants of the given name as a sorted list within the given
	 * range.
	 * 
	 * @param name
	 *            the name whose variants should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all variants of the given name as a sorted list within the given
	 *         range.
	 */
	public List<Variant> getVariants(Name name, int offset, int limit, Comparator<Variant> comparator);

	/**
	 * Returns all roles of the given association as a list within the given
	 * range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles of the given association as a list within the given
	 *         range.
	 */
	public List<Role> getRoles(Association association, int offset, int limit);

	/**
	 * Returns all roles of the given association as a sorted list within the
	 * given range.
	 * 
	 * @param association
	 *            the association whose roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles of the given association as a sorted list within the
	 *         given range.
	 */
	public List<Role> getRoles(Association association, int offset, int limit, Comparator<Role> comparator);

	/**
	 * Returns all associations played by given topic as a list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations played by given topic as a list within the given
	 *         range.
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit);

	/**
	 * Returns all associations played by given topic as a sorted list within
	 * the given range.
	 * 
	 * @param topic
	 *            the topic whose played associations should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations played by given topic as a sorted list within
	 *         the given range.
	 */
	public List<Association> getAssociationsPlayed(Topic topic, int offset, int limit, Comparator<Association> comparator);

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
	 * Returns all roles played by given topic as a sorted list within the given
	 * range.
	 * 
	 * @param topic
	 *            the topic whose played roles should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles played by given topic as a sorted list within the given
	 *         range.
	 */
	public List<Role> getRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator);

}
