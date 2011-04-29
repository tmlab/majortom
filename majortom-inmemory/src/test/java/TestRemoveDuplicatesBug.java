import java.io.File;

import junit.framework.TestCase;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.model.core.ITopicMap;

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
public class TestRemoveDuplicatesBug extends TestCase {

	ITopicMap tm;
	
	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		
		tm = (	ITopicMap)TopicMapSystemFactory.newInstance().newTopicMapSystem().createTopicMap("http://example.org");
		
		XTMTopicMapReader reader = new XTMTopicMapReader(tm, new File("src/test/resources/before-remove-duplicate.xtm"));
		reader.read();
		
		
	}
	
	
	public void testname() throws Exception {
		Topic t = tm.getTopicBySubjectIdentifier(tm.createLocator("http://fundivers.biow.uni-leipzig.de/categoric_value/5e3db02e3a7249a0f698a8211ec0a216"));
		assertNotNull(t);
		assertEquals(2, t.getNames().size());
		for ( int i = 0 ; i < 100 ; i ++ ){
		tm.removeDuplicates();
		assertEquals("Error in " + i  +". iteration",2, t.getNames().size());
		}
	}
	
}
