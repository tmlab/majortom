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
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryProcessor;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BasePostGreSqlConnectionProvider implements IConnectionProvider {

	protected static final Map<String, List<String>> schemaInformation = HashUtil.getHashMap();

	static {
		schemaInformation.put("associations", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type" }));
		schemaInformation.put("changesets", Arrays.asList(new String[] { "id", "id_revision", "id_notifier", "type", "newvalue", "oldvalue", "time" }));
		schemaInformation.put("constructs", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap" }));
		schemaInformation.put("datatypeawares", Arrays
				.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "value", "id_datatype" }));
		schemaInformation.put("history", Arrays.asList(new String[] { "id", "id_topicmap", "id_revision", "id_parent", "names", "occurrences", "variants",
				"associations", "id_scope", "id_reification", "id_player", "types", "supertypes", "value", "type", "themes", "itemidentifiers",
				"subjectidentifiers", "subjectlocators", "datatype", "roles", "bestlabel" }));
		schemaInformation.put("literals", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "value" }));
		schemaInformation.put("locators", Arrays.asList(new String[] { "id", "reference" }));
		schemaInformation.put("metadata", Arrays.asList(new String[] { "id_revision", "key", "value" }));
		schemaInformation.put("names", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type", "value" }));
		schemaInformation.put("occurrences", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "id_type", "value",
				"id_datatype" }));
		schemaInformation.put("reifiables", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier" }));
		schemaInformation.put("rel_instance_of", Arrays.asList(new String[] { "id_instance", "id_type" }));
		schemaInformation.put("rel_item_identifiers", Arrays.asList(new String[] { "id_construct", "id_locator" }));
		schemaInformation.put("rel_kind_of", Arrays.asList(new String[] { "id_subtype", "id_supertype" }));
		schemaInformation.put("rel_subject_identifiers", Arrays.asList(new String[] { "id_topic", "id_locator" }));
		schemaInformation.put("rel_subject_locators", Arrays.asList(new String[] { "id_topic", "id_locator" }));
		schemaInformation.put("rel_themes", Arrays.asList(new String[] { "id_scope", "id_theme" }));
		schemaInformation.put("revisions", Arrays.asList(new String[] { "id", "time", "id_topicmap" }));
		schemaInformation.put("roles", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_type", "id_player" }));
		schemaInformation.put("scopeables", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope" }));
		schemaInformation.put("scopes", Arrays.asList(new String[] { "id", "id_topicmap" }));
		schemaInformation.put("tags", Arrays.asList(new String[] { "tag", "time" }));
		schemaInformation.put("topicmaps", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_base_locator" }));
		schemaInformation.put("topics", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap" }));
		schemaInformation.put("typeables", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_type" }));
		schemaInformation.put("variants", Arrays.asList(new String[] { "id", "id_parent", "id_topicmap", "id_reifier", "id_scope", "value", "id_datatype" }));
	}

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
	private Sql99QueryProcessor processor;

	/**
	 * internal reference of the topic map store
	 */
	private JdbcTopicMapStore store;

	// storing some connection data in case it's needed after the db closed the connection 
	private String user;
	private String password;
	private String database;
	private String host;

	/**
	 * constructor
	 */
	protected BasePostGreSqlConnectionProvider() {
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
		try {
			if (connection == null) {
				throw new TopicMapStoreException("Connection is not established!");
			} else if (connection.isClosed()) {
				openConnection(host, database, user, password);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException(e);
		}
		return processor;
	}

	/**
	 * {@inheritDoc}
	 */
	public void openConnection(String host, String database, String user, String password) throws SQLException, TopicMapStoreException {
		if (store == null) {
			throw new TopicMapStoreException("Topic map store not set!");
		}
		if (connection != null && !connection.isClosed()) {
			return;
//			throw new TopicMapStoreException("Connection already established!");
		}
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new TopicMapStoreException("Cannot find driver class for PostGreSQL!", e);
		}
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		
		connection = DriverManager.getConnection("jdbc:postgresql://" + host + "/" + database, user, password);
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
	protected abstract Sql99QueryProcessor createProcessor(IConnectionProvider provider, Connection connection);

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
					System.err.println("Column '" + columnName + "' of table '" + tableName + "' is invalid for postgres database schema of MaJorToM");
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
				System.err.println("At least one column " + entry.getValue() + " of table '" + entry.getKey() + "' is missing!");
				return STATE_DATABASE_IS_INVALID;
			}
		}
		return STATE_DATABASE_IS_VALID;
	}
}
