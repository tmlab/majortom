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
package de.topicmapslab.majortom.redis.store;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.core.TopicMapSystemImpl;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class RedisTopicMapSystem extends TopicMapSystemImpl {

	private long numberOfTopicMaps = 0;
	
	/**
	 * constructor for JAVA services
	 */
	public RedisTopicMapSystem() {
	}

	/**
	 * constructor
	 * 
	 * @param factory
	 *            the factory
	 */
	public RedisTopicMapSystem(TopicMapSystemFactoryImpl factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<? extends ITopicMapStore> getHandledClass() {
		return RedisTopicMapStore.class;
	}
	
	
	@Override
	public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
		if ( numberOfTopicMaps > 0 ){
			throw new TopicMapStoreException("The redis topic map system only supports one topic map instance!");
		}
		numberOfTopicMaps++;
		return super.createTopicMap(locator);
	}
	
	@Override
	public TopicMap createTopicMap(Locator locator, ITopicMapStore store) throws TopicMapExistsException {
		if ( numberOfTopicMaps > 0 ){
			throw new TopicMapStoreException("The redis topic map system only supports one topic map instance!");
		}
		numberOfTopicMaps++;
		return super.createTopicMap(locator, store);
	}
	
	@Override
	public ITopicMap removeTopicMap(Locator locator) {
		numberOfTopicMaps--;
		return super.removeTopicMap(locator);
	}
	
	@Override
	public void setFactory(TopicMapSystemFactory factory) {
		super.setFactory(factory);
		try {
			setFeature(FeatureStrings.SUPPORT_HISTORY, false);
			setFeature(FeatureStrings.SUPPORT_TRANSACTION, false);
			setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
			setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		} catch (FeatureNotRecognizedException e) {
			e.printStackTrace();
		}
	}
}
