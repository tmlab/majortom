package de.topicmapslab.majortom.importer.helper;

import java.util.ArrayList;
import java.util.List;



/**
 * Helper class which encapsulates the data of one name
 * 
 * @author Hannes Niederhausen
 *
 */
public class Name extends Characteristic {
	
	private List<Variants> variants = new ArrayList<Variants>();  
	
	/**
	 * @return the list of variants
	 */
	public List<Variants> getVariants() {
		return variants;
	}
	
	
}
