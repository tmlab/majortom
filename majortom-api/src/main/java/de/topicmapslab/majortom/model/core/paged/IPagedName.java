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

import org.tmapi.core.Variant;

/**
 * Additional interface of a topic name defining some paging methods
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedName {

	/**
	 * Returns all variants of the name as a list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return all variants of the name as a list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Variant> getVariants(int offset, int limit);

	/**
	 * Returns all variants of the name as a sorted list within the given range.
	 * 
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return all variants of the name as a sorted list within the given range.
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public List<Variant> getVariants(int offset, int limit, Comparator<Variant> comparator);

	/**
	 * Return the number of variants of the name
	 * 
	 * @return the number of variants
	 * @throws UnsupportedOperationException
	 *             thrown if paging is not supported by the underlying store
	 */
	public int getNumberOfVariants();

}
