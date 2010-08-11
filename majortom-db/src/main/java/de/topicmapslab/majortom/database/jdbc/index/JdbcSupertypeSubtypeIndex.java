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
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcSupertypeSubtypeIndex extends IndexImpl<JdbcTopicMapStore> implements ISupertypeSubtypeIndex {

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
	public Collection<Topic> getDirectSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getDirectSubtypes(getStore().getTopicMap(), (ITopic) type));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getDirectSupertypes(getStore().getTopicMap(), (ITopic) type));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap()));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap(), (ITopic) type));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic... types) {
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
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types) {
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
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap(), types, all));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap()));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type != null && !type.getTopicMap().equals(getStore().getTopicMap())) {
			throw new IllegalArgumentException("Topic has to be a part of this topic map.");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap(), (ITopic) type));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic... types) {
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
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types) {
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
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap(), types, all));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
