package de.topicmapslab.geotype.mecator;

import java.text.ParseException;

import de.topicmapslab.geotype.model.IGeoCoordinate;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate;

public class MecatorCoordinate implements IGeoCoordinate {

	private double y;
	private double x;

	public MecatorCoordinate(final Wgs84Coordinate latitude, final Wgs84Coordinate longitude) {
		this.x = latitude.getValue() - 90;
		this.y = 0.5 * Math.log((1 + Math.sin(longitude.getValue())) / (1 - Math.sin(longitude.getValue())));
	}

	public MecatorCoordinate(final String value) throws ParseException {
		parse(value);
	}

	public MecatorCoordinate(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	// TODO implement
	public double getDistance(IGeoCoordinate coord) {
		if (coord instanceof MecatorCoordinate) {
			// Coordinate a = new Coordinate(x, y);
			// Coordinate b = new Coordinate(((MecatorCoordinate) coord).getX(),
			// ((MecatorCoordinate) coord).getY());
			// return a.distance(b);
		}
		throw new IllegalArgumentException("Invalid coordinate type. Expects mecator coordinates.");
	}

	@Override
	public String toString() {
		return "[" + getX() + "," + getY() + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	public void parse(String value) throws ParseException {
		try {
			String[] parts = value.substring(1, value.length() - 1).split(",");
			try {
				x = Wgs84Coordinate.parse(parts[0]).getValue();
			} catch (Exception e) {
				x = Double.parseDouble(parts[0]);
			}
			try {
				y = Wgs84Coordinate.parse(parts[1]).getValue();
			} catch (Exception e) {
				y = Double.parseDouble(parts[1]);
			}
		} catch (Exception e) {
			throw new ParseException(value, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof MecatorCoordinate) {
			return ((MecatorCoordinate) obj).getX() == x && ((MecatorCoordinate) obj).getY() == y;
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
