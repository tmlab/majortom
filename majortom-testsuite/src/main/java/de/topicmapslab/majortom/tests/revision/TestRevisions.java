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
package de.topicmapslab.majortom.tests.revision;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.revision.IRevisionChange;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociation;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociationRole;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class TestRevisions extends MaJorToMTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.topicmapslab.engine.tests.MaJorToMTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory.setFeature(FeatureStrings.SUPPORT_HISTORY, true);
	}

	public void testTopicRevisions() throws Exception {
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		if (topicMap.getTopicMapSystem().getFeature(FeatureStrings.SUPPORT_HISTORY)) {

			assertNull(index.getLastModification());

			ITopic topic = createTopic();
			assertEquals(1, index.getRevisions(topic).size());
			assertEquals(1, index.getRevisions(topic).get(0).getChangeset().size());
			checkChange(index.getRevisions(topic).get(0).getChangeset().get(0), TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
			assertEquals(1, index.getChangeset(topic).size());

			topic.addSubjectIdentifier(topicMap.createLocator("http://psi.exampple.org/topicWithoutII"));
			assertEquals(2, index.getRevisions(topic).size());
			assertEquals(1, index.getRevisions(topic).get(1).getChangeset().size());
			assertEquals(2, index.getChangeset(topic).size());

			ITopic type = createTopic();
			assertEquals(1, index.getRevisions(type).size());
			assertEquals(1, index.getRevisions(type).get(0).getChangeset().size());

			topic.addType(type);
			assertEquals(3, index.getRevisions(topic).size());
			assertEquals(4, index.getRevisions(topic).get(2).getChangeset().size());
			assertEquals(3, index.getChangeset(topic).size());
			assertEquals(2, index.getRevisions(type).size());
			assertEquals(4, index.getRevisions(type).get(1).getChangeset().size());
			assertEquals(2, index.getChangeset(type).size());

			// File file = new File("src/test/resources/history.xml");
			// index.toXml(file);

			topic.createName(type, "Name", new Topic[0]);
			assertEquals(2, index.getChangeset(type).size());
			assertEquals(4, index.getChangeset(topic).size());
		}
	}

	public void testLastModification() throws Exception {
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		assertNull(index.getLastModification());

		ITopic topicWithoutIdentifier = createTopic();
		Calendar calendar = new GregorianCalendar();
		Calendar lastModification = index.getLastModification();
		assertNotNull(lastModification);
		assertEquals(calendar.getTimeInMillis(), lastModification.getTimeInMillis(), 20);

		Calendar lastModificationOrTopic = index.getLastModification(topicWithoutIdentifier);
		assertNotNull(lastModificationOrTopic);
		assertEquals(calendar.getTimeInMillis(), lastModificationOrTopic.getTimeInMillis(), 20);

		topicWithoutIdentifier.addSubjectIdentifier(topicMap.createLocator("http://psi.exampple.org/topicWithoutII"));

		assertNotSame(lastModificationOrTopic, index.getLastModification(topicWithoutIdentifier));
		assertNotSame(lastModification, index.getLastModification());
	}

	public void checkChange(IRevisionChange change, TopicMapEventType type, Construct context, Object newValue, Object oldValue) {
		assertEquals(type, change.getType());
		assertEquals(context, change.getContext());
		assertEquals(newValue, change.getNewValue());
		assertEquals(oldValue, change.getOldValue());
	}

	public void testTopicRemovedRevision() throws Exception {
		ITopic topic = createTopic();
		ITopic type = createTopic();
		IAssociation association = createAssociation(createTopic());
		Role role = association.createRole(type, topic);

		topic.remove(true);

		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();
		IRevision revision = index.getLastRevision();
		assertEquals(3, revision.getChangeset().size());
		Changeset set = revision.getChangeset();
		assertEquals(topic, set.get(2).getOldValue());
		ITopic clone = (ITopic) set.get(2).getOldValue();
		assertEquals(1, clone.getRolesPlayed().size());
		assertTrue(clone.getRolesPlayed().contains(role));
		assertEquals(1, clone.getRolesPlayed(type).size());
		assertTrue(clone.getRolesPlayed(type).contains(role));
		assertEquals(1, clone.getAssociationsPlayed().size());
		assertTrue(clone.getAssociationsPlayed().contains(association));
	}

	public void testRoleRevisions() throws Exception {
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		IAssociation association = createAssociation(createTopic());
		ITopic player = createTopic();
		ITopic type = createTopic();
		Role role = association.createRole(type, player);
		role.remove();

		IRevision revision = index.getLastRevision();
		assertNotNull(revision);
		assertFalse(revision.getChangeset().isEmpty());
		IRevisionChange change = revision.getChangeset().get(0);
		checkChange(change, TopicMapEventType.ROLE_REMOVED, association, null, role);
		role = (Role) change.getOldValue();
		assertTrue(role instanceof ReadOnlyAssociationRole);
		assertEquals(type, role.getType());
		assertEquals(player, role.getPlayer());
	}

	public void testAssociationRevisions() throws Exception {
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		ITopic assoicationType = createTopic();
		IAssociation association = createAssociation(assoicationType);
		ITopic player = createTopic();
		ITopic type = createTopic();
		Role role = association.createRole(type, player);
		ITopic otherPlayer = createTopic();
		ITopic otherType = createTopic();
		Role otherRole = association.createRole(otherType, otherPlayer);

		Changeset set = index.getAssociationChangeset(assoicationType);
		assertEquals(3, set.size());

		IAssociation other = createAssociation(assoicationType);
		set = index.getAssociationChangeset(assoicationType);
		assertEquals(4, set.size());

		other.createRole(createTopic(), createTopic());
		set = index.getAssociationChangeset(assoicationType);
		assertEquals(5, set.size());

		association.remove();

		IRevision revision = index.getLastRevision();
		assertNotNull(revision);
		assertEquals(3, revision.getChangeset().size());
		IRevisionChange change = revision.getChangeset().get(0);
		assertEquals(TopicMapEventType.ROLE_REMOVED, change.getType());
		assertEquals(association, change.getContext());
		assertNull(change.getNewValue());
		assertTrue(otherRole.equals(change.getOldValue()) || role.equals(change.getOldValue()));

		change = revision.getChangeset().get(1);
		assertEquals(TopicMapEventType.ROLE_REMOVED, change.getType());
		assertEquals(association, change.getContext());
		assertNull(change.getNewValue());
		assertTrue(otherRole.equals(change.getOldValue()) || role.equals(change.getOldValue()));

		change = revision.getChangeset().get(2);
		checkChange(change, TopicMapEventType.ASSOCIATION_REMOVED, topicMap, null, association);
		association = (IAssociation) change.getOldValue();
		assertTrue(association instanceof ReadOnlyAssociation);
		assertEquals(2, association.getRoles().size());
		assertTrue(association.getRoles().contains(otherRole));
		assertTrue(association.getRoles().contains(role));

		set = index.getAssociationChangeset(assoicationType);

	}

	// public void testMergingRevisions() throws Exception {
	//
	// factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION,
	// false);
	// factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION,
	// false);
	//
	// IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
	// index.open();
	//
	// ITopic topic = createTopicBySI("http://psi.example.org/topic");
	// topic.createName("Name", new Topic[0]);
	// topic.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
	// topic.addType(createTopic());
	//
	// ITopic topic2 = createTopicBySL("http://psi.example.org/topic");
	// topic2.createName("Name 2", new Topic[0]);
	// topic2.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
	//
	// Topic otherTopic = createTopic();
	// Name n = otherTopic.createName("Name 3", new Topic[0]);
	// n.setReifier(topic2);
	//
	// assertEquals(7, topicMap.getTopics().size());
	//		
	// topic.addSubjectLocator(topicMap.createLocator("http://psi.example.org/topic"));
	//
	// assertEquals(6, topicMap.getTopics().size());
	//		
	// assertEquals(topic.getId(), topic2.getId());
	// assertEquals(topic.getSubjectIdentifiers(),
	// topic2.getSubjectIdentifiers());
	// assertEquals(1, topic.getSubjectIdentifiers().size());
	// assertEquals(topic.getSubjectLocators(), topic2.getSubjectLocators());
	// assertEquals(1, topic.getSubjectLocators().size());
	// assertEquals(topic.getNames(), topic2.getNames());
	// assertEquals(2, topic.getNames().size());
	// assertEquals(topic.getOccurrences(), topic2.getOccurrences());
	// assertEquals(2, topic.getOccurrences().size());
	// assertEquals(topic.getTypes(), topic2.getTypes());
	// assertEquals(1, topic.getTypes().size());
	// assertEquals(topic.getReified(), topic2.getReified());
	// assertNotNull(topic.getReified());
	// assertEquals(n, topic.getReified());
	//
	// IRevision revision = index.getLastRevision().getPast();
	// assertEquals(18, revision.getChangeset().size());
	// }

	public void testRemovingTypeInstanceRelation() throws Exception {
		ITopic topic = createTopic();
		ITopic other = createTopic();

		topic.addType(other);

		assertEquals(1, topic.getTypes().size());
		assertTrue(topic.getTypes().contains(other));

		assertEquals(1, topic.getAssociationsPlayed().size());
		Association a = topic.getAssociationsPlayed().iterator().next();
		a.remove();

		assertEquals(0, topic.getAssociationsPlayed().size());
		assertEquals(1, topic.getTypes().size());
		assertTrue(topic.getTypes().contains(other));
		topic.remove();

		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		topic = createTopic();

		topic.addType(other);

		assertEquals(1, topic.getTypes().size());
		assertTrue(topic.getTypes().contains(other));

		assertEquals(1, topic.getAssociationsPlayed().size());
		assertEquals(1, topic.getTypes().size());
		assertTrue(topic.getTypes().contains(other));
		topic.remove();
	}

	public void testMetaData() {
		createTopic();
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();
		IRevision revision = index.getLastRevision();
		assertNotNull(revision);

		for (long l = 1; l < 100; l++) {
			revision.addMetaData("key#" + Long.toString(l), "value#" + Long.toString(l));
			assertEquals("value#" + Long.toString(l), revision.getMetaData("key#" + Long.toString(l)));
			assertEquals(l, revision.getMetadata().size());
		}

		for (long l = 1; l < 100; l++) {
			revision.addMetaData("key#" + Long.toString(l), "new#" + Long.toString(l));
			assertEquals("new#" + Long.toString(l), revision.getMetaData("key#" + Long.toString(l)));
			assertEquals("Number of meta-data should be keep constants because of overwrite key", 99, revision.getMetadata().size());
		}
	}
}
