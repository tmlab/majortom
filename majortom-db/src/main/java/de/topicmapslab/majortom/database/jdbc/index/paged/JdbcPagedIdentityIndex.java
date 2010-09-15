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
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedIdentityIndexImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedIdentityIndex extends PagedIdentityIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 * @param parentIndex
	 */
	public JdbcPagedIdentityIndex(JdbcTopicMapStore store, IIdentityIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Construct> doGetConstructsByIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		return super.doGetConstructsByIdentifier(regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Construct> doGetConstructsByIdentifier(Pattern regExp, int offset, int limit) {
		try {
			List<Construct> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getConstructsByIdentitifer(getStore().getTopicMap(), regExp.pattern(), offset, limit));
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
	protected List<Construct> doGetConstructsByItemIdentifier(Pattern regExp, int offset, int limit, Comparator<Construct> comparator) {
		return super.doGetConstructsByItemIdentifier(regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Construct> doGetConstructsByItemIdentifier(Pattern regExp, int offset, int limit) {
		try {
			List<Construct> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getConstructsByItemIdentitifer(getStore().getTopicMap(), regExp.pattern(), offset, limit));
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
	protected List<Locator> doGetItemIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		return super.doGetItemIdentifiers(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Locator> doGetItemIdentifiers(int offset, int limit) {
		try {
			List<Locator> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getItemIdentifiers(getStore().getTopicMap(), offset, limit));
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
	protected List<Locator> doGetSubjectIdentifiers(int offset, int limit, Comparator<Locator> comparator) {
		return super.doGetSubjectIdentifiers(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Locator> doGetSubjectIdentifiers(int offset, int limit) {
		try {
			List<Locator> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getSubjectIdentifiers(getStore().getTopicMap(), offset, limit));
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
	protected List<Locator> doGetSubjectLocators(int offset, int limit, Comparator<Locator> comparator) {
		return super.doGetSubjectLocators(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Locator> doGetSubjectLocators(int offset, int limit) {
		try {
			List<Locator> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getSubjectLocators(getStore().getTopicMap(), offset, limit));
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
	protected List<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTopicsBySubjectIdentifier(regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp, int offset, int limit) {
		try {
			List<Topic> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getTopicsBySubjectIdentitifer(getStore().getTopicMap(), regExp.pattern(), offset, limit));
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
	protected List<Topic> doGetTopicsBySubjectLocator(Pattern regExp, int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetTopicsBySubjectLocator(regExp, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetTopicsBySubjectLocator(Pattern regExp, int offset, int limit) {
		try {
			List<Topic> list = HashUtil.getList();
			list.addAll(getStore().getProcessor().getTopicsBySubjectLocator(getStore().getTopicMap(), regExp.pattern(), offset, limit));
			return list;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
