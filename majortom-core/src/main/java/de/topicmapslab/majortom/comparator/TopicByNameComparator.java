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

import java.util.Collection;
import java.util.Comparator;

import org.tmapi.core.Name;
import org.tmapi.core.Topic;

/**
 * Topic comparator sort topics by its names of a specific type.
 * 
 * @author Sven Krosse
 * 
 */
public class TopicByNameComparator  implements Comparator<Topic> {

	private final Topic nameType;	

	private final boolean ascending;

	/**
	 * constructor
	 * 
	 * @param nameType
	 *            the name type
	 */
	public TopicByNameComparator(Topic nameType) {
		this(nameType, true);
	}

	/**
	 * constructor
	 * 
	 * @param nameType
	 *            the name type
	 * @param ascending
	 *            sorting order ascending?
	 */
	public TopicByNameComparator(Topic nameType, boolean ascending) {
		this.nameType = nameType;
		this.ascending = ascending;
	}

	/**
	 * If topic and other have no names of the given type <code>0</code> will be
	 * returned. If topic has no names of the given type and other has at least
	 * one, <code>-1(ASC) or 1(DESC)</code> will be returned.If other has no
	 * names of the given type and topic has at least one,
	 * <code>1(ASC) or -1(DESC)</code> will be returned. If both topics have at
	 * least one name of the given type, its values will be compared.
	 */
	public int compare(Topic topic, Topic other) {
		Collection<Name> names1 = topic.getNames(nameType);
		Collection<Name> names2 = other.getNames(nameType);

		if (names1.isEmpty() && names2.isEmpty()) {
			return 0;
		} else if (names1.isEmpty() && !names2.isEmpty()) {
			return ascending ? -1 : 1;
		} else if (!names1.isEmpty() && names2.isEmpty()) {
			return ascending ? 1 : -1;
		} else {
			int compare = names1.iterator().next().getValue().compareTo(names2.iterator().next().getValue());
			return ascending ? compare : compare * -1;
		}
	}
}
