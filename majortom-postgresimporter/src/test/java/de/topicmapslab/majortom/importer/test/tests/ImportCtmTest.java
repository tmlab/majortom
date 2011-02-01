package de.topicmapslab.majortom.importer.test.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.core.Variant;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.format_estimator.FormatEstimator.Format;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.importer.Importer;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.io.CXTMTopicMapWriter;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

/**
 * 
 * CTM import test
 * 
 *
 */
public class ImportCtmTest {

	private TopicMap db_map;
	private TopicMap memory_map;
	private String databaseBaseLocator;
	
	
	/**
	 * 
	 * Starts the import, creates a db and memory store and exports the two topic
	 * maps into the tmp directory
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		
		databaseBaseLocator = "http://psi.freebase.de/";
		String fileName = "freebasefull.ctm";
		
		// get database
		TopicMapSystemFactory db_factory = TopicMapSystemFactory.newInstance();
		db_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		
		db_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getCanonicalName());
		
		InputStream is = getClass().getResourceAsStream("/db.properties");
		Properties properties = new Properties();
		properties.load(is);
		
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.host", properties.get(HOST));
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.database", properties.get(DATABASE));
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.user", properties.get(USERNAME));
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.password", properties.get(PASSWORD));
		
		db_factory.setProperty("de.topicmapslab.majortom.jdbc.dialect", "POSTGRESQL");
		
		TopicMapSystem db_system = db_factory.newTopicMapSystem();
		this.db_map = (ITopicMap)db_system.createTopicMap(databaseBaseLocator);
				
		// clear database
		this.db_map.clear();
		
		// load file into database
		is = ImportCtmTest.class.getResourceAsStream("/" + fileName);
		if (is==null)
			throw new Exception("Couldn't find " + fileName);
		
		Importer.importStream(is, databaseBaseLocator, Format.CTM);
		
		// load file into memory
		TopicMapSystemFactory memory_factory = TopicMapSystemFactory.newInstance();
		memory_factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		memory_factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		
		TopicMapSystem memory_system = memory_factory.newTopicMapSystem();
		this.memory_map = (ITopicMap)memory_system.createTopicMap(databaseBaseLocator);
		
		is = ImportCtmTest.class.getResourceAsStream("/" + fileName);
		if (is==null)
			throw new Exception("Couldn't find " + fileName);
		
		CTMTopicMapReader reader = new CTMTopicMapReader(this.memory_map, is, databaseBaseLocator);
		reader.read();
		
	}
	@Test
	public void testImport() throws Exception {
		
	}
	
	/**
	 * Test if the hello_welt topic has a type
	 */
	@Test
	public void type() {
		Locator si = db_map.createLocator("http://test.de/hallo_welt");
		
		Topic dbTopic = db_map.getTopicBySubjectIdentifier(si);
		Topic memTopic = memory_map.getTopicBySubjectIdentifier(si);
		
		assertEquals(1, memTopic.getTypes().size());
		assertEquals(1, dbTopic.getTypes().size());
	}
	
	
	/**
	 * Checks if all identifiers of hello_welt are imported
	 */
	@Test
	public void identifier() {
		Locator si = db_map.createLocator("http://test.de/hallo_welt");
		
		Topic dbTopic = db_map.getTopicBySubjectIdentifier(si);
		Topic memTopic = memory_map.getTopicBySubjectIdentifier(si);
		
		assertEquals(1, memTopic.getSubjectIdentifiers().size());
		assertEquals(si, memTopic.getSubjectIdentifiers().iterator().next());
		assertEquals(1, memTopic.getItemIdentifiers().size());
		assertEquals(1, memTopic.getSubjectLocators().size());
		
		
		assertEquals(1, dbTopic.getSubjectIdentifiers().size());
		assertEquals(si, dbTopic.getSubjectIdentifiers().iterator().next());
		assertEquals(1, dbTopic.getItemIdentifiers().size());
		assertEquals(1, dbTopic.getSubjectLocators().size());
	}
	
