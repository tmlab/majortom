package de.topicmapslab.geotype.model;

public interface IGeoCoordinate<T extends IGeoCoordinate<?>> {

	/**
	 * Returns the distance between the given coordinate and the current
	 * instance.
	 * 
	 * @param coord
	 *            the other coordinates
	 * @return the distance in meters
	 */
	double getDistance(T coord);
}
