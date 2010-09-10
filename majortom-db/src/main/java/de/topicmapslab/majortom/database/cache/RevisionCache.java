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
package de.topicmapslab.majortom.database.cache;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Cache handling revisions
 * 
 * @author Sven Krosse
 * 
 */
public class RevisionCache implements ITopicMapListener {

	private static final String CHANGESET = "dependendChangeset";
	private static final String REVISIONS = "dependendRevisions";
	private static final String TIMESTAMP = "lastModification";
	private static final String PAST = "pastRevision";
	private static final String FUTURE = "futureRevisionRevision";
	private static final String METADATA = "metaData";

	class CacheEntry {

		public Changeset dependendChangeset;

		public List<IRevision> dependendRevisions;

		public Calendar lastModification;

		public IRevision pastRevision;

		public IRevision futureRevisionRevision;

		public Map<String, String> metaData;
	}

	private Map<ITopic, CacheEntry> topicDependent;

	private Map<ITopic, CacheEntry> associationDependents;

	private Map<IRevision, CacheEntry> revisionDependents;

	private Map<Calendar, IRevision> revisionsByTimestamp;
	
	private Map<Long, IRevision> revisionsById;

	private Map<String, Calendar> tags;

	private Calendar lastChange;

	private IRevision firstRevision, lastRevision;

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		if (topicDependent != null) {
			topicDependent.clear();
		}
		if (associationDependents != null) {
			associationDependents.clear();
		}
		if (revisionDependents != null) {
			revisionDependents.clear();
		}
		if (tags != null) {
			tags.clear();
		}
		if (revisionsByTimestamp != null) {
			revisionsByTimestamp.clear();
		}
		if (revisionsById != null) {
			revisionsById.clear();
		}
		
