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
package de.topicmapslab.geotype.mecator;

import java.text.ParseException;

import de.topicmapslab.geotype.model.IGeoCircuit;
import de.topicmapslab.geotype.model.IGeoSurface;

/**
 * @author Sven Krosse
 * 
 */
public class MecatorCircuit implements IGeoCircuit<MecatorCoordinate> {

	private MecatorCoordinate center;
	private double radius;

	/**
	 * Constructor
	 * 
	 * @param center
	 * @param radius
	 */
	public MecatorCircuit(MecatorCoordinate center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	/**
	 * constructor
	 * @param value the string literal
	 * @throws ParseException thrown by {@link #parse(String)}
	 */
	public MecatorCircuit(final String value) throws ParseException{
		parse(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public MecatorCoordinate getCenterPoint() {
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
	public boolean contains(MecatorCoordinate coord) {
		return coord.getDistance(center) <= radius;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean intersects(IGeoSurface<MecatorCoordinate> surface) {
		if (surface instanceof MecatorCircuit) {
			MecatorCircuit m = ((MecatorCircuit) surface);
			return m.getCenterPoint().getDistance(center) <= (radius + m.getRadius());
		}
		throw new IllegalArgumentException("Unsupported surface type.");
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return center.toString() + ";" + Double.toString(radius);
	}

	/**
	 * {@inheritDoc}
	 */
	public void parse(String value) throws ParseException {
		try {
			String[] parts = value.split(";");
			center = new MecatorCoordinate(parts[0]);
			radius = Double.parseDouble(parts[1]);
		} catch (Exception e) {
			throw new ParseException(value, 0);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if ( obj instanceof MecatorCircuit){
			return ((MecatorCircuit) obj).getCenterPoint().equals(center) && ((MecatorCircuit) obj).getRadius() == radius;
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {		
		return super.hashCode();
	}

}
