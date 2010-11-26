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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryProcessor;
import de.topicmapslab.majortom.database.jdbc.util.Jdbc2Construct;
import de.topicmapslab.majortom.database.store.JdbcIdentity;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryProcessor extends Sql99QueryProcessor {

	/**
	 * constructor
	 * 
	 * @param session
	 *            the session
	 * @param connection
	 *            the database connection of this query processor
	 */
	public PostGreSqlQueryProcessor(PostGreSqlSession session, Connection connection) {
		super(session, connection);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public PostGreSqlSession getSession() {
		return (PostGreSqlSession) super.getSession();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Sql99QueryBuilder createQueryBuilder() {
		return new PostGreSqlQueryBuilder(getSession());
	}

	/**
	 * {@inheritDoc}
	 */
	protected PostGreSqlQueryBuilder getQueryBuilder() {
		return (PostGreSqlQueryBuilder) super.getQueryBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t, long offset, long limit) throws SQLException {
		/*
		 * check if optimization procedure exists
		 */
		if (getSession().getConnectionProvider().existsProcedureTransitiveSupertypes()) {
			Collection<ITopic> set = HashUtil.getHashSet(getSupertypes(t.getTopicMap(), t, offset, limit));
			return set;
		}
		return super.doReadSuptertypes(t, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope readScopeByThemes(ITopicMap topicMap, Collection<ITopic> themes) throws SQLException {
		if (themes.isEmpty()) {
			PreparedStatement stmt = getQueryBuilder().getQueryReadEmptyScope();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			ResultSet set = stmt.executeQuery();
			if (set.next()) {
				long id = set.getLong("id");
				set.close();
				return new ScopeImpl(Long.toString(id));
			}
			return null;
		}
		/*
		 * check if optimisation method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureScopeByThemes()) {
			return super.readScopeByThemes(topicMap, themes);
		}
		PreparedStatement stmt = getQueryBuilder().getQueryReadScopeByThemes();
		List<Long> ids = HashUtil.getList();
		for (ITopic theme : themes) {
			ids.add(Long.parseLong(theme.getId()));
		}
		Collections.sort(ids);
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids.toArray(new Long[0])));
		stmt.setBoolean(2, true);
		stmt.setBoolean(3, true);
		stmt.setLong(4, Long.parseLong(topicMap.getId()));
		Collection<IScope> scopes = Jdbc2Construct.toScopes(topicMap, stmt.executeQuery());
		if (scopes.isEmpty()) {
			return null;
		}
		return scopes.iterator().next();
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws SQLException {
		if (getSession().getConnectionProvider().existsProcedureBestLabel()) {
			PreparedStatement stmt = ((PostGreSqlQueryBuilder) getQueryBuilder()).getQueryReadBestLabel();
			stmt.setLong(1, Long.parseLong(topic.getTopicMap().getId()));
			stmt.setLong(2, Long.parseLong(topic.getId()));
			ResultSet set = stmt.executeQuery();
			if (set.next()) {
				String s = set.getString(1);
				set.close();
				return s;
			}
			return "An error occurred";
		}
		return super.doReadBestLabel(topic);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws SQLException {
		if (getSession().getConnectionProvider().existsProcedureBestLabelWithTheme()) {
			PreparedStatement stmt = ((PostGreSqlQueryBuilder) getQueryBuilder()).getQueryReadBestLabelWithTheme();
			stmt.setLong(1, Long.parseLong(topic.getTopicMap().getId()));
			stmt.setLong(2, Long.parseLong(topic.getId()));
			stmt.setLong(3, Long.parseLong(theme.getId()));
			stmt.setBoolean(4, strict);
			ResultSet set = stmt.executeQuery();
			if (set.next()) {
				String s = set.getString(1);
				set.close();
				return s;
			}
			return null;
		}
		return super.doReadBestLabel(topic);
	}

	// ****************
	// * INDEX METHOD *
	// ****************

	// TransitiveTypeInstanceIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getAssociationsByTypeTransitive(type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectAssociationsByTypeTransitive(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toAssociations(type.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociation> getAssociationsByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getAssociationsByTypeTransitive(topicMap, types, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectAssociationsByTypeTransitive(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toAssociations(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> getRolesByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getRolesByTypeTransitive(type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectRolesByTypeTransitive(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toRoles(type.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociationRole> getRolesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getRolesByTypeTransitive(topicMap, types, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectRolesByTypeTransitive(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toRoles(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getNamesByTypeTransitive(type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByTypeTransitive(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toNames(type.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IName> getNamesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getNamesByTypeTransitive(topicMap, types, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByTypeTransitive(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toNames(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getOccurrencesByTypeTransitive(type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByTypeTransitive(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toOccurrences(type.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getOccurrencesByTypeTransitive(topicMap, types, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByTypeTransitive(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getTopicsByTypeTransitive(type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicsByTypeTransitive(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(type.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> types, boolean all, long offset, long limit) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTopicsByTypeTransitive()) {
			return super.getTopicsByTypesTransitive(topicMap, types, all, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicsByTypesTransitive(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, all);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	// SupertypeSubtypeIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSubtypes(ITopicMap topicMap, ITopic type, long offset, long limit) throws SQLException {
		if (type == null) {
			PreparedStatement stmt = null;
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSubtypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			if (offset != -1) {
				stmt.setLong(2, offset);
				stmt.setLong(3, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		}
		/*
		 * check if optimisation method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTransitiveSubtypes()) {
			return super.getSubtypes(topicMap, type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSubtypesOfTopic(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(type.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSubtypes(ITopicMap topicMap, Collection<T> types, boolean matchAll, long offset, long limit) throws SQLException {
		if (types.isEmpty()) {
			PreparedStatement stmt = null;
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSubtypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			if (offset != -1) {
				stmt.setLong(2, offset);
				stmt.setLong(3, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		}
		/*
		 * check if optimisation method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTransitiveSubtypesArray()) {
			return super.getSubtypes(topicMap, types, matchAll, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSubtypesOfTopics(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, matchAll);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSupertypes(ITopicMap topicMap, ITopic type, long offset, long limit) throws SQLException {
		if (type == null) {
			PreparedStatement stmt = null;
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSupertypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			if (offset != -1) {
				stmt.setLong(2, offset);
				stmt.setLong(3, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		}
		/*
		 * check if optimisation method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTransitiveSupertypes()) {
			return super.getSupertypes(topicMap, type, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSupertypesOfTopic(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(type.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSupertypes(ITopicMap topicMap, Collection<T> types, boolean matchAll, long offset, long limit) throws SQLException {
		if (types.isEmpty()) {
			PreparedStatement stmt = null;
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSupertypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			if (offset != -1) {
				stmt.setLong(2, offset);
				stmt.setLong(3, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		}
		/*
		 * check if optimisation method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureTransitiveSupertypesArray()) {
			return super.getSupertypes(topicMap, types, matchAll, offset, limit);
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSupertypesOfTopics(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, matchAll);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	// ScopedIndex

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IScope> getScopesByThemes(final ITopicMap topicMap, Collection<T> themes, boolean all) throws SQLException {
		/*
		 * check if optimization method exists
		 */
		if (!getSession().getConnectionProvider().existsProcedureScopeByThemes()) {
			return super.getScopesByThemes(topicMap, themes, all);
		}
		PreparedStatement stmt = getQueryBuilder().getQueryScopesByThemesUsed();
		List<Long> ids = HashUtil.getList();
		for (T theme : themes) {
			ids.add(Long.parseLong(theme.getId()));
		}
		Collections.sort(ids);
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids.toArray(new Long[0])));
		stmt.setBoolean(2, all);
		stmt.setBoolean(3, false);
		stmt.setLong(4, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toScopes(topicMap, stmt.executeQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	public void doMergeTopics(ITopic context, ITopic other) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getPerformMergeTopics();
		long idContext = Long.parseLong(context.getId());
		long idOther = Long.parseLong(other.getId());
		int max = 12;
		for (int n = 0; n < max; n++) {
			stmt.setLong(n * 2 + 1, idContext);
			stmt.setLong(n * 2 + 2, idOther);
		}
		stmt.setLong(max * 2 + 1, idOther);
		stmt.execute();
		doRemoveDuplicateTopicContent(context);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canPerformRemoveDuplicates() {
		return getSession().getConnectionProvider().existsProcedureRemoveDuplicates();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveDuplicates() throws SQLException, UnsupportedOperationException {
		if (!canPerformRemoveDuplicates()) {
			throw new UnsupportedOperationException("The function 'remove_dupliates' is not provided by the underlying database!");
		}
		PreparedStatement stmt = getQueryBuilder().getPerformRemoveDuplicates();
		stmt.setLong(1, getSession().getTopicMapStore().getTopicMapIdentity().longId());
		stmt.execute();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void doRemoveDuplicateTopicContent(ITopic topic) throws SQLException, UnsupportedOperationException {
		if (!getSession().getConnectionProvider().existsProcedureRemoveDuplicateTopicContent()) {
			getSession().getTopicMapStore().removeDuplicates();
		}
		PreparedStatement stmt = getQueryBuilder().getPerformRemoveDuplicateTopicContent();
		stmt.setLong(1, getSession().getTopicMapStore().getTopicMapIdentity().longId());
		stmt.setLong(2, ((JdbcIdentity)((ConstructImpl)topic).getIdentity()).longId());
		stmt.execute();
	}
}