	/**
	 * Tests the imported names
	 */
	@Test
	public void names() {
		Locator si = db_map.createLocator("http://test.de/hallo_welt");
		
		Topic dbTopic = db_map.getTopicBySubjectIdentifier(si);
		Topic memTopic = memory_map.getTopicBySubjectIdentifier(si);
		
		assertEquals(3, memTopic.getNames().size());
		assertEquals(3, dbTopic.getNames().size());
		
		checkNames(memTopic, "Memory Topic Map: ");
		checkNames(dbTopic, "Imported Topic Map: ");
	}
	
	/**
	 * Tests imported occurrences
	 */
	@Test
	public void occurrences() {
		Locator si = db_map.createLocator("http://test.de/hallo_welt");
		
		Topic dbTopic = db_map.getTopicBySubjectIdentifier(si);
		Topic memTopic = memory_map.getTopicBySubjectIdentifier(si);
		
		assertEquals(3, memTopic.getOccurrences().size());
		assertEquals(3, dbTopic.getOccurrences().size());
		
		checkOccurrences(memTopic, "Memory Topic Map: ");
		checkOccurrences(dbTopic, "Imported Topic Map: ");
	}
	
	/**
	 * Tests the import of associations
	 */
	@Test
	public void associations() {
		checkAssociations(memory_map, "Memory Topic Map: ");
		checkAssociations(db_map, "Imported Topic Map: ");
		
	}

	/**
	 * Compares the two exported files byte by byte
	 * @throws IOException if loading or reading fails
	 */
	@Test
	public void test() throws Exception {
		

		exportTM(this.db_map, "imported.cxtm", databaseBaseLocator);
		exportTM(this.memory_map, "orig.cxtm", databaseBaseLocator);
		
		String property = "java.io.tmpdir";
		String tempDir = System.getProperty(property);
		property = "file.separator";
		String sep = System.getProperty(property);
		
		File orig = new File(tempDir+sep+"orig.cxtm");
		File imported = new File(tempDir+sep+"imported.cxtm");
		
		FileInputStream origFi = new FileInputStream(orig);
		FileInputStream importedFi = new FileInputStream(imported);
		
		int c1, c2;
		
		int counter = 0;
		
		while ( (c1 = origFi.read()) != -1) {
			c2 = importedFi.read();
			counter++;
			if (c1!=c2)
				fail("Found unequal byte at : "+counter);
		}
		
	}

	private void exportTM(TopicMap tm, String filename, String databaseBaseLocator) throws FileNotFoundException, IOException, Exception {
		String property = "java.io.tmpdir";
		String tempDir = System.getProperty(property);
		
		property = "file.separator";
		String sep = System.getProperty(property);
		
		FileOutputStream fos = new FileOutputStream(new File(tempDir+sep+filename));
		
		CXTMTopicMapWriter writer = new CXTMTopicMapWriter(fos, databaseBaseLocator);
		writer.write(tm);
		fos.close();
	}

	private void checkAssociations(TopicMap topicMap, String topicSource) {
		Topic assocType1 = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator("http://test.de/maiana/assoc"));
		
		assertEquals(2, topicMap.getAssociations().size());
		
		for (Association a : topicMap.getAssociations()) {
			if (a.getType().equals(assocType1)) {
				assertEquals(topicSource, 1, a.getRoles().size());
				
				Role r = a.getRoles().iterator().next();
				assertEquals(topicSource, "http://test.de/maiana/role", r.getType().getSubjectIdentifiers().iterator().next().toExternalForm());
				assertEquals(topicSource, "http://test.de/p/1", r.getPlayer().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				assertEquals(topicSource, 2, a.getScope().size());
				
				assertNotNull(topicSource, a.getReifier());
				assertNotNull(topicSource, a.getReifier());
				assertEquals(topicSource, 1, a.getReifier().getSubjectIdentifiers().size());
				assertEquals(topicSource, "http://tmp.de/reifier3", a.getReifier().getSubjectIdentifiers().iterator().next().toExternalForm());
			}
		}
	}
	
	
	
