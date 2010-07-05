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
package de.topicmapslab.majortom.model.core.paged;

import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

/**
 * Additional interface of a topic defining some paging methods
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedTopic {

	/**
	 * Returns all types of the topic as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all types of the topic as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Topic> getTypes(int offset, int limit);

	/**
	 * Returns all types of the topic as a sorted list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the topic as a sorted list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Topic> getTypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all types of the topic as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all types of the topic as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Topic> getSupertypes(int offset, int limit);

	/**
	 * Returns all types of the topic as a sorted list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all types of the topic as a sorted list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Topic> getSupertypes(int offset, int limit, Comparator<Topic> comparator);

	/**
	 * Returns all names of the topic as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all names of the topic as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Name> getNames(int offset, int limit);

	/**
	 * Returns all names of the topic as a sorted list within the given range.
	 * 
	 * @param topic
	 *            the topic whose names should be returned
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all names of the topic as a sorted list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Name> getNames(int offset, int limit, Comparator<Name> comparator);

	/**
	 * Returns all occurrences of the topic as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all occurrences of the topic as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Occurrence> getOccurrences(int offset, int limit);

	/**
	 * Returns all occurrences of the the topic as a sorted list within the
	 * given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all occurrences of the the topic as a sorted list within the
	 *         given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Occurrence> getOccurrences(int offset, int limit, Comparator<Occurrence> comparator);

	/**
	 * Returns all associations played by the topic as a list within the given
	 * range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all associations played by the topic as a list within the given
	 *         range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Association> getAssociationsPlayed(int offset, int limit);

	/**
	 * Returns all associations the by given topic as a sorted list within the
	 * given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all associations played by the topic as a sorted list within the
	 *         given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Association> getAssociationsPlayed(int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns all roles played by the topic as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all roles played by the topic as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Role> getRolesPlayed(int offset, int limit);

	/**
	 * Returns all roles played by the topic as a sorted list within the given
	 * range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all roles played by the topic as a sorted list within the given
	 *         range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Role> getRolesPlayed(int offset, int limit, Comparator<Role> comparator);

}
