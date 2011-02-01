/**
 * 
 */
package de.topicmapslab.geotype.wgs84;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Sven Krosse
 * 
 */
public class TestWgs84 {

	@Test
	public void testWgs84Degree() {

		double decimalDegree = 52.123;
		double radiant = decimalDegree * Math.PI / 180;

		Wgs84Degree degree = new Wgs84Degree(decimalDegree);
		assertEquals(decimalDegree, degree.getValue());
		assertEquals(radiant, degree.getRadiantValue());
		assertEquals(Double.toString(decimalDegree), degree.toString());

		degree = new Wgs84Degree(Double.toString(decimalDegree));
		degree.declareAsLatitude();
		assertEquals(decimalDegree, degree.getValue());
		assertEquals(radiant, degree.getRadiantValue());
		assertEquals(Double.toString(decimalDegree), degree.toString());
	}

	@Test
	public void testWgs84Coordinate() throws Exception {
		double lat1 = 52.5164;
		double lon1 = 13.3777;
		double lat2 = 38.692668;
		double lon2 = -9.177944;

		Wgs84Coordinate coordA = new Wgs84Coordinate(lat1, lon1);
		assertEquals(lat1, coordA.getLatitude().getValue());
		assertEquals(lon1, coordA.getLongitude().getValue());
		assertEquals(lat1 + ";" + lon1, coordA.toString());

		Wgs84Coordinate coordB = new Wgs84Coordinate(lat2, lon2);
		assertEquals(lat2, coordB.getLatitude().getValue());
		assertEquals(lon2, coordB.getLongitude().getValue());
		assertEquals(lat2 + ";" + lon2, coordB.toString());

		assertEquals(2317.722, coordA.getDistance(coordB), 0.5);
		assertEquals(0D, coordA.getDistance(coordA));
		assertEquals(2317.722, coordB.getDistance(coordA), 0.5);
		assertEquals(0D, coordB.getDistance(coordB));

		coordA = new Wgs84Coordinate(lat1 + ";" + lon1);
		assertEquals(lat1, coordA.getLatitude().getValue());
		assertEquals(lon1, coordA.getLongitude().getValue());
		assertEquals(lat1 + ";" + lon1, coordA.toString());

		coordB = new Wgs84Coordinate(lat2 + ";" + lon2);
		assertEquals(lat2, coordB.getLatitude().getValue());
		assertEquals(lon2, coordB.getLongitude().getValue());
		assertEquals(lat2 + ";" + lon2, coordB.toString());

		assertEquals(2317.722, coordA.getDistance(coordB), 0.5);
		assertEquals(0D, coordA.getDistance(coordA));
		assertEquals(2317.722, coordB.getDistance(coordA), 0.5);
		assertEquals(0D, coordB.getDistance(coordB));
		
		coordB = new Wgs84Coordinate(" " + lat2 + " ; " + lon2 + " ");
		assertEquals(lat2, coordB.getLatitude().getValue());
		assertEquals(lon2, coordB.getLongitude().getValue());
		assertEquals(lat2 + ";" + lon2, coordB.toString());

		assertEquals(2317.722, coordA.getDistance(coordB), 0.5);
		assertEquals(0D, coordA.getDistance(coordA));
		assertEquals(2317.722, coordB.getDistance(coordA), 0.5);
		assertEquals(0D, coordB.getDistance(coordB));
	}

}