	private void checkOccurrences(Topic topic, String topicSource) {
		for (Occurrence o : topic.getOccurrences()) {
			if ("Erste".equals(o.getValue())) {
				assertEquals("http://tmp.de/occ1", o.getType().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				assertEquals(topicSource, "http://www.w3.org/2001/XMLSchema#string", o.getDatatype().toExternalForm());
				
				continue;
			} else if ("123".equals(o.getValue())) {
				assertEquals("http://tmp.de/occ2", o.getType().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				assertEquals(topicSource, "http://www.w3.org/2001/XMLSchema#integer", o.getDatatype().toExternalForm());
				
				assertNotNull(topicSource, o.getReifier());
				assertEquals(topicSource, 1, o.getReifier().getSubjectIdentifiers().size());
				assertEquals(topicSource, "http://tmp.de/reifier2", o.getReifier().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				continue;
			} else if ("Dritte".equals(o.getValue())) {
				assertEquals("http://tmp.de/occ3", o.getType().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				assertEquals(topicSource, "http://www.w3.org/2001/XMLSchema#string", o.getDatatype().toExternalForm());
				
				assertEquals(topicSource, 2, o.getScope().size());
				
				continue;
			} else {
				fail(topicSource+": Unknown occurrence found");
			}
		}
	}

	private void checkNames(Topic topic, String topicSource) {
		for (Name name : topic.getNames()) {
			if ("Hallo".equals(name.getValue())) {
				Set<Locator> nameTypeSI = name.getType().getSubjectIdentifiers();
				assertEquals(topicSource, 1, nameTypeSI.size());
				
				assertEquals(topicSource, "http://psi.topicmaps.org/iso13250/model/topic-name", nameTypeSI.iterator().next().toExternalForm());
				
				assertEquals(topicSource, 0, name.getScope().size());
				
				// check variant
				Set<Variant> variants = name.getVariants();
				assertEquals(topicSource, 1, variants.size());
				Variant v = variants.iterator().next();

				assertEquals(topicSource, "bullshit", v.getValue());
				assertEquals(topicSource, "http://www.w3.org/2001/XMLSchema#string", v.getDatatype().toExternalForm());
				
				assertEquals(topicSource, 1, v.getScope().size());
				assertEquals(topicSource, 1, v.getScope().iterator().next().getSubjectIdentifiers().size());
				assertEquals(topicSource, "http://tmp.de/variante", v.getScope().iterator().next().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				assertNotNull(topicSource, v.getReifier());
				assertEquals(topicSource, 1, v.getReifier().getSubjectIdentifiers().size());
				assertEquals(topicSource, "http://tmp.de/reifier", v.getReifier().getSubjectIdentifiers().iterator().next().toExternalForm());
				
				
				continue;
			} else if ("Welt".equals(name.getValue())) {
				Set<Locator> nameTypeSI = name.getType().getSubjectIdentifiers();
				assertEquals(topicSource, 1, nameTypeSI.size());
				
				assertEquals(topicSource, "http://test.de/name", nameTypeSI.iterator().next().toExternalForm());
				
				assertEquals(topicSource, 0, name.getVariants().size());
				assertEquals(topicSource, 2, name.getScope().size());
				
				
				
				continue;
			} else if ("No type name".equals(name.getValue())) {
				Set<Locator> nameTypeSI = name.getType().getSubjectIdentifiers();
				assertEquals(topicSource, 1, nameTypeSI.size());
				
				assertEquals(topicSource, "http://psi.topicmaps.org/iso13250/model/topic-name", nameTypeSI.iterator().next().toExternalForm());
				assertEquals(topicSource, 0, name.getVariants().size());
				assertEquals(topicSource, 0, name.getScope().size());
				
				continue;
			}
			fail(topicSource+": Unknown name found");
		}
	}
	
}
