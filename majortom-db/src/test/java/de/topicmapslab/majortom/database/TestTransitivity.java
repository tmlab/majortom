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

import java.io.File;

import org.tmapi.core.Topic;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestTransitivity extends MaJorToMTestCase {

	public void testname() throws Exception {
		CTMTopicMapReader reader = new CTMTopicMapReader(topicMap, new File("src/test/resources/toytm.ctm"));
		reader.read();

		ITopic t = (ITopic)topicMap.getTopicBySubjectIdentifier(topicMap.createLocator("http://en.wikipedia.org/wiki/Country"));
		assertNotNull(t);
		
		ISupertypeSubtypeIndex i = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		i.open();
		assertEquals(4, i.getSubtypes(t).size());
		
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		index.open();
				
		assertEquals(10, index.getTopics(t).size());
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void tearDown() throws Exception {		
	}

}
