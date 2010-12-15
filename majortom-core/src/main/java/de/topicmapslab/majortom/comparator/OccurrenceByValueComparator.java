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

import org.tmapi.core.Occurrence;

/**
 * Occurrence comparator
 * 
 * @author Sven Krosse
 * 
 */
public class OccurrenceByValueComparator implements Comparator<Occurrence> {

	private static OccurrenceByValueComparator instanceAsc = null;
	private static OccurrenceByValueComparator instanceDesc = null;

	private final boolean ascending;

	/**
	 * Returns the singleton instance of the comparator
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 * @return the instance the comparator instance
	 */
	public static OccurrenceByValueComparator getInstance(boolean ascending) {
		if (ascending) {
			if (instanceAsc == null) {
				instanceAsc = new OccurrenceByValueComparator(true);
			}
			return instanceAsc;
		}
		if (instanceDesc == null) {
			instanceDesc = new OccurrenceByValueComparator(false);
		}
		return instanceDesc;
	}

	/**
	 * constructor
	 * 
	 * @param ascending
	 *            sorting order ascending?
	 */
	private OccurrenceByValueComparator(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(Occurrence o1, Occurrence o2) {
		int compare = o1.getValue().compareTo(o2.getValue());
		return ascending ? compare : compare * -1;
	}
}
