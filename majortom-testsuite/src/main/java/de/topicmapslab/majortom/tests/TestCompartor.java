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
package de.topicmapslab.majortom.tests;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.comparator.TopicByIdentityComparator;

/**
 * @author Sven Krosse
 * 
 */
public class TestCompartor extends MaJorToMTestCase {

	public void testNonIdentityTopics() throws Exception {
		Topic t = createTopicBySI("http://de.si");
		t.removeSubjectIdentifier(createLocator("http://de.si"));
		Topic t2 = createTopicBySI("http://de.si2");
		t2.removeSubjectIdentifier(createLocator("http://de.si2"));

		TopicByIdentityComparator comparator = TopicByIdentityComparator.getInstance(true);
		assertEquals(0, comparator.compare(t, t2));
		assertEquals(0, comparator.compare(t2, t));
	}
	
	public void testSISLTopics() throws Exception {
		Topic t = createTopicBySI("http://de.si");		
		Topic t2 = createTopicBySL("http://de.sl");

		TopicByIdentityComparator comparator = TopicByIdentityComparator.getInstance(true);
		assertEquals(-1, comparator.compare(t, t2));
		assertEquals(1, comparator.compare(t2, t));
	}
	
	public void testSubjectIdentifierTopics() throws Exception {
		Topic t = createTopicBySI("http://de.si");		
		Topic t2 = createTopicBySI("http://de.si2");

		TopicByIdentityComparator comparator = TopicByIdentityComparator.getInstance(true);
		assertEquals(-1, comparator.compare(t, t2));
		assertEquals(1, comparator.compare(t2, t));
	}
	
	public void testSubjectLocatorTopics() throws Exception {
		Topic t = createTopicBySL("http://de.sl");		
		Topic t2 = createTopicBySL("http://de.sl2");

		TopicByIdentityComparator comparator = TopicByIdentityComparator.getInstance(true);
		assertEquals(-1, comparator.compare(t, t2));
		assertEquals(1, comparator.compare(t2, t));
	}
	
	public void testItemIdentifierTopics() throws Exception {
		Topic t = createTopicByII("http://de.ii2");		
		Topic t2 = createTopicByII("http://de.ii");

		TopicByIdentityComparator comparator = TopicByIdentityComparator.getInstance(true);
		assertEquals(1, comparator.compare(t, t2));
		assertEquals(-1, comparator.compare(t2, t));
	}

}
