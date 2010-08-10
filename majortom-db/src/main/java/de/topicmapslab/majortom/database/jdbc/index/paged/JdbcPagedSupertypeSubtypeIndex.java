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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedSupertypeSubtypeIndex extends PagedSupertypeSubtypeIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 * @param parentIndex
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit) {
		return super.doGetDirectSubtypes(type, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit) {
		return super.doGetDirectSupertypes(type, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		return super.doGetSubtypes(types, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit) {
		return super.doGetSubtypes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit) {
		return super.doGetSubtypes(type, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		return super.doGetSupertypes(types, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit) {
		return super.doGetSupertypes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit) {
		return super.doGetSupertypes(type, offset, limit);
	}

}
