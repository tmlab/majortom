package de.topicmapslab.geotype.wgs84;

import java.util.regex.Pattern;

public class Wgs84Degree {

	public enum Orientation {
		N,

		E,

		S,

		W
	}

	public static Pattern regExp = Pattern
			.compile("[0-9]+°[0-9]+'([0-9]+(.[0-9]+)?'')?( [NSEW])?");

	private final double degree;
	private final double minute;
	private final double second;
	private final double value;
	private Orientation orientation;

	public Wgs84Degree(final double decimalValue) {
		this.value = decimalValue;
		double value = Math.round(this.value);
		this.degree = Math.floor(value);
		this.minute = Math.floor((value - degree) * 60);
		this.second = Double.valueOf(Math
				.floor((value - (degree + minute / 60)) * 360000)) / 100;
	}

	public Wgs84Degree(final double degree, final double minute,
			final Orientation orientation) {
		this(degree, minute, 0, orientation);
	}

	public Wgs84Degree(final double degree, final double minute,
			final double second, final Orientation orientation) {
		this.degree = degree;
		this.minute = minute;
		this.second = second;
		this.orientation = orientation;
		/*
		 * degrees of western or southern hemisphere are negative
		 */
		this.value = (((second / 60) + minute) / 60 + degree)
				* (orientation == Orientation.W || orientation == Orientation.S ? -1
						: 1);
	}

	public Wgs84Degree(final String value) throws NumberFormatException {
		this.value = Double.parseDouble(value);
		this.degree = Math.round(this.value);
		this.minute = Math.round((this.value - degree) * 60);
		this.second = Double.valueOf(Math
				.round((this.value - (degree + minute / 60)) * 360000)) / 100;
	}

	/**
	 * Returns the internal value of decimal degree
	 * 
	 * @return the double value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Returns the internal value of decimal degree in radiant
	 * 
	 * @return the double value
	 */
	public double getRadiantValue() {
		return value * Math.PI / 180;
	}

	@Override
	public String toString() {
		return Double.toString(getValue());
	}

	/**
	 * Returns the whole information about this WGS 84 degree
	 * 
	 * @return the WGS 84 degree information
	 */
	public String print() {
		StringBuilder builder = new StringBuilder();
		builder.append("decimal:\t");
		builder.append(getValue());
		builder.append("\r\n");
		builder.append("orientation:\t");
		builder.append(orientation.name());
		builder.append("\r\n");
		builder.append("degree:\t");
		builder.append(degree);
		builder.append("\r\n");
		builder.append("minutes:\t");
		builder.append(minute);
		builder.append("\r\n");
		builder.append("seconds:\t");
		builder.append(second);
		builder.append("\r\n");
		return builder.toString();
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

	/**
	 * Declare the internal value as latitude value. If the value is negative
	 * the orientation set to {@link Orientation#S}, otherwise to
	 * {@link Orientation#N}
	 */
	void declareAsLatitude() {
		if (value < 0) {
			orientation = Orientation.S;
		} else {
			orientation = Orientation.N;
		}
	}

	/**
	 * Declare the internal value as longitude value. If the value is negative
	 * the orientation set to {@link Orientation#W}, otherwise to
	 * {@link Orientation#E}
	 */
	void declareAsLongitude() {
		if (value < 0) {
			orientation = Orientation.W;
		} else {
			orientation = Orientation.E;
		}
	}
}
