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
package de.topicmapslab.majortom.inmemory.store.revision;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyAssociation;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyAssociationRole;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyName;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyOccurrence;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyTopic;
import de.topicmapslab.majortom.inmemory.store.revision.readonly.InMemoryReadOnlyVariant;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.revision.IRevisionChange;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.revision.RevisionChangeImpl;
import de.topicmapslab.majortom.revision.RevisionImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Store handling revisions
 * 
 * @author Sven Krosse
 * 
 */
public class RevisionStore implements IDataStore {

	private Map<String, LinkedList<IRevision>> topicRevisions;

	private Map<String, LinkedList<IRevision>> associationRevisions;

	private Map<IAssociation, IRevisionChange> associationCreations;

	private Map<IRevision, Changeset> changesets;

	private Map<String, Changeset> topicChangesets;

	private Map<String, Changeset> associationChangesets;

	private List<IRevision> revisions;

	private Map<IRevision, Calendar> timestamps;

	private Map<String, Calendar> tags;

	private Calendar lastChange;

	private long id = 1;

	private final ITopicMapStore store;

	private Map<String, IConstruct> lazyCopies;

	private Map<IRevision, Map<String, String>> metaData;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the parent store
	 */
	public RevisionStore(ITopicMapStore store) {
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (topicRevisions != null) {
			topicRevisions.clear();
		}

		if (changesets != null) {
			changesets.clear();
		}

		if (revisions != null) {
			revisions.clear();
		}

		if (timestamps != null) {
			timestamps.clear();
		}

		if (tags != null) {
			tags.clear();
		}

		if (topicChangesets != null) {
			topicChangesets.clear();
		}

		if (associationChangesets != null) {
			associationChangesets.clear();
		}

		if (associationRevisions != null) {
			associationRevisions.clear();
		}

		if (associationCreations != null) {
			associationCreations.clear();
		}

		if (metaData != null) {
			metaData.clear();
		}
	}

	/**
	 * Adding a new revision to the internal history
	 * 
	 * @param revision
	 *            the new revision
	 */
	protected void addRevision(IRevision revision) {
		if (revisions == null) {
			revisions = new ArrayList<IRevision>();
		} else if (revisions.contains(revision)) {
			throw new TopicMapStoreException("Revisions is already stored by the internal storage.");
		}
		revisions.add(revision);

		if (timestamps == null) {
			timestamps = HashUtil.getHashMap();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		timestamps.put(revision, calendar);

		if (changesets == null) {
			changesets = HashUtil.getHashMap();
		}
		changesets.put(revision, new Changeset());

		this.lastChange = calendar;
	}

	/**
	 * Adding a new atomic change to the change set of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @param type
	 *            the kind of change
	 * @param context
	 *            the context of change
	 * @param newValue
	 *            the new value
	 * @param oldValue
	 *            the old value
	 */
	public void addChange(IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		addChange(revision, new RevisionChangeImpl(revision, type, context, newValue, oldValue) {
			/**
			 * {@inheritDoc}
			 */
			public IConstruct getContext() {
				IConstruct context = super.getContext();
				if (lazyCopies == null || !lazyCopies.containsKey(context.getId())) {
					return context;
				}
				return lazyCopies.get(context.getId());
			}

			/**
			 * {@inheritDoc}
			 */
			public Object getNewValue() {
				Object newValue = super.getNewValue();
				if (newValue instanceof IConstruct && lazyCopies != null && lazyCopies.containsKey(((IConstruct) newValue).getId())) {
					return lazyCopies.get(((IConstruct) newValue).getId());
				}
				return newValue;
			}

			/**
			 * {@inheritDoc}
			 */
			public Object getOldValue() {
				Object oldValue = super.getOldValue();
				if (oldValue instanceof IConstruct && lazyCopies != null && lazyCopies.containsKey(((IConstruct) oldValue).getId())) {
					return lazyCopies.get(((IConstruct) oldValue).getId());
				}
				return oldValue;
			}
		});
	}

	/**
	 * Adding a new atomic change to the change set of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @param change
	 *            the atomic change
	 */
	private void addChange(IRevision revision, IRevisionChange change) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("Revision is unknown!");
		}

		changesets.get(revision).add(change);

