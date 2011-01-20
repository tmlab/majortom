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

import java.util.Calendar;

import de.topicmapslab.majortom.model.store.ITopicMapStoreMetaData;

/**
 * @author Sven Krosse
 * 
 */
public class TopicMapStoreMetaDataImpl implements ITopicMapStoreMetaData {

	private final Calendar creationTime;
	private Calendar lastModificationTime;

	/**
	 * constructor
	 */
	public TopicMapStoreMetaDataImpl() {
		this.creationTime = Calendar.getInstance();
		this.lastModificationTime = creationTime;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Calendar getCreationTime() {
		return creationTime;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Calendar getLastModificationTime() {
		return lastModificationTime;
	}

	/**
	 * Sets the time of last modification to current time stamp
	 */
	public void setModificationTime() {
		this.lastModificationTime = Calendar.getInstance();
	}

}
