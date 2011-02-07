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
package de.topicmapslab.majortom.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;

import org.tmapi.core.Locator;

import de.topicmapslab.geotype.model.IGeoSurface;
import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import static de.topicmapslab.majortom.model.namespace.Namespaces.XSD.*;

/**
 * Utility class for XSD data-types and any extension types for example for
 * geographical coordinates.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class XmlSchemeDatatypes {

	/**
	 * hidden constructor
	 */
	private XmlSchemeDatatypes() {

	}
	
	private static final String COLON = ":";

	/**
	 * Method transform the given identifier to its absolute IRI if it starts
	 * with the XML scheme definition prefix 'xsd' otherwise the string returned
	 * unmodified.
	 * 
	 * @param identifier
	 *            the identifier to transform
	 * @return the transformed or unmodified string-represented IRI.
	 */
	public static String toExternalForm(final String identifier) {
		if (identifier.startsWith(QNAME + COLON)) {
			return PREFIX.concat(identifier.substring(4));
		}
		return identifier;
	}

	/**
	 * Transform the given java class to one of the contained identifiers.
	 * 
	 * @param clazz
	 *            the java class
	 * @return the identifier
	 */
	public static String javaToXsd(final Class<?> clazz) {
		if (Calendar.class.isAssignableFrom(clazz)) {
			return DATETIME;
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return INT;
		} else if (Long.class.isAssignableFrom(clazz)) {
			return LONG;
		} else if (Float.class.isAssignableFrom(clazz)) {
			return FLOAT;
		} else if (Double.class.isAssignableFrom(clazz)) {
			return DOUBLE;
		} else if (BigInteger.class.isAssignableFrom(clazz)) {
			return INTEGER;
		} else if (BigDecimal.class.isAssignableFrom(clazz)) {
			return DECIMAL;
		} else if (Double.class.isAssignableFrom(clazz)) {
			return DOUBLE;
		} else if (Wgs84Coordinate.class.isAssignableFrom(clazz)) {
			return WGS84_COORDINATE;
		} else if (IGeoSurface.class.isAssignableFrom(clazz)) {
			return GEOSURFACE;
		} else if (URI.class.isAssignableFrom(clazz) || Locator.class.isAssignableFrom(clazz)) {
			return ANYURI;
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return BOOLEAN;
		} else if (String.class.isAssignableFrom(clazz)) {
			return STRING;
		} else {
			return ANY;
		}
	}

	/**
	 * Transform the given XSD identifier to its java class.
	 * 
	 * @param locator
	 *            the locator
	 * @return the java class
	 */
	public static Class<?> xsdToJava(final Locator loc) {
		if (DATETIME.equals(loc.getReference())) {
			return Calendar.class;
		} else if (INT.equals(loc.getReference())) {
			return Integer.class;
		} else if (LONG.equals(loc.getReference())) {
			return Long.class;
		} else if (FLOAT.equals(loc.getReference())) {
			return Float.class;
		} else if (DOUBLE.equals(loc.getReference())) {
			return Double.class;
		} else if (INTEGER.equals(loc.getReference())) {
			return BigInteger.class;
		} else if (DECIMAL.equals(loc.getReference())) {
			return BigDecimal.class;
		} else if (DOUBLE.equals(loc.getReference())) {
			return Double.class;
		} else if (WGS84_COORDINATE.equals(loc.getReference())) {
			return Wgs84Coordinate.class;
		} else if (GEOSURFACE.equals(loc.getReference())) {
			return Wgs84Circuit.class;
		} else if (ANYURI.equals(loc.getReference())) {
			return Locator.class;
		} else if (BOOLEAN.equals(loc.getReference())) {
			return Boolean.class;
		} else if (STRING.equals(loc.getReference())) {
			return String.class;
		} else {
			return Object.class;
		}
	}

}
