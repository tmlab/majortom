package de.topicmapslab.majortom.redis.store.index;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.COLON;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.ITEM_IDENTIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.STAR;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBJECT_IDENTIFIER;
import static de.topicmapslab.majortom.redis.store.RedisTopicMapStore.SUBJECT_LOCATOR;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.redis.store.RedisStoreIdentity;
import de.topicmapslab.majortom.redis.store.RedisTopicMapStore;
import de.topicmapslab.majortom.redis.util.RedisHandler;
import de.topicmapslab.majortom.util.HashUtil;

public class RedisIdentityIndex extends IndexImpl<RedisTopicMapStore> implements IIdentityIndex {

	private static final String NULL_IS_AN_INVALID_REGEX = "null is an invalid regex";
	private static final String NULL_IS_AN_INVALID_LOCATOR = "null is an invalid locator";
	private static final String INDEX_IS_CLOSED = "Index is closed!";
	private RedisHandler redis;

	public RedisIdentityIndex(RedisTopicMapStore store) {
		super(store);
		redis = getTopicMapStore().getRedis();
	}

	public Collection<Locator> getItemIdentifiers() {
		Set<Locator> set = HashUtil.getHashSet();
		for(String reference:getItemIdentifierKeys()) {
			set.add(new LocatorImpl(reference.substring(ITEM_IDENTIFIER.length() + 1)));
		}
		return set;
	}
	public Collection<String> getItemIdentifierKeys() {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.list(ITEM_IDENTIFIER + COLON + STAR);
	}

	public Collection<Locator> getSubjectIdentifiers() {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<Locator> set = HashUtil.getHashSet();
		for(String reference:getSubjectIdentifierKeys()) {
			set.add(new LocatorImpl(reference.substring(SUBJECT_IDENTIFIER.length() + 1)));
		}
		return set;
	}
	
	public Collection<String> getSubjectIdentifierKeys() {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.list(SUBJECT_IDENTIFIER + COLON + STAR);
	}

	public Collection<Locator> getSubjectLocators() {
		Set<Locator> set = HashUtil.getHashSet();
		for(String reference:getSubjectLocatorKeys()) {
			set.add(new LocatorImpl(reference.substring(SUBJECT_LOCATOR.length() + 1)));
		}
		return set;
	}
	
	public Collection<String> getSubjectLocatorKeys() {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.list(SUBJECT_LOCATOR + COLON + STAR);
	}

	public Construct getConstructByItemIdentifier(String reference) throws MalformedIRIException {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		String constructId = redis.get(ITEM_IDENTIFIER + COLON + reference);
		if(constructId == null) {
			return null;
		}
		return getTopicMapStore().doReadConstruct(getTopicMapStore().getTopicMap(), constructId);
	}

	public Construct getConstructByItemIdentifier(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return getConstructByItemIdentifier(locator.getReference());
	}

	public Collection<Construct> getConstructsByItemIdentifier(String regex) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(regex == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_REGEX);
		}
		return getConstructsByItemIdentifier(Pattern.compile(regex));
	}

	public Collection<Construct> getConstructsByItemIdentifier(Pattern regExp) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<Construct> set = HashUtil.getHashSet();
		for(String key: getItemIdentifierKeys()) {
			String reference = key.substring(ITEM_IDENTIFIER.length() + 1);
			if(regExp.matcher(reference).matches()) {
				set.add(getConstructByItemIdentifier(reference));
			}
		}
		return set;
	}

	public Topic getTopicBySubjectIdentifier(String reference) throws MalformedIRIException {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		final String id = redis.get(SUBJECT_IDENTIFIER + COLON + reference);
		if (id != null) {
			return getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap());
		}
		return null;
	}

	public Topic getTopicBySubjectIdentifier(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return getTopicBySubjectIdentifier(locator.getReference());
	}

	public Collection<Topic> getTopicsBySubjectIdentifier(String regex) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(regex == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_REGEX);
		}
		return getTopicsBySubjectIdentifier(Pattern.compile(regex));
	}

	public Collection<Topic> getTopicsBySubjectIdentifier(Pattern regExp) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<Topic> set = HashUtil.getHashSet();
		for(String key: getSubjectIdentifierKeys()) {
			String reference = key.substring(SUBJECT_IDENTIFIER.length() + 1);
			if(regExp.matcher(reference).matches()) {
				set.add(getTopicBySubjectIdentifier(reference));
			}
		}
		return set;
	}

	public Topic getTopicBySubjectLocator(String reference) throws MalformedIRIException {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		final String id = redis.get(SUBJECT_LOCATOR + COLON + reference);
		if (id != null) {
			return getTopicMapStore().getConstructFactory().newTopic(new RedisStoreIdentity(id), getTopicMapStore().getTopicMap());
		}
		return null;
	}

	public Topic getTopicBySubjectLocator(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return getTopicBySubjectLocator(locator.getReference());
	}

	public Collection<Topic> getTopicsBySubjectLocator(String regex) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(regex == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_REGEX);
		}
		return getTopicsBySubjectLocator(Pattern.compile(regex));
	}

	public Collection<Topic> getTopicsBySubjectLocator(Pattern regExp) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<Topic> set = HashUtil.getHashSet();
		for(String key: getSubjectLocatorKeys()) {
			String reference = key.substring(SUBJECT_LOCATOR.length() + 1);
			if(regExp.matcher(reference).matches()) {
				set.add(getTopicBySubjectLocator(reference));
			}
		}
		return set;
	}

	public Collection<Construct> getConstructsByIdentifier(String regex) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(regex == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_REGEX);
		}
		return getConstructsByIdentifier(Pattern.compile(regex));
	}

	public Collection<Construct> getConstructsByIdentifier(Pattern regExp) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		Set<Construct> set = HashUtil.getHashSet();
		set.addAll(getTopicsBySubjectIdentifier(regExp));
		set.addAll(getTopicsBySubjectLocator(regExp));
		set.addAll(getConstructsByItemIdentifier(regExp));
		return set;
	}

	public boolean existsSubjectIdentifier(String reference) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.exists(SUBJECT_IDENTIFIER + COLON + reference);
	}

	public boolean existsSubjectIdentifier(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return existsSubjectIdentifier(locator.getReference());
	}

	public boolean existsSubjectLocator(String reference) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.exists(SUBJECT_LOCATOR + COLON + reference);
	}

	public boolean existsSubjectLocator(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return existsSubjectLocator(locator.getReference());
	}

	public boolean existsItemIdentifier(String reference) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		return redis.exists(ITEM_IDENTIFIER + COLON + reference);
	}

	public boolean existsItemIdentifier(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if(locator == null) {
			throw new IllegalArgumentException(NULL_IS_AN_INVALID_LOCATOR);
		}
		return existsItemIdentifier(locator.getReference());
	}

	public boolean existsIdentifier(String reference) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (existsSubjectIdentifier(reference)) {
			return true;
		}
		if (existsSubjectLocator(reference)) {
			return true;
		}
		if (existsItemIdentifier(reference)) {
			return true;
		}
		return false;
	}

	public boolean existsIdentifier(Locator locator) {
		if ( !isOpen()){
			throw new TopicMapStoreException(INDEX_IS_CLOSED);
		}
		if (existsSubjectIdentifier(locator)) {
			return true;
		}
		if (existsSubjectLocator(locator)) {
			return true;
		}
		if (existsItemIdentifier(locator)) {
			return true;
		}
		return false;
	}

}
