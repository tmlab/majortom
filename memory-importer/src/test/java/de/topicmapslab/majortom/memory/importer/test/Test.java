package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;

import org.junit.Ignore;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

@Ignore
public class Test {

	public static void main(String[] args) throws Exception {

		File file1 = new File("src/test/resources/fb-3000.xtm");
		File file2 = new File("src/test/resources/fb-3000.xtm");

		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem) factory.newTopicMapSystem();

		

		ITopicMap map = (ITopicMap) system.createTopicMap("http://test");

		long s = System.currentTimeMillis();

		System.out.println("Read " + file1.getName());
		Importer.importFile((InMemoryTopicMapStore) map.getStore(), file1, "http://test");
		System.out.println("Read " + file2.getName());
		Importer.importFile((InMemoryTopicMapStore) map.getStore(), file2, "http://test");

		System.out.println("Import two file took " + ((System.currentTimeMillis() - s) / 1000) + " secound.");

		ILiteralIndex index = map.getIndex(ILiteralIndex.class);
		index.open();

		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Variants: " + index.getVariants().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());

		s = System.currentTimeMillis();
		map.removeDuplicates();
	
		System.out.println("Remove duplicates took " + ((System.currentTimeMillis() - s) / 1000) + " secound.");

		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Variants: " + index.getVariants().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());

		
	}

}
