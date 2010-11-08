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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.nonpaged.CachedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcSupertypeSubtypeIndex extends
		CachedSupertypeSubtypeIndexImpl<JdbcTopicMapStore> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public JdbcSupertypeSubtypeIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetDirectSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getDirectSubtypes(
					getTopicMapStore().getTopicMap(), (ITopic) type, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetDirectSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getDirectSupertypes(
					getTopicMapStore().getTopicMap(), (ITopic) type, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSubtypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSubtypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSubtypes(
					getTopicMapStore().getTopicMap(), (ITopic) type, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSubtypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		return getSubtypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSubtypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		return getSubtypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSubtypes(Collection<? extends Topic> types,
			boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSubtypes(
					getTopicMapStore().getTopicMap(), types, all, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSupertypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSupertypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type != null
				&& !type.getTopicMap().equals(getTopicMapStore().getTopicMap())) {
			throw new IllegalArgumentException(
					"Topic has to be a part of this topic map.");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSupertypes(
					getTopicMapStore().getTopicMap(), (ITopic) type, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSupertypes(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		return getSupertypes(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSupertypes(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		return getSupertypes(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetSupertypes(Collection<? extends Topic> types,
			boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getSupertypes(
					getTopicMapStore().getTopicMap(), types, all, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
