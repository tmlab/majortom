package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestTypeTransaction extends MaJorToMTestCase {

	public void testAssociationModification() throws Exception {
		Locator loc = createLoctor("http://psi.example.org/1");
		Locator loc2 = createLoctor("http://psi.example.org/2");
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
		
		try{
			association.setType(transaction.getTopicBySubjectIdentifier(loc2));
			fail("Construct should be removed!");
		}catch(ConstructRemovedException e){
			//NOTHING TO DO
		}
	}

}
