package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;
import java.io.FileOutputStream;

import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapWriter;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

public class Test {

public static void main(String[] args) throws Exception {
		
		File file = new File("src/test/resources/manual.ctm");
		
		
		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		
		ITopicMap map = (ITopicMap)system.createTopicMap("http://test");

		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file, "http://test");	
		
		CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(new File("src/test/resources/testResult.ctm")), "http://test");
		writer.write(map);
		
	}
	
}
