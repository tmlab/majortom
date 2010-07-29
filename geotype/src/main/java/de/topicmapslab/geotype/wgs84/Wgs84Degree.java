package de.topicmapslab.geotype.wgs84;

import java.util.regex.Pattern;

public class Wgs84Degree {

	public enum Orientation {
		N,

		E,

		S,

		W
	}

	public static Pattern regExp = Pattern.compile("[0-9]+°[0-9]+'([0-9]+(.[0-9]+)?'')?( [NSEW])?");

	private final double degree;
	private final double minute;
	private final double second;
	private final double value;
	private final Orientation orientation;

	public Wgs84Degree(final double decimalValue, final Orientation orientation) {
		this.value = decimalValue;
		this.orientation = orientation;
		this.degree = Math.round(value);
		this.minute = Math.round((value - degree) * 60);
		this.second = Double.valueOf(Math.round((value - (degree + minute / 60)) * 360000)) / 100;
	}

	public Wgs84Degree(final double degree, final double minute, final Orientation orientation) {
		this(degree, minute, 0, orientation);
	}

	public Wgs84Degree(final double degree, final double minute, final double second, final Orientation orientation) {
		this.degree = degree;
		this.minute = minute;
		this.second = second;
		this.orientation = orientation;
		this.value = ((second / 60) + minute) / 60 + degree;
	}
	
	public Wgs84Degree(final String value) throws NumberFormatException {
		this.value = Double.parseDouble(value.substring(0, value.length()-1));
		this.orientation = Orientation.valueOf(value.substring(value.length()-1));	
		this.degree = Math.round(this.value);
		this.minute = Math.round((this.value - degree) * 60);
		this.second = Double.valueOf(Math.round((this.value - (degree + minute / 60)) * 360000)) / 100;
	}

	/**
	 * Returns the internal value of decimal degree
	 * 
	 * @return the double value
	 */
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value + orientation.name();
	}
	
	/**
	 * @return the minute
	 */
	public double getMinute() {
		return minute;
	}
	
	/**
	 * @return the second
	 */
	public double getSecond() {
		return second;
	}
	
	/**
	 * @return the orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}
	
	/**
	 * @return the degree
	 */
	public double getDegree() {
		return degree;
	}
}
