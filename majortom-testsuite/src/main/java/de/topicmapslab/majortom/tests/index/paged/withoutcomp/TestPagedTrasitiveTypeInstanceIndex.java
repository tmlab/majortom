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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.paging.IPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TestPagedTrasitiveTypeInstanceIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getAssociationTypes(int, int)}
	 * .
	 */
	public void testGetAssociationTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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
				createAssociation(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfAssociationTypes());

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getAssociationTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getAssociationTypes(100, 10);
		assertEquals(1, list.size());

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getAssociations(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetAssociationsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		Association[] associations = new Association[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				associations[j] = createAssociation(type);
				associations[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfAssociations(supertype));

		List<Association> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getAssociations(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getAssociations(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getAssociations(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetAssociationsCollectionOfQextendsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Association[] associations = new Association[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (i % 2 == 0) {
					associations[j] = createAssociation(type);
				} else {
					associations[j] = createAssociation(otherType);
				}
				associations[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
			

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);
		
		assertEquals(101, index.getNumberOfAssociations(types));

		List<Association> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getAssociations(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getAssociations(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getCharacteristicTypes(int, int)}
	 * .
	 */
	public void testGetCharacteristicTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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
				if (j % 2 == 0) {
					createTopic().createName(topics[j], "Name", new Topic[0]);
				} else {
					createTopic().createOccurrence(topics[j], "Occ", new Topic[0]);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfCharacteristicTypes());

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristicTypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getCharacteristics(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetCharacteristicsTopicIntInt() {

		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		ICharacteristics[] characteristics = new ICharacteristics[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IName) createTopic().createName(type, "Value", new Topic[0]);
				} else {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(type, "Value", new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfCharacteristics(supertype));

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristics(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getCharacteristics(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetCharacteristicsCollectionOfQextendsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		ICharacteristics[] characteristics = new ICharacteristics[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IName) createTopic().createName(type, "Value", new Topic[0]);
				} else {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(otherType, "Value", new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);
		
		assertEquals(101, index.getNumberOfCharacteristics(types));

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristics(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getNameTypes(int, int)}
	 * .
	 */
	public void testGetNameTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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

				createTopic().createName(topics[j], "Name", new Topic[0]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfNameTypes());

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getNameTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getNameTypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getNames(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetNamesTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		Name[] names = new Name[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				names[j] = createTopic().createName(type, "Value", new Topic[0]);
				names[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		assertEquals(101, index.getNumberOfNames(supertype));
		
		List<Name> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getNames(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getNames(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getNames(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetNamesCollectionOfQextendsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Name[] names = new Name[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					names[j] = createTopic().createName(type, "Value", new Topic[0]);
				} else {
					names[j] = createTopic().createName(otherType, "Value", new Topic[0]);
				}
				names[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);
		
		assertEquals(101, index.getNumberOfNames(types));

		List<Name> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getNames(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getNames(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getOccurrenceTypes(int, int)}
	 * .
	 */
	public void testGetOccurrenceTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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
				createTopic().createOccurrence(topics[j], "Occ", new Topic[0]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		assertEquals(101, index.getNumberOfOccurrenceTypes());
		
		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getOccurrenceTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getOccurrenceTypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getOccurrences(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetOccurrencesTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		Occurrence[] occurrences = new Occurrence[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = createTopic().createOccurrence(type, "Value", new Topic[0]);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfOccurrences(supertype));

		List<Occurrence> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getOccurrences(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getOccurrences(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getOccurrences(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetOccurrencesCollectionOfQextendsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Occurrence[] occurrences = new Occurrence[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					occurrences[j] = createTopic().createOccurrence(type, "Value", new Topic[0]);
				} else {
					occurrences[j] = createTopic().createOccurrence(otherType, "Value", new Topic[0]);
				}
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);

		assertEquals(101, index.getNumberOfOccurrences(types));
		
		List<Occurrence> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getOccurrences(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getOccurrences(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getRoleTypes(int, int)}
	 * .
	 */
	public void testGetRoleTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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
				createAssociation(createTopic()).createRole(topics[j], createTopic());
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		assertEquals(101, index.getNumberOfRoleTypes());
		
		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getRoleTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getRoleTypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getRoles(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetRolesTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		Role[] roles = new Role[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				roles[j] = createAssociation(createTopic()).createRole(type, createTopic());
				roles[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfRoles(supertype));

		List<Role> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getRoles(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getRoles(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getRoles(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetRolesCollectionOfQextendsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Role[] roles = new Role[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					roles[j] = createAssociation(createTopic()).createRole(type, createTopic());
				} else {
					roles[j] = createAssociation(createTopic()).createRole(otherType, createTopic());
				}
				roles[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);
		
		assertEquals(101, index.getNumberOfRoles(types));

		List<Role> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getRoles(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getRoles(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getTopicTypes(int, int)}
	 * .
	 */
	public void testGetTopicTypesIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
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
				createTopic().addType(topics[j]);
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		
		assertEquals(101, index.getNumberOfTopicTypes());

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getTopicTypes(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopicTypes(100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getTopics(org.tmapi.core.Topic, int, int)}
	 * .
	 */
	public void testGetTopicsTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ITopic type = createTopic();
		Topic supertype = createTopic();
		type.addSupertype(supertype);
		String base = "http://psi.example.org/topics/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topics[j].addType(type);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		assertEquals(101, index.getNumberOfTopics(supertype));
		
		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getTopics(supertype, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopics(supertype, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getTopics(java.util.Collection, int, int)}
	 * .
	 */
	public void testGetTopicsCollectionOfTopicIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				if (j % 2 == 0) {
					topics[j].addType(type);
				} else {
					topics[j].addType(otherType);
				}
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);

		assertEquals(101, index.getNumberOfTopics(types));
		
		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getTopics(types, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopics(types, 100, 10);
		assertEquals(1, list.size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.PagedTypeInstanceIndexImpl.index.paged.InMemoryPagedTypeInstanceIndex#getTopics(java.util.Collection, boolean, int, int)}
	 * .
	 */
	public void testGetTopicsCollectionOfTopicBooleanIntInt() {
		IPagedTransitiveTypeInstanceIndex index = topicMap.getIndex(IPagedTransitiveTypeInstanceIndex.class);
		assertNotNull(index);
		try {
			index.getTopicTypes(0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Topic supertype = createTopic();
		ITopic type = createTopic();
		type.addSupertype(supertype);
		ITopic supertypeB = createTopic();
		ITopic otherType = createTopic();
		otherType.addSupertype(supertypeB);
		String base = "http://psi.example.org/topics/";
		Topic[] topics = new Topic[101];
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				topics[j] = createTopicBySI(base + c + i);
				topics[j].addType(type);
				topics[j].addType(otherType);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Collection<Topic> types = HashUtil.getHashSet();
		types.add(supertype);
		types.add(supertypeB);
		
		assertEquals(101, index.getNumberOfTopics(types, true));

		List<Topic> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getTopics(types, true, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getTopics(types, true, 100, 10);
		assertEquals(1, list.size());
	}
}
