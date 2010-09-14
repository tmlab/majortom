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
package de.topicmapslab.majortom.database.jdbc.index;

import java.net.URI;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.nonpaged.CachedLiteralIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcLiteralIndex extends CachedLiteralIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 */
	public JdbcLiteralIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNames(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(String literal) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (literal == null) {
			throw new IllegalArgumentException("Literal cannot be null");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNames(
					getStore().getTopicMap(), literal));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(String literal) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (literal == null) {
			throw new IllegalArgumentException("Literal cannot be null");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), literal,
					XmlSchemeDatatypes.XSD_STRING, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value.getReference(),
					XmlSchemeDatatypes.XSD_ANYURI, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(String value,
			Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value, datatype.getReference(),
					-1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetBooleans(boolean value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), Boolean.toString(value),
					XmlSchemeDatatypes.XSD_BOOLEAN, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getCharacteristics(
					getStore().getTopicMap(), value, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getCharacteristicsByDatatype(
					getStore().getTopicMap(), datatype.getReference(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(String value,
			Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getCharacteristics(
					getStore().getTopicMap(), value, datatype.getReference(),
					-1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristicsMatches(
			String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getCharacteristicsByPattern(
					getStore().getTopicMap(), regExp, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristicsMatches(
			String regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getCharacteristicsByPattern(
					getStore().getTopicMap(), regExp, datatype.getReference(),
					-1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristicsMatches(
			Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		return doGetCharacteristicsMatches(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristicsMatches(
			Pattern regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		return doGetCharacteristicsMatches(regExp.pattern(), datatype);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCoordinates(Wgs84Coordinate value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value.toString(),
					XmlSchemeDatatypes.XSD_GEOCOORDINATE, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b>Currently not supported!
	 * </p>
	 */
	public Collection<ICharacteristics> doGetCoordinates(Wgs84Coordinate value,
			double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> doGetDatatypeAwares(Locator dataType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (dataType == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<IDatatypeAware> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getDatatypeAwaresByDatatype(
					getStore().getTopicMap(), dataType.getReference(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetDateTime(Calendar value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(),
					DatatypeAwareUtils.toString(value,
							XmlSchemeDatatypes.XSD_DATETIME),
					XmlSchemeDatatypes.XSD_DATETIME, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetDateTime(Calendar value,
			Calendar deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (deviance == null) {
			throw new IllegalArgumentException("Deviance cannot be null");
		}
		try {
			Calendar lower = (Calendar) value.clone();
			Calendar upper = (Calendar) value.clone();
			for (int field : new int[] { Calendar.SECOND, Calendar.MINUTE,
					Calendar.HOUR, Calendar.DAY_OF_MONTH, Calendar.MONTH,
					Calendar.YEAR }) {
				lower.add(field, -1 * deviance.get(field));
				upper.add(field, deviance.get(field));
			}
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), lower, upper, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetDoubles(double value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), Double.toString(value),
					XmlSchemeDatatypes.XSD_DOUBLE, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetDoubles(double value,
			double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value, deviance,
					XmlSchemeDatatypes.XSD_DOUBLE, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetFloats(float value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), Float.toString(value),
					XmlSchemeDatatypes.XSD_FLOAT, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetFloats(float value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value, deviance,
					XmlSchemeDatatypes.XSD_FLOAT, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetIntegers(int value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), Integer.toString(value),
					XmlSchemeDatatypes.XSD_INT, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetIntegers(int value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value, deviance,
					XmlSchemeDatatypes.XSD_INT, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetLongs(long value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), Long.toString(value),
					XmlSchemeDatatypes.XSD_LONG, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetLongs(long value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value, deviance,
					XmlSchemeDatatypes.XSD_LONG, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetUris(URI value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(
					getStore().getTopicMap(), value.toString(),
					XmlSchemeDatatypes.XSD_ANYURI, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(
					getStore().getTopicMap(), value,
					XmlSchemeDatatypes.XSD_STRING));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(
					getStore().getTopicMap(), value.toString(),
					XmlSchemeDatatypes.XSD_ANYURI));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(String value, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(
					getStore().getTopicMap(), value, datatype.getReference()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
