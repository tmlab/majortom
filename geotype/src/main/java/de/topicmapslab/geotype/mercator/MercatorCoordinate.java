//package de.topicmapslab.geotype.mercator;
//
//import de.topicmapslab.geotype.model.IGeoCoordinate;
//import de.topicmapslab.geotype.wgs84.Wgs84Degree;
//
//public class MercatorCoordinate implements IGeoCoordinate {
//
//	private double y;
//	private double x;
//
//	public MercatorCoordinate(final Wgs84Degree latitude, final Wgs84Degree longitude) {
//		this.x = latitude.getValue() - 90;
//		this.y = 0.5 * Math.log((1 + Math.sin(longitude.getValue())) / (1 - Math.sin(longitude.getValue())));
//	}
//
//	public MercatorCoordinate(final double x, final double y) {
//		this.x = x;
//		this.y = y;
//	}
//
//	public double getX() {
//		return x;
//	}
//
//	public double getY() {
//		return y;
//	}
//
//	@Override
//	// TODO implement
//	public double getDistance(IGeoCoordinate coord) {
//		if (coord instanceof MercatorCoordinate) {
//			// Coordinate a = new Coordinate(x, y);
//			// Coordinate b = new Coordinate(((MecatorCoordinate) coord).getX(),
//			// ((MecatorCoordinate) coord).getY());
//			// return a.distance(b);
//		}
//		throw new IllegalArgumentException("Invalid coordinate type. Expects mecator coordinates.");
//	}
//
//	@Override
//	public String toString() {
//		return "[" + getX() + "," + getY() + "]";
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public boolean equals(Object obj) {
//		if (obj instanceof MercatorCoordinate) {
//			return ((MercatorCoordinate) obj).getX() == x && ((MercatorCoordinate) obj).getY() == y;
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
//}
