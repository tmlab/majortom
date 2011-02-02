import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.DATABASE;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.HOST;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.PASSWORD;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.USERNAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.format_estimator.FormatEstimator.Format;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.importer.Importer;
import de.topicmapslab.majortom.importer.file.FileWriterMapHandler;
import de.topicmapslab.majortom.importer.file.PSQLTask;
import de.topicmapslab.majortom.importer.model.IHandler;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;
/**
 * 
 */

/**
 * @author Sven Krosse
 * 
 */
public class ImportTask {

	public static void main(String[] args) throws Exception{
		
		final String dir = "D:/epg-xtms";
		
		final String databaseBaseLocator = "http://psi.freebase.de/";

		Properties p = new Properties();
		p.setProperty(HOST, "localhost");
		p.setProperty(DATABASE, "freebase-16600");
		p.setProperty(USERNAME, "postgres");
		p.setProperty(PASSWORD, "postgres");
		
		// get database
		TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
		db_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getCanonicalName());
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", "freebase-16600");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");

		TopicMapSystem db_system = db_factory.newTopicMapSystem();
		db_system.createTopicMap(databaseBaseLocator);		

		final String target = "D:/epg-xtms/full-fb.sql";
		File f = new File(target);
		FileOutputStream out = new FileOutputStream(f);
		IHandler handler = new FileWriterMapHandler(out,p);

		for (final String file : new File(dir).list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
//				return name.endsWith(".xtm") && name.startsWith("export");
				return name.equals("fb-16600.xtm");
			}
		})) {
			// load file into database
			InputStream is = new FileInputStream(new File(dir + "/" + file));
			long t = System.currentTimeMillis();
			System.out.println("Start to convert XTM to SQL ...");
			Importer.importStream(handler, is, databaseBaseLocator, Format.XTM_2_1);
			System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
		}		

//		final String dir = "D:/epg-xtms";
//
////		ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
////		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
//
//		int i = 0;
//		for (final String file : new File(dir).list(new FilenameFilter() {
//			
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.endsWith(".xtm");
//			}
//		})) {
//			final String f = dir + "/" + file;
//			EPGCallable callable = new EPGCallable(f, i);
//			callable.call();
////			futures.add(threadPool.submit(callable));
//			i++;
//		}

//		threadPool.shutdown();

//		for (Future<Boolean> future : futures) {
//			try {
//				future.get();
//				System.out.println(future.toString() + " finished successful!");
//			} catch (Exception e) {
//				e.printStackTrace(System.err);
//			}
//		}

	}

}

class EPGCallable implements Callable<Boolean> {

	final String file;
	final int number;

	/**
	 * 
	 */
	public EPGCallable(final String file, final int number) {
		this.file = file;
		this.number = number;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean call() throws Exception {

		final String databaseBaseLocator = "http://psi.freebase.de/";

		Properties p = new Properties();
		p.setProperty(HOST, "localhost");
		p.setProperty(DATABASE, "epg-" + number);
		p.setProperty(USERNAME, "postgres");
		p.setProperty(PASSWORD, "postgres");
		
		// get database
		TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
		db_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getCanonicalName());
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", "epg-" + number);
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");

		TopicMapSystem db_system = db_factory.newTopicMapSystem();
		db_system.createTopicMap(databaseBaseLocator);

		// load file into database
		InputStream is = new FileInputStream(new File(file));

		final String target = file.replace(".xtm", ".sql");
		File f = new File(target);
		FileOutputStream out = new FileOutputStream(f);

		long t = System.currentTimeMillis();
		System.out.println("Start to convert XTM to SQL ...");
		Importer.importStream(new FileWriterMapHandler(out,p), is, databaseBaseLocator, Format.XTM_2_1);
		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
		t = System.currentTimeMillis();
		System.out.println("Start to write SQL to DB ...");
		PSQLTask.runPSQL("C:/Programme/PostgreSQL/8.4//bin", target, "epg-" + number, "postgres", "postgres");
		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");

		return true;
	}
}
