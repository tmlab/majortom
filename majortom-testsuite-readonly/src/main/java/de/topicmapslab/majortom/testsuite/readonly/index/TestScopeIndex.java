/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.index;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.index.ScopedIndex;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;

public class TestScopeIndex extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#open()}.
	 */
	@Test
	public void testOpen() {  

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		assertFalse(index.isOpen());
		index.open();
		assertTrue(index.isOpen());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociations(java.util.Collection)}.
	 * 
	 * 1 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme1)
	 * (http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme2)
	 * and 2 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme2)
	 * (http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme3)
	 */
	@Test
	public void testGetAssociationsCollectionOfIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsCollectionOfIScope/theme3"));
		assertNotNull(theme3);
		
		IScope scope1 = map.createScope(theme1, theme2);
		IScope scope2 = map.createScope(theme2, theme3);
		IScope scope3 = map.createScope(theme1, theme2, theme3);
		
		Collection<IScope> sc1 = new HashSet<IScope>();
		sc1.add(scope1);
		
		Collection<IScope> sc2 = new HashSet<IScope>();
		sc2.add(scope2);
				
		Collection<IScope> sc12 = new HashSet<IScope>();
		sc12.add(scope1);
		sc12.add(scope2);
		
		Collection<IScope> sc3 = new HashSet<IScope>();
		sc3.add(scope3);
				
		assertEquals(1, index.getAssociations(sc1).size());
		assertEquals(2, index.getAssociations(sc2).size());
		assertEquals(3, index.getAssociations(sc12).size());
		assertEquals(0, index.getAssociations(sc3).size());
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociations(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * 1 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsIScope/theme1)
	 * (http://TestScopeIndex/testGetAssociationsIScope/theme2)
	 * and 2 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsIScope/theme2)
	 * (http://TestScopeIndex/testGetAssociationsIScope/theme3)
	 */
	@Test
	public void testGetAssociationsIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScope/theme3"));
		assertNotNull(theme3);
		
		IScope scope1 = map.createScope(theme1, theme2);
		IScope scope2 = map.createScope(theme2, theme3);
		IScope scope3 = map.createScope(theme1, theme2, theme3);
		
		assertEquals(1, index.getAssociations(scope1).size());
		assertEquals(2, index.getAssociations(scope2).size());
		assertEquals(0, index.getAssociations(scope3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociations(de.topicmapslab.majortom.model.core.IScope[])}.
	 * 
	 * 1 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsIScopeArray/theme1)
	 * (http://TestScopeIndex/testGetAssociationsIScopeArray/theme2)
	 * and 2 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsIScopeArray/theme2)
	 * (http://TestScopeIndex/testGetAssociationsIScopeArray/theme3)
	 */
	@Test
	public void testGetAssociationsIScopeArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScopeArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScopeArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsIScopeArray/theme3"));
		assertNotNull(theme3);
		
		IScope scope1 = map.createScope(theme1, theme2);
		IScope scope2 = map.createScope(theme2, theme3);
		IScope scope3 = map.createScope(theme1, theme2, theme3);
				
		assertEquals(1, index.getAssociations(scope1).size());
		assertEquals(2, index.getAssociations(scope2).size());
		assertEquals(0, index.getAssociations(scope3).size());
		assertEquals(3, index.getAssociations(scope1, scope2).size());
		assertEquals(3, index.getAssociations(scope1, scope2, scope3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociations(org.tmapi.core.Topic)}.
	 * 
	 *  1 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsTopic/theme1)
	 * (http://TestScopeIndex/testGetAssociationsTopic/theme2)
	 * and 2 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsTopic/theme2)
	 * (http://TestScopeIndex/testGetAssociationsTopic/theme3)
	 */
	@Test
	public void testGetAssociationsTopic() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopic/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopic/theme3"));
		assertNotNull(theme3);
		
		assertEquals(1, index.getAssociations(theme1).size());
		assertEquals(3, index.getAssociations(theme2).size());
		assertEquals(2, index.getAssociations(theme3).size());
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociations(org.tmapi.core.Topic[], boolean)}.
	 * 
	 * 1 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme1)
	 * (http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme2)
	 * and 2 association with scope:
	 * (http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme2)
	 * (http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme3)
	 */
	@Test
	public void testGetAssociationsTopicArrayBoolean() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetAssociationsTopicArrayBoolean/theme3"));
		assertNotNull(theme3);
		
		assertEquals(3, index.getAssociations(new Topic[] {theme1, theme2}, false).size());
		assertEquals(1, index.getAssociations(new Topic[] {theme1, theme2}, true).size());
		
		assertEquals(3, index.getAssociations(new Topic[] {theme2, theme3}, false).size());
		assertEquals(2, index.getAssociations(new Topic[] {theme2, theme3}, true).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociationScopes()}.
	 */
	@Test
	public void testGetAssociationScopes() {
		
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getAssociationThemes()}.
	 */
	@Test
	public void testGetAssociationThemes() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getCharacteristics(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetCharacteristicsIScope/theme1)
	 * (http://TestScopeIndex/testGetCharacteristicsIScope/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetCharacteristicsIScope/theme2)
	 * (http://TestScopeIndex/testGetCharacteristicsIScope/theme3)
	 * 
	 */
	@Test
	public void testGetCharacteristicsIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScope/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		IScope s3 = map.createScope(theme1,theme2,theme3);
		
		assertEquals(1, index.getCharacteristics(s1).size());
		assertEquals(2, index.getCharacteristics(s2).size());
		assertEquals(0, index.getCharacteristics(s3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getCharacteristics(de.topicmapslab.majortom.model.core.IScope[])}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme1)
	 * (http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme2)
	 * (http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme3)
	 */
	@Test
	public void testGetCharacteristicsIScopeArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetCharacteristicsIScopeArray/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		
		assertEquals(1, index.getCharacteristics(new IScope[] {s1}).size());
		assertEquals(2, index.getCharacteristics(new IScope[] {s2}).size());
		assertEquals(3, index.getCharacteristics(new IScope[] {s1,s2}).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNames(java.util.Collection)}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetNamesCollectionOfIScope/theme1)
	 * (http://TestScopeIndex/testGetNamesCollectionOfIScope/theme2)
	 * and 2 names with scope:
	 * (http://TestScopeIndex/testGetNamesCollectionOfIScope/theme2)
	 * (http://TestScopeIndex/testGetNamesCollectionOfIScope/theme3)
	 */
	@Test
	public void testGetNamesCollectionOfIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesCollectionOfIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesCollectionOfIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesCollectionOfIScope/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		
		Collection<IScope> sc1 = new HashSet<IScope>();
		sc1.add(s1);
		
		Collection<IScope> sc2 = new HashSet<IScope>();
		sc2.add(s2);
		
		Collection<IScope> sc12 = new HashSet<IScope>();
		sc12.add(s1);
		sc12.add(s2);
		
		assertEquals(1, index.getNames(sc1).size());
		assertEquals(2, index.getNames(sc2).size());
		assertEquals(3, index.getNames(sc12).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNames(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetNamesIScope/theme1)
	 * (http://TestScopeIndex/testGetNamesIScope/theme2)
	 * and 2 names with scope:
	 * (http://TestScopeIndex/testGetNamesIScope/theme2)
	 * (http://TestScopeIndex/testGetNamesIScope/theme3)
	 */
	@Test
	public void testGetNamesIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScope/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		IScope s3 = map.createScope(theme1,theme2,theme3);
		
		assertEquals(1, index.getNames(s1).size());
		assertEquals(2, index.getNames(s2).size());
		assertEquals(0, index.getNames(s3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNames(de.topicmapslab.majortom.model.core.IScope[])}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetNamesIScopeArray/theme1)
	 * (http://TestScopeIndex/testGetNamesIScopeArray/theme2)
	 * and 2 names with scope:
	 * (http://TestScopeIndex/testGetNamesIScopeArray/theme2)
	 * (http://TestScopeIndex/testGetNamesIScopeArray/theme3)
	 */
	@Test
	public void testGetNamesIScopeArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScopeArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScopeArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesIScopeArray/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		
		assertEquals(1, index.getNames(new IScope[] {s1}).size());
		assertEquals(2, index.getNames(new IScope[] {s2}).size());
		assertEquals(3, index.getNames(new IScope[] {s1,s2}).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNames(org.tmapi.core.Topic)}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetNamesTopic/theme1)
	 * (http://TestScopeIndex/testGetNamesTopic/theme2)
	 * and 2 names with scope:
	 * (http://TestScopeIndex/testGetNamesTopic/theme2)
	 * (http://TestScopeIndex/testGetNamesTopic/theme3)
	 */
	@Test
	public void testGetNamesTopic() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopic/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopic/theme3"));
		assertNotNull(theme3);
		
		assertEquals(1, index.getNames(theme1).size());
		assertEquals(3, index.getNames(theme2).size());
		assertEquals(2, index.getNames(theme3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNames(org.tmapi.core.Topic[], boolean)}.
	 * 
	 * 1 name with scope:
	 * (http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme1)
	 * (http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme2)
	 * and 2 names with scope:
	 * (http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme2)
	 * (http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme3)
	 */
	@Test
	public void testGetNamesTopicArrayBoolean() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetNamesTopicArrayBoolean/theme3"));
		assertNotNull(theme3);
				
		assertEquals(3, index.getNames(new Topic[] {theme1,theme2},false).size());
		assertEquals(1, index.getNames(new Topic[] {theme1,theme2},true).size());
		
		assertEquals(3, index.getNames(new Topic[] {theme2,theme3},false).size());
		assertEquals(2, index.getNames(new Topic[] {theme2,theme3},true).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNameScopes()}.
	 */
	@Test
	public void testGetNameScopes() {

		/// TODO implement	
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getNameThemes()}.
	 */
	@Test
	public void testGetNameThemes() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrences(java.util.Collection)}.
	 * 
	 * 1 occurrence with scope:
	 * (http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme1)
	 * (http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme2)
	 * (http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme3)
	 */
	@Test
	public void testGetOccurrencesCollectionOfIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesCollectionOfIScope/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		
		Collection<IScope> sc1 = new HashSet<IScope>();
		sc1.add(s1);
		
		Collection<IScope> sc2 = new HashSet<IScope>();
		sc2.add(s2);
		
		Collection<IScope> sc12 = new HashSet<IScope>();
		sc12.add(s1);
		sc12.add(s2);
		
		assertEquals(1, index.getOccurrences(sc1).size());
		assertEquals(2, index.getOccurrences(sc2).size());
		assertEquals(3, index.getOccurrences(sc12).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrences(de.topicmapslab.majortom.model.core.IScope)}.
	 * 
	 * 1 occurrence with scope:
	 * (http://TestScopeIndex/testGetOccurrencesIScope/theme1)
	 * (http://TestScopeIndex/testGetOccurrencesIScope/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetOccurrencesIScope/theme2)
	 * (http://TestScopeIndex/testGetOccurrencesIScope/theme3)
	 */
	@Test
	public void testGetOccurrencesIScope() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScope/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScope/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScope/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		IScope s3 = map.createScope(theme1,theme2,theme3);
		
		assertEquals(1, index.getOccurrences(s1).size());
		assertEquals(2, index.getOccurrences(s2).size());
		assertEquals(0, index.getOccurrences(s3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrences(de.topicmapslab.majortom.model.core.IScope[])}.
	 * 
	 * 1 occurrence with scope:
	 * (http://TestScopeIndex/testGetOccurrencesIScopeArray/theme1)
	 * (http://TestScopeIndex/testGetOccurrencesIScopeArray/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetOccurrencesIScopeArray/theme2)
	 * (http://TestScopeIndex/testGetOccurrencesIScopeArray/theme3)
	 */
	@Test
	public void testGetOccurrencesIScopeArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScopeArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScopeArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesIScopeArray/theme3"));
		assertNotNull(theme3);
		
		IScope s1 = map.createScope(theme1,theme2);
		IScope s2 = map.createScope(theme2,theme3);
		
		assertEquals(1, index.getOccurrences(new IScope[] {s1}).size());
		assertEquals(2, index.getOccurrences(new IScope[] {s2}).size());
		assertEquals(3, index.getOccurrences(new IScope[] {s1,s2}).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrences(org.tmapi.core.Topic)}.
	 * 
	 * 1 occurrence with scope:
	 * (http://TestScopeIndex/testGetOccurrencesTopic/theme1)
	 * (http://TestScopeIndex/testGetOccurrencesTopic/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetOccurrencesTopic/theme2)
	 * (http://TestScopeIndex/testGetOccurrencesTopic/theme3)
	 */
	@Test
	public void testGetOccurrencesTopic() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopic/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopic/theme3"));
		assertNotNull(theme3);
		
		assertEquals(1, index.getOccurrences(theme1).size());
		assertEquals(3, index.getOccurrences(theme2).size());
		assertEquals(2, index.getOccurrences(theme3).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrences(org.tmapi.core.Topic[], boolean)}.
	 * 
	 * 1 occurrence with scope:
	 * (http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme1)
	 * (http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme2)
	 * and 2 occurrences with scope:
	 * (http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme2)
	 * (http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme3)
	 */
	@Test
	public void testGetOccurrencesTopicArrayBoolean() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetOccurrencesTopicArrayBoolean/theme3"));
		assertNotNull(theme3);
				
		assertEquals(3, index.getOccurrences(new Topic[] {theme1,theme2},false).size());
		assertEquals(1, index.getOccurrences(new Topic[] {theme1,theme2},true).size());
		
		assertEquals(3, index.getOccurrences(new Topic[] {theme2,theme3},false).size());
		assertEquals(2, index.getOccurrences(new Topic[] {theme2,theme3},true).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrenceScopes()}.
	 */
	@Test
	public void testGetOccurrenceScopes() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getOccurrenceThemes()}.
	 */
	@Test
	public void testGetOccurrenceThemes() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScopables(de.topicmapslab.majortom.model.core.IScope)}.
	 */
	@Test
	public void testGetScopablesIScope() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScopables(de.topicmapslab.majortom.model.core.IScope[])}.
	 * 
	 */
	@Test
	public void testGetScopablesIScopeArray() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScope(java.util.Collection)}.
	 * 
	 * 1 name with scope
	 * (http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme1)
	 * (http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme2)
	 * and 1 occurrence with scope
	 * (http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme1)
	 * (http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme2)
	 * (http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme3)
	 */
	@Test
	public void testGetScopeCollectionOfQextendsTopic() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeCollectionOfQextendsTopic/theme3"));
		assertNotNull(theme3);
		
		Collection<Topic> goodScope1 = new HashSet<Topic>();
		goodScope1.add(theme1);
		goodScope1.add(theme2);
		
		Collection<Topic> goodScope2 = new HashSet<Topic>();
		goodScope2.add(theme1);
		goodScope2.add(theme2);
		goodScope2.add(theme3);
		
		Collection<Topic> badScope1 = new HashSet<Topic>();
		badScope1.add(theme1);
		badScope1.add(theme3);
		
		Collection<Topic> badScope2 = new HashSet<Topic>();
		badScope2.add(theme2);
		badScope2.add(theme3);
		
		assertNotNull(index.getScope(goodScope1));
		assertNotNull(index.getScope(goodScope2));
		assertNull(index.getScope(badScope1));
		assertNull(index.getScope(badScope2));

	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScope(org.tmapi.core.Topic[])}.
	 * 
	 * 1 name with scope
	 * (http://TestScopeIndex/testGetScopeTopicArray/theme1)
	 * (http://TestScopeIndex/testGetScopeTopicArray/theme2)
	 * and 1 occurrence with scope
	 * (http://TestScopeIndex/testGetScopeTopicArray/theme1)
	 * (http://TestScopeIndex/testGetScopeTopicArray/theme2)
	 * (http://TestScopeIndex/testGetScopeTopicArray/theme3)
	 */
	@Test
	public void testGetScopeTopicArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeTopicArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopeTopicArray/theme3"));
		assertNotNull(theme3);
		
		assertNotNull(index.getScope(theme1, theme2));
		assertNotNull(index.getScope(theme1, theme2, theme3));
		assertNull(index.getScope(theme1, theme3));
		assertNull(index.getScope(theme2, theme3));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScopes(java.util.Collection, boolean)}.
	 * 
	 * 1 name with scope
	 * (http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme1)
	 * (http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme2)
	 * and 1 occurrence with scope
	 * (http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme1)
	 * (http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme2)
	 * (http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme3)
	 * 
	 * topic http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme4
	 */
	@Test
	public void testGetScopesCollectionOfTopicBoolean() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme3"));
		assertNotNull(theme3);
		
		ITopic theme4 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesCollectionOfTopicBoolean/theme4"));
		assertNotNull(theme4);
		
		Collection<Topic> scope1 = new HashSet<Topic>();
		scope1.add(theme1);
		scope1.add(theme2);
		scope1.add(theme4);
		
		Collection<Topic> scope2 = new HashSet<Topic>();
		scope2.add(theme1);
		scope2.add(theme2);
		scope2.add(theme3);
		scope2.add(theme4);
		
		assertEquals(0, index.getScopes(scope1, true).size());
		assertEquals(2, index.getScopes(scope1, false).size());

		assertEquals(0, index.getScopes(scope2, true).size());
		assertEquals(2, index.getScopes(scope2, false).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScopes(org.tmapi.core.Topic[])}. /// TODO clear with sven the exspected behaviour of those methods and update major tom docu
	 * 
	 * 1 name with scope
	 * (http://TestScopeIndex/testGetScopesTopicArray/theme1)
	 * (http://TestScopeIndex/testGetScopesTopicArray/theme2)
	 * and 1 occurrence with scope
	 * (http://TestScopeIndex/testGetScopesTopicArray/theme1)
	 * (http://TestScopeIndex/testGetScopesTopicArray/theme2)
	 * (http://TestScopeIndex/testGetScopesTopicArray/theme3)
	 */
	@Test
	public void testGetScopesTopicArray() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArray/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArray/theme3"));
		assertNotNull(theme3);
	
		assertEquals(1, index.getScopes(theme3).size());
		assertEquals(2, index.getScopes(theme3, theme2).size());

	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getScopes(org.tmapi.core.Topic[], boolean)}.
	 * 
	 * 1 name with scope
	 * (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme1)
	 * (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme2)
	 * and 1 occurrence with scope
	 * (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme1)
	 * (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme2)
	 * (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme3)
	 * 
	 * Topic (http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme4)
	 */
	@Test
	public void testGetScopesTopicArrayBoolean() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme2"));
		assertNotNull(theme2);
		
		ITopic theme3 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme3"));
		assertNotNull(theme3);
		
		ITopic theme4 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestScopeIndex/testGetScopesTopicArrayBoolean/theme4"));
		assertNotNull(theme4);
		
		assertEquals(0, index.getScopes(new Topic[] {theme1,theme2,theme4}, true).size());
		assertEquals(2, index.getScopes(new Topic[] {theme1,theme2,theme4}, false).size());

		assertEquals(0, index.getScopes(new Topic[] {theme1,theme2,theme3,theme4}, true).size());
		assertEquals(2, index.getScopes(new Topic[] {theme1,theme2,theme3,theme4}, false).size());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariants(java.util.Collection)}.
	 */
	@Test
	public void testGetVariantsCollectionOfIScope() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariants(de.topicmapslab.majortom.model.core.IScope)}.
	 */
	@Test
	public void testGetVariantsIScope() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariants(de.topicmapslab.majortom.model.core.IScope[])}.
	 */
	@Test
	public void testGetVariantsIScopeArray() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariants(org.tmapi.core.Topic)}.
	 */
	@Test
	public void testGetVariantsTopic() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariants(org.tmapi.core.Topic[], boolean)}.
	 */
	@Test
	public void testGetVariantsTopicArrayBoolean() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariantScopes()}.
	 */
	@Test
	public void testGetVariantScopes() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#getVariantThemes()}.
	 */
	@Test
	public void testGetVariantThemes() {
		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#isOpen()}.
	 */
	@Test
	public void testIsOpen() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		assertFalse(index.isOpen());
		index.open();
		assertTrue(index.isOpen());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.model.index.IScopeIndex#close()}.
	 */
	@Test(expected=TMAPIRuntimeException.class)
	public void testClose() {

		assertNotNull(map);
		IScopedIndex index = (IScopedIndex)map.getIndex(ScopedIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		index.close();
		assertFalse(index.isOpen());
		
		index.getAssociationScopes();
	}
}
