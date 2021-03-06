package de.topicmapslab.majortom.tests.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.core.TopicMapSystemImpl;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

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
 * @author Sven Krosse
 * 
 */
public class TestTopicMapSystem extends TestCase {

	public void testTopicMapSystemFactory() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		Assert.assertTrue(factory instanceof TopicMapSystemFactoryImpl);
		TopicMapSystem system = factory.newTopicMapSystem();
		Assert.assertTrue(system instanceof TopicMapSystemImpl);
		TopicMap topicMap = system.createTopicMap("http://engine.topicmapslab.de/lalel33u/");
		assertNotNull(factory.getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS));
		Assert.assertTrue(topicMap instanceof ITopicMap);
	}

	public void testConcurrentCollectionFeature() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setFeature(FeatureStrings.CONCURRENT_COLLECTIONS, true);
		TopicMapSystem system = factory.newTopicMapSystem();
		system.createTopicMap("http://engine.topicmapslab.de/lalel33u/");
		assertEquals(CopyOnWriteArraySet.class, HashUtil.getHashSet().getClass());
		assertEquals(ConcurrentHashMap.class, HashUtil.getHashMap().getClass());
	}

	public void testConcurrentCollectionPropertyClass() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setProperty(TopicMapStoreProperty.MAP_IMPLEMENTATION_CLASS, ConcurrentHashMap.class);
		factory.setProperty(TopicMapStoreProperty.SET_IMPLEMENTATION_CLASS, CopyOnWriteArraySet.class);
		TopicMapSystem system = factory.newTopicMapSystem();
		system.createTopicMap("http://engine.topicmapslab.de/lalel33u/");
		assertEquals(CopyOnWriteArraySet.class, HashUtil.getHashSet().getClass());
		assertEquals(ConcurrentHashMap.class, HashUtil.getHashMap().getClass());
	}

	public void testConcurrentCollectionPropertyString() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setProperty(TopicMapStoreProperty.MAP_IMPLEMENTATION_CLASS, ConcurrentHashMap.class.getName());
		factory.setProperty(TopicMapStoreProperty.SET_IMPLEMENTATION_CLASS, CopyOnWriteArraySet.class.getName());
		TopicMapSystem system = factory.newTopicMapSystem();
		system.createTopicMap("http://engine.topicmapslab.de/lalel33u/");
		assertEquals(CopyOnWriteArraySet.class, HashUtil.getHashSet().getClass());
		assertEquals(ConcurrentHashMap.class, HashUtil.getHashMap().getClass());
	}
}
