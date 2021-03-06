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
package de.topicmapslab.majortom.tests.merge;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * 
 * @author Sven Krosse
 * 
 */
public class TestMergeByName extends MaJorToMTestCase {

	public void testname() throws Exception {

		Topic t = topicMap.createTopic();
		t.createName("Name", new Topic[0]);

		Topic t2 = topicMap.createTopic();
		t2.createName("Name", new Topic[0]);
		
		if ( factory.getFeature(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME)){
			assertEquals(2, topicMap.getTopics().size());
		}else{
			assertEquals(3, topicMap.getTopics().size());
		}
	}

}
