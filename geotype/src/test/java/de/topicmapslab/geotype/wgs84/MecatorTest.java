package de.topicmapslab.geotype.wgs84;

import junit.framework.TestCase;
import de.topicmapslab.geotype.mecator.MecatorCoordinate;
import de.topicmapslab.geotype.wgs84.Wgs84Coordinate.Orientation;

public class MecatorTest extends TestCase {

	public void testname() {
		MecatorCoordinate m1 = new MecatorCoordinate(new Wgs84Coordinate(
				52.5164, Orientation.N), new Wgs84Coordinate(13.3777,
				Orientation.E));
		System.out.println(m1);
		MecatorCoordinate m2 = new MecatorCoordinate(new Wgs84Coordinate(
				38.692668, Orientation.N), new Wgs84Coordinate(-9.177944,
				Orientation.E));
		System.out.println(m2);

//		System.out.println(m1.getDistance(m2));
	}

}
