package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestTypeTransaction extends MaJorToMTestCase {

	public void testAssociationModification() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Locator loc2 = createLocator("http://psi.example.org/2");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);
		Topic otherType = topicMap.createTopicBySubjectIdentifier(loc2);

		ITransaction transaction = topicMap.createTransaction();
		Association association = transaction.createAssociation(type, new Topic[0]);
		transaction.rollback();
		assertEquals(0, topicMap.getAssociations().size());

		transaction = topicMap.createTransaction();
		association = transaction.createAssociation(type, new Topic[0]);
		assertEquals(1, transaction.getAssociations(type).size());
		assertTrue(transaction.getAssociations(type).contains(association));

		association.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, transaction.getAssociations(type).size());
		assertEquals(1, transaction.getAssociations(otherType).size());
		assertTrue(transaction.getAssociations(otherType).contains(association));
		transaction.rollback();

		assertEquals(0, topicMap.getAssociations(type).size());
		assertEquals(0, topicMap.getAssociations(otherType).size());

		transaction = topicMap.createTransaction();
		association = transaction.createAssociation(type, new Topic[0]);
		assertEquals(1, transaction.getAssociations(type).size());
		assertTrue(transaction.getAssociations(type).contains(association));

		association.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, transaction.getAssociations(type).size());
		assertEquals(1, transaction.getAssociations(otherType).size());
		assertTrue(transaction.getAssociations(otherType).contains(association));
		association.remove();
		assertEquals(0, transaction.getAssociations(type).size());
		assertEquals(0, transaction.getAssociations(otherType).size());

		try {
			association.setType(transaction.getTopicBySubjectIdentifier(loc2));
			fail("Construct should be removed!");
		} catch (ConstructRemovedException e) {
			// NOTHING TO DO
		}
		transaction.rollback();

		transaction = topicMap.createTransaction();
		association = transaction.createAssociation(type, new Topic[0]);
		assertEquals(1, transaction.getAssociations(type).size());
		assertTrue(transaction.getAssociations(type).contains(association));

		association.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, transaction.getAssociations(type).size());
		assertEquals(1, transaction.getAssociations(otherType).size());
		assertTrue(transaction.getAssociations(otherType).contains(association));
		transaction.commit();

		assertEquals(0, topicMap.getAssociations(type).size());
		assertEquals(topicMap, otherType.getParent());
		assertEquals(1, topicMap.getAssociations(otherType).size());
		assertEquals(1, topicMap.getAssociations().size());
	}

	public void testRoleModification() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Locator loc2 = createLocator("http://psi.example.org/2");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);
		Topic otherType = topicMap.createTopicBySubjectIdentifier(loc2);

		Association association = topicMap.createAssociation(type, new Topic[0]);

		ITransaction transaction = topicMap.createTransaction();
		IAssociation association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		Role r = association_.createRole(transaction.getTopicBySubjectIdentifier(loc), transaction.createTopic());
		transaction.rollback();
		assertEquals(0, association.getRoles().size());

		transaction = topicMap.createTransaction();
		association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		r = association_.createRole(transaction.getTopicBySubjectIdentifier(loc), transaction.createTopic());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).contains(r));

		r.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).contains(r));
		transaction.rollback();

		assertEquals(0, association.getRoles(type).size());
		assertEquals(0, association.getRoles(otherType).size());

		transaction = topicMap.createTransaction();
		association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		r = association_.createRole(transaction.getTopicBySubjectIdentifier(loc), transaction.createTopic());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).contains(r));

		r.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).contains(r));
		r.remove();
		assertEquals(0, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(0, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).size());

		try {
			r.setType(transaction.getTopicBySubjectIdentifier(loc2));
			fail("Construct should be removed!");
		} catch (ConstructRemovedException e) {
			// NOTHING TO DO
		}
		transaction.rollback();

		transaction = topicMap.createTransaction();
		association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		r = association_.createRole(transaction.getTopicBySubjectIdentifier(loc), transaction.createTopic());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).contains(r));

		r.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(association_.getRoles(transaction.getTopicBySubjectIdentifier(loc2)).contains(r));
		transaction.commit();

		assertEquals(0, association.getRoles(type).size());
		assertEquals(topicMap, otherType.getParent());
		assertEquals(1, association.getRoles(otherType).size());
		assertEquals(1, association.getRoles().size());
		assertEquals(1, topicMap.getAssociations().size());
	}

	public void testNameModification() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Locator loc2 = createLocator("http://psi.example.org/2");
		Locator loc3 = createLocator("http://psi.example.org/3");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);
		Topic otherType = topicMap.createTopicBySubjectIdentifier(loc2);
		Topic topic = topicMap.createTopicBySubjectIdentifier(loc3);

		ITransaction transaction = topicMap.createTransaction();
		Topic topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		Name n = topic_.createName(transaction.getTopicBySubjectIdentifier(loc), "Name", new Topic[0]);
		transaction.rollback();
		assertEquals(0, topic.getNames().size());

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		n = topic_.createName(transaction.getTopicBySubjectIdentifier(loc), "Name", new Topic[0]);
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).contains(n));

		n.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).contains(n));
		transaction.rollback();

		assertEquals(0, topic.getNames(type).size());
		assertEquals(0, topic.getNames(otherType).size());

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		n = topic_.createName(transaction.getTopicBySubjectIdentifier(loc), "Name", new Topic[0]);
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).contains(n));

		n.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).contains(n));
		n.remove();
		assertEquals(0, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(0, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).size());

		try {
			n.setType(transaction.getTopicBySubjectIdentifier(loc2));
			fail("Construct should be removed!");
		} catch (ConstructRemovedException e) {
			// NOTHING TO DO
		}
		transaction.rollback();

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		n = topic_.createName(transaction.getTopicBySubjectIdentifier(loc), "Name", new Topic[0]);
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).contains(n));

		n.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getNames(transaction.getTopicBySubjectIdentifier(loc2)).contains(n));
		transaction.commit();

		assertEquals(0, topic.getNames(type).size());
		assertEquals(topicMap, otherType.getParent());
		assertEquals(1, topic.getNames(otherType).size());
		assertEquals(1, topic.getNames().size());
	}

	public void testOccurrenceModification() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Locator loc2 = createLocator("http://psi.example.org/2");
		Locator loc3 = createLocator("http://psi.example.org/3");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);
		Topic otherType = topicMap.createTopicBySubjectIdentifier(loc2);
		Topic topic = topicMap.createTopicBySubjectIdentifier(loc3);

		ITransaction transaction = topicMap.createTransaction();
		Topic topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		Occurrence o = topic_.createOccurrence(transaction.getTopicBySubjectIdentifier(loc), "Occurrence", new Topic[0]);
		transaction.rollback();
		assertEquals(0, topic.getOccurrences().size());

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		o = topic_.createOccurrence(transaction.getTopicBySubjectIdentifier(loc), "Occurrence", new Topic[0]);
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).contains(o));

		o.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).contains(o));
		transaction.rollback();

		assertEquals(0, topic.getOccurrences(type).size());
		assertEquals(0, topic.getOccurrences(otherType).size());

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		o = topic_.createOccurrence(transaction.getTopicBySubjectIdentifier(loc), "Occurrence", new Topic[0]);
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).contains(o));

		o.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).contains(o));
		o.remove();
		assertEquals(0, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(0, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).size());

		try {
			o.setType(transaction.getTopicBySubjectIdentifier(loc2));
			fail("Construct should be removed!");
		} catch (ConstructRemovedException e) {
			// NOTHING TO DO
		}
		transaction.rollback();

		transaction = topicMap.createTransaction();
		topic_ = transaction.getTopicBySubjectIdentifier(loc3);
		assertEquals(topic, topic_);
		o = topic_.createOccurrence(transaction.getTopicBySubjectIdentifier(loc), "Occurrence", new Topic[0]);
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).contains(o));

		o.setType(transaction.getTopicBySubjectIdentifier(loc2));
		assertEquals(0, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc)).size());
		assertEquals(1, topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).size());
		assertTrue(topic_.getOccurrences(transaction.getTopicBySubjectIdentifier(loc2)).contains(o));
		transaction.commit();

		assertEquals(0, topic.getOccurrences(type).size());
		assertEquals(topicMap, otherType.getParent());
		assertEquals(1, topic.getOccurrences(otherType).size());
		assertEquals(1, topic.getOccurrences().size());
	}

}
