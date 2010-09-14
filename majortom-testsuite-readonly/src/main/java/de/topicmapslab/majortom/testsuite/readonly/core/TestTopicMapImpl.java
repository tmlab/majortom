package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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


public class TestTopicMapImpl extends AbstractTest {

	/*
	 * 
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		assertNotNull(map.getId());
	}

	/* void addTag(String name)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTagString() {
		assertNotNull(map);
		map.addTag("TAG");
	}

	/* void addTag(String name, Calendar timestamp)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTagStringCalendar() {
		assertNotNull(map);
		map.addTag("TAG",Calendar.getInstance());
	}

	/* <T extends Association> Collection<T> getAssociations(Topic type)
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

	/* <T extends Association> Collection<T> getAssociations(IScope scope)
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

	/* <T extends Association> Collection<T> getAssociations(Topic type, IScope scope)
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

	/* <T extends Topic> Collection<T> getTopics(Topic type)
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

	/* void close()
	 * 
	 */
	@Test(expected=TopicMapStoreException.class)
	public void testClose() {
		assertNotNull(map);
		map.close();
		map.createLocator("http://TestTopicMapImpl/testClose");
	}

	/* Association createAssociation(Topic type, Topic... themes)
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

	/* Association createAssociation(Topic type, Collection<Topic> themes)
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

	/* Locator createLocator(String ref)
	 * 
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

	/* Topic createTopic()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopic() {
		assertNotNull(map);
		map.createTopic();
	}

	/* Topic createTopicByItemIdentifier(Locator identifier)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicByItemIdentifier() {
		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicByItemIdentifier");
		assertNotNull(l);
		map.createTopicByItemIdentifier(l);
	}

	/* Topic createTopicBySubjectIdentifier(Locator identifier)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectIdentifier() {
		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicBySubjectIdentifier");
		assertNotNull(l);
		map.createTopicBySubjectIdentifier(l);
	}
	

	/* Topic createTopicBySubjectLocator(Locator locator)
	 *  
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectLocator() {
		assertNotNull(map);
		Locator l = map.createLocator("http://TestTopicMapImpl/testCreateTopicBySubjectLocator");
		assertNotNull(l);
		map.createTopicBySubjectLocator(l);
	}
	

	/* Set<Association> getAssociations()
	 * TopicMap hast exactly two associations
	 */
	@Test
	public void testGetAssociations() {

		assertNotNull(map);
		Collection<Association> ass = map.getAssociations();
		assertEquals(2, ass.size());
	}

	/* Construct getConstructById(String id)
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

	/* Construct getConstructByItemIdentifier(Locator identifier)
	 * 
	 */
	@Test
	public void testGetConstructByItemIdentifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/* <I extends Index> I getIndex(Class<I> clazz)
	 * 
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

	/* Locator getLocator()
	 * TopicMap has base locator (http://TestTopicMapImpl/)
	 */
	@Test
	public void testGetLocator() {

		assertNotNull(map);
		Locator l = map.getLocator();
		assertNotNull(l);
		assertEquals(l.getReference(), "http://TestTopicMapImpl/");
	}

	/* Topic getTopicBySubjectIdentifier(Locator identifier)
	 * Topic (http://TestTopicMapImpl/testGetTopicBySubjectIdentifier/topic/1)
	 */
	@Test
	public void testGetTopicBySubjectIdentifier() {

		Locator l = map.createLocator("http://TestTopicMapImpl/testGetTopicBySubjectIdentifier/topic/1");
		assertNotNull(l);
		
		Topic topic = map.getTopicBySubjectIdentifier(l);
		assertNotNull(topic);
	}

	/* Topic getTopicBySubjectLocator(Locator locator)
	 * Topic with subject locator (http://TestTopicMapImpl/testGetTopicBySubjectLocator/topic/1)
	 */
	@Test
	public void testGetTopicBySubjectLocator() {

		Locator l = map.createLocator("http://TestTopicMapImpl/testGetTopicBySubjectLocator/topic/1");
		assertNotNull(l);
		
		Topic topic = map.getTopicBySubjectLocator(l);
		assertNotNull(topic);
	}

	/* Set<Topic> getTopics()
	 * 
	 */
	@Test
	public void testGetTopics() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/* void mergeIn(TopicMap topicMap)
	 * 
	 */
	@Test
	public void testMergeIn() {

		// TODO implement: where to get an other topic map
		fail("Not yet implemented");
	}

	/*  ITransaction createTransaction()
	 * 
	 */
	@Test(expected=UnsupportedOperationException.class)
	public void testCreateTransaction() {

		assertNotNull(map);
		map.createTransaction();
		
	}

	/*  ITopicMapSystem getTopicMapSystem()
	 * 
	 */
	@Test
	public void testGetTopicMapSystem() {

		// TODO implement
		fail("Not yet implemented");
	}

	/*  IScope createScope(Collection<Topic> themes)
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

	/*  IScope createScope(Topic... themes)
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

	/* void removeDuplicates()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveDuplicates() {
		assertNotNull(map);
		map.removeDuplicates();
	}

	/* void clear()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testClear() {
		// TODO implement: what does this method?
		fail("Not yet implemented");
	}
	
	

	
}
