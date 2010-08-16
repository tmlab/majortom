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

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryProcessor;
import de.topicmapslab.majortom.database.jdbc.util.Jdbc2Construct;
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
	 * @param connection
	 *            the JDBC connection
	 */
	public PostGreSqlQueryProcessor(IConnectionProvider provider, Connection connection) {
		super(provider, connection);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Sql99QueryBuilder createQueryBuilder() {
		return new PostGreSqlQueryBuilder(getConnection());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadSupertypes();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id");
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

	// ****************
	// * INDEX METHOD *
	// ****************

	// TransitiveTypeInstanceIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
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
	public <T extends Topic> Collection<IAssociation> getAssociationsByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit)
			throws SQLException {
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
	public <T extends Topic> Collection<IAssociationRole> getRolesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit)
			throws SQLException {
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
	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopicMap topicMap, Collection<T> types, long offset, long limit)
			throws SQLException {
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
	public <T extends Topic> Collection<ITopic> getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> types, boolean all, long offset, long limit)
			throws SQLException {
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
	public <T extends Topic> Collection<ITopic> getSubtypes(ITopicMap topicMap, Collection<T> types, boolean matchAll, long offset, long limit)
			throws SQLException {
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
		if (type == null ) {
			PreparedStatement stmt = null;
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSupertypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			if (offset != -1) {
				stmt.setLong(2, offset);
				stmt.setLong(3, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
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
	public <T extends Topic> Collection<ITopic> getSupertypes(ITopicMap topicMap, Collection<T> types, boolean matchAll, long offset, long limit)
			throws SQLException {
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
}
