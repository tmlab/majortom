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
/**
 * 
 */
package de.topicmapslab.majortom.database.jdbc.index;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.RevisionImpl;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcRevisionIndex extends IndexImpl<JdbcTopicMapStore> implements IRevisionIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 */
	public JdbcRevisionIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset getAssociationChangeset(Topic associationType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (associationType == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			Changeset set = session.getProcessor().doReadChangesetsByAssociationType((ITopic) associationType);
			session.commit();
			session.close();
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> getAssociationRevisions(Topic associationType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (associationType == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			List<IRevision> list = session.getProcessor().doReadRevisionsByAssociationType((ITopic) associationType);
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset getChangeset(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			Changeset set =  session.getProcessor().doReadChangesetsByTopic((ITopic) topic);
			session.commit();
			session.close();
			return set;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getFirstRevision() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			IRevision r = session.getProcessor().doReadFirstRevision(getTopicMapStore().getTopicMap());
			session.commit();
			session.close();
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getLastModification() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			Calendar c = session.getProcessor().doReadLastModification(getTopicMapStore().getTopicMap());
			session.commit();
			session.close();
			return c;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getLastModification(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			Calendar c = session.getProcessor().doReadLastModificationOfTopic((ITopic) topic);
			session.commit();
			session.close();
			return c;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getLastRevision() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			IRevision r = session.getProcessor().doReadLastRevision(getTopicMapStore().getTopicMap());
			session.commit();
			session.close();
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(Calendar timestamp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (timestamp == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			IRevision r =  session.getProcessor().doReadRevisionByTimestamp(getTopicMapStore().getTopicMap(), timestamp);
			session.commit();
			session.close();
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(String tag) throws IndexException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (tag == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			IRevision r =   session.getProcessor().doReadRevisionByTag(getTopicMapStore().getTopicMap(), tag);
			session.commit();
			session.close();
			return r;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(long id) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return new RevisionImpl(getTopicMapStore(), id) {
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> getRevisions(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			ISession session = getTopicMapStore().openSession();
			List<IRevision> list = session.getProcessor().doReadRevisionsByTopic((ITopic) topic);
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void toXml(File file) throws IndexException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (file == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		//NOTHING TO DO
	}

}
