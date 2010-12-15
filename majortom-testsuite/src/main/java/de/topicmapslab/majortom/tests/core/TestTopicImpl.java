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
package de.topicmapslab.majortom.tests.core;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;

import de.topicmapslab.majortom.core.ScopeableImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;

/**
 * @author Sven Krosse
 * 
 */
public class TestTopicImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed()}.
	 */
	public void testGetAssociationsPlayed() {
		ITopic topic = createTopic();

		assertEquals(0, topic.getAssociationsPlayed().size());

		IAssociation association = createAssociation(createTopic());
		association.createRole(createTopic(), topic);

		assertEquals(1, topic.getAssociationsPlayed().size());
		assertTrue(topic.getAssociationsPlayed().contains(association));

		ITopic type = createTopic();
		IAssociation typedAsso = createAssociation(type);
		typedAsso.createRole(createTopic(), topic);

		assertEquals(2, topic.getAssociationsPlayed().size());
		assertTrue(topic.getAssociationsPlayed().contains(association));
		assertTrue(topic.getAssociationsPlayed().contains(typedAsso));

		assertEquals(1, topic.getAssociationsPlayed(type).size());
		assertTrue(topic.getAssociationsPlayed(type).contains(typedAsso));

		ITopic theme = createTopic();

		IAssociation scopedAsso = (IAssociation) topicMap.createAssociation(
				type, theme);
		scopedAsso.createRole(createTopic(), topic);

		assertEquals(3, topic.getAssociationsPlayed().size());
		assertTrue(topic.getAssociationsPlayed().contains(association));
		assertTrue(topic.getAssociationsPlayed().contains(typedAsso));
		assertTrue(topic.getAssociationsPlayed().contains(scopedAsso));

		assertEquals(2, topic.getAssociationsPlayed(type).size());
		assertTrue(topic.getAssociationsPlayed(type).contains(typedAsso));
		assertTrue(topic.getAssociationsPlayed(type).contains(scopedAsso));

		IScope scopeObject = scopedAsso.getScopeObject();
		assertEquals(1, topic
				.getAssociationsPlayed(scopeObject).size());
		assertTrue(topic.getAssociationsPlayed(scopeObject)
				.contains(scopedAsso));

		assertEquals(1,
				topic.getAssociationsPlayed(type, scopeObject)
						.size());
		assertTrue(topic.getAssociationsPlayed(type,
				scopeObject).contains(scopedAsso));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics()}.
	 */
	public void testGetCharacteristics() {

		ITopic type = createTopic();
		ITopic otherType = createTopic();

		ITopic topic = createTopicBySI("http://psi.example.org/topic");
		Name n = topic.createName(type, "Name", new Topic[0]);
		Occurrence o = topic.createOccurrence(otherType, "Occurrence",
				new Topic[0]);

		assertEquals(1, topic.getCharacteristics(type).size());
		assertTrue(topic.getCharacteristics(type).contains(n));
		assertEquals(1, topic.getCharacteristics(otherType).size());
		assertTrue(topic.getCharacteristics(otherType).contains(o));
		IScope scope = ((ScopeableImpl) o).getScopeObject();

		ITopic theme = createTopic();
		o.addTheme(theme);
		scope = ((ScopeableImpl) o).getScopeObject();
		assertEquals(1, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(o));

		o.removeTheme(theme);
		assertEquals(0, topic.getCharacteristics(scope).size());

		scope = ((ScopeableImpl) o).getScopeObject();
		assertEquals(2, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(o));
		assertTrue(topic.getCharacteristics(scope).contains(n));

		o.remove();
		assertEquals(1, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(n));

		n.addTheme(theme);
		assertEquals(0, topic.getCharacteristics(scope).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#addSupertype(org.tmapi.core.Topic)}
	 * .
	 */
	public void testSupertypes() throws Exception {
		int cnt = 0;
		ITopic t = createTopic();
		if (factory
				.getFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION)) {
			Association a = createAssociation(createTopicBySI(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION));
			a.createRole(
					createTopicBySI(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE),
					t);
			a.createRole(
					createTopicBySI(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE),
					createTopic());
			cnt++;
		}
		assertEquals(cnt, t.getSupertypes().size());

		t.addSupertype(createTopic());
		cnt++;
		assertEquals(cnt, t.getSupertypes().size());

		ITopic st = createTopic();
		t.addSupertype(st);
		cnt++;
		assertEquals(cnt, t.getSupertypes().size());
		assertTrue(t.getSupertypes().contains(st));

		t.removeSupertype(st);
		cnt--;
		assertEquals(cnt, t.getSupertypes().size());
		assertFalse(t.getSupertypes().contains(st));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#mergeIn(org.tmapi.core.Topic)}
	 * .
	 */
	public void testMergeIn() {
		ITopic topic = createTopicBySI("http://psi.example.org/topic");
		topic.createName("Name", new Topic[0]);
		topic.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
		topic.addType(createTopic());

		ITopic topic2 = createTopicBySL("http://psi.example.org/topic");
		topic2.createName("Name 2", new Topic[0]);
		topic2.createOccurrence(createTopic(), "Occurrence", new Topic[0]);

		Topic otherTopic = createTopic();
		Name n = otherTopic.createName("Name 3", new Topic[0]);
		n.setReifier(topic2);

		long cnt = topicMap.getTopics().size();
		topic.mergeIn(topic2);

		assertEquals(cnt - 1, topicMap.getTopics().size());

		assertEquals(topic.getId(), topic2.getId());
		assertEquals(topic.getSubjectIdentifiers(),
				topic2.getSubjectIdentifiers());
		assertEquals(1, topic.getSubjectIdentifiers().size());
		assertEquals(topic.getSubjectLocators(), topic2.getSubjectLocators());
		assertEquals(1, topic.getSubjectLocators().size());
		assertEquals(topic.getNames(), topic2.getNames());
		assertEquals(2, topic.getNames().size());
		assertEquals(topic.getOccurrences(), topic2.getOccurrences());
		assertEquals(2, topic.getOccurrences().size());
		assertEquals(topic.getTypes(), topic2.getTypes());
		assertEquals(1, topic.getTypes().size());
		assertEquals(topic.getReified(), topic2.getReified());
		assertNotNull(topic.getReified());
		assertEquals(n, topic.getReified());
	}

	public void testRemove() throws Exception {
		Topic reifier = createTopic();
		Topic topic = createTopic();
		Name n = topic.createName(createTopic(), "Name", new Topic[0]);
		n.setReifier(reifier);
		assertEquals(3, topicMap.getTopics().size());
		assertTrue(topicMap.getTopics().contains(reifier));
		assertTrue(topicMap.getTopics().contains(topic));
		try {
			reifier.remove();
		} catch (TopicInUseException e) {
			fail("Deletion constraint is false!");
		}
		assertEquals(2, topicMap.getTopics().size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 */
	public void testRemoveBoolean() {
		ITopic topic = createTopic();
		IAssociation association = createAssociation(createTopic());
		association.createRole(createTopic(), topic);
		try {
			topic.remove();
			fail("Topic is in use!");
		} catch (Exception e) {
			// NOTHING TO DO
		}
		try {
			topic.remove(true);
		} catch (Exception e) {
			fail("Topic is in use, but delete cascade!");
		}
	}

	public void testBestLabel() {
		Locator si = createLocator("http://psi.example.org/si/topic");
		Locator oSi = createLocator("http://psi.example.org/si/topic");
		Locator sl = createLocator("http://psi.example.org/sl/topic");
		Locator oSl = createLocator("http://psi.example.org/sl/topic");
		Locator ii = createLocator("http://psi.example.org/ii/topic");
		Locator oIi = createLocator("http://psi.example.org/ii/topic");
		ITopic topic = (ITopic) topicMap.createTopicBySubjectIdentifier(si);
		topic.removeSubjectIdentifier(si);

		assertEquals("Best label should be the id", topic.getId(),
				topic.getBestLabel());

		topic.addItemIdentifier(ii);
		assertEquals("Best label should be the item-identifier",
				ii.getReference(), topic.getBestLabel());
		topic.addItemIdentifier(oIi);
		assertEquals(
				"Best label should be the lexicographically smallest item-identifier",
				ii.getReference(), topic.getBestLabel());

		topic.addSubjectLocator(sl);
		assertEquals("Best label should be the subject-locator",
				sl.getReference(), topic.getBestLabel());
		topic.addSubjectLocator(oSl);
		assertEquals(
				"Best label should be the lexicographically smallest subject-locator",
				sl.getReference(), topic.getBestLabel());

		topic.addSubjectIdentifier(si);
		assertEquals("Best label should be the subject-identifier",
				si.getReference(), topic.getBestLabel());
		topic.addSubjectIdentifier(oSi);
		assertEquals(
				"Best label should be the lexicographically smallest subject-identifier",
				si.getReference(), topic.getBestLabel());

		Topic type = createTopic();
		Topic theme = createTopic();
		Topic otherTheme = createTopic();

		Name name1 = topic.createName("Name");
		assertEquals("Best label should be the name", name1.getValue(),
				topic.getBestLabel());
		
		Name name2 = topic.createName("NameZZZ");
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the the lexicographically smallest name value",
				name1.getValue(), topic.getBestLabel());

		name1.setType(type);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals("Best label should be the default name", name2.getValue(),
				topic.getBestLabel());
		
		name2.setType(type);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals("Best label should be the default name", name1.getValue(),
				topic.getBestLabel());

		name1.addTheme(theme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the unconstained scope",
				name2.getValue(), topic.getBestLabel());
		
		name2.addTheme(otherTheme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());
		
		name2.setValue(name1.getValue());
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());

		name2.setValue("NameZZZ");
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());

		name2.removeTheme(otherTheme);
		name2.addTheme(theme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the unconstained scope",
				name1.getValue(), topic.getBestLabel());

		name1.addTheme(otherTheme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name2.getValue(), topic.getBestLabel());
		
		name2.addTheme(otherTheme);		
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());

		name1.removeTheme(otherTheme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());
		
		name2.removeTheme(theme);
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());
		
		name2.setValue("A");
		System.out.println("Topic-ID:" + topic.getId() + "  name1-ID: " + name1.getId() + "  name2-ID: " + name2.getId());
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes and shortest value",
				name2.getValue(), topic.getBestLabel());
	}

	public void testBestLabelWithTheme() {
		Topic theme = createTopic();
		Topic otherTheme = createTopic();
		Topic newTheme = createTopic();
		Locator si = createLocator("http://psi.example.org/si/topic");
		Locator oSi = createLocator("http://psi.example.org/si/topic");
		Locator sl = createLocator("http://psi.example.org/sl/topic");
		Locator oSl = createLocator("http://psi.example.org/sl/topic");
		Locator ii = createLocator("http://psi.example.org/ii/topic");
		Locator oIi = createLocator("http://psi.example.org/ii/topic");
		ITopic topic = (ITopic) topicMap.createTopicBySubjectIdentifier(si);
		topic.removeSubjectIdentifier(si);

		assertEquals("Best label should be the id", topic.getId(),
				topic.getBestLabel());

		topic.addItemIdentifier(ii);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals("Best label should be the item-identifier",
				ii.getReference(), topic.getBestLabel());
		topic.addItemIdentifier(oIi);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals(
				"Best label should be the lexicographically smallest item-identifier",
				ii.getReference(), topic.getBestLabel());

		topic.addSubjectLocator(sl);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals("Best label should be the subject-locator",
				sl.getReference(), topic.getBestLabel());
		topic.addSubjectLocator(oSl);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals(
				"Best label should be the lexicographically smallest subject-locator",
				sl.getReference(), topic.getBestLabel());

		topic.addSubjectIdentifier(si);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals("Best label should be the subject-identifier",
				si.getReference(), topic.getBestLabel());
		topic.addSubjectIdentifier(oSi);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals(
				"Best label should be the lexicographically smallest subject-identifier",
				si.getReference(), topic.getBestLabel());

		Topic type = createTopic();

		Name name1 = topic.createName("Name");
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals("Best label should be the name", name1.getValue(),
				topic.getBestLabel());
		Name name2 = topic.createName("NameZZZ");
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals(
				"Best label should be the the lexicographically smallest name value",
				name1.getValue(), topic.getBestLabel());

		name1.setType(type);
		assertEquals("Best label should be the default name", name2.getValue(),
				topic.getBestLabel());
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		name2.setType(type);
		assertNull("Strict mode for best label should return null.",
				topic.getBestLabel(theme, true));
		assertEquals("Best label should be the default name", name1.getValue(),
				topic.getBestLabel());

		name1.addTheme(theme);
		assertEquals(
				"Best label should be the name with the unconstained scope",
				name2.getValue(), topic.getBestLabel());
		assertEquals(
				"Best label should be the name with the given theme scope",
				name1.getValue(), topic.getBestLabel(theme));
		name2.addTheme(theme);
		assertEquals(
				"Best label should be the name with the unconstained scope",
				name1.getValue(), topic.getBestLabel());

		name1.addTheme(otherTheme);
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name2.getValue(), topic.getBestLabel());
		name2.addTheme(otherTheme);
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());

		name1.removeTheme(otherTheme);
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());
		name2.removeTheme(theme);
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes",
				name1.getValue(), topic.getBestLabel());
		name2.setValue("A");
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes and shortest value",
				name2.getValue(), topic.getBestLabel());

		name1.addTheme(newTheme);
		name2.addTheme(newTheme);
		assertEquals(
				"Best label should be the name with the scope with the smallest number of themes and shortest value",
				name2.getValue(), topic.getBestLabel(newTheme));
	}

}
