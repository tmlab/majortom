package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestReificationTransaction extends MaJorToMTestCase {

	public void testAssociationReification() throws Exception {
		_testReifided(createAssociation(createTopic()));
	}

	public void testAssociationRoleReification() throws Exception {
		_testReifided((IAssociationRole) createAssociation(createTopic()).createRole(createTopic(), createTopic()));
	}

	public void testOccurrenceReification() throws Exception {
		_testReifided((IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]));
	}

	public void testNameReification() throws Exception {
		_testReifided((IName) createTopic().createName(createTopic(), "Name", new Topic[0]));
	}

	public void testVariantReification() throws Exception {
		_testReifided((IVariant) createTopic().createName(createTopic(), "Name", new Topic[0]).createVariant("Variant", createTopic()));
	}
	
	public void testTopicMapReification() throws Exception {
		_testReifided(topicMap);
	}

	private void _testReifided(IReifiable reified) throws Exception {
		Locator loc = topicMap.createLocator("http://psi.example.org/reifier");

		ITopic reifier = createTopic();

		ITransaction transaction = topicMap.createTransaction();
		ITopic reifier_ = transaction.moveToTransactionContext(reifier);
		IReifiable reified_ = transaction.moveToTransactionContext(reified);

		reified_.setReifier(reifier_);
		assertNull(reified.getReifier());
		assertNull(reifier.getReified());
		assertNotNull(reified_.getReifier());
		assertNotNull(reifier_.getReified());
		assertEquals(reifier_, reified_.getReifier());
		assertEquals(reified_, reifier_.getReified());
		transaction.rollback();

		assertNull(reified.getReifier());

		transaction = topicMap.createTransaction();
		reifier_ = transaction.moveToTransactionContext(reifier);
		reified_ = transaction.moveToTransactionContext(reified);

		reified_.setReifier(reifier_);
		assertNull(reified.getReifier());
		assertNull(reifier.getReified());
		assertNotNull(reified_.getReifier());
		assertNotNull(reifier_.getReified());
		assertEquals(reifier_, reified_.getReifier());
		assertEquals(reified_, reifier_.getReified());

		reified_.setReifier(null);
		assertNull(reified.getReifier());
		assertNull(reifier.getReified());
		assertNull(reified_.getReifier());
		assertNull(reifier_.getReified());

		transaction.rollback();

		reified.setReifier(reifier);
		transaction = topicMap.createTransaction();
		reifier_ = transaction.moveToTransactionContext(reifier);
		reified_ = transaction.moveToTransactionContext(reified);

		reified_.setReifier(null);
		assertNotNull(reified.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(reifier, reified.getReifier());
		assertEquals(reified, reifier.getReified());
		assertNull(reified_.getReifier());
		assertNull(reifier_.getReified());

		Topic newReifier = transaction.createTopic();
		reified_.setReifier(newReifier);
		assertNotNull(reified.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(reifier, reified.getReifier());
		assertEquals(reified, reifier.getReified());
		assertNotNull(reified_.getReifier());
		assertNotNull(newReifier.getReified());
		assertEquals(newReifier, reified_.getReifier());
		assertEquals(reified_, newReifier.getReified());

		reified_.setReifier(null);
		assertNotNull(reified.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(reifier, reified.getReifier());
		assertEquals(reified, reifier.getReified());
		assertNull(reified_.getReifier());
		assertNull(reifier_.getReified());

		reified.setReifier(reifier);
		transaction = topicMap.createTransaction();
		reifier_ = transaction.moveToTransactionContext(reifier);
		reified_ = transaction.moveToTransactionContext(reified);

		reified_.setReifier(null);
		assertNotNull(reified.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(reifier, reified.getReifier());
		assertEquals(reified, reifier.getReified());
		assertNull(reified_.getReifier());
		assertNull(reifier_.getReified());

		newReifier = transaction.createTopicBySubjectIdentifier(loc);
		reified_.setReifier(newReifier);
		assertNotNull(reified.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(reifier, reified.getReifier());
		assertEquals(reified, reifier.getReified());
		assertNotNull(reified_.getReifier());
		assertNotNull(newReifier.getReified());
		assertEquals(newReifier, reified_.getReifier());
		assertEquals(reified_, newReifier.getReified());

		transaction.commit();

		try {
			reified_.getReifier();
			fail("transaction should be close!");
		} catch (TransactionException e) {
			// NOTHING TO DO
		}

		assertNotNull(reified.getReifier());
		assertNull(reifier.getReified());
		Topic t = topicMap.getTopicBySubjectIdentifier(loc);
		assertNotNull(t);
		assertNotNull(t.getReified());
		assertEquals(t, reified.getReifier());
		assertEquals(reified, t.getReified());
	}

}
