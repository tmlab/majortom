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
package de.topicmapslab.majortom.inmemory.index.paged;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
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

import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedLiteralIndex extends InMemoryPagedIndex<ILiteralIndex> implements IPagedLiteralIndex {

	enum Param {
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
	public InMemoryPagedLiteralIndex(InMemoryTopicMapStore store, ILiteralIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.BOOLEAN, "getBooleans", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.BOOLEAN, "getBooleans", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.STRING, "getCharacteristics", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.STRING, "getCharacteristics", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATATYPE, "getCharacteristics", datatype, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATATYPE, "getCharacteristics", datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.CHARACTERISTICS, "getCharacteristics", value, datatype, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.CHARACTERISTICS, "getCharacteristics", value, datatype, offset, limit, comparator);
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
	public List<ICharacteristics> getCharacteristicsMatches(String regExp, Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator) {
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
		return getLiterals(Param.REGEXP, "getCharacteristicsMatches", regExp, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.REGEXP, "getCharacteristicsMatches", regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.REGEXP, "getCharacteristicsMatches", regExp, datatype, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.REGEXP, "getCharacteristicsMatches", regExp, datatype, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(IGeoCoordinate value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.COORDINATES, "getCoordinates", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(IGeoCoordinate value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.COORDINATES, "getCoordinates", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(IGeoCoordinate value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.COORDINATES, "getCoordinates", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(IGeoCoordinate value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.COORDINATES, "getCoordinates", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(ILocator dataType, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATATYPEAWARE, "getDatatypeAwares", dataType, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(ILocator dataType, int offset, int limit, Comparator<IDatatypeAware> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATATYPEAWARE, "getDatatypeAwares", dataType, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATETIME, "getDateTime", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATETIME, "getDateTime", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATETIME, "getDateTime", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DATETIME, "getDateTime", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DOUBLE, "getDoubles", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DOUBLE, "getDoubles", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DOUBLE, "getDoubles", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.DOUBLE, "getDoubles", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.FLOAT, "getFloats", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.FLOAT, "getFloats", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.FLOAT, "getFloats", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.FLOAT, "getFloats", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.INTEGER, "getIntegers", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.INTEGER, "getIntegers", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.INTEGER, "getIntegers", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.INTEGER, "getIntegers", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.LONG, "getLongs", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.LONG, "getLongs", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.LONG, "getLongs", value, deviance, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.LONG, "getLongs", value, deviance, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Name.class, "getNames", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit, Comparator<IName> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Name.class, "getNames", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Occurrence.class, "getOccurrences", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit, Comparator<IOccurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Occurrence.class, "getOccurrences", offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.URI, "getUris", value, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getLiterals(Param.URI, "getUris", value, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Variant.class, "getVariants", offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit, Comparator<IVariant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getConstructs(Variant.class, "getVariants", offset, limit, comparator);
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
		
		Object dependValue= null;
		
		/*
		 * construct was removed
		 */
		if ( event == TopicMapEventType.CONSTRUCT_REMOVED ){
			dependValue = oldValue;
		}
		/*
		 * new construct
		 */
		else if ( event == TopicMapEventType.OCCURRENCE_ADDED || event == TopicMapEventType.NAME_ADDED || event == TopicMapEventType.VARIANT_ADDED){
			dependValue = newValue;
		}
		/*
		 * data type or value modified
		 */
		else if ( event == TopicMapEventType.DATATYPE_SET || event == TopicMapEventType.VALUE_MODIFIED){
			dependValue = notifier;
		}
		
		/*
		 * clear dependent caches
		 */
		if ( dependValue instanceof Occurrence ){
			clearOccurrenceCache();
		}else if ( dependValue instanceof Name ){
			clearNameCache();
		}else if ( dependValue instanceof Variant){
			clearVariantCache();
		}
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
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param value
	 *            the value
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getLiterals(Param param, final String methodName, E value, int offset, int limit) {
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
		List<T> list = (List<T>) cached.get(value);
		if (list == null) {
			try {
				/*
				 * call method to get literals
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, value.getClass());
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), value));
				/*
				 * store list
				 */
				cached.put(value, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
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
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param value
	 *            the value
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getLiterals(Param param, final String methodName, E value, int offset, int limit,
			Comparator<T> comparator) {
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
		 * get sorted values by comparator
		 */
		List<T> list = (List<T>) cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get literals
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, value.getClass());
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), value));
				/*
				 * sort and store list
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
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
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getLiterals(Param param, final String methodName, E value, Object deviance, int offset,
			int limit) {
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
		 * get literals by deviance
		 */
		List<T> list = (List<T>) cached.get(deviance);
		if (list == null) {
			try {
				/*
				 * call method to get literals
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, param.getClass(), deviance.getClass());
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), value, deviance));
				/*
				 * store list
				 */
				cached.put(deviance, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
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
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param value
	 *            the value
	 * @param deviance
	 *            the deviance
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getLiterals(Param param, final String methodName, E value, Object deviance, int offset,
			int limit, Comparator<T> comparator) {
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
		List<T> list = (List<T>) cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get literals
				 */
				Method method = getParentIndex().getClass().getMethod(methodName, value.getClass(), deviance.getClass());
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex(), value, deviance));
				/*
				 * sort and store list
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @param comparator
	 *            the comparator
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getConstructs(Class<E> clazz, final String methodName, int offset, int limit) {
		/*
		 * initialize cache
		 */
		if (cachedConstructs == null) {
			cachedConstructs = HashUtil.getWeakHashMap();
		}
		/*
		 * get cached constructs by type
		 */
		List<T> list = (List<T>) cachedConstructs.get(clazz);
		if (list == null) {
			try {
				/*
				 * call method to get constructs
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex()));
				/*
				 * store list
				 */
				cachedConstructs.put(clazz, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
	}

	/**
	 * Internal method to read constructs
	 * 
	 * @param <E>
	 *            the value type of literals ( Boolean, Calendar etc. )
	 * @param <T>
	 *            the literals type to return
	 * @param param
	 *            value type of literals ( Boolean, Calendar etc. )
	 * @param methodName
	 *            the method name to call if literals are not cached
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return the list within the given range
	 */
	@SuppressWarnings("unchecked")
	private final <E extends Object, T extends Construct> List<T> getConstructs(Class<E> clazz, final String methodName, int offset, int limit,
			Comparator<T> comparator) {
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
		List<T> list = (List<T>) cached.get(comparator);
		if (list == null) {
			try {
				/*
				 * call method to get constructs
				 */
				Method method = getParentIndex().getClass().getMethod(methodName);
				list = HashUtil.getList((Collection<T>) method.invoke(getParentIndex()));
				/*
				 * sort and store list
				 */
				Collections.sort(list, comparator);
				cached.put(comparator, list);
			} catch (SecurityException e) {
				throw new IndexException(e);
			} catch (NoSuchMethodException e) {
				throw new IndexException(e);
			} catch (IllegalArgumentException e) {
				throw new IndexException(e);
			} catch (IllegalAccessException e) {
				throw new IndexException(e);
			} catch (InvocationTargetException e) {
				throw new IndexException(e);
			}
		}
		return secureSubList(list, offset, limit);
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

}
