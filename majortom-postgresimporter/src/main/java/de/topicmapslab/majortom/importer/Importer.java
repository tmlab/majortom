/**
 * 
 */

package de.topicmapslab.majortom.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.semagia.mio.IDeserializer;
import com.semagia.mio.IDeserializerFactory;
import com.semagia.mio.MIOException;
import com.semagia.mio.Source;
import com.semagia.mio.ctm.CTMDeserializerFactory;
import com.semagia.mio.jtm.JTMDeserializerFactory;
import com.semagia.mio.ltm.LTMDeserializerFactory;
import com.semagia.mio.xtm.XTMDeserializerFactory;

import de.topicmapslab.format_estimator.FormatEstimator;
import de.topicmapslab.format_estimator.FormatEstimator.Format;

/**
 * Main class which provides some static methods to import from files and streams
 * 
 * @author Hannes Niederhausen
 * 
 */
public class Importer {

	/**
	 * Imports a topic map read by the file
	 * 
	 * @param file file to load
	 * @param baseIRI base iri to use
	 * @param dbProperties database properties overriding values in db.properties
	 * @throws MIOException
	 */
	public static void importFile(File file, String baseIRI, Properties dbProperties) throws MIOException {
		try {
			MapHandler mapHandler = new MapHandler(dbProperties);
			
			Format format = FormatEstimator.guessFormat(new FileReader(file));
			
			
			importStream(new FileInputStream(file), baseIRI, mapHandler, format);
		} catch (FileNotFoundException e) {
			throw new MIOException(e);
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}
	
	/**
	 * Imports a topic map read by the file
	 * 
	 * @param file file to load
	 * @param baseIRI base iri to use
	 * @param dbProperties database properties overriding values in db.properties
	 * @throws MIOException
	 */
	public static void importFile(File file, String baseIRI) throws MIOException {
		try {
			MapHandler mapHandler = new MapHandler();
			Format format = FormatEstimator.guessFormat(new FileReader(file));
			importStream(new FileInputStream(file), baseIRI, mapHandler, format);
		} catch (FileNotFoundException e) {
			throw new MIOException(e);
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}
	
	/**
	 * Imports a topic map read by the input stream
	 * 
	 * @param is the {@link InputStream} of the serialized topic map
	 * @param baseIRI the base iri for the topic map
	 * 
	 * @throws MIOException
	 */
	public static void importStream(InputStream is, String baseIRI, Format format,  Properties dbProperties) throws MIOException {
		MapHandler mapHandler = new MapHandler(dbProperties);
		importStream(is, baseIRI, mapHandler, format);
	}
	
	/**
	 * Imports a topic map read by the input stream
	 * 
	 * @param is the {@link InputStream} of the serialized topic map
	 * @param baseIRI the base iri for the topic map
	 * @param format the serialization format
	 * @throws MIOException
	 */
	public static void importStream(InputStream is, String baseIRI, Format format) throws MIOException {
		MapHandler mapHandler = new MapHandler();
		importStream(is, baseIRI, mapHandler, format);
	}

	
	
	/**
	 * Instantiates the deserilizer for the given format and reads the topic map
	 * 
	 * @param is the {@link InputStream} for the serialized topic map
	 * @param baseIRI the base iri to use for the topic map
	 * @param mapHandler the {@link MapHandler} 
	 * @param format the format of the topic map
	 * @throws MIOException if somethihng went wront
	 */
	private static void importStream(InputStream is, String baseIRI, MapHandler mapHandler, Format format) throws MIOException {
		
		IDeserializerFactory fac = null;
		
		switch(format) {
		case CTM:
		case CTM_1_0:
			fac = new CTMDeserializerFactory();
			break;
		case JTM:
		case JTM_1_0:
		case JTM_1_1:
			fac = new JTMDeserializerFactory();
			break;
		case LTM:
		case LTM_1_0:
		case LTM_1_1:
		case LTM_1_2:
		case LTM_1_3:
			fac = new LTMDeserializerFactory();
		case XTM:
		case XTM_1_0:
		case XTM_1_1:
		case XTM_2_0:
		case XTM_2_1:
			fac = new XTMDeserializerFactory();
			break;
		default:
			throw new MIOException("Unsupported Format");
		}
		
		
		try {
			IDeserializer deserializer = fac.createDeserializer();
			deserializer.setMapHandler(mapHandler);
			deserializer.parse(new Source(is, baseIRI));
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}
	
}
