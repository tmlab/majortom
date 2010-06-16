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

import org.tmapi.core.Topic;

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

}
