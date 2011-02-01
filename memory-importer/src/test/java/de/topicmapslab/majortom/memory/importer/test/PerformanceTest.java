package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

public class PerformanceTest {

	public static void main(String[] args) throws Exception {
		
		File file = new File("src/test/resources/fb-10400.xtm");
		
		
		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		
		ITopicMap map = (ITopicMap)system.createTopicMap("http://test");
		
		
		long s = System.currentTimeMillis();
		XTMTopicMapReader reader = new XTMTopicMapReader(map, file);
		reader.read();
		
		System.out.println("Using the normal reader took " + ((System.currentTimeMillis() - s)/1000) + " secound.");
		
		
		map.clear();
		s = System.currentTimeMillis();
		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file, "http://test");	
		System.out.println("Using the importer took " + ((System.currentTimeMillis() - s)/1000) + " secound.");
	}
	
}
