/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.tests.index;

import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.geotype.wgs84.Wgs84Degree;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class TestLiteralIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getBooleans(boolean)}
	 * .
	 */
	public void testGetBooleans() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		assertEquals(0, index.getBooleans(true).size());
		assertEquals(0, index.getBooleans(false).size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getBooleans(true).size());
		assertEquals(0, index.getBooleans(false).size());

		occurrence.setValue(true);
		assertEquals(1, index.getBooleans(true).size());
		assertTrue(index.getBooleans(true).contains(occurrence));
		assertEquals(0, index.getBooleans(false).size());

		occurrence.setValue(false);
		assertEquals(1, index.getBooleans(false).size());
		assertTrue(index.getBooleans(false).contains(occurrence));
		assertEquals(0, index.getBooleans(true).size());

		otherOccurrence.setValue(false);
		assertEquals(2, index.getBooleans(false).size());
		assertTrue(index.getBooleans(false).contains(occurrence));
		assertTrue(index.getBooleans(false).contains(otherOccurrence));
		assertEquals(0, index.getBooleans(true).size());

		otherOccurrence.remove();
		assertEquals(1, index.getBooleans(false).size());
		assertTrue(index.getBooleans(false).contains(occurrence));
		assertEquals(0, index.getBooleans(true).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCharacteristics(java.lang.String)}
	 * .
	 */
	public void testGetCharacteristicsString() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		assertEquals(0, index.getCharacteristics("Occ").size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		IName name = (IName) createTopic().createName("Name", createTopic());

		assertEquals(2, index.getCharacteristics("Occ").size());
		assertTrue(index.getCharacteristics("Occ").contains(occurrence));
		assertTrue(index.getCharacteristics("Occ").contains(otherOccurrence));

		name.setValue("Occ");
		assertEquals(3, index.getCharacteristics("Occ").size());
		assertTrue(index.getCharacteristics("Occ").contains(occurrence));
		assertTrue(index.getCharacteristics("Occ").contains(otherOccurrence));
		assertTrue(index.getCharacteristics("Occ").contains(name));

		occurrence.setValue(false);
		assertEquals(2, index.getCharacteristics("Occ").size());
		assertTrue(index.getCharacteristics("Occ").contains(otherOccurrence));
		assertTrue(index.getCharacteristics("Occ").contains(name));

		name.remove();
		assertEquals(1, index.getCharacteristics("Occ").size());
		assertTrue(index.getCharacteristics("Occ").contains(otherOccurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCharacteristics(org.tmapi.core.Locator)}
	 * .
	 */
	public void testGetCharacteristicsLocator() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		Locator xsdString = createLocator(XmlSchemeDatatypes.XSD_STRING);
		Locator xsdBool = createLocator(XmlSchemeDatatypes.XSD_BOOLEAN);

		assertEquals(0, index.getCharacteristics(xsdString).size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		IName name = (IName) createTopic().createName("Name", createTopic());

		assertEquals(3, index.getCharacteristics(xsdString).size());
		assertTrue(index.getCharacteristics(xsdString).contains(occurrence));
		assertTrue(index.getCharacteristics(xsdString).contains(name));
		assertTrue(index.getCharacteristics(xsdString).contains(otherOccurrence));

		name.setValue("Occ");
		assertEquals(3, index.getCharacteristics(xsdString).size());
		assertTrue(index.getCharacteristics(xsdString).contains(occurrence));
		assertTrue(index.getCharacteristics(xsdString).contains(name));
		assertTrue(index.getCharacteristics(xsdString).contains(otherOccurrence));

		occurrence.setValue(false);
		assertEquals(2, index.getCharacteristics(xsdString).size());
		assertTrue(index.getCharacteristics(xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristics(xsdString).contains(name));
		assertEquals(1, index.getCharacteristics(xsdBool).size());
		assertTrue(index.getCharacteristics(xsdBool).contains(occurrence));

		otherOccurrence.remove();
		assertEquals(1, index.getCharacteristics(xsdString).size());
		assertTrue(index.getCharacteristics(xsdString).contains(name));
		assertEquals(1, index.getCharacteristics(xsdBool).size());
		assertTrue(index.getCharacteristics(xsdBool).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCharacteristics(java.lang.String, org.tmapi.core.Locator)}
	 * .
	 */
	public void testGetCharacteristicsStringLocator() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		Locator xsdString = createLocator(XmlSchemeDatatypes.XSD_STRING);
		Locator xsdBool = createLocator(XmlSchemeDatatypes.XSD_BOOLEAN);

		assertEquals(0, index.getCharacteristics(xsdString).size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		IName name = (IName) createTopic().createName("Name", createTopic());

		assertEquals(2, index.getCharacteristics("Occ", xsdString).size());
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(occurrence));
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(otherOccurrence));

		name.setValue("Occ");
		assertEquals(3, index.getCharacteristics("Occ", xsdString).size());
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(occurrence));
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(name));

		occurrence.setValue(false);
		assertEquals(2, index.getCharacteristics("Occ", xsdString).size());
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(name));
		assertEquals(1, index.getCharacteristics("false", xsdBool).size());
		assertTrue(index.getCharacteristics("false", xsdBool).contains(occurrence));

		otherOccurrence.remove();
		assertEquals(1, index.getCharacteristics("Occ", xsdString).size());
		assertTrue(index.getCharacteristics("Occ", xsdString).contains(name));
		assertEquals(1, index.getCharacteristics("false", xsdBool).size());
		assertTrue(index.getCharacteristics("false", xsdBool).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCharacteristicsMatches(java.lang.String)}
	 * .
	 */
	public void testGetCharacteristicsMatchesString() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		Locator xsdString = createLocator(XmlSchemeDatatypes.XSD_STRING);
		Locator xsdBool = createLocator(XmlSchemeDatatypes.XSD_BOOLEAN);

		assertEquals(0, index.getCharacteristicsMatches("Occ", xsdString).size());
		assertEquals(0, index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		IName name = (IName) createTopic().createName("Name", createTopic());

		assertEquals(2, index.getCharacteristicsMatches("Occ", xsdString).size());
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(otherOccurrence));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(otherOccurrence));

		name.setValue("Occ");
		assertEquals(3, index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(name));
		assertEquals(3, index.getCharacteristicsMatches("Occ", xsdString).size());
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(name));

		occurrence.setValue(false);
		assertEquals(2, index.getCharacteristicsMatches("Occ", xsdString).size());
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(name));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(otherOccurrence));
		assertEquals(1, index.getCharacteristicsMatches("false", xsdBool).size());
		assertTrue(index.getCharacteristicsMatches("false", xsdBool).contains(occurrence));
		assertEquals(1, index.getCharacteristicsMatches(Pattern.compile("false"), xsdBool).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("false"), xsdBool).contains(occurrence));

		name.remove();
		assertEquals(1, index.getCharacteristicsMatches("Occ", xsdString).size());
		assertTrue(index.getCharacteristicsMatches("Occ", xsdString).contains(otherOccurrence));
		assertEquals(1, index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ"), xsdString).contains(otherOccurrence));

		occurrence.setValue("Occurrence");
		assertEquals(2, index.getCharacteristicsMatches("Occ.*", xsdString).size());
		assertTrue(index.getCharacteristicsMatches("Occ.*", xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ.*", xsdString).contains(occurrence));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ.*"), xsdString).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ.*"), xsdString).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ.*"), xsdString).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCharacteristicsMatches(java.lang.String, org.tmapi.core.Locator)}
	 * .
	 */
	public void testGetCharacteristicsMatchesStringLocator() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		assertEquals(0, index.getCharacteristicsMatches("Occ").size());
		assertEquals(0, index.getCharacteristicsMatches(Pattern.compile("Occ")).size());

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		IName name = (IName) createTopic().createName("Name", createTopic());

		assertEquals(2, index.getCharacteristicsMatches("Occ").size());
		assertTrue(index.getCharacteristicsMatches("Occ").contains(occurrence));
		assertTrue(index.getCharacteristicsMatches("Occ").contains(otherOccurrence));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ")).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(otherOccurrence));

		name.setValue("Occ");
		assertEquals(3, index.getCharacteristicsMatches(Pattern.compile("Occ")).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(occurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(name));
		assertEquals(3, index.getCharacteristicsMatches("Occ").size());
		assertTrue(index.getCharacteristicsMatches("Occ").contains(occurrence));
		assertTrue(index.getCharacteristicsMatches("Occ").contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ").contains(name));

		occurrence.setValue(false);
		assertEquals(2, index.getCharacteristicsMatches("Occ").size());
		assertTrue(index.getCharacteristicsMatches("Occ").contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ").contains(name));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ")).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(name));

		name.remove();
		assertEquals(1, index.getCharacteristicsMatches("Occ").size());
		assertTrue(index.getCharacteristicsMatches("Occ").contains(otherOccurrence));
		assertEquals(1, index.getCharacteristicsMatches(Pattern.compile("Occ")).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ")).contains(otherOccurrence));

		occurrence.setValue("Occurrence");
		assertEquals(2, index.getCharacteristicsMatches("Occ.*").size());
		assertTrue(index.getCharacteristicsMatches("Occ.*").contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches("Occ.*").contains(occurrence));
		assertEquals(2, index.getCharacteristicsMatches(Pattern.compile("Occ.*")).size());
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ.*")).contains(otherOccurrence));
		assertTrue(index.getCharacteristicsMatches(Pattern.compile("Occ.*")).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getCoordinates(de.topicmapslab.geotype.model.IGeoCoordinate, double)}
	 * .
	 */
	public void testGetCoordinatesIGeoCoordinateDouble() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);


		Wgs84Degree lat = new Wgs84Degree(38.692668);
		Wgs84Degree lng = new Wgs84Degree(-9.177944);
		// Lissabon Tejo Brücke
		Wgs84Coordinate coordinate = new Wgs84Coordinate(lat, lng);

		lat = new Wgs84Degree(52.5164);
		lng = new Wgs84Degree(13.3777);
		// Berlin Brandenburger Tor
		Wgs84Coordinate other = new Wgs84Coordinate(lat, lng);

		assertEquals(0, index.getCoordinates(coordinate).size());
		assertEquals(0, index.getCoordinates(other).size());

		occurrence.setValue(coordinate);
		assertEquals(1, index.getCoordinates(coordinate).size());
		assertTrue(index.getCoordinates(coordinate).contains(occurrence));
		assertEquals(0, index.getCoordinates(other).size());

		occurrence.setValue(other);
		assertEquals(1, index.getCoordinates(other).size());
		assertTrue(index.getCoordinates(other).contains(occurrence));
		assertEquals(0, index.getCoordinates(coordinate).size());

		otherOccurrence.setValue(coordinate);
		assertEquals(1, index.getCoordinates(other).size());
		assertTrue(index.getCoordinates(other).contains(occurrence));
		assertEquals(1, index.getCoordinates(coordinate).size());
		assertTrue(index.getCoordinates(coordinate).contains(otherOccurrence));

		otherOccurrence.setValue(other);
		assertEquals(2, index.getCoordinates(other).size());
		assertTrue(index.getCoordinates(other).contains(occurrence));
		assertTrue(index.getCoordinates(other).contains(otherOccurrence));
		assertEquals(0, index.getCoordinates(coordinate).size());

		otherOccurrence.remove();
		assertEquals(1, index.getCoordinates(other).size());
		assertTrue(index.getCoordinates(other).contains(occurrence));

		occurrence.setValue(0D);
		assertEquals(0, index.getCoordinates(other).size());
		occurrence.remove();
		
		occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		occurrence.setValue(coordinate);
		otherOccurrence.setValue(other);
		double distance = coordinate.getDistance(other);
		
		
		assertEquals(2, index.getCoordinates(other, distance+1).size());
		assertTrue(index.getCoordinates(other, distance+1).contains(occurrence));
		assertTrue(index.getCoordinates(other, distance+1).contains(otherOccurrence));
		System.out.println(distance);
