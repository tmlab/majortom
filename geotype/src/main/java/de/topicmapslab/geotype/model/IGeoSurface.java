package de.topicmapslab.geotype.model;

import java.text.ParseException;

public interface IGeoSurface<T extends IGeoCoordinate> {

	/**
	 * Checks if there is an intersection between the given surface and the
	 * current one.
	 * 
	 * @param surface the other surface
	 * @return <code>true</code> if there is an intersection, <code>false</code>
	 *         otherwise
	 */
	boolean intersects(IGeoSurface<T> surface);

	/**
	 * Checks if the given coordinate is contained by the current one
	 * 
	 * @param coord the coordinates
	 * @return <code>true</code> if the coordinate is contained,
	 *         <code>false</code> otherwise.
	 */
	boolean contains(T coord);

	/**
	 * Parse the internal values from the given string.
	 * 
	 * @param value the value
	 * @throws ParseException thrown if the value cannot parse or the pattern is invalid
	 */
	public void parse(final String value) throws ParseException;
}
