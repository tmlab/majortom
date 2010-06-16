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
package de.topicmapslab.majortom.inMemory.store.revision.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;

import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.geotype.model.IGeoSurface;
import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyDatatypeAware extends ReadOnlyScopable implements IDatatypeAware {

	private final String value;
	private final Locator datatype;

	/**
	 * @param clone
	 */
	public ReadOnlyDatatypeAware(IDatatypeAware clone) {
		super((IScopable) clone);
		this.value = clone.getValue();
		this.datatype = new LocatorImpl(clone.getDatatype().getReference());
	}

	/**
	 * {@inheritDoc}
	 */
	public Boolean booleanValue() throws ParseException {
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
	public IGeoCoordinate coordinateValue() throws ParseException {
		try {
			return (IGeoCoordinate) DatatypeAwareUtils.toValue(value, IGeoCoordinate.class);
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
	public void setValue(IGeoCoordinate value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(IGeoSurface<?> value) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public IGeoSurface<?> surfaceValue() throws ParseException {
		try {
			return (IGeoSurface<?>) DatatypeAwareUtils.toValue(value, IGeoSurface.class);
		} catch (NumberFormatException e) {
			throw new ParseException(value, 0);
		} catch (URISyntaxException e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public URI uriValue() throws URISyntaxException {
		try {
			return (URI) DatatypeAwareUtils.toValue(value, URI.class);
		} catch (NumberFormatException e) {
			throw new URISyntaxException(value, e.getMessage());
		} catch (ParseException e) {
			throw new URISyntaxException(value, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal decimalValue() {
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
	public Locator getDatatype() {
		return datatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public int intValue() {
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
