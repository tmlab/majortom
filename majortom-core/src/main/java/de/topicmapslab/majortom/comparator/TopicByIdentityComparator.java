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
	 * Return <code>-1</code> if the reference of the first subject-identifier
	 * of the first topic is lexicographically higher or the second topic has no subject-identifier.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first subject-identifier of the first
	 * topic is lexicographically smaller or the first topic has no subject-identifier.
	 * </p>
	 * 
	 * <p>
	 * <b>If the at least one of the topic has a subject-locator:</b><br /> 
	 * Return <code>-1</code> if the reference of the first subject-locator
	 * of the first topic is lexicographically higher or the second topic has no subject-locator.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first subject-locator of the first
	 * topic is lexicographically smaller or the first topic has no subject-locator.
	 * </p>
	 * 
	 * <p>
	 * <b>If the at least one of the topic has a item-identifier:</b><br /> 
	 * Return <code>-1</code> if the reference of the first item-identifier
	 * of the first topic is lexicographically higher or the second topic has no item-identifier.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first item-identifier of the first
	 * topic is lexicographically smaller or the first topic has no item-identifier.
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
		List<Locator> si1 = HashUtil.getList(o1.getSubjectIdentifiers());
		List<Locator> si2 = HashUtil.getList(o2.getSubjectIdentifiers());
		int siDiff = si2.size() - si1.size();
				
		/*
		 * first topic has subject-identifier and second not
		 */
		if ( siDiff == -1 ){
			return ascending ? -1 :1;
		}
		/*
		 * second topic has subject-identifier and first not
		 */
		else if ( siDiff == 1 ){
			return ascending ? 1 :-1;
		}
		/*
		 * both topics has subject-identifiers
		 */
		else if ( siDiff != 0 && !si1.isEmpty()){
			Collections.sort(si1, LocatorByReferenceComparator.getInstance(ascending));
			Collections.sort(si2, LocatorByReferenceComparator.getInstance(ascending));
			return LocatorByReferenceComparator.getInstance(ascending).compare(si1.get(0), si2.get(0));
		}
		
		/*
		 * compare by subject-locator
		 */
		List<Locator> sl1 = HashUtil.getList(o1.getSubjectLocators());
		List<Locator> sl2 = HashUtil.getList(o2.getSubjectLocators());
		int slDiff = sl2.size() - sl1.size();
		
		/*
		 * first topic has subject-locator and second not
		 */
		if ( slDiff == -1 ){
			return ascending ? -1 :1;
		}
		/*
		 * second topic has subject-locator and first not
		 */
		else if ( slDiff == 1 ){
			return ascending ? 1 :-1;
		}
		/*
		 * both topics has subject-locators
		 */
		else if ( slDiff != 0 && !sl1.isEmpty()){
			Collections.sort(sl1, LocatorByReferenceComparator.getInstance(ascending));
			Collections.sort(sl2, LocatorByReferenceComparator.getInstance(ascending));
			return LocatorByReferenceComparator.getInstance(ascending).compare(sl1.get(0), sl2.get(0));
		}
		
		/*
		 * compare by item-identifier
		 */
		List<Locator> ii1 = HashUtil.getList(o1.getItemIdentifiers());
		List<Locator> ii2 = HashUtil.getList(o2.getItemIdentifiers());
		int iiDiff = ii2.size() - ii1.size();
		
		/*
		 * first topic has item-identifier and second not
		 */
		if ( iiDiff == -1 ){
			return ascending ? -1 :1;
		}
		/*
		 * second topic has item-identifier and first not
		 */
		else if ( iiDiff == 1 ){
			return ascending ? 1 :-1;
		}
		/*
		 * both topics has item-identifiers
		 */
		else if ( iiDiff != 0 && !ii1.isEmpty()){
			Collections.sort(ii1, LocatorByReferenceComparator.getInstance(ascending));
			Collections.sort(ii2, LocatorByReferenceComparator.getInstance(ascending));
			return LocatorByReferenceComparator.getInstance(ascending).compare(ii1.get(0), ii2.get(0));
		}
		
		/*
		 * both topics have no subject-identifier, locator or item-identifier
		 */
		return 0;
	}

}
