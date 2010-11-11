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
package de.topicmapslab.majortom.database.jdbc.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public abstract class SessionImpl implements ISession {

	private static final String WORKAROUND = "SELECT id FROM topics OFFSET 0 LIMIT 1";

	private final String user;
	private final String password;
	private final String url;
	private IQueryProcessor processor;
	private Connection connection;
	private final IConnectionProvider connectionProvider;
	private final boolean autoCommit;

	/**
	 * Constructor
	 * 
	 * @param connectionProvider
	 *            the parent connection provider
	 * @param url
	 *            the URL
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 */
	public <T extends IConnectionProvider> SessionImpl(final T connectionProvider, final String url, final String user,
			final String password) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.connectionProvider = connectionProvider;
		this.autoCommit = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends IQueryProcessor> T getProcessor() throws TopicMapStoreException {
		try {
			if (!isAlive()) {
				connection = openConnection();
				processor = createProcessor(connection);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot initialize session for database!", e);
		}
		return (T) processor;
	}

	/**
	 * Internal method to create a processor instance for the given connection
	 * 
	 * @param <T>
	 *            the type of processor
	 * @param connection
	 *            the connection
	 * @return the created processor
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract <T extends IQueryProcessor> T createProcessor(Connection connection)
			throws TopicMapStoreException;

	/**
	 * {@inheritDoc}
	 */
	public void close() throws SQLException {
		if (processor != null) {
			processor.close();
		}
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws SQLException {
		if (!isAutoCommit()) {
			getConnection().commit();
		}
	}

	/**
	 * Internal method to check if the connection is still alive
	 * 
	 * @return <code>true</code> if the connection is alive <code>false</code> otherwise
	 */
	protected boolean isAlive() throws SQLException {
		if (connection == null || connection.isClosed()) {
			return false;
		}
		try {
			connection.createStatement().execute(WORKAROUND);
		} catch (Exception e) {
			close();
			return false;
		}
		return true;
	}

	/**
	 * Internal method to open a connection to the defined database
	 * 
	 * @return the connection
	 * @throws SQLException
	 *             thrown if connection cannot be established
	 */
	protected Connection openConnection() throws SQLException {
		connection = DriverManager.getConnection(url, user, password);
		connection.setAutoCommit(isAutoCommit());
		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	public Connection getConnection() throws SQLException {
		if (!isAlive()) {
			connection = openConnection();
		}
		return connection;
	}

	/**
	 * {@inheritDoc}
	 */
	public JdbcTopicMapStore getTopicMapStore() {
		return getConnectionProvider().getTopicMapStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

}
