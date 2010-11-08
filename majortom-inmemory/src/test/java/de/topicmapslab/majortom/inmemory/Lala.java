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
package de.topicmapslab.majortom.inmemory;

import java.io.File;

import org.tmapi.core.Topic;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class Lala extends MaJorToMTestCase {

	public void testIt() throws Exception {
		XTMTopicMapReader reader = new XTMTopicMapReader(topicMap, new File(
				"c:/cerny2.xtm"));
		reader.read();
		Topic t = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator("http://www.topincs.com/topicmaps/212"));
		Topic theme = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator("http://www.topincs.com/topicmaps/211"));
		System.out.println(((ITopic)t).getBestLabel(theme));
	}

}
