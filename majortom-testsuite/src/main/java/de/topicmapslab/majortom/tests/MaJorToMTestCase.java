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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * {@link TestCase}
 * 
 * @author Sven Krosse
 * 
 */
public class MaJorToMTestCase extends TestCase {

	protected ITopicMap topicMap;
	protected TopicMapSystemFactory factory;

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		factory = TopicMapSystemFactory.newInstance();
		// factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
		// InMemoryTopicMapStore.class.getCanonicalName());
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, true);
		factory.setFeature(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION, false);
		topicMap = (ITopicMap) factory.newTopicMapSystem().createTopicMap("http://psi.majortom.test");
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void tearDown() throws Exception {
		topicMap.remove(true);		
	}

	protected Locator createLocator(final String reference) {
		return topicMap.createLocator(reference);
	}

	protected ITopic createTopic() {
		return (ITopic) topicMap.createTopic();
	}

	protected ITopic createTopicByII(String reference) {
		return (ITopic) topicMap.createTopicByItemIdentifier(createLocator(reference));
	}
	
	protected ITopic createTopicBySI(String reference) {
		return (ITopic) topicMap.createTopicBySubjectIdentifier(createLocator(reference));
	}

	protected ITopic createTopicBySL(String reference) {
		return (ITopic) topicMap.createTopicBySubjectLocator(createLocator(reference));
	}

	protected IAssociation createAssociation(final Topic type) {
		return (IAssociation) topicMap.createAssociation(type, new Topic[0]);
	}

	protected void readTopicMap(String filename) throws IOException {
		InputStream is = getClass().getResourceAsStream("/"+filename);
		if (is==null)
			throw new FileNotFoundException("The file is not in the main/resource folder");
				
		XTMTopicMapReader reader = new XTMTopicMapReader(topicMap, is, "http://majortomtest.de/");	
		if(topicMap.getStore().supportRevisionManagement()){
			topicMap.getStore().enableRevisionManagement(false);
		}
		reader.read();
		if(topicMap.getStore().supportRevisionManagement()){
			topicMap.getStore().enableRevisionManagement(true);
		}
	}
	
}
