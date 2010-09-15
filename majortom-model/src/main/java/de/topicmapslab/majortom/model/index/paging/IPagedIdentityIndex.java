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
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.IIndex;

/**
 * Special {@link IIdentityIndex} supporting paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedIdentityIndex extends IIndex {

	/**
	 * Returns all known item-identifiers of the current topic map within the
	 * given range.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all known item-identifiers within the given range
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit);

	/**
	 * Returns all known item-identifiers of the current topic map within the
	 * given range.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all known item-identifiers within the given range
	 */
	public List<Locator> getItemIdentifiers(int offset, int limit,
			Comparator<Locator> comparator);

	/**
	 * Returns all known subject-identifiers of the current topic map
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all known subject-identifiers within the given range
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit);

	/**
	 * Returns all known subject-identifiers of the current topic map
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all known subject-identifiers within the given range
	 */
	public List<Locator> getSubjectIdentifiers(int offset, int limit,
			Comparator<Locator> comparator);

	/**
	 * Returns all known subject-locators of the current topic map
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all known subject-locators within the given range
	 */
	public List<Locator> getSubjectLocators(int offset, int limit);

	/**
	 * Returns all known subject-locators of the current topic map
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all known subject-locators within the given range
	 */
	public List<Locator> getSubjectLocators(int offset, int limit,
			Comparator<Locator> comparator);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByItemIdentifier(final String regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByItemIdentifier(final String regExp,
			int offset, int limit, Comparator<Construct> comparator);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByItemIdentifier(final Pattern regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByItemIdentifier(final Pattern regExp,
			int offset, int limit, Comparator<Construct> comparator);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectIdentifier(final String regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectIdentifier(final String regExp,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectIdentifier(final Pattern regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectIdentifier(final Pattern regExp,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * The method try to identify all topic using a subject-Topic matching the
	 * given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectLocator(final String regExp,
			int offset, int limit);

	/**
	 * The method try to identify all topic using a subject-Topic matching the
	 * given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectLocator(final String regExp,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * The method try to identify all topic using a subject-Topic matching the
	 * given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectLocator(final Pattern regExp,
			int offset, int limit);

	/**
	 * The method try to identify all topic using a subject-Topic matching the
	 * given regular expression within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified topics within the given range but never
	 *         <code>null</code>
	 */
	public List<Topic> getTopicsBySubjectLocator(final Pattern regExp,
			int offset, int limit, Comparator<Topic> comparator);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-Topic matching the given regular expression
	 * within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByIdentifier(final String regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-Topic matching the given regular expression
	 * within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByIdentifier(final String regExp,
			int offset, int limit, Comparator<Construct> comparator);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-Topic matching the given regular expression
	 * within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all identified constructs within the given range but
	 *         never <code>null</code>
	 */
	public List<Construct> getConstructsByIdentifier(final Pattern regExp,
			int offset, int limit);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-Topic matching the given regular expression
	 * within the given range.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all identified constructs within the given range
	 *         but never <code>null</code>
	 */
	public List<Construct> getConstructsByIdentifier(final Pattern regExp,
			int offset, int limit, Comparator<Construct> comparator);

}
