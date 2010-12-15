/**
 * 
 */
package de.topicmapslab.majortom.importer.helper;

/**
 * helper class for occurrences
 * 
 * @author Hannes Niederhausen
 *
 */
public class Occurrence extends Characteristic {

	private String datatype;
	
	/**
	 * @param datatype
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	
	/**
	 * @return
	 */
	public String getDatatype() {
		return datatype;
	}
}
