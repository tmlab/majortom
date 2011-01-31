package de.topicmapslab.majortom.memory.importer;

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
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;


public class Importer {

	public static void importFile(InMemoryTopicMapStore store, File file, String baseIRI) throws MIOException {
		try {
			MapHandler mapHandler = new MapHandler(store, baseIRI);

			Format format = FormatEstimator.guessFormat(new FileReader(file));

			importStream(new FileInputStream(file), baseIRI, mapHandler, format);
		} catch (FileNotFoundException e) {
			throw new MIOException(e);
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}
	
	public static void importStream(InMemoryTopicMapStore store, InputStream is, String baseIRI, Format format)
			throws MIOException {
		MapHandler mapHandler = new MapHandler(store, baseIRI);
		importStream(is, baseIRI, mapHandler, format);
	}
	

	
	private static void importStream(InputStream is, String baseIRI, MapHandler mapHandler, Format format)
			throws MIOException {

		IDeserializerFactory fac = null;

		switch (format) {
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
