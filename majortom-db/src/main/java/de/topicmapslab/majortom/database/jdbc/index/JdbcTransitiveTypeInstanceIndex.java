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

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcTransitiveTypeInstanceIndex extends IndexImpl<JdbcTopicMapStore> implements ITransitiveTypeInstanceIndex {

	/**
	 * @param store
	 */
	public JdbcTransitiveTypeInstanceIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getAssociations(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getCharacteristicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> col = HashUtil.getHashSet(getNameTypes());
		col.addAll(getOccurrenceTypes());
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTypeTransitive((ITopic) type, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByTypeTransitive((ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getCharacteristics(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getNames(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getOccurrences(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getRoles(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Role> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getRolesByTypeTransitive(getStore().getTopicMap(), types, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getTopics(Arrays.asList(types), false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getTopics(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByTypesTransitive(getStore().getTopicMap(), types, all, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationTypes(getStore().getTopicMap(),-1,-1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByTypeTransitive((ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNameTypes(getStore().getTopicMap(),-1,-1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTypeTransitive((ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrenceTypes(getStore().getTopicMap(),-1,-1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByTypeTransitive((ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getRoleTypes(getStore().getTopicMap(),-1,-1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		try {
			Collection<Role> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getRolesByTypeTransitive((ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicTypes(getStore().getTopicMap(),-1,-1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			if (type == null) {
				col.addAll(getStore().getProcessor().getTopicsByType(getStore().getTopicMap(),type,-1,-1));
			} else {
				col.addAll(getStore().getProcessor().getTopicsByTypeTransitive((ITopic) type, -1, -1));
			}
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic[] types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Arguments may not be null!");
		}
		return getTopics(Arrays.asList(types), all);
	}

}
