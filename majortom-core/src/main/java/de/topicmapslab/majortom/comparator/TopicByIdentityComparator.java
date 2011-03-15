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
/**
 * 
 */
package de.topicmapslab.majortom.comparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.util.HashUtil;

/**
 * Special comparator ordering topics by identity.
 * 
 * @author Sven Krosse
 * 
 */
public class TopicByIdentityComparator implements Comparator<Topic> {

	private static TopicByIdentityComparator instanceAsc = null;
	private static TopicByIdentityComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static TopicByIdentityComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new TopicByIdentityComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new TopicByIdentityComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	private TopicByIdentityComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Compare two topic items in this order:
	 * <p>
	 * <b>If the at least one of the topic has a subject-identifier:</b><br />
	 * Return <code>-1</code> if the reference of the first subject-identifier of the first topic is lexicographically
	 * higher or the second topic has no subject-identifier.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first subject-identifier of the first topic is lexicographically
	 * smaller or the first topic has no subject-identifier.
	 * </p>
	 * 
	 * <p>
	 * <b>If the at least one of the topic has a subject-locator:</b><br />
	 * Return <code>-1</code> if the reference of the first subject-locator of the first topic is lexicographically
	 * higher or the second topic has no subject-locator.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first subject-locator of the first topic is lexicographically
	 * smaller or the first topic has no subject-locator.
	 * </p>
	 * 
	 * <p>
	 * <b>If the at least one of the topic has a item-identifier:</b><br />
	 * Return <code>-1</code> if the reference of the first item-identifier of the first topic is lexicographically
	 * higher or the second topic has no item-identifier.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first item-identifier of the first topic is lexicographically
	 * smaller or the first topic has no item-identifier.
	 * </p>
	 * 
	 * <p>
	 * Return <code>0</code> if both topics has no subject-identifier, subject-locator or item-identifier.
	 * </p>
	 */
	public int compare(Topic o1, Topic o2) {
		/*
		 * compare by subject-identifier
		 */

		Integer compare = compare(HashUtil.getList(o1.getSubjectIdentifiers()), HashUtil.getList(o2.getSubjectIdentifiers()));
		if ( compare != null ){
			return compare.intValue();
		}
		/*
		 * compare by subject-locator
		 */
		compare = compare(HashUtil.getList(o1.getSubjectLocators()), HashUtil.getList(o2.getSubjectLocators()));
		if ( compare != null ){
			return compare.intValue();
		}
		/*
		 * compare by item-identifier
		 */
		compare = compare(HashUtil.getList(o1.getItemIdentifiers()), HashUtil.getList(o2.getItemIdentifiers()));
		if ( compare != null ){
			return compare.intValue();
		}
		/*
		 * both topics have no subject-identifier, locator or item-identifier
		 */
		return 0;
	}

	/**
	 * compares two lists of locators
	 * 
	 * @param list1
	 *            the first list
	 * @param list2
	 *            the second list return <code>null</code> of both lists are empty otherwise the locators are compared
	 */
	private Integer compare(List<Locator> list1, List<Locator> list2) {
		/*
		 * first topic has subject-identifier and second not
		 */
		if (list2.isEmpty() && !list1.isEmpty()) {
			return ascending ? -1 : 1;
		}
		/*
		 * second topic has subject-identifier and first not
		 */
		else if (!list2.isEmpty() && list1.isEmpty()) {
			return ascending ? 1 : -1;
		}
		/*
		 * both topics has subject-identifiers
		 */
		else if (!list2.isEmpty() && !list1.isEmpty()) {
			Collections.sort(list1, LocatorByReferenceComparator.getInstance(ascending));
			Collections.sort(list2, LocatorByReferenceComparator.getInstance(ascending));
			return LocatorByReferenceComparator.getInstance(ascending).compare(list1.get(0), list2.get(0));
		}

		return null;
	}

}
