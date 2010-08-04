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
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcLiteralIndex extends IndexImpl<JdbcTopicMapStore> implements ILiteralIndex {

	/**
	 * @param store
	 */
	public JdbcLiteralIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNames() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getNames(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(String literal) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (literal == null) {
			throw new IllegalArgumentException("Literal cannot be null");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNames(getStore().getTopicMap(), literal));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getOccurrences(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String literal) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (literal == null) {
			throw new IllegalArgumentException("Literal cannot be null");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), literal, XmlSchemeDatatypes.XSD_STRING));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value.getReference(), XmlSchemeDatatypes.XSD_ANYURI));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(String value, Locator datatype) {
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
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, datatype.getReference()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getBooleans(boolean value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Boolean.toString(value), XmlSchemeDatatypes.XSD_BOOLEAN));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value));
			col.addAll(getStore().getProcessor().getNames(getStore().getTopicMap(), value));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByPattern(getStore().getTopicMap(), ".*", datatype.getReference()));
			if (datatype.getReference().equalsIgnoreCase(XmlSchemeDatatypes.XSD_STRING)) {
				col.addAll(getStore().getProcessor().getNames(getStore().getTopicMap()));
			}
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(String value, Locator datatype) {
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
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, datatype.getReference()));
			if (datatype.getReference().equalsIgnoreCase(XmlSchemeDatatypes.XSD_STRING)) {
				col.addAll(getStore().getProcessor().getNames(getStore().getTopicMap(), value));
			}
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByPattern(getStore().getTopicMap(), regExp));
			col.addAll(getStore().getProcessor().getNamesByPattern(getStore().getTopicMap(), regExp));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByPattern(getStore().getTopicMap(), regExp, datatype.getReference()));
			if (datatype.getReference().equalsIgnoreCase(XmlSchemeDatatypes.XSD_STRING)) {
				col.addAll(getStore().getProcessor().getNamesByPattern(getStore().getTopicMap(), regExp));
			}
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		return getCharacteristicsMatches(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		if (datatype == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		return getCharacteristicsMatches(regExp.pattern(), datatype);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value.toString(), XmlSchemeDatatypes.XSD_GEOCOORDINATE));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance) {
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
	public Collection<IDatatypeAware> getDatatypeAwares(Locator dataType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (dataType == null) {
			throw new IllegalArgumentException("Datatype cannot be null");
		}
		try {
			Collection<IDatatypeAware> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByDatatype(getStore().getTopicMap(), dataType.getReference()));
			col.addAll(getStore().getProcessor().getVariantsByDatatype(getStore().getTopicMap(), dataType.getReference()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), DatatypeAwareUtils.toString(value, XmlSchemeDatatypes.XSD_DATETIME),
					XmlSchemeDatatypes.XSD_DATETIME));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDateTime(Calendar value, Calendar deviance) {
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
			for (int field : new int[] { Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR }) {
				lower.add(field, -1 * deviance.get(field));
				upper.add(field, deviance.get(field));
			}
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), lower, upper));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Double.toString(value), XmlSchemeDatatypes.XSD_DOUBLE));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getDoubles(double value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_DOUBLE));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Float.toString(value), XmlSchemeDatatypes.XSD_FLOAT));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getFloats(float value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_FLOAT));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Integer.toString(value), XmlSchemeDatatypes.XSD_INT));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getIntegers(int value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_INT));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Long.toString(value), XmlSchemeDatatypes.XSD_LONG));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getLongs(long value, double deviance) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_LONG));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getUris(URI value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value.toString(), XmlSchemeDatatypes.XSD_ANYURI));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getVariants(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(getStore().getTopicMap(), value, XmlSchemeDatatypes.XSD_STRING));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Locator value) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariants(getStore().getTopicMap(), value.toString(), XmlSchemeDatatypes.XSD_ANYURI));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(String value, Locator datatype) {
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
			col.addAll(getStore().getProcessor().getVariants(getStore().getTopicMap(), value, datatype.getReference()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
