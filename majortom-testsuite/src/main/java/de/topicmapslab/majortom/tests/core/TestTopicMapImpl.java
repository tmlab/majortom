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

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TestTopicMapImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopicMap()}.
	 */
	public void testGetTopicMap() {
		assertEquals(topicMap, topicMap.getTopicMap());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#addTag(java.lang.String)}
	 * .
	 */
	public void testAddTagString() {
		if (topicMap.getStore().isRevisionManagementEnabled()) {

			IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
			index.open();

			try {
				index.getRevision("tag");
				fail("Tag should be unknown!");
			} catch (Exception e) {
				// NOTHING TO DO
			}

			topicMap.addTag("tag");
			try {
				index.getRevision("tag");
			} catch (Exception e) {
				fail("Tag should be known!");
			}

			Calendar c = new GregorianCalendar();
			topicMap.addTag("tag2", c);

			createTopic();

			IRevision revisonByCalendar = index.getRevision(c);
			IRevision revisonByTag = index.getRevision("tag2");

			assertNotNull(revisonByTag);
			assertNotNull(revisonByCalendar);
			assertEquals(revisonByCalendar, revisonByTag);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getAssociations(org.tmapi.core.Topic)}
	 * .
	 */
	public void testGetAssociations() {

		ITopic type = createTopic();
		IAssociation association = createAssociation(createTopic());

		assertEquals(1, topicMap.getAssociations().size());
		assertEquals(0, topicMap.getAssociations(type).size());

		association.setType(type);
		assertEquals(1, topicMap.getAssociations().size());
		assertEquals(1, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));

		IAssociation otherAssociation = createAssociation(type);
		assertEquals(2, topicMap.getAssociations().size());
		assertEquals(2, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));
		assertTrue(topicMap.getAssociations(type).contains(otherAssociation));

		ITopic theme = createTopic();
		IAssociation scopedAssociation = (IAssociation) topicMap.createAssociation(createTopic(), theme);
		IScope scope = scopedAssociation.getScopeObject();
		assertEquals(3, topicMap.getAssociations().size());
		assertEquals(2, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));
		assertTrue(topicMap.getAssociations(type).contains(otherAssociation));
		assertEquals(1, topicMap.getAssociations(scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(scopedAssociation));
		assertEquals(0, topicMap.getAssociations(type, scope).size());

		association.addTheme(theme);
		assertEquals(3, topicMap.getAssociations().size());
		assertEquals(2, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));
		assertTrue(topicMap.getAssociations(type).contains(otherAssociation));
		assertEquals(2, topicMap.getAssociations(scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(scopedAssociation));
		assertTrue(topicMap.getAssociations(scope).contains(association));
		assertEquals(1, topicMap.getAssociations(type, scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(association));

		association.addTheme(theme);
		assertEquals(3, topicMap.getAssociations().size());
		assertEquals(2, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));
		assertTrue(topicMap.getAssociations(type).contains(otherAssociation));
		assertEquals(2, topicMap.getAssociations(scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(scopedAssociation));
		assertTrue(topicMap.getAssociations(scope).contains(association));
		assertEquals(1, topicMap.getAssociations(type, scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(association));

		association.removeTheme(theme);
		assertEquals(3, topicMap.getAssociations().size());
		assertEquals(2, topicMap.getAssociations(type).size());
		assertTrue(topicMap.getAssociations(type).contains(association));
		assertTrue(topicMap.getAssociations(type).contains(otherAssociation));
		assertEquals(1, topicMap.getAssociations(scope).size());
		assertTrue(topicMap.getAssociations(scope).contains(scopedAssociation));
		assertFalse(topicMap.getAssociations(scope).contains(association));
		assertEquals(0, topicMap.getAssociations(type, scope).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#createAssociation(org.tmapi.core.Topic, org.tmapi.core.Topic[])}
	 * .
	 */
	public void testCreateAssociation() {
		ITopic type = createTopic();
		ITopic theme = createTopic();

		IAssociation association = createAssociation(type);
		assertEquals(type, association.getType());
		assertEquals(0, association.getScope().size());
		assertEquals(0, association.getScopeObject().getThemes().size());

		IAssociation associationWithTopic = (IAssociation) topicMap.createAssociation(type, theme);
		assertEquals(type, associationWithTopic.getType());
		assertEquals(1, associationWithTopic.getScope().size());
		assertTrue(associationWithTopic.getScope().contains(theme));
		assertEquals(1, associationWithTopic.getScopeObject().getThemes().size());
		assertTrue(associationWithTopic.getScopeObject().getThemes().contains(theme));

		ITopic theme2 = createTopic();
		IAssociation associationWithTopics = (IAssociation) topicMap.createAssociation(type, theme, theme2);
		assertEquals(type, associationWithTopics.getType());
		assertEquals(2, associationWithTopics.getScope().size());
		assertTrue(associationWithTopics.getScope().contains(theme));
		assertTrue(associationWithTopics.getScope().contains(theme2));
		assertEquals(2, associationWithTopics.getScopeObject().getThemes().size());
		assertTrue(associationWithTopics.getScopeObject().getThemes().contains(theme));
		assertTrue(associationWithTopics.getScopeObject().getThemes().contains(theme2));

		IAssociation associationWithTopicArray = (IAssociation) topicMap.createAssociation(type, new Topic[] { theme, theme2 });
		assertEquals(type, associationWithTopicArray.getType());
		assertEquals(2, associationWithTopicArray.getScope().size());
		assertTrue(associationWithTopicArray.getScope().contains(theme));
		assertTrue(associationWithTopicArray.getScope().contains(theme2));
		assertEquals(2, associationWithTopicArray.getScopeObject().getThemes().size());
		assertTrue(associationWithTopicArray.getScopeObject().getThemes().contains(theme));
		assertTrue(associationWithTopicArray.getScopeObject().getThemes().contains(theme2));

		Set<Topic> themes = HashUtil.getHashSet();
		themes.add(theme);
		themes.add(theme2);

		IAssociation associationWithCollection = (IAssociation) topicMap.createAssociation(type, themes);
		assertEquals(type, associationWithCollection.getType());
		assertEquals(2, associationWithCollection.getScope().size());
		assertTrue(associationWithCollection.getScope().contains(theme));
		assertTrue(associationWithCollection.getScope().contains(theme2));
		assertEquals(2, associationWithCollection.getScopeObject().getThemes().size());
		assertTrue(associationWithCollection.getScopeObject().getThemes().contains(theme));
		assertTrue(associationWithCollection.getScopeObject().getThemes().contains(theme2));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#createLocator(java.lang.String)}
	 * .
	 */
	public void testCreateLocator() {
		final String ref = "http://psi.example.org";
		Locator locator = topicMap.createLocator(ref);
		assertTrue(locator.getReference().equalsIgnoreCase(ref));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#createTopic()}.
	 */
	public void testCreateTopic() {

		ITopic topic = (ITopic) topicMap.createTopic();
		assertEquals(0, topic.getSubjectIdentifiers().size());
		assertEquals(1, topic.getItemIdentifiers().size());
		assertEquals(0, topic.getSubjectLocators().size());

		Locator si = createLocator("http://psi.example.org/si");
		ITopic topicWithSi = (ITopic) topicMap.createTopicBySubjectIdentifier(si);
		assertEquals(1, topicWithSi.getSubjectIdentifiers().size());
		assertTrue(topicWithSi.getSubjectIdentifiers().contains(si));
		assertEquals(0, topicWithSi.getItemIdentifiers().size());
		assertEquals(0, topicWithSi.getSubjectLocators().size());

		Locator ii = createLocator("http://psi.example.org/ii");
		ITopic topicWithII = (ITopic) topicMap.createTopicByItemIdentifier(ii);
		assertEquals(0, topicWithII.getSubjectIdentifiers().size());
		assertEquals(1, topicWithII.getItemIdentifiers().size());
		assertTrue(topicWithII.getItemIdentifiers().contains(ii));
		assertEquals(0, topicWithII.getSubjectLocators().size());

		Locator sl = createLocator("http://psi.example.org/sl");
		ITopic topicWithSl = (ITopic) topicMap.createTopicBySubjectLocator(sl);
		assertEquals(0, topicWithSl.getSubjectIdentifiers().size());
		assertEquals(0, topicWithSl.getItemIdentifiers().size());
		assertEquals(1, topicWithSl.getSubjectLocators().size());
		assertTrue(topicWithSl.getSubjectLocators().contains(sl));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getConstructById(java.lang.String)}
	 * .
	 */
	public void testGetConstructById() {

		assertEquals(topicMap, topicMap.getConstructById(topicMap.getId()));

		ITopic topic = createTopic();
		assertEquals(topic, topicMap.getConstructById(topic.getId()));

		Name n = topic.createName("Name", new Topic[0]);
		assertEquals(n, topicMap.getConstructById(n.getId()));

		Variant v = n.createVariant("Var", createTopic());
		assertEquals(v, topicMap.getConstructById(v.getId()));

		Occurrence o = topic.createOccurrence(createTopic(), "Occ", new Topic[0]);
		assertEquals(o, topicMap.getConstructById(o.getId()));

		Association a = createAssociation(createTopic());
		assertEquals(a, topicMap.getConstructById(a.getId()));

		Role r = a.createRole(createTopic(), createTopic());
		assertEquals(r, topicMap.getConstructById(r.getId()));

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getConstructByItemIdentifier(org.tmapi.core.Locator)}
	 * .
	 */
	public void testGetConstructOrTopic() {
		ITopic topic = (ITopic) topicMap.createTopic();
		assertEquals(0, topic.getSubjectIdentifiers().size());
		assertEquals(1, topic.getItemIdentifiers().size());
		assertEquals(0, topic.getSubjectLocators().size());
		assertEquals(topic, topicMap.getConstructByItemIdentifier(topic.getItemIdentifiers().iterator().next()));

		Locator si = createLocator("http://psi.example.org/si");
		Locator sl = createLocator("http://psi.example.org/sl");
		Locator ii = createLocator("http://psi.example.org/ii");

		ITopic topicWithSi = (ITopic) topicMap.createTopicBySubjectIdentifier(si);
		assertEquals(1, topicWithSi.getSubjectIdentifiers().size());
		assertTrue(topicWithSi.getSubjectIdentifiers().contains(si));
		assertEquals(0, topicWithSi.getItemIdentifiers().size());
		assertEquals(0, topicWithSi.getSubjectLocators().size());
		assertEquals(topicWithSi, topicMap.getTopicBySubjectIdentifier(si));
		assertNull(topicMap.getTopicBySubjectIdentifier(ii));
		assertNull(topicMap.getTopicBySubjectIdentifier(sl));

		ITopic topicWithII = (ITopic) topicMap.createTopicByItemIdentifier(ii);
		assertEquals(0, topicWithII.getSubjectIdentifiers().size());
		assertEquals(1, topicWithII.getItemIdentifiers().size());
		assertTrue(topicWithII.getItemIdentifiers().contains(ii));
		assertEquals(0, topicWithII.getSubjectLocators().size());
		assertEquals(topicWithII, topicMap.getConstructByItemIdentifier(ii));
		assertNull(topicMap.getConstructByItemIdentifier(si));
		assertNull(topicMap.getConstructByItemIdentifier(sl));

		ITopic topicWithSl = (ITopic) topicMap.createTopicBySubjectLocator(sl);
		assertEquals(0, topicWithSl.getSubjectIdentifiers().size());
		assertEquals(0, topicWithSl.getItemIdentifiers().size());
		assertEquals(1, topicWithSl.getSubjectLocators().size());
		assertTrue(topicWithSl.getSubjectLocators().contains(sl));
		assertEquals(topicWithSl, topicMap.getTopicBySubjectLocator(sl));
		assertNull(topicMap.getTopicBySubjectLocator(si));
		assertNull(topicMap.getTopicBySubjectLocator(ii));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getIndex(java.lang.Class)}
	 * .
	 */
	public void testGetIndex() {
		for (Class<? extends Index> clazz : new Class[] { ITypeInstanceIndex.class, ITransitiveTypeInstanceIndex.class, IRevisionIndex.class,
				IScopedIndex.class, ILiteralIndex.class, ISupertypeSubtypeIndex.class, IIdentityIndex.class }) {
			try {
				Index i = topicMap.getIndex(clazz);
				assertNotNull("Index of type " + clazz.getSimpleName() + " should not be null", i);
				assertTrue("Invalid type of index, expects " + clazz.getSimpleName(), clazz.isAssignableFrom(clazz));
				assertFalse(i.isOpen());
			} catch (UnsupportedOperationException e) {
				// NOTHING TO DO
			}
		}

		Index myIndex = new Index() {

			@Override
			public void close() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isAutoUpdated() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isOpen() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void open() {
				// TODO Auto-generated method stub

			}

			@Override
			public void reindex() {
				// TODO Auto-generated method stub

			}

		};

		try {
			topicMap.getIndex(myIndex.getClass());
		} catch (UnsupportedOperationException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopics()}.
	 */
	public void testGetTopics() {

		assertEquals(0, topicMap.getTopics().size());

		ITopic topic = createTopic();
		assertEquals(1, topicMap.getTopics().size());
		assertTrue(topicMap.getTopics().contains(topic));

		ITopic type = createTopic();
		assertEquals(2, topicMap.getTopics().size());
		assertTrue(topicMap.getTopics().contains(topic));
		assertTrue(topicMap.getTopics().contains(type));
		assertEquals(0, topicMap.getTopics(type).size());

		topic.addType(type);
		assertTrue(topicMap.getTopics().contains(topic));
		assertTrue(topicMap.getTopics().contains(type));
		assertEquals(1, topicMap.getTopics(type).size());
		assertTrue(topicMap.getTopics(type).contains(topic));

		ITopic otherType = createTopic();
		assertTrue(topicMap.getTopics().contains(topic));
		assertTrue(topicMap.getTopics().contains(type));
		assertTrue(topicMap.getTopics().contains(otherType));
		assertEquals(1, topicMap.getTopics(type).size());
		assertTrue(topicMap.getTopics(type).contains(topic));
		assertEquals(0, topicMap.getTopics(otherType).size());

		topic.addType(otherType);
		assertTrue(topicMap.getTopics().contains(topic));
		assertTrue(topicMap.getTopics().contains(type));
		assertTrue(topicMap.getTopics().contains(otherType));
		assertEquals(1, topicMap.getTopics(type).size());
		assertTrue(topicMap.getTopics(type).contains(topic));
		assertEquals(1, topicMap.getTopics(otherType).size());
		assertTrue(topicMap.getTopics(otherType).contains(topic));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.TopicMapImpl#mergeIn(org.tmapi.core.TopicMap)}
	 * .
	 */
	public void testMergeIn() throws Exception {

		ITopicMap otherTopicMap = (ITopicMap) factory.newTopicMapSystem().createTopicMap("http://psi.example.org/merge-in");
		try {
			Locator si = createLocator("http://psi.example.org/si");
			ITopic topic = (ITopic) topicMap.createTopicBySubjectIdentifier(si);
			Name n = topic.createName("Name", new Topic[0]);
			n.createVariant("Variant", topicMap.createTopicBySubjectIdentifier(topicMap.createLocator("http://psi.example.org/theme")));
			topic.createOccurrence(topicMap.createTopicByItemIdentifier(topicMap.createLocator("http://psi.example.org/occ-type")), "Occurrence", new Topic[0]);

			assertEquals(0, topic.getTypes().size());

			IAssociation association = createAssociation(createTopic());
			association.createRole(createTopic(), topic);

			assertEquals(6, topicMap.getTopics().size());

			ITopic t = (ITopic) otherTopicMap.createTopicBySubjectIdentifier(si);
			n = t.createName("Name", new Topic[0]);
			n.createVariant("Variant", otherTopicMap.createTopicBySubjectIdentifier(otherTopicMap.createLocator("http://psi.example.org/theme")));
			t.createOccurrence(otherTopicMap.createTopicByItemIdentifier(otherTopicMap.createLocator("http://psi.example.org/occ-type")), "Occurrence",
					new Topic[0]);
			t.addType(otherTopicMap.createTopic());

			association = (IAssociation) otherTopicMap.createAssociation(otherTopicMap.createTopic(), new Topic[0]);
			association.createRole(otherTopicMap.createTopic(), t);

			if (factory.hasFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
				assertEquals(10, otherTopicMap.getTopics().size());
			} else {
				assertEquals(7, otherTopicMap.getTopics().size());
			}

			topicMap.mergeIn(otherTopicMap);

			if (factory.hasFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
				assertEquals(10, otherTopicMap.getTopics().size());
			} else {
				assertEquals(7, otherTopicMap.getTopics().size());
			}
			if (factory.hasFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
				assertEquals(12, topicMap.getTopics().size());
			} else {
				assertEquals(9, topicMap.getTopics().size());
			}

			assertEquals(1, t.getNames().size());
			assertEquals(1, topic.getNames().size());
			assertEquals(1, t.getNames().iterator().next().getVariants().size());
			assertEquals(1, topic.getNames().iterator().next().getVariants().size());
			assertEquals(1, t.getOccurrences().size());
			assertEquals(1, topic.getOccurrences().size());
			if (factory.hasFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION)) {
				assertEquals(2, t.getAssociationsPlayed().size());
				assertEquals(3, topic.getAssociationsPlayed().size());
				assertEquals(2, t.getRolesPlayed().size());
				assertEquals(3, topic.getRolesPlayed().size());
			} else {
				assertEquals(1, t.getAssociationsPlayed().size());
				assertEquals(2, topic.getAssociationsPlayed().size());
				assertEquals(1, t.getRolesPlayed().size());
				assertEquals(2, topic.getRolesPlayed().size());
			}

			assertEquals(1, t.getTypes().size());
			assertEquals(1, topic.getTypes().size());
		} finally {
			otherTopicMap.remove(true);
		}
	}

	public void testCreateScope() {
		ITopic theme = createTopic();
		ITopic theme2 = createTopic();
		ITopic theme3 = createTopic();
		ITopic theme4 = createTopic();

		IScope scope = topicMap.createScope(theme, theme2, theme3);
		assertNotNull(scope);
		assertEquals(3, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
		assertTrue(scope.containsTheme(theme2));
		assertTrue(scope.containsTheme(theme3));

		Collection<Topic> themes = HashUtil.getHashSet();
		themes.add(theme4);
		scope = topicMap.createScope(themes);
		assertNotNull(scope);
		assertEquals(1, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme4));

	}
	
	public void testClear() {
		try {
			readTopicMap("toytm-with-unary-assoc.xtm");			
			topicMap.clear();
			assertEquals(0, topicMap.getAssociations().size());
			assertEquals(0, topicMap.getTopics().size());			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
	}

}
