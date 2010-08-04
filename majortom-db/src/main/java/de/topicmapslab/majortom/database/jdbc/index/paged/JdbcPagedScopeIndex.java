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
import de.topicmapslab.majortom.model.index.IScopedIndex;

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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Collection<IScope> scopes, int offset, int limit) {
		return super.doGetAssociations(scopes, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(IScope scope, int offset, int limit) {
		return super.doGetAssociations(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Topic theme, int offset, int limit) {
		return super.doGetAssociations(theme, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Association> doGetAssociations(Topic[] themes, boolean all, int offset, int limit) {
		return super.doGetAssociations(themes, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IScope> doGetAssociationScopes(int offset, int limit) {
		return super.doGetAssociationScopes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetAssociationThemes(int offset, int limit) {
		return super.doGetAssociationThemes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<ICharacteristics> doGetCharacteristics(IScope scope, int offset, int limit) {
		return super.doGetCharacteristics(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Collection<IScope> scopes, int offset, int limit) {
		return super.doGetNames(scopes, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(IScope scope, int offset, int limit) {
		return super.doGetNames(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Topic theme, int offset, int limit) {
		return super.doGetNames(theme, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Name> doGetNames(Topic[] themes, boolean all, int offset, int limit) {
		return super.doGetNames(themes, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IScope> doGetNameScopes(int offset, int limit) {
		return super.doGetNameScopes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetNameThemes(int offset, int limit) {
		return super.doGetNameThemes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Collection<IScope> scopes, int offset, int limit) {
		return super.doGetOccurrences(scopes, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(IScope scope, int offset, int limit) {
		return super.doGetOccurrences(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Topic theme, int offset, int limit) {
		return super.doGetOccurrences(theme, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Occurrence> doGetOccurrences(Topic[] themes, boolean all, int offset, int limit) {
		return super.doGetOccurrences(themes, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IScope> doGetOccurrenceScopes(int offset, int limit) {
		return super.doGetOccurrenceScopes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetOccurrenceThemes(int offset, int limit) {
		return super.doGetOccurrenceThemes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Scoped> doGetScopables(IScope scope, int offset, int limit) {
		return super.doGetScopables(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(Collection<IScope> scopes, int offset, int limit) {
		return super.doGetVariants(scopes, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(IScope scope, int offset, int limit) {
		return super.doGetVariants(scope, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(Topic theme, int offset, int limit) {
		return super.doGetVariants(theme, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Variant> doGetVariants(Topic[] themes, boolean all, int offset, int limit) {
		return super.doGetVariants(themes, all, offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<IScope> doGetVariantScopes(int offset, int limit) {
		return super.doGetVariantScopes(offset, limit);
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
	 * <p>
	 * <b>Hint:</b> Method extracts all items from database to enable the usage
	 * of comparators. The operation can be very slowly.
	 * </p>
	 */
	protected List<Topic> doGetVariantThemes(int offset, int limit) {
		return super.doGetVariantThemes(offset, limit);
	}

}
