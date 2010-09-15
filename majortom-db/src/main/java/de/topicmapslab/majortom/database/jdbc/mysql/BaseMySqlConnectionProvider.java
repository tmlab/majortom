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
package de.topicmapslab.majortom.database.jdbc.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for MySql.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseMySqlConnectionProvider implements IConnectionProvider {

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
	private MySqlQueryProcessor processor;

	/**
	 * internal reference of the topic map store
	 */
	private JdbcTopicMapStore store;

	/**
	 * constructor
	 */
	protected BaseMySqlConnectionProvider() {
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void setTopicMapStore(JdbcTopicMapStore store) {
		this.store = store;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public JdbcTopicMapStore getTopicMapStore() {
		return store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			processor.close();
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
		return  processor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void openConnection(String host, String database, String user, String password) throws SQLException, TopicMapStoreException {
		if (store == null) {
			throw new TopicMapStoreException("Topic map store not set!");
		}
		if (connection != null) {
			throw new TopicMapStoreException("Connection already established!");
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new TopicMapStoreException("Cannot find driver class for PostGreSQL!", e);
		}
		connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
		metaData = connection.getMetaData();
		processor = createProcessor(this, connection);
		int state = getDatabaseState();
		switch (state) {
		case STATE_DATABASE_IS_EMPTY: {
			createSchema();
		}
			break;
		case STATE_DATABASE_IS_VALID: {
			// NOTHING TO DO
		}
			break;
		case STATE_DATABASE_IS_INVALID:
		default:
			throw new TopicMapStoreException("Invalid database schema or unknown database state '" + state + "!");
		}
	}

	/**
	 * Abstract method to create a new query processor.
	 * 
	 * @param provider
	 *            the calling provider instance
	 * @param connection
	 *            the connection
	 * @return the created query processor instance
	 */
	protected abstract MySqlQueryProcessor createProcessor(IConnectionProvider provider, Connection connection);

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getDatabaseMetaData() throws TopicMapStoreException {
		if (connection == null) {
			throw new TopicMapStoreException("Connection is not established!");
		}
		return metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	public void createSchema() throws SQLException {
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt.executeUpdate(getSchemaQuery());
	}

	/**
	 * Returns the SQL query to create the database schema.
	 * 
	 * @return the query
	 */
	protected abstract String getSchemaQuery();

	/**
	 * {@inheritDoc}
	 */
	public int getDatabaseState() throws SQLException {
		int state = STATE_DATABASE_IS_EMPTY;
		/*
		 * extract all tables
		 */
		ResultSet rs = getDatabaseMetaData().getTables(null, null, null, new String[] { "TABLE" });
		rs.beforeFirst();
		/*
		 * check if each table of the database is valid for the given schema
		 */
		if (rs.next()) {
			state = STATE_DATABASE_IS_VALID;
		}
		rs.close();
		return state;
	}
}
