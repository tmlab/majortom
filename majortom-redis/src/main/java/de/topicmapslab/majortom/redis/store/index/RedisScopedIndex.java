package de.topicmapslab.majortom.redis.store.index;

import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.EMPTY_SCOPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.EMPTY_SCOPE_ID;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.IN_SCOPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_ASSOCIATIONS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_CHARACTERISTICS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_NAMES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_OCCURRENCES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_VARIANTS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.STAR;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.redis.store.RedisStoreIdentity;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.HashUtil;

public class RedisScopedIndex extends IndexImpl<RedisTopicMapStore> implements IScopedIndex {

	private final RedisHandler redis;

	/**
	 * Constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public RedisScopedIndex(RedisTopicMapStore store) {
		super(store);
		this.redis = store.getRedis();
	}

	/**
	 * Returns the topic map
	 * 
	 * @return the topic map
	 */
	public ITopicMap getTopicMap() {
		return getTopicMapStore().getTopicMap();
	}

	/**
	 * Utility method to read all scopes of the construct redis key
	 * 
	 * @param TYPE
	 *            the key
	 * @return a set of scopess
	 */
	public Collection<IScope> getScopes(final String TYPE) {
		Set<String> list = redis.list(TYPE + COLON + STAR);
		if (list.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[list.size()];
		int start = (TYPE + COLON).length();
		int i = 0;
		for (String key : list) {
			String id = key.substring(start);
			/*
			 * store knowledge that empty scope are contained
			 */

			keys[i++] = id;
		}
		Map<String, ITopic> cache = HashUtil.getHashMap();
		Set<IScope> set = HashUtil.getHashSet();
		for (String id : keys) {
			if (EMPTY_SCOPE_ID.equalsIgnoreCase(id)) {
				set.add(EMPTY_SCOPE);
			} else {
				Set<ITopic> themes = HashUtil.getHashSet();
				for (String tId : redis.smembers(id)) {
					if (cache.containsKey(tId)) {
						themes.add(cache.get(tId));
					} else {
						ITopic t = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(tId), getTopicMap());
						cache.put(tId, t);
						themes.add(t);
					}
				}
				set.add(new ScopeImpl(id, themes));
			}
		}
		return set;
	}

	/**
	 * Utility method to read all themes of a specific construct redis key
	 * 
	 * @param TYPE
	 *            the key
	 * @return a set of topic themes
	 */
	public Collection<Topic> getThemes(final String TYPE) {
		Set<String> list = redis.list(TYPE + COLON + STAR);
		if (list.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[list.size()];
		int start = (TYPE + COLON).length();
		int i = 0;
		for (String key : list) {
			keys[i++] = key.substring(start);
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (String id : redis.sunion(keys)) {
			set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMap()));
		}
		return set;
	}

