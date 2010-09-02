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

import org.tmapi.core.Variant;

/**
 * Variant comparator
 * 
 * @author Sven Krosse
 * 
 */
public class VariantByValueComparator implements Comparator<Variant> {

	private static VariantByValueComparator instanceAsc = null;
	private static VariantByValueComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static VariantByValueComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new VariantByValueComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new VariantByValueComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	public VariantByValueComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(Variant o1, Variant o2) {
		int compare = o1.getValue().compareTo(o2.getValue());
		return ascending ? compare : compare * -1;
	}
}
