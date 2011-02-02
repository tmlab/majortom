import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.DATABASE;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.HOST;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.PASSWORD;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.USERNAME;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * 
 */

/**
 * @author Sven
 * 
 */
public class Extractor {

	/**
	 * 
	 */
	private static final String UTF_8 = "UTF-8";
	/**
	 * 
	 */
	private static final String STRING = "\r\n";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		final String databaseBaseLocator = "http://psi.freebase.de/";

		Set<String> ids = new HashSet<String>();
		List<String> ids_ = new ArrayList<String>();

		for (int i = 0; i < 5; i++) {
			
			long size = ids_.size();
			long size_ = ids.size();

			Properties p = new Properties();
			p.setProperty(HOST, "localhost");
			p.setProperty(DATABASE, "epg-" + i);
			p.setProperty(USERNAME, "postgres");
			p.setProperty(PASSWORD, "postgres");

			// get database
			TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
			db_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
			db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
					JdbcTopicMapStore.class.getCanonicalName());
			db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", "localhost");
			db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", "epg-" + i);
			db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres");
			db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres");
			db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");

			TopicMapSystem db_system = db_factory.newTopicMapSystem();
			ITopicMap tm = (ITopicMap) db_system.createTopicMap(databaseBaseLocator);
			ISession s = ((JdbcTopicMapStore) tm.getStore()).openSession();

			final String query = "SELECT value FROM occurrences WHERE id_type IN ( SELECT id_topic FROM locators, rel_subject_identifiers WHERE id_locator = id AND reference = 'http://epg.topicmapslab.de/freebase_id' )";

			Connection c = s.getConnection();
			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				ids.add(rs.getString(1));
				ids_.add(rs.getString(1));
			}
			rs.close();
			st.close();
			c.close();
			
			System.out.println("Before: " + size + " after: " + ids_.size());
			System.err.println("Before: " + size_ + " after: " + ids.size());
		}
//		FileOutputStream stream = new FileOutputStream(new File("D:/epg-xtms/fb.txt"));
//		for (String id : ids) {
//			stream.write(id.getBytes(UTF_8));
//			stream.write(STRING.getBytes(UTF_8));
//			stream.flush();
//		}
//		stream.flush();
//		stream.close();
		
		System.out.println("All Ids: " + ids_.size());
		System.out.println("Merged Ids: " + ids.size());
	}
}
