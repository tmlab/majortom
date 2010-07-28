package de.topicmapslab.geotype.model;

public interface IGeoCircuit<T extends IGeoCoordinate> extends IGeoSurface<T> {

	/**
	 * Returns the center of this sphere.
	 * 
	 * @return the center coordinates
	 */
	T getCenterPoint();

	/**
	 * Method returns the radius of this sphere.
	 * 
	 * @return the radius in meter
	 */
	double getRadius();

}
