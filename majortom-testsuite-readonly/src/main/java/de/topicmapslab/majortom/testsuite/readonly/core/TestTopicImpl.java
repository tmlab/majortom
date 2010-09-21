/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


/**
 * @author ch
 *
 */
public class TestTopicImpl extends AbstractTest{

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed()}.
	 * 
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayed/topic/1) has not associations
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayed/topic/2) has exactly one association
	 */
	@Test
	public void testGetAssociationsPlayed() {

		assertNotNull(map);

		ITopic topic1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayed/topic/1"));
		assertNotNull(topic1);

		Collection<Association> ass1 = topic1.getAssociationsPlayed();
		assertTrue(ass1.isEmpty());
		
		ITopic topic2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayed/topic/2"));
		assertNotNull(topic2);
		
		Collection<Association> ass2 = topic2.getAssociationsPlayed();
		assertEquals(1,ass2.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed(org.tmapi.core.Topic)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayedTopic/topic/1) has
	 * 1 association of type(http://TestTopicImpl/testGetAssociationsPlayedTopic/ass/1)
	 * 0 associations of type(http://TestTopicImpl/testGetAssociationsPlayedTopic/ass/2)
	 */
	@Test
	public void testGetAssociationsPlayedTopic() {

		assertNotNull(map);
		
		ITopic type1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopic/ass/1"));
		ITopic type2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopic/ass/2"));
		assertNotNull(type1);
		assertNotNull(type2);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopic/topic/1"));
		assertNotNull(topic);
		
		Collection<Association> ass1 = topic.getAssociationsPlayed(type1);
		Collection<Association> ass2 = topic.getAssociationsPlayed(type2);
				
		assertEquals(1,ass1.size());
		assertEquals(0,ass2.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayedIScope/topic/1) has
	 * 1 association of type(http://TestTopicImpl/testGetAssociationsPlayedIScope/ass/1) and
	 * 1 association of type(http://TestTopicImpl/testGetAssociationsPlayedIScope/ass/2) with single theme scope(http://TestTopicImpl/testGetAssociationsPlayedIScope/theme/1)
	 */
	@Test
	public void testGetAssociationsPlayedIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedIScope/topic/1"));
		assertNotNull(topic);
		
		Collection<Association> all = topic.getAssociationsPlayed();
		assertEquals(2,all.size());
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedIScope/theme/1"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Association> ass = topic.getAssociationsPlayed(scope);
		assertEquals(1,ass.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed(org.tmapi.core.Topic, de.topicmapslab.majortom.model.core.IScope)}.
	 *	
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/topic/1) has
	 * 1 association of type(http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/ass/1) 
	 * with single theme scope(http://TestTopicImpl/testGetAssociationsPlayedIScope/theme/1) and
	 * 1 association of type(http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/ass/2) 
	 * with single theme scope(http://TestTopicImpl/testGetAssociationsPlayedIScope/theme/1) 
	 */
	@Test
	public void testGetAssociationsPlayedTopicIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/topic/1"));
		assertNotNull(topic);

		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/theme/1"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Association> all = topic.getAssociationsPlayed();
		assertEquals(2,all.size());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetAssociationsPlayedTopicIScope/ass/2"));
		assertNotNull(type);
		
		Collection<Association> ass = topic.getAssociationsPlayed(type, scope);
		assertEquals(1,ass.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics()}.
	 * 
	 * Topic(http://TestTopicImpl/testGetCharacteristics/topic/1) has
	 * exactly 1 Name and 1 Occurrence
	 */
	@Test
	public void testGetCharacteristics() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristics/topic/1"));
		assertNotNull(topic);
		
		Collection<ICharacteristics> chars = topic.getCharacteristics();
		assertEquals(2,chars.size());
		
		Object[] charsArray = chars.toArray();
		
		assertTrue((charsArray[0] instanceof IName && charsArray[1] instanceof IOccurrence) || (charsArray[0] instanceof IOccurrence && charsArray[1] instanceof IName));
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics(org.tmapi.core.Topic)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetCharacteristicsTopic/topic/1) has
	 * 1 Name of type(http://TestTopicImpl/testGetCharacteristicsTopic/name) and
	 * 2 Occurrences of type(http://TestTopicImpl/testGetCharacteristicsTopic/occurrence)
	 */
	@Test
	public void testGetCharacteristicsTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopic/topic/1"));
		assertNotNull(topic);
		
		Collection<ICharacteristics> chars = topic.getCharacteristics();
		assertEquals(3,chars.size());
		
		ITopic nameType = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopic/name"));
		assertNotNull(nameType);
		ITopic occurrenceType = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopic/occurrence"));
		assertNotNull(occurrenceType);
		
		Collection<ICharacteristics> names = topic.getCharacteristics(nameType);
		assertEquals(1,names.size());
		Collection<ICharacteristics> occurrences = topic.getCharacteristics(occurrenceType);
		assertEquals(2,occurrences.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetCharacteristicsIScope/topic/1) has
	 * 1 name with single theme scope (http://TestTopicImpl/testGetCharacteristicsIScope/theme),
	 * 1 occurrnece with single theme scope (http://TestTopicImpl/testGetCharacteristicsIScope/theme) and
	 * 1 occurrence without scope
	 */
	@Test
	public void testGetCharacteristicsIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsIScope/topic/1"));
		assertNotNull(topic);
		
		Collection<ICharacteristics> chars = topic.getCharacteristics();
		assertEquals(3,chars.size());
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<ICharacteristics> scoped = topic.getCharacteristics(scope);
		assertEquals(2,scoped.size());
		
		Object[] scopedArray = scoped.toArray();
		
		assertTrue((scopedArray[0] instanceof IName && scopedArray[1] instanceof IOccurrence) || (scopedArray[0] instanceof IOccurrence && scopedArray[1] instanceof IName));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getCharacteristics(org.tmapi.core.Topic, de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetCharacteristicsTopicIScope/topic/1) has
	 * 1 name of type(http://TestTopicImpl/testGetCharacteristicsTopicIScope/name) 
	 * with single theme scope (http://TestTopicImpl/testGetCharacteristicsTopicIScope/theme),
	 * 1 occurrnece of type(http://TestTopicImpl/testGetCharacteristicsTopicIScope/occurrence) 
	 * with single theme scope (http://TestTopicImpl/testGetCharacteristicsTopicIScope/theme) and
	 * 1 occurrence of type(http://TestTopicImpl/testGetCharacteristicsTopicIScope/occurrence) without scope
	 */
	@Test
	public void testGetCharacteristicsTopicIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopicIScope/topic/1"));
		assertNotNull(topic);
		
		Collection<ICharacteristics> chars = topic.getCharacteristics();
		assertEquals(3,chars.size());
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopicIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<ICharacteristics> scoped = topic.getCharacteristics(scope);
		assertEquals(2,scoped.size());
		
		ITopic nameType = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopicIScope/name"));
		assertNotNull(nameType);
		ITopic occurrenceType = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetCharacteristicsTopicIScope/occurrence"));
		assertNotNull(occurrenceType);
		
		Collection<ICharacteristics> scopedNames = topic.getCharacteristics(nameType, scope);
		assertEquals(1,scopedNames.size());
		
		Collection<ICharacteristics> scopedOccurrences = topic.getCharacteristics(occurrenceType, scope);
		assertEquals(1,scopedOccurrences.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames(org.tmapi.core.Topic, de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetNamesTopicIScope/topic/1) has
	 * 1 default name with single theme scope (http://TestTopicImpl/testGetNamesTopicIScope/theme)
	 * 1 name of type (http://TestTopicImpl/testGetNamesTopicIScope/name) 
	 * with single theme scope (http://TestTopicImpl/testGetNamesTopicIScope/theme) and
	 * 1 name of type (http://TestTopicImpl/testGetNamesTopicIScope/name) without scope 
	 */
	@Test
	public void testGetNamesTopicIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesTopicIScope/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(3,names.size());
		
		ITopic nameType = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesTopicIScope/name"));
		assertNotNull(nameType);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesTopicIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Name> scoped = topic.getNames(scope);
		assertEquals(2,scoped.size());
		
		Collection<Name> typed = topic.getNames(nameType);
		assertEquals(2,typed.size());
		
		Collection<Name> typedScoped = topic.getNames(nameType, scope);
		assertEquals(1,typedScoped.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetNamesIScope/topic/1) has
	 * 1 name with single theme scope (http://TestTopicImpl/testGetNamesIScope/theme) and
	 * 1 name without scope 
	 */
	@Test
	public void testGetNamesIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesIScope/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(2,names.size());
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Name> scoped = topic.getNames(scope);
		assertEquals(1,scoped.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences(org.tmapi.core.Topic, de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetOccurrencesTopicIScope/topic/1) has
	 * 1 occurrence of type(http://TestTopicImpl/testGetOccurrencesTopicIScope/occ/1) without scope
	 * 1 occurrence of type(http://TestTopicImpl/testGetOccurrencesTopicIScope/occ/1)
	 * with single theme scope(http://TestTopicImpl/testGetOccurrencesTopicIScope/theme) and
	 * 1 occurrence of type(http://TestTopicImpl/testGetOccurrencesTopicIScope/occ/2)
	 * with single theme scope(http://TestTopicImpl/testGetOccurrencesTopicIScope/theme)
	 */
	@Test
	public void testGetOccurrencesTopicIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopicIScope/topic/1"));
		assertNotNull(topic);
		
		Set<Occurrence> occs = topic.getOccurrences();
		assertEquals(3, occs.size());
		
		ITopic occurrenceType1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopicIScope/occ/1"));
		assertNotNull(occurrenceType1);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopicIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Set<Occurrence> typedOccs = topic.getOccurrences(occurrenceType1);
		assertEquals(2, typedOccs.size());
		
		Collection<Occurrence> typedScopedOccs = topic.getOccurrences(occurrenceType1, scope);
		assertEquals(1, typedScopedOccs.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetOccurrencesIScope/topic/1) has
	 * 1 occurrence with single theme scope (http://TestTopicImpl/testGetOccurrencesIScope/theme) and
	 * 1 occurrence without scope
	 */
	@Test
	public void testGetOccurrencesIScope() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesIScope/topic/1"));
		assertNotNull(topic);
		
		Set<Occurrence> occs = topic.getOccurrences();
		assertEquals(2, occs.size());
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesIScope/theme"));
		assertNotNull(theme);
		IScope scope = map.createScope(theme);
		assertNotNull(scope);
		
		Collection<Occurrence> scopedOccs = topic.getOccurrences(scope);
		assertEquals(1, scopedOccs.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getSupertypes()}.
	 */
	@Test
	public void testGetSupertypes() {

		/// TODO implement testGetSupertypes()
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#addSupertype(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testAddSupertype/topic/1)
	 * Topic (http://TestTopicImpl/testAddSupertype/topic/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddSupertype() {

		assertNotNull(map);
		
		ITopic topic1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddSupertype/topic/1"));
		assertNotNull(topic1);

		ITopic topic2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddSupertype/topic/2"));
		assertNotNull(topic2);
		
		topic1.addSupertype(topic2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#removeSupertype(org.tmapi.core.Topic)}.
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveSupertype() {

		/// TODO implement testRemoveSupertype()
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#addSubjectIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicImpl/testAddSubjectIdentifier/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddSubjectIdentifier() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddSubjectIdentifier/topic/1"));
		assertNotNull(topic);
		
		Locator l = map.createLocator("http://TestTopicImpl/testAddSubjectIdentifier/topic/2");
		topic.addSubjectIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#addSubjectLocator(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicImpl/testAddSubjectLocator/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddSubjectLocator() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddSubjectLocator/topic/1"));
		assertNotNull(topic);
		
		Locator l = map.createLocator("http://TestTopicImpl/testAddSubjectLocator/topic/2");
		topic.addSubjectLocator(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createName(java.lang.String, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateNameStringTopicArray/topic/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringTopicArray/theme/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringTopicArray/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateNameStringTopicArray() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringTopicArray/topic/1"));
		assertNotNull(topic);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringTopicArray/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringTopicArray/theme/2"));
		assertNotNull(theme2);
		
		topic.createName("Name", theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createName(java.lang.String, java.util.Collection)}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateNameStringCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		topic.createName("Name", themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createName(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/type/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateNameStringCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateNameTopicStringTopicArray() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringTopicArray/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringTopicArray/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringTopicArray/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringTopicArray/theme/2"));
		assertNotNull(theme2);
		
		topic.createName(type, "Name", theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createName(org.tmapi.core.Topic, java.lang.String, java.util.Collection)}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/type/1)
	 * Topic (http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateNameTopicStringCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateNameTopicStringCollectionOfTopic/theme/2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		topic.createName(type, "name", themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicStringTopicArray() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringTopicArray/theme/2"));
		assertNotNull(theme2);
		
		topic.createOccurrence(type, "Occurrence", theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, java.lang.String, java.util.Collection)}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicStringCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringCollectionOfTopic/theme/2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		topic.createOccurrence(type, "Occurrence", themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicLocatorTopicArray() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorTopicArray/theme/2"));
		assertNotNull(theme2);
		
		topic.createOccurrence(type, map.createLocator("http://occurrence/value"), theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, org.tmapi.core.Locator, java.util.Collection)}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicLocatorCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicLocatorCollectionOfTopic/theme/2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		topic.createOccurrence(type, map.createLocator("http://occurrence/value"), themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicStringLocatorTopicArray() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorTopicArray/theme/2"));
		assertNotNull(theme2);
		
		topic.createOccurrence(type, "value",map.createLocator("xsd:string"), theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#createOccurrence(org.tmapi.core.Topic, java.lang.String, org.tmapi.core.Locator, java.util.Collection)}.
	 * 
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/topic/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/type/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/theme/1)
	 * Topic (http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/theme/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateOccurrenceTopicStringLocatorCollectionOfTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/type/1"));
		assertNotNull(type);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/theme/1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testCreateOccurrenceTopicStringLocatorCollectionOfTopic/theme/2"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		topic.createOccurrence(type, "value", map.createLocator("xsd:string"), themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetNames/topic/1) has
	 * 1 default name and
	 * 2 name of type(http://TestTopicImpl/testGetNames/name)
	 */
	@Test
	public void testGetNames() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNames/topic/1"));
		assertNotNull(topic);
		
		assertEquals(3, topic.getNames().size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testGetNamesTopic/topic/1) has
	 * 1 default name and
	 * 2 name of type(http://TestTopicImpl/testGetNamesTopic/name)
	 */
	@Test
	public void testGetNamesTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesTopic/topic/1"));
		assertNotNull(topic);
		
		assertEquals(3, topic.getNames().size());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetNamesTopic/name"));
		assertNotNull(type);
		
		assertEquals(2, topic.getNames(type).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetOccurrences/topic/1) has
	 * 1 occurrence of type(http://TestTopicImpl/testGetOccurrences/occ/1)
	 * 2 occurrences of type(http://TestTopicImpl/testGetOccurrences/occ/2)
	 */
	@Test
	public void testGetOccurrences() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrences/topic/1"));
		assertNotNull(topic);
		
		assertEquals(3, topic.getOccurrences().size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testGetOccurrencesTopic/topic/1) has
	 * 1 occurrence of type(http://TestTopicImpl/testGetOccurrencesTopic/occ/1)
	 * 2 occurrences of type(http://TestTopicImpl/testGetOccurrencesTopic/occ/2)
	 */
	@Test
	public void testGetOccurrencesTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopic/topic/1"));
		assertNotNull(topic);
		
		assertEquals(3, topic.getOccurrences().size());
		
		ITopic type1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopic/occ/1"));
		assertNotNull(type1);
		
		ITopic type2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetOccurrencesTopic/occ/2"));
		assertNotNull(type2);
		
		assertEquals(1, topic.getOccurrences(type1).size());
		assertEquals(2, topic.getOccurrences(type2).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getParent()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetParent/topic/1)
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetParent/topic/1"));
		assertNotNull(topic);
		assertNotNull(topic.getParent());
		assertEquals(map, topic.getParent());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getReified()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetReified/topic/1) playes in exactly one association
	 * which has an reifier
	 */
	@Test
	public void testGetReified() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetReified/topic/1"));
		assertNotNull(topic);
		
		Collection<Association> ass = topic.getAssociationsPlayed();
		assertEquals(1, ass.size());
		
		Association a = ass.iterator().next();
		
		Topic reifier = a.getReifier();
		assertNotNull(reifier);
		
		// check
		Reifiable r = reifier.getReified();
		assertNotNull(r);
		assertEquals(r, a);
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getRolesPlayed()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetRolesPlayed/topic/1) plays
	 * exactly 2 roles of type(http://TestTopicImpl/testGetRolesPlayed/roletype)
	 */
	@Test
	public void testGetRolesPlayed() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayed/topic/1"));
		assertNotNull(topic);
		
		Set<Role> roles = topic.getRolesPlayed();
		assertEquals(2, roles.size());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayed/roletype"));
		assertNotNull(type);
		
		for(Role r:roles)
			assertEquals(type, r.getType());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getRolesPlayed(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testGetRolesPlayedTopic/topic/1) plays
	 * exactly 1 role of type(http://TestTopicImpl/testGetRolesPlayedTopic/roletype1)
	 * and 1 role of type(http://TestTopicImpl/testGetRolesPlayedTopic/roletype2)
	 */
	@Test
	public void testGetRolesPlayedTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopic/topic/1"));
		assertNotNull(topic);
		
		Set<Role> roles = topic.getRolesPlayed();
		assertEquals(2, roles.size());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopic/roletype1"));
		assertNotNull(type);
		
		Set<Role> typedRoles = topic.getRolesPlayed(type);
		assertEquals(1, typedRoles.size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getRolesPlayed(org.tmapi.core.Topic, org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testGetRolesPlayedTopicTopic/topic/1) plays
	 * exactly 1 role of type(http://TestTopicImpl/testGetRolesPlayedTopicTopic/roletype1)
	 * in association of type(http://TestTopicImpl/testGetRolesPlayedTopicTopic/associationtype1)
	 * 1 role of type(http://TestTopicImpl/testGetRolesPlayed/roletype2)
	 * in in association of type(http://TestTopicImpl/testGetRolesPlayedTopicTopic/associationtype1)
	 * and 1 role of type(http://TestTopicImpl/testGetRolesPlayed/roletype1)
	 * in in association of type(http://TestTopicImpl/testGetRolesPlayedTopicTopic/associationtype2)
	 */
	@Test
	public void testGetRolesPlayedTopicTopic() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopicTopic/topic/1"));
		assertNotNull(topic);
		
		Set<Role> roles = topic.getRolesPlayed();
		assertEquals(3, roles.size());
		
		ITopic roletype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopicTopic/roletype1"));
		assertNotNull(roletype1);
		
		ITopic roletype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopicTopic/roletype2"));
		assertNotNull(roletype2);
		
		ITopic asstype1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopicTopic/associationtype1"));
		assertNotNull(asstype1);
		
		ITopic asstype2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetRolesPlayedTopicTopic/associationtype2"));
		assertNotNull(asstype2);
		
		roles = topic.getRolesPlayed(roletype1);
		assertEquals(2, roles.size());
		
		roles = topic.getRolesPlayed(roletype2);
		assertEquals(1, roles.size());
		
		roles = topic.getRolesPlayed(roletype1, asstype1);
		assertEquals(1, roles.size());
	}
	

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getSubjectIdentifiers()}.
	 * 
	 * Topic  has subject identifier 
	 *  http://TestTopicImpl/testGetSubjectIdentifiers/topic/1/si1
	 *  http://TestTopicImpl/testGetSubjectIdentifiers/topic/1/si2
	 */
	@Test
	public void testGetSubjectIdentifiers() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetSubjectIdentifiers/topic/1/si1"));
		assertNotNull(topic);
		assertEquals(2, topic.getSubjectIdentifiers().size());
		assertEquals(topic, (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetSubjectIdentifiers/topic/1/si2")));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getSubjectLocators()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetSubjectLocators/topic/1) has
	 * 2 subjectLocator (http://TestTopicImpl/testGetSubjectLocators/topic/1/sl1) and
	 * (http://TestTopicImpl/testGetSubjectLocators/topic/1/sl2)
	 */
	@Test
	public void testGetSubjectLocators() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetSubjectLocators/topic/1"));
		assertNotNull(topic);
		assertEquals(2, topic.getSubjectLocators().size());
		assertEquals(topic, (ITopic)map.getTopicBySubjectLocator(map.createLocator("http://TestTopicImpl/testGetSubjectLocators/topic/1/sl1")));
		assertEquals(topic, (ITopic)map.getTopicBySubjectLocator(map.createLocator("http://TestTopicImpl/testGetSubjectLocators/topic/1/sl2")));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getTypes()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetTypes/topic/1) has
	 * type (http://TestTopicImpl/testGetTypes/type1)
	 * and type (http://TestTopicImpl/testGetTypes/type2)
	 */
	@Test
	public void testGetTypes() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetTypes/topic/1"));
		assertNotNull(topic);
		
		Set<Topic> types = topic.getTypes();
		assertEquals(2, types.size());
		
		Object[] ta = types.toArray();
		
		assertTrue(((Topic)ta[0]).getSubjectIdentifiers().iterator().next().getReference().equals("http://TestTopicImpl/testGetTypes/type1") 
				|| ((Topic)ta[0]).getSubjectIdentifiers().iterator().next().getReference().equals("http://TestTopicImpl/testGetTypes/type2"));
		
		assertTrue(((Topic)ta[1]).getSubjectIdentifiers().iterator().next().getReference().equals("http://TestTopicImpl/testGetTypes/type1") 
				|| ((Topic)ta[1]).getSubjectIdentifiers().iterator().next().getReference().equals("http://TestTopicImpl/testGetTypes/type2"));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#addType(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testAddType/topic/1)
	 * Topic (http://TestTopicImpl/testAddType/topic/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddType() {

		assertNotNull(map);
		
		ITopic topic1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddType/topic/1"));
		assertNotNull(topic1);
		ITopic topic2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddType/topic/2"));
		assertNotNull(topic2);
		
		topic1.addType(topic2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#removeType(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testRemoveType/topic/1)
	 * has type (http://TestTopicImpl/testRemoveType/type)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveType() {

		assertNotNull(map);
		
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemoveType/topic/1"));
		assertNotNull(topic);
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemoveType/type"));
		assertNotNull(type);
		assertEquals(1, topic.getTypes().size());
		assertEquals(type, topic.getTypes().iterator().next());
		
		topic.removeType(type);
	}
	

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#mergeIn(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestTopicImpl/testMergeIn/topic/1)
	 * Topic (http://TestTopicImpl/testMergeIn/topic/2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testMergeIn() {

		ITopic topic1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testMergeIn/topic/1"));
		assertNotNull(topic1);
		
		ITopic topic2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testMergeIn/topic/2"));
		assertNotNull(topic2);
		
		topic1.mergeIn(topic2);
	}
	

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#removeSubjectIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicImpl/testRemoveSubjectIdentifier/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveSubjectIdentifier() {

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemoveSubjectIdentifier/topic/1"));
		assertNotNull(topic);
		
		assertEquals(1, topic.getSubjectIdentifiers().size());
		Locator si = topic.getSubjectIdentifiers().iterator().next();
		
		topic.removeSubjectIdentifier(si);
	}
	

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#removeSubjectLocator(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicImpl/testRemoveSubjectLocator/topic/1)
	 * has one subject locator
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveSubjectLocator() {

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemoveSubjectLocator/topic/1"));
		assertNotNull(topic);
		
		assertEquals(1, topic.getSubjectLocators().size());
		Locator sl = topic.getSubjectLocators().iterator().next();
		
		topic.removeSubjectLocator(sl);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetAssociationsPlayedIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getAssociationsPlayed(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetAssociationsPlayedIntIntComparatorOfAssociation() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetNamesIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNames(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetNamesIntIntComparatorOfName() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetOccurrencesIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getOccurrences(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetOccurrencesIntIntComparatorOfOccurrence() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getRolesPlayed(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetRolesPlayedIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getRolesPlayed(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetRolesPlayedIntIntComparatorOfRole() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getSupertypes(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetSupertypesIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getSupertypes(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetSupertypesIntIntComparatorOfTopic() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getTypes(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetTypesIntInt() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getTypes(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetTypesIntIntComparatorOfTopic() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfAssociationsPlayed()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfAssociationsPlayed() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfNames()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfNames() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfOccurrences()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfOccurrences() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfRolesPlayed()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfRolesPlayed() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfSupertypes()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfSupertypes() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getNumberOfTypes()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfTypes() {
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getBestLabel(Topic theme, boolean strict)}.
	 * 
	 * Topic(http://TestTopicImpl/testGetBestLabelTopic/topic/1) has names "a" without scope 
	 * and names "aa" and "bb" with scope:
	 * (http://TestTopicImpl/testGetBestLabelTopic/theme1)
	 * 
	 * Topic(http://TestTopicImpl/testGetBestLabelTopic/topic/2) has "a" with scope:
	 * (http://TestTopicImpl/testGetBestLabelTopic/theme1)
	 * (http://TestTopicImpl/testGetBestLabelTopic/theme2)
	 * and names "aa" and "bb"
	 * with scope
	 * (http://TestTopicImpl/testGetBestLabelTopic/theme2)
	 * 
	 * Topic(http://TestTopicImpl/testGetBestLabelTopic/topic/3)
	 * has names "abcdefg" without scope
	 * and name "a" with scope:
	 * (http://TestTopicImpl/testGetBestLabelTopic/theme1)
	 * 
	 * 
	 * TODO improve test!
	 */
	@Test
	public void testGetBestLabelTopicBoolean() {
		
		assertNotNull(map);
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLabelTopic/theme1"));
		assertNotNull(theme1);
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLabelTopic/theme2"));
		assertNotNull(theme2);
		
		assertEquals("http://TestTopicImpl/testGetBestLabelTopic/theme2", theme2.getBestLabel());
		assertNull(theme2.getBestLabel(theme1,true));
		
		ITopic topic1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLabelTopic/topic/1"));
		assertNotNull(topic1);
		assertEquals("aa", topic1.getBestLabel(theme1,false));
		assertNull(topic1.getBestLabel(theme2,true));
		
		ITopic topic2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLabelTopic/topic/2"));
		assertNotNull(topic2);
		assertEquals("aa", topic2.getBestLabel(theme2,false));
		assertEquals("a", topic2.getBestLabel(theme1,false));
		
		ITopic topic3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLabelTopic/topic/3"));
		assertNotNull(topic3);
		assertEquals("abcdefg", topic3.getBestLabel(theme2,false)); // no name so use min scope, i.e. abcdefg
		
	}
	
	/**
	 * Test method for {@link de.topicmapslab.majortom.core.TopicImpl#getBestLabel()}.
	 * 
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/1) has only the item identifier (http://TestTopicImpl/testGetBestLable/topic/1)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/2) has one ii and the sl (http://TestTopicImpl/testGetBestLable/topic/2)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/3) has one ii, one sl and the si (http://TestTopicImpl/testGetBestLable/topic/3)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/4) has the sis (http://TestTopicImpl/testGetBestLable/topic/4) and (http://TestTopicImpl/testGetBestLable/topic/44)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/5) has default names "aa" and "bb" and typed name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/6) has typed names "aa" and "bb" and scoped name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/7) has one theme names "aa" and "bb" and two theme name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/8) has two theme names "aa" and "bb"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/9) has scoped default name "aa" and unscoped but typed name "a"
	 * 
	 */
	@Test
	public void testGetBestLabel() {

		assertNotNull(map);
		
		ITopic topic1 = (ITopic)map.getConstructByItemIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/1"));
		assertEquals("http://TestTopicImpl/testGetBestLable/topic/1", topic1.getBestLabel());
		
		ITopic topic2 = (ITopic)map.getTopicBySubjectLocator(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/2"));
		assertEquals("http://TestTopicImpl/testGetBestLable/topic/2", topic2.getBestLabel());
		
		ITopic topic3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/3"));
		assertEquals("http://TestTopicImpl/testGetBestLable/topic/3", topic3.getBestLabel());
		
		ITopic topic4 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/44"));
		assertEquals("http://TestTopicImpl/testGetBestLable/topic/4", topic4.getBestLabel());
		
		ITopic topic5 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/5"));
		assertEquals("aa", topic5.getBestLabel());
		
		ITopic topic6 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/6"));
		assertEquals("aa", topic6.getBestLabel());
		
		ITopic topic7 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/7"));
		assertEquals("aa", topic7.getBestLabel());
		
		ITopic topic8 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/8"));
		assertEquals("aa", topic8.getBestLabel());
		
		ITopic topic9 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetBestLable/topic/9"));
		assertEquals("aa", topic9.getBestLabel());

	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetTopicMap/topic/1)
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetTopicMap/topic/1"));
		assertNotNull(topic);
		
		TopicMap m = topic.getTopicMap();
		assertNotNull(m);
		assertEquals(m, map);
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestTopicImpl/testAddItemIdentifier/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testAddItemIdentifier/topic/1"));
		assertNotNull(topic);
		
		Locator l = map.createLocator("http://TestTopicImpl/testAddItemIdentifier/ii");
		assertNotNull(l);
		topic.addItemIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 * 
	 * Topic (http://TestTopicImpl/testGetId/topic/1)
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetId/topic/1"));
		assertNotNull(topic);
		
		String id = topic.getId();
		assertNotNull(id);
		assertEquals(id, topic.getId());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 *
	 * Topic (http://TestTopicImpl/testGetItemIdentifiers/topic/1)
	 * has 2 item identifiers:
	 * http://TestTopicImpl/testGetItemIdentifiers/topic/1/ii/1
	 * http://TestTopicImpl/testGetItemIdentifiers/topic/1/ii/2
	 */
	@Test
	public void testGetItemIdentifiers() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testGetItemIdentifiers/topic/1"));
		assertNotNull(topic);
		
		Collection<Locator> iis = topic.getItemIdentifiers();
		assertEquals(2, iis.size());
		
		Locator ii1 = map.createLocator("http://TestTopicImpl/testGetItemIdentifiers/topic/1/ii/1");
		assertNotNull(ii1);
		Locator ii2 = map.createLocator("http://TestTopicImpl/testGetItemIdentifiers/topic/1/ii/2");
		assertNotNull(ii2);
		
		assertTrue(iis.contains(ii1));
		assertTrue(iis.contains(ii2));
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 * 
	 * Topic (http://TestTopicImpl/testRemove/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemove/topic/1"));
		assertNotNull(topic);
		
		topic.remove();
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 * 
	 * Topic (http://TestTopicImpl/testRemoveBoolean/topic/1)
	 */
	@Test
	public void testRemoveBoolean() {

		assertNotNull(map);
		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestTopicImpl/testRemoveBoolean/topic/1"));
		assertNotNull(topic);
		
		try{
			topic.remove(true);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
		try{
			topic.remove(false);
			fail("No exception thrown");
		}catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic with II (http://TestTopicImpl/testRemoveItemIdentifier/topic/1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveItemIdentifier() {
		
		assertNotNull(map);
		Locator ii = map.createLocator("http://TestTopicImpl/testRemoveItemIdentifier/topic/1");
		ITopic topic = (ITopic)map.getConstructByItemIdentifier(ii);
		assertNotNull(topic);
		
		topic.removeItemIdentifier(ii);
	}

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
