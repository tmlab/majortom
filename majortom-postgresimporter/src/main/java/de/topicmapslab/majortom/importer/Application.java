/**
 * 
 */
package de.topicmapslab.majortom.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import argparser.ArgParser;
import argparser.StringHolder;

/**
 * 
 * Class containing the main to use the lib as stand alone application
 * 
 * @author Hannes Niederhausen
 *
 */
public class Application {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) {
		try {
			
			// try to load log4j.properties
			File f = new File("log4j.properties");
			if (f.exists()) {
				Properties props = new Properties();
				props.load(new FileInputStream(f));
				LogManager.resetConfiguration();
			    PropertyConfigurator.configure(props);
			}
			
			
			ArgParser parser = new ArgParser("Use java -jar dbimporter.jar -file <string> [-base-iri <string>] [-properties <string>]");
			
			StringHolder fileName = new StringHolder();
			StringHolder baseIRI = new StringHolder();
			StringHolder propertiesFileName = new StringHolder();
			
			
			parser.addOption("-file %s #name of the file to load", fileName);
			parser.addOption("-base-iri %s #base locator for the topic map", baseIRI);
			parser.addOption("-properties %s #name of the properties file to load", propertiesFileName);

			parser.matchAllArgs(args);

			if (fileName.value==null)
				System.out.println(parser.getHelpMessage());

			f = new File(fileName.value);
			
			if (baseIRI.value==null) {
				baseIRI.value = "file://"+f.getAbsolutePath()+"/";
			}
			
			if (propertiesFileName.value!=null) {
				File propFile = new File(propertiesFileName.value);
				
				Properties properties = new Properties();
				properties.load(new FileInputStream(propFile));
				
				Importer.importFile(f, baseIRI.value, properties);
			}  else {
				Importer.importFile(f, baseIRI.value);
			}
		} catch (Exception e) {
			System.out.println("An error occurred: "+e.getMessage());
			e.printStackTrace();
		}

	}

}
