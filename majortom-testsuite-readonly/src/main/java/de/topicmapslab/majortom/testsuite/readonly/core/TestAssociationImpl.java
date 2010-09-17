/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IScope;
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
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testSetType/asstype)
	 * 
	 * Topic (http://TestAssociationImpl/testSetType/newtype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetType() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testSetType/asstype"));
		assertNotNull(asstype);
		
		ITopic newtype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testSetType/newtype"));
		assertNotNull(newtype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		ass.setType(newtype);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getType()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetType/asstype)
	 */
	@Test
	public void testGetType() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetType/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		assertEquals(asstype, ass.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetRolesIntInt() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getRoles(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetRolesIntIntComparatorOfRole() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.AssociationImpl#getNumberOfRoles()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfRoles() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetScopeObject/asstype)
	 * with a single theme scope (http://TestAssociationImpl/testGetScopeObject/theme)
	 */
	@Test
	public void testGetScopeObject() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetScopeObject/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetScopeObject/theme"));
		assertNotNull(theme);
		
		IScope scope = ass.getScopeObject();
		assertNotNull(scope);
		assertEquals(1, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#addTheme(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testAddTheme/asstype)
	 * 
	 * topic (http://TestAssociationImpl/testAddTheme/theme)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTheme() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testAddTheme/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testAddTheme/theme"));
		assertNotNull(theme);
		
		ass.addTheme(theme);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScope()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetScope/asstype)
	 * with a single theme scope (http://TestAssociationImpl/testGetScope/theme)
	 */
	@Test
	public void testGetScope() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetScope/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetScope/theme"));
		assertNotNull(theme);
		
		assertEquals(1, ass.getScope().size());
		assertTrue(ass.getScope().contains(theme));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#removeTheme(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testRemoveTheme/asstype)
	 * with a single theme scope (http://TestAssociationImpl/testRemoveTheme/theme)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveTheme() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testRemoveTheme/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testRemoveTheme/theme"));
		assertNotNull(theme);

		assertTrue(ass.getScope().contains(theme));
		ass.removeTheme(theme);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetReifier/asstype1) 
	 * with an reifier
	 * and exactly one association of type (http://TestAssociationImpl/testGetReifier/asstype2)
	 * without reifier 
	 */
	@Test
	public void testGetReifier() {

		assertNotNull(map);
		
		ITopic asstype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetReifier/asstype1"));
		assertNotNull(asstype1);
		
		ITopic asstype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetReifier/asstype2"));
		assertNotNull(asstype2);
		
		assertEquals(1, map.getAssociations(asstype1).size());
		IAssociation ass1 = (IAssociation)map.getAssociations(asstype1).iterator().next();
		
		assertEquals(1, map.getAssociations(asstype2).size());
		IAssociation ass2 = (IAssociation)map.getAssociations(asstype2).iterator().next();
		
		assertNotNull(ass1.getReifier());
		assertNull(ass2.getReifier());

	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testSetReifier/asstype) 
	 * without an reifier
	 * 
	 * topic (http://TestAssociationImpl/testSetReifier/ref) 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetReifier() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testSetReifier/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ITopic ref = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testSetReifier/ref"));
		assertNotNull(ref);
		
		assertNull(ass.getReifier());
		ass.setReifier(ref);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testGetTopicMap/asstype) 
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetTopicMap/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		assertEquals(map, ass.getTopicMap());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testAddItemIdentifier/asstype) 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testAddItemIdentifier/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		Locator l = map.createLocator("http://TestAssociationImpl/testAddItemIdentifier");
		assertNotNull(l);
		
		ass.addItemIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 * 
	 *  it exist exactly one association of type (http://TestAssociationImpl/testGetId/asstype) 
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testGetId/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		String id = ass.getId();
		assertNotNull(id);
		assertEquals(id, ass.getId());
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
	 * it exist exactly one association of type (http://TestAssociationImpl/testRemove/asstype) 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testRemove/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		ass.remove();
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 * 
	 * it exist exactly one association of type (http://TestAssociationImpl/testRemoveBoolean/asstype) 
	 */
	@Test
	public void testRemoveBoolean() {

		assertNotNull(map);
		
		ITopic asstype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestAssociationImpl/testRemoveBoolean/asstype"));
		assertNotNull(asstype);
		
		assertEquals(1, map.getAssociations(asstype).size());
		IAssociation ass = (IAssociation)map.getAssociations(asstype).iterator().next();
		
		try{
			ass.remove(true);
			fail("No Exception thrown");
			
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
		try{
			ass.remove(false);
			fail("No Exception thrown");
			
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
