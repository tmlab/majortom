package de.topicmapslab.majortom.redis.store;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
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
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.redis.store.index.RedisIdentityIndex;
import de.topicmapslab.majortom.redis.store.index.RedisLiteralIndex;
import de.topicmapslab.majortom.redis.store.index.RedisScopedIndex;
import de.topicmapslab.majortom.redis.store.index.RedisSupertypeSubtypeIndex;
import de.topicmapslab.majortom.redis.store.index.RedisTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.index.RedisTypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisConstructIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisIdentityIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisLiteralIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisScopedIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisSupertypeSubtypeIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.index.paged.PagedRedisTypeInstanceIndex;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.store.ModifableTopicMapStoreImpl;
import de.topicmapslab.majortom.util.BestLabelUtils;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

public class RedisTopicMapStore extends ModifableTopicMapStoreImpl {

	private String host;
	private String password;
	private int database;
	private int port;

	public static final String HASHPOSTFIX = "h";
	public static final String COLON = ":";
	public static final String CHARACTERISTICS = "c";
	public static final String TOPICMAP = "topicmap:";
	public static final String PLAYED_ASSOCIATION = "pa";
	public static final String PLAYED_ROLE = "pr";
	public static final String TYPED_ROLES = "tr";
	public static final String TYPED_ASSOCIATIONS = "ta";
	public static final String TYPED_NAMES = "tn";
	public static final String TYPED_OCCURRENCES = "to";
	public static final String TYPED_CHARACTERISTICS = "tc";
	public static final String SCOPED_ASSOCIATIONS = "sa";
	public static final String SCOPED_NAMES = "sn";
	public static final String SCOPED_OCCURRENCES = "so";
	public static final String SCOPED_VARIANTS = "sv";
	public static final String SCOPED_CHARACTERISTICS = "sc";

	public static final String ROLES_BY_ASSOCTYPE = "rba";
	// TODO remove colon
	public static final String IN_SCOPE = ":is";
	public static final String PLAYER = "p";
	public static final String ITEM_IDENTIFIER = "ii";
	public static final String SUBJECT_LOCATOR = "sl";
	public static final String SUBJECT_IDENTIFIER = "si";
	public static final String TOPICS_OF_TOPICMAP = "topics";
	public static final String ASSOCIATION = "a";
	public static final String ROLE = "r";
	public static final String TOPIC = "t";
	public static final String NAME = "n";
	public static final String OCCURRENCE = "o";
	public static final String VARIANT = "y";
	public static final String TYPE = "t";
	public static final String SCOPE = "s";
	public static final String PARENT = "^";
	public static final String VALUE = "v";
	public static final String DATATYPE = "d";
	public static final String REIFIER = "~";
	public static final String REIFIED = REIFIER;
	public static final String LOCATORS = "l:";

	public static final String SCOPE_COLON = "s:";
	public static final String ASSOCIATION_OF_TOPICMAP = "associations";
	public static final String SUPERTYPES = "sp";
	public static final String SUBTYPES = "sb";
	public static final String TYPES = "t";
	public static final String INSTANCES = "i";
	public static final String STAR = "*";
	public static final String EMPTY_SCOPE_ID = "s:0";
	public static final IScope EMPTY_SCOPE = new ScopeImpl(EMPTY_SCOPE_ID);

	private RedisHandler redis;
	private static Set<ITopic> EmptyTopicSet = Collections.emptySet();
	private ITopicMapStoreIdentity topicMapIdentity;

	/*
	 * non paged
	 */
	private ITypeInstanceIndex typeInstanceIndex;
	private ITransitiveTypeInstanceIndex transitiveTypeInstanceIndex;
	private ILiteralIndex literalIndex;
	private IScopedIndex scopedIndex;
	private ISupertypeSubtypeIndex supertypeSubtypeIndex;
	private IIdentityIndex identityIndex;
	/*
	 * paged
	 */
	private IPagedTypeInstanceIndex pagedTypeInstanceIndex;
	private IPagedTransitiveTypeInstanceIndex pagedTransitiveTypeInstanceIndex;
	private IPagedLiteralIndex pagedLiteralIndex;
	private IPagedScopedIndex pagedScopedIndex;
	private IPagedIdentityIndex pagedIdentityIndex;
	private IPagedConstructIndex pagedConstructIndex;
	private IPagedSupertypeSubtypeIndex pagedSupertypeSubtypeIndex;

	private Map<Collection<ITopic>, IScope> scopeCache;

	public RedisTopicMapStore() {
	}

	public RedisTopicMapStore(ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	@Override
	public void connect() throws TopicMapStoreException {
		super.connect();
		redis = new RedisHandler(host, port, password, database);
		topicMapIdentity = new RedisStoreIdentity(getNewRedisId(TOPICMAP));
		scopeCache = HashUtil.getHashMap();
	}

	@Override
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		super.initialize(topicMapBaseLocator);

		Object host = getTopicMapSystem().getProperty(IRedisTopicMapStoreProperty.REDIS_HOST);
		Object port = getTopicMapSystem().getProperty(IRedisTopicMapStoreProperty.REDIS_PORT);
		Object database = getTopicMapSystem().getProperty(IRedisTopicMapStoreProperty.REDIS_DATABASE);
		Object password = getTopicMapSystem().getProperty(IRedisTopicMapStoreProperty.REDIS_PASSWORD);

		if (host == null || database == null) {
			throw new TopicMapStoreException("Missing requiered parameters redis.host or redis.database");
		}
		/*
		 * set host
		 */
		this.host = host.toString();
		/*
		 * set database
		 */
		try {
			this.database = Integer.parseInt(database.toString());
		} catch (NumberFormatException e) {
			throw new TopicMapStoreException("Invalid argument for redis.database property. A Number is expected.");
		}
		/*
		 * set password
		 */
		if (password != null) {
			this.password = password.toString();
		}
		/*
		 * set port
		 */
		if (port != null) {
			try {
				this.port = Integer.parseInt(port.toString());
			} catch (NumberFormatException e) {
				throw new TopicMapStoreException("Invalid argument for redis.port property. A Number is expected.");
			}
		}else{
			this.port = Integer.MIN_VALUE;
		}
	}

