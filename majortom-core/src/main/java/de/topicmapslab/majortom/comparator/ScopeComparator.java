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

import de.topicmapslab.majortom.model.core.IScope;

/**
 * scope comparator
 * 
 * @author Sven Krosse
 * 
 * @since 1.1.2
 */
public class ScopeComparator implements Comparator<IScope> {

	private static ScopeComparator instanceAsc = null;
	private static ScopeComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	private ScopeComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static ScopeComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new ScopeComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new ScopeComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * Compare two scope items.
	 * 
	 * <p>
	 * Return <code>0</code> if both scopes has the same number of themes.
	 * </p>
	 * <p>
	 * Return <code>-1</code> if the number of themes of the first scope is
	 * smaller than of the second scope.
	 * </p>
	 * <p>
	 * Return <code>1</code> if the number of themes of the first scope is
	 * higher than of the second scope.
	 * </p>
	 */
	public int compare(IScope o1, IScope o2) {
		int compare = o1.getThemes().size() - o2.getThemes().size();
		return ascending ? compare : compare * -1;
	}

}
