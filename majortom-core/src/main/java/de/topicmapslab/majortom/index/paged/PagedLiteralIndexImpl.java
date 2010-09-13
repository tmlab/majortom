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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.index.core.BaseCachedLiteralIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public abstract class PagedLiteralIndexImpl<X extends ITopicMapStore> extends BaseCachedLiteralIndexImpl<X> implements IPagedLiteralIndex {

	private final ILiteralIndex parentIndex;

	/**
	 * @param store
	 * @param parentIndex
	 */
	public PagedLiteralIndexImpl(X store, ILiteralIndex parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * @return the parentIndex
	 */
	public ILiteralIndex getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Boolean.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetBooleans(value, offset, limit);
			cache(Boolean.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getBooleans(boolean value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Boolean.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetBooleans(value, offset, limit, comparator);
			cache(Boolean.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(value, offset, limit);
			cache(String.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(value, offset, limit, comparator);
			cache(String.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, datatype, null, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(datatype, offset, limit);
			cache(String.class, datatype, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, datatype, null, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(datatype, offset, limit, comparator);
			cache(String.class, datatype, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, value, datatype, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristics(value, datatype, offset, limit);
			cache(String.class, value, datatype, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristics(String value, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(String.class, value, datatype, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristics(value, datatype, offset, limit, comparator);
			cache(String.class, value, datatype, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
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
		Collection<ICharacteristics> results = read(Pattern.class, regExp, null, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp, offset, limit);
			cache(Pattern.class, regExp, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Pattern.class, regExp, null, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp, offset, limit, comparator);
			cache(Pattern.class, regExp, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Pattern.class, regExp, datatype, offset, limit, null);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp, datatype, offset, limit);
			cache(Pattern.class, regExp, datatype, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Pattern.class, regExp, datatype, offset, limit, comparator);
		if (results == null) {
			results = doGetCharacteristicsMatches(regExp, datatype, offset, limit, comparator);
			cache(Pattern.class, regExp, datatype, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetCoordinates(value, offset, limit);
			cache(Wgs84Coordinate.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetCoordinates(value, offset, limit, comparator);
			cache(Wgs84Coordinate.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetCoordinates(value, deviance, offset, limit);
			cache(Wgs84Coordinate.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getCoordinates(Wgs84Coordinate value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Wgs84Coordinate.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetCoordinates(value, deviance, offset, limit, comparator);
			cache(Wgs84Coordinate.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(Locator dataType, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IDatatypeAware> results = read(IDatatypeAware.class, dataType, offset, limit, null);
		if (results == null) {
			results = doGetDatatypeAwares(dataType, offset, limit);
			cache(IDatatypeAware.class, dataType, offset, limit, null, results);
		}
		return (List<IDatatypeAware>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IDatatypeAware> getDatatypeAwares(Locator dataType, int offset, int limit, Comparator<IDatatypeAware> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IDatatypeAware> results = read(ILocator.class, dataType, null, offset, limit, comparator);
		if (results == null) {
			results = doGetDatatypeAwares(dataType, offset, limit, comparator);
			cache(ILocator.class, dataType, null, offset, limit, comparator, results);
		}
		return (List<IDatatypeAware>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetDateTime(value, offset, limit);
			cache(Calendar.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetDateTime(value, offset, limit, comparator);
			cache(Calendar.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetDateTime(value, deviance, offset, limit);
			cache(Calendar.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDateTime(Calendar value, Calendar deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Calendar.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetDateTime(value, deviance, offset, limit, comparator);
			cache(Calendar.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Double.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetDoubles(value, offset, limit);
			cache(Double.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Double.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetDoubles(value, offset, limit, comparator);
			cache(Double.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Double.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetDoubles(value, deviance, offset, limit);
			cache(Double.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getDoubles(double value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Double.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetDoubles(value, deviance, offset, limit, comparator);
			cache(Double.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Float.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetFloats(value, offset, limit);
			cache(Float.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Float.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetFloats(value, offset, limit, comparator);
			cache(Float.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Float.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetFloats(value, deviance, offset, limit);
			cache(Float.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getFloats(float value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Float.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetFloats(value, deviance, offset, limit, comparator);
			cache(Float.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Integer.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetIntegers(value, offset, limit);
			cache(Integer.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Integer.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetIntegers(value, offset, limit, comparator);
			cache(Integer.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Integer.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetIntegers(value, deviance, offset, limit);
			cache(Integer.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getIntegers(int value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Integer.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetIntegers(value, deviance, offset, limit, comparator);
			cache(Integer.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Long.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetLongs(value, offset, limit);
			cache(Long.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Long.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetLongs(value, offset, limit, comparator);
			cache(Long.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Long.class, value, deviance, offset, limit, null);
		if (results == null) {
			results = doGetLongs(value, deviance, offset, limit);
			cache(Long.class, value, deviance, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getLongs(long value, double deviance, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(Long.class, value, deviance, offset, limit, comparator);
		if (results == null) {
			results = doGetLongs(value, deviance, offset, limit, comparator);
			cache(Long.class, value, deviance, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IName> results = read(IName.class, offset, limit, null);
		if (results == null) {
			results = doGetNames(offset, limit);
			cache(IName.class, offset, limit, null, results);
		}
		return (List<IName>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IName> getNames(int offset, int limit, Comparator<IName> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IName> results = read(IName.class, offset, limit, comparator);
		if (results == null) {
			results = doGetNames(offset, limit, comparator);
			cache(IName.class, offset, limit, comparator, results);
		}
		return (List<IName>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IOccurrence> results = read(IOccurrence.class, offset, limit, null);
		if (results == null) {
			results = doGetOccurrences(offset, limit);
			cache(IOccurrence.class, offset, limit, null, results);
		}
		return (List<IOccurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IOccurrence> getOccurrences(int offset, int limit, Comparator<IOccurrence> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IOccurrence> results = read(IOccurrence.class, offset, limit, comparator);
		if (results == null) {
			results = doGetOccurrences(offset, limit, comparator);
			cache(IOccurrence.class, offset, limit, comparator, results);
		}
		return (List<IOccurrence>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(URI.class, value, null, offset, limit, null);
		if (results == null) {
			results = doGetUris(value, offset, limit);
			cache(URI.class, value, null, offset, limit, null, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ICharacteristics> getUris(URI value, int offset, int limit, Comparator<ICharacteristics> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> results = read(URI.class, value, null, offset, limit, comparator);
		if (results == null) {
			results = doGetUris(value, offset, limit, comparator);
			cache(URI.class, value, null, offset, limit, comparator, results);
		}
		return (List<ICharacteristics>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IVariant> results = read(IVariant.class, offset, limit, null);
		if (results == null) {
			results = doGetVariants(offset, limit);
			cache(IVariant.class, offset, limit, null, results);
		}
		return (List<IVariant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IVariant> getVariants(int offset, int limit, Comparator<IVariant> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<IVariant> results = read(IVariant.class, offset, limit, comparator);
		if (results == null) {
			results = doGetVariants(offset, limit, comparator);
			cache(IVariant.class, offset, limit, comparator, results);
		}
		return (List<IVariant>) results;
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		if (!parentIndex.isOpen()) {
			parentIndex.open();
		}
		super.open();
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
	protected List<ICharacteristics> doGetCharacteristicsMatches(Pattern regExp, Locator datatype, int offset, int limit, Comparator<ICharacteristics> comparator) {
		List<ICharacteristics> list = HashUtil.getList(getParentIndex().getCharacteristicsMatches(regExp, datatype));
		Collections.sort(list, comparator);
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
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
		return HashUtil.secureSubList(list, offset, limit);
	}

}
