package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.junit.Ignore;
import org.tmapi.core.Association;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapix.io.CTMTopicMapWriter;
import org.tmapix.io.XTM20TopicMapWriter;
import org.tmapix.io.XTMTopicMapReader;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.io.CXTMTopicMapWriter;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

@Ignore
public class Test {

	public static void main(String[] args) throws Exception {
		
//		File file = new File("src/test/resources/manual.ctm");
//		
//		
//		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
//		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
//		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
//		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
//		
//		ITopicMap map = (ITopicMap)system.createTopicMap("http://test");
//
//		map.clear();
//		
//		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file, "http://test");	
//		
//		CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(new File("src/test/resources/testResult.ctm")), "http://test");
//		writer.write(map);
	
	
		File file1 = new File("src/test/resources/fb-10400.xtm");
		File file2 = new File("src/test/resources/toytm_or.xtm");
		
		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		
		ITopicMap map = (ITopicMap)system.createTopicMap("http://test");

		long s = System.currentTimeMillis();
		
		System.out.println("Read " + file2.getName());
		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file2, "http://test");
		System.out.println("Read " + file2.getName());
		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file2, "http://test");
		
		System.out.println("Import two file took " + ((System.currentTimeMillis() - s)) + " milisecound.");

		
		ILiteralIndex index = map.getIndex(ILiteralIndex.class);
		index.open();
		
		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());
		
		CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(new File("src/test/resources/toytm1.ctm")), "http://toytm/");
		writer.write(map);
		

		
		s = System.currentTimeMillis();
		map.removeDuplicates();
		
		System.out.println("Remove duplicates took " + ((System.currentTimeMillis() - s)) + " milisecound.");
		
		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());

		writer = new CTMTopicMapWriter(new FileOutputStream(new File("src/test/resources/toytm2.ctm")), "http://toytm/");
		writer.write(map);
	}
	
}
