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

import de.topicmapslab.geotype.mecator.MecatorCircuit;
import de.topicmapslab.geotype.mecator.MecatorCoordinate;
import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.geotype.model.IGeoSurface;
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

		final String ref = datatype.getReference();

		if (ref.equalsIgnoreCase(XmlSchemeDatatypes.XSD_ANYURI)) {
			if (value instanceof Locator) {
				return ((Locator) value).getReference();
			}
			return value.toString();
		} else if (ref.equalsIgnoreCase(XmlSchemeDatatypes.XSD_DATE)) {
			if (value instanceof Calendar) {
				return dateFormat.format(((Calendar) value).getTime());
			}
			return value.toString();
		} else if (ref.equalsIgnoreCase(XmlSchemeDatatypes.XSD_DATETIME)) {
			if (value instanceof Calendar) {
				return dateTimeFormat.format(((Calendar) value).getTime());
			}
			return value.toString();
		} else if (ref.equalsIgnoreCase(XmlSchemeDatatypes.XSD_TIME)) {
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
		} else if (IGeoCoordinate.class.equals(clazz)) {
			if (value instanceof IGeoCoordinate) {
				return (IGeoCoordinate) value;
			}
			return new MecatorCoordinate(value.toString());
		} else if (IGeoSurface.class.equals(clazz)) {
			if (value instanceof IGeoSurface<?>) {
				return (IGeoSurface<?>) value;
			}
			return new MecatorCircuit(value.toString());
		}
		return value;
	}

}
