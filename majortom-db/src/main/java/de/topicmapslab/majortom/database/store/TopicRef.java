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
package de.topicmapslab.majortom.database.store;


/**
 * Topic Reference container
 * @author Sven Krosse
 * 
 */
public class TopicRef {

	enum Type {
		SUBJECT_IDENTIFIER,

		SUBJECT_LOCATOR,

		ITEM_IDENTIFIER
	};

	private final String locator;
	private final Type type;

	/**
	 * constructor
	 * 
	 * @param locator
	 *            the locator reference
	 * @param type the reference type
	 */
	public TopicRef(String locator, Type type) {
		this.locator = locator;
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		int hash = locator.hashCode();
		hash |= type.hashCode();
		return hash;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if ( this == obj){
			return true;
		}
		if ( obj instanceof TopicRef ){
			return locator.equals(((TopicRef) obj).locator) && type.equals(((TopicRef) obj).type);
		}
		return false;
	}

}
