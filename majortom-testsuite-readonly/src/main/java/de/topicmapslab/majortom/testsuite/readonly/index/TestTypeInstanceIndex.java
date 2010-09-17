/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.index;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;



public class TestTypeInstanceIndex extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getAssociations(java.util.Collection)}.
	 * 
	 * TopicMap has 1 association of type (http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype1)
	 * 2 associations of type (http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype2)
	 * and no associations of type (http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype3)
	 * 
	 */
	@Test
	public void testGetAssociationsCollectionOfQextendsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic asstype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype1"));
		assertNotNull(asstype1);
		
		ITopic asstype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype2"));
		assertNotNull(asstype2);
		
		ITopic asstype3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsCollectionOfQextendsTopic/asstype3"));
		assertNotNull(asstype3);
		
		Collection<Topic> t1 = new HashSet<Topic>();
		t1.add(asstype1);
		
		Collection<ITopic> t2 = new HashSet<ITopic>();
		t2.add(asstype2);
		
		Collection<Topic> t3 = new HashSet<Topic>();
		t3.add(asstype3);
		
		assertEquals(1, index.getAssociations(t1).size());
		for(Association a:index.getAssociations(t1))
			assertEquals(asstype1, a.getType());
		assertEquals(2, index.getAssociations(t2).size());
		for(Association a:index.getAssociations(t2))
			assertEquals(asstype2, a.getType());
		assertEquals(0, index.getAssociations(t3).size());
		
		Collection<Topic> t1_2 = new HashSet<Topic>();
		t1_2.add(asstype1);
		t1_2.add(asstype2);
		
		assertEquals(3, index.getAssociations(t1_2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic)}.
	 * 
	 * TopicMap has 2 associations of type (http://TestTypeInstanceIndex/testGetAssociationsTopic/asstype1)
	 */
	@Test
	public void testGetAssociationsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic asstype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsTopic/asstype1"));
		assertNotNull(asstype1);
		
		Collection<Association> ass = index.getAssociations(asstype1);
		assertNotNull(ass);
		assertEquals(2, ass.size());
		
		for(Association a:ass)
			assertEquals(asstype1, a.getType());
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getAssociations(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has 1 association of type (http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype1)
	 * 2 associations of type (http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype2)
	 * and no associations of type (http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype3)
	 */
	@Test
	public void testGetAssociationsTopicArray() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic asstype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype1"));
		assertNotNull(asstype1);
		
		ITopic asstype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype2"));
		assertNotNull(asstype2);
		
		ITopic asstype3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetAssociationsTopicArray/asstype3"));
		assertNotNull(asstype3);
		
		assertEquals(1, index.getAssociations(asstype1).size());
		for(Association a:index.getAssociations(asstype1))
			assertEquals(asstype1, a.getType());
		assertEquals(2, index.getAssociations(asstype2).size());
		for(Association a:index.getAssociations(asstype2))
			assertEquals(asstype2, a.getType());
		assertEquals(0, index.getAssociations(asstype3).size());
		
		assertEquals(3, index.getAssociations(asstype1, asstype2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getAssociationTypes()}.
	 * 
	 * 
	 * 
	 */
	@Test
	public void testGetAssociationTypes() {

		/// implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getCharacteristics(java.util.Collection)}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetAssociationTypes/nametype1)
	 * and 2 occurrences of type (http://TestTypeInstanceIndex/testGetAssociationTypes/occtype1)
	 */
	@Test
	public void testGetCharacteristicsCollectionOfQextendsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic nametype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetCharacteristicsCollectionOfQextendsTopic/nametype1"));
		assertNotNull(nametype1);
		
		ITopic occtype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetCharacteristicsCollectionOfQextendsTopic/occtype1"));
		assertNotNull(occtype1);
		
		Collection<Topic> nt = new HashSet<Topic>();
		nt.add(nametype1);
		
		Collection<ITopic> ot = new HashSet<ITopic>();
		ot.add(occtype1);
		
		assertEquals(1, index.getCharacteristics(nt).size());
		for(ICharacteristics c:index.getCharacteristics(nt))
			assertEquals(nametype1, c.getType());
		
		assertEquals(2, index.getCharacteristics(ot).size());
		for(ICharacteristics c:index.getCharacteristics(ot))
			assertEquals(occtype1, c.getType());
		
		Collection<Topic> t = new HashSet<Topic>();
		t.add(nametype1);
		t.add(occtype1);
		
		assertEquals(3, index.getCharacteristics(t).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getCharacteristics(org.tmapi.core.Topic)}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetCharacteristicsTopic/type)
	 * and 2 occurrences of type type (http://TestTypeInstanceIndex/testGetCharacteristicsTopic/type)
	 */
	@Test
	public void testGetCharacteristicsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetCharacteristicsTopic/type"));
		assertNotNull(type);
		
		assertEquals(3, index.getCharacteristics(type).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getCharacteristics(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetCharacteristicsTopicArray/nametype1)
	 * and 2 occurrences of type (http://TestTypeInstanceIndex/testGetCharacteristicsTopicArray/occtype1)
	 */
	@Test
	public void testGetCharacteristicsTopicArray() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic nametype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetCharacteristicsTopicArray/nametype1"));
		assertNotNull(nametype1);
		
		ITopic occtype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetCharacteristicsTopicArray/occtype1"));
		assertNotNull(occtype1);
		
		assertEquals(1, index.getCharacteristics(nametype1).size());
		for(ICharacteristics c:index.getCharacteristics(nametype1))
			assertEquals(nametype1, c.getType());
		
		assertEquals(2, index.getCharacteristics(occtype1).size());
		for(ICharacteristics c:index.getCharacteristics(occtype1))
			assertEquals(occtype1, c.getType());

		assertEquals(3, index.getCharacteristics(nametype1, occtype1).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getCharacteristicTypes()}.
	 */
	@Test
	public void testGetCharacteristicTypes() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getNames(java.util.Collection)}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetNamesCollectionOfQextendsTopic/nametype1)
	 * and 2 names of type (http://TestTypeInstanceIndex/testGetNamesCollectionOfQextendsTopic/nametype2)
	 */
	@Test
	public void testGetNamesCollectionOfQextendsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic nametype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesCollectionOfQextendsTopic/nametype1"));
		assertNotNull(nametype1);
		
		ITopic nametype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesCollectionOfQextendsTopic/nametype2"));
		assertNotNull(nametype2);
		
		Collection<Topic> nt1 = new HashSet<Topic>();
		nt1.add(nametype1);
		
		Collection<ITopic> nt2 = new HashSet<ITopic>();
		nt2.add(nametype2);
		
		assertEquals(1, index.getNames(nt1).size());
		for(Name n:index.getNames(nt1))
			assertEquals(nametype1, n.getType());
		
		assertEquals(2, index.getNames(nt2).size());
		for(Name n:index.getNames(nt2))
			assertEquals(nametype2, n.getType());
		
		Collection<Topic> t = new HashSet<Topic>();
		t.add(nametype1);
		t.add(nametype2);
		
		assertEquals(3, index.getNames(t).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getNames(org.tmapi.core.Topic)}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetNamesTopic/nametype1)
	 * and 2 names of type (http://TestTypeInstanceIndex/testGetNamesTopic/nametype2)
	 */
	@Test
	public void testGetNamesTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic nametype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesTopic/nametype1"));
		assertNotNull(nametype1);
		
		ITopic nametype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesTopic/nametype2"));
		assertNotNull(nametype2);
		
		assertEquals(1, index.getNames(nametype1).size());
		for(Name n:index.getNames(nametype1))
			assertEquals(nametype1, n.getType());
		
		assertEquals(2, index.getNames(nametype2).size());
		for(Name n:index.getNames(nametype2))
			assertEquals(nametype2, n.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getNames(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has 1 name of type (http://TestTypeInstanceIndex/testGetNamesTopicArray/nametype1)
	 * and 2 names of type (http://TestTypeInstanceIndex/testGetNamesTopicArray/nametype2)
	 */
	@Test
	public void testGetNamesTopicArray() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic nametype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesTopicArray/nametype1"));
		assertNotNull(nametype1);
		
		ITopic nametype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetNamesTopicArray/nametype2"));
		assertNotNull(nametype2);
		
		assertEquals(1, index.getNames(nametype1).size());
		for(Name n:index.getNames(nametype1))
			assertEquals(nametype1, n.getType());
		
		assertEquals(2, index.getNames(nametype2).size());
		for(Name n:index.getNames(nametype2))
			assertEquals(nametype2, n.getType());
		
		assertEquals(3, index.getNames(nametype1, nametype2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getNameTypes()}.
	 */
	@Test
	public void testGetNameTypes() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getOccurrences(java.util.Collection)}.
	 * 
	 * TopicMap has 1 occurrence of type (http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype1)
	 * and 2 occurrences of type (http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype2)
	 */
	@Test
	public void testGetOccurrencesCollectionOfQextendsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic occtype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype1"));
		assertNotNull(occtype1);
		
		ITopic occtype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype2"));
		assertNotNull(occtype2);
		
		Collection<Topic> t1 = new HashSet<Topic>();
		t1.add(occtype1);
		
		Collection<ITopic> t2 = new HashSet<ITopic>();
		t2.add(occtype2);
		
		assertEquals(1, index.getOccurrences(t1).size());
		for(Occurrence o:index.getOccurrences(t1))
			assertEquals(occtype1, o.getType());
		
		assertEquals(2, index.getOccurrences(t2).size());
		for(Occurrence o:index.getOccurrences(t2))
			assertEquals(occtype2, o.getType());
		
		Collection<Topic> t = new HashSet<Topic>();
		t.add(occtype1);
		t.add(occtype2);
		
		assertEquals(3, index.getOccurrences(t).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic)}.
	 * 
	 * TopicMap has 1 occurrence of type (http://TestTypeInstanceIndex/testGetOccurrencesTopic/occtype1)
	 * and 2 occurrences of type (http://TestTypeInstanceIndex/testGetOccurrencesTopic/occtype2)
	 */
	@Test
	public void testGetOccurrencesTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic occtype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype1"));
		assertNotNull(occtype1);
		
		ITopic occtype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesCollectionOfQextendsTopic/occtype2"));
		assertNotNull(occtype2);
		
		assertEquals(1, index.getOccurrences(occtype1).size());
		for(Occurrence o:index.getOccurrences(occtype1))
			assertEquals(occtype1, o.getType());
		
		assertEquals(2, index.getOccurrences(occtype2).size());
		for(Occurrence o:index.getOccurrences(occtype2))
			assertEquals(occtype2, o.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getOccurrences(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has 1 occurrence of type (http://TestTypeInstanceIndex/testGetOccurrencesTopicArray/occtype1)
	 * and 2 occurrences of type (http://TestTypeInstanceIndex/testGetOccurrencesTopicArray/occtype2)
	 */
	@Test
	public void testGetOccurrencesTopicArray() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic occtype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesTopicArray/occtype1"));
		assertNotNull(occtype1);
		
		ITopic occtype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetOccurrencesTopicArray/occtype2"));
		assertNotNull(occtype2);
		
		assertEquals(1, index.getOccurrences(occtype1).size());
		for(Occurrence o:index.getOccurrences(occtype1))
			assertEquals(occtype1, o.getType());
		
		assertEquals(2, index.getOccurrences(occtype2).size());
		for(Occurrence o:index.getOccurrences(occtype2))
			assertEquals(occtype2, o.getType());
		
		assertEquals(3, index.getOccurrences(occtype1, occtype2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getOccurrenceTypes()}.
	 */
	@Test
	public void testGetOccurrenceTypes() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getRoles(java.util.Collection)}.
	 * 
	 * TopicMap has 1 role of type (http://TestTypeInstanceIndex/testGetRolesCollectionOfQextendsTopic/roletype1)
	 * and 2 roles of type (http://TestTypeInstanceIndex/testGetRolesCollectionOfQextendsTopic/roletype2)
	 */
	@Test
	public void testGetRolesCollectionOfQextendsTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic roletype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesCollectionOfQextendsTopic/roletype1"));
		assertNotNull(roletype1);
		
		ITopic roletype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesCollectionOfQextendsTopic/roletype2"));
		assertNotNull(roletype2);
		
		Collection<Topic> t1 = new HashSet<Topic>();
		t1.add(roletype1);
		
		Collection<ITopic> t2 = new HashSet<ITopic>();
		t2.add(roletype2);
		
		assertEquals(1, index.getRoles(t1).size());
		for(Role r:index.getRoles(t1))
			assertEquals(roletype1, r.getType());
		
		assertEquals(2, index.getRoles(t2).size());
		for(Role r:index.getRoles(t2))
			assertEquals(roletype2, r.getType());
		
		Collection<Topic> t = new HashSet<Topic>();
		t.add(roletype1);
		t.add(roletype2);
		
		assertEquals(3, index.getRoles(t).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getRoles(org.tmapi.core.Topic)}.
	 * 
	 * TopicMap has 1 role of type (http://TestTypeInstanceIndex/testGetRolesTopic/roletype1)
	 * and 2 roles of type (http://TestTypeInstanceIndex/testGetRolesTopic/roletype2)
	 */
	@Test
	public void testGetRolesTopic() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic roletype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesTopic/roletype1"));
		assertNotNull(roletype1);
		
		ITopic roletype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesTopic/roletype2"));
		assertNotNull(roletype2);
		
		assertEquals(1, index.getRoles(roletype1).size());
		for(Role r:index.getRoles(roletype1))
			assertEquals(roletype1, r.getType());
		
		assertEquals(2, index.getRoles(roletype2).size());
		for(Role r:index.getRoles(roletype2))
			assertEquals(roletype2, r.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getRoles(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has 1 role of type (http://TestTypeInstanceIndex/testGetRolesTopicArray/roletype1)
	 * and 2 roles of type (http://TestTypeInstanceIndex/testGetRolesTopicArray/roletype2)
	 */
	@Test
	public void testGetRolesTopicArray() {

		assertNotNull(map);
		ITypeInstanceIndex index = (ITypeInstanceIndex)map.getIndex(TypeInstanceIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic roletype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesTopicArray/roletype1"));
		assertNotNull(roletype1);
		
		ITopic roletype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTypeInstanceIndex/testGetRolesTopicArray/roletype2"));
		assertNotNull(roletype2);
		
		assertEquals(1, index.getRoles(roletype1).size());
		for(Role r:index.getRoles(roletype1))
			assertEquals(roletype1, r.getType());
		
		assertEquals(2, index.getRoles(roletype2).size());
		for(Role r:index.getRoles(roletype2))
			assertEquals(roletype2, r.getType());
		
		assertEquals(3, index.getRoles(roletype1, roletype2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getRoleTypes()}.
	 */
	@Test
	public void testGetRoleTypes() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopics(java.util.Collection, boolean)}.
	 */
	@Test
	public void testGetTopicsCollectionOfTopicBoolean() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopics(java.util.Collection)}.
	 */
	@Test
	public void testGetTopicsCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testGetTopicsTopic() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[])}.
	 */
	@Test
	public void testGetTopicsTopicArray() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopics(org.tmapi.core.Topic[], boolean)}.
	 */
	@Test
	public void testGetTopicsTopicArrayBoolean() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#getTopicTypes()}.
	 */
	@Test
	public void testGetTopicTypes() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.ITypeInstanceIndex#clear()}.
	 */
	@Test
	public void testClear() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link  de.topicmapslab.majortom.model.index.ITypeInstanceIndex#open()}.
	 */
	@Test
	public void testOpen() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link  de.topicmapslab.majortom.model.index.ITypeInstanceIndex#isOpen()}.
	 */
	@Test
	public void testIsOpen() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link  de.topicmapslab.majortom.model.index.ITypeInstanceIndex#close()}.
	 */
	@Test
	public void testClose() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link  de.topicmapslab.majortom.model.index.ITypeInstanceIndex#isAutoUpdated()}.
	 */
	@Test
	public void testIsAutoUpdated() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link  de.topicmapslab.majortom.model.index.ITypeInstanceIndex#reindex()}.
	 */
	@Test
	public void testReindex() {

		fail("Not yet implemented");
	}
}
