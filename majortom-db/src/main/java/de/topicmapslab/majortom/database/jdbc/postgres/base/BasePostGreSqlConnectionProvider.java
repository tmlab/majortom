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
package de.topicmapslab.majortom.database.jdbc.postgres.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.postgresql.ds.PGConnectionPoolDataSource;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
@SuppressWarnings("unchecked")
public abstract class BasePostGreSqlConnectionProvider extends RDBMSConnectionProvider {

	/**
	 * constant for database name
	 */
	private static final String POSTGRESQL = "postgresql";
	/**
	 * constant for driver class
	 */
	private static final String ORG_POSTGRESQL_DRIVER = "org.postgresql.Driver";
	protected static final Map<String, List<String>> schemaInformation = HashUtil.getHashMap();

	private PGConnectionPoolDataSource pool;

	static {
		schemaInformation.put("associations",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type" }));
		schemaInformation.put("changesets", Arrays.asList(new String[] { "id", "id_revision", "id_notifier", "type",
				"newvalue", "oldvalue", "time" }));
		schemaInformation.put("constructs", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap" }));
		schemaInformation.put(
				"datatypeawares",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "value",
						"id_datatype" }));
		schemaInformation.put(
				"history",
				Arrays.asList(new String[] { "id", "id_topicmap", "id_revision", "id_parent", "names", "occurrences",
						"variants", "associations", "id_scope", "id_reification", "id_player", "types", "supertypes",
						"value", "type", "themes", "itemidentifiers", "subjectidentifiers", "subjectlocators",
						"datatype", "roles", "bestlabel", "bestidentifier" }));
		schemaInformation.put("literals", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "value" }));
		schemaInformation.put("locators", Arrays.asList(new String[] { "id", "reference" }));
		schemaInformation.put("metadata", Arrays.asList(new String[] { "id_revision", "key", "value" }));
		schemaInformation.put(
				"names",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type",
						"value" }));
		schemaInformation.put(
				"occurrences",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type",
						"value", "id_datatype" }));
		schemaInformation.put("reifiables",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier" }));
		schemaInformation.put("rel_instance_of", Arrays.asList(new String[] { "id_instance", "id_type" }));
		schemaInformation.put("rel_item_identifiers", Arrays.asList(new String[] { "id_construct", "id_locator" }));
		schemaInformation.put("rel_kind_of", Arrays.asList(new String[] { "id_subtype", "id_supertype" }));
		schemaInformation.put("rel_subject_identifiers", Arrays.asList(new String[] { "id_topic", "id_locator" }));
		schemaInformation.put("rel_subject_locators", Arrays.asList(new String[] { "id_topic", "id_locator" }));
		schemaInformation.put("rel_themes", Arrays.asList(new String[] { "id_scope", "id_theme" }));
		schemaInformation.put("revisions", Arrays.asList(new String[] { "id", "time", "id_topicmap", "type" }));
		schemaInformation.put("roles",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_type", "id_player" }));
		schemaInformation.put("scopeables",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope" }));
		schemaInformation.put("scopes", Arrays.asList(new String[] { "id", "id_topicmap" }));
		schemaInformation.put("tags", Arrays.asList(new String[] { "tag", "time" }));
		schemaInformation.put("topicmaps",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_base_locator" }));
		schemaInformation.put("topics", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap" }));
		schemaInformation.put("typeables", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_type" }));
		schemaInformation.put(
				"variants",
				Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "value",
						"id_datatype" }));
	}

	/**
	 * constructor
	 */
	protected BasePostGreSqlConnectionProvider() {
		// VOID
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
	public BasePostGreSqlConnectionProvider(String host, String database, String user, String password) {
		super(host, database, user, password);
		initializeConnectionPool();
	}

	/**
	 * Method is called to initialize the connection pool
	 */
	private final void initializeConnectionPool() {
		if (pool == null) {
			pool = new PGConnectionPoolDataSource();
			pool.setPassword(getPassword());
			pool.setUser(getUser());
			pool.setServerName(getHost());
			pool.setDatabaseName(getDatabase());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTopicMapStore(JdbcTopicMapStore store) {
		super.setTopicMapStore(store);
		initializeConnectionPool();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getDatabaseState() throws SQLException {
		Map<String, List<String>> databaseValues = HashUtil.getHashMap();
		/*
		 * extract all tables
		 */
		ResultSet rs = getDatabaseMetaData().getTables(null, null, null, new String[] { "TABLE" });
		rs.beforeFirst();
		/*
		 * check if each table of the database is valid for the given schema
		 */
		while (rs.next()) {
			/*
			 * extract table name
			 */
			final String tableName = rs.getString("TABLE_NAME");

			List<String> columnsInformation = schemaInformation.get(tableName);
			if (columnsInformation == null) {
				rs.close();
				System.err.println("Table '" + tableName + "' is invalid for postgres database schema of MaJorToM");
				return STATE_DATABASE_IS_INVALID;
			}
			List<String> databaseColumns = HashUtil.getList();
			/*
			 * extract all columns
			 */
			ResultSet rsColumns = getDatabaseMetaData().getColumns(null, null, tableName, null);
			rsColumns.beforeFirst();
			/*
			 * iterate over columns
			 */
			while (rsColumns.next()) {
				final String columnName = rsColumns.getString("COLUMN_NAME");
				if (!columnsInformation.contains(columnName)) {
					rsColumns.close();
					rs.close();
					System.err.println("Column '" + columnName + "' of table '" + tableName
							+ "' is invalid for postgres database schema of MaJorToM");
					return STATE_DATABASE_IS_INVALID;
				}
				databaseColumns.add(columnName);
			}
			rsColumns.close();
			databaseValues.put(tableName, databaseColumns);
		}
		rs.close();
		/*
		 * check if database is empty
		 */
		if (databaseValues.isEmpty()) {
			return STATE_DATABASE_IS_EMPTY;
		}
		/*
		 * check if all tables are contained
		 */
		for (Entry<String, List<String>> entry : schemaInformation.entrySet()) {
			List<String> columns = databaseValues.get(entry.getKey());
			if (columns == null) {
				System.err.println("Table '" + entry.getKey() + "' is missing!");
				return STATE_DATABASE_IS_INVALID;
			}
			if (!entry.getValue().containsAll(columns)) {
				entry.getValue().removeAll(columns);
				System.err.println("At least one column " + entry.getValue() + " of table '" + entry.getKey()
						+ "' is missing!");
				return STATE_DATABASE_IS_INVALID;
			}
		}
		return STATE_DATABASE_IS_VALID;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDriverClassName() {
		return ORG_POSTGRESQL_DRIVER;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getRdbmsName() {
		return POSTGRESQL;
	}

	/**
	 * Opens a connection by using the connection pool
	 * 
	 * @return the connection from connection pool
	 */
	public Connection getConnection() throws SQLException {
		initializeConnectionPool();
		return pool.getConnection();
	}
}
