package de.topicmapslab.majortom.inmemory.store;

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.core.AssociationImpl;
import de.topicmapslab.majortom.core.AssociationRoleImpl;
import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.NameImpl;
import de.topicmapslab.majortom.core.OccurrenceImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.core.VariantImpl;
import de.topicmapslab.majortom.inmemory.index.InMemoryIdentityIndex;
import de.topicmapslab.majortom.inmemory.index.InMemoryLiteralIndex;
import de.topicmapslab.majortom.inmemory.index.InMemoryRevisionIndex;
import de.topicmapslab.majortom.inmemory.index.InMemoryScopedIndex;
import de.topicmapslab.majortom.inmemory.index.InMemorySupertypeSubtypeIndex;
import de.topicmapslab.majortom.inmemory.index.InMemoryTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.inmemory.index.InMemoryTypeInstanceIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedConstructIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedIdentityIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedLiteralIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedScopeIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.inmemory.index.paged.InMemoryPagedTypeInstanceIndex;
import de.topicmapslab.majortom.inmemory.store.internal.AssociationStore;
import de.topicmapslab.majortom.inmemory.store.internal.CharacteristicsStore;
import de.topicmapslab.majortom.inmemory.store.internal.IdentityStore;
import de.topicmapslab.majortom.inmemory.store.internal.ReificationStore;
import de.topicmapslab.majortom.inmemory.store.internal.ScopeStore;
import de.topicmapslab.majortom.inmemory.store.internal.TopicTypeStore;
import de.topicmapslab.majortom.inmemory.store.internal.TypedStore;
import de.topicmapslab.majortom.inmemory.store.revision.RevisionStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransaction;
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
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

public class InMemoryTopicMapStore extends TopicMapStoreImpl {

	private IdentityStore identityStore;
	private CharacteristicsStore characteristicsStore;
	private TypedStore typedStore;
	private ScopeStore scopeStore;
	private TopicTypeStore topicTypeStore;
	private ReificationStore reificationStore;
	private AssociationStore associationStore;
	private RevisionStore revisionStore;

	/**
	 * the identity of the topic map itself
	 */
	private InMemoryIdentity identity;

	/**
	 * indexes
	 */
	private TypeInstanceIndex typeInstanceIndex;
	private ITransitiveTypeInstanceIndex transitiveTypeInstanceIndex;
	private ScopedIndex scopedIndex;
	private LiteralIndex literalIndex;
	private IIdentityIndex identityIndex;
	private ISupertypeSubtypeIndex supertypeSubtypeIndex;
	private IRevisionIndex revisionIndex;
	/**
	 * paged indexes
	 */
	private IPagedTypeInstanceIndex pagedTypeInstanceIndex;
	private IPagedTransitiveTypeInstanceIndex pagedTransitiveTypeInstanceIndex;
	private IPagedSupertypeSubtypeIndex pagedSupertypeSubtypeIndex;
	private IPagedScopedIndex pagedScopedIndex;
	private IPagedIdentityIndex pagedIdentityIndex;
	private IPagedLiteralIndex pagedLiteralIndex;
	private IPagedConstructIndex pagedConstructIndex;

	/**
	 * thread specific attributes
	 */
	private boolean blocked = false;
	private List<Runnable> queue;

	/**
	 * constructor
	 */
	public InMemoryTopicMapStore() {
		super();
	}

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 */
	public InMemoryTopicMapStore(ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		// NOTHING TO DO
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateItemIdentifier(ITopicMap topicMap) {
		return getIdentityStore().createItemIdentifier(topicMap);
	}

	/**
	 * Internal creation method for associations.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @return the association
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	IAssociation createAssociation(ITopicMap topicMap) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create topic and add to identity store
		 */
		IAssociation a = new AssociationImpl(new InMemoryIdentity(id), topicMap);
		getIdentityStore().setId(a, id);
		/*
		 * register association
		 */
		getAssociationStore().addAssociation(a);
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws TopicMapStoreException {
		/*
		 * create topic and add to identity store
		 */
		IAssociation a = createAssociation(topicMap);
		/*
		 * register type
		 */
		modifyType(a, type, null);
		/*
		 * register scope
		 */
		getScopeStore().setScope(a, getScopeStore().getEmptyScope());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
		/*
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
		return a;
	}

	/**
	 * Internal method to create an association
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param type
	 *            the type
	 * @param themes
	 *            the themes
	 * @param revision
	 *            the revision
	 * @return the created association
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	IAssociation createAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes, IRevision revision) throws TopicMapStoreException {
		/*
		 * create topic and add to identity store
		 */
		IAssociation a = createAssociation(topicMap);
		/*
		 * register type
		 */
		modifyType(a, type, null);
		/*
		 * get new scope object
		 */
		IScope scope = getScopeStore().getScope(themes);
		/*
		 * register scope
		 */
		getScopeStore().setScope(a, scope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
		/*
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws TopicMapStoreException {
		return createAssociation(topicMap, type, themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference) throws TopicMapStoreException {
		return getIdentityStore().createLocator(reference);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);

		/*
		 * check if topics should merge because of same name
		 */
		if (doMergingByTopicName()) {
			Set<ITopic> themes = HashUtil.getHashSet();
			Map<ITopic, IName> candidate = InMemoryMergeUtils.detectMergeByNameCandidate(this, topic, type, value, themes);
			if (candidate != null) {
				Entry<ITopic, IName> c = candidate.entrySet().iterator().next();
				if (!doAutomaticMerging()) {
					throw new ModelConstraintException(c.getValue(),
							"A topic with the same name already exists and the merge-by-name feature is set, but auto-merge is disabled.");
				}
				mergeTopics(topic, c.getKey(), revision);
				return c.getValue();
			}
		}

		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = new NameImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(name, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addName(topic, name);
		/*
		 * register typed
		 */
		modifyType(name, type, null);
		/*
		 * register value
		 */
		modifyValue(name, value, null);
		/*
		 * register scope
		 */
		getScopeStore().setScope(name, getScopeStore().getEmptyScope());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, name, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_ADDED, topic, name, null);
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);

		/*
		 * check if topics should merge because of same name
		 */
		if (doMergingByTopicName()) {
			Map<ITopic, IName> candidate = InMemoryMergeUtils.detectMergeByNameCandidate(this, topic, type, value, themes);
			if (candidate != null) {
				Entry<ITopic, IName> c = candidate.entrySet().iterator().next();
				if (!doAutomaticMerging()) {
					throw new ModelConstraintException(c.getValue(),
							"A topic with the same name already exists and the merge-by-name feature is set, but auto-merge is disabled.");
				}
				mergeTopics(topic, c.getKey(), revision);
				return c.getValue();
			}
		}
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = new NameImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(name, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addName(topic, name);
		/*
		 * register typed
		 */
		modifyType(name, type, null);
		/*
		 * register value
		 */
		modifyValue(name, value, null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(name, scope);
		/*
		 * notify listener revision
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, name, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_ADDED, topic, name, null);
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * check if topics should merge because of same name
		 */
		if (doMergingByTopicName()) {
			Set<ITopic> themes = HashUtil.getHashSet();
			Map<ITopic, IName> candidate = InMemoryMergeUtils.detectMergeByNameCandidate(this, topic, type, value, themes);
			if (candidate != null) {
				Entry<ITopic, IName> c = candidate.entrySet().iterator().next();
				if (!doAutomaticMerging()) {
					throw new ModelConstraintException(c.getValue(),
							"A topic with the same name already exists and the merge-by-name feature is set, but auto-merge is disabled.");
				}
				mergeTopics(topic, c.getKey(), revision);
				return c.getValue();
			}
		}
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = new NameImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(name, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addName(topic, name);
		/*
		 * register typed
		 */
		modifyType(name, type, null);
		/*
		 * register value
		 */
		modifyValue(name, value, null);
		/*
		 * register scope
		 */
		getScopeStore().setScope(name, getScopeStore().getEmptyScope());
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, name, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_ADDED, topic, name, null);
		return name;
	}

	/**
	 * Internal modification method to create a new name item to a topic item.
	 * 
	 * @param topic
	 *            the topic
	 * @param type
	 *            the name type
	 * @param value
	 *            the value
	 * @param themes
	 *            the themes
	 * @param revision
	 *            the revision
	 * @return the created name
	 * @throws TopicMapStoreException
	 *             thrown if opteration fails
	 */
	IName createName(ITopic topic, ITopic type, String value, Collection<ITopic> themes, IRevision revision) throws TopicMapStoreException {
		/*
		 * check if topics should merge because of same name
		 */
		if (doMergingByTopicName()) {
			Map<ITopic, IName> candidate = InMemoryMergeUtils.detectMergeByNameCandidate(this, topic, type, value, themes);
			if (candidate != null) {
				Entry<ITopic, IName> c = candidate.entrySet().iterator().next();
				if (!doAutomaticMerging()) {
					throw new ModelConstraintException(c.getValue(),
							"A topic with the same name already exists and the merge-by-name feature is set, but auto-merge is disabled.");
				}
				mergeTopics(topic, c.getKey(), revision);
				return c.getValue();
			}
		}
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = new NameImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(name, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addName(topic, name);
		/*
		 * register typed
		 */
		modifyType(name, type, null);
		/*
		 * register value
		 */
		modifyValue(name, value, null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(name, scope);
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, name, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_ADDED, topic, name, null);
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createName(topic, type, value, themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING), createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING), themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI), createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI), themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, themes, createRevision());
	}

