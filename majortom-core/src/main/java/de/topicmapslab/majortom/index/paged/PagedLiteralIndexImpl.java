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
package de.topicmapslab.majortom.index.paged;

import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public abstract class PagedLiteralIndexImpl<X extends ITopicMapStore> extends PagedIndexImpl<X, ILiteralIndex> implements IPagedLiteralIndex {

	public enum Param {
		BOOLEAN,

		CHARACTERISTICS,

		COORDINATES,

		DATETIME,

		DOUBLE,

		FLOAT,

		INTEGER,

		LONG,

		STRING,

		DATATYPE,

		REGEXP,

		DATATYPEAWARE,

		URI
	}

	private Map<Class<?>, List<? extends Construct>> cachedConstructs;
	private Map<Class<?>, Map<Comparator<? extends Construct>, List<? extends Construct>>> cachedComparedConstructs;
	private Map<Param, Map<Object, List<? extends Construct>>> cachedLiterals;
	private Map<Param, Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>>> cachedComparedLiterals;
	private Map<Param, Map<Object, Map<Object, List<? extends Construct>>>> cachedLiteralsWithDeviance;
	private Map<Param, Map<Object, Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>>>> cachedComparedLiteralsWithDeviance;

	/**
	 * @param store
	 * @param parentIndex
	 */
	public PagedLiteralIndexImpl(X store, ILiteralIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.BOOLEAN, value);
		if (cache == null) {
			return doGetBooleans(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.BOOLEAN, value, comparator);
		if (cache == null) {
			return doGetBooleans(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.STRING, value);
		if (cache == null) {
			return doGetCharacteristics(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.STRING, value, comparator);
		if (cache == null) {
			return doGetCharacteristics(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATATYPE, datatype);
		if (cache == null) {
			return doGetCharacteristics(datatype, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATATYPE, datatype, comparator);
		if (cache == null) {
			return doGetCharacteristics(datatype, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.CHARACTERISTICS, value, datatype);
		if (cache == null) {
			return doGetCharacteristics(value, datatype, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.CHARACTERISTICS, value, datatype, comparator);
		if (cache == null) {
			return doGetCharacteristics(value, datatype, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(String regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getCharacteristicsMatches(Pattern.compile(regExp), offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(String regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getCharacteristicsMatches(Pattern.compile(regExp), offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}

		return getCharacteristicsMatches(Pattern.compile(regExp), datatype, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getCharacteristicsMatches(Pattern.compile(regExp), datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.REGEXP, regExp);
		if (cache == null) {
			return doGetCharacteristicsMatches(regExp, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.REGEXP, regExp, comparator);
		if (cache == null) {
			return doGetCharacteristicsMatches(regExp, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.REGEXP, regExp, datatype);
		if (cache == null) {
			return doGetCharacteristicsMatches(regExp, datatype, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.REGEXP, regExp, datatype, comparator);
		if (cache == null) {
			return doGetCharacteristicsMatches(regExp, datatype, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.COORDINATES, value);
		if (cache == null) {
			return doGetCoordinates(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.COORDINATES, value, comparator);
		if (cache == null) {
			return doGetCoordinates(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.COORDINATES, value, deviance);
		if (cache == null) {
			return doGetCoordinates(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.COORDINATES, value, deviance, comparator);
		if (cache == null) {
			return doGetCoordinates(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(Locator dataType, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IDatatypeAware> cache = read(Param.DATATYPEAWARE, dataType);
		if (cache == null) {
			return doGetDatatypeAwares(dataType, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(Locator dataType, int offset, int limit, Comparator<IDatatypeAware> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IDatatypeAware> cache = read(Param.DATATYPEAWARE, dataType, comparator);
		if (cache == null) {
			return doGetDatatypeAwares(dataType, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATETIME, value);
		if (cache == null) {
			return doGetDateTime(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATETIME, value, comparator);
		if (cache == null) {
			return doGetDateTime(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATETIME, value, deviance);
		if (cache == null) {
			return doGetDateTime(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DATETIME, value, deviance, comparator);
		if (cache == null) {
			return doGetDateTime(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DOUBLE, value);
		if (cache == null) {
			return doGetDoubles(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DOUBLE, value, comparator);
		if (cache == null) {
			return doGetDoubles(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DOUBLE, value, deviance);
		if (cache == null) {
			return doGetDoubles(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.DOUBLE, value, deviance, comparator);
		if (cache == null) {
			return doGetDoubles(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.FLOAT, value);
		if (cache == null) {
			return doGetFloats(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.FLOAT, value, comparator);
		if (cache == null) {
			return doGetFloats(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.FLOAT, value, deviance);
		if (cache == null) {
			return doGetFloats(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.FLOAT, value, deviance, comparator);
		if (cache == null) {
			return doGetFloats(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.INTEGER, value);
		if (cache == null) {
			return doGetIntegers(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.INTEGER, value, comparator);
		if (cache == null) {
			return doGetIntegers(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.INTEGER, value, deviance);
		if (cache == null) {
			return doGetIntegers(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.INTEGER, value, deviance, comparator);
		if (cache == null) {
			return doGetIntegers(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.LONG, value);
		if (cache == null) {
			return doGetLongs(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.LONG, value, comparator);
		if (cache == null) {
			return doGetLongs(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.LONG, value, deviance);
		if (cache == null) {
			return doGetLongs(value, deviance, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.LONG, value, deviance, comparator);
		if (cache == null) {
			return doGetLongs(value, deviance, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IName> cache = read(Name.class);
		if (cache == null) {
			return doGetNames(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit, Comparator<IName> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IName> cache = read(Name.class, comparator);
		if (cache == null) {
			return doGetNames(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IOccurrence> cache = read(Occurrence.class);
		if (cache == null) {
			return doGetOccurrences(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit, Comparator<IOccurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IOccurrence> cache = read(Occurrence.class, comparator);
		if (cache == null) {
			return doGetOccurrences(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.URI, value);
		if (cache == null) {
			return doGetUris(value, offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<ICharacteristics> cache = read(Param.URI, value, comparator);
		if (cache == null) {
			return doGetUris(value, offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IVariant> cache = read(Variant.class);
		if (cache == null) {
			return doGetVariants(offset, limit);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit, Comparator<IVariant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		List<IVariant> cache = read(Variant.class, comparator);
		if (cache == null) {
			return doGetVariants(offset, limit, comparator);
		}
		return secureSubList(cache, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {

		Object dependValue = null;
		/*
		 * construct was removed
		 */
		if (event == TopicMapEventType.VARIANT_REMOVED || event == TopicMapEventType.NAME_REMOVED || event == TopicMapEventType.OCCURRENCE_REMOVED
				|| event == TopicMapEventType.TOPIC_REMOVED || event == TopicMapEventType.ASSOCIATION_REMOVED || event == TopicMapEventType.ROLE_REMOVED) {
			dependValue = oldValue;
		}
		/*
		 * new construct
		 */
		else if (event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.NAME_ADDED || event == TopicMapEventType.VARIANT_ADDED) {
			dependValue = newValue;
		}
		/*
		 * data type or value modified
		 */
		else if (event == TopicMapEventType.DATATYPE_SET || event == TopicMapEventType.VALUE_MODIFIED) {
			dependValue = notifier;
		}

		/*
		 * clear dependent caches
		 */
		if (dependValue instanceof Occurrence) {
			clearOccurrenceCache();
		} else if (dependValue instanceof Name) {
			clearNameCache();
		} else if (dependValue instanceof Variant) {
			clearVariantCache();
		}
	}

	/**
	 * Internal method to read literals by value from cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @return the list or <code>null</code> if key is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Param param, E value) {
		/*
		 * check main cache
		 */
		if (cachedLiterals == null) {
			return null;
		}
		/*
		 * get map between value and list
		 */
		Map<Object, List<? extends Construct>> cached = cachedLiterals.get(param);
		if (cached == null) {
			return null;
		}
		return (List<T>) cached.get(value);
	}

	/**
	 * Internal method to add literals by value to cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 */
	protected final <E extends Object, T extends Construct> void store(Param param, E value, List<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedLiterals == null) {
			cachedLiterals = HashUtil.getWeakHashMap();
		}
		/*
		 * get map between value and list
		 */
		Map<Object, List<? extends Construct>> cached = cachedLiterals.get(param);
		if (cached == null) {
			cached = HashUtil.getHashMap();
			cachedLiterals.put(param, cached);
		}
		cached.put(value, values);
	}

	/**
	 * Internal method to read literals by value
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param comparator
	 *            the comparator
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Param param, E value, Comparator<T> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedLiterals == null) {
			return null;
		}
		/*
		 * get mapping of cached compared list by type
		 */
		Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>> cachedCompared = cachedComparedLiterals.get(param);
		if (cachedCompared == null) {
			return null;
		}
		/*
		 * get mapping of compared list by value
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedCompared.get(value);
		if (cached == null) {
			return null;
		}
		/*
		 * get sorted values by comparator
		 */
		return (List<T>) cached.get(comparator);
	}

	/**
	 * Internal method to add literals by value to internal cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final <E extends Object, T extends Construct> void store(Param param, E value, Comparator<T> comparator, List<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedLiterals == null) {
			cachedComparedLiterals = HashUtil.getWeakHashMap();
		}
		/*
		 * get mapping of cached compared list by type
		 */
		Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>> cachedCompared = cachedComparedLiterals.get(param);
		if (cachedCompared == null) {
			cachedCompared = HashUtil.getHashMap();
			cachedComparedLiterals.put(param, cachedCompared);
		}
		/*
		 * get mapping of compared list by value
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedCompared.get(value);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedCompared.put(value, cached);
		}
		/*
		 * store values by comparator
		 */
		cached.put(comparator, values);
	}

	/**
	 * Internal method to read literals by value from cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Param param, E value, Object deviance) {

		/*
		 * check main cache
		 */
		if (cachedLiteralsWithDeviance == null) {
			return null;
		}
		/*
		 * get map between value and deviance-dependent list by type
		 */
		Map<Object, Map<Object, List<? extends Construct>>> map = cachedLiteralsWithDeviance.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get mapping between deviance and literals by value
		 */
		Map<Object, List<? extends Construct>> cached = map.get(value);
		if (cached == null) {
			return null;
		}
		/*
		 * get literals by deviance
		 */
		return (List<T>) cached.get(deviance);
	}

	/**
	 * Internal method to add literals by value to internal cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @param values
	 *            the values to store
	 */
	protected final <E extends Object, T extends Construct> void store(Param param, E value, Object deviance, List<T> values) {

		/*
		 * initialize cache
		 */
		if (cachedLiteralsWithDeviance == null) {
			cachedLiteralsWithDeviance = HashUtil.getWeakHashMap();
		}
		/*
		 * get map between value and deviance-dependent list by type
		 */
		Map<Object, Map<Object, List<? extends Construct>>> map = cachedLiteralsWithDeviance.get(param);
		if (map == null) {
			map = HashUtil.getHashMap();
			cachedLiteralsWithDeviance.put(param, map);
		}
		/*
		 * get mapping between deviance and literals by value
		 */
		Map<Object, List<? extends Construct>> cached = map.get(value);
		if (cached == null) {
			cached = HashUtil.getHashMap();
			map.put(value, cached);
		}
		/*
		 * store literals by deviance
		 */
		cached.put(deviance, values);
	}

	/**
	 * Internal method to read literals by value from cache
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @param comparator
	 *            the comparator
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Param param, E value, Object deviance, Comparator<T> comparator) {
		/*
		 * check main cache
		 */
		if (cachedComparedLiteralsWithDeviance == null) {
			return null;
		}
		/*
		 * get mapping of cached compared list with deviance by type
		 */
		Map<Object, Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>>> map = cachedComparedLiteralsWithDeviance.get(param);
		if (map == null) {
			return null;
		}
		/*
		 * get mapping of deviance and cached comparet literals
		 */
		Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>> cachedCompared = map.get(value);
		if (cachedCompared == null) {
			return null;
		}
		/*
		 * get mapping of compared list by value
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedCompared.get(deviance);
		if (cached == null) {
			return null;
		}
		/*
		 * get sorted values by comparator
		 */
		return (List<T>) cached.get(comparator);
	}

	/**
	 * Internal method to add literals by value to the internal cache.
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final <E extends Object, T extends Construct> void store(Param param, E value, Object deviance, Comparator<T> comparator, List<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedLiteralsWithDeviance == null) {
			cachedComparedLiteralsWithDeviance = HashUtil.getWeakHashMap();
		}
		/*
		 * get mapping of cached compared list with deviance by type
		 */
		Map<Object, Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>>> map = cachedComparedLiteralsWithDeviance.get(param);
		if (map == null) {
			map = HashUtil.getHashMap();
			cachedComparedLiteralsWithDeviance.put(param, map);
		}
		/*
		 * get mapping of deviance and cached comparet literals
		 */
		Map<Object, Map<Comparator<? extends Construct>, List<? extends Construct>>> cachedCompared = map.get(value);
		if (cachedCompared == null) {
			cachedCompared = HashUtil.getHashMap();
			map.put(value, cachedCompared);
		}
		/*
		 * get mapping of compared list by value
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedCompared.get(deviance);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedCompared.put(deviance, cached);
		}
		/*
		 * get sorted values by comparator
		 */
		cached.put(comparator, values);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of returned information items
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Class<E> clazz) {
		/*
		 * check main cache
		 */
		if (cachedConstructs == null) {
			return null;
		}
		/*
		 * get cached constructs by type
		 */
		return (List<T>) cachedConstructs.get(clazz);
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            the type of returned information items
	 * @param values
	 *            the values to store
	 */
	protected final <E extends Object, T extends Construct> void store(Class<E> clazz, List<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * store cached constructs by type
		 */
		cachedConstructs.put(clazz, values);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param comparator
	 *            the comparator
	 * @return the list or <code>null</code> if the key-pair is unknown
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> read(Class<E> clazz, Comparator<T> comparator) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructs == null) {
			return null;
		}
		/*
		 * get compared constructs by type
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedComparedConstructs.get(clazz);
		if (cached == null) {
			return null;
		}
		/*
		 * get cached constructs by comparator
		 */
		return (List<T>) cached.get(comparator);
	}

	/**
	 * Internal method to add constructs of the given type to internal store.
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param clazz
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values to store
	 */
	protected final <E extends Object, T extends Construct> void store(Class<E> clazz, Comparator<T> comparator, List<T> values) {
		/*
		 * initialize cache
		 */
		if (cachedComparedConstructs == null) {
			cachedComparedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * get compared constructs by type
		 */
		Map<Comparator<? extends Construct>, List<? extends Construct>> cached = cachedComparedConstructs.get(clazz);
		if (cached == null) {
			cached = HashUtil.getWeakHashMap();
			cachedComparedConstructs.put(clazz, cached);
		}
		/*
		 * get cached constructs by comparator
		 */
		cached.put(comparator, values);
	}

	/**
	 * Internal method to clear all caches
	 */
	private final void clearCache() {
		if (cachedConstructs != null) {
			cachedConstructs.clear();
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.clear();
		}
		if (cachedLiterals != null) {
			cachedLiterals.clear();
		}
		if (cachedComparedLiterals != null) {
			cachedComparedLiterals.clear();
		}
		if (cachedLiteralsWithDeviance != null) {
			cachedLiteralsWithDeviance.clear();
		}
		if (cachedComparedLiteralsWithDeviance != null) {
			cachedComparedLiteralsWithDeviance.clear();
		}
	}

	/**
	 * Internal method to clear all caches depend on occurrences
	 */
	private final void clearOccurrenceCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Occurrence.class);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Occurrence.class);
		}
		if (cachedLiterals != null) {
			cachedLiterals.clear();
		}
		if (cachedComparedLiterals != null) {
			cachedComparedLiterals.clear();
		}
		if (cachedLiteralsWithDeviance != null) {
			cachedLiteralsWithDeviance.clear();
		}
		if (cachedComparedLiteralsWithDeviance != null) {
			cachedComparedLiteralsWithDeviance.clear();
		}
	}

	/**
	 * Internal method to clear all caches depend on names
	 */
	private final void clearNameCache() {
		clearVariantCache();
		if (cachedConstructs != null) {
			cachedConstructs.remove(Name.class);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Name.class);
		}
		if (cachedLiterals != null) {
			cachedLiterals.remove(Param.STRING);
			cachedLiterals.remove(Param.REGEXP);
		}
		if (cachedComparedLiterals != null) {
			cachedComparedLiterals.remove(Param.STRING);
			cachedComparedLiterals.remove(Param.REGEXP);
		}
		if (cachedLiteralsWithDeviance != null) {
			cachedLiteralsWithDeviance.remove(Param.STRING);
			cachedLiteralsWithDeviance.remove(Param.REGEXP);
		}
		if (cachedComparedLiteralsWithDeviance != null) {
			cachedComparedLiteralsWithDeviance.remove(Param.STRING);
			cachedComparedLiteralsWithDeviance.remove(Param.REGEXP);
		}
	}

	/**
	 * Internal method to clear all caches depend on variants
	 */
	private final void clearVariantCache() {
		if (cachedConstructs != null) {
			cachedConstructs.remove(Variant.class);
		}
		if (cachedComparedConstructs != null) {
			cachedComparedConstructs.remove(Variant.class);
		}
		if (cachedLiterals != null) {
			cachedLiterals.remove(Param.DATATYPEAWARE);
		}
		if (cachedComparedLiterals != null) {
			cachedComparedLiterals.remove(Param.DATATYPEAWARE);
		}
	}

	/**
	 * Returns all characteristics with the given value.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(value));
		store(Param.CHARACTERISTICS, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(value));
		Collections.sort(list, comparator);
		store(Param.CHARACTERISTICS, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given datatype.
	 * 
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Locator datatype, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(datatype));
		store(Param.CHARACTERISTICS, datatype, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given datatype.
	 * 
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(datatype));
		Collections.sort(list, comparator);
		store(Param.CHARACTERISTICS, datatype, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the given datatype.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, Locator datatype, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(value, datatype));
		store(Param.CHARACTERISTICS, value, datatype, list);
		return secureSubList(list, offset, limit);
	}


	/**
	 * Returns all characteristics with the given value and the given datatype.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCharacteristics(String value, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristics(value, datatype));
		Collections.sort(list, comparator);
		store(Param.CHARACTERISTICS, value, datatype, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with matching values
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristicsMatches(regExp));
		store(Param.REGEXP, regExp, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with matching values
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristicsMatches(regExp));
		Collections.sort(list, comparator);
		store(Param.REGEXP, regExp, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression and has the datatype.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with matching values
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristicsMatches(regExp, datatype));
		store(Param.REGEXP, regExp, datatype, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression and has the datatype.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with matching values
	 */
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristicsMatches(regExp, datatype));
		Collections.sort(list, comparator);
		store(Param.REGEXP, regExp, datatype, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:anyURI.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the URI value
	 */

	protected List<ICharacteristics> doGetUris(URI value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getUris(value));
		store(Param.URI, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:anyURI.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the URI value
	 */
	protected List<ICharacteristics> doGetUris(URI value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getUris(value));
		Collections.sort(list, comparator);
		store(Param.URI, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:integer.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the integer value
	 */
	protected List<ICharacteristics> doGetIntegers(int value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getIntegers(value));
		store(Param.INTEGER, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:integer.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the integer value
	 */
	protected List<ICharacteristics> doGetIntegers(int value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getIntegers(value));
		Collections.sort(list, comparator);
		store(Param.INTEGER, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:integer and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetIntegers(int value, double deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getIntegers(value, deviance));
		store(Param.INTEGER, value, deviance, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:integer and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetIntegers(int value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getIntegers(value, deviance));
		Collections.sort(list, comparator);
		store(Param.INTEGER, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:long.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the long value
	 */
	protected List<ICharacteristics> doGetLongs(long value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getLongs(value));
		store(Param.LONG, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:long.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the long value
	 */
	protected List<ICharacteristics> doGetLongs(long value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getLongs(value));
		Collections.sort(list, comparator);
		store(Param.LONG, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:long and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetLongs(long value, double deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getLongs(value, deviance));
		store(Param.LONG, value, deviance, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:long and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetLongs(long value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getLongs(value, deviance));
		Collections.sort(list, comparator);
		store(Param.LONG, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}	

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:float.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the float value
	 */
	protected List<ICharacteristics> doGetFloats(float value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getFloats(value));
		store(Param.FLOAT, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:float.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the float value
	 */
	protected List<ICharacteristics> doGetFloats(float value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getFloats(value));
		Collections.sort(list, comparator);
		store(Param.FLOAT, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:float and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetFloats(float value, double deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getFloats(value, deviance));
		store(Param.FLOAT, value, deviance, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:float and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetFloats(float value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getFloats(value, deviance));
		Collections.sort(list, comparator);
		store(Param.FLOAT, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:double.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the double value
	 */
	protected List<ICharacteristics> doGetDoubles(double value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDoubles(value));
		store(Param.DOUBLE, value, list);
		return secureSubList(list, offset, limit);
	}
	
	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:double.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the double value
	 */
	protected List<ICharacteristics> doGetDoubles(double value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDoubles(value));
		Collections.sort(list, comparator);
		store(Param.DOUBLE, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:double and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetDoubles(double value, double deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDoubles(value, deviance));
		store(Param.DOUBLE, value, deviance, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:double and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetDoubles(double value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDoubles(value, deviance));
		Collections.sort(list, comparator);
		store(Param.DOUBLE, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:dateTime.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the dateTime
	 *         value
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDateTime(value));
		store(Param.DATETIME, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:dateTime.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the dateTime
	 *         value
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDateTime(value));
		Collections.sort(list, comparator);
		store(Param.DATETIME, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:time and with a time
	 * value which has a difference from the given value lower or equals than
	 * the given deviance. The given deviance calendar will be interpreted as
	 * maximum difference from the given value. Each time information handled as
	 * difference. For example if the difference should be lower than one day,
	 * one hour and 10 minutes the dateTime literal should be
	 * <code>0000-00-01T01:10:00</code>.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the time value
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, Calendar deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDateTime(value, deviance));
		store(Param.DATETIME, value, deviance, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:time and with a time
	 * value which has a difference from the given value lower or equals than
	 * the given deviance. The given deviance calendar will be interpreted as
	 * maximum difference from the given value. Each time information handled as
	 * difference. For example if the difference should be lower than one day,
	 * one hour and 10 minutes the dateTime literal should be
	 * <code>0000-00-01T01:10:00</code>.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the time value
	 */
	protected List<ICharacteristics> doGetDateTime(Calendar value, Calendar deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getDateTime(value, deviance));
		Collections.sort(list, comparator);
		store(Param.DATETIME, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:boolean.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the boolean value
	 */
	protected List<ICharacteristics> doGetBooleans(boolean value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getBooleans(value));
		store(Param.BOOLEAN, value, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:boolean.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the boolean value
	 */
	protected List<ICharacteristics> doGetBooleans(boolean value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getBooleans(value));
		Collections.sort(list, comparator);
		store(Param.BOOLEAN, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the given value and the datatype tm:geo.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range with the geographic
	 *         coordinates
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCoordinates(value));
		store(Param.COORDINATES, value, list);
		return secureSubList(list, offset, limit);
	}
	
	/**
	 * Returns all characteristics with the given value and the datatype tm:geo.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range with the geographic
	 *         coordinates
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCoordinates(value));
		Collections.sort(list, comparator);
		store(Param.COORDINATES, value, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all characteristics with the datatype xsd:integer and a
	 * geographical coordinate which has a lower or equal distance to the given
	 * coordinate like the given deviance.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum distance
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCoordinates(value, deviance));
		store(Param.COORDINATES, value, deviance, list);
		return secureSubList(list, offset, limit);
	}
	
	/**
	 * Returns all characteristics with the datatype xsd:integer and a
	 * geographical coordinate which has a lower or equal distance to the given
	 * coordinate like the given deviance.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum distance
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return the characteristics within the given range
	 */
	protected List<ICharacteristics> doGetCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCoordinates(value, deviance));
		Collections.sort(list, comparator);
		store(Param.COORDINATES, value, deviance, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all variants and occurrences with the given data-type.
	 * 
	 * @param dataType
	 *            the data type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of all matching variants and occurrences within the
	 *         given range
	 */
	protected List<IDatatypeAware> doGetDatatypeAwares(Locator dataType, int offset, int limit) {
		List<IDatatypeAware> list = HashUtil.getList(getParentIndex().getDatatypeAwares(dataType));
		store(Param.DATATYPEAWARE, dataType, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Returns all variants and occurrences with the given data-type.
	 * 
	 * @param dataType
	 *            the data type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all matching variants and occurrences within the
	 *         given range
	 */
	protected List<IDatatypeAware> doGetDatatypeAwares(Locator dataType, int offset, int limit, Comparator<IDatatypeAware> comparator) {
			List<IDatatypeAware> list = HashUtil.getList(getParentIndex().getDatatypeAwares(dataType));
			Collections.sort(list, comparator);
			store(Param.DATATYPEAWARE, dataType, comparator, list);
			return secureSubList(list, offset, limit);
		}

	/**
	 * Return all names contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range of the topic map
	 */
	protected List<IName> doGetNames(int offset, int limit) {
		List<IName> list = HashUtil.getList(getParentIndex().getNames());
		store(Name.class, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all names contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range of the topic map
	 */
	protected List<IName> doGetNames(int offset, int limit, Comparator<IName> comparator) {
		List<IName> list = HashUtil.getList(getParentIndex().getNames());
		Collections.sort(list, comparator);
		store(Name.class, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range of the topic map
	 */
	protected List<IOccurrence> doGetOccurrences(int offset, int limit) {
		List<IOccurrence> list = HashUtil.getList(getParentIndex().getOccurrences());
		store(Occurrence.class, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all occurrences contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range of the topic map
	 */
	protected List<IOccurrence> doGetOccurrences(int offset, int limit, Comparator<IOccurrence> comparator) {
		List<IOccurrence> list = HashUtil.getList(getParentIndex().getOccurrences());
		Collections.sort(list, comparator);
		store(Occurrence.class, comparator, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range of the topic map
	 */	
	protected List<IVariant> doGetVariants(int offset, int limit) {
		List<IVariant> list = HashUtil.getList(getParentIndex().getVariants());
		store(Variant.class, list);
		return secureSubList(list, offset, limit);
	}

	/**
	 * Return all variants contained by the current topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range of the topic map
	 */	
	protected List<IVariant> doGetVariants(int offset, int limit, Comparator<IVariant> comparator) {
		List<IVariant> list = HashUtil.getList(getParentIndex().getVariants());
		Collections.sort(list, comparator);
		store(Variant.class, comparator, list);
		return secureSubList(list, offset, limit);
	}

}
