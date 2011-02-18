package de.topicmapslab.majortom.redis.store.index;

import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.INSTANCES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.PARENT;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.STAR;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TOPICS_OF_TOPICMAP;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_ASSOCIATIONS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_CHARACTERISTICS;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_NAMES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.*;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPED_ROLES;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.TYPES;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.redis.store.RedisStoreIdentity;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.HashUtil;

public class RedisTypeInstanceIndex extends IndexImpl<RedisTopicMapStore> implements ITypeInstanceIndex {

	private RedisHandler redis;

	public RedisTypeInstanceIndex(RedisTopicMapStore store) {
		super(store);
		redis = getTopicMapStore().getRedis();
	}

	public Collection<Topic> getAssociationTypes() {
		return getTypes(TYPED_ASSOCIATIONS);
	}

	public Collection<Association> getAssociations(Topic type) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Association> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(TYPED_ASSOCIATIONS + COLON + type.getId());
		for (String key : keys) {
			set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(key), getTopicMapStore().getTopicMap()));
		}
		return set;
	}

	public Collection<Topic> getNameTypes() {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTypes(TYPED_NAMES);
	}

	public Collection<Name> getNames(Topic type) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		// XXX The way I'm getting the parent Topics seems very inefficient to
		// me - what's better?
		Set<Name> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(TYPED_NAMES + COLON + type.getId());
		for (String key : keys) {
			String parentId = redis.get(key, PARENT);
			ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(key), parent));
		}
		return set;
	}

	public Collection<Topic> getOccurrenceTypes() {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTypes(TYPED_OCCURRENCES);
	}

	public Collection<Occurrence> getOccurrences(Topic type) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		// XXX The way I'm getting the parent Topics seems very inefficient to
		// me - what's better?
		Set<Occurrence> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(TYPED_OCCURRENCES + COLON + type.getId());
		for (String key : keys) {
			String parentId = redis.get(key, PARENT);
			ITopic parent = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(parentId), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(key), parent));
		}
		return set;
	}

	public Collection<Topic> getRoleTypes() {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTypes(TYPED_ROLES);
	}

	public Collection<Role> getRoles(Topic type) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		// XXX The way I'm getting the parent Assoc seems very inefficient to me
		// - what's better?
		Set<Role> set = HashUtil.getHashSet();
		Set<String> keys = redis.smembers(TYPED_ROLES + COLON + type.getId());
		for (String key : keys) {
			String parentId = redis.get(key, PARENT);
			IAssociation parent = getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(parentId), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newAssociationRole(new RedisStoreIdentity(key), parent));
		}
		return set;
	}

	public Collection<Topic> getTopicTypes() {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		Set<String> keys = redis.list(STAR + COLON + INSTANCES);
		for (String key : keys) {
			int index = key.lastIndexOf(COLON+INSTANCES);
			set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key.substring(0,index)), getTopicMapStore().getTopicMap()));			
		}
		return set;
	}

	public Collection<Topic> getTopics(Topic type) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();

		if (type == null) {
			//TODO  could be faster
			Set<String> keys = redis.smembers(TOPICS_OF_TOPICMAP);
			for ( String key : keys ){
				if ( !redis.exists(key + COLON + TYPES)){
					set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMapStore().getTopicMap()));
				}
			}
		} else {
			Set<String> keys = redis.smembers(type.getId() + COLON + INSTANCES);
			for (String key : keys) {
				set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMapStore().getTopicMap()));
			}
		}
		return set;
	}

	public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		Set<String> typeIds = HashUtil.getHashSet();
		for (Topic t : types) {
			typeIds.add(t.getId() + COLON + INSTANCES);
		}
		final String[] typeQuery = (String[]) typeIds.toArray(new String[typeIds.size()]);
		Set<String> keys = null;
		if (matchAll) {
			keys = redis.sinter(typeQuery);
		} else {
			keys = redis.sunion(typeQuery);
		}
		for (String key : keys) {
			set.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key), getTopicMapStore().getTopicMap()));
		}
		return set;
	}

	public Collection<Topic> getTopics(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTopics(types, false); 
	}

	public Collection<Topic> getTopics(Collection<Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTopics((Topic[]) types.toArray(new Topic[types.size()]), false);
	}

	public Collection<Topic> getTopics(Collection<Topic> types, boolean matchAll) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTopics((Topic[]) types.toArray(new Topic[types.size()]), matchAll);
	}

	public Collection<Association> getAssociations(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getAssociations(Arrays.asList(types));
	}

	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		String[] keys = new String[types.size()];
		int i = 0;
		for ( Topic ty : types){
			keys[i++] = TYPED_ASSOCIATIONS + COLON + ty.getId();
		}
		Set<Association> set = HashUtil.getHashSet();
		for ( String id : redis.sunion(keys)){
			set.add(getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap()));
		}
		return set;
	}

	public Collection<Role> getRoles(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getRoles(Arrays.asList(types));
	}

	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		String[] keys = new String[types.size()];
		int i = 0;
		for ( Topic ty : types){
			keys[i++] = TYPED_ROLES + COLON + ty.getId();
		}
		Set<Role> set = HashUtil.getHashSet();
		for ( String id : redis.sunion(keys)){
			IAssociation association = getTopicMapStore().getConstructFactory().newAssociation(new RedisStoreIdentity(redis.get(id, PARENT)), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newAssociationRole(new RedisStoreIdentity(id), association));
		}
		return set;
	}

	public Collection<Topic> getCharacteristicTypes() {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getTypes(TYPED_CHARACTERISTICS);
	}

	public Collection<ICharacteristics> getCharacteristics(Topic type) {	
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}	
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for ( String id : redis.smembers(TYPED_CHARACTERISTICS + COLON + type.getId())){			
			set.add((ICharacteristics)getTopicMapStore().doReadConstruct(getTopicMapStore().getTopicMap(), id));
		}
		return set;
	}

	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getCharacteristics(Arrays.asList(types));
	}

	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		String[] keys = new String[types.size()];
		int i = 0;
		for ( Topic ty : types){
			keys[i++] = TYPED_CHARACTERISTICS + COLON + ty.getId();
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for ( String id : redis.sunion(keys)){			
			set.add((ICharacteristics)getTopicMapStore().doReadConstruct(getTopicMapStore().getTopicMap(), id));
		}
		return set;
	}

	public Collection<Name> getNames(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getNames(Arrays.asList(types));
	}

	public Collection<Name> getNames(Collection<? extends Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		String[] keys = new String[types.size()];
		int i = 0;
		for ( Topic ty : types){
			keys[i++] = TYPED_NAMES + COLON + ty.getId();
		}
		Set<Name> set = HashUtil.getHashSet();
		for ( String id : redis.sunion(keys)){			
			ITopic topic = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(redis.get(id, PARENT)), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newName(new RedisStoreIdentity(id), topic));
		}
		return set;
	}

	public Collection<Occurrence> getOccurrences(Topic... types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		return getOccurrences(Arrays.asList(types));
	}

	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		String[] keys = new String[types.size()];
		int i = 0;
		for ( Topic ty : types){
			keys[i++] = TYPED_OCCURRENCES + COLON + ty.getId();
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for ( String id : redis.sunion(keys)){			
			ITopic topic = getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(redis.get(id, PARENT)), getTopicMapStore().getTopicMap());
			set.add(getTopicMapStore().getConstructFactory().newOccurrence(new RedisStoreIdentity(id), topic));
		}
		return set;
	}

	public Collection<Topic> getTypes(final String TYPE) {
		if ( !isOpen()){
			throw new TopicMapStoreException("Index is closed!");
		}
		Set<String> keys = redis.list(TYPE + COLON + "*");
		int start = (TYPE + COLON).length();
		Set<Topic> types = HashUtil.getHashSet();
		for (String key : keys) {
			types.add(getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(key.substring(start)), getTopicMapStore().getTopicMap()));
		}
		return types;
	}

}