		lastChange = null;
		firstRevision = null;
		lastRevision = null;
	}

	/**
	 * Internal method to access the stored values of the cache entry
	 * 
	 * @param <T>
	 *            the key type
	 * @param <R>
	 *            the return type
	 * @param map
	 *            the map to extract the value requested
	 * @param key
	 *            the key t extract the value requested
	 * @return the cache entry or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	private <T extends Object, R extends Object> R getCachedEntry(
			Map<T, CacheEntry> map, T key, String field) {
		if (map == null || !map.containsKey(key)) {
			return null;
		}
		CacheEntry entry = map.get(key);
		try {
			return (R) CacheEntry.class.getField(field).get(entry);
		} catch (Exception e) {
			throw new TopicMapStoreException(
					"Internal error during the access of cache!", e);
		}
	}

	/**
	 * Return the time stamp representing the beginning of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the time stamp or <code>null</code> if the value was not cached
	 *         before
	 */
	public Calendar getRevisionTimestamp(IRevision revision) {
		return getCachedEntry(revisionDependents, revision, TIMESTAMP);
	}

	/**
	 * Return the following revision of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the following revision or <code>null</code> if the value was not
	 *         cached before
	 */
	public IRevision getNextRevision(IRevision revision) {
		return getCachedEntry(revisionDependents, revision, FUTURE);
	}

	/**
	 * Return the previous revision of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the previous revision or <code>null</code> if the value was not
	 *         cached before
	 */
	public IRevision getPastRevision(IRevision revision) {
		return getCachedEntry(revisionDependents, revision, PAST);
	}

	/**
	 * Returns a time stamp representing the last change of the current topic
	 * map
	 * 
	 * @return the lastChange
	 */
	public Calendar getLastModification() {
		return lastChange;
	}

	/**
	 * Returns a time stamp representing the last change of the given topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the lastChange
	 */
	public Calendar getLastModification(ITopic topic) {
		return getCachedEntry(topicDependent, topic, TIMESTAMP);
	}

	/**
	 * Returns the change set of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the change set
	 */
	public Changeset getChangeset(IRevision revision) {
		return getCachedEntry(revisionDependents, revision, CHANGESET);
	}

	/**
	 * Returns the first revision of the history
	 * 
	 * @return the first revision or <code>null</code> if history is empty
	 */
	public IRevision getFirstRevision() {
		return firstRevision;
	}

	/**
	 * Returns the last revision of the history
	 * 
	 * @return the last revision or <code>null</code> if history is empty
	 */
	public IRevision getLastRevision() {
		return lastRevision;
	}

	/**
	 * Returns the revision of the given time stamp.
	 * 
	 * @param timestamp
	 *            the time stamp
	 * @return the revision
	 */
	public IRevision getRevision(Calendar timestamp) {
		if (revisionsByTimestamp == null
				|| !revisionsByTimestamp.containsKey(timestamp)) {
			return null;
		}
		return revisionsByTimestamp.get(timestamp);
	}

	/**
	 * Returns the revision of the given tag.
	 * 
	 * @param tag
	 *            the tag name
	 * @return the revision
	 */
	public IRevision getRevision(final String tag) {
		if (tags == null || !tags.containsKey(tag)) {
			return null;
		}
		return getRevision(tags.get(tag));
	}

	/**
	 * Returns the revision of the given id.
	 * 
	 * @param id
	 *            the id
	 * @return the revision
	 */
	public IRevision getRevision(final long id) {
		if (revisionsById == null || !revisionsById.containsKey(id)) {
			return null;
		}
		return revisionsById.get(id);
	}

	/**
	 * Returns all revisions of the topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the revisions
	 */
	public List<IRevision> getRevisions(ITopic topic) {
		return getCachedEntry(topicDependent, topic, REVISIONS);
	}

	/**
	 * Returns the changes set of the topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the changes set
	 */
	public Changeset getChangeset(ITopic topic) {
		return getCachedEntry(topicDependent, topic, CHANGESET);
	}

	/**
	 * Returns the changes set of all association items typed by the association
	 * type
	 * 
	 * @param associationType
	 *            the association type
	 * @return the changes set
	 */
	public Changeset getAssociationChangeset(ITopic associationType) {
		return getCachedEntry(associationDependents, associationType, CHANGESET);
	}

	/**
	 * Returns all revisions of all association items typed by the association
	 * type
	 * 
	 * @param associationType
	 *            the association type
	 * @return the revisions
	 */
	public List<IRevision> getAssociationRevisions(ITopic associationType) {
		return getCachedEntry(associationDependents, associationType, REVISIONS);
	}

	/**
	 * Returns the value of the meta set of the given revision identified by the
	 * given key
	 * 
	 * @param revision
	 *            the revision
	 * @param key
	 *            the key
	 * @return the value or <code>null</code> if the key is unknown
	 */
	public String getMetaData(final IRevision revision, final String key) {
		Map<String, String> map = getCachedEntry(revisionDependents, revision,
				METADATA);
		if (map == null) {
			return null;
		}
		return map.get(key);
	}

	/**
	 * Returns all meta data sets of the given revision as copy.
	 * 
	 * @param revision
	 *            the revision
	 * @return the copy of all meta data sets
	 */
	public Map<String, String> getMetaData(final IRevision revision) {
		return getCachedEntry(revisionDependents, revision, METADATA);
	}

	/**
	 * Internal method to access the stored values of the cache entry
	 * 
	 * @param <T>
	 *            the key type
	 * @param <R>
	 *            the return type
	 * @param map
	 *            the map to extract the value requested
	 * @param key
	 *            the key t extract the value requested
	 * @return the cache entry or <code>null</code>
	 */
	private <T extends Object, R extends Object> void cachedEntry(
			Map<T, CacheEntry> map, T key, String field, R value) {
		CacheEntry entry = map.get(key);
		if (entry == null) {
			entry = new CacheEntry();
			map.put(key, entry);
		}
		try {
			CacheEntry.class.getField(field).set(entry, value);
		} catch (Exception e) {
			throw new TopicMapStoreException(
					"Internal error during the access of cache!", e);
		}
	}

	/**
	 * Add the timestamp of the given revision to internal cache
	 * 
	 * @param revision
	 *            the revision
	 * @param timestamp
	 *            the timestamp to add
	 */
	public void cacheRevisionTimestamp(IRevision revision, Calendar timestamp) {
		if (revisionDependents == null) {
			revisionDependents = HashUtil.getHashMap();
		}
		cachedEntry(revisionDependents, revision, TIMESTAMP, timestamp);
	}

	/**
	 * Add the future revision to internal cache
	 * 
	 * @param revision
	 *            the revision
	 * @param future
	 *            the revision after the given one
	 */
	public void cacheNextRevision(IRevision revision, IRevision future) {
		if (revisionDependents == null) {
			revisionDependents = HashUtil.getHashMap();
		}
		cachedEntry(revisionDependents, revision, FUTURE, future);
	}

	/**
	 * Add the previous revision of the given revision to internal cache
	 * 
	 * @param revision
	 *            the revision
	 * @param past
	 *            the revision before the given one
	 */
	public void cachePastRevision(IRevision revision, IRevision past) {
		if (revisionDependents == null) {
			revisionDependents = HashUtil.getHashMap();
		}
		cachedEntry(revisionDependents, revision, PAST, past);
	}

	/**
	 * Add the given meta data of the given revision to internal cache
	 * 
	 * @param revision
	 *            the revision
	 * @param metaData
	 *            the meta data
	 */
	public void cacheMetaData(final IRevision revision,
			Map<String, String> metaData) {
		if (revisionDependents == null) {
			revisionDependents = HashUtil.getHashMap();
		}
		cachedEntry(revisionDependents, revision, METADATA, metaData);
	}

	/**
	 * Add time stamp representing the last change of the current topic map to
	 * internal cache
	 * 
	 * @param timestamp
	 *            the timestamp
	 */
	public void cacheLastModification(Calendar timestamp) {
		lastChange = timestamp;
	}

	/**
	 * Add the time stamp representing the last change of the given topic to
	 * internal cache
	 * 
	 * @param topic
	 *            the topic
	 * @param timestamp
	 *            the timestamp
	 */
	public void cacheLastModification(ITopic topic, Calendar timestamp) {
		if (topicDependent == null) {
			topicDependent = HashUtil.getHashMap();
		}
		cachedEntry(topicDependent, topic, TIMESTAMP, timestamp);
	}

	/**
	 * Add the change set of the given revision to internal cache
	 * 
	 * @param revision
	 *            the revision
	 * @param changeset
	 *            the change set to add
	 */
	public void cacheChangeset(IRevision revision, Changeset changeset) {
		if (revisionDependents == null) {
			revisionDependents = HashUtil.getHashMap();
		}
		cachedEntry(revisionDependents, revision, CHANGESET, changeset);
	}

	/**
	 * Add the given revision as first revision to cache
	 * 
	 * @param revision
	 *            the revision
	 */
	public void cacheFirstRevision(IRevision revision) {
		firstRevision = revision;
	}

	/**
	 * Add the given revision as last revision to cache
	 * 
	 * @param revision
	 *            the revision
	 */
	public void cacheLastRevision(IRevision revision) {
		lastRevision = revision;
	}

	/**
	 * Returns the revision of the given time stamp.
	 * 
	 * @param timestamp
	 *            the time stamp
	 * @param revision
	 *            the revision
	 */
	public void cacheRevision(Calendar timestamp, IRevision revision) {
		if (revisionsByTimestamp == null) {
			revisionsByTimestamp = HashUtil.getHashMap();
		}
		revisionsByTimestamp.put(timestamp, revision);
	}

	/**
	 * Add the revision of the given id to internal cache.
	 * 
	 * @param id
	 *            the id
	 * @param revision
	 *            the revision
	 */
	public void cacheRevision(final long id, IRevision revision) {
		if (revisionsById == null) {
			revisionsById = HashUtil.getHashMap();
		}
		revisionsById.put(id, revision);
	}

	/**
	 * Add the revisions of the topic to internal cache
	 * 
	 * @param topic
	 *            the topic
	 * @param revisions
	 *            the revisions to add
	 */
	public void cacheRevisions(ITopic topic, List<IRevision> revisions) {
		if (topicDependent == null) {
			topicDependent = HashUtil.getHashMap();
		}
		cachedEntry(topicDependent, topic, REVISIONS, revisions);
	}

	/**
	 * Add the change set of the topic to internal cache
	 * 
	 * @param topic
	 *            the topic
	 * @param changeset
	 *            the change set to add
	 */
	public void cacheChangeset(ITopic topic, Changeset changeset) {
		if (topicDependent == null) {
			topicDependent = HashUtil.getHashMap();
		}
		cachedEntry(topicDependent, topic, CHANGESET, changeset);
	}

	/**
	 * Add the change set of the association type to internal cache
	 * 
	 * @param associationType
	 *            the association type
	 * @param changeset
	 *            the change set to add
	 */
	public void cacheAssociationChangeset(ITopic associationType,
			Changeset changeset) {
		if (associationDependents == null) {
			associationDependents = HashUtil.getHashMap();
		}
		cachedEntry(associationDependents, associationType, CHANGESET,
				changeset);
	}

	/**
	 * Add the revisions of the association type to internal cache
	 * 
	 * @param associationType
	 *            the association type
	 * @param revisions
	 *            the revisions to add
	 */
	public void cacheAssociationRevisions(ITopic associationType,
			List<IRevision> revisions) {
		if (associationDependents == null) {
			associationDependents = HashUtil.getHashMap();
		}
		cachedEntry(associationDependents, associationType, REVISIONS,
				revisions);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		clear();		
	}
}
