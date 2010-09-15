/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.tmapi.core.Occurrence;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;


/**
 * @author ch
 *
 */
public class TestOccurrenceImpl extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.OccurrenceImpl#getParent()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetParent/topic/1)
	 * has exactly one occurrence
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestOccurrenceImpl/testGetParent/topic/1"));
		assertNotNull(topic);
		
		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());
		
		Occurrence occurrence = occurrences.iterator().next();
		assertEquals(topic, occurrence.getParent());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.OccurrenceImpl#setType(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetType/topic/1)
	 * has exactly one occurrence with type != (http://TestOccurrenceImpl/testSetType/occurrencetype)
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetType/occurrencetype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetType() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestOccurrenceImpl/testSetType/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestOccurrenceImpl/testSetType/occurrencetype"));
		assertNotNull(type);
		
		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());
		
		Occurrence occurrence = occurrences.iterator().next();
		assertNotSame(type, occurrence.getType());
		occurrence.setType(type);
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.OccurrenceImpl#getType()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetType/topic/1)
	 * has exactly one occurrence with type (http://TestOccurrenceImpl/testGetType/occurrencetype)
	 */
	@Test
	public void testGetType() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestOccurrenceImpl/testGetType/topic/1"));
		assertNotNull(topic);
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestOccurrenceImpl/testGetType/occurrencetype"));
		assertNotNull(type);
		
		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());
		
		Occurrence occurrence = occurrences.iterator().next();
		assertEquals(type, occurrence.getType());
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#booleanValue()}.
//	 * 
//	 * Topic (http://TestOccurrenceImpl/testGetType/topic/1)
//	 * has two occurrences with datatype xsd:boolean
//	 * with one true and one false value
//	 */
//	@Test
//	public void testBooleanValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#coordinateValue()}.
//	 */
//	@Test
//	public void testCoordinateValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#dateTimeValue()}.
//	 */
//	@Test
//	public void testDateTimeValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#doubleValue()}.
//	 */
//	@Test
//	public void testDoubleValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.Boolean)}.
//	 */
//	@Test
//	public void testSetValueBoolean() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.Double)}.
//	 */
//	@Test
//	public void testSetValueDouble() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.util.Calendar)}.
//	 */
//	@Test
//	public void testSetValueCalendar() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.net.URI)}.
//	 */
//	@Test
//	public void testSetValueURI() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(de.topicmapslab.geotype.wgs84.Wgs84Coordinate)}.
//	 */
//	@Test
//	public void testSetValueWgs84Coordinate() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#uriValue()}.
//	 */
//	@Test
//	public void testUriValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#decimalValue()}.
//	 */
//	@Test
//	public void testDecimalValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#floatValue()}.
//	 */
//	@Test
//	public void testFloatValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#getDatatype()}.
//	 */
//	@Test
//	public void testGetDatatype() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#getValue()}.
//	 */
//	@Test
//	public void testGetValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#intValue()}.
//	 */
//	@Test
//	public void testIntValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#integerValue()}.
//	 */
//	@Test
//	public void testIntegerValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#locatorValue()}.
//	 */
//	@Test
//	public void testLocatorValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#longValue()}.
//	 */
//	@Test
//	public void testLongValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#surfaceValue()}.
//	 */
//	@Test
//	public void testSurfaceValue() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.String)}.
//	 */
//	@Test
//	public void testSetValueString() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(org.tmapi.core.Locator)}.
//	 */
//	@Test
//	public void testSetValueLocator() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.math.BigDecimal)}.
//	 */
//	@Test
//	public void testSetValueBigDecimal() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.math.BigInteger)}.
//	 */
//	@Test
//	public void testSetValueBigInteger() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(long)}.
//	 */
//	@Test
//	public void testSetValueLong() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(float)}.
//	 */
//	@Test
//	public void testSetValueFloat() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(int)}.
//	 */
//	@Test
//	public void testSetValueInt() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(de.topicmapslab.geotype.wgs84.Wgs84Circuit)}.
//	 */
//	@Test
//	public void testSetValueWgs84Circuit() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.String, org.tmapi.core.Locator)}.
//	 */
//	@Test
//	public void testSetValueStringLocator() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
//	 */
//	@Test
//	public void testGetScopeObject() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#addTheme(org.tmapi.core.Topic)}.
//	 */
//	@Test
//	public void testAddTheme() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScope()}.
//	 */
//	@Test
//	public void testGetScope() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#removeTheme(org.tmapi.core.Topic)}.
//	 */
//	@Test
//	public void testRemoveTheme() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
//	 */
//	@Test
//	public void testGetReifier() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
//	 */
//	@Test
//	public void testSetReifier() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
//	 */
//	@Test
//	public void testGetTopicMap() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
//	 */
//	@Test
//	public void testAddItemIdentifier() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
//	 */
//	@Test
//	public void testGetId() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
//	 */
//	@Test
//	public void testGetItemIdentifiers() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
//	 */
//	@Test
//	public void testRemove() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
//	 */
//	@Test
//	public void testRemoveBoolean() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
//	 */
//	@Test
//	public void testRemoveItemIdentifier() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getParent()}.
//	 */
//	@Test
//	public void testGetParent1() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getIdentity()}.
//	 */
//	@Test
//	public void testGetIdentity() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#compareTo(de.topicmapslab.majortom.model.core.IConstruct)}.
//	 */
//	@Test
//	public void testCompareTo() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#equals(java.lang.Object)}.
//	 */
//	@Test
//	public void testEqualsObject() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#isRemoved()}.
//	 */
//	@Test
//	public void testIsRemoved() {
//
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#setRemoved(boolean)}.
//	 */
//	@Test
//	public void testSetRemoved() {
//
//		fail("Not yet implemented");
//	}
}
