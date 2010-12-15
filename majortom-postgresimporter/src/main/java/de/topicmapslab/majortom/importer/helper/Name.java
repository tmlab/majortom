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
	
	private List<Variant> variants = new ArrayList<Variant>();  
	
	/**
	 * @return the list of variants
	 */
	public List<Variant> getVariants() {
		return variants;
	}
	
	
}
