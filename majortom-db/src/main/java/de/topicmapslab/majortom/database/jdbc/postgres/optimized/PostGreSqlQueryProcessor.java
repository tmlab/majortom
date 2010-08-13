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
import java.util.Collection;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryProcessor;
import de.topicmapslab.majortom.database.jdbc.util.Jdbc2Construct;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryProcessor extends Sql99QueryProcessor {

	private final PostGreSqlQueryBuilder queryBuilder;

	/**
	 * constructor
	 * 
	 * @param connection
	 *            the JDBC connection
	 */
	public PostGreSqlQueryProcessor(IConnectionProvider provider, Connection connection) {
		super(provider, connection);
		this.queryBuilder = new PostGreSqlQueryBuilder(connection);
	}

//	/**
//	 * {@inheritDoc}
//	 */
//	public void doMergeTopics(ITopic context, ITopic other) throws SQLException {
//		PreparedStatement stmt = queryBuilder.getPerformMergeTopics();
//		stmt.setLong(1, Long.parseLong(context.getId()));
//		stmt.setLong(2, Long.parseLong(other.getId()));
//		stmt.execute();
//		// long idContext = Long.parseLong(context.getId());
//		// long idOther = Long.parseLong(other.getId());
//		// int max = 12;
//		// for (int n = 0; n < max; n++) {
//		// stmt.setLong(n * 2 + 1, idContext);
//		// stmt.setLong(n * 2 + 2, idOther);
//		// }
//		// stmt.setLong(max * 2 + 1, idOther);
//		// stmt.execute();
//	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithType();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setLong(4, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithType();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrencesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrencesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRolesWithType();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedRolesWithType();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedRolesWithTypeAndAssociationType();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(assocType.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSupertypes();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopicsWithType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id_instance");
	}

	// ****************
	// * INDEX METHOD *
	// ****************

	// // TypeInstanceIndex
	//
	// public Collection<ITopic> getAssociationTypes(ITopicMap topicMap) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectAssociationTypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getNameTypes(ITopicMap topicMap) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectNameTypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getOccurrenceTypes(ITopicMap topicMap) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectOccurrenceTypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getRoleTypes(ITopicMap topicMap) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectRoleTypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getTopicTypes(ITopicMap topicMap) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectTopicTypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByType(ITopic type) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectAssociationsByType();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(type.getTopicMap(), set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByType(ITopic type) throws SQLException
	// {
	// PreparedStatement stmt = queryBuilder.getQuerySelectNamesByType();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByType(ITopic type) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByType();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociationRole> getRolesByType(ITopic type) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectRolesByType();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toRoles(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<ITopic>
	// getTopicsByTypes(Collection<T> types, boolean all) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectTopicsByTypes(types.size(), all);
	// ITopicMap topicMap = null;
	// int n = 2;
	// for (T type : types) {
	// topicMap = (ITopicMap) type.getTopicMap();
	// stmt.setLong(n++, Long.parseLong(type.getId()));
	// }
	// /*
	// * empty type set
	// */
	// if (topicMap == null) {
	// return HashUtil.getHashSet();
	// }
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_instance");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<ITopic> getTopicsByType(ITopicMap
	// topicMap, T type) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectTopicsByTypes(type ==
	// null ? 0 : 1, true);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// if (type != null) {
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_instance");
	// }
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	// }
	//
	// // TransitiveTypeInstanceIndex
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic
	// type, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectAssociationsByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(type.getTopicMap(), set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<IAssociation>
	// getAssociationsByTypeTransitive(ITopicMap topicMap, Collection<T> types,
	// long offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectAssociationsByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T type : types) {
	// ids[i++] = Long.parseLong(type.getId());
	// }
	// stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(topicMap, set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByTypeTransitive(ITopic type, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectNamesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<IName>
	// getNamesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectNamesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T type : types) {
	// ids[i++] = Long.parseLong(type.getId());
	// }
	// stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopic
	// type, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<IOccurrence>
	// getOccurrencesByTypeTransitive(ITopicMap topicMap, Collection<T> types,
	// long offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T type : types) {
	// ids[i++] = Long.parseLong(type.getId());
	// }
	// stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociationRole> getRolesByTypeTransitive(ITopic type,
	// long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectRolesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toRoles(type.getTopicMap(), set, "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<IAssociationRole>
	// getRolesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long
	// offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectRolesByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T type : types) {
	// ids[i++] = Long.parseLong(type.getId());
	// }
	// stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toRoles(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getTopicsByTypeTransitive(ITopic type, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectTopicsByTypeTransitive();
	// stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
	// stmt.setLong(2, Long.parseLong(type.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toTopics(type.getTopicMap(), set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<ITopic>
	// getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> types,
	// boolean all, long offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectTopicsByTypesTransitive();
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T type : types) {
	// ids[i++] = Long.parseLong(type.getId());
	// }
	// stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
	// stmt.setBoolean(2, all);
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toTopics(topicMap, set, "id");
	// }
	//
	// // ScopedIndex
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<IScope> getScopesByThemes(final
	// ITopicMap topicMap, Collection<T> themes, boolean all) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryScopesByThemesUsed();
	// Long ids[] = new Long[themes.size()];
	// int n = 0;
	// for (T theme : themes) {
	// ids[n++] = Long.parseLong(theme.getId());
	// }
	// stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
	// stmt.setBoolean(2, all);
	// stmt.setBoolean(3, false);
	// stmt.setLong(4, Long.parseLong(topicMap.getId()));
	// ResultSet rs = stmt.executeQuery();
	//
	// List<Long> list = HashUtil.getList();
	// while (rs.next()) {
	// list.add(rs.getLong("id"));
	// }
	// rs.close();
	//
	// /*
	// * read themes of the scopes
	// */
	// Collection<IScope> scopes = HashUtil.getHashSet();
	// for (Long id : list) {
	// scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
	// }
	// return scopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByScope(ITopicMap
	// topicMap, IScope scope, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryAssociationsByScope(offset
	// != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(scope.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(topicMap, set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByScopes(ITopicMap
	// topicMap, Collection<IScope> scopes, long offset, long limit) throws
	// SQLException {
	// /*
	// * if no scope is specified return empty set
	// */
	// if (scopes.isEmpty()) {
	// return HashUtil.getHashSet();
	// }
	// PreparedStatement stmt = queryBuilder.getQueryAssociationsByScopes(offset
	// != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (IScope s : scopes) {
	// stmt.setLong(n++, Long.parseLong(s.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(topicMap, set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByTheme(ITopicMap
	// topicMap, Topic theme, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// /*
	// * require empty scope
	// */
	// if (theme == null) {
	// stmt = queryBuilder.getQueryAssociationsByScope(offset != -1);
	// }
	// /*
	// * require non-empty scope
	// */
	// else {
	// stmt = queryBuilder.getQueryAssociationsByTheme(offset != -1);
	// stmt.setLong(2, Long.parseLong(theme.getId()));
	// }
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(topicMap, set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IAssociation> getAssociationsByThemes(ITopicMap
	// topicMap, Topic[] themes, boolean all, long offset, long limit) throws
	// SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryAssociationsByThemes(all,
	// offset != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (Topic theme : themes) {
	// stmt.setLong(n++, Long.parseLong(theme.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toAssociations(topicMap, set, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IScope> getAssociationScopes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryAssociationScopes(offset !=
	// -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet rs = stmt.executeQuery();
	// List<Long> ids = HashUtil.getList();
	// while (rs.next()) {
	// ids.add(rs.getLong("id_scope"));
	// }
	// rs.close();
	//
	// /*
	// * read themes of the scopes
	// */
	// Collection<IScope> scopes = HashUtil.getHashSet();
	// for (Long id : ids) {
	// scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
	// }
	// return scopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getAssociationThemes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryAssociationThemes(offset !=
	// -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_theme");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByScope(ITopicMap topicMap, IScope
	// scope, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryNamesByScope(false);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(scope.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByScopes(ITopicMap topicMap,
	// Collection<IScope> scopes, long offset, long limit) throws SQLException {
	// /*
	// * if no scope is specified return empty set
	// */
	// if (scopes.isEmpty()) {
	// return HashUtil.getHashSet();
	// }
	// PreparedStatement stmt = queryBuilder.getQueryNamesByScopes(offset !=
	// -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (IScope s : scopes) {
	// stmt.setLong(n++, Long.parseLong(s.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByTheme(ITopicMap topicMap, Topic theme,
	// long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// /*
	// * require empty scope
	// */
	// if (theme == null) {
	// stmt = queryBuilder.getQueryNamesByScope(true);
	// }
	// /*
	// * require non-empty scope
	// */
	// else {
	// stmt = queryBuilder.getQueryNamesByTheme(offset != -1);
	// stmt.setLong(2, Long.parseLong(theme.getId()));
	// }
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByThemes(ITopicMap topicMap, Topic[]
	// themes, boolean all, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryNamesByThemes(all, offset
	// != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (Topic theme : themes) {
	// stmt.setLong(n++, Long.parseLong(theme.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IScope> getNameScopes(ITopicMap topicMap, long offset,
	// long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryNameScopes(offset != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet rs = stmt.executeQuery();
	// List<Long> ids = HashUtil.getList();
	// while (rs.next()) {
	// ids.add(rs.getLong("id_scope"));
	// }
	// rs.close();
	//
	// /*
	// * read themes of the scopes
	// */
	// Collection<IScope> scopes = HashUtil.getHashSet();
	// for (Long id : ids) {
	// scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
	// }
	// return scopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getNameThemes(ITopicMap topicMap, long offset,
	// long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryNameThemes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_theme");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByScope(ITopicMap topicMap,
	// IScope scope, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryOccurrencesByScope(false);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(scope.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByScopes(ITopicMap topicMap,
	// Collection<IScope> scopes, long offset, long limit) throws SQLException {
	// /*
	// * if no scope is specified return empty set
	// */
	// if (scopes.isEmpty()) {
	// return HashUtil.getHashSet();
	// }
	// PreparedStatement stmt =
	// queryBuilder.getQueryOccurrencesByScopes(scopes.size());
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (IScope s : scopes) {
	// stmt.setLong(n++, Long.parseLong(s.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByTheme(ITopicMap topicMap,
	// Topic theme, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// /*
	// * require empty scope
	// */
	// if (theme == null) {
	// stmt = queryBuilder.getQueryOccurrencesByScope(true);
	// }
	// /*
	// * require non-empty scope
	// */
	// else {
	// stmt = queryBuilder.getQueryOccurrencesByTheme();
	// stmt.setLong(2, Long.parseLong(theme.getId()));
	// }
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByThemes(ITopicMap topicMap,
	// Topic[] themes, boolean all, long offset, long limit) throws SQLException
	// {
	// PreparedStatement stmt =
	// queryBuilder.getQueryOccurrencesByThemes(themes.length, all);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (Topic theme : themes) {
	// stmt.setLong(n++, Long.parseLong(theme.getId()));
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IScope> getOccurrenceScopes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryOccurrenceScopes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet rs = stmt.executeQuery();
	// List<Long> ids = HashUtil.getList();
	// while (rs.next()) {
	// ids.add(rs.getLong("id_scope"));
	// }
	// rs.close();
	//
	// /*
	// * read themes of the scopes
	// */
	// Collection<IScope> scopes = HashUtil.getHashSet();
	// for (Long id : ids) {
	// scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
	// }
	// return scopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getOccurrenceThemes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryOccurrenceThemes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_theme");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByScope(ITopicMap topicMap, IScope
	// scope, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryVariantsByScope();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(scope.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, set);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByScopes(ITopicMap topicMap,
	// Collection<IScope> scopes, long offset, long limit) throws SQLException {
	// /*
	// * if no scope is specified return empty set
	// */
	// if (scopes.isEmpty()) {
	// return HashUtil.getHashSet();
	// }
	// PreparedStatement stmt =
	// queryBuilder.getQueryVariantsByScopes(scopes.size());
	// Long ids[] = new Long[scopes.size()];
	// int n = 0;
	// for (IScope s : scopes) {
	// ids[n++] = Long.parseLong(s.getId());
	// }
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
	// stmt.setLong(3, Long.parseLong(topicMap.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, set);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByTheme(ITopicMap topicMap, Topic
	// theme, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryVariantsByTheme();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(theme.getId()));
	// stmt.setLong(3, Long.parseLong(theme.getId()));
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, set);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByThemes(ITopicMap topicMap,
	// Topic[] themes, boolean all, long offset, long limit) throws SQLException
	// {
	// PreparedStatement stmt =
	// queryBuilder.getQueryVariantsByThemes(themes.length, all);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// int n = 2;
	// for (Topic theme : themes) {
	// stmt.setLong(n, Long.parseLong(theme.getId()));
	// if (!all) {
	// stmt.setLong(n + themes.length, Long.parseLong(theme.getId()));
	// }
	// n++;
	// }
	// ResultSet set = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, set);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IScope> getVariantScopes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryVariantScopes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// ResultSet rs = stmt.executeQuery();
	// List<Long> ids = HashUtil.getList();
	// while (rs.next()) {
	// ids.add(rs.getLong("id_scope"));
	// }
	// rs.close();
	//
	// /*
	// * read themes of the scopes
	// */
	// Collection<IScope> scopes = HashUtil.getHashSet();
	// for (Long id : ids) {
	// scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
	// }
	// return scopes;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getVariantThemes(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQueryVariantThemes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_theme");
	// }
	//
	// // LiteralIndex
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNames(ITopicMap topicMap, String value)
	// throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectNamesByValue();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, value);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IName> getNamesByPattern(ITopicMap topicMap, String
	// pattern) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectNamesByPattern();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, pattern);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrences(ITopicMap topicMap,
	// Calendar lower, Calendar upper, long offset, long limit) throws
	// SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByDateRange(offset != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, XmlSchemeDatatypes.XSD_DATETIME);
	// stmt.setTimestamp(3, new Timestamp(lower.getTimeInMillis()));
	// stmt.setTimestamp(4, new Timestamp(upper.getTimeInMillis()));
	// return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id",
	// "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, String
	// value) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByValue();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, value);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByDatatype(ITopicMap
	// topicMap, String reference, long offset, long limit) throws SQLException
	// {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByDatatype(offset != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, reference);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap
	// topicMap, String pattern) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByPattern();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, pattern);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap
	// topicMap, String pattern, String reference) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectOccurrencesByPatternAndDatatype();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, pattern);
	// stmt.setString(3, reference);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariants(ITopicMap topicMap, String value)
	// throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByValue();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, value);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, rs);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariants(ITopicMap topicMap, String value,
	// String reference) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectVariantsByValueAndDatatype();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, value);
	// stmt.setString(3, reference);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, rs);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByDatatype(ITopicMap topicMap,
	// String reference) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByDatatype();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, reference);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, rs);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantByPattern(ITopicMap topicMap,
	// String pattern) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByPattern();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, pattern);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, rs);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IVariant> getVariantsByPattern(ITopicMap topicMap,
	// String pattern, String reference) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectVariantsByPatternAndDatatype();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, pattern);
	// stmt.setString(3, reference);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toVariants(topicMap, rs);
	// }
	//
	// // IdentityIndex
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ILocator> getItemIdentifiers(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectItemIdentifiers();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ILocator> getSubjectIdentifiers(ITopicMap topicMap,
	// long offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSubjectIdentifiers();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ILocator> getSubjectLocators(ITopicMap topicMap, long
	// offset, long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSubjectLocators();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IConstruct> getConstructsByIdentitifer(ITopicMap
	// topicMap, String regExp, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectConstructsByIdentitifer();
	// long topicMapId = Long.parseLong(topicMap.getId());
	// String pattern = "^" + regExp + "$";
	// stmt.setLong(1, topicMapId);
	// stmt.setString(2, pattern);
	// stmt.setLong(3, topicMapId);
	// stmt.setString(4, pattern);
	// stmt.setLong(5, topicMapId);
	// stmt.setLong(6, topicMapId);
	// stmt.setString(7, pattern);
	// ResultSet rs = stmt.executeQuery();
	// Collection<IConstruct> set = HashUtil.getHashSet();
	// Collection<Long> ids = HashUtil.getHashSet();
	// while (rs.next()) {
	// if (rs.getString("type").equalsIgnoreCase("t")) {
	// set.add(new TopicImpl(new JdbcIdentity(rs.getString("id")), topicMap));
	// } else {
	// ids.add(rs.getLong("id"));
	// }
	// }
	// rs.close();
	// for (Long id : ids) {
	// set.add(doReadConstruct(topicMap, Long.toString(id), false));
	// }
	// return set;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<IConstruct> getConstructsByItemIdentitifer(ITopicMap
	// topicMap, String regExp, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectConstructsByItemIdentitifer();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setLong(2, Long.parseLong(topicMap.getId()));
	// stmt.setString(3, "^" + regExp + "$");
	// Collection<Long> ids = HashUtil.getHashSet();
	// ResultSet rs = stmt.executeQuery();
	// while (rs.next()) {
	// ids.add(rs.getLong("id_construct"));
	// }
	// rs.close();
	// Collection<IConstruct> set = HashUtil.getHashSet();
	// for (Long id : ids) {
	// set.add(doReadConstruct(topicMap, Long.toString(id), false));
	// }
	// return set;
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getTopicsBySubjectIdentitifer(ITopicMap
	// topicMap, String regExp, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectTopicsBySubjectIdentitifer();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, "^" + regExp + "$");
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_topic");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getTopicsBySubjectLocator(ITopicMap topicMap,
	// String regExp, long offset, long limit) throws SQLException {
	// PreparedStatement stmt =
	// queryBuilder.getQuerySelectTopicsBySubjectLocator();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// stmt.setString(2, "^" + regExp + "$");
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_topic");
	// }
	//
	// // SupertypeSubtypeIndex
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getDirectSubtypes(ITopicMap topicMap, ITopic
	// type, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// if (type == null) {
	// stmt = queryBuilder.getQuerySelectTopicsWithoutSubtypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// } else {
	// stmt = queryBuilder.getQuerySelectDirectSubtypes();
	// stmt.setLong(1, Long.parseLong(type.getId()));
	// }
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getDirectSupertypes(ITopicMap topicMap, ITopic
	// type, long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// if (type == null) {
	// stmt = queryBuilder.getQuerySelectTopicsWithoutSupertypes(false);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// } else {
	// stmt = queryBuilder.getQuerySelectDirectSupertypes();
	// stmt.setLong(1, Long.parseLong(type.getId()));
	// }
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getSubtypes(ITopicMap topicMap, ITopic type,
	// long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// if (type == null) {
	// stmt = queryBuilder.getQuerySelectTopicsWithoutSubtypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// } else {
	// stmt = queryBuilder.getQuerySelectSubtypesOfTopic();
	// stmt.setLong(1, Long.parseLong(type.getId()));
	// }
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getSubtypes(ITopicMap topicMap, long offset,
	// long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSubtypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_subtype");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<ITopic> getSubtypes(ITopicMap
	// topicMap, Collection<T> types, boolean matchAll, long offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSubtypesOfTopics();
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T topic : types) {
	// ids[i++] = Long.parseLong(topic.getId());
	// }
	// stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
	// stmt.setBoolean(2, matchAll);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toTopics(topicMap, rs, "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getSupertypes(ITopicMap topicMap, ITopic type,
	// long offset, long limit) throws SQLException {
	// PreparedStatement stmt = null;
	// if (type == null) {
	// stmt = queryBuilder.getQuerySelectTopicsWithoutSupertypes(offset != -1);
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// } else {
	// stmt = queryBuilder.getQuerySelectSupertypesOfTopic();
	// stmt.setLong(1, Long.parseLong(type.getId()));
	// }
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public Collection<ITopic> getSupertypes(ITopicMap topicMap, long offset,
	// long limit) throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSupertypes();
	// stmt.setLong(1, Long.parseLong(topicMap.getId()));
	// return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(),
	// "id_supertype");
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public <T extends Topic> Collection<ITopic> getSupertypes(ITopicMap
	// topicMap, Collection<T> types, boolean matchAll, long offset, long limit)
	// throws SQLException {
	// PreparedStatement stmt = queryBuilder.getQuerySelectSupertypesOfTopics();
	// Long ids[] = new Long[types.size()];
	// int i = 0;
	// for (T topic : types) {
	// ids[i++] = Long.parseLong(topic.getId());
	// }
	// stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
	// stmt.setBoolean(2, matchAll);
	// ResultSet rs = stmt.executeQuery();
	// return Jdbc2Construct.toTopics(topicMap, rs, "id");
	// }
}
