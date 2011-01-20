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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.tmapi.core.Locator;
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
import de.topicmapslab.majortom.database.jdbc.model.ISession;
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
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.store.ModifableTopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * MaJorToM database topic map store
 * 
 * @author Sven Krosse
 * 
 */
public class JdbcTopicMapStore extends ModifableTopicMapStoreImpl {
	/**
	 * 
	 */
	private static final String MESSAGE_SESSION_CANNOT_BE_CLOSED = "Session cannot be closed!";
	/**
	 * the connection provider
	 */
	private IConnectionProvider provider;
	/**
	 * the topic map identity
	 */
	private JdbcIdentity identity;

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
	private String dialect;

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
	 * Returns whether the topic map is empty or not
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	protected boolean isTopicMapEmpty(ITopicMap topicMap) {

		ISession session = this.provider.openSession();

		try {

			Long num = session.getProcessor().doReadNumberOfTopics(topicMap);

			if (num == 0)
				return true;

			return false;

		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		return doCreateAssociation(topicMap, type, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IAssociation a = session.getProcessor().doCreateAssociation(topicMap, type, themes);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.ASSOCIATION_ADDED);
			storeRevision(r, TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
			storeRevision(r, TopicMapEventType.TYPE_SET, a, type, null);
			storeRevision(r, TopicMapEventType.SCOPE_MODIFIED, a, doCreateScope(getTopicMap(), themes), null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
			return a;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateItemIdentifier(ITopicMap topicMap) {
		return doCreateLocator(topicMap, getTopicMapBaseLocator().getReference()
				+ (getTopicMapBaseLocator().getReference().endsWith("/") || getTopicMapBaseLocator().getReference().endsWith("#") ? "" : "/") + UUID.randomUUID());
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ILocator locator = session.getProcessor().doCreateLocator(topicMap, reference);
			session.commit();
			return locator;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		return doCreateName(topic, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IName n = session.getProcessor().doCreateName(topic, value, themes);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.NAME_ADDED);
			storeRevision(r, TopicMapEventType.NAME_ADDED, topic, n, null);
			storeRevision(r, TopicMapEventType.TYPE_SET, n, getTmdmDefaultNameType(), null);
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, n, value, null);
			storeRevision(r, TopicMapEventType.SCOPE_MODIFIED, n, doCreateScope(getTopicMap(), themes), null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		return doCreateName(topic, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IName n = session.getProcessor().doCreateName(topic, type, value, themes);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.NAME_ADDED);
			storeRevision(r, TopicMapEventType.NAME_ADDED, topic, n, null);
			storeRevision(r, TopicMapEventType.TYPE_SET, n, type, null);
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, n, value, null);
			storeRevision(r, TopicMapEventType.SCOPE_MODIFIED, n, doCreateScope(getTopicMap(), themes), null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
			return n;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		return doCreateOccurrence(topic, type, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		return doCreateOccurrence(topic, type, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		return doCreateOccurrence(topic, type, value.toExternalForm(), datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		return doCreateOccurrence(topic, type, value.toExternalForm(), datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws TopicMapStoreException {
		Collection<ITopic> themes = Collections.emptySet();
		return doCreateOccurrence(topic, type, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IOccurrence o = session.getProcessor().doCreateOccurrence(topic, type, value, datatype, themes);
			session.commit();

			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.OCCURRENCE_ADDED);
			storeRevision(r, TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
			storeRevision(r, TopicMapEventType.TYPE_SET, o, type, null);
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, o, value, null);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, o, datatype, null);
			storeRevision(r, TopicMapEventType.SCOPE_MODIFIED, o, doCreateScope(getTopicMap(), themes), null);
			return o;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IAssociationRole r = session.getProcessor().doCreateRole(association, type, player);
			session.commit();
			/*
			 * create revision
			 */
			IRevision rev = createRevision(TopicMapEventType.ROLE_ADDED);
			storeRevision(rev, TopicMapEventType.ROLE_ADDED, association, r, null);
			storeRevision(rev, TopicMapEventType.TYPE_SET, r, type, null);
			storeRevision(rev, TopicMapEventType.PLAYER_MODIFIED, r, player, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, r, null);
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IScope scope = session.getProcessor().doCreateScope(topicMap, themes);
			session.commit();
			return scope;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doCreateTopicWithoutIdentifier(topicMap);
			session.commit();
			/*
			 * create revision
			 */
			storeRevision(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doCreateTopicByItemIdentifier(topicMap, itemIdentifier);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.TOPIC_ADDED);
			storeRevision(r, TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			storeRevision(r, TopicMapEventType.ITEM_IDENTIFIER_ADDED, t, itemIdentifier, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doCreateTopicBySubjectIdentifier(topicMap, subjectIdentifier);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.TOPIC_ADDED);
			storeRevision(r, TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			storeRevision(r, TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doCreateTopicBySubjectLocator(topicMap, subjectLocator);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.TOPIC_ADDED);
			storeRevision(r, TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			storeRevision(r, TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING);
		return doCreateVariant(name, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		ILocator datatype = doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI);
		return doCreateVariant(name, value.toExternalForm(), datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IVariant v = session.getProcessor().doCreateVariant(name, value, datatype, themes);
			session.commit();
			/*
			 * create revision
			 */
			IRevision r = createRevision(TopicMapEventType.VARIANT_ADDED);
			storeRevision(r, TopicMapEventType.VARIANT_ADDED, name, v, null);
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, v, value, null);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, v, datatype, null);
			storeRevision(r, TopicMapEventType.SCOPE_MODIFIED, v, doCreateScope(getTopicMap(), themes), null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VARIANT_ADDED, name, v, null);
			return v;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopicMaps(TopicMap context, TopicMap other) throws TopicMapStoreException {
		JdbcMergeUtils.doMergeTopicMaps(this, (ITopicMap) context, other);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic newTopic = session.getProcessor().doCreateTopicWithoutIdentifier(getTopicMap());
			/*
			 * store history and notify listeners
			 */
			IRevision r = createRevision(TopicMapEventType.TOPIC_ADDED);
			storeRevision(r, TopicMapEventType.TOPIC_ADDED, getTopicMap(), newTopic, null);
			notifyListeners(TopicMapEventType.TOPIC_ADDED, getTopicMap(), newTopic, null);
			/*
			 * merge topics
			 */
			session.getProcessor().doMergeTopics(newTopic, context);
			String oldId = context.getId();
			((TopicImpl) context).getIdentity().setId(newTopic.getId());
			/*
			 * store history and notify listeners
			 */
			storeRevision(r, TopicMapEventType.MERGE, getTopicMap(), newTopic, context);
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic, context);
			notifyListeners(TopicMapEventType.ID_MODIFIED, context, newTopic.getId(), oldId);
			/*
			 * merge topics
			 */
			session.getProcessor().doMergeTopics(newTopic, other);
			oldId = other.getId();
			((TopicImpl) other).getIdentity().setId(newTopic.getId());
			session.commit();
			/*
			 * store history and notify listeners
			 */
			storeRevision(r, TopicMapEventType.MERGE, getTopicMap(), newTopic, other);
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic, other);
			notifyListeners(TopicMapEventType.ID_MODIFIED, context, newTopic.getId(), oldId);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doModifyItemIdentifier(c, itemIdentifier);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c, itemIdentifier, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c, itemIdentifier, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic oldPlayer = session.getProcessor().doReadPlayer(role);
			session.getProcessor().doModifyPlayer(role, player);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.PLAYER_MODIFIED, role, player, oldPlayer);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.PLAYER_MODIFIED, role, player, oldPlayer);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic oldReifier = session.getProcessor().doReadReification(r);
			session.getProcessor().doModifyReifier(r, reifier);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.REIFIER_SET, r, reifier, oldReifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.REIFIER_SET, r, reifier, oldReifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IScope oldScope = session.getProcessor().doReadScope(s);
			session.getProcessor().doModifyScope(s, theme);
			IScope scope = session.getProcessor().doReadScope(s);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doModifySubjectIdentifier(t, subjectIdentifier);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doModifySubjectLocator(t, subjectLocator);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doModifySupertype(t, type);
			session.commit();
			/*
			 * store history
			 */
			IRevision r = createRevision(TopicMapEventType.SUPERTYPE_ADDED);
			storeRevision(r, TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
			/*
			 * create type-hierarchy as association if necessary
			 */
			if (recognizingSupertypeSubtypeAssociation()) {
				createSupertypeSubtypeAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doCreateTag(tag, new GregorianCalendar());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doCreateTag(tag, timestamp);
			session.commit();
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic oldType = session.getProcessor().doReadType(t);
			session.getProcessor().doModifyType(t, type);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.TYPE_SET, t, type, oldType);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_SET, t, type, oldType);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTopicType(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doModifyType(t, type);
			session.commit();
			/*
			 * store history
			 */
			IRevision r = createRevision(TopicMapEventType.TYPE_ADDED);
			storeRevision(r, TopicMapEventType.TYPE_ADDED, t, type, null);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
			/*
			 * create association if necessary
			 */
			if (recognizingTypeInstanceAssociation()) {
				createTypeInstanceAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			String oldValue = session.getProcessor().doReadValue(n).toString();
			session.getProcessor().doModifyValue(n, value);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value) throws TopicMapStoreException {
		doModifyValue(t, value, doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Object oldValue = session.getProcessor().doReadValue(t);
			ILocator oldDatatype = session.getProcessor().doReadDataType(t);
			session.getProcessor().doModifyValue(t, value, datatype);
			session.commit();
			/*
			 * store history
			 */
			IRevision r = createRevision(TopicMapEventType.VALUE_MODIFIED);
			storeRevision(r, TopicMapEventType.VALUE_MODIFIED, t, value, oldValue);
			storeRevision(r, TopicMapEventType.DATATYPE_SET, t, datatype, oldDatatype);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, t, value, oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, t, datatype, oldDatatype);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware t, Object value) throws TopicMapStoreException {
		final ILocator loc = doCreateLocator(t.getTopicMap(), XmlSchemeDatatypes.javaToXsd(value.getClass()));
		doModifyValue(t, DatatypeAwareUtils.toString(value, loc), loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		/*
		 * avoid caching of transaction constructs
		 */
		if (context != null && (context instanceof ITransaction || context.getTopicMap() instanceof ITransaction)) {
			return super.doRead(context, paramType, params);
		}
		/*
		 * check if caching is enabled
		 */
		if (isCachingEnabled()) {
			return getCache().doRead(context, paramType, params);
		}
		return super.doRead(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(t, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModifyMetaData(IRevision revision, String key, String value) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doCreateMetadata(revision, key, value);
			session.commit();
			if (isCachingEnabled()) {
				getCache().clearMetaData(revision);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(t, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(t, type, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(t, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(tm, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(tm, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(tm, type, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociation> set = HashUtil.getHashSet(session.getProcessor().doReadAssociation(tm, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Changeset set = session.getProcessor().doReadChangeset(getTopicMap(), r);
			session.commit();
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMapEventType doReadChangeSetType(IRevision r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			TopicMapEventType type = session.getProcessor().doReadChangesetType(getTopicMap(), r);
			session.commit();
			return type;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ICharacteristics> set = HashUtil.getHashSet(session.getProcessor().doReadCharacteristics(t));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ICharacteristics> set = HashUtil.getHashSet(session.getProcessor().doReadCharacteristics(t, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ICharacteristics> set = HashUtil.getHashSet(session.getProcessor().doReadCharacteristics(t, type, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ICharacteristics> set = HashUtil.getHashSet(session.getProcessor().doReadCharacteristics(t, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, String id) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IConstruct c = session.getProcessor().doReadConstruct(t, Long.parseLong(id), false);
			session.commit();
			return c;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} catch (NumberFormatException e) {
			throw new TopicMapStoreException("Given Id is invalid for the current topic map store.", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IConstruct c = session.getProcessor().doReadConstruct(t, itemIdentifier);
			session.commit();
			return c;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware d) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ILocator loc = session.getProcessor().doReadDataType(d);
			session.commit();
			return loc;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFutureRevision(IRevision r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IRevision rev = session.getProcessor().doReadFutureRevision(getTopicMap(), r);
			session.commit();
			return rev;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		if (c instanceof ITopicMap) {
			return this.identity.getId();
		}
		return ((ConstructImpl) c).getIdentity().getId();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadItemIdentifiers(IConstruct c) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ILocator> set = HashUtil.getHashSet(session.getProcessor().doReadItemIdentifiers(c));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IName> set = HashUtil.getHashSet(session.getProcessor().doReadNames(t, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IName> set = HashUtil.getHashSet(session.getProcessor().doReadNames(t, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IName> set = HashUtil.getHashSet(session.getProcessor().doReadNames(t, type, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IName> set = HashUtil.getHashSet(session.getProcessor().doReadNames(t, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IOccurrence> set = HashUtil.getHashSet(session.getProcessor().doReadOccurrences(t, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IOccurrence> set = HashUtil.getHashSet(session.getProcessor().doReadOccurrences(t, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IOccurrence> set = HashUtil.getHashSet(session.getProcessor().doReadOccurrences(t, type, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IOccurrence> set = HashUtil.getHashSet(session.getProcessor().doReadOccurrences(t, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadPlayer(IAssociationRole role) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doReadPlayer(role);
			session.commit();
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(IRevision r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IRevision rev = session.getProcessor().doReadPastRevision(getTopicMap(), r);
			session.commit();
			return rev;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IReifiable r = session.getProcessor().doReadReification(t);
			session.commit();
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic t = session.getProcessor().doReadReification(r);
			session.commit();
			return t;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadRevisionTimestamp(IRevision r) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Calendar c = session.getProcessor().doReadTimestamp(r);
			session.commit();
			return c;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadRoleTypes(IAssociation association) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ITopic> set = HashUtil.getHashSet(session.getProcessor().doReadRoleTypes(association));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> set = HashUtil.getHashSet(session.getProcessor().doReadRoles(association, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> set = HashUtil.getHashSet(session.getProcessor().doReadRoles(association, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> set = HashUtil.getHashSet(session.getProcessor().doReadRoles(player, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> set = HashUtil.getHashSet(session.getProcessor().doReadRoles(player, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> set = HashUtil.getHashSet(session.getProcessor().doReadRoles(player, type, assocType));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doReadScope(IScopable s) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IScope scope = session.getProcessor().doReadScope(s);
			/*
			 * add scope of name if construct is a variant
			 */
			if (s instanceof IVariant) {
				IScope parent = session.getProcessor().doReadScope((IScopable) s.getParent());
				Collection<ITopic> themes = HashUtil.getHashSet(scope.getThemes());
				themes.addAll(parent.getThemes());
				scope = session.getProcessor().doCreateScope(getTopicMap(), themes);
			}
			session.commit();
			return scope;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ILocator> set = HashUtil.getHashSet(session.getProcessor().doReadSubjectIdentifiers(t));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectLocators(ITopic t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ILocator> set = HashUtil.getHashSet(session.getProcessor().doReadSubjectLocators(t));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ITopic> doReadSuptertypes(ITopic t) throws TopicMapStoreException {
		return getSuptertypes(t, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ITopic> getSuptertypes(ITopic t, int offset, int limit) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			List<ITopic> supertypes = HashUtil.getList(session.getProcessor().doReadSuptertypes(t, offset, limit));
			if (existsTmdmSupertypeSubtypeAssociationType()) {
				for (IAssociation association : session.getProcessor().doReadAssociation(t, getTmdmSupertypeSubtypeAssociationType())) {
					Collection<IAssociationRole> rSubtypes = session.getProcessor().doReadRoles(association, getTmdmSubtypeRoleType());
					Collection<IAssociationRole> rSupertypes = session.getProcessor().doReadRoles(association, getTmdmSupertypeRoleType());
					if (rSubtypes.size() == 1 && rSupertypes.size() == 1) {
						IAssociationRole rSubtype = rSubtypes.iterator().next();
						if (session.getProcessor().doReadPlayer(rSubtype).equals(t)) {
							IAssociationRole rSupertype = rSupertypes.iterator().next();
							ITopic player = session.getProcessor().doReadPlayer(rSupertype);
							if (!supertypes.contains(player)) {
								supertypes.add(player);
							}
						}
					} else {
						throw new TopicMapStoreException("Invalid TMDM supertype-subtype association.");
					}
				}
			}
			session.commit();
			return supertypes;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic topic = session.getProcessor().doReadTopicBySubjectIdentifier(t, subjectIdentifier);
			session.commit();
			return topic;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic topic = session.getProcessor().doReadTopicBySubjectLocator(t, subjectLocator);
			session.commit();
			return topic;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ITopic> set = HashUtil.getHashSet(session.getProcessor().doReadTopics(t, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ITopic> set = HashUtil.getHashSet(session.getProcessor().doReadTopics(t, type));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic topic = session.getProcessor().doReadType(typed);
			session.commit();
			return topic;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		return getTypes(t, -1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> getTypes(ITopic t, int offset, int limit) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<ITopic> types = HashUtil.getHashSet(session.getProcessor().doReadTypes(t, offset, limit));
			if (existsTmdmTypeInstanceAssociationType()) {
				for (IAssociation association : session.getProcessor().doReadAssociation(t, getTmdmTypeInstanceAssociationType())) {
					Collection<IAssociationRole> rInstances = session.getProcessor().doReadRoles(association, getTmdmInstanceRoleType());
					Collection<IAssociationRole> rTypes = session.getProcessor().doReadRoles(association, getTmdmTypeRoleType());
					if (rInstances.size() == 1 && rTypes.size() == 1) {
						if (rInstances.contains(t)) {
							IAssociationRole rType = rTypes.iterator().next();
							types.add(session.getProcessor().doReadPlayer(rType));
						}
					} else {
						throw new TopicMapStoreException("Invalid TMDM type-instance association.");
					}
				}
			}
			session.commit();
			return types;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Object value = session.getProcessor().doReadValue(n);
			session.commit();
			return value;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware t) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Object value = session.getProcessor().doReadValue(t);
			session.commit();
			return value;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T doReadValue(IDatatypeAware t, Class<T> type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			T value = (T) DatatypeAwareUtils.toValue(session.getProcessor().doReadValue(t), type);
			session.commit();
			return value;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} catch (Exception e) {
			throw new TopicMapStoreException("Cannot convert to given type", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IVariant> set = HashUtil.getHashSet(session.getProcessor().doReadVariants(n, -1, -1));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IVariant> set = HashUtil.getHashSet(session.getProcessor().doReadVariants(n, scope));
			session.commit();
			if (set.isEmpty()) {
				return Collections.emptySet();
			}
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetaData(IRevision revision) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Map<String, String> map = session.getProcessor().doReadMetadata(revision);
			session.commit();
			if (map.isEmpty()) {
				return Collections.emptyMap();
			}
			return map;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetaData(IRevision revision, String key) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			String value = session.getProcessor().doReadMetadataByKey(revision, key);
			session.commit();
			return value;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			String label = session.getProcessor().doReadBestLabel(topic);
			session.commit();
			return label;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			String label = session.getProcessor().doReadBestLabel(topic, theme, strict);
			session.commit();
			return label;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestIdentifier(ITopic topic, boolean withPrefix) {
		ISession session = provider.openSession();
		try {
			String bestIdentifier = session.getProcessor().doReadBestIdentifier(topic, withPrefix);
			session.commit();
			return bestIdentifier;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			Set<IAssociationRole> roles = HashUtil.getHashSet(session.getProcessor().doReadRoles(association, -1, -1));
			/*
			 * remove association
			 */
			if (!session.getProcessor().doRemoveAssociation(association, cascade)) {
				session.commit();
				IRevision revision = createRevision(TopicMapEventType.ASSOCIATION_REMOVED);
				for (IAssociationRole role : roles) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.ROLE_REMOVED, association, null, role);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.ROLE_REMOVED, association, null, role);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.ASSOCIATION_REMOVED, getTopicMap(), null, association);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED, getTopicMap(), null, association);
			} else {
				session.commit();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveItemIdentifier(c, itemIdentifier);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null, itemIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null, itemIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic parent = name.getParent();
			ITopic reifier = (ITopic) name.getReifier();
			Set<IVariant> variants = HashUtil.getHashSet(session.getProcessor().doReadVariants(name, -1, -1));
			/*
			 * remove name and variants
			 */
			if (!session.getProcessor().doRemoveName(name, cascade)) {
				session.commit();
				IRevision revision = createRevision(TopicMapEventType.NAME_REMOVED);
				/*
				 * notify listener
				 */
				for (IVariant variant : variants) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.VARIANT_REMOVED, name, null, variant);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.VARIANT_REMOVED, name, null, variant);
				}
				if (reifier != null) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.NAME_REMOVED, parent, null, name);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.NAME_REMOVED, parent, null, name);
			} else {
				session.commit();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			ITopic parent = occurrence.getParent();
			ITopic reifier = (ITopic) occurrence.getReifier();
			/*
			 * remove occurrence
			 */
			if (!session.getProcessor().doRemoveOccurrence(occurrence, cascade)) {
				session.commit();
				IRevision revision = createRevision(TopicMapEventType.OCCURRENCE_REMOVED);
				if (reifier != null) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.OCCURRENCE_REMOVED, parent, null, occurrence);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, parent, null, occurrence);
			} else {
				session.commit();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IAssociation parent = role.getParent();
			ITopic reifier = (ITopic) role.getReifier();
			/*
			 * remove role
			 */
			if (!session.getProcessor().doRemoveRole(role, cascade)) {
				session.commit();
				IRevision revision = createRevision(TopicMapEventType.ROLE_REMOVED);
				if (reifier != null) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.ROLE_REMOVED, parent, null, role);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.ROLE_REMOVED, parent, null, role);
			} else {
				session.commit();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IScope oldScope = session.getProcessor().doReadScope(s);
			session.getProcessor().doRemoveScope(s, theme);
			IScope scope = session.getProcessor().doReadScope(s);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, scope, oldScope);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveSubjectIdentifier(t, subjectIdentifier);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null, subjectIdentifier);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null, subjectIdentifier);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveSubjectLocator(t, subjectLocator);
			session.commit();
			/*
			 * store history
			 */
			storeRevision(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null, subjectLocator);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null, subjectLocator);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveSupertype(t, type);
			session.commit();
			/*
			 * store history
			 */
			IRevision r = createRevision(TopicMapEventType.SUPERTYPE_REMOVED);
			storeRevision(r, TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
			/*
			 * remove supertype-association if necessary
			 */
			if (recognizingSupertypeSubtypeAssociation() && existsTmdmSupertypeSubtypeAssociationType()) {
				removeSupertypeSubtypeAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveTopic(topic, cascade);
			session.commit();
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveTopicMap(topicMap, cascade);
			session.commit();
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveType(ITopic t, ITopic type) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			session.getProcessor().doRemoveType(t, type);
			session.commit();
			/*
			 * store history
			 */
			IRevision r = createRevision(TopicMapEventType.TYPE_REMOVED);
			storeRevision(r, TopicMapEventType.TYPE_REMOVED, t, null, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_REMOVED, t, null, type);
			/*
			 * remove type-association if necessary
			 */
			if (existsTmdmTypeInstanceAssociationType()) {
				removeTypeInstanceAssociation(t, type, r);
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade) throws TopicMapStoreException {
		ISession session = provider.openSession();
		try {
			IName parent = variant.getParent();
			ITopic reifier = (ITopic) variant.getReifier();
			/*
			 * remove variant
			 */
			if (!session.getProcessor().doRemoveVariant(variant, cascade)) {
				session.commit();
				IRevision revision = createRevision(TopicMapEventType.VARIANT_REMOVED);
				if (reifier != null) {
					/*
					 * store history
					 */
					storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
					/*
					 * notify listener
					 */
					notifyListeners(TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, reifier);
				}
				/*
				 * store history
				 */
				storeRevision(revision, TopicMapEventType.VARIANT_REMOVED, parent, null, variant);
				/*
				 * notify listener
				 */
				notifyListeners(TopicMapEventType.VARIANT_REMOVED, parent, null, variant);
			} else {
				session.commit();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
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
				this.pagedTransitiveTypeInstanceIndex = new JdbcPagedTransitiveTypeInstanceIndex(this, getIndex(ITransitiveTypeInstanceIndex.class));
			}
			return (I) pagedTransitiveTypeInstanceIndex;
		} else if (IPagedTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTypeInstanceIndex == null) {
				this.pagedTypeInstanceIndex = new JdbcPagedTypeInstanceIndex(this, getIndex(ITypeInstanceIndex.class));
			}
			return (I) pagedTypeInstanceIndex;
		} else if (IPagedIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedIdentityIndex == null) {
				this.pagedIdentityIndex = new JdbcPagedIdentityIndex(this, getIndex(IIdentityIndex.class));
			}
			return (I) pagedIdentityIndex;
		} else if (IPagedLiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedLiteralIndex == null) {
				this.pagedLiteralIndex = new JdbcPagedLiteralIndex(this, getIndex(ILiteralIndex.class));
			}
			return (I) pagedLiteralIndex;
		} else if (IPagedSupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedSupertypeSubtypeIndex == null) {
				this.pagedSupertypeSubtypeIndex = new JdbcPagedSupertypeSubtypeIndex(this, getIndex(ISupertypeSubtypeIndex.class));
			}
			return (I) pagedSupertypeSubtypeIndex;
		} else if (IPagedConstructIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedConstructIndex == null) {
				this.pagedConstructIndex = new JdbcPagedConstructIndex(this);
			}
			return (I) pagedConstructIndex;
		} else if (IPagedScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedScopedIndex == null) {
				this.pagedScopedIndex = new JdbcPagedScopeIndex(this, getIndex(IScopedIndex.class));
			}
			return (I) pagedScopedIndex;
		} else if (ITransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.transitiveTypeInstanceIndex == null) {
				transitiveTypeInstanceIndex = new JdbcTransitiveTypeInstanceIndex(this);
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
		throw new UnsupportedOperationException("The index class '" + (clazz == null ? "null" : clazz.getCanonicalName()) + "' is not supported by the current engine.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		super.initialize(topicMapBaseLocator);
		Object oDialect = getTopicMapSystem().getProperty(JdbcTopicMapStoreProperty.SQL_DIALECT);
		if (oDialect == null) {
			throw new TopicMapStoreException("Missing connection properties!");
		}
		dialect = oDialect.toString();
		provider = ConnectionProviderFactory.getFactory().newConnectionProvider(dialect);
		provider.setTopicMapStore(this);
		ISession session = provider.openSession();
		try {
			Long id = session.getProcessor().doReadTopicMapIdentity(getTopicMapBaseLocator());
			/*
			 * create a new topic map
			 */
			if (id == null) {
				id = session.getProcessor().doCreateTopicMapIdentity(getTopicMapBaseLocator());

			}
			session.commit();
			this.identity = new JdbcIdentity(id);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot open connection to database!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		try {
			provider.close();
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		super.connect();
		if (isRevisionManagementEnabled()) {
			try {
				ISession session = provider.openSession();
				try {
					IRevision r = session.getProcessor().doReadFirstRevision(getTopicMap());
					/*
					 * topic map creation is not part of the history
					 */
					if (r == null) {
						r = createRevision(TopicMapEventType.TOPIC_MAP_CREATED);
						storeRevision(r, TopicMapEventType.TOPIC_MAP_CREATED, getTopicMap(), getTopicMap(), null);
					}
				} finally {
					session.close();
				}
			} catch (SQLException e) {
				throw new TopicMapStoreException("Cannot establish connection to database", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransactable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision createRevision(TopicMapEventType type) {
		if (isRevisionManagementEnabled()) {
			ISession session = provider.openSession();
			try {
				IRevision revision = session.getProcessor().doCreateRevision(getTopicMap(), type);
				session.commit();
				return revision;
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			} finally {
				try {
					session.close();
				} catch (SQLException e) {
					throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void storeRevision(IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		if (isRevisionManagementEnabled()) {
			ISession session = provider.openSession();
			try {
				session.getProcessor().doCreateChangeSet(revision, type, context, newValue, oldValue);
				session.commit();
			} catch (SQLException e) {
				throw new TopicMapStoreException("Internal database error!", e);
			} finally {
				try {
					session.close();
				} catch (SQLException e) {
					throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
				}
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
	protected void createTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) {
		ISession session = provider.openSession();
		try {
			/*
			 * create association
			 */
			IAssociation association = session.getProcessor().doCreateAssociation(getTopicMap(), getTmdmTypeInstanceAssociationType());
			/*
			 * create roles
			 */
			IAssociationRole roleInstance = session.getProcessor().doCreateRole(association, getTmdmInstanceRoleType(), instance);
			IAssociationRole roleType = session.getProcessor().doCreateRole(association, getTmdmTypeRoleType(), type);
			session.commit();
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(), association, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, roleInstance, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, roleType, null);
			/*
			 * store history
			 */
			storeRevision(revision, TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(), association, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association, roleInstance, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association, roleType, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void createSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) {
		ISession session = provider.openSession();
		try {
			/*
			 * create association
			 */
			IAssociation association = session.getProcessor().doCreateAssociation(getTopicMap(), getTmdmSupertypeSubtypeAssociationType());
			/*
			 * create roles
			 */
			IAssociationRole roleSubtype = session.getProcessor().doCreateRole(association, getTmdmSubtypeRoleType(), type);
			IAssociationRole roleSupertype = session.getProcessor().doCreateRole(association, getTmdmSupertypeRoleType(), supertype);
			session.commit();
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(), association, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, roleSubtype, null);
			notifyListeners(TopicMapEventType.ROLE_ADDED, association, roleSupertype, null);
			/*
			 * store history
			 */
			storeRevision(revision, TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(), association, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association, roleSubtype, null);
			storeRevision(revision, TopicMapEventType.ROLE_ADDED, association, roleSupertype, null);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void removeSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) throws TopicMapStoreException {
		ISession session = provider.openSession();
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmSupertypeSubtypeAssociationType());
		try {
			for (IAssociation association : associations) {
				try {
					/*
					 * get subtype roles
					 */
					Collection<IAssociationRole> rolesSubtype = session.getProcessor().doReadRoles(association, getTmdmSubtypeRoleType());
					/*
					 * get player
					 */
					ITopic player = session.getProcessor().doReadPlayer(rolesSubtype.iterator().next());
					if (player.equals(type)) {
						/*
						 * get supertype roles
						 */
						Collection<IAssociationRole> rolesSupertype = session.getProcessor().doReadRoles(association, getTmdmSupertypeRoleType());
						/*
						 * get player
						 */
						player = session.getProcessor().doReadPlayer(rolesSupertype.iterator().next());
						if (player.equals(supertype)) {
							session.getProcessor().doRemoveAssociation(association, true, revision);
							session.commit();
							break;
						}
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model! Missing supertype or subtype role!", e);
				} catch (SQLException e) {
					throw new TopicMapStoreException("Internal database error!", e);
				}
			}
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected void removeTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) throws TopicMapStoreException {
		ISession session = provider.openSession();
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmTypeInstanceAssociationType());
		try {
			for (IAssociation association : associations) {
				try {
					/*
					 * get subtype roles
					 */
					Collection<IAssociationRole> rolesInstances = session.getProcessor().doReadRoles(association, getTmdmInstanceRoleType());/*
																																			 * get
																																			 * player
																																			 */
					ITopic player = session.getProcessor().doReadPlayer(rolesInstances.iterator().next());
					if (player.equals(instance)) {
						/*
						 * get supertype roles
						 */
						Collection<IAssociationRole> rolesType = session.getProcessor().doReadRoles(association, getTmdmTypeRoleType());
						/*
						 * get player
						 */
						player = session.getProcessor().doReadPlayer(rolesType.iterator().next());
						if (player.equals(type)) {
							session.getProcessor().doRemoveAssociation(association, true, revision);
							session.commit();
							break;
						}
					}
				} catch (NoSuchElementException e) {
					throw new TopicMapStoreException("Invalid meta model! Missing type or instance role!", e);
				} catch (SQLException e) {
					throw new TopicMapStoreException("Internal database error!", e);
				}
			}
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException(MESSAGE_SESSION_CANNOT_BE_CLOSED, e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		super.clearCache();
		boolean wasCachingEnabled = isCachingEnabled();
		enableCaching(false);
		ISession session = provider.openSession();
		try {
			session.getProcessor().doClearTopicMap(getTopicMap());
			if (typeInstanceIndex != null) {
				typeInstanceIndex.clear();
			}
			if (transitiveTypeInstanceIndex != null) {
				transitiveTypeInstanceIndex.clear();
			}
			if (supertSubtypeIndex != null) {
				supertSubtypeIndex.clear();
			}
			if (scopedIndex != null) {
				scopedIndex.clear();
			}
			if (literalIndex != null) {
				literalIndex.clear();
			}
			if (identityIndex != null) {
				identityIndex.clear();
			}
			if (revisionIndex != null) {
				revisionIndex.clear();
			}

			// Paged Indexes
			if (pagedTypeInstanceIndex != null) {
				pagedTypeInstanceIndex.clear();
			}
			if (pagedIdentityIndex != null) {
				pagedIdentityIndex.clear();
			}
			if (pagedConstructIndex != null) {
				pagedConstructIndex.clear();
			}
			if (pagedScopedIndex != null) {
				pagedScopedIndex.clear();
			}
			if (pagedSupertypeSubtypeIndex != null) {
				pagedSupertypeSubtypeIndex.clear();
			}
			if (pagedTransitiveTypeInstanceIndex != null) {
				pagedTransitiveTypeInstanceIndex.clear();
			}
			if (pagedLiteralIndex != null) {
				pagedLiteralIndex.clear();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e) {
				throw new TopicMapStoreException("Session cannot be closed!", e);
			}
		}
		enableCaching(wasCachingEnabled);
	}

	/**
	 * Returns the internal identity of the topic map
	 * 
	 * @return the identity
	 */
	public JdbcIdentity getTopicMapIdentity() {
		return identity;
	}

	/**
	 * Creates a new session using connection provider
	 * 
	 * @return the new session
	 */
	public ISession openSession() {
		if (!isConnected()) {
			throw new TopicMapStoreException("Topic map store is not connected!");
		}
		return provider.openSession();
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		ISession session = openSession();
		/*
		 * check if the processor provides special mechanism or functions to remove duplicates
		 */
		if (session.getProcessor().canPerformRemoveDuplicates()) {
			try {
				session.getProcessor().doRemoveDuplicates();
				clearCache();
			} catch (SQLException e) {
				throw new TopicMapStoreException("Execution of remove-duplicates failed!", e);
			} finally {
				try {
					session.close();
				} catch (SQLException e) {
					throw new TopicMapStoreException("Session cannot be closed!", e);
				}
			}
		}
		/*
		 * fall-back -> remove by iteration
		 */
		else {
			JdbcMergeUtils.removeDuplicates(this, getTopicMap());
		}
	}

	/**
	 * @return the dialect
	 */
	public String getDialect() {
		return dialect;
	}

}
