package de.topicmapslab.majortom.model.store;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.exception.ConcurrentThreadsException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.transaction.ITransaction;

/**
 * Interface definition of a topic map data store
 * 
 * @author Sven Krosse
 * 
 */
public interface ITopicMapStore {

	/**
	 * Initializing method of the topic map store.
	 * 
	 * @param topicMapBaseLocator
	 *            TODO
	 * 
	 * @throws TopicMapStoreException
	 *             thrown if initialization failed
	 */
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException;

	/**
	 * Open the connection to the topic map store.
	 * 
	 * @throws TopicMapStoreException
	 *             thrown if the connection cannot be established
	 */
	public void connect() throws TopicMapStoreException;

	/**
	 * Close the connection to the topic map data store.
	 * 
	 * @throws TopicMapStoreException
	 *             thrown if the connection is currently used.
	 */
	public void close() throws TopicMapStoreException;

	/**
	 * Checks if the connection to the data store is already established.
	 * 
	 * @return <code>true</code> if the connection is established,
	 *         <code>false</code> otherwise.
	 */
	public boolean isConnected();

	/**
	 * Indicates if the current topic map store instance only supports read-only
	 * access.
	 * 
	 * @return <code>true</code> if only read-operations are supported,
	 *         <code>false</code> if there is a read-write-access.
	 */
	public boolean isReadOnly();

	/**
	 * Indicates if the current topic map store instance supports the history
	 * functionality.
	 * 
	 * @return <code>true</code> if the store save all changes of the topic map,
	 *         <code>false</code> otherwise.
	 */
	public boolean supportRevisions();

	/**
	 * Operation method to merge a set of constructs to one new construct
	 * 
	 * @param context
	 *            the construct to merge
	 * @param others
	 *            the other constructs to merge in
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	<T extends Construct> void doMerge(final T context, final T... others) throws TopicMapStoreException;

	/**
	 * Operation method to delete a construct from the store.
	 * 
	 * @param context
	 *            the construct to remove
	 * @param paramType
	 *            the parameter specify the content to remove
	 * @param params
	 *            an array of arguments
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	void doRemove(final IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException;

	/**
	 * Operation method to delete a construct from the store.
	 * 
	 * @param context
	 *            the construct to remove
	 * @param cascade
	 *            flag indicates if all dependent constructs should be removed
	 *            too
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	void doRemove(final IConstruct context, boolean cascade) throws TopicMapStoreException;

	/**
	 * Operation method to read some informations form the store
	 * 
	 * @param context
	 *            the context
	 * @param paramType
	 *            the parameter specify the content to read
	 * @param params
	 *            an array of arguments
	 * @return the result of this operation
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	Object doRead(final IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException;

	/**
	 * Operation method to create a new information item
	 * 
	 * @param context
	 *            the context
	 * @param paramType
	 *            the parameter specify the content to read
	 * @param params
	 *            an array of arguments
	 * @return the create construct
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	Object doCreate(final IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException;

	/**
	 * Operation method to add or set some information items to a construct of
	 * the store
	 * 
	 * @param context
	 *            the context
	 * @param paramType
	 *            the parameter specify the content to modify
	 * @param params
	 *            an array of arguments
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails or is not supported
	 */
	void doModify(final IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException;

	/**
	 * Registers the listener to the topic map.
	 * 
	 * @param listener
	 *            the listener to register
	 */
	public void addTopicMapListener(ITopicMapListener listener);

	/**
	 * Removes the listener to the topic map.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeTopicMapListener(ITopicMapListener listener);

	/**
	 * Returns the internal index instance for the given class attribute.
	 * 
	 * @param <I>
	 *            the index type
	 * @param clazz
	 *            the class of the index
	 * @return the index instance and never null
	 */
	public <I extends Index> I getIndex(Class<I> clazz);

	/**
	 * Set the topic map of the store.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @throws TopicMapStoreException
	 *             thrown if internal topic map already set
	 */
	public void setTopicMap(ITopicMap topicMap) throws TopicMapStoreException;

	/**
	 * Creating a new transaction.
	 * 
	 * @return the created transaction
	 */
	public ITransaction createTransaction();

	/**
	 * Checks if the underlying store support transactions
	 * 
	 * @return transaction support
	 */
	public boolean isTransactable();

	/**
	 * Setting the internal reference of the topic map system.
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 */
	public void setTopicMapSystem(ITopicMapSystem topicMapSystem);

	/**
	 * Return the internal topic map instance of this store.
	 * 
	 * @return the topic map
	 */
	public ITopicMap getTopicMap();

	/**
	 * Method commit all changes of every queue of the topic map store. The
	 * calling thread will be blocked until the changes are commited.
	 * 
	 * @throws ConcurrentThreadsException
	 *             thrown if the method was called by another thread before
	 */
	public void commit() throws ConcurrentThreadsException;
	
	/**
	 * Method removes all duplicated from the topic map
	 */
	public void removedDuplicates();
}
