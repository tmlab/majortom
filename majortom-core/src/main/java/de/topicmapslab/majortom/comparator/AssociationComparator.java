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

import java.util.Comparator;

import org.tmapi.core.Association;

/**
 * association comparator
 * 
 * @author Sven Krosse
 * 
 */
public class AssociationComparator implements Comparator<Association> {

	private static AssociationComparator instanceAsc = null;
	private static AssociationComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	private AssociationComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static AssociationComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new AssociationComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new AssociationComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * Compare two association items.
	 * 
	 * <p>
	 * Return <code>0</code> if both associations have the same type and number
	 * of roles.
	 * </p>
	 * <p>
	 * Return <code>-1</code> if the identity of the first type is <i>higher</i>
	 * or the first association has more roles
	 * </p>
	 * <p>
	 * Return <code>1</code> if the identity of the second type is <i>higher</i>
	 * or the second association has more roles
	 * </p>
	 */
	public int compare(Association o1, Association o2) {
		int compare = TopicByIdentityComparator.getInstance(ascending).compare(o1.getType(), o2.getType());
		if (compare == 0) {
			compare = o2.getRoles().size() - o1.getRoles().size();
			compare = ascending ? compare : compare * -1;
		}
		return compare;
	}

}
