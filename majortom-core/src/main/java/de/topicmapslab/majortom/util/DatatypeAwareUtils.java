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
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.tmapi.core.Locator;

import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;

/**
 * Utility class for {@link IDatatypeAware}
 * 
 * @author Sven Krosse
 * 
 */
public class DatatypeAwareUtils {

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSSSS+HH:mm");
	private final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSS+HH:mm");

	/**
	 * Transform the given values to its string representations dependent from
	 * the given data type.
	 * 
	 * @param value
	 *            the value
	 * @param datatype
	 *            the data type
	 * @return the string literal
	 */
	public static final String toString(Object value, ILocator datatype) {
		return toString(value, datatype.getReference());
	}

	/**
	 * Transform the given values to its string representations dependent from
	 * the given data type.
	 * 
	 * @param value
	 *            the value
	 * @param reference
	 *            the reference
	 * @return the string literal
	 */
	public static final String toString(Object value, String reference) {
		if (reference.equalsIgnoreCase(XmlSchemeDatatypes.XSD_ANYURI)) {
			if (value instanceof Locator) {
				return ((Locator) value).getReference();
			}
			return value.toString();
		} else if (reference.equalsIgnoreCase(XmlSchemeDatatypes.XSD_DATE)) {
			if (value instanceof Calendar) {
				return dateFormat.format(((Calendar) value).getTime());
			}
			return value.toString();
		} else if (reference.equalsIgnoreCase(XmlSchemeDatatypes.XSD_DATETIME)) {
			if (value instanceof Calendar) {
				return dateTimeFormat.format(((Calendar) value).getTime());
			}
			return value.toString();
		} else if (reference.equalsIgnoreCase(XmlSchemeDatatypes.XSD_TIME)) {
			if (value instanceof Calendar) {
				return timeFormat.format(((Calendar) value).getTime());
			}
			return value.toString();
		}
		return value.toString();
	}

