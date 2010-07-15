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

		IAssociation scopedAsso = (IAssociation) topicMap.createAssociation(type, theme);
		scopedAsso.createRole(createTopic(), topic);

		assertEquals(3, topic.getAssociationsPlayed().size());
		assertTrue(topic.getAssociationsPlayed().contains(association));
		assertTrue(topic.getAssociationsPlayed().contains(typedAsso));
		assertTrue(topic.getAssociationsPlayed().contains(scopedAsso));

		assertEquals(2, topic.getAssociationsPlayed(type).size());
		assertTrue(topic.getAssociationsPlayed(type).contains(typedAsso));
		assertTrue(topic.getAssociationsPlayed(type).contains(scopedAsso));

		assertEquals(1, topic.getAssociationsPlayed(scopedAsso.getScopeObject()).size());
		assertTrue(topic.getAssociationsPlayed(scopedAsso.getScopeObject()).contains(scopedAsso));

		assertEquals(1, topic.getAssociationsPlayed(type, scopedAsso.getScopeObject()).size());
		assertTrue(topic.getAssociationsPlayed(type, scopedAsso.getScopeObject()).contains(scopedAsso));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics()}.
	 */
	public void testGetCharacteristics() {

		ITopic superType = createTopic();
		ITopic type = createTopic();
		type.addSupertype(superType);
		ITopic otherType = createTopic();
		otherType.addSupertype(superType);

		ITopic topic = createTopicBySI("http://psi.example.org/topic");
		Name n = topic.createName(type, "Name", new Topic[0]);
		Occurrence o = topic.createOccurrence(otherType, "Occurrence", new Topic[0]);

		assertEquals(1, topic.getCharacteristics(type).size());
		assertTrue(topic.getCharacteristics(type).contains(n));
		assertEquals(1, topic.getCharacteristics(otherType).size());
		assertTrue(topic.getCharacteristics(otherType).contains(o));
		assertEquals(2, topic.getCharacteristics(superType).size());
		assertTrue(topic.getCharacteristics(superType).contains(o));
		assertTrue(topic.getCharacteristics(superType).contains(n));
		IScope scope = ((ScopeableImpl) o).getScopeObject();
		assertEquals(2, topic.getCharacteristics(superType, scope).size());
		assertTrue(topic.getCharacteristics(superType,scope).contains(o));
		assertTrue(topic.getCharacteristics(superType,scope).contains(n));

		ITopic theme = createTopic();
		o.addTheme(theme);
		assertEquals(1, topic.getCharacteristics(superType, scope).size());
		assertTrue(topic.getCharacteristics(superType,scope).contains(n));
		scope = ((ScopeableImpl) o).getScopeObject();
		assertEquals(1, topic.getCharacteristics(superType, scope).size());
		assertTrue(topic.getCharacteristics(superType,scope).contains(o));
		assertEquals(1, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(o));
		
		o.removeTheme(theme);
		assertEquals(0, topic.getCharacteristics(superType, scope).size());
		assertEquals(0, topic.getCharacteristics(scope).size());
		
		scope = ((ScopeableImpl) o).getScopeObject();
		assertEquals(2, topic.getCharacteristics(superType, scope).size());
		assertTrue(topic.getCharacteristics(superType,scope).contains(o));
		assertTrue(topic.getCharacteristics(superType,scope).contains(n));
		assertEquals(2, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(o));
		assertTrue(topic.getCharacteristics(scope).contains(n));
		
		o.remove();
		assertEquals(1, topic.getCharacteristics(superType, scope).size());
		assertTrue(topic.getCharacteristics(superType,scope).contains(n));
		assertEquals(1, topic.getCharacteristics(scope).size());
		assertTrue(topic.getCharacteristics(scope).contains(n));
		
		n.addTheme(theme);
		assertEquals(0, topic.getCharacteristics(superType, scope).size());
		assertEquals(0, topic.getCharacteristics(scope).size());
		assertEquals(1, topic.getCharacteristics(superType).size());
		assertTrue(topic.getCharacteristics(superType).contains(n));
		
		type.removeSupertype(superType);
		assertEquals(0,topic.getCharacteristics(superType).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicImpl#addSupertype(org.tmapi.core.Topic)}
	 * .
	 */
	public void testSupertypes() {
		Association a = createAssociation(createTopicBySI(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION));
		ITopic t = createTopic();
		a.createRole(createTopicBySI(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE), t);
		a.createRole(createTopicBySI(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE), createTopic());
		assertEquals(1, t.getSupertypes().size());

		t.addSupertype(createTopic());
		assertEquals(2, t.getSupertypes().size());

		ITopic st = createTopic();
		t.addSupertype(st);
		assertEquals(3, t.getSupertypes().size());
		assertTrue(t.getSupertypes().contains(st));

		t.removeSupertype(st);
		assertEquals(2, t.getSupertypes().size());
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
		assertEquals(topic.getSubjectIdentifiers(), topic2.getSubjectIdentifiers());
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
			topic.remove();
			fail("topic is use as reifier!");
		} catch (TopicInUseException e) {
			// NOTHING TO DO
		}
		assertEquals(3, topicMap.getTopics().size());

		factory.setFeature(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION, false);
		try {
			topic.remove();
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

}
