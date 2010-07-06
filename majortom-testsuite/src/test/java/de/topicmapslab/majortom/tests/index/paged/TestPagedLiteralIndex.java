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
/**
 * 
 */
package de.topicmapslab.majortom.tests.index.paged;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.mecator.MecatorCoordinate;
import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.majortom.comparator.ConstructByItemIdentifierComparator;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class TestPagedLiteralIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getBooleans(boolean, int, int)}
	 * .
	 */
	public void testGetBooleansBooleanIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue(true);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getBooleans(true, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getBooleans(true, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getBooleans(true, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getBooleans(true, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristics(java.lang.String, int, int)}
	 * .
	 */
	public void testGetCharacteristicsStringIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), "Value", new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics("Value", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristics("Value", 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics("Value", i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristics("Value", 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristics(org.tmapi.core.Locator, int, int)}
	 * .
	 */
	public void testGetCharacteristicsLocatorIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Locator value = createLocator(XmlSchemeDatatypes.XSD_STRING);
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), "Value", new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristics(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristics(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristics(java.lang.String, org.tmapi.core.Locator, int, int)}
	 * .
	 */
	public void testGetCharacteristicsStringLocatorIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Locator value = createLocator(XmlSchemeDatatypes.XSD_STRING);
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), "Value", new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics("Value", value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristics("Value", value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristics("Value", value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristics("Value", value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristicsMatches(java.lang.String, int, int)}
	 * .
	 */
	public void testGetCharacteristicsMatchesStringIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), base + c + i, new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), base + c + i, new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(base + ".*", i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristicsMatches(base + ".*", 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(base + ".*", i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristicsMatches(base + ".*", 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristicsMatches(java.lang.String, org.tmapi.core.Locator, int, int)}
	 * .
	 */
	public void testGetCharacteristicsMatchesStringLocatorIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Locator value = createLocator(XmlSchemeDatatypes.XSD_STRING);
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), base + c + i, new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), base + c + i, new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(base + ".*", value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristicsMatches(base + ".*", value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(base + ".*", value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristicsMatches(base + ".*", value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristicsMatches(java.util.regex.Pattern, int, int)}
	 * .
	 */
	public void testGetCharacteristicsMatchesPatternIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		Pattern regexp = Pattern.compile(base + ".*");
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), base + c + i, new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), base + c + i, new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(regexp, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristicsMatches(regexp, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(regexp, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristicsMatches(regexp, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCharacteristicsMatches(java.util.regex.Pattern, org.tmapi.core.Locator, int, int)}
	 * .
	 */
	public void testGetCharacteristicsMatchesPatternLocatorIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}

		Locator value = createLocator(XmlSchemeDatatypes.XSD_STRING);
		ICharacteristics[] characteristics = new ICharacteristics[101];
		String base = "http://psi.example.org/";
		Pattern regexp = Pattern.compile(base + ".*");
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					characteristics[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), base + c + i, new Topic[0]);
				} else {
					characteristics[j] = (IName) createTopic().createName(createTopic(), base + c + i, new Topic[0]);
				}
				characteristics[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(regexp, value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCharacteristicsMatches(regexp, value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCharacteristicsMatches(regexp, value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(characteristics[i * 10], list.get(0));
			assertEquals(characteristics[i * 10 + 1], list.get(1));
			assertEquals(characteristics[i * 10 + 2], list.get(2));
			assertEquals(characteristics[i * 10 + 3], list.get(3));
			assertEquals(characteristics[i * 10 + 4], list.get(4));
			assertEquals(characteristics[i * 10 + 5], list.get(5));
			assertEquals(characteristics[i * 10 + 6], list.get(6));
			assertEquals(characteristics[i * 10 + 7], list.get(7));
			assertEquals(characteristics[i * 10 + 8], list.get(8));
			assertEquals(characteristics[i * 10 + 9], list.get(9));
		}
		list = index.getCharacteristicsMatches(regexp, value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(characteristics[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getCoordinates(de.topicmapslab.geotype.model.IGeoCoordinate, int, int)}
	 * .
	 */
	public void testGetCoordinatesIGeoCoordinateIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		IGeoCoordinate value = new MecatorCoordinate(20, 20);
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue(value);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getCoordinates(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getCoordinates(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getCoordinates(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getCoordinates(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getDatatypeAwares(de.topicmapslab.majortom.model.core.ILocator, int, int)}
	 * .
	 */
	public void testGetDatatypeAwaresILocatorIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Locator value = createLocator(XmlSchemeDatatypes.XSD_DOUBLE);
		IDatatypeAware[] datatypeAwares = new IDatatypeAware[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				if (j % 2 == 0) {
					datatypeAwares[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				} else {
					datatypeAwares[j] = (IVariant) createTopic().createName(createTopic(), "Value", new Topic[0]).createVariant("Value", createTopic());
				}
				datatypeAwares[j].setValue(Math.random());
				datatypeAwares[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<IDatatypeAware> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDatatypeAwares(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDatatypeAwares(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<IDatatypeAware> comp = new ConstructByItemIdentifierComparator<IDatatypeAware>();

		for (int i = 0; i < 10; i++) {
			list = index.getDatatypeAwares(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(datatypeAwares[i * 10], list.get(0));
			assertEquals(datatypeAwares[i * 10 + 1], list.get(1));
			assertEquals(datatypeAwares[i * 10 + 2], list.get(2));
			assertEquals(datatypeAwares[i * 10 + 3], list.get(3));
			assertEquals(datatypeAwares[i * 10 + 4], list.get(4));
			assertEquals(datatypeAwares[i * 10 + 5], list.get(5));
			assertEquals(datatypeAwares[i * 10 + 6], list.get(6));
			assertEquals(datatypeAwares[i * 10 + 7], list.get(7));
			assertEquals(datatypeAwares[i * 10 + 8], list.get(8));
			assertEquals(datatypeAwares[i * 10 + 9], list.get(9));
		}
		list = index.getDatatypeAwares(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(datatypeAwares[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getDateTime(java.util.Calendar, int, int)}
	 * .
	 */
	public void testGetDateTimeCalendarIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Calendar value = new GregorianCalendar(2010, 6, 10, 10, 0, 0);
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue(value);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDateTime(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDateTime(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getDateTime(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getDateTime(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getDateTime(java.util.Calendar, java.util.Calendar, int, int)}
	 * .
	 */
	public void testGetDateTimeCalendarCalendarIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Calendar value = new GregorianCalendar(2010, 6, 10, 10, 0, 0);
		Calendar other = new GregorianCalendar(2010, 7, 10, 10, 0, 0);
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				if (j % 2 == 0) {
					occurrences[j].setValue(value);
				} else {
					occurrences[j].setValue(other);
				}
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		Calendar deviance = new GregorianCalendar(0, 2, 1, 0, 0, 0);
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDateTime(value, deviance, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDateTime(value, deviance, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getDateTime(value, deviance, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getDateTime(value, deviance, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getDoubles(double, int, int)}
	 * .
	 */
	public void testGetDoublesDoubleIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		double value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue(value);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDoubles(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDoubles(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getDoubles(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getDoubles(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getDoubles(double, double, int, int)}
	 * .
	 */
	public void testGetDoublesDoubleDoubleIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		double value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue(value + Math.random() * 0.5D);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getDoubles(value, 0.5D, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getDoubles(value, 0.5D, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getDoubles(value, 0.5D, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getDoubles(value, 0.5D, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getFloats(float, int, int)}
	 * .
	 */
	public void testGetFloatsFloatIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		float value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((float) (value));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getFloats(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getFloats(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getFloats(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getFloats(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getFloats(float, double, int, int)}
	 * .
	 */
	public void testGetFloatsFloatDoubleIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		float value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((float) (value + Math.random() * 0.5F));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getFloats(value, 0.5D, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getFloats(value, 0.5D, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getFloats(value, 0.5D, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getFloats(value, 0.5D, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getIntegers(int, int, int)}
	 * .
	 */
	public void testGetIntegersIntIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		int value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((int) (value));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getIntegers(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getIntegers(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getIntegers(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getIntegers(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getIntegers(int, double, int, int)}
	 * .
	 */
	public void testGetIntegersIntDoubleIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		int value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((int) (value + Math.random() * 5));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getIntegers(value, 5, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getIntegers(value, 5, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getIntegers(value, 5, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getIntegers(value, 5, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getLongs(long, int, int)}
	 * .
	 */
	public void testGetLongsLongIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		long value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((long) (value));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getLongs(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getLongs(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getLongs(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getLongs(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getLongs(long, double, int, int)}
	 * .
	 */
	public void testGetLongsLongDoubleIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		long value = 15;
		IOccurrence[] occurrences = new IOccurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = (IOccurrence) createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].setValue((long) (value + Math.random() * 5));
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}
		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getLongs(value, 5, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getLongs(value, 5, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getLongs(value, 5, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getLongs(value, 5, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getNames(int, int)}
	 * .
	 */
	public void testGetNamesIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Name[] names = new Name[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				names[j] = createTopic().createName(createTopic(), "Value", new Topic[0]);
				names[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<IName> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getNames(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getNames(100, 10);
		assertEquals(1, list.size());

		Comparator<IName> comp = new ConstructByItemIdentifierComparator<IName>();

		for (int i = 0; i < 10; i++) {
			list = index.getNames(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(names[i * 10], list.get(0));
			assertEquals(names[i * 10 + 1], list.get(1));
			assertEquals(names[i * 10 + 2], list.get(2));
			assertEquals(names[i * 10 + 3], list.get(3));
			assertEquals(names[i * 10 + 4], list.get(4));
			assertEquals(names[i * 10 + 5], list.get(5));
			assertEquals(names[i * 10 + 6], list.get(6));
			assertEquals(names[i * 10 + 7], list.get(7));
			assertEquals(names[i * 10 + 8], list.get(8));
			assertEquals(names[i * 10 + 9], list.get(9));
		}
		list = index.getNames(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(names[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getOccurrences(int, int)}
	 * .
	 */
	public void testGetOccurrencesIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Occurrence[] occurrences = new Occurrence[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<IOccurrence> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getOccurrences(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getOccurrences(100, 10);
		assertEquals(1, list.size());

		Comparator<IOccurrence> comp = new ConstructByItemIdentifierComparator<IOccurrence>();

		for (int i = 0; i < 10; i++) {
			list = index.getOccurrences(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getOccurrences(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getUris(java.net.URI, int, int)}
	 * .
	 * 
	 * @throws URISyntaxException
	 */
	public void testGetUrisURIIntInt() throws URISyntaxException {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Occurrence[] occurrences = new Occurrence[101];
		String base = "http://psi.example.org/";
		URI value = new URI(base);
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				occurrences[j] = createTopic().createOccurrence(createTopic(), "Value", new Topic[0]);
				occurrences[j].addItemIdentifier(createLocator(base + c + i));
				((IOccurrence) occurrences[j]).setValue(value);
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<ICharacteristics> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getUris(value, i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getUris(value, 100, 10);
		assertEquals(1, list.size());

		Comparator<ICharacteristics> comp = new ConstructByItemIdentifierComparator<ICharacteristics>();

		for (int i = 0; i < 10; i++) {
			list = index.getUris(value, i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(occurrences[i * 10], list.get(0));
			assertEquals(occurrences[i * 10 + 1], list.get(1));
			assertEquals(occurrences[i * 10 + 2], list.get(2));
			assertEquals(occurrences[i * 10 + 3], list.get(3));
			assertEquals(occurrences[i * 10 + 4], list.get(4));
			assertEquals(occurrences[i * 10 + 5], list.get(5));
			assertEquals(occurrences[i * 10 + 6], list.get(6));
			assertEquals(occurrences[i * 10 + 7], list.get(7));
			assertEquals(occurrences[i * 10 + 8], list.get(8));
			assertEquals(occurrences[i * 10 + 9], list.get(9));
		}
		list = index.getUris(value, 100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(occurrences[100], list.get(0));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex#getVariants(int, int)}
	 * .
	 */
	public void testGetVariantsIntInt() {
		IPagedLiteralIndex index = topicMap.getIndex(IPagedLiteralIndex.class);
		assertNotNull(index);
		try {
			index.getBooleans(false, 0, 0);
			fail("Index should be closed!");
		} catch (Exception e) {
			index.open();
		}
		Variant[] variants = new Variant[101];
		String base = "http://psi.example.org/";
		int j = 0;
		for (String c : new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K" }) {
			for (int i = 0; i < 10 && j < 101; i++) {
				variants[j] = createTopic().createName(createTopic(), "Value", new Topic[0]).createVariant("Value", createTopic());
				variants[j].addItemIdentifier(createLocator(base + c + i));
				j++;
			}
			if (j == 101) {
				break;
			}
		}

		List<IVariant> list = null;
		for (int i = 0; i < 10; i++) {
			list = index.getVariants(i * 10, 10);
			assertEquals(10, list.size());
		}
		list = index.getVariants(100, 10);
		assertEquals(1, list.size());

		Comparator<IVariant> comp = new ConstructByItemIdentifierComparator<IVariant>();

		for (int i = 0; i < 10; i++) {
			list = index.getVariants(i * 10, 10, comp);
			assertEquals(10, list.size());
			assertEquals(variants[i * 10], list.get(0));
			assertEquals(variants[i * 10 + 1], list.get(1));
			assertEquals(variants[i * 10 + 2], list.get(2));
			assertEquals(variants[i * 10 + 3], list.get(3));
			assertEquals(variants[i * 10 + 4], list.get(4));
			assertEquals(variants[i * 10 + 5], list.get(5));
			assertEquals(variants[i * 10 + 6], list.get(6));
			assertEquals(variants[i * 10 + 7], list.get(7));
			assertEquals(variants[i * 10 + 8], list.get(8));
			assertEquals(variants[i * 10 + 9], list.get(9));
		}
		list = index.getVariants(100, 10, comp);
		assertEquals(1, list.size());
		assertEquals(variants[100], list.get(0));
	}

}
