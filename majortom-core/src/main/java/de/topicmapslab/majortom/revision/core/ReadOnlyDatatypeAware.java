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
package de.topicmapslab.majortom.revision.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;

import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;

/**
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyDatatypeAware extends ReadOnlyScopable implements IDatatypeAware {

	/**
	 * @param clone
	 */
	public ReadOnlyDatatypeAware(IDatatypeAware clone) {
		super((IScopable) clone);
	}

	/**
	 * {@inheritDoc}
	 */
	public Boolean booleanValue() throws ParseException {
		final String value = getValue();
		try {
			return (Boolean) DatatypeAwareUtils.toValue(value, Boolean.class);
		} catch (NumberFormatException e) {
			throw new ParseException(value, 0);
		} catch (URISyntaxException e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Wgs84Coordinate coordinateValue() throws ParseException {
		final String value = getValue();
		try {
			return (Wgs84Coordinate) DatatypeAwareUtils.toValue(value, Wgs84Coordinate.class);
		} catch (NumberFormatException e) {
			throw new ParseException(value, 0);
		} catch (URISyntaxException e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar dateTimeValue() throws ParseException {
		final String value = getValue();
		try {
			return (Calendar) DatatypeAwareUtils.toValue(value, Calendar.class);
		} catch (NumberFormatException e) {
			throw new ParseException(value, 0);
		} catch (URISyntaxException e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Double doubleValue() throws NumberFormatException {
		final String value = getValue();
		try {
			return (Double) DatatypeAwareUtils.toValue(value, Double.class);
		} catch (ParseException e) {
			throw new NumberFormatException();
		} catch (URISyntaxException e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Boolean value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Double value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Calendar value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(URI value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Wgs84Coordinate value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Wgs84Circuit value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}
	
	/**
	 * Return the internal value of this occurrence.
	 * @return the value as object
	 */
	protected abstract Object objectValue();

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return objectValue().toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Wgs84Circuit surfaceValue() throws ParseException {
		final Object value = objectValue();
		try {
			return (Wgs84Circuit) DatatypeAwareUtils.toValue(value, Wgs84Circuit.class);
		} catch (NumberFormatException e) {
			throw new ParseException(value.toString(), 0);
		} catch (URISyntaxException e) {
			throw new ParseException(value.toString(), 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public URI uriValue() throws URISyntaxException {
		final Object value = objectValue();
		try {
			return (URI) DatatypeAwareUtils.toValue(value, URI.class);
		} catch (NumberFormatException e) {
			throw new URISyntaxException(value.toString(), e.getMessage());
		} catch (ParseException e) {
			throw new URISyntaxException(value.toString(), e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal decimalValue() {
		final Object value = objectValue();
		try {
			return (BigDecimal) DatatypeAwareUtils.toValue(value, BigDecimal.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public float floatValue() {
		final Object value = objectValue();
		try {
			return (Float) DatatypeAwareUtils.toValue(value, Float.class);
		} catch (ParseException e) {
			throw new NumberFormatException();
		} catch (URISyntaxException e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int intValue() {
		final Object value = objectValue();
		try {
			return (Integer) DatatypeAwareUtils.toValue(value, Integer.class);
		} catch (ParseException e) {
			throw new NumberFormatException();
		} catch (URISyntaxException e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigInteger integerValue() {
		final Object value = objectValue();
		try {
			return (BigInteger) DatatypeAwareUtils.toValue(value, BigInteger.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator locatorValue() {
		final Object value = objectValue();
		try {
			return (Locator) DatatypeAwareUtils.toValue(value, Locator.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public long longValue() {
		final Object value = objectValue();
		try {
			return (Long) DatatypeAwareUtils.toValue(value, Long.class);
		} catch (ParseException e) {
			throw new NumberFormatException();
		} catch (URISyntaxException e) {
			throw new NumberFormatException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Locator arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(BigDecimal arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(BigInteger arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(long arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(float arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(int arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String arg0, Locator arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

}
