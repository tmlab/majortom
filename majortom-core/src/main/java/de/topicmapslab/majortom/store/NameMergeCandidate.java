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
package de.topicmapslab.majortom.store;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.ITopic;

/**
 * Class represents a duplicate name candidate
 * 
 * @author Sven Krosse
 * 
 */
public class NameMergeCandidate {
	private ITopic topic;
	private IName name;

	/**
	 * @param topic
	 *            the parent topic
	 * @param name
	 *            the duplicate name
	 */
	public NameMergeCandidate(ITopic topic, IName name) {
		this.topic = topic;
		this.name = name;
	}

	/**
	 * Return the duplicated name
	 * 
	 * @return the name
	 */
	public IName getName() {
		return name;
	}

	/**
	 * Return the duplicated topic
	 * 
	 * @return the topic
	 */
	public ITopic getTopic() {
		return topic;
	}

}