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

import de.topicmapslab.geotype.model.IGeoCircuit;

/**
 * @author Sven Krosse
 * 
 */
public final class Wgs84Circuit implements IGeoCircuit<Wgs84Coordinate> {

	private final Wgs84Coordinate center;
	private final double radius;

	/**
	 * constructor
	 * 
	 * @param value
	 */
	public Wgs84Circuit(final String value) throws ParseException {
		StringTokenizer tokenizer = new StringTokenizer(value, "~");
		try {
			this.center = new Wgs84Coordinate(tokenizer.nextToken());
			this.radius = Double.parseDouble(tokenizer.nextToken());
		} catch (Exception e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * constructor
	 * 
	 * @param center
	 *            the center point
	 * @param radius
	 *            the radius
	 */
	public Wgs84Circuit(Wgs84Coordinate center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	/**
	 * {@inheritDoc}
	 */
	public Wgs84Coordinate getCenterPoint() {
		return center;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getRadius() {
		return radius;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Wgs84Coordinate coord) {
		return this.getCenterPoint().getDistance(coord) < radius;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return this.center.toString() + "~" + getRadius();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Wgs84Circuit) {
			Wgs84Circuit other = (Wgs84Circuit) obj;
			return getCenterPoint().equals(other.getCenterPoint()) && getRadius() == other.getRadius();
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return center.hashCode() | ((Double)radius).hashCode();
	}

}
