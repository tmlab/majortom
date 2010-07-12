import junit.framework.Assert;
import junit.framework.TestCase;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.core.TopicMapSystemImpl;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;

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

/**
 * @author Sven Krosse
 * 
 */
public class TestStuff extends TestCase {

	private TopicMap topicMap;

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		assertNull(factory.getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS));
		Assert.assertTrue(factory instanceof TopicMapSystemFactoryImpl);
		TopicMapSystem system = factory.newTopicMapSystem();
		Assert.assertTrue(system instanceof TopicMapSystemImpl);
		topicMap = system.createTopicMap("http://engine.topicmapslab.de/myTopicMap/");
		assertNotNull(factory.getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS));
		Assert.assertTrue(topicMap instanceof ITopicMap);
	}

	public void testCreateConstructs() {
	 Locator locator = topicMap.createLocator("http://psi.example.org/test-create-locator");
		// Topic t =
		topicMap.createTopicBySubjectIdentifier(locator);
		topicMap.createTopicBySubjectLocator(locator);
		locator = topicMap.createLocator("http://psi.example.org/test-create-locator/2");
		topicMap.createTopicByItemIdentifier(locator);
		// t.createName("Name", new Topic[0]);
	}

}
