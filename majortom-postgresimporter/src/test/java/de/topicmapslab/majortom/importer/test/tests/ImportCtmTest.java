package de.topicmapslab.majortom.importer.test.tests;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.importer.ImportFile;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;

public class ImportCtmTest {

	private TopicMap db_map;
	private TopicMap memory_map;
	
	
	@Before
	public void setUp() throws Exception{
		
		
		String databaseBaseLocator = "http://dbimporter/test/";
		String fileName = "manual.ctm";
		
		// get database
		TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
		db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getCanonicalName());
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", "hass.tm.informatik.uni-leipzig.de:5432");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", "majortom");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", "postgres");
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");
		
		TopicMapSystem db_system = db_factory.newTopicMapSystem();
		this.db_map = (ITopicMap)db_system.createTopicMap(databaseBaseLocator);
				
		// clear database
		this.db_map.clear();
		
		// load file into database
		InputStream is = ImportCtmTest.class.getResourceAsStream("/" + fileName);
		if (is==null)
			throw new Exception("Couldn't find " + fileName);
		
		ImportFile.importFile(is, databaseBaseLocator);
		
		// load file into memory
		TopicMapSystemFactory memory_factory = TopicMapSystemFactory.newInstance();
		memory_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		
		TopicMapSystem memory_system = memory_factory.newTopicMapSystem();
		this.memory_map = (ITopicMap)memory_system.createTopicMap(databaseBaseLocator);
		
		
		
	}
	
	@Test
	public void test(){
		
	}
	
}
