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
package de.topicmapslab.majortom.tests.revision;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.revision.IRevisionChange;
import de.topicmapslab.majortom.revision.core.ReadOnlyTopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class TestTopicRevisions extends MaJorToMTestCase {

	public void testTypes() throws Exception {
		if (topicMap.getTopicMapSystem().getFeature(FeatureStrings.SUPPORT_HISTORY)) {
			IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
			index.open();

			/*
			 * create three topics
			 */
			ITopic topic = createTopic();
			ITopic type = createTopic();
			ITopic otherType = createTopic();
			/*
			 * add two types
			 */
			topic.addType(type);
			topic.addType(otherType);

			/*
			 * check number of types
			 */
			assertEquals("Number of type should be two", 2, topic.getTypes().size());

			/*
			 * remove topic
			 */
			topic.remove();

			/*
			 * get last revision and change
			 */
			IRevision r = index.getLastRevision();
			IRevisionChange change = null;
			if (topicMap.getTopicMapSystem().getFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
				assertEquals(7, r.getChangeset().size());
				change = r.getChangeset().get(6);
			} else {
				assertEquals(1, r.getChangeset().size());
				change = r.getChangeset().get(0);
			}
			/*
			 * check number of type of read only topic
			 */
			assertEquals(topic, change.getOldValue());
			assertTrue(change.getOldValue() instanceof ReadOnlyTopic);
			assertEquals(2, ((ITopic) change.getOldValue()).getTypes().size());

			/*
			 * create 20 topics
			 */
			for (int i = 0; i < 20; i++) {
				ITopic t = createTopic();
				/*
				 * add n types
				 */
				for (long j = 0; j < i; j++) {
					t.addType(createTopic());
				}
				assertEquals("Number of types should be " + i, i, t.getTypes().size());
				t.remove();
				r = index.getLastRevision();
				if ( topicMap.getTopicMapSystem().getFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
					assertEquals(1+i*3, r.getChangeset().size());
					change = r.getChangeset().get(i*3);
				} else {
					assertEquals(1, r.getChangeset().size());
					change = r.getChangeset().get(0);
				}
				/*
				 * check number of type of read only topic
				 */
				assertEquals(t, change.getOldValue());
				assertTrue(change.getOldValue() instanceof ReadOnlyTopic);
				assertEquals("Number of types should be " + i,i, ((ITopic) change.getOldValue()).getTypes().size());
			}
			
			/*
			 * create 20 topics
			 */
			for (int i = 0; i < 20; i++) {
				ITopic t = createTopic();
				ITopic types[] = new ITopic[i];
				/*
				 * add n types
				 */
				for (int j = 0; j < types.length; j++) {
					types[j] = createTopic();
					t.addType(types[j]);
				}
				assertEquals("Number of types should be " + i, i, t.getTypes().size());
				/*
				 * remove all topics and all types
				 */
				t.remove();
				r = index.getLastRevision();
				for ( int j = 0 ; j < types.length ; j++ ){
					types[j].remove();
				}				
				if ( topicMap.getTopicMapSystem().getFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
					assertEquals(1+i*3, r.getChangeset().size());
					change = r.getChangeset().get(i*3);
				} else {
					assertEquals(1, r.getChangeset().size());
					change = r.getChangeset().get(0);
				}
				/*
				 * check number of type of read only topic
				 */
				assertEquals(t, change.getOldValue());
				assertTrue(change.getOldValue() instanceof ReadOnlyTopic);
				assertEquals("Number of types should be " + i,i, ((ITopic) change.getOldValue()).getTypes().size());
			}
		}
	}

}
