package de.topicmapslab.geotype.model;

public interface IGeoSurface<T extends IGeoCoordinate<?>> {

	/**
	 * Checks if the given coordinate is contained by the current one
	 * 
	 * @param coord
	 *            the coordinates
	 * @return <code>true</code> if the coordinate is contained,
	 *         <code>false</code> otherwise.
	 */
	boolean contains(T coord);
}
