package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapExistsException;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


public class TestTopicImpl extends AbstractTest {

	/* String doReadBestLabel(ITopic topic)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/1) has only the item identifier (http://TestTopicImpl/testGetBestLable/topic/1)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/2) has one ii and the sl (http://TestTopicImpl/testGetBestLable/topic/2)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/3) has one ii, one sl and the si (http://TestTopicImpl/testGetBestLable/topic/3)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/4) has the sis (http://TestTopicImpl/testGetBestLable/topic/4) and (http://TestTopicImpl/testGetBestLable/topic/44)
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/5) has default names "aa" and "bb" and typed name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/6) has typed names "aa" and "bb" and scoped name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/7) has one theme names "aa" and "bb" and two theme name "a"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/8) has two theme names "aa" and "bb"
	 * Topic(http://TestTopicImpl/testGetBestLable/topic/9) has scoped default name "aa" and unscoped but typed name "a"
	 */
	@Test
	public void testGetBestLable() throws TopicMapExistsException {
	
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
		
	
	
	
	/* Collection<Association> getAssociationsPlayed()
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayed/topic/1) has not associations
	 * Topic(http://TestTopicImpl/testGetAssociationsPlayed/topic/2) has exactly one association
	 * @throws TopicMapExistsException 
	 * @throws MalformedIRIException 
	 */
	@Test
	public void testGetAssociationsPlayed() throws MalformedIRIException, TopicMapExistsException {
	
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
	

	/* Collection<Association> getAssociationsPlayed(Topic type)
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

	/* Collection<Association> getAssociationsPlayed(IScope scope)
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

	/* Collection<Association> getAssociationsPlayed(Topic type, IScope scope)
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

	/* Collection<ICharacteristics> getCharacteristics()
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

	/* Collection<ICharacteristics> getCharacteristics(Topic type)
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

	/* Collection<ICharacteristics> getCharacteristics(IScope scope)
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

	/* Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope) 
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

	/* Collection<Name> getNames(Topic type, IScope scope)
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

	/* Collection<Name> getNames(IScope scope)
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

	/* Collection<Occurrence> getOccurrences(Topic type, IScope scope)
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

	/* Collection<Occurrence> getOccurrences(IScope scope)
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

	/* Collection<Topic> getSupertypes()
	 * 
	 */
	@Test
	public void testGetSupertypes() {

		/// TODO implement testGetSupertypes()
		fail("Not yet implemented");
	}

	/* void addSupertype(Topic type)
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

	/* void removeSupertype(Topic type)
	 * 
	 */
	@Test
	public void testRemoveSupertype() {

		/// TODO implement testRemoveSupertype()
		fail("Not yet implemented");
	}

	/* void addSubjectIdentifier(Locator identifier)
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

	/* void addSubjectLocator(Locator locator)
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

	/* Name createName(String value, Topic... themes)
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

	/* Name createName(String value, Collection<Topic> themes)
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

	/* Name createName(Topic type, String value, Topic... themes)
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

	/* Name createName(Topic type, String value, Collection<Topic> themes)
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

	/* Occurrence createOccurrence(Topic type, String value, Topic... themes)
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

	/* Occurrence createOccurrence(Topic type, String value, Collection<Topic> themes)
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

	/* Occurrence createOccurrence(Topic type, Locator value, Topic... themes)
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

	/* Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> themes)
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

	/* Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... themes)
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

	/* Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> themes)
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

	/* Set<Name> getNames()
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

	/* Set<Name> getNames(Topic type)
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

	/* Set<Occurrence> getOccurrences()
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

	/* Set<Occurrence> getOccurrences(Topic type)
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

	/* ITopicMap getParent()
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

	/* Reifiable getReified()
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

	/* Set<Role> getRolesPlayed()
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

	/* Set<Role> getRolesPlayed(Topic roleType)
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

	/* Set<Role> getRolesPlayed(Topic roleType, Topic associtaionType)
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

	/* Set<Locator> getSubjectIdentifiers()
	 *  Topic  has subject identifier 
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

	/* Set<Locator> getSubjectLocators()
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

	/* Set<Topic> getTypes()
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

	/* void addType(Topic type)
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

	/* void removeType(Topic type)
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

	/* void mergeIn(Topic topic)
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

	/* void removeSubjectIdentifier(Locator identifier)
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

	/* void removeSubjectLocator(Locator locator)
	 * 
	 */
	@Test
	public void testRemoveSubjectLocator() {

		fail("Not yet implemented");
	}

	/* String toString()
	 * 
	 */
	@Test
	public void testToString() {

		fail("Not yet implemented");
	}

	/* List<Association> getAssociationsPlayed(int offset, int limit)
	 * 
	 */
	@Test
	public void testGetAssociationsPlayedIntInt() {

		fail("Not yet implemented");
	}

	/* List<Association> getAssociationsPlayed(int offset, int limit, Comparator<Association> comparator)
	 * 
	 */
	@Test
	public void testGetAssociationsPlayedIntIntComparatorOfAssociation() {

		fail("Not yet implemented");
	}

	/* List<Name> getNames(int offset, int limit)
	 * 
	 */
	@Test
	public void testGetNamesIntInt() {

		fail("Not yet implemented");
	}

	/* List<Name> getNames(int offset, int limit, Comparator<Name> comparator)
	 * 
	 */
	@Test
	public void testGetNamesIntIntComparatorOfName() {

		fail("Not yet implemented");
	}

	/* List<Occurrence> getOccurrences(int offset, int limit)
	 * 
	 */
	@Test
	public void testGetOccurrencesIntInt() {

		fail("Not yet implemented");
	}

	/* List<Occurrence> getOccurrences(int offset, int limit, Comparator<Occurrence> comparator)
	 * 
	 */
	@Test
	public void testGetOccurrencesIntIntComparatorOfOccurrence() {

		fail("Not yet implemented");
	}

	/* List<Role> getRolesPlayed(int offset, int limit)
	 * 
	 */
	@Test
	public void testGetRolesPlayedIntInt() {

		fail("Not yet implemented");
	}
 
	/* List<Role> getRolesPlayed(int offset, int limit, Comparator<Role> comparator)
	 * 
	 */
	@Test
	public void testGetRolesPlayedIntIntComparatorOfRole() {

		fail("Not yet implemented");
	}

	/* List<Topic> getSupertypes(int offset, int limit) 
	 * 
	 */
	@Test
	public void testGetSupertypesIntInt() {

		fail("Not yet implemented");
	}

	/* List<Topic> getSupertypes(int offset, int limit, Comparator<Topic> comparator)
	 * 
	 */
	@Test
	public void testGetSupertypesIntIntComparatorOfTopic() {

		fail("Not yet implemented");
	}

	/* List<Topic> getTypes(int offset, int limit)
	 * 
	 */
	@Test
	public void testGetTypesIntInt() {

		fail("Not yet implemented");
	}

	/* List<Topic> getTypes(int offset, int limit, Comparator<Topic> comparator)
	 * 
	 */
	@Test
	public void testGetTypesIntIntComparatorOfTopic() {

		fail("Not yet implemented");
	}

	/* long getNumberOfAssociationsPlayed()
	 * 
	 */
	@Test
	public void testGetNumberOfAssociationsPlayed() {

		fail("Not yet implemented");
	}

	/* long getNumberOfNames()
	 * 
	 */
	@Test
	public void testGetNumberOfNames() {

		fail("Not yet implemented");
	}

	/* long getNumberOfOccurrences()
	 * 
	 */
	@Test
	public void testGetNumberOfOccurrences() {

		fail("Not yet implemented");
	}

	/* long getNumberOfRolesPlayed()
	 * 
	 */
	@Test
	public void testGetNumberOfRolesPlayed() {

		fail("Not yet implemented");
	}

	/*  long getNumberOfSupertypes()
	 * 
	 */
	@Test
	public void testGetNumberOfSupertypes() {

		fail("Not yet implemented");
	}

	/* long getNumberOfTypes()
	 * 
	 */
	@Test
	public void testGetNumberOfTypes() {

		fail("Not yet implemented");
	}
}
