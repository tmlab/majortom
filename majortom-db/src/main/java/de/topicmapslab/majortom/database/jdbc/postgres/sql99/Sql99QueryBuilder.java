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
package de.topicmapslab.majortom.database.jdbc.postgres.sql99;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import de.topicmapslab.majortom.database.jdbc.model.IQueryBuilder;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99ConstraintsQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99DeleteQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99DumpQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99IndexQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99InsertQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99RevisionQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99SelectQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.query.ISql99UpdateQueries;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class Sql99QueryBuilder implements IQueryBuilder {

	/**
	 * the JDBC connection
	 */
	private final Connection connection;

	/**
	 * @param connection
	 *            the JDBC connection to create the {@link PreparedStatement}
	 */
	public Sql99QueryBuilder(Connection connection) {
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			for (Field field : getClass().getDeclaredFields()) {
				if (PreparedStatement.class.equals(field.getType())) {
					PreparedStatement stmt = (PreparedStatement) field.get(this);
					if (stmt != null) {
						stmt.cancel();
						stmt.close();
					}
				}
			}
		} catch (Exception e) {
			throw new TopicMapStoreException("Cannot close prepared statements!", e);
		}
	}

	// ****************
	// * INSERT QUERY *
	// ****************

	private PreparedStatement preparedStatementCreateAssociation;
	private PreparedStatement preparedStatementCreateAssociationWithScope;
	private PreparedStatement preparedStatementCreateLocator;
	private PreparedStatement preparedStatementCreateNameWithScope;
	private PreparedStatement preparedStatementCreateName;
	private PreparedStatement preparedStatementCreateOccurrence;
	private PreparedStatement preparedStatementCreateOccurrenceWithScope;
	private PreparedStatement preparedStatementCreateRole;
	private PreparedStatement preparedStatementCreateScope;
	private PreparedStatement preparedStatementCreateTopicMap;
	private PreparedStatement preparedStatementCreateTopic;
	private PreparedStatement preparedStatementCreateVariant;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateAssociation() throws SQLException {
		if (this.preparedStatementCreateAssociation == null) {
			this.preparedStatementCreateAssociation = connection
					.prepareStatement(ISql99InsertQueries.QUERY_CREATE_ASSOCIATION, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateAssociationWithScope() throws SQLException {
		if (this.preparedStatementCreateAssociationWithScope == null) {
			this.preparedStatementCreateAssociationWithScope = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_ASSOCIATION_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateLocator() throws SQLException {
		if (this.preparedStatementCreateLocator == null) {
			this.preparedStatementCreateLocator = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_LOCATOR, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateNameWithScope() throws SQLException {
		if (this.preparedStatementCreateNameWithScope == null) {
			this.preparedStatementCreateNameWithScope = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_NAME_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateNameWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateName() throws SQLException {
		if (this.preparedStatementCreateName == null) {
			this.preparedStatementCreateName = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_NAME, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateName;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateOccurrence() throws SQLException {
		if (this.preparedStatementCreateOccurrence == null) {
			this.preparedStatementCreateOccurrence = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_OCCURRENCE, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateOccurrence;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateOccurrenceWithScope() throws SQLException {
		if (this.preparedStatementCreateOccurrenceWithScope == null) {
			this.preparedStatementCreateOccurrenceWithScope = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_OCCURRENCE_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateOccurrenceWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateRole() throws SQLException {
		if (this.preparedStatementCreateRole == null) {
			this.preparedStatementCreateRole = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_ROLE, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateScope() throws SQLException {
		if (this.preparedStatementCreateScope == null) {
			this.preparedStatementCreateScope = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_SCOPE, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateTopicMap() throws SQLException {
		if (this.preparedStatementCreateTopicMap == null) {
			this.preparedStatementCreateTopicMap = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_TOPICMAP, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateTopic() throws SQLException {
		if (this.preparedStatementCreateTopic == null) {
			this.preparedStatementCreateTopic = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_TOPIC, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateVariant() throws SQLException {
		if (this.preparedStatementCreateVariant == null) {
			this.preparedStatementCreateVariant = connection.prepareStatement(ISql99InsertQueries.QUERY_CREATE_VARIANT, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateVariant;
	}

	// ****************
	// * SELECT QUERY *
	// ****************

	private PreparedStatement preparedStatementReadPlayedAssociation;
	private PreparedStatement preparedStatementReadPlayedAssociationPaged;
	private PreparedStatement preparedStatementReadPlayedAssociationWithType;
	private PreparedStatement preparedStatementReadPlayedAssociationWithTypeAndScope;
	private PreparedStatement preparedStatementReadPlayedAssociationWithScope;
	private PreparedStatement preparedStatementReadAssociation;
	private PreparedStatement preparedStatementReadAssociationWithType;
	private PreparedStatement preparedStatementReadAssociationWithTypeAndScope;
	private PreparedStatement preparedStatementReadAssociationWithScope;
	private PreparedStatement preparedStatementReadConstructById;
	// private PreparedStatement preparedStatementReadNameById;
	// private PreparedStatement preparedStatementReadOccurrenceById;
	// private PreparedStatement preparedStatementReadVariantById;
	// private PreparedStatement preparedStatementReadAssociationById;
	// private PreparedStatement preparedStatementReadRoleById;
	private PreparedStatement preparedStatementReadConstructByItemIdentifier;
	private PreparedStatement preparedStatementReadDataType;
	private PreparedStatement preparedStatementReadItemIdentifiers;
	private PreparedStatement preparedStatementReadNames;
	private PreparedStatement preparedStatementReadNamesPaged;
	private PreparedStatement preparedStatementReadNamesWithType;
	private PreparedStatement preparedStatementReadNamesWithTypeAndScope;
	private PreparedStatement preparedStatementReadNamesWithScope;
	private PreparedStatement preparedStatementReadOccurrences;
	private PreparedStatement preparedStatementReadOccurrencesPaged;
	private PreparedStatement preparedStatementReadOccurrencesWithType;
	private PreparedStatement preparedStatementReadOccurrencesWithTypeAndScope;
	private PreparedStatement preparedStatementReadOccurrencesWithScope;
	private PreparedStatement preparedStatementReadPlayer;
	private PreparedStatement preparedStatementReadReifier;
	private PreparedStatement preparedStatementReadReified;
	private PreparedStatement preparedStatementReadRoles;
	private PreparedStatement preparedStatementReadRolesPaged;
	private PreparedStatement preparedStatementReadRolesWithType;
	private PreparedStatement preparedStatementReadPlayedRoles;
	private PreparedStatement preparedStatementReadPlayedRolesPaged;
	private PreparedStatement preparedStatementReadPlayedRolesWithType;
	private PreparedStatement preparedStatementReadPlayedRolesWithTypeAndAssociationType;
	private PreparedStatement preparedStatementReadRoleTypes;
	private PreparedStatement preparedStatementReadSubjectIdentifiers;
	private PreparedStatement preparedStatementReadSubjectLocators;
	private PreparedStatement preparedStatementReadSupertypes;
	private PreparedStatement preparedStatementReadThemes;
	private PreparedStatement preparedStatementReadTopicBySubjectIdentifier;
	private PreparedStatement preparedStatementReadTopicBySubjectLocator;
	private PreparedStatement preparedStatementReadTopicMap;
	private PreparedStatement preparedStatementReadTopics;
	private PreparedStatement preparedStatementReadTopicsWithType;
	private PreparedStatement preparedStatementReadType;
	private PreparedStatement preparedStatementReadTypes;
	private PreparedStatement preparedStatementReadTypesPaged;
	private PreparedStatement preparedStatementReadScope;
	private PreparedStatement preparedStatementReadScopeByThemes;
	private PreparedStatement preparedStatementReadEmptyScope;
	private PreparedStatement preparedStatementReadValue;
	private PreparedStatement preparedStatementReadVariants;
	private PreparedStatement preparedStatementReadVariantsPaged;
	private PreparedStatement preparedStatementReadVariantsWithScope;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociation() throws SQLException {
		if (this.preparedStatementReadAssociation == null) {
			this.preparedStatementReadAssociation = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ASSOCIATIONS);
		}
		return this.preparedStatementReadAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithScope() throws SQLException {
		if (this.preparedStatementReadAssociationWithScope == null) {
			this.preparedStatementReadAssociationWithScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ASSOCIATIONS_WITH_SCOPE);
		}
		return this.preparedStatementReadAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithType() throws SQLException {
		if (this.preparedStatementReadAssociationWithType == null) {
			this.preparedStatementReadAssociationWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ASSOCIATIONS_WITH_TYPE);
		}
		return this.preparedStatementReadAssociationWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadAssociationWithTypeAndScope == null) {
			this.preparedStatementReadAssociationWithTypeAndScope = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadAssociationWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadConstructById() throws SQLException {
		if (this.preparedStatementReadConstructById == null) {
			this.preparedStatementReadConstructById = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_CONSTRUCT);
		}
		return this.preparedStatementReadConstructById;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadConstructByItemIdentifier() throws SQLException {
		if (this.preparedStatementReadConstructByItemIdentifier == null) {
			this.preparedStatementReadConstructByItemIdentifier = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER);
		}
		return this.preparedStatementReadConstructByItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadDataType() throws SQLException {
		if (this.preparedStatementReadDataType == null) {
			this.preparedStatementReadDataType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_DATATYPE);
		}
		return this.preparedStatementReadDataType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadItemIdentifiers() throws SQLException {
		if (this.preparedStatementReadItemIdentifiers == null) {
			this.preparedStatementReadItemIdentifiers = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ITEM_IDENTIFIERS);
		}
		return this.preparedStatementReadItemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNames(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadNamesPaged == null) {
				this.preparedStatementReadNamesPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NAMES);
			}
			return this.preparedStatementReadNamesPaged;
		}
		if (this.preparedStatementReadNames == null) {
			this.preparedStatementReadNames = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_NAMES);
		}
		return this.preparedStatementReadNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithScope() throws SQLException {
		if (this.preparedStatementReadNamesWithScope == null) {
			this.preparedStatementReadNamesWithScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_NAMES_WITH_SCOPE);
		}
		return this.preparedStatementReadNamesWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithType() throws SQLException {
		if (this.preparedStatementReadNamesWithType == null) {
			this.preparedStatementReadNamesWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_NAMES_WITH_TYPE);
		}
		return this.preparedStatementReadNamesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadNamesWithTypeAndScope == null) {
			this.preparedStatementReadNamesWithTypeAndScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_NAMES_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadNamesWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrences(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadOccurrencesPaged == null) {
				this.preparedStatementReadOccurrencesPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_OCCURRENCES);
			}
			return this.preparedStatementReadOccurrencesPaged;
		}
		if (this.preparedStatementReadOccurrences == null) {
			this.preparedStatementReadOccurrences = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_OCCURRENCES);
		}
		return this.preparedStatementReadOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithScope() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithScope == null) {
			this.preparedStatementReadOccurrencesWithScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_OCCURRENCES_WITH_SCOPE);
		}
		return this.preparedStatementReadOccurrencesWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithType() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithType == null) {
			this.preparedStatementReadOccurrencesWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_OCCURRENCES_WITH_TYPE);
		}
		return this.preparedStatementReadOccurrencesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithTypeAndScope == null) {
			this.preparedStatementReadOccurrencesWithTypeAndScope = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_OCCURRENCES_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadOccurrencesWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociation(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadPlayedAssociationPaged == null) {
				this.preparedStatementReadPlayedAssociationPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_PLAYED_ASSOCIATIONS);
			}
			return this.preparedStatementReadPlayedAssociationPaged;
		}
		if (this.preparedStatementReadPlayedAssociation == null) {
			this.preparedStatementReadPlayedAssociation = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ASSOCIATIONS);
		}
		return this.preparedStatementReadPlayedAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithScope() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithScope == null) {
			this.preparedStatementReadPlayedAssociationWithScope = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_SCOPE);
		}
		return this.preparedStatementReadPlayedAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithType() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithType == null) {
			this.preparedStatementReadPlayedAssociationWithType = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE);
		}
		return this.preparedStatementReadPlayedAssociationWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithTypeAndScope == null) {
			this.preparedStatementReadPlayedAssociationWithTypeAndScope = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadPlayedAssociationWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRoles(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadPlayedRolesPaged == null) {
				this.preparedStatementReadPlayedRolesPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_PLAYED_ROLES);
			}
			return this.preparedStatementReadPlayedRolesPaged;
		}
		if (this.preparedStatementReadPlayedRoles == null) {
			this.preparedStatementReadPlayedRoles = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ROLES);
		}
		return this.preparedStatementReadPlayedRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRolesWithType() throws SQLException {
		if (this.preparedStatementReadPlayedRolesWithType == null) {
			this.preparedStatementReadPlayedRolesWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ROLES_WITH_TYPE);
		}
		return this.preparedStatementReadPlayedRolesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRolesWithTypeAndAssociationType() throws SQLException {
		if (this.preparedStatementReadPlayedRolesWithTypeAndAssociationType == null) {
			this.preparedStatementReadPlayedRolesWithTypeAndAssociationType = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE);
		}
		return this.preparedStatementReadPlayedRolesWithTypeAndAssociationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayer() throws SQLException {
		if (this.preparedStatementReadPlayer == null) {
			this.preparedStatementReadPlayer = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_PLAYER);
		}
		return this.preparedStatementReadPlayer;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadReified() throws SQLException {
		if (this.preparedStatementReadReified == null) {
			this.preparedStatementReadReified = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_REIFIED);
		}
		return this.preparedStatementReadReified;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadReifier() throws SQLException {
		if (this.preparedStatementReadReifier == null) {
			this.preparedStatementReadReifier = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_REIFIER);
		}
		return this.preparedStatementReadReifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRoleTypes() throws SQLException {
		if (this.preparedStatementReadRoleTypes == null) {
			this.preparedStatementReadRoleTypes = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ROLESTYPES);
		}
		return this.preparedStatementReadRoleTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRoles(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadRolesPaged == null) {
				this.preparedStatementReadRolesPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_ROLES);
			}
			return this.preparedStatementReadRolesPaged;
		}
		if (this.preparedStatementReadRoles == null) {
			this.preparedStatementReadRoles = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ROLES);
		}
		return this.preparedStatementReadRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRolesWithType() throws SQLException {
		if (this.preparedStatementReadRolesWithType == null) {
			this.preparedStatementReadRolesWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_ROLES_WITH_TYPE);
		}
		return this.preparedStatementReadRolesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadScope() throws SQLException {
		if (this.preparedStatementReadScope == null) {
			this.preparedStatementReadScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_SCOPE);
		}
		return this.preparedStatementReadScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadScopeByThemes() throws SQLException {
		if (preparedStatementReadScopeByThemes == null) {
			this.preparedStatementReadScopeByThemes = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_SCOPES_BY_THEME);
		}
		return preparedStatementReadScopeByThemes;
	}

	public PreparedStatement getQueryReadEmptyScope() throws SQLException {
		if (this.preparedStatementReadEmptyScope == null) {
			this.preparedStatementReadEmptyScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_EMPTY_SCOPE);
		}
		return this.preparedStatementReadEmptyScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSubjectIdentifiers() throws SQLException {
		if (this.preparedStatementReadSubjectIdentifiers == null) {
			this.preparedStatementReadSubjectIdentifiers = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_SUBJECT_IDENTIFIERS);
		}
		return this.preparedStatementReadSubjectIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSubjectLocators() throws SQLException {
		if (this.preparedStatementReadSubjectLocators == null) {
			this.preparedStatementReadSubjectLocators = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_SUBJECT_LOCATORS);
		}
		return this.preparedStatementReadSubjectLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSupertypes() throws SQLException {
		if (this.preparedStatementReadSupertypes == null) {
			this.preparedStatementReadSupertypes = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_SUPERTYPES);
		}
		return this.preparedStatementReadSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadThemes() throws SQLException {
		if (this.preparedStatementReadThemes == null) {
			this.preparedStatementReadThemes = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_THEMES);
		}
		return this.preparedStatementReadThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicBySubjectIdentifier() throws SQLException {
		if (this.preparedStatementReadTopicBySubjectIdentifier == null) {
			this.preparedStatementReadTopicBySubjectIdentifier = connection
					.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TOPIC_BY_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementReadTopicBySubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicBySubjectLocator() throws SQLException {
		if (this.preparedStatementReadTopicBySubjectLocator == null) {
			this.preparedStatementReadTopicBySubjectLocator = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TOPIC_BY_SUBJECT_LOCATOR);
		}
		return this.preparedStatementReadTopicBySubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicMap() throws SQLException {
		if (this.preparedStatementReadTopicMap == null) {
			this.preparedStatementReadTopicMap = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TOPICMAP);
		}
		return this.preparedStatementReadTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopics() throws SQLException {
		if (this.preparedStatementReadTopics == null) {
			this.preparedStatementReadTopics = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TOPICS);
		}
		return this.preparedStatementReadTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicsWithType() throws SQLException {
		if (this.preparedStatementReadTopicsWithType == null) {
			this.preparedStatementReadTopicsWithType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TOPICS_WITH_TYPE);
		}
		return this.preparedStatementReadTopicsWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadType() throws SQLException {
		if (this.preparedStatementReadType == null) {
			this.preparedStatementReadType = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TYPE);
		}
		return this.preparedStatementReadType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadTypesPaged == null) {
				this.preparedStatementReadTypesPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_TYPES);
			}
			return this.preparedStatementReadTypesPaged;
		}
		if (this.preparedStatementReadTypes == null) {
			this.preparedStatementReadTypes = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_TYPES);
		}
		return this.preparedStatementReadTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadValue() throws SQLException {
		if (this.preparedStatementReadValue == null) {
			this.preparedStatementReadValue = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_VALUE);
		}
		return this.preparedStatementReadValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadVariants(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementReadVariantsPaged == null) {
				this.preparedStatementReadVariantsPaged = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_VARIANTS);
			}
			return this.preparedStatementReadVariantsPaged;
		}
		if (this.preparedStatementReadVariants == null) {
			this.preparedStatementReadVariants = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_VARIANTS);
		}
		return this.preparedStatementReadVariants;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadVariantsWithScope() throws SQLException {
		if (this.preparedStatementReadVariantsWithScope == null) {
			this.preparedStatementReadVariantsWithScope = connection.prepareStatement(ISql99SelectQueries.NonPaged.QUERY_READ_VARIANTS_WITH_SCOPE);
		}
		return this.preparedStatementReadVariantsWithScope;
	}

	// ****************
	// * UPDATE QUERY *
	// ****************

	private PreparedStatement preparedStatementAddItemIdentifier;
	private PreparedStatement preparedStatementAddSubjectIdentifier;
	private PreparedStatement preparedStatementAddSubjectLocator;
	private PreparedStatement preparedStatementModifyType;
	private PreparedStatement preparedStatementModifyTypes;
	private PreparedStatement preparedStatementModifyPlayer;
	private PreparedStatement preparedStatementModifyReifier;
	private PreparedStatement preparedStatementModifyScope;
	private PreparedStatement preparedStatementModifySupertypes;
	private PreparedStatement preparedStatementModifyValue;
	private PreparedStatement preparedStatementModifyValueWithDatatype;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddItemIdentifier() throws SQLException {
		if (this.preparedStatementAddItemIdentifier == null) {
			this.preparedStatementAddItemIdentifier = connection.prepareStatement(ISql99UpdateQueries.QUERY_ADD_ITEM_IDENTIFIER);
		}
		return this.preparedStatementAddItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddSubjectIdentifier() throws SQLException {
		if (this.preparedStatementAddSubjectIdentifier == null) {
			this.preparedStatementAddSubjectIdentifier = connection.prepareStatement(ISql99UpdateQueries.QUERY_ADD_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementAddSubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddSubjectLocator() throws SQLException {
		if (this.preparedStatementAddSubjectLocator == null) {
			this.preparedStatementAddSubjectLocator = connection.prepareStatement(ISql99UpdateQueries.QUERY_ADD_SUBJECT_LOCATOR);
		}
		return this.preparedStatementAddSubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddThemes(long themeNumber) throws SQLException {
		String query = "";
		for (long n = 0; n < themeNumber; n++) {
			query += ISql99UpdateQueries.QUERY_ADD_THEME;
		}
		return connection.prepareStatement(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyType() throws SQLException {
		if (this.preparedStatementModifyType == null) {
			this.preparedStatementModifyType = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_TYPE);
		}
		return this.preparedStatementModifyType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyTypes() throws SQLException {
		if (this.preparedStatementModifyTypes == null) {
			this.preparedStatementModifyTypes = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_TYPES);
		}
		return this.preparedStatementModifyTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyPlayer() throws SQLException {
		if (this.preparedStatementModifyPlayer == null) {
			this.preparedStatementModifyPlayer = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_PLAYER);
		}
		return this.preparedStatementModifyPlayer;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyReifier() throws SQLException {
		if (this.preparedStatementModifyReifier == null) {
			this.preparedStatementModifyReifier = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_REIFIER, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementModifyReifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyScope() throws SQLException {
		if (this.preparedStatementModifyScope == null) {
			this.preparedStatementModifyScope = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_SCOPE);
		}
		return this.preparedStatementModifyScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifySupertypes() throws SQLException {
		if (this.preparedStatementModifySupertypes == null) {
			this.preparedStatementModifySupertypes = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_SUPERTYPES);
		}
		return this.preparedStatementModifySupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyValue() throws SQLException {
		if (this.preparedStatementModifyValue == null) {
			this.preparedStatementModifyValue = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_VALUE);
		}
		return this.preparedStatementModifyValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyValueWithDatatype() throws SQLException {
		if (this.preparedStatementModifyValueWithDatatype == null) {
			this.preparedStatementModifyValueWithDatatype = connection.prepareStatement(ISql99UpdateQueries.QUERY_MODIFY_VALUE_WITH_DATATYPE);
		}
		return this.preparedStatementModifyValueWithDatatype;
	}

	// ****************
	// * REMOVE QUERY *
	// ****************
	
	private PreparedStatement preparedStatementClearTopicMap;
	private PreparedStatement preparedStatementDeleteTopicMap;	
	private PreparedStatement preparedStatementDeleteTopic;
	private PreparedStatement preparedStatementDeleteName;
	private PreparedStatement preparedStatementDeleteOccurrence;
	private PreparedStatement preparedStatementDeleteVariant;
	private PreparedStatement preparedStatementDeleteAssociation;
	private PreparedStatement preparedStatementDeleteRole;
	private PreparedStatement preparedStatementDeleteType;
	private PreparedStatement preparedStatementDeleteSupertype;
	private PreparedStatement preparedStatementDeleteSubjectIdentifier;
	private PreparedStatement preparedStatementDeleteSubjectLocator;
	private PreparedStatement preparedStatementDeleteItemIdentifier;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteTopicMap() throws SQLException {
		if (this.preparedStatementDeleteTopicMap == null) {
			this.preparedStatementDeleteTopicMap = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_TOPICMAP);
		}
		return this.preparedStatementDeleteTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteAssociation() throws SQLException {
		if (this.preparedStatementDeleteAssociation == null) {
			this.preparedStatementDeleteAssociation = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_ASSOCIATION);
		}
		return this.preparedStatementDeleteAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteItemIdentifier() throws SQLException {
		if (this.preparedStatementDeleteItemIdentifier == null) {
			this.preparedStatementDeleteItemIdentifier = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_ITEM_IDENTIFIER);
		}
		return this.preparedStatementDeleteItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteName() throws SQLException {
		if (this.preparedStatementDeleteName == null) {
			this.preparedStatementDeleteName = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_NAME);
		}
		return this.preparedStatementDeleteName;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteOccurrence() throws SQLException {
		if (this.preparedStatementDeleteOccurrence == null) {
			this.preparedStatementDeleteOccurrence = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_OCCURRENCE);
		}
		return this.preparedStatementDeleteOccurrence;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteRole() throws SQLException {
		if (this.preparedStatementDeleteRole == null) {
			this.preparedStatementDeleteRole = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_ROLE);
		}
		return this.preparedStatementDeleteRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSubjectIdentifier() throws SQLException {
		if (this.preparedStatementDeleteSubjectIdentifier == null) {
			this.preparedStatementDeleteSubjectIdentifier = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementDeleteSubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSubjectLocator() throws SQLException {
		if (this.preparedStatementDeleteSubjectLocator == null) {
			this.preparedStatementDeleteSubjectLocator = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_SUBJECT_LOCATOR);
		}
		return this.preparedStatementDeleteSubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSupertype() throws SQLException {
		if (this.preparedStatementDeleteSupertype == null) {
			this.preparedStatementDeleteSupertype = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_SUPERTYPE);
		}
		return this.preparedStatementDeleteSupertype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteTopic() throws SQLException {
		if (this.preparedStatementDeleteTopic == null) {
			this.preparedStatementDeleteTopic = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_TOPIC);
		}
		return this.preparedStatementDeleteTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteType() throws SQLException {
		if (this.preparedStatementDeleteType == null) {
			this.preparedStatementDeleteType = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_TYPE);
		}
		return this.preparedStatementDeleteType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteVariant() throws SQLException {
		if (this.preparedStatementDeleteVariant == null) {
			this.preparedStatementDeleteVariant = connection.prepareStatement(ISql99DeleteQueries.QUERY_DELETE_VARIANT);
		}
		return this.preparedStatementDeleteVariant;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryClearTopicMap() throws SQLException {
		if (this.preparedStatementClearTopicMap == null) {
			this.preparedStatementClearTopicMap = connection.prepareStatement(ISql99DeleteQueries.QUERY_CLEAR_TOPICMAP);
		}
		return this.preparedStatementClearTopicMap;
	}
	
	// ***************
	// * INDEX QUERY *
	// ***************

	// PagedConstructIndex

	private PreparedStatement preparedStatementNumberOfAssociationsPlayed;
	private PreparedStatement preparedStatementNumberOfNames;
	private PreparedStatement preparedStatementNumberOfOccurrences;
	private PreparedStatement preparedStatementNumberOfVariants;
	private PreparedStatement preparedStatementNumberOfTypes;
	private PreparedStatement preparedStatementNumberOfSupertypes;
	private PreparedStatement preparedStatementNumberOfRolesPlayed;
	private PreparedStatement preparedStatementNumberOfRoles;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfAssociationsPlayed() throws SQLException {
		if (this.preparedStatementNumberOfAssociationsPlayed == null) {
			this.preparedStatementNumberOfAssociationsPlayed = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_PLAYED_ASSOCIATIONS);
		}
		return this.preparedStatementNumberOfAssociationsPlayed;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfNames() throws SQLException {
		if (this.preparedStatementNumberOfNames == null) {
			this.preparedStatementNumberOfNames = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_NAMES);
		}
		return this.preparedStatementNumberOfNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfOccurrences() throws SQLException {
		if (this.preparedStatementNumberOfOccurrences == null) {
			this.preparedStatementNumberOfOccurrences = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_OCCURRENCES);
		}
		return this.preparedStatementNumberOfOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfRoles() throws SQLException {
		if (this.preparedStatementNumberOfRoles == null) {
			this.preparedStatementNumberOfRoles = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_ROLES);
		}
		return this.preparedStatementNumberOfRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfRolesPlayed() throws SQLException {
		if (this.preparedStatementNumberOfRolesPlayed == null) {
			this.preparedStatementNumberOfRolesPlayed = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_PLAYED_ROLES);
		}
		return this.preparedStatementNumberOfRolesPlayed;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfTopicsWithoutSupertypes() throws SQLException {
		if (this.preparedStatementNumberOfSupertypes == null) {
			this.preparedStatementNumberOfSupertypes = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES);
		}
		return this.preparedStatementNumberOfSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfTypes() throws SQLException {
		if (this.preparedStatementNumberOfTypes == null) {
			this.preparedStatementNumberOfTypes = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_TYPES);
		}
		return this.preparedStatementNumberOfTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNumberOfVariants() throws SQLException {
		if (this.preparedStatementNumberOfVariants == null) {
			this.preparedStatementNumberOfVariants = connection.prepareStatement(ISql99SelectQueries.Paged.QUERY_READ_NUMBER_OF_VARIANTS);
		}
		return this.preparedStatementNumberOfVariants;
	}

	// TypeInstanceIndex

	private PreparedStatement preparedStatementIndexAssociationTypes;
	private PreparedStatement preparedStatementIndexNameTypes;
	private PreparedStatement preparedStatementIndexOccurrenceTypes;
	private PreparedStatement preparedStatementIndexCharacteristicTypes;
	private PreparedStatement preparedStatementIndexRoleTypes;
	private PreparedStatement preparedStatementIndexTopicTypes;
	private PreparedStatement preparedStatementIndexAssociationsByType;
	private PreparedStatement preparedStatementIndexAssociationsByTypes;
	private PreparedStatement preparedStatementIndexCharacteristicsByType;
	private PreparedStatement preparedStatementIndexCharacteristicsByTypes;
	private PreparedStatement preparedStatementIndexRolesByType;
	private PreparedStatement preparedStatementIndexRolesByTypes;
	private PreparedStatement preparedStatementIndexNamesByType;
	private PreparedStatement preparedStatementIndexNamesByTypes;
	private PreparedStatement preparedStatementIndexOccurrencesByType;
	private PreparedStatement preparedStatementIndexOccurrencesByTypes;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementsIndexTopicsByTypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutType;

	private PreparedStatement preparedStatementIndexAssociationTypesWithLimit;
	private PreparedStatement preparedStatementIndexNameTypesWithLimit;
	private PreparedStatement preparedStatementIndexOccurrenceTypesWithLimit;
	private PreparedStatement preparedStatementIndexCharacteristicTypesWithLimit;
	private PreparedStatement preparedStatementIndexRoleTypesWithLimit;
	private PreparedStatement preparedStatementIndexTopicTypesWithLimit;
	private PreparedStatement preparedStatementIndexAssociationsByTypeWithLimit;
	private PreparedStatement preparedStatementIndexAssociationsByTypesWithLimit;
	private PreparedStatement preparedStatementIndexCharacteristicsByTypeWithLimit;
	private PreparedStatement preparedStatementIndexCharacteristicsByTypesWithLimit;
	private PreparedStatement preparedStatementIndexRolesByTypeWithLimit;
	private PreparedStatement preparedStatementIndexRolesByTypesWithLimit;
	private PreparedStatement preparedStatementIndexNamesByTypeWithLimit;
	private PreparedStatement preparedStatementIndexNamesByTypesWithLimit;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeWithLimit;
	private PreparedStatement preparedStatementIndexOccurrencesByTypesWithLimit;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementsIndexTopicsByTypesWithLimit;
	private PreparedStatement preparedStatementIndexTopicsWithoutTypeWithLimit;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexAssociationTypesWithLimit == null) {
				this.preparedStatementIndexAssociationTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ASSOCIATIONTYPES);
			}
			return this.preparedStatementIndexAssociationTypesWithLimit;
		}
		if (this.preparedStatementIndexAssociationTypes == null) {
			this.preparedStatementIndexAssociationTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ASSOCIATIONTYPES);
		}
		return this.preparedStatementIndexAssociationTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNameTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexNameTypesWithLimit == null) {
				this.preparedStatementIndexNameTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_NAMETYPES);
			}
			return this.preparedStatementIndexNameTypesWithLimit;
		}
		if (this.preparedStatementIndexNameTypes == null) {
			this.preparedStatementIndexNameTypes = connection.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_NAMETYPES);
		}
		return this.preparedStatementIndexNameTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrenceTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexOccurrenceTypesWithLimit == null) {
				this.preparedStatementIndexOccurrenceTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_OCCURRENCETYPES);
			}
			return this.preparedStatementIndexOccurrenceTypesWithLimit;
		}
		if (this.preparedStatementIndexOccurrenceTypes == null) {
			this.preparedStatementIndexOccurrenceTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_OCCURRENCETYPES);
		}
		return this.preparedStatementIndexOccurrenceTypes;
	}

	public PreparedStatement getQuerySelectCharacteristicTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexCharacteristicTypesWithLimit == null) {
				this.preparedStatementIndexCharacteristicTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_CHARACTERISTICTYPES);
			}
			return this.preparedStatementIndexCharacteristicTypesWithLimit;
		}
		if (this.preparedStatementIndexCharacteristicTypes == null) {
			this.preparedStatementIndexCharacteristicTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_CHARACTERISTICTYPES);
		}
		return this.preparedStatementIndexCharacteristicTypes;
	}

	public PreparedStatement getQuerySelectRoleTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexRoleTypesWithLimit == null) {
				this.preparedStatementIndexRoleTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ROLETYPES);
			}
			return this.preparedStatementIndexRoleTypesWithLimit;
		}
		if (this.preparedStatementIndexRoleTypes == null) {
			this.preparedStatementIndexRoleTypes = connection.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ROLETYPES);
		}
		return this.preparedStatementIndexRoleTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexTopicTypesWithLimit == null) {
				this.preparedStatementIndexTopicTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_TOPICTYPES);
			}
			return this.preparedStatementIndexTopicTypesWithLimit;
		}
		if (this.preparedStatementIndexTopicTypes == null) {
			this.preparedStatementIndexTopicTypes = connection.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPICTYPES);
		}
		return this.preparedStatementIndexTopicTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByType(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexAssociationsByTypeWithLimit == null) {
				this.preparedStatementIndexAssociationsByTypeWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
			}
			return this.preparedStatementIndexAssociationsByTypeWithLimit;
		}
		if (this.preparedStatementIndexAssociationsByType == null) {
			this.preparedStatementIndexAssociationsByType = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
		}
		return this.preparedStatementIndexAssociationsByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexAssociationsByTypesWithLimit == null) {
				this.preparedStatementIndexAssociationsByTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_TYPES);
			}
			return this.preparedStatementIndexAssociationsByTypesWithLimit;
		}
		if (this.preparedStatementIndexAssociationsByTypes == null) {
			this.preparedStatementIndexAssociationsByTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_TYPES);
		}
		return this.preparedStatementIndexAssociationsByTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByType(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexCharacteristicsByTypeWithLimit == null) {
				this.preparedStatementIndexCharacteristicsByTypeWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_TYPE);
			}
			return this.preparedStatementIndexCharacteristicsByTypeWithLimit;
		}
		if (this.preparedStatementIndexCharacteristicsByType == null) {
			this.preparedStatementIndexCharacteristicsByType = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_TYPE);
		}
		return this.preparedStatementIndexCharacteristicsByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexCharacteristicsByTypesWithLimit == null) {
				this.preparedStatementIndexCharacteristicsByTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_TYPES);
			}
			return this.preparedStatementIndexCharacteristicsByTypesWithLimit;
		}
		if (this.preparedStatementIndexCharacteristicsByTypes == null) {
			this.preparedStatementIndexCharacteristicsByTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_TYPES);
		}
		return this.preparedStatementIndexCharacteristicsByTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByType(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexRolesByTypeWithLimit == null) {
				this.preparedStatementIndexRolesByTypeWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ROLES_BY_TYPE);
			}
			return this.preparedStatementIndexRolesByTypeWithLimit;
		}
		if (this.preparedStatementIndexRolesByType == null) {
			this.preparedStatementIndexRolesByType = connection.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ROLES_BY_TYPE);
		}
		return this.preparedStatementIndexRolesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexRolesByTypesWithLimit == null) {
				this.preparedStatementIndexRolesByTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_ROLES_BY_TYPES);
			}
			return this.preparedStatementIndexRolesByTypesWithLimit;
		}
		if (this.preparedStatementIndexRolesByTypes == null) {
			this.preparedStatementIndexRolesByTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_ROLES_BY_TYPES);
		}
		return this.preparedStatementIndexRolesByTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByType(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexNamesByTypeWithLimit == null) {
				this.preparedStatementIndexNamesByTypeWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_NAMES_BY_TYPE);
			}
			return this.preparedStatementIndexNamesByTypeWithLimit;
		}
		if (this.preparedStatementIndexNamesByType == null) {
			this.preparedStatementIndexNamesByType = connection.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_NAMES_BY_TYPE);
		}
		return this.preparedStatementIndexNamesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexNamesByTypesWithLimit == null) {
				this.preparedStatementIndexNamesByTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_NAMES_BY_TYPES);
			}
			return this.preparedStatementIndexNamesByTypesWithLimit;
		}
		if (this.preparedStatementIndexNamesByTypes == null) {
			this.preparedStatementIndexNamesByTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_NAMES_BY_TYPES);
		}
		return this.preparedStatementIndexNamesByTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByType(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexOccurrencesByTypeWithLimit == null) {
				this.preparedStatementIndexOccurrencesByTypeWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
			}
			return this.preparedStatementIndexOccurrencesByTypeWithLimit;
		}
		if (this.preparedStatementIndexOccurrencesByType == null) {
			this.preparedStatementIndexOccurrencesByType = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
		}
		return this.preparedStatementIndexOccurrencesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByTypes(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexOccurrencesByTypesWithLimit == null) {
				this.preparedStatementIndexOccurrencesByTypesWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_TYPES);
			}
			return this.preparedStatementIndexOccurrencesByTypesWithLimit;
		}
		if (this.preparedStatementIndexOccurrencesByTypes == null) {
			this.preparedStatementIndexOccurrencesByTypes = connection
					.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_TYPES);
		}
		return this.preparedStatementIndexOccurrencesByTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypes(long typeCount, boolean all, boolean withLimit) throws SQLException {
		/*
		 * check if number of types is zero
		 */
		if (typeCount == 0) {
			if (withLimit) {
				if (this.preparedStatementIndexTopicsWithoutTypeWithLimit == null) {
					this.preparedStatementIndexTopicsWithoutTypeWithLimit = connection
							.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_TOPIC_WITHOUT_TYPE);
				}
				return this.preparedStatementIndexTopicsWithoutTypeWithLimit;
			}
			if (this.preparedStatementIndexTopicsWithoutType == null) {
				this.preparedStatementIndexTopicsWithoutType = connection
						.prepareStatement(ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPIC_WITHOUT_TYPE);
			}
			return this.preparedStatementIndexTopicsWithoutType;
		}

		Map<Boolean, Map<Long, PreparedStatement>> sourceMap;
		final String query;
		if (withLimit) {
			query = all ? ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL
					: ISql99IndexQueries.QueryTypeInstanceIndex.Paged.QUERY_SELECT_TOPIC_BY_TYPES;
			sourceMap = preparedStatementsIndexTopicsByTypesWithLimit;
		} else {
			query = all ? ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL
					: ISql99IndexQueries.QueryTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPIC_BY_TYPES;
			sourceMap = preparedStatementsIndexTopicsByTypes;
		}
		/*
		 * check if cache is initialized
		 */
		if (sourceMap == null) {
			sourceMap = HashUtil.getHashMap();
		}
		Map<Long, PreparedStatement> map = sourceMap.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			sourceMap.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(typeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(query, "id_type", typeCount, all);
			map.put(typeCount, stmt);
		}
		return stmt;
	}

	// TransitiveTypeInstanceIndex

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByTypeTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByTypeTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByTypeTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByTypeTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypeTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypesTransitive(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	// ScopeIndex
	private PreparedStatement preparedStatementIndexScopesByThemesUsed;

	private PreparedStatement preparedStatementIndexAssociationsByScope;
	private PreparedStatement preparedStatementIndexAssociationsByScopePaged;
	private PreparedStatement preparedStatementIndexAssociationsByScopes;
	private PreparedStatement preparedStatementIndexAssociationsByScopesPaged;
	private PreparedStatement preparedStatementIndexAssociationsByTheme;
	private PreparedStatement preparedStatementIndexAssociationsByThemePaged;
	private PreparedStatement preparedStatementIndexAssociationsByThemes;
	private PreparedStatement preparedStatementIndexAssociationsByThemesPaged;
	private PreparedStatement preparedStatementIndexAssociationsByThemesMatchingAll;
	private PreparedStatement preparedStatementIndexAssociationsByThemesMatchingAllPaged;
	private PreparedStatement preparedStatementIndexAssociationScopes;
	private PreparedStatement preparedStatementIndexAssociationScopesPaged;
	private PreparedStatement preparedStatementIndexAssociationThemes;
	private PreparedStatement preparedStatementIndexAssociationThemesPaged;

	private PreparedStatement preparedStatementIndexCharacteristicsByScope;
	private PreparedStatement preparedStatementIndexCharacteristicsByScopePaged;

	private PreparedStatement preparedStatementIndexNamesByScope;
	private PreparedStatement preparedStatementIndexNamesByScopes;
	private PreparedStatement preparedStatementIndexNamesByTheme;
	private PreparedStatement preparedStatementIndexNamesByThemes;
	private PreparedStatement preparedStatementIndexNamesByThemesMatchingAll;
	private PreparedStatement preparedStatementIndexNameScopes;
	private PreparedStatement preparedStatementIndexNameThemes;
	private PreparedStatement preparedStatementIndexNamesByScopePaged;
	private PreparedStatement preparedStatementIndexNamesByScopesPaged;
	private PreparedStatement preparedStatementIndexNamesByThemePaged;
	private PreparedStatement preparedStatementIndexNamesByThemesPaged;
	private PreparedStatement preparedStatementIndexNamesByThemesMatchingAllPaged;
	private PreparedStatement preparedStatementIndexNameScopesPaged;
	private PreparedStatement preparedStatementIndexNameThemesPaged;

	private PreparedStatement preparedStatementIndexOccurrencesByScope;
	private PreparedStatement preparedStatementIndexOccurrencesByScopes;
	private PreparedStatement preparedStatementIndexOccurrencesByTheme;
	private PreparedStatement preparedStatementIndexOccurrencesByThemes;
	private PreparedStatement preparedStatementIndexOccurrencesByThemesMatchingAll;
	private PreparedStatement preparedStatementIndexOccurrenceScopes;
	private PreparedStatement preparedStatementIndexOccurrenceThemes;
	private PreparedStatement preparedStatementIndexOccurrencesByScopePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByScopesPaged;
	private PreparedStatement preparedStatementIndexOccurrencesByThemePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByThemesPaged;
	private PreparedStatement preparedStatementIndexOccurrencesByThemesMatchingAllPaged;
	private PreparedStatement preparedStatementIndexOccurrenceScopesPaged;
	private PreparedStatement preparedStatementIndexOccurrenceThemesPaged;

	private PreparedStatement preparedStatementIndexScopables;
	private PreparedStatement preparedStatementIndexScopablesPaged;

	private PreparedStatement preparedStatementIndexVariantsByScope;
	private PreparedStatement preparedStatementIndexVariantsByScopes;
	private PreparedStatement preparedStatementIndexVariantsByTheme;
	private PreparedStatement preparedStatementIndexVariantsByThemes;
	private PreparedStatement preparedStatementIndexVariantsByThemesMatchingAll;
	private PreparedStatement preparedStatementIndexVariantScopes;
	private PreparedStatement preparedStatementIndexVariantThemes;
	private PreparedStatement preparedStatementIndexVariantsByScopePaged;
	private PreparedStatement preparedStatementIndexVariantsByScopesPaged;
	private PreparedStatement preparedStatementIndexVariantsByThemePaged;
	private PreparedStatement preparedStatementIndexVariantsByThemesPaged;
	private PreparedStatement preparedStatementIndexVariantsByThemesMatchingAllPaged;
	private PreparedStatement preparedStatementIndexVariantScopesPaged;
	private PreparedStatement preparedStatementIndexVariantThemesPaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryScopesByThemesUsed() throws SQLException {
		if (this.preparedStatementIndexScopesByThemesUsed == null) {
			preparedStatementIndexScopesByThemesUsed = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_SCOPES_BY_THEMES_USED);
		}
		return preparedStatementIndexScopesByThemesUsed;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByScope(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationsByScopePaged == null) {
				this.preparedStatementIndexAssociationsByScopePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_SCOPE);
			}
			return this.preparedStatementIndexAssociationsByScopePaged;
		}
		if (this.preparedStatementIndexAssociationsByScope == null) {
			this.preparedStatementIndexAssociationsByScope = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_SCOPE);
		}
		return this.preparedStatementIndexAssociationsByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationsByScopesPaged == null) {
				this.preparedStatementIndexAssociationsByScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_SCOPES);
			}
			return this.preparedStatementIndexAssociationsByScopesPaged;
		}
		if (this.preparedStatementIndexAssociationsByScopes == null) {
			this.preparedStatementIndexAssociationsByScopes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_SCOPES);
		}
		return this.preparedStatementIndexAssociationsByScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByTheme(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationsByThemePaged == null) {
				this.preparedStatementIndexAssociationsByThemePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_THEME);
			}
			return this.preparedStatementIndexAssociationsByThemePaged;
		}
		if (this.preparedStatementIndexAssociationsByTheme == null) {
			this.preparedStatementIndexAssociationsByTheme = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_THEME);
		}
		return this.preparedStatementIndexAssociationsByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByThemes(boolean all, boolean paged) throws SQLException {
		/*
		 * scope of construct should contain all themes
		 */
		if (all) {
			if (paged) {
				if (this.preparedStatementIndexAssociationsByThemesMatchingAllPaged == null) {
					this.preparedStatementIndexAssociationsByThemesMatchingAllPaged = connection
							.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_THEMES_MATCH_ALL);
				}
				return this.preparedStatementIndexAssociationsByThemesMatchingAllPaged;
			}
			if (this.preparedStatementIndexAssociationsByThemesMatchingAll == null) {
				this.preparedStatementIndexAssociationsByThemesMatchingAll = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_THEMES_MATCH_ALL);
			}
			return this.preparedStatementIndexAssociationsByThemesMatchingAll;
		}
		if (paged) {
			if (this.preparedStatementIndexAssociationsByThemesPaged == null) {
				this.preparedStatementIndexAssociationsByThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_THEMES);
			}
			return this.preparedStatementIndexAssociationsByThemesPaged;
		}
		if (this.preparedStatementIndexAssociationsByThemes == null) {
			this.preparedStatementIndexAssociationsByThemes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_THEMES);
		}
		return this.preparedStatementIndexAssociationsByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationScopesPaged == null) {
				this.preparedStatementIndexAssociationScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATION_SCOPES);
			}
			return this.preparedStatementIndexAssociationScopesPaged;
		}
		if (this.preparedStatementIndexAssociationScopes == null) {
			this.preparedStatementIndexAssociationScopes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATION_SCOPES);
		}
		return this.preparedStatementIndexAssociationScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationThemes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationThemesPaged == null) {
				this.preparedStatementIndexAssociationThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_ASSOCIATION_THEMES);
			}
			return this.preparedStatementIndexAssociationThemesPaged;
		}
		if (this.preparedStatementIndexAssociationThemes == null) {
			this.preparedStatementIndexAssociationThemes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_ASSOCIATION_THEMES);
		}
		return this.preparedStatementIndexAssociationThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCharacteristicsByScope(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsByScopePaged == null) {
				this.preparedStatementIndexCharacteristicsByScopePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_SCOPE);
			}
			return this.preparedStatementIndexCharacteristicsByScopePaged;
		}
		if (this.preparedStatementIndexCharacteristicsByScope == null) {
			this.preparedStatementIndexCharacteristicsByScope = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_SCOPE);
		}
		return this.preparedStatementIndexCharacteristicsByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByScope(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNamesByScopePaged == null) {
				this.preparedStatementIndexNamesByScopePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAMES_BY_SCOPE);
			}
			return this.preparedStatementIndexNamesByScopePaged;
		}
		if (this.preparedStatementIndexNamesByScope == null) {
			this.preparedStatementIndexNamesByScope = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAMES_BY_SCOPE);
		}
		return this.preparedStatementIndexNamesByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNamesByScopesPaged == null) {
				this.preparedStatementIndexNamesByScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAMES_BY_SCOPES);
			}
			return this.preparedStatementIndexNamesByScopesPaged;
		}
		if (this.preparedStatementIndexNamesByScopes == null) {
			this.preparedStatementIndexNamesByScopes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAMES_BY_SCOPES);
		}
		return this.preparedStatementIndexNamesByScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByTheme(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNamesByThemePaged == null) {
				this.preparedStatementIndexNamesByThemePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAMES_BY_THEME);
			}
			return this.preparedStatementIndexNamesByThemePaged;
		}
		if (this.preparedStatementIndexNamesByTheme == null) {
			this.preparedStatementIndexNamesByTheme = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAMES_BY_THEME);
		}
		return this.preparedStatementIndexNamesByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByThemes(boolean all, boolean paged) throws SQLException {
		/*
		 * scope of construct should contain all themes
		 */
		if (all) {
			if (paged) {
				if (this.preparedStatementIndexNamesByThemesMatchingAllPaged == null) {
					this.preparedStatementIndexNamesByThemesMatchingAllPaged = connection
							.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAMES_BY_THEMES_MATCH_ALL);
				}
				return this.preparedStatementIndexNamesByThemesMatchingAllPaged;
			}
			if (this.preparedStatementIndexNamesByThemesMatchingAll == null) {
				this.preparedStatementIndexNamesByThemesMatchingAll = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAMES_BY_THEMES_MATCH_ALL);
			}
			return this.preparedStatementIndexNamesByThemesMatchingAll;
		}
		if (paged) {
			if (this.preparedStatementIndexNamesByThemesPaged == null) {
				this.preparedStatementIndexNamesByThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAMES_BY_THEMES);
			}
			return this.preparedStatementIndexNamesByThemesPaged;
		}
		if (this.preparedStatementIndexNamesByThemes == null) {
			this.preparedStatementIndexNamesByThemes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAMES_BY_THEMES);
		}
		return this.preparedStatementIndexNamesByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNameScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNameScopesPaged == null) {
				this.preparedStatementIndexNameScopesPaged = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAME_SCOPES);
			}
			return this.preparedStatementIndexNameScopesPaged;
		}
		if (this.preparedStatementIndexNameScopes == null) {
			this.preparedStatementIndexNameScopes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAME_SCOPES);
		}
		return this.preparedStatementIndexNameScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNameThemes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNameThemesPaged == null) {
				this.preparedStatementIndexNameThemesPaged = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_NAME_THEMES);
			}
			return this.preparedStatementIndexNameThemesPaged;
		}
		if (this.preparedStatementIndexNameThemes == null) {
			this.preparedStatementIndexNameThemes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_NAME_THEMES);
		}
		return this.preparedStatementIndexNameThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByScope(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByScopePaged == null) {
				this.preparedStatementIndexOccurrencesByScopePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_SCOPE);
			}
			return this.preparedStatementIndexOccurrencesByScopePaged;
		}
		if (this.preparedStatementIndexOccurrencesByScope == null) {
			this.preparedStatementIndexOccurrencesByScope = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_SCOPE);
		}
		return this.preparedStatementIndexOccurrencesByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByScopesPaged == null) {
				this.preparedStatementIndexOccurrencesByScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_SCOPES);
			}
			return this.preparedStatementIndexOccurrencesByScopesPaged;
		}
		if (this.preparedStatementIndexOccurrencesByScopes == null) {
			this.preparedStatementIndexOccurrencesByScopes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_SCOPES);
		}
		return this.preparedStatementIndexOccurrencesByScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByTheme(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByThemePaged == null) {
				this.preparedStatementIndexOccurrencesByThemePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_THEME);
			}
			return this.preparedStatementIndexOccurrencesByThemePaged;
		}
		if (this.preparedStatementIndexOccurrencesByTheme == null) {
			this.preparedStatementIndexOccurrencesByTheme = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_THEME);
		}
		return this.preparedStatementIndexOccurrencesByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByThemes(boolean all, boolean paged) throws SQLException {
		/*
		 * scope of construct should contain all themes
		 */
		if (all) {
			if (paged) {
				if (this.preparedStatementIndexOccurrencesByThemesMatchingAllPaged == null) {
					this.preparedStatementIndexOccurrencesByThemesMatchingAllPaged = connection
							.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_THEMES_MATCH_ALL);
				}
				return this.preparedStatementIndexOccurrencesByThemesMatchingAllPaged;
			}
			if (this.preparedStatementIndexOccurrencesByThemesMatchingAll == null) {
				this.preparedStatementIndexOccurrencesByThemesMatchingAll = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_THEMES_MATCH_ALL);
			}
			return this.preparedStatementIndexOccurrencesByThemesMatchingAll;
		}
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByThemesPaged == null) {
				this.preparedStatementIndexOccurrencesByThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_THEMES);
			}
			return this.preparedStatementIndexOccurrencesByThemesPaged;
		}
		if (this.preparedStatementIndexOccurrencesByThemes == null) {
			this.preparedStatementIndexOccurrencesByThemes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_THEMES);
		}
		return this.preparedStatementIndexOccurrencesByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrenceScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrenceScopesPaged == null) {
				this.preparedStatementIndexOccurrenceScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCE_SCOPES);
			}
			return this.preparedStatementIndexOccurrenceScopesPaged;
		}
		if (this.preparedStatementIndexOccurrenceScopes == null) {
			this.preparedStatementIndexOccurrenceScopes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCE_SCOPES);
		}
		return this.preparedStatementIndexOccurrenceScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrenceThemes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrenceThemesPaged == null) {
				this.preparedStatementIndexOccurrenceThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_OCCURRENCE_THEMES);
			}
			return this.preparedStatementIndexOccurrenceThemesPaged;
		}
		if (this.preparedStatementIndexOccurrenceThemes == null) {
			this.preparedStatementIndexOccurrenceThemes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_OCCURRENCE_THEMES);
		}
		return this.preparedStatementIndexOccurrenceThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryScopables(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexScopablesPaged == null) {
				this.preparedStatementIndexScopablesPaged = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_SCOPABLES);
			}
			return this.preparedStatementIndexScopablesPaged;
		}
		if (this.preparedStatementIndexScopables == null) {
			this.preparedStatementIndexScopables = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_SCOPABLES);
		}
		return this.preparedStatementIndexScopables;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByScope(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantsByScopePaged == null) {
				this.preparedStatementIndexVariantsByScopePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANTS_BY_SCOPE);
			}
			return this.preparedStatementIndexVariantsByScopePaged;
		}
		if (this.preparedStatementIndexVariantsByScope == null) {
			this.preparedStatementIndexVariantsByScope = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_SCOPE);
		}
		return this.preparedStatementIndexVariantsByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantsByScopesPaged == null) {
				this.preparedStatementIndexVariantsByScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANTS_BY_SCOPES);
			}
			return this.preparedStatementIndexVariantsByScopesPaged;
		}
		if (this.preparedStatementIndexVariantsByScopes == null) {
			this.preparedStatementIndexVariantsByScopes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_SCOPES);
		}
		return this.preparedStatementIndexVariantsByScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByTheme(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantsByThemePaged == null) {
				this.preparedStatementIndexVariantsByThemePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANTS_BY_THEME);
			}
			return this.preparedStatementIndexVariantsByThemePaged;
		}
		if (this.preparedStatementIndexVariantsByTheme == null) {
			this.preparedStatementIndexVariantsByTheme = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_THEME);
		}
		return this.preparedStatementIndexVariantsByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByThemes(boolean all, boolean paged) throws SQLException {
		/*
		 * scope of construct should contain all themes
		 */
		if (all) {
			if (paged) {
				if (this.preparedStatementIndexVariantsByThemesMatchingAllPaged == null) {
					this.preparedStatementIndexVariantsByThemesMatchingAllPaged = connection
							.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANTS_BY_THEMES_MATCH_ALL);
				}
				return this.preparedStatementIndexVariantsByThemesMatchingAllPaged;
			}
			if (this.preparedStatementIndexVariantsByThemesMatchingAll == null) {
				this.preparedStatementIndexVariantsByThemesMatchingAll = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_THEMES_MATCH_ALL);
			}
			return this.preparedStatementIndexVariantsByThemesMatchingAll;
		}
		if (paged) {
			if (this.preparedStatementIndexVariantsByThemesPaged == null) {
				this.preparedStatementIndexVariantsByThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANTS_BY_THEMES);
			}
			return this.preparedStatementIndexVariantsByThemesPaged;
		}
		if (this.preparedStatementIndexVariantsByThemes == null) {
			this.preparedStatementIndexVariantsByThemes = connection
					.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_THEMES);
		}
		return this.preparedStatementIndexVariantsByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantScopes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantScopesPaged == null) {
				this.preparedStatementIndexVariantScopesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANT_SCOPES);
			}
			return this.preparedStatementIndexVariantScopesPaged;
		}
		if (this.preparedStatementIndexVariantScopes == null) {
			this.preparedStatementIndexVariantScopes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANT_SCOPES);
		}
		return this.preparedStatementIndexVariantScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantThemes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantThemesPaged == null) {
				this.preparedStatementIndexVariantThemesPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryScopeIndex.Paged.QUERY_SELECT_VARIANT_THEMES);
			}
			return this.preparedStatementIndexVariantThemesPaged;
		}
		if (this.preparedStatementIndexVariantThemes == null) {
			this.preparedStatementIndexVariantThemes = connection.prepareStatement(ISql99IndexQueries.QueryScopeIndex.NonPaged.QUERY_SELECT_VARIANT_THEMES);
		}
		return this.preparedStatementIndexVariantThemes;
	}

	// LiteralIndex

	private PreparedStatement preparedStatementIndexCharacteristics;
	private PreparedStatement preparedStatementIndexCharacteristicsPaged;
	private PreparedStatement preparedStatementIndexCharacteristicsByValue;
	private PreparedStatement preparedStatementIndexCharacteristicsByValuePaged;
	private PreparedStatement preparedStatementIndexCharacteristicsByDatatype;
	private PreparedStatement preparedStatementIndexCharacteristicsByDatatypePaged;
	private PreparedStatement preparedStatementIndexCharacteristicsByPattern;
	private PreparedStatement preparedStatementIndexCharacteristicsByPatternPaged;
	private PreparedStatement preparedStatementIndexCharacteristicsByPatternAndDatatype;
	private PreparedStatement preparedStatementIndexCharacteristicsByPatternAndDatatypePaged;
	private PreparedStatement preparedStatementIndexDatatypeAwaresByDatatype;
	private PreparedStatement preparedStatementIndexDatatypeAwaresByDatatypePaged;
	private PreparedStatement preparedStatementIndexNames;
	private PreparedStatement preparedStatementIndexNamesPaged;
	private PreparedStatement preparedStatementIndexNamesByValue;
	private PreparedStatement preparedStatementIndexNamesByPattern;
	private PreparedStatement preparedStatementIndexOccurrences;
	private PreparedStatement preparedStatementIndexOccurrencesPaged;
	private PreparedStatement preparedStatementIndexOccurrencesByDatatype;
	private PreparedStatement preparedStatementIndexOccurrencesByDatatypePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByDateRange;
	private PreparedStatement preparedStatementIndexOccurrencesByDateRangePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByRange;
	private PreparedStatement preparedStatementIndexOccurrencesByRangePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByValue;
	private PreparedStatement preparedStatementIndexOccurrencesByValueAndDatatype;
	private PreparedStatement preparedStatementIndexOccurrencesByValueAndDatatypePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByPattern;
	private PreparedStatement preparedStatementIndexOccurrencesByPatternAndDatatype;
	private PreparedStatement preparedStatementIndexVariants;
	private PreparedStatement preparedStatementIndexVariantsPaged;
	private PreparedStatement preparedStatementIndexVariantsByDatatype;
	private PreparedStatement preparedStatementIndexVariantsByValue;
	private PreparedStatement preparedStatementIndexVariantsByValueAndDatatype;
	private PreparedStatement preparedStatementIndexVariantsByPattern;
	private PreparedStatement preparedStatementIndexVariantsByPatternAndDatatype;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristics(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsPaged == null) {
				this.preparedStatementIndexCharacteristicsPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_CHARACTERISTICS);
			}
			return this.preparedStatementIndexCharacteristicsPaged;
		}
		if (this.preparedStatementIndexCharacteristics == null) {
			this.preparedStatementIndexCharacteristics = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS);
		}
		return this.preparedStatementIndexCharacteristics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByValue(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsByValuePaged == null) {
				this.preparedStatementIndexCharacteristicsByValuePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_VALUE);
			}
			return this.preparedStatementIndexCharacteristicsByValuePaged;
		}
		if (this.preparedStatementIndexCharacteristicsByValue == null) {
			this.preparedStatementIndexCharacteristicsByValue = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_VALUE);
		}
		return this.preparedStatementIndexCharacteristicsByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByDatatype(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsByDatatypePaged == null) {
				this.preparedStatementIndexCharacteristicsByDatatypePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_DATATYPE);
			}
			return this.preparedStatementIndexCharacteristicsByDatatypePaged;
		}
		if (this.preparedStatementIndexCharacteristicsByDatatype == null) {
			this.preparedStatementIndexCharacteristicsByDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_DATATYPE);
		}
		return this.preparedStatementIndexCharacteristicsByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByPattern(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsByPatternPaged == null) {
				this.preparedStatementIndexCharacteristicsByPatternPaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_REGEXP);
			}
			return this.preparedStatementIndexCharacteristicsByPatternPaged;
		}
		if (this.preparedStatementIndexCharacteristicsByPattern == null) {
			this.preparedStatementIndexCharacteristicsByPattern = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_REGEXP);
		}
		return this.preparedStatementIndexCharacteristicsByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectCharacteristicsByPatternAndDatatype(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexCharacteristicsByPatternAndDatatypePaged == null) {
				this.preparedStatementIndexCharacteristicsByPatternAndDatatypePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_CHARACTERISTICS_BY_REGEXP_AND_DATATYPE);
			}
			return this.preparedStatementIndexCharacteristicsByPatternAndDatatypePaged;
		}
		if (this.preparedStatementIndexCharacteristicsByPatternAndDatatype == null) {
			this.preparedStatementIndexCharacteristicsByPatternAndDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_CHARACTERISTICS_BY_REGEXP_AND_DATATYPE);
		}
		return this.preparedStatementIndexCharacteristicsByPatternAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectDatatypeAwaresByDatatype(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexDatatypeAwaresByDatatypePaged == null) {
				this.preparedStatementIndexDatatypeAwaresByDatatypePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_DATATYPEAWARES_BY_DATATYPE);
			}
			return this.preparedStatementIndexDatatypeAwaresByDatatypePaged;
		}
		if (this.preparedStatementIndexDatatypeAwaresByDatatype == null) {
			this.preparedStatementIndexDatatypeAwaresByDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_DATATYPEAWARES_BY_DATATYPE);
		}
		return this.preparedStatementIndexDatatypeAwaresByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNames(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNamesPaged == null) {
				this.preparedStatementIndexNamesPaged = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_NAMES);
			}
			return this.preparedStatementIndexNamesPaged;
		}
		if (this.preparedStatementIndexNames == null) {
			this.preparedStatementIndexNames = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_NAMES);
		}
		return this.preparedStatementIndexNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByValue() throws SQLException {
		if (this.preparedStatementIndexNamesByValue == null) {
			this.preparedStatementIndexNamesByValue = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_NAMES_BY_VALUE);
		}
		return this.preparedStatementIndexNamesByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByPattern() throws SQLException {
		if (this.preparedStatementIndexNamesByPattern == null) {
			this.preparedStatementIndexNamesByPattern = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_NAMES_BY_REGEXP);
		}
		return this.preparedStatementIndexNamesByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrences(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesPaged == null) {
				this.preparedStatementIndexOccurrencesPaged = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_OCCURRENCES);
			}
			return this.preparedStatementIndexOccurrencesPaged;
		}
		if (this.preparedStatementIndexOccurrences == null) {
			this.preparedStatementIndexOccurrences = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES);
		}
		return this.preparedStatementIndexOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByDatatype(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByDatatypePaged == null) {
				this.preparedStatementIndexOccurrencesByDatatypePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_DATATYPE);
			}
			return this.preparedStatementIndexOccurrencesByDatatypePaged;
		}
		if (this.preparedStatementIndexOccurrencesByDatatype == null) {
			this.preparedStatementIndexOccurrencesByDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByDateRange(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByDateRangePaged == null) {
				this.preparedStatementIndexOccurrencesByDateRangePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_DATERANGE);
			}
			return this.preparedStatementIndexOccurrencesByDateRangePaged;
		}
		if (this.preparedStatementIndexOccurrencesByDateRange == null) {
			this.preparedStatementIndexOccurrencesByDateRange = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_DATERANGE);
		}
		return this.preparedStatementIndexOccurrencesByDateRange;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByRange(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByRangePaged == null) {
				this.preparedStatementIndexOccurrencesByRangePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_RANGE);
			}
			return this.preparedStatementIndexOccurrencesByRangePaged;
		}
		if (this.preparedStatementIndexOccurrencesByRange == null) {
			this.preparedStatementIndexOccurrencesByRange = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_RANGE);
		}
		return this.preparedStatementIndexOccurrencesByRange;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByValue() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByValue == null) {
			this.preparedStatementIndexOccurrencesByValue = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_VALUE);
		}
		return this.preparedStatementIndexOccurrencesByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByValueAndDatatype(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByValueAndDatatypePaged == null) {
				this.preparedStatementIndexOccurrencesByValueAndDatatypePaged = connection
						.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_VALUE_AND_DATATYPE);
			}
			return this.preparedStatementIndexOccurrencesByValueAndDatatypePaged;
		}
		if (this.preparedStatementIndexOccurrencesByValueAndDatatype == null) {
			this.preparedStatementIndexOccurrencesByValueAndDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_VALUE_AND_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByValueAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByPattern() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByPattern == null) {
			this.preparedStatementIndexOccurrencesByPattern = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_REGEXP);
		}
		return this.preparedStatementIndexOccurrencesByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByPatternAndDatatype() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByPatternAndDatatype == null) {
			this.preparedStatementIndexOccurrencesByPatternAndDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_REGEXP_AND_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByPatternAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariants(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexVariantsPaged == null) {
				this.preparedStatementIndexVariantsPaged = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.Paged.QUERY_SELECT_VARIANTS);
			}
			return this.preparedStatementIndexVariantsPaged;
		}
		if (this.preparedStatementIndexVariants == null) {
			this.preparedStatementIndexVariants = connection.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS);
		}
		return this.preparedStatementIndexVariants;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByDatatype == null) {
			this.preparedStatementIndexVariantsByDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_DATATYPE);
		}
		return this.preparedStatementIndexVariantsByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByValue() throws SQLException {
		if (this.preparedStatementIndexVariantsByValue == null) {
			this.preparedStatementIndexVariantsByValue = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_VALUE);
		}
		return this.preparedStatementIndexVariantsByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByValueAndDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByValueAndDatatype == null) {
			this.preparedStatementIndexVariantsByValueAndDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_VALUE_AND_DATATYPE);
		}
		return this.preparedStatementIndexVariantsByValueAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByPattern() throws SQLException {
		if (this.preparedStatementIndexVariantsByPattern == null) {
			this.preparedStatementIndexVariantsByPattern = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_REGEXP);
		}
		return this.preparedStatementIndexVariantsByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByPatternAndDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByPatternAndDatatype == null) {
			this.preparedStatementIndexVariantsByPatternAndDatatype = connection
					.prepareStatement(ISql99IndexQueries.QueryLiteralIndex.NonPaged.QUERY_SELECT_VARIANTS_BY_REGEXP_AND_DATATYPE);
		}
		return this.preparedStatementIndexVariantsByPatternAndDatatype;
	}

	// IdentityIndex

	private PreparedStatement preparedStatementIndexItemIdentifiers;
	private PreparedStatement preparedStatementIndexSubjectIdentifiers;
	private PreparedStatement preparedStatementIndexSubjectLocators;
	private PreparedStatement preparedStatementIndexConstructsByIdentifier;
	private PreparedStatement preparedStatementIndexConstructsByItemIdentifier;
	private PreparedStatement preparedStatementIndexTopicsBySubjectIdentifier;
	private PreparedStatement preparedStatementIndexTopicsBySubjectLocator;

	private PreparedStatement preparedStatementIndexItemIdentifiersWithLimit;
	private PreparedStatement preparedStatementIndexSubjectIdentifiersWithLimit;
	private PreparedStatement preparedStatementIndexSubjectLocatorsWithLimit;
	private PreparedStatement preparedStatementIndexConstructsByIdentifierWithLimit;
	private PreparedStatement preparedStatementIndexConstructsByItemIdentifierWithLimit;
	private PreparedStatement preparedStatementIndexTopicsBySubjectIdentifierWithLimit;
	private PreparedStatement preparedStatementIndexTopicsBySubjectLocatorWithLimit;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectItemIdentifiers(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexItemIdentifiersWithLimit == null) {
				this.preparedStatementIndexItemIdentifiersWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_ITEM_IDENTIFIERS);
			}
			return this.preparedStatementIndexItemIdentifiersWithLimit;
		}
		if (this.preparedStatementIndexItemIdentifiers == null) {
			this.preparedStatementIndexItemIdentifiers = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_ITEM_IDENTIFIERS);
		}
		return this.preparedStatementIndexItemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubjectIdentifiers(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexSubjectIdentifiersWithLimit == null) {
				this.preparedStatementIndexSubjectIdentifiersWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_SUBJECT_IDENTIFIERS);
			}
			return this.preparedStatementIndexSubjectIdentifiersWithLimit;
		}
		if (this.preparedStatementIndexSubjectIdentifiers == null) {
			this.preparedStatementIndexSubjectIdentifiers = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_SUBJECT_IDENTIFIERS);
		}
		return this.preparedStatementIndexSubjectIdentifiers;
	}

	public PreparedStatement getQuerySelectSubjectLocators(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexSubjectLocatorsWithLimit == null) {
				this.preparedStatementIndexSubjectLocatorsWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_SUBJECT_LOCATORS);
			}
			return this.preparedStatementIndexSubjectLocatorsWithLimit;
		}
		if (this.preparedStatementIndexSubjectLocators == null) {
			this.preparedStatementIndexSubjectLocators = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_SUBJECT_LOCATORS);
		}
		return this.preparedStatementIndexSubjectLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectConstructsByIdentitifer(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexConstructsByIdentifierWithLimit == null) {
				this.preparedStatementIndexConstructsByIdentifierWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN);
			}
			return this.preparedStatementIndexConstructsByIdentifierWithLimit;
		}
		if (this.preparedStatementIndexConstructsByIdentifier == null) {
			this.preparedStatementIndexConstructsByIdentifier = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexConstructsByIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectConstructsByItemIdentitifer(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexConstructsByItemIdentifierWithLimit == null) {
				this.preparedStatementIndexConstructsByItemIdentifierWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN);
			}
			return this.preparedStatementIndexConstructsByItemIdentifierWithLimit;
		}
		if (this.preparedStatementIndexConstructsByItemIdentifier == null) {
			this.preparedStatementIndexConstructsByItemIdentifier = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexConstructsByItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsBySubjectIdentitifer(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexTopicsBySubjectIdentifierWithLimit == null) {
				this.preparedStatementIndexTopicsBySubjectIdentifierWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN);
			}
			return this.preparedStatementIndexTopicsBySubjectIdentifierWithLimit;
		}
		if (this.preparedStatementIndexTopicsBySubjectIdentifier == null) {
			this.preparedStatementIndexTopicsBySubjectIdentifier = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexTopicsBySubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsBySubjectLocator(boolean withLimit) throws SQLException {
		if (withLimit) {
			if (this.preparedStatementIndexTopicsBySubjectLocatorWithLimit == null) {
				this.preparedStatementIndexTopicsBySubjectLocatorWithLimit = connection
						.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.Paged.QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN);
			}
			return this.preparedStatementIndexTopicsBySubjectLocatorWithLimit;
		}
		if (this.preparedStatementIndexTopicsBySubjectLocator == null) {
			this.preparedStatementIndexTopicsBySubjectLocator = connection
					.prepareStatement(ISql99IndexQueries.QueryIdentityIndex.NonPaged.QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN);
		}
		return this.preparedStatementIndexTopicsBySubjectLocator;
	}

	// SupertypeSubtypeIndex

	private PreparedStatement preparedStatementIndexDirectSubtypes;
	private PreparedStatement preparedStatementIndexDirectSubtypesPaged;
	private PreparedStatement preparedStatementIndexTopicsWithoutSubtypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutSubtypesPaged;
	private PreparedStatement preparedStatementIndexSubtypesOfTopic;
	private PreparedStatement preparedStatementIndexSubtypes;
	private PreparedStatement preparedStatementIndexSubtypesPaged;
	private PreparedStatement preparedStatementIndexDirectSupertypes;
	private PreparedStatement preparedStatementIndexDirectSupertypesPaged;
	private PreparedStatement preparedStatementIndexTopicsWithoutSupertypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutSupertypesPaged;
	private PreparedStatement preparedStatementIndexSupertypesOfTopic;
	private PreparedStatement preparedStatementIndexSupertypes;
	private PreparedStatement preparedStatementIndexSupertypesPaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectDirectSubtypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexDirectSubtypesPaged == null) {
				this.preparedStatementIndexDirectSubtypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_DIRECT_SUBTYPES);
			}
			return this.preparedStatementIndexDirectSubtypesPaged;
		}
		if (this.preparedStatementIndexDirectSubtypes == null) {
			this.preparedStatementIndexDirectSubtypes = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_DIRECT_SUBTYPES);
		}
		return this.preparedStatementIndexDirectSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectDirectSupertypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexDirectSupertypesPaged == null) {
				this.preparedStatementIndexDirectSupertypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_DIRECT_SUPERTYPES);
			}
			return this.preparedStatementIndexDirectSupertypesPaged;
		}
		if (this.preparedStatementIndexDirectSupertypes == null) {
			this.preparedStatementIndexDirectSupertypes = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_DIRECT_SUPERTYPES);
		}
		return this.preparedStatementIndexDirectSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSubtypesPaged == null) {
				this.preparedStatementIndexSubtypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUBTYPES);
			}
			return this.preparedStatementIndexSubtypesPaged;
		}
		if (this.preparedStatementIndexSubtypes == null) {
			this.preparedStatementIndexSubtypes = connection.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES);
		}
		return this.preparedStatementIndexSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSupertypesPaged == null) {
				this.preparedStatementIndexSupertypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUPERTYPES);
			}
			return this.preparedStatementIndexSupertypesPaged;
		}
		if (this.preparedStatementIndexSupertypes == null) {
			this.preparedStatementIndexSupertypes = connection.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES);
		}
		return this.preparedStatementIndexSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopic(boolean paged) throws SQLException {
		if (this.preparedStatementIndexSubtypesOfTopic == null) {
			this.preparedStatementIndexSubtypesOfTopic = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSubtypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopics(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL processor implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopic(boolean paged) throws SQLException {
		if (this.preparedStatementIndexSupertypesOfTopic == null) {
			this.preparedStatementIndexSupertypesOfTopic = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSupertypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopics(boolean paged) throws SQLException {
		throw new UnsupportedOperationException("Unsupported by the SQL query builder implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsWithoutSubtypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexTopicsWithoutSubtypesPaged == null) {
				this.preparedStatementIndexTopicsWithoutSubtypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_TOPICS_WITHOUT_SUBTYPES);
			}
			return this.preparedStatementIndexTopicsWithoutSubtypesPaged;
		}
		if (this.preparedStatementIndexTopicsWithoutSubtypes == null) {
			this.preparedStatementIndexTopicsWithoutSubtypes = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_TOPICS_WITHOUT_SUBTYPES);
		}
		return this.preparedStatementIndexTopicsWithoutSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsWithoutSupertypes(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexTopicsWithoutSupertypesPaged == null) {
				this.preparedStatementIndexTopicsWithoutSupertypesPaged = connection
						.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES);
			}
			return this.preparedStatementIndexTopicsWithoutSupertypesPaged;
		}
		if (this.preparedStatementIndexTopicsWithoutSupertypes == null) {
			this.preparedStatementIndexTopicsWithoutSupertypes = connection
					.prepareStatement(ISql99IndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES);
		}
		return this.preparedStatementIndexTopicsWithoutSupertypes;
	}

	// *******************
	// * Utility methods *
	// *******************

	/**
	 * Method creates a prepared statement to query constructs matching by one
	 * or all items in different conditions ( matching all and number of items )
	 * 
	 * @param query
	 *            the base query
	 * @param columnName
	 *            the name of the column to check like this
	 *            <code>column = ?</code>
	 * @param count
	 *            the number of item
	 * @param all
	 *            matching all condition
	 * @return the created statement
	 * @throws SQLException
	 *             thrown if statement cannot created
	 */
	private PreparedStatement createPreparedStatementForMatchingThemes(String query, final String columnName, long count, boolean all) throws SQLException {
		String replacer = all ? "%ARRAY%" : "%SUBQUERY%";
		String placeholder = all ? "?" : (columnName + " = ?");
		String delimer = all ? "," : " OR ";
		/*
		 * create statement
		 */
		String subquery = "";
		for (long n = 0; n < count; n++) {
			subquery += subquery.isEmpty() ? "" : delimer;
			subquery += placeholder;
		}
		return connection.prepareStatement(query.replaceAll(replacer, subquery));
	}

	// *****************
	// * PERFORM QUERY *
	// *****************

	private PreparedStatement preparedStatementPerformMergeTopics;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getPerformMergeTopics() throws SQLException {
		if (preparedStatementPerformMergeTopics == null) {
			preparedStatementPerformMergeTopics = connection.prepareStatement(ISql99UpdateQueries.QueryMerge.QUERY_MERGE_TOPIC);
		}
		return preparedStatementPerformMergeTopics;
	}

	// ********************
	// * CONSTRAINT QUERY *
	// ********************

	private PreparedStatement preparedStatementDuplicateName;
	private PreparedStatement preparedStatementMoveVariants;
	private PreparedStatement preparedStatementMoveItemIdentifiers;
	private PreparedStatement preparedStatementDuplicateOccurrence;
	private PreparedStatement preparedStatementDuplicateVariant;
	private PreparedStatement preparedStatementDuplicateAssociations;
	private PreparedStatement preparedStatementDuplicateRoles;

	public PreparedStatement getQueryDuplicateName() throws SQLException {
		if (this.preparedStatementDuplicateName == null) {
			this.preparedStatementDuplicateName = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_DUPLICATE_NAME);
		}
		return preparedStatementDuplicateName;
	}

	public PreparedStatement getQueryMoveVariants() throws SQLException {
		if (this.preparedStatementMoveVariants == null) {
			this.preparedStatementMoveVariants = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_MOVE_VARIANTS);
		}
		return preparedStatementMoveVariants;
	}

	public PreparedStatement getQueryMoveItemIdentifiers() throws SQLException {
		if (this.preparedStatementMoveItemIdentifiers == null) {
			this.preparedStatementMoveItemIdentifiers = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_MOVE_ITEM_IDENTIFIERS);
		}
		return preparedStatementMoveItemIdentifiers;
	}

	public PreparedStatement getQueryDuplicateOccurrence() throws SQLException {
		if (this.preparedStatementDuplicateOccurrence == null) {
			this.preparedStatementDuplicateOccurrence = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_DUPLICATE_OCCURRENCE);
		}
		return preparedStatementDuplicateOccurrence;
	}

	public PreparedStatement getQueryDuplicateVariant() throws SQLException {
		if (this.preparedStatementDuplicateVariant == null) {
			this.preparedStatementDuplicateVariant = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_DUPLICATE_VARIANTS);
		}
		return preparedStatementDuplicateVariant;
	}

	public PreparedStatement getQueryDuplicateAssociations() throws SQLException {
		if (this.preparedStatementDuplicateAssociations == null) {
			this.preparedStatementDuplicateAssociations = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_DUPLICATE_ASSOCIATIONS);
		}
		return preparedStatementDuplicateAssociations;
	}

	public PreparedStatement getQueryDuplicateRoles() throws SQLException {
		if (this.preparedStatementDuplicateRoles == null) {
			this.preparedStatementDuplicateRoles = connection.prepareStatement(ISql99ConstraintsQueries.QUERY_DUPLICATE_ROLES);
		}
		return preparedStatementDuplicateRoles;
	}

	// ******************
	// * REVISION QUERY *
	// ******************

	private PreparedStatement preparedStatementQueryCreateRevision;
	private PreparedStatement preparedStatementQueryCreateChangeset;
	private PreparedStatement preparedStatementQueryCreateTag;
	private PreparedStatement preparedStatementQueryModifyTag;
	private PreparedStatement preparedStatementQueryCreateMetadata;
	private PreparedStatement preparedStatementQueryModifyMetadata;
	private PreparedStatement preparedStatementQueryReadFirstRevision;
	private PreparedStatement preparedStatementQueryReadLastRevision;
	private PreparedStatement preparedStatementQueryReadPastRevision;
	private PreparedStatement preparedStatementQueryReadFutureRevision;
	private PreparedStatement preparedStatementQueryReadChangesets;
	private PreparedStatement preparedStatementQueryReadTimestamp;
	private PreparedStatement preparedStatementQueryReadRevisionsByTopic;
	private PreparedStatement preparedStatementQueryReadChangesetsByTopic;
	private PreparedStatement preparedStatementQueryReadRevisionsByAssociationType;
	private PreparedStatement preparedStatementQueryReadChangesetsByAssociationType;
	private PreparedStatement preparedStatementQueryReadLastModification;
	private PreparedStatement preparedStatementQueryReadLastModificationOfTopic;
	private PreparedStatement preparedStatementQueryReadRevisionByTag;
	private PreparedStatement preparedStatementQueryReadRevisionByTimestamp;
	private PreparedStatement preparedStatementQueryReadMetadata;
	private PreparedStatement preparedStatementQueryReadMetadataByKey;

	private PreparedStatement preparedStatementQueryAssociationDump;
	private PreparedStatement preparedStatementQueryRoleDump;
	private PreparedStatement preparedStatementQueryVariantDump;
	private PreparedStatement preparedStatementQueryNameDump;
	private PreparedStatement preparedStatementQueryOccurrenceDump;
	private PreparedStatement preparedStatementQueryTopicDump;

	private PreparedStatement preparedStatementQueryReadHistory;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateRevision() throws SQLException {
		if (preparedStatementQueryCreateRevision == null) {
			preparedStatementQueryCreateRevision = connection.prepareStatement(ISql99RevisionQueries.QUERY_CREATE_REVISION, Statement.RETURN_GENERATED_KEYS);
		}
		return preparedStatementQueryCreateRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateChangeset() throws SQLException {
		if (preparedStatementQueryCreateChangeset == null) {
			preparedStatementQueryCreateChangeset = connection.prepareStatement(ISql99RevisionQueries.QUERY_CREATE_CHANGESET);
		}
		return preparedStatementQueryCreateChangeset;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateTag() throws SQLException {
		if (preparedStatementQueryCreateTag == null) {
			preparedStatementQueryCreateTag = connection.prepareStatement(ISql99RevisionQueries.QUERY_CREATE_TAG);
		}
		return preparedStatementQueryCreateTag;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateMetadata() throws SQLException {
		if (preparedStatementQueryCreateMetadata == null) {
			preparedStatementQueryCreateMetadata = connection.prepareStatement(ISql99RevisionQueries.QUERY_CREATE_METADATA);
		}
		return preparedStatementQueryCreateMetadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyTag() throws SQLException {
		if (preparedStatementQueryModifyTag == null) {
			preparedStatementQueryModifyTag = connection.prepareStatement(ISql99RevisionQueries.QUERY_MODIFY_TAG);
		}
		return preparedStatementQueryModifyTag;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyMetadata() throws SQLException {
		if (preparedStatementQueryModifyMetadata == null) {
			preparedStatementQueryModifyMetadata = connection.prepareStatement(ISql99RevisionQueries.QUERY_MODIFY_METADATA);
		}
		return preparedStatementQueryModifyMetadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadFirstRevision() throws SQLException {
		if (preparedStatementQueryReadFirstRevision == null) {
			preparedStatementQueryReadFirstRevision = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_FIRST_REVISION);
		}
		return preparedStatementQueryReadFirstRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadLastRevision() throws SQLException {
		if (preparedStatementQueryReadLastRevision == null) {
			preparedStatementQueryReadLastRevision = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_LAST_REVISION);
		}
		return preparedStatementQueryReadLastRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPastRevision() throws SQLException {
		if (preparedStatementQueryReadPastRevision == null) {
			preparedStatementQueryReadPastRevision = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_PAST_REVISION);
		}
		return preparedStatementQueryReadPastRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadFutureRevision() throws SQLException {
		if (preparedStatementQueryReadFutureRevision == null) {
			preparedStatementQueryReadFutureRevision = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_FUTURE_REVISION);
		}
		return preparedStatementQueryReadFutureRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadChangesets() throws SQLException {
		if (preparedStatementQueryReadChangesets == null) {
			preparedStatementQueryReadChangesets = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_CHANGESET);
		}
		return preparedStatementQueryReadChangesets;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTimestamp() throws SQLException {
		if (preparedStatementQueryReadTimestamp == null) {
			preparedStatementQueryReadTimestamp = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_TIMESTAMP);
		}
		return preparedStatementQueryReadTimestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRevisionsByTopic() throws SQLException {
		if (preparedStatementQueryReadRevisionsByTopic == null) {
			preparedStatementQueryReadRevisionsByTopic = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_REVISIONS_BY_TOPIC);
		}
		return preparedStatementQueryReadRevisionsByTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRevisionsByAssociationType() throws SQLException {
		if (preparedStatementQueryReadRevisionsByAssociationType == null) {
			preparedStatementQueryReadRevisionsByAssociationType = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_REVISIONS_BY_ASSOCIATIONTYPE);
		}
		return preparedStatementQueryReadRevisionsByAssociationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadChangesetsByTopic() throws SQLException {
		if (preparedStatementQueryReadChangesetsByTopic == null) {
			preparedStatementQueryReadChangesetsByTopic = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_CHANGESETS_BY_TOPIC);
		}
		return preparedStatementQueryReadChangesetsByTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadChangesetsByAssociationType() throws SQLException {
		if (preparedStatementQueryReadChangesetsByAssociationType == null) {
			preparedStatementQueryReadChangesetsByAssociationType = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_CHANGESETS_BY_ASSOCIATIONTYPE);
		}
		return preparedStatementQueryReadChangesetsByAssociationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadLastModification() throws SQLException {
		if (preparedStatementQueryReadLastModification == null) {
			preparedStatementQueryReadLastModification = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_LAST_MODIFICATION);
		}
		return preparedStatementQueryReadLastModification;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadLastModificationOfTopic() throws SQLException {
		if (preparedStatementQueryReadLastModificationOfTopic == null) {
			preparedStatementQueryReadLastModificationOfTopic = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_LAST_MODIFICATION_OF_TOPIC);
		}
		return preparedStatementQueryReadLastModificationOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRevisionByTag() throws SQLException {
		if (preparedStatementQueryReadRevisionByTag == null) {
			preparedStatementQueryReadRevisionByTag = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_REVISION_BY_TAG);
		}
		return preparedStatementQueryReadRevisionByTag;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRevisionByTimestamp() throws SQLException {
		if (preparedStatementQueryReadRevisionByTimestamp == null) {
			preparedStatementQueryReadRevisionByTimestamp = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_REVISION_BY_TIMESTAMP);
		}
		return preparedStatementQueryReadRevisionByTimestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadMetadata() throws SQLException {
		if (preparedStatementQueryReadMetadata == null) {
			preparedStatementQueryReadMetadata = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_METADATA);
		}
		return preparedStatementQueryReadMetadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadMetadataByKey() throws SQLException {
		if (preparedStatementQueryReadMetadataByKey == null) {
			preparedStatementQueryReadMetadataByKey = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_METADATA_BY_KEY);
		}
		return preparedStatementQueryReadMetadataByKey;
	}

	public PreparedStatement getQueryRoleDump() throws SQLException {
		if (preparedStatementQueryRoleDump == null) {
			preparedStatementQueryRoleDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_ROLE);
		}
		return preparedStatementQueryRoleDump;
	}

	public PreparedStatement getQueryAssociationDump() throws SQLException {
		if (preparedStatementQueryAssociationDump == null) {
			preparedStatementQueryAssociationDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_ASSOCIATION);
		}
		return preparedStatementQueryAssociationDump;
	}

	public PreparedStatement getQueryVariantDump() throws SQLException {
		if (preparedStatementQueryVariantDump == null) {
			preparedStatementQueryVariantDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_VARIANT);
		}
		return preparedStatementQueryVariantDump;
	}

	public PreparedStatement getQueryNameDump() throws SQLException {
		if (preparedStatementQueryNameDump == null) {
			preparedStatementQueryNameDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_NAME);
		}
		return preparedStatementQueryNameDump;
	}

	public PreparedStatement getQueryOccurrenceDump() throws SQLException {
		if (preparedStatementQueryOccurrenceDump == null) {
			preparedStatementQueryOccurrenceDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_OCCURRENCE);
		}
		return preparedStatementQueryOccurrenceDump;
	}

	public PreparedStatement getQueryTopicDump() throws SQLException {
		if (preparedStatementQueryTopicDump == null) {
			preparedStatementQueryTopicDump = connection.prepareStatement(ISql99DumpQueries.QUERY_DUMP_TOPIC);
		}
		return preparedStatementQueryTopicDump;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadHistory() throws SQLException {
		if (preparedStatementQueryReadHistory == null) {
			preparedStatementQueryReadHistory = connection.prepareStatement(ISql99RevisionQueries.QUERY_READ_HISTORY);
		}
		return preparedStatementQueryReadHistory;
	}

}
