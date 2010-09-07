package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TopicMapExistsException;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


public class TestTopicImpl extends AbstractTest {

		
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
	 * 
	 */
	@Test
	public void testGetNamesTopicIScope() {

		fail("Not yet implemented");
	}

	/* Collection<Name> getNames(IScope scope)
	 * 
	 */
	@Test
	public void testGetNamesIScope() {

		fail("Not yet implemented");
	}

	/* Collection<Occurrence> getOccurrences(Topic type, IScope scope)
	 * 
	 */
	@Test
	public void testGetOccurrencesTopicIScope() {

		fail("Not yet implemented");
	}

	/* Collection<Occurrence> getOccurrences(IScope scope)
	 * 
	 */
	@Test
	public void testGetOccurrencesIScope() {

		fail("Not yet implemented");
	}

	/* Collection<Topic> getSupertypes()
	 * 
	 */
	@Test
	public void testGetSupertypes() {

		fail("Not yet implemented");
	}

	/* void addSupertype(Topic type)
	 * 
	 */
	@Test
	public void testAddSupertype() {

		fail("Not yet implemented");
	}

	/* void removeSupertype(Topic type)
	 * 
	 */
	@Test
	public void testRemoveSupertype() {

		fail("Not yet implemented");
	}

	/* void addSubjectIdentifier(Locator identifier)
	 * 
	 */
	@Test
	public void testAddSubjectIdentifier() {

		fail("Not yet implemented");
	}

	/* void addSubjectLocator(Locator locator)
	 * 
	 */
	@Test
	public void testAddSubjectLocator() {

		fail("Not yet implemented");
	}

	/* Name createName(String value, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateNameStringTopicArray() {

		fail("Not yet implemented");
	}

	/* Name createName(String value, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateNameStringCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Name createName(Topic type, String value, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateNameTopicStringTopicArray() {

		fail("Not yet implemented");
	}

	/* Name createName(Topic type, String value, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateNameTopicStringCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, String value, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicStringTopicArray() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, String value, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicStringCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, Locator value, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicLocatorTopicArray() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicLocatorCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicStringLocatorTopicArray() {

		fail("Not yet implemented");
	}

	/* Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> themes)
	 * 
	 */
	@Test
	public void testCreateOccurrenceTopicStringLocatorCollectionOfTopic() {

		fail("Not yet implemented");
	}

	/* Set<Name> getNames()
	 * 
	 */
	@Test
	public void testGetNames() {

		fail("Not yet implemented");
	}

	/* Set<Name> getNames(Topic type)
	 * 
	 */
	@Test
	public void testGetNamesTopic() {

		fail("Not yet implemented");
	}

	/* Set<Occurrence> getOccurrences()
	 * 
	 */
	@Test
	public void testGetOccurrences() {

		fail("Not yet implemented");
	}

	/* Set<Occurrence> getOccurrences(Topic type)
	 * 
	 */
	@Test
	public void testGetOccurrencesTopic() {

		fail("Not yet implemented");
	}

	/* ITopicMap getParent()
	 * 
	 */
	@Test
	public void testGetParent() {

		fail("Not yet implemented");
	}

	/* Reifiable getReified()
	 * 
	 */
	@Test
	public void testGetReified() {

		fail("Not yet implemented");
	}

	/* Set<Role> getRolesPlayed()
	 * 
	 */
	@Test
	public void testGetRolesPlayed() {

		fail("Not yet implemented");
	}

	/* Set<Role> getRolesPlayed(Topic roleType)
	 * 
	 */
	@Test
	public void testGetRolesPlayedTopic() {

		fail("Not yet implemented");
	}

	/* Set<Role> getRolesPlayed(Topic roleType, Topic associtaionType)
	 * 
	 */
	@Test
	public void testGetRolesPlayedTopicTopic() {

		fail("Not yet implemented");
	}

	/* Set<Locator> getSubjectIdentifiers()
	 *  
	 */
	@Test
	public void testGetSubjectIdentifiers() {

		fail("Not yet implemented");
	}

	/* Set<Locator> getSubjectLocators()
	 * 
	 */
	@Test
	public void testGetSubjectLocators() {

		fail("Not yet implemented");
	}

	/* Set<Topic> getTypes()
	 * 
	 */
	@Test
	public void testGetTypes() {

		fail("Not yet implemented");
	}

	/* void addType(Topic type)
	 * 
	 */
	@Test
	public void testAddType() {

		fail("Not yet implemented");
	}

	/* void removeType(Topic type)
	 * 
	 */
	@Test
	public void testRemoveType() {

		fail("Not yet implemented");
	}

	/* void mergeIn(Topic topic)
	 * 
	 */
	@Test
	public void testMergeIn() {

		fail("Not yet implemented");
	}

	/* void removeSubjectIdentifier(Locator identifier)
	 * 
	 */
	@Test
	public void testRemoveSubjectIdentifier() {

		fail("Not yet implemented");
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