	@SuppressWarnings("unchecked")
	public <I extends Index> I getIndex(Class<I> clazz) {
		if (IPagedTransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (pagedTransitiveTypeInstanceIndex == null) {
				pagedTransitiveTypeInstanceIndex = new PagedRedisTransitiveTypeInstanceIndex(this, getIndex(RedisTransitiveTypeInstanceIndex.class));
			}
			return (I) pagedTransitiveTypeInstanceIndex;
		} else if (IPagedTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (pagedTypeInstanceIndex == null) {
				pagedTypeInstanceIndex = new PagedRedisTypeInstanceIndex(this, getIndex(RedisTypeInstanceIndex.class));
			}
			return (I) pagedTypeInstanceIndex;
		} else if (IPagedLiteralIndex.class.isAssignableFrom(clazz)) {
			if (pagedLiteralIndex == null) {
				pagedLiteralIndex = new PagedRedisLiteralIndex(this, getIndex(ILiteralIndex.class));
			}
			return (I) pagedLiteralIndex;
		} else if (IPagedScopedIndex.class.isAssignableFrom(clazz)) {
			if (pagedScopedIndex == null) {
				pagedScopedIndex = new PagedRedisScopedIndex(this, getIndex(IScopedIndex.class));
			}
			return (I) pagedScopedIndex;
		} else if (IPagedSupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (pagedSupertypeSubtypeIndex == null) {
				pagedSupertypeSubtypeIndex = new PagedRedisSupertypeSubtypeIndex(this, getIndex(ISupertypeSubtypeIndex.class));
			}
			return (I) pagedSupertypeSubtypeIndex;
		} else if (IPagedIdentityIndex.class.isAssignableFrom(clazz)) {
			if (pagedIdentityIndex == null) {
				pagedIdentityIndex = new PagedRedisIdentityIndex(this, getIndex(IIdentityIndex.class));
			}
			return (I) pagedIdentityIndex;
		} else if (IPagedConstructIndex.class.isAssignableFrom(clazz)) {
			if (pagedConstructIndex == null) {
				pagedConstructIndex = new PagedRedisConstructIndex(this);
			}
			return (I) pagedConstructIndex;
		} else if (ITransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (transitiveTypeInstanceIndex == null) {
				transitiveTypeInstanceIndex = new RedisTransitiveTypeInstanceIndex(this);
			}
			return (I) transitiveTypeInstanceIndex;
		} else if (TypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (typeInstanceIndex == null) {
				typeInstanceIndex = new RedisTypeInstanceIndex(this);
			}
			return (I) typeInstanceIndex;
		} else if (LiteralIndex.class.isAssignableFrom(clazz)) {
			if (literalIndex == null) {
				literalIndex = new RedisLiteralIndex(this);
			}
			return (I) literalIndex;
		} else if (ScopedIndex.class.isAssignableFrom(clazz)) {
			if (scopedIndex == null) {
				scopedIndex = new RedisScopedIndex(this);
			}
			return (I) scopedIndex;
		} else if (ISupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (supertypeSubtypeIndex == null) {
				supertypeSubtypeIndex = new RedisSupertypeSubtypeIndex(this);
			}
			return (I) supertypeSubtypeIndex;
		} else if (IIdentityIndex.class.isAssignableFrom(clazz)) {
			if (identityIndex == null) {
				identityIndex = new RedisIdentityIndex(this);
			}
			return (I) identityIndex;
		}
		System.err.println("Index not implemented: " + clazz.getName());
		throw new UnsupportedOperationException();
	}

	public ITopicMapStoreIdentity getTopicMapIdentity() {
		return topicMapIdentity;
	}

	@Override
	protected ILocator doCreateItemIdentifier(ITopicMap topicMap) {
		return new LocatorImpl(getNewRedisId(LOCATORS));
	}

	@Override
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws TopicMapStoreException {
		return doCreateAssociation(topicMap, type, EmptyTopicSet);
	}

