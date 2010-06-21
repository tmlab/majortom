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

package de.topicmapslab.majortom.core;

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
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Sven Krosse
 * 
 * @author Sven Krosse
 * 
 */
public abstract class DataTypeAwareImpl extends ScopeableImpl implements IDatatypeAware {

	/**
	 * constructor
	 * 
	 * @param identity the {@link ITopicMapStoreIdentity}
	 * @param topicMap the topic map
	 * @param parent the parent construct
	 */
	public DataTypeAwareImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap, IConstruct parent) {
		super(identity, topicMap, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public Boolean booleanValue() throws ParseException {
		try {
			return (Boolean) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Boolean.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof ParseException) {
				throw (ParseException) e.getCause();
			}
			throw new ParseException("Cannot convert literal to boolean.", 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IGeoCoordinate coordinateValue() throws ParseException {
		try {
			return (IGeoCoordinate) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, IGeoCoordinate.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof ParseException) {
				throw (ParseException) e.getCause();
			}
			throw new ParseException("Cannot convert literal to geographical coordinate.", 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar dateTimeValue() throws ParseException {
		try {
			return (Calendar) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Calendar.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof ParseException) {
				throw (ParseException) e.getCause();
			}
			throw new ParseException("Cannot convert literal to dateTime.", 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Double doubleValue() throws NumberFormatException {
		try {
			return (Double) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Double.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to double.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Boolean value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Double value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Calendar value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(URI value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(IGeoCoordinate value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public URI uriValue() throws URISyntaxException {
		try {
			return (URI) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, URI.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof URISyntaxException) {
				throw (URISyntaxException) e.getCause();
			}
			throw new URISyntaxException(getValue(), "Invalid URI.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigDecimal decimalValue() {
		try {
			return (BigDecimal) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, BigDecimal.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to decimal.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public float floatValue() {
		try {
			return (Float) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Float.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to float.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator getDatatype() {
		return (Locator) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.DATATYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public int intValue() {
		try {
			return (Integer) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Integer.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to integer.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public BigInteger integerValue() {
		try {
			return (BigInteger) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, BigInteger.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to integer.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator locatorValue() {
		try {
			return (Locator) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Locator.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new IllegalArgumentException("Cannot convert literal to a locator.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public long longValue() {
		try {
			return (Long) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, Long.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof NumberFormatException) {
				throw (NumberFormatException) e.getCause();
			}
			throw new NumberFormatException("Cannot convert literal to long.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IGeoSurface<?> surfaceValue() throws ParseException {
		try {
			return (IGeoSurface<?>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE, IGeoSurface.class);
		} catch (TopicMapStoreException e) {
			if (e.getCause() instanceof ParseException) {
				throw (ParseException) e.getCause();
			}
			throw new ParseException("Cannot convert literal to surface value.", 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String value) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(Locator value) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(BigDecimal value) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(BigInteger value) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(long value) {
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(float value) {
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(int value) {
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(IGeoSurface<?> value) {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);

	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String value, Locator locator) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (locator == null) {
			throw new ModelConstraintException(this, "Locator cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value, locator);
	}

}
