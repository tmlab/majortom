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
package de.topicmapslab.majortom.tests.index.paged.withoutcomp;

import java.util.List;
import java.util.regex.Pattern;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.index.paging.IPagedIdentityIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestPagedIdentityIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getConstructsByIdentifier(java.util.regex.Pattern, int, int)}
	 * .
	 */
	public void testGetConstructsByIdentifierPatternIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Construct[] constructs = new Construct[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 4 == 0) {
					if (j < 50) {
						constructs[j] = createTopicBySI(base + c + i);
					} else {
						constructs[j] = createTopicByII(base + c + i);
					}
				} else if (j % 4 == 1) {
					constructs[j] = createAssociation(createTopic());
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				} else if (j % 4 == 2) {
					constructs[j] = createTopic().createName("Name", new Topic[0]);
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				} else if (j % 4 == 3) {
					constructs[j] = createAssociation(createTopic()).createRole(createTopic(), createTopic());
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Construct> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getConstructsByIdentifier(base + ".*", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getConstructsByIdentifier(base + ".*", 100, 10);
		assertEquals(1, list.size());
		for (int i = 0; i < 10; i++) {
			list = index.getConstructsByIdentifier(Pattern.compile(base + ".*"), i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getConstructsByIdentifier(Pattern.compile(base + ".*"), 100, 10);
		assertEquals(1, list.size());

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getConstructsByItemIdentifier(java.util.regex.Pattern, int, int)}
	 * .
	 */
	public void testGetConstructsByItemIdentifierPatternIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Construct[] constructs = new Construct[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 4 == 0) {
					constructs[j] = createTopicByII(base + c + i);
				} else if (j % 4 == 1) {
					constructs[j] = createAssociation(createTopic());
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				} else if (j % 4 == 2) {
					constructs[j] = createTopic().createName("Name", new Topic[0]);
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				} else if (j % 4 == 3) {
					constructs[j] = createAssociation(createTopic()).createRole(createTopic(), createTopic());
					constructs[j].addItemIdentifier(createLocator(base + c + i));
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Construct> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getConstructsByItemIdentifier(base + ".*", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getConstructsByItemIdentifier(base + ".*", 100, 10);
		assertEquals(1, list.size());
		for (int i = 0; i < 10; i++) {
			list = index.getConstructsByItemIdentifier(Pattern.compile(base + ".*"), i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getConstructsByItemIdentifier(Pattern.compile(base + ".*"), 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getItemIdentifiers(int, int)}
	 * .
	 */
	public void testGetItemIdentifiersIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Locator[] locators = new Locator[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				locators[j] = createLocator(base + c + i);
				if (j % 4 == 0) {
					topicMap.createTopicByItemIdentifier(locators[j]);
				} else if (j % 4 == 1) {
					Name n = createTopicBySI(base + j).createName("name", new Topic[0]);
					n.addItemIdentifier(locators[j]);
				} else if (j % 4 == 2) {
					Association a = createAssociation(createTopicBySI(base + j));
					a.addItemIdentifier(locators[j]);
				} else if (j % 4 == 3) {
					Topic t = createTopicBySI(base + j);
					Role r = createAssociation(t).createRole(t, t);
					r.addItemIdentifier(locators[j]);
				}

				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Locator> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getItemIdentifiers(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getItemIdentifiers(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getSubjectIdentifiers(int, int)}
	 * .
	 */
	public void testGetSubjectIdentifiersIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Locator[] locators = new Locator[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				locators[j] = createLocator(base + c + i);
				topicMap.createTopicBySubjectIdentifier(locators[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Locator> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getSubjectIdentifiers(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubjectIdentifiers(100, 10);
		assertEquals(1, list.size());

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getSubjectLocators(int, int)}
	 * .
	 */
	public void testGetSubjectLocatorsIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Locator[] locators = new Locator[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				locators[j] = createLocator(base + c + i);
				topicMap.createTopicBySubjectLocator(locators[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Locator> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getSubjectLocators(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubjectLocators(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getTopicsBySubjectIdentifier(java.util.regex.Pattern, int, int)}
	 * .
	 */
	public void testGetTopicsBySubjectIdentifierPatternIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getTopicsBySubjectIdentifier(base + ".*", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopicsBySubjectIdentifier(base + ".*", 100, 10);
		assertEquals(1, list.size());
		for (int i = 0; i < 10; i++) {
			list = index.getTopicsBySubjectIdentifier(Pattern.compile(base + ".*"), i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopicsBySubjectIdentifier(Pattern.compile(base + ".*"), 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedIdentityIndexImpl.index.paged.InMemoryPagedIdentityIndex#getTopicsBySubjectLocator(java.util.regex.Pattern, int, int)}
	 * .
	 */
	public void testGetTopicsBySubjectLocatorPatternIntInt() {
		IPagedIdentityIndex index = topicMap.getIndex(IPagedIdentityIndex.class);
		assertNotNull(index);
		try {
			index.getItemIdentifiers(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		String base = "http://psi.example.org/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySL(base + c + i);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		/*
		 * with string
		 */
		for (int i = 0; i < 10; i++) {
			list = index.getTopicsBySubjectLocator(base + ".*", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopicsBySubjectLocator(base + ".*", 100, 10);
		assertEquals(1, list.size());
		for (int i = 0; i < 10; i++) {
			list = index.getTopicsBySubjectLocator(Pattern.compile(base + ".*"), i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopicsBySubjectLocator(Pattern.compile(base + ".*"), 100, 10);
		assertEquals(1, list.size());
	}

}
