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
package de.topicmapslab.majortom.database.jdbc.index.paged;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedSupertypeSubtypeIndex extends PagedSupertypeSubtypeIndexImpl<JdbcTopicMapStore> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 * @param parentIndex
	 *            the parent index
	 */
	public JdbcPagedSupertypeSubtypeIndex(JdbcTopicMapStore store, ISupertypeSubtypeIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetDirectSubtypes(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getDirectSubtypes(getStore().getTopicMap(), (ITopic) type, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetDirectSupertypes(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getDirectSupertypes(getStore().getTopicMap(), (ITopic) type, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSubtypes(types, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap(), types, all, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSubtypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap(), offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSubtypes(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSubtypes(getStore().getTopicMap(), (ITopic) type, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSupertypes(types, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap(), types, all, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSupertypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap(), offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSupertypes(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit) {
		try {
			List<Topic> topics = HashUtil.getList();
			topics.addAll(getStore().getProcessor().getSupertypes(getStore().getTopicMap(), (ITopic) type, offset, limit));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
