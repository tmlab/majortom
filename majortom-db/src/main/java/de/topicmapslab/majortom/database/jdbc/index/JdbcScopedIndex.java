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
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.nonpaged.CachedScopeIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcScopedIndex extends CachedScopeIndexImpl<JdbcTopicMapStore>
		implements IScopedIndex {

	/**
	 * @param store
	 */
	public JdbcScopedIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> doGetAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getAssociationScopes(
					getStore().getTopicMap(), -1, -1);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationThemes(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByTheme(
					getStore().getTopicMap(), theme, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByThemes(
					getStore().getTopicMap(), themes, all, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(
			Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> doGetNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getNameScopes(
					getStore().getTopicMap(), -1, -1);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNameThemes(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTheme(
					getStore().getTopicMap(), theme, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByThemes(
					getStore().getTopicMap(), themes, all, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> doGetOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getOccurrenceScopes(
					getStore().getTopicMap(), -1, -1);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrenceThemes(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByTheme(
					getStore().getTopicMap(), theme, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByThemes(
					getStore().getTopicMap(), themes, all, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> doGetScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Scoped> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScope(
					getStore().getTopicMap(), scope, -1, -1));
			col.addAll(getStore().getProcessor().getNamesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByScope(
					getStore().getTopicMap(), scope, -1, -1));
			col.addAll(getStore().getProcessor().getVariantsByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> doGetScopables(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Scoped> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			col.addAll(getStore().getProcessor().getNamesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			col.addAll(getStore().getProcessor().getVariantsByScopes(
					getStore().getTopicMap(), scopes, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope doGetScope(Collection<? extends Topic> themes) {
		return getStore().getTopicMap().createScope(
				themes.toArray(new Topic[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> doGetScopes(Collection<? extends Topic> themes,
			boolean matchAll) {
		try {
			return getStore().getProcessor().getScopesByThemes(
					getStore().getTopicMap(), themes, matchAll);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> doGetVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getVariantScopes(
					getStore().getTopicMap(), -1, -1);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByScope(
					getStore().getTopicMap(), scope, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getVariants(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			for (IScope scope : scopes) {
				col.addAll(getStore().getProcessor().getVariantsByScope(
						getStore().getTopicMap(), scope, -1, -1));
			}
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantThemes(
					getStore().getTopicMap(), -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByTheme(
					getStore().getTopicMap(), theme, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> doGetVariants(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByThemes(
					getStore().getTopicMap(), themes, all, -1, -1));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}
}
