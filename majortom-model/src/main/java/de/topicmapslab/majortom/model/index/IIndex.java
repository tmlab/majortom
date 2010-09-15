/**
 * 
 */
package de.topicmapslab.majortom.model.index;

import org.tmapi.index.Index;

/**
 * Extension for {@link Index}
 * @author Sven Krosse
 *
 */
public interface IIndex extends Index {

	/**
	 * Removed any cached content from internal cache
	 */
	public void clear();
	
}
