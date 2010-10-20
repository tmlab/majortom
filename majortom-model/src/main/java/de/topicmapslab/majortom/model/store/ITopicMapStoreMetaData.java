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

package de.topicmapslab.majortom.model.store;

import java.util.Calendar;

/**
 * Meta data interface of a topic map store
 * @author Sven Krosse
 */
public interface ITopicMapStoreMetaData {

	/**
	 * Returns the creation time of the topic map store
	 * 
	 * @return the creation time
	 */
	public Calendar getCreationTime();
	/**
	 * Returns the time of last modification
	 * 
	 * @return the last modification time
	 */
	public Calendar getLastModificationTime();
	
}
