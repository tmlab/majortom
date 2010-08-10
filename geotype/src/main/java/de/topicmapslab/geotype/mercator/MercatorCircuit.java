///*******************************************************************************
// * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package de.topicmapslab.geotype.mercator;
//
//import de.topicmapslab.geotype.model.IGeoCircuit;
//import de.topicmapslab.geotype.model.IGeoSurface;
//
///**
// * @author Sven Krosse
// * 
// */
//public class MercatorCircuit implements IGeoCircuit<MercatorCoordinate> {
//
//	private MercatorCoordinate center;
//	private double radius;
//
//	/**
//	 * Constructor
//	 * 
//	 * @param center
//	 * @param radius
//	 */
//	public MercatorCircuit(MercatorCoordinate center, double radius) {
//		this.center = center;
//		this.radius = radius;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public MercatorCoordinate getCenterPoint() {
//		return center;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public double getRadius() {
//		return radius;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public boolean contains(MercatorCoordinate coord) {
//		return coord.getDistance(center) <= radius;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public boolean intersects(IGeoSurface<MercatorCoordinate> surface) {
//		if (surface instanceof MercatorCircuit) {
//			MercatorCircuit m = ((MercatorCircuit) surface);
//			return m.getCenterPoint().getDistance(center) <= (radius + m.getRadius());
//		}
//		throw new IllegalArgumentException("Unsupported surface type.");
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public String toString() {
//		return center.toString() + ";" + Double.toString(radius);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public boolean equals(Object obj) {
//		if (obj instanceof MercatorCircuit) {
//			return ((MercatorCircuit) obj).getCenterPoint().equals(center) && ((MercatorCircuit) obj).getRadius() == radius;
//		}
//		return false;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public int hashCode() {
//		return super.hashCode();
//	}
//
//}
