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
package de.topicmapslab.majortom.database.jdbc.rdbms;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public class RDBMSConnectionProvider implements IConnectionProvider {

	/**
	 * the JDBC connection to modify database
	 */
	private Connection writerConnection;
	/**
	 * the JDBC connection to read from database
	 */
	private Connection readerConnection;
	/**
	 * the meta data
	 */
	private DatabaseMetaData metaData;
	/**
	 * the internal query processor
	 */
	private RDBMSQueryProcessor processor;

	/**
	 * internal reference of the topic map store
	 */
	private JdbcTopicMapStore store;

	/**
	 * constructor
	 */
	public RDBMSConnectionProvider() {
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public RDBMSQueryProcessor getProcessor() throws TopicMapStoreException {
		if (processor == null) {
			throw new TopicMapStoreException("Connection is not established!");
		}
		return processor;
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
	public void closeConnections() throws SQLException {
		if (((readerConnection != null && !readerConnection.isClosed()))
				|| (writerConnection != null && !writerConnection.isClosed())) {
			processor.close();
		}
		if (readerConnection != null && !readerConnection.isClosed()) {
			readerConnection.close();
			readerConnection = null;
		}
		if (writerConnection != null && !writerConnection.isClosed()) {
			writerConnection.close();
			writerConnection = null;
		}
	}

	/**
	 * Returning the name of the used RDBMS
	 * 
	 * @return the name of the RDBMS
	 */
	protected String getRdbmsName() {
		return "mysql";
	}
	
	/**
	 * Returning the name of the used driver class
	 * 
	 * @return the name of the used driver class
	 */
	protected String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	public void openConnections(String host, String database, String user,
			String password) throws SQLException, TopicMapStoreException {
		try {
			Class.forName(getDriverClassName());
		} catch (ClassNotFoundException e) {
			throw new TopicMapStoreException("Cannot find driver class for "
					+ getRdbmsName() + "!", e);
		}		
		if (writerConnection == null || writerConnection.isClosed()) {
			writerConnection = DriverManager.getConnection("jdbc:postgresql://"
					+ host + "/" + database, user, password);
		}
		if (readerConnection == null || readerConnection.isClosed()) {
			readerConnection = DriverManager.getConnection("jdbc:postgresql://"
					+ host + "/" + database, user, password);
		}
		if (metaData == null) {
			metaData = readerConnection.getMetaData();
		}
		if (processor == null) {
			processor = createProcessor(this, readerConnection, writerConnection);
		}
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
			throw new TopicMapStoreException(
					"Invalid database schema or unknown database state '"
							+ state + "!");
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
	protected RDBMSQueryProcessor createProcessor(
			RDBMSConnectionProvider provider, Connection readerConnection,
			Connection writerConnetion) {
		return new RDBMSQueryProcessor(provider, readerConnection, writerConnetion);
	}

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getDatabaseMetaData() throws TopicMapStoreException {
		if (readerConnection == null) {
			throw new TopicMapStoreException("Connection is not established!");
		}
		return metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	public void createSchema() throws SQLException {
		Statement stmt = writerConnection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		stmt.executeUpdate(getSchemaQuery());
	}

	/**
	 * Returns the SQL query to create the database schema.
	 * 
	 * @return the query
	 */
	protected String getSchemaQuery() {
		InputStream is = getClass().getResourceAsStream("script.sql");
		if (is == null) {
			throw new TopicMapStoreException("Cannot load database schema!");
		}
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner(is);
		while (scanner.hasNextLine()) {
			buffer.append(scanner.nextLine() + "\r\n");
		}
		scanner.close();
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDatabaseState() throws SQLException {
		int state = STATE_DATABASE_IS_EMPTY;
		/*
		 * extract all tables
		 */
		ResultSet rs = getDatabaseMetaData().getTables(null, null, null,
				new String[] { "TABLE" });
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
