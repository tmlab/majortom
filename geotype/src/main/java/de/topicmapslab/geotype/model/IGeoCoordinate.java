package de.topicmapslab.geotype.model;

import java.text.ParseException;

public interface IGeoCoordinate {

	/**
	 * Returns the distance between the given coordinate and the current
	 * instance.
	 * 
	 * @param coord
	 *            the other coordinates
	 * @return the distance in meters
	 */
	double getDistance(IGeoCoordinate coord);
	
	/**
	 * Parse the internal values from the given string.
	 * 
	 * @param value the value
	 * @throws ParseException thrown if the value cannot parse or the pattern is invalid
	 */
	public void parse(final String value) throws ParseException;
}
