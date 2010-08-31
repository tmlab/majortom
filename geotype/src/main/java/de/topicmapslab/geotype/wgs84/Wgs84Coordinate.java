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
package de.topicmapslab.geotype.wgs84;

import java.text.ParseException;
import java.util.StringTokenizer;

import de.topicmapslab.geotype.model.IGeoCoordinate;

/**
 * @author Sven Krosse
 * 
 */
public final class Wgs84Coordinate implements IGeoCoordinate<Wgs84Coordinate> {

	private final Wgs84Degree longitude;
	private final Wgs84Degree latitude;
	private final Long altitude;

	/**
	 * constructor
	 * 
	 * @param longitude
	 *            the longitude
	 * @param latitude
	 *            the latitude
	 * @param altitude
	 *            the altitude
	 */
	public Wgs84Coordinate(Wgs84Degree latitude, Wgs84Degree longitude, long altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	/**
	 * constructor
	 * 
	 * @param longitude
	 *            the longitude
	 * @param latitude
	 *            the latitude
	 */
	public Wgs84Coordinate(Wgs84Degree longitude, Wgs84Degree latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = null;
	}
	
	/**
	 * constructor
	 * 
	 * @param longitude
	 *            the longitude
	 * @param latitude
	 *            the latitude
	 */
	public Wgs84Coordinate(String value) throws ParseException{
		String v = value.substring(1, value.length()-1);
		StringTokenizer tokenizer = new StringTokenizer(v,";");
		try{
			this.latitude = new Wgs84Degree(tokenizer.nextToken());
			this.longitude = new Wgs84Degree(tokenizer.nextToken());
			if ( tokenizer.hasMoreTokens()){
				this.altitude = Long.parseLong(tokenizer.nextToken());
			}else{
				this.altitude = null;
			}
		}catch(Exception e){
			throw new ParseException(value, 0);
		}
	}

	/**
	 * @return the longitude
	 */
	public Wgs84Degree getLongitude() {
		return longitude;
	}

	/**
	 * @return the latitude
	 */
	public Wgs84Degree getLatitude() {
		return latitude;
	}

	/**
	 * @return the altitude
	 */
	public Long getAltitude() {
		return altitude;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getDistance(Wgs84Coordinate coord) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof Wgs84Coordinate) {
			Wgs84Coordinate other = (Wgs84Coordinate) arg0;
			boolean result = longitude.getValue() == other.longitude.getValue() && latitude.getValue() == other.latitude.getValue();
			result &= (altitude == null) ? (other.altitude == null) : (altitude.equals(other.altitude));
			return result;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return latitude.hashCode() | longitude.hashCode() | (altitude == null ? 0 : altitude.hashCode());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "{" + latitude.toString() + ";" + longitude.toString() +";" + (altitude==null?"":altitude.toString())+"}";
	}

}
