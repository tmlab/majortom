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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryProcessor;
import de.topicmapslab.majortom.database.jdbc.util.Jdbc2Construct;
import de.topicmapslab.majortom.database.store.JdbcIdentity;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

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

	/**
	 * {@inheritDoc}
	 */
	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryCreateAssociation();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.execute();
		return Jdbc2Construct.toAssociation(topicMap, stmt.getGeneratedKeys(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws SQLException {
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateAssociationWithScope();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setLong(4, Long.parseLong(scope.getId()));
		stmt.execute();
		return Jdbc2Construct.toAssociation(topicMap, stmt.getGeneratedKeys(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doCreateLocator(ITopicMap topicMap, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryCreateLocator();
		stmt.setString(1, reference);
		stmt.setString(2, reference);
		stmt.execute();
		return new LocatorImpl(reference);
	}

	/**
	 * {@inheritDoc}
	 */
	public IName doCreateName(ITopic topic, String value) throws SQLException {
		ILocator loc = doCreateLocator(topic.getTopicMap(), TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic type = doReadTopicBySubjectIdentifier(topic.getTopicMap(), loc);
		if (type == null) {
			type = doCreateTopicBySubjectIdentifier(topic.getTopicMap(), loc);
		}
		return doCreateName(topic, type, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws SQLException {
		ILocator loc = doCreateLocator(topic.getTopicMap(), TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic type = doReadTopicBySubjectIdentifier(topic.getTopicMap(), loc);
		if (type == null) {
			type = doCreateTopicBySubjectIdentifier(topic.getTopicMap(), loc);
		}
		return doCreateName(topic, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public IName doCreateName(ITopic topic, ITopic type, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryCreateName();
		ITopicMap topicMap = topic.getTopicMap();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.execute();
		return Jdbc2Construct.toName(topic, stmt.getGeneratedKeys(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateNameWithScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setLong(5, Long.parseLong(scope.getId()));
		stmt.execute();
		return Jdbc2Construct.toName(topic, stmt.getGeneratedKeys(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrence();
		stmt.setString(1, XmlSchemeDatatypes.XSD_STRING);
		stmt.setString(2, XmlSchemeDatatypes.XSD_STRING);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value);
		stmt.setString(7, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrenceWithScope();
		stmt.setString(1, XmlSchemeDatatypes.XSD_STRING);
		stmt.setString(2, XmlSchemeDatatypes.XSD_STRING);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value);
		stmt.setLong(7, Long.parseLong(scope.getId()));
		stmt.setString(8, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrence();
		stmt.setString(1, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setString(2, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value.getReference());
		stmt.setString(7, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrenceWithScope();
		stmt.setString(1, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setString(2, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value.getReference());
		stmt.setLong(7, Long.parseLong(scope.getId()));
		stmt.setString(8, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrence();
		stmt.setString(1, datatype.getReference());
		stmt.setString(2, datatype.getReference());
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value);
		stmt.setString(7, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateOccurrenceWithScope();
		stmt.setString(1, datatype.getReference());
		stmt.setString(2, datatype.getReference());
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topic.getId()));
		stmt.setLong(5, Long.parseLong(type.getId()));
		stmt.setString(6, value);
		stmt.setLong(7, Long.parseLong(scope.getId()));
		stmt.setString(8, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws SQLException {
		ITopicMap topicMap = association.getTopicMap();
		PreparedStatement stmt = queryBuilder.getQueryCreateRole();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(association.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setLong(4, Long.parseLong(player.getId()));
		stmt.execute();
		return Jdbc2Construct.toRole(association, stmt.getGeneratedKeys(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws SQLException {
		/*
		 * read scope by themes
		 */
		PreparedStatement stmt = queryBuilder.getQueryReadScopeByThemes();
		/*
		 * set topic map if empty scope is required
		 */
		List<Long> ids = HashUtil.getList();
		for (ITopic theme : themes) {
			ids.add(Long.parseLong(theme.getId()));
		}
		Collections.sort(ids);
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids.toArray(new Long[0])));
		stmt.setBoolean(2, true);
		stmt.setBoolean(3, true);
		stmt.setLong(4, Long.parseLong(topicMap.getId()));
		/*
		 * query for scope
		 */
		ResultSet set = stmt.executeQuery();
		if (set.next()) {
			long id = set.getLong(1);
			set.close();
			return new ScopeImpl(Long.toString(id), themes);
		}
		/*
		 * create scope
		 */
		stmt = queryBuilder.getQueryCreateScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.execute();
		/*
		 * get generated scope id
		 */
		set = stmt.getGeneratedKeys();
		set.next();
		long id = set.getLong("id");
		set.close();
		/*
		 * add all themes
		 */
		stmt = queryBuilder.getQueryAddThemes(themes.size());
		int i = 0;
		for (ITopic theme : themes) {
			stmt.setLong(i * 2 + 1, id);
			stmt.setLong(i * 2 + 2, Long.parseLong(theme.getId()));
			i++;
		}
		stmt.execute();
		stmt.close();
		/*
		 * create scope instance
		 */
		return new ScopeImpl(Long.toString(id), themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doCreateTopicMap(ILocator baseLocator) throws SQLException {
		/*
		 * get string to create new topic map
		 */
		PreparedStatement stmt = queryBuilder.getQueryCreateTopicMap();
		stmt.setString(1, baseLocator.getReference());
		stmt.setString(2, baseLocator.getReference());
		stmt.setString(3, baseLocator.getReference());
		stmt.setString(4, baseLocator.getReference());
		stmt.execute();
		/*
		 * get result set containing generated keys
		 */
		ResultSet set = stmt.getResultSet();
		/*
		 * check if topic map was created
		 */
		if (set != null && set.next()) {
			return set.getString("id");
		}
		/*
		 * topic map already exists -> get topic map id by base locator
		 */
		stmt = queryBuilder.getQueryReadTopicMap();
		stmt.setString(1, baseLocator.getReference());
		set = stmt.executeQuery();
		set.next();
		final String id = set.getString("id");
		set.close();
		return id;
	}

	/**
	 * Internal method to create a new topic
	 * 
	 * @param topicMap
	 *            the topic map
	 * @return the new topic and never <code>null</code>
	 * @throws SQLException
	 *             thrown if any SQL error occurs
	 */
	public ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws SQLException {
		/*
		 * get string to create new topic
		 */
		PreparedStatement stmt = queryBuilder.getQueryCreateTopic();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		stmt.execute();
		/*
		 * get result set containing generated keys
		 */
		ResultSet set = stmt.getGeneratedKeys();
		/*
		 * check if topic was created
		 */
		if (set != null && set.next()) {
			ITopic topic = new TopicImpl(new JdbcIdentity(set.getString("id")), topicMap);
			set.close();
			return topic;
		}
		throw new TopicMapStoreException("Internal SQL error, missing result 'id'.");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws SQLException {
		/*
		 * create the topic instance
		 */
		ITopic topic = doCreateTopicWithoutIdentifier(topicMap);
		/*
		 * add item-identifier
		 */
		doModifyItemIdentifier(topic, itemIdentifier);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws SQLException {
		/*
		 * create the topic instance
		 */
		ITopic topic = doCreateTopicWithoutIdentifier(topicMap);
		/*
		 * add subject-identifier
		 */
		doModifySubjectIdentifier(topic, subjectIdentifier);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws SQLException {
		/*
		 * create the topic instance
		 */
		ITopic topic = doCreateTopicWithoutIdentifier(topicMap);
		/*
		 * add subject-locator
		 */
		doModifySubjectLocator(topic, subjectLocator);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateVariant();
		stmt.setString(1, XmlSchemeDatatypes.XSD_STRING);
		stmt.setString(2, XmlSchemeDatatypes.XSD_STRING);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(name.getId()));
		stmt.setString(5, value);
		stmt.setLong(6, Long.parseLong(scope.getId()));
		stmt.setString(7, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateVariant();
		stmt.setString(1, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setString(2, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(name.getId()));
		stmt.setString(5, value.getReference());
		stmt.setLong(6, Long.parseLong(scope.getId()));
		stmt.setString(7, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = queryBuilder.getQueryCreateVariant();
		stmt.setString(1, datatype.getReference());
		stmt.setString(2, datatype.getReference());
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(name.getId()));
		stmt.setString(5, value);
		stmt.setLong(6, Long.parseLong(scope.getId()));
		stmt.setString(7, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getResultSet(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doMergeTopicMaps(TopicMap context, TopicMap other) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doMergeTopics(ITopic context, ITopic other) throws SQLException {
		PreparedStatement stmt = queryBuilder.getPerformMergeTopics();
		stmt.setLong(1, Long.parseLong(context.getId()));
		stmt.setLong(2, Long.parseLong(other.getId()));
		stmt.execute();
		// long idContext = Long.parseLong(context.getId());
		// long idOther = Long.parseLong(other.getId());
		// int max = 12;
		// for (int n = 0; n < max; n++) {
		// stmt.setLong(n * 2 + 1, idContext);
		// stmt.setLong(n * 2 + 2, idOther);
		// }
		// stmt.setLong(max * 2 + 1, idOther);
		// stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAddItemIdentifier();
		stmt.setString(1, itemIdentifier.getReference());
		stmt.setString(2, itemIdentifier.getReference());
		stmt.setLong(3, Long.parseLong(c.getId()));
		stmt.setString(4, itemIdentifier.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyPlayer(IAssociationRole role, ITopic player) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyPlayer();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(role.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyReifier(IReifiable r, ITopic reifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyReifier();
		if (reifier == null) {
			stmt.setNull(1, Types.BIGINT);
		} else {
			stmt.setLong(1, Long.parseLong(reifier.getId()));
		}
		stmt.setLong(2, Long.parseLong(r.getId()));
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyScope(IScopable s, ITopic theme) throws SQLException {
		Collection<ITopic> themes = HashUtil.getHashSet(s.getScopeObject().getThemes());
		themes.add(theme);
		IScope scope = doCreateScope(s.getTopicMap(), themes);
		PreparedStatement stmt = queryBuilder.getQueryModifyScope();
		stmt.setLong(1, Long.parseLong(scope.getId()));
		stmt.setLong(2, Long.parseLong(s.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAddSubjectIdentifier();
		stmt.setString(1, subjectIdentifier.getReference());
		stmt.setString(2, subjectIdentifier.getReference());
		stmt.setLong(3, Long.parseLong(t.getId()));
		stmt.setString(4, subjectIdentifier.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAddSubjectLocator();
		stmt.setString(1, subjectLocator.getReference());
		stmt.setString(2, subjectLocator.getReference());
		stmt.setLong(3, Long.parseLong(t.getId()));
		stmt.setString(4, subjectLocator.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySupertype(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifySupertypes();
		long subtypeId = Long.parseLong(t.getId());
		long supertypeId = Long.parseLong(type.getId());
		stmt.setLong(1, subtypeId);
		stmt.setLong(2, supertypeId);
		stmt.setLong(3, subtypeId);
		stmt.setLong(4, supertypeId);
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyTag(ITopicMap tm, String tag) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyType(ITypeable t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyType(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyTypes();
		long instanceId = Long.parseLong(t.getId());
		long typeId = Long.parseLong(type.getId());
		stmt.setLong(1, instanceId);
		stmt.setLong(2, typeId);
		stmt.setLong(3, instanceId);
		stmt.setLong(4, typeId);
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyValue(IName n, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyValue();
		stmt.setString(1, value);
		stmt.setLong(2, Long.parseLong(n.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyValueWithDatatype();
		stmt.setString(1, value);
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setString(3, datatype.getReference());
		stmt.setString(4, datatype.getReference());
		stmt.setString(5, datatype.getReference());
		stmt.setLong(6, Long.parseLong(t.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociation();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

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
	public Collection<IAssociation> doReadAssociation(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithScope();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociation();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
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
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t));
		set.addAll(doReadOccurrences(t));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type));
		set.addAll(doReadOccurrences(t, type));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type, scope));
		set.addAll(doReadOccurrences(t, type, scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, scope));
		set.addAll(doReadOccurrences(t, scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet set = null;
		long id = Long.parseLong(t.getId());
		stmt = queryBuilder.getQueryReadConstructByItemIdentifier();
		stmt.setLong(1, id);
		stmt.setLong(2, id);
		stmt.setString(3, itemIdentifier.getReference());
		set = stmt.executeQuery();
		if (set.next()) {
			final String id_ = set.getString("id");
			set.close();
			return doReadConstruct(t, id_, false);
		}
		set.close();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware d) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadDataType();
		stmt.setLong(1, Long.parseLong(d.getId()));
		return Jdbc2Construct.toLocator(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadItemIdentifiers(IConstruct c) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadItemIdentifiers();
		stmt.setLong(1, Long.parseLong(c.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadLocator(ITopicMap t) throws SQLException {
		return (ILocator) t.getLocator();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNames();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
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
	public Collection<IName> doReadNames(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrences();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
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
	public Collection<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrencesWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadPlayer(IAssociationRole role) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayer();
		stmt.setLong(1, Long.parseLong(role.getId()));
		return Jdbc2Construct.toTopic(role.getTopicMap(), stmt.executeQuery(), "id_player");
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadReified();
		stmt.setLong(1, Long.parseLong(t.getId()));
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			String id = result.getString("id");
			result.close();
			return (IReifiable) doReadConstruct(t.getTopicMap(), id, false);
		}
		result.close();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadReifier();
		stmt.setLong(1, Long.parseLong(r.getId()));
		ResultSet result = stmt.executeQuery();
		if (result.next()) {
			String id = result.getString("id_reifier");
			result.close();
			if (id != null && !"null".equalsIgnoreCase(id)) {
				return new TopicImpl(new JdbcIdentity(id), r.getTopicMap());
			}
		}
		result.close();
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadRoleTypes(IAssociation association) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRoleTypes();
		stmt.setLong(1, Long.parseLong(association.getId()));
		return Jdbc2Construct.toTopics(association.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(IAssociation association) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRoles();
		stmt.setLong(1, Long.parseLong(association.getId()));
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
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
	public Collection<IAssociationRole> doReadRoles(ITopic player) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedRoles();
		stmt.setLong(1, Long.parseLong(player.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
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
	public IScope doReadScope(IScopable s) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadScope();
		stmt.setLong(1, Long.parseLong(s.getId()));
		ResultSet set = stmt.executeQuery();
		set.next();
		long scopeId = set.getLong("id_scope");
		set.close();
		return new ScopeImpl(Long.toString(scopeId), doReadThemes(s.getTopicMap(), scopeId));
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadThemes(ITopicMap topicMap, long scopeId) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadThemes();
		stmt.setLong(1, scopeId);
		ResultSet set = stmt.executeQuery();
		Collection<ITopic> themes = HashUtil.getHashSet();
		while (set.next()) {
			themes.add(new TopicImpl(new JdbcIdentity(set.getString("id_theme")), topicMap));
		}
		set.close();
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadSubjectIdentifiers(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSubjectIdentifiers();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadSubjectLocators(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSubjectLocators();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSupertypes();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopicBySubjectIdentifier();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectIdentifier.getReference());
		ResultSet set = stmt.executeQuery();
		if (set.next()) {
			return new TopicImpl(new JdbcIdentity(set.getString("id")), t);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopicBySubjectLocator();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectLocator.getReference());
		ResultSet set = stmt.executeQuery();
		if (set.next()) {
			return new TopicImpl(new JdbcIdentity(set.getString("id")), t);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTopics(ITopicMap t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopics();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopicsWithType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id_instance");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadType();
		stmt.setLong(1, Long.parseLong(typed.getId()));
		return Jdbc2Construct.toTopic(typed.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTypes(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTypes();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadValue();
		stmt.setLong(1, Long.parseLong(n.getId()));
		ResultSet result = stmt.executeQuery();
		result.next();
		String value = result.getString("value");
		result.close();
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadValue();
		stmt.setLong(1, Long.parseLong(t.getId()));
		ResultSet result = stmt.executeQuery();
		result.next();
		String value = result.getString("value");
		result.close();
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> doReadVariants(IName n) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadVariants();
		stmt.setLong(1, Long.parseLong(n.getId()));
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> doReadVariants(IName n, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadVariantsWithScope();
		stmt.setLong(1, Long.parseLong(n.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveAssociation(IAssociation association, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteAssociation();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.execute();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteItemIdentifier();
		stmt.setLong(1, Long.parseLong(c.getId()));
		stmt.setString(2, itemIdentifier.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveName(IName name, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteName();
		stmt.setLong(1, Long.parseLong(name.getId()));
		stmt.execute();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveScope(IScopable s, ITopic theme) throws SQLException {
		IScope oldScope = doReadScope(s);
		Collection<ITopic> themes = HashUtil.getHashSet(oldScope.getThemes());
		themes.remove(theme);
		IScope scope = doCreateScope(s.getTopicMap(), themes);
		PreparedStatement stmt = queryBuilder.getQueryModifyScope();
		stmt.setLong(1, Long.parseLong(scope.getId()));
		stmt.setLong(2, Long.parseLong(s.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteSubjectIdentifier();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectIdentifier.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteSubjectLocator();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectLocator.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSupertype(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteSupertype();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveTopic(ITopic topic, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteTopic();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		stmt.execute();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteTopicMap();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		stmt.setLong(4, Long.parseLong(topicMap.getId()));
		stmt.setLong(5, Long.parseLong(topicMap.getId()));
		stmt.execute();
		// TODO remove all prepared statements
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveType(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.execute();
	}

	// ****************
	// * INDEX METHOD *
	// ****************

	// TypeInstanceIndex

	public Collection<ITopic> getAssociationTypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectAssociationTypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getNameTypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNameTypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getOccurrenceTypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrenceTypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getRoleTypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectRoleTypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicTypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicTypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByType(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectAssociationsByType();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(type.getTopicMap(), set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByType(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNamesByType();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByType(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByType();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> getRolesByType(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectRolesByType();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toRoles(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByTypes(Collection<T> types, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsByTypes(types.size(), all);
		ITopicMap topicMap = null;
		int n = 2;
		for (T type : types) {
			topicMap = (ITopicMap) type.getTopicMap();
			stmt.setLong(n++, Long.parseLong(type.getId()));
		}
		/*
		 * empty type set
		 */
		if (topicMap == null) {
			return HashUtil.getHashSet();
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_instance");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByType(ITopicMap topicMap, T type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsByTypes(type == null ? 0 : 1, true);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (type != null) {
			stmt.setLong(2, Long.parseLong(type.getId()));
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_instance");
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	// TransitiveTypeInstanceIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectAssociationsByTypeTransitive();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(type.getTopicMap(), set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociation> getAssociationsByTypeTransitive(ITopicMap topicMap, Collection<T> types) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectAssociationsByTypeTransitive();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T type : types) {
			ids[i++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByTypeTransitive(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNamesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IName> getNamesByTypeTransitive(ITopicMap topicMap, Collection<T> types) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNamesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T type : types) {
			ids[i++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopicMap topicMap, Collection<T> types) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T type : types) {
			ids[i++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> getRolesByTypeTransitive(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectRolesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toRoles(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociationRole> getRolesByTypeTransitive(ITopicMap topicMap, Collection<T> types) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectRolesByTypeTransitive();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T type : types) {
			ids[i++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toRoles(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsByTypeTransitive(ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsByTypeTransitive();
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toTopics(type.getTopicMap(), set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> types, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsByTypesTransitive();
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T type : types) {
			ids[i++] = Long.parseLong(type.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, all);
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toTopics(topicMap, set, "id");
	}

	// ScopedIndex

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IScope> getScopesByThemes(final ITopicMap topicMap, Collection<T> themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryScopesByThemesUsed();
		Long ids[] = new Long[themes.size()];
		int n = 0;
		for (T theme : themes) {
			ids[n++] = Long.parseLong(theme.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, all);
		stmt.setBoolean(3, false);
		stmt.setLong(4, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();

		List<Long> list = HashUtil.getList();
		while (rs.next()) {
			list.add(rs.getLong("id"));
		}
		rs.close();

		/*
		 * read themes of the scopes
		 */
		Collection<IScope> scopes = HashUtil.getHashSet();
		for (Long id : list) {
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByScope(ITopicMap topicMap, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAssociationsByScope(false);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByScopes(ITopicMap topicMap, Collection<IScope> scopes) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = queryBuilder.getQueryAssociationsByScopes(scopes.size());
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (IScope s : scopes) {
			stmt.setLong(n++, Long.parseLong(s.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTheme(ITopicMap topicMap, Topic theme) throws SQLException {
		PreparedStatement stmt = null;
		/*
		 * require empty scope
		 */
		if (theme == null) {
			stmt = queryBuilder.getQueryAssociationsByScope(true);
		}
		/*
		 * require non-empty scope
		 */
		else {
			stmt = queryBuilder.getQueryAssociationsByTheme();
			stmt.setLong(2, Long.parseLong(theme.getId()));
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByThemes(ITopicMap topicMap, Topic[] themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAssociationsByThemes(themes.length, all);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (Topic theme : themes) {
			stmt.setLong(n++, Long.parseLong(theme.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAssociationScopes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		List<Long> ids = HashUtil.getList();
		while (rs.next()) {
			ids.add(rs.getLong("id_scope"));
		}
		rs.close();

		/*
		 * read themes of the scopes
		 */
		Collection<IScope> scopes = HashUtil.getHashSet();
		for (Long id : ids) {
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getAssociationThemes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryAssociationThemes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByScope(ITopicMap topicMap, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryNamesByScope(false);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByScopes(ITopicMap topicMap, Collection<IScope> scopes) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = queryBuilder.getQueryNamesByScopes(scopes.size());
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (IScope s : scopes) {
			stmt.setLong(n++, Long.parseLong(s.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByTheme(ITopicMap topicMap, Topic theme) throws SQLException {
		PreparedStatement stmt = null;
		/*
		 * require empty scope
		 */
		if (theme == null) {
			stmt = queryBuilder.getQueryNamesByScope(true);
		}
		/*
		 * require non-empty scope
		 */
		else {
			stmt = queryBuilder.getQueryNamesByTheme();
			stmt.setLong(2, Long.parseLong(theme.getId()));
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByThemes(ITopicMap topicMap, Topic[] themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryNamesByThemes(themes.length, all);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (Topic theme : themes) {
			stmt.setLong(n++, Long.parseLong(theme.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryNameScopes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		List<Long> ids = HashUtil.getList();
		while (rs.next()) {
			ids.add(rs.getLong("id_scope"));
		}
		rs.close();

		/*
		 * read themes of the scopes
		 */
		Collection<IScope> scopes = HashUtil.getHashSet();
		for (Long id : ids) {
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getNameThemes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryNameThemes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByScope(ITopicMap topicMap, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryOccurrencesByScope(false);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByScopes(ITopicMap topicMap, Collection<IScope> scopes) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = queryBuilder.getQueryOccurrencesByScopes(scopes.size());
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (IScope s : scopes) {
			stmt.setLong(n++, Long.parseLong(s.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByTheme(ITopicMap topicMap, Topic theme) throws SQLException {
		PreparedStatement stmt = null;
		/*
		 * require empty scope
		 */
		if (theme == null) {
			stmt = queryBuilder.getQueryOccurrencesByScope(true);
		}
		/*
		 * require non-empty scope
		 */
		else {
			stmt = queryBuilder.getQueryOccurrencesByTheme();
			stmt.setLong(2, Long.parseLong(theme.getId()));
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByThemes(ITopicMap topicMap, Topic[] themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryOccurrencesByThemes(themes.length, all);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (Topic theme : themes) {
			stmt.setLong(n++, Long.parseLong(theme.getId()));
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryOccurrenceScopes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		List<Long> ids = HashUtil.getList();
		while (rs.next()) {
			ids.add(rs.getLong("id_scope"));
		}
		rs.close();

		/*
		 * read themes of the scopes
		 */
		Collection<IScope> scopes = HashUtil.getHashSet();
		for (Long id : ids) {
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getOccurrenceThemes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryOccurrenceThemes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByScope(ITopicMap topicMap, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryVariantsByScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		ResultSet set = stmt.executeQuery();
		System.out.println(stmt);
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByScopes(ITopicMap topicMap, Collection<IScope> scopes) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = queryBuilder.getQueryVariantsByScopes(scopes.size());
		Long ids[] = new Long[scopes.size()];
		int n = 0;
		for (IScope s : scopes) {
			ids[n++] = Long.parseLong(s.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByTheme(ITopicMap topicMap, Topic theme) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryVariantsByTheme();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(theme.getId()));
		stmt.setLong(3, Long.parseLong(theme.getId()));
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByThemes(ITopicMap topicMap, Topic[] themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryVariantsByThemes(themes.length, all);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		int n = 2;
		for (Topic theme : themes) {
			stmt.setLong(n, Long.parseLong(theme.getId()));
			if (!all) {
				stmt.setLong(n + themes.length, Long.parseLong(theme.getId()));
			}
			n++;
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryVariantScopes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		List<Long> ids = HashUtil.getList();
		while (rs.next()) {
			ids.add(rs.getLong("id_scope"));
		}
		rs.close();

		/*
		 * read themes of the scopes
		 */
		Collection<IScope> scopes = HashUtil.getHashSet();
		for (Long id : ids) {
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getVariantThemes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryVariantThemes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	// LiteralIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNames(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNames();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNames(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNamesByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectNamesByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrences();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, Calendar lower, Calendar upper) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByDateRange();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, XmlSchemeDatatypes.XSD_DATETIME);
		stmt.setTimestamp(3, new Timestamp(lower.getTimeInMillis()));
		stmt.setTimestamp(4, new Timestamp(upper.getTimeInMillis()));
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, double value, double deviance, final String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByRange();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		stmt.setDouble(3, value - deviance);
		stmt.setDouble(4, value + deviance);
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, String value, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByValueAndDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		stmt.setString(3, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByDatatype(ITopicMap topicMap, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap topicMap, String pattern, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectOccurrencesByPatternAndDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		stmt.setString(3, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariants();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap, String value, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByValueAndDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		stmt.setString(3, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByDatatype(ITopicMap topicMap, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByPattern(ITopicMap topicMap, String pattern, String reference) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectVariantsByPatternAndDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		stmt.setString(3, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	// IdentityIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> getItemIdentifiers(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectItemIdentifiers();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> getSubjectIdentifiers(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSubjectIdentifiers();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> getSubjectLocators(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSubjectLocators();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IConstruct> getConstructsByIdentitifer(ITopicMap topicMap, String regExp, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectConstructsByIdentitifer();
		long topicMapId = Long.parseLong(topicMap.getId());
		String pattern = "^" + regExp + "$";
		stmt.setLong(1, topicMapId);
		stmt.setString(2, pattern);
		stmt.setLong(3, topicMapId);
		stmt.setString(4, pattern);
		stmt.setLong(5, topicMapId);
		stmt.setLong(6, topicMapId);
		stmt.setString(7, pattern);
		ResultSet rs = stmt.executeQuery();
		Collection<IConstruct> set = HashUtil.getHashSet();
		Collection<Long> ids = HashUtil.getHashSet();
		while (rs.next()) {
			if (rs.getString("type").equalsIgnoreCase("t")) {
				set.add(new TopicImpl(new JdbcIdentity(rs.getString("id")), topicMap));
			} else {
				ids.add(rs.getLong("id"));
			}
		}
		rs.close();
		for (Long id : ids) {
			set.add(doReadConstruct(topicMap, Long.toString(id), false));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IConstruct> getConstructsByItemIdentitifer(ITopicMap topicMap, String regExp, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectConstructsByItemIdentitifer();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		stmt.setString(3, "^" + regExp + "$");
		Collection<Long> ids = HashUtil.getHashSet();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			ids.add(rs.getLong("id_construct"));
		}
		rs.close();
		Collection<IConstruct> set = HashUtil.getHashSet();
		for (Long id : ids) {
			set.add(doReadConstruct(topicMap, Long.toString(id), false));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsBySubjectIdentitifer(ITopicMap topicMap, String regExp, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsBySubjectIdentitifer();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, "^" + regExp + "$");
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_topic");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsBySubjectLocator(ITopicMap topicMap, String regExp, long offset, long limit) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectTopicsBySubjectLocator();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, "^" + regExp + "$");
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_topic");
	}

	// SupertypeSubtypeIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getDirectSubtypes(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = queryBuilder.getQuerySelectTopicsWithoutSubtypes();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = queryBuilder.getQuerySelectDirectSubtypes();
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getDirectSupertypes(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = queryBuilder.getQuerySelectTopicsWithoutSupertypes();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = queryBuilder.getQuerySelectDirectSupertypes();
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSubtypes(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = queryBuilder.getQuerySelectTopicsWithoutSubtypes();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = queryBuilder.getQuerySelectSubtypesOfTopic();
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSubtypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSubtypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_subtype");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSubtypes(ITopicMap topicMap, Collection<T> types, boolean matchAll) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSubtypesOfTopics();
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T topic : types) {
			ids[i++] = Long.parseLong(topic.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, matchAll);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toTopics(topicMap, rs, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSupertypes(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = queryBuilder.getQuerySelectTopicsWithoutSupertypes();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = queryBuilder.getQuerySelectSupertypesOfTopic();
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSupertypes(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSupertypes();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_supertype");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSupertypes(ITopicMap topicMap, Collection<T> types, boolean matchAll) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectSupertypesOfTopics();
		Long ids[] = new Long[types.size()];
		int i = 0;
		for (T topic : types) {
			ids[i++] = Long.parseLong(topic.getId());
		}
		stmt.setArray(1, getConnection().createArrayOf("bigint", ids));
		stmt.setBoolean(2, matchAll);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toTopics(topicMap, rs, "id");
	}
}
