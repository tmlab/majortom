/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


/**
 * @author ch
 *
 */
public class TestTopicMapImpl extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopicMap()}.
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);
		assertEquals(map, map.getTopicMap());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getId()}.
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		assertNotNull(map.getId());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#remove(boolean)}.
	 */
	@Test
	public void testRemoveBoolean() {

		assertNotNull(map);
		
		try{
			map.remove(true);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
		try{
			map.remove(false);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#addTag(java.lang.String)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTagString() {
		assertNotNull(map);
		map.addTag("TAG");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#addTag(java.lang.String, java.util.Calendar)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTagStringCalendar() {
		assertNotNull(map);
		map.addTag("TAG",Calendar.getInstance());
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#addTopicMapListener(de.topicmapslab.majortom.model.event.ITopicMapListener)}.
//	 */
//	@Test
//	public void testAddTopicMapListener() {
//
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getAssociations(org.tmapi.core.Topic)}.
	 * 
	 * Topic Map has 2 associations of type (http://TestTopicMapImpl/testGetAssociationsTopic/asstype)
	 */
	@Test
	public void testGetAssociationsTopic() {
		assertNotNull(map);
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetAssociationsTopic/asstype"));
		assertNotNull(type);
		Collection<Association> ass = map.getAssociations(type);
		assertEquals(2, ass.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getAssociations(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic Map has 1 associations of type (http://TestTopicMapImpl/testGetAssociationsIScope/asstype/1)
	 * and 1 associations of type (http://TestTopicMapImpl/testGetAssociationsIScope/asstype/2)
	 * both with single theme scope (http://TestTopicMapImpl/testGetAssociationsIScope/theme)
	 */
	@Test
	public void testGetAssociationsIScope() {
		
		assertNotNull(map);
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetAssociationsIScope/theme"));
		assertNotNull(theme);
		
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Association> ass = map.getAssociations(scope);
		assertEquals(2, ass.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getAssociations(org.tmapi.core.Topic, de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic Map has 1 associations of type (http://TestTopicMapImpl/testGetAssociationsTopicIScope/asstype/1)
	 * and 2 associations of type (http://TestTopicMapImpl/testGetAssociationsTopicIScope/asstype/2)
	 * all with single theme scope (http://TestTopicMapImpl/testGetAssociationsTopicIScope/theme)
	 */
	@Test
	public void testGetAssociationsTopicIScope() {

		assertNotNull(map);
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetAssociationsTopicIScope/theme"));
		assertNotNull(theme);
		
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		ITopic type1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetAssociationsTopicIScope/asstype/1"));
		assertNotNull(type1);
		
		ITopic type2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetAssociationsTopicIScope/asstype/2"));
		assertNotNull(type2);
		
		Collection<Association> ass = map.getAssociations(scope);
		assertEquals(3, ass.size());
		
		ass = map.getAssociations(type1, scope);
		assertEquals(1, ass.size());
		
		ass = map.getAssociations(type2, scope);
		assertEquals(2, ass.size());
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getStore()}.
//	 */
//	@Test
//	public void testGetStore() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#setStore(de.topicmapslab.majortom.model.store.ITopicMapStore)}.
//	 */
//	@Test
//	public void testSetStore() {
//
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopics(org.tmapi.core.Topic)}.
	 * 
	 * Topic map has 2 topics of type (http://TestTopicMapImpl/testGetTopicsTopic/type)
	 */
	@Test
	public void testGetTopicsTopic() {

		assertNotNull(map);

		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetTopicsTopic/type"));
		assertNotNull(type);
		
		Collection<Topic> topics = map.getTopics(type);
		assertEquals(2, topics.size());
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#removeTopicMapListener(de.topicmapslab.majortom.model.event.ITopicMapListener)}.
//	 */
//	@Test
//	public void testRemoveTopicMapListener() {
//
//		fail("Not yet implemented");
//	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#close()}.
	 */
	@Test(expected=TopicMapStoreException.class)
	public void testClose() {

		assertNotNull(map);
		map.close();
		map.createLocator("http://TestTopicMapImpl/testClose");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createAssociation(org.tmapi.core.Topic, org.tmapi.core.Topic[])}.
	 * 
	 * Topic Map hast Topics (http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/type)
	 * and (http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/theme)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateAssociationTopicTopicArray() {
		
		assertNotNull(map);
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/type"));
		assertNotNull(type);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/theme"));
		assertNotNull(theme);
		
		map.createAssociation(type, theme);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createAssociation(org.tmapi.core.Topic, java.util.Collection)}.
	 * 
	 * Topic Map hast Topics (http://TestTopicMapImpl/testCreateAssociationTopicCollectionOfTopic/type)
	 * and (http://TestTopicMapImpl/testCreateAssociationTopicCollectionOfTopic/theme)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateAssociationTopicCollectionOfTopic() {

		assertNotNull(map);
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/type"));
		assertNotNull(type);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateAssociationTopicTopicArray/theme"));
		assertNotNull(theme);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme);
		
		map.createAssociation(type, themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createLocator(java.lang.String)}.
	 */
	@Test
	public void testCreateLocator() {

		String iri = "http://TestTopicMapImpl/testCreateLocator";
		
		assertNotNull(map);
		Locator l = map.createLocator(iri);
		assertNotNull(l);
		Locator l2 = map.createLocator(iri);
		assertEquals(l, l2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createTopic()}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopic() {

		assertNotNull(map);
		map.createTopic();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createTopicByItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicByItemIdentifier() {

		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicByItemIdentifier");
		assertNotNull(l);
		map.createTopicByItemIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createTopicBySubjectIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectIdentifier() {

		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicBySubjectIdentifier");
		assertNotNull(l);
		map.createTopicBySubjectIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createTopicBySubjectLocator(org.tmapi.core.Locator)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectLocator() {

		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicBySubjectLocator");
		assertNotNull(l);
		map.createTopicBySubjectLocator(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getAssociations()}.
	 * 
	 * TopicMap hast exactly two associations
	 */
	@Test
	public void testGetAssociations() {

		assertNotNull(map);
		Collection<Association> ass = map.getAssociations();
		assertEquals(2, ass.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getConstructById(java.lang.String)}.
	 * 
	 * TopicMap has
	 * 1 Topic of type (http://TestTopicMapImpl/testGetConstructById/topictype) with
	 * 1 Name of type (http://TestTopicMapImpl/testGetConstructById/nametype)
	 * and 1 Occurrence of type (http://TestTopicMapImpl/testGetConstructById/occurrencetype)
	 * 1 Association of type (http://TestTopicMapImpl/testGetConstructById/associationtype) with 
	 * and 1 Role of type (http://TestTopicMapImpl/testGetConstructById/roletype)
	 */
	@Test
	public void testGetConstructById() {

		assertNotNull(map);
		
		ITopic topictype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetConstructById/topictype"));
		assertNotNull(topictype);
		ITopic nametype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetConstructById/nametype"));
		assertNotNull(nametype);
		ITopic occurrencetype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetConstructById/occurrencetype"));
		assertNotNull(occurrencetype);
		ITopic associationtype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetConstructById/associationtype"));
		assertNotNull(associationtype);
		ITopic roletype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testGetConstructById/roletype"));
		assertNotNull(roletype);
		
		String id = topictype.getId();
		assertEquals(topictype, map.getConstructById(id));
		
		assertEquals(1, map.getTopics(topictype).size());
		Topic topic = map.getTopics(topictype).iterator().next();
		id = topic.getId();
		assertEquals(topic, map.getConstructById(id));
		
		assertEquals(1, topic.getNames(nametype).size());
		Name name = topic.getNames(nametype).iterator().next();
		id = name.getId();
		assertEquals(name, map.getConstructById(id));
		
		assertEquals(1, topic.getOccurrences(occurrencetype).size());
		Occurrence occurrence = topic.getOccurrences(occurrencetype).iterator().next();
		id = occurrence.getId();
		assertEquals(occurrence, map.getConstructById(id));
				
		assertEquals(1, map.getAssociations(associationtype).size());
		Association association = map.getAssociations(associationtype).iterator().next();
		id = association.getId();
		assertEquals(association, map.getConstructById(id));
		
		assertEquals(1, association.getRoles(roletype).size());
		Role role = association.getRoles(roletype).iterator().next();
		id = role.getId();
		assertEquals(role, map.getConstructById(id));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getConstructByItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testGetConstructByItemIdentifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getIndex(java.lang.Class)}.
	 */
	@Test
	public void testGetIndex() {

		assertNotNull(map);
		
		// check TypeInstanceIndex
		
		try{
			
			TypeInstanceIndex index = map.getIndex(TypeInstanceIndex.class);
			assertNotNull(index);
			
		}catch (Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
		
		// check ScopedIndex
		
		try{
			
			ScopedIndex index = map.getIndex(ScopedIndex.class);
			assertNotNull(index);
			
		}catch (Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
		
		// check LiteralIndex
		
		try{
			
			LiteralIndex index = map.getIndex(LiteralIndex.class);
			assertNotNull(index);
			
		}catch (Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getLocator()}.
	 * 
	 * TopicMap has base locator (http://TestTopicMapImpl/)
	 */
	@Test
	public void testGetLocator() {

		assertNotNull(map);
		Locator l = map.getLocator();
		assertNotNull(l);
		assertEquals(l.getReference(), "http://TestTopicMapImpl/");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopicBySubjectIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicMapImpl/testGetTopicBySubjectIdentifier/topic/1)
	 */
	@Test
	public void testGetTopicBySubjectIdentifier() {

		Locator l = map.createLocator("http://TestTopicMapImpl/testGetTopicBySubjectIdentifier/topic/1");
		assertNotNull(l);
		
		Topic topic = map.getTopicBySubjectIdentifier(l);
		assertNotNull(topic);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopicBySubjectLocator(org.tmapi.core.Locator)}.
	 * 
	 * Topic with subject locator (http://TestTopicMapImpl/testGetTopicBySubjectLocator/topic/1)
	 */
	@Test
	public void testGetTopicBySubjectLocator() {

		Locator l = map.createLocator("http://TestTopicMapImpl/testGetTopicBySubjectLocator/topic/1");
		assertNotNull(l);
		
		Topic topic = map.getTopicBySubjectLocator(l);
		assertNotNull(topic);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopics()}.
	 */
	@Test
	public void testGetTopics() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#mergeIn(org.tmapi.core.TopicMap)}.
	 */
	@Test
	public void testMergeIn() {

		// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createTransaction()}.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testCreateTransaction() {

		assertNotNull(map);
		map.createTransaction();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#getTopicMapSystem()}.
	 */
	@Test
	public void testGetTopicMapSystem() {

		// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createScope(java.util.Collection)}.
	 * 
	 * TopicMap has topics:
	 * (http://TestTopicMapImpl/testCreateScopeCollectionOfTopic/theme1)
	 * (http://TestTopicMapImpl/testCreateScopeCollectionOfTopic/theme2)
	 */
	@Test
	public void testCreateScopeCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateScopeCollectionOfTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateScopeCollectionOfTopic/theme2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		IScope scope = map.createScope(themes);
		assertNotNull(scope);
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme1));
		assertTrue(scope.containsTheme(theme2));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#createScope(org.tmapi.core.Topic[])}.
	 * 
	 * TopicMap has topics:
	 * (http://TestTopicMapImpl/testCreateScopeTopicArray/theme1)
	 * (http://TestTopicMapImpl/testCreateScopeTopicArray/theme2)
	 */
	@Test
	public void testCreateScopeTopicArray() {

		assertNotNull(map);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateScopeTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testCreateScopeTopicArray/theme2"));
		assertNotNull(theme2);
		
		IScope scope = map.createScope(theme1, theme2);
		assertNotNull(scope);
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme1));
		assertTrue(scope.containsTheme(theme2));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#removeDuplicates()}.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveDuplicates() {

		assertNotNull(map);
		map.removeDuplicates();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicMapImpl#clear()}.
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testClear() {

		assertNotNull(map);
		map.clear();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	@Test
	public void testGetReifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
	 * 
	 * Topic http://TestTopicMapImpl/testSetReifier/topic/1
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetReifier() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicMapImpl/testSetReifier/topic/1"));
		assertNotNull(topic);
		
		map.setReifier(topic);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testAddItemIdentifier");
		assertNotNull(l);
		map.addItemIdentifier(l);
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
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);
		map.remove();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testRemoveItemIdentifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getParent()}.
	 */
	@Test
	public void testGetParent() {
		assertNotNull(map);
		assertNull(map.getParent());
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
