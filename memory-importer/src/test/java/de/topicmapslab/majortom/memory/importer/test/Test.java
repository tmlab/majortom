package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.tmapi.core.Construct;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

@Ignore
public class Test {

	public static void main(String[] args) throws Exception {

		// File file = new File("src/test/resources/manual.ctm");
		//
		//
		// TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		// factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		// factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS,
		// InMemoryTopicMapStore.class.getCanonicalName());
		// ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		//
		// ITopicMap map = (ITopicMap)system.createTopicMap("http://test");
		//
		// map.clear();
		//
		// Importer.importFile((InMemoryTopicMapStore)map.getStore(), file, "http://test");
		//
		// CTMTopicMapWriter writer = new CTMTopicMapWriter(new FileOutputStream(new
		// File("src/test/resources/testResult.ctm")), "http://test");
		// writer.write(map);

		File file1 = new File("src/test/resources/toytm.xtm");
		// File file2 = new File("src/test/resources/fb-3000.xtm");

		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem) factory.newTopicMapSystem();

		

		ITopicMap map = (ITopicMap) system.createTopicMap("http://test");

		long s = System.currentTimeMillis();

		System.out.println("Read " + file1.getName());
		Importer.importFile((InMemoryTopicMapStore) map.getStore(), file1, "http://test");

		Map<String, String> topics = HashUtil.getHashMap();
		for (Topic t : map.getTopics()) {
			if (!t.getSubjectIdentifiers().isEmpty()){
				topics.put(t.getId(), "si:" + t.getSubjectIdentifiers().iterator().next().getReference());
			}else if (!t.getSubjectLocators().isEmpty()){
				topics.put(t.getId(), "sl:" + t.getSubjectLocators().iterator().next().getReference());
			}else if (!t.getItemIdentifiers().isEmpty()){
				topics.put(t.getId(), "ii:" + t.getItemIdentifiers().iterator().next().getReference());
			}else{
				topics.put(t.getId(), "id:" +  t.getId());
			}
		}
		
		IIdentityIndex ii = map.getIndex(IIdentityIndex.class);
		ii.open();
		System.out.println(ii.getItemIdentifiers());

		System.out.println("Read " + file1.getName());
		Importer.importFile((InMemoryTopicMapStore) map.getStore(), file1, "http://test");
		System.out.println("Import two file took " + ((System.currentTimeMillis() - s) / 1000) + " secound.");

		ILiteralIndex index = map.getIndex(ILiteralIndex.class);
		index.open();

		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());
		System.out.println("Variants: " + index.getVariants().size());

		s = System.currentTimeMillis();
		map.removeDuplicates();

		Map<String, String> topics_ = HashUtil.getHashMap();
		for (Topic t : map.getTopics()) {
			if (!t.getSubjectIdentifiers().isEmpty()){
				topics_.put(t.getId(), "si:" + t.getSubjectIdentifiers().iterator().next().getReference());
			}else if (!t.getSubjectLocators().isEmpty()){
				topics_.put(t.getId(), "sl:" + t.getSubjectLocators().iterator().next().getReference());
			}else if (!t.getItemIdentifiers().isEmpty()){
				topics_.put(t.getId(), "ii:" + t.getItemIdentifiers().iterator().next().getReference());
			}else{
				topics_.put(t.getId(), "id:" +  t.getId());
			}
		}
	
		System.out.println("Remove duplicates took " + ((System.currentTimeMillis() - s) / 1000) + " secound.");

		System.out.println("Topics: " + map.getTopics().size());
		System.out.println("Associations: " + map.getAssociations().size());
		System.out.println("Names: " + index.getNames().size());
		System.out.println("Occurrences: " + index.getOccurrences().size());
		System.out.println("Variants: " + index.getVariants().size());
		
		System.out.println("Diff");
		
		for ( Entry<String, String> entry : topics.entrySet()){
			if ( !topics_.containsKey(entry.getKey()) && !topics_.containsValue(entry.getValue())){
				Construct c = map.getConstructByItemIdentifier(map.createLocator(entry.getValue().substring(3)));
				if ( c != null ){
					System.out.println("Missing topic with id " + entry.getKey() + " - " + entry.getValue() + " -> merged with " + c.getId());
				}else{
					System.out.println("Missing topic with id " + entry.getKey() + " - " + entry.getValue());
				}
			}
		}

	}

}
