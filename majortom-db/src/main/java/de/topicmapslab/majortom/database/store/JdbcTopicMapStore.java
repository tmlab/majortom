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
package de.topicmapslab.majortom.database.store;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.database.jdbc.core.ConnectionProviderFactory;
import de.topicmapslab.majortom.database.jdbc.index.JdbcIdentityIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcLiteralIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcRevisionIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcScopedIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcSupertypeSubtypeIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.database.jdbc.index.JdbcTypeInstanceIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedConstructIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedIdentityIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedLiteralIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedScopeIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.database.jdbc.index.paged.JdbcPagedTypeInstanceIndex;
import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.database.transaction.InMemoryTransaction;
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
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.ConcurrentThreadsException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedIdentityIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedLiteralIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedScopedIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.store.MergeUtils;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * MaJorToM database topic map store
 * 
 * @author Sven Krosse
 * 
 */
public class JdbcTopicMapStore extends TopicMapStoreImpl {

	/**
	 * the connection provider
	 */
	private IConnectionProvider provider;
	/**
	 * the topic map identity
	 */
	private JdbcIdentity identity;
	/**
	 * the base locator of the topic map
	 */
	private ILocator baseLocator;

	// Index Instances
	private ITypeInstanceIndex typeInstanceIndex;
	private ITransitiveTypeInstanceIndex transitiveTypeInstanceIndex;
	private ISupertypeSubtypeIndex supertSubtypeIndex;
	private IScopedIndex scopedIndex;
	private ILiteralIndex literalIndex;
	private IIdentityIndex identityIndex;
	private IRevisionIndex revisionIndex;

	// Paged Indexes
	private IPagedTypeInstanceIndex pagedTypeInstanceIndex;
	private IPagedIdentityIndex pagedIdentityIndex;
	private IPagedConstructIndex pagedConstructIndex;
	private IPagedScopedIndex pagedScopedIndex;
	private IPagedSupertypeSubtypeIndex pagedSupertypeSubtypeIndex;
	private IPagedTransitiveTypeInstanceIndex pagedTransitiveTypeInstanceIndex;
	private IPagedLiteralIndex pagedLiteralIndex;

	/**
	 * constructor
	 */
	public JdbcTopicMapStore() {
		// VOID
	}

