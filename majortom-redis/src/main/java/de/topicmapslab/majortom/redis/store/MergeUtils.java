package de.topicmapslab.majortom.redis.store;

import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ASSOCIATION_OF_TOPICMAP;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.CHARACTERISTICS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.INSTANCES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.IN_SCOPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ITEM_IDENTIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.NAME;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.OCCURRENCE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PARENT;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PLAYED_ASSOCIATION;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PLAYED_ROLE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PLAYER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.REIFIED;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.REIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ROLE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ROLES_BY_ASSOCTYPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_ASSOCIATIONS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_CHARACTERISTICS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_NAMES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPED_OCCURRENCES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SCOPE_COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBJECT_IDENTIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBJECT_LOCATOR;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBTYPES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUPERTYPES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TOPICS_OF_TOPICMAP;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPE;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_ASSOCIATIONS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_CHARACTERISTICS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_NAMES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_OCCURRENCES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_ROLES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.VARIANT;

import java.util.Map;
import java.util.Set;

import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.HashUtil;

public class MergeUtils {

	public static void mergeTopics(RedisTopicMapStore store, RedisHandler redis, ITopic context, ITopic other) throws TopicMapStoreException {
		mergeTopics(store, redis, context.getId(), other.getId());
		store.removeTopic(other.getId(), true, false);
		((RedisStoreIdentity) ((TopicImpl) other).getIdentity()).setId(context.getId());
	}

