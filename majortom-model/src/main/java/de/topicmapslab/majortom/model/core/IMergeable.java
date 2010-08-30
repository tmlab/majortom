package de.topicmapslab.majortom.model.core;

/**
 * Interface definition representing construct which can be merged with a
 * construct of the same type.
 * 
 * @author Sven Krosse
 * 
 */
public interface IMergeable {

	/**
	 * Merges the given construct into the current instance.
	 * 
	 * @param construct
	 *            the construct to merge in
	 */
	public void merge(IConstruct construct);

}
