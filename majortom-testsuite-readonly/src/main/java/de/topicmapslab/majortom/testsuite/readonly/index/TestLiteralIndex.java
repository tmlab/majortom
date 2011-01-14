package de.topicmapslab.majortom.testsuite.readonly.index;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.GregorianCalendar;

import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.index.LiteralIndex;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

public class TestLiteralIndex extends AbstractTest {

	/**
	 * Topic map has one occurrence with datatype xsd:boolean and value <code>true</code>
	 * and one occurrence with datatype xsd:boolean and value <code>false</code>
	 */
	@Test
	public void testDoGetBooleansBoolean() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characterostics = index.getBooleans(true);
		
		assertNotNull(characterostics);
		assertEquals(1, characterostics.size());
		
		characterostics = index.getBooleans(false);
		
		assertNotNull(characterostics);
		assertEquals(1, characterostics.size());
	}

	/**
	 * Topic Map has exactly one characteristic with datatype "http://testDoGetCharacteristicsLocator/datatype1"
	 * and no characteristic with datatype  "http://testDoGetCharacteristicsLocator/datatype2"
	 */
	@Test
	public void testDoGetCharacteristicsLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characterostics = index.getCharacteristics(map.createLocator("http://testDoGetCharacteristicsLocator/datatype1"));
		
		assertNotNull(characterostics);
		assertEquals(1, characterostics.size());
		
		characterostics = index.getCharacteristics(map.createLocator("http://testDoGetCharacteristicsLocator/datatype2"));
		
		assertNotNull(characterostics);
		assertTrue(characterostics.isEmpty());
	}
	
	/**
	 * Topic map has one name with value "testDoGetCharacteristicsString_name"
	 * and one occurrence with value "testDoGetCharacteristicsString_occurrence"
	 * and no characteristic with value "testDoGetCharacteristicsString_other"
	 */
	@Test
	public void testDoGetCharacteristicsString() {
	
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getCharacteristics("testDoGetCharacteristicsString_name");
				
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getCharacteristics("testDoGetCharacteristicsString_occurrence");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getCharacteristics("testDoGetCharacteristicsString_other");
		
		assertNotNull(characteristics);
		assertTrue(characteristics.isEmpty());
		
	}

	/**
	 * Exist one occurrence of datatype "http://testDoGetCharacteristicsStringLocator" with value "testDoGetCharacteristicsStringLocator1"
	 * and no occurrence  of datatype "http://testDoGetCharacteristicsStringLocator" with value "testDoGetCharacteristicsStringLocator2"
	 * but a occurrence of datatype "xsd:string" with value "testDoGetCharacteristicsStringLocator2"
	 */
	@Test
	public void testDoGetCharacteristicsStringLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Locator l = map.createLocator("http://testDoGetCharacteristicsStringLocator");
		
		Collection<ICharacteristics> characteristics = index.getCharacteristics("testDoGetCharacteristicsStringLocator1", l);
				
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getCharacteristics("testDoGetCharacteristicsStringLocator2", l);
		
		assertNotNull(characteristics);
		assertTrue(characteristics.isEmpty());
		
		characteristics = index.getCharacteristics("testDoGetCharacteristicsStringLocator2");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * Exist one occurrence with value "123(45)6789" with datatype http://testDoGetCharacteristicsMatches
	 * and one name with value "234567890"
	 * 
	 */
	@Test
	public void testDoGetCharacteristicsMatchesPattern() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getCharacteristicsMatches("678");
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getCharacteristicsMatches("678");
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getCharacteristicsMatches("^23");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getCharacteristicsMatches("6789$");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
	}

	/**
	 * use from previous
	 */
	@Test
	public void testDoGetCharacteristicsMatchesPatternLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getCharacteristicsMatches("678");
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getCharacteristicsMatches("678",map.createLocator("http://testDoGetCharacteristicsMatches"));
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * topic map hat following occurrences with datatype "http://en.wikipedia.org/wiki/World_Geodetic_System_1984"
	 * "1.00000;2.00000"
	 * "1.0;2.0"
	 * "2.0;1.0"
	 */
	@Test
	public void testDoGetCoordinatesWgs84Coordinate() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getCoordinates(new Wgs84Coordinate(1.0, 2.0));
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getCoordinates(new Wgs84Coordinate(2.0, 1.0));
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getCoordinates(new Wgs84Coordinate(1.0, 2.1));
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * use the same data as the previous test
	 */
	@Test
	public void testDoGetCoordinatesWgs84CoordinateDouble() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getCoordinates(new Wgs84Coordinate(1.0, 2.0));
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getCoordinates(new Wgs84Coordinate(1.0, 2.0),3600); // use deviation
		
		assertNotNull(characteristics);
		assertEquals(3, characteristics.size());
		
	}

	/**
	 * Topic map has exactly one occurrence with datatype "http://testDoGetDatatypeAwaresLocator"
	 * and no occurrence with datatype "http://testDoGetDatatypeAwaresLocator2"
	 */
	@Test
	public void testDoGetDatatypeAwaresLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<IDatatypeAware> da = index.getDatatypeAwares(map.createLocator("http://testDoGetDatatypeAwaresLocator"));
		
		assertNotNull(da);
		assertEquals(1, da.size());
		
		da = index.getDatatypeAwares(map.createLocator("http://testDoGetDatatypeAwaresLocator2"));
		
		assertNotNull(da);
		assertTrue(da.isEmpty());

	}

	/**
	 * It exist one occurrence with datatype "xsd:dateTime" and value 200-01-01T12:45:30
	 */
	@Test
	public void testDoGetDateTimeCalendar() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getDateTime(new GregorianCalendar(2000, 0, 1, 12, 45, 30));
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getDateTime(new GregorianCalendar(2000, 0, 1));
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use data from previous test
	 */
	@Test
	public void testDoGetDateTimeCalendarCalendar() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getDateTime(new GregorianCalendar(2000, 0, 1));
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
		characteristics = index.getDateTime(new GregorianCalendar(2000, 0, 1), new GregorianCalendar(0,0,1)); // use one day deviance
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * Exist exactly one occurrence with datatype xsd:double and value 3.1415926535
	 */
	@Test
	public void testDoGetDoublesDouble() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getDoubles(3.1415926535);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getDoubles(3.1415926536);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use data from previous test
	 */
	@Test
	public void testDoGetDoublesDoubleDouble() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getDoubles(3.1415926536);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
		characteristics = index.getDoubles(3.1415926536, 0.0000000001);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * Exist exactly one occurrence with datatype xsd:float and value 3.14159
	 */
	@Test
	public void testDoGetFloatsFloat() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getFloats(3.14159f);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getFloats(3.14158f);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use data from previous test
	 */
	@Test
	public void testDoGetFloatsFloatDouble() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getFloats(3.14158f);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
		characteristics = index.getFloats(3.14158f, 0.00001);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * Exist exactly one occurrence with datatype xsd:integer and value 123456
	 */
	@Test
	public void testDoGetIntegersInt() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getIntegers(123456);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getIntegers(123457);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use data from previous test
	 */
	@Test
	public void testDoGetIntegersIntDouble() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getIntegers(123457);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
		characteristics = index.getIntegers(123457, 1);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * Exist exactly one occurrence with datatype xsd:long and value 1111111111
	 */
	@Test
	public void testDoGetLongsLong() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getLongs(1111111111);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getLongs(1111111112);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use data from previous test
	 */
	@Test
	public void testDoGetLongsLongDouble() {
	
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<ICharacteristics> characteristics = index.getLongs(1111111112);
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
		characteristics = index.getLongs(1111111112, 1);
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * use name form test testDoGetCharacteristicsString
	 */
	@Test
	public void testDoGetNames() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Name> characteristics = index.getNames();
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
	}

	/**
	 * use name form test testDoGetCharacteristicsString
	 */
	@Test
	public void testDoGetNamesString() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Name> characteristics = index.getNames("testDoGetCharacteristicsString_name");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getNames("testDoGetCharacteristicsString_nam");
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * Use all occurrences from the other tests () TODO
	 */
	@Test
	public void testDoGetOccurrences() {
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Occurrence> characteristics = index.getOccurrences();
		
		System.out.println(characteristics.size());
		
		fail();
	}

	/**
	 * exist one occurrence with datatype http://testDoGetOccurrencesLocator
	 * and no with datatype http://testDoGetOccurrencesLocator2
	 */
	@Test
	public void testDoGetOccurrencesLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Occurrence> characteristics = index.getOccurrences(map.createLocator("http://testDoGetOccurrencesLocator"));
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getOccurrences(map.createLocator("http://testDoGetOccurrencesLocator2"));
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * exist one occurrence with value "testDoGetOccurrencesString"
	 * and no with value testDoGetOccurrencesLocator2
	 */
	@Test
	public void testDoGetOccurrencesString() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Occurrence> characteristics = index.getOccurrences("testDoGetOccurrencesString");
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getOccurrences("testDoGetOccurrencesString2");
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
	}

	/**
	 * exist one occurrence with value "testDoGetOccurrencesStringLocator" and datatype http://testDoGetOccurrencesStringLocator
	 * and no with value testDoGetOccurrencesStringLocator2 and datatype http://testDoGetOccurrencesStringLocator
	 * and one with value "testDoGetOccurrencesStringLocator" and datatype xsd:string
	 */
	@Test
	public void testDoGetOccurrencesStringLocator() {
		
		assertNotNull(map);
		ILiteralIndex index = (ILiteralIndex)map.getIndex(LiteralIndex.class);
		assertNotNull(index);
		index.open();
		assertTrue(index.isOpen());
		
		Collection<Occurrence> characteristics = index.getOccurrences("testDoGetOccurrencesStringLocator");
		
		assertNotNull(characteristics);
		assertEquals(2, characteristics.size());
		
		characteristics = index.getOccurrences("testDoGetOccurrencesStringLocator", map.createLocator("http://testDoGetOccurrencesStringLocator"));
		
		assertNotNull(characteristics);
		assertEquals(1, characteristics.size());
		
		characteristics = index.getOccurrences("testDoGetOccurrencesStringLocator2", map.createLocator("http://testDoGetOccurrencesStringLocator"));
		
		assertNotNull(characteristics);
		assertEquals(0, characteristics.size());
		
	}

	@Test
	public void testDoGetUrisURI() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoGetVariants() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoGetVariantsLocator() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoGetVariantsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoGetVariantsStringLocator() {
		fail("Not yet implemented");
	}

}
