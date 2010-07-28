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
package de.topicmapslab.majortom.model.index;

import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.regex.Pattern;

import org.tmapi.core.Locator;
import org.tmapi.index.LiteralIndex;

import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IVariant;

/**
 * Interface definition of an index for characteristics of topic items.
 * 
 * @author Sven Krosse
 * 
 */
public interface ILiteralIndex extends LiteralIndex {

	/**
	 * Returns all characteristics with the given value.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(final String value);

	/**
	 * Returns all characteristics with the given datatype.
	 * 
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(final Locator datatype);

	/**
	 * Returns all characteristics with the given value and the given datatype.
	 * 
	 * @param value
	 *            the value of the characteristics
	 * @param datatype
	 *            the datatype
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(final String value, final Locator datatype);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the characteristics with matching values
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(final String regExp);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression and has the datatype.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * @return the characteristics with matching values
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(final String regExp, final Locator datatype);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return the characteristics with matching values
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(final Pattern regExp);

	/**
	 * Returns all characteristics which has a value matches the given regular
	 * expression and has the datatype.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @param datatype
	 *            the datatype
	 * @return the characteristics with matching values
	 */
	public Collection<ICharacteristics> getCharacteristicsMatches(final Pattern regExp, final Locator datatype);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:anyURI.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the URI value
	 */
	public Collection<ICharacteristics> getUris(final URI value);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:integer.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the integer value
	 */
	public Collection<ICharacteristics> getIntegers(final int value);

	/**
	 * Returns all characteristics with the datatype xsd:integer and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getIntegers(final int value, final double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:long.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the long value
	 */
	public Collection<ICharacteristics> getLongs(final long value);

	/**
	 * Returns all characteristics with the datatype xsd:long and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getLongs(final long value, final double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:float.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the float value
	 */
	public Collection<ICharacteristics> getFloats(final float value);

	/**
	 * Returns all characteristics with the datatype xsd:float and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getFloats(final float value, final double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:double.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the double value
	 */
	public Collection<ICharacteristics> getDoubles(final double value);

	/**
	 * Returns all characteristics with the datatype xsd:double and a value
	 * contained by the given range [value - deviance, value + deviance].
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum difference
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getDoubles(final double value, final double deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:dateTime.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the dateTime value
	 */
	public Collection<ICharacteristics> getDateTime(final Calendar value);

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
	 * @return the characteristics with the time value
	 */
	public Collection<ICharacteristics> getDateTime(final Calendar value, final Calendar deviance);

	/**
	 * Returns all characteristics with the given value and the datatype
	 * xsd:boolean.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the boolean value
	 */
	public Collection<ICharacteristics> getBooleans(final boolean value);

	/**
	 * Returns all characteristics with the given value and the datatype tm:geo.
	 * 
	 * @param value
	 *            the value
	 * @return the characteristics with the geographic coordinates
	 */
	public Collection<ICharacteristics> getCoordinates(final IGeoCoordinate value);

	/**
	 * Returns all characteristics with the datatype xsd:integer and a
	 * geographical coordinate which has a lower or equal distance to the given
	 * coordinate like the given deviance.
	 * 
	 * @param value
	 *            the value
	 * @param deviance
	 *            the maximum distance
	 * @return the characteristics
	 */
	public Collection<ICharacteristics> getCoordinates(final IGeoCoordinate value, final double deviance);

	/**
	 * Returns all variants and occurrences with the given data-type.
	 * 
	 * @param dataType
	 *            the data type
	 * @return a collection of all matching variants and occurrences
	 */
	public Collection<IDatatypeAware> getDatatypeAwares(final Locator dataType);

	/**
	 * Return all names contained by the current topic map.
	 * 
	 * @return all names of the topic map
	 */
	public Collection<IName> getNames();

	/**
	 * Return all occurrences contained by the current topic map.
	 * 
	 * @return all occurrences of the topic map
	 */
	public Collection<IOccurrence> getOccurrences();

	/**
	 * Return all variants contained by the current topic map.
	 * 
	 * @return all variants of the topic map
	 */
	public Collection<IVariant> getVariants();
}
