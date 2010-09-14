package de.topicmapslab.majortom.database.cache;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.core.ConstructImpl;
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
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.store.ReadOnlyTopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;

public class Cache extends ReadOnlyTopicMapStoreImpl {

	private ConstructCache cache;

	/**
	 * reference to the parent store
	 */
	private final ReadOnlyTopicMapStoreImpl parentStore;

	/**
	 * constructor
	 * 
	 * @param parentStore
	 *            the parent store
	 */
	public Cache(ReadOnlyTopicMapStoreImpl parentStore) {
		this.parentStore = parentStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(t, null, null);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(t);
			cache.cacheAssociation(t, null, null, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(t, type, null);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(t, type);
			cache.cacheAssociation(t, type, null, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(t, null, scope);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(t, scope);
			cache.cacheAssociation(t, null, scope, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(t, type, scope);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(t, type, scope);
			cache.cacheAssociation(t, type, scope, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(tm, null, null);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(tm);
			cache.cacheAssociation(tm, null, null, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(tm, type, null);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(tm, type);
			cache.cacheAssociation(tm, type, null, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope)
			throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(tm, null, scope);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(tm, scope);
			cache.cacheAssociation(tm, null, scope, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type,
			IScope scope) throws TopicMapStoreException {
		Set<IAssociation> associations = cache.getAssociation(tm, type, scope);
		if (associations == null) {
			associations = getParentStore().doReadAssociation(tm, type, scope);
			cache.cacheAssociation(tm, type, scope, associations);
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t)
			throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t));
		set.addAll(doReadOccurrences(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type)
			throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type));
		set.addAll(doReadOccurrences(t, type));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope)
			throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, scope));
		set.addAll(doReadOccurrences(t, scope));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type,
			IScope scope) throws TopicMapStoreException {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(doReadNames(t, type, scope));
		set.addAll(doReadOccurrences(t, type, scope));
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
		IConstruct c = cache.getIdentityCache().byId(id);
		if (c == null) {
			c = getParentStore().doReadConstruct(t, id);
			if (c != null) {
				cache.getIdentityCache().cacheId(id, c);
			}
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier)
			throws TopicMapStoreException {
		IConstruct c = cache.getIdentityCache()
				.byItemIdentifier(itemIdentifier);
		if (c == null) {
			c = getParentStore().doReadConstruct(t, itemIdentifier);
			if (c != null) {
				cache.getIdentityCache().cacheItemIdentifier(itemIdentifier, c);
			}
		}
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadDataType(IDatatypeAware c)
			throws TopicMapStoreException {
		ILocator locator = cache.getCharacteristicsCache().getDatatype(c);
		if (locator == null) {
			locator = getParentStore().doReadDataType(c);
			cache.getCharacteristicsCache().cacheDatatype(c, locator);
		}
		return locator;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		if ( c instanceof TopicMap ){
			return getParentStore().doReadId(c);
		}
		if (c instanceof ConstructImpl) {
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
		Set<ILocator> set = cache.getIdentityCache().getItemIdentifiers(c);
		if (set == null) {
			set = getParentStore().doReadItemIdentifiers(c);
			cache.getIdentityCache().cacheItemIdentifiers(c, set);
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException {
		return getParentStore().doReadLocator(t);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t) throws TopicMapStoreException {
		Set<IName> names = cache.getNames(t, null, null);
		if (names == null) {
			names = getParentStore().doReadNames(t);
			cache.cacheNames(t, null, null, names);
		}
		if (names.isEmpty()) {
			return Collections.emptySet();
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, ITopic type)
			throws TopicMapStoreException {
		Set<IName> names = cache.getNames(t, type, null);
		if (names == null) {
			names = getParentStore().doReadNames(t, type);
			cache.cacheNames(t, type, null, names);
		}
		if (names.isEmpty()) {
			return Collections.emptySet();
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> doReadNames(ITopic t, IScope scope)
			throws TopicMapStoreException {
		Set<IName> names = cache.getNames(t, null, scope);
		if (names == null) {
			names = getParentStore().doReadNames(t, scope);
			cache.cacheNames(t, null, scope, names);
		}
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
		Set<IName> names = cache.getNames(t, type, scope);
		if (names == null) {
			names = getParentStore().doReadNames(t, type, scope);
			cache.cacheNames(t, type, scope, names);
		}
		if (names.isEmpty()) {
			return Collections.emptySet();
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t)
			throws TopicMapStoreException {
		Set<IOccurrence> occurrences = cache.getOccurrences(t, null, null);
		if (occurrences == null) {
			occurrences = getParentStore().doReadOccurrences(t);
			cache.cacheOccurrences(t, null, null, occurrences);
		}
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

		Set<IOccurrence> occurrences = cache.getOccurrences(t, type, null);
		if (occurrences == null) {
			occurrences = getParentStore().doReadOccurrences(t, type);
			cache.cacheOccurrences(t, type, null, occurrences);
		}
		if (occurrences.isEmpty()) {
			return Collections.emptySet();
		}
		return occurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope)
			throws TopicMapStoreException {
		Set<IOccurrence> occurrences = cache.getOccurrences(t, null, scope);
		if (occurrences == null) {
			occurrences = getParentStore().doReadOccurrences(t, scope);
			cache.cacheOccurrences(t, null, scope, occurrences);
		}
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
		Set<IOccurrence> occurrences = cache.getOccurrences(t, type, scope);
		if (occurrences == null) {
			occurrences = getParentStore().doReadOccurrences(t, type, scope);
			cache.cacheOccurrences(t, type, scope, occurrences);
		}
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
		ITopic player = cache.getAssociationCache().getPlayer(role);
		if (player == null) {
			player = getParentStore().doReadPlayer(role);
			cache.getAssociationCache().cachePlayer(role, player);
		}
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable doReadReification(ITopic t) throws TopicMapStoreException {
		IReifiable reifiable = cache.getReificationCache().getReified(t);
		if (reifiable == null) {
			reifiable = getParentStore().doReadReification(t);
			if (reifiable != null) {
				cache.getReificationCache().cacheReification(reifiable, t);
			}
		}
		return reifiable;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadReification(IReifiable r) throws TopicMapStoreException {
		ITopic reifier = cache.getReificationCache().getReifier(r);
		if (reifier == null) {
			reifier = getParentStore().doReadReification(r);
			if (reifier != null) {
				cache.getReificationCache().cacheReification(r, reifier);
			}
		}
		return reifier;
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
			set.add(doReadType(r));
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
		Set<IAssociationRole> roles = cache.getRoles(association, null);
		if (roles == null) {
			roles = getParentStore().doReadRoles(association);
			cache.cacheRoles(association, null, roles);
		}
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociationRole> doReadRoles(IAssociation association,
			ITopic type) throws TopicMapStoreException {
		Set<IAssociationRole> roles = cache.getRoles(association, type);
		if (roles == null) {
			roles = getParentStore().doReadRoles(association, type);
			cache.cacheRoles(association, type, roles);
		}
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
		Set<IAssociationRole> roles = cache.getRoles(player, null);
		if (roles == null) {
			roles = getParentStore().doReadRoles(player);
			cache.cacheRoles(player, null, roles);
		}
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
		Set<IAssociationRole> roles = cache.getRoles(player, type);
		if (roles == null) {
			roles = getParentStore().doReadRoles(player, type);
			cache.cacheRoles(player, type, roles);
		}
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
		IScope scope = cache.getScopeCache().getScope(s);
		if (scope == null) {
			scope = getParentStore().doReadScope(s);
			cache.getScopeCache().cacheScope(s, scope);
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ILocator> doReadSubjectIdentifiers(ITopic t)
			throws TopicMapStoreException {
		Set<ILocator> set = cache.getIdentityCache().getSubjectIdentifiers(t);
		if (set == null) {
			set = getParentStore().doReadSubjectIdentifiers(t);
			cache.getIdentityCache().cacheSubjectIdentifiers(t, set);
		}
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
		Set<ILocator> set = cache.getIdentityCache().getSubjectLocators(t);
		if (set == null) {
			set = getParentStore().doReadSubjectLocators(t);
			cache.getIdentityCache().cacheSubjectLocators(t, set);
		}
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
		Set<ITopic> set = cache.getTopicTypeCache().getDirectSupertypes(t);
		if (set == null) {
			set = HashUtil.getHashSet(getParentStore().doReadSuptertypes(t));
			cache.getTopicTypeCache().cacheSupertypes(t, set);
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectIdentifier(ITopicMap tm,
			ILocator subjectIdentifier) throws TopicMapStoreException {
		ITopic t = cache.getIdentityCache().bySubjectIdentifier(
				subjectIdentifier);
		if (t == null) {
			t = getParentStore().doReadTopicBySubjectIdentifier(tm,
					subjectIdentifier);
			if (t != null) {
				cache.getIdentityCache().cacheSubjectIdentifier(
						subjectIdentifier, t);
			}
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadTopicBySubjectLocator(ITopicMap tm,
			ILocator subjectLocator) throws TopicMapStoreException {
		ITopic t = cache.getIdentityCache().bySubjectLocator(subjectLocator);
		if (t == null) {
			t = getParentStore()
					.doReadTopicBySubjectLocator(tm, subjectLocator);
			if (t != null) {
				cache.getIdentityCache().cacheSubjectLocator(subjectLocator, t);
			}
		}
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException {
		Set<ITopic> topics = cache.getTopics(null);
		if (topics == null) {
			topics = getParentStore().doReadTopics(t);
			cache.cacheTopics(null, topics);
		}
		if (topics.isEmpty()) {
			return Collections.emptySet();
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type)
			throws TopicMapStoreException {
		Set<ITopic> topics = cache.getTopics(type);
		if (topics == null) {
			topics = getParentStore().doReadTopics(t, type);
			cache.cacheTopics(type, topics);
		}
		if (topics.isEmpty()) {
			return Collections.emptySet();
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic doReadType(ITypeable typed) throws TopicMapStoreException {
		ITopic type = cache.getTypedCache().getType(typed);
		if (type == null) {
			type = getParentStore().doReadType(typed);
			cache.getTypedCache().cacheType(typed, type);
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException {
		Set<ITopic> set = cache.getTopicTypeCache().getDirectTypes(t);
		if (set == null) {
			set = getParentStore().doReadTypes(t);
			cache.getTopicTypeCache().cacheTypes(t, set);
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IDatatypeAware c) throws TopicMapStoreException {
		Object value = cache.getCharacteristicsCache().getValueAsString(c);
		if (value == null) {
			value = getParentStore().doReadValue(c);
			cache.getCharacteristicsCache().cacheValue(c, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T> T doReadValue(IDatatypeAware c, Class<T> type)
			throws TopicMapStoreException {
		Object value = cache.getCharacteristicsCache().getValue(c);
		if (value == null) {
			value = getParentStore().doReadValue(c, type);
			cache.getCharacteristicsCache().cacheValue(c, value);
		} else {
			try {
				value = DatatypeAwareUtils.toValue(value, type);
			} catch (NumberFormatException e) {
				throw new TopicMapStoreException(
						"Cannot convert to numeric value!", e);
			} catch (URISyntaxException e) {
				throw new TopicMapStoreException(
						"Cannot convert to URI value!", e);
			} catch (ParseException e) {
				throw new TopicMapStoreException(
						"Cannot convert to specified value!", e);
			}
		}
		return (T) value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doReadValue(IName n) throws TopicMapStoreException {
		Object value = cache.getCharacteristicsCache().getValueAsString(n);
		if (value == null) {
			value = getParentStore().doReadValue(n);
			cache.getCharacteristicsCache().cacheValue(n, value);
		}
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException {
		Set<IVariant> variants = cache.getVariants(n, null);
		if (variants == null) {
			variants = getParentStore().doReadVariants(n);
			cache.cacheVariants(n, null, variants);
		}
		if (variants.isEmpty()) {
			return Collections.emptySet();
		}
		return variants;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> doReadVariants(IName n, IScope scope)
			throws TopicMapStoreException {
		Set<IVariant> variants = cache.getVariants(n, scope);
		if (variants == null) {
			variants = getParentStore().doReadVariants(n, scope);
			cache.cacheVariants(n, scope, variants);
		}
		if (variants.isEmpty()) {
			return Collections.emptySet();
		}
		return variants;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator)
			throws TopicMapStoreException {
		cache = new ConstructCache();
		getParentStore().addTopicMapListener(cache);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		getParentStore().removeTopicMapListener(cache);
		cache.clear();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public <I extends Index> I getIndex(Class<I> clazz) {
		throw new UnsupportedOperationException("Index class "
				+ clazz.getSimpleName()
				+ " not supported by the this cache implementation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap() {
		return parentStore.getTopicMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadFutureRevision(IRevision r)
			throws TopicMapStoreException {
		IRevision revision = cache.getRevisionCache().getFutureRevision(r);
		if ( revision == null ){
			revision = getParentStore().doReadFutureRevision(r);
			if ( revision != null ){
				cache.getRevisionCache().cacheFutureRevision(r, revision);
			}
		}
		return revision;
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision doReadPastRevision(IRevision r)
			throws TopicMapStoreException {
		IRevision revision = cache.getRevisionCache().getPastRevision(r);
		if ( revision == null ){
			revision = getParentStore().doReadPastRevision(r);
			if ( revision != null ){
				cache.getRevisionCache().cachePastRevision(r, revision);
			}
		}
		return revision;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar doReadRevisionTimestamp(IRevision r)
			throws TopicMapStoreException {
		Calendar c = cache.getRevisionCache().getRevisionTimestamp(r);
		if ( c == null ){
			c = getParentStore().doReadRevisionTimestamp(r);
			cache.getRevisionCache().cacheRevisionTimestamp(r, c);
		}
		return DatatypeAwareUtils.cloneCalendar(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException {
		Changeset changeset = cache.getRevisionCache().getChangeset(r);
		if ( changeset == null ){
			changeset = getParentStore().doReadChangeSet(r);
			cache.getRevisionCache().cacheChangeset(r, changeset);
		}
		return changeset;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String, String> doReadMetaData(IRevision revision)
			throws TopicMapStoreException {
		Map<String, String> metaData = cache.getRevisionCache().getMetaData(revision);
		if ( metaData == null ){
			metaData = getParentStore().doReadMetaData(revision);
			cache.getRevisionCache().cacheMetaData(revision, metaData);
		}
		return HashUtil.getHashMap(metaData);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadMetaData(IRevision revision, String key)
			throws TopicMapStoreException {		
		Map<String, String> metaData = doReadMetaData(revision);
		return metaData.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		String bestLabel = cache.getIdentityCache().getBestLabel(topic);
		if (bestLabel == null) {
			bestLabel = getParentStore().doReadBestLabel(topic);
			cache.getIdentityCache().cacheBestLabel(topic, bestLabel);
		}
		return bestLabel;
	}

	/**
	 * @return the parentStore
	 */
	public ReadOnlyTopicMapStoreImpl getParentStore() {
		return parentStore;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	public void clear(){
		cache.clear();
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
}