		/*
		 * check if context is a topic or depends on a topic
		 */
		if (change.getContext() instanceof ITopic) {
			storeDependentRevision((ITopic) change.getContext(), revision);
			storeDependentRevisionChanges((ITopic) change.getContext(), change);
		} else if (change.getContext() instanceof ICharacteristics) {
			storeDependentRevision((ITopic) change.getContext().getParent(), revision);
			storeDependentRevisionChanges((ITopic) change.getContext().getParent(), change);
		} else if (change.getContext() instanceof IVariant) {
			storeDependentRevision((ITopic) change.getContext().getParent().getParent(), revision);
			storeDependentRevisionChanges((ITopic) change.getContext().getParent().getParent(), change);
		} else if (change.getContext() instanceof IAssociation) {
			storeDependentRevision((IAssociation) change.getContext(), revision);
			storeDependentRevisionChanges((IAssociation) change.getContext(), change);
			storeDependentRevision((IAssociation) change.getContext(), revision);
			storeDependentRevisionChanges((IAssociation) change.getContext(), change);
		}
		/*
		 * check if old value is a topic
		 */
		if (change.getOldValue() instanceof ITopic) {
			storeDependentRevision((ITopic) change.getOldValue(), revision);
			storeDependentRevisionChanges((ITopic) change.getOldValue(), change);
		}
		/*
		 * check if old value is a role indicates the deletion of a role item
		 */
		else if (change.getOldValue() instanceof IAssociationRole) {
			storeDependentRevision((ITopic) ((IAssociationRole) change.getOldValue()).getPlayer(), revision);
			storeDependentRevisionChanges((ITopic) ((IAssociationRole) change.getOldValue()).getPlayer(), change);
			/*
			 * changes depends on the association
			 */
			storeDependentRevision(((IAssociationRole) change.getOldValue()).getParent(), revision);
			storeDependentRevisionChanges(((IAssociationRole) change.getOldValue()).getParent(), change);
		}
		/*
		 * check if old value is an association item
		 */
		else if (change.getOldValue() instanceof IAssociation) {
			storeDependentRevision((IAssociation) change.getOldValue(), revision);
			storeDependentRevisionChanges((IAssociation) change.getOldValue(), change);
		}

