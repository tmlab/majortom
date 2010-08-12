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
package de.topicmapslab.majortom.database.jdbc.index.paged;

import java.net.URI;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedLiteralIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * Implementation class of {@link IPagedLiteralIndex} of the Jdbc Topic Map
 * Store.
 * 
 * @author Sven Krosse
 * 
 */
public class JdbcPagedLiteralIndex extends PagedLiteralIndexImpl<JdbcTopicMapStore> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index ( non-paged index)
	 */
	public JdbcPagedLiteralIndex(JdbcTopicMapStore store, ILiteralIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetBooleans(boolean value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetBooleans(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetBooleans(boolean value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Boolean.toString(value), XmlSchemeDatatypes.XSD_BOOLEAN, offset,
					limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(Locator datatype, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getCharacteristicsByDatatype(getStore().getTopicMap(), datatype.getReference(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getCharacteristics(getStore().getTopicMap(), value, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(value, datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, Locator datatype, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getCharacteristics(getStore().getTopicMap(), value, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristicsMatches(regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getCharacteristicsByPattern(getStore().getTopicMap(), regExp.pattern(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristicsMatches(regExp, datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getCharacteristicsByPattern(getStore().getTopicMap(), regExp.pattern(), datatype.getReference(), offset,
					limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Currently not supported.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCoordinates(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Currently not supported.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit) {
		return super.doGetCoordinates(value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCoordinates(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value.toString(), XmlSchemeDatatypes.XSD_GEOCOORDINATE, offset,
					limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IDatatypeAware> doGetDatatypeAwares(Locator dataType, int offset, int limit, Comparator<IDatatypeAware> comparator) {
		return super.doGetDatatypeAwares(dataType, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IDatatypeAware> doGetDatatypeAwares(Locator dataType, int offset, int limit) {
		try {
			List<IDatatypeAware> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getDatatypeAwaresByDatatype(getStore().getTopicMap(), dataType.getReference(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, Calendar deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetDateTime(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, Calendar deviance, int offset, int limit) {
		try {
			Calendar lower = (Calendar) value.clone();
			Calendar upper = (Calendar) value.clone();
			for (int field : new int[] { Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR }) {
				lower.add(field, -1 * deviance.get(field));
				upper.add(field, deviance.get(field));
			}
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), lower, upper, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetDateTime(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), DatatypeAwareUtils.toString(value, XmlSchemeDatatypes.XSD_DATETIME),
					XmlSchemeDatatypes.XSD_DATETIME, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetDoubles(double value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetDoubles(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetDoubles(double value, double deviance, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_DOUBLE, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetDoubles(double value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetDoubles(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetDoubles(double value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor()
					.getOccurrences(getStore().getTopicMap(), Double.toString(value), XmlSchemeDatatypes.XSD_DOUBLE, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetFloats(float value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetFloats(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetFloats(float value, double deviance, int offset, int limit) {

		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_FLOAT, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetFloats(float value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetFloats(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetFloats(float value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Float.toString(value), XmlSchemeDatatypes.XSD_FLOAT, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetIntegers(int value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetIntegers(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetIntegers(int value, double deviance, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_INT, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetIntegers(int value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetIntegers(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetIntegers(int value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Integer.toString(value), XmlSchemeDatatypes.XSD_INT, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetLongs(long value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetLongs(value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetLongs(long value, double deviance, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value, deviance, XmlSchemeDatatypes.XSD_LONG, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetLongs(long value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetLongs(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetLongs(long value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), Long.toString(value), XmlSchemeDatatypes.XSD_LONG, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IName> doGetNames(int offset, int limit, Comparator<IName> comparator) {
		return super.doGetNames(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IName> doGetNames(int offset, int limit) {
		try {
			List<IName> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getNames(getStore().getTopicMap(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IOccurrence> doGetOccurrences(int offset, int limit, Comparator<IOccurrence> comparator) {
		return super.doGetOccurrences(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IOccurrence> doGetOccurrences(int offset, int limit) {
		try {
			List<IOccurrence> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetUris(URI value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetUris(value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetUris(URI value, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getOccurrences(getStore().getTopicMap(), value.toString(), XmlSchemeDatatypes.XSD_ANYURI, offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IVariant> doGetVariants(int offset, int limit, Comparator<IVariant> comparator) {
		return super.doGetVariants(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IVariant> doGetVariants(int offset, int limit) {
		try {
			List<IVariant> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getVariants(getStore().getTopicMap(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
