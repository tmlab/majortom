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
package de.topicmapslab.majortom.tests.index.paged.withcomp;

import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.comparator.ConstructByItemIdentifierComparator;
import de.topicmapslab.majortom.comparator.TopicByIdentityComparator;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.paged.IPagedAssociation;
import de.topicmapslab.majortom.model.core.paged.IPagedName;
import de.topicmapslab.majortom.model.core.paged.IPagedTopic;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestPagedConstructIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getAssociationsPlayed(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetAssociationsPlayedTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		Association[] associations = new Association[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				associations[j] = createAssociation(createTopic());
				associations[j].createRole(createTopic(), topic);
				associations[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Association> list = null;
		Comparator<Association> comp = new ConstructByItemIdentifierComparator<Association>(true);

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfAssociationsPlayed(topic));

		for (int i = 0; i < 10; i++) {
			list = index.getAssociationsPlayed(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(associations[i * 10], list.get(0));
			assertEquals(associations[i * 10 + 1], list.get(1));
			assertEquals(associations[i * 10 + 2], list.get(2));
			assertEquals(associations[i * 10 + 3], list.get(3));
			assertEquals(associations[i * 10 + 4], list.get(4));
			assertEquals(associations[i * 10 + 5], list.get(5));
			assertEquals(associations[i * 10 + 6], list.get(6));
			assertEquals(associations[i * 10 + 7], list.get(7));
			assertEquals(associations[i * 10 + 8], list.get(8));
			assertEquals(associations[i * 10 + 9], list.get(9));
		}
		list = index.getAssociationsPlayed(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(associations[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfAssociationsPlayed());
	
		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getAssociationsPlayed(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(associations[i * 10], list.get(0));
			assertEquals(associations[i * 10 + 1], list.get(1));
			assertEquals(associations[i * 10 + 2], list.get(2));
			assertEquals(associations[i * 10 + 3], list.get(3));
			assertEquals(associations[i * 10 + 4], list.get(4));
			assertEquals(associations[i * 10 + 5], list.get(5));
			assertEquals(associations[i * 10 + 6], list.get(6));
			assertEquals(associations[i * 10 + 7], list.get(7));
			assertEquals(associations[i * 10 + 8], list.get(8));
			assertEquals(associations[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getAssociationsPlayed(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(associations[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getNames(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetNamesTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		Name[] names = new Name[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				names[j] = topic.createName("name", new Topic[0]);
				names[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Name> list = null;
		Comparator<Name> comp = new ConstructByItemIdentifierComparator<Name>();

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfNames(topic));
		for (int i = 0; i < 10; i++) {
			list = index.getNames(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(names[i * 10], list.get(0));
			assertEquals(names[i * 10 + 1], list.get(1));
			assertEquals(names[i * 10 + 2], list.get(2));
			assertEquals(names[i * 10 + 3], list.get(3));
			assertEquals(names[i * 10 + 4], list.get(4));
			assertEquals(names[i * 10 + 5], list.get(5));
			assertEquals(names[i * 10 + 6], list.get(6));
			assertEquals(names[i * 10 + 7], list.get(7));
			assertEquals(names[i * 10 + 8], list.get(8));
			assertEquals(names[i * 10 + 9], list.get(9));
		}
		list = index.getNames(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(names[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfNames());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getNames(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(names[i * 10], list.get(0));
			assertEquals(names[i * 10 + 1], list.get(1));
			assertEquals(names[i * 10 + 2], list.get(2));
			assertEquals(names[i * 10 + 3], list.get(3));
			assertEquals(names[i * 10 + 4], list.get(4));
			assertEquals(names[i * 10 + 5], list.get(5));
			assertEquals(names[i * 10 + 6], list.get(6));
			assertEquals(names[i * 10 + 7], list.get(7));
			assertEquals(names[i * 10 + 8], list.get(8));
			assertEquals(names[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getNames(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(names[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getOccurrences(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetOccurrencesTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		Occurrence[] occurrences = new Occurrence[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = topic.createOccurrence(createTopic(),"name", new Topic[0]);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Occurrence> list = null;
		Comparator<Occurrence> comp = new ConstructByItemIdentifierComparator<Occurrence>();

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfOccurrences(topic));

		for (int i = 0; i < 10; i++) {
			list = index.getOccurrences(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getOccurrences(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfOccurrences());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getOccurrences(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getOccurrences(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getRoles(org.tmapi.core.Association, int, int)}
	 * .
	 */
	public void testGetRolesAssociationIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Association association = createAssociation(createTopic());
		String base = "http://psi.example.org/topics/";
		Role[] roles = new Role[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				roles[j] = association.createRole(createTopic(), createTopic());
				roles[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Role> list = null;
		Comparator<Role> comp = new ConstructByItemIdentifierComparator<Role>();

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfRoles(association));

		for (int i = 0; i < 10; i++) {
			list = index.getRoles(association, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(roles[i * 10], list.get(0));
			assertEquals(roles[i * 10 + 1], list.get(1));
			assertEquals(roles[i * 10 + 2], list.get(2));
			assertEquals(roles[i * 10 + 3], list.get(3));
			assertEquals(roles[i * 10 + 4], list.get(4));
			assertEquals(roles[i * 10 + 5], list.get(5));
			assertEquals(roles[i * 10 + 6], list.get(6));
			assertEquals(roles[i * 10 + 7], list.get(7));
			assertEquals(roles[i * 10 + 8], list.get(8));
			assertEquals(roles[i * 10 + 9], list.get(9));
		}
		list = index.getRoles(association, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(roles[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedAssociation) association).getNumberOfRoles());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedAssociation) association).getRoles(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(roles[i * 10], list.get(0));
			assertEquals(roles[i * 10 + 1], list.get(1));
			assertEquals(roles[i * 10 + 2], list.get(2));
			assertEquals(roles[i * 10 + 3], list.get(3));
			assertEquals(roles[i * 10 + 4], list.get(4));
			assertEquals(roles[i * 10 + 5], list.get(5));
			assertEquals(roles[i * 10 + 6], list.get(6));
			assertEquals(roles[i * 10 + 7], list.get(7));
			assertEquals(roles[i * 10 + 8], list.get(8));
			assertEquals(roles[i * 10 + 9], list.get(9));
		}
		list = ((IPagedAssociation) association).getRoles(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(roles[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getRolesPlayed(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetRolesPlayedTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		Role[] roles = new Role[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				roles[j] = createAssociation(createTopic()).createRole(createTopic(), topic);
				roles[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Role> list = null;
		Comparator<Role> comp = new ConstructByItemIdentifierComparator<Role>();

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfRolesPlayed(topic));

		for (int i = 0; i < 10; i++) {
			list = index.getRolesPlayed(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(roles[i * 10], list.get(0));
			assertEquals(roles[i * 10 + 1], list.get(1));
			assertEquals(roles[i * 10 + 2], list.get(2));
			assertEquals(roles[i * 10 + 3], list.get(3));
			assertEquals(roles[i * 10 + 4], list.get(4));
			assertEquals(roles[i * 10 + 5], list.get(5));
			assertEquals(roles[i * 10 + 6], list.get(6));
			assertEquals(roles[i * 10 + 7], list.get(7));
			assertEquals(roles[i * 10 + 8], list.get(8));
			assertEquals(roles[i * 10 + 9], list.get(9));
		}
		list = index.getRolesPlayed(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(roles[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfRolesPlayed());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getRolesPlayed(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(roles[i * 10], list.get(0));
			assertEquals(roles[i * 10 + 1], list.get(1));
			assertEquals(roles[i * 10 + 2], list.get(2));
			assertEquals(roles[i * 10 + 3], list.get(3));
			assertEquals(roles[i * 10 + 4], list.get(4));
			assertEquals(roles[i * 10 + 5], list.get(5));
			assertEquals(roles[i * 10 + 6], list.get(6));
			assertEquals(roles[i * 10 + 7], list.get(7));
			assertEquals(roles[i * 10 + 8], list.get(8));
			assertEquals(roles[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getRolesPlayed(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(roles[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getSupertypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetSupertypesTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		ITopic[] topics = new ITopic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topic.addSupertype(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Topic> list = null;
		Comparator<Topic> comp = TopicByIdentityComparator.getInstance(true);

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfSupertypes(topic));

		for (int i = 0; i < 10; i++) {
			list = index.getSupertypes(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(topics[i * 10], list.get(0));
			assertEquals(topics[i * 10 + 1], list.get(1));
			assertEquals(topics[i * 10 + 2], list.get(2));
			assertEquals(topics[i * 10 + 3], list.get(3));
			assertEquals(topics[i * 10 + 4], list.get(4));
			assertEquals(topics[i * 10 + 5], list.get(5));
			assertEquals(topics[i * 10 + 6], list.get(6));
			assertEquals(topics[i * 10 + 7], list.get(7));
			assertEquals(topics[i * 10 + 8], list.get(8));
			assertEquals(topics[i * 10 + 9], list.get(9));
		}
		list = index.getSupertypes(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(topics[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfSupertypes());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getSupertypes(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(topics[i * 10], list.get(0));
			assertEquals(topics[i * 10 + 1], list.get(1));
			assertEquals(topics[i * 10 + 2], list.get(2));
			assertEquals(topics[i * 10 + 3], list.get(3));
			assertEquals(topics[i * 10 + 4], list.get(4));
			assertEquals(topics[i * 10 + 5], list.get(5));
			assertEquals(topics[i * 10 + 6], list.get(6));
			assertEquals(topics[i * 10 + 7], list.get(7));
			assertEquals(topics[i * 10 + 8], list.get(8));
			assertEquals(topics[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getSupertypes(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(topics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getTypes(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetTypesTopicIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic topic = createTopic();
		String base = "http://psi.example.org/topics/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topic.addType(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Topic> list = null;
		Comparator<Topic> comp = TopicByIdentityComparator.getInstance(true);

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfTypes(topic));

		for (int i = 0; i < 10; i++) {
			list = index.getTypes(topic, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(topics[i * 10], list.get(0));
			assertEquals(topics[i * 10 + 1], list.get(1));
			assertEquals(topics[i * 10 + 2], list.get(2));
			assertEquals(topics[i * 10 + 3], list.get(3));
			assertEquals(topics[i * 10 + 4], list.get(4));
			assertEquals(topics[i * 10 + 5], list.get(5));
			assertEquals(topics[i * 10 + 6], list.get(6));
			assertEquals(topics[i * 10 + 7], list.get(7));
			assertEquals(topics[i * 10 + 8], list.get(8));
			assertEquals(topics[i * 10 + 9], list.get(9));
		}
		list = index.getTypes(topic, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(topics[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedTopic) topic).getNumberOfTypes());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedTopic) topic).getTypes(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(topics[i * 10], list.get(0));
			assertEquals(topics[i * 10 + 1], list.get(1));
			assertEquals(topics[i * 10 + 2], list.get(2));
			assertEquals(topics[i * 10 + 3], list.get(3));
			assertEquals(topics[i * 10 + 4], list.get(4));
			assertEquals(topics[i * 10 + 5], list.get(5));
			assertEquals(topics[i * 10 + 6], list.get(6));
			assertEquals(topics[i * 10 + 7], list.get(7));
			assertEquals(topics[i * 10 + 8], list.get(8));
			assertEquals(topics[i * 10 + 9], list.get(9));
		}
		list = ((IPagedTopic) topic).getTypes(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(topics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex#getVariants(org.tmapi.core.Name, int, int)}
	 * .
	 */
	public void testGetVariantsNameIntInt() {
		IPagedConstructIndex index = topicMap.getIndex(IPagedConstructIndex.class);
		assertNotNull(index);
		try {
			index.getNumberOfNames(null);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Name name = createTopic().createName("Name", new Topic[0]);
		String base = "http://psi.example.org/topics/";
		Variant[] variants = new Variant[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				variants[j] = name.createVariant("Value", createTopic());
				variants[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<Variant> list = null;
		Comparator<Variant> comp = new ConstructByItemIdentifierComparator<Variant>();

		/*
		 * using index methods
		 */
		assertEquals(101, index.getNumberOfVariants(name));

		for (int i = 0; i < 10; i++) {
			list = index.getVariants(name, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(variants[i * 10], list.get(0));
			assertEquals(variants[i * 10 + 1], list.get(1));
			assertEquals(variants[i * 10 + 2], list.get(2));
			assertEquals(variants[i * 10 + 3], list.get(3));
			assertEquals(variants[i * 10 + 4], list.get(4));
			assertEquals(variants[i * 10 + 5], list.get(5));
			assertEquals(variants[i * 10 + 6], list.get(6));
			assertEquals(variants[i * 10 + 7], list.get(7));
			assertEquals(variants[i * 10 + 8], list.get(8));
			assertEquals(variants[i * 10 + 9], list.get(9));
		}
		list = index.getVariants(name, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(variants[100], list.get(0));

		/*
		 * using construct methods
		 */
		assertEquals(101, ((IPagedName) name).getNumberOfVariants());

		for (int i = 0; i < 10; i++) {
			list = ((IPagedName) name).getVariants(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(variants[i * 10], list.get(0));
			assertEquals(variants[i * 10 + 1], list.get(1));
			assertEquals(variants[i * 10 + 2], list.get(2));
			assertEquals(variants[i * 10 + 3], list.get(3));
			assertEquals(variants[i * 10 + 4], list.get(4));
			assertEquals(variants[i * 10 + 5], list.get(5));
			assertEquals(variants[i * 10 + 6], list.get(6));
			assertEquals(variants[i * 10 + 7], list.get(7));
			assertEquals(variants[i * 10 + 8], list.get(8));
			assertEquals(variants[i * 10 + 9], list.get(9));
		}
		list = ((IPagedName) name).getVariants(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(variants[100], list.get(0));
	}

}