	/**
	 * @param topicMapSystem
	 */
	public JdbcTopicMapStore(ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type)
			throws TopicMapStoreException {
		try {
			IAssociation a = provider.getProcessor().doCreateAssociation(
					topicMap, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
					null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
					null);
			return a;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type,
			Collection<ITopic> themes) throws TopicMapStoreException {
		try {
			IAssociation a = provider.getProcessor().doCreateAssociation(
					topicMap, type, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
					null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
					null);
			return a;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateItemIdentifier(ITopicMap topicMap) {
		return doCreateLocator(topicMap, baseLocator.getReference()
				+ (baseLocator.getReference().endsWith("/")
						|| baseLocator.getReference().endsWith("#") ? "" : "/")
				+ UUID.randomUUID());
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doCreateLocator(topicMap, reference);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value)
			throws TopicMapStoreException {
		try {
			IName n = provider.getProcessor().doCreateName(topic, value);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		try {
			IName n = provider.getProcessor()
					.doCreateName(topic, value, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value)
			throws TopicMapStoreException {
		try {
			IName n = provider.getProcessor().doCreateName(topic, type, value);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		try {
			IName n = provider.getProcessor().doCreateName(topic, type, value,
					themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value) throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value) throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype) throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value, datatype);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		try {
			IOccurrence o = provider.getProcessor().doCreateOccurrence(topic,
					type, value, datatype, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association,
			ITopic type, ITopic player) throws TopicMapStoreException {
		try {
			IAssociationRole r = provider.getProcessor().doCreateRole(
					association, type, player);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, r, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.ROLE_ADDED, association, r, null);
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doCreateScope(topicMap, themes);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap)
			throws TopicMapStoreException {
		try {
			ITopic t = provider.getProcessor().doCreateTopicWithoutIdentifier(
					topicMap);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap,
			ILocator itemIdentifier) throws TopicMapStoreException {
		try {
			ITopic t = provider.getProcessor().doCreateTopicByItemIdentifier(
					topicMap, itemIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		try {
			ITopic t = provider.getProcessor()
					.doCreateTopicBySubjectIdentifier(topicMap,
							subjectIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap,
			ILocator subjectLocator) throws TopicMapStoreException {
		try {
			ITopic t = provider.getProcessor().doCreateTopicBySubjectLocator(
					topicMap, subjectLocator);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		try {
			IVariant v = provider.getProcessor().doCreateVariant(name, value,
					themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VARIANT_ADDED, name, v, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.VARIANT_ADDED, name, v, null);
			return v;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, ILocator value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		try {
			IVariant v = provider.getProcessor().doCreateVariant(name, value,
					themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VARIANT_ADDED, name, v, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.VARIANT_ADDED, name, v, null);
			return v;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		try {
			IVariant v = provider.getProcessor().doCreateVariant(name, value,
					datatype, themes);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VARIANT_ADDED, name, v, null);
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.VARIANT_ADDED, name, v, null);
			return v;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO revision and event?
	protected void doMergeTopicMaps(TopicMap context, TopicMap other)
			throws TopicMapStoreException {
		TypeInstanceIndex index = other.getIndex(TypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		MergeUtils.doMergeTopicMaps(this, (ITopicMap) context, other);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other)
			throws TopicMapStoreException {
		try {
			ITopic newTopic = provider.getProcessor()
					.doCreateTopicWithoutIdentifier(getTopicMap());
			provider.getProcessor().doMergeTopics(newTopic, context);
			provider.getProcessor().doMergeTopics(newTopic, other);
			// ((TopicImpl) other).getIdentity().setId(context.getId());
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, getTopicMap(),
					newTopic, null);
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					context);
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					other);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, getTopicMap(),
					newTopic, null);
			storeRevision(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					context);
			storeRevision(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					other);
			/*
			 * change id
			 */
			((TopicImpl) context).getIdentity().setId(newTopic.getId());
			((TopicImpl) other).getIdentity().setId(newTopic.getId());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doModifyItemIdentifier(c, itemIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c,
					itemIdentifier, null);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c,
					itemIdentifier, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player)
			throws TopicMapStoreException {
		try {
			ITopic oldPlayer = provider.getProcessor().doReadPlayer(role);
			provider.getProcessor().doModifyPlayer(role, player);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.PLAYER_MODIFIED, role, player,
					oldPlayer);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.PLAYER_MODIFIED, role, player,
					oldPlayer);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier)
			throws TopicMapStoreException {
		try {
			ITopic oldReifier = provider.getProcessor().doReadReification(r);
			provider.getProcessor().doModifyReifier(r, reifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.REIFIER_SET, r, reifier,
					oldReifier);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.REIFIER_SET, r, reifier, oldReifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		try {
			IScope oldScope = provider.getProcessor().doReadScope(s);
			provider.getProcessor().doModifyScope(s, theme);
			IScope scope = provider.getProcessor().doReadScope(s);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, scope,
					oldScope);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		try {
			provider.getProcessor().doModifySubjectIdentifier(t,
					subjectIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t,
					subjectIdentifier, null);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t,
					subjectIdentifier, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doModifySubjectLocator(t, subjectLocator);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t,
					subjectLocator, null);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t,
					subjectLocator, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doModifySupertype(t, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
			/*
			 * create type-hierarchy as association if necessary
			 */
			if (recognizingSupertypeSubtypeAssociation()) {
				createSupertypeSubtypeAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doCreateTag(tag, new GregorianCalendar());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doCreateTag(tag, timestamp);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type)
			throws TopicMapStoreException {
		try {
			ITopic oldType = provider.getProcessor().doReadType(t);
			provider.getProcessor().doModifyType(t, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_SET, t, type, oldType);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.TYPE_SET, t, type, oldType);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doModifyType(t, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.TYPE_ADDED, t, type, null);
			/*
			 * create association if necessary
			 */
			if (recognizingTypeInstanceAssociation()) {
				createTypeInstanceAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value)
			throws TopicMapStoreException {
		try {
			String oldValue = provider.getProcessor().doReadValue(n).toString();
			provider.getProcessor().doModifyValue(n, value);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, n, value,
					oldValue);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value)
			throws TopicMapStoreException {
		try {
			Object oldValue = provider.getProcessor().doReadValue(t);
			ILocator oldDatatype = provider.getProcessor().doReadDataType(t);
			ILocator datatype = doCreateLocator(getTopicMap(),
					XmlSchemeDatatypes.XSD_STRING);
			provider.getProcessor().doModifyValue(t, value, datatype);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, t, datatype,
					oldDatatype);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, t, datatype,
					oldDatatype);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value,
			ILocator datatype) throws TopicMapStoreException {
		try {
			Object oldValue = provider.getProcessor().doReadValue(t);
			ILocator oldDatatype = provider.getProcessor().doReadDataType(t);
			provider.getProcessor().doModifyValue(t, value, datatype);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, t, datatype,
					oldDatatype);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, t, datatype,
					oldDatatype);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, Object value)
			throws TopicMapStoreException {
		try {
			Object oldValue = provider.getProcessor().doReadValue(t);
			ILocator oldDatatype = provider.getProcessor().doReadDataType(t);
			final ILocator loc = doCreateLocator(t.getTopicMap(),
					XmlSchemeDatatypes.javaToXsd(value.getClass()));
			provider.getProcessor().doModifyValue(t,
					DatatypeAwareUtils.toString(value, loc), loc);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, t, loc, oldDatatype);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, t, value,
					oldValue);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, t, loc,
					oldDatatype);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(t, -1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyMetaData(IRevision revision, String key, String value)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doCreateMetadata(revision, key, value);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(t, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(t, type, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(t, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(tm));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(tm, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type,
			IScope scope) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(tm, type, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadAssociation(tm, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Changeset doReadChangeSet(IRevision r)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadChangeset(getTopicMap(), r);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadCharacteristics(t));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadCharacteristics(t, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t,
			ITopic type, IScope scope) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadCharacteristics(t, type, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadCharacteristics(t, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IConstruct doReadConstruct(ITopicMap t, String id)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadConstruct(t, id, false);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadConstruct(t, itemIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doReadDataType(IDatatypeAware d)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadDataType(d);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IRevision doReadFutureRevision(IRevision r)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadFutureRevision(getTopicMap(),
					r);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected String doReadId(IConstruct c) throws TopicMapStoreException {
		if (c instanceof ITopicMap) {
			return this.identity.getId();
		}
		return ((ConstructImpl) c).getIdentity().getId();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadItemIdentifiers(IConstruct c)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadItemIdentifiers(c));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException {
		return baseLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadNames(t,
					-1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadNames(t,
					type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, ITopic type, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadNames(t,
					type, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadNames(t,
					scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadOccurrences(t, -1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadOccurrences(t, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadOccurrences(t, type, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadOccurrences(t, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadPlayer(IAssociationRole role)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadPlayer(role);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IRevision doReadPastRevision(IRevision r)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadPastRevision(getTopicMap(), r);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IReifiable doReadReification(ITopic t)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadReification(t);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadReification(IReifiable r)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadReification(r);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Calendar doReadRevisionTimestamp(IRevision r)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadTimestamp(r);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadRoleTypes(IAssociation association)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoleTypes(
					association));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(IAssociation association)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoles(
					association, -1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(IAssociation association,
			ITopic type) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoles(
					association, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoles(
					player, -1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoles(
					player, type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player, ITopic type,
			ITopic assocType) throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadRoles(
					player, type, assocType));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doReadScope(IScopable s) throws TopicMapStoreException {
		try {
			IScope scope = provider.getProcessor().doReadScope(s);
			/*
			 * add scope of name if construct is a variant
			 */
			if (s instanceof IVariant) {
				IScope parent = provider.getProcessor().doReadScope(
						(IScopable) s.getParent());
				Collection<ITopic> themes = HashUtil.getHashSet(scope
						.getThemes());
				themes.addAll(parent.getThemes());
				scope = doCreateScope(getTopicMap(), themes);
			}
			return scope;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadSubjectIdentifiers(ITopic t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadSubjectIdentifiers(t));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadSubjectLocators(ITopic t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor()
					.doReadSubjectLocators(t));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadSuptertypes(ITopic t)
			throws TopicMapStoreException {
		return getSuptertypes(t, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getSuptertypes(ITopic t, int offset, int limit)
			throws TopicMapStoreException {
		try {
			Set<ITopic> supertypes = HashUtil.getHashSet(provider
					.getProcessor().doReadSuptertypes(t, offset, limit));
			if (existsTmdmSupertypeSubtypeAssociationType()) {
				for (IAssociation association : provider.getProcessor()
						.doReadAssociation(t,
								getTmdmSupertypeSubtypeAssociationType())) {
					Set<Role> rSubtypes = association
							.getRoles(getTmdmSubtypeRoleType());
					Set<Role> rSupertypes = association
							.getRoles(getTmdmSupertypeRoleType());
					if (rSubtypes.size() == 1 && rSupertypes.size() == 1) {
						if (rSubtypes.contains(t)) {
							supertypes.add((ITopic) rSupertypes.iterator()
									.next().getPlayer());
						}
					} else {
						throw new TopicMapStoreException(
								"Invalid TMDM supertype-subtype association.");
					}
				}
			}
			return supertypes;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadTopicBySubjectIdentifier(ITopicMap t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadTopicBySubjectIdentifier(t,
					subjectIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadTopicBySubjectLocator(ITopicMap t,
			ILocator subjectLocator) throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadTopicBySubjectLocator(t,
					subjectLocator);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTopics(ITopicMap t)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadTopics(t));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTopics(ITopicMap t, ITopic type)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadTopics(t,
					type));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadType(typed);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		return getTypes(t, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getTypes(ITopic t, int offset, int limit)
			throws TopicMapStoreException {
		try {
			Set<ITopic> types = HashUtil.getHashSet(provider.getProcessor()
					.doReadTypes(t, offset, limit));
			if (existsTmdmTypeInstanceAssociationType()) {
				for (IAssociation association : provider.getProcessor()
						.doReadAssociation(t,
								getTmdmTypeInstanceAssociationType())) {
					Set<Role> rInstances = association
							.getRoles(getTmdmInstanceRoleType());
					Set<Role> rTypes = association
							.getRoles(getTmdmTypeRoleType());
					if (rInstances.size() == 1 && rTypes.size() == 1) {
						if (rInstances.contains(t)) {
							types.add((ITopic) rTypes.iterator().next()
									.getPlayer());
						}
					} else {
						throw new TopicMapStoreException(
								"Invalid TMDM type-instance association.");
					}
				}
			}
			return types;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object doReadValue(IName n) throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadValue(n);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object doReadValue(IDatatypeAware t)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadValue(t);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doReadValue(IDatatypeAware t, Class<T> type)
			throws TopicMapStoreException {
		try {
			return (T) DatatypeAwareUtils.toValue(provider.getProcessor()
					.doReadValue(t), type);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} catch (Exception e) {
			throw new TopicMapStoreException("Cannot convert to given type", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IVariant> doReadVariants(IName n)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadVariants(
					n, -1, -1));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IVariant> doReadVariants(IName n, IScope scope)
			throws TopicMapStoreException {
		try {
			return HashUtil.getHashSet(provider.getProcessor().doReadVariants(
					n, scope));
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Map<String, String> doReadMetaData(IRevision revision)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadMetadata(revision);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected String doReadMetaData(IRevision revision, String key)
			throws TopicMapStoreException {
		try {
			return provider.getProcessor().doReadMetadataByKey(revision, key);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade)
			throws TopicMapStoreException {
		try {
			Set<IAssociationRole> roles = HashUtil.getHashSet(provider
					.getProcessor().doReadRoles(association, -1, -1));
			/*
			 * remove association
			 */
			if (!provider.getProcessor().doRemoveAssociation(association,
					cascade)) {
				IRevision revision = createRevision();
				for (IAssociationRole role : roles) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.ROLE_REMOVED,
							association, null, role);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.ROLE_REMOVED,
							association, null, role);
				}
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED,
						getTopicMap(), null, association);
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.ASSOCIATION_REMOVED,
						getTopicMap(), null, association);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveItemIdentifier(c, itemIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null,
					itemIdentifier);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null,
					itemIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade)
			throws TopicMapStoreException {
		try {
			ITopic parent = name.getParent();
			ITopic reifier = (ITopic) name.getReifier();
			Set<IVariant> variants = HashUtil.getHashSet(provider
					.getProcessor().doReadVariants(name, -1, -1));
			/*
			 * remove name and variants
			 */
			if (!provider.getProcessor().doRemoveName(name, cascade)) {
				IRevision revision = createRevision();
				/*
				 * notify listener
				 */
				for (IVariant variant : variants) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.VARIANT_REMOVED, name,
							null, variant);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.VARIANT_REMOVED,
							name, null, variant);
				}
				if (reifier != null) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
				}
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.NAME_REMOVED, parent, null,
						name);
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.NAME_REMOVED, parent,
						null, name);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade)
			throws TopicMapStoreException {
		try {
			ITopic parent = occurrence.getParent();
			ITopic reifier = (ITopic) occurrence.getReifier();
			/*
			 * remove occurrence
			 */
			if (!provider.getProcessor()
					.doRemoveOccurrence(occurrence, cascade)) {
				IRevision revision = createRevision();
				if (reifier != null) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.OCCURRENCE_REMOVED,
						parent, null, occurrence);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, parent,
						null, occurrence);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade)
			throws TopicMapStoreException {
		try {
			IAssociation parent = role.getParent();
			ITopic reifier = (ITopic) role.getReifier();
			/*
			 * remove role
			 */
			if (!provider.getProcessor().doRemoveRole(role, cascade)) {
				IRevision revision = createRevision();
				if (reifier != null) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.ROLE_REMOVED, parent,
						null, role);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.ROLE_REMOVED, parent, null,
						role);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		try {
			IScope oldScope = provider.getProcessor().doReadScope(s);
			provider.getProcessor().doRemoveScope(s, theme);
			IScope scope = provider.getProcessor().doReadScope(s);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, scope,
					oldScope);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveSubjectIdentifier(t,
					subjectIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t,
					null, subjectIdentifier);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t,
					null, subjectIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveSubjectLocator(t, subjectLocator);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null,
					subjectLocator);
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null,
					subjectLocator);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveSupertype(t, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
			/*
			 * remove supertype-association if necessary
			 */
			if (recognizingSupertypeSubtypeAssociation()
					&& existsTmdmSupertypeSubtypeAssociationType()) {
				removeSupertypeSubtypeAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveTopic(topic, cascade);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveTopicMap(topicMap, cascade);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		try {
			provider.getProcessor().doRemoveType(t, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_REMOVED, t, null, type);
			/*
			 * store history
			 */
			IRevision r = createRevision();
			storeRevision(r, TopicMapEventType.TYPE_REMOVED, t, null, type);
			/*
			 * remove type-association if necessary
			 */
			if (existsTmdmTypeInstanceAssociationType()) {
				removeTypeInstanceAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade)
			throws TopicMapStoreException {
		try {
			IName parent = variant.getParent();
			ITopic reifier = (ITopic) variant.getReifier();
			/*
			 * remove variant
			 */
			if (!provider.getProcessor().doRemoveVariant(variant, cascade)) {
				IRevision revision = createRevision();
				if (reifier != null) {
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED,
							getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.VARIANT_REMOVED,
						parent, null, variant);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.VARIANT_REMOVED, parent,
						null, variant);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws ConcurrentThreadsException {
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction createTransaction() {
		return new InMemoryTransaction(getTopicMap());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <I extends Index> I getIndex(Class<I> clazz) {
		if (IPagedTransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTransitiveTypeInstanceIndex == null) {
				this.pagedTransitiveTypeInstanceIndex = new JdbcPagedTransitiveTypeInstanceIndex(
						this, getIndex(ITransitiveTypeInstanceIndex.class));
			}
			return (I) pagedTransitiveTypeInstanceIndex;
		} else if (IPagedTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTypeInstanceIndex == null) {
				this.pagedTypeInstanceIndex = new JdbcPagedTypeInstanceIndex(
						this, getIndex(ITypeInstanceIndex.class));
			}
			return (I) pagedTypeInstanceIndex;
		} else if (IPagedIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedIdentityIndex == null) {
				this.pagedIdentityIndex = new JdbcPagedIdentityIndex(this,
						getIndex(IIdentityIndex.class));
			}
			return (I) pagedIdentityIndex;
		} else if (IPagedLiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedLiteralIndex == null) {
				this.pagedLiteralIndex = new JdbcPagedLiteralIndex(this,
						getIndex(ILiteralIndex.class));
			}
			return (I) pagedLiteralIndex;
		} else if (IPagedSupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedSupertypeSubtypeIndex == null) {
				this.pagedSupertypeSubtypeIndex = new JdbcPagedSupertypeSubtypeIndex(
						this, getIndex(ISupertypeSubtypeIndex.class));
			}
			return (I) pagedSupertypeSubtypeIndex;
		} else if (IPagedConstructIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedConstructIndex == null) {
				this.pagedConstructIndex = new JdbcPagedConstructIndex(this);
			}
			return (I) pagedConstructIndex;
		} else if (IPagedScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedScopedIndex == null) {
				this.pagedScopedIndex = new JdbcPagedScopeIndex(this,
						getIndex(IScopedIndex.class));
			}
			return (I) pagedScopedIndex;
		} else if (ITransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.transitiveTypeInstanceIndex == null) {
				transitiveTypeInstanceIndex = new JdbcTransitiveTypeInstanceIndex(
						this);
			}
			return (I) transitiveTypeInstanceIndex;
		} else if (TypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.typeInstanceIndex == null) {
				this.typeInstanceIndex = new JdbcTypeInstanceIndex(this);
			}
			return (I) this.typeInstanceIndex;
		} else if (ScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.scopedIndex == null) {
				this.scopedIndex = new JdbcScopedIndex(this);
			}
			return (I) this.scopedIndex;
		} else if (LiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.literalIndex == null) {
				this.literalIndex = new JdbcLiteralIndex(this);
			}
			return (I) this.literalIndex;
		} else if (IIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.identityIndex == null) {
				this.identityIndex = new JdbcIdentityIndex(this);
			}
			return (I) this.identityIndex;
		} else if (ISupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.supertSubtypeIndex == null) {
				this.supertSubtypeIndex = new JdbcSupertypeSubtypeIndex(this);
			}
			return (I) this.supertSubtypeIndex;
		} else if (IRevisionIndex.class.isAssignableFrom(clazz)) {
			if (this.revisionIndex == null) {
				this.revisionIndex = new JdbcRevisionIndex(this);
			}
			return (I) this.revisionIndex;
		}
		throw new UnsupportedOperationException("The index class '"
				+ (clazz == null ? "null" : clazz.getCanonicalName())
				+ "' is not supported by the current engine.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator)
			throws TopicMapStoreException {
		Object host = getTopicMapSystem().getProperty(
				JdbcTopicMapStoreProperty.DATABASE_HOST);
		Object database = getTopicMapSystem().getProperty(
				JdbcTopicMapStoreProperty.DATABASE_NAME);
		Object user = getTopicMapSystem().getProperty(
				JdbcTopicMapStoreProperty.DATABASE_USER);
		Object password = getTopicMapSystem().getProperty(
				JdbcTopicMapStoreProperty.DATABASE_PASSWORD);
		Object dialect = getTopicMapSystem().getProperty(
				JdbcTopicMapStoreProperty.SQL_DIALECT);

		if (database == null || host == null || user == null || dialect == null) {
			throw new TopicMapStoreException("Missing connection properties!");
		}
		provider = ConnectionProviderFactory.getFactory()
				.newConnectionProvider(dialect.toString());
		provider.setTopicMapStore(this);
		try {
			provider.openConnection(host.toString(), database.toString(),
					user.toString(),
					password == null ? "" : password.toString());
			this.identity = new JdbcIdentity(provider.getProcessor()
					.doCreateTopicMap((ILocator) topicMapBaseLocator));
		} catch (SQLException e) {
			throw new TopicMapStoreException(
					"Cannot open connection to database!", e);
		}
		this.baseLocator = (ILocator) topicMapBaseLocator;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		try {
			provider.closeConnection();
		} catch (SQLException e) {
			throw new TopicMapStoreException(
					"Cannot close connection to database!", e);
		}
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransactable() {
		return true;
	}

	/**
	 * Returns the internal sql processor
	 * 
	 * @return the provider
	 */
	public IQueryProcessor getProcessor() {
		if (provider == null) {
			throw new TopicMapStoreException("Connection not established!");
		}
		return provider.getProcessor();
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision createRevision() {
		if (isRevisionManagementEnabled()) {
			try {
				return provider.getProcessor().doCreateRevision(getTopicMap());
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void storeRevision(IRevision revision, TopicMapEventType type,
			IConstruct context, Object newValue, Object oldValue) {
		if (isRevisionManagementEnabled()) {
			try {
				provider.getProcessor().doCreateChangeSet(revision, type,
						context, newValue, oldValue);
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			}
		}
	}

	// /***********
	// * UTILITY *
	// ***********/

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void createTypeInstanceAssociation(ITopic instance, ITopic type,
			IRevision revision) {
		try {
			/*
			 * create association
			 */
			IAssociation association = getProcessor().doCreateAssociation(
					getTopicMap(), getTmdmTypeInstanceAssociationType());
			/*
			 * create roles
			 */
			IAssociationRole roleInstance = getProcessor().doCreateRole(
					association, getTmdmInstanceRoleType(), instance);
			IAssociationRole roleType = getProcessor().doCreateRole(
					association, getTmdmTypeRoleType(), type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(),
					association, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association,
					roleInstance, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association,
					roleType, null);
			/*
			 * store history
			 */
			storeRevision(revision, TopicMapEventType.ASSOCIATION_ADDED,
					getTopicMap(), association, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association,
					roleInstance, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association,
					roleType, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void createSupertypeSubtypeAssociation(ITopic type,
			ITopic supertype, IRevision revision) {
		try {
			/*
			 * create association
			 */
			IAssociation association = getProcessor().doCreateAssociation(
					getTopicMap(), getTmdmSupertypeSubtypeAssociationType());
			/*
			 * create roles
			 */
			IAssociationRole roleSubtype = getProcessor().doCreateRole(
					association, getTmdmSubtypeRoleType(), type);
			IAssociationRole roleSupertype = getProcessor().doCreateRole(
					association, getTmdmSupertypeRoleType(), supertype);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(),
					association, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association,
					roleSubtype, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association,
					roleSupertype, null);
			/*
			 * store history
			 */
			storeRevision(revision, TopicMapEventType.ASSOCIATION_ADDED,
					getTopicMap(), association, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association,
					roleSubtype, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association,
					roleSupertype, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void removeSupertypeSubtypeAssociation(ITopic type,
			ITopic supertype, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type,
				getTmdmSupertypeSubtypeAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmSubtypeRoleType()).iterator()
						.next().getPlayer().equals(type)
						&& association.getRoles(getTmdmSupertypeRoleType())
								.iterator().next().getPlayer()
								.equals(supertype)) {
					getProcessor().doRemoveAssociation(association, true,
							revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException(
						"Invalid meta model! Missing supertype or subtype role!",
						e);
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void removeTypeInstanceAssociation(ITopic instance, ITopic type,
			IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type,
				getTmdmTypeInstanceAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmInstanceRoleType()).iterator()
						.next().getPlayer().equals(instance)
						&& association.getRoles(getTmdmTypeRoleType())
								.iterator().next().getPlayer().equals(type)) {
					getProcessor().doRemoveAssociation(association, true,
							revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException(
						"Invalid meta model! Missing type or instance role!", e);
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		try {
			getProcessor().doClearTopicMap(getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}
}
