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
package de.topicmapslab.majortom.tests.io;

import java.io.File;
import java.util.Set;

import junit.framework.TestCase;

import org.tmapi.core.Construct;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;
import org.tmapix.io.LTMTopicMapReader;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class TestImport extends TestCase implements ITopicMapListener {

	private Set<String> subjectIdentifiers, subjectLocators;

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		super.setUp();
		// Scanner scanner = new Scanner(new
		// File("src/test/resources/subject-identifiers.txt"));
		// subjectIdentifiers = new HashSet<String>();
		// while (scanner.hasNextLine()) {
		// subjectIdentifiers.add(scanner.nextLine().trim());
		// }
		// scanner.close();
		// scanner = new Scanner(new
		// File("src/test/resources/subject-locators.txt"));
		// subjectLocators = new HashSet<String>();
		// while (scanner.hasNextLine()) {
		// subjectLocators.add(scanner.nextLine().trim());
		// }
		// scanner.close();
	}

	public void testLtmImport() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setFeature(FeatureStrings.AUTOMATIC_MERGING, true);
		ITopicMap map = (ITopicMap) factory.newTopicMapSystem().createTopicMap("http://psi.example.org");
		// map.addTopicMapListener(this);
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
		assertNotNull(map);

		try {
			LTMTopicMapReader reader = new LTMTopicMapReader(map, new File("src/test/resources/Opera Topic Map.ltm"));
			reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot read topic map");
		}

		IRevisionIndex index = map.getIndex(IRevisionIndex.class);
		index.open();
		assertNull(index.getFirstRevision());

		// checkImport(map);
	}

	public void testXtmImport() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setFeature(FeatureStrings.AUTOMATIC_MERGING, true);
		TopicMap map = factory.newTopicMapSystem().createTopicMap("http://psi.example.org");
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
		assertNotNull(map);

		try {
			XTMTopicMapReader reader = new XTMTopicMapReader(map, new File("src/test/resources/toyTM_xtm10.xtm"));
			reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot read topic map");
		}

		// checkImport(map);
	}

	public void testXtm2Import() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setFeature(FeatureStrings.AUTOMATIC_MERGING, true);
		TopicMap map = factory.newTopicMapSystem().createTopicMap("http://psi.example.org");
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
		assertNotNull(map);

		try {
			XTMTopicMapReader reader = new XTMTopicMapReader(map, new File("src/test/resources/Opera Topic Map.xtm2"));
			reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot read topic map");
		}

		// checkImport(map);
	}

	public void testCtmImport() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setFeature(FeatureStrings.AUTOMATIC_MERGING, true);
		TopicMap map = factory.newTopicMapSystem().createTopicMap("http://psi.example.org");
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, false);
		assertNotNull(map);

		try {
			CTMTopicMapReader reader = new CTMTopicMapReader(map, new File("src/test/resources/Opera Topic Map.ctm"));
			reader.read();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Cannot read topic map");
		}

		// checkImport(map);
	}

	public void checkImport(TopicMap topicMap) {

		System.out.println("Check import");

		for (String subjectIdentifier : subjectIdentifiers) {
			assertNotNull("The topic with the subject-identifier '" + subjectIdentifier + "' is missing!", topicMap.getTopicBySubjectIdentifier(topicMap
					.createLocator(subjectIdentifier)));
		}

		for (String subjectLocator : subjectLocators) {
			assertNotNull("The topic with the subject-locator '" + subjectLocator + "' is missing!", topicMap.getTopicBySubjectLocator(topicMap
					.createLocator(subjectLocator)));
		}
	}

	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		StringBuilder builder = new StringBuilder();
		builder.append("Id:\t" + id + "\r\n");
		builder.append("Type:\t" + event.name() + "\r\n");
		builder.append("Context:\t" + (notifier == null ? "null" : notifier.toString()) + "\r\n");
		builder.append("New value:\t" + (newValue == null ? "null" : newValue.toString()) + "\r\n");
		builder.append("Old value:\t" + (oldValue == null ? "null" : oldValue.toString()) + "\r\n");
		System.out.println(builder.toString());
	}
}
