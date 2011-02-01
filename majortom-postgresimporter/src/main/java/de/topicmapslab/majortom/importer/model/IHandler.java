/**
 * 
 */
package de.topicmapslab.majortom.importer.model;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.importer.helper.Association;
import de.topicmapslab.majortom.importer.helper.Name;
import de.topicmapslab.majortom.importer.helper.Occurrence;

/**
 * @author Hannes Niederhausen
 * 
 */
public interface IHandler {

	/**
	 * Commits the last statements
	 * 
	 * @throws MIOException
	 */
	public void commit() throws MIOException;

	/**
	 * Handler for start event prepareing the connection
	 * 
	 * @throws MIOException
	 */
	public void start() throws MIOException;

	/**
	 * Closes the connection
	 * 
	 * @throws MIOException
	 */
	public void end() throws MIOException;

	/**
	 * Returns the id of the topic map for the given locator. If it does not exist it will be created.
	 * 
	 * @param locator
	 *            locator of the tm
	 * @return the id of the tm
	 * @throws MIOException
	 */
	public long getTopicMapId(String locator) throws MIOException ;

	/**
	 * Adds the given association to the database.
	 * 
	 * @param assoc
	 * @throws MIOException
	 */
	public void addAssociation(Association assoc) throws MIOException;

	/**
	 * Adds a name to the topic map
	 * 
	 * @param name
	 * @throws MIOException
	 */
	public void addName(Name name) throws MIOException;

	/**
	 * Adds an occurrence
	 * 
	 * @param occurrence
	 * @throws MIOException
	 */
	public void addOccurrence(Occurrence occurrence) throws MIOException;

	/**
	 * Returns the topic id for the given identifier. It it isn't in the db, it will be created.
	 * 
	 * @param ref
	 *            the identifier of the topic
	 * @return the id of the topic
	 * @throws MIOException
	 */
	public long getTopic(IRef ref) throws MIOException;

	/**
	 * adds an identifier
	 * 
	 * @param topicId
	 *            id of the topic
	 * @param ref
	 *            the uri
	 * @param type
	 *            the type of identifier
	 * @throws MIOException
	 */
	public void addIdentifier(long topicId, String ref, int type) throws MIOException;

	/**
	 * Adds a type to the topic with the given id
	 * 
	 * @param currTopicId
	 *            the current topic id
	 * @param arg0
	 *            the reference of the typing topic
	 * @throws MIOException
	 * @throws
	 */
	public void addType(long currTopicId, IRef arg0) throws MIOException;
}
