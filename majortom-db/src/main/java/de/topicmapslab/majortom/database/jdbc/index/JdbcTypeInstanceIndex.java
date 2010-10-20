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
import de.topicmapslab.majortom.index.nonpaged.CachedTypeInstanceIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcTypeInstanceIndex extends
		CachedTypeInstanceIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 */
	public JdbcTypeInstanceIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getAssociationsByType(
					(ITopic) type, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getAssociations(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(
			Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> col = HashUtil.getHashSet();
		for (Topic type : types) {
			col.addAll(getAssociations(type));
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetCharacteristicTypes() {
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
	public Collection<ICharacteristics> doGetCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> col = HashUtil.getHashSet();
		for (Name n : getNames(type)) {
			col.add((IName) n);
		}
		for (Occurrence o : getOccurrences(type)) {
			col.add((IOccurrence) o);
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return doGetCharacteristics(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(
			Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> col = HashUtil.getHashSet();
		for (Name n : getNames(types)) {
			col.add((IName) n);
		}
		for (Occurrence o : getOccurrences(types)) {
			col.add((IOccurrence) o);
		}
		return col;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getNamesByType((ITopic) type,
					-1, -1));
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
		return getNames(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> col = HashUtil.getHashSet();
		for (Topic type : types) {
			col.addAll(getNames(type));
		}
		return col;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getOccurrencesByType(
					(ITopic) type, -1, -1));
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
		return getOccurrences(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(
			Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> col = HashUtil.getHashSet();
		for (Topic type : types) {
			col.addAll(getOccurrences(type));
		}
		return col;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Role> doGetRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Role> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getRolesByType((ITopic) type,
					-1, -1));
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
		return getRoles(Arrays.asList(types));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> doGetRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Role> col = HashUtil.getHashSet();
		for (Topic type : types) {
			col.addAll(getRoles(type));
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getTopicsByType(
					getTopicMapStore().getTopicMap(), type, -1, -1));
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
		return getTopics(Arrays.asList(types), all);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
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
		return getTopics(types, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getTopicsByTypes(types, all,
					-1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getAssociationTypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getNameTypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getOccurrenceTypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getRoleTypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getTopicMapStore().getProcessor().getTopicTypes(
					getTopicMapStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
