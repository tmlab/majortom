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
package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestIdentityTransaction extends MaJorToMTestCase {

	public void testIdentityTransactions() throws Exception {
		if (topicMap.getStore().isTransactable()) {
			ITransaction transaction = topicMap.createTransaction();
			assertNotNull(transaction);

			Topic topic = transaction.createTopic();
			assertTrue(topic.getTopicMap().equals(transaction));

			final String si = "http://psi.example.org/si";
			final String si_2 = "http://psi.example.org/si/2";
			final String ii = "http://psi.example.org/ii";
			final String ii_2 = "http://psi.example.org/ii/2";
			final String sl = "http://psi.example.org/sl";
			final String sl_2 = "http://psi.example.org/sl/2";

			topic.addSubjectIdentifier(transaction.createLocator(si));
			topic.addSubjectIdentifier(transaction.createLocator(si_2));
			assertEquals(2, topic.getSubjectIdentifiers().size());
			topic.removeSubjectIdentifier(transaction.createLocator(si_2));
			assertEquals(1, topic.getSubjectIdentifiers().size());

			topic.addSubjectLocator(transaction.createLocator(sl));
			topic.addSubjectLocator(transaction.createLocator(sl_2));
			assertEquals(2, topic.getSubjectLocators().size());
			topic.removeSubjectLocator(transaction.createLocator(sl_2));
			assertEquals(1, topic.getSubjectLocators().size());

			topic.addItemIdentifier(transaction.createLocator(ii));
			topic.addItemIdentifier(transaction.createLocator(ii_2));
			assertEquals(3, topic.getItemIdentifiers().size());
			topic.removeItemIdentifier(transaction.createLocator(ii_2));
			assertEquals(2, topic.getItemIdentifiers().size());

			assertEquals(0, topicMap.getTopics().size());
			assertEquals(1, transaction.getTopics().size());

			transaction.commit();

			assertEquals(1, topicMap.getTopics().size());
			try {
				transaction.getTopics().size();
				fail("Transaction should be closed!");
			} catch (TransactionException e) {
				// NOTHING TO DO
			}

			Topic topicBySI = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(si));
			assertNotNull(topicBySI);
			assertNull(topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(si_2)));
			assertTrue(topicBySI.getParent().equals(topicMap));

			Topic topicBySL = topicMap.getTopicBySubjectLocator(topicMap.createLocator(sl));
			assertNotNull(topicBySL);
			assertNull(topicMap.getTopicBySubjectLocator(topicMap.createLocator(sl_2)));
			assertTrue(topicBySL.getParent().equals(topicMap));

			Topic topicByII = (Topic) topicMap.getConstructByItemIdentifier(topicMap.createLocator(ii));
			assertNotNull(topicByII);
			assertNull(topicMap.getConstructByItemIdentifier(topicMap.createLocator(ii_2)));
			assertTrue(topicByII.getParent().equals(topicMap));

			topic = topicMap.getTopics().iterator().next();
			assertNotNull(topic);
			assertTrue(topic.getParent().equals(topicMap));
			assertEquals(1, topic.getSubjectIdentifiers().size());
			assertEquals(1, topic.getSubjectLocators().size());
			assertEquals(2, topic.getItemIdentifiers().size());

			assertEquals(topicBySI, topic);
			assertEquals(topicBySL, topic);
			assertEquals(topicByII, topic);
		}
	}

	public void testRemoveTransactions() throws Exception {
		if (topicMap.getStore().isTransactable()) {
			ITransaction transaction = topicMap.createTransaction();
			assertNotNull(transaction);

			Topic topic = transaction.createTopic();
			assertTrue(topic.getTopicMap().equals(transaction));

			final String si = "http://psi.example.org/si";
			final String si_2 = "http://psi.example.org/si_2";
			final String ii = "http://psi.example.org/ii";
			final String ii_2 = "http://psi.example.org/ii_2";
			final String sl = "http://psi.example.org/sl";
			final String sl_2 = "http://psi.example.org/sl_2";

			topic.addSubjectIdentifier(transaction.createLocator(si));
			topic.addSubjectIdentifier(transaction.createLocator(si_2));
			assertEquals(2, topic.getSubjectIdentifiers().size());
			topic.removeSubjectIdentifier(transaction.createLocator(si_2));
			assertEquals(1, topic.getSubjectIdentifiers().size());

			topic.addSubjectLocator(transaction.createLocator(sl));
			topic.addSubjectLocator(transaction.createLocator(sl_2));
			assertEquals(2, topic.getSubjectLocators().size());
			topic.removeSubjectLocator(transaction.createLocator(sl_2));
			assertEquals(1, topic.getSubjectLocators().size());

			topic.addItemIdentifier(transaction.createLocator(ii));
			topic.addItemIdentifier(transaction.createLocator(ii_2));
			assertEquals(3, topic.getItemIdentifiers().size());
			topic.removeItemIdentifier(transaction.createLocator(ii_2));
			assertEquals(2, topic.getItemIdentifiers().size());

			assertEquals(0, topicMap.getTopics().size());
			assertEquals(1, transaction.getTopics().size());

			topic.remove();

			transaction.commit();

			assertEquals(0, topicMap.getTopics().size());
			try {
				transaction.getTopics().size();
				fail("Transaction should be closed!");
			} catch (TransactionException e) {
				// NOTHING TO DO
			}

			Topic topicBySI = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(si));
			assertNull(topicBySI);
			assertNull(topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(si_2)));

			Topic topicBySL = topicMap.getTopicBySubjectLocator(topicMap.createLocator(sl));
			assertNull(topicBySL);
			assertNull(topicMap.getTopicBySubjectLocator(topicMap.createLocator(sl_2)));

			Topic topicByII = (Topic) topicMap.getConstructByItemIdentifier(topicMap.createLocator(ii));
			assertNull(topicByII);
			assertNull(topicMap.getConstructByItemIdentifier(topicMap.createLocator(ii_2)));

		}
	}

	public void testTopicMap() throws Exception {
		_testItemIdentifier(topicMap);
	}

	public void testOccurrence() throws Exception {
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "val", new Topic[0]);
		_testItemIdentifier(occurrence);
		_testRemoveTransaction(occurrence);
	}

	public void testName() throws Exception {
		IName name = (IName) createTopic().createName("val", new Topic[0]);
		_testItemIdentifier(name);
		_testRemoveTransaction(name);
	}

	public void testVariant() throws Exception {
		IVariant variant = (IVariant) createTopic().createName("val", new Topic[0]).createVariant("val", createTopic());
		_testItemIdentifier(variant);
		_testRemoveTransaction(variant);
	}

	public void testAssociation() throws Exception {
		IAssociation association = createAssociation(createTopic());
		_testItemIdentifier(association);
		_testRemoveTransaction(association);
	}

	public void testRole() throws Exception {
		IAssociationRole role = (IAssociationRole) createAssociation(createTopic()).createRole(createTopic(), createTopic());
		_testItemIdentifier(role);
		_testRemoveTransaction(role);
	}

	public void _testRemoveTransaction(IConstruct construct) throws Exception {
		Locator loc = topicMap.createLocator("http://psi.example.org");
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");
		construct.addItemIdentifier(loc);

		ITransaction transaction = topicMap.createTransaction();
		IConstruct construct_ = transaction.moveToTransactionContext(construct);
		assertEquals(construct, construct_);

		construct_.remove();
		assertNull(transaction.getConstructByItemIdentifier(loc));
		try {
			construct_.addItemIdentifier(loc2);
			fail("Construct should be removed");
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		assertNotNull(topicMap.getConstructByItemIdentifier(loc));

		transaction.rollback();

		transaction = topicMap.createTransaction();
		construct_ = transaction.moveToTransactionContext(construct);
		assertEquals(construct, construct_);

		construct_.remove();
		assertNull(transaction.getConstructByItemIdentifier(loc));
		try {
			construct_.addItemIdentifier(loc2);
			fail("Construct should be removed");
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		assertNotNull(topicMap.getConstructByItemIdentifier(loc));

		transaction.commit();

		assertNull(topicMap.getConstructByItemIdentifier(loc));
	}

	public void _testItemIdentifier(IConstruct construct) throws Exception {
		Locator loc1 = topicMap.createLocator("http://psi.example.org/ii/1");
		Locator loc2 = topicMap.createLocator("http://psi.example.org/ii/2");
		Locator loc3 = topicMap.createLocator("http://psi.example.org/ii/3");

		ITransaction transaction = topicMap.createTransaction();
		IConstruct construct_ = transaction.moveToTransactionContext(construct);
		if (!(construct instanceof ITopicMap)) {
			assertEquals(construct, construct_);
		}

		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNull(transaction.getConstructByItemIdentifier(loc1));
		assertNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(0, construct_.getItemIdentifiers().size());

		construct_.addItemIdentifier(loc1);
		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNotNull(transaction.getConstructByItemIdentifier(loc1));
		assertNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(1, construct_.getItemIdentifiers().size());
		assertTrue(construct_.getItemIdentifiers().contains(loc1));

		construct_.addItemIdentifier(loc2);
		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNotNull(transaction.getConstructByItemIdentifier(loc1));
		assertNotNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(2, construct_.getItemIdentifiers().size());
		assertTrue(construct_.getItemIdentifiers().contains(loc1));
		assertTrue(construct_.getItemIdentifiers().contains(loc2));

		transaction.rollback();

		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());

		transaction = topicMap.createTransaction();
		construct_ = transaction.moveToTransactionContext(construct);
		if (!(construct instanceof ITopicMap)) {
			assertEquals(construct, construct_);
		}

		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNull(transaction.getConstructByItemIdentifier(loc1));
		assertNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(0, construct_.getItemIdentifiers().size());

		construct_.addItemIdentifier(loc1);
		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNotNull(transaction.getConstructByItemIdentifier(loc1));
		assertNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(1, construct_.getItemIdentifiers().size());
		assertTrue(construct_.getItemIdentifiers().contains(loc1));

		construct_.addItemIdentifier(loc2);
		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNotNull(transaction.getConstructByItemIdentifier(loc1));
		assertNotNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(2, construct_.getItemIdentifiers().size());
		assertTrue(construct_.getItemIdentifiers().contains(loc1));
		assertTrue(construct_.getItemIdentifiers().contains(loc2));

		construct_.removeItemIdentifier(loc2);

		assertNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertNotNull(transaction.getConstructByItemIdentifier(loc1));
		assertNull(transaction.getConstructByItemIdentifier(loc2));
		assertNull(transaction.getConstructByItemIdentifier(loc3));
		assertEquals(0, construct.getItemIdentifiers().size());
		assertEquals(1, construct_.getItemIdentifiers().size());
		assertTrue(construct_.getItemIdentifiers().contains(loc1));

		transaction.commit();

		assertNotNull(topicMap.getConstructByItemIdentifier(loc1));
		assertNull(topicMap.getConstructByItemIdentifier(loc2));
		assertNull(topicMap.getConstructByItemIdentifier(loc3));
		assertEquals(1, construct.getItemIdentifiers().size());
		assertTrue(construct.getItemIdentifiers().contains(loc1));
	}

	public void testTopicMerge() throws Exception {
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");

		ITopic topic = createTopic();
		String id = topic.getId();
		ITopic otherTopic = createTopic();

		ITransaction transaction = topicMap.createTransaction();
		ITopic topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		ITopic otherTopic_ = transaction.moveToTransactionContext(otherTopic);
		assertEquals(otherTopic, otherTopic_);
		
		topic_.addSubjectIdentifier(loc1);		
		otherTopic_.addSubjectIdentifier(loc2);
		
		otherTopic_.createName("Name", new Topic[0]);
		assertEquals(0, topic_.getNames().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		assertEquals(id, topic_.getId());
		topic_.mergeIn(otherTopic_);
		assertEquals(id, topic.getId());
		assertEquals(1, topic_.getNames().size());
		assertEquals(2, topic_.getSubjectIdentifiers().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		transaction.rollback();
		assertEquals(id, topic.getId());
		
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		assertEquals(0, topic.getSubjectIdentifiers().size());
		assertEquals(0, otherTopic.getSubjectIdentifiers().size());
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		otherTopic_ = transaction.moveToTransactionContext(otherTopic);
		assertEquals(otherTopic, otherTopic_);
		
		topic_.addSubjectIdentifier(loc1);		
		otherTopic_.addSubjectIdentifier(loc2);
		
		assertEquals(0, topic_.getNames().size());
		otherTopic_.createName("Name", new Topic[0]);
		assertEquals(0, topic_.getNames().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		assertEquals(id, topic_.getId());
		topic_.addSubjectIdentifier(loc2);
		assertEquals(id, topic.getId());
		topic_ = (ITopic)transaction.getTopicBySubjectIdentifier(loc1);
		assertEquals(1, topic_.getNames().size());
		assertEquals(2, topic_.getSubjectIdentifiers().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		transaction.rollback();
		assertEquals(id, topic.getId());
		
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		assertEquals(0, topic.getSubjectIdentifiers().size());
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(id, topic.getId());
		assertEquals(topic, topic_);
		assertEquals(id, topic_.getId());
		otherTopic_ = transaction.moveToTransactionContext(otherTopic);
		assertEquals(otherTopic, otherTopic_);
		
		topic_.addSubjectIdentifier(loc1);		
		otherTopic_.addSubjectIdentifier(loc2);
		
		assertEquals(0, topic_.getNames().size());
		otherTopic_.createName("Name", new Topic[0]);
		assertEquals(0, topic_.getNames().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		topic_.addSubjectIdentifier(loc2);
		topic_ = (ITopic)transaction.getTopicBySubjectIdentifier(loc1);
		assertEquals(1, topic_.getNames().size());
		assertEquals(2, topic_.getSubjectIdentifiers().size());
		assertEquals(1, otherTopic_.getNames().size());
		assertEquals(0, topic.getNames().size());
		assertEquals(0, otherTopic.getNames().size());
		
		transaction.commit();
		topic = (ITopic)topicMap.getTopicBySubjectIdentifier(loc1);
		assertNotNull(topic);
		assertEquals(1, topic.getNames().size());
		assertEquals(2, topic.getSubjectIdentifiers().size());
		assertTrue(topic.getSubjectIdentifiers().contains(loc1));
		assertTrue(topic.getSubjectIdentifiers().contains(loc2));
		
		
	}

}