	public Collection<Topic> getAssociationThemes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getThemes(SCOPED_ASSOCIATIONS);
	}

	public Collection<Association> getAssociations(Topic theme) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Association> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByTheme(theme);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_ASSOCIATIONS + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap()));
		}
		return set;
	}

	public Collection<Association> getAssociations(Topic[] themes, boolean arg1) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		Set<Association> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByThemes(themes, arg1);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_ASSOCIATIONS + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap()));
		}
		return set;
	}

	public Collection<Topic> getNameThemes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getThemes(SCOPED_NAMES);
	}

	public Collection<Name> getNames(Topic theme) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Name> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByTheme(theme);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_NAMES + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add((IName) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Name> getNames(Topic[] themes, boolean arg1) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		Set<Name> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByThemes(themes, arg1);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_NAMES + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add((IName) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Topic> getOccurrenceThemes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getThemes(SCOPED_OCCURRENCES);
	}

	public Collection<Occurrence> getOccurrences(Topic theme) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByTheme(theme);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_OCCURRENCES + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add((IOccurrence) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Occurrence> getOccurrences(Topic[] themes, boolean arg1) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByThemes(themes, arg1);
		if (scopeIds.isEmpty()) {
			return Collections.emptySet();
		}
		String[] keys = new String[scopeIds.size()];
		int i = 0;
		for (String key : scopeIds) {
			keys[i++] = SCOPED_OCCURRENCES + COLON + key;
		}
		for (String id : redis.sunion(keys)) {
			set.add((IOccurrence) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Topic> getVariantThemes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		Collection<Topic> c = HashUtil.getHashSet();
		c.addAll(getThemes(SCOPED_VARIANTS));
		c.addAll(getThemes(SCOPED_NAMES));
		return c;
	}

	public Collection<Variant> getVariants(Topic theme) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException();
		}
		Set<Variant> set = HashUtil.getHashSet();
		Set<String> scopeIds = getScopeIdsByTheme(theme);
		if (!scopeIds.isEmpty()) {
			String[] keys = new String[scopeIds.size()];
			int i = 0;
			for (String key : scopeIds) {
				keys[i++] = SCOPED_VARIANTS + COLON + key;
			}
			for (String id : redis.sunion(keys)) {
				set.add((IVariant) getTopicMapStore().doReadConstruct(getTopicMap(), id));
			}
		}
		/*
		 * get names by this theme
		 */
		for (Name n : getNames(theme)) {
			set.addAll(getTopicMapStore().doReadVariants((IName) n));
		}
		return set;
	}

	public Collection<Variant> getVariants(Topic[] themes, boolean arg1) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		ILiteralIndex index = getTopicMapStore().getIndex(ILiteralIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		Collection<Variant> variants = index.getVariants(); 
		Set<Variant> set = HashUtil.getHashSet();
		for ( Variant v : variants){
			IScope s = getTopicMapStore().doReadScope((IScopable)v);
			if ( arg1 && s.getThemes().containsAll(Arrays.asList(themes))){
				set.add(v);
			}else if ( !arg1 ){
				for  ( Topic t : themes ){
					if ( s.getThemes().contains(t)){
						set.add(v);
						break;
					}
				}
			}
		}
		return set;
	}

	public Collection<IScope> getAssociationScopes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getScopes(SCOPED_ASSOCIATIONS);
	}

	public Collection<Association> getAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}
		Set<Association> set = HashUtil.getHashSet();
		for (String id : redis.smembers(SCOPED_ASSOCIATIONS + COLON + scope.getId())) {
			set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap()));
		}
		return set;
	}

	public Collection<Association> getAssociations(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		return getAssociations(Arrays.asList(scopes));
	}

	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<Association> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (String id : redis.smembers(SCOPED_ASSOCIATIONS + COLON + scope.getId())) {
				set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMap()));
			}
		}
		return set;
	}

	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (String id : redis.smembers(SCOPED_CHARACTERISTICS + COLON + scope.getId())) {
			set.add((ICharacteristics) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (String id : redis.smembers(SCOPED_CHARACTERISTICS + COLON + scope.getId())) {
				set.add((ICharacteristics) getTopicMapStore().doReadConstruct(getTopicMap(), id));
			}
		}
		return set;
	}

	public Collection<IScope> getNameScopes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getScopes(SCOPED_NAMES);
	}

	public Collection<Name> getNames(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}
		Set<Name> set = HashUtil.getHashSet();
		for (String id : redis.smembers(SCOPED_NAMES + COLON + scope.getId())) {
			set.add((IName) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Name> getNames(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		return getNames(Arrays.asList(scopes));
	}

	public Collection<Name> getNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<Name> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (String id : redis.smembers(SCOPED_NAMES + COLON + scope.getId())) {
				set.add((Name) getTopicMapStore().doReadConstruct(getTopicMap(), id));
			}
		}
		return set;
	}

	public Collection<IScope> getOccurrenceScopes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getScopes(SCOPED_OCCURRENCES);
	}

	public Collection<Occurrence> getOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (String id : redis.smembers(SCOPED_OCCURRENCES + COLON + scope.getId())) {
			set.add((Occurrence) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		return getOccurrences(Arrays.asList(scopes));
	}

	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (String id : redis.smembers(SCOPED_OCCURRENCES + COLON + scope.getId())) {
				set.add((Occurrence) getTopicMapStore().doReadConstruct(getTopicMap(), id));
			}
		}
		return set;
	}

	public Collection<Scoped> getScopables(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}
		Set<Scoped> set = HashUtil.getHashSet();
		for (final String key : redis.list(STAR + COLON + scope.getId())) {
			for (String id : redis.smembers(key)) {
				set.add((Scoped) getTopicMapStore().doReadConstruct(getTopicMap(), id));
			}
		}
		return set;
	}

	public Collection<Scoped> getScopables(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<Scoped> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (final String key : redis.list(STAR + COLON + scope.getId())) {
				for (String id : redis.smembers(key)) {
					set.add((Scoped) getTopicMapStore().doReadConstruct(getTopicMap(), id));
				}
			}
		}
		return set;
	}

	public IScope getScope(Topic... themes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		return getScope(Arrays.asList(themes));
	}

	public IScope getScope(Collection<? extends Topic> themes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		Set<ITopic> themes_ = HashUtil.getHashSet();
		for (Topic theme : themes) {
			themes_.add((ITopic) theme);
		}
		return getTopicMapStore().doCreateScope(getTopicMap(), themes_);
	}

	public Collection<IScope> getScopes(Topic... themes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		return getScopes(Arrays.asList(themes), false);
	}

	public Collection<IScope> getScopes(Topic[] themes, boolean arg1) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		return getScopes(Arrays.asList(themes), arg1);
	}

	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		Map<String, ITopic> cache = HashUtil.getHashMap();
		Set<IScope> set = HashUtil.getHashSet();
		for (String scopeId : getScopeIdsByThemes(themes, matchAll)) {
			Set<ITopic> themes_ = HashUtil.getHashSet();
			for (String tId : redis.smembers(scopeId)) {
				if (cache.containsKey(tId)) {
					themes_.add(cache.get(tId));
				} else {
					ITopic t = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(tId), getTopicMap());
					cache.put(tId, t);
					themes_.add(t);
				}
			}
			IScope s = new ScopeImpl(scopeId, themes_);
			if ( !getScopables(s).isEmpty()){
				set.add(s);
			}
		}
		return set;
	}

	public Collection<IScope> getVariantScopes() {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		return getScopes(SCOPED_VARIANTS);
	}

	public Collection<Variant> getVariants(IScope scope) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException();
		}

		Set<Variant> set = HashUtil.getHashSet();
		for (String id : redis.smembers(SCOPED_VARIANTS + COLON + scope.getId())) {
			set.add((Variant) getTopicMapStore().doReadConstruct(getTopicMap(), id));
		}
		return set;
	}

	public Collection<Variant> getVariants(IScope... scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		return getVariants(Arrays.asList(scopes));
	}

	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException();
		}
		Set<Variant> set = HashUtil.getHashSet();
		for (final IScope scope : scopes) {
			for (final String key : redis.list(SCOPED_VARIANTS + COLON + scope.getId())) {
				for (String id : redis.smembers(key)) {
					set.add((Variant) getTopicMapStore().doReadConstruct(getTopicMap(), id));
				}
			}
		}
		return set;
	}

	public Set<String> getScopeIdsByTheme(final Topic theme) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (theme == null) {
			Set<String> set = HashUtil.getHashSet();
			set.add("s:0");
			return set;
		}
		return redis.smembers(theme.getId() + IN_SCOPE);
	}

	public Set<String> getScopeIdsByThemes(final Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		return getScopeIdsByThemes(Arrays.asList(themes), matchAll);
	}

	public Set<String> getScopeIdsByThemes(final Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TopicMapStoreException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException();
		}
		if (themes.isEmpty()) {
			Set<String> set = HashUtil.getHashSet();
			set.add("s:0");
			return set;
		}
		String[] keys = new String[themes.size()];
		int i = 0;
		for (Topic t : themes) {
			keys[i++] = t.getId() + IN_SCOPE;
		}
		Set<String> scopeIds = HashUtil.getHashSet();
		if (matchAll) {
			scopeIds = redis.sinter(keys);
		} else {
			scopeIds = redis.sunion(keys);
		}
		return scopeIds;
	}
}
