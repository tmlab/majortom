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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlIndexQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlSelectQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

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
	// * SELECT QUERY *
	// ****************

	// private PreparedStatement preparedStatementReadPlayedAssociationWithType;
	// private PreparedStatement
	// preparedStatementReadPlayedAssociationWithTypeAndScope;
	// private PreparedStatement preparedStatementReadAssociationWithType;
	// private PreparedStatement
	// preparedStatementReadAssociationWithTypeAndScope;
	// private PreparedStatement preparedStatementReadNamesWithType;
	// private PreparedStatement preparedStatementReadNamesWithTypeAndScope;
	// private PreparedStatement preparedStatementReadOccurrencesWithType;
	// private PreparedStatement
	// preparedStatementReadOccurrencesWithTypeAndScope;
	// private PreparedStatement preparedStatementReadRolesWithType;
	// private PreparedStatement preparedStatementReadPlayedRolesWithType;
	// private PreparedStatement
	// preparedStatementReadPlayedRolesWithTypeAndAssociationType;
	private PreparedStatement preparedStatementReadSupertypes;
	// private PreparedStatement preparedStatementReadTopicMap;
	private PreparedStatement preparedStatementReadScopeByThemes;

	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadAssociationWithType() throws
	// SQLException {
	// if (this.preparedStatementReadAssociationWithType == null) {
	// this.preparedStatementReadAssociationWithType =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS_WITH_TYPE);
	// }
	// return this.preparedStatementReadAssociationWithType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadAssociationWithTypeAndScope() throws
	// SQLException {
	// if (this.preparedStatementReadAssociationWithTypeAndScope == null) {
	// this.preparedStatementReadAssociationWithTypeAndScope = connection
	// .prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
	// }
	// return this.preparedStatementReadAssociationWithTypeAndScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadNamesWithType() throws SQLException
	// {
	// if (this.preparedStatementReadNamesWithType == null) {
	// this.preparedStatementReadNamesWithType =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES_WITH_TYPE);
	// }
	// return this.preparedStatementReadNamesWithType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadNamesWithTypeAndScope() throws
	// SQLException {
	// if (this.preparedStatementReadNamesWithTypeAndScope == null) {
	// this.preparedStatementReadNamesWithTypeAndScope =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_NAMES_WITH_TYPE_AND_SCOPE);
	// }
	// return this.preparedStatementReadNamesWithTypeAndScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadOccurrencesWithType() throws
	// SQLException {
	// if (this.preparedStatementReadOccurrencesWithType == null) {
	// this.preparedStatementReadOccurrencesWithType =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES_WITH_TYPE);
	// }
	// return this.preparedStatementReadOccurrencesWithType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadOccurrencesWithTypeAndScope() throws
	// SQLException {
	// if (this.preparedStatementReadOccurrencesWithTypeAndScope == null) {
	// this.preparedStatementReadOccurrencesWithTypeAndScope = connection
	// .prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_OCCURRENCES_WITH_TYPE_AND_SCOPE);
	// }
	// return this.preparedStatementReadOccurrencesWithTypeAndScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadPlayedAssociationWithType() throws
	// SQLException {
	// if (this.preparedStatementReadPlayedAssociationWithType == null) {
	// this.preparedStatementReadPlayedAssociationWithType = connection
	// .prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE);
	// }
	// return this.preparedStatementReadPlayedAssociationWithType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadPlayedAssociationWithTypeAndScope()
	// throws SQLException {
	// if (this.preparedStatementReadPlayedAssociationWithTypeAndScope == null)
	// {
	// this.preparedStatementReadPlayedAssociationWithTypeAndScope = connection
	// .prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE);
	// }
	// return this.preparedStatementReadPlayedAssociationWithTypeAndScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadPlayedRolesWithType() throws
	// SQLException {
	// if (this.preparedStatementReadPlayedRolesWithType == null) {
	// this.preparedStatementReadPlayedRolesWithType =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ROLES_WITH_TYPE);
	// }
	// return this.preparedStatementReadPlayedRolesWithType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement
	// getQueryReadPlayedRolesWithTypeAndAssociationType() throws SQLException {
	// if (this.preparedStatementReadPlayedRolesWithTypeAndAssociationType ==
	// null) {
	// this.preparedStatementReadPlayedRolesWithTypeAndAssociationType =
	// connection
	// .prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE);
	// }
	// return this.preparedStatementReadPlayedRolesWithTypeAndAssociationType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadRolesWithType() throws SQLException
	// {
	// if (this.preparedStatementReadRolesWithType == null) {
	// this.preparedStatementReadRolesWithType =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_ROLES_WITH_TYPE);
	// }
	// return this.preparedStatementReadRolesWithType;
	// }
	//
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
	public PreparedStatement getQueryReadSupertypes() throws SQLException {
		if (this.preparedStatementReadSupertypes == null) {
			this.preparedStatementReadSupertypes = connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SUPERTYPES);
		}
		return this.preparedStatementReadSupertypes;
	}

	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryReadTopicMap() throws SQLException {
	// if (this.preparedStatementReadTopicMap == null) {
	// this.preparedStatementReadTopicMap =
	// connection.prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_TOPICMAP);
	// }
	// return this.preparedStatementReadTopicMap;
	// }

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

	// ***************
	// * INDEX QUERY *
	// ***************

	// // TypeInstanceIndex
	//
	// private PreparedStatement preparedStatementIndexAssociationTypes;
	// private PreparedStatement preparedStatementIndexNameTypes;
	// private PreparedStatement preparedStatementIndexOccurrenceTypes;
	// private PreparedStatement preparedStatementIndexRoleTypes;
	// private PreparedStatement preparedStatementIndexTopicTypes;
	// private PreparedStatement preparedStatementIndexAssociationsByType;
	// private PreparedStatement preparedStatementIndexRolesByType;
	// private PreparedStatement preparedStatementIndexNamesByType;
	// private PreparedStatement preparedStatementIndexOccurrencesByType;
	// private Map<Boolean, Map<Long, PreparedStatement>>
	// preparedStatementsIndexTopicsByTypes;
	// private PreparedStatement preparedStatementIndexTopicsWithoutType;
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectAssociationTypes() throws
	// SQLException {
	// if (this.preparedStatementIndexAssociationTypes == null) {
	// this.preparedStatementIndexAssociationTypes = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ASSOCIATIONTYPES);
	// }
	// return this.preparedStatementIndexAssociationTypes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectNameTypes() throws SQLException {
	// if (this.preparedStatementIndexNameTypes == null) {
	// this.preparedStatementIndexNameTypes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_NAMETYPES);
	// }
	// return this.preparedStatementIndexNameTypes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrenceTypes() throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrenceTypes == null) {
	// this.preparedStatementIndexOccurrenceTypes = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_OCCURRENCETYPES);
	// }
	// return this.preparedStatementIndexOccurrenceTypes;
	// }
	//
	// public PreparedStatement getQuerySelectRoleTypes() throws SQLException {
	// if (this.preparedStatementIndexRoleTypes == null) {
	// this.preparedStatementIndexRoleTypes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ROLETYPES);
	// }
	// return this.preparedStatementIndexRoleTypes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectTopicTypes() throws SQLException {
	// if (this.preparedStatementIndexTopicTypes == null) {
	// this.preparedStatementIndexTopicTypes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPICTYPES);
	// }
	// return this.preparedStatementIndexTopicTypes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectAssociationsByType() throws
	// SQLException {
	// if (this.preparedStatementIndexAssociationsByType == null) {
	// this.preparedStatementIndexAssociationsByType = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
	// }
	// return this.preparedStatementIndexAssociationsByType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectRolesByType() throws SQLException
	// {
	// if (this.preparedStatementIndexRolesByType == null) {
	// this.preparedStatementIndexRolesByType =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_ROLES_BY_TYPE);
	// }
	// return this.preparedStatementIndexRolesByType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectNamesByType() throws SQLException
	// {
	// if (this.preparedStatementIndexNamesByType == null) {
	// this.preparedStatementIndexNamesByType =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_NAMES_BY_TYPE);
	// }
	// return this.preparedStatementIndexNamesByType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByType() throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrencesByType == null) {
	// this.preparedStatementIndexOccurrencesByType = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_OCCURRENCES_BY_TYPE);
	// }
	// return this.preparedStatementIndexOccurrencesByType;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectTopicsByTypes(long typeCount,
	// boolean all) throws SQLException {
	// if (typeCount == 0) {
	// if (this.preparedStatementIndexTopicsWithoutType == null) {
	// this.preparedStatementIndexTopicsWithoutType = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_WITHOUT_TYPE);
	// }
	// return this.preparedStatementIndexTopicsWithoutType;
	// }
	//
	// /*
	// * check if cache is initialized
	// */
	// if (preparedStatementsIndexTopicsByTypes == null) {
	// preparedStatementsIndexTopicsByTypes = HashUtil.getHashMap();
	// }
	// /*
	// * check if binding exists
	// */
	// Map<Long, PreparedStatement> map =
	// preparedStatementsIndexTopicsByTypes.get(all);
	// if (map == null) {
	// map = HashUtil.getHashMap();
	// preparedStatementsIndexTopicsByTypes.put(all, map);
	// }
	// /*
	// * check if statement exists
	// */
	// PreparedStatement stmt = map.get(typeCount);
	// if (stmt == null) {
	// stmt = createPreparedStatementForMatchingThemes(all ?
	// IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL
	// :
	// IPostGreSqlIndexQueries.QueryTypeInstanceIndex.QUERY_SELECT_TOPIC_BY_TYPES,
	// "id_type", typeCount, all);
	// map.put(typeCount, stmt);
	// }
	// return stmt;
	// }
	//
	// TransitiveTypeInstanceIndex

	private PreparedStatement preparedStatementIndexAssociationsByTypeTransitive;
	private PreparedStatement preparedStatementIndexRolesByTypeTransitive;
	private PreparedStatement preparedStatementIndexNamesByTypeTransitive;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypesTransitive;
	private PreparedStatement preparedStatementIndexAssociationsByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexRolesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexNamesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexTopicsByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexTopicsByTypesTransitivePaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByTypeTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexAssociationsByTypeTransitivePaged == null) {
				this.preparedStatementIndexAssociationsByTypeTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
			}
			return this.preparedStatementIndexAssociationsByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexAssociationsByTypeTransitive == null) {
			this.preparedStatementIndexAssociationsByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
		}
		return this.preparedStatementIndexAssociationsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByTypeTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexRolesByTypeTransitivePaged == null) {
				this.preparedStatementIndexRolesByTypeTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_ROLES_BY_TYPE);
			}
			return this.preparedStatementIndexRolesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexRolesByTypeTransitive == null) {
			this.preparedStatementIndexRolesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_ROLES_BY_TYPE);
		}
		return this.preparedStatementIndexRolesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByTypeTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexNamesByTypeTransitivePaged == null) {
				this.preparedStatementIndexNamesByTypeTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_NAMES_BY_TYPE);
			}
			return this.preparedStatementIndexNamesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexNamesByTypeTransitive == null) {
			this.preparedStatementIndexNamesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_NAMES_BY_TYPE);
		}
		return this.preparedStatementIndexNamesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByTypeTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByTypeTransitivePaged == null) {
				this.preparedStatementIndexOccurrencesByTypeTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
			}
			return this.preparedStatementIndexOccurrencesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexOccurrencesByTypeTransitive == null) {
			this.preparedStatementIndexOccurrencesByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
		}
		return this.preparedStatementIndexOccurrencesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypeTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexTopicsByTypeTransitivePaged == null) {
				this.preparedStatementIndexTopicsByTypeTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_TOPICS_BY_TYPE);
			}
			return this.preparedStatementIndexTopicsByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexTopicsByTypeTransitive == null) {
			this.preparedStatementIndexTopicsByTypeTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPICS_BY_TYPE);
		}
		return this.preparedStatementIndexTopicsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypesTransitive(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexTopicsByTypesTransitivePaged == null) {
				this.preparedStatementIndexTopicsByTypesTransitivePaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_TOPICS_BY_TYPES);
			}
			return this.preparedStatementIndexTopicsByTypesTransitivePaged;
		}
		if (this.preparedStatementIndexTopicsByTypesTransitive == null) {
			this.preparedStatementIndexTopicsByTypesTransitive = connection
					.prepareStatement(IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPICS_BY_TYPES);
		}
		return this.preparedStatementIndexTopicsByTypesTransitive;
	}

	//
	// ScopeIndex
	private PreparedStatement preparedStatementIndexScopesByThemesUsed;

	// private PreparedStatement preparedStatementIndexAssociationsByTheme;
	// private PreparedStatement preparedStatementIndexAssociationScopes;
	// private PreparedStatement preparedStatementIndexAssociationThemes;
	// private PreparedStatement preparedStatementIndexNamesByEmptyScope;
	// private PreparedStatement preparedStatementIndexNamesByScope;
	// private PreparedStatement preparedStatementIndexNamesByTheme;
	// private PreparedStatement preparedStatementIndexNameScopes;
	// private PreparedStatement preparedStatementIndexNameThemes;
	// private PreparedStatement preparedStatementIndexOccurrencesByEmptyScope;
	// private PreparedStatement preparedStatementIndexOccurrencesByScope;
	// private Map<Long, PreparedStatement>
	// preparedStatementIndexOccurrencesByScopes;
	// private PreparedStatement preparedStatementIndexOccurrencesByTheme;
	// private Map<Boolean, Map<Long, PreparedStatement>>
	// preparedStatementIndexOccurrencesByThemes;
	// private PreparedStatement preparedStatementIndexOccurrenceScopes;
	// private PreparedStatement preparedStatementIndexOccurrenceThemes;
	// private PreparedStatement preparedStatementIndexVariantsByScope;
	// private PreparedStatement preparedStatementIndexVariantsByScopes;
	// private PreparedStatement preparedStatementIndexVariantsByTheme;
	// private Map<Boolean, Map<Long, PreparedStatement>>
	// preparedStatementIndexVariantsByThemes;
	// private PreparedStatement preparedStatementIndexVariantScopes;
	// private PreparedStatement preparedStatementIndexVariantThemes;
	//
	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryScopesByThemesUsed() throws SQLException {
		if (this.preparedStatementIndexScopesByThemesUsed == null) {
			preparedStatementIndexScopesByThemesUsed = connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_SCOPES_BY_THEMES_USED);
		}
		return preparedStatementIndexScopesByThemesUsed;
	}

	//
	// // /**
	// // * {@inheritDoc}
	// // */
	// // public PreparedStatement getQueryAssociationsByScope(boolean paged)
	// // throws SQLException {
	// // /*
	// // * is empty scope
	// // */
	// // if (paged) {
	// // if (this.preparedStatementIndexAssociationsByEmptyScope == null) {
	// // preparedStatementIndexAssociationsByEmptyScope = connection
	// //
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_EMPTYSCOPE);
	// // }
	// // return preparedStatementIndexAssociationsByEmptyScope;
	// // }
	// // /*
	// // * is non-empty scope
	// // */
	// // if (this.preparedStatementIndexAssociationsByScope == null) {
	// // this.preparedStatementIndexAssociationsByScope = connection
	// //
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_SCOPE);
	// // }
	// // return this.preparedStatementIndexAssociationsByScope;
	// // }
	// //
	// // /**
	// // * {@inheritDoc}
	// // */
	// // public PreparedStatement getQueryAssociationsByScopes(boolean paged)
	// // throws SQLException {
	// // /*
	// // * check if cache is initialized
	// // */
	// // if (preparedStatementIndexAssociationsByScopes == null) {
	// // preparedStatementIndexAssociationsByScopes = HashUtil.getHashMap();
	// // }
	// // /*
	// // * check if statement exists
	// // */
	// // PreparedStatement stmt =
	// // preparedStatementIndexAssociationsByScopes.get(scopeCount);
	// // if (stmt == null) {
	// // /*
	// // * create statement
	// // */
	// // String subquery = "";
	// // for (long n = 0; n < scopeCount; n++) {
	// // subquery += subquery.isEmpty() ? "" : " OR ";
	// // subquery += "id_scope = ?";
	// // }
	// // stmt =
	// //
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_SCOPES
	// // + " " + subquery);
	// // preparedStatementIndexAssociationsByScopes.put(scopeCount, stmt);
	// // }
	// // return stmt;
	// // }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryAssociationsByTheme(boolean paged)
	// throws SQLException {
	// if (this.preparedStatementIndexAssociationsByTheme == null) {
	// this.preparedStatementIndexAssociationsByTheme = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEME);
	// }
	// return this.preparedStatementIndexAssociationsByTheme;
	// }
	//
	// // /**
	// // * {@inheritDoc}
	// // */
	// // public PreparedStatement getQueryAssociationsByThemes(boolean all,
	// // boolean paged) throws SQLException {
	// // /*
	// // * check if cache is initialized
	// // */
	// // if (preparedStatementIndexAssociationsByThemes == null) {
	// // preparedStatementIndexAssociationsByThemes = HashUtil.getHashMap();
	// // }
	// // /*
	// // * check if binding exists
	// // */
	// // Map<Long, PreparedStatement> map =
	// // preparedStatementIndexAssociationsByThemes.get(all);
	// // if (map == null) {
	// // map = HashUtil.getHashMap();
	// // preparedStatementIndexAssociationsByThemes.put(all, map);
	// // }
	// // /*
	// // * check if statement exists
	// // */
	// // PreparedStatement stmt = map.get(themeCount);
	// // if (stmt == null) {
	// // stmt = createPreparedStatementForMatchingThemes(all ?
	// //
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEMES_MATCH_ALL
	// // :
	// //
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATIONS_BY_THEMES,
	// // "id_theme", themeCount, all);
	// // map.put(themeCount, stmt);
	// // }
	// // return stmt;
	// // }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryAssociationScopes(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexAssociationScopes == null) {
	// this.preparedStatementIndexAssociationScopes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATION_SCOPES);
	// }
	// return this.preparedStatementIndexAssociationScopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryAssociationThemes(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexAssociationThemes == null) {
	// this.preparedStatementIndexAssociationThemes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_ASSOCIATION_THEMES);
	// }
	// return this.preparedStatementIndexAssociationThemes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryNamesByScope(boolean paged) throws
	// SQLException {
	// /*
	// * is empty scope
	// */
	// if (paged) {
	// if (this.preparedStatementIndexNamesByEmptyScope == null) {
	// preparedStatementIndexNamesByEmptyScope =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_EMPTYSCOPE);
	// }
	// return preparedStatementIndexNamesByEmptyScope;
	// }
	// /*
	// * is non-empty scope
	// */
	// if (this.preparedStatementIndexNamesByScope == null) {
	// this.preparedStatementIndexNamesByScope =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_SCOPE);
	// }
	// return this.preparedStatementIndexNamesByScope;
	// }
	//
	// // /**
	// // * {@inheritDoc}
	// // */
	// // public PreparedStatement getQueryNamesByScopes(boolean paged) throws
	// // SQLException {
	// // /*
	// // * check if cache is initialized
	// // */
	// // if (preparedStatementIndexNamesByScopes == null) {
	// // preparedStatementIndexNamesByScopes = HashUtil.getHashMap();
	// // }
	// // /*
	// // * check if statement exists
	// // */
	// // PreparedStatement stmt =
	// // preparedStatementIndexNamesByScopes.get(scopeCount);
	// // if (stmt == null) {
	// // /*
	// // * create statement
	// // */
	// // String subquery = "";
	// // for (long n = 0; n < scopeCount; n++) {
	// // subquery += subquery.isEmpty() ? "" : " OR ";
	// // subquery += "id_scope = ?";
	// // }
	// // stmt =
	// //
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_SCOPES
	// // + " " + subquery);
	// // preparedStatementIndexNamesByScopes.put(scopeCount, stmt);
	// // }
	// // return stmt;
	// // }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryNamesByTheme(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexNamesByTheme == null) {
	// this.preparedStatementIndexNamesByTheme =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEME);
	// }
	// return this.preparedStatementIndexNamesByTheme;
	// }
	//
	// // /**
	// // * {@inheritDoc}
	// // */
	// // public PreparedStatement getQueryNamesByThemes(boolean all, boolean
	// // paged) throws SQLException {
	// // /*
	// // * check if cache is initialized
	// // */
	// // if (preparedStatementIndexNamesByThemes == null) {
	// // preparedStatementIndexNamesByThemes = HashUtil.getHashMap();
	// // }
	// // /*
	// // * check if binding exists
	// // */
	// // Map<Long, PreparedStatement> map =
	// // preparedStatementIndexNamesByThemes.get(all);
	// // if (map == null) {
	// // map = HashUtil.getHashMap();
	// // preparedStatementIndexNamesByThemes.put(all, map);
	// // }
	// // /*
	// // * check if statement exists
	// // */
	// // PreparedStatement stmt = map.get(themeCount);
	// // if (stmt == null) {
	// // stmt = createPreparedStatementForMatchingThemes(all ?
	// //
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEMES_MATCH_ALL
	// // :
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAMES_BY_THEMES,
	// // "id_theme", themeCount, all);
	// // map.put(themeCount, stmt);
	// // }
	// // return stmt;
	// // }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryNameScopes(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexNameScopes == null) {
	// this.preparedStatementIndexNameScopes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAME_SCOPES);
	// }
	// return this.preparedStatementIndexNameScopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryNameThemes() throws SQLException {
	// if (this.preparedStatementIndexNameThemes == null) {
	// this.preparedStatementIndexNameThemes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_NAME_THEMES);
	// }
	// return this.preparedStatementIndexNameThemes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrencesByScope(boolean emptyScope)
	// throws SQLException {
	// /*
	// * is empty scope
	// */
	// if (emptyScope) {
	// if (this.preparedStatementIndexOccurrencesByEmptyScope == null) {
	// preparedStatementIndexOccurrencesByEmptyScope = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_EMPTYSCOPE);
	// }
	// return preparedStatementIndexOccurrencesByEmptyScope;
	// }
	// /*
	// * is non-empty scope
	// */
	// if (this.preparedStatementIndexOccurrencesByScope == null) {
	// this.preparedStatementIndexOccurrencesByScope = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_SCOPE);
	// }
	// return this.preparedStatementIndexOccurrencesByScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrencesByScopes(long scopeCount)
	// throws SQLException {
	// /*
	// * check if cache is initialized
	// */
	// if (preparedStatementIndexOccurrencesByScopes == null) {
	// preparedStatementIndexOccurrencesByScopes = HashUtil.getHashMap();
	// }
	// /*
	// * check if statement exists
	// */
	// PreparedStatement stmt =
	// preparedStatementIndexOccurrencesByScopes.get(scopeCount);
	// if (stmt == null) {
	// /*
	// * create statement
	// */
	// String subquery = "";
	// for (long n = 0; n < scopeCount; n++) {
	// subquery += subquery.isEmpty() ? "" : " OR ";
	// subquery += "id_scope = ?";
	// }
	// stmt =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_SCOPES
	// + " " + subquery);
	// preparedStatementIndexOccurrencesByScopes.put(scopeCount, stmt);
	// }
	// return stmt;
	// }
	//
	// /**
	// *
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrencesByTheme() throws SQLException
	// {
	// if (this.preparedStatementIndexOccurrencesByTheme == null) {
	// this.preparedStatementIndexOccurrencesByTheme = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEME);
	// }
	// return this.preparedStatementIndexOccurrencesByTheme;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrencesByThemes(long themeCount,
	// boolean all) throws SQLException {
	// /*
	// * check if cache is initialized
	// */
	// if (preparedStatementIndexOccurrencesByThemes == null) {
	// preparedStatementIndexOccurrencesByThemes = HashUtil.getHashMap();
	// }
	// /*
	// * check if binding exists
	// */
	// Map<Long, PreparedStatement> map =
	// preparedStatementIndexOccurrencesByThemes.get(all);
	// if (map == null) {
	// map = HashUtil.getHashMap();
	// preparedStatementIndexOccurrencesByThemes.put(all, map);
	// }
	// /*
	// * check if statement exists
	// */
	// PreparedStatement stmt = map.get(themeCount);
	// if (stmt == null) {
	// stmt = createPreparedStatementForMatchingThemes(all ?
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEMES_MATCH_ALL
	// :
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCES_BY_THEMES,
	// "id_theme", themeCount, all);
	// map.put(themeCount, stmt);
	// }
	// return stmt;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrenceScopes() throws SQLException {
	// if (this.preparedStatementIndexOccurrenceScopes == null) {
	// this.preparedStatementIndexOccurrenceScopes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCE_SCOPES);
	// }
	// return this.preparedStatementIndexOccurrenceScopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryOccurrenceThemes() throws SQLException {
	// if (this.preparedStatementIndexOccurrenceThemes == null) {
	// this.preparedStatementIndexOccurrenceThemes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_OCCURRENCE_THEMES);
	// }
	// return this.preparedStatementIndexOccurrenceThemes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantsByScope() throws SQLException {
	// if (this.preparedStatementIndexVariantsByScope == null) {
	// this.preparedStatementIndexVariantsByScope =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_SCOPE);
	// }
	// return this.preparedStatementIndexVariantsByScope;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantsByScopes(long scopeCount) throws
	// SQLException {
	// /*
	// * check if cache is initialized
	// */
	// if (preparedStatementIndexVariantsByScopes == null) {
	// preparedStatementIndexVariantsByScopes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_SCOPES);
	// }
	// return preparedStatementIndexVariantsByScopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantsByTheme() throws SQLException {
	// if (this.preparedStatementIndexVariantsByTheme == null) {
	// this.preparedStatementIndexVariantsByTheme =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEME);
	// }
	// return this.preparedStatementIndexVariantsByTheme;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantsByThemes(long themeCount,
	// boolean all) throws SQLException {
	// /*
	// * check if cache is initialized
	// */
	// if (preparedStatementIndexVariantsByThemes == null) {
	// preparedStatementIndexVariantsByThemes = HashUtil.getHashMap();
	// }
	// /*
	// * check if binding exists
	// */
	// Map<Long, PreparedStatement> map =
	// preparedStatementIndexVariantsByThemes.get(all);
	// if (map == null) {
	// map = HashUtil.getHashMap();
	// preparedStatementIndexVariantsByThemes.put(all, map);
	// }
	// /*
	// * check if statement exists
	// */
	// PreparedStatement stmt = map.get(themeCount);
	// if (stmt == null) {
	// stmt = createPreparedStatementForMatchingThemes(all ?
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEMES_MATCH_ALL
	// :
	// IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANTS_BY_THEMES,
	// "id_theme", themeCount, all);
	// map.put(themeCount, stmt);
	// }
	// return stmt;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantScopes() throws SQLException {
	// if (this.preparedStatementIndexVariantScopes == null) {
	// this.preparedStatementIndexVariantScopes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANT_SCOPES);
	// }
	// return this.preparedStatementIndexVariantScopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQueryVariantThemes() throws SQLException {
	// if (this.preparedStatementIndexVariantThemes == null) {
	// this.preparedStatementIndexVariantThemes =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_VARIANT_THEMES);
	// }
	// return this.preparedStatementIndexVariantThemes;
	// }
	//
	// // LiteralIndex
	//
	// private PreparedStatement preparedStatementIndexNames;
	// private PreparedStatement preparedStatementIndexNamesByValue;
	// private PreparedStatement preparedStatementIndexNamesByPattern;
	// private PreparedStatement preparedStatementIndexOccurrences;
	// private PreparedStatement preparedStatementIndexOccurrencesByDatatype;
	// private PreparedStatement preparedStatementIndexOccurrencesByDateRange;
	// private PreparedStatement preparedStatementIndexOccurrencesByRange;
	// private PreparedStatement preparedStatementIndexOccurrencesByValue;
	// private PreparedStatement
	// preparedStatementIndexOccurrencesByValueAndDatatype;
	// private PreparedStatement preparedStatementIndexOccurrencesByPattern;
	// private PreparedStatement
	// preparedStatementIndexOccurrencesByPatternAndDatatype;
	// private PreparedStatement preparedStatementIndexVariants;
	// private PreparedStatement preparedStatementIndexVariantsByDatatype;
	// private PreparedStatement preparedStatementIndexVariantsByValue;
	// private PreparedStatement
	// preparedStatementIndexVariantsByValueAndDatatype;
	// private PreparedStatement preparedStatementIndexVariantsByPattern;
	// private PreparedStatement
	// preparedStatementIndexVariantsByPatternAndDatatype;
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectNames(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexNames == null) {
	// this.preparedStatementIndexNames =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES);
	// }
	// return this.preparedStatementIndexNames;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectNamesByValue() throws SQLException
	// {
	// if (this.preparedStatementIndexNamesByValue == null) {
	// this.preparedStatementIndexNamesByValue =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES_BY_VALUE);
	// }
	// return this.preparedStatementIndexNamesByValue;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectNamesByPattern() throws
	// SQLException {
	// if (this.preparedStatementIndexNamesByPattern == null) {
	// this.preparedStatementIndexNamesByPattern =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_NAMES_BY_REGEXP);
	// }
	// return this.preparedStatementIndexNamesByPattern;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrences(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrences == null) {
	// this.preparedStatementIndexOccurrences =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES);
	// }
	// return this.preparedStatementIndexOccurrences;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByDatatype(boolean
	// paged) throws SQLException {
	// if (this.preparedStatementIndexOccurrencesByDatatype == null) {
	// this.preparedStatementIndexOccurrencesByDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_DATATYPE);
	// }
	// return this.preparedStatementIndexOccurrencesByDatatype;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByDateRange(boolean
	// paged) throws SQLException {
	// if (this.preparedStatementIndexOccurrencesByDateRange == null) {
	// this.preparedStatementIndexOccurrencesByDateRange = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_DATERANGE);
	// }
	// return this.preparedStatementIndexOccurrencesByDateRange;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByRange(boolean paged)
	// throws SQLException {
	// if (this.preparedStatementIndexOccurrencesByRange == null) {
	// this.preparedStatementIndexOccurrencesByRange = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_RANGE);
	// }
	// return this.preparedStatementIndexOccurrencesByRange;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByValue() throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrencesByValue == null) {
	// this.preparedStatementIndexOccurrencesByValue = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_VALUE);
	// }
	// return this.preparedStatementIndexOccurrencesByValue;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement
	// getQuerySelectOccurrencesByValueAndDatatype(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrencesByValueAndDatatype == null) {
	// this.preparedStatementIndexOccurrencesByValueAndDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_VALUE_AND_DATATYPE);
	// }
	// return this.preparedStatementIndexOccurrencesByValueAndDatatype;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByPattern() throws
	// SQLException {
	// if (this.preparedStatementIndexOccurrencesByPattern == null) {
	// this.preparedStatementIndexOccurrencesByPattern = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_REGEXP);
	// }
	// return this.preparedStatementIndexOccurrencesByPattern;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectOccurrencesByPatternAndDatatype()
	// throws SQLException {
	// if (this.preparedStatementIndexOccurrencesByPatternAndDatatype == null) {
	// this.preparedStatementIndexOccurrencesByPatternAndDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_OCCURRENCES_BY_REGEXP_AND_DATATYPE);
	// }
	// return this.preparedStatementIndexOccurrencesByPatternAndDatatype;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariants(boolean paged) throws
	// SQLException {
	// if (this.preparedStatementIndexVariants == null) {
	// this.preparedStatementIndexVariants =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS);
	// }
	// return this.preparedStatementIndexVariants;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariantsByDatatype() throws
	// SQLException {
	// if (this.preparedStatementIndexVariantsByDatatype == null) {
	// this.preparedStatementIndexVariantsByDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_DATATYPE);
	// }
	// return this.preparedStatementIndexVariantsByDatatype;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariantsByValue() throws
	// SQLException {
	// if (this.preparedStatementIndexVariantsByValue == null) {
	// this.preparedStatementIndexVariantsByValue =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_VALUE);
	// }
	// return this.preparedStatementIndexVariantsByValue;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariantsByValueAndDatatype()
	// throws SQLException {
	// if (this.preparedStatementIndexVariantsByValueAndDatatype == null) {
	// this.preparedStatementIndexVariantsByValueAndDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_VALUE_AND_DATATYPE);
	// }
	// return this.preparedStatementIndexVariantsByValueAndDatatype;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariantsByPattern() throws
	// SQLException {
	// if (this.preparedStatementIndexVariantsByPattern == null) {
	// this.preparedStatementIndexVariantsByPattern = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_REGEXP);
	// }
	// return this.preparedStatementIndexVariantsByPattern;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectVariantsByPatternAndDatatype()
	// throws SQLException {
	// if (this.preparedStatementIndexVariantsByPatternAndDatatype == null) {
	// this.preparedStatementIndexVariantsByPatternAndDatatype = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryLiteralIndex.QUERY_SELECT_VARIANTS_BY_REGEXP_AND_DATATYPE);
	// }
	// return this.preparedStatementIndexVariantsByPatternAndDatatype;
	// }
	//
	// // IdentityIndex
	//
	// private PreparedStatement preparedStatementIndexItemIdentifiers;
	// private PreparedStatement preparedStatementIndexSubjectIdentifiers;
	// private PreparedStatement preparedStatementIndexSubjectLocators;
	// private PreparedStatement preparedStatementIndexConstructsByIdentifier;
	// private PreparedStatement
	// preparedStatementIndexConstructsByItemIdentifier;
	// private PreparedStatement
	// preparedStatementIndexTopicsBySubjectIdentifier;
	// private PreparedStatement preparedStatementIndexTopicsBySubjectLocator;
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectItemIdentifiers() throws
	// SQLException {
	// if (this.preparedStatementIndexItemIdentifiers == null) {
	// this.preparedStatementIndexItemIdentifiers =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_ITEM_IDENTIFIERS);
	// }
	// return this.preparedStatementIndexItemIdentifiers;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectSubjectIdentifiers() throws
	// SQLException {
	// if (this.preparedStatementIndexSubjectIdentifiers == null) {
	// this.preparedStatementIndexSubjectIdentifiers = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_SUBJECT_IDENTIFIERS);
	// }
	// return this.preparedStatementIndexSubjectIdentifiers;
	// }
	//
	// public PreparedStatement getQuerySelectSubjectLocators() throws
	// SQLException {
	// if (this.preparedStatementIndexSubjectLocators == null) {
	// this.preparedStatementIndexSubjectLocators =
	// connection.prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_SUBJECT_LOCATORS);
	// }
	// return this.preparedStatementIndexSubjectLocators;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectConstructsByIdentitifer() throws
	// SQLException {
	// if (this.preparedStatementIndexConstructsByIdentifier == null) {
	// this.preparedStatementIndexConstructsByIdentifier = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN);
	// }
	// return this.preparedStatementIndexConstructsByIdentifier;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectConstructsByItemIdentitifer()
	// throws SQLException {
	// if (this.preparedStatementIndexConstructsByItemIdentifier == null) {
	// this.preparedStatementIndexConstructsByItemIdentifier = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN);
	// }
	// return this.preparedStatementIndexConstructsByItemIdentifier;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectTopicsBySubjectIdentitifer()
	// throws SQLException {
	// if (this.preparedStatementIndexTopicsBySubjectIdentifier == null) {
	// this.preparedStatementIndexTopicsBySubjectIdentifier = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN);
	// }
	// return this.preparedStatementIndexTopicsBySubjectIdentifier;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getQuerySelectTopicsBySubjectLocator() throws
	// SQLException {
	// if (this.preparedStatementIndexTopicsBySubjectLocator == null) {
	// this.preparedStatementIndexTopicsBySubjectLocator = connection
	// .prepareStatement(IPostGreSqlIndexQueries.QueryIdentityIndex.QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN);
	// }
	// return this.preparedStatementIndexTopicsBySubjectLocator;
	// }
	//
	// SupertypeSubtypeIndex

	private PreparedStatement preparedStatementIndexSubtypesOfTopic;
	private PreparedStatement preparedStatementIndexSubtypesOfTopics;
	private PreparedStatement preparedStatementIndexSupertypesOfTopic;
	private PreparedStatement preparedStatementIndexSupertypesOfTopics;
	private PreparedStatement preparedStatementIndexSubtypesOfTopicPaged;
	private PreparedStatement preparedStatementIndexSubtypesOfTopicsPaged;
	private PreparedStatement preparedStatementIndexSupertypesOfTopicPaged;
	private PreparedStatement preparedStatementIndexSupertypesOfTopicsPaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopic(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSubtypesOfTopicPaged == null) {
				this.preparedStatementIndexSubtypesOfTopicPaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUBTYPES_OF_TOPIC);
			}
			return this.preparedStatementIndexSubtypesOfTopicPaged;
		}
		if (this.preparedStatementIndexSubtypesOfTopic == null) {
			this.preparedStatementIndexSubtypesOfTopic = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSubtypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopics(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSubtypesOfTopicsPaged == null) {
				this.preparedStatementIndexSubtypesOfTopicsPaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUBTYPES_OF_TOPICS);
			}
			return this.preparedStatementIndexSubtypesOfTopicsPaged;
		}
		if (this.preparedStatementIndexSubtypesOfTopics == null) {
			this.preparedStatementIndexSubtypesOfTopics = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSubtypesOfTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopic(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSupertypesOfTopicPaged == null) {
				this.preparedStatementIndexSupertypesOfTopicPaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
			}
			return this.preparedStatementIndexSupertypesOfTopicPaged;
		}
		if (this.preparedStatementIndexSupertypesOfTopic == null) {
			this.preparedStatementIndexSupertypesOfTopic = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSupertypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopics(boolean paged) throws SQLException {
		if (paged) {
			if (this.preparedStatementIndexSupertypesOfTopicsPaged == null) {
				this.preparedStatementIndexSupertypesOfTopicsPaged = connection
						.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUPERTYPES_OF_TOPICS);
			}
			return this.preparedStatementIndexSupertypesOfTopicsPaged;
		}
		if (this.preparedStatementIndexSupertypesOfTopics == null) {
			this.preparedStatementIndexSupertypesOfTopics = connection
					.prepareStatement(IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSupertypesOfTopics;
	}

	// // *******************
	// // * Utility methods *
	// // *******************
	//
	// /**
	// * Method creates a prepared statement to query constructs matching by one
	// * or all items in different conditions ( matching all and number of items
	// )
	// *
	// * @param query
	// * the base query
	// * @param columnName
	// * the name of the column to check like this
	// * <code>column = ?</code>
	// * @param count
	// * the number of item
	// * @param all
	// * matching all condition
	// * @return the created statement
	// * @throws SQLException
	// * thrown if statement cannot created
	// */
	// private PreparedStatement createPreparedStatementForMatchingThemes(String
	// query, final String columnName, long count, boolean all) throws
	// SQLException {
	// String replacer = all ? "%ARRAY%" : "%SUBQUERY%";
	// String placeholder = all ? "?" : (columnName + " = ?");
	// String delimer = all ? "," : " OR ";
	// /*
	// * create statement
	// */
	// String subquery = "";
	// for (long n = 0; n < count; n++) {
	// subquery += subquery.isEmpty() ? "" : delimer;
	// subquery += placeholder;
	// }
	// return connection.prepareStatement(query.replaceAll(replacer, subquery));
	// }

	// *****************
	// * PERFORM QUERY *
	// *****************
	//
	// private PreparedStatement preparedStatementPerformMergeTopics;
	//
	// /**
	// * {@inheritDoc}
	// */
	// public PreparedStatement getPerformMergeTopics() throws SQLException {
	// if (preparedStatementPerformMergeTopics == null) {
	// preparedStatementPerformMergeTopics =
	// connection.prepareStatement(IPostGreSqlPerformQueries.PERFORM_MERGE_TOPICS);
	// }
	// return preparedStatementPerformMergeTopics;
	// }

}
