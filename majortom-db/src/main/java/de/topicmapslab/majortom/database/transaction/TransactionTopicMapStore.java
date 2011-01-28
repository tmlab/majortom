package de.topicmapslab.majortom.database.transaction;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.comparator.NameByValueComparator;
import de.topicmapslab.majortom.comparator.ScopeComparator;
import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.database.store.JdbcIdentity;
import de.topicmapslab.majortom.database.transaction.cache.AssociationCache;
import de.topicmapslab.majortom.database.transaction.cache.CharacteristicsCache;
import de.topicmapslab.majortom.database.transaction.cache.IdentityCache;
import de.topicmapslab.majortom.database.transaction.cache.ReificationCache;
import de.topicmapslab.majortom.database.transaction.cache.ScopeCache;
import de.topicmapslab.majortom.database.transaction.cache.TopicTypeCache;
import de.topicmapslab.majortom.database.transaction.cache.TypedCache;
import de.topicmapslab.majortom.database.transaction.index.TransactionIdentityIndex;
import de.topicmapslab.majortom.database.transaction.index.TransactionLiteralIndex;
import de.topicmapslab.majortom.database.transaction.index.TransactionScopedIndex;
import de.topicmapslab.majortom.database.transaction.index.TransactionSupertypeSubtypeIndex;
import de.topicmapslab.majortom.database.transaction.index.TransactionTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.database.transaction.index.TransactionTypeInstanceIndex;
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
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.model.transaction.ITransactionTopicMapStore;
import de.topicmapslab.majortom.store.MergeUtils;
import de.topicmapslab.majortom.store.ModifableTopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

public class TransactionTopicMapStore extends ModifableTopicMapStoreImpl implements ITransactionTopicMapStore {
	/**
	 * 
	 */
	protected static final String SUBJECTIDENTIFIER_PREFIX = "si:";
	/**
	 * 
	 */
	protected static final String SUBJECTLOCATOR_PREFIX = "sl:";
	/**
	 * 
	 */
	protected static final String ITEMIDENTIFIER_PREFIX = "ii:";
	/**
	 * 
	 */
	private static final String ID_PREFIX = "id:";
	private List<TransactionCommand> commands = new LinkedList<TransactionCommand>();
	private List<TransactionCommand> commited = new LinkedList<TransactionCommand>();
	private final ModifableTopicMapStoreImpl store;
	private final ITransaction transaction;

	private IdentityCache identityStore;
	private CharacteristicsCache characteristicsStore;
	private TypedCache typedStore;
	private ScopeCache scopeStore;
	private TopicTypeCache topicTypeCache;
	private ReificationCache reificationCache;
	private AssociationCache associationCache;

	/**
	 * the identity of the topic map itself
	 */
	private JdbcIdentity identity;

	/**
	 * indexes
	 */
	private TypeInstanceIndex typeInstanceIndex;
	private ITransitiveTypeInstanceIndex transitiveTypeInstanceIndex;
	private ScopedIndex scopedIndex;
	private LiteralIndex literalIndex;
	private IIdentityIndex identityIndex;
	private ISupertypeSubtypeIndex supertypeSubtypeIndex;

