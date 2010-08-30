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
package de.topicmapslab.majortom.model.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;

import org.tmapi.core.DatatypeAware;

import de.topicmapslab.geotype.model.IGeoSurface;
import de.topicmapslab.geotype.wgs84.Wgs84Circuit;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;

/**
 * Interface definition of a data type aware extensions handling extended data
 * types like geographical coordinates.
 * 
 * @author Sven Krosse
 * 
 */
public interface IDatatypeAware extends DatatypeAware, IConstruct {

	/**
	 * Returns the characteristics value as boolean literal.
	 * 
	 * @return the boolean value of this characteristics
	 */
	public Boolean booleanValue() throws ParseException;

	/**
	 * Returns the characteristics value as double literal.
	 * 
	 * @return the double value of this characteristics
	 */
	public Double doubleValue() throws NumberFormatException;

	/**
	 * Returns the characteristics value as URI literal.
	 * 
	 * @return the URI value of this characteristics
	 */
	public URI uriValue() throws URISyntaxException;

	/**
	 * Returns the characteristics value as dateTime literal.
	 * 
	 * @return the dateTime value of this characteristics
	 */
	public Calendar dateTimeValue() throws ParseException;

	/**
	 * Returns the characteristics value as geographic coordinate literal.
	 * 
	 * @return the geographic coordinate of this characteristics
	 */
	public Wgs84Coordinate coordinateValue() throws ParseException;

	/**
	 * Returns the characteristics value as geographic surface literal.
	 * 
	 * @return the geographic surface of this characteristics
	 */
	public IGeoSurface<?> surfaceValue() throws ParseException;

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:boolean.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final Boolean value);

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:double.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final Double value);

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:dateTime.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final Calendar value);

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:anyURI.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final URI value);

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:geoCoordinate.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final Wgs84Coordinate value);

	/**
	 * Changes the value of this characteristics to the given value. The
	 * datatype will be changed to xsd:surface.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(final Wgs84Circuit value);

}