		/*
		 * check if new value is a topic
		 */
		if (change.getNewValue() instanceof ITopic) {
			storeDependentRevision((ITopic) change.getNewValue(), revision);
			storeDependentRevisionChanges((ITopic) change.getNewValue(), change);
		}
		/*
		 * check if new value is an association
		 */
		else if (change.getNewValue() instanceof IAssociation) {
			/*
			 * changes depends on the association
			 */
			storeDependentRevision(((IAssociation) change.getNewValue()), revision);
			storeDependentRevisionChanges(((IAssociation) change.getNewValue()), change);
		}
		/*
		 * check if new value is a role indicates the deletion of a role item
		 */
		else if (change.getNewValue() instanceof IAssociationRole) {
			/*
			 * changes depends on the association
			 */
			storeDependentRevision(((IAssociationRole) change.getNewValue()).getParent(), revision);
			storeDependentRevisionChanges(((IAssociationRole) change.getNewValue()).getParent(), change);
		}
	}

	/**
	 * Store the given revision as a change-set containing at least one change
	 * of the given topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param revision
	 *            the revision
	 */
	public void storeDependentRevision(ITopic topic, IRevision revision) {
		if (topicRevisions == null) {
			topicRevisions = HashUtil.getHashMap();
		}

		LinkedList<IRevision> revisions = topicRevisions.get(topic.getId());
		if (revisions == null) {
			revisions = new LinkedList<IRevision>();
		}
		if (!revisions.contains(revision)) {
			revisions.add(revision);
			topicRevisions.put(topic.getId(), revisions);
		}
	}

	/**
	 * Store the given revision change to the change set of the given topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param change
	 *            the revision change
	 */
	public void storeDependentRevisionChanges(ITopic topic, IRevisionChange change) {
		if (topicChangesets == null) {
			topicChangesets = HashUtil.getHashMap();
		}

		Changeset changeset = topicChangesets.get(topic.getId());
		if (changeset == null) {
			changeset = new Changeset();
		}
		if (!changeset.contains(change)) {
			changeset.add(change);
			topicChangesets.put(topic.getId(), changeset);
		}
	}

	/**
	 * Store the given revision as a change-set containing at least one change
	 * of the given association.
	 * 
	 * @param association
	 *            the association
	 * @param revision
	 *            the revision
	 */
	public void storeDependentRevision(IAssociation association, IRevision revision) {
		if (associationRevisions == null) {
			associationRevisions = HashUtil.getHashMap();
		}

		LinkedList<IRevision> revisions = associationRevisions.get(association.getType().getId());
		if (revisions == null) {
			revisions = new LinkedList<IRevision>();
		}

		if (!revisions.contains(revision)) {
			revisions.add(revision);
			associationRevisions.put(association.getType().getId(), revisions);
		}
	}

	/**
	 * Store the given revision change to the change set of the given
	 * association.
	 * 
	 * @param association
	 *            the association
	 * @param change
	 *            the revision change
	 */
	public void storeDependentRevisionChanges(IAssociation association, IRevisionChange change) {
		if (associationChangesets == null) {
			associationChangesets = HashUtil.getHashMap();
		}

		Changeset changeset = associationChangesets.get(association.getType().getId());
		if (changeset == null) {
			changeset = new Changeset();
		}

		/*
		 * add creation if exists
		 */
		if (associationCreations != null && associationCreations.containsKey(association)) {
			changeset.add(associationCreations.get(association));
			associationCreations.remove(association);
			if (associationCreations.isEmpty()) {
				associationCreations = null;
			}
		}

		if (!changeset.contains(change)) {
			changeset.add(change);
			associationChangesets.put(association.getType().getId(), changeset);
		}
	}

	/**
	 * Return the time stamp representing the beginning of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the time stamp
	 */
	public Calendar getRevisionTimestamp(IRevision revision) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("Revision is unknown!");
		}

		return timestamps.get(revision);
	}

	/**
	 * Return the following revision of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the following revision or <code>null</code> if this is the last
	 *         revision
	 */
	public IRevision getNextRevision(IRevision revision) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("Revision is unknown!");
		}
		int index = revisions.indexOf(revision);
		if (revisions.size() > index + 1) {
			return revisions.get(index + 1);
		}
		return null;
	}

	/**
	 * Return the previous revision of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the previous revision or <code>null</code> if this is the first
	 *         revision
	 */
	public IRevision getPastRevision(IRevision revision) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("Revision is unknown!");
		}
		int index = revisions.indexOf(revision);
		if (index == 0) {
			return null;
		}
		return revisions.get(index - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		// NOTHING TO DO
	}

	/**
	 * Adding a new tag for the current time stamp
	 * 
	 * @param name
	 *            the tag name
	 */
	public void addTag(final String name) {
		addTag(name, new GregorianCalendar());
	}

	/**
	 * Adding a new tag for the given time stamp.
	 * 
	 * @param name
	 *            the tag name
	 * @param calendar
	 *            the time stamp
	 */
	public void addTag(final String name, final Calendar calendar) {
		if (tags == null) {
			tags = HashUtil.getHashMap();
		}
		tags.put(name, calendar);
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
		if (revisions == null || !topicRevisions.containsKey(topic.getId())) {
			return null;
		}
		return topicRevisions.get(topic.getId()).getLast().getTimestamp();
	}

	/**
	 * Returns the change set of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return the change set
	 */
	public Changeset getChangeset(IRevision revision) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("Revision is unknown!");
		}
		return changesets.get(revision);
	}

	/**
	 * Returns the first revision of the history
	 * 
	 * @return the first revision or <code>null</code> if history is empty
	 */
	public IRevision getFirstRevision() {
		if (revisions == null) {
			return null;
		}
		return revisions.get(0);
	}

	/**
	 * Returns the last revision of the history
	 * 
	 * @return the last revision or <code>null</code> if history is empty
	 */
	public IRevision getLastRevision() {
		if (revisions == null) {
			return null;
		}
		return revisions.get(revisions.size() - 1);
	}

	/**
	 * Returns the revision of the given time stamp.
	 * 
	 * @param timestamp
	 *            the time stamp
	 * @return the revision
	 */
	public IRevision getRevision(Calendar timestamp) {
		if (revisions == null) {
			return null;
		}

		IRevision revision = getLastRevision();
		while (revision != null && revision.getTimestamp().after(timestamp)) {
			revision = revision.getPast();
		}
		return revision;
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
			throw new TopicMapStoreException("Unknown tag name!");
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
		if (id >= this.id || id < 0) {
			throw new TopicMapStoreException("Invalid revision id!");
		} else if (revisions == null) {
			throw new TopicMapStoreException("Revision is unknown!");
		}
		return revisions.get((int) id);
	}

	/**
	 * Returns all revisions of the topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the revisions
	 */
	public List<IRevision> getRevisions(ITopic topic) {
		if (revisions == null || topicRevisions == null || !topicRevisions.containsKey(topic.getId())) {
			return new LinkedList<IRevision>();
		}
		List<IRevision> revisions = new LinkedList<IRevision>(topicRevisions.get(topic.getId()));
		return revisions;
	}

	/**
	 * Returns the changes set of the topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the changes set
	 */
	public Changeset getChangeset(ITopic topic) {
		if (revisions == null || topicChangesets == null || !topicChangesets.containsKey(topic.getId())) {
			return new Changeset();
		}
		return topicChangesets.get(topic.getId());
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
		if (revisions == null || associationChangesets == null || !associationChangesets.containsKey(associationType.getId())) {
			return new Changeset();
		}
		return associationChangesets.get(associationType.getId());
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
		if (revisions == null || associationRevisions == null || !associationRevisions.containsKey(associationType.getId())) {
			return new LinkedList<IRevision>();
		}
		List<IRevision> revisions = new LinkedList<IRevision>(associationRevisions.get(associationType.getId()));
		return revisions;
	}

	/**
	 * Create a new revision and store them into the internal history
	 * 
	 * @return the revision
	 */
	public IRevision createRevision() {
		IRevision revision = new RevisionImpl(store, id) {
			// IMPLEMENT ABSTRACT REVISION CLASS
		};
		id++;
		addRevision(revision);
		return revision;
	}

	/**
	 * Exports the information of this history item as XML node.
	 * 
	 * @return the document
	 */
	public Document toXml() throws TopicMapStoreException {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Node root = doc.createElement("history");
			for (IRevision revision : revisions) {
				root.appendChild(revision.toXml(doc));
			}
			doc.appendChild(root);
			return doc;
		} catch (ParserConfigurationException e) {
			throw new TopicMapStoreException(e);
		}
	}

	/**
	 * Store a lazy copy of the given topic
	 * 
	 * @param topic
	 *            the topic
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(ITopic topic) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(topic.getId(), new InMemoryReadOnlyTopic(topic));
	}

	/**
	 * Store a lazy copy of the given occurrence
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(IOccurrence occurrence) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(occurrence.getId(), new InMemoryReadOnlyOccurrence(occurrence));
	}

	/**
	 * Store a lazy copy of the given name
	 * 
	 * @param name
	 *            the name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(IName name) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(name.getId(), new InMemoryReadOnlyName(name));
	}

	/**
	 * Store a lazy copy of the given variant
	 * 
	 * @param variant
	 *            the variant
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(IVariant variant) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(variant.getId(), new InMemoryReadOnlyVariant(variant));
	}

	/**
	 * Store a lazy copy of the given association
	 * 
	 * @param association
	 *            the association
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(IAssociation association) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(association.getId(), new InMemoryReadOnlyAssociation(association));
	}

	/**
	 * Store a lazy copy of the given association role
	 * 
	 * @param role
	 *            the association role
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public void createLazyCopy(IAssociationRole role) throws TopicMapStoreException {
		if (lazyCopies == null) {
			lazyCopies = HashUtil.getHashMap();
		}
		lazyCopies.put(role.getId(), new InMemoryReadOnlyAssociationRole(role));
	}

	/**
	 * Checks if there is a lazy copy stored for this id by the internal store.
	 * 
	 * @param id
	 *            the id
	 * @return <code>true</code> if there is a lazy copy, <code>false</code>
	 *         otherwise.
	 */
	public boolean isLazyCopy(final String id) {
		if (lazyCopies == null || !lazyCopies.containsKey(id)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the lazy copy of the identified by the given id.
	 * 
	 * @param id
	 *            the id
	 * @return the lazy copy or <code>null</code> if there is no lazy copy
	 *         stored
	 */
	public IConstruct getLazyCopy(final String id) {
		if (isLazyCopy(id)) {
			return lazyCopies.get(id);
		}
		return null;
	}

	/**
	 * Add the new key-value-pair to the meta data of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @param key
	 *            the key of meta data
	 * @param value
	 *            the value of meta data
	 */
	public void addMetaData(IRevision revision, final String key, final String value) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("The given revision is unknown. Not stored in the current store." + (revision == null ? "null" : revision.getId()));
		}
		if (metaData == null) {
			metaData = HashUtil.getHashMap();
		}
		Map<String, String> map = metaData.get(revision);
		if (map == null) {
			map = HashUtil.getHashMap();
		}
		map.put(key, value);
		metaData.put(revision, map);
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
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("The given revision is unknown. Not stored in the current store." + (revision == null ? "null" : revision.getId()));
		}
		if (metaData == null || !metaData.containsKey(revision)) {
			return null;
		}
		return metaData.get(revision).get(key);
	}

	/**
	 * Returns all meta data sets of the given revision as copy.
	 * 
	 * @param revision
	 *            the revision
	 * @return the copy of all meta data sets
	 */
	public Map<String, String> getMetaData(final IRevision revision) {
		if (revisions == null || !revisions.contains(revision)) {
			throw new TopicMapStoreException("The given revision is unknown. Not stored in the current store." + (revision == null ? "null" : revision.getId()));
		}
		if (metaData == null || !metaData.containsKey(revision)) {
			return HashUtil.getHashMap();
		}
		return HashUtil.getHashMap(metaData.get(revision));
	}

}
