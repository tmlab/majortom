package de.topicmapslab.majortom.model.index;

import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.exception.IndexException;

/**
 * Interface definition of an index of the topic maps engine.
 * 
 * @author Sven Krosse
 * 
 */
public interface IIndex extends Index {

	/**
	 * Checks if the current index is current synchronous with the data stored
	 * in the data store.
	 * 
	 * @return <code>true</code> if the index is synchronous, <code>false</code>
	 *         otherwise.
	 */
	public boolean isSynchronous();

	/**
	 * Method synchronize the current index instance with the underlying data
	 * store.
	 * 
	 * @throws IndexException
	 *             thrown if the synchronization of the index fails
	 */
	public void synchronize() throws IndexException;

}
