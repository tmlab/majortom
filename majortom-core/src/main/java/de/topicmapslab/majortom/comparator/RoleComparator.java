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

import org.tmapi.core.Role;

/**
 * association role comparator
 * 
 * @author Sven Krosse
 * 
 */
public class RoleComparator implements Comparator<Role> {

	private static RoleComparator instanceAsc = null;
	private static RoleComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static RoleComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new RoleComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new RoleComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	private RoleComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Compare two association items.
	 * 
	 * <p>
	 * Return <code>0</code> if both associations roles have the same type and
	 * player (lexicographical comparison of identities).
	 * </p>
	 * <p>
	 * Return <code>-1</code> if the type or player of the first role is higher
	 * (lexicographical comparison of identities).
	 * </p>
	 * <p>
	 * Return <code>1</code> if the type or player of the second role is higher
	 * (lexicographical comparison of identities).
	 * </p>
	 */
	public int compare(Role o1, Role o2) {
		int compare = TopicByIdentityComparator.getInstance(ascending).compare(o1.getType(), o2.getType());
		if (compare == 0) {
			compare = TopicByIdentityComparator.getInstance(ascending).compare(o1.getPlayer(), o2.getPlayer());			
		}
		if ( compare == 0 ){
			compare = AssociationComparator.getInstance(ascending).compare(o1.getParent(), o2.getParent());
		}			
		return compare;
	}

}
