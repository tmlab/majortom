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
package de.topicmapslab.majortom.model.index.paging;

import java.net.URI;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.index.IIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;

/**
 * Special {@link ILiteralIndex} supporting paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedLiteralIndex extends IIndex {

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
	public List<ICharacteristics> getCharacteristics(final String value,
			int offset, int limit);

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
	public List<ICharacteristics> getCharacteristics(final String value,
			int offset, int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristics(final Locator datatype,
			int offset, int limit);

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
	public List<ICharacteristics> getCharacteristics(final Locator datatype,
			int offset, int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristics(final String value,
			final Locator datatype, int offset, int limit);

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
	public List<ICharacteristics> getCharacteristics(final String value,
			final Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final String regExp, int offset, int limit);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final String regExp, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final String regExp, final Locator datatype, int offset, int limit);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final String regExp, final Locator datatype, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final Pattern regExp, int offset, int limit);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final Pattern regExp, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final Pattern regExp, final Locator datatype, int offset, int limit);

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
	public List<ICharacteristics> getCharacteristicsMatches(
			final Pattern regExp, final Locator datatype, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getUris(final URI value, int offset, int limit);

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
	public List<ICharacteristics> getUris(final URI value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getIntegers(final int value, int offset,
			int limit);

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
	public List<ICharacteristics> getIntegers(final int value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getIntegers(final int value,
			final double deviance, int offset, int limit);

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
	public List<ICharacteristics> getIntegers(final int value,
			final double deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getLongs(final long value, int offset,
			int limit);

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
	public List<ICharacteristics> getLongs(final long value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getLongs(final long value,
			final double deviance, int offset, int limit);

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
	public List<ICharacteristics> getLongs(final long value,
			final double deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getFloats(final float value, int offset,
			int limit);

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
	public List<ICharacteristics> getFloats(final float value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getFloats(final float value,
			final double deviance, int offset, int limit);

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
	public List<ICharacteristics> getFloats(final float value,
			final double deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getDoubles(final double value, int offset,
			int limit);

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
	public List<ICharacteristics> getDoubles(final double value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getDoubles(final double value,
			final double deviance, int offset, int limit);

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
	public List<ICharacteristics> getDoubles(final double value,
			final double deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getDateTime(final Calendar value, int offset,
			int limit);

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
	public List<ICharacteristics> getDateTime(final Calendar value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getDateTime(final Calendar value,
			final Calendar deviance, int offset, int limit);

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
	public List<ICharacteristics> getDateTime(final Calendar value,
			final Calendar deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getBooleans(final boolean value, int offset,
			int limit);

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
	public List<ICharacteristics> getBooleans(final boolean value, int offset,
			int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCoordinates(final Wgs84Coordinate value,
			int offset, int limit);

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
	public List<ICharacteristics> getCoordinates(final Wgs84Coordinate value,
			int offset, int limit, Comparator<ICharacteristics> comparator);

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
	public List<ICharacteristics> getCoordinates(final Wgs84Coordinate value,
			final double deviance, int offset, int limit);

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
	public List<ICharacteristics> getCoordinates(final Wgs84Coordinate value,
			final double deviance, int offset, int limit,
			Comparator<ICharacteristics> comparator);

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
	public List<IDatatypeAware> getDatatypeAwares(final Locator dataType,
			int offset, int limit);

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
	public List<IDatatypeAware> getDatatypeAwares(final Locator dataType,
			int offset, int limit, Comparator<IDatatypeAware> comparator);

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
	public List<Name> getNames(int offset, int limit);

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
	public List<Name> getNames(int offset, int limit,
			Comparator<Name> comparator);

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
	public List<Occurrence> getOccurrences(int offset, int limit);

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
	public List<Occurrence> getOccurrences(int offset, int limit,
			Comparator<Occurrence> comparator);

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
	public List<Variant> getVariants(int offset, int limit);

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
	public List<Variant> getVariants(int offset, int limit,
			Comparator<Variant> comparator);

}
