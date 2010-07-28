package de.topicmapslab.geotype.wgs84;

import java.text.ParseException;
import java.util.regex.Pattern;

public class Wgs84Coordinate {

	enum Orientation {
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

	public Wgs84Coordinate(final double decimalValue, final Orientation orientation) {
		this.value = decimalValue;
		this.orientation = orientation;
		this.degree = Math.round(value);
		this.minute = Math.round((value - degree) * 60);
		this.second = Double.valueOf(Math.round((value - (degree + minute / 60)) * 360000)) / 100;
	}

	public Wgs84Coordinate(final double degree, final double minute, final Orientation orientation) {
		this(degree, minute, 0, orientation);
	}

	public Wgs84Coordinate(final double degree, final double minute, final double second, final Orientation orientation) {
		this.degree = degree;
		this.minute = minute;
		this.second = second;
		this.orientation = orientation;
		this.value = ((second / 60) + minute) / 60 + degree;
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
		StringBuilder builder = new StringBuilder();
		builder.append(Math.round(degree) + "°");
		builder.append(Math.round(minute) + "'");
		if (second > 0) {
			builder.append(Double.toString(second) + "''");
		}
		builder.append(" " + orientation.name());
		return builder.toString();
	}

	public static Wgs84Coordinate parse(final String value) throws ParseException {
		if (!regExp.matcher(value).matches()) {
			throw new ParseException("Invalid coordinate literal. Expects: " + regExp.pattern(), 0);
		}

		return null;
	}

}
