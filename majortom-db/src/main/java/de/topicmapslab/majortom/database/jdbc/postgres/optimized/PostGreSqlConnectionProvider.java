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
package de.topicmapslab.majortom.database.jdbc.postgres.optimized;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99ConnectionProvider;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public class PostGreSqlConnectionProvider extends Sql99ConnectionProvider {

	private boolean procedureScopeByThemes = false;
	private boolean procedureTopicsByTypeTransitive = false;
	private boolean procedureTransitiveSubtypes = false;
	private boolean procedureTransitiveSubtypesArray = false;
	private boolean procedureTransitiveSupertypes = false;
	private boolean procedureTransitiveSupertypesArray = false;
	private boolean procedureTransitiveTypes = false;
	private boolean procedureTypesAndSubtypes = false;
	private boolean procedureTypesAndSubtypesArray = false;

	/**
	 * constructor
	 */
	public PostGreSqlConnectionProvider() {
	}

	/**
	 * {@inheritDoc}
	 */
	public PostGreSqlQueryProcessor getProcessor() throws TopicMapStoreException {
		return (PostGreSqlQueryProcessor) super.getProcessor();
	}

	/**
	 * {@inheritDoc}
	 */
	protected PostGreSqlQueryProcessor createProcessor(IConnectionProvider provider, Connection connection) {
		return new PostGreSqlQueryProcessor(this, connection);
	}

	/**
	 * {@inheritDoc}
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
	public void openConnection(String host, String database, String user, String password) throws SQLException, TopicMapStoreException {
		super.openConnection(host, database, user, password);
		/*
		 * Check if procedure 'scope_by_themes' exists
		 */
		ResultSet rs = getDatabaseMetaData().getProcedures(null, null, "scope_by_themes");
		procedureScopeByThemes = rs.next();
		rs.close();
		/*
		 * Check if procedure 'topics_by_type_transitive' exists
		 */
		rs = getDatabaseMetaData().getProcedures(null, null, "topics_by_type_transitive");
		procedureTopicsByTypeTransitive = rs.next();
		rs.close();
		/*
		 * Check if procedure 'transitive_types' exists
		 */
		rs = getDatabaseMetaData().getProcedures(null, null, "transitive_types");
		procedureTransitiveTypes = rs.next();
		rs.close();
		/*
		 * Check if procedures 'transitive_subtypes' are existing
		 */
		rs = getDatabaseMetaData().getProcedureColumns(null, null, "transitive_subtypes", "%");
		while (rs.next()) {
			String columnName = rs.getString("COLUMN_NAME");
			if (columnName.equalsIgnoreCase("typeId")) {
				procedureTransitiveSubtypes = true;
			} else if (columnName.equalsIgnoreCase("typeIds")) {
				procedureTransitiveSubtypesArray = true;
			}
		}
		rs.close();
		/*
		 * Check if procedures 'transitive_supertypes' are existing
		 */
		rs = getDatabaseMetaData().getProcedureColumns(null, null, "transitive_supertypes", "%");
		while (rs.next()) {
			String columnName = rs.getString("COLUMN_NAME");
			if (columnName.equalsIgnoreCase("typeId")) {
				procedureTransitiveSupertypes = true;
			} else if (columnName.equalsIgnoreCase("typeIds")) {
				procedureTransitiveSupertypesArray = true;
			}
		}
		rs.close();
		/*
		 * Check if procedures 'types_and_subtypes' are existing
		 */
		rs = getDatabaseMetaData().getProcedureColumns(null, null, "types_and_subtypes", "%");
		while (rs.next()) {
			String columnName = rs.getString("COLUMN_NAME");
			if (columnName.equalsIgnoreCase("typeId")) {
				procedureTypesAndSubtypes = true;
			} else if (columnName.equalsIgnoreCase("typeIds")) {
				procedureTypesAndSubtypesArray = true;
			}
		}
		rs.close();
	}

	/**
	 * Method checks if the procedure 'scope_by_themes' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureScopeByThemes() {
		return procedureScopeByThemes;
	}

	/**
	 * Method checks if the procedure 'topics_by_type_transitive' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTopicsByTypeTransitive() {
		return procedureTopicsByTypeTransitive;
	}

	/**
	 * Method checks if the procedure 'transitive_subtypes' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTransitiveSubtypes() {
		return procedureTransitiveSubtypes;
	}

	/**
	 * Method checks if the procedure 'transitive_subtypes' with array parameter
	 * exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTransitiveSubtypesArray() {
		return procedureTransitiveSubtypesArray;
	}

	/**
	 * Method checks if the procedure 'transitive_supertypes' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTransitiveSupertypes() {
		return procedureTransitiveSupertypes;
	}

	/**
	 * Method checks if the procedure 'transitive_supertypes' with array
	 * parameter exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTransitiveSupertypesArray() {
		return procedureTransitiveSupertypesArray;
	}

	/**
	 * Method checks if the procedure 'transitive_types' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTransitiveTypes() {
		return procedureTransitiveTypes;
	}

	/**
	 * Method checks if the procedure 'types_and_subtypes' exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTypesAndSubtypes() {
		return procedureTypesAndSubtypes;
	}

	/**
	 * Method checks if the procedure 'types_and_subtypes' with array parameter
	 * exists.
	 * 
	 * @return <code>true</code> if the procedure exists, <code>false</code>
	 *         otherwise.
	 */
	protected boolean existsProcedureTypesAndSubtypesArray() {
		return procedureTypesAndSubtypesArray;
	}

}
