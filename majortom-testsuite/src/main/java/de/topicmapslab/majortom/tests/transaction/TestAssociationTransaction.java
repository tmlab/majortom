package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestAssociationTransaction extends MaJorToMTestCase {

	public void testPlayerModification() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);
		Topic player = topicMap.createTopic();

		Association association = topicMap.createAssociation(type, new Topic[0]);
		Role role = association.createRole(type, player);

		ITransaction transaction = topicMap.createTransaction();
		IAssociation association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		IAssociationRole role_ = transaction.moveToTransactionContext((IAssociationRole) role);
		assertEquals(role, role_);
		ITopic player_ = transaction.moveToTransactionContext((ITopic) player);
		assertEquals(player, player_);

		assertEquals(role_.getPlayer(), player_);
		assertEquals(1, player_.getRolesPlayed().size());
		assertTrue(player_.getRolesPlayed().contains(role_));
		Topic newPlayer = transaction.createTopic();
		role_.setPlayer(newPlayer);
		assertEquals(role_.getPlayer(), newPlayer);
		assertEquals("The origin context should not be modified!", role.getPlayer(), player);
		assertEquals(3, transaction.getTopics().size());
		assertEquals("The origin context should not be modified!", 2, topicMap.getTopics().size());		
		assertEquals(0, player_.getRolesPlayed().size());
		assertEquals(1, newPlayer.getRolesPlayed().size());
		assertTrue(newPlayer.getRolesPlayed().contains(role_));
		
		transaction.rollback();
		
		assertEquals("The origin context should not be modified!", 2, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified!", role.getPlayer(), player);
		
		transaction = topicMap.createTransaction();
		association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(association, association_);
		role_ = transaction.moveToTransactionContext((IAssociationRole) role);
		assertEquals(role, role_);
		player_ = transaction.moveToTransactionContext((ITopic) player);
		assertEquals(player, player_);

		assertEquals(role_.getPlayer(), player_);
		assertEquals(1, player_.getRolesPlayed().size());
		assertTrue(player_.getRolesPlayed().contains(role_));
		newPlayer = transaction.createTopicBySubjectLocator(loc);
		role_.setPlayer(newPlayer);
		assertEquals(role_.getPlayer(), newPlayer);
		assertEquals("The origin context should not be modified!", role.getPlayer(), player);
		assertEquals(3, transaction.getTopics().size());
		assertEquals("The origin context should not be modified!", 2, topicMap.getTopics().size());		
		assertEquals(0, player_.getRolesPlayed().size());
		assertEquals(1, newPlayer.getRolesPlayed().size());
		assertTrue(newPlayer.getRolesPlayed().contains(role_));
		
		transaction.commit();
		
		newPlayer = topicMap.getTopicBySubjectLocator(loc);
		assertNotNull(newPlayer);
		assertEquals("The origin context should be modified!", 3, topicMap.getTopics().size());		
		assertEquals(0, player.getRolesPlayed().size());
		assertEquals(1, newPlayer.getRolesPlayed().size());		
		assertTrue(newPlayer.getRolesPlayed().contains(role));
	}

	public void testRoleCreation() throws Exception {
		Locator loc = createLocator("http://psi.example.org/1");
		Topic type = topicMap.createTopicBySubjectIdentifier(loc);

		Association association = topicMap.createAssociation(type, new Topic[0]);
		ITransaction transaction = topicMap.createTransaction();
		IAssociation association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(1, topicMap.getTopics().size());
		assertEquals(0, association.getRoles().size());
		assertEquals(1, transaction.getTopics().size());
		assertEquals(0, association_.getRoles().size());
		
		association_.createRole(transaction.createTopic(), transaction.createTopic());
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(3, transaction.getTopics().size());
		assertEquals(1, association_.getRoles().size());
		
		Role role = association_.createRole(transaction.createTopic(), transaction.createTopic()); 
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(5, transaction.getTopics().size());
		assertEquals(2, association_.getRoles().size());
		
		role.remove();
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(5, transaction.getTopics().size());
		assertEquals(1, association_.getRoles().size());
		
		transaction.rollback();
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		
		transaction = topicMap.createTransaction();
		association_ = transaction.moveToTransactionContext((IAssociation) association);
		assertEquals(1, topicMap.getTopics().size());
		assertEquals(0, association.getRoles().size());
		assertEquals(1, transaction.getTopics().size());
		assertEquals(0, association_.getRoles().size());
		
		association_.createRole(transaction.createTopic(), transaction.createTopic());
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(3, transaction.getTopics().size());
		assertEquals(1, association_.getRoles().size());
		
		role = association_.createRole(transaction.createTopic(), transaction.createTopic()); 
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(5, transaction.getTopics().size());
		assertEquals(2, association_.getRoles().size());
		
		role.remove();
		assertEquals("The origin context should not be modified",1, topicMap.getTopics().size());
		assertEquals("The origin context should not be modified",0, association.getRoles().size());
		assertEquals(5, transaction.getTopics().size());
		assertEquals(1, association_.getRoles().size());
		
		transaction.commit();
		assertEquals("The origin context should be modified",5, topicMap.getTopics().size());
		assertEquals("The origin context should be modified",1, association.getRoles().size());		
	}

}
