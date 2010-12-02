/**
 * 
 */

package de.topicmapslab.majortom.importer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.MIOException;
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
	 * Imports a topic map read by the input stream
	 * @param is
	 * @param baseIRI
	 * @throws SQLException
	 * @throws IOException
	 * @throws MIOException
	 */
	public static void importFile(InputStream is, String baseIRI) throws SQLException, IOException, MIOException {
		CTMDeserializerFactory fac = new CTMDeserializerFactory();
		IDeserializer deserializer = fac.createDeserializer();

		deserializer.setMapHandler(new MapHandler());
		deserializer.parse(new Source(is, baseIRI));
	}

}
