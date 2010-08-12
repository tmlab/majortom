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

	public final static String DATABASE_NOT_EXISTS = "3D000";
	
	/**
	 * Open a new connection to the given database. If there is already an
	 * existing connection to any database an exception will be thrown.
	 * 
	 * @param host
	 *            the host
	 * @param database
	 *            the database name
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @throws SQLException
	 *             thrown if the connection cannot be established
	 * @throws TopicMapStoreException
	 *             thrown if the driver class is unknown
	 */
	public void openConnection(String host, String database, String user, String password) throws SQLException, TopicMapStoreException;

	/**
	 * Close an existing connection. If there is not an existing exception,
	 * nothing happens.
	 * 
	 * @throws SQLException
	 *             thrown if connection cannot be closed.
	 */
	public void closeConnection() throws SQLException;

	/**
	 * Returns the internal query processor instance, which should be used to
	 * execute queries.
	 * 
	 * @return the query processor
	 * @throws TopicMapStoreException
	 *             thrown if the connection is not established
	 */
	public IQueryProcessor getProcessor() throws TopicMapStoreException;

	/**
	 * Return the meta data of the existing connection. If the connection is not
	 * open, an exception will be thrown.
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
	 * Method set the internal reference of the JDBC topic map store to the
	 * given reference.
	 * 
	 * @param store
	 *            the topic map store
	 */
	public void setTopicMapStore(JdbcTopicMapStore store);

}
