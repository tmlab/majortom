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
package de.topicmapslab.majortom.database.jdbc.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import de.topicmapslab.majortom.database.jdbc.model.IQueryBuilder;
import de.topicmapslab.majortom.database.jdbc.postgres.query.IPostGreSqlDeleteQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.query.IPostGreSqlInsertQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.query.IPostGreSqlSelectQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.query.IPostGreSqlUpdateQueries;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryBuilder implements IQueryBuilder {

	/**
	 * the JDBC connection
	 */
	private final Connection connection;

	/**
	 * @param connection
	 *            the JDBC connection to create the {@link PreparedStatement}
	 */
	public PostGreSqlQueryBuilder(Connection connection) {
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
	private PreparedStatement preparedStatementReadTopicByItemIdentifier;
	private PreparedStatement preparedStatementReadNameByItemIdentifier;
	private PreparedStatement preparedStatementReadOccurrenceByItemIdentifier;
	private PreparedStatement preparedStatementReadVariantByItemIdentifier;
	private PreparedStatement preparedStatementReadAssociationByItemIdentifier;
	private PreparedStatement preparedStatementReadRoleByItemIdentifier;
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
	private PreparedStatement preparedStatementReadTopicBySubjectIdentifier;
	private PreparedStatement preparedStatementReadTopicBySubjectLocator;
	private PreparedStatement preparedStatementReadTopicMap;
	private PreparedStatement preparedStatementReadTopics;
	private PreparedStatement preparedStatementReadTopicsWithType;
	private PreparedStatement preparedStatementReadType;
	private PreparedStatement preparedStatementReadTypes;
	private PreparedStatement preparedStatementReadScope;
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
				this.preparedStatementReadAssociationById = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONC_BY_ID);
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
	public PreparedStatement getQueryReadConstructByItemIdentifier(Class<? extends IConstruct> clazz) throws SQLException {
		if (ITopic.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadTopicByItemIdentifier == null) {
				this.preparedStatementReadTopicByItemIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPIC_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadTopicByItemIdentifier;
		} else if (IName.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadNameByItemIdentifier == null) {
				this.preparedStatementReadNameByItemIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAME_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadNameByItemIdentifier;
		} else if (IOccurrence.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadOccurrenceByItemIdentifier == null) {
				this.preparedStatementReadOccurrenceByItemIdentifier = connection
						.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCE_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadOccurrenceByItemIdentifier;
		} else if (IVariant.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadVariantByItemIdentifier == null) {
				this.preparedStatementReadVariantByItemIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_VARIANT_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadVariantByItemIdentifier;
		} else if (IAssociation.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadAssociationByItemIdentifier == null) {
				this.preparedStatementReadAssociationByItemIdentifier = connection
						.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATION_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadAssociationByItemIdentifier;
		} else if (IAssociationRole.class.isAssignableFrom(clazz)) {
			if (this.preparedStatementReadRoleByItemIdentifier == null) {
				this.preparedStatementReadRoleByItemIdentifier = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLE_BY_ITEM_IDENTIFIER);
			}
			return this.preparedStatementReadRoleByItemIdentifier;
		}
		throw new IllegalArgumentException("Unsupported clazz type " + clazz.getCanonicalName());
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
	public PreparedStatement getQueryReadNames() throws SQLException {
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
	public PreparedStatement getQueryReadOccurrences() throws SQLException {
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
	public PreparedStatement getQueryReadPlayedAssociation() throws SQLException {
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
	public PreparedStatement getQueryReadPlayedRoles() throws SQLException {
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
	public PreparedStatement getQueryReadRoles() throws SQLException {
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
	public PreparedStatement getQueryReadScopeByThemes(long themeNumber) throws SQLException {
		String subquery = "";
		for (long n = 0; n < themeNumber; n++) {
			subquery += subquery.isEmpty() ? "?" : ",?";
		}
		return connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SCOPE_BY_THEMES.replaceFirst("\\?", subquery));
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
	public PreparedStatement getQueryReadTypes() throws SQLException {
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
	public PreparedStatement getQueryReadVariants() throws SQLException {
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
			this.preparedStatementModifyReifier = connection.prepareStatement(IPostGreSqlUpdateQueries.QUERY_MODIFY_REIFIER);
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

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryDeleteTopicMap() throws SQLException {
		if (this.preparedStatementDeleteTopicMap == null) {
			this.preparedStatementDeleteTopicMap = connection.prepareStatement(IPostGreSqlDeleteQueries.QUERY_DELETE_TOPICMAP);
		}
		return this.preparedStatementDeleteTopicMap;
	}

}
