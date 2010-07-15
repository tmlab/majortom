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
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcScopedIndex extends JdbcIndex implements IScopedIndex {

	/**
	 * @param store
	 */
	public JdbcScopedIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getAssociationScopes(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getAssociations(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScopes(getStore().getTopicMap(), scopes));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationThemes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByTheme(getStore().getTopicMap(), theme));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByThemes(getStore().getTopicMap(), themes, all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScope(getStore().getTopicMap(), scope));
			col.addAll(getStore().getProcessor().getOccurrencesByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<IScope> c = Arrays.asList(scopes);
			Collection<ICharacteristics> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScopes(getStore().getTopicMap(), c));
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(getStore().getTopicMap(), c));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getNameScopes(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getNames(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByScopes(getStore().getTopicMap(), scopes));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNameThemes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByTheme(getStore().getTopicMap(), theme));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByThemes(getStore().getTopicMap(), themes, all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getOccurrenceScopes(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getOccurrences(Arrays.asList(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(getStore().getTopicMap(), scopes));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrenceThemes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByTheme(getStore().getTopicMap(), theme));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByThemes(getStore().getTopicMap(), themes, all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Scoped> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScope(getStore().getTopicMap(), scope));
			col.addAll(getStore().getProcessor().getNamesByScope(getStore().getTopicMap(), scope));
			col.addAll(getStore().getProcessor().getOccurrencesByScope(getStore().getTopicMap(), scope));
			col.addAll(getStore().getProcessor().getVariantsByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<IScope> c = Arrays.asList(scopes);
			Collection<Scoped> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByScopes(getStore().getTopicMap(), c));
			col.addAll(getStore().getProcessor().getNamesByScopes(getStore().getTopicMap(), c));
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(getStore().getTopicMap(), c));
			col.addAll(getStore().getProcessor().getVariantsByScopes(getStore().getTopicMap(), c));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getStore().getTopicMap().createScope(themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<? extends Topic> themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		return getStore().getTopicMap().createScope(themes.toArray(new Topic[0]));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), Arrays.asList(themes), false);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), Arrays.asList(themes), matchAll);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), themes, matchAll);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getVariantScopes(getStore().getTopicMap());
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByScope(getStore().getTopicMap(), scope));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope... scopes) {
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
	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByScopes(getStore().getTopicMap(), scopes));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantThemes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByTheme(getStore().getTopicMap(), theme));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic[] themes, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Arguments cannot be null!");
		}
		try {
			Collection<Variant> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getVariantsByThemes(getStore().getTopicMap(), themes, all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

}
