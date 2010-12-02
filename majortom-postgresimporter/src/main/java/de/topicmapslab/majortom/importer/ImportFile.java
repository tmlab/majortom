/**
 * 
 */

package de.topicmapslab.majortom.importer;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.Source;
import com.semagia.mio.ctm.CTMDeserializerFactory;

/**
 * Main class of the console application
 * 
 * @author Hannes Niederhausen
 * 
 */
public class ImportFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// TODO readargs

			CTMDeserializerFactory fac = new CTMDeserializerFactory();
			IDeserializer deserializer = fac.createDeserializer();

			deserializer.setMapHandler(new MapHandler());
			deserializer.parse(new Source("file:/tmp/manual.ctm"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
