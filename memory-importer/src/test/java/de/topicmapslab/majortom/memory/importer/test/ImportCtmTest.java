package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapWriter;

import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;


public class ImportCtmTest {

	public static void main(String[] args) throws TMAPIException, MIOException, IOException {
		
		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		
		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		ITopicMap map = (ITopicMap)system.createTopicMap("http://test");
		
		File file = new File("src/test/resources/manual.ctm");
		
		Importer.importFile((InMemoryTopicMapStore)map.getStore(), file, "http://test");
		
		System.out.println("Topic maps has " + map.getTopics().size() + " topics.");
		
		File o = new File("src/test/resources/testResult.ctm");
		if(o.exists())
			o.delete();
		
		FileOutputStream out = new FileOutputStream(o);
		
		CTMTopicMapWriter writer = new CTMTopicMapWriter(out, "http://test");
		writer.write(map);
		
	}
	
}
