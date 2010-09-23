/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.geotype.wgs84.Wgs84Degree;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author ch
 * 
 */
public class TestOccurrenceImpl extends AbstractTest {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.OccurrenceImpl#getParent()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetParent/topic/1) has exactly one
	 * occurrence
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetParent/topic/1"));
		assertNotNull(topic);

		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());

		Occurrence occurrence = occurrences.iterator().next();
		assertEquals(topic, occurrence.getParent());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.OccurrenceImpl#setType(org.tmapi.core.Topic)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetType/topic/1) has exactly one
	 * occurrence with type !=
	 * (http://TestOccurrenceImpl/testSetType/occurrencetype)
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetType/occurrencetype)
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetType() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetType/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetType/occurrencetype"));
		assertNotNull(type);

		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());

		Occurrence occurrence = occurrences.iterator().next();
		assertNotSame(type, occurrence.getType());
		occurrence.setType(type);

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.OccurrenceImpl#getType()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetType/topic/1) has exactly one
	 * occurrence with type
	 * (http://TestOccurrenceImpl/testGetType/occurrencetype)
	 */
	@Test
	public void testGetType() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetType/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetType/occurrencetype"));
		assertNotNull(type);

		Set<Occurrence> occurrences = topic.getOccurrences();
		assertEquals(1, occurrences.size());

		Occurrence occurrence = occurrences.iterator().next();
		assertEquals(type, occurrence.getType());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#booleanValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testBooleanValue/topic/1) has two
	 * occurrences with datatype xsd:boolean and type
	 * http://TestOccurrenceImpl/testBooleanValue/type-boolean with one true and
	 * one false value and one occurrence with datatype xsd:string
	 * http://TestOccurrenceImpl/testBooleanValue/type-string
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testBooleanValue() throws ParseException {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testBooleanValue/topic/1"));
		assertNotNull(topic);

		ITopic type_boolean = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testBooleanValue/type-boolean"));
		assertNotNull(type_boolean);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testBooleanValue/type-string"));
		assertNotNull(type_string);

		Set<Occurrence> booleans = topic.getOccurrences(type_boolean);
		assertEquals(2, booleans.size());

		Object[] ba = booleans.toArray();

		assertTrue((((IOccurrence) ba[0]).booleanValue().equals(true) && ((IOccurrence) ba[1])
				.booleanValue().equals(false))
				|| (((IOccurrence) ba[1]).booleanValue().equals(true) && ((IOccurrence) ba[0])
						.booleanValue().equals(false)));

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		try {
			o.booleanValue();
		} catch (Exception e) {
			assertTrue(e instanceof ParseException);
		}

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#coordinateValue()}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testCoordinateValue/topic/1) has 1
	 * occurrence of type
	 * (http://TestOccurrenceImpl/testCoordinateValue/type-geo) with datatype
	 * xsd:geoCoordinate and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testCoordinateValue/type-string) with datatype
	 * xsd:string
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testCoordinateValue() throws ParseException {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testCoordinateValue/topic/1"));
		assertNotNull(topic);

		ITopic type_geo = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testCoordinateValue/type-geo"));
		assertNotNull(type_geo);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testCoordinateValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type_geo).size());
		IOccurrence geo = (IOccurrence) topic.getOccurrences(type_geo)
				.iterator().next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		Wgs84Coordinate coord = geo.coordinateValue();
		assertNotNull(coord);

		try {
			str.coordinateValue();
		} catch (Exception e) {
			assertTrue(e instanceof ParseException);
		}

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#dateTimeValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testDateTimeValue/topic/1) has 1
	 * occurrence of type
	 * (http://TestOccurrenceImpl/testDateTimeValue/type-datetime) with datatype
	 * xsd:dateTime and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testDateTimeValue/type-string) with datatype
	 * xsd:string
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testDateTimeValue() throws ParseException {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDateTimeValue/topic/1"));
		assertNotNull(topic);

		ITopic type_datetime = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDateTimeValue/type-datetime"));
		assertNotNull(type_datetime);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDateTimeValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type_datetime).size());
		IOccurrence datetime = (IOccurrence) topic
				.getOccurrences(type_datetime).iterator().next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		Calendar cal = datetime.dateTimeValue();
		assertNotNull(cal);

		try {
			str.dateTimeValue();
		} catch (Exception e) {
			assertTrue(e instanceof ParseException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#doubleValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testDoubleValue/topic/1) has 1
	 * occurrence of type
	 * (http://TestOccurrenceImpl/testDoubleValue/type-double) with datatype
	 * xsd:double and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testDoubleValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testDoubleValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDoubleValue/topic/1"));
		assertNotNull(topic);

		ITopic type_double = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDoubleValue/type-double"));
		assertNotNull(type_double);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDoubleValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type_double).size());
		IOccurrence datetime = (IOccurrence) topic.getOccurrences(type_double)
				.iterator().next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		Double d = datetime.doubleValue();
		assertNotNull(d);

		try {
			str.dateTimeValue();
		} catch (Exception e) {
			assertTrue(e instanceof ParseException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.Boolean)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueBoolean/topic/1) has exactly
	 * 1 occurrence with datatype xsd:boolean
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueBoolean() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueBoolean/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_BOOLEAN));
		o.setValue(false);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.Double)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueDouble/topic/1) has exactly
	 * 1 occurrence with datatype xsd:double
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueDouble() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueDouble/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_DOUBLE));
		o.setValue(3.1415);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.util.Calendar)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueCalendar/topic/1) has
	 * exactly 1 occurrence with datatype xsd:dateTime
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueCalendar() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueCalendar/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_DATETIME));
		o.setValue(new GregorianCalendar(1601, 1, 1, 8, 14, 47));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.net.URI)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueURI/topic/1) has exactly 1
	 * occurrence with datatype xsd:anyUri
	 * 
	 * @throws URISyntaxException
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueURI() throws URISyntaxException {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueURI/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_ANYURI));
		o.setValue(new URI("http://justatest.com"));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(de.topicmapslab.geotype.wgs84.Wgs84Coordinate)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueWgs84Coordinate/topic/1) has
	 * exactly 1 occurrence with datatype xsd:geoCoordinate
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueWgs84Coordinate() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueWgs84Coordinate/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.WGS84_COORDINATE));
		o.setValue(new Wgs84Coordinate(new Wgs84Degree(47.0), new Wgs84Degree(
				11.0)));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#uriValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testUriValue/topic/1) has 1 occurrence
	 * of type (http://TestOccurrenceImpl/testUriValue/type-uri) with datatype
	 * xsd:anyUri and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testUriValue/type-string) with datatype
	 * xsd:string
	 * 
	 * @throws URISyntaxException
	 */
	@Test
	public void testUriValue() throws URISyntaxException {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testUriValue/topic/1"));
		assertNotNull(topic);

		ITopic type_uri = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testUriValue/type-uri"));
		assertNotNull(type_uri);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testUriValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type_uri).size());
		IOccurrence uri = (IOccurrence) topic.getOccurrences(type_uri)
				.iterator().next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		URI u = uri.uriValue();
		assertNotNull(u);

		try {
			str.uriValue();
		} catch (Exception e) {
			assertTrue(e instanceof ParseException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#decimalValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testDecimalValue/topic/1) has 1
	 * occurrence of type (http://TestOccurrenceImpl/testDecimalValue/type-dec)
	 * with datatype xsd:decimal and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testDecimalValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testDecimalValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDecimalValue/topic/1"));
		assertNotNull(topic);

		ITopic type_dec = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDecimalValue/type-dec"));
		assertNotNull(type_dec);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testDecimalValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type_dec).size());
		IOccurrence dec = (IOccurrence) topic.getOccurrences(type_dec)
				.iterator().next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		BigDecimal d = dec.decimalValue();
		assertNotNull(d);

		try {
			str.decimalValue();
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#floatValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testFloatValue/topic/1) has 1 occurrence
	 * of type (http://TestOccurrenceImpl/testFloatValue/type-float) with
	 * datatype xsd:float and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testFloatValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testFloatValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testFloatValue/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testFloatValue/type-float"));
		assertNotNull(type);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testFloatValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type).iterator()
				.next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		Float f = o.floatValue();
		assertNotNull(f);

		try {
			str.floatValue();
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#getDatatype()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetDatatype/topic/1) has 1
	 * occurrence with datatype xsd:float
	 * 
	 */
	@Test
	public void testGetDatatype() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetDatatype/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();
		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_FLOAT));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#getValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetValue/topic/1) has 1 occurrence
	 * of type with datatype xsd:string
	 * 
	 * TODO check other datatypes and compare values?
	 */
	@Test
	public void testGetValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetValue/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		String value = o.getValue();
		assertNotNull(value);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#intValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testIntValue/topic/1) has 1 occurrence
	 * of type (http://TestOccurrenceImpl/testIntValue/type-int) with datatype
	 * xsd:int and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testIntValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testIntValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntValue/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntValue/type-int"));
		assertNotNull(type);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type).iterator()
				.next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		int i = o.intValue();
		assertNotNull(i);

		try {
			str.intValue();
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#integerValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testIntegerValue/topic/1) has 1
	 * occurrence of type
	 * (http://TestOccurrenceImpl/testIntegerValue/type-integer) with datatype
	 * xsd:integer and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testIntegerValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testIntegerValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntegerValue/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntegerValue/type-integer"));
		assertNotNull(type);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testIntegerValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type).iterator()
				.next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		BigInteger i = o.integerValue();
		assertNotNull(i);

		try {
			str.integerValue();
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#locatorValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testLocatorValue/topic/1) has 1
	 * occurrence of type
	 * (http://TestOccurrenceImpl/testLocatorValue/type-locator) with datatype
	 * xsd:anyURI and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testLocatorValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testLocatorValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLocatorValue/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLocatorValue/type-locator"));
		assertNotNull(type);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLocatorValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type).iterator()
				.next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		Locator l = o.locatorValue();
		assertNotNull(l);

		try {
			str.locatorValue();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#longValue()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testLongValue/topic/1) has 1 occurrence
	 * of type (http://TestOccurrenceImpl/testLongValue/type-long) with datatype
	 * xsd:long and 1 occurrenceof type
	 * (http://TestOccurrenceImpl/testLongValue/type-string) with datatype
	 * xsd:string
	 */
	@Test
	public void testLongValue() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLongValue/topic/1"));
		assertNotNull(topic);

		ITopic type = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLongValue/type-long"));
		assertNotNull(type);
		ITopic type_string = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testLongValue/type-string"));
		assertNotNull(type_string);

		assertEquals(1, topic.getOccurrences(type).size());
		IOccurrence o = (IOccurrence) topic.getOccurrences(type).iterator()
				.next();

		assertEquals(1, topic.getOccurrences(type_string).size());
		IOccurrence str = (IOccurrence) topic.getOccurrences(type_string)
				.iterator().next();

		long l = o.longValue();
		assertNotNull(l);

		try {
			str.locatorValue();
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#surfaceValue()}.
	 */
	@Ignore
	@Test
	public void testSurfaceValue() {
		// overridden and ignored
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.String)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueString/topic/1) has exactly
	 * 1 occurrence with datatype xsd:string
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueString() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueString/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_STRING));
		o.setValue("test");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(org.tmapi.core.Locator)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueLocator/topic/1) has exactly
	 * 1 occurrence with datatype xsd:anyURI
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueLocator() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueLocator/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_ANYURI));
		o.setValue(map.createLocator("http://wontworkthis.way"));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.math.BigDecimal)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueBigDecimal/topic/1) has
	 * exactly 1 occurrence with datatype xsd:int
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueBigDecimal() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueBigDecimal/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_INT));
		o.setValue(666);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.math.BigInteger)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueBigInteger/topic/1) has
	 * exactly 1 occurrence with datatype xsd:integer
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueBigInteger() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueBigInteger/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_INTEGER));
		o.setValue(666);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(long)}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueLong/topic/1) has exactly 1
	 * occurrence with datatype xsd:long
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueLong() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueLong/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_LONG));
		o.setValue(666);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(float)}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueFloat/topic/1) has exactly 1
	 * occurrence with datatype xsd:float
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueFloat() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueFloat/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_FLOAT));
		o.setValue(3.14);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(int)}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueInt/topic/1) has exactly 1
	 * occurrence with datatype xsd:int
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueInt() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueInt/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.XSD_INT));
		o.setValue(666);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(de.topicmapslab.geotype.wgs84.Wgs84Circuit)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueWgs84Circuit/topic/1) has
	 * exactly 1 occurrence with datatype xsd:geoCoordinate /// TODO is
	 * geoCoordinate right here?
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueWgs84Circuit() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueWgs84Circuit/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(o.getDatatype(),
				map.createLocator(XmlSchemeDatatypes.WGS84_COORDINATE));
		o.setValue(new Wgs84Circuit(new Wgs84Coordinate(new Wgs84Degree(47.0),
				new Wgs84Degree(11.0)), 2));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.DataTypeAwareImpl#setValue(java.lang.String, org.tmapi.core.Locator)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetValueStringLocator/topic/1) has
	 * exactly 1 occurrence
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetValueStringLocator() {

		assertNotNull(map);

		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetValueStringLocator/topic/1"));
		assertNotNull(topic);

		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		o.setValue("new value",
				map.createLocator(XmlSchemeDatatypes.XSD_STRING));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetScopeObject/topic/1) has exactly
	 * 1 occurrence with single theme scope
	 * (http://TestOccurrenceImpl/testGetScopeObject/theme)
	 */
	@Test
	public void testGetScopeObject() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetScopeObject/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		IScope scope = o.getScopeObject();
		assertNotNull(scope);
		assertEquals(1, scope.getThemes().size());
		ITopic theme = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetScopeObject/theme"));
		assertNotNull(theme);
		assertTrue(scope.containsTheme(theme));

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ScopeableImpl#addTheme(org.tmapi.core.Topic)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testAddTheme/topic/1) has exactly 1
	 * occurrence without scope
	 * 
	 * Topic (http://TestOccurrenceImpl/testAddTheme/theme)
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testAddTheme() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testAddTheme/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		ITopic theme = (ITopic) map.getTopicBySubjectIdentifier(map
				.createLocator("http://TestOccurrenceImpl/testAddTheme/theme"));
		assertNotNull(theme);

		o.addTheme(theme);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ScopeableImpl#getScope()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetScope/topic/1) has exactly 1
	 * occurrence with single theme scope
	 * (http://TestOccurrenceImpl/testGetScope/theme)
	 */
	@Test
	public void testGetScope() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetScopeObject/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		Set<Topic> themes = o.getScope();
		assertEquals(1, themes.size());
		ITopic theme = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testGetScopeObject/theme"));
		assertNotNull(theme);
		assertTrue(themes.contains(theme));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ScopeableImpl#removeTheme(org.tmapi.core.Topic)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testRemoveTheme/topic/1) has exactly 1
	 * occurrence with single theme scope
	 * (http://TestOccurrenceImpl/testRemoveTheme/theme)
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testRemoveTheme() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testRemoveTheme/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		Set<Topic> themes = o.getScope();
		assertEquals(1, themes.size());
		ITopic theme = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testRemoveTheme/theme"));
		assertNotNull(theme);
		assertTrue(themes.contains(theme));

		o.removeTheme(theme);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	@Test
	public void testGetReifier() {

		// / TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetReifier/topic/1) has exactly 1
	 * occurrence
	 * 
	 * Topic (http://TestOccurrenceImpl/testSetReifier/ref)
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testSetReifier() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetReifier/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		ITopic ref = (ITopic) map.getTopicBySubjectIdentifier(map
				.createLocator("http://TestOccurrenceImpl/testSetReifier/ref"));
		assertNotNull(ref);

		o.setReifier(ref);

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetTopicMap/topic/1) has exactly 1
	 * occurrence
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testSetReifier/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		assertEquals(map, o.getTopicMap());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}
	 * .
	 * 
	 * Topic (http://TestOccurrenceImpl/testAddItemIdentifier/topic/1) has
	 * exactly 1 occurrence
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testAddItemIdentifier/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		Locator l = map
				.createLocator("http://TestOccurrenceImpl/testAddItemIdentifier/ii");
		assertNotNull(l);
		o.addItemIdentifier(l);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testGetId/topic/1) has exactly 1
	 * occurrence
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);
		ITopic topic = (ITopic) map.getTopicBySubjectIdentifier(map
				.createLocator("http://TestOccurrenceImpl/testGetId/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		String id = o.getId();
		assertNotNull(id);
		assertEquals(id, o.getId());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	@Test
	public void testGetItemIdentifiers() {

		// / TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testRemove/topic/1) has exactly 1
	 * occurrence
	 */
	@Test(expected = UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);
		ITopic topic = (ITopic) map.getTopicBySubjectIdentifier(map
				.createLocator("http://TestOccurrenceImpl/testRemove/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		o.remove();
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#remove(boolean)}.
	 * 
	 * Topic (http://TestOccurrenceImpl/testRemoveBoolean/topic/1) has exactly 1
	 * occurrence
	 */
	@Test
	public void testRemoveBoolean() {

		assertNotNull(map);
		ITopic topic = (ITopic) map
				.getTopicBySubjectIdentifier(map
						.createLocator("http://TestOccurrenceImpl/testRemoveBoolean/topic/1"));
		assertNotNull(topic);
		assertEquals(1, topic.getOccurrences().size());
		IOccurrence o = (IOccurrence) topic.getOccurrences().iterator().next();

		try {
			o.remove(true);
			fail("No exception thrown");
		} catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}

		try {
			o.remove(false);
			fail("No exception thrown");
		} catch (Exception e) {
			assertTrue(e instanceof UnmodifyableStoreException);
		}
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}
	 * .
	 */
	@Test
	public void testRemoveItemIdentifier() {

		// / TODO implement
		fail("Not yet implemented");
	}

	// /**
	// * Test method for {@link
	// de.topicmapslab.majortom.core.ConstructImpl#getIdentity()}.
	// */
	// @Test
	// public void testGetIdentity() {
	//
	// fail("Not yet implemented");
	// }

	// /**
	// * Test method for {@link
	// de.topicmapslab.majortom.core.ConstructImpl#compareTo(de.topicmapslab.majortom.model.core.IConstruct)}.
	// */
	// @Test
	// public void testCompareTo() {
	//
	// fail("Not yet implemented");
	// }

	// /**
	// * Test method for {@link
	// de.topicmapslab.majortom.core.ConstructImpl#equals(java.lang.Object)}.
	// */
	// @Test
	// public void testEqualsObject() {
	//
	// fail("Not yet implemented");
	// }

	// /**
	// * Test method for {@link
	// de.topicmapslab.majortom.core.ConstructImpl#isRemoved()}.
	// */
	// @Test
	// public void testIsRemoved() {
	//
	// fail("Not yet implemented");
	// }

	// /**
	// * Test method for {@link
	// de.topicmapslab.majortom.core.ConstructImpl#setRemoved(boolean)}.
	// */
	// @Test
	// public void testSetRemoved() {
	//
	// fail("Not yet implemented");
	// }
}