//		System.out.println(coordinate.print());
//		System.out.println(other.print());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getDate(java.util.Calendar, java.util.Calendar)}
	 * .
	 */
	public void testGetDateTimeCalendarCalendar() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		Calendar calendar = new GregorianCalendar(2000, 10, 10);
		Calendar otherCalendar = new GregorianCalendar(2000, 10, 12);
		Calendar anotherCalendar = new GregorianCalendar(2002, 12, 12);

		assertEquals(0, index.getDateTime(calendar).size());

		occurrence.setValue(calendar);
		assertEquals(1, index.getDateTime(calendar).size());
		assertTrue(index.getDateTime(calendar).contains(occurrence));

		Calendar deviance = new GregorianCalendar(0, 0, 3);
		otherOccurrence.setValue(otherCalendar);
		assertEquals(1, index.getDateTime(calendar).size());
		assertTrue(index.getDateTime(calendar).contains(occurrence));
		assertEquals(2, index.getDateTime(calendar, deviance).size());
		assertTrue(index.getDateTime(calendar, deviance).contains(occurrence));
		assertTrue(index.getDateTime(calendar, deviance).contains(otherOccurrence));

		otherOccurrence.setValue(anotherCalendar);
		assertEquals(1, index.getDateTime(calendar).size());
		assertTrue(index.getDateTime(calendar).contains(occurrence));
		assertEquals(1, index.getDateTime(calendar, deviance).size());
		assertTrue(index.getDateTime(calendar, deviance).contains(occurrence));
		assertEquals(1, index.getDateTime(anotherCalendar).size());
		assertTrue(index.getDateTime(anotherCalendar).contains(otherOccurrence));

		deviance = new GregorianCalendar(0, 2, 2);
		otherOccurrence.setValue(otherCalendar);
		assertEquals(1, index.getDateTime(calendar).size());
		assertTrue(index.getDateTime(calendar).contains(occurrence));
		assertEquals(2, index.getDateTime(calendar, deviance).size());
		assertTrue(index.getDateTime(calendar, deviance).contains(occurrence));
		assertTrue(index.getDateTime(calendar, deviance).contains(otherOccurrence));

		otherOccurrence.setValue(false);
		assertEquals(1, index.getDateTime(calendar).size());
		assertTrue(index.getDateTime(calendar).contains(occurrence));
		assertEquals(1, index.getDateTime(calendar, deviance).size());
		assertTrue(index.getDateTime(calendar, deviance).contains(occurrence));

		occurrence.remove();
		assertEquals(0, index.getDateTime(calendar).size());
		assertEquals(0, index.getDateTime(calendar, deviance).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getDoubles(double, double)}
	 * .
	 */
	public void testGetDoublesDoubleDouble() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getDoubles(0D).size());

		occurrence.setValue(0D);
		assertEquals(1, index.getDoubles(0D).size());
		assertTrue(index.getDoubles(0D).contains(occurrence));

		otherOccurrence.setValue(0.00000001D);
		assertEquals(1, index.getDoubles(0D).size());
		assertTrue(index.getDoubles(0D).contains(occurrence));
		assertEquals(1, index.getDoubles(0.00000001D).size());
		assertTrue(index.getDoubles(.00000001D).contains(otherOccurrence));
		assertEquals(2, index.getDoubles(0.00000001D, .00000002D).size());
		assertTrue(index.getDoubles(.00000001D, .00000002D).contains(otherOccurrence));
		assertTrue(index.getDoubles(.00000001D, .00000002D).contains(occurrence));

		otherOccurrence.setValue(0F);
		assertEquals(1, index.getDoubles(0D).size());
		assertTrue(index.getDoubles(0D).contains(occurrence));
		assertEquals(1, index.getDoubles(0.00000001D, .00000002D).size());
		assertTrue(index.getDoubles(.00000001D, .00000002D).contains(occurrence));

		occurrence.remove();
		assertEquals(0, index.getDoubles(0D).size());
		assertEquals(0, index.getDoubles(0.00000001D, .00000002D).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getFloats(float, double)}
	 * .
	 */
	public void testGetFloatsFloatDouble() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getFloats(0F).size());

		occurrence.setValue(0F);
		assertEquals(1, index.getFloats(0F).size());
		assertTrue(index.getFloats(0F).contains(occurrence));

		otherOccurrence.setValue(0.00000001F);
		assertEquals(1, index.getFloats(0F).size());
		assertTrue(index.getFloats(0F).contains(occurrence));
		assertEquals(1, index.getFloats(0.00000001F).size());
		assertTrue(index.getFloats(.00000001F).contains(otherOccurrence));
		assertEquals(2, index.getFloats(0.00000001F, .00000002D).size());
		assertTrue(index.getFloats(.00000001F, .00000002F).contains(otherOccurrence));
		assertTrue(index.getFloats(.00000001F, .00000002F).contains(occurrence));

		otherOccurrence.setValue(0D);
		assertEquals(1, index.getFloats(0F).size());
		assertTrue(index.getFloats(0F).contains(occurrence));
		assertEquals(1, index.getFloats(0.00000001F, .00000002F).size());
		assertTrue(index.getFloats(.00000001F, .00000002F).contains(occurrence));

		occurrence.remove();
		assertEquals(0, index.getFloats(0F).size());
		assertEquals(0, index.getFloats(0.00000001F, .00000002F).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getIntegers(int, double)}
	 * .
	 */
	public void testGetIntegersIntDouble() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getIntegers(0).size());

		occurrence.setValue(0);
		assertEquals(1, index.getIntegers(0).size());
		assertTrue(index.getIntegers(0).contains(occurrence));

		otherOccurrence.setValue(1);
		assertEquals(1, index.getIntegers(0).size());
		assertTrue(index.getIntegers(0).contains(occurrence));
		assertEquals(1, index.getIntegers(1).size());
		assertTrue(index.getIntegers(1).contains(otherOccurrence));
		assertEquals(2, index.getIntegers(0, 1).size());
		assertTrue(index.getIntegers(0, 1).contains(otherOccurrence));
		assertTrue(index.getIntegers(0, 1).contains(occurrence));

		otherOccurrence.setValue(0L);
		assertEquals(1, index.getIntegers(0).size());
		assertTrue(index.getIntegers(0).contains(occurrence));
		assertEquals(1, index.getIntegers(0, 1).size());
		assertTrue(index.getIntegers(0, 1).contains(occurrence));

		occurrence.remove();
		assertEquals(0, index.getIntegers(0).size());
		assertEquals(0, index.getIntegers(0, 1).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getLongs(long, double)}
	 * .
	 */
	public void testGetLongsLongDouble() {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getLongs(0).size());

		occurrence.setValue(0L);
		assertEquals(1, index.getLongs(0).size());
		assertTrue(index.getLongs(0).contains(occurrence));

		otherOccurrence.setValue(1L);
		assertEquals(1, index.getLongs(0).size());
		assertTrue(index.getLongs(0).contains(occurrence));
		assertEquals(1, index.getLongs(1).size());
		assertTrue(index.getLongs(1).contains(otherOccurrence));
		assertEquals(2, index.getLongs(0, 1).size());
		assertTrue(index.getLongs(0, 1).contains(otherOccurrence));
		assertTrue(index.getLongs(0, 1).contains(occurrence));

		otherOccurrence.setValue(0);
		assertEquals(1, index.getLongs(0).size());
		assertTrue(index.getLongs(0).contains(occurrence));
		assertEquals(1, index.getLongs(0, 1).size());
		assertTrue(index.getLongs(0, 1).contains(occurrence));

		occurrence.remove();
		assertEquals(0, index.getLongs(0).size());
		assertEquals(0, index.getLongs(0, 1).size());
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryLiteralIndex#getUris(java.net.URI)}
	 * .
	 */
	public void testGetUris() throws Exception {
		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false);
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		URI uri = new URI("http://psi.example.org/test");
		URI otherURI = new URI("http://psi.example.org/2");

		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		IOccurrence otherOccurrence = (IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);

		assertEquals(0, index.getUris(uri).size());
		assertEquals(0, index.getUris(otherURI).size());

		occurrence.setValue(uri);
		assertEquals(1, index.getUris(uri).size());
		assertTrue(index.getUris(uri).contains(occurrence));
		assertEquals(0, index.getUris(otherURI).size());

		otherOccurrence.setValue(otherURI);
		assertEquals(1, index.getUris(uri).size());
		assertTrue(index.getUris(uri).contains(occurrence));
		assertEquals(1, index.getUris(otherURI).size());
		assertTrue(index.getUris(otherURI).contains(otherOccurrence));

		occurrence.setValue(otherURI);
		assertEquals(0, index.getUris(uri).size());
		assertEquals(2, index.getUris(otherURI).size());
		assertTrue(index.getUris(otherURI).contains(otherOccurrence));
		assertTrue(index.getUris(otherURI).contains(occurrence));

		otherOccurrence.remove();
		assertEquals(1, index.getUris(otherURI).size());
		assertTrue(index.getUris(otherURI).contains(occurrence));

		occurrence.setValue(0D);
		assertEquals(0, index.getUris(otherURI).size());
	}
}
