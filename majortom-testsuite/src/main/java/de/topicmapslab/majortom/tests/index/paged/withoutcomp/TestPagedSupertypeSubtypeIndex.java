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

import java.util.Collection;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TestPagedSupertypeSubtypeIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getDirectSubtypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetDirectSubtypesTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topics[j].addSupertype(type);
				createTopic().addSupertype(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDirectSubtypes(type, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDirectSubtypes(type, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getDirectSupertypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetDirectSupertypesTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				type.addSupertype(topics[j]);
				topics[j].addSupertype(createTopic());
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDirectSupertypes(type, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDirectSupertypes(type, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSubtypes(int, int)}
	 * .
	 */
	public void testGetSubtypesIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topics[j].addSupertype(createTopic());
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSubtypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubtypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSubtypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetSubtypesTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				if (j < 50) {
					topics[j].addSupertype(type);
				} else {
					topics[j].addSupertype(topics[j - 50]);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSubtypes(type, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubtypes(type, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSubtypes(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetSubtypesCollectionOfQextendsTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				if (j < 25) {
					topics[j].addSupertype(type);
				} else if (j < 50) {
					topics[j].addSupertype(topics[j - 25]);
				} else if (j < 75) {
					topics[j].addSupertype(otherType);
				} else {
					topics[j].addSupertype(topics[j - 25]);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(type);
		types.add(otherType);

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSubtypes(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubtypes(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSubtypes(java.util.Collection, boolean, int, int)}
	 * .
	 */
	public void testGetSubtypesCollectionOfQextendsTopicBooleanIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topics[j].addSupertype(type);
				topics[j].addSupertype(otherType);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(type);
		types.add(otherType);

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSubtypes(types, true, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSubtypes(types, true, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSupertypes(int, int)}
	 * .
	 */
	public void testGetSupertypesIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		String base = "http://psi.example.org/topics/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				createTopic().addSupertype(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSupertypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSupertypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSupertypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetSupertypesTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				if (j < 50) {
					type.addSupertype(topics[j]);
				} else {
					topics[j - 50].addSupertype(topics[j]);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSupertypes(type, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSupertypes(type, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSupertypes(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetSupertypesCollectionOfQextendsTopicIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				if (j < 25) {
					type.addSupertype(topics[j]);
				} else if (j < 50) {
					topics[j - 25].addSupertype(topics[j]);
				} else if (j < 75) {
					otherType.addSupertype(topics[j]);
				} else {
					topics[j - 25].addSupertype(topics[j]);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(type);
		types.add(otherType);

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSupertypes(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSupertypes(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedSupertypeSubtypeIndexImpl.index.paged.InMemoryPagedSupertypeSubtypeIndex#getSupertypes(java.util.Collection, boolean, int, int)}
	 * .
	 */
	public void testGetSupertypesCollectionOfQextendsTopicBooleanIntInt() {
		IPagedSupertypeSubtypeIndex index = topicMap.getIndex(IPagedSupertypeSubtypeIndex.class);
		assertNotNull(index);
		try {
			index.getSupertypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				type.addSupertype(topics[j]);
				otherType.addSupertype(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(type);
		types.add(otherType);

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getSupertypes(types, true, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getSupertypes(types, true, 100, 10);
		assertEquals(1, list.size());
	}

}
