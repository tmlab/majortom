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
	public Wgs84Coordinate(Wgs84Degree latitude, Wgs84Degree longitude,
			long altitude) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}

	/**
	 * constructor
	 * 
	 * @param latitude
	 *            the latitude in decimal degree
	 * 
	 * @param longitude
	 *            the longitude in decimal degree
	 */
	public Wgs84Coordinate(double latitude, double longitude) {
		this.latitude = new Wgs84Degree(latitude);
		this.latitude.declareAsLatitude();
		this.longitude = new Wgs84Degree(longitude);
		this.longitude.declareAsLongitude();
		this.altitude = null;
	}

	/**
	 * constructor
	 * 
	 * @param latitude
	 *            the latitude in decimal degree
	 * 
	 * @param longitude
	 *            the longitude in decimal degree
	 * @param altitude
	 *            the altitude
	 */
	public Wgs84Coordinate(double latitude, double longitude, long altitude) {
		this.latitude = new Wgs84Degree(latitude);
		this.latitude.declareAsLatitude();
		this.longitude = new Wgs84Degree(longitude);
		this.longitude.declareAsLongitude();
		this.altitude = altitude;
	}

	/**
	 * constructor
	 * 
	 * @param latitude
	 *            the latitude
	 * 
	 * @param longitude
	 *            the longitude
	 */
	public Wgs84Coordinate(Wgs84Degree latitude, Wgs84Degree longitude) {		
		this.latitude = latitude;
		this.latitude.declareAsLatitude();
		this.longitude = longitude;
		this.longitude.declareAsLongitude();
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
	public Wgs84Coordinate(String value) throws ParseException {
		StringTokenizer tokenizer = new StringTokenizer(value, ";");
		try {
			this.latitude = new Wgs84Degree(tokenizer.nextToken());
			this.latitude.declareAsLatitude();
			this.longitude = new Wgs84Degree(tokenizer.nextToken());
			this.longitude.declareAsLongitude();
			if (tokenizer.hasMoreTokens()) {
				this.altitude = Long.parseLong(tokenizer.nextToken());
			} else {
				this.altitude = null;
			}
		} catch (Exception e) {
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
		double lat1 = getLatitude().getRadiantValue();
		double lat2 = coord.getLatitude().getRadiantValue();
		double lon1 = getLongitude().getRadiantValue();
		double lon2 = coord.getLongitude().getRadiantValue();
		/*
		 * great circle distance d between two points with coordinates
		 * {lat1,lon1} and {lat2,lon2}
		 * 
		 * d = acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2))
		 */
		return 6378.388 * Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof Wgs84Coordinate) {
			Wgs84Coordinate other = (Wgs84Coordinate) arg0;
			boolean result = longitude.getValue() == other.longitude.getValue()
					&& latitude.getValue() == other.latitude.getValue();
			result &= (altitude == null) ? (other.altitude == null) : (altitude
					.equals(other.altitude));
			return result;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return latitude.hashCode() | longitude.hashCode()
				| (altitude == null ? 0 : altitude.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(latitude);
		builder.append(";");
		builder.append(longitude);
		if (altitude != null) {
			builder.append(";");
			builder.append(altitude);
		}
		return builder.toString();
	}

	/**
	 * Returns the whole information about this WGS 84 coordinate
	 * 
	 * @return the WGS 84 coordinate information
	 */
	public String print() {
		StringBuilder builder = new StringBuilder();
		builder.append("Latitude:\r\n");
		builder.append(latitude.print());
		builder.append("\r\nLongitude:\r\n");
		builder.append(longitude.print());
		builder.append("\r\nAltitude:\r\n");
		builder.append(altitude == null ? "0" : altitude);
		return builder.toString();
	}

}
