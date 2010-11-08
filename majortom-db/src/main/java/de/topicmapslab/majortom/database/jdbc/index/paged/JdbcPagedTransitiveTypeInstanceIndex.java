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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedTypeInstanceIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedTransitiveTypeInstanceIndex extends PagedTypeInstanceIndexImpl<JdbcTopicMapStore> implements IPagedTransitiveTypeInstanceIndex {

	/**
	 * @param store
	 * @param parentIndex
	 */
	public JdbcPagedTransitiveTypeInstanceIndex(JdbcTopicMapStore store, ITransitiveTypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}
	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetAssociationTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getAssociationTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
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
	protected List<Topic> doGetAssociationTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetAssociationTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit) {
		try {
			List<Association> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getAssociationsByTypeTransitive(getTopicMapStore().getTopicMap(),types, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Collection<? extends Topic> types, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(types, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(Topic type, int offset, int limit) {
		try {
			List<Association> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getAssociationsByTypeTransitive((ITopic) type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Topic type, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getCharacteristicsByTypesTransitive(types, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(types, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit) {
		try {
			List<ICharacteristics> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getCharacteristicsByTypeTransitive((ITopic) type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(Topic type, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetCharacteristicTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getCharacteristicsTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
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
	protected List<Topic> doGetCharacteristicTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetCharacteristicTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit) {
		try {
			List<Name> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getNamesByTypeTransitive(getTopicMapStore().getTopicMap(),types, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Collection<? extends Topic> types, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(types, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(Topic type, int offset, int limit) {
		try {
			List<Name> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getNamesByTypeTransitive((ITopic) type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Topic type, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetNameTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getNameTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
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
	protected List<Topic> doGetNameTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetNameTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit) {
		try {
			List<Occurrence> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getOccurrencesByTypeTransitive(getTopicMapStore().getTopicMap(),types, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Collection<? extends Topic> types, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(types, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(Topic type, int offset, int limit) {
		try {
			List<Occurrence> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getOccurrencesByTypeTransitive((ITopic) type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Topic type, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetOccurrenceTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetOccurrenceTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetOccurrenceTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getOccurrenceTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
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
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit, Comparator<Role> comparator) {
		return super.doGetRoles(types, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Role> doGetRoles(Collection<? extends Topic> types, int offset, int limit) {
		try {
			List<Role> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getRolesByTypeTransitive(getTopicMapStore().getTopicMap(),types, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Role> doGetRoles(Topic type, int offset, int limit, Comparator<Role> comparator) {
		return super.doGetRoles(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Role> doGetRoles(Topic type, int offset, int limit) {
		try {
			List<Role> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getRolesByTypeTransitive((ITopic) type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetRoleTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetRoleTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetRoleTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getRoleTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
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
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTopics(types, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetTopics(Collection<Topic> types, boolean all, int offset, int limit) {
		try {
			List<Topic> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getTopicsByTypesTransitive(getTopicMapStore().getTopicMap(),types, all, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetTopics(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTopics(type, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetTopics(Topic type, int offset, int limit) {
		try {
			List<Topic> list = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			list.addAll(session.getProcessor().getTopicsByTypeTransitive((ITopic)type, offset, limit));
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
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetTopicTypes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTopicTypes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetTopicTypes(int offset, int limit) {
		try {
			List<Topic> types = HashUtil.getList();
			ISession session = getTopicMapStore().openSession();
			types.addAll(session.getProcessor().getTopicTypes(getTopicMapStore().getTopicMap(), offset, limit));
			session.commit();
			session.close();
			return types;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}	
}
