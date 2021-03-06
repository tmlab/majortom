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
package de.topicmapslab.majortom.model.core;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Interface definition to extends the {@link TopicMapSystem} interface.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITopicMapSystem extends TopicMapSystem {

	/**
	 * Create a new topic map instance for the given locator and set the store reference to the given variable.
	 * 
	 * @param locator
	 *            the base locator
	 * @param store
	 *            the topic map store
	 * @return the created topic map
	 * @throws TopicMapExistsException
	 *             thrown if the topic map cannot created
	 */
	public TopicMap createTopicMap(Locator locator, ITopicMapStore store) throws TopicMapExistsException;

	/**
	 * Create a new topic map instance with the base locator created from the given reference and set the store
	 * reference to the given variable.
	 * 
	 * @param ref
	 *            the reference of the base locator
	 * @param store
	 *            the topic map store
	 * @return the created topic map
	 * @throws TopicMapExistsException
	 *             thrown if the topic map cannot created
	 */
	public TopicMap createTopicMap(String ref, ITopicMapStore store) throws TopicMapExistsException;

	/**
	 * Internal method to set a feature, called from topic map store
	 * 
	 * @param key
	 *            the feature keys
	 * @param value
	 *            the feature value
	 * @throws FeatureNotSupportedException
	 *             thrown if feature is unknown for MaJorToM
	 * @throws FeatureNotRecognizedException
	 *             thrown if feature is not supported by MaJorToM
	 */
	public void setFeature(String key, boolean value) throws FeatureNotSupportedException,
			FeatureNotRecognizedException;

	/**
	 * Internal method to set the factory instance
	 * 
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(TopicMapSystemFactory factory);
	
	/**
	 * Returns the topic map store class which can be handled by this topic map system instance
	 * @return the class
	 */
	public Class<? extends ITopicMapStore> getHandledClass();
}
