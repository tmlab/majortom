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
import org.tmapi.core.Topic;

/**
 * Additional interface of a topic map defining some paging methods
 * 
 * @author Sven Krosse
 * 
 * @since 1.2.0
 */
public interface IPagedTopicMap {

	/**
	 * Returns all topics of the topic map within the given range
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return a list of the selected topics but never <code>null</code>
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
	 */
	public List<Association> getAssociations(int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns the number of associations.
	 * 
	 * @return the number of associations
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations();

}