	/**
	 * Transform the given value to a representation of the given class
	 * 
	 * @param value
	 *            the value
	 * @param clazz
	 *            the class to transform
	 * @return the converted value
	 * @throws Exception
	 *             if the given value cannot convert to the given class
	 */
	public static final Object toValue(Object value, Class<?> clazz) throws NumberFormatException, URISyntaxException, ParseException {

		if (Integer.class.equals(clazz)) {
			if (value instanceof Integer) {
				return (Integer) value;
			}
			return Integer.valueOf((int) Math.round((Double) toValue(value, Double.class)));
		} else if (Long.class.equals(clazz)) {
			if (value instanceof Long) {
				return (Long) value;
			}
			return Math.round((Double) toValue(value, Double.class));
		} else if (Float.class.equals(clazz)) {
			if (value instanceof Float) {
				return (Float) value;
			}
			return Float.parseFloat(value.toString());
		} else if (Double.class.equals(clazz)) {
			if (value instanceof Double) {
				return (Double) value;
			}
			return Double.parseDouble(value.toString());
		} else if (BigInteger.class.equals(clazz)) {
			if (value instanceof BigInteger) {
				return (BigInteger) value;
			}
			return BigInteger.valueOf(Math.round((Double) toValue(value, Double.class)));
		} else if (BigDecimal.class.equals(clazz)) {
			if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			}
			double v1 = (Double) toValue(value, Double.class);
			if (v1 == Math.round(v1)) {
				return BigDecimal.valueOf(Math.round(v1));
			}
			return BigDecimal.valueOf(v1);
		} else if (Locator.class.equals(clazz)) {
			if (value instanceof Locator) {
				return (Locator) value;
			}
			return new LocatorImpl(value.toString());
		} else if (URI.class.equals(clazz)) {
			if (value instanceof URI) {
				return (URI) value;
			}
			return new URI(value.toString());
		} else if (Calendar.class.equals(clazz)) {
			if (value instanceof Calendar) {
				return (Calendar) value;
			}
			if (LiteralUtils.isTime(value.toString())) {
				return LiteralUtils.asTime(value.toString());
			} else if (LiteralUtils.isDate(value.toString())) {
				return LiteralUtils.asDate(value.toString());
			} else if (LiteralUtils.isDateTime(value.toString())) {
				return LiteralUtils.asDateTime(value.toString());
			}
			throw new IllegalArgumentException("Cannot cast date literal " + value.toString());
		} else if (Boolean.class.equals(clazz)) {
			if (value instanceof Boolean) {
				return (Boolean) value;
			}
			return Boolean.parseBoolean(value.toString());
		} else if (Wgs84Coordinate.class.equals(clazz)) {
			if (value instanceof Wgs84Coordinate) {
				return (Wgs84Coordinate) value;
			}
			return new Wgs84Coordinate(value.toString());
		} else if (Wgs84Circuit.class.equals(clazz)) {
			if (value instanceof Wgs84Circuit) {
				return (Wgs84Circuit) value;
			}
			return new Wgs84Circuit(value.toString());
		}
		return value;
	}

	/**
	 * Transform the given value to a representation of the given class
	 * 
	 * @param obj
	 *            the datatype-aware
	 * @return the converted value
	 * @throws Exception
	 *             if the given value cannot convert to the given class
	 */
	public static final Object toValue(IDatatypeAware obj) throws NumberFormatException, URISyntaxException, ParseException {
		return toValue(obj, XmlSchemeDatatypes.xsdToJava(obj.getDatatype()));
	}
	
	/**
	 * Transform the given value to a representation of the given class
	 * 
	 * @param obj
	 *            the datatype-aware
	 * @param clazz
	 *            the class to transform
	 * @return the converted value
	 * @throws Exception
	 *             if the given value cannot convert to the given class
	 */
	public static final Object toValue(IDatatypeAware obj, Class<?> clazz) throws NumberFormatException, URISyntaxException, ParseException {
		if (Integer.class.equals(clazz)) {
			try {
				return obj.intValue();
			} catch (Exception e) {
				return Integer.valueOf((int) Math.round((Double) toValue(obj.getValue(), Double.class)));
			}
		} else if (Long.class.equals(clazz)) {
			try {
				return obj.longValue();
			} catch (Exception e) {
				return Math.round((Double) toValue(obj.getValue(), Double.class));
			}
		} else if (Float.class.equals(clazz)) {
			try {
				return obj.floatValue();
			} catch (Exception e) {
				return Float.parseFloat(obj.getValue().toString());
			}
		} else if (Double.class.equals(clazz)) {
			try {
				return obj.doubleValue();
			} catch (Exception e) {
				return Double.parseDouble(obj.getValue());
			}
		} else if (BigInteger.class.equals(clazz)) {
			try {
				return obj.integerValue();
			} catch (Exception e) {
				return BigInteger.valueOf(Math.round((Double) toValue(obj.getValue(), Double.class)));
			}
		} else if (BigDecimal.class.equals(clazz)) {
			try {
				return obj.decimalValue();
			} catch (Exception e) {
				double v1 = (Double) toValue(obj.getValue(), Double.class);
				if (v1 == Math.round(v1)) {
					return BigDecimal.valueOf(Math.round(v1));
				}
				return BigDecimal.valueOf(v1);
			}
		} else if (Locator.class.equals(clazz)) {
			try {
				return obj.locatorValue();
			} catch (Exception e) {
				return new LocatorImpl(obj.getValue());
			}
		} else if (URI.class.equals(clazz)) {
			try {
				return obj.uriValue();
			} catch (Exception e) {
				return new URI(obj.getValue());
			}
		} else if (Calendar.class.equals(clazz)) {
			try {
				return obj.dateTimeValue();
			} catch (Exception e) {
				if (LiteralUtils.isTime(obj.getValue())) {
					return LiteralUtils.asTime(obj.getValue());
				} else if (LiteralUtils.isDate(obj.getValue())) {
					return LiteralUtils.asDate(obj.getValue());
				} else if (LiteralUtils.isDateTime(obj.getValue())) {
					return LiteralUtils.asDateTime(obj.getValue());
				}
				throw new IllegalArgumentException("Cannot cast date literal " + obj.getValue());
			}
		} else if (Boolean.class.equals(clazz)) {
			try {
				return obj.booleanValue();
			} catch (Exception e) {
				return Boolean.parseBoolean(obj.getValue());
			}
		} else if (Wgs84Coordinate.class.equals(clazz)) {
			return obj.coordinateValue();
		} else if (Wgs84Circuit.class.equals(clazz)) {
			return obj.surfaceValue();
		}
		return obj.getValue();
	}
	
	

}
