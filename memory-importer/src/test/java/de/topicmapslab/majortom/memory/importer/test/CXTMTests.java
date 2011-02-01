package de.topicmapslab.majortom.memory.importer.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.internal.runners.statements.Fail;
import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystemFactory;

import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.io.CXTMTopicMapWriter;
import de.topicmapslab.majortom.memory.importer.Importer;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;

public class CXTMTests {

	private final static String pathToCXTMTestSuite = "/home/ch/workspace/cxtm-tests-0.4/";
	
	public static void main(String[] args) {
		
		try{

		//baseTest("jtm","jtm");
			
		baseTest("xtm21","xtm");
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void baseTest(final String pathPart, final String extension) throws Exception{
		
		String inPath = pathToCXTMTestSuite + pathPart + "/in/";
		String baselinePath = pathToCXTMTestSuite + pathPart + "/baseline/";
		
		File path = new File(inPath);
		
		if(!path.exists()){
			System.out.println("Path dont exist.");
			return;
		}
		
		
		TopicMapSystemFactory factory = new TopicMapSystemFactoryImpl();
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, InMemoryTopicMapStore.class.getCanonicalName());
		ITopicMapSystem system = (ITopicMapSystem)factory.newTopicMapSystem();
		
		for(String file:path.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				
				if(name.endsWith("." + extension))
					return true;
				
				return false;
			}
		})){

			File in = new File(path.getCanonicalPath() + "/" + file);
			
			if(!in.exists()){
				System.out.println("File " + in.getCanonicalPath() + " no exist.");
				return;
			}
			
			File reference = new File(baselinePath + in.getName() + ".cxtm");
			
			if(!reference.exists()){
				System.out.println("No reference for " + in.getName() + " found!");
				fail();
			}
			
			String baseLocator = "file:" + in.getCanonicalPath();
			
			ITopicMap map = (ITopicMap)system.createTopicMap(baseLocator);
			// read file
			//System.out.println("Read " + in.getName() + "...");
			Importer.importFile((InMemoryTopicMapStore)map.getStore(), in, baseLocator);	
			
			// write to cxtm
			
			//System.out.println("Write cxtm file...");
			
			File out = new File("src/test/resources/out.cxtm");
			if(out.exists())
				out.delete();
			
			FileOutputStream outStream = new FileOutputStream(out);
			
			
			CXTMTopicMapWriter writer = new CXTMTopicMapWriter(outStream, baseLocator);
			writer.write(map);
			
			
			map.clear();
			
			String result = compareFiles(reference, out);
			
			if(result != null){
				System.out.println("Error in " + in.getName() + ":");
				System.out.println(result);
			}
			
		}
		
	}
	
	
	private static String compareFiles(File reference, File exported) throws IOException{
		
		FileInputStream referenceFi = new FileInputStream(reference);
		FileInputStream exportedFi = new FileInputStream(exported);
		
		int c1, c2;
		
		int counter = 0;
		
		while ( (c1 = referenceFi.read()) != -1) {
			c2 = exportedFi.read();
			counter++;
			if (c1!=c2)
				return ("Found unequal byte at : "+counter);
		}
		
		return null;
	}
	
	
	
}
