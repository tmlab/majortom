/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.topicmapslab.majortom.database.jdbc.model;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Interface definition of a connection provider.
 * 
 * @author Sven Krosse
 * 
 */
public interface IConnectionProvider {

	/**
	 * state code if the database is empty
	 */
	public final static int STATE_DATABASE_IS_EMPTY = 0;

	/**
	 * state code if the database if invalid
	 */
	public final static int STATE_DATABASE_IS_INVALID = 1;

	/**
	 * state code if the database schema is valid
	 */
	public final static int STATE_DATABASE_IS_VALID = 2;

	public final static String DATABASE_NOT_EXISTS = "3D000";

	/**
	 * Creating a new session for accessing the database
	 * 
	 * @return the new session
	 */
	public <T extends ISession> T openSession();

	/**
	 * Closing the connection provider
	 * @throws SQLException thrown if operation fails
	 */
	public void close() throws SQLException;
	
	/**
	 * Return the meta data of the existing connection. If the connection is not open, an exception will be thrown.
	 * 
	 * @return the meta data
	 * @throws TopicMapStoreException
	 *             thrown if the connection is not established
	 */
	public DatabaseMetaData getDatabaseMetaData() throws TopicMapStoreException;

	/**
	 * Returns the internal reference of the encapsulated topic map store
	 * 
	 * @returns the store
	 */
	public JdbcTopicMapStore getTopicMapStore();

	/**
	 * Method set the internal reference of the JDBC topic map store to the given reference.
	 * 
	 * @param store
	 *            the topic map store
	 */
	public void setTopicMapStore(JdbcTopicMapStore store);

	/**
	 * Method called by the connection provider to initialize the database schema.
	 */
	public void createSchema() throws SQLException;

	/**
	 * Method checks if the schema of the current database connection is valid..
	 * 
	 * @return <p>
	 *         <code>0</code> if the database is empty. <br />
	 *         <code>1</code> if the database schema is invalid. <br />
	 *         <code>2</code> if the database schema is valid.
	 *         </p>
	 */
	public int getDatabaseState() throws SQLException;
}
