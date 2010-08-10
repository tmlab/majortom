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

import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.geotype.model.IGeoSurface;
import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;

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

	/**
	 * prefix colon
	 */
	private static final String COLON = ":";

	/**
	 * Base identifier of all XML Scheme Definition data-types <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#</code>
	 */
	public static final String XSD_BASE = "http://www.w3.org/2001/XMLSchema#";

	/**
	 * QName of all XML Scheme Definition data-types <br />
	 * <br />
	 * <code>xsd</code>
	 */
	public static final String XSD_QNAME = "xsd";

	/**
	 * XML Scheme Definition data-types of string <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#string</code>
	 */
	public static final String XSD_STRING = XSD_BASE + "string";

	/**
	 * QNamed XML Scheme Definition data-types of string <br />
	 * <br />
	 * <code>xsd:string</code>
	 */
	public static final String XSD_QSTRING = XSD_QNAME + COLON + "string";

	/**
	 * XML Scheme Definition data-types of URI <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#anyURI</code>
	 */
	public static final String XSD_ANYURI = XSD_BASE + "anyURI";

	/**
	 * QNamed XML Scheme Definition data-types of URI <br />
	 * <br />
	 * <code>xsd:anyURI</code>
	 */
	public static final String XSD_QANYURI = XSD_QNAME + COLON + "anyURI";

	/**
	 * XML Scheme Definition data-types of decimal <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#decimal</code>
	 */
	public static final String XSD_DECIMAL = XSD_BASE + "decimal";

	/**
	 * QNamed XML Scheme Definition data-types of decimal <br />
	 * <br />
	 * <code>xsd:decimal</code>
	 */
	public static final String XSD_QDECIMAL = XSD_QNAME + COLON + "decimal";

	/**
	 * XML Scheme Definition data-types of integer <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#int</code>
	 */
	public static final String XSD_INT = XSD_BASE + "int";

	/**
	 * QNamed XML Scheme Definition data-types of integer <br />
	 * <br />
	 * <code>xsd:int</code>
	 */
	public static final String XSD_QINT = XSD_QNAME + COLON + "int";

	/**
	 * XML Scheme Definition data-types of integer <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#integer</code>
	 */
	public static final String XSD_INTEGER = XSD_BASE + "integer";

	/**
	 * QNamed XML Scheme Definition data-types of integer <br />
	 * <br />
	 * <code>xsd:integer</code>
	 */
	public static final String XSD_QINTEGER = XSD_QNAME + COLON + "integer";

	/**
	 * XML Scheme Definition data-types of long numbers <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#long</code>
	 */
	public static final String XSD_LONG = XSD_BASE + "long";

	/**
	 * QNamed XML Scheme Definition data-types of long numbers <br />
	 * <br />
	 * <code>xsd:long</code>
	 */
	public static final String XSD_QLONG = XSD_QNAME + COLON + "long";

	/**
	 * XML Scheme Definition data-types of floating point numbers <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#float</code>
	 */
	public static final String XSD_FLOAT = XSD_BASE + "float";

	/**
	 * QNamed XML Scheme Definition data-types of floating point numbers <br />
	 * <br />
	 * <code>xsd:float</code>
	 */
	public static final String XSD_QFLOAT = XSD_QNAME + COLON + "float";

	/**
	 * XML Scheme Definition data-types of double floating point numbers <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#double</code>
	 */
	public static final String XSD_DOUBLE = XSD_BASE + "double";

	/**
	 * QNamed XML Scheme Definition data-types of double floating point numbers <br />
	 * <br />
	 * <code>xsd:double</code>
	 */
	public static final String XSD_QDOUBLE = XSD_QNAME + COLON + "double";

	/**
	 * XML Scheme Definition data-types of boolean <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#boolean</code>
	 */
	public static final String XSD_BOOLEAN = XSD_BASE + "boolean";

	/**
	 * QNamed XML Scheme Definition data-types of boolean <br />
	 * <br />
	 * <code>xsd:boolean</code>
	 */
	public static final String XSD_QBOOLEAN = XSD_QNAME + COLON + "boolean";

	/**
	 * XML Scheme Definition data-types of date <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#date</code>
	 */
	public static final String XSD_DATE = XSD_BASE + "date";

	/**
	 * QNamed XML Scheme Definition data-types of date <br />
	 * <br />
	 * <code>xsd:date</code>
	 */
	public static final String XSD_QDATE = XSD_QNAME + COLON + "date";

	/**
	 * XML Scheme Definition data-types of time <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#time</code>
	 */
	public static final String XSD_TIME = XSD_BASE + "time";

	/**
	 * QNamed XML Scheme Definition data-types of time <br />
	 * <br />
	 * <code>xsd:time</code>
	 */
	public static final String XSD_QTIME = XSD_QNAME + COLON + "time";

	/**
	 * XML Scheme Definition data-types of date-time <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#dateTime</code>
	 */
	public static final String XSD_DATETIME = XSD_BASE + "dateTime";

	/**
	 * QNamed XML Scheme Definition data-types of date-time <br />
	 * <br />
	 * <code>xsd:dateTime</code>
	 */
	public static final String XSD_QDATETIME = XSD_QNAME + COLON + "dateTime";

	/**
	 * XML Scheme Definition data-types of geographical coordinates<br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#geoCoordinate</code>
	 */
	public static final String XSD_GEOCOORDINATE = XSD_BASE + "geoCoordinate";

	/**
	 * QNamed XML Scheme Definition data-types of geographical coordinates<br />
	 * <br />
	 * <code>xsd:geoCoordinate</code>
	 */
	public static final String XSD_QGEOCOORDINATE = XSD_QNAME + COLON + "geoCoordinate";

	/**
	 * XML Scheme Definition data-types of geographical surfaces<br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#surface</code>
	 */
	public static final String XSD_GEOSURFACE = XSD_BASE + "surface";

	/**
	 * QNamed XML Scheme Definition data-types of geographical surfaces<br />
	 * <br />
	 * <code>xsd:surface</code>
	 */
	public static final String XSD_QGEOSURFACE = XSD_QNAME + COLON + "surface";

	/**
	 * XML Scheme Definition data-types of any <br />
	 * <br />
	 * <code>http://www.w3.org/2001/XMLSchema#any</code>
	 */
	public static final String XSD_ANY = XSD_BASE + "any";

	/**
	 * QNamed XML Scheme Definition data-types of any <br />
	 * <br />
	 * <code>xsd:any</code>
	 */
	public static final String XSD_QANY = XSD_QNAME + COLON + "any";

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
		if (identifier.startsWith(XSD_QNAME + COLON)) {
			return XSD_BASE.concat(identifier.substring(4));
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
			return XSD_DATETIME;
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return XSD_INT;
		} else if (Long.class.isAssignableFrom(clazz)) {
			return XSD_LONG;
		} else if (Float.class.isAssignableFrom(clazz)) {
			return XSD_FLOAT;
		} else if (Double.class.isAssignableFrom(clazz)) {
			return XSD_DOUBLE;
		} else if (BigInteger.class.isAssignableFrom(clazz)) {
			return XSD_INTEGER;
		} else if (BigDecimal.class.isAssignableFrom(clazz)) {
			return XSD_DECIMAL;
		} else if (Double.class.isAssignableFrom(clazz)) {
			return XSD_DOUBLE;
		} else if (IGeoCoordinate.class.isAssignableFrom(clazz)) {
			return XSD_GEOCOORDINATE;
		} else if (IGeoSurface.class.isAssignableFrom(clazz)) {
			return XSD_GEOSURFACE;
		} else if (URI.class.isAssignableFrom(clazz) || Locator.class.isAssignableFrom(clazz)) {
			return XSD_ANYURI;
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return XSD_BOOLEAN;
		} else if (String.class.isAssignableFrom(clazz)) {
			return XSD_STRING;
		} else {
			return XSD_ANY;
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
		if (XSD_DATETIME.equals(loc.getReference())) {
			return Calendar.class;
		} else if (XSD_INT.equals(loc.getReference())) {
			return Integer.class;
		} else if (XSD_LONG.equals(loc.getReference())) {
			return Long.class;
		} else if (XSD_FLOAT.equals(loc.getReference())) {
			return Float.class;
		} else if (XSD_DOUBLE.equals(loc.getReference())) {
			return Double.class;
		} else if (XSD_INTEGER.equals(loc.getReference())) {
			return BigInteger.class;
		} else if (XSD_DECIMAL.equals(loc.getReference())) {
			return BigDecimal.class;
		} else if (XSD_DOUBLE.equals(loc.getReference())) {
			return Double.class;
		} else if (XSD_GEOCOORDINATE.equals(loc.getReference())) {
			return Wgs84Coordinate.class;
		} else if (XSD_GEOSURFACE.equals(loc.getReference())) {
			return Wgs84Circuit.class;
		} else if (XSD_ANYURI.equals(loc.getReference())) {
			return Locator.class;
		} else if (XSD_BOOLEAN.equals(loc.getReference())) {
			return Boolean.class;
		} else if (XSD_STRING.equals(loc.getReference())) {
			return String.class;
		} else {
			return Object.class;
		}
	}

}
