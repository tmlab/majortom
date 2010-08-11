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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlDeleteQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlIndexQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlInsertQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlPerformQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlQueryRevisions;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlSelectQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlUpdateQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryBuilder extends Sql99QueryBuilder {

	/**
	 * the JDBC connection
	 */
	private final Connection connection;

	/**
	 * @param connection
	 *            the JDBC connection to create the {@link PreparedStatement}
	 */
	public PostGreSqlQueryBuilder(Connection connection) {
		super(connection);
		this.connection = connection;
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
			this.preparedStatementCreateAssociation = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_ASSOCIATION,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateAssociationWithScope() throws SQLException {
		if (this.preparedStatementCreateAssociationWithScope == null) {
			this.preparedStatementCreateAssociationWithScope = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_ASSOCIATION_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateLocator() throws SQLException {
		if (this.preparedStatementCreateLocator == null) {
			this.preparedStatementCreateLocator = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_LOCATOR, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateNameWithScope() throws SQLException {
		if (this.preparedStatementCreateNameWithScope == null) {
			this.preparedStatementCreateNameWithScope = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_NAME_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateNameWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateName() throws SQLException {
		if (this.preparedStatementCreateName == null) {
			this.preparedStatementCreateName = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_NAME, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateName;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateOccurrence() throws SQLException {
		if (this.preparedStatementCreateOccurrence == null) {
			this.preparedStatementCreateOccurrence = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_OCCURRENCE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateOccurrence;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateOccurrenceWithScope() throws SQLException {
		if (this.preparedStatementCreateOccurrenceWithScope == null) {
			this.preparedStatementCreateOccurrenceWithScope = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_OCCURRENCE_WITH_SCOPE,
					Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateOccurrenceWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateRole() throws SQLException {
		if (this.preparedStatementCreateRole == null) {
			this.preparedStatementCreateRole = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_ROLE, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateScope() throws SQLException {
		if (this.preparedStatementCreateScope == null) {
			this.preparedStatementCreateScope = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_SCOPE, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateTopicMap() throws SQLException {
		if (this.preparedStatementCreateTopicMap == null) {
			this.preparedStatementCreateTopicMap = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_TOPICMAP, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateTopic() throws SQLException {
		if (this.preparedStatementCreateTopic == null) {
			this.preparedStatementCreateTopic = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_TOPIC, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateVariant() throws SQLException {
		if (this.preparedStatementCreateVariant == null) {
			this.preparedStatementCreateVariant = connection.prepareStatement(IPostGreSqlInsertQueries.QUERY_CREATE_VARIANT, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementCreateVariant;
	}

	// ****************
	// * SELECT QUERY *
	// ****************

	private PreparedStatement preparedStatementReadPlayedAssociation;
	private PreparedStatement preparedStatementReadPlayedAssociationWithType;
	private PreparedStatement preparedStatementReadPlayedAssociationWithTypeAndScope;
	private PreparedStatement preparedStatementReadPlayedAssociationWithScope;
	private PreparedStatement preparedStatementReadAssociation;
	private PreparedStatement preparedStatementReadAssociationWithType;
	private PreparedStatement preparedStatementReadAssociationWithTypeAndScope;
	private PreparedStatement preparedStatementReadAssociationWithScope;
	private PreparedStatement preparedStatementReadTopicById;
	private PreparedStatement preparedStatementReadNameById;
	private PreparedStatement preparedStatementReadOccurrenceById;
	private PreparedStatement preparedStatementReadVariantById;
	private PreparedStatement preparedStatementReadAssociationById;
	private PreparedStatement preparedStatementReadRoleById;
	private PreparedStatement preparedStatementReadConstructByItemIdentifier;
	private PreparedStatement preparedStatementReadDataType;
	private PreparedStatement preparedStatementReadItemIdentifiers;
	private PreparedStatement preparedStatementReadNames;
	private PreparedStatement preparedStatementReadNamesWithType;
	private PreparedStatement preparedStatementReadNamesWithTypeAndScope;
	private PreparedStatement preparedStatementReadNamesWithScope;
	private PreparedStatement preparedStatementReadOccurrences;
	private PreparedStatement preparedStatementReadOccurrencesWithType;
	private PreparedStatement preparedStatementReadOccurrencesWithTypeAndScope;
	private PreparedStatement preparedStatementReadOccurrencesWithScope;
	private PreparedStatement preparedStatementReadPlayer;
	private PreparedStatement preparedStatementReadReifier;
	private PreparedStatement preparedStatementReadReified;
	private PreparedStatement preparedStatementReadRoles;
	private PreparedStatement preparedStatementReadRolesWithType;
	private PreparedStatement preparedStatementReadPlayedRoles;
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
	private PreparedStatement preparedStatementReadScope;
	private PreparedStatement preparedStatementReadScopeByThemes;
	private PreparedStatement preparedStatementReadValue;
	private PreparedStatement preparedStatementReadVariants;
	private PreparedStatement preparedStatementReadVariantsWithScope;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociation() throws SQLException {
		if (this.preparedStatementReadAssociation == null) {
			this.preparedStatementReadAssociation = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS);
		}
		return this.preparedStatementReadAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithScope() throws SQLException {
		if (this.preparedStatementReadAssociationWithScope == null) {
			this.preparedStatementReadAssociationWithScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS_WITH_SCOPE);
		}
		return this.preparedStatementReadAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithType() throws SQLException {
		if (this.preparedStatementReadAssociationWithType == null) {
			this.preparedStatementReadAssociationWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS_WITH_TYPE);
		}
		return this.preparedStatementReadAssociationWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadAssociationWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadAssociationWithTypeAndScope == null) {
			this.preparedStatementReadAssociationWithTypeAndScope = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadAssociationWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadConstructById(Class<? extends IConstruct> clazz) throws SQLException {
		if (ITopic.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadTopicById == null) {
				this.preparedStatementReadTopicById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPIC_BY_ID);
			}
			return this.preparedStatementReadTopicById;
		} else if (IName.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadNameById == null) {
				this.preparedStatementReadNameById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAME_BY_ID);
			}
			return this.preparedStatementReadNameById;
		} else if (IOccurrence.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadOccurrenceById == null) {
				this.preparedStatementReadOccurrenceById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCE_BY_ID);
			}
			return this.preparedStatementReadOccurrenceById;
		} else if (IVariant.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadVariantById == null) {
				this.preparedStatementReadVariantById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_VARIANT_BY_ID);
			}
			return this.preparedStatementReadVariantById;
		} else if (IAssociation.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadAssociationById == null) {
				this.preparedStatementReadAssociationById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATION_BY_ID);
			}
			return this.preparedStatementReadAssociationById;
		} else if (IAssociationRole.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadRoleById == null) {
				this.preparedStatementReadRoleById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLE_BY_ID);
			}
			return this.preparedStatementReadRoleById;
		}
		throw new IllegalArgumentException("Unsupported clazz type " + clazz.getCanonicalName());
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadConstructByItemIdentifier() throws SQLException {
		if (this.preparedStatementReadConstructByItemIdentifier == null) {
			this.preparedStatementReadConstructByItemIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER);
		}
		return this.preparedStatementReadConstructByItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadDataType() throws SQLException {
		if (this.preparedStatementReadDataType == null) {
			this.preparedStatementReadDataType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_DATATYPE);
		}
		return this.preparedStatementReadDataType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadItemIdentifiers() throws SQLException {
		if (this.preparedStatementReadItemIdentifiers == null) {
			this.preparedStatementReadItemIdentifiers = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ITEM_IDENTIFIERS);
		}
		return this.preparedStatementReadItemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNames(boolean paged) throws SQLException {
		if (this.preparedStatementReadNames == null) {
			this.preparedStatementReadNames = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES);
		}
		return this.preparedStatementReadNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithScope() throws SQLException {
		if (this.preparedStatementReadNamesWithScope == null) {
			this.preparedStatementReadNamesWithScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES_WITH_SCOPE);
		}
		return this.preparedStatementReadNamesWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithType() throws SQLException {
		if (this.preparedStatementReadNamesWithType == null) {
			this.preparedStatementReadNamesWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES_WITH_TYPE);
		}
		return this.preparedStatementReadNamesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadNamesWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadNamesWithTypeAndScope == null) {
			this.preparedStatementReadNamesWithTypeAndScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadNamesWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrences(boolean paged) throws SQLException {
		if (this.preparedStatementReadOccurrences == null) {
			this.preparedStatementReadOccurrences = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES);
		}
		return this.preparedStatementReadOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithScope() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithScope == null) {
			this.preparedStatementReadOccurrencesWithScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES_WITH_SCOPE);
		}
		return this.preparedStatementReadOccurrencesWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithType() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithType == null) {
			this.preparedStatementReadOccurrencesWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES_WITH_TYPE);
		}
		return this.preparedStatementReadOccurrencesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadOccurrencesWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadOccurrencesWithTypeAndScope == null) {
			this.preparedStatementReadOccurrencesWithTypeAndScope = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadOccurrencesWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociation(boolean paged) throws SQLException {
		if (this.preparedStatementReadPlayedAssociation == null) {
			this.preparedStatementReadPlayedAssociation = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS);
		}
		return this.preparedStatementReadPlayedAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithScope() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithScope == null) {
			this.preparedStatementReadPlayedAssociationWithScope = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_SCOPE);
		}
		return this.preparedStatementReadPlayedAssociationWithScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithType() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithType == null) {
			this.preparedStatementReadPlayedAssociationWithType = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE);
		}
		return this.preparedStatementReadPlayedAssociationWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedAssociationWithTypeAndScope() throws SQLException {
		if (this.preparedStatementReadPlayedAssociationWithTypeAndScope == null) {
			this.preparedStatementReadPlayedAssociationWithTypeAndScope = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
		}
		return this.preparedStatementReadPlayedAssociationWithTypeAndScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRoles(boolean paged) throws SQLException {
		if (this.preparedStatementReadPlayedRoles == null) {
			this.preparedStatementReadPlayedRoles = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ROLES);
		}
		return this.preparedStatementReadPlayedRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRolesWithType() throws SQLException {
		if (this.preparedStatementReadPlayedRolesWithType == null) {
			this.preparedStatementReadPlayedRolesWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ROLES_WITH_TYPE);
		}
		return this.preparedStatementReadPlayedRolesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayedRolesWithTypeAndAssociationType() throws SQLException {
		if (this.preparedStatementReadPlayedRolesWithTypeAndAssociationType == null) {
			this.preparedStatementReadPlayedRolesWithTypeAndAssociationType = connection
					.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE);
		}
		return this.preparedStatementReadPlayedRolesWithTypeAndAssociationType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadPlayer() throws SQLException {
		if (this.preparedStatementReadPlayer == null) {
			this.preparedStatementReadPlayer = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYER);
		}
		return this.preparedStatementReadPlayer;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadReified() throws SQLException {
		if (this.preparedStatementReadReified == null) {
			this.preparedStatementReadReified = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_REIFIED);
		}
		return this.preparedStatementReadReified;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadReifier() throws SQLException {
		if (this.preparedStatementReadReifier == null) {
			this.preparedStatementReadReifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_REIFIER);
		}
		return this.preparedStatementReadReifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRoleTypes() throws SQLException {
		if (this.preparedStatementReadRoleTypes == null) {
			this.preparedStatementReadRoleTypes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLESTYPES);
		}
		return this.preparedStatementReadRoleTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRoles(boolean paged) throws SQLException {
		if (this.preparedStatementReadRoles == null) {
			this.preparedStatementReadRoles = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLES);
		}
		return this.preparedStatementReadRoles;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadRolesWithType() throws SQLException {
		if (this.preparedStatementReadRolesWithType == null) {
			this.preparedStatementReadRolesWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLES_WITH_TYPE);
		}
		return this.preparedStatementReadRolesWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadScope() throws SQLException {
		if (this.preparedStatementReadScope == null) {
			this.preparedStatementReadScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SCOPE);
		}
		return this.preparedStatementReadScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadScopeByThemes() throws SQLException {
		if (preparedStatementReadScopeByThemes == null) {
			this.preparedStatementReadScopeByThemes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SCOPES_BY_THEMES);
		}
		return preparedStatementReadScopeByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSubjectIdentifiers() throws SQLException {
		if (this.preparedStatementReadSubjectIdentifiers == null) {
			this.preparedStatementReadSubjectIdentifiers = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SUBJECT_IDENTIFIERS);
		}
		return this.preparedStatementReadSubjectIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSubjectLocators() throws SQLException {
		if (this.preparedStatementReadSubjectLocators == null) {
			this.preparedStatementReadSubjectLocators = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SUBJECT_LOCATORS);
		}
		return this.preparedStatementReadSubjectLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSupertypes() throws SQLException {
		if (this.preparedStatementReadSupertypes == null) {
			this.preparedStatementReadSupertypes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SUPERTYPES);
		}
		return this.preparedStatementReadSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadThemes() throws SQLException {
		if (this.preparedStatementReadThemes == null) {
			this.preparedStatementReadThemes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_THEMES);
		}
		return this.preparedStatementReadThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicBySubjectIdentifier() throws SQLException {
		if (this.preparedStatementReadTopicBySubjectIdentifier == null) {
			this.preparedStatementReadTopicBySubjectIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPIC_BY_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementReadTopicBySubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicBySubjectLocator() throws SQLException {
		if (this.preparedStatementReadTopicBySubjectLocator == null) {
			this.preparedStatementReadTopicBySubjectLocator = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPIC_BY_SUBJECT_LOCATOR);
		}
		return this.preparedStatementReadTopicBySubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicMap() throws SQLException {
		if (this.preparedStatementReadTopicMap == null) {
			this.preparedStatementReadTopicMap = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPICMAP);
		}
		return this.preparedStatementReadTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopics() throws SQLException {
		if (this.preparedStatementReadTopics == null) {
			this.preparedStatementReadTopics = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPICS);
		}
		return this.preparedStatementReadTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTopicsWithType() throws SQLException {
		if (this.preparedStatementReadTopicsWithType == null) {
			this.preparedStatementReadTopicsWithType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPICS_WITH_TYPE);
		}
		return this.preparedStatementReadTopicsWithType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadType() throws SQLException {
		if (this.preparedStatementReadType == null) {
			this.preparedStatementReadType = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TYPE);
		}
		return this.preparedStatementReadType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadTypes(boolean paged) throws SQLException {
		if (this.preparedStatementReadTypes == null) {
			this.preparedStatementReadTypes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TYPES);
		}
		return this.preparedStatementReadTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadValue() throws SQLException {
		if (this.preparedStatementReadValue == null) {
			this.preparedStatementReadValue = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_VALUE);
		}
		return this.preparedStatementReadValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadVariants(boolean paged) throws SQLException {
		if (this.preparedStatementReadVariants == null) {
			this.preparedStatementReadVariants = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_VARIANTS);
		}
		return this.preparedStatementReadVariants;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadVariantsWithScope() throws SQLException {
		if (this.preparedStatementReadVariantsWithScope == null) {
			this.preparedStatementReadVariantsWithScope = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_VARIANTS_WITH_SCOPE);
		}
		return this.preparedStatementReadVariantsWithScope;
	}

	// ***************
	// * MERGE QUERY *
	// ***************

	// private PreparedStatement preparedStatementMergeTopics;

	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryMergeTopic() throws SQLException {
	// if (preparedStatementMergeTopics == null) {
	// preparedStatementMergeTopics =
	// connection.prepareStatement(IPostGreSqlUpdateQueries.QueryMerge.QUERY_MERGE_TOPIC);
	// }
	// return preparedStatementMergeTopics;
	// }

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
			this.preparedStatementAddItemIdentifier = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_ADD_ITEM_IDENTIFIER);
		}
		return this.preparedStatementAddItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddSubjectIdentifier() throws SQLException {
		if (this.preparedStatementAddSubjectIdentifier == null) {
			this.preparedStatementAddSubjectIdentifier = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_ADD_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementAddSubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddSubjectLocator() throws SQLException {
		if (this.preparedStatementAddSubjectLocator == null) {
			this.preparedStatementAddSubjectLocator = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_ADD_SUBJECT_LOCATOR);
		}
		return this.preparedStatementAddSubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAddThemes(long themeNumber) throws SQLException {
		String query = "";
		for (long n = 0; n < themeNumber; n++) {
			query += IPostGreSqlUpdateQueries.QUERY_ADD_THEME;
		}
		return connection.prepareStatement(query);
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyType() throws SQLException {
		if (this.preparedStatementModifyType == null) {
			this.preparedStatementModifyType = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_TYPE);
		}
		return this.preparedStatementModifyType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyTypes() throws SQLException {
		if (this.preparedStatementModifyTypes == null) {
			this.preparedStatementModifyTypes = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_TYPES);
		}
		return this.preparedStatementModifyTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyPlayer() throws SQLException {
		if (this.preparedStatementModifyPlayer == null) {
			this.preparedStatementModifyPlayer = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_PLAYER);
		}
		return this.preparedStatementModifyPlayer;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyReifier() throws SQLException {
		if (this.preparedStatementModifyReifier == null) {
			this.preparedStatementModifyReifier = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_REIFIER, Statement.RETURN_GENERATED_KEYS);
		}
		return this.preparedStatementModifyReifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyScope() throws SQLException {
		if (this.preparedStatementModifyScope == null) {
			this.preparedStatementModifyScope = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_SCOPE);
		}
		return this.preparedStatementModifyScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifySupertypes() throws SQLException {
		if (this.preparedStatementModifySupertypes == null) {
			this.preparedStatementModifySupertypes = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_SUPERTYPES);
		}
		return this.preparedStatementModifySupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyValue() throws SQLException {
		if (this.preparedStatementModifyValue == null) {
			this.preparedStatementModifyValue = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_VALUE);
		}
		return this.preparedStatementModifyValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryModifyValueWithDatatype() throws SQLException {
		if (this.preparedStatementModifyValueWithDatatype == null) {
			this.preparedStatementModifyValueWithDatatype = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_VALUE_WITH_DATATYPE);
		}
		return this.preparedStatementModifyValueWithDatatype;
	}

	// ****************
	// * REMOVE QUERY *
	// ****************

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
			this.preparedStatementDeleteTopicMap = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_TOPICMAP);
		}
		return this.preparedStatementDeleteTopicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteAssociation() throws SQLException {
		if (this.preparedStatementDeleteAssociation == null) {
			this.preparedStatementDeleteAssociation = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_ASSOCIATION);
		}
		return this.preparedStatementDeleteAssociation;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteItemIdentifier() throws SQLException {
		if (this.preparedStatementDeleteItemIdentifier == null) {
			this.preparedStatementDeleteItemIdentifier = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_ITEM_IDENTIFIER);
		}
		return this.preparedStatementDeleteItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteName() throws SQLException {
		if (this.preparedStatementDeleteName == null) {
			this.preparedStatementDeleteName = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_NAME);
		}
		return this.preparedStatementDeleteName;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteOccurrence() throws SQLException {
		if (this.preparedStatementDeleteOccurrence == null) {
			this.preparedStatementDeleteOccurrence = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_OCCURRENCE);
		}
		return this.preparedStatementDeleteOccurrence;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteRole() throws SQLException {
		if (this.preparedStatementDeleteRole == null) {
			this.preparedStatementDeleteRole = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_ROLE);
		}
		return this.preparedStatementDeleteRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSubjectIdentifier() throws SQLException {
		if (this.preparedStatementDeleteSubjectIdentifier == null) {
			this.preparedStatementDeleteSubjectIdentifier = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_SUBJECT_IDENTIFIER);
		}
		return this.preparedStatementDeleteSubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSubjectLocator() throws SQLException {
		if (this.preparedStatementDeleteSubjectLocator == null) {
			this.preparedStatementDeleteSubjectLocator = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_SUBJECT_LOCATOR);
		}
		return this.preparedStatementDeleteSubjectLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteSupertype() throws SQLException {
		if (this.preparedStatementDeleteSupertype == null) {
			this.preparedStatementDeleteSupertype = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_SUPERTYPE);
		}
		return this.preparedStatementDeleteSupertype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteTopic() throws SQLException {
		if (this.preparedStatementDeleteTopic == null) {
			this.preparedStatementDeleteTopic = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_TOPIC);
		}
		return this.preparedStatementDeleteTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteType() throws SQLException {
		if (this.preparedStatementDeleteType == null) {
			this.preparedStatementDeleteType = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_TYPE);
		}
		return this.preparedStatementDeleteType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteVariant() throws SQLException {
		if (this.preparedStatementDeleteVariant == null) {
			this.preparedStatementDeleteVariant = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_VARIANT);
		}
		return this.preparedStatementDeleteVariant;
	}

	// ***************
	// * INDEX QUERY *
	// ***************

	// TypeInstanceIndex

	private PreparedStatement preparedStatementIndexAssociationTypes;
	private PreparedStatement preparedStatementIndexNameTypes;
	private PreparedStatement preparedStatementIndexOccurrenceTypes;
	private PreparedStatement preparedStatementIndexRoleTypes;
	private PreparedStatement preparedStatementIndexTopicTypes;
	private PreparedStatement preparedStatementIndexAssociationsByType;
	private PreparedStatement preparedStatementIndexRolesByType;
	private PreparedStatement preparedStatementIndexNamesByType;
	private PreparedStatement preparedStatementIndexOccurrencesByType;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementsIndexTopicsByTypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutType;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationTypes() throws SQLException {
		if (this.preparedStatementIndexAssociationTypes == null) {
			this.preparedStatementIndexAssociationTypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ASSOCIATIONTYPES);
		}
		return this.preparedStatementIndexAssociationTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNameTypes() throws SQLException {
		if (this.preparedStatementIndexNameTypes == null) {
			this.preparedStatementIndexNameTypes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_NAMETYPES);
		}
		return this.preparedStatementIndexNameTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrenceTypes() throws SQLException {
		if (this.preparedStatementIndexOccurrenceTypes == null) {
			this.preparedStatementIndexOccurrenceTypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_OCCURRENCETYPES);
		}
		return this.preparedStatementIndexOccurrenceTypes;
	}

	public PreparedStatement getQuerySelectRoleTypes() throws SQLException {
		if (this.preparedStatementIndexRoleTypes == null) {
			this.preparedStatementIndexRoleTypes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ROLETYPES);
		}
		return this.preparedStatementIndexRoleTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicTypes() throws SQLException {
		if (this.preparedStatementIndexTopicTypes == null) {
			this.preparedStatementIndexTopicTypes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPICTYPES);
		}
		return this.preparedStatementIndexTopicTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByType() throws SQLException {
		if (this.preparedStatementIndexAssociationsByType == null) {
			this.preparedStatementIndexAssociationsByType = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
		}
		return this.preparedStatementIndexAssociationsByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByType() throws SQLException {
		if (this.preparedStatementIndexRolesByType == null) {
			this.preparedStatementIndexRolesByType = connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ROLES_BY_TYPE);
		}
		return this.preparedStatementIndexRolesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByType() throws SQLException {
		if (this.preparedStatementIndexNamesByType == null) {
			this.preparedStatementIndexNamesByType = connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_NAMES_BY_TYPE);
		}
		return this.preparedStatementIndexNamesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByType() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByType == null) {
			this.preparedStatementIndexOccurrencesByType = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_OCCURRENCES_BY_TYPE);
		}
		return this.preparedStatementIndexOccurrencesByType;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypes(long typeCount, boolean all) throws SQLException {
		if (typeCount == 0) {
			if (this.preparedStatementIndexTopicsWithoutType == null) {
				this.preparedStatementIndexTopicsWithoutType = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_WITHOUT_TYPE);
			}
			return this.preparedStatementIndexTopicsWithoutType;
		}

		/*
		 * check if cache is initialized
		 */
		if (preparedStatementsIndexTopicsByTypes == null) {
			preparedStatementsIndexTopicsByTypes = HashUtil.getHashMap();
		}
		/*
		 * check if binding exists
		 */
		Map<Long, PreparedStatement> map = preparedStatementsIndexTopicsByTypes.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			preparedStatementsIndexTopicsByTypes.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(typeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(all ? IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL
					: IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_BY_TYPES, "id_type", typeCount, all);
			map.put(typeCount, stmt);
		}
		return stmt;
	}

	// TransitiveTypeInstanceIndex

	private PreparedStatement preparedStatementIndexAssociationsByTypeTransitive;
	private PreparedStatement preparedStatementIndexRolesByTypeTransitive;
	private PreparedStatement preparedStatementIndexNamesByTypeTransitive;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypesTransitive;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByTypeTransitive() throws SQLException {
		if (this.preparedStatementIndexAssociationsByTypeTransitive == null) {
			this.preparedStatementIndexAssociationsByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
		}
		return this.preparedStatementIndexAssociationsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByTypeTransitive() throws SQLException {
		if (this.preparedStatementIndexRolesByTypeTransitive == null) {
			this.preparedStatementIndexRolesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_ROLES_BY_TYPE);
		}
		return this.preparedStatementIndexRolesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByTypeTransitive() throws SQLException {
		if (this.preparedStatementIndexNamesByTypeTransitive == null) {
			this.preparedStatementIndexNamesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_NAMES_BY_TYPE);
		}
		return this.preparedStatementIndexNamesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByTypeTransitive() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByTypeTransitive == null) {
			this.preparedStatementIndexOccurrencesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_OCCURRENCES_BY_TYPE);
		}
		return this.preparedStatementIndexOccurrencesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypeTransitive() throws SQLException {
		if (this.preparedStatementIndexTopicsByTypeTransitive == null) {
			this.preparedStatementIndexTopicsByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_TOPICS_BY_TYPE);
		}
		return this.preparedStatementIndexTopicsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypesTransitive() throws SQLException {
		if (this.preparedStatementIndexTopicsByTypesTransitive == null) {
			this.preparedStatementIndexTopicsByTypesTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.QUERY_SELECT_TOPICS_BY_TYPES);
		}
		return this.preparedStatementIndexTopicsByTypesTransitive;
	}

	// ScopeIndex
	private PreparedStatement preparedStatementIndexScopesByThemesUsed;
	private PreparedStatement preparedStatementIndexAssociationsByEmptyScope;
	private PreparedStatement preparedStatementIndexAssociationsByScope;
	private Map<Long, PreparedStatement> preparedStatementIndexAssociationsByScopes;
	private PreparedStatement preparedStatementIndexAssociationsByTheme;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementIndexAssociationsByThemes;
	private PreparedStatement preparedStatementIndexAssociationScopes;
	private PreparedStatement preparedStatementIndexAssociationThemes;
	private PreparedStatement preparedStatementIndexNamesByEmptyScope;
	private PreparedStatement preparedStatementIndexNamesByScope;
	private Map<Long, PreparedStatement> preparedStatementIndexNamesByScopes;
	private PreparedStatement preparedStatementIndexNamesByTheme;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementIndexNamesByThemes;
	private PreparedStatement preparedStatementIndexNameScopes;
	private PreparedStatement preparedStatementIndexNameThemes;
	private PreparedStatement preparedStatementIndexOccurrencesByEmptyScope;
	private PreparedStatement preparedStatementIndexOccurrencesByScope;
	private Map<Long, PreparedStatement> preparedStatementIndexOccurrencesByScopes;
	private PreparedStatement preparedStatementIndexOccurrencesByTheme;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementIndexOccurrencesByThemes;
	private PreparedStatement preparedStatementIndexOccurrenceScopes;
	private PreparedStatement preparedStatementIndexOccurrenceThemes;
	private PreparedStatement preparedStatementIndexVariantsByScope;
	private PreparedStatement preparedStatementIndexVariantsByScopes;
	private PreparedStatement preparedStatementIndexVariantsByTheme;
	private Map<Boolean, Map<Long, PreparedStatement>> preparedStatementIndexVariantsByThemes;
	private PreparedStatement preparedStatementIndexVariantScopes;
	private PreparedStatement preparedStatementIndexVariantThemes;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryScopesByThemesUsed() throws SQLException {
		if (this.preparedStatementIndexScopesByThemesUsed == null) {
			preparedStatementIndexScopesByThemesUsed = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_SCOPES_BY_THEMES_USED);
		}
		return preparedStatementIndexScopesByThemesUsed;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByScope(boolean emptyScope) throws SQLException {
		/*
		 * is empty scope
		 */
		if (emptyScope) {
			if (this.preparedStatementIndexAssociationsByEmptyScope == null) {
				preparedStatementIndexAssociationsByEmptyScope = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_EMPTYSCOPE);
			}
			return preparedStatementIndexAssociationsByEmptyScope;
		}
		/*
		 * is non-empty scope
		 */
		if (this.preparedStatementIndexAssociationsByScope == null) {
			this.preparedStatementIndexAssociationsByScope = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_SCOPE);
		}
		return this.preparedStatementIndexAssociationsByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByScopes(long scopeCount) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexAssociationsByScopes == null) {
			preparedStatementIndexAssociationsByScopes = HashUtil.getHashMap();
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = preparedStatementIndexAssociationsByScopes.get(scopeCount);
		if (stmt == null) {
			/*
			 * create statement
			 */
			String subquery = "";
			for (long n = 0; n < scopeCount; n++) {
				subquery += subquery.isEmpty() ? "" : " OR ";
				subquery += "id_scope = ?";
			}
			stmt = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_SCOPES + " " + subquery);
			preparedStatementIndexAssociationsByScopes.put(scopeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByTheme() throws SQLException {
		if (this.preparedStatementIndexAssociationsByTheme == null) {
			this.preparedStatementIndexAssociationsByTheme = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEME);
		}
		return this.preparedStatementIndexAssociationsByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationsByThemes(long themeCount, boolean all) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexAssociationsByThemes == null) {
			preparedStatementIndexAssociationsByThemes = HashUtil.getHashMap();
		}
		/*
		 * check if binding exists
		 */
		Map<Long, PreparedStatement> map = preparedStatementIndexAssociationsByThemes.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			preparedStatementIndexAssociationsByThemes.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(themeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(all ? IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEMES_MATCH_ALL
					: IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEMES, "id_theme", themeCount, all);
			map.put(themeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationScopes() throws SQLException {
		if (this.preparedStatementIndexAssociationScopes == null) {
			this.preparedStatementIndexAssociationScopes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATION_SCOPES);
		}
		return this.preparedStatementIndexAssociationScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryAssociationThemes() throws SQLException {
		if (this.preparedStatementIndexAssociationThemes == null) {
			this.preparedStatementIndexAssociationThemes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATION_THEMES);
		}
		return this.preparedStatementIndexAssociationThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByScope(boolean emptyScope) throws SQLException {
		/*
		 * is empty scope
		 */
		if (emptyScope) {
			if (this.preparedStatementIndexNamesByEmptyScope == null) {
				preparedStatementIndexNamesByEmptyScope = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_EMPTYSCOPE);
			}
			return preparedStatementIndexNamesByEmptyScope;
		}
		/*
		 * is non-empty scope
		 */
		if (this.preparedStatementIndexNamesByScope == null) {
			this.preparedStatementIndexNamesByScope = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_SCOPE);
		}
		return this.preparedStatementIndexNamesByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByScopes(long scopeCount) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexNamesByScopes == null) {
			preparedStatementIndexNamesByScopes = HashUtil.getHashMap();
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = preparedStatementIndexNamesByScopes.get(scopeCount);
		if (stmt == null) {
			/*
			 * create statement
			 */
			String subquery = "";
			for (long n = 0; n < scopeCount; n++) {
				subquery += subquery.isEmpty() ? "" : " OR ";
				subquery += "id_scope = ?";
			}
			stmt = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_SCOPES + " " + subquery);
			preparedStatementIndexNamesByScopes.put(scopeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByTheme() throws SQLException {
		if (this.preparedStatementIndexNamesByTheme == null) {
			this.preparedStatementIndexNamesByTheme = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEME);
		}
		return this.preparedStatementIndexNamesByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNamesByThemes(long themeCount, boolean all) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexNamesByThemes == null) {
			preparedStatementIndexNamesByThemes = HashUtil.getHashMap();
		}
		/*
		 * check if binding exists
		 */
		Map<Long, PreparedStatement> map = preparedStatementIndexNamesByThemes.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			preparedStatementIndexNamesByThemes.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(themeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(all ? IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEMES_MATCH_ALL
					: IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEMES, "id_theme", themeCount, all);
			map.put(themeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNameScopes() throws SQLException {
		if (this.preparedStatementIndexNameScopes == null) {
			this.preparedStatementIndexNameScopes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAME_SCOPES);
		}
		return this.preparedStatementIndexNameScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryNameThemes() throws SQLException {
		if (this.preparedStatementIndexNameThemes == null) {
			this.preparedStatementIndexNameThemes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAME_THEMES);
		}
		return this.preparedStatementIndexNameThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByScope(boolean emptyScope) throws SQLException {
		/*
		 * is empty scope
		 */
		if (emptyScope) {
			if (this.preparedStatementIndexOccurrencesByEmptyScope == null) {
				preparedStatementIndexOccurrencesByEmptyScope = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_EMPTYSCOPE);
			}
			return preparedStatementIndexOccurrencesByEmptyScope;
		}
		/*
		 * is non-empty scope
		 */
		if (this.preparedStatementIndexOccurrencesByScope == null) {
			this.preparedStatementIndexOccurrencesByScope = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_SCOPE);
		}
		return this.preparedStatementIndexOccurrencesByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByScopes(long scopeCount) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexOccurrencesByScopes == null) {
			preparedStatementIndexOccurrencesByScopes = HashUtil.getHashMap();
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = preparedStatementIndexOccurrencesByScopes.get(scopeCount);
		if (stmt == null) {
			/*
			 * create statement
			 */
			String subquery = "";
			for (long n = 0; n < scopeCount; n++) {
				subquery += subquery.isEmpty() ? "" : " OR ";
				subquery += "id_scope = ?";
			}
			stmt = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_SCOPES + " " + subquery);
			preparedStatementIndexOccurrencesByScopes.put(scopeCount, stmt);
		}
		return stmt;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByTheme() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByTheme == null) {
			this.preparedStatementIndexOccurrencesByTheme = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEME);
		}
		return this.preparedStatementIndexOccurrencesByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrencesByThemes(long themeCount, boolean all) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexOccurrencesByThemes == null) {
			preparedStatementIndexOccurrencesByThemes = HashUtil.getHashMap();
		}
		/*
		 * check if binding exists
		 */
		Map<Long, PreparedStatement> map = preparedStatementIndexOccurrencesByThemes.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			preparedStatementIndexOccurrencesByThemes.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(themeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(all ? IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEMES_MATCH_ALL
					: IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEMES, "id_theme", themeCount, all);
			map.put(themeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrenceScopes() throws SQLException {
		if (this.preparedStatementIndexOccurrenceScopes == null) {
			this.preparedStatementIndexOccurrenceScopes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCE_SCOPES);
		}
		return this.preparedStatementIndexOccurrenceScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryOccurrenceThemes() throws SQLException {
		if (this.preparedStatementIndexOccurrenceThemes == null) {
			this.preparedStatementIndexOccurrenceThemes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCE_THEMES);
		}
		return this.preparedStatementIndexOccurrenceThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByScope() throws SQLException {
		if (this.preparedStatementIndexVariantsByScope == null) {
			this.preparedStatementIndexVariantsByScope = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_SCOPE);
		}
		return this.preparedStatementIndexVariantsByScope;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByScopes(long scopeCount) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexVariantsByScopes == null) {
			preparedStatementIndexVariantsByScopes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_SCOPES);
		}
		return preparedStatementIndexVariantsByScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByTheme() throws SQLException {
		if (this.preparedStatementIndexVariantsByTheme == null) {
			this.preparedStatementIndexVariantsByTheme = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEME);
		}
		return this.preparedStatementIndexVariantsByTheme;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantsByThemes(long themeCount, boolean all) throws SQLException {
		/*
		 * check if cache is initialized
		 */
		if (preparedStatementIndexVariantsByThemes == null) {
			preparedStatementIndexVariantsByThemes = HashUtil.getHashMap();
		}
		/*
		 * check if binding exists
		 */
		Map<Long, PreparedStatement> map = preparedStatementIndexVariantsByThemes.get(all);
		if (map == null) {
			map = HashUtil.getHashMap();
			preparedStatementIndexVariantsByThemes.put(all, map);
		}
		/*
		 * check if statement exists
		 */
		PreparedStatement stmt = map.get(themeCount);
		if (stmt == null) {
			stmt = createPreparedStatementForMatchingThemes(all ? IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEMES_MATCH_ALL
					: IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEMES, "id_theme", themeCount, all);
			map.put(themeCount, stmt);
		}
		return stmt;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantScopes() throws SQLException {
		if (this.preparedStatementIndexVariantScopes == null) {
			this.preparedStatementIndexVariantScopes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANT_SCOPES);
		}
		return this.preparedStatementIndexVariantScopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryVariantThemes() throws SQLException {
		if (this.preparedStatementIndexVariantThemes == null) {
			this.preparedStatementIndexVariantThemes = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANT_THEMES);
		}
		return this.preparedStatementIndexVariantThemes;
	}

	// LiteralIndex

	private PreparedStatement preparedStatementIndexNames;
	private PreparedStatement preparedStatementIndexNamesByValue;
	private PreparedStatement preparedStatementIndexNamesByPattern;
	private PreparedStatement preparedStatementIndexOccurrences;
	private PreparedStatement preparedStatementIndexOccurrencesByDatatype;
	private PreparedStatement preparedStatementIndexOccurrencesByDateRange;
	private PreparedStatement preparedStatementIndexOccurrencesByRange;
	private PreparedStatement preparedStatementIndexOccurrencesByValue;
	private PreparedStatement preparedStatementIndexOccurrencesByValueAndDatatype;
	private PreparedStatement preparedStatementIndexOccurrencesByPattern;
	private PreparedStatement preparedStatementIndexOccurrencesByPatternAndDatatype;
	private PreparedStatement preparedStatementIndexVariants;
	private PreparedStatement preparedStatementIndexVariantsByDatatype;
	private PreparedStatement preparedStatementIndexVariantsByValue;
	private PreparedStatement preparedStatementIndexVariantsByValueAndDatatype;
	private PreparedStatement preparedStatementIndexVariantsByPattern;
	private PreparedStatement preparedStatementIndexVariantsByPatternAndDatatype;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNames() throws SQLException {
		if (this.preparedStatementIndexNames == null) {
			this.preparedStatementIndexNames = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES);
		}
		return this.preparedStatementIndexNames;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByValue() throws SQLException {
		if (this.preparedStatementIndexNamesByValue == null) {
			this.preparedStatementIndexNamesByValue = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES_BY_VALUE);
		}
		return this.preparedStatementIndexNamesByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByPattern() throws SQLException {
		if (this.preparedStatementIndexNamesByPattern == null) {
			this.preparedStatementIndexNamesByPattern = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES_BY_REGEXP);
		}
		return this.preparedStatementIndexNamesByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrences() throws SQLException {
		if (this.preparedStatementIndexOccurrences == null) {
			this.preparedStatementIndexOccurrences = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES);
		}
		return this.preparedStatementIndexOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByDatatype() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByDatatype == null) {
			this.preparedStatementIndexOccurrencesByDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByDateRange() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByDateRange == null) {
			this.preparedStatementIndexOccurrencesByDateRange = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_DATERANGE);
		}
		return this.preparedStatementIndexOccurrencesByDateRange;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByRange() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByRange == null) {
			this.preparedStatementIndexOccurrencesByRange = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_RANGE);
		}
		return this.preparedStatementIndexOccurrencesByRange;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByValue() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByValue == null) {
			this.preparedStatementIndexOccurrencesByValue = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_VALUE);
		}
		return this.preparedStatementIndexOccurrencesByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByValueAndDatatype() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByValueAndDatatype == null) {
			this.preparedStatementIndexOccurrencesByValueAndDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_VALUE_AND_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByValueAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByPattern() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByPattern == null) {
			this.preparedStatementIndexOccurrencesByPattern = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_REGEXP);
		}
		return this.preparedStatementIndexOccurrencesByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByPatternAndDatatype() throws SQLException {
		if (this.preparedStatementIndexOccurrencesByPatternAndDatatype == null) {
			this.preparedStatementIndexOccurrencesByPatternAndDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_REGEXP_AND_DATATYPE);
		}
		return this.preparedStatementIndexOccurrencesByPatternAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariants() throws SQLException {
		if (this.preparedStatementIndexVariants == null) {
			this.preparedStatementIndexVariants = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS);
		}
		return this.preparedStatementIndexVariants;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByDatatype == null) {
			this.preparedStatementIndexVariantsByDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_DATATYPE);
		}
		return this.preparedStatementIndexVariantsByDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByValue() throws SQLException {
		if (this.preparedStatementIndexVariantsByValue == null) {
			this.preparedStatementIndexVariantsByValue = connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_VALUE);
		}
		return this.preparedStatementIndexVariantsByValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByValueAndDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByValueAndDatatype == null) {
			this.preparedStatementIndexVariantsByValueAndDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_VALUE_AND_DATATYPE);
		}
		return this.preparedStatementIndexVariantsByValueAndDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByPattern() throws SQLException {
		if (this.preparedStatementIndexVariantsByPattern == null) {
			this.preparedStatementIndexVariantsByPattern = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_REGEXP);
		}
		return this.preparedStatementIndexVariantsByPattern;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectVariantsByPatternAndDatatype() throws SQLException {
		if (this.preparedStatementIndexVariantsByPatternAndDatatype == null) {
			this.preparedStatementIndexVariantsByPatternAndDatatype = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_REGEXP_AND_DATATYPE);
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

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectItemIdentifiers() throws SQLException {
		if (this.preparedStatementIndexItemIdentifiers == null) {
			this.preparedStatementIndexItemIdentifiers = connection.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_ITEM_IDENTIFIERS);
		}
		return this.preparedStatementIndexItemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubjectIdentifiers() throws SQLException {
		if (this.preparedStatementIndexSubjectIdentifiers == null) {
			this.preparedStatementIndexSubjectIdentifiers = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_SUBJECT_IDENTIFIERS);
		}
		return this.preparedStatementIndexSubjectIdentifiers;
	}

	public PreparedStatement getQuerySelectSubjectLocators() throws SQLException {
		if (this.preparedStatementIndexSubjectLocators == null) {
			this.preparedStatementIndexSubjectLocators = connection.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_SUBJECT_LOCATORS);
		}
		return this.preparedStatementIndexSubjectLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectConstructsByIdentitifer() throws SQLException {
		if (this.preparedStatementIndexConstructsByIdentifier == null) {
			this.preparedStatementIndexConstructsByIdentifier = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexConstructsByIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectConstructsByItemIdentitifer() throws SQLException {
		if (this.preparedStatementIndexConstructsByItemIdentifier == null) {
			this.preparedStatementIndexConstructsByItemIdentifier = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexConstructsByItemIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsBySubjectIdentitifer() throws SQLException {
		if (this.preparedStatementIndexTopicsBySubjectIdentifier == null) {
			this.preparedStatementIndexTopicsBySubjectIdentifier = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN);
		}
		return this.preparedStatementIndexTopicsBySubjectIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsBySubjectLocator() throws SQLException {
		if (this.preparedStatementIndexTopicsBySubjectLocator == null) {
			this.preparedStatementIndexTopicsBySubjectLocator = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN);
		}
		return this.preparedStatementIndexTopicsBySubjectLocator;
	}

	// SupertypeSubtypeIndex

	private PreparedStatement preparedStatementIndexDirectSubtypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutSubtypes;
	private PreparedStatement preparedStatementIndexSubtypesOfTopic;
	private PreparedStatement preparedStatementIndexSubtypesOfTopics;
	private PreparedStatement preparedStatementIndexSubtypes;
	private PreparedStatement preparedStatementIndexDirectSupertypes;
	private PreparedStatement preparedStatementIndexTopicsWithoutSupertypes;
	private PreparedStatement preparedStatementIndexSupertypesOfTopic;
	private PreparedStatement preparedStatementIndexSupertypesOfTopics;
	private PreparedStatement preparedStatementIndexSupertypes;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectDirectSubtypes() throws SQLException {
		if (this.preparedStatementIndexDirectSubtypes == null) {
			this.preparedStatementIndexDirectSubtypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_DIRECT_SUBTYPES);
		}
		return this.preparedStatementIndexDirectSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectDirectSupertypes() throws SQLException {
		if (this.preparedStatementIndexDirectSupertypes == null) {
			this.preparedStatementIndexDirectSupertypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_DIRECT_SUPERTYPES);
		}
		return this.preparedStatementIndexDirectSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypes() throws SQLException {
		if (this.preparedStatementIndexSubtypes == null) {
			this.preparedStatementIndexSubtypes = connection.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUBTYPES);
		}
		return this.preparedStatementIndexSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypes() throws SQLException {
		if (this.preparedStatementIndexSupertypes == null) {
			this.preparedStatementIndexSupertypes = connection.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUPERTYPES);
		}
		return this.preparedStatementIndexSupertypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopic() throws SQLException {
		if (this.preparedStatementIndexSubtypesOfTopic == null) {
			this.preparedStatementIndexSubtypesOfTopic = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUBTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSubtypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopics() throws SQLException {
		if (this.preparedStatementIndexSubtypesOfTopics == null) {
			this.preparedStatementIndexSubtypesOfTopics = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUBTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSubtypesOfTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopic() throws SQLException {
		if (this.preparedStatementIndexSupertypesOfTopic == null) {
			this.preparedStatementIndexSupertypesOfTopic = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSupertypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopics() throws SQLException {
		if (this.preparedStatementIndexSupertypesOfTopics == null) {
			this.preparedStatementIndexSupertypesOfTopics = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_SUPERTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSupertypesOfTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsWithoutSubtypes() throws SQLException {
		if (this.preparedStatementIndexTopicsWithoutSubtypes == null) {
			this.preparedStatementIndexTopicsWithoutSubtypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_TOPICS_WITHOUT_SUBTYPES);
		}
		return this.preparedStatementIndexTopicsWithoutSubtypes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsWithoutSupertypes(boolean paged) throws SQLException {
		if (this.preparedStatementIndexTopicsWithoutSupertypes == null) {
			this.preparedStatementIndexTopicsWithoutSupertypes = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES);
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
			preparedStatementPerformMergeTopics = connection.prepareStatement(IPostGreSqlPerformQueries.PERFORM_MERGE_TOPICS);
		}
		return preparedStatementPerformMergeTopics;
	}

	// ******************
	// * REVISION QUERY *
	// ******************

	private PreparedStatement preparedStatementQueryCreateRevision;
	private PreparedStatement preparedStatementQueryCreateChangeset;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateRevision() throws SQLException {
		if (preparedStatementQueryCreateRevision == null) {
			preparedStatementQueryCreateRevision = connection
					.prepareStatement(IPostGreSqlQueryRevisions.QUERY_CREATE_REVISION, Statement.RETURN_GENERATED_KEYS);
		}
		return preparedStatementQueryCreateRevision;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryCreateChangeset() throws SQLException {
		if (preparedStatementQueryCreateChangeset == null) {
			preparedStatementQueryCreateChangeset = connection.prepareStatement(IPostGreSqlQueryRevisions.QUERY_CREATE_CHANGESET,
					Statement.RETURN_GENERATED_KEYS);
		}
		return preparedStatementQueryCreateChangeset;
	}

}
