/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


public class TestAssociationImpl extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#createRole(org.tmapi.core.Topic, org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testCreateRole/asstype)
	 * 
	 * Topic (http://TestAssociationImpl/testCreateRole/topic/1)
	 * Topic (http://TestAssociationImpl/testCreateRole/roletype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateRole() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testCreateRole/asstype"));
		assertNotNull(asstype);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testCreateRole/topic/1"));
		assertNotNull(topic);
		
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testCreateRole/roletype"));
		assertNotNull(roletype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ass.createRole(roletype, topic);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getParent()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetParent/asstype)
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetParent/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		assertEquals(map, ass.getParent());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoleTypes()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetRoleTypes/asstype)
	 * which has two roles of type (http://TestAssociationImpl/testGetRoleTypes/roletype)
	 */
	@Test
	public void testGetRoleTypes() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetRoleTypes/asstype"));
		assertNotNull(asstype);
		
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetRoleTypes/roletype"));
		assertNotNull(roletype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		assertEquals(2, ass.getRoles().size());
		assertEquals(1, ass.getRoleTypes().size());
		assertEquals(roletype, ass.getRoleTypes().iterator().next());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetRoles/asstype)
	 * which has two roles
	 */
	@Test
	public void testGetRoles() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetRoles/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		Set<Role> roles = ass.getRoles();
		assertNotNull(roles);
		assertEquals(2, roles.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetRolesTopic/asstype)
	 * which has two roles, one of type (http://TestAssociationImpl/testGetRolesTopic/roletype)
	 */
	@Test
	public void testGetRolesTopic() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetRolesTopic/asstype"));
		assertNotNull(asstype);
		
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetRolesTopic/roletype"));
		assertNotNull(roletype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		assertEquals(2, ass.getRoles().size());
		assertEquals(1, ass.getRoles(roletype).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#setType(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testSetType() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getType()}.
	 */
	@Test
	public void testGetType() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles(int, int)}.
	 */
	@Test
	public void testGetRolesIntInt() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles(int, int, java.util.Comparator)}.
	 */
	@Test
	public void testGetRolesIntIntComparatorOfRole() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getNumberOfRoles()}.
	 */
	@Test
	public void testGetNumberOfRoles() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 */
	@Test
	public void testGetScopeObject() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#addTheme(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testAddTheme() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScope()}.
	 */
	@Test
	public void testGetScope() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#removeTheme(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testRemoveTheme() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	@Test
	public void testGetReifier() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testSetReifier() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 */
	@Test
	public void testGetTopicMap() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testAddItemIdentifier() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 */
	@Test
	public void testGetId() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	@Test
	public void testGetItemIdentifiers() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 */
	@Test
	public void testRemove() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 */
	@Test
	public void testRemoveBoolean() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testRemoveItemIdentifier() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getParent()}.
	 */
	@Test
	public void testGetParent1() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getIdentity()}.
	 */
	@Test
	public void testGetIdentity() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#compareTo(de.topicmapslab.majortom.model.core.IConstruct)}.
	 */
	@Test
	public void testCompareTo() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#isRemoved()}.
	 */
	@Test
	public void testIsRemoved() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#setRemoved(boolean)}.
	 */
	@Test
	public void testSetRemoved() {

		fail("Not yet implemented");
	}
}