	/**
	 * Internal method to create an occurrence of the given topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param type
	 *            the occurrence type
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @param revision
	 *            the revision to store history
	 * @return the created occurrence
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private IOccurrence createOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IOccurrence occurrence = new OccurrenceImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(occurrence, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addOccurrence(topic, occurrence);
		/*
		 * register typed
		 */
		modifyType(occurrence, type, null);
		/*
		 * register value
		 */
		modifyValue(occurrence, value, datatype, null);
		/*
		 * register scope
		 */
		getScopeStore().setScope(occurrence, getScopeStore().getEmptyScope());
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, occurrence, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.OCCURRENCE_ADDED, topic, occurrence, null);
		return occurrence;
	}

	/**
	 * Internal modification method to create a new occurrence for the given
	 * topic item.
	 * 
	 * @param topic
	 *            the topic item
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @param themes
	 *            the themes
	 * @param revision
	 *            the revision
	 * @return the created occurrence
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	IOccurrence createOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IOccurrence occurrence = new OccurrenceImpl(new InMemoryIdentity(id), topic);
		getIdentityStore().setId(occurrence, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addOccurrence(topic, occurrence);
		/*
		 * register typed
		 */
		modifyType(occurrence, type, null);
		/*
		 * register value
		 */
		modifyValue(occurrence, value, datatype, null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(occurrence, scope);
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, occurrence, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.OCCURRENCE_ADDED, topic, occurrence, null);
		return occurrence;
	}

	/**
	 * Internal creation method for roles.
	 * 
	 * @param association
	 *            the association
	 * @param type
	 *            the type
	 * @param player
	 *            the player
	 * @param revision
	 *            the revision
	 * @return the created role
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	IAssociationRole createRole(IAssociation association, ITopic type, ITopic player, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IAssociationRole role = new AssociationRoleImpl(new InMemoryIdentity(id), association);
		getIdentityStore().setId(role, id);
		/*
		 * register typed
		 */
		modifyType(role, type, null);
		/*
		 * register role construct
		 */
		getAssociationStore().addRole(association, role, player);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_ADDED, association, role, null);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ROLE_ADDED, association, role, null);
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws TopicMapStoreException {
		return createRole(association, type, player, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws TopicMapStoreException {
		return scopeStore.getScope(themes);
	}

	/**
	 * Internal creation method for a topic
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param revision
	 *            the revision
	 * @return the create topic
	 * @throws TopicMapStoreException
	 */
	ITopic createTopic(ITopicMap topicMap, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create topic and add to identity store
		 */
		ITopic t = new TopicImpl(new InMemoryIdentity(id), topicMap);
		getIdentityStore().setId(t, id);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
			/*
			 * store as revision
			 */
			storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		ITopic topic = createTopic(topicMap, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add item identifier
		 */
		modifyItemIdentifier(topic, itemIdentifier, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add subject identifier
		 */
		modifySubjectIdentifier(topic, subjectIdentifier, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add subject locator
		 */
		modifySubjectLocator(topic, subjectLocator, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create role and add to identity store
		 */
		IVariant variant = new VariantImpl(new InMemoryIdentity(id), name);
		getIdentityStore().setId(variant, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addVariant(name, variant);
		/*
		 * register value
		 */
		modifyValue(variant, value, getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING), null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(variant, scope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_ADDED, name, variant, null);
		return variant;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create role and add to identity store
		 */
		IVariant variant = new VariantImpl(new InMemoryIdentity(id), name);
		getIdentityStore().setId(variant, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addVariant(name, variant);
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
		storeRevision(revision, TopicMapEventType.VARIANT_ADDED, name, variant, null);
		/*
		 * register value
		 */
		modifyValue(variant, value, getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_ANYURI), null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(variant, scope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_ADDED, name, variant, null);
		return variant;
	}

	/**
	 * Internal method to create a variant of the given name
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @param datatype
	 *            the datatype
	 * @param themes
	 *            the themes
	 * @param revision
	 *            the revision
	 * @return the created variant
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	IVariant createVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IVariant variant = new VariantImpl(new InMemoryIdentity(id), name);
		getIdentityStore().setId(variant, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addVariant(name, variant);
		/*
		 * register value
		 */
		modifyValue(variant, value, datatype, null);
		/*
		 * register scope
		 */
		IScope scope = getScopeStore().getScope(themes);
		getScopeStore().setScope(variant, scope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
		/*
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_ADDED, name, variant, null);
		return variant;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		return createVariant(name, value, datatype, themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopicMaps(TopicMap context, TopicMap other) throws TopicMapStoreException {
		if (!context.equals(getTopicMap())) {
			throw new TopicMapStoreException("Calling topic map does not belong to the called store.");
		}
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.MERGE, getTopicMap(), context, other);
		InMemoryMergeUtils.doMergeTopicMaps(this, (ITopicMap) context, other);

	}

	/**
	 * Internal modification method to merge two topics
	 * 
	 * @param context
	 *            the context
	 * @param other
	 *            the topic to merge in the other one
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void mergeTopics(ITopic context, ITopic other, IRevision revision) throws TopicMapStoreException {
		if (!context.equals(other)) {
			/*
			 * merge into
			 */
			ITopic newTopic = createTopic(getTopicMap(), revision);
			InMemoryMergeUtils.doMerge(this, newTopic, context, revision);
			InMemoryMergeUtils.doMerge(this, newTopic, other, revision);
			((InMemoryIdentity) ((TopicImpl) context).getIdentity()).setId(newTopic.getId());
			((InMemoryIdentity) ((TopicImpl) other).getIdentity()).setId(newTopic.getId());
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic, other);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other) throws TopicMapStoreException {
		mergeTopics(context, other, createRevision());
	}

	/**
	 * Internal modification method to add a item-identifier
	 * 
	 * @param c
	 *            the construct
	 * @param itemIdentifier
	 *            the item identifier
	 * @param revision
	 *            the revision to store dependent changes
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyItemIdentifier(IConstruct c, ILocator itemIdentifier, IRevision revision) throws TopicMapStoreException {
		/*
		 * check if item-identifier causes merging
		 */
		ITopic topic = checkMergeConditionOfItemIdentifier(c, itemIdentifier);
		if (topic != null) {
			mergeTopics((ITopic) c, topic, revision);
		}
		getIdentityStore().addItemIdentifer(c, itemIdentifier);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c, itemIdentifier, null);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.ITEM_IDENTIFIER_ADDED, c, itemIdentifier, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		/*
		 * do modification
		 */
		modifyItemIdentifier(c, itemIdentifier, createRevision());
	}

	/**
	 * Internal method to modify the player of an association role
	 * 
	 * @param role
	 *            the role
	 * @param player
	 *            the player
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyPlayer(IAssociationRole role, ITopic player, IRevision revision) throws TopicMapStoreException {
		ITopic oldValue = getAssociationStore().setPlayer(role, player);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.PLAYER_MODIFIED, role, player, oldValue);
		/*
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.PLAYER_MODIFIED, role, player, oldValue);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player) throws TopicMapStoreException {
		modifyPlayer(role, player, createRevision());
	}

	/**
	 * Internal modification method to change the reification of the given
	 * construct.
	 * 
	 * @param r
	 *            the construct
	 * @param reifier
	 *            the reifier
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyReifier(IReifiable r, ITopic reifier, IRevision revision) throws TopicMapStoreException {
		ITopic oldValue = getReificationStore().setReifier(r, reifier);
		if (oldValue != reifier && revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.REIFIER_SET, r, reifier, oldValue);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.REIFIER_SET, r, reifier, oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier) throws TopicMapStoreException {
		modifyReifier(r, reifier, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		/*
		 * remove old scope relation
		 */
		IScope oldScope = getScopeStore().removeScope(s);
		/*
		 * get new scope object
		 */
		Collection<ITopic> themes = HashUtil.getHashSet(oldScope.getThemes());
		themes.add(theme);
		IScope newScope = getScopeStore().getScope(themes);
		/*
		 * set new scope
		 */
		getScopeStore().setScope(s, newScope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
		/*
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
	}

	/**
	 * Internal modification method to add a new subject-identifier
	 * 
	 * @param t
	 *            the topic
	 * @param subjectIdentifier
	 *            the subject-identifier
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifySubjectIdentifier(ITopic t, ILocator subjectIdentifier, IRevision revision) throws TopicMapStoreException {
		/*
		 * check if subject-identifier causes merging
		 */
		ITopic topic = checkMergeConditionOfSubjectIdentifier(t, subjectIdentifier);
		if (topic != null) {
			mergeTopics(t, topic, revision);
		}
		getIdentityStore().addSubjectIdentifier(t, subjectIdentifier);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		modifySubjectIdentifier(t, subjectIdentifier, createRevision());
	}

	/**
	 * Internal modification method to add a new subject-locator.
	 * 
	 * @param t
	 *            the topic
	 * @param subjectLocator
	 *            the subject-locator
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if the operation fails
	 */
	void modifySubjectLocator(ITopic t, ILocator subjectLocator, IRevision revision) throws TopicMapStoreException {
		/*
		 * check if subject-locator causes merging
		 */
		ITopic topic = checkMergeConditionOfSubjectLocator(t, subjectLocator);
		if (topic != null) {
			mergeTopics(t, topic, revision);
		} else {
			getIdentityStore().addSubjectLocator(t, subjectLocator);
			if (revision != null) {
				/*
				 * notify listeners
				 */
				notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
				/*
				 * store revision
				 */
				storeRevision(revision, TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		modifySubjectLocator(t, subjectLocator, createRevision());
	}

	/**
	 * Internal modification method to add a new super type to a topic type
	 * 
	 * @param t
	 *            the topic type
	 * @param type
	 *            the super type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifySupertype(ITopic t, ITopic type, IRevision revision) throws TopicMapStoreException {
		if (!getTopicTypeStore().getSupertypes(t).contains(type)) {
			getTopicTypeStore().addSupertype(t, type);
			/*
			 * create type-hierarchy as association
			 */
			if (typeHiearchyAsAssociation()) {
				createSupertypeSubtypeAssociation(t, type, revision);
			}
			notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		modifySupertype(t, type, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag) throws TopicMapStoreException {
		getRevisionStore().addTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws TopicMapStoreException {
		getRevisionStore().addTag(tag, timestamp);
	}

	/**
	 * Internal method to modify type
	 * 
	 * @param t
	 *            the typed item
	 * @param type
	 *            the type
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyType(ITypeable t, ITopic type, IRevision revision) throws TopicMapStoreException {
		/*
		 * remove oldType
		 */
		ITopic oldType = getTypedStore().removeType(t);
		/*
		 * set new type
		 */
		getTypedStore().setType(t, type);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.TYPE_SET, t, type, oldType);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.TYPE_SET, t, type, oldType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type) throws TopicMapStoreException {
		modifyType(t, type, createRevision());
	}

	/**
	 * Internal modification method to add a new type to a topic item.
	 * 
	 * @param t
	 *            the topic item
	 * @param type
	 *            the new type
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyType(ITopic t, ITopic type, IRevision revision) throws TopicMapStoreException {
		if (!getTopicTypeStore().getTypes(t).contains(type)) {
			getTopicTypeStore().addType(t, type);
			if (typeHiearchyAsAssociation()) {
				createTypeInstanceAssociation(t, type, revision);
			}
			if (revision != null) {
				/*
				 * notify listeners
				 */
				notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
				/*
				 * store revision
				 */
				storeRevision(revision, TopicMapEventType.TYPE_ADDED, t, type, null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITopic t, ITopic type) throws TopicMapStoreException {
		modifyType(t, type, createRevision());
	}

	/**
	 * Internal modification method to modify the value and the data type of a
	 * {@link IDatatypeAware}.
	 * 
	 * @param c
	 *            the {@link IDatatypeAware}
	 * @param value
	 *            the value
	 * @param datatype
	 *            the data type
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void modifyValue(IDatatypeAware c, Object value, ILocator datatype, IRevision revision) throws TopicMapStoreException {
		/*
		 * modify the value of the characteristics
		 */
		Object oldValue = getCharacteristicsStore().setValue(c, value);
		/*
		 * modify the data type of the characteristics
		 */
		ILocator oldDataType = getCharacteristicsStore().setDatatype(c, datatype);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, c, value, oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, c, datatype, oldDataType);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, c, value, oldValue);
			storeRevision(revision, TopicMapEventType.DATATYPE_SET, c, datatype, oldDataType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value) throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING);
		modifyValue(c, value, datatype, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value, ILocator datatype) throws TopicMapStoreException {
		modifyValue(c, value, datatype, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, Object value) throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(XmlSchemeDatatypes.javaToXsd(value.getClass()));
		modifyValue(c, value, datatype, createRevision());
	}

	/**
	 * Internal modification method to modify the value of a name
	 * 
	 * @param n
	 *            the name
	 * @param value
	 *            the value
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 */
	void modifyValue(IName n, String value, IRevision revision) throws TopicMapStoreException {
		/*
		 * modify the value of the characteristics
		 */
		Object oldValue = getCharacteristicsStore().setValue(n, value);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value) throws TopicMapStoreException {
		modifyValue(n, value, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		for (IAssociationRole r : getAssociationStore().getRoles(t)) {
			associations.add(r.getParent());
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociation> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet();
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic type_ : types) {
			/*
			 * get all associations
			 */
			Set<IAssociation> associations = HashUtil.getHashSet(doReadAssociation(t));
			/*
			 * filter by type
			 */
			associations.retainAll(getTypedStore().getTyped(type_));
			set.addAll(associations);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociation> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet();// getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic type_ : types) {
			/*
			 * get all associations
			 */
			Set<IAssociation> associations = HashUtil.getHashSet(doReadAssociation(t));
			/*
			 * filter by type
			 */
			associations.retainAll(getTypedStore().getTyped(type_));
			/*
			 * filter by scope
			 */
			associations.retainAll(getScopeStore().getScoped(scope));
			set.addAll(associations);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil.getHashSet(doReadAssociation(t));

		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm) throws TopicMapStoreException {
		return HashUtil.getHashSet(getAssociationStore().getAssociations());
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociation> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic t : types) {
			/*
			 * get all associations
			 */
			Set<IAssociation> associations = HashUtil.getHashSet(getAssociationStore().getAssociations());
			/*
			 * filter by type
			 */
			associations.retainAll(getTypedStore().getTyped(t));
			set.addAll(associations);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociation> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic t : types) {
			/*
			 * get all associations
			 */
			Set<IAssociation> associations = HashUtil.getHashSet(getAssociationStore().getAssociations());
			/*
			 * filter by type
			 */
			associations.retainAll(getTypedStore().getTyped(t));
			/*
			 * filter by scope
			 */
			associations.retainAll(getScopeStore().getScoped(scope));
			set.addAll(associations);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil.getHashSet(getAssociationStore().getAssociations());
		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getChangeset(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(t));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws TopicMapStoreException {
		/*
		 * get characteristics of the topic
		 */
		Set<ICharacteristics> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all roles
			 */
			Set<ICharacteristics> characteristics = HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(t));
			/*
			 * filter by type
			 */
			characteristics.retainAll(getTypedStore().getTyped(ty));
			set.addAll(characteristics);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		/*
		 * get characteristics of the topic
		 */
		Set<ICharacteristics> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all roles
			 */
			Set<ICharacteristics> characteristics = HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(t));
			/*
			 * filter by type
			 */
			characteristics.retainAll(getTypedStore().getTyped(ty));
			set.addAll(characteristics);
		}
		/*
		 * filter by scope
		 */
		set.retainAll(getScopeStore().getScoped(scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws TopicMapStoreException {
		/*
		 * get characteristics of the topic
		 */
		Set<ICharacteristics> set = HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(t));
		/*
		 * filter by scope
		 */
		set.retainAll(getScopeStore().getScoped(scope));
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IConstruct doReadConstruct(ITopicMap t, String id) throws TopicMapStoreException {
		/*
		 * check if id identifies the topic map itself
		 */
		if (this.identity.getId().equalsIgnoreCase(id)) {
			return getTopicMap();
		}
		/*
		 * check if id identifies a lazy copy of a removed construct
		 */
		else if (getRevisionStore().isLazyCopy(id)) {
			return getRevisionStore().getLazyCopy(id);
		}
		/*
		 * return the construct by id
		 */
		return getIdentityStore().byId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws TopicMapStoreException {
		return getIdentityStore().byItemIdentifier(itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doReadDataType(IDatatypeAware c) throws TopicMapStoreException {
		return getCharacteristicsStore().getDatatype(c);
	}

	/**
	 * {@inheritDoc}
	 */
	protected String doReadId(IConstruct c) throws TopicMapStoreException {
		if (c instanceof ITopicMap) {
			return this.identity.getId();
		} else if (c instanceof ConstructImpl) {
			ITopicMapStoreIdentity identity = ((ConstructImpl) c).getIdentity();
			return identity.getId();
		}
		throw new TopicMapStoreException("IConstruct created by external instance.");
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadItemIdentifiers(IConstruct c) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getItemIdentifiers(c));
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore().getNames(t));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IName> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all roles
			 */
			Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore().getNames(t));
			/*
			 * filter by type
			 */
			names.retainAll(getTypedStore().getTyped(ty));
			set.addAll(names);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IName> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all names
			 */
			Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore().getNames(t));
			/*
			 * filter by type
			 */
			names.retainAll(getTypedStore().getTyped(ty));
			/*
			 * filter by scope
			 */
			names.retainAll(getScopeStore().getScoped(scope));
			set.addAll(names);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IName> doReadNames(ITopic t, IScope scope) throws TopicMapStoreException {
		/*
		 * get all names
		 */
		Set<IName> names = HashUtil.getHashSet();
		names.addAll(getCharacteristicsStore().getNames(t));
		/*
		 * filter by scope
		 */
		names.retainAll(getScopeStore().getScoped(scope));
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IRevision doReadFutureRevision(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getNextRevision(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t) throws TopicMapStoreException {
		Set<IOccurrence> occurrences = HashUtil.getHashSet();
		occurrences.addAll(getCharacteristicsStore().getOccurrences(t));
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IOccurrence> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all occurrence
			 */
			Set<IOccurrence> occurrences = HashUtil.getHashSet(getCharacteristicsStore().getOccurrences(t));
			/*
			 * filter by type
			 */
			occurrences.retainAll(getTypedStore().getTyped(ty));
			set.addAll(occurrences);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IOccurrence> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic ty : types) {
			/*
			 * get all occurrence
			 */
			Set<IOccurrence> occurrences = HashUtil.getHashSet(getCharacteristicsStore().getOccurrences(t));
			/*
			 * filter by type
			 */
			occurrences.retainAll(getTypedStore().getTyped(ty));
			/*
			 * filter by scope
			 */
			occurrences.retainAll(getScopeStore().getScoped(scope));
			set.addAll(occurrences);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws TopicMapStoreException {
		/*
		 * get all occurrence
		 */
		Set<IOccurrence> occurrences = HashUtil.getHashSet(getCharacteristicsStore().getOccurrences(t));
		/*
		 * filter by scope
		 */
		occurrences.retainAll(getScopeStore().getScoped(scope));
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadPlayer(IAssociationRole role) throws TopicMapStoreException {
		return getAssociationStore().getPlayer(role);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IRevision doReadPreviousRevision(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getPreviousRevision(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		return getReificationStore().getReified(t);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		return getReificationStore().getReifier(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Calendar doReadRevisionBegin(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getRevisionBegin(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Calendar doReadRevisionEnd(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getRevisionEnd(r);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadRoleTypes(IAssociation association) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet();
		/*
		 * iterate over all roles
		 */
		for (IAssociationRole r : doReadRoles(association)) {
			/*
			 * add type of the current role
			 */
			set.add(getTypedStore().getType(r));
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(IAssociation association) throws TopicMapStoreException {
		return HashUtil.getHashSet(getAssociationStore().getRoles(association));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociationRole> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic t : types) {
			/*
			 * get all roles
			 */
			Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore().getRoles(association));
			/*
			 * filter by type
			 */
			roles.retainAll(getTypedStore().getTyped(t));
			set.addAll(roles);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player) throws TopicMapStoreException {
		return getAssociationStore().getRoles(player);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociationRole> set = HashUtil.getHashSet();
		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(type));
		types.add(type);
		/*
		 * iterate over types
		 */
		for (ITopic t : types) {
			/*
			 * get all roles
			 */
			Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore().getRoles(player));
			/*
			 * filter by type
			 */
			roles.retainAll(getTypedStore().getTyped(t));
			set.addAll(roles);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociationRole> set = HashUtil.getHashSet();

		/*
		 * get all types
		 */
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getSubtypes(assocType));
		types.add(assocType);

		for (IAssociationRole role : doReadRoles(player, type)) {
			/*
			 * check association type
			 */
			if (types.contains(doReadType(role.getParent()))) {
				set.add(role);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doReadScope(IScopable s) throws TopicMapStoreException {
		return getScopeStore().getScope(s);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getSubjectIdentifiers(t));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ILocator> doReadSubjectLocators(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getSubjectLocators(t));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadSuptertypes(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getTopicTypeStore().getSupertypes(t));
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws TopicMapStoreException {
		return getIdentityStore().bySubjectIdentifier(subjectIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws TopicMapStoreException {
		return getIdentityStore().bySubjectLocator(subjectLocator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getTopics());
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws TopicMapStoreException {
		return HashUtil.getHashSet(getTopicTypeStore().getDirectInstances(type));
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		return getTypedStore().getType(typed);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore().getDirectTypes(t));
		if (recognizingTypeInstanceAssociation() && existsTmdmTypeInstanceAssociationType()) {
			Collection<IAssociation> associations = doReadAssociation(t, getTmdmTypeInstanceAssociationType());
			for (Association association : associations) {
				if (association.getRoles(getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(t)) {
					types.add((ITopic) association.getRoles(getTmdmTypeRoleType()).iterator().next().getPlayer());
				}
			}
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object doReadValue(IDatatypeAware c) throws TopicMapStoreException {
		return getCharacteristicsStore().getValueAsString(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doReadValue(IDatatypeAware c, Class<T> type) throws TopicMapStoreException {
		Object obj = getCharacteristicsStore().getValue(c);
		try {
			return (T) DatatypeAwareUtils.toValue(obj, type);
		} catch (Exception e) {
			throw new TopicMapStoreException("Cannot convert characteristics value to given type!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object doReadValue(IName n) throws TopicMapStoreException {
		return getCharacteristicsStore().getValue(n);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore().getVariants(n));
	}

	/**
	 * {@inheritDoc}
	 */
	protected Set<IVariant> doReadVariants(IName n, IScope scope) throws TopicMapStoreException {
		Set<IVariant> variants = HashUtil.getHashSet(getCharacteristicsStore().getVariants(n));
		variants.retainAll(getScopeStore().getScoped(scope));
		return variants;
	}

	/**
	 * Internal method to remove an association
	 * 
	 * @param association
	 *            the association
	 * @param cascade
	 *            cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeAssocaition(IAssociation association, boolean cascade, IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		getRevisionStore().createLazyCopy(association);
		/*
		 * remove roles
		 */
		Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore().getRoles(association));
		for (IAssociationRole r : roles) {
			removeRole(r, false, revision);
		}
		/*
		 * remove association
		 */
		getAssociationStore().removeAssociation(association);
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(association);
		if (reifier != null) {
			removeTopic(reifier, cascade, revision);
			getReificationStore().removeReification(association);
		}
		/*
		 * remove scope
		 */
		getScopeStore().removeScope(association);
		/*
		 * remove type
		 */
		getTypedStore().removeType(association);
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(association);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED, association.getParent(), null, association);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ASSOCIATION_REMOVED, association.getParent(), null, association);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade) throws TopicMapStoreException {
		removeAssocaition(association, cascade, createRevision());
	}

	/**
	 * Internal method to remove a construct
	 * 
	 * @param construct
	 *            the construct
	 * @param cascade
	 *            cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeConstruct(IConstruct construct, boolean cascade, IRevision revision) throws TopicMapStoreException {
		if (construct instanceof ITopic) {
			removeTopic((ITopic) construct, cascade, revision);
		} else if (construct instanceof IName) {
			removeName((IName) construct, cascade, revision);
		} else if (construct instanceof IOccurrence) {
			removeOccurrence((IOccurrence) construct, cascade, revision);
		} else if (construct instanceof IAssociation) {
			removeAssocaition((IAssociation) construct, cascade, revision);
		} else if (construct instanceof IAssociationRole) {
			removeRole((IAssociationRole) construct, cascade, revision);
		} else if (construct instanceof IVariant) {
			removeVariant((IVariant) construct, cascade, revision);
		} else {
			throw new TopicMapStoreException("Calling method removeConstruct() with an instance of " + construct.getClass().getSimpleName() + " not expected!");
		}
	}

	/**
	 * Internal method to remove a topic name
	 * 
	 * @param name
	 *            the name
	 * @param cascade
	 *            cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeName(IName name, boolean cascade, IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		if (supportRevisions()) {
			getRevisionStore().createLazyCopy(name);
		}
		/*
		 * remove variants
		 */
		for (IVariant v : doReadVariants(name)) {
			removeVariant(v, true, revision);
		}
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(name);
		/*
		 * remove characteristics
		 */
		getCharacteristicsStore().removeName(name);
		/*
		 * remove scope
		 */
		getScopeStore().removeScope(name);
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(name);
		if (reifier != null) {
			removeTopic(reifier, cascade, revision);
			getReificationStore().removeReification(name);
		}
		/*
		 * remove typed
		 */
		getTypedStore().removeType(name);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_REMOVED, name.getParent(), null, name);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_REMOVED, name.getParent(), null, name);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade) throws TopicMapStoreException {
		removeName(name, cascade, createRevision());
	}

	/**
	 * Internal method to remove a occurrence
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param cascade
	 *            cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeOccurrence(IOccurrence occurrence, boolean cascade, IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		getRevisionStore().createLazyCopy(occurrence);
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(occurrence);
		/*
		 * remove characteristics
		 */
		getCharacteristicsStore().removeOccurrence(occurrence);
		/*
		 * remove scope
		 */
		getScopeStore().removeScope(occurrence);
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(occurrence);
		if (reifier != null) {
			removeTopic(reifier, cascade, revision);
			getReificationStore().removeReification(occurrence);
		}
		/*
		 * remove typed
		 */
		getTypedStore().removeType(occurrence);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, occurrence.getParent(), null, occurrence);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.OCCURRENCE_REMOVED, occurrence.getParent(), null, occurrence);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws TopicMapStoreException {
		removeOccurrence(occurrence, cascade, createRevision());
	}

	/**
	 * Internal method to remove an association role
	 * 
	 * @param role
	 *            the role
	 * @param cascade
	 *            the cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeRole(IAssociationRole role, boolean cascade, IRevision revision) throws TopicMapStoreException {
		// REMOVED BECAUSE WILL BE HANDLED BY TOPICMAPSTOREIMPL
		// /*
		// * remove dependent association too?
		// */
		// if (cascade) {
		// /*
		// * remove parent association
		// */
		// doRemoveAssociation(role.getParent(), true);
		// } else {
		/*
		 * store lazy copy of the object before deletion
		 */
		getRevisionStore().createLazyCopy(role);
		/*
		 * remove role
		 */
		getAssociationStore().removeRole(role);
		/*
		 * remove reification
		 */
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(role);
		if (reifier != null) {
			removeTopic(reifier, cascade, revision);
			getReificationStore().removeReification(role);
		}
		/*
		 * remove type
		 */
		getTypedStore().removeType(role);
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(role);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_REMOVED, role.getParent(), null, role);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ROLE_REMOVED, role.getParent(), null, role);
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade) throws TopicMapStoreException {
		removeRole(role, cascade, createRevision());
	}

	/**
	 * Internal method to remove a variant
	 * 
	 * @param variant
	 *            the variant
	 * @param cascade
	 *            cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeVariant(IVariant variant, boolean cascade, IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		getRevisionStore().createLazyCopy(variant);
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(variant);
		/*
		 * remove characteristics
		 */
		getCharacteristicsStore().removeVariant(variant);
		/*
		 * remove scope
		 */
		getScopeStore().removeScope(variant);
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(variant);
		if (reifier != null) {
			removeConstruct(reifier, cascade, revision);
			getReificationStore().removeReification(variant);
		}
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_REMOVED, variant.getParent(), null, variant);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_REMOVED, variant.getParent(), null, variant);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade) throws TopicMapStoreException {
		removeVariant(variant, cascade, createRevision());
	}

	/**
	 * Internal method to return a topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param cascade
	 *            the cascading flag
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeTopic(ITopic topic, boolean cascade, IRevision revision) throws TopicMapStoreException {
		Set<String> topicIds = HashUtil.getHashSet();
		removeTopic(topic, cascade, revision, topicIds);
	}

	/**
	 * Internal method to return a topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param cascade
	 *            the cascading flag
	 * @param revision
	 *            the revision
	 * @param topicIds
	 *            a set containing all topic id to avoid cycle deletion
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private void removeTopic(ITopic topic, boolean cascade, IRevision revision, final Set<String> topicIds) throws TopicMapStoreException {
		if (!cascade && isTopicInUse(topic)) {
			throw new TopicInUseException(topic, "The given topic is in use.");
		}
		topicIds.add(topic.getId());
		/*
		 * store lazy copy
		 */
		getRevisionStore().createLazyCopy(topic);

		/*
		 * remove all instances
		 */
		Set<ITopic> instances = HashUtil.getHashSet(getTopicTypeStore().getDirectInstances(topic));
		for (ITopic instance : instances) {
			if (!topicIds.contains(instance.getId())) {
				removeTopic(instance, cascade, revision, topicIds);
			}
		}

		/*
		 * remove sub types
		 */
		Set<ITopic> subtypes = HashUtil.getHashSet(getTopicTypeStore().getDirectSubtypes(topic));
		for (ITopic subtype : subtypes) {
			if (!topicIds.contains(subtype.getId())) {
				removeTopic(subtype, cascade, revision, topicIds);
			}
		}
		/*
		 * remove from type hierarchy
		 */
		if (!getTopicTypeStore().removeTopic(topic).isEmpty()) {
			throw new TopicMapStoreException("All topic instances or subtypes should already removed!");
		}

		/*
		 * remove typed items
		 */
		Set<ITypeable> typeables = HashUtil.getHashSet(getTypedStore().getTyped(topic));
		for (ITypeable typeable : typeables) {
			removeConstruct(typeable, cascade, revision);
		}
		/*
		 * remove from typed store
		 */
		if (!getTypedStore().removeType(topic).isEmpty()) {
			throw new TopicMapStoreException("All typed items should already removed!");
		}

		/*
		 * remove played associations
		 */
		Set<IAssociation> associations = HashUtil.getHashSet(doReadAssociation(topic));
		for (IAssociation association : associations) {
			removeAssocaition(association, cascade, revision);
		}

		/*
		 * remove scoped items
		 */
		Set<IScope> scopes = HashUtil.getHashSet(getScopeStore().getScopes(topic));
		for (IScope scope : scopes) {
			Set<IScopable> scopables = HashUtil.getHashSet(getScopeStore().getScoped(scope));
			for (IScopable scopable : scopables) {
				removeConstruct(scopable, cascade, revision);
			}
		}
		/*
		 * remove scopes
		 */
		if (!getScopeStore().removeScopes(topic).isEmpty()) {
			throw new TopicMapStoreException("All scoped items should already removed!");
		}

		/*
		 * remove reification
		 */
		getReificationStore().removeReifier(topic);

		/*
		 * remove characteristics
		 */
		Set<ICharacteristics> characteristics = HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(topic));
		for (ICharacteristics characteristic : characteristics) {
			removeConstruct(characteristic, cascade, revision);
		}

		/*
		 * remove the identity
		 */
		getIdentityStore().removeTopic(topic);
		/*
		 * remove as parent
		 */
		getCharacteristicsStore().removeTopic(topic);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_REMOVED, topic.getTopicMap(), null, topic);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_REMOVED, topic.getParent(), null, topic);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade) throws TopicMapStoreException {
		removeTopic(topic, cascade, createRevision());
	}

	/**
	 * Method checks if the topic is used by any topic map relation.
	 * 
	 * @param topic
	 *            the topic to check
	 * @return <code>true</code> if the topic is used as type, reifier etc. ,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isTopicInUse(final ITopic topic) {
		/*
		 * used as theme
		 */
		if (getScopeStore().usedAsTheme(topic)) {
			return true;
		}
		/*
		 * used as super type
		 */
		if (!getTopicTypeStore().getSubtypes(topic).isEmpty()) {
			return true;
		}
		/*
		 * used as topic type
		 */
		if (!getTopicTypeStore().getDirectInstances(topic).isEmpty()) {
			return true;
		}
		/*
		 * used as typed type
		 */
		if (!getTypedStore().getTyped(topic).isEmpty()) {
			return true;
		}

		/*
		 * check if deletion constraints are defined and topic is used as
		 * reifier
		 */
		if (isReificationDeletionRestricted() && getReificationStore().getReified(topic) != null) {
			return true;
		}

		/*
		 * used as role player
		 */
		Set<IAssociationRole> roles = getAssociationStore().getRoles(topic);
		if (existsTmdmSupertypeSubtypeAssociationType() || existsTmdmTypeInstanceAssociationType()) {
			for (IAssociationRole role : roles) {
				if (existsTmdmInstanceRoleType() && role.getType().equals(getTmdmInstanceRoleType())) {
					continue;
				}
				if (existsTmdmSubtypeRoleType() && role.getType().equals(getTmdmSubtypeRoleType())) {
					continue;
				}
				return true;
			}
		} else if (!roles.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws TopicMapStoreException {
		// NOTHING TO DO
	}

	/**
	 * Internal method to remove an item identifier
	 * 
	 * @param c
	 *            the construct
	 * @param itemIdentifier
	 *            the item identifier
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeItemIdentifier(IConstruct c, ILocator itemIdentifier, IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeItemIdentifer(c, itemIdentifier);
		notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null, itemIdentifier);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null, itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		removeItemIdentifier(c, itemIdentifier, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		/*
		 * check if the theme is contained
		 */
		if (getScopeStore().getScope(s).containsTheme(theme)) {
			/*
			 * remove old scope relation
			 */
			IScope oldScope = getScopeStore().removeScope(s);
			/*
			 * get new scope object
			 */
			Collection<ITopic> themes = HashUtil.getHashSet();
			themes.addAll(oldScope.getThemes());
			themes.remove(theme);
			IScope newScope = getScopeStore().getScope(themes);
			/*
			 * set new scope
			 */
			getScopeStore().setScope(s, newScope);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
			/*
			 * store revision
			 */
			storeRevision(createRevision(), TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
		}
	}

	/**
	 * Internal method to remove an subject identifier
	 * 
	 * @param t
	 *            the topic
	 * @param subjectIdentifier
	 *            the subject identifier
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeSubjectIdentifier(ITopic t, ILocator subjectIdentifier, IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeSubjectIdentifier(t, subjectIdentifier);
		notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null, subjectIdentifier);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null, subjectIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		removeSubjectIdentifier(t, subjectIdentifier, createRevision());
	}

	/**
	 * Internal method to remove an subject locator
	 * 
	 * @param t
	 *            the topic
	 * @param subjectLocator
	 *            the subject locator
	 * @param revision
	 *            the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	void removeSubjectLocator(ITopic t, ILocator subjectLocator, IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeSubjectLocator(t, subjectLocator);
		notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null, subjectLocator);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null, subjectLocator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		removeSubjectLocator(t, subjectLocator, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		if (getTopicTypeStore().getSupertypes(t).contains(type)) {
			IRevision revision = createRevision();
			getTopicTypeStore().removeSupertype(t, type);
			if (typeHiearchyAsAssociation() && existsTmdmSupertypeSubtypeAssociationType()) {
				removeSupertypeSubtypeAssociation(t, type, revision);
			}
			notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveType(ITopic t, ITopic type) throws TopicMapStoreException {
		if (getTopicTypeStore().getTypes(t).contains(type)) {
			IRevision revision = createRevision();
			getTopicTypeStore().removeType(t, type);
			if (recognizingTypeInstanceAssociation() && typeHiearchyAsAssociation() && existsTmdmTypeInstanceAssociationType()) {
				removeTypeInstanceAssociation(t, type, revision);
			}
			notifyListeners(TopicMapEventType.TYPE_REMOVED, t, null, type);
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.TYPE_REMOVED, t, null, type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void close() throws TopicMapStoreException {
		super.close();
		getIdentityStore().close();
		getCharacteristicsStore().close();
		getTypedStore().close();
		getScopeStore().close();
		getTopicTypeStore().close();
		getReificationStore().close();
		getAssociationStore().close();
		getRevisionStore().close();

		this.identityStore = null;
		this.characteristicsStore = null;
		this.typedStore = null;
		this.scopeStore = null;
		this.topicTypeStore = null;
		this.reificationStore = null;
		this.associationStore = null;
		this.revisionStore = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void connect() throws TopicMapStoreException {
		super.connect();
		this.identityStore = createIdentityStore(this);
		this.characteristicsStore = createCharacteristicsStore(this, getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING));
		this.typedStore = createTypedStore(this);
		this.scopeStore = createScopeStore(this);
		this.topicTypeStore = createTopicTypeStore(this);
		this.reificationStore = createReificationStore(this);
		this.associationStore = createAssociationStore(this);
		this.revisionStore = createRevisionStore(this);

		this.identity = new InMemoryIdentity(UUID.randomUUID().toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <I extends Index> I getIndex(Class<I> clazz) {

		/*
		 * non-paged indexes
		 */
		if (ITransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.transitiveTypeInstanceIndex == null) {
				this.transitiveTypeInstanceIndex = new InMemoryTransitiveTypeInstanceIndex(this);
			}
			return (I) this.transitiveTypeInstanceIndex;
		} else if (TypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.typeInstanceIndex == null) {
				this.typeInstanceIndex = new InMemoryTypeInstanceIndex(this);
			}
			return (I) this.typeInstanceIndex;
		} else if (ScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.scopedIndex == null) {
				this.scopedIndex = new InMemoryScopedIndex(this);
			}
			return (I) this.scopedIndex;
		} else if (LiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.literalIndex == null) {
				this.literalIndex = new InMemoryLiteralIndex(this);
			}
			return (I) this.literalIndex;
		} else if (IIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.identityIndex == null) {
				this.identityIndex = new InMemoryIdentityIndex(this);
			}
			return (I) this.identityIndex;
		} else if (ISupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.supertypeSubtypeIndex == null) {
				this.supertypeSubtypeIndex = new InMemorySupertypeSubtypeIndex(this);
			}
			return (I) this.supertypeSubtypeIndex;
		} else if (IRevisionIndex.class.isAssignableFrom(clazz)) {
			if (this.revisionIndex == null) {
				this.revisionIndex = new InMemoryRevisionIndex(this);
			}
			return (I) this.revisionIndex;
		}
		/*
		 * paged indexes
		 */
		else if (IPagedTransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTransitiveTypeInstanceIndex == null) {
				this.pagedTransitiveTypeInstanceIndex = new InMemoryPagedTransitiveTypeInstanceIndex(this, getIndex(ITransitiveTypeInstanceIndex.class));
			}
			return (I) this.pagedTransitiveTypeInstanceIndex;
		} else if (IPagedTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTypeInstanceIndex == null) {
				this.pagedTypeInstanceIndex = new InMemoryPagedTypeInstanceIndex(this, getIndex(ITypeInstanceIndex.class));
			}
			return (I) this.pagedTypeInstanceIndex;
		} else if (IPagedSupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedSupertypeSubtypeIndex == null) {
				this.pagedSupertypeSubtypeIndex = new InMemoryPagedSupertypeSubtypeIndex(this, getIndex(ISupertypeSubtypeIndex.class));
			}
			return (I) this.pagedSupertypeSubtypeIndex;
		} else if (IPagedScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedScopedIndex == null) {
				this.pagedScopedIndex = new InMemoryPagedScopeIndex(this, getIndex(IScopedIndex.class));
			}
			return (I) this.pagedScopedIndex;
		} else if (IPagedIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedIdentityIndex == null) {
				this.pagedIdentityIndex = new InMemoryPagedIdentityIndex(this, getIndex(IIdentityIndex.class));
			}
			return (I) this.pagedIdentityIndex;
		} else if (IPagedLiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedLiteralIndex == null) {
				this.pagedLiteralIndex = new InMemoryPagedLiteralIndex(this, getIndex(ILiteralIndex.class));
			}
			return (I) this.pagedLiteralIndex;
		} else if (IPagedConstructIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedConstructIndex == null) {
				this.pagedConstructIndex = new InMemoryPagedConstructIndex(this);
			}
			return (I) this.pagedConstructIndex;
		}
		throw new UnsupportedOperationException("The index class '" + (clazz == null ? "null" : clazz.getCanonicalName())
				+ "' is not supported by the current engine.");
	}

	/**
	 * Create the default name type if not exists.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param revision
	 *            the revision the revision to handle created type
	 * @return the default name type
	 */
	private ITopic createDefaultNameType(ITopicMap topicMap, IRevision revision) {
		/*
		 * get default-name-type
		 */
		ILocator locDefaultNameType = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic defaultNameType = getIdentityStore().bySubjectIdentifier(locDefaultNameType);
		if (defaultNameType == null) {
			defaultNameType = createTopic(topicMap, revision);
			modifySubjectIdentifier(defaultNameType, locDefaultNameType, revision);
		}
		return defaultNameType;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) {
		Set<ITopic> themes = HashUtil.getHashSet();
		IAssociation association = createAssociation(getTopicMap(), getTmdmTypeInstanceAssociationType(), themes, revision);
		createRole(association, getTmdmInstanceRoleType(), instance, revision);
		createRole(association, getTmdmTypeRoleType(), type, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) {
		Set<ITopic> themes = HashUtil.getHashSet();
		IAssociation association = createAssociation(getTopicMap(), getTmdmSupertypeSubtypeAssociationType(), themes, revision);
		createRole(association, getTmdmSubtypeRoleType(), type, revision);
		createRole(association, getTmdmSupertypeRoleType(), supertype, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void removeSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmSupertypeSubtypeAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmSubtypeRoleType()).iterator().next().getPlayer().equals(type)
						&& association.getRoles(getTmdmSupertypeRoleType()).iterator().next().getPlayer().equals(supertype)) {
					removeAssocaition(association, false, revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException("Invalid meta model! Missing supertype or subtype role!", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean removeTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmTypeInstanceAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)
						&& association.getRoles(getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
					removeAssocaition(association, false, revision);
					return true;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException("Invalid meta model! Missing type or instance role!", e);
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmTypeInstanceAssociationType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmTypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmInstanceRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmSupertypeSubtypeAssociationType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmSubtypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmTypeInstanceAssociationType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmTypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmInstanceRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmSupertypeSubtypeAssociationType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean existsTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean existsTmdmSubtypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * Creates the internal characteristic store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * @param xsdString
	 *            the locator of datatype xsd:string
	 * 
	 * @return the characteristicsStore
	 */
	protected CharacteristicsStore createCharacteristicsStore(InMemoryTopicMapStore store, ILocator xsdString) {
		if (this.characteristicsStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new CharacteristicsStore(xsdString);
	}

	/**
	 * Creates the internal scope store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the scopeStore
	 */
	protected ScopeStore createScopeStore(InMemoryTopicMapStore store) {
		if (this.scopeStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new ScopeStore();
	}

	/**
	 * Creates the internal association store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the associationStore
	 */
	protected AssociationStore createAssociationStore(InMemoryTopicMapStore store) {
		if (this.associationStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new AssociationStore();
	}

	/**
	 * Creates the internal identity store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the identityStore
	 */
	protected IdentityStore createIdentityStore(InMemoryTopicMapStore store) {
		if (this.identityStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new IdentityStore(store);
	}

	/**
	 * Creates the internal reification store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the reificationStore
	 */
	protected ReificationStore createReificationStore(InMemoryTopicMapStore store) {
		if (this.reificationStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new ReificationStore(store);
	}

	/**
	 * Creates the internal topic-type hierarchy store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the topicTypeStore
	 */
	protected TopicTypeStore createTopicTypeStore(InMemoryTopicMapStore store) {
		if (this.topicTypeStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new TopicTypeStore(store);
	}

	/**
	 * Creates the internal types store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the typedStore
	 */
	protected TypedStore createTypedStore(InMemoryTopicMapStore store) {
		if (this.typedStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new TypedStore(store);
	}

	/**
	 * Creates the internal revision store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the revision store
	 */
	protected RevisionStore createRevisionStore(InMemoryTopicMapStore store) {
		if (this.revisionStore != null) {
			throw new TopicMapStoreException("Store already initialized!");
		}
		return new RevisionStore(store);
	}

	/**
	 * Returns the internal characteristic store reference.
	 * 
	 * @return the characteristicsStore
	 */
	public CharacteristicsStore getCharacteristicsStore() {
		return this.characteristicsStore;
	}

	/**
	 * Returns the internal scope store reference.
	 * 
	 * @return the scopeStore
	 */
	public ScopeStore getScopeStore() {
		return this.scopeStore;
	}

	/**
	 * Returns the internal association store reference.
	 * 
	 * @return the associationStore
	 */
	public AssociationStore getAssociationStore() {
		return this.associationStore;
	}

	/**
	 * Returns the internal identity store reference.
	 * 
	 * @return the identityStore
	 */
	public IdentityStore getIdentityStore() {
		return this.identityStore;
	}

	/**
	 * Returns the internal reification store reference.
	 * 
	 * @return the reificationStore
	 */
	public ReificationStore getReificationStore() {
		return this.reificationStore;
	}

	/**
	 * Returns the internal topic-type hierarchy store reference.
	 * 
	 * @return the topicTypeStore
	 */
	public TopicTypeStore getTopicTypeStore() {
		return this.topicTypeStore;
	}

	/**
	 * Returns the internal types store reference.
	 * 
	 * @return the typedStore
	 */
	public TypedStore getTypedStore() {
		return this.typedStore;
	}

	/**
	 * Returns the internal revision store
	 * 
	 * @return the revisionStore
	 */
	public RevisionStore getRevisionStore() {
		return this.revisionStore;
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
	public boolean isTransactable() {
		return true;
	}

	/**
	 * Store a change set for the given revision. The change set will be created
	 * by the given arguments.
	 * 
	 * @param revision
	 *            the revision the change set should add to
	 * @param type
	 *            the type of change
	 * @param context
	 *            the context of change
	 * @param newValue
	 *            the new value after change
	 * @param oldValue
	 *            the old value before change
	 */
	public final void storeRevision(final IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		if (supportRevisions()) {
			// getThreadPool().execute(new RevisionNotifier(revisionStore,
			// revision, type, context, newValue, oldValue));
			getRevisionStore().addChange(revision, type, context, newValue, oldValue);
		}
	}

	/**
	 * Creating a new revision object
	 * 
	 * @return the new revision object
	 */
	synchronized IRevision createRevision() {
		if (supportRevisions()) {
			return getRevisionStore().createRevision();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addTaskToThreadPool(Runnable task) {
		if (blocked) {
			queue.add(task);
		} else {
			super.addTaskToThreadPool(task);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws ConcurrentThreadsException {
		synchronized (this) {
			if (blocked) {
				throw new ConcurrentThreadsException("Topic Map Store already blocked!");
			}
			blocked = true;
		}

		queue = new LinkedList<Runnable>();
		while (super.getThreadPool().getActiveCount() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new ConcurrentThreadsException(e);
			}
		}
		synchronized (queue) {
			blocked = false;
			for (Runnable r : queue) {
				addTaskToThreadPool(r);
			}
		}
		queue.clear();
	}
}
