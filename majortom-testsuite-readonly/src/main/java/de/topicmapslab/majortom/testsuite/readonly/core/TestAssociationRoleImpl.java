/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import org.junit.Test;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;



public class TestAssociationRoleImpl extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationRoleImpl#getParent()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testGetParent/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testGetParent/roletype)
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetParent/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetParent/roletype"));
		assertNotNull(roletype);
		
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		assertEquals(ass, role.getParent());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationRoleImpl#getPlayer()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testGetPlayer/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testGetPlayer/roletype)
	 * which player is topic (http://TestAssociationRoleImpl/testGetPlayer/topic/1)
	 */
	@Test
	public void testGetPlayer() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetPlayer/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetPlayer/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetPlayer/topic/1"));
		assertNotNull(topic);
		
		assertNotNull(role.getPlayer());
		assertEquals(topic, role.getPlayer());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationRoleImpl#setPlayer(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testSetPlayer/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testSetPlayer/roletype)
	 * 
	 * topic (http://TestAssociationRoleImpl/testGetPlayer/newplayer)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetPlayer() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetPlayer/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetPlayer/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		ITopic newplayer = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetPlayer/newplayer"));
		assertNotNull(newplayer);
		
		role.setPlayer(newplayer);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationRoleImpl#setType(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testSetType/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testSetType/roletype)
	 * 
	 * topic (http://TestAssociationRoleImpl/testSetType/newtype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetType() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetType/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetType/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		ITopic newtype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetType/newtype"));
		assertNotNull(newtype);
		
		role.setType(newtype);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationRoleImpl#getType()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testGetType/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testGetType/roletype)
	 */
	@Test
	public void testGetType() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetType/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetType/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		assertEquals(roletype, role.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	@Test
	public void testGetReifier() {

		/// implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testSetReifier/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testSetReifier/roletype) without reifier
	 * 
	 * topic (http://TestAssociationRoleImpl/testSetReifier/ref)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetReifier() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetReifier/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetReifier/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		ITopic ref = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testSetReifier/ref"));
		assertNotNull(ref);
		
		assertNull(role.getReifier());
		role.setReifier(ref);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testGetTopicMap/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testGetTopicMap/roletype)
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetTopicMap/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetTopicMap/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		assertEquals(map, role.getTopicMap());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testAddItemIdentifier/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testAddItemIdentifier/roletype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testAddItemIdentifier/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testAddItemIdentifier/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		Locator l = map.createLocator("http://TestAssociationRoleImpl/testAddItemIdentifier");
		assertNotNull(l);
		role.addItemIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testGetId/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testGetId/roletype)
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetId/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testGetId/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		String id = role.getId();
		assertNotNull(id);
		assertEquals(id, role.getId());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	@Test
	public void testGetItemIdentifiers() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testRemove/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testRemove/roletype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testRemove/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testRemove/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		role.remove();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationRoleImpl/testRemove/asstype)
	 * which has exactly one role of type (http://TestAssociationRoleImpl/testRemove/roletype)
	 */
	@Test
	public void testRemoveBoolean() {

		assertNotNull(map);
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testRemoveBoolean/asstype"));
		assertNotNull(asstype);
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationRoleImpl/testRemoveBoolean/roletype"));
		assertNotNull(roletype);
		assertEquals(1, ass.getRoles(roletype).size());
		IAssociationRole role = (IAssociationRole)ass.getRoles(roletype).iterator().next();
		
		try{
			role.remove(true);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
		try{
			role.remove(false);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testRemoveItemIdentifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getIdentity()}.
//	 */
//	@Test
//	public void testGetIdentity() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#compareTo(de.topicmapslab.majortom.model.core.IConstruct)}.
//	 */
//	@Test
//	public void testCompareTo() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#equals(java.lang.Object)}.
//	 */
//	@Test
//	public void testEqualsObject() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#isRemoved()}.
//	 */
//	@Test
//	public void testIsRemoved() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#setRemoved(boolean)}.
//	 */
//	@Test
//	public void testSetRemoved() {
//
//		fail("Not yet implemented");
//	}
}
