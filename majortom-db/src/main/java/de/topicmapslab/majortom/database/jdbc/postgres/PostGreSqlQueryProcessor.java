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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.core.AssociationImpl;
import de.topicmapslab.majortom.core.AssociationRoleImpl;
import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.NameImpl;
import de.topicmapslab.majortom.core.OccurrenceImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.core.VariantImpl;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
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
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryProcessor implements IQueryProcessor {

	private final PostGreSqlQueryBuilder queryBuilder;
	private final Connection conncetion;

	/**
	 * constructor
	 * 
	 * @param connection
	 *            the JDBC connection
	 */
	public PostGreSqlQueryProcessor(Connection connection) {
		this.conncetion = connection;
		this.queryBuilder = new PostGreSqlQueryBuilder(conncetion);
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
		PreparedStatement stmt = queryBuilder.getQueryReadScopeByThemes(themes.size());
		/*
		 * order themes by id
		 */
		List<Long> ids = HashUtil.getList();
		for (ITopic theme : themes) {
			ids.add(Long.parseLong(theme.getId()));
		}
		Collections.sort(ids);
		/*
		 * replace theme id
		 */
		int i = 1;
		for (Long id : ids) {
			stmt.setLong(i, id);
			i++;
		}
		/*
		 * query for scope
		 */
		ResultSet set = stmt.executeQuery();
		if (set.next()) {
			long id = set.getLong("id_scope");
			set.close();
			stmt.close();
			return new ScopeImpl(Long.toString(id), themes);
		}
		stmt.close();
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
		i = 0;
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
	private ITopic doCreateTopic(ITopicMap topicMap) throws SQLException {
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
		ITopic topic = doCreateTopic(topicMap);
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
		ITopic topic = doCreateTopic(topicMap);
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
		ITopic topic = doCreateTopic(topicMap);
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
		throw new UnsupportedOperationException("Not Implemented yet!");
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
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyScope(IScopable s, ITopic theme) throws SQLException {
		Set<ITopic> themes = HashUtil.getHashSet(s.getScopeObject().getThemes());
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
	public void doModifyValue(IDatatypeAware t, String value) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryModifyValue();
		stmt.setString(1, value);
		stmt.setLong(2, Long.parseLong(t.getId()));
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
	public Set<IAssociation> doReadAssociation(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociation();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedAssociationWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociation();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithType();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadAssociationWithScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeSet(IRevision r) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t) throws SQLException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t));
		set.addAll(doReadOccurrences(t));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws SQLException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type));
		set.addAll(doReadOccurrences(t, type));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws SQLException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type, scope));
		set.addAll(doReadOccurrences(t, type, scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws SQLException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, scope));
		set.addAll(doReadOccurrences(t, scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, String id) throws SQLException {
		if (t.getId().equalsIgnoreCase(id)) {
			return t;
		}
		PreparedStatement stmt = null;
		ResultSet set = null;
		long topicmapId = Long.parseLong(t.getId());
		long id_ = Long.parseLong(id);
		try {
			/*
			 * check for topic
			 */
			stmt = queryBuilder.getQueryReadConstructById(ITopic.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new TopicImpl(new JdbcIdentity(set.getString("id")), t);
			}
			/*
			 * check for name
			 */
			stmt = queryBuilder.getQueryReadConstructById(IName.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new NameImpl(new JdbcIdentity(set.getString("id")), new TopicImpl(new JdbcIdentity(set.getString("id_parent")), t));
			}
			/*
			 * check for occurrence
			 */
			stmt = queryBuilder.getQueryReadConstructById(IOccurrence.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new OccurrenceImpl(new JdbcIdentity(set.getString("id")), new TopicImpl(new JdbcIdentity(set.getString("id_parent")), t));
			}
			/*
			 * check for association
			 */
			stmt = queryBuilder.getQueryReadConstructById(IAssociation.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new AssociationImpl(new JdbcIdentity(set.getString("id")), t);
			}
			/*
			 * check for role
			 */
			stmt = queryBuilder.getQueryReadConstructById(IAssociationRole.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new AssociationRoleImpl(new JdbcIdentity(set.getString("id")), new AssociationImpl(new JdbcIdentity(set.getString("id_parent")), t));
			}
			/*
			 * check for variant
			 */
			stmt = queryBuilder.getQueryReadConstructById(IVariant.class);
			stmt.setLong(1, topicmapId);
			stmt.setLong(2, id_);
			set = stmt.executeQuery();
			if (set.next()) {
				return new VariantImpl(new JdbcIdentity(set.getString(1)), new NameImpl(new JdbcIdentity(set.getString(2)), new TopicImpl(new JdbcIdentity(set
						.getString(2)), t)));
			}
			return null;

		}
		/*
		 * finally close the result set
		 */
		finally {
			if (set != null) {
				set.close();
			}
		}
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
			return doReadConstruct(t, id_);
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
	public IRevision doReadFutureRevision(IRevision r) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadItemIdentifiers(IConstruct c) throws SQLException {
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
	public Set<IName> doReadNames(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNames();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadNamesWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrences();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrencesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadOccurrencesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws SQLException {
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
	public IRevision doReadPreviousRevision(IRevision r) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
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
			return (IReifiable) doReadConstruct(t.getTopicMap(), id);
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
	public Calendar doReadRevisionBegin(IRevision r) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadRevisionEnd(IRevision r) throws SQLException {
		throw new UnsupportedOperationException("Not Implemented yet!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadRoleTypes(IAssociation association) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRoleTypes();
		stmt.setLong(1, Long.parseLong(association.getId()));
		return Jdbc2Construct.toTopics(association.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRoles();
		stmt.setLong(1, Long.parseLong(association.getId()));
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadRolesWithType();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedRoles();
		stmt.setLong(1, Long.parseLong(player.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadPlayedRolesWithType();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws SQLException {
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
		return new ScopeImpl(Long.toString(scopeId), readThemes(s.getTopicMap(), scopeId));
	}

	/**
	 * Internal method to read all themes of a scope
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param scopeId
	 *            the scope id
	 * @return a collection of all themes
	 * @throws SQLException
	 *             thrown if a database error occurrs
	 */
	private Collection<ITopic> readThemes(ITopicMap topicMap, long scopeId) throws SQLException {
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
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSubjectIdentifiers();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectLocators(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSubjectLocators();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadSuptertypes(ITopic t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadSupertypes();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id_supertype");
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
	public Set<ITopic> doReadTopics(ITopicMap t) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopics();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadTopicsWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id");
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
	public Set<ITopic> doReadTypes(ITopic t) throws SQLException {
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
	public Set<IVariant> doReadVariants(IName n) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadVariants();
		stmt.setLong(1, Long.parseLong(n.getId()));
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryReadVariantsWithScope();
		stmt.setLong(1, Long.parseLong(n.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveAssociation(IAssociation association, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteAssociation();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.execute();
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
	public void doRemoveName(IName name, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteName();
		stmt.setLong(1, Long.parseLong(name.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteOccurrence();
		stmt.setLong(1, Long.parseLong(occurrence.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveRole(IAssociationRole role, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteRole();
		stmt.setLong(1, Long.parseLong(role.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveScope(IScopable s, ITopic theme) throws SQLException {
		Set<ITopic> themes = HashUtil.getHashSet(s.getScopeObject().getThemes());
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
	public void doRemoveTopic(ITopic topic, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteTopic();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteTopicMap();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		stmt.setLong(3, Long.parseLong(topicMap.getId()));
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

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveVariant(IVariant variant, boolean cascade) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQueryDeleteVariant();
		stmt.setLong(1, Long.parseLong(variant.getId()));
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

	// ScopedIndex

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IScope> getScopesByThemes(final ITopicMap topicMap, Collection<T> themes, boolean all) throws SQLException {
		PreparedStatement stmt = queryBuilder.getQuerySelectScopes(themes.size(), all);		
		int n = 1;
		for (T theme : themes) {			
			stmt.setLong(n++, Long.parseLong(theme.getId()));
		}
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
			scopes.add(new ScopeImpl(Long.toString(id), readThemes(topicMap, id)));
		}
		return scopes;
	}

}
