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
package de.topicmapslab.majortom.database;

import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestCrash extends MaJorToMTestCase {

	public void testDbRestart() throws Exception{
		topicMap.getAssociations();
		
		Thread.sleep(10000);
		
		topicMap.getTopics();
	}
	
	public void testLocators() throws Exception{
		assertTrue(factory.newTopicMapSystem().getLocators().size() >= 1 );		
		TopicMap tm = factory.newTopicMapSystem().getTopicMap(BASE);
		assertEquals(topicMap, tm);
	}
	
}
