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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class RDBMSConnectionProvider implements IConnectionProvider {

	/**
	 * internal reference of the topic map store
	 */
	private JdbcTopicMapStore store;

	/**
	 * the database user
	 */
	private String user;
	/**
	 * the database password
	 */
	private String password;
	/**
	 * the database URL
	 */
	private String url;

	/**
	 * the database name
	 */
	private String database;
	/**
	 * the host
	 */
	private String host;

	/**
	 * the number of connections for connection pool
	 */
	private long connectionCount;

	/**
	 * the global session of the connection provider
	 */
	private ISession globalSession;

	/**
	 * constructor
	 */
	public RDBMSConnectionProvider() {
	}

	/**
	 * Constructor
	 * 
	 * @param host
	 *            the host
	 * @param database
	 *            database
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 */
	public RDBMSConnectionProvider(String host, String database, String user, String password) {
		this.user = user;
		this.password = password;
		this.url = "jdbc:" + getRdbmsName() + "://" + host.toString() + "/" + database.toString();
		this.database = database;
		this.host = host;
		this.connectionCount = 10;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public RDBMSSession openSession() {
		return new RDBMSSession(this, getUrl(), getUser(), getPassword());
	}

	/**
	 * Returns the URL to the database
	 * 
	 * @return the URl
	 */
	protected String getUrl() {
		return url;
	}

	/**
	 * Returns the user database property
	 * 
	 * @return the user
	 */
	protected String getUser() {
		return user;
	}

	/**
	 * Returns the password database property
	 * 
	 * @return the passwords
	 */
	protected String getPassword() {
		return password;
	}

	/**
	 * Returns the database name
	 * 
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Returns the database host server
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the connectionCount
	 */
	public long getConnectionCount() {
		return connectionCount;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void setTopicMapStore(JdbcTopicMapStore store) {
		this.store = store;

		/*
		 * load connection properties from topic map system
		 */
		Object host = store.getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.DATABASE_HOST);
		Object database = store.getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.DATABASE_NAME);
		Object user = store.getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.DATABASE_USER);
		Object password = store.getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD);
		Object count = store.getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.POOL_CONNECTION_COUNT);
		if (database == null || host == null || user == null) {
			throw new TopicMapStoreException("Missing connection properties!");
		}
		/*
		 * store connection properties
		 */
		this.database = database.toString();
		this.host = host.toString();
		this.url = "jdbc:" + getRdbmsName() + "://" + host.toString() + "/" + database.toString();
		this.user = user.toString();
		this.password = password == null ? "" : password.toString();
		/*
		 * set maximum number of connections
		 */
		if (count != null) {
			try {
				this.connectionCount = Long.parseLong(count.toString());
			} catch (NumberFormatException e) {
				this.connectionCount = 10;
			}
		}
		globalSession = openSession();
		try {
			switch (getDatabaseState()) {
				case STATE_DATABASE_IS_EMPTY: {
					createSchema();
				}
					break;
				case STATE_DATABASE_IS_INVALID: {
					throw new TopicMapStoreException("Database schema not valid for majortom jdbc store");
				}
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot establish global session!", e);
		}
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
	public void close() throws SQLException {
		getGlobalSession().close();
	}

	/**
	 * Returns the internal session of the connection provider
	 * 
	 * @return the internal session of the connection provider
	 */
	protected ISession getGlobalSession() {
		return globalSession;
	}

	/**
	 * Returning the name of the used RDBMS
	 * 
	 * @return the name of the RDBMS
	 */
	protected abstract String getRdbmsName();

	/**
	 * Returning the name of the used driver class
	 * 
	 * @return the name of the used driver class
	 */
	protected abstract String getDriverClassName();

	/**
	 * {@inheritDoc}
	 */
	public DatabaseMetaData getDatabaseMetaData() throws TopicMapStoreException {
		try {
			return getGlobalSession().getConnection().getMetaData();
		} catch (SQLException e) {
			throw new TopicMapStoreException("Meta data cannot be resolved!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void createSchema() throws SQLException {
		String[] allQueries = getSchemaQueries();
		Statement stmt = getGlobalSession().getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		for (String query : allQueries) {
			String q = query.trim();
			if (!q.equals(""))
				stmt.executeUpdate(query);
		}
		stmt.close();
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
	 * Returns the queries needed to create the database schema (because mysql does not support multi-statements)
	 * 
	 * @return an array of queries
	 */

	protected String[] getSchemaQueries() {
		return getSchemaQuery().split(";;");
	}

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
