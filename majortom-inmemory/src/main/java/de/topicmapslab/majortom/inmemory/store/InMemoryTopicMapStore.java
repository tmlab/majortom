package de.topicmapslab.majortom.inmemory.store;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.comparator.LocatorByReferenceComparator;
import de.topicmapslab.majortom.comparator.NameByValueComparator;
import de.topicmapslab.majortom.comparator.ScopeComparator;
import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.TopicImpl;
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
import de.topicmapslab.majortom.store.NameMergeCandidate;
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
	public void initialize(Locator topicMapBaseLocator)
			throws TopicMapStoreException {
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
	IAssociation createAssociation(ITopicMap topicMap)
			throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create topic and add to identity store
		 */
		IAssociation a = getConstructFactory().newAssociation(
				new InMemoryIdentity(id), topicMap);
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
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type)
			throws TopicMapStoreException {
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
		 * store revision
		 */
		IRevision rev = createRevision();
		storeRevision(rev, TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
				null);
		storeRevision(rev, TopicMapEventType.TYPE_SET, a, type, null);
		storeRevision(rev, TopicMapEventType.SCOPE_MODIFIED, a, getScopeStore()
				.getEmptyScope(), null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
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
	IAssociation createAssociation(ITopicMap topicMap, ITopic type,
			Collection<ITopic> themes, IRevision revision)
			throws TopicMapStoreException {
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ASSOCIATION_ADDED, topicMap, a,
				null);
		storeRevision(revision, TopicMapEventType.TYPE_SET, a, type, null);
		storeRevision(revision, TopicMapEventType.SCOPE_MODIFIED, a, getScopeStore()
				.getScope(themes), null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, topicMap, a, null);
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type,
			Collection<ITopic> themes) throws TopicMapStoreException {
		return createAssociation(topicMap, type, themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference)
			throws TopicMapStoreException {
		return getIdentityStore().createLocator(reference);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value)
			throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);
		return createName(topic, type, value, null, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = createRevision();
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);
		return createName(topic, type, value, themes, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value)
			throws TopicMapStoreException {
		return createName(topic, type, value, null, createRevision());
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
	 *             thrown if operation fails
	 */
	IName createName(ITopic topic, ITopic type, String value,
			Collection<ITopic> themes, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * check if topics should merge because of same name
		 */
		if (doMergingByTopicName()) {
			NameMergeCandidate candidate = InMemoryMergeUtils
					.detectMergeByNameCandidate(this, topic, type, value,
							themes);
			if (candidate != null) {
				if (!doAutomaticMerging()) {
					throw new ModelConstraintException(
							candidate.getName(),
							"A topic with the same name already exists and the merge-by-name feature is set, but auto-merge is disabled.");
				}
				mergeTopics(topic, candidate.getTopic(), revision);
				return candidate.getName();
			}
		}
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = getConstructFactory().newName(new InMemoryIdentity(id),
				topic);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_ADDED, topic, name, null);
		storeRevision(revision, TopicMapEventType.TYPE_SET, name, type, null);
		storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, name, value,
				null);
		storeRevision(revision, TopicMapEventType.SCOPE_MODIFIED, name,
				getScopeStore().getScope(themes), null);
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, name, null);
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		return createName(topic, type, value, themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value,
				doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING),
				null, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createOccurrence(topic, type, value,
				doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING),
				themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(),
				doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI),
				null, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			ILocator value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(),
				doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI),
				themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, null,
				createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type,
			String value, ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, themes,
				createRevision());
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
	IOccurrence createOccurrence(ITopic topic, ITopic type, String value,
			ILocator datatype, Collection<ITopic> themes, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IOccurrence occurrence = getConstructFactory().newOccurrence(
				new InMemoryIdentity(id), topic);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.OCCURRENCE_ADDED, topic,
				occurrence, null);
		storeRevision(revision, TopicMapEventType.TYPE_SET, occurrence, type,
				null);
		storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, occurrence,
				value, null);
		storeRevision(revision, TopicMapEventType.DATATYPE_SET, occurrence,
				datatype, null);
		storeRevision(revision, TopicMapEventType.SCOPE_MODIFIED, occurrence,
				getScopeStore().getScope(themes), null);
		/*
		 * notify listener
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, occurrence,
				null);
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
	IAssociationRole createRole(IAssociation association, ITopic type,
			ITopic player, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IAssociationRole role = getConstructFactory().newAssociationRole(
				new InMemoryIdentity(id), association);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ROLE_ADDED, association,
				role, null);
		storeRevision(revision, TopicMapEventType.TYPE_SET, role, type, null);
		storeRevision(revision, TopicMapEventType.PLAYER_MODIFIED, role,
				player, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_ADDED, association, role, null);
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association,
			ITopic type, ITopic player) throws TopicMapStoreException {
		return createRole(association, type, player, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes)
			throws TopicMapStoreException {
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
	ITopic createTopic(ITopicMap topicMap, IRevision revision)
			throws TopicMapStoreException {
		return createTopic(topicMap, revision, null, null, null);
	}

	/**
	 * Internal creation method for a topic
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param revision
	 *            the revision
	 * @param subjectIdentifier
	 *            the subject-identifier or <code>null</code>
	 * @param subjectLocator
	 *            the subject-locator or <code>null</code>
	 * @param itemIdentifier
	 *            the item-identifier or <code>null</code>
	 * @return the create topic
	 * @throws TopicMapStoreException
	 */
	ITopic createTopic(ITopicMap topicMap, IRevision revision,
			ILocator subjectIdentifier, ILocator subjectLocator,
			ILocator itemIdentifier) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create topic and add to identity store
		 */
		ITopic t = getConstructFactory().newTopic(new InMemoryIdentity(id),
				topicMap);
		getIdentityStore().setId(t, id);

		/*
		 * add subject identifier
		 */
		if (subjectIdentifier != null) {
			modifySubjectIdentifier(t, subjectIdentifier, null);
		}
		/*
		 * add subject locator
		 */
		if (subjectLocator != null) {
			modifySubjectLocator(t, subjectLocator, null);
		}
		/*
		 * add item identifier
		 */
		if (itemIdentifier != null) {
			modifyItemIdentifier(t, itemIdentifier, null);
		}

		if (revision != null) {
			/*
			 * store as revision
			 */
			storeRevision(revision, TopicMapEventType.TOPIC_ADDED, topicMap, t,
					null);
			/*
			 * add subject identifier
			 */
			if (subjectIdentifier != null) {
				storeRevision(revision,
						TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t,
						subjectIdentifier, null);
			}
			/*
			 * add subject locator
			 */
			if (subjectLocator != null) {
				storeRevision(revision,
						TopicMapEventType.SUBJECT_LOCATOR_ADDED, t,
						subjectLocator, null);
			}
			/*
			 * add item identifier
			 */
			if (itemIdentifier != null) {
				storeRevision(revision,
						TopicMapEventType.ITEM_IDENTIFIER_ADDED, t,
						itemIdentifier, null);
			}
		}
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);

		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap)
			throws TopicMapStoreException {
		return createTopic(topicMap, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap,
			ILocator itemIdentifier) throws TopicMapStoreException {
		return createTopic(topicMap, createRevision(), null, null,
				itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		return createTopic(topicMap, createRevision(), subjectIdentifier, null,
				null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap,
			ILocator subjectLocator) throws TopicMapStoreException {
		return createTopic(topicMap, createRevision(), null, subjectLocator,
				null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		return createVariant(
				name,
				value,
				getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING),
				themes, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, ILocator value,
			Collection<ITopic> themes) throws TopicMapStoreException {
		return createVariant(name, value.toExternalForm(), getIdentityStore()
				.createLocator(XmlSchemeDatatypes.XSD_ANYURI), themes,
				createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value,
			ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createVariant(name, value, datatype, themes, createRevision());
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
	IVariant createVariant(IName name, String value, ILocator datatype,
			Collection<ITopic> themes, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IVariant variant = getConstructFactory().newVariant(
				new InMemoryIdentity(id), name);
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
		 * store as revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_ADDED, name, variant,
				null);
		storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, variant,
				value, null);
		storeRevision(revision, TopicMapEventType.DATATYPE_SET, variant,
				datatype, null);
		storeRevision(revision, TopicMapEventType.SCOPE_MODIFIED, variant,
				scope, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
		return variant;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopicMaps(TopicMap context, TopicMap other)
			throws TopicMapStoreException {
		if (!context.equals(getTopicMap())) {
			throw new TopicMapStoreException(
					"Calling topic map does not belong to the called store.");
		}
		InMemoryMergeUtils.doMergeTopicMaps(this, (ITopicMap) context, other);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.MERGE, getTopicMap(), context, other);

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
	void mergeTopics(ITopic context, ITopic other, IRevision revision)
			throws TopicMapStoreException {
		if (!context.equals(other)) {
			/*
			 * merge into
			 */
			ITopic newTopic = createTopic(getTopicMap(), revision);
			InMemoryMergeUtils.doMerge(this, newTopic, context, revision);
			String oldId = context.getId();
			((InMemoryIdentity) ((TopicImpl) context).getIdentity())
					.setId(newTopic.getId());
			((ConstructImpl) context).setRemoved(false);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					context);
			notifyListeners(TopicMapEventType.ID_MODIFIED, context,
					newTopic.getId(), oldId);

			InMemoryMergeUtils.doMerge(this, newTopic, other, revision);
			oldId = other.getId();
			((InMemoryIdentity) ((TopicImpl) other).getIdentity())
					.setId(newTopic.getId());
			((ConstructImpl) other).setRemoved(false);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic,
					other);
			notifyListeners(TopicMapEventType.ID_MODIFIED, other,
					newTopic.getId(), oldId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other)
			throws TopicMapStoreException {
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
	void modifyItemIdentifier(IConstruct c, ILocator itemIdentifier,
			IRevision revision) throws TopicMapStoreException {
		/*
		 * check if item-identifier causes merging
		 */
		ITopic topic = checkMergeConditionOfItemIdentifier(c, itemIdentifier);
		if (topic != null) {
			mergeTopics((ITopic) c, topic, revision);
		}
		getIdentityStore().addItemIdentifer(c, itemIdentifier);
		/*
		 * if revision is null the method is called by createTopic
		 */
		if (revision != null) {
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.ITEM_IDENTIFIER_ADDED, c,
					itemIdentifier, null);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c,
					itemIdentifier, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
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
	void modifyPlayer(IAssociationRole role, ITopic player, IRevision revision)
			throws TopicMapStoreException {
		ITopic oldValue = getAssociationStore().setPlayer(role, player);
		/*
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.PLAYER_MODIFIED,
				role, player, oldValue);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.PLAYER_MODIFIED, role, player,
				oldValue);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player)
			throws TopicMapStoreException {
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
	void modifyReifier(IReifiable r, ITopic reifier, IRevision revision)
			throws TopicMapStoreException {
		ITopic oldValue = getReificationStore().setReifier(r, reifier);
		if (oldValue != reifier && revision != null) {
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.REIFIER_SET, r, reifier,
					oldValue);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.REIFIER_SET, r, reifier, oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier)
			throws TopicMapStoreException {
		modifyReifier(r, reifier, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		/*
		 * remove old scope relation
		 */
		IScope oldScope = getScopeStore().removeScoped(s);
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
		 * store revision
		 */
		storeRevision(createRevision(), TopicMapEventType.SCOPE_MODIFIED, s,
				newScope, oldScope);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
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
	void modifySubjectIdentifier(ITopic t, ILocator subjectIdentifier,
			IRevision revision) throws TopicMapStoreException {
		/*
		 * check if subject-identifier causes merging
		 */
		ITopic topic = checkMergeConditionOfSubjectIdentifier(t,
				subjectIdentifier);
		if (topic != null) {
			mergeTopics(t, topic, revision);
		}
		getIdentityStore().addSubjectIdentifier(t, subjectIdentifier);
		if (revision != null) {
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.SUBJECT_IDENTIFIER_ADDED,
					t, subjectIdentifier, null);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t,
					subjectIdentifier, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
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
	void modifySubjectLocator(ITopic t, ILocator subjectLocator,
			IRevision revision) throws TopicMapStoreException {
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
				 * store revision
				 */
				storeRevision(revision,
						TopicMapEventType.SUBJECT_LOCATOR_ADDED, t,
						subjectLocator, null);
				/*
				 * notify listeners
				 */
				notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t,
						subjectLocator, null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
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
	void modifySupertype(ITopic t, ITopic type, IRevision revision)
			throws TopicMapStoreException {
		if (!getTopicTypeStore().getSupertypes(t).contains(type)) {
			getTopicTypeStore().addSupertype(t, type);
			/*
			 * create type-hierarchy as association
			 */
			if (recognizingSupertypeSubtypeAssociation()) {
				createSupertypeSubtypeAssociation(t, type, revision);
			}
			/*
			 * store revision
			 */
			if (revision != null) {
				storeRevision(revision, TopicMapEventType.SUPERTYPE_ADDED, t,
						type, null);
			}

			notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		modifySupertype(t, type, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag)
			throws TopicMapStoreException {
		getRevisionStore().addTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp)
			throws TopicMapStoreException {
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
	void modifyType(ITypeable t, ITopic type, IRevision revision)
			throws TopicMapStoreException {
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
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.TYPE_SET, t, type,
					oldType);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.TYPE_SET, t, type, oldType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type)
			throws TopicMapStoreException {
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
	void modifyTopicType(ITopic t, ITopic type, IRevision revision)
			throws TopicMapStoreException {
		if (!getTopicTypeStore().getTypes(t).contains(type)) {
			getTopicTypeStore().addType(t, type);
			if (recognizingTypeInstanceAssociation()) {
				createTypeInstanceAssociation(t, type, revision);
			}
			if (revision != null) {
				/*
				 * store revision
				 */
				storeRevision(revision, TopicMapEventType.TYPE_ADDED, t, type,
						null);
			}
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTopicType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		modifyTopicType(t, type, createRevision());
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
	void modifyValue(IDatatypeAware c, Object value, ILocator datatype,
			IRevision revision) throws TopicMapStoreException {
		/*
		 * modify the value of the characteristics
		 */
		Object oldValue = getCharacteristicsStore().setValue(c, value);
		/*
		 * modify the data type of the characteristics
		 */
		ILocator oldDataType = getCharacteristicsStore().setDatatype(c,
				datatype);
		if (revision != null) {
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, c, value,
					oldValue);
			storeRevision(revision, TopicMapEventType.DATATYPE_SET, c,
					datatype, oldDataType);

			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, c, value,
					oldValue);
			notifyListeners(TopicMapEventType.DATATYPE_SET, c, datatype,
					oldDataType);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value)
			throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(
				XmlSchemeDatatypes.XSD_STRING);
		modifyValue(c, value, datatype, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value,
			ILocator datatype) throws TopicMapStoreException {
		modifyValue(c, value, datatype, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, Object value)
			throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(
				XmlSchemeDatatypes.javaToXsd(value.getClass()));
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
	void modifyValue(IName n, String value, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * modify the value of the characteristics
		 */
		Object oldValue = getCharacteristicsStore().setValue(n, value);
		if (revision != null) {
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.VALUE_MODIFIED, n, value,
					oldValue);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.VALUE_MODIFIED, n, value,
					oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value)
			throws TopicMapStoreException {
		modifyValue(n, value, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyMetaData(IRevision revision, String key, String value)
			throws TopicMapStoreException {
		getRevisionStore().addMetaData(revision, key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t)
			throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		for (IAssociationRole r : getAssociationStore().getRoles(t)) {
			associations.add(r.getParent());
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type)
			throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(doReadAssociation(t));
		/*
		 * filter by type
		 */
		associations.retainAll(getTypedStore().getTyped(type));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(doReadAssociation(t));
		/*
		 * filter by type
		 */
		associations.retainAll(getTypedStore().getTyped(type));
		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope)
			throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(doReadAssociation(t));

		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm)
			throws TopicMapStoreException {
		return HashUtil.getHashSet(getAssociationStore().getAssociations());
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type)
			throws TopicMapStoreException {

		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(getAssociationStore().getAssociations());
		/*
		 * filter by type
		 */
		associations.retainAll(getTypedStore().getTyped(type));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type,
			IScope scope) throws TopicMapStoreException {
		/*
		 * get all associations of the given type
		 */
		Set<IAssociation> associations = doReadAssociation(tm, type);
		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope)
			throws TopicMapStoreException {
		/*
		 * get all associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(getAssociationStore().getAssociations());
		/*
		 * filter by scope
		 */
		associations.retainAll(getScopeStore().getScoped(scope));
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		return getRevisionStore().getChangeset(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t)
			throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore()
				.getCharacteristics(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type)
			throws TopicMapStoreException {
		/*
		 * get all characteristics
		 */
		Set<ICharacteristics> characteristics = HashUtil
				.getHashSet(getCharacteristicsStore().getCharacteristics(t));
		/*
		 * filter by type
		 */
		characteristics.retainAll(getTypedStore().getTyped(type));
		if (characteristics.isEmpty()) {
			return Collections.emptySet();
		}
		return characteristics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		/*
		 * get all characteristics
		 */
		Set<ICharacteristics> characteristics = doReadCharacteristics(t, type);
		/*
		 * filter by scope
		 */
		characteristics.retainAll(getScopeStore().getScoped(scope));
		if (characteristics.isEmpty()) {
			return Collections.emptySet();
		}
		return characteristics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope)
			throws TopicMapStoreException {
		/*
		 * get characteristics of the topic
		 */
		Set<ICharacteristics> set = HashUtil
				.getHashSet(getCharacteristicsStore().getCharacteristics(t));
		/*
		 * filter by scope
		 */
		set.retainAll(getScopeStore().getScoped(scope));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, String id)
			throws TopicMapStoreException {
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
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier)
			throws TopicMapStoreException {
		return getIdentityStore().byItemIdentifier(itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware c)
			throws TopicMapStoreException {
		return getCharacteristicsStore().getDatatype(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		if (c instanceof ITopicMap) {
			return this.identity.getId();
		} else if (c instanceof ConstructImpl) {
			ITopicMapStoreIdentity identity = ((ConstructImpl) c).getIdentity();
			return identity.getId();
		}
		throw new TopicMapStoreException(
				"IConstruct created by external instance.");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadItemIdentifiers(IConstruct c)
			throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet(getIdentityStore()
				.getItemIdentifiers(c));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		Set<IName> set = HashUtil.getHashSet(getCharacteristicsStore()
				.getNames(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type)
			throws TopicMapStoreException {
		/*
		 * get all names
		 */
		Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore()
				.getNames(t));
		/*
		 * filter by type
		 */
		names.retainAll(getTypedStore().getTyped(type));
		if (names.isEmpty()) {
			return Collections.emptySet();
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope)
			throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IName> set = doReadNames(t, type);
		/*
		 * filter by scope
		 */
		set.retainAll(getScopeStore().getScoped(scope));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, IScope scope)
			throws TopicMapStoreException {
		/*
		 * get all names
		 */
		Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore()
				.getNames(t));
		/*
		 * filter by scope
		 */
		names.retainAll(getScopeStore().getScoped(scope));
		if (names.isEmpty()) {
			return Collections.emptySet();
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFutureRevision(IRevision r)
			throws TopicMapStoreException {
		return getRevisionStore().getNextRevision(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t)
			throws TopicMapStoreException {
		Set<IOccurrence> occurrences = HashUtil
				.getHashSet(getCharacteristicsStore().getOccurrences(t));
		if (occurrences.isEmpty()) {
			return Collections.emptySet();
		}
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type)
			throws TopicMapStoreException {

		/*
		 * get all occurrence
		 */
		Set<IOccurrence> occurrences = HashUtil
				.getHashSet(getCharacteristicsStore().getOccurrences(t));
		/*
		 * filter by type
		 */
		occurrences.retainAll(getTypedStore().getTyped(type));
		if (occurrences.isEmpty()) {
			return Collections.emptySet();
		}
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IOccurrence> set = doReadOccurrences(t, type);
		/*
		 * filter by scope
		 */
		set.retainAll(getScopeStore().getScoped(scope));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope)
			throws TopicMapStoreException {
		/*
		 * get all occurrence
		 */
		Set<IOccurrence> occurrences = HashUtil
				.getHashSet(getCharacteristicsStore().getOccurrences(t));
		/*
		 * filter by scope
		 */
		occurrences.retainAll(getScopeStore().getScoped(scope));
		if (occurrences.isEmpty()) {
			return Collections.emptySet();
		}
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadPlayer(IAssociationRole role)
			throws TopicMapStoreException {
		return getAssociationStore().getPlayer(role);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(IRevision r)
			throws TopicMapStoreException {
		return getRevisionStore().getPastRevision(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		return getReificationStore().getReified(t);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		return getReificationStore().getReifier(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadRevisionTimestamp(IRevision r)
			throws TopicMapStoreException {
		return getRevisionStore().getRevisionTimestamp(r);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadRoleTypes(IAssociation association)
			throws TopicMapStoreException {
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
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association)
			throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet(getAssociationStore()
				.getRoles(association));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association,
			ITopic type) throws TopicMapStoreException {
		/*
		 * get all roles
		 */
		Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore()
				.getRoles(association));
		/*
		 * filter by type
		 */
		roles.retainAll(getTypedStore().getTyped(type));
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player)
			throws TopicMapStoreException {
		Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore()
				.getRoles(player));
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type)
			throws TopicMapStoreException {
		/*
		 * get all roles
		 */
		Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore()
				.getRoles(player));
		/*
		 * filter by type
		 */
		roles.retainAll(getTypedStore().getTyped(type));
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type,
			ITopic assocType) throws TopicMapStoreException {
		/*
		 * create result set
		 */
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (IAssociationRole role : doReadRoles(player, type)) {
			/*
			 * check association type
			 */
			if (assocType.equals(doReadType(role.getParent()))) {
				set.add(role);
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doReadScope(IScopable s) throws TopicMapStoreException {
		return getScopeStore().getScope(s);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t)
			throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet(getIdentityStore()
				.getSubjectIdentifiers(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectLocators(ITopic t)
			throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet(getIdentityStore()
				.getSubjectLocators(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadSuptertypes(ITopic t)
			throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet(getTopicTypeStore()
				.getSupertypes(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		return getIdentityStore().bySubjectIdentifier(subjectIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t,
			ILocator subjectLocator) throws TopicMapStoreException {
		return getIdentityStore().bySubjectLocator(subjectLocator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet(getIdentityStore().getTopics());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type)
			throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet(getTopicTypeStore()
				.getDirectInstances(type));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		return getTypedStore().getType(typed);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		Set<ITopic> types = HashUtil.getHashSet(getTopicTypeStore()
				.getDirectTypes(t));
		if (types.isEmpty()) {
			return Collections.emptySet();
		}
		return types;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware c) throws TopicMapStoreException {
		return getCharacteristicsStore().getValueAsString(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T doReadValue(IDatatypeAware c, Class<T> type)
			throws TopicMapStoreException {
		Object obj = getCharacteristicsStore().getValue(c);
		try {
			return (T) DatatypeAwareUtils.toValue(obj, type);
		} catch (Exception e) {
			throw new TopicMapStoreException(
					"Cannot convert characteristics value to given type!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws TopicMapStoreException {
		return getCharacteristicsStore().getValue(n);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		Set<IVariant> set = HashUtil.getHashSet(getCharacteristicsStore()
				.getVariants(n));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope)
			throws TopicMapStoreException {
		Set<IVariant> variants = HashUtil.getHashSet(getCharacteristicsStore()
				.getVariants(n));
		/*
		 * filter by scope
		 */
		variants.retainAll(getScopeStore().getScoped(scope));
		if (variants.isEmpty()) {
			return Collections.emptySet();
		}
		return variants;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetaData(IRevision revision)
			throws TopicMapStoreException {
		return getRevisionStore().getMetaData(revision);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetaData(IRevision revision, String key)
			throws TopicMapStoreException {
		return getRevisionStore().getMetaData(revision, key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<IName> names = getCharacteristicsStore().getNames(topic);
		if (!names.isEmpty()) {
			return readBestName(topic, names);
		}
		return readBestIdentifier(topic);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<IName> names = getCharacteristicsStore().getNames(topic);
		if (!names.isEmpty()) {
			return readBestName(topic, theme, names);
		}
		return readBestIdentifier(topic);
	}	

	/**
	 * Internal best label method only check name attributes.
	 * 
	 * @param topic
	 *            the topic
	 *            @param theme the theme
	 * @param set
	 *            the non-empty set of names
	 * @return the best name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private String readBestName(ITopic topic, ITopic theme,  Set<IName> names)
			throws TopicMapStoreException {
		
		List<IScope> scopes = HashUtil.getList(getScopeStore().getScopes(theme));
		/*
		 * sort scopes by number of themes
		 */
		Collections.sort(scopes, ScopeComparator.getInstance(true));
		for (IScope s : scopes) {
			/*
			 * get names of the scope and topic
			 */
			Set<IName> tmp = HashUtil.getHashSet(names);
			tmp.retainAll(getScopeStore().getScopedNames(s));
			/*
			 * only one name of the current scope
			 */
			if (tmp.size() == 1) {
				return tmp.iterator().next().getValue();
			}
			/*
			 * more than one name
			 */
			else if (tmp.size() > 1) {
				names = tmp;
				break;
			}
		}		
		return readBestName(topic, names);
	}
	
	/**
	 * Internal best label method only check name attributes.
	 * 
	 * @param topic
	 *            the topic
	 * @param set
	 *            the non-empty set of names
	 * @return the best name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private String readBestName(ITopic topic, Set<IName> names)
			throws TopicMapStoreException {
		/*
		 * check if default name type exists
		 */
		if (existsTmdmDefaultNameType()) {
			Set<IName> tmp = HashUtil.getHashSet(names);
			tmp.retainAll(getTypedStore().getTypedNames(
					getTmdmDefaultNameType()));
			/*
			 * return the default name
			 */
			if (tmp.size() == 1) {
				return tmp.iterator().next().getValue();
			}
			/*
			 * more than one default name
			 */
			else if (tmp.size() > 1) {
				names = tmp;
			}
		}
		/*
		 * filter by scoping themes
		 */
		List<IScope> scopes = HashUtil.getList(getScopeStore().getNameScopes());
		scopes.add(getScopeStore().getEmptyScope());
		if (!scopes.isEmpty()) {
			/*
			 * sort scopes by number of themes
			 */
			Collections.sort(scopes, ScopeComparator.getInstance(true));
			for (IScope s : scopes) {
				/*
				 * get names of the scope and topic
				 */
				Set<IName> tmp = HashUtil.getHashSet(names);
				tmp.retainAll(getScopeStore().getScopedNames(s));
				/*
				 * only one name of the current scope
				 */
				if (tmp.size() == 1) {
					return tmp.iterator().next().getValue();
				}
				/*
				 * more than one name
				 */
				else if (tmp.size() > 1) {
					names = tmp;
					break;
				}
			}
		}
		/*
		 * sort by value
		 */
		List<IName> list = HashUtil.getList(names);
		Collections.sort(list, NameByValueComparator.getInstance(true));
		return list.get(0).getValue();
	}

	/**
	 * Internal best label method only check identifier attribute.
	 * 
	 * @param topic
	 *            the topic
	 * @return the best identifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private String readBestIdentifier(ITopic topic)
			throws TopicMapStoreException {
		Set<ILocator> set = getIdentityStore().getSubjectIdentifiers(topic);
		if (set.isEmpty()) {
			set = getIdentityStore().getSubjectLocators(topic);
			if (set.isEmpty()) {
				set = getIdentityStore().getItemIdentifiers(topic);
				if (set.isEmpty()) {
					return topic.getId();
				}
			}
		}
		List<ILocator> list = HashUtil.getList(set);
		Collections.sort(list, LocatorByReferenceComparator.getInstance(true));
		return list.iterator().next().getReference();
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
	void removeAssociation(IAssociation association, boolean cascade,
			IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		if (isRevisionManagementEnabled()) {
			getRevisionStore().createLazyCopy(association);
		}
		/*
		 * remove roles
		 */
		Set<IAssociationRole> roles = HashUtil.getHashSet(getAssociationStore()
				.getRoles(association));
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
		getScopeStore().removeScoped(association);
		/*
		 * remove type
		 */
		getTypedStore().removeType(association);
		/*
		 * remove construct
		 */
		getIdentityStore().removeConstruct(association);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ASSOCIATION_REMOVED,
				association.getParent(), null, association);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED,
				association.getParent(), null, association);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade)
			throws TopicMapStoreException {
		removeAssociation(association, cascade, createRevision());
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
	void removeConstruct(IConstruct construct, boolean cascade,
			IRevision revision) throws TopicMapStoreException {
		if (construct instanceof ITopic) {
			removeTopic((ITopic) construct, cascade, revision);
		} else if (construct instanceof IName) {
			removeName((IName) construct, cascade, revision);
		} else if (construct instanceof IOccurrence) {
			removeOccurrence((IOccurrence) construct, cascade, revision);
		} else if (construct instanceof IAssociation) {
			removeAssociation((IAssociation) construct, cascade, revision);
		} else if (construct instanceof IAssociationRole) {
			removeRole((IAssociationRole) construct, cascade, revision);
		} else if (construct instanceof IVariant) {
			removeVariant((IVariant) construct, cascade, revision);
		} else {
			throw new TopicMapStoreException(
					"Calling method removeConstruct() with an instance of "
							+ construct.getClass().getSimpleName()
							+ " not expected!");
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
	void removeName(IName name, boolean cascade, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		if (isRevisionManagementEnabled()) {
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
		getScopeStore().removeScoped(name);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.NAME_REMOVED,
				name.getParent(), null, name);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_REMOVED, name.getParent(), null,
				name);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade)
			throws TopicMapStoreException {
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
	void removeOccurrence(IOccurrence occurrence, boolean cascade,
			IRevision revision) throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		if (isRevisionManagementEnabled()) {
			getRevisionStore().createLazyCopy(occurrence);
		}
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
		getScopeStore().removeScoped(occurrence);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.OCCURRENCE_REMOVED,
				occurrence.getParent(), null, occurrence);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED,
				occurrence.getParent(), null, occurrence);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade)
			throws TopicMapStoreException {
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
	void removeRole(IAssociationRole role, boolean cascade, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * store lazy copy of the object before deletion
		 */
		if (isRevisionManagementEnabled()) {
			getRevisionStore().createLazyCopy(role);
		}
		/*
		 * remove role
		 */
		getAssociationStore().removeRole(role);
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ROLE_REMOVED,
				role.getParent(), null, role);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_REMOVED, role.getParent(), null,
				role);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade)
			throws TopicMapStoreException {
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
	void removeVariant(IVariant variant, boolean cascade, IRevision revision)
			throws TopicMapStoreException {
		/*
		 * store lazy copy
		 */
		if (isRevisionManagementEnabled()) {
			getRevisionStore().createLazyCopy(variant);
		}
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
		getScopeStore().removeScoped(variant);
		/*
		 * remove reification
		 */
		ITopic reifier = getReificationStore().getReifier(variant);
		if (reifier != null) {
			removeConstruct(reifier, cascade, revision);
			getReificationStore().removeReification(variant);
		}
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.VARIANT_REMOVED,
				variant.getParent(), null, variant);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_REMOVED, variant.getParent(),
				null, variant);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade)
			throws TopicMapStoreException {
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
	void removeTopic(ITopic topic, boolean cascade, IRevision revision)
			throws TopicMapStoreException {
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
	private void removeTopic(ITopic topic, boolean cascade, IRevision revision,
			final Set<String> topicIds) throws TopicMapStoreException {
		if (!cascade && isTopicInUse(topic)) {
			throw new TopicInUseException(topic, "The given topic is in use.");
		}
		topicIds.add(topic.getId());
		/*
		 * store lazy copy
		 */
		if (isRevisionManagementEnabled()) {
			getRevisionStore().createLazyCopy(topic);
		}

		/*
		 * remove all instances
		 */
		Set<ITopic> instances = HashUtil.getHashSet(getTopicTypeStore()
				.getDirectInstances(topic));
		for (ITopic instance : instances) {
			if (!topicIds.contains(instance.getId())) {
				removeTopic(instance, cascade, revision, topicIds);
			}
		}

		/*
		 * remove sub types
		 */
		Set<ITopic> subtypes = HashUtil.getHashSet(getTopicTypeStore()
				.getDirectSubtypes(topic));
		for (ITopic subtype : subtypes) {
			if (!topicIds.contains(subtype.getId())) {
				removeTopic(subtype, cascade, revision, topicIds);
			}
		}
		/*
		 * remove from type hierarchy
		 */
		if (!getTopicTypeStore().removeTopic(topic).isEmpty()) {
			throw new TopicMapStoreException(
					"All topic instances or subtypes should already removed!");
		}

		/*
		 * remove typed items
		 */
		Set<ITypeable> typeables = HashUtil.getHashSet(getTypedStore()
				.getTyped(topic));
		for (ITypeable typeable : typeables) {
			removeConstruct(typeable, cascade, revision);
		}
		/*
		 * remove from typed store
		 */
		if (!getTypedStore().removeType(topic).isEmpty()) {
			throw new TopicMapStoreException(
					"All typed items should already removed!");
		}

		/*
		 * remove played associations
		 */
		Set<IAssociation> associations = HashUtil
				.getHashSet(doReadAssociation(topic));
		for (IAssociation association : associations) {
			removeAssociation(association, cascade, revision);
		}

		/*
		 * remove scoped items
		 */
		Set<IScope> scopes = HashUtil.getHashSet(getScopeStore().getScopes(
				topic));
		for (IScope scope : scopes) {
			Set<IScopable> scopables = HashUtil.getHashSet(getScopeStore()
					.getScoped(scope));
			for (IScopable scopable : scopables) {
				removeConstruct(scopable, cascade, revision);
			}
		}

		/*
		 * remove scopes
		 */
		if (!getScopeStore().removeScopes(topic).isEmpty()) {
			throw new TopicMapStoreException(
					"All scoped items should already removed!");
		}

		/*
		 * remove reification
		 */
		getReificationStore().removeReifier(topic);

		/*
		 * remove characteristics
		 */
		Set<ICharacteristics> characteristics = HashUtil
				.getHashSet(getCharacteristicsStore().getCharacteristics(topic));
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
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.TOPIC_REMOVED,
				topic.getParent(), null, topic);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_REMOVED, topic.getTopicMap(),
				null, topic);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade)
			throws TopicMapStoreException {
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
		if (isReificationDeletionRestricted()
				&& getReificationStore().getReified(topic) != null) {
			return true;
		}

		/*
		 * used as role player
		 */
		Set<IAssociationRole> roles = getAssociationStore().getRoles(topic);
		/*
		 * ignore TMDM type-instance and supertype-subtype association
		 */
		if (existsTmdmSupertypeSubtypeAssociationType()
				|| existsTmdmTypeInstanceAssociationType()) {
			for (IAssociationRole role : roles) {
				if (existsTmdmInstanceRoleType()
						&& role.getType().equals(getTmdmInstanceRoleType())) {
					continue;
				}
				if (existsTmdmSubtypeRoleType()
						&& role.getType().equals(getTmdmSubtypeRoleType())) {
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
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade)
			throws TopicMapStoreException {
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
	void removeItemIdentifier(IConstruct c, ILocator itemIdentifier,
			IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeItemIdentifer(c, itemIdentifier);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c,
				null, itemIdentifier);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null,
				itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier)
			throws TopicMapStoreException {
		removeItemIdentifier(c, itemIdentifier, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveScope(IScopable s, ITopic theme)
			throws TopicMapStoreException {
		/*
		 * check if the theme is contained
		 */
		if (getScopeStore().getScope(s).containsTheme(theme)) {
			/*
			 * remove old scope relation
			 */
			IScope oldScope = getScopeStore().removeScoped(s);
			/*
			 * get new scope object
			 */
			Collection<ITopic> themes = HashUtil.getHashSet(oldScope
					.getThemes());
			themes.remove(theme);
			IScope newScope = getScopeStore().getScope(themes);
			/*
			 * set new scope
			 */
			getScopeStore().setScope(s, newScope);
			/*
			 * store revision
			 */
			storeRevision(createRevision(), TopicMapEventType.SCOPE_MODIFIED,
					s, newScope, oldScope);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, newScope,
					oldScope);
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
	void removeSubjectIdentifier(ITopic t, ILocator subjectIdentifier,
			IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeSubjectIdentifier(t, subjectIdentifier);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED,
				t, null, subjectIdentifier);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null,
				subjectIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t,
			ILocator subjectIdentifier) throws TopicMapStoreException {
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
	void removeSubjectLocator(ITopic t, ILocator subjectLocator,
			IRevision revision) throws TopicMapStoreException {
		getIdentityStore().removeSubjectLocator(t, subjectLocator);
		/*
		 * store revision
		 */
		storeRevision(revision, TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t,
				null, subjectLocator);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null,
				subjectLocator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator)
			throws TopicMapStoreException {
		removeSubjectLocator(t, subjectLocator, createRevision());
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO move association handling to topicTypeStore
	protected void doRemoveSupertype(ITopic t, ITopic type)
			throws TopicMapStoreException {
		if (getTopicTypeStore().getSupertypes(t).contains(type)) {
			IRevision revision = createRevision();
			getTopicTypeStore().removeSupertype(t, type);
			if (recognizingSupertypeSubtypeAssociation()
					&& existsTmdmSupertypeSubtypeAssociationType()) {
				removeSupertypeSubtypeAssociation(t, type, revision);
			}
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.SUPERTYPE_REMOVED, t,
					null, type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO move association handling to topicTypeStore
	protected void doRemoveType(ITopic t, ITopic type)
			throws TopicMapStoreException {
		if (getTopicTypeStore().getTypes(t).contains(type)) {
			IRevision revision = createRevision();
			getTopicTypeStore().removeType(t, type);
			if (recognizingTypeInstanceAssociation()
					&& existsTmdmTypeInstanceAssociationType()) {
				removeTypeInstanceAssociation(t, type, revision);
			}
			/*
			 * store revision
			 */
			storeRevision(revision, TopicMapEventType.TYPE_REMOVED, t, null,
					type);
			/*
			 * notify listener
			 */
			notifyListeners(TopicMapEventType.TYPE_REMOVED, t, null, type);
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
		this.characteristicsStore = createCharacteristicsStore(this,
				getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING));
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
				this.transitiveTypeInstanceIndex = new InMemoryTransitiveTypeInstanceIndex(
						this);
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
				this.supertypeSubtypeIndex = new InMemorySupertypeSubtypeIndex(
						this);
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
		else if (IPagedTransitiveTypeInstanceIndex.class
				.isAssignableFrom(clazz)) {
			if (this.pagedTransitiveTypeInstanceIndex == null) {
				this.pagedTransitiveTypeInstanceIndex = new InMemoryPagedTransitiveTypeInstanceIndex(
						this, getIndex(ITransitiveTypeInstanceIndex.class));
			}
			return (I) this.pagedTransitiveTypeInstanceIndex;
		} else if (IPagedTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedTypeInstanceIndex == null) {
				this.pagedTypeInstanceIndex = new InMemoryPagedTypeInstanceIndex(
						this, getIndex(ITypeInstanceIndex.class));
			}
			return (I) this.pagedTypeInstanceIndex;
		} else if (IPagedSupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedSupertypeSubtypeIndex == null) {
				this.pagedSupertypeSubtypeIndex = new InMemoryPagedSupertypeSubtypeIndex(
						this, getIndex(ISupertypeSubtypeIndex.class));
			}
			return (I) this.pagedSupertypeSubtypeIndex;
		} else if (IPagedScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedScopedIndex == null) {
				this.pagedScopedIndex = new InMemoryPagedScopeIndex(this,
						getIndex(IScopedIndex.class));
			}
			return (I) this.pagedScopedIndex;
		} else if (IPagedIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedIdentityIndex == null) {
				this.pagedIdentityIndex = new InMemoryPagedIdentityIndex(this,
						getIndex(IIdentityIndex.class));
			}
			return (I) this.pagedIdentityIndex;
		} else if (IPagedLiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedLiteralIndex == null) {
				this.pagedLiteralIndex = new InMemoryPagedLiteralIndex(this,
						getIndex(ILiteralIndex.class));
			}
			return (I) this.pagedLiteralIndex;
		} else if (IPagedConstructIndex.class.isAssignableFrom(clazz)) {
			if (this.pagedConstructIndex == null) {
				this.pagedConstructIndex = new InMemoryPagedConstructIndex(this);
			}
			return (I) this.pagedConstructIndex;
		}
		throw new UnsupportedOperationException("The index class '"
				+ (clazz == null ? "null" : clazz.getCanonicalName())
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
		ILocator locDefaultNameType = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic defaultNameType = getIdentityStore().bySubjectIdentifier(
				locDefaultNameType);
		if (defaultNameType == null) {
			defaultNameType = createTopic(topicMap, revision);
			modifySubjectIdentifier(defaultNameType, locDefaultNameType,
					revision);
		}
		return defaultNameType;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createTypeInstanceAssociation(ITopic instance, ITopic type,
			IRevision revision) {
		Set<ITopic> themes = HashUtil.getHashSet();
		IAssociation association = createAssociation(getTopicMap(),
				getTmdmTypeInstanceAssociationType(), themes, revision);
		createRole(association, getTmdmInstanceRoleType(), instance, revision);
		createRole(association, getTmdmTypeRoleType(), type, revision);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void createSupertypeSubtypeAssociation(ITopic type,
			ITopic supertype, IRevision revision) {
		Set<ITopic> themes = HashUtil.getHashSet();
		IAssociation association = createAssociation(getTopicMap(),
				getTmdmSupertypeSubtypeAssociationType(), themes, revision);
		createRole(association, getTmdmSubtypeRoleType(), type, revision);
		createRole(association, getTmdmSupertypeRoleType(), supertype, revision);
	}

	/**
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
					removeAssociation(association, false, revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException(
						"Invalid meta model! Missing supertype or subtype role!",
						e);
			}
		}
	}

	/**
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
					removeAssociation(association, false, revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException(
						"Invalid meta model! Missing type or instance role!", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmTypeInstanceAssociationType()
			throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
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
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
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
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmSupertypeSubtypeAssociationType()
			throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
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
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
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
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getTmdmDefaultNameType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic topic = getIdentityStore().bySubjectIdentifier(loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmTypeInstanceAssociationType()
			throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmTypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmInstanceRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmSupertypeSubtypeAssociationType()
			throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmSubtypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmDefaultNameType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(
				TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
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
	protected CharacteristicsStore createCharacteristicsStore(
			InMemoryTopicMapStore store, ILocator xsdString) {
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
	protected AssociationStore createAssociationStore(
			InMemoryTopicMapStore store) {
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
	protected ReificationStore createReificationStore(
			InMemoryTopicMapStore store) {
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
	 * 
	 * {@inheritDoc}
	 */
	public final void storeRevision(final IRevision revision,
			TopicMapEventType type, IConstruct context, Object newValue,
			Object oldValue) {
		if (isRevisionManagementEnabled()) {
			getRevisionStore().addChange(revision, type, context, newValue,
					oldValue);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected synchronized IRevision createRevision() {
		if (isRevisionManagementEnabled()) {
			return getRevisionStore().createRevision();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void addTaskToThreadPool(Runnable task) {
		// NOTHING TO DO
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		// NOTHING TO DO
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		InMemoryMergeUtils.removeDuplicates(this, getTopicMap());
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		getIdentityStore().close();
		getCharacteristicsStore().close();
		getTypedStore().close();
		getScopeStore().close();
		getTopicTypeStore().close();
		getReificationStore().close();
		getAssociationStore().close();
		getRevisionStore().close();

		this.identityStore = createIdentityStore(this);
		this.characteristicsStore = createCharacteristicsStore(this,
				getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING));
		this.typedStore = createTypedStore(this);
		this.scopeStore = createScopeStore(this);
		this.topicTypeStore = createTopicTypeStore(this);
		this.reificationStore = createReificationStore(this);
		this.associationStore = createAssociationStore(this);
		this.revisionStore = createRevisionStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableCaching(boolean enable) {
		// NOTHING TO DO
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCachingEnabled() {
		return false;
	}
}