	@Override
	protected IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws TopicMapStoreException {
		String id = getNewRedisId(ASSOCIATION + COLON);
		redis.set(id, TYPE, type.getId());
		IScope scope = doCreateScope(getTopicMap(), themes);
		redis.set(id, SCOPE, scope.getId());

		// store as association of the topic map
		redis.sadd(ASSOCIATION_OF_TOPICMAP, id);
		// typed associations
		redis.sadd(TYPED_ASSOCIATIONS + COLON + type.getId(), id);
		// scoped assocaitions
		redis.sadd(SCOPED_ASSOCIATIONS + COLON + scope.getId(), id);
		IAssociation a = getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_ADDED, getTopicMap(), a, null);
		return a;
	}

	@Override
	protected IName doCreateName(ITopic topic, String value) throws TopicMapStoreException {
		return doCreateName(topic, getTmdmDefaultNameType(), value, EmptyTopicSet);
	}

	@Override
	protected IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return doCreateName(topic, getTmdmDefaultNameType(), value, themes);
	}

	@Override
	protected IName doCreateName(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		return doCreateName(topic, type, value, EmptyTopicSet);
	}

	@Override
	protected IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		String id = getNewRedisId(NAME + COLON);
		redis.set(id, PARENT, topic.getId());
		redis.set(id, TYPE, type.getId());
		redis.set(id, VALUE, value);
		IScope scope = doCreateScope(getTopicMap(), themes);
		redis.set(id, SCOPE, scope.getId());
		// store as characteristics
		redis.sadd(topic.getId() + COLON + CHARACTERISTICS, id);
		redis.sadd(topic.getId() + COLON + NAME, id);
		// store typed characteristics
		redis.sadd(TYPED_CHARACTERISTICS + COLON + type.getId(), id);
		redis.sadd(TYPED_NAMES + COLON + type.getId(), id);
		// store scoped characteristics
		redis.sadd(SCOPED_CHARACTERISTICS + COLON + scope.getId(), id);
		redis.sadd(SCOPED_NAMES + COLON + scope.getId(), id);
		IName n = getConstructFactory().newName(new RedisStoreIdentity(id), topic);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_ADDED, topic, n, null);
		return n;
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, Namespaces.XSD.STRING, EmptyTopicSet);
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value, Namespaces.XSD.STRING, themes);
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), Namespaces.XSD.ANYURI, EmptyTopicSet);
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createOccurrence(topic, type, value.getReference(), Namespaces.XSD.ANYURI, themes);
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws TopicMapStoreException {
		return doCreateOccurrence(topic, type, value, datatype, EmptyTopicSet);
	}

	@Override
	protected IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes)
			throws TopicMapStoreException {
		return createOccurrence(topic, type, value, datatype.getReference(), themes);
	}

	private IOccurrence createOccurrence(ITopic topic, ITopic type, String value, String datatype, Collection<ITopic> themes) {
		String id = getNewRedisId(OCCURRENCE + COLON);
		redis.set(id, PARENT, topic.getId());
		redis.set(id, TYPE, type.getId());
		redis.set(id, VALUE, value);
		redis.set(id, DATATYPE, datatype);
		IScope scope = doCreateScope(getTopicMap(), themes);
		redis.set(id, SCOPE, scope.getId());
		// store as characteristics
		redis.sadd(topic.getId() + COLON + CHARACTERISTICS, id);
		redis.sadd(topic.getId() + COLON + OCCURRENCE, id);
		// store typed characteristics
		redis.sadd(TYPED_CHARACTERISTICS + COLON + type.getId(), id);
		redis.sadd(TYPED_OCCURRENCES + COLON + type.getId(), id);
		// store scoped characteristics
		redis.sadd(SCOPED_CHARACTERISTICS + COLON + scope.getId(), id);
		redis.sadd(SCOPED_OCCURRENCES + COLON + scope.getId(), id);
		IOccurrence o = getConstructFactory().newOccurrence(new RedisStoreIdentity(id), topic);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_ADDED, topic, o, null);
		return o;
	}

	@Override
	protected IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws TopicMapStoreException {
		final String id = getNewRedisId(ROLE + COLON);
		// store parent relation
		redis.set(id, PARENT, association.getId());
		// store type relation
		redis.set(id, TYPE, type.getId());
		// store player relation
		redis.set(id, PLAYER, player.getId());
		// add new role to parent
		redis.sadd(association.getId() + COLON + ROLE, id);
		// add role type
		redis.sadd(association.getId() + COLON + TYPE, type.getId());
		// add role player
		redis.sadd(association.getId() + COLON + PLAYER, player.getId());
		// add typed role
		redis.sadd(TYPED_ROLES + COLON + type.getId(), id);
		redis.sadd(ROLES_BY_ASSOCTYPE + COLON + doReadType(association).getId(), id);
		// add played role
		redis.sadd(player.getId() + COLON + PLAYED_ROLE, id);
		// add played association
		redis.sadd(player.getId() + COLON + PLAYED_ASSOCIATION, association.getId());
		IAssociationRole r = getConstructFactory().newAssociationRole(new RedisStoreIdentity(id), association);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_ADDED, association, r, null);
		return r;
	}

	@Override
	protected ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws TopicMapStoreException {
		return createTopic(topicMap, null, null, null);
	}

	@Override
	protected ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws TopicMapStoreException {
		return createTopic(topicMap, null, null, itemIdentifier.getReference());
	}

	@Override
	protected ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws TopicMapStoreException {
		return createTopic(topicMap, subjectIdentifier.getReference(), null, null);
	}

	@Override
	protected ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws TopicMapStoreException {
		return createTopic(topicMap, null, subjectLocator.getReference(), null);
	}

	/**
	 * Internal method to create a topic
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param subjectIdentifier
	 *            the subject identifier or <code>null</code>
	 * @param subjectLocator
	 *            the subject locator or <code>null</code>
	 * @param itemIdentifier
	 *            the item identifier or <code>null</code>
	 * @return the new topic
	 */
	private ITopic createTopic(ITopicMap topicMap, final String subjectIdentifier, final String subjectLocator, final String itemIdentifier) {
		final String id = getNewRedisId(TOPIC + COLON);
		redis.sadd(TOPICS_OF_TOPICMAP, id);
		// redis.set(id, OK);
		if (subjectIdentifier != null) {
			redis.sadd(id + COLON + SUBJECT_IDENTIFIER, subjectIdentifier);
			redis.set(SUBJECT_IDENTIFIER + COLON + subjectIdentifier, id);
		}
		if (subjectLocator != null) {
			redis.sadd(id + COLON + SUBJECT_LOCATOR, subjectLocator);
			redis.set(SUBJECT_LOCATOR + COLON + subjectLocator, id);
		}
		if (itemIdentifier != null) {
			redis.sadd(id + COLON + ITEM_IDENTIFIER, itemIdentifier);
			redis.set(ITEM_IDENTIFIER + COLON + itemIdentifier, id);
		}
		ITopic t = getConstructFactory().newTopic(new RedisStoreIdentity(id), topicMap);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
		return t;
	}

	@Override
	protected IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		return createVariant(name, value, Namespaces.XSD.STRING, themes);
	}

	@Override
	protected IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException {
		if (value == null) {
			throw new TopicMapStoreException("value must not be null");
		}
		return createVariant(name, value.getReference(), Namespaces.XSD.ANYURI, themes);
	}

	@Override
	protected IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		if (datatype == null) {
			throw new TopicMapStoreException("datatype must not be null");
		}
		return createVariant(name, value, datatype.getReference(), themes);
	}

	protected IVariant createVariant(IName name, String value, String datatype, Collection<ITopic> themes) throws TopicMapStoreException {
		String id = getNewRedisId(VARIANT + COLON);
		redis.set(id, PARENT, name.getId());
		redis.set(id, VALUE, value);
		redis.set(id, DATATYPE, datatype);
		IScope scope = doCreateScope(getTopicMap(), themes);
		redis.set(id, SCOPE, scope.getId());
		redis.sadd(name.getId() + COLON + VARIANT, id);
		// Adding extra "V;" prefix because of wrong matches in LiteralIndex
		// getNames
		redis.sadd( name.getId() + COLON + VARIANT, id);

		redis.sadd(SCOPED_VARIANTS + COLON + scope.getId(), id);
		IVariant v = getConstructFactory().newVariant(new RedisStoreIdentity(id), name);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_ADDED, name, v, null);
		return v;
	}

	@Override
	protected void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		modifyItemIdentifier(c, itemIdentifier.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_ADDED, c, itemIdentifier, null);
	}

	private void modifyItemIdentifier(IConstruct c, String itemIdentifier) throws TopicMapStoreException {
		// just add it. there's no problem if it exists.
		redis.sadd(c.getId() + COLON + ITEM_IDENTIFIER, itemIdentifier);
		// get construct from item identifier
		redis.set(ITEM_IDENTIFIER + COLON + itemIdentifier, c.getId());
	}

	@Override
	protected void doModifyPlayer(IAssociationRole role, ITopic player) throws TopicMapStoreException {
		// get the old player to update its index
		ITopic oldPlayer = doReadPlayer(role);

		// save the new player to the role
		redis.set(role.getId(), PLAYER, player.getId());

		// update indexes of the old player
		redis.srem(oldPlayer.getId() + COLON + PLAYED_ROLE, role.getId());
		redis.srem(oldPlayer.getId() + COLON + PLAYED_ASSOCIATION, role.getParent().getId());

		// update indexes of the new player
		redis.sadd(player.getId() + COLON + PLAYED_ROLE, role.getId());
		redis.sadd(player.getId() + COLON + PLAYED_ASSOCIATION, role.getParent().getId());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.PLAYER_MODIFIED, role, player, oldPlayer);
	}

	@Override
	protected void doModifyReifier(IReifiable r, ITopic reifier) throws TopicMapStoreException {
		ITopic oldReifier = doReadReification(r);

		if (reifier != null) {
			// save the new reifier to the reifiable
			redis.set(r.getId(), REIFIER, reifier.getId());
			// update indexes of the new reifier
			redis.set(reifier.getId(), REIFIED, r.getId());
		} else {
			redis.hdel(r.getId(), REIFIER);
		}

		if (oldReifier != null) {
			// update indexes of the old reifier
			redis.hdel(oldReifier.getId(), REIFIED);
		}
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.REIFIER_SET, r, reifier, oldReifier);
	}

	@Override
	protected void doModifyScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		IScope scope = internalReadScope(s);
		Set<ITopic> themes = HashUtil.getHashSet(scope.getThemes());
		themes.add(theme);
		IScope newScope = doCreateScope(getTopicMap(), themes);
		internalModifyScope(s, scope, newScope);
	}

	/**
	 * Internal method to modify the scope
	 * 
	 * @param s
	 *            the scoped construct
	 * @param oldScope
	 *            the old scope
	 * @param newScope
	 *            the new scope
	 */
	private void internalModifyScope(IScopable s, IScope oldScope, IScope newScope) {
		if (oldScope.equals(newScope)) {
			return;
		}
		if (s instanceof ICharacteristics) {
			redis.srem(SCOPED_CHARACTERISTICS + COLON + oldScope.getId(), s.getId());
			redis.sadd(SCOPED_CHARACTERISTICS + COLON + newScope.getId(), s.getId());
			if (s instanceof IName) {
				redis.srem(SCOPED_NAMES + COLON + oldScope.getId(), s.getId());
				redis.sadd(SCOPED_NAMES + COLON + newScope.getId(), s.getId());
			} else {
				redis.srem(SCOPED_OCCURRENCES + COLON + oldScope.getId(), s.getId());
				redis.sadd(SCOPED_OCCURRENCES + COLON + newScope.getId(), s.getId());
			}
		} else if (s instanceof IAssociation) {
			redis.srem(SCOPED_ASSOCIATIONS + COLON + oldScope.getId(), s.getId());
			redis.sadd(SCOPED_ASSOCIATIONS + COLON + newScope.getId(), s.getId());
		} else {
			redis.srem(SCOPED_VARIANTS + COLON + oldScope.getId(), s.getId());
			redis.sadd(SCOPED_VARIANTS + COLON + newScope.getId(), s.getId());
		}
		redis.set(s.getId(), SCOPE, newScope.getId());
		/*
		 * check if the scope should be removed
		 */
		// boolean keepScope = redis.exists(SCOPED_ASSOCIATIONS + COLON +
		// oldScope.getId())
		// || redis.exists(SCOPED_OCCURRENCES + COLON + oldScope.getId()) ||
		// redis.exists(SCOPED_NAMES + COLON + oldScope.getId())
		// || redis.exists(SCOPED_VARIANTS + COLON + oldScope.getId());
		// if (!keepScope) {
		// redis.del(oldScope.getId());
		// for (ITopic theme : oldScope.getThemes()) {
		// redis.srem(theme.getId() + IN_SCOPE, oldScope.getId());
		// }
		// }
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SCOPE_MODIFIED, s, newScope, oldScope);
	}

	@Override
	protected void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		modifySubjectIdentifier(t, subjectIdentifier.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_ADDED, t, subjectIdentifier, null);
	}

	private void modifySubjectIdentifier(ITopic t, String subjectIdentifier) throws TopicMapStoreException {
		// just add it. there's no problem if it exists.
		redis.sadd(t.getId() + COLON + SUBJECT_IDENTIFIER, subjectIdentifier);
		// get construct from item identifier
		redis.set(SUBJECT_IDENTIFIER + COLON + subjectIdentifier, t.getId());
	}

	@Override
	protected void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		modifySubjectLocator(t, subjectLocator.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_ADDED, t, subjectLocator, null);
	}

	private void modifySubjectLocator(ITopic t, String subjectLocator) throws TopicMapStoreException {
		// just add it. there's no problem if it exists.
		redis.sadd(t.getId() + COLON + SUBJECT_LOCATOR, subjectLocator);
		// get construct from item identifier
		redis.set(SUBJECT_LOCATOR + COLON + subjectLocator, t.getId());
	}

	@Override
	protected void doModifySupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		redis.sadd(t.getId() + COLON + SUPERTYPES, type.getId());
		redis.sadd(type.getId() + COLON + SUBTYPES, t.getId());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUPERTYPE_ADDED, t, type, null);
	}

	@Override
	protected void doModifyTag(ITopicMap tm, String tag) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doModifyType(ITypeable t, ITopic type) throws TopicMapStoreException {
		String typableId = t.getId();
		ITopic oldType = doReadType(t);
		redis.set(typableId, TYPE, type.getId());
		if (t instanceof IAssociation) {
			// association of the topic map
			redis.sadd(ASSOCIATION_OF_TOPICMAP + COLON + type.getId(), typableId);
			redis.srem(ASSOCIATION_OF_TOPICMAP + COLON + oldType.getId(), typableId);

			// typed associations
			redis.sadd(TYPED_ASSOCIATIONS + COLON + type.getId(), typableId);
			redis.srem(TYPED_ASSOCIATIONS + COLON + oldType.getId(), typableId);

			// update roles-by-assoc-type-index
			for (IAssociationRole role : doReadRoles((IAssociation) t)) {
				redis.sadd(ROLES_BY_ASSOCTYPE + COLON + type.getId(), role.getId());
				redis.srem(ROLES_BY_ASSOCTYPE + COLON + oldType.getId(), role.getId());
			}

		} else if (t instanceof IAssociationRole) {
			// role types in their association
			IAssociation association = ((IAssociationRole) t).getParent();
			redis.sadd(association + COLON + TYPE, type.getId());
			redis.srem(association + COLON + TYPE, oldType.getId());

			// role types in the typing topic
			redis.sadd(TYPED_ROLES + COLON + type.getId(), typableId);
			redis.srem(TYPED_ROLES + COLON + oldType.getId(), typableId);

		} else if (t instanceof IName) {
			// typed characteristics
			redis.sadd(TYPED_CHARACTERISTICS + COLON + type.getId(), typableId);
			redis.srem(TYPED_CHARACTERISTICS + COLON + oldType.getId(), typableId);
			// typed names
			redis.sadd(TYPED_NAMES + COLON + type.getId(), typableId);
			redis.srem(TYPED_NAMES + COLON + oldType.getId(), typableId);
		} else if (t instanceof IOccurrence) {
			// typed characteristics
			redis.sadd(TYPED_CHARACTERISTICS + COLON + type.getId(), typableId);
			redis.srem(TYPED_CHARACTERISTICS + COLON + oldType.getId(), typableId);
			// typed names
			redis.sadd(TYPED_OCCURRENCES + COLON + type.getId(), typableId);
			redis.srem(TYPED_OCCURRENCES + COLON + oldType.getId(), typableId);
		}
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TYPE_SET, t, type, oldType);
	}

	@Override
	protected void doModifyTopicType(ITopic t, ITopic type) throws TopicMapStoreException {
		redis.sadd(t.getId() + COLON + TYPES, type.getId());
		redis.sadd(type.getId() + COLON + INSTANCES, t.getId());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TYPE_ADDED, t, type, null);
	}

	@Override
	protected void doModifyValue(IName n, String value) throws TopicMapStoreException {
		String oldValue = redis.get(n.getId(), VALUE);
		redis.set(n.getId(), VALUE, value);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VALUE_MODIFIED, n, value, oldValue);
	}

	@Override
	protected void doModifyValue(IDatatypeAware t, String value) throws TopicMapStoreException {
		modifyValue(t, value, Namespaces.XSD.STRING);
	}

	@Override
	protected void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws TopicMapStoreException {
		modifyValue(t, value, datatype.getReference());
	}

	private void modifyValue(IDatatypeAware t, String value, String datatype) throws TopicMapStoreException {
		String oldValue = redis.get(t.getId(), VALUE);
		String oldDatatype = redis.get(t.getId(), DATATYPE);
		redis.set(t.getId(), VALUE, value);
		redis.set(t.getId(), DATATYPE, datatype);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VALUE_MODIFIED, t, value, oldValue);
		notifyListeners(TopicMapEventType.DATATYPE_SET, t, new LocatorImpl(datatype), new LocatorImpl(oldDatatype));
	}

	@Override
	protected void doModifyValue(IDatatypeAware t, Object value) throws TopicMapStoreException {
		String datatype = XmlSchemeDatatypes.javaToXsd(value.getClass());
		String v = DatatypeAwareUtils.toString(value, datatype);
		modifyValue(t, v, datatype);
	}

	@Override
	protected void doModifyMetaData(IRevision revision, String key, String value) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void doMergeTopics(ITopic context, ITopic other) throws TopicMapStoreException {
		MergeUtils.mergeTopics(this, redis, context, other);
		notifyListeners(TopicMapEventType.MERGE, getTopicMap(), context, other);
	}

	@Override
	protected void doMergeTopicMaps(TopicMap context, TopicMap other) throws TopicMapStoreException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException {
		removeItemIdentifier(c, itemIdentifier.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ITEM_IDENTIFIER_REMOVED, c, null, itemIdentifier);
	}

	private void removeItemIdentifier(IConstruct c, String itemIdentifier) throws TopicMapStoreException {
		// just remove it.
		redis.srem(c.getId() + COLON + ITEM_IDENTIFIER, itemIdentifier);
		// remove construct from item identifier
		redis.del(ITEM_IDENTIFIER + COLON + itemIdentifier);
	}

	@Override
	protected void doRemoveScope(IScopable s, ITopic theme) throws TopicMapStoreException {
		IScope scope = internalReadScope(s);
		Set<ITopic> themes = HashUtil.getHashSet(scope.getThemes());
		themes.remove(theme);
		IScope newScope = doCreateScope(getTopicMap(), themes);
		internalModifyScope(s, scope, newScope);
	}

	@Override
	protected void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException {
		removeSubjectIdentifier(t, subjectIdentifier.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED, t, null, subjectIdentifier);
	}

	private void removeSubjectIdentifier(ITopic t, String subjectIdentifier) throws TopicMapStoreException {
		// just remove it.
		redis.srem(t.getId() + COLON + SUBJECT_IDENTIFIER, subjectIdentifier);
		// remove construct from subject identifier
		redis.del(SUBJECT_IDENTIFIER + COLON + subjectIdentifier);
	}

	@Override
	protected void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException {
		removeSubjectLocator(t, subjectLocator.getReference());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUBJECT_LOCATOR_REMOVED, t, null, subjectLocator);
	}

	private void removeSubjectLocator(ITopic t, String subjectLocator) throws TopicMapStoreException {
		// just remove it.
		redis.srem(t.getId() + COLON + SUBJECT_LOCATOR, subjectLocator);
		// remove construct from subject locator
		redis.del(SUBJECT_LOCATOR + COLON + subjectLocator);
	}

	@Override
	protected void doRemoveSupertype(ITopic t, ITopic type) throws TopicMapStoreException {
		redis.srem(t.getId() + COLON + SUPERTYPES, type.getId());
		redis.srem(type.getId() + COLON + SUBTYPES, t.getId());
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.SUPERTYPE_REMOVED, t, null, type);
	}

	@Override
	protected void doRemoveType(ITopic t, ITopic type) throws TopicMapStoreException {
		redis.srem(t.getId() + COLON + TYPES, type.getId());
		redis.srem(type.getId() + COLON + INSTANCES, t.getId());/*
																 * notify
																 * listeners
																 */
		notifyListeners(TopicMapEventType.TYPE_REMOVED, t, null, type);
	}

	@Override
	protected void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws TopicMapStoreException {
		redis.clear();
	}

	@Override
	protected void doRemoveTopic(ITopic topic, boolean cascade) throws TopicMapStoreException {
		doRemoveTopic(topic, cascade, false);
	}

	void removeTopic(String id, boolean cascade, boolean fromReification) throws TopicMapStoreException {
		/*
		 * destroy reification
		 */
		if (!fromReification) {
			String r = redis.get(id, REIFIED);
			if (r != null) {
				redis.hdel(r, REIFIER);
			}
		}
		/*
		 * remove characteristics and played associations
		 */
		for (String n : redis.smembers(id + COLON + NAME)) {
			removeName(n, true);
		}
		for (String o : redis.smembers(id + COLON + OCCURRENCE)) {
			removeOccurrence(o, true);
		}
		for (String a : redis.smembers(id + COLON + PLAYED_ASSOCIATION)) {
			removeAssociation(a, true);
		}
		/*
		 * remove typed constructs
		 */
		for (String key : redis.list(TYPED_ASSOCIATIONS + COLON + id)) {
			for (String id_ : redis.smembers(key)) {
				removeAssociation(id_, true);
			}
		}
		for (String key : redis.list(TYPED_NAMES + COLON + id)) {
			for (String id_ : redis.smembers(key)) {
				removeName(id_, true);
			}
		}
		for (String key : redis.list(TYPED_OCCURRENCES + COLON + id)) {
			for (String id_ : redis.smembers(key)) {
				removeOccurrence(id_, true);
			}
		}
		for (String key : redis.list(TYPED_ROLES + COLON + id)) {
			for (String id_ : redis.smembers(key)) {
				removeRole(id_, true, false);
			}
		}
		redis.del(TYPED_NAMES + COLON + id, TYPED_OCCURRENCES + COLON + id, TYPED_ASSOCIATIONS + COLON + id, TYPED_ROLES + COLON + id,
				TYPED_CHARACTERISTICS + COLON + id);
		/*
		 * remove scoped constructs
		 */
		for (String key : redis.smembers(id + COLON + IN_SCOPE)) {
			for (String key_ : redis.list(SCOPED_ASSOCIATIONS + COLON + id)) {
				for (String id_ : redis.smembers(key_)) {
					removeAssociation(id_, true);
				}
			}
			for (String key_ : redis.list(SCOPED_NAMES + COLON + id)) {
				for (String id_ : redis.smembers(key_)) {
					removeName(id_, true);
				}
			}
			for (String key_ : redis.list(SCOPED_OCCURRENCES + COLON + id)) {
				for (String id_ : redis.smembers(key_)) {
					removeOccurrence(id_, true);
				}
			}
			redis.del(key, id + COLON + IN_SCOPE, SCOPED_ASSOCIATIONS + COLON + id, SCOPED_NAMES + COLON + id, SCOPED_OCCURRENCES + COLON + id,
					SCOPED_CHARACTERISTICS + COLON + id);
		}
		/*
		 * remove instances
		 */
		for (String key : redis.smembers(id + COLON + INSTANCES)) {
			removeTopic(key, true, false);
		}
		for (String type : redis.smembers(id + COLON + TYPES)) {
			redis.srem(type + COLON + INSTANCES, id);
		}
		/*
		 * remove sub types
		 */
		for (String key : redis.smembers(id + COLON + SUBTYPES)) {
			removeTopic(key, true, false);
		}
		for (String type : redis.smembers(id + COLON + SUPERTYPES)) {
			redis.srem(type + COLON + SUBTYPES, id);
		}
		redis.del(id + COLON + INSTANCES, id + COLON + SUBTYPES, id + COLON + TYPES, id + COLON + SUPERTYPES);
		/*
		 * remove identities
		 */
		removeItemIdentifiers(id);
		removeIdentifiers(id, SUBJECT_IDENTIFIER);
		removeIdentifiers(id, SUBJECT_LOCATOR);
		/*
		 * remove topic
		 */
		redis.srem(TOPICS_OF_TOPICMAP, id);
	}

	protected void doRemoveTopic(ITopic topic, boolean cascade, boolean fromReification) throws TopicMapStoreException {
		removeTopic(topic.getId(), cascade, fromReification);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.TOPIC_REMOVED, getTopicMap(), null, topic);
	}

	void removeName(String id, boolean cascade) throws TopicMapStoreException {
		String parentId = redis.get(id, PARENT);
		String typeId = redis.get(id, TYPE);
		String scopeId = redis.get(id, SCOPE);

		removeReifier(id, cascade);

		redis.del(id); // deletes Hash which contains PARENT, TYPE, VALUE, SCOPE
						// and REIFIER

		redis.srem(parentId + COLON + CHARACTERISTICS, id);
		redis.srem(parentId + COLON + NAME, id);
		// store typed characteristics
		redis.srem(TYPED_CHARACTERISTICS + COLON + typeId, id);
		redis.srem(TYPED_NAMES + COLON + typeId, id);
		// store scoped characteristics
		redis.srem(SCOPED_CHARACTERISTICS + COLON + scopeId, id);
		redis.srem(SCOPED_NAMES + COLON + scopeId, id);

		removeItemIdentifiers(id);
	}

	@Override
	protected void doRemoveName(IName name, boolean cascade) throws TopicMapStoreException {
		removeName(name.getId(), cascade);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.NAME_REMOVED, name.getParent(), null, name);
	}

	void removeOccurrence(String id, boolean cascade) throws TopicMapStoreException {
		String parentId = redis.get(id, PARENT);
		String typeId = redis.get(id, TYPE);
		String scopeId = redis.get(id, SCOPE);

		removeReifier(id, cascade);

		redis.del(id); // deletes Hash which contains PARENT, TYPE, VALUE,
						// DATATYPE, SCOPE and REIFIER

		redis.srem(parentId + COLON + CHARACTERISTICS, id);
		redis.srem(parentId + COLON + OCCURRENCE, id);
		// store typed characteristics
		redis.srem(TYPED_CHARACTERISTICS + COLON + typeId, id);
		redis.srem(TYPED_OCCURRENCES + COLON + typeId, id);
		// store scoped characteristics
		redis.srem(SCOPED_CHARACTERISTICS + COLON + scopeId, id);
		redis.srem(SCOPED_OCCURRENCES + COLON + scopeId, id);

		removeItemIdentifiers(id);
	}

	@Override
	protected void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws TopicMapStoreException {
		removeOccurrence(occurrence.getId(), cascade);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.OCCURRENCE_REMOVED, occurrence.getParent(), null, occurrence);
	}

	void removeAssociation(String id, boolean cascade) throws TopicMapStoreException {
		final String scope = redis.get(id, SCOPE);
		final String type = redis.get(id, TYPE);

		removeReifier(id, cascade);
		for (String role : redis.smembers(id + COLON + ROLE)) {
			removeRole(role, cascade, true);
		}

		redis.del(id); // deletes Hash which contains PARENT, TYPE, SCOPE and
						// REIFIER

		removeItemIdentifiers(id);
		redis.srem(SCOPED_ASSOCIATIONS + COLON + scope, id);
		redis.srem(TYPED_ASSOCIATIONS + COLON + type, id);
		redis.srem(ASSOCIATION_OF_TOPICMAP, id);
	}

	@Override
	protected void doRemoveAssociation(IAssociation association, boolean cascade) throws TopicMapStoreException {
		removeAssociation(association.getId(), cascade);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ASSOCIATION_REMOVED, getTopicMap(), null, association);
	}

	void removeRole(String id, boolean cascade, boolean calledFromAssociation) throws TopicMapStoreException {
		String parentId = redis.get(id, PARENT);
		String typeId = redis.get(id, TYPE);
		String playerId = redis.get(id, PLAYER);
		String parentTypeId = redis.get(parentId, TYPE);

		removeReifier(id, cascade);

		redis.del(id); // deletes Hash which contains PARENT, TYPE, PLAYER and
						// REIFIER

		if (!calledFromAssociation) {
			redis.srem(parentId + COLON + ROLE, id);
			// add role player
			redis.srem(parentId + COLON + PLAYER, playerId);
			/*
			 * check if role type should be removed
			 */
			boolean delete = true;
			for (String r : redis.smembers(parentId + COLON + ROLE)) {
				if (id != r && typeId.equalsIgnoreCase(redis.get(r, TYPE))) {
					delete = false;
					break;
				}
			}
			if (delete) {
				redis.srem(parentId + COLON + TYPE, typeId);
			}
		}

		redis.srem(TYPED_ROLES + COLON + typeId, id);
		redis.srem(ROLES_BY_ASSOCTYPE + COLON + parentTypeId, id);
		// add played role
		redis.srem(playerId + COLON + PLAYED_ROLE, id);
		// add played association
		redis.srem(playerId + COLON + PLAYED_ASSOCIATION, parentId);
		removeItemIdentifiers(id);
	}

	private void removeRole(IAssociationRole role, boolean cascade, boolean calledFromAssociation) throws TopicMapStoreException {
		removeRole(role.getId(), cascade, calledFromAssociation);
	}

	@Override
	protected void doRemoveRole(IAssociationRole role, boolean cascade) throws TopicMapStoreException {
		removeRole(role, cascade, false);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.ROLE_REMOVED, role.getParent(), null, role);
	}

	@Override
	protected void doRemoveVariant(IVariant variant, boolean cascade) throws TopicMapStoreException {
		removeVariant(variant.getId(), cascade);
		/*
		 * notify listeners
		 */
		notifyListeners(TopicMapEventType.VARIANT_REMOVED, variant.getParent(), null, variant);
	}

	void removeVariant(String id, boolean cascade) throws TopicMapStoreException {
		String parentId = redis.get(id, PARENT);
		String scopeId = redis.get(id, SCOPE);

		removeReifier(id, cascade);

		redis.del(id); // deletes Hash which contains PARENT, TYPE, VALUE,
						// DATATYPE, SCOPE and REIFIER

		redis.srem(parentId + COLON + VARIANT, id);

		redis.srem(SCOPED_VARIANTS + COLON + scopeId, id);

		removeItemIdentifiers(id);
	}

	/**
	 * This removes the reifier from a Reifiable but does NOT remove it from the
	 * hash in the reifiable which is given as parameter
	 * 
	 * @param r
	 *            the reifiable construct id
	 * @param cascade
	 * @throws TopicMapStoreException
	 */
	private void removeReifier(String id, boolean cascade) throws TopicMapStoreException {
		String reifier = redis.get(id, REIFIER);
		if (reifier == null) {
			removeTopic(id, cascade, true);
		}
	}

	/**
	 * This removes the reifier from a Reifiable but does NOT remove it from the
	 * hash in the reifiable which is given as parameter
	 * 
	 * @param r
	 *            the reifiable construct
	 * @param cascade
	 * @throws TopicMapStoreException
	 */
	protected void doRemoveReifier(IReifiable r, boolean cascade) throws TopicMapStoreException {
		removeReifier(r.getId(), cascade);
	}

	/**
	 * Removes all item-identifiers of the construct
	 * 
	 * @param c
	 *            the construct
	 * @throws TopicMapStoreException
	 */
	private void removeItemIdentifiers(String id) throws TopicMapStoreException {
		removeIdentifiers(id, ITEM_IDENTIFIER);
	}

	/**
	 * Removes all identifiers of the construct
	 * 
	 * @param c
	 *            the construct
	 * @param TYPE
	 *            constant defines the key part of redis
	 * @throws TopicMapStoreException
	 */
	private void removeIdentifiers(String id, final String TYPE) throws TopicMapStoreException {
		Set<String> iis = redis.smembers(id + COLON + TYPE);
		String[] keys = new String[iis.size() + 1];
		int i = 0;
		for (String ii : iis) {
			keys[i++] = TYPE + COLON + ii;
		}
		keys[iis.size()] = id + COLON + TYPE;
		redis.del(keys);
	}

	@Override
	public void storeRevision(IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected IRevision createRevision(TopicMapEventType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected ILocator doCreateLocator(ITopicMap topicMap, String reference) throws TopicMapStoreException {
		return new LocatorImpl(reference);
	}

	@Override
	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws TopicMapStoreException {
		if (themes.isEmpty()) {
			return EMPTY_SCOPE;
		}
		/*
		 * cache look up
		 */
		if (scopeCache.containsKey(themes)) {
			return scopeCache.get(themes);
		}
		String[] keys = new String[themes.size()];
		int i = 0;
		for (ITopic theme : themes) {
			keys[i++] = theme.getId() + IN_SCOPE;
		}
		Set<String> ids = redis.sinter(keys);
		/*
		 * reuse existing
		 */
		if (!ids.isEmpty()) {
			for (String id : ids) {
				Set<String> themeIds = redis.smembers(id);
				if (themeIds.size() == themes.size()) {
					IScope s = new ScopeImpl(id, themes);
					scopeCache.put(themes, s);
					return s;
				}
			}
		}

		/*
		 * generate new scope id
		 */
		final String id = getNewRedisId(SCOPE_COLON);
		/*
		 * add new scope to themes
		 */
		for (String key : keys) {
			redis.sadd(key, id);
		}
		/*
		 * store themes for scope
		 */
		for (ITopic t : themes) {
			redis.sadd(id, t.getId());
		}
		IScope s = new ScopeImpl(id, themes);
		scopeCache.put(themes, s);
		return s;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopic t) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(t.getId() + COLON + PLAYED_ASSOCIATION);
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + PLAYED_ASSOCIATION, TYPED_ASSOCIATIONS + COLON + type.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + PLAYED_ASSOCIATION, TYPED_ASSOCIATIONS + COLON + type.getId(), SCOPED_ASSOCIATIONS
				+ COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + PLAYED_ASSOCIATION, SCOPED_ASSOCIATIONS + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopicMap tm) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(ASSOCIATION_OF_TOPICMAP);
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(TYPED_ASSOCIATIONS + COLON + type.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(TYPED_ASSOCIATIONS + COLON + type.getId(), SCOPED_ASSOCIATIONS + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws TopicMapStoreException {
		Set<IAssociation> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(SCOPED_ASSOCIATIONS + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<ICharacteristics> doReadCharacteristics(ITopic t) throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(t.getId() + COLON + CHARACTERISTICS);
		for (String key : keys) {
			if (key.startsWith(NAME + COLON)) {
				set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
			} else {
				set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
			}
		}
		return set;
	}

	@Override
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + CHARACTERISTICS, TYPED_CHARACTERISTICS + COLON + type.getId());
		for (String key : keys) {
			if (key.startsWith(NAME + COLON)) {
				set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
			} else {
				set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
			}
		}
		return set;
	}

	@Override
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + CHARACTERISTICS, TYPED_CHARACTERISTICS + COLON + type.getId(), SCOPED_CHARACTERISTICS
				+ COLON + scope.getId());
		for (String key : keys) {
			if (key.startsWith(NAME + COLON)) {
				set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
			} else {
				set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
			}
		}
		return set;
	}

	@Override
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + CHARACTERISTICS, SCOPED_CHARACTERISTICS + COLON + scope.getId());
		for (String key : keys) {
			if (key.startsWith(NAME + COLON)) {
				set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
			} else {
				set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
			}
		}
		return set;
	}

	@Override
	public IConstruct doReadConstruct(ITopicMap tm, String id) throws TopicMapStoreException {
		if (id.equalsIgnoreCase(getTopicMapIdentity().getId())) {
			return getTopicMap();
		}
		if (id.startsWith(ASSOCIATION + COLON)) {
			return getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap());
		} else if (id.startsWith(ROLE + COLON)) {
			final String parent = redis.get(id, PARENT);
			return getConstructFactory().newAssociationRole(new RedisStoreIdentity(id),
					getConstructFactory().newAssociation(new RedisStoreIdentity(parent), getTopicMap()));
		} else if (id.startsWith(TOPIC + COLON)) {
			return getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMap());
		} else if (id.startsWith(NAME + COLON)) {
			final String parent = redis.get(id, PARENT);
			return getConstructFactory().newName(new RedisStoreIdentity(id),
					getConstructFactory().newTopic(new RedisStoreIdentity(parent), getTopicMap()));
		} else if (id.startsWith(OCCURRENCE + COLON)) {
			final String parent = redis.get(id, PARENT);
			return getConstructFactory().newOccurrence(new RedisStoreIdentity(id),
					getConstructFactory().newTopic(new RedisStoreIdentity(parent), getTopicMap()));
		} else if (id.startsWith(VARIANT + COLON)) {
			final String parentNameId = redis.get(id, PARENT);
			final String parentsTopicId = redis.get(parentNameId, PARENT);
			final ITopic parentsTopic = getConstructFactory().newTopic(new RedisStoreIdentity(parentsTopicId), getTopicMap());
			final IName parentName = getConstructFactory().newName(new RedisStoreIdentity(parentNameId), parentsTopic);
			return getConstructFactory().newVariant(new RedisStoreIdentity(id), parentName);

		}
		return null;
	}

	@Override
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws TopicMapStoreException {
		String construct = redis.get(ITEM_IDENTIFIER + COLON + itemIdentifier.getReference());
		if (construct == null) {
			return null;
		}
		return doReadConstruct(t, construct);
	}

	@Override
	public ILocator doReadDataType(IDatatypeAware d) throws TopicMapStoreException {
		return new LocatorImpl(redis.get(d.getId(), DATATYPE));

	}

	@Override
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		if (c == getTopicMap()) {
			return getTopicMapIdentity().getId();
		}
		return c.getId();
	}

	@Override
	public Set<ILocator> doReadItemIdentifiers(IConstruct c) throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet();
		for (String ii : redis.smembers(c.getId() + COLON + ITEM_IDENTIFIER)) {
			set.add(new LocatorImpl(ii));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	@Override
	public Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		Set<IName> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(t.getId() + COLON + NAME);
		for (String key : keys) {
			set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IName> doReadNames(ITopic t, ITopic type) throws TopicMapStoreException {
		Set<IName> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + NAME, TYPED_NAMES + COLON + type.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		Set<IName> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + NAME, TYPED_NAMES + COLON + type.getId(), SCOPED_NAMES + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IName> doReadNames(ITopic t, IScope scope) throws TopicMapStoreException {
		Set<IName> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + NAME, SCOPED_NAMES + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newName(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public IRevision doReadFutureRevision(IRevision r) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<IOccurrence> doReadOccurrences(ITopic t) throws TopicMapStoreException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(t.getId() + COLON + OCCURRENCE);
		for (String key : keys) {
			set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws TopicMapStoreException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + OCCURRENCE, TYPED_OCCURRENCES + COLON + type.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + OCCURRENCE, TYPED_OCCURRENCES + COLON + type.getId(),
				SCOPED_OCCURRENCES + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws TopicMapStoreException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(t.getId() + COLON + OCCURRENCE, SCOPED_OCCURRENCES + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newOccurrence(new RedisStoreIdentity(key), t));
		}
		return set;
	}

	@Override
	public ITopic doReadPlayer(IAssociationRole role) throws TopicMapStoreException {
		return getConstructFactory().newTopic(new RedisStoreIdentity(redis.get(role.getId(), PLAYER)), getTopicMap());
	}

	@Override
	public IRevision doReadPastRevision(IRevision r) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		String reifier = redis.get(t.getId(), REIFIED);
		if (reifier == null) {
			return null;
		}
		return (IReifiable) doReadConstruct(getTopicMap(), reifier);
	}

	@Override
	public ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		String reifier = redis.get(r.getId(), REIFIER);
		if (reifier == null) {
			return null;
		}
		return getConstructFactory().newTopic(new RedisStoreIdentity(reifier), getTopicMap());
	}

	@Override
	public Calendar doReadRevisionTimestamp(IRevision r) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<IAssociationRole> doReadRoles(IAssociation association) throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (String key : redis.smembers(association.getId() + COLON + ROLE)) {
			set.add(getConstructFactory().newAssociationRole(new RedisStoreIdentity(key), association));
		}
		return set;
	}

	@Override
	public Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (String key : redis.sinter(association.getId() + COLON + ROLE, TYPED_ROLES + COLON + type.getId())) {
			set.add(getConstructFactory().newAssociationRole(new RedisStoreIdentity(key), association));
		}
		return set;
	}

	@Override
	public Set<IAssociationRole> doReadRoles(ITopic player) throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (String key : redis.smembers(player.getId() + COLON + PLAYED_ROLE)) {
			final String a = redis.get(key, PARENT);
			set.add(getConstructFactory().newAssociationRole(new RedisStoreIdentity(key),
					getConstructFactory().newAssociation(new RedisStoreIdentity(a), getTopicMap())));
		}
		return set;
	}

	@Override
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (String key : redis.sinter(player.getId() + COLON + PLAYED_ROLE, TYPED_ROLES + COLON + type.getId())) {
			final String a = redis.get(key, PARENT);
			set.add(getConstructFactory().newAssociationRole(new RedisStoreIdentity(key),
					getConstructFactory().newAssociation(new RedisStoreIdentity(a), getTopicMap())));
		}
		return set;
	}

	@Override
	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws TopicMapStoreException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (String key : redis.sinter(player.getId() + COLON + PLAYED_ROLE, TYPED_ROLES + COLON + type.getId(), ROLES_BY_ASSOCTYPE + COLON
				+ assocType.getId())) {
			final String a = redis.get(key, PARENT);
			set.add(getConstructFactory().newAssociationRole(new RedisStoreIdentity(key),
					getConstructFactory().newAssociation(new RedisStoreIdentity(a), getTopicMap())));
		}
		return set;
	}

	@Override
	public Set<ITopic> doReadRoleTypes(IAssociation association) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet();
		for (String key : redis.smembers(association.getId() + COLON + TYPE)) {
			set.add(getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet();
		for (String si : redis.smembers(t.getId() + COLON + SUBJECT_IDENTIFIER)) {
			set.add(new LocatorImpl(si));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	@Override
	public Set<ILocator> doReadSubjectLocators(ITopic t) throws TopicMapStoreException {
		Set<ILocator> set = HashUtil.getHashSet();
		for (String sl : redis.smembers(t.getId() + COLON + SUBJECT_LOCATOR)) {
			set.add(new LocatorImpl(sl));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	@Override
	public Collection<ITopic> doReadSuptertypes(ITopic t) throws TopicMapStoreException {
		ISupertypeSubtypeIndex index = getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic st : index.getSupertypes(t)) {
			set.add((ITopic) st);
		}
		return set;
	}

	@Override
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws TopicMapStoreException {
		final String id = redis.get(SUBJECT_IDENTIFIER + COLON + subjectIdentifier.getReference());
		if (id != null) {
			return getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMap());
		}
		return null;
	}

	@Override
	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws TopicMapStoreException {
		final String id = redis.get(SUBJECT_LOCATOR + COLON + subjectLocator.getReference());
		if (id != null) {
			return getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMap());
		}
		return null;
	}

	@Override
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet();
		for (String key : redis.smembers(TOPICS_OF_TOPICMAP)) {
			set.add(getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet();
		for (String key : redis.sinter(TOPICS_OF_TOPICMAP, type.getId() + COLON + INSTANCES)) {
			set.add(getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		return getConstructFactory().newTopic(new RedisStoreIdentity(redis.get(typed.getId(), TYPE)), getTopicMap());
	}

	@Override
	public TopicMapEventType doReadChangeSetType(IRevision revision) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		Set<ITopic> set = HashUtil.getHashSet();
		for (String key : redis.smembers(t.getId() + COLON + TYPES)) {
			set.add(getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMap()));
		}
		return set;
	}

	@Override
	public IScope doReadScope(IScopable s) throws TopicMapStoreException {
		IScope scope = internalReadScope(s);
		if (s instanceof IVariant) {
			Collection<ITopic> themes = HashUtil.getHashSet(scope.getThemes());
			themes.addAll(doReadScope((IScopable) s.getParent()).getThemes());
			scope = new ScopeImpl(scope.getId(), themes);
		}
		return scope;
	}

	/**
	 * Internal method to read the scope of a construct. The scope will returned
	 * without the special variant handling.
	 * 
	 * @param s
	 *            the scoped construct
	 * @return the scope
	 * @throws TopicMapStoreException
	 */
	private IScope internalReadScope(IScopable s) throws TopicMapStoreException {
		final String scopeId = redis.get(s.getId(), SCOPE);
		Set<ITopic> themes = HashUtil.getHashSet();
		for (String key : redis.smembers(scopeId)) {
			themes.add(getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMap()));
		}
		return new ScopeImpl(scopeId, themes);
	}

	@Override
	public Object doReadValue(IName n) throws TopicMapStoreException {
		return redis.get(n.getId(), VALUE);
	}

	@Override
	public Object doReadValue(IDatatypeAware t) throws TopicMapStoreException {
		return redis.get(t.getId(), VALUE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T doReadValue(IDatatypeAware t, Class<T> type) throws TopicMapStoreException {
		try {
			return (T) DatatypeAwareUtils.toValue(redis.get(t.getId(), VALUE), type);
		} catch (NumberFormatException e) {
			throw new TopicMapStoreException(e);
		} catch (URISyntaxException e) {
			throw new TopicMapStoreException(e);
		} catch (ParseException e) {
			throw new TopicMapStoreException(e);
		}
	}

	@Override
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		Set<IVariant> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(n.getId() + COLON + VARIANT);
		for (String key : keys) {
			set.add(getConstructFactory().newVariant(new RedisStoreIdentity(key), n));
		}
		return set;
	}

	@Override
	public Set<IVariant> doReadVariants(IName n, IScope scope) throws TopicMapStoreException {
		Set<IVariant> set = HashUtil.getHashSet();
		Set<String> keys = redis.sinter(n.getId() + COLON + VARIANT, SCOPED_VARIANTS + COLON + scope.getId());
		for (String key : keys) {
			set.add(getConstructFactory().newVariant(new RedisStoreIdentity(key), n));
		}
		return set;
	}

	@Override
	public Map<String, String> doReadMetaData(IRevision revision) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String doReadMetaData(IRevision revision, String key) throws TopicMapStoreException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		return BestLabelUtils.doReadBestLabel(topic);
	}

	@Override
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws TopicMapStoreException {
		return BestLabelUtils.doReadBestLabel(topic, theme, strict);
	}

	@Override
	public String doReadBestIdentifier(ITopic topic, boolean withPrefix) {
		return BestLabelUtils.doReadBestIdentifier(topic, withPrefix);
	}

	@Override
	public long generateId() {
		return redis.nextId();
	}

	@Override
	public void clear() {
		redis.clear();
	}

	/**
	 * Genrates a new redis id with the prefix
	 * 
	 * @param type
	 *            the prefix of type
	 * @return the new string id
	 */
	public String getNewRedisId(final String type) {	
//		if ( TOPICMAP.equalsIgnoreCase(type)){
			return type + Long.toHexString(generateId()) + COLON + HASHPOSTFIX;
//		}
//		return getTopicMapBaseLocatorHash() + type + Long.toHexString(generateId()) + COLON + HASHPOSTFIX;
	}
	/**
	 * Convert the given topic map locator to a hash which can be used as prefix for topic maps
	 * @return the hash
	 */
	public String getTopicMapBaseLocatorHash() {		
		return TOPICMAP + COLON + super.getTopicMapBaseLocatorReference().hashCode();
	}

	// XXX hide! visibility protected?
	public RedisHandler getRedis() {
		return redis;
	}

	@Override
	public boolean isCachingEnabled() {
		return false;
	}

	@Override
	public void commit() {
		// NOTHING TO DO
	}

	@Override
	public void removeDuplicates() {
		MergeUtils.removeDuplicates(this, redis);
	}

}
