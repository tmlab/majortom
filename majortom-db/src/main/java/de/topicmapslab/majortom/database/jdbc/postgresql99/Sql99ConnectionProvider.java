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
package de.topicmapslab.majortom.database.jdbc.postgresql99;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public class Sql99ConnectionProvider implements IConnectionProvider {

	/**
	 * the JDBC connection
	 */
	private Connection connection;
	/**
	 * the meta data
	 */
	private DatabaseMetaData metaData;
	/**
	 * the internal query processor
	 */
	private IQueryProcessor processor;

	/**
	 * constructor
	 */
	public Sql99ConnectionProvider() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IQueryProcessor getProcessor() throws TopicMapStoreException {
		if (connection == null) {
			throw new TopicMapStoreException("Connection is not established!");
		}
		return processor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void openConnection(String host, String database, String user, String password) throws SQLException, TopicMapStoreException {
		if (connection != null) {
			throw new TopicMapStoreException("Connection already established!");
		}
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new TopicMapStoreException("Cannot find driver class for PostGreSQL!", e);
		}
		connection = DriverManager.getConnection("jdbc:postgresql://" + host + "/"+ database, user, password);
		metaData = connection.getMetaData();
		processor = new Sql99QueryProcessor(connection);

	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getDatabaseMetaData() throws TopicMapStoreException {
		if (connection == null) {
			throw new TopicMapStoreException("Connection is not established!");
		}
		return metaData;
	}

}
