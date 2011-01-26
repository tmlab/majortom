/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.importer.test.tests;

import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.DATABASE;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.HOST;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.PASSWORD;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.USERNAME;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.format_estimator.FormatEstimator.Format;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.importer.Importer;
import de.topicmapslab.majortom.importer.file.FileWriterMapHandler;
import de.topicmapslab.majortom.importer.file.ImportToDatabaseTask;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * @author Sven Krosse
 * 
 */
public class TestEpgImport {

	private TopicMap db_map;
	private String databaseBaseLocator;
	private String user;
	private String password;
	private String database;

	/**
	 * 
	 * Starts the import, creates a db and memory store and exports the two topic maps into the tmp directory
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		databaseBaseLocator = "http://psi.freebase.de/";

		// get database
		TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
		db_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);

		db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getCanonicalName());

		InputStream is = getClass().getResourceAsStream("/db.properties");
		Properties properties = new Properties();
		properties.load(is);

		db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", properties.get(HOST));
		database = properties.get(DATABASE).toString();
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", properties.get(DATABASE));
		user = properties.get(USERNAME).toString();
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", properties.get(USERNAME));
		password = properties.get(PASSWORD).toString();
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", properties.get(PASSWORD));

		db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");

		TopicMapSystem db_system = db_factory.newTopicMapSystem();
		this.db_map = (ITopicMap) db_system.createTopicMap(databaseBaseLocator);

		// clear database
		this.db_map.clear();

	}
	@After
	public void tearDown(){
//		this.db_map.remove();
	}

	@Test
	public void testImportFromXTM() throws Exception {
		// load file into database
		String fileName = "D:/export.xtm";
		InputStream is = new FileInputStream(new File(fileName));

		long t = System.currentTimeMillis();
		System.out.print("Start import XTM ...");
		Importer.importStream(is, databaseBaseLocator, Format.XTM_2_1);
		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
	}

	@Test
	public void testFileImporter() throws Exception {
		// load file into database
		String fileName = "D:/export.xtm";
		InputStream is = new FileInputStream(new File(fileName));

		File f = new File("D:/export.sql");
		FileOutputStream out = new FileOutputStream(f);
//		ByteArrayOutputStream out = new ByteArrayOutputStream(90000000);

		long t = System.currentTimeMillis();
		System.out.print("Start to convert XTM to SQL ...");
		Importer.importStream(new FileWriterMapHandler(out), is, databaseBaseLocator,
				Format.XTM_2_1);
		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
		t = System.currentTimeMillis();
		System.out.println("Start to write SQL to DB ...");
		ImportToDatabaseTask.importSql("C:/Programme/PostgreSQL/8.4//bin","D:/export.sql", database, user, password);		
		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
//		ISession session = ((JdbcTopicMapStore)((ITopicMap)this.db_map).getStore()).openSession();
//		t = System.currentTimeMillis();		
//		System.out.print("Start to write SQL to DB ...");
//		session.getConnection().createStatement().execute(out.toString());
//		System.out.println(" finished after " + (System.currentTimeMillis() - t) + "ms");
	}

}
