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
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.paged.PagedScopeIndexImpl;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcPagedScopeIndex extends PagedScopeIndexImpl<JdbcTopicMapStore> {

	/**
	 * @param store
	 * @param parentIndex
	 */
	public JdbcPagedScopeIndex(JdbcTopicMapStore store, IScopedIndex parentIndex) {
		super(store, parentIndex);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Collection<IScope> scopes, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(Collection<IScope> scopes, int offset, int limit) {
		try {
			List<Association> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationsByScopes(getStore().getTopicMap(), scopes, offset, limit));
			return col;
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
	protected List<Association> doGetAssociations(IScope scope, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(IScope scope, int offset, int limit) {
		try {
			List<Association> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationsByScope(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Association> doGetAssociations(Topic theme, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(Topic theme, int offset, int limit) {
		try {
			List<Association> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationsByTheme(getStore().getTopicMap(), theme, offset, limit));
			return col;
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
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit, Comparator<Association> comparator) {
		return super.doGetAssociations(themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit) {
		try {
			List<Association> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationsByThemes(getStore().getTopicMap(), themes, all, offset, limit));
			return col;
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
	protected List<IScope> doGetAssociationScopes(int offset, int limit, Comparator<IScope> comparator) {
		return super.doGetAssociationScopes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IScope> doGetAssociationScopes(int offset, int limit) {
		try {
			List<IScope> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationScopes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Topic> doGetAssociationThemes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetAssociationThemes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetAssociationThemes(int offset, int limit) {
		try {
			List<Topic> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getAssociationThemes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<ICharacteristics> doGetCharacteristics(IScope scope, int offset, int limit, Comparator<ICharacteristics> comparator) {
		return super.doGetCharacteristics(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ICharacteristics> doGetCharacteristics(IScope scope, int offset, int limit) {
		try {
			List<ICharacteristics> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getCharacteristicsByScope(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Name> doGetNames(Collection<IScope> scopes, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(Collection<IScope> scopes, int offset, int limit) {
		try {
			List<Name> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNamesByScopes(getStore().getTopicMap(), scopes, offset, limit));
			return col;
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
	protected List<Name> doGetNames(IScope scope, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(IScope scope, int offset, int limit) {
		try {
			List<Name> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNamesByScope(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Name> doGetNames(Topic theme, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(Topic theme, int offset, int limit) {
		try {
			List<Name> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNamesByTheme(getStore().getTopicMap(), theme, offset, limit));
			return col;
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
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit, Comparator<Name> comparator) {
		return super.doGetNames(themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit) {

		try {
			List<Name> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNamesByThemes(getStore().getTopicMap(), themes, all, offset, limit));
			return col;
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
	protected List<IScope> doGetNameScopes(int offset, int limit, Comparator<IScope> comparator) {
		return super.doGetNameScopes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IScope> doGetNameScopes(int offset, int limit) {
		try {
			List<IScope> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNameScopes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Topic> doGetNameThemes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetNameThemes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetNameThemes(int offset, int limit) {
		try {
			List<Topic> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getNameThemes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Occurrence> doGetOccurrences(Collection<IScope> scopes, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(Collection<IScope> scopes, int offset, int limit) {
		try {
			List<Occurrence> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrencesByScopes(getStore().getTopicMap(), scopes, offset, limit));
			return col;
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
	protected List<Occurrence> doGetOccurrences(IScope scope, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(IScope scope, int offset, int limit) {
		try {
			List<Occurrence> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrencesByScope(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Occurrence> doGetOccurrences(Topic theme, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(Topic theme, int offset, int limit) {
		try {
			List<Occurrence> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrencesByTheme(getStore().getTopicMap(), theme, offset, limit));
			return col;
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
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit, Comparator<Occurrence> comparator) {
		return super.doGetOccurrences(themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		try {
			List<Occurrence> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrencesByThemes(getStore().getTopicMap(), themes, all, offset, limit));
			return col;
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
	protected List<IScope> doGetOccurrenceScopes(int offset, int limit, Comparator<IScope> comparator) {
		return super.doGetOccurrenceScopes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IScope> doGetOccurrenceScopes(int offset, int limit) {
		try {
			List<IScope> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrenceScopes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Topic> doGetOccurrenceThemes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetOccurrenceThemes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetOccurrenceThemes(int offset, int limit) {
		try {
			List<Topic> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getOccurrenceThemes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit, Comparator<Scoped> comparator) {
		return super.doGetScopables(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit) {
		try {
			List<Scoped> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getScopables(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit, Comparator<Variant> comparator) {
		return super.doGetVariants(scopes, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit) {
		try {
			List<Variant> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantsByScopes(getStore().getTopicMap(), scopes, offset, limit));
			return col;
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
	protected List<Variant> doGetVariants(IScope scope, int offset, int limit, Comparator<Variant> comparator) {
		return super.doGetVariants(scope, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Variant> doGetVariants(IScope scope, int offset, int limit) {
		try {
			List<Variant> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantsByScope(getStore().getTopicMap(), scope, offset, limit));
			return col;
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
	protected List<Variant> doGetVariants(Topic theme, int offset, int limit, Comparator<Variant> comparator) {
		return super.doGetVariants(theme, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Variant> doGetVariants(Topic theme, int offset, int limit) {
		try {
			List<Variant> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantsByTheme(getStore().getTopicMap(), theme, offset, limit));
			return col;
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
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit, Comparator<Variant> comparator) {
		return super.doGetVariants(themes, all, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit) {
		try {
			List<Variant> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantsByThemes(getStore().getTopicMap(), themes, all, offset, limit));
			return col;
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
	protected List<IScope> doGetVariantScopes(int offset, int limit, Comparator<IScope> comparator) {
		return super.doGetVariantScopes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<IScope> doGetVariantScopes(int offset, int limit) {
		try {
			List<IScope> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantScopes(getStore().getTopicMap(), offset, limit));
			return col;
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
	protected List<Topic> doGetVariantThemes(int offset, int limit, Comparator<Topic> comparator) {
		return super.doGetVariantThemes(offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<Topic> doGetVariantThemes(int offset, int limit) {
		try {
			List<Topic> col = HashUtil.getList();
			col.addAll(getStore().getProcessor().getVariantThemes(getStore().getTopicMap(), offset, limit));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

}
