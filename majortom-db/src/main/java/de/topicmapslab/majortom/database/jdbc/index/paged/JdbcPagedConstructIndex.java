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
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedConstructIndexImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedConstructIndex extends PagedConstructIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 */
	public JdbcPagedConstructIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit,
			Comparator<Association> comparator) {
		return super.doGetAssociationsPlayed(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociationsPlayed(Topic topic, int offset, int limit) {
		try {
			List<Association> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadAssociation((ITopic) topic, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Topic topic, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Topic topic, int offset, int limit) {
		try {
			List<Name> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadNames((ITopic) topic, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfAssociationsPlayed(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfAssociationsPlayed((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfNames(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfNames((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfOccurrences(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfOccurrences((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfRoles(Association association) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfRoles((IAssociation) association);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfRolesPlayed(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfRolesPlayed((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfSupertypes(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfSupertypes((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfTypes(Topic topic) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfTypes((ITopic) topic);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected long doGetNumberOfVariants(Name name) {
		try {
			ISession session = getTopicMapStore().openSession();
			long number = session.getProcessor().doReadNumberOfVariants((IName) name);
			session.commit();
			session.close();
			return number;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Topic topic, int offset, int limit) {
		try {
			List<Occurrence> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadOccurrences((ITopic) topic, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit, Comparator<Role> comparator) {
		return super.doGetRoles(association, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Role> doGetRoles(Association association, int offset, int limit) {
		try {
			List<Role> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadRoles((IAssociation) association, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Role> doGetRolesPlayed(Topic topic, int offset, int limit, Comparator<Role> comparator) {
		return super.doGetRolesPlayed(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Role> doGetRolesPlayed(Topic topic, int offset, int limit) {
		try {
			List<Role> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadRoles((ITopic) topic, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetSupertypes(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Topic topic, int offset, int limit) {
		List<Topic> list = HashUtil.getList();
		list.addAll(getTopicMapStore().getSuptertypes((ITopic) topic, offset, limit));
		return list;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Topic> doGetTypes(Topic topic, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTypes(topic, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Topic> doGetTypes(Topic topic, int offset, int limit) {
		List<Topic> list = HashUtil.getList();
		list.addAll(getTopicMapStore().getTypes((ITopic) topic, offset, limit));
		return list;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(Name name, int offset, int limit, Comparator<Variant> comparator) {
		return super.doGetVariants(name, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage of comparators. The operation can be
	 * very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(Name name, int offset, int limit) {
		try {
			List<Variant> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().doReadVariants((IName) name, offset, limit));
			session.commit();
			session.close();
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