	/**
	 * thread specific attributes
	 */
	private boolean blocked = false;
	private List<Runnable> queue;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the parent topic map system
	 * @param store
	 *            the real topic map store
	 * @param transaction
	 *            the transaction
	 */
	public TransactionTopicMapStore(ITopicMapSystem topicMapSystem, ModifableTopicMapStoreImpl store, ITransaction transaction) {
		super(topicMapSystem);
		this.store = store;
		this.transaction = transaction;
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
		IAssociation a = getConstructFactory().newAssociation(generateIdentity(), topicMap);
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
		return a;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws TopicMapStoreException {
		return createAssociation(topicMap, type, themes, null);
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
		IRevision revision = null;
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = getConstructFactory().newName(generateIdentity(), topic);
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
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = null;
		/*
		 * create or read default name type
		 */
		ITopic type = createDefaultNameType(topic.getTopicMap(), revision);
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = getConstructFactory().newName(generateIdentity(), topic);
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
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = getConstructFactory().newName(generateIdentity(), topic);
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
	 *             thrown if operation fails
	 */
	IName createName(ITopic topic, ITopic type, String value, Collection<ITopic> themes, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IName name = getConstructFactory().newName(generateIdentity(), topic);
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
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createName(topic, type, value, themes, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING), null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_STRING), themes, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI), null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), doCreateLocator(getTopicMap(), XmlSchemeDatatypes.XSD_ANYURI), themes, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype, themes, null);
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
		IOccurrence occurrence = getConstructFactory().newOccurrence(generateIdentity(), topic);
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
		return occurrence;
	}

	/**
	 * Internal modification method to create a new occurrence for the given topic item.
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
	IOccurrence createOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes, IRevision revision) throws TopicMapStoreException {
		/*
		 * create random id
		 */
		final String id = UUID.randomUUID().toString();
		/*
		 * create role and add to identity store
		 */
		IOccurrence occurrence = getConstructFactory().newOccurrence(generateIdentity(), topic);
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
		IAssociationRole role = getConstructFactory().newAssociationRole(generateIdentity(), association);
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
		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws TopicMapStoreException {
		return createRole(association, type, player, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws TopicMapStoreException {
		return getScopeStore().getScope(themes);
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
		ITopic t = getConstructFactory().newTopic(generateIdentity(), topicMap);
		getIdentityStore().setId(t, id);
		if (revision != null) {
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws TopicMapStoreException {
		ITopic topic = createTopic(topicMap, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws TopicMapStoreException {
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add item identifier
		 */
		modifyItemIdentifier(topic, itemIdentifier, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws TopicMapStoreException {
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add subject identifier
		 */
		modifySubjectIdentifier(topic, subjectIdentifier, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
		return topic;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws TopicMapStoreException {
		ITopic topic = createTopic(topicMap, null);
		/*
		 * add subject locator
		 */
		modifySubjectLocator(topic, subjectLocator, null);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, topic, null);
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
		 * create role and add to identity store
		 */
		IVariant variant = getConstructFactory().newVariant(generateIdentity(), name);
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
		 * create role and add to identity store
		 */
		IVariant variant = getConstructFactory().newVariant(generateIdentity(), name);
		getIdentityStore().setId(variant, id);
		/*
		 * register characteristics
		 */
		getCharacteristicsStore().addVariant(name, variant);
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, variant, null);
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
		IVariant variant = getConstructFactory().newVariant(generateIdentity(), name);
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
		return variant;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		return createVariant(name, value, datatype, themes, null);
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
		TransactionMergeUtils.doMergeTopicMaps(this, (ITopicMap) context, other);

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
			TransactionMergeUtils.doMerge(this, newTopic, context, revision);
			TransactionMergeUtils.doMerge(this, newTopic, other, revision);
			((TopicImpl) context).getIdentity().setId(newTopic.longId());
			((ConstructImpl) context).setRemoved(false);
			((TopicImpl) other).getIdentity().setId(newTopic.longId());
			((ConstructImpl) other).setRemoved(false);
			/*
			 * notify listeners
			 */
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic, other);
			notifyListeners(TopicMapEventType.MERGE, getTopicMap(), newTopic, context);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doMergeTopics(ITopic context, ITopic other) throws TopicMapStoreException {
		mergeTopics(context, other, null);
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		/*
		 * do modification
		 */
		modifyItemIdentifier(c, itemIdentifier, null);
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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyPlayer(IAssociationRole role, ITopic player) throws TopicMapStoreException {
		modifyPlayer(role, player, null);
	}

	/**
	 * Internal modification method to change the reification of the given construct.
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyReifier(IReifiable r, ITopic reifier) throws TopicMapStoreException {
		modifyReifier(r, reifier, null);
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		modifySubjectIdentifier(t, subjectIdentifier, null);
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
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		modifySubjectLocator(t, subjectLocator, null);
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
			if (recognizingSupertypeSubtypeAssociation()) {
				createSupertypeSubtypeAssociation(t, type, revision);
			}
			notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifySupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		modifySupertype(t, type, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyType(ITypeable t, ITopic type) throws TopicMapStoreException {
		modifyType(t, type, null);
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
			if (revision != null) {
				/*
				 * notify listeners
				 */
				notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
			}
			if (recognizingTypeInstanceAssociation()) {
				createTypeInstanceAssociation(t, type, revision);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyTopicType(ITopic t, ITopic type) throws TopicMapStoreException {
		modifyType(t, type, null);
	}

	/**
	 * Internal modification method to modify the value and the data type of a {@link IDatatypeAware}.
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value) throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING);
		modifyValue(c, value, datatype, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, String value, ILocator datatype) throws TopicMapStoreException {
		modifyValue(c, value, datatype, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IDatatypeAware c, Object value) throws TopicMapStoreException {
		ILocator datatype = getIdentityStore().createLocator(XmlSchemeDatatypes.javaToXsd(value.getClass()));
		modifyValue(c, value, datatype, null);
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
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyValue(IName n, String value) throws TopicMapStoreException {
		modifyValue(n, value, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doModifyMetaData(IRevision revision, String key, String value) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		for (IAssociationRole r : getAssociationStore().getRoles(t)) {
			associations.add(r.getParent());
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws TopicMapStoreException {
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
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
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
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws TopicMapStoreException {
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
	public Set<IAssociation> doReadAssociation(ITopicMap tm) throws TopicMapStoreException {
		return HashUtil.getHashSet(getAssociationStore().getAssociations());
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws TopicMapStoreException {
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
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws TopicMapStoreException {
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
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws TopicMapStoreException {
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
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMapEventType doReadChangeSetType(IRevision revision) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore().getCharacteristics(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws TopicMapStoreException {
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
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
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
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws TopicMapStoreException {
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
	public IConstruct doReadConstruct(ITopicMap t, String id) throws TopicMapStoreException {
		/*
		 * check if id identifies the topic map itself
		 */
		if (this.identity.getId().equalsIgnoreCase(id)) {
			return getTopicMap();
		}
		/*
		 * return the construct by id
		 */
		return getIdentityStore().byId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws TopicMapStoreException {
		return getIdentityStore().byItemIdentifier(itemIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware c) throws TopicMapStoreException {
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
		throw new TopicMapStoreException("IConstruct created by external instance.");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadItemIdentifiers(IConstruct c) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getItemIdentifiers(c));
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
		return HashUtil.getHashSet(getCharacteristicsStore().getNames(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type) throws TopicMapStoreException {
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
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
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
	public Set<IName> doReadNames(ITopic t, IScope scope) throws TopicMapStoreException {
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
	public IRevision doReadFutureRevision(IRevision r) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t) throws TopicMapStoreException {
		Set<IOccurrence> occurrences = HashUtil.getHashSet();
		occurrences.addAll(getCharacteristicsStore().getOccurrences(t));
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws TopicMapStoreException {
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
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
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
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws TopicMapStoreException {
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
	public ITopic doReadPlayer(IAssociationRole role) throws TopicMapStoreException {
		return getAssociationStore().getPlayer(role);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(IRevision r) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
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
	public Calendar doReadRevisionTimestamp(IRevision r) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadRoleTypes(IAssociation association) throws TopicMapStoreException {
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
	public Set<IAssociationRole> doReadRoles(IAssociation association) throws TopicMapStoreException {
		return HashUtil.getHashSet(getAssociationStore().getRoles(association));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws TopicMapStoreException {
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
	public Set<IAssociationRole> doReadRoles(ITopic player) throws TopicMapStoreException {
		return getAssociationStore().getRoles(player);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws TopicMapStoreException {
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
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws TopicMapStoreException {
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
	public IScope doReadScope(IScopable s) throws TopicMapStoreException {
		return getScopeStore().getScope(s);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getSubjectIdentifiers(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectLocators(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getSubjectLocators(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadSuptertypes(ITopic t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getTopicTypeStore().getSupertypes(t));
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws TopicMapStoreException {
		return getIdentityStore().bySubjectIdentifier(subjectIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws TopicMapStoreException {
		return getIdentityStore().bySubjectLocator(subjectLocator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		return HashUtil.getHashSet(getIdentityStore().getTopics());
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws TopicMapStoreException {
		return HashUtil.getHashSet(getTopicTypeStore().getDirectInstances(type));
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
	public Object doReadValue(IDatatypeAware c) throws TopicMapStoreException {
		return getCharacteristicsStore().getValueAsString(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T doReadValue(IDatatypeAware c, Class<T> type) throws TopicMapStoreException {
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
	public Object doReadValue(IName n) throws TopicMapStoreException {
		return getCharacteristicsStore().getValue(n);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		return HashUtil.getHashSet(getCharacteristicsStore().getVariants(n));
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope) throws TopicMapStoreException {
		Set<IVariant> variants = HashUtil.getHashSet(getCharacteristicsStore().getVariants(n));
		variants.retainAll(getScopeStore().getScoped(scope));
		return variants;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetaData(IRevision revision) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetaData(IRevision revision, String key) throws TopicMapStoreException {
		throw new TopicMapStoreException("History management not supported!");
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestIdentifier(ITopic topic, boolean withPrefix) {
		final String prefix;
		Set<ILocator> locators;
		/*
		 * try subject-identifier
		 */
		locators = getIdentityStore().getSubjectIdentifiers(topic);
		if (locators.isEmpty()) {
			/*
			 * try subject-locator
			 */
			locators = getIdentityStore().getSubjectLocators(topic);
			if (locators.isEmpty()) {
				/*
				 * try item-identifier
				 */
				locators = getIdentityStore().getItemIdentifiers(topic);
				if (locators.isEmpty()) {
					String bestIdentifier = withPrefix ? ID_PREFIX : "";
					bestIdentifier += topic.getId();
					return bestIdentifier;
				}
				prefix = ITEMIDENTIFIER_PREFIX;
			} else {
				prefix = SUBJECTLOCATOR_PREFIX;
			}
		} else {
			prefix = SUBJECTIDENTIFIER_PREFIX;
		}

		if (locators.size() == 1) {
			String bestIdentifier = withPrefix ? prefix : "";
			bestIdentifier += locators.iterator().next().getReference();
			return bestIdentifier;
		}

		List<ILocator> sorted = HashUtil.getList(locators);
		Collections.sort(sorted, new Comparator<ILocator>() {
			/**
			 * {@inheritDoc}
			 */
			public int compare(ILocator o1, ILocator o2) {
				return o1.getReference().length() - o2.getReference().length();
			}
		});

		/*
		 * extract all references with the shortest length
		 */
		String first = sorted.get(0).getReference();
		List<String> references = HashUtil.getList();
		references.add(first);
		for (int i = 1; i < sorted.size(); i++) {
			String s = sorted.get(i).getReference();
			if (s.length() == first.length()) {
				references.add(s);
			} else {
				break;
			}
		}
		/*
		 * is only one
		 */
		if (references.size() == 1) {
			String bestIdentifier = withPrefix ? prefix : "";
			bestIdentifier += references.get(0);
			return bestIdentifier;
		}
		/*
		 * sort lexicographically
		 */
		Collections.sort(references);
		String bestIdentifier = withPrefix ? prefix : "";
		bestIdentifier += references.get(0);
		return bestIdentifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore().getNames(topic));
		if (!names.isEmpty()) {
			return readBestName(topic, names);
		}
		return doReadBestIdentifier(topic, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<IName> names = HashUtil.getHashSet(getCharacteristicsStore().getNames(topic));
		if (!names.isEmpty()) {
			return readBestName(topic, theme, names, strict);
		}
		/*
		 * is strict mode
		 */
		if (strict) {
			return null;
		}
		return doReadBestIdentifier(topic, false);
	}

	/**
	 * Method filter the given names by the default name type
	 * 
	 * @param topic
	 *            the topic as parent of the names
	 * @param names
	 *            the names
	 * @return the filtered names
	 */
	private Set<IName> filterByDefaultNameType(ITopic topic, Set<IName> names) {
		/*
		 * check if default name type exists
		 */
		if (existsTmdmDefaultNameType()) {
			Set<IName> tmp = HashUtil.getHashSet(names);
			tmp.retainAll(getTypedStore().getTypedNames(getTmdmDefaultNameType()));
			/*
			 * more than one default name
			 */
			if (tmp.size() > 0) {
				return tmp;
			}
		}
		return names;
	}

	/**
	 * Internal best label method only check name attributes.
	 * 
	 * @param topic
	 *            the topic
	 * @param theme
	 *            the theme
	 * @param set
	 *            the non-empty set of names
	 * @param strict
	 *            if there is no name with the given theme and strict is <code>true</code>, then <code>null</code> will
	 *            be returned.
	 * @return the best name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private String readBestName(ITopic topic, ITopic theme, Set<IName> names, boolean strict) throws TopicMapStoreException {

		List<IScope> scopes = HashUtil.getList(getScopeStore().getScopes(theme));
		/*
		 * sort scopes by number of themes
		 */
		Collections.sort(scopes, ScopeComparator.getInstance(true));
		boolean atLeastOneName = false;
		int numberOfThemes = -1;
		Set<IName> tmp = HashUtil.getHashSet();
		for (IScope s : scopes) {
			Set<IName> scopedNames = doReadNames(topic, s);
			if (scopedNames.isEmpty()) {
				continue;
			}
			/*
			 * set number of themes
			 */
			if (numberOfThemes == -1) {
				numberOfThemes = s.getThemes().size();
			}
			/*
			 * current scope has more themes than expected
			 */
			if (numberOfThemes < s.getThemes().size()) {
				break;
			}
			/*
			 * get names of the scope and topic
			 */
			tmp.addAll(scopedNames);
			atLeastOneName = true;
		}
		/*
		 * is strict mode but no scoped name
		 */
		if (strict && !atLeastOneName) {
			return null;
		}
		if (!tmp.isEmpty()) {
			names.retainAll(tmp);
		}
		/*
		 * only one name of the current scope
		 */
		if (names.size() == 1) {
			return names.iterator().next().getValue();
		}
		/*
		 * check default name type
		 */
		names = filterByDefaultNameType(topic, names);
		/*
		 * sort by value
		 */
		List<IName> list = HashUtil.getList(names);
		Collections.sort(list, NameByValueComparator.getInstance(true));
		return list.get(0).getValue();
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
	private String readBestName(ITopic topic, Set<IName> names) throws TopicMapStoreException {
		/*
		 * check if default name type exists
		 */
		names = filterByDefaultNameType(topic, names);
		if (names.size() == 1) {
			return names.iterator().next().getValue();
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
			Set<IName> tmp = HashUtil.getHashSet();
			int numberOfThemes = -1;
			for (IScope s : scopes) {
				Set<IName> scopedNames = doReadNames(topic, s);
				if (scopedNames.isEmpty()) {
					continue;
				}
				/*
				 * set number of themes
				 */
				if (numberOfThemes == -1) {
					numberOfThemes = s.getThemes().size();
				}
				/*
				 * current scope has more themes than expected
				 */
				if (numberOfThemes < s.getThemes().size()) {
					break;
				}
				/*
				 * get names of the scope and topic
				 */
				tmp.addAll(scopedNames);
			}
			if (!tmp.isEmpty()) {
				names.retainAll(tmp);
			}
			/*
			 * only one name of the current scope
			 */
			if (names.size() == 1) {
				return names.iterator().next().getValue();
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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveAssociation(IAssociation association, boolean cascade) throws TopicMapStoreException {
		removeAssocaition(association, cascade, null);
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
		 * remove variants
		 */
		for (IVariant v : doReadVariants(name)) {
			removeVariant(v, true, revision);
		}
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
		 * remove construct
		 */
		getIdentityStore().removeConstruct(name);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_REMOVED, name.getParent(), null, name);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveName(IName name, boolean cascade) throws TopicMapStoreException {
		removeName(name, cascade, null);
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
		 * remove construct
		 */
		getIdentityStore().removeConstruct(occurrence);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, occurrence.getParent(), null, occurrence);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws TopicMapStoreException {
		removeOccurrence(occurrence, cascade, null);
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
		// }
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveRole(IAssociationRole role, boolean cascade) throws TopicMapStoreException {
		removeRole(role, cascade, null);
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
		 * remove construct
		 */
		getIdentityStore().removeConstruct(variant);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_REMOVED, variant.getParent(), null, variant);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveVariant(IVariant variant, boolean cascade) throws TopicMapStoreException {
		removeVariant(variant, cascade, null);
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
		Set<ITypeable> removeTypes = getTypedStore().removeType(topic);
		if (!removeTypes.isEmpty()) {
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
		// for (IScopable scopable : getScopeStore().removeScopes(topic)) {
		// removeConstruct(scopable, cascade, revision);
		// }

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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveTopic(ITopic topic, boolean cascade) throws TopicMapStoreException {
		removeTopic(topic, cascade, null);
	}

	/**
	 * Method checks if the topic is used by any topic map relation.
	 * 
	 * @param topic
	 *            the topic to check
	 * @return <code>true</code> if the topic is used as type, reifier etc. , <code>false</code> otherwise.
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
		 * check if deletion constraints are defined and topic is used as reifier
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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		removeItemIdentifier(c, itemIdentifier, null);
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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		removeSubjectIdentifier(t, subjectIdentifier, null);
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
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		removeSubjectLocator(t, subjectLocator, null);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveSupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		if (getTopicTypeStore().getSupertypes(t).contains(type)) {
			getTopicTypeStore().removeSupertype(t, type);
			if (recognizingSupertypeSubtypeAssociation() && existsTmdmSupertypeSubtypeAssociationType()) {
				removeSupertypeSubtypeAssociation(t, type, null);
			}
			notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doRemoveType(ITopic t, ITopic type) throws TopicMapStoreException {
		if (getTopicTypeStore().getTypes(t).contains(type)) {
			getTopicTypeStore().removeType(t, type);
			if (recognizingTypeInstanceAssociation() && existsTmdmTypeInstanceAssociationType()) {
				removeTypeInstanceAssociation(t, type, null);
			}
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

		this.identityStore = null;
		this.characteristicsStore = null;
		this.typedStore = null;
		this.scopeStore = null;
		this.topicTypeCache = null;
		this.reificationCache = null;
		this.associationCache = null;
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
		this.topicTypeCache = createTopicTypeStore(this);
		this.reificationCache = createReificationStore(this);
		this.associationCache = createAssociationStore(this);

		this.identity = (JdbcIdentity) generateIdentity();
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
				this.transitiveTypeInstanceIndex = new TransactionTransitiveTypeInstanceIndex(this);
			}
			return (I) this.transitiveTypeInstanceIndex;
		} else if (TypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (this.typeInstanceIndex == null) {
				this.typeInstanceIndex = new TransactionTypeInstanceIndex(this);
			}
			return (I) this.typeInstanceIndex;
		} else if (ScopedIndex.class.isAssignableFrom(clazz)) {
			if (this.scopedIndex == null) {
				this.scopedIndex = new TransactionScopedIndex(this);
			}
			return (I) this.scopedIndex;
		} else if (LiteralIndex.class.isAssignableFrom(clazz)) {
			if (this.literalIndex == null) {
				this.literalIndex = new TransactionLiteralIndex(this);
			}
			return (I) this.literalIndex;
		} else if (IIdentityIndex.class.isAssignableFrom(clazz)) {
			if (this.identityIndex == null) {
				this.identityIndex = new TransactionIdentityIndex(this);
			}
			return (I) this.identityIndex;
		} else if (ISupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (this.supertypeSubtypeIndex == null) {
				this.supertypeSubtypeIndex = new TransactionSupertypeSubtypeIndex(this);
			}
			return (I) this.supertypeSubtypeIndex;
		}
		throw new UnsupportedOperationException("The index class '" + (clazz == null ? "null" : clazz.getCanonicalName()) + "' is not supported by the current engine.");
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
	protected void removeTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmTypeInstanceAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)
						&& association.getRoles(getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
					removeAssocaition(association, false, revision);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException("Invalid meta model! Missing type or instance role!", e);
			}
		}
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
	public boolean existsTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = getIdentityStore().createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		return getIdentityStore().containsSubjectIdentifier(loc);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsTmdmSubtypeRoleType() throws TopicMapStoreException {
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
	protected CharacteristicsCache createCharacteristicsStore(TransactionTopicMapStore store, ILocator xsdString) {
		return new CharacteristicsCache(store, xsdString);
	}

	/**
	 * Creates the internal scope store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the scopeStore
	 */
	protected ScopeCache createScopeStore(TransactionTopicMapStore store) {
		return new ScopeCache(store);
	}

	/**
	 * Creates the internal association store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the associationStore
	 */
	protected AssociationCache createAssociationStore(TransactionTopicMapStore store) {
		return new AssociationCache(store);
	}

	/**
	 * Creates the internal identity store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the identityStore
	 */
	protected IdentityCache createIdentityStore(TransactionTopicMapStore store) {
		return new IdentityCache(store);
	}

	/**
	 * Creates the internal reification store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the reificationStore
	 */
	protected ReificationCache createReificationStore(TransactionTopicMapStore store) {
		return new ReificationCache(store);
	}

	/**
	 * Creates the internal topic-type hierarchy store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the topicTypeStore
	 */
	protected TopicTypeCache createTopicTypeStore(TransactionTopicMapStore store) {
		return new TopicTypeCache(store);
	}

	/**
	 * Creates the internal types store reference.
	 * 
	 * @param store
	 *            the calling store instance
	 * 
	 * @return the typedStore
	 */
	protected TypedCache createTypedStore(TransactionTopicMapStore store) {
		return new TypedCache(store);
	}

	/**
	 * Returns the internal characteristic store reference.
	 * 
	 * @return the characteristicsStore
	 */
	public CharacteristicsCache getCharacteristicsStore() {
		return this.characteristicsStore;
	}

	/**
	 * Returns the internal scope store reference.
	 * 
	 * @return the scopeStore
	 */
	public ScopeCache getScopeStore() {
		return this.scopeStore;
	}

	/**
	 * Returns the internal association store reference.
	 * 
	 * @return the associationStore
	 */
	public AssociationCache getAssociationStore() {
		return this.associationCache;
	}

	/**
	 * Returns the internal identity store reference.
	 * 
	 * @return the identityStore
	 */
	public IdentityCache getIdentityStore() {
		return this.identityStore;
	}

	/**
	 * Returns the internal reification store reference.
	 * 
	 * @return the reificationStore
	 */
	public ReificationCache getReificationStore() {
		return this.reificationCache;
	}

	/**
	 * Returns the internal topic-type hierarchy store reference.
	 * 
	 * @return the topicTypeStore
	 */
	public TopicTypeCache getTopicTypeStore() {
		return this.topicTypeCache;
	}

	/**
	 * Returns the internal types store reference.
	 * 
	 * @return the typedStore
	 */
	public TypedCache getTypedStore() {
		return this.typedStore;
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
	public final void storeRevision(final IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		throw new UnsupportedOperationException("The feature of revision management is not supported by the transaction store.");
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	protected synchronized IRevision createRevision(TopicMapEventType type) {
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
	public void removeDuplicates() {
		MergeUtils.removeDuplicates(this, getTopicMap());
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

		this.identityStore = createIdentityStore(this);
		this.characteristicsStore = createCharacteristicsStore(this, getIdentityStore().createLocator(XmlSchemeDatatypes.XSD_STRING));
		this.typedStore = createTypedStore(this);
		this.scopeStore = createScopeStore(this);
		this.topicTypeCache = createTopicTypeStore(this);
		this.reificationCache = createReificationStore(this);
		this.associationCache = createAssociationStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction getTransaction() {
		return transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void commit() throws TransactionException {
		try {
			final Map<Object, Object> lazy = HashUtil.getHashMap();
			lazy.put(transaction, transaction.getTopicMap());
			ITopicMapListener listener = new ITopicMapListener() {
				public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
					switch (event) {
						case MERGE: {
							Object oldValue_ = null;
							/* find old value */
							// By subject-identifier
							for (Locator l : ((ITopic) oldValue).getSubjectIdentifiers()) {
								oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, l);
								if (oldValue_ != null) {
									break;
								}
							}
							// By subject-locator
							if (oldValue_ == null) {
								for (Locator l : ((ITopic) oldValue).getSubjectLocators()) {
									oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, l);
									if (oldValue_ != null) {
										break;
									}
								}
							}
							// By item-identifier
							if (oldValue_ == null) {
								for (Locator l : ((ITopic) oldValue).getItemIdentifiers()) {
									oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, l);
									if (oldValue_ != null) {
										break;
									}
								}
							}
							// store mapping
							lazy.put(oldValue_, newValue);
						}
							break;
					}
				}
			};
			getRealStore().addTopicMapListener(listener);
			for (TransactionCommand command : commands) {
				Object obj = command.commit(getRealStore(), lazy);
				if (obj != null && command.getResult() != null) {
					lazy.put(command.getResult(), obj);
				}
				commited.add(command);
			}
			commands.clear();
			getRealStore().removeTopicMapListener(listener);
		} catch (TransactionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ModifableTopicMapStoreImpl getRealStore() {
		return store;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void rollback() {
		for (TransactionCommand command : commited) {
			// TODO undo command
			command.notify();
		}
		commited.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		commands.add(new TransactionCommand(getTransaction(), null, TransactionOperation.MODIFY, context, paramType, params));
		super.doModify(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		commands.add(new TransactionCommand(getTransaction(), null, TransactionOperation.REMOVE, context, paramType, params));
		super.doRemove(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		commands.add(new TransactionCommand(getTransaction(), null, TransactionOperation.REMOVE, context, null, cascade));
		super.doRemove(context, cascade);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		Object obj = super.doCreate(context, paramType, params);
		commands.add(new TransactionCommand(getTransaction(), obj, TransactionOperation.CREATE, context, paramType, params));
		return obj;
	}

	/**
	 * Generates an identity for a constructs
	 * 
	 * @return the generate id
	 */
	public ITopicMapStoreIdentity generateIdentity() {
		return new JdbcIdentity(Math.round(Math.random() * Long.MAX_VALUE));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCachingEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableCaching(boolean enable) {
		// NOTHING TO DO HERE
	}

	/**
	 * {@inheritDoc}
	 */
	public JdbcIdentity getTopicMapIdentity() {
		return identity;
	}
}
