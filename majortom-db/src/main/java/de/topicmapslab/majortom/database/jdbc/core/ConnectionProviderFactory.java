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
package de.topicmapslab.majortom.database.jdbc.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import de.topicmapslab.majortom.database.jdbc.hsqldb.HSQLDBConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.monetdb.MonetDBConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.mysql.MySqlConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.PostGreSqlConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99ConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Connection factory to create a specific connection provider for a given SQL dialect.
 * 
 * @author Sven Krosse
 * 
 */
public class ConnectionProviderFactory {

	/**
	 * the singleton
	 */
	private static ConnectionProviderFactory factory;

	/**
	 * mapping between SQL dialect and connection provider class
	 */
	private static Map<String, Class<? extends IConnectionProvider>> protocols = HashUtil.getHashMap();
	static {
		protocols.put(SqlDialect.POSTGRESQL.name(), PostGreSqlConnectionProvider.class);
		protocols.put(SqlDialect.POSTGRESQL99.name(), Sql99ConnectionProvider.class);
		protocols.put(SqlDialect.MYSQL.name(), MySqlConnectionProvider.class);
		protocols.put(SqlDialect.RDBMS.name(), RDBMSConnectionProvider.class);
		protocols.put(SqlDialect.MONET_DB.name(), MonetDBConnectionProvider.class);
		protocols.put(SqlDialect.HSQLDB.name(), HSQLDBConnectionProvider.class);
	}

	/**
	 * hidden constructor
	 */
	private ConnectionProviderFactory() {
		// VOID
	}

	/**
	 * Get the internal singleton instance of the factory
	 * 
	 * @return the factory
	 */
	public static final ConnectionProviderFactory getFactory() {
		if (factory == null) {
			factory = new ConnectionProviderFactory();
		}
		return factory;
	}

	/**
	 * Register a new connection provider class for the given SQL dialect
	 * 
	 * @param dialect
	 *            the SQL dialect
	 * @param clazz
	 *            the connection provider class
	 */
	public static void registerConnectionProvider(String dialect, Class<? extends IConnectionProvider> clazz) {
		protocols.put(dialect, clazz);
	}

	/**
	 * Create a new connection provider for the given dialect
	 * 
	 * @param dialect
	 *            the SQL dialect
	 * @return a new connection provider
	 * @throws IllegalArgumentException
	 *             thrown if the given SQL dialect is unknown
	 * @throws TopicMapStoreException
	 *             thrown if the connection provider cannot be initialized
	 */
	public IConnectionProvider newConnectionProvider(final SqlDialect dialect) throws IllegalArgumentException,
			TopicMapStoreException {
		return newConnectionProvider(dialect.name());
	}

	/**
	 * Create a new connection provider for the given dialect
	 * 
	 * @param dialect
	 *            the SQL dialect
	 * @param host
	 *            the database host
	 * @param database
	 *            the database names
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 * @return a new connection provider
	 * @throws IllegalArgumentException
	 *             thrown if the given SQL dialect is unknown
	 * @throws TopicMapStoreException
	 *             thrown if the connection provider cannot be initialized
	 * 
	 */
	public IConnectionProvider newConnectionProvider(final String dialect, final String host, final String database,
			final String user, final String password) throws TopicMapStoreException {
		if (!protocols.containsKey(dialect)) {
			throw new TopicMapStoreException("No connection provider class registered for given dialect '" + dialect
					+ "'.");
		}
		Class<? extends IConnectionProvider> clazz = protocols.get(dialect);
		try {
			Constructor<? extends IConnectionProvider> constructor = clazz.getConstructor(String.class, String.class,
					String.class, String.class);
			return constructor.newInstance(host, database, user, password);
		} catch (InstantiationException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (IllegalAccessException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (IllegalArgumentException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (InvocationTargetException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (SecurityException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (NoSuchMethodException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		}
	}

	/**
	 * Create a new connection provider for the given dialect
	 * 
	 * @param dialect
	 *            the SQL dialect
	 * @return a new connection provider
	 * @throws IllegalArgumentException
	 *             thrown if the given SQL dialect is unknown
	 * @throws TopicMapStoreException
	 *             thrown if the connection provider cannot be initialized
	 * 
	 */
	public IConnectionProvider newConnectionProvider(final String dialect) throws TopicMapStoreException {
		if (!protocols.containsKey(dialect)) {
			throw new TopicMapStoreException("No connection provider class registered for given dialect '" + dialect
					+ "'.");
		}
		Class<? extends IConnectionProvider> clazz = protocols.get(dialect);
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		} catch (IllegalAccessException e) {
			throw new TopicMapStoreException("Cannot initialize connection provider for dialect '" + dialect + "'.", e);
		}
	}

}