	private static void mergeTopics(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {
		mergeIdentity(redis, context, other);
		mergeCharacteristics(store, redis, context, other);
		mergeTypeHierarchy(redis, context, other);
		mergeScopes(store, redis, context, other);
		mergePlayed(store, redis, context, other);
		final String otherReified = redis.get(other, REIFIED);
		if (otherReified != null) {
			final String reified = redis.get(context, REIFIED);
			if (reified != null) {
				throw new TopicMapStoreException("Both constructs are reified");
			}
			redis.set(context, REIFIED, otherReified);
			redis.set(otherReified, REIFIER, context);
		}
		redis.srem(TOPICS_OF_TOPICMAP, other);
	}

	private static void mergeCharacteristics(RedisTopicMapStore store, RedisHandler redis, String context, String other)
			throws TopicMapStoreException {
		// copy names
		for (String duplicate : redis.smembers(other + COLON + NAME)) {
			Set<String> duplicates = checkForDuplciate(redis, context, duplicate, NAME);
			if (duplicates.isEmpty()) {
				redis.sadd(context + COLON + NAME, duplicate);
				redis.sadd(context + COLON + CHARACTERISTICS, duplicate);
			} else {
				for (String nId : duplicates) {
					mergeItemIdentifiers(redis, nId, duplicate);
					mergeReificiation(store, redis, nId, duplicate);
					mergeVariants(store, redis, nId, duplicate);
					store.removeName(duplicate, true);
				}
			}
		}
		// copy occurrences
		for (String duplicate : redis.smembers(other + COLON + OCCURRENCE)) {
			Set<String> duplicates = checkForDuplciate(redis, context, duplicate, OCCURRENCE);
			if (duplicates.isEmpty()) {
				redis.sadd(context + COLON + OCCURRENCE, duplicate);
				redis.sadd(context + COLON + CHARACTERISTICS, duplicate);
			} else {
				for (String oId : duplicates) {
					mergeItemIdentifiers(redis, oId, duplicate);
					mergeReificiation(store, redis, oId, duplicate);
					store.removeOccurrence(duplicate, true);
				}
			}
		}
		redis.del(other + COLON + OCCURRENCE, other + COLON + NAME, other + COLON + CHARACTERISTICS);
	}

	private static Set<String> checkForDuplciate(RedisHandler redis, String tId, String nId, String key) throws TopicMapStoreException {
		Set<String> duplicates = HashUtil.getHashSet();
		Map<String, String> map = redis.hgetall(nId);
		map.remove(REIFIER);
		map.remove(PARENT);
		for (String nId_ : redis.smembers(tId + COLON + key)) {
			if (nId_.equalsIgnoreCase(nId)) {
				continue;
			}
			Map<String, String> map_ = redis.hgetall(nId_);
			map_.remove(REIFIER);
			map_.remove(PARENT);
			if (map.equals(map_)) {
				duplicates.add(nId_);
			}
		}
		return duplicates;
	}

	private static void mergeReificiation(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {
		final String otherReifier = redis.get(other, REIFIER);
		if (otherReifier != null) {
			final String reifier = redis.get(context, REIFIER);
			if (reifier != null) {
				redis.hdel(otherReifier, REIFIED);
				redis.hdel(other, REIFIER);
				mergeTopics(store, redis, reifier, otherReifier);
				store.removeTopic(otherReifier, true, true);
				redis.set(reifier, REIFIED, context);
				redis.set(context, REIFIER, reifier);
			} else {
				redis.set(otherReifier, REIFIED, context);
				redis.set(context, REIFIER, otherReifier);
				redis.hdel(other, REIFIER);
			}
		}
	}

	private static void mergeVariants(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {
		// copy variants
		for (String duplicate : redis.smembers(other + COLON + VARIANT)) {
			Set<String> duplicates = checkForDuplciate(redis, context, duplicate, VARIANT);
			if (duplicates.isEmpty()) {
				redis.sadd(context + COLON + VARIANT, duplicate);
			} else {
				for (String vId : duplicates) {
					mergeItemIdentifiers(redis, vId, duplicate);
					mergeReificiation(store, redis, vId, duplicate);
					store.removeVariant(duplicate, true);
				}
			}
		}
		redis.del(other + COLON + VARIANT);
	}

	private static void mergeItemIdentifiers(RedisHandler redis, String context, String other) throws TopicMapStoreException {
		// copy item-identifiers
		for (String ii : redis.smembers(other + COLON + ITEM_IDENTIFIER)) {
			redis.set(ITEM_IDENTIFIER + COLON + ii, context);
			redis.sadd(context + COLON + ITEM_IDENTIFIER, ii);
		}
		redis.del(other + COLON + ITEM_IDENTIFIER);
	}

	private static void mergeIdentity(RedisHandler redis, String context, String other) throws TopicMapStoreException {
		mergeItemIdentifiers(redis, context, other);
		// copy subject-identifiers
		for (String si : redis.smembers(other + COLON + SUBJECT_IDENTIFIER)) {
			redis.set(SUBJECT_IDENTIFIER + COLON + si, context);
			redis.sadd(context + COLON + SUBJECT_IDENTIFIER, si);
		}

		// copy subject-locators
		for (String sl : redis.smembers(other + COLON + SUBJECT_LOCATOR)) {
			redis.set(SUBJECT_LOCATOR + COLON + sl, context);
			redis.sadd(context + COLON + SUBJECT_LOCATOR, sl);
		}
		redis.del(other + COLON + SUBJECT_IDENTIFIER, other + COLON + SUBJECT_LOCATOR);
	}

	private static void mergeTypeHierarchy(RedisHandler redis, String context, String other) throws TopicMapStoreException {
		/*
		 * copy typed constructs
		 */
		// characteristics
		for (String typed : redis.smembers(TYPED_CHARACTERISTICS + COLON + other)) {
			redis.set(typed, TYPE, context);
			redis.sadd(TYPED_CHARACTERISTICS + COLON + context, typed);
		}
		// names
		for (String typed : redis.smembers(TYPED_NAMES + COLON + other)) {
			redis.set(typed, TYPE, context);
			redis.sadd(TYPED_NAMES + COLON + context, typed);
		}
		// ocurrences
		for (String typed : redis.smembers(TYPED_OCCURRENCES + COLON + other)) {
			redis.set(typed, TYPE, context);
			redis.sadd(TYPED_OCCURRENCES + COLON + context, typed);
		}
		// associations
		for (String typed : redis.smembers(TYPED_ASSOCIATIONS + COLON + other)) {
			redis.set(typed, TYPE, context);
			redis.sadd(TYPED_ASSOCIATIONS + COLON + context, typed);
		}
		// roles by association type
		for (String id : redis.smembers(ROLES_BY_ASSOCTYPE + COLON + other)) {
			redis.sadd(ROLES_BY_ASSOCTYPE + COLON + context, id);
		}
		// roles
		for (String typed : redis.smembers(TYPED_ROLES + COLON + other)) {
			redis.set(typed, TYPE, context);
			redis.sadd(TYPED_ROLES + COLON + context, typed);
			String a = redis.get(typed, PARENT);
			redis.srem(a + COLON + TYPE, other);
			redis.sadd(a + COLON + TYPE, context);
		}
		redis.del(TYPED_CHARACTERISTICS + COLON + other, TYPED_NAMES + COLON + other, TYPED_OCCURRENCES + COLON + other, TYPED_ASSOCIATIONS + COLON
				+ other, TYPED_ROLES + COLON + other, ROLES_BY_ASSOCTYPE + COLON + other);
		// copy types
		for (String type : redis.smembers(other + COLON + TYPES)) {
			redis.sadd(context + COLON + TYPES, type);
			redis.sadd(type + COLON + INSTANCES, context);
			redis.srem(type + COLON + INSTANCES, other);
		}
		redis.del(other + COLON + TYPES);
		// copy supertypes
		for (String type : redis.smembers(other + COLON + SUPERTYPES)) {
			redis.sadd(context + COLON + SUPERTYPES, type);
			redis.sadd(type + COLON + SUBTYPES, context);
			redis.srem(type + COLON + SUBTYPES, other);
		}
		redis.del(other + COLON + SUPERTYPES);
	}

	private static void mergeScopes(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {
		/*
		 * copy scoped constructs
		 */
		for (final String scopeId : redis.smembers(other + COLON + IN_SCOPE)) {
			Set<String> themes = HashUtil.getHashSet();
			for (String key : redis.smembers(scopeId)) {
				if (!key.equalsIgnoreCase(other)) {
					themes.add(key);
				}
			}
			themes.add(scopeId);
			Set<String> ids = redis.sinter(themes.toArray(new String[0]));
			String newScopeId;
			/*
			 * reuse existing
			 */
			if (!ids.isEmpty()) {
				newScopeId = ids.iterator().next();
			}
			/*
			 * generate new scope id
			 */
			else {
				newScopeId = store.getNewRedisId(SCOPE_COLON);
				/*
				 * add new scope to themes
				 */
				for (String key : themes) {
					redis.sadd(key + COLON + IN_SCOPE, newScopeId);
					redis.sadd(newScopeId, key);
				}
			}

			// characteristics
			for (String scoped : redis.smembers(SCOPED_CHARACTERISTICS + COLON + scopeId)) {
				redis.set(scoped, SCOPE, newScopeId);
				redis.sadd(SCOPED_CHARACTERISTICS + COLON + newScopeId, scoped);
			}
			// names
			for (String scoped : redis.smembers(SCOPED_NAMES + COLON + scopeId)) {
				redis.set(scoped, SCOPE, newScopeId);
				redis.sadd(SCOPED_NAMES + COLON + newScopeId, scoped);
			}
			// ocurrences
			for (String scoped : redis.smembers(SCOPED_OCCURRENCES + COLON + scopeId)) {
				redis.set(scoped, SCOPE, newScopeId);
				redis.sadd(SCOPED_OCCURRENCES + COLON + newScopeId, scoped);
			}
			// associations
			for (String scoped : redis.smembers(SCOPED_ASSOCIATIONS + COLON + scopeId)) {
				redis.set(scoped, SCOPE, newScopeId);
				redis.sadd(SCOPED_ASSOCIATIONS + COLON + newScopeId, scoped);
			}
			redis.del(SCOPED_ASSOCIATIONS + COLON + scopeId, SCOPED_OCCURRENCES + COLON + scopeId, SCOPED_NAMES + COLON + scopeId,
					SCOPED_CHARACTERISTICS + COLON + scopeId, scopeId);
		}
		redis.del(other + COLON + IN_SCOPE);
	}

	private static void mergePlayed(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {

		for (String roleId : redis.smembers(other + COLON + PLAYED_ROLE)) {
			redis.set(roleId, PLAYER, context);
			redis.sadd(context + COLON + PLAYED_ROLE, roleId);
		}
		for (String associationId : redis.smembers(other + COLON + PLAYED_ASSOCIATION)) {
			redis.sadd(context + COLON + PLAYED_ASSOCIATION, associationId);
		}
		Set<String> removed = HashUtil.getHashSet();
		for (String associationId : redis.smembers(context + COLON + PLAYED_ASSOCIATION)) {
			if ( removed.contains(associationId)){
				continue;
			}
			Set<String> duplicates = checkForDuplciate(redis, associationId, context);
			for (String duplicateId : duplicates) {
				if ( removed.contains(duplicateId)){
					continue;
				}
				mergeItemIdentifiers(redis, associationId, duplicateId);
				mergeReificiation(store, redis, associationId, duplicateId);
				store.removeAssociation(duplicateId, true);
				removed.add(duplicateId);
			}
			for (String roleId : redis.smembers(associationId + COLON + ROLE)) {
				if ( removed.contains(roleId)){
					continue;
				}
				Set<String> duplicateRoles = checkForDuplciate(redis, associationId, roleId, ROLE);
				if (!duplicateRoles.isEmpty()) {
					for (String duplicateId : duplicateRoles) {
						if ( removed.contains(duplicateId)){
							continue;
						}
						mergeItemIdentifiers(redis, roleId, duplicateId);
						mergeReificiation(store, redis, roleId, duplicateId);
						store.removeRole(roleId, true, false);
						removed.add(duplicateId);
					}
				}
			}
		}

		redis.del(other + COLON + PLAYED_ROLE, other + COLON + PLAYED_ASSOCIATION);
	}

	private static Set<String> checkForDuplciate(RedisHandler redis, String aId, String pId) throws TopicMapStoreException {
		String scopeId = redis.get(aId, SCOPE);
		Map<Map<String, String>, String> roleHashes = HashUtil.getHashMap();
		for (String roleId : redis.smembers(aId + COLON + ROLE)) {
			Map<String, String> hash = redis.hgetall(roleId);
			hash.remove(PARENT);
			hash.remove(REIFIER);
			roleHashes.put(hash, roleId);
		}
		Set<String> duplicates = HashUtil.getHashSet();
		Set<String> duplicateIds = HashUtil.getHashSet();
		if (pId == null) {
			duplicateIds.addAll(redis.smembers(ASSOCIATION_OF_TOPICMAP));
		} else {
			duplicateIds.addAll(redis.smembers(pId + COLON + PLAYED_ASSOCIATION));
		}
		for (String duplicateId : duplicateIds) {
			/*
			 * ignore itself
			 */
			if (aId.equalsIgnoreCase(duplicateId)) {
				continue;
			}
			/*
			 * check scope
			 */
			if (!scopeId.equalsIgnoreCase(redis.get(duplicateId, SCOPE))) {
				continue;
			}
			Set<String> duplicateRoleIds = redis.smembers(duplicateId + COLON + ROLE);
			if (roleHashes.size() != duplicateRoleIds.size()) {
				continue;
			}
			boolean mismatch = false;
			for (String roleId : duplicateRoleIds) {
				Map<String, String> map = redis.hgetall(roleId);
				map.remove(PARENT);
				map.remove(REIFIER);
				if (!roleHashes.containsKey(map)) {
					mismatch = true;
					break;
				}
			}
			if (!mismatch) {
				duplicates.add(duplicateId);
			}
		}
		return duplicates;
	}

	public static void removeDuplicates(RedisTopicMapStore store, RedisHandler redis) throws TopicMapStoreException {
		Set<String> removed = HashUtil.getHashSet();
		/*
		 * check topics
		 */
		for (String topic : redis.smembers(TOPICS_OF_TOPICMAP)) {
			// check names
			for (String name : redis.smembers(topic + COLON + NAME)) {
				if (removed.contains(name)) {
					continue;
				}
				Set<String> duplicates = checkForDuplciate(redis, topic, name, NAME);
				for ( String nId : duplicates){
					mergeItemIdentifiers(redis, name, nId);
					mergeReificiation(store, redis, name, nId);
					mergeVariants(store, redis, name, nId);
					store.removeName(nId, true);
					removed.add(nId);
				}
				/*
				 * check variants
				 */
				for (String variant : redis.smembers(name + COLON + VARIANT)) {
					if (removed.contains(variant)) {
						continue;
					}
					Set<String> vIds = checkForDuplciate(redis, name, variant, VARIANT);
					for ( String vId : vIds){
						mergeItemIdentifiers(redis, variant, vId);
						mergeReificiation(store, redis, variant, vId);
						store.removeVariant(vId, true);
						removed.add(vId);
					}
				}
			}
			// check occurrences
			for (String occurrence : redis.smembers(topic + COLON + OCCURRENCE)) {
				if (removed.contains(occurrence)) {
					continue;
				}
				Set<String> duplicates = checkForDuplciate(redis, topic, occurrence, OCCURRENCE);
				for ( String oId : duplicates){
					mergeItemIdentifiers(redis, occurrence, oId);
					mergeReificiation(store, redis, occurrence, oId);
					store.removeOccurrence(oId, true);
					removed.add(oId);
				}
			}
		}
		/*
		 * check associations
		 */
		for (String association : redis.smembers(ASSOCIATION_OF_TOPICMAP)) {
			if (removed.contains(association)) {
				continue;
			}
			Set<String> duplicates = checkForDuplciate(redis, association, null);
			for (String aId : duplicates) {
				if (removed.contains(aId)) {
					continue;
				}
				mergeItemIdentifiers(redis, association, aId);
				mergeReificiation(store, redis, association, aId);
				mergeRoles(store, redis, association, aId);
				store.removeAssociation(aId, true);
				removed.add(aId);
			}
			/*
			 * check roles
			 */
			for (String role : redis.smembers(association + COLON + ROLE)) {
				if (removed.contains(role)) {
					continue;
				}
				Set<String> rIds = checkForDuplciate(redis, association, role, ROLE);
				for ( String rId : rIds){
					if (removed.contains(rId)) {
						continue;
					}
					mergeItemIdentifiers(redis, role, rId);
					mergeReificiation(store, redis, role, rId);
					store.removeRole(rId, false, false);
					removed.add(rId);
				}
			}
		}
	}
	
	private static void mergeRoles(RedisTopicMapStore store, RedisHandler redis, String context, String other) throws TopicMapStoreException {
		// copy variants
		for (String duplicate : redis.smembers(other + COLON + ROLE)) {
			Set<String> duplicates = checkForDuplciate(redis, context, duplicate, ROLE);
			if (duplicates.isEmpty()) {
				redis.sadd(context + COLON + ROLE, duplicate);
			} else {
				for (String rId : duplicates) {
					mergeItemIdentifiers(redis, rId, duplicate);
					mergeReificiation(store, redis, rId, duplicate);
					store.removeVariant(duplicate, true);
				}
			}
		}
		redis.del(other + COLON + ROLE);
	}
}
