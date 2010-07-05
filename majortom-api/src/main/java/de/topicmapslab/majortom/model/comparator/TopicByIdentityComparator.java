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
package de.topicmapslab.majortom.model.comparator;

import java.util.Comparator;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

/**
 * Special comparator ordering topics by identity.
 * 
 * @author Sven Krosse
 * 
 */
public class TopicByIdentityComparator implements Comparator<Topic> {

	private final boolean ascending;

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	public TopicByIdentityComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Compare two topic items.
	 * 
	 * <p>
	 * Return <code>0</code> if the reference of the first identities
	 * (subject-identifier, subject-locator or item-identifier) are
	 * lexicographically equal.
	 * </p>
	 * <p>
	 * Return <code>-1</code> if the reference of the first identity
	 * (subject-identifier, subject-locator or item-identifier) of the first
	 * topic is lexicographically higher.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the reference of the first identity
	 * (subject-identifier, subject-locator or item-identifier) of the first
	 * topic is lexicographically smaller.
	 * </p>
	 */
	public int compare(Topic o1, Topic o2) {
		Locator locator1 = null;
		if (!o1.getSubjectIdentifiers().isEmpty()) {
			locator1 = o1.getSubjectIdentifiers().iterator().next();
		} else if (!o1.getSubjectLocators().isEmpty()) {
			locator1 = o1.getSubjectLocators().iterator().next();
		} else if (!o1.getItemIdentifiers().isEmpty()) {
			locator1 = o1.getItemIdentifiers().iterator().next();
		}
		Locator locator2 = null;
		if (!o2.getSubjectIdentifiers().isEmpty()) {
			locator2 = o2.getSubjectIdentifiers().iterator().next();
		} else if (!o2.getSubjectLocators().isEmpty()) {
			locator2 = o2.getSubjectLocators().iterator().next();
		} else if (!o2.getItemIdentifiers().isEmpty()) {
			locator2 = o2.getItemIdentifiers().iterator().next();
		}
		if (locator1 == null && locator2 == null) {
			return 0;
		} else if (locator1 == null && locator2 != null) {
			return ascending ? -1 : 1;
		} else if (locator1 != null && locator2 == null) {
			return ascending ? 1 : -1;
		} else {
			return locator1.getReference().compareTo(locator2.getReference());
		}
	}

}
