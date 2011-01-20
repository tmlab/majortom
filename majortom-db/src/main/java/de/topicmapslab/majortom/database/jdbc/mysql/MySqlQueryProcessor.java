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
package de.topicmapslab.majortom.database.jdbc.mysql;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSQueryProcessor;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSSession;
import de.topicmapslab.majortom.database.jdbc.util.Jdbc2Construct;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyAssociation;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyAssociationRole;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyName;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyOccurrence;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyTopic;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyVariant;
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
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.RevisionImpl;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class MySqlQueryProcessor extends RDBMSQueryProcessor {

	/**
	 * constructor
	 * 
	 * @param session
	 *            the session
	 * @param connection
	 *            the database connection for this query processor
	 */
	public MySqlQueryProcessor(RDBMSSession session, Connection connection) {
		super(session, connection);
	}

	protected MySqlQueryBuilder getQueryBuilder() {
		return (MySqlQueryBuilder) super.getQueryBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryCreateAssociation();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.execute();
		return Jdbc2Construct.toAssociation(topicMap, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes)
			throws SQLException {
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateAssociationWithScope();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setLong(4, Long.parseLong(scope.getId()));
		stmt.execute();
		return Jdbc2Construct.toAssociation(topicMap, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doCreateLocator(ITopicMap topicMap, String reference) throws SQLException {
		/*
		 * check if locator exists
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryReadLocator();
		stmt.setString(1, reference);
		ResultSet rs = stmt.executeQuery();
		final String id;
		if (!rs.next()) {
			stmt = getQueryBuilder().getQueryCreateLocator();
			stmt.setString(1, reference);
			stmt.execute();
			ResultSet set = stmt.getGeneratedKeys();
			set.next();
			id = set.getString(1);
			set.close();
		} else {
			id = rs.getString(1);
		}
		rs.close();
		return new LocatorImpl(reference, id);
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
		PreparedStatement stmt = getQueryBuilder().getQueryCreateName();
		ITopicMap topicMap = topic.getTopicMap();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.execute();
		return Jdbc2Construct.toName(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException {
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateNameWithScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setLong(5, Long.parseLong(scope.getId()));
		stmt.execute();
		return Jdbc2Construct.toName(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws SQLException {
		doCreateLocator(topic.getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrence();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setString(5, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes)
			throws SQLException {
		doCreateLocator(topic.getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrenceWithScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setLong(5, Long.parseLong(scope.getId()));
		stmt.setString(6, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws SQLException {
		doCreateLocator(topic.getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrence();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value.getReference());
		stmt.setString(5, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes)
			throws SQLException {
		doCreateLocator(topic.getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrenceWithScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value.getReference());
		stmt.setLong(5, Long.parseLong(scope.getId()));
		stmt.setString(6, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype)
			throws SQLException {
		doCreateLocator(topic.getTopicMap(), datatype.getReference());
		ITopicMap topicMap = topic.getTopicMap();
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrence();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setString(5, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype,
			Collection<ITopic> themes) throws SQLException {
		doCreateLocator(topic.getTopicMap(), datatype.getReference());
		ITopicMap topicMap = topic.getTopicMap();
		IScope scope = doCreateScope(topicMap, themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateOccurrenceWithScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topic.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setString(4, value);
		stmt.setLong(5, Long.parseLong(scope.getId()));
		stmt.setString(6, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toOccurrence(topic, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws SQLException {
		ITopicMap topicMap = association.getTopicMap();
		PreparedStatement stmt = getQueryBuilder().getQueryCreateRole();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(association.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		stmt.setLong(4, Long.parseLong(player.getId()));
		stmt.execute();
		return Jdbc2Construct.toRole(association, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

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
		Collection<IScope> set = HashUtil.getHashSet();
		/*
		 * read scope by themes
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryReadScopeByTheme();
		boolean first = true;
		for (ITopic theme : themes) {
			Collection<Long> ids = HashUtil.getHashSet();
			stmt.setLong(1, Long.parseLong(theme.getId()));
			stmt.setInt(2, themes.size());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ids.add(rs.getLong("id_scope"));
			}
			rs.close();

			Collection<IScope> temp = HashUtil.getHashSet();
			for (Long id : ids) {
				temp.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
			}
			if (first) {
				first = false;
				set.addAll(temp);
			} else {
				set.retainAll(temp);
				if (set.isEmpty()) {
					break;
				}
			}
		}

		if (set.isEmpty()) {
			return null;
		}

		return set.iterator().next();
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws SQLException {
		IScope scope = readScopeByThemes(topicMap, themes);
		if (scope != null) {
			return scope;
		}
		/*
		 * create scope
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryCreateScope();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.execute();
		/*
		 * get generated scope id
		 */
		ResultSet set = stmt.getGeneratedKeys();
		set.next();
		long id = set.getLong("GENERATED_KEY");
		set.close();
		/*
		 * add all themes
		 */
		for (ITopic theme : themes) {
			stmt = getQueryBuilder().getQueryAddThemes(themes.size());
			stmt.setLong(1, id);
			stmt.setLong(2, Long.parseLong(theme.getId()));
			stmt.execute();
			stmt.close();
		}
		/*
		 * create scope instance
		 */
		return new ScopeImpl(Long.toString(id), themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doCreateTopicMap(ILocator baseLocator) throws SQLException {
		doCreateLocator(null, baseLocator.getReference());

		/*
		 * topic map already exists -> get topic map id by base locator
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryReadTopicMap();
		stmt.setString(1, baseLocator.getReference());
		ResultSet set = stmt.executeQuery();
		if (set.next()) {
			final long id = set.getLong("id");
			set.close();
			return id;
		}
		/*
		 * get string to create new topic map
		 */
		stmt = getQueryBuilder().getQueryCreateTopicMap();
		stmt.setString(1, baseLocator.getReference());
		stmt.setString(2, baseLocator.getReference());
		stmt.execute();
		/*
		 * get result set containing generated keys
		 */
		set = stmt.getGeneratedKeys();
		/*
		 * check if topic map was created
		 */
		final long id = set.getLong("GENERATED_KEY");
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
		PreparedStatement stmt = getQueryBuilder().getQueryCreateTopic();
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
		if (set != null) {
			ITopic topic = Jdbc2Construct.toTopic(topicMap, set, "GENERATED_KEY");
			if (topic == null) {
				throw new TopicMapStoreException("Internal SQL error, missing result 'id'.");
			}
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
		doCreateLocator(name.getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateVariant();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(name.getId()));
		stmt.setString(3, value);
		stmt.setLong(4, Long.parseLong(scope.getId()));
		stmt.setString(5, XmlSchemeDatatypes.XSD_STRING);
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws SQLException {
		doCreateLocator(name.getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateVariant();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(name.getId()));
		stmt.setString(3, value.getReference());
		stmt.setLong(4, Long.parseLong(scope.getId()));
		stmt.setString(5, XmlSchemeDatatypes.XSD_ANYURI);
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getGeneratedKeys(), "GENERATED_KEY");
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes)
			throws SQLException {
		doCreateLocator(name.getTopicMap(), datatype.getReference());
		ITopicMap topicMap = name.getTopicMap();
		IScope scope = doCreateScope(name.getTopicMap(), themes);
		PreparedStatement stmt = getQueryBuilder().getQueryCreateVariant();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(name.getId()));
		stmt.setString(3, value);
		stmt.setLong(4, Long.parseLong(scope.getId()));
		stmt.setString(5, datatype.getReference());
		stmt.execute();
		return Jdbc2Construct.toVariant(name, stmt.getGeneratedKeys(), "GENERATED_KEY");
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
		PreparedStatement stmt = getQueryBuilder().getPerformMergeTopics();
		// stmt.setLong(1, Long.parseLong(context.getId()));
		// stmt.setLong(2, Long.parseLong(other.getId()));
		// stmt.execute();
		long idContext = Long.parseLong(context.getId());
		long idOther = Long.parseLong(other.getId());
		int max = 12;
		for (int n = 0; n < max; n++) {
			stmt.setLong(n * 2 + 1, idContext);
			stmt.setLong(n * 2 + 2, idOther);
		}
		stmt.setLong(max * 2 + 1, idOther);
		stmt.execute();

		detectDuplicateNames(context);
		detectDuplicateOccurrences(context);
		detectDuplicateAssociations(context);
	}

	private void detectDuplicateNames(ITopic topic) throws SQLException {
		Collection<Name> removed = HashUtil.getHashSet();
		/*
		 * test duplicated names
		 */
		for (Name n : topic.getNames()) {
			if (removed.contains(n)) {
				continue;
			}
			PreparedStatement stmt = getQueryBuilder().getQueryDuplicateName();
			stmt.setLong(1, Long.parseLong(topic.getId()));
			stmt.setLong(2, Long.parseLong(n.getId()));
			stmt.setLong(3, Long.parseLong(n.getType().getId()));
			stmt.setString(4, n.getValue());
			stmt.setLong(5, Long.parseLong(((IName) n).getScopeObject().getId()));
			ResultSet rs = stmt.executeQuery();
			IName name = Jdbc2Construct.toName(topic, rs, "id");
			if (name != null) {
				ITopic reifier = (ITopic) n.getReifier();
				ITopic otherReifier = (ITopic) name.getReifier();
				if (reifier != null) {
					if (otherReifier != null) {
						doMergeTopics(reifier, otherReifier);
						name.setReifier(null);
					}
				} else if (otherReifier != null) {
					name.setReifier(null);
					n.setReifier(otherReifier);
				}

				/*
				 * move item-identifier
				 */
				PreparedStatement stmtMoveII = getQueryBuilder().getQueryMoveItemIdentifiers();
				stmtMoveII.setLong(1, Long.parseLong(n.getId()));
				stmtMoveII.setLong(2, Long.parseLong(name.getId()));
				stmtMoveII.execute();
				/*
				 * move variants
				 */
				PreparedStatement stmtMoveVariants = getQueryBuilder().getQueryMoveVariants();
				stmtMoveVariants.setLong(1, Long.parseLong(n.getId()));
				stmtMoveVariants.setLong(2, Long.parseLong(name.getId()));
				stmtMoveVariants.execute();

				/*
				 * remove name
				 */
				name.remove();
				removed.add(name);

				/*
				 * check duplicated variants
				 */
				detectDuplicateVariants((IName) n);
			}
		}
	}

	private void detectDuplicateVariants(IName name) throws SQLException {
		Collection<Variant> removed = HashUtil.getHashSet();
		/*
		 * test duplicated names
		 */
		for (Variant v : name.getVariants()) {
			if (removed.contains(v)) {
				continue;
			}
			PreparedStatement stmt = getQueryBuilder().getQueryDuplicateVariant();
			stmt.setLong(1, Long.parseLong(name.getId()));
			stmt.setLong(2, Long.parseLong(v.getId()));
			stmt.setString(3, v.getValue());
			stmt.setString(4, v.getDatatype().getReference());
			stmt.setLong(5, Long.parseLong(((IVariant) v).getScopeObject().getId()));
			ResultSet rs = stmt.executeQuery();
			IVariant variant = Jdbc2Construct.toVariant(name, rs, "id");
			if (variant != null) {
				ITopic reifier = (ITopic) v.getReifier();
				ITopic otherReifier = (ITopic) variant.getReifier();
				if (reifier != null) {
					if (otherReifier != null) {
						doMergeTopics(reifier, otherReifier);
						variant.setReifier(null);
					}
				} else if (otherReifier != null) {
					variant.setReifier(null);
					v.setReifier(otherReifier);
				}

				/*
				 * move item-identifier
				 */
				PreparedStatement stmtMoveII = getQueryBuilder().getQueryMoveItemIdentifiers();
				stmtMoveII.setLong(1, Long.parseLong(v.getId()));
				stmtMoveII.setLong(2, Long.parseLong(variant.getId()));
				stmtMoveII.execute();
				/*
				 * remove variant
				 */
				variant.remove();
				removed.add(variant);
			}
		}
	}

	private void detectDuplicateOccurrences(ITopic topic) throws SQLException {
		Collection<IOccurrence> removed = HashUtil.getHashSet();
		/*
		 * test duplicated names
		 */
		for (Occurrence o : topic.getOccurrences()) {
			if (removed.contains(o)) {
				continue;
			}
			PreparedStatement stmt = getQueryBuilder().getQueryDuplicateOccurrence();
			stmt.setLong(1, Long.parseLong(topic.getId()));
			stmt.setLong(2, Long.parseLong(o.getId()));
			stmt.setLong(3, Long.parseLong(o.getType().getId()));
			stmt.setString(4, o.getValue());
			stmt.setString(5, o.getDatatype().getReference());
			stmt.setLong(6, Long.parseLong(((IOccurrence) o).getScopeObject().getId()));
			ResultSet rs = stmt.executeQuery();
			IOccurrence occurrence = Jdbc2Construct.toOccurrence(topic, rs, "id");
			if (occurrence != null) {
				ITopic reifier = (ITopic) o.getReifier();
				ITopic otherReifier = (ITopic) occurrence.getReifier();
				if (reifier != null) {
					if (otherReifier != null) {
						doMergeTopics(reifier, otherReifier);
						occurrence.setReifier(null);
					}
				} else if (otherReifier != null) {
					occurrence.setReifier(null);
					o.setReifier(otherReifier);
				}

				/*
				 * move item-identifier
				 */
				PreparedStatement stmtMoveII = getQueryBuilder().getQueryMoveItemIdentifiers();
				stmtMoveII.setLong(1, Long.parseLong(o.getId()));
				stmtMoveII.setLong(2, Long.parseLong(occurrence.getId()));
				stmtMoveII.execute();
				/*
				 * remove occurrence
				 */
				occurrence.remove();
				removed.add(occurrence);
			}
		}
	}

	private void detectDuplicateAssociations(ITopic topic) throws SQLException {
		Collection<IAssociation> removed = HashUtil.getHashSet();
		/*
		 * test duplicated names
		 */
		for (Association a : topic.getAssociationsPlayed()) {
			if (removed.contains(a)) {
				continue;
			}
			PreparedStatement stmt = getQueryBuilder().getQueryDuplicateAssociations();
			stmt.setLong(1, Long.parseLong(a.getId()));
			stmt.setLong(2, Long.parseLong(a.getId()));
			stmt.setLong(3, Long.parseLong(a.getType().getId()));
			stmt.setLong(4, Long.parseLong(((IAssociation) a).getScopeObject().getId()));

			ResultSet rs = stmt.executeQuery();
			IAssociation association = Jdbc2Construct.toAssociation(topic.getTopicMap(), rs, "id");
			if (association != null) {
				/*
				 * move reifier
				 */
				ITopic reifier = (ITopic) a.getReifier();
				ITopic otherReifier = (ITopic) association.getReifier();
				if (reifier != null) {
					if (otherReifier != null) {
						doMergeTopics(reifier, otherReifier);
					}
				} else if (otherReifier != null) {
					association.setReifier(null);
					a.setReifier(otherReifier);
				}

				/*
				 * move item-identifier
				 */
				PreparedStatement stmtMoveII = getQueryBuilder().getQueryMoveItemIdentifiers();
				stmtMoveII.setLong(1, Long.parseLong(a.getId()));
				stmtMoveII.setLong(2, Long.parseLong(association.getId()));
				stmtMoveII.execute();
				/*
				 * check role reifier
				 */
				for (Role r : a.getRoles()) {
					PreparedStatement stmtRole = getQueryBuilder().getQueryDuplicateRoles();
					stmtRole.setLong(1, Long.parseLong(association.getId()));
					stmtRole.setLong(2, Long.parseLong(r.getType().getId()));
					stmtRole.setLong(3, Long.parseLong(r.getPlayer().getId()));
					IAssociationRole role = Jdbc2Construct.toRole(association, stmtRole.executeQuery(), "id");
					/*
					 * move reifier
					 */
					reifier = (ITopic) r.getReifier();
					otherReifier = (ITopic) role.getReifier();
					if (reifier != null) {
						if (otherReifier != null) {
							doMergeTopics(reifier, otherReifier);
						}
					} else if (otherReifier != null) {
						role.setReifier(null);
						r.setReifier(otherReifier);
					}

					/*
					 * move item-identifier
					 */
					stmtMoveII = getQueryBuilder().getQueryMoveItemIdentifiers();
					stmtMoveII.setLong(1, Long.parseLong(r.getId()));
					stmtMoveII.setLong(2, Long.parseLong(role.getId()));
					stmtMoveII.execute();
				}
				/*
				 * remove association
				 */
				association.remove();
				removed.add(association);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException {
		doCreateLocator(c.getTopicMap(), itemIdentifier.getReference());
		PreparedStatement stmt = getQueryBuilder().getQueryAddItemIdentifier();
		stmt.setLong(1, Long.parseLong(c.getId()));
		stmt.setString(2, itemIdentifier.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyPlayer(IAssociationRole role, ITopic player) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryModifyPlayer();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(role.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyReifier(IReifiable r, ITopic reifier) throws SQLException {
		PreparedStatement stmt = null;
		if (r instanceof IName) {
			stmt = getQueryBuilder().getQueryModifyNameReifier();
		} else if (r instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryModifyOccurrenceReifier();
		} else if (r instanceof IVariant) {
			stmt = getQueryBuilder().getQueryModifyVariantReifier();
		} else if (r instanceof IAssociationRole) {
			stmt = getQueryBuilder().getQueryModifyRoleReifier();
		} else if (r instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryModifyAssociationReifier();
		} else {
			stmt = getQueryBuilder().getQueryModifyTopicMapReifier();
		}
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
		PreparedStatement stmt = null;
		if (s instanceof IName) {
			stmt = getQueryBuilder().getQueryModifyNameScope();
		} else if (s instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryModifyOccurrenceScope();
		} else if (s instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryModifyAssociationScope();
		} else {
			stmt = getQueryBuilder().getQueryModifyVariantScope();
		}
		stmt.setLong(1, Long.parseLong(scope.getId()));
		stmt.setLong(2, Long.parseLong(s.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException {
		doCreateLocator(t.getTopicMap(), subjectIdentifier.getReference());
		PreparedStatement stmt = getQueryBuilder().getQueryAddSubjectIdentifier();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectIdentifier.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException {
		doCreateLocator(t.getTopicMap(), subjectLocator.getReference());
		PreparedStatement stmt = getQueryBuilder().getQueryAddSubjectLocator();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectLocator.getReference());
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifySupertype(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryModifySupertypes();
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
		PreparedStatement stmt = null;
		if (t instanceof IName) {
			stmt = getQueryBuilder().getQueryModifyNameType();
		} else if (t instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryModifyOccurrenceType();
		} else if (t instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryModifyAssociationType();
		} else {
			stmt = getQueryBuilder().getQueryModifyRoleType();
		}
		stmt.setLong(1, Long.parseLong(type.getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyType(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryModifyTypes();
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
		PreparedStatement stmt = getQueryBuilder().getQueryModifyValue();
		stmt.setString(1, value);
		stmt.setLong(2, Long.parseLong(n.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws SQLException {
		doCreateLocator(t.getTopicMap(), datatype.getReference());
		PreparedStatement stmt = getQueryBuilder().getQueryModifyValueWithDatatype();
		stmt.setString(1, value);
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setString(3, datatype.getReference());
		stmt.setLong(4, Long.parseLong(t.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedAssociation(offset != -1);
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedAssociationWithType();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setLong(3, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedAssociationWithTypeAndScope();
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
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedAssociationWithScope();
		stmt.setLong(1, Long.parseLong(t.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(t.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(t.getTopicMap(), stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadAssociation();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadAssociationWithType();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadAssociationWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadAssociationWithScope();
		stmt.setLong(1, Long.parseLong(tm.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toAssociations(tm, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, -1, -1));
		set.addAll(doReadOccurrences(t, -1, -1));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type));
		set.addAll(doReadOccurrences(t, type));
		for (ITopic type_ : getSubtypes(t.getTopicMap(), type, -1, -1)) {
			set.addAll(doReadNames(t, type_));
			set.addAll(doReadOccurrences(t, type_));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws SQLException {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type, scope));
		set.addAll(doReadOccurrences(t, type, scope));
		for (ITopic type_ : getSubtypes(t.getTopicMap(), type, -1, -1)) {
			set.addAll(doReadNames(t, type_, scope));
			set.addAll(doReadOccurrences(t, type_, scope));
		}
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
	public IConstruct doReadConstruct(ITopicMap topicMap, Long id, boolean lookupHistory) throws SQLException {
		if (topicMap.getId().equalsIgnoreCase(Long.toString(id))) {
			return topicMap;
		}
		PreparedStatement stmt = getQueryBuilder().getQueryReadConstructById();
		stmt.setLong(1, id);
		Collection<IConstruct> c = Jdbc2Construct.toConstructs(topicMap, stmt.executeQuery());
		if (c.isEmpty()) {
			if (lookupHistory) {
				return readHistoryConstruct(id);
			}
			return null;
		}
		return c.iterator().next();
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap topicMap, ILocator itemIdentifier) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadConstructByItemIdentifier();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setString(1, itemIdentifier.getReference());
		stmt.setLong(2, topicMapId);
		stmt.setString(3, itemIdentifier.getReference());
		stmt.setLong(4, topicMapId);
		stmt.setString(5, itemIdentifier.getReference());
		stmt.setLong(6, topicMapId);
		stmt.setString(7, itemIdentifier.getReference());
		stmt.setLong(8, topicMapId);
		stmt.setString(9, itemIdentifier.getReference());
		stmt.setLong(10, topicMapId);
		stmt.setString(11, itemIdentifier.getReference());
		stmt.setLong(12, topicMapId);
		stmt.setString(13, itemIdentifier.getReference());
		Collection<IConstruct> c = Jdbc2Construct.toConstructs(topicMap, stmt.executeQuery());
		if (c.isEmpty()) {
			return null;
		}
		return c.iterator().next();
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware d) throws SQLException {
		PreparedStatement stmt = null;
		if (d instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryReadOccurrenceDataType();
		} else {
			stmt = getQueryBuilder().getQueryReadVariantDataType();
		}
		stmt.setLong(1, Long.parseLong(d.getId()));
		return Jdbc2Construct.toLocator(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadItemIdentifiers(IConstruct c) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadItemIdentifiers();
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
	public Collection<IName> doReadNames(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNames(offset != -1);
		stmt.setLong(1, Long.parseLong(t.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNamesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNamesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> doReadNames(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNamesWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toNames(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadOccurrences(offset != -1);
		stmt.setLong(1, Long.parseLong(t.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadOccurrencesWithType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadOccurrencesWithTypeAndScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadOccurrencesWithScope();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toOccurrences(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadPlayer(IAssociationRole role) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayer();
		stmt.setLong(1, Long.parseLong(role.getId()));
		return Jdbc2Construct.toTopic(role.getTopicMap(), stmt.executeQuery(), "id_player");
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadReified();
		long topicId = Long.parseLong(t.getId());
		stmt.setLong(1, topicId);
		stmt.setLong(2, topicId);
		stmt.setLong(3, topicId);
		stmt.setLong(4, topicId);
		stmt.setLong(5, topicId);
		stmt.setLong(6, topicId);
		Collection<IConstruct> c = Jdbc2Construct.toConstructs(t.getTopicMap(), stmt.executeQuery());
		if (c.isEmpty()) {
			return null;
		}
		return (IReifiable) c.iterator().next();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws SQLException {
		PreparedStatement stmt = null;
		if (r instanceof IName) {
			stmt = getQueryBuilder().getQueryReadNameReifier();
		} else if (r instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryReadOccurrenceReifier();
		} else if (r instanceof IVariant) {
			stmt = getQueryBuilder().getQueryReadVariantReifier();
		} else if (r instanceof IAssociationRole) {
			stmt = getQueryBuilder().getQueryReadRoleReifier();
		} else if (r instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryReadAssociationReifier();
		} else {
			stmt = getQueryBuilder().getQueryReadTopicMapReifier();
		}
		stmt.setLong(1, Long.parseLong(r.getId()));
		ResultSet result = stmt.executeQuery();
		return Jdbc2Construct.toTopic(r.getTopicMap(), result, "id_reifier");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadRoleTypes(IAssociation association) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRoleTypes();
		stmt.setLong(1, Long.parseLong(association.getId()));
		return Jdbc2Construct.toTopics(association.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(IAssociation association, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRoles(offset != -1);
		stmt.setLong(1, Long.parseLong(association.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRolesWithType();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(association, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(ITopic player, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedRoles(offset != -1);
		stmt.setLong(1, Long.parseLong(player.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedRolesWithType();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPlayedRolesWithTypeAndAssociationType();
		stmt.setLong(1, Long.parseLong(player.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.setLong(3, Long.parseLong(assocType.getId()));
		return Jdbc2Construct.toRoles(player.getTopicMap(), stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doReadScope(IScopable s) throws SQLException {
		PreparedStatement stmt = null;
		if (s instanceof IName) {
			stmt = getQueryBuilder().getQueryReadNameScope();
		} else if (s instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryReadOccurrenceScope();
		} else if (s instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryReadAssociationScope();
		} else {
			stmt = getQueryBuilder().getQueryReadVariantScope();
		}
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
		PreparedStatement stmt = getQueryBuilder().getQueryReadThemes();
		stmt.setLong(1, scopeId);
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toTopics(topicMap, set, "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadSubjectIdentifiers(ITopic t) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadSubjectIdentifiers();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> doReadSubjectLocators(ITopic t) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadSubjectLocators();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadSuptertypes(ITopic t, long offset, long limit) throws SQLException {
		Collection<ITopic> set = HashUtil.getHashSet(getSupertypes(t.getTopicMap(), t, offset, limit));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTopicBySubjectIdentifier();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectIdentifier.getReference());
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toTopic(t, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTopicBySubjectLocator();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectLocator.getReference());
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toTopic(t, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTopics(ITopicMap t) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTopics();
		stmt.setLong(1, Long.parseLong(t.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTopicsWithType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		return Jdbc2Construct.toTopics(t, stmt.executeQuery(), "id_instance");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws SQLException {
		PreparedStatement stmt = null;
		if (typed instanceof IName) {
			stmt = getQueryBuilder().getQueryReadNameType();
		} else if (typed instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryReadOccurrenceType();
		} else if (typed instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryReadAssociationType();
		} else {
			stmt = getQueryBuilder().getQueryReadRoleType();
		}
		stmt.setLong(1, Long.parseLong(typed.getId()));
		return Jdbc2Construct.toTopic(typed.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> doReadTypes(ITopic t, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(t.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(t.getTopicMap(), stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNameValue();
		stmt.setLong(1, Long.parseLong(n.getId()));
		ResultSet result = stmt.executeQuery();
		if (!result.next()) {
			return null;
		}
		String value = result.getString("value");
		result.close();
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware t) throws SQLException {
		PreparedStatement stmt = null;
		if (t instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryReadOccurrenceValue();
		} else {
			stmt = getQueryBuilder().getQueryReadVariantValue();
		}
		stmt.setLong(1, Long.parseLong(t.getId()));
		ResultSet result = stmt.executeQuery();
		if (!result.next()) {
			return null;
		}
		String value = result.getString("value");
		result.close();
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> doReadVariants(IName n, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadVariants(offset != -1);
		stmt.setLong(1, Long.parseLong(n.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> doReadVariants(IName n, IScope scope) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadVariantsWithScope();
		stmt.setLong(1, Long.parseLong(n.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		return Jdbc2Construct.toVariants(n, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveAssociation(IAssociation association, boolean cascade) throws SQLException {
		doRemoveAssociation(association, cascade,
				getSession().getTopicMapStore().createRevision(TopicMapEventType.ASSOCIATION_REMOVED));
		return true;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void doRemoveAssociation(IAssociation association, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump association
		 */
		dump(revision, association);
		/*
		 * remove roles
		 */
		for (IAssociationRole role : doReadRoles(association, -1, -1)) {
			/*
			 * remove role
			 */
			doRemoveRole(role, cascade, revision);
		}
		/*
		 * check to remove reification
		 */
		ITopic topic = doReadReification(association);
		if (topic != null) {
			doRemoveTopic(topic, cascade, revision);
		}
		/*
		 * remove association
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteAssociation();
		stmt.setLong(1, Long.parseLong(association.getId()));
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED,
				association.getTopicMap(), null, association);
		/*
		 * store history
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.ASSOCIATION_REMOVED,
				association.getTopicMap(), null, association);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteItemIdentifier();
		stmt.setLong(1, Long.parseLong(c.getId()));
		stmt.setString(2, itemIdentifier.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveName(IName name, boolean cascade) throws SQLException {
		doRemoveName(name, cascade, getSession().getTopicMapStore().createRevision(TopicMapEventType.NAME_ADDED));
		return true;
	}

	/**
	 * Using JDBC to remove a name instance and all variants.
	 * 
	 * @param name
	 *            the name
	 * @param cascade
	 *            cascading deletion
	 * @param revision
	 *            the revision to store changes
	 * @throws SQLException
	 *             thrown if JDBC command fails
	 */
	protected void doRemoveName(IName name, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump name
		 */
		dump(revision, name);
		/*
		 * remove variants
		 */
		for (IVariant variant : doReadVariants(name, -1, -1)) {
			/*
			 * remove variant
			 */
			doRemoveVariant(variant, cascade, revision);
		}
		/*
		 * check to remove reification
		 */
		ITopic topic = doReadReification(name);
		if (topic != null) {
			doRemoveTopic(topic, cascade, revision);
		}
		/*
		 * remove name
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteName();
		stmt.setLong(1, Long.parseLong(name.getId()));
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.NAME_REMOVED, name.getParent(), null, name);
		/*
		 * store history
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.NAME_REMOVED, name.getParent(), null,
				name);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws SQLException {
		doRemoveOccurrence(occurrence, cascade,
				getSession().getTopicMapStore().createRevision(TopicMapEventType.OCCURRENCE_ADDED));
		return true;
	}

	/**
	 * Using JDBC to remove an occurrence instance.
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param cascade
	 *            cascading deletion
	 * @param revision
	 *            the revision to store changes
	 * @throws SQLException
	 *             thrown if JDBC command fails
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump occurrence
		 */
		dump(revision, occurrence);
		/*
		 * check to remove reification
		 */
		ITopic topic = doReadReification(occurrence);
		if (topic != null) {
			doRemoveTopic(topic, cascade, revision);
		}
		/*
		 * remove occurrence
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteOccurrence();
		stmt.setLong(1, Long.parseLong(occurrence.getId()));
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, occurrence.getParent(),
				null, occurrence);
		/*
		 * store history
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.OCCURRENCE_REMOVED,
				occurrence.getParent(), null, occurrence);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveRole(IAssociationRole role, boolean cascade) throws SQLException {
		doRemoveRole(role, cascade, getSession().getTopicMapStore().createRevision(TopicMapEventType.ROLE_REMOVED));
		return true;
	}

	/**
	 * Using JDBC to remove a role instance.
	 * 
	 * @param role
	 *            the role
	 * @param cascade
	 *            cascading deletion
	 * @param revision
	 *            the revision to store changes
	 * @throws SQLException
	 *             thrown if JDBC command fails
	 */
	public void doRemoveRole(IAssociationRole role, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump role
		 */
		dump(revision, role);
		/*
		 * check to remove reification
		 */
		ITopic topic = doReadReification(role);
		if (topic != null) {
			doRemoveTopic(topic, cascade, revision);
		}
		/*
		 * remove role
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteRole();
		stmt.setLong(1, Long.parseLong(role.getId()));
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.ROLE_REMOVED, role.getParent(), null, role);
		/*
		 * store history
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.ROLE_REMOVED, role.getParent(), null,
				role);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveScope(IScopable s, ITopic theme) throws SQLException {
		IScope oldScope = doReadScope(s);
		Collection<ITopic> themes = HashUtil.getHashSet(oldScope.getThemes());
		themes.remove(theme);
		IScope scope = doCreateScope(s.getTopicMap(), themes);
		PreparedStatement stmt = null;
		if (s instanceof IName) {
			stmt = getQueryBuilder().getQueryModifyNameScope();
		} else if (s instanceof IOccurrence) {
			stmt = getQueryBuilder().getQueryModifyOccurrenceScope();
		} else if (s instanceof IAssociation) {
			stmt = getQueryBuilder().getQueryModifyAssociationScope();
		} else {
			stmt = getQueryBuilder().getQueryModifyVariantScope();
		}
		stmt.setLong(1, Long.parseLong(scope.getId()));
		stmt.setLong(2, Long.parseLong(s.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteSubjectIdentifier();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectIdentifier.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteSubjectLocator();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setString(2, subjectLocator.getReference());
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveSupertype(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteSupertype();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.execute();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean doRemoveTopic(ITopic topic, boolean cascade) throws SQLException {
		doRemoveTopic(topic, cascade, getSession().getTopicMapStore().createRevision(TopicMapEventType.TOPIC_REMOVED));
		return true;
	}

	/**
	 * Using JDBC to remove a topic and all its dependent constructs.
	 * 
	 * @param topic
	 *            the topic
	 * @param cascade
	 *            the cascading flag
	 * @param revision
	 *            the revision
	 * @throws SQLException
	 *             thrown if the JDBC command fails
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump topic
		 */
		dump(revision, topic);
		/*
		 * remove all names of the topic
		 */
		for (IName name : doReadNames(topic, -1, -1)) {
			doRemoveName(name, cascade, revision);
		}
		/*
		 * remove all occurrences of the topic
		 */
		for (IOccurrence occurrence : doReadOccurrences(topic, -1, -1)) {
			doRemoveOccurrence(occurrence, cascade, revision);
		}
		/*
		 * remove all played-association
		 */
		for (IAssociation association : doReadAssociation(topic, -1, -1)) {
			doRemoveAssociation(association, cascade, revision);
		}
		/*
		 * remove all instance
		 */
		for (ITopic instance : doReadTopics(topic.getTopicMap(), topic)) {
			doRemoveTopic(instance, cascade, revision);
		}
		/*
		 * remove all subtypes
		 */
		for (ITopic subtype : getSubtypes(topic.getTopicMap(), topic, -1, -1)) {
			doRemoveTopic(subtype, cascade, revision);
		}
		/*
		 * remove typed associations
		 */
		for (IAssociation association : getAssociationsByType(topic, -1, -1)) {
			/*
			 * remove association
			 */
			doRemoveAssociation(association, cascade, revision);
		}
		/*
		 * remove typed roles
		 */
		for (IAssociationRole role : getRolesByType(topic, -1, -1)) {
			/*
			 * remove role
			 */
			doRemoveRole(role, cascade, revision);
		}
		/*
		 * remove typed name
		 */
		for (IName name : getNamesByType(topic, -1, -1)) {
			/*
			 * remove name
			 */
			doRemoveName(name, cascade, revision);
		}
		/*
		 * remove typed occurrences
		 */
		for (IOccurrence occurrence : getOccurrencesByType(topic, -1, -1)) {
			/*
			 * remove occurrence
			 */
			doRemoveOccurrence(occurrence, cascade, revision);
		}
		/*
		 * get all scopes
		 */
		Collection<ITopic> themes = HashUtil.getHashSet();
		themes.add(topic);
		for (IScope scope : getScopesByThemes(topic.getTopicMap(), themes, false)) {
			/*
			 * remove scoped associations
			 */
			for (IAssociation association : getAssociationsByScope(topic.getTopicMap(), scope, -1, -1)) {
				/*
				 * remove association
				 */
				doRemoveAssociation(association, cascade, revision);
			}
			/*
			 * remove scoped name
			 */
			for (IName name : getNamesByScope(topic.getTopicMap(), scope, -1, -1)) {
				/*
				 * remove name
				 */
				doRemoveName(name, cascade, revision);
			}
			/*
			 * remove scoped occurrences
			 */
			for (IOccurrence occurrence : getOccurrencesByScope(topic.getTopicMap(), scope, -1, -1)) {
				/*
				 * remove occurrence
				 */
				doRemoveOccurrence(occurrence, cascade, revision);
			}
			/*
			 * remove scoped variants
			 */
			for (IVariant variant : getVariantsByScope(topic.getTopicMap(), scope, -1, -1)) {
				/*
				 * remove variant
				 */
				doRemoveVariant(variant, cascade, revision);
			}
		}
		/*
		 * remove topic
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteTopic();
		long id = Long.parseLong(topic.getId());
		stmt.setLong(1, id);
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.TOPIC_REMOVED, topic.getTopicMap(), null,
				topic);
		/*
		 * store revision
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, topic.getTopicMap(),
				null, topic);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws SQLException {
		for (ILocator loc : doReadItemIdentifiers(topicMap)) {
			doRemoveItemIdentifier(topicMap, loc);
		}
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteTopicMap();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doClearTopicMap(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryClearTopicMap();
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, topicMapId);
		stmt.setLong(4, topicMapId);
		stmt.setLong(5, topicMapId);
		stmt.setLong(6, topicMapId);
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemoveType(ITopic t, ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteType();
		stmt.setLong(1, Long.parseLong(t.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean doRemoveVariant(IVariant variant, boolean cascade) throws SQLException {
		doRemoveVariant(variant, cascade,
				getSession().getTopicMapStore().createRevision(TopicMapEventType.VARIANT_REMOVED));
		return true;
	}

	public void doRemoveVariant(IVariant variant, boolean cascade, IRevision revision) throws SQLException {
		/*
		 * dump variant
		 */
		dump(revision, variant);
		/*
		 * check to remove reification
		 */
		ITopic topic = doReadReification(variant);
		if (topic != null) {
			doRemoveTopic(topic, cascade, revision);
		}
		/*
		 * remove variant
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryDeleteVariant();
		stmt.setLong(1, Long.parseLong(variant.getId()));
		stmt.execute();
		/*
		 * notify listener
		 */
		getSession().getTopicMapStore().notifyListeners(TopicMapEventType.VARIANT_REMOVED, variant.getParent(), null,
				variant);
		/*
		 * store history
		 */
		getSession().getTopicMapStore().storeRevision(revision, TopicMapEventType.VARIANT_REMOVED, variant.getParent(),
				null, variant);
	}

	// ****************
	// * INDEX METHOD *
	// ****************

	// PagedConstructIndex

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfAssociationsPlayed(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfAssociationsPlayed();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfNames(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfNames();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfOccurrences(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfOccurrences();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfRoles(IAssociation association) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfRoles();
		stmt.setLong(1, Long.parseLong(association.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfRolesPlayed(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfRolesPlayed();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfSupertypes(ITopic topic) throws SQLException {
		if (topic == null) {
			PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfTopicsWithoutSupertypes();
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getLong(1);
		}
		return doReadSuptertypes(topic, -1, -1).size();
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfTypes(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfTypes();
		stmt.setLong(1, Long.parseLong(topic.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	/**
	 * {@inheritDoc}
	 */
	public long doReadNumberOfVariants(IName name) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadNumberOfVariants();
		stmt.setLong(1, Long.parseLong(name.getId()));
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getLong(1);
	}

	// TypeInstanceIndex

	public Collection<ITopic> getAssociationTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectAssociationTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getNameTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNameTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getOccurrenceTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrenceTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getCharacteristicsTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getRoleTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectRoleTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicTypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_type");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByType(ITopic type, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectAssociationsByType(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(type.getTopicMap(), set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociation> getAssociationsByTypes(Collection<T> types, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectAssociationsByTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(getSession().getTopicMapStore().getTopicMap().getId()));
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(getSession().getTopicMapStore().getTopicMap(), set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByType(ITopic type, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByType(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(getSession().getTopicMapStore().getTopicMap(), set);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ICharacteristics> getCharacteristicsByTypes(Collection<T> types, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByTypes(offset != -1);
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		Array a = getConnection().createArrayOf("bigint", ids);
		stmt.setArray(1, a);
		stmt.setArray(2, a);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(getSession().getTopicMapStore().getTopicMap(), set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByType(ITopic type, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByType(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IName> getNamesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(getSession().getTopicMapStore().getTopicMap().getId()));
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(getSession().getTopicMapStore().getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByType(ITopic type, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByType(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(getSession().getTopicMapStore().getTopicMap().getId()));
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(getSession().getTopicMapStore().getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> getRolesByType(ITopic type, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectRolesByType(offset != -1);
		stmt.setLong(1, Long.parseLong(type.getTopicMap().getId()));
		stmt.setLong(2, Long.parseLong(type.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toRoles(type.getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociationRole> getRolesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectRolesByTypes(offset != -1);
		stmt.setLong(1, Long.parseLong(getSession().getTopicMapStore().getTopicMap().getId()));
		Long ids[] = new Long[types.size()];
		int n = 0;
		for (T type : types) {
			ids[n++] = Long.parseLong(type.getId());
		}
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toRoles(getSession().getTopicMapStore().getTopicMap(), set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByTypes(Collection<T> types, boolean all, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicsByTypes(types.size(), all, offset != -1);
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
		if (offset != -1) {
			stmt.setLong(n++, offset);
			stmt.setLong(n++, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_instance");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByType(ITopicMap topicMap, T type, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder()
				.getQuerySelectTopicsByTypes(type == null ? 0 : 1, true, offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (type != null) {
			stmt.setLong(2, Long.parseLong(type.getId()));
			if (offset != -1) {
				stmt.setLong(3, offset);
				stmt.setLong(4, limit);
			}
			return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_instance");
		}
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	// TransitiveTypeInstanceIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException {
		/*
		 * create association set and add associations directly typed by the given type
		 */
		Collection<IAssociation> set = HashUtil.getHashSet(getAssociationsByType(type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all associations typed by the sub-type
			 */
			set.addAll(getAssociationsByType(t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IAssociation> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociation> getAssociationsByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * create association set
		 */
		Collection<IAssociation> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add associations transitive by type
			 */
			set.addAll(getAssociationsByTypeTransitive((ITopic) type, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IAssociation> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException {
		/*
		 * create characteristics set and add characteristics directly typed by the given type
		 */
		Collection<ICharacteristics> set = HashUtil.getHashSet(getCharacteristicsByType(type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all characteristics typed by the sub-type
			 */
			set.addAll(getCharacteristicsByType(t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<ICharacteristics> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ICharacteristics> getCharacteristicsByTypesTransitive(Collection<T> types,
			long offset, long limit) throws SQLException {
		/*
		 * create characteristics set
		 */
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add characteristics transitive by type
			 */
			set.addAll(getCharacteristicsByTypeTransitive((ITopic) type, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<ICharacteristics> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * create name set and add names directly typed by the given type
		 */
		Collection<IName> set = HashUtil.getHashSet(getNamesByType(type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all names typed by the sub-type
			 */
			set.addAll(getNamesByType(t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IName> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IName> getNamesByTypeTransitive(ITopicMap topicMap, Collection<T> types,
			long offset, long limit) throws SQLException {
		/*
		 * create name set
		 */
		Collection<IName> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add names transitive by type
			 */
			set.addAll(getNamesByTypeTransitive((ITopic) type, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IName> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException {
		/*
		 * create occurrence set and add occurrences directly typed by the given type
		 */
		Collection<IOccurrence> set = HashUtil.getHashSet(getOccurrencesByType(type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all occurrences typed by the sub-type
			 */
			set.addAll(getOccurrencesByType(t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IOccurrence> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * create occurrences set
		 */
		Collection<IOccurrence> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add occurrences transitive by type
			 */
			set.addAll(getOccurrencesByTypeTransitive((ITopic) type, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IOccurrence> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociationRole> getRolesByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException {
		/*
		 * create role set and add roles directly typed by the given type
		 */
		Collection<IAssociationRole> set = HashUtil.getHashSet(getRolesByType(type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all roles typed by the sub-type
			 */
			set.addAll(getRolesByType(t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IAssociationRole> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IAssociationRole> getRolesByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException {
		/*
		 * create roles set
		 */
		Collection<IAssociationRole> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add roles transitive by type
			 */
			set.addAll(getRolesByTypeTransitive((ITopic) type, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<IAssociationRole> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsByTypeTransitive(ITopic type, long offset, long limit) throws SQLException {
		/*
		 * create topics set and add topics directly typed by the given type
		 */
		Collection<ITopic> set = HashUtil.getHashSet(getTopicsByType(type.getTopicMap(), type, -1, -1));
		/*
		 * iterate over all sub-types of the given type
		 */
		for (ITopic t : getSubtypes(type.getTopicMap(), type, -1, -1)) {
			/*
			 * add all topics typed by the sub-type
			 */
			set.addAll(getTopicsByType(type.getTopicMap(), t, -1, -1));
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<ITopic> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> types,
			boolean all, long offset, long limit) throws SQLException {
		/*
		 * flag indicates first iteration
		 */
		boolean first = true;
		/*
		 * create topics set
		 */
		Collection<ITopic> set = HashUtil.getHashSet();
		/*
		 * iterate over all types
		 */
		for (T type : types) {
			/*
			 * add roles transitive by type if is first iteration or matching-all flag is false
			 */
			if (first || !all) {
				set.addAll(getTopicsByTypeTransitive((ITopic) type, -1, -1));
			} else {
				set.retainAll(getTopicsByTypeTransitive((ITopic) type, -1, -1));
			}
			first = false;
		}
		/*
		 * check if paging is expected
		 */
		if (offset != -1) {
			List<ITopic> list = HashUtil.getList(set);
			Collections.sort(list, new Comparator<IConstruct>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(IConstruct o1, IConstruct o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			list = list.subList((int) offset, (int) ((offset + limit < list.size()) ? offset + limit : list.size()));
			return list;
		}
		return set;
	}

	// ScopedIndex

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<IScope> getScopesByThemes(final ITopicMap topicMap, Collection<T> themes,
			boolean all) throws SQLException {
		Collection<IScope> scopes = HashUtil.getHashSet();
		if (themes.isEmpty()) {
			PreparedStatement stmt = getQueryBuilder().getQueryReadEmptyScope();
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
			ResultSet set = stmt.executeQuery();
			if (set.next()) {
				long id = set.getLong("id");
				set.close();
				scopes.add(new ScopeImpl(Long.toString(id)));
			}
		} else {
			/*
			 * read scope by themes
			 */
			PreparedStatement stmt = getQueryBuilder().getQueryReadScopeByTheme();
			boolean first = true;
			for (T theme : themes) {
				Collection<Long> ids = HashUtil.getHashSet();
				stmt.setLong(1, Long.parseLong(theme.getId()));
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					ids.add(rs.getLong("id_scope"));
				}
				rs.close();

				Collection<IScope> temp = HashUtil.getHashSet();
				for (Long id : ids) {
					temp.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
				}
				if (first || !all) {
					first = false;
					scopes.addAll(temp);
				} else {
					scopes.retainAll(temp);
					if (scopes.isEmpty()) {
						break;
					}
				}
			}
			Collection<IScope> temp = HashUtil.getHashSet(scopes);
			scopes.clear();
			for (IScope scope : temp) {
				if (!getAssociationsByScope(topicMap, scope, -1, -1).isEmpty()) {
					scopes.add(scope);
					continue;
				}
				if (!getNamesByScope(topicMap, scope, -1, -1).isEmpty()) {
					scopes.add(scope);
					continue;
				}
				if (!getOccurrencesByScope(topicMap, scope, -1, -1).isEmpty()) {
					scopes.add(scope);
					continue;
				}
				if (!getVariantsByScope(topicMap, scope, -1, -1).isEmpty()) {
					scopes.add(scope);
					continue;
				}
			}
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationsByScope(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationsByScopes(offset != -1);
		Long ids[] = new Long[scopes.size()];
		int n = 0;
		for (IScope s : scopes) {
			ids[n++] = Long.parseLong(s.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException {
		/*
		 * require empty scope
		 */
		if (theme == null) {
			Collection<ITopic> themes = HashUtil.getHashSet();
			IScope scope = doCreateScope(topicMap, themes);
			return getAssociationsByScope(topicMap, scope, offset, limit);
		}
		/*
		 * require non-empty scope
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationsByTheme(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(theme.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IAssociation> getAssociationsByThemes(ITopicMap topicMap, Topic[] themes, boolean all,
			long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationsByThemes(all, offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[themes.length];
		int n = 0;
		for (Topic t : themes) {
			ids[n++] = Long.parseLong(t.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toAssociations(topicMap, set, "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationScopes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
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
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getAssociationThemes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationThemes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByScope(ITopicMap topicMap, IScope scope, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryCharacteristicsByScope(offset != -1);
		long scopeId = Long.parseLong(scope.getId());
		stmt.setLong(1, scopeId);
		stmt.setLong(2, scopeId);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryNamesByScope(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset, long limit)
			throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = getQueryBuilder().getQueryNamesByScopes(offset != -1);
		Long ids[] = new Long[scopes.size()];
		int n = 0;
		for (IScope s : scopes) {
			ids[n++] = Long.parseLong(s.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException {
		/*
		 * require empty scope
		 */
		if (theme == null) {
			Collection<ITopic> themes = HashUtil.getHashSet();
			IScope scope = doCreateScope(topicMap, themes);
			return getNamesByScope(topicMap, scope, offset, limit);
		}
		/*
		 * require non-empty scope
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryNamesByTheme(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(theme.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryNamesByThemes(all, offset != -1);
		Long ids[] = new Long[themes.length];
		int n = 0;
		for (Topic t : themes) {
			ids[n++] = Long.parseLong(t.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryNameScopes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
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
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getNameThemes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryNameThemes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrencesByScope(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrencesByScopes(offset != -1);
		Long ids[] = new Long[scopes.size()];
		int n = 0;
		for (IScope s : scopes) {
			ids[n++] = Long.parseLong(s.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException {
		/*
		 * require empty scope
		 */
		if (theme == null) {
			Collection<ITopic> themes = HashUtil.getHashSet();
			IScope scope = doCreateScope(topicMap, themes);
			return getOccurrencesByScope(topicMap, scope, offset, limit);
		}/*
		 * require non-empty scope
		 */
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrencesByTheme(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(theme.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrencesByThemes(all, offset != -1);
		Long ids[] = new Long[themes.length];
		int n = 0;
		for (Topic t : themes) {
			ids[n++] = Long.parseLong(t.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, set, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrenceScopes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
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
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getOccurrenceThemes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrenceThemes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScopable> getScopables(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryScopables(offset != -1);
		long scopeId = Long.parseLong(scope.getId());
		stmt.setLong(1, scopeId);
		stmt.setLong(2, scopeId);
		stmt.setLong(3, scopeId);
		stmt.setLong(4, scopeId);
		if (offset != -1) {
			stmt.setLong(5, offset);
			stmt.setLong(6, limit);
		}
		return Jdbc2Construct.toScopables(topicMap, stmt.executeQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantsByScope(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(scope.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException {
		/*
		 * if no scope is specified return empty set
		 */
		if (scopes.isEmpty()) {
			return HashUtil.getHashSet();
		}
		PreparedStatement stmt = getQueryBuilder().getQueryVariantsByScopes(offset != -1);
		Long ids[] = new Long[scopes.size()];
		int n = 0;
		for (IScope s : scopes) {
			ids[n++] = Long.parseLong(s.getId());
		}
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, getConnection().createArrayOf("bigint", ids));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantsByTheme(offset != -1);
		long themeId = Long.parseLong(theme.getId());
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, themeId);
		stmt.setLong(3, themeId);
		if (offset != -1) {
			stmt.setLong(4, offset);
			stmt.setLong(5, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantsByThemes(all, offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		Long ids[] = new Long[themes.length];
		int n = 0;
		for (Topic t : themes) {
			ids[n++] = Long.parseLong(t.getId());
		}
		Array array = getConnection().createArrayOf("bigint", ids);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setArray(2, array);
		n = 3;
		if (!all) {
			stmt.setArray(3, array);
			n++;
		}
		if (offset != -1) {
			stmt.setLong(n++, offset);
			stmt.setLong(n, limit);
		}
		ResultSet set = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantScopes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
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
			scopes.add(new ScopeImpl(Long.toString(id), doReadThemes(topicMap, id)));
		}
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getVariantThemes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantThemes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_theme");
	}

	// LiteralIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(ITopicMap topicMap, String value, String reference,
			long offset, long limit) throws SQLException {
		if (!XmlSchemeDatatypes.XSD_STRING.equals(reference)) {
			Collection<ICharacteristics> col = HashUtil.getList();
			col.addAll(getOccurrences(topicMap, value, reference, offset, limit));
			return col;
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristics(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, value);
		stmt.setString(3, reference);
		stmt.setLong(4, topicMapId);
		stmt.setString(5, value);
		if (offset != -1) {
			stmt.setLong(6, offset);
			stmt.setLong(7, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(ITopicMap topicMap, String value, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByValue(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, value);
		stmt.setLong(3, topicMapId);
		stmt.setString(4, value);
		if (offset != -1) {
			stmt.setLong(5, offset);
			stmt.setLong(6, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByDatatype(ITopicMap topicMap, String reference, long offset,
			long limit) throws SQLException {
		if (!XmlSchemeDatatypes.XSD_STRING.equals(reference)) {
			Collection<ICharacteristics> col = HashUtil.getList();
			col.addAll(getOccurrencesByDatatype(topicMap, reference, offset, limit));
			return col;
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByDatatype(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, reference);
		stmt.setLong(3, topicMapId);
		if (offset != -1) {
			stmt.setLong(4, offset);
			stmt.setLong(5, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByPattern(ITopicMap topicMap, String value, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByPattern(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, value);
		stmt.setLong(3, topicMapId);
		stmt.setString(4, value);
		if (offset != -1) {
			stmt.setLong(5, offset);
			stmt.setLong(6, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristicsByPattern(ITopicMap topicMap, String value, String reference,
			long offset, long limit) throws SQLException {
		if (!XmlSchemeDatatypes.XSD_STRING.equals(reference)) {
			Collection<ICharacteristics> col = HashUtil.getList();
			col.addAll(getOccurrencesByPattern(topicMap, value, reference));
			return col;
		}
		PreparedStatement stmt = getQueryBuilder().getQuerySelectCharacteristicsByPatternAndDatatype(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, value);
		stmt.setString(3, reference);
		stmt.setLong(4, topicMapId);
		stmt.setString(5, value);
		if (offset != -1) {
			stmt.setLong(6, offset);
			stmt.setLong(7, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toCharacteristics(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IDatatypeAware> getDatatypeAwaresByDatatype(ITopicMap topicMap, String reference, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectDatatypeAwaresByDatatype(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		stmt.setLong(1, topicMapId);
		stmt.setString(2, reference);
		stmt.setLong(3, topicMapId);
		stmt.setString(4, reference);
		if (offset != -1) {
			stmt.setLong(5, offset);
			stmt.setLong(6, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toDatatypeAwares(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNames(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNames(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNames(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IName> getNamesByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectNamesByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toNames(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrences(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, Calendar lower, Calendar upper, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByDateRange(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setTimestamp(2, new Timestamp(lower.getTimeInMillis()));
		stmt.setTimestamp(3, new Timestamp(upper.getTimeInMillis()));
		if (offset != -1) {
			stmt.setLong(4, offset);
			stmt.setLong(5, limit);
		}
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, double value, double deviance,
			final String reference, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByRange(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		stmt.setDouble(3, value - deviance);
		stmt.setDouble(4, value + deviance);
		if (offset != -1) {
			stmt.setLong(5, offset);
			stmt.setLong(6, limit);
		}
		return Jdbc2Construct.toOccurrences(topicMap, stmt.executeQuery(), "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrences(ITopicMap topicMap, String value, String reference, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByValueAndDatatype(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		stmt.setString(3, reference);
		if (offset != -1) {
			stmt.setLong(4, offset);
			stmt.setLong(5, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByDatatype(ITopicMap topicMap, String reference, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByDatatype(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IOccurrence> getOccurrencesByPattern(ITopicMap topicMap, String pattern, String reference)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectOccurrencesByPatternAndDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		stmt.setString(3, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toOccurrences(topicMap, rs, "id", "id_parent");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariants(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap, String value) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariantsByValue();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, value);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariants(ITopicMap topicMap, String value, String reference) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariantsByValueAndDatatype();
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
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariantsByDatatype();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, reference);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantByPattern(ITopicMap topicMap, String pattern) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariantsByPattern();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, pattern);
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toVariants(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IVariant> getVariantsByPattern(ITopicMap topicMap, String pattern, String reference)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectVariantsByPatternAndDatatype();
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
		PreparedStatement stmt = getQueryBuilder().getQuerySelectItemIdentifiers(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> getSubjectIdentifiers(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSubjectIdentifiers(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ILocator> getSubjectLocators(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSubjectLocators(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toLocators(stmt.executeQuery(), "reference");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IConstruct> getConstructsByIdentitifer(ITopicMap topicMap, String regExp, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectConstructsByIdentitifer(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		String pattern = "^" + regExp + "$";
		stmt.setString(1, pattern);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, topicMapId);
		stmt.setLong(4, topicMapId);
		stmt.setLong(5, topicMapId);
		stmt.setLong(6, topicMapId);
		stmt.setLong(7, topicMapId);
		if (offset != -1) {
			stmt.setLong(8, offset);
			stmt.setLong(9, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toConstructs(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IConstruct> getConstructsByItemIdentitifer(ITopicMap topicMap, String regExp, long offset,
			long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectConstructsByItemIdentitifer(offset != -1);
		long topicMapId = Long.parseLong(topicMap.getId());
		String pattern = "^" + regExp + "$";
		stmt.setString(1, pattern);
		stmt.setLong(2, topicMapId);
		stmt.setLong(3, topicMapId);
		stmt.setLong(4, topicMapId);
		stmt.setLong(5, topicMapId);
		stmt.setLong(6, topicMapId);
		stmt.setLong(7, topicMapId);
		if (offset != -1) {
			stmt.setLong(8, offset);
			stmt.setLong(9, limit);
		}
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toConstructs(topicMap, rs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsBySubjectIdentitifer(ITopicMap topicMap, String regExp, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicsBySubjectIdentitifer(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, "^" + regExp + "$");
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_topic");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getTopicsBySubjectLocator(ITopicMap topicMap, String regExp, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectTopicsBySubjectLocator(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, "^" + regExp + "$");
		if (offset != -1) {
			stmt.setLong(3, offset);
			stmt.setLong(4, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_topic");
	}

	// SupertypeSubtypeIndex

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getDirectSubtypes(ITopicMap topicMap, ITopic type, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSubtypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = getQueryBuilder().getQuerySelectDirectSubtypes(offset != -1);
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getDirectSupertypes(ITopicMap topicMap, ITopic type, long offset, long limit)
			throws SQLException {
		PreparedStatement stmt = null;
		if (type == null) {
			stmt = getQueryBuilder().getQuerySelectTopicsWithoutSupertypes(offset != -1);
			stmt.setLong(1, Long.parseLong(topicMap.getId()));
		} else {
			stmt = getQueryBuilder().getQuerySelectDirectSupertypes(offset != -1);
			stmt.setLong(1, Long.parseLong(type.getId()));
		}
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

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
		Collection<ITopic> knownSubtypes = HashUtil.getHashSet();
		List<ITopic> result = HashUtil.getList(getSubtypes(topicMap, type, knownSubtypes));
		if (offset != -1) {
			Collections.sort(result, new Comparator<ITopic>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(ITopic o1, ITopic o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			result = result.subList((int) offset,
					(int) ((offset + limit < result.size()) ? offset + limit : result.size()));
		}
		return result;
	}

	private Collection<ITopic> getSubtypes(ITopicMap topicMap, ITopic type, Collection<ITopic> knownSubtypes)
			throws SQLException {
		/*
		 * get sub-types of given topic
		 */
		PreparedStatement stmt = null;
		stmt = getQueryBuilder().getQuerySelectSubtypesOfTopic(false);
		stmt.setLong(1, Long.parseLong(type.getId()));
		Collection<ITopic> types = HashUtil.getHashSet();
		Collection<ITopic> topics = Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		for (ITopic topic : topics) {
			/*
			 * avoid cycle
			 */
			if (knownSubtypes.contains(topic)) {
				continue;
			}
			knownSubtypes.add(topic);
			types.add(topic);
			types.addAll(getSubtypes(topicMap, topic, knownSubtypes));
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSubtypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSubtypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id_subtype");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSubtypes(ITopicMap topicMap, Collection<T> types, boolean matchAll,
			long offset, long limit) throws SQLException {
		Collection<ITopic> topics = HashUtil.getHashSet();
		boolean first = true;
		for (T type : types) {
			if (first || !matchAll) {
				topics.addAll(getSubtypes(topicMap, (ITopic) type, -1, -1));
			} else {
				topics.retainAll(getSubtypes(topicMap, (ITopic) type, -1, -1));
			}
			first = false;
		}
		List<ITopic> result = HashUtil.getList(topics);
		if (offset != -1) {
			Collections.sort(result, new Comparator<ITopic>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(ITopic o1, ITopic o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			result = result.subList((int) offset,
					(int) ((offset + limit < result.size()) ? offset + limit : result.size()));
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSupertypes(ITopicMap topicMap, ITopic type, long offset, long limit)
			throws SQLException {
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
		Collection<ITopic> knownSupertypes = HashUtil.getHashSet();
		List<ITopic> result = HashUtil.getList(getSupertypes(topicMap, type, knownSupertypes));
		if (offset != -1) {
			Collections.sort(result, new Comparator<ITopic>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(ITopic o1, ITopic o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			result = result.subList((int) offset,
					(int) ((offset + limit < result.size()) ? offset + limit : result.size()));
		}
		return result;

	}

	/**
	 * Returns all super-types of the given topic type.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param type
	 *            the type
	 * @param knownSupertypes
	 *            a collection containing all known super-types to avoid cycles
	 * @return a collection of all transitive supertypes of the given type
	 * @throws SQLException
	 *             thrown by JDBC
	 */
	private Collection<ITopic> getSupertypes(ITopicMap topicMap, ITopic type, Collection<ITopic> knownSupertypes)
			throws SQLException {
		/*
		 * get super-types of given topic
		 */
		PreparedStatement stmt = null;
		stmt = getQueryBuilder().getQuerySelectSupertypesOfTopic(false);
		stmt.setLong(1, Long.parseLong(type.getId()));
		Collection<ITopic> types = HashUtil.getHashSet();
		Collection<ITopic> topics = Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
		/*
		 * iterate over all super-types and get transitive super-types
		 */
		for (ITopic topic : topics) {
			/*
			 * avoid cycle
			 */
			if (knownSupertypes.contains(topic)) {
				continue;
			}
			knownSupertypes.add(topic);
			types.add(topic);
			types.addAll(getSupertypes(topicMap, topic, knownSupertypes));
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ITopic> getSupertypes(ITopicMap topicMap, long offset, long limit) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQuerySelectSupertypes(offset != -1);
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		if (offset != -1) {
			stmt.setLong(2, offset);
			stmt.setLong(3, limit);
		}
		return Jdbc2Construct.toTopics(topicMap, stmt.executeQuery(), "id");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Topic> Collection<ITopic> getSupertypes(ITopicMap topicMap, Collection<T> types,
			boolean matchAll, long offset, long limit) throws SQLException {
		Collection<ITopic> topics = HashUtil.getHashSet();
		boolean first = true;
		for (T type : types) {
			if (first || !matchAll) {
				topics.addAll(getSupertypes(topicMap, (ITopic) type, -1, -1));
			} else {
				topics.retainAll(getSupertypes(topicMap, (ITopic) type, -1, -1));
			}
			first = false;
		}
		List<ITopic> result = HashUtil.getList(topics);
		if (offset != -1) {
			Collections.sort(result, new Comparator<ITopic>() {
				/**
				 * {@inheritDoc}
				 */
				public int compare(ITopic o1, ITopic o2) {
					return (int) (Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()));
				}
			});
			result = result.subList((int) offset,
					(int) ((offset + limit < result.size()) ? offset + limit : result.size()));
		}
		return result;
	}

	/*
	 * revision management
	 */

	/**
	 * {@inheritDoc}
	 */
	public IRevision doCreateRevision(ITopicMap topicMap, TopicMapEventType type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryCreateRevision();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, type.name());
		stmt.execute();
		ResultSet rs = stmt.getGeneratedKeys();
		try {
			rs.next();
			return new RevisionImpl(getSession().getTopicMapStore(), rs.getLong("GENERATED_KEY")) {
			};
		} finally {
			rs.close();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void doCreateChangeSet(IRevision revision, TopicMapEventType type, IConstruct notifier, Object newValue,
			Object oldValue) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryCreateChangeset();
		stmt.setLong(1, revision.getId());
		stmt.setString(2, type.name());
		stmt.setLong(3, Long.parseLong(notifier.getId()));
		String nVal = null;
		if (newValue instanceof IConstruct) {
			nVal = ((IConstruct) newValue).getId();
		} else if (newValue instanceof ILocator) {
			nVal = ((ILocator) newValue).getReference();
		} else if (newValue instanceof IScope) {
			nVal = ((IScope) newValue).getId();
		} else if (newValue != null) {
			nVal = newValue.toString();
		}
		if (nVal != null) {
			stmt.setString(4, nVal);
		} else {
			stmt.setNull(4, Types.VARCHAR);
		}
		String oVal = null;
		if (oldValue instanceof IConstruct) {
			oVal = ((IConstruct) oldValue).getId();
		} else if (oldValue instanceof ILocator) {
			oVal = ((ILocator) oldValue).getReference();
		} else if (oldValue instanceof IScope) {
			oVal = ((IScope) oldValue).getId();
		} else if (oldValue != null) {
			oVal = oldValue.toString();
		}
		if (oVal != null) {
			stmt.setString(5, oVal);
		} else {
			stmt.setNull(5, Types.VARCHAR);
		}
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doCreateTag(String tag, Calendar time) throws SQLException {
		PreparedStatement stmt = null;
		/*
		 * try to update time if tag exists
		 */
		stmt = getQueryBuilder().getQueryModifyTag();
		stmt.setTimestamp(1, new Timestamp(time.getTimeInMillis()));
		stmt.setString(2, tag);
		/*
		 * tag was not found -> create tag
		 */
		if (stmt.executeUpdate() == 0) {
			stmt = getQueryBuilder().getQueryCreateTag();
			stmt.setString(1, tag);
			stmt.setTimestamp(2, new Timestamp(time.getTimeInMillis()));
			stmt.execute();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doCreateMetadata(IRevision revision, String key, String value) throws SQLException {
		PreparedStatement stmt = null;
		/*
		 * try to update value if key exists
		 */
		stmt = getQueryBuilder().getQueryModifyMetadata();
		stmt.setString(1, value);
		stmt.setLong(2, revision.getId());
		stmt.setString(3, key);
		/*
		 * key was not found -> create key
		 */
		if (stmt.executeUpdate() == 0) {
			stmt = getQueryBuilder().getQueryCreateMetadata();
			stmt.setLong(1, revision.getId());
			stmt.setString(2, key);
			stmt.setString(3, value);
			stmt.execute();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeset(ITopicMap topicMap, IRevision revision) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadChangesets();
		stmt.setLong(1, revision.getId());
		ResultSet rs = stmt.executeQuery();
		return Jdbc2Construct.toChangeSet(this, topicMap, rs, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangesetsByAssociationType(ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadChangesetsByAssociationType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		return Jdbc2Construct.toChangeSet(this, type.getTopicMap(), stmt.executeQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangesetsByTopic(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadChangesetsByTopic();
		long topicId = Long.parseLong(topic.getId());
		stmt.setLong(1, topicId);
		stmt.setString(2, topic.getId());
		stmt.setString(3, topic.getId());
		return Jdbc2Construct.toChangeSet(this, topic.getTopicMap(), stmt.executeQuery());
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFirstRevision(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadFirstRevision();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		IRevision revision = null;
		if (rs.next()) {
			revision = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return revision;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFutureRevision(ITopicMap topicMap, IRevision revision) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadFutureRevision();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, revision.getId());
		ResultSet rs = stmt.executeQuery();
		IRevision r = null;
		if (rs.next()) {
			r = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadLastModification(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadLastModification();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		Calendar c = null;
		if (rs.next()) {
			Timestamp ts = rs.getTimestamp("time");
			c = new GregorianCalendar();
			c.setTimeInMillis(ts.getTime());
		}
		rs.close();
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadLastModificationOfTopic(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadLastModificationOfTopic();
		long topicId = Long.parseLong(topic.getId());
		stmt.setLong(1, topicId);
		stmt.setString(2, topic.getId());
		stmt.setString(3, topic.getId());
		ResultSet rs = stmt.executeQuery();
		Calendar c = null;
		if (rs.next()) {
			Timestamp ts = rs.getTimestamp("time");
			c = new GregorianCalendar();
			c.setTimeInMillis(ts.getTime());
		}
		rs.close();
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadLastRevision(ITopicMap topicMap) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadLastRevision();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		ResultSet rs = stmt.executeQuery();
		IRevision revision = null;
		if (rs.next()) {
			revision = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return revision;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(ITopicMap topicMap, IRevision revision) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadPastRevision();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setLong(2, revision.getId());
		ResultSet rs = stmt.executeQuery();
		IRevision r = null;
		if (rs.next()) {
			r = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> doReadRevisionsByAssociationType(ITopic type) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRevisionsByAssociationType();
		stmt.setLong(1, Long.parseLong(type.getId()));
		List<IRevision> revisions = new LinkedList<IRevision>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			revisions.add(new RevisionImpl(getSession().getTopicMapStore(), rs.getLong("id")) {
			});
		}
		return revisions;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> doReadRevisionsByTopic(ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRevisionsByTopic();
		long topicId = Long.parseLong(topic.getId());
		stmt.setLong(1, topicId);
		stmt.setString(2, topic.getId());
		stmt.setString(3, topic.getId());
		List<IRevision> revisions = new LinkedList<IRevision>();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			revisions.add(new RevisionImpl(getSession().getTopicMapStore(), rs.getLong("id")) {
			});
		}
		return revisions;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadTimestamp(IRevision revision) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadTimestamp();
		stmt.setLong(1, revision.getId());
		ResultSet rs = stmt.executeQuery();
		Calendar c = null;
		if (rs.next()) {
			Timestamp ts = rs.getTimestamp("time");
			c = new GregorianCalendar();
			c.setTimeInMillis(ts.getTime());
		}
		rs.close();
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadRevisionByTag(ITopicMap topicMap, String tag) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRevisionByTag();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setString(2, tag);
		ResultSet rs = stmt.executeQuery();
		IRevision r = null;
		if (rs.next()) {
			r = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadRevisionByTimestamp(ITopicMap topicMap, Calendar time) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadRevisionByTimestamp();
		stmt.setLong(1, Long.parseLong(topicMap.getId()));
		stmt.setTimestamp(2, new Timestamp(time.getTimeInMillis()));
		ResultSet rs = stmt.executeQuery();
		IRevision r = null;
		if (rs.next()) {
			r = new RevisionImpl(topicMap.getStore(), rs.getLong("id")) {
			};
		}
		rs.close();
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetadata(IRevision revision) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadMetadata();
		stmt.setLong(1, revision.getId());
		Map<String, String> metadata = HashUtil.getHashMap();
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			metadata.put(rs.getString("key"), rs.getString("value"));
		}
		rs.close();
		return metadata;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetadataByKey(IRevision revision, String key) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryReadMetadataByKey();
		stmt.setLong(1, revision.getId());
		stmt.setString(2, key);
		ResultSet rs = stmt.executeQuery();
		try {
			if (rs.next()) {
				return rs.getString("value");
			}
			return null;
		} finally {
			rs.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, IAssociationRole role) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryRoleDump();
		stmt.setLong(1, revision.getId());
		stmt.setLong(2, Long.parseLong(role.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, IAssociation association) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryAssociationDump();
		stmt.setLong(1, revision.getId());
		stmt.setLong(2, Long.parseLong(association.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, IVariant variant) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryVariantDump();
		stmt.setLong(1, revision.getId());
		stmt.setLong(2, Long.parseLong(variant.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, IName name) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryNameDump();
		stmt.setLong(1, revision.getId());
		stmt.setLong(2, Long.parseLong(name.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, IOccurrence occurrence) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryOccurrenceDump();
		stmt.setLong(1, revision.getId());
		stmt.setLong(2, Long.parseLong(occurrence.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public void dump(IRevision revision, ITopic topic) throws SQLException {
		PreparedStatement stmt = getQueryBuilder().getQueryTopicDump();
		stmt.setLong(1, revision.getId());
		stmt.setString(2, topic.getBestLabel());
		stmt.setLong(3, Long.parseLong(topic.getId()));
		stmt.execute();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<TopicMapStoreParameterType, Object> doReadHistory(IConstruct c, TopicMapStoreParameterType... arguments)
			throws SQLException {
		IConstruct c_ = doReadConstruct(c.getTopicMap(), Long.parseLong(c.getId()), false);
		Map<TopicMapStoreParameterType, Object> results = HashUtil.getHashMap();
		/*
		 * construct exists and was not removed
		 */
		if (c_ != null) {
			/*
			 * read data from store
			 */
			for (TopicMapStoreParameterType type : arguments) {
				results.put(type, getSession().getTopicMapStore().doRead(c_, type));
			}
		}
		/*
		 * removed construct -> load data from history
		 */
		else {
			PreparedStatement stmt = getQueryBuilder().getQueryReadHistory();
			stmt.setLong(1, Long.parseLong(c.getId()));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				for (TopicMapStoreParameterType type : arguments) {
					switch (type) {
						case ITEM_IDENTIFIER: {
							Collection<ILocator> set = HashUtil.getHashSet();
							Array a = rs.getArray("itemidentifiers");
							for (String ref : (String[]) a.getArray()) {
								set.add(new LocatorImpl(ref));
							}
							results.put(type, set);
						}
							break;
						case SUBJECT_IDENTIFIER: {
							Collection<ILocator> set = HashUtil.getHashSet();
							Array a = rs.getArray("subjectidentifiers");
							for (String ref : (String[]) a.getArray()) {
								set.add(new LocatorImpl(ref));
							}
							results.put(type, set);
						}
							break;
						case SUBJECT_LOCATOR: {
							Collection<ILocator> set = HashUtil.getHashSet();
							Array a = rs.getArray("subjectlocators");
							for (String ref : (String[]) a.getArray()) {
								set.add(new LocatorImpl(ref));
							}
							results.put(type, set);
						}
							break;
						case NAME: {
							Collection<IName> set = HashUtil.getHashSet();
							Array a = rs.getArray("names");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyName(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newName(new JdbcIdentity(id), (ITopic) c)));
							}
							results.put(type, set);
						}
							break;
						case OCCURRENCE: {
							Collection<IOccurrence> set = HashUtil.getHashSet();
							Array a = rs.getArray("occurrences");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyOccurrence(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newOccurrence(new JdbcIdentity(id), (ITopic) c)));
							}
							results.put(type, set);
						}
							break;
						case VARIANT: {
							Collection<IVariant> set = HashUtil.getHashSet();
							Array a = rs.getArray("variants");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyVariant(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newVariant(new JdbcIdentity(id), (IName) c)));
							}
							results.put(type, set);
						}
							break;
						case ASSOCIATION: {
							Collection<IAssociation> set = HashUtil.getHashSet();
							Array a = rs.getArray("associations");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyAssociation(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newAssociation(new JdbcIdentity(id), c.getTopicMap())));
							}
							results.put(type, set);
						}
							break;
						case TYPE: {
							Collection<ITopic> set = HashUtil.getHashSet();
							Array a = rs.getArray("types");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyTopic(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newTopic(new JdbcIdentity(id), c.getTopicMap())));
							}
							results.put(type, set);
						}
							break;
						case SUPERTYPE: {
							Collection<ITopic> set = HashUtil.getHashSet();
							Array a = rs.getArray("supertypes");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyTopic(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newTopic(new JdbcIdentity(id), c.getTopicMap())));
							}
							results.put(type, set);
						}
							break;
						case ROLE: {
							Collection<IAssociationRole> set = HashUtil.getHashSet();
							Array a = rs.getArray("roles");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyAssociationRole(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory()
										.newAssociationRole(new JdbcIdentity(id), (IAssociation) c)));
							}
							results.put(type, set);
						}
							break;
						case PLAYER: {
							results.put(type,
									new JdbcReadOnlyTopic(getSession().getConnectionProvider(), getSession().getTopicMapStore().getConstructFactory()
											.newTopic(new JdbcIdentity(rs.getLong("id_player")), c.getTopicMap())));
						}
							break;
						case REIFICATION: {
							results.put(type, rs.getLong("id_reification"));
						}
							break;
						case VALUE: {
							results.put(type, rs.getString("value"));
						}
							break;
						case DATATYPE: {
							results.put(type, new LocatorImpl(rs.getString("datatype")));
						}
							break;
						case SCOPE: {
							Collection<ITopic> set = HashUtil.getHashSet();
							Array a = rs.getArray("themes");
							for (Long id : (Long[]) a.getArray()) {
								set.add(new JdbcReadOnlyTopic(getSession().getConnectionProvider(), getSession().getTopicMapStore()
										.getConstructFactory().newTopic(new JdbcIdentity(id), c.getTopicMap())));
							}
							results.put(type, new ScopeImpl(Long.toString(rs.getLong("id_scope")), set));
						}
							break;
						case BEST_LABEL: {
							results.put(type, rs.getString("bestlabel"));
						}
							break;
						case BEST_IDENTIFIER: {
							results.put(type, rs.getString("bestIdentifier"));
						}
							break;
					}
				}
			}
			rs.close();
			/*
			 * check if reification should be cleared
			 */
			if (results.containsKey(TopicMapStoreParameterType.REIFICATION)
					&& results.get(TopicMapStoreParameterType.REIFICATION) != null) {
				Long id = (Long) results.get(TopicMapStoreParameterType.REIFICATION);
				/*
				 * a topic is calling -> reification value represents the reified construct
				 */
				if (c instanceof ITopic) {
					c_ = doReadConstruct(c.getTopicMap(), id, false);
					if (c_ != null) {
						results.put(TopicMapStoreParameterType.REIFICATION, asReadOnlyConstruct(c_));
					} else {
						results.put(TopicMapStoreParameterType.REIFICATION, readHistoryConstruct(id));
					}
				}
				/*
				 * calling object is not a topic -> reification value represents the reifier topic
				 */
				else {
					results.put(TopicMapStoreParameterType.REIFICATION, new JdbcReadOnlyTopic(getSession().getConnectionProvider(), getSession()
							.getTopicMapStore().getConstructFactory().newTopic(new JdbcIdentity(id), c.getTopicMap())));
				}
			}
		}
		return results;
	}

	// /**
	// * Method read the construct with the given id from history
	// *
	// * @param id
	// * the construct id
	// * @return the read only construct
	// */
	// private final IConstruct readHistoryConstruct(final long id) throws SQLException {
	// PreparedStatement stmt = getQueryBuilder().getQueryReadHistory();
	// stmt.setLong(1, Long.parseLong(id));
	// ResultSet rs = stmt.executeQuery();
	// long parentId = -1;
	// String type = null;
	// if (rs.next()) {
	// parentId = rs.getInt("id_parent");
	// type = rs.getString("type");
	// }
	// rs.close();
	// if (type != null) {
	// if (type.equalsIgnoreCase("n")) {
	// return new JdbcReadOnlyName(this, getSession()
	// .getTopicMapStore()
	// .getConstructFactory()
	// .newName(
	// new JdbcIdentity(id),
	// getSession().getTopicMapStore().getConstructFactory()
	// .newTopic(new JdbcIdentity(Long.toString(parentId)), getSession().getTopicMapStore().getTopicMap())));
	// } else if (type.equalsIgnoreCase("o")) {
	// return new JdbcReadOnlyOccurrence(this, getSession()
	// .getTopicMapStore()
	// .getConstructFactory()
	// .newOccurrence(
	// new JdbcIdentity(id),
	// getSession().getTopicMapStore().getConstructFactory()
	// .newTopic(new JdbcIdentity(Long.toString(parentId)), getSession().getTopicMapStore().getTopicMap())));
	// } else if (type.equalsIgnoreCase("t")) {
	// return new JdbcReadOnlyTopic(this, getSession().getTopicMapStore().getConstructFactory()
	// .newTopic(new JdbcIdentity(id), getSession().getTopicMapStore().getTopicMap()));
	// } else if (type.equalsIgnoreCase("a")) {
	// return new JdbcReadOnlyAssociation(this, getSession().getTopicMapStore().getConstructFactory()
	// .newAssociation(new JdbcIdentity(id), getSession().getTopicMapStore().getTopicMap()));
	// } else if (type.equalsIgnoreCase("r")) {
	// return new JdbcReadOnlyAssociationRole(this, getSession()
	// .getTopicMapStore()
	// .getConstructFactory()
	// .newAssociationRole(
	// new JdbcIdentity(id),
	// new JdbcReadOnlyAssociation(this, getSession().getTopicMapStore().getConstructFactory()
	// .newAssociation(new JdbcIdentity(Long.toString(parentId)),
	// getSession().getTopicMapStore().getTopicMap()))));
	// } else if (type.equalsIgnoreCase("v")) {
	// IName parent = (IName) doReadConstruct(getSession().getTopicMapStore().getTopicMap(),
	// Long.toString(parentId), false);
	// if (parent == null) {
	// parent = (IName) readHistoryConstruct(Long.toString(parentId));
	// } else {
	// parent = (IName) asReadOnlyConstruct(parent);
	// }
	// return new JdbcReadOnlyVariant(this,
	// getSession().getTopicMapStore().getConstructFactory().newVariant(new JdbcIdentity(id), parent));
	// }
	// }
	// return null;
	// }

	/**
	 * Method converts the given construct to a read-only construct.
	 * 
	 * @param c
	 *            the construct
	 * @return the read-only construct
	 */
	private final IConstruct asReadOnlyConstruct(IConstruct c) {
		if (c instanceof ITopic) {
			return new JdbcReadOnlyTopic(getSession().getConnectionProvider(), (ITopic) c);
		} else if (c instanceof IName) {
			return new JdbcReadOnlyName(getSession().getConnectionProvider(), (IName) c);
		} else if (c instanceof IOccurrence) {
			return new JdbcReadOnlyOccurrence(getSession().getConnectionProvider(), (IOccurrence) c);
		} else if (c instanceof IVariant) {
			return new JdbcReadOnlyVariant(getSession().getConnectionProvider(), (IVariant) c);
		} else if (c instanceof IAssociation) {
			return new JdbcReadOnlyAssociation(getSession().getConnectionProvider(), (IAssociation) c);
		} else if (c instanceof IAssociationRole) {
			return new JdbcReadOnlyAssociationRole(getSession().getConnectionProvider(), (IAssociationRole) c);
		}
		/*
		 * construct is the topic map itself
		 */
		return c;
	}
}
