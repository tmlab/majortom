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
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class TestConstructImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	public void testGetItemIdentifiers() {
		Association a = createAssociation(createTopic());
		assertTrue(a.getItemIdentifiers().isEmpty());

		Locator ii = createLocator("http://psi.example.org/ii");
		a.addItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));

		a.addItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));

		Locator ii2 = createLocator("http://psi.example.org/ii2");
		a.addItemIdentifier(ii2);
		assertEquals(2, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));
		assertTrue(a.getItemIdentifiers().contains(ii2));

		a.removeItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertFalse(a.getItemIdentifiers().contains(ii));
		assertTrue(a.getItemIdentifiers().contains(ii2));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 */
	public void testRemove() {
		Association a = createAssociation(createTopic());
		a.createRole(createTopic(), createTopic());
		a.createRole(createTopic(), createTopic());
		a.createRole(createTopic(), createTopic());
		a.remove();
		assertTrue(topicMap.getAssociations().isEmpty());
	}

	public void testToString() throws Exception {
		String si_ = "http://psi.example.org/si";
		Locator si = createLocator(si_);
		String sl_ = "http://psi.example.org/sl";
		Locator sl = createLocator(sl_);
		String ii_ = "http://psi.example.org/ii";
		Locator ii = createLocator(ii_);

		/*
		 * topic with subject-identifier
		 */
		Topic t = createTopicBySI(si_);
		assertTrue(t.toString().equalsIgnoreCase("Topic{si:" + si_ + "}"));

		/*
		 * topic with subject-locator
		 */
		t.removeSubjectIdentifier(si);
		t.addSubjectLocator(sl);
		assertTrue(t.toString().equalsIgnoreCase("Topic{sl:" + sl_ + "}"));

		/*
		 * topic with item-identifier
		 */
		t.removeSubjectLocator(sl);
		t.addItemIdentifier(ii);
		assertTrue(t.toString().equalsIgnoreCase("Topic{ii:" + ii_ + "}"));

		/*
		 * topic with nothing
		 */
		t.removeItemIdentifier(ii);
		assertTrue(t.toString().equalsIgnoreCase("Topic{id:" + t.getId() + "}"));

		/*
		 * topic with name
		 */
		Topic type = createTopicBySI("http://psi.example.org/type");
		Name n = t.createName(type, "Name", new Topic[0]);
		assertTrue(t.toString().equalsIgnoreCase("Topic{Name:Name}"));

		/*
		 * Name
		 */
		assertTrue(n.toString().equalsIgnoreCase("Topic-Name{Parent:" + t.toString() + ";Type:" + type.toString() + ";Value:Name}"));

		/*
		 * Variant
		 */
		Variant v = n.createVariant("Value", topicMap.createLocator("xsd:string"), createTopic());
		assertTrue(v.toString().equalsIgnoreCase("Topic-Name-Variant{Parent:" + n.toString() + ";Value:Value;Datatype:xsd:string}"));

		/*
		 * Occurrence
		 */
		Occurrence o = t.createOccurrence(type, "Value", topicMap.createLocator("xsd:string"), new Topic[0]);
		assertTrue(o.toString().equalsIgnoreCase("Occurrence{Parent:" + t.toString() + ";Type:" + type.toString() + ";Value:Value;Datatype:xsd:string}"));

		/*
		 * Association Role
		 */
		Association association = createAssociation(type);
		Role role = association.createRole(type, t);
		assertTrue(role.toString().equalsIgnoreCase("Association-Role{Type:" + type.toString() + ";Player:" + t.toString() + "}"));

		/*
		 * Association
		 */
		assertTrue(association.toString().equalsIgnoreCase("Association{Type:" + type.toString() + ";Roles:" + association.getRoles().toString() + "}"));

		/*
		 * Topic-Map
		 */
		assertTrue(topicMap.toString().equalsIgnoreCase("Topic-Map{Base-Locator:" + topicMap.getLocator().toExternalForm() + "}"));
	}

	public void testIdentityClash() throws Exception {
		Locator locator = createLocator("http://psi.example.org/test");
		Topic topic = topicMap.createTopicByItemIdentifier(locator);
		
		assertEquals(1, topicMap.getTopics().size());
		

		/*
		 * used by topic		
		 */
		int cnt = 2;
		Topic other = createTopic();
		assertEquals(cnt, topicMap.getTopics().size());
		if ( topicMap.getTopicMapSystem().getFeature(FeatureStrings.AUTOMATIC_MERGING)){			
			other.addItemIdentifier(locator);
			cnt--;
			assertEquals(cnt, topicMap.getTopics().size());
		}else{
			try{
				other.addItemIdentifier(locator);
				fail("Item-identifier in use!");
			}catch(IdentityConstraintException e){
				assertEquals(e.getReporter(), other);
				assertEquals(e.getExisting(), topic);
			}
		}
		/*
		 * used by topic by subject-identifier	
		 */
		Topic another = createTopic();
		cnt++;
		assertEquals(cnt, topicMap.getTopics().size());
		if ( topicMap.getTopicMapSystem().getFeature(FeatureStrings.AUTOMATIC_MERGING)){			
			another.addSubjectIdentifier(locator);
			cnt--;
			assertEquals(cnt, topicMap.getTopics().size());
		}else{
			try{
				another.addSubjectIdentifier(locator);
				fail("Identifier is used as item-identifier!");
			}catch(IdentityConstraintException e){
				assertEquals(e.getReporter(), other);
				assertEquals(e.getExisting(), topic);
			}
		}
		
		/*
		 * get merged topic
		 */
		topic = (ITopic)topicMap.getConstructByItemIdentifier(locator);
		
		/*
		 * used by name
		 */
		Name name = createTopic().createName("Name", new Topic[0]);		
		try{
			name.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), name);
			assertEquals(e.getExisting(), topic);
		}
		/*
		 * used by variant
		 */
		Variant variant = name.createVariant("Variant", createTopic());		
		try{
			variant.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), variant);
			assertEquals(e.getExisting(), topic);
		}
		/*
		 * used by occurrence
		 */
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);		
		try{
			occurrence.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), occurrence);
			assertEquals(e.getExisting(), topic);
		}
		/*
		 * used by association
		 */
		Association association = createAssociation(createTopic());		
		try{
			association.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), association);
			assertEquals(e.getExisting(), topic);
		}
		/*
		 * used by role
		 */
		Role role = association.createRole(createTopic(), createTopic());		
		try{
			role.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), role);
			assertEquals(e.getExisting(), topic);
		}
		/*
		 * used by topic map
		 */
		try{
			topicMap.addItemIdentifier(locator);
			fail("Item-identifier in use!");
		}catch(IdentityConstraintException e){
			assertEquals(e.getReporter(), topicMap);
			assertEquals(e.getExisting(), topic);
		}
		
	}
}
