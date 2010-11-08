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
package de.topicmapslab.majortom.database.jdbc.model;

import java.sql.Connection;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public interface ISession {

	/**
	 * Returns the internal query processor instance, which should be used to execute queries.
	 * 
	 * @return the query processor
	 * @throws TopicMapStoreException
	 *             thrown if the connection is not established
	 */
	public <T extends IQueryProcessor> T getProcessor() throws TopicMapStoreException;

	/**
	 * Closing the current session instance
	 */
	public void close() throws SQLException;

	/**
	 * Commit all changes of this session
	 * 
	 * @throws SQLException
	 *             thrown if commit failed
	 */
	public void commit() throws SQLException;

	/**
	 * Access to the internal connection
	 * 
	 * @return the connection
	 * @throws SQLException
	 *             thrown if connection cannot be established
	 */
	Connection getConnection() throws SQLException;

	/**
	 * Returns the topic map store instance
	 * 
	 * @return the topic map store
	 */
	JdbcTopicMapStore getTopicMapStore();

	/**
	 * Returns the parent connection provider
	 * 
	 * @return the connectionProvider the connection provider
	 */
	IConnectionProvider getConnectionProvider();

	/**
	 * Checks if the session automatically commits all changes
	 * 
	 * @return <code>true</code> if the session commits automatically, <code>false</code> otherwise.
	 */
	public boolean isAutoCommit();

}
