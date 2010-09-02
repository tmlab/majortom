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
package de.topicmapslab.majortom.comparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.util.HashUtil;

/**
 * Special comparator ordering constructs by their item-identifier
 * 
 * @author Sven Krosse
 * 
 */
public class ConstructByItemIdentifierComparator<T extends Construct> implements Comparator<T> {


	private final boolean ascending;

	/**
	 * constructor
	 * 
	 * Sorting order is ascending.
	 */
	public ConstructByItemIdentifierComparator() {
		this(true);
	}

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	public ConstructByItemIdentifierComparator(final boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * Compare two constructs of the specified type.
	 * <p>
	 * Return <code>0</code> if both constructs has no item-identifiers.
	 * </p>
	 * <p>
	 * Return <code>-1</code> if only the first construct has no
	 * item-identifiers or the reference of the first item-identifier is
	 * lexicographically smaller.
	 * </p>
	 * <p>
	 * Return <code>-1</code> if only the second construct has no
	 * item-identifiers or the reference of the second item-identifier is
	 * lexicographically higher.
	 * </p>
	 */
	public int compare(T o1, T o2) {
		List<Locator> ii1 = HashUtil.getList(o1.getItemIdentifiers());
		Collections.sort(ii1, LocatorByReferenceComparator.getInstance(ascending));
		List<Locator> ii2 = HashUtil.getList(o2.getItemIdentifiers());
		Collections.sort(ii2, LocatorByReferenceComparator.getInstance(ascending));
		if (ii1.isEmpty() && ii2.isEmpty()) {
			return 0;
		} else if (ii1.isEmpty()) {
			return ascending ? -1 : 1;
		} else if (ii2.isEmpty()) {
			return ascending ? 1 : -1;
		}
		return LocatorByReferenceComparator.getInstance(ascending).compare(ii1.get(0), ii2.get(0));
	}
}
