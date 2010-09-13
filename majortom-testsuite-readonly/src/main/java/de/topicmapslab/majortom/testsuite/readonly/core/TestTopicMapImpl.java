package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.transaction.ITransaction;
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
		map.addTag("TAG");
	}

	/* void addTag(String name, Calendar timestamp)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTagStringCalendar() {
		map.addTag("TAG",Calendar.getInstance());
	}

	/* <T extends Association> Collection<T> getAssociations(Topic type)
	 * 
	 */
	@Test
	public void testGetAssociationsTopic() {

		fail("Not yet implemented");
	}

	/* <T extends Association> Collection<T> getAssociations(IScope scope)
	 * 
	 */
	@Test
	public void testGetAssociationsIScope() {

		fail("Not yet implemented");
	}

	/* <T extends Association> Collection<T> getAssociations(Topic type, IScope scope)
	 * 
	 */
	@Test
	public void testGetAssociationsTopicIScope() {

		fail("Not yet implemented");
	}

	/* <T extends Topic> Collection<T> getTopics(Topic type)
	 * 
	 */
	@Test
	public void testGetTopicsTopic() {

		fail("Not yet implemented");
	}

	/* void close()
	 * 
	 */
	@Test(expected=TopicMapStoreException.class)
	public void testClose() {
		map.close();
		map.createLocator("http:test");
	}

	/* Association createAssociation(Topic type, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateAssociationTopicTopicArray() {

		fail("Not yet implemented");
	}

	/* Association createAssociation(Topic type, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateAssociationTopicCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Locator createLocator(String ref)
	 * 
	 */
	@Test
	public void testCreateLocator() {

		fail("Not yet implemented");
	}

	/* Topic createTopic()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopic() {
		map.createTopic();
	}

	/* Topic createTopicByItemIdentifier(Locator identifier)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicByItemIdentifier() {
		Locator l = map.createLocator("http://test");
		assertNotNull(l);
		map.createTopicByItemIdentifier(l);
	}

	/* Topic createTopicBySubjectIdentifier(Locator identifier)
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectIdentifier() {
		Locator l = map.createLocator("http://test");
		assertNotNull(l);
		map.createTopicBySubjectIdentifier(l);
	}

	/* Topic createTopicBySubjectLocator(Locator locator)
	 *  
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateTopicBySubjectLocator() {
		Locator l = map.createLocator("http://test");
		assertNotNull(l);
		map.createTopicBySubjectLocator(l);
	}

	/* Set<Association> getAssociations()
	 * 
	 */
	@Test
	public void testGetAssociations() {

		fail("Not yet implemented");
	}

	/* Construct getConstructById(String id)
	 * 
	 */
	@Test
	public void testGetConstructById() {

		fail("Not yet implemented");
	}

	/* Construct getConstructByItemIdentifier(Locator identifier)
	 * 
	 */
	@Test
	public void testGetConstructByItemIdentifier() {

		fail("Not yet implemented");
	}

	/* <I extends Index> I getIndex(Class<I> clazz)
	 * 
	 */
	@Test
	public void testGetIndex() {

		fail("Not yet implemented");
	}

	/* Locator getLocator()
	 * 
	 */
	@Test
	public void testGetLocator() {

		fail("Not yet implemented");
	}

	/* Topic getTopicBySubjectIdentifier(Locator identifier)
	 * 
	 */
	@Test
	public void testGetTopicBySubjectIdentifier() {

		fail("Not yet implemented");
	}

	/* Topic getTopicBySubjectLocator(Locator locator)
	 * 
	 */
	@Test
	public void testGetTopicBySubjectLocator() {

		fail("Not yet implemented");
	}

	/* Set<Topic> getTopics()
	 * 
	 */
	@Test
	public void testGetTopics() {

		fail("Not yet implemented");
	}

	/* void mergeIn(TopicMap topicMap)
	 * 
	 */
	@Test
	public void testMergeIn() {

		fail("Not yet implemented");
	}

	/*  ITransaction createTransaction()
	 * 
	 */
	@Test 
	public void testCreateTransaction() {

		fail("Not yet implemented");
	}

	/*  ITopicMapSystem getTopicMapSystem()
	 * 
	 */
	@Test
	public void testGetTopicMapSystem() {

		fail("Not yet implemented");
	}

	/*  IScope createScope(Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateScopeCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/*  IScope createScope(Topic... themes)
	 * 
	 */
	@Test
	public void testCreateScopeTopicArray() {

		fail("Not yet implemented");
	}

	/* void removeDuplicates()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveDuplicates() {
		map.removeDuplicates();
	}

	/* void clear()
	 * 
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testClear() {
		map.clear();
	}
}
