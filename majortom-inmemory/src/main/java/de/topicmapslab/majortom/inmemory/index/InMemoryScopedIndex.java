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
package de.topicmapslab.majortom.inmemory.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of {@link IScopedIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryScopedIndex extends IndexImpl<InMemoryTopicMapStore> implements IScopedIndex {

	/**
	 * constructor
	 * 
	 * @param store the in-memory store
	 */
	public InMemoryScopedIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}

		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : themes) {
			set.add((ITopic) t);
		}

		return getStore().getScopeStore().getScope(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<? extends Topic> themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : themes) {
			set.add((ITopic) t);
		}

		return getStore().getScopeStore().getScope(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic... themes) {
		return Collections.unmodifiableCollection(getScopes(themes, false));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		Set<IScope> scopes = HashUtil.getHashSet();
		for (Topic t : themes) {
			if (scopes.isEmpty() || !matchAll) {
				scopes.addAll(getStore().getScopeStore().getScopes((ITopic) t));
			} else {
				scopes.retainAll(getStore().getScopeStore().getScopes((ITopic) t));
			}
		}
		return Collections.unmodifiableCollection(scopes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		Set<IScope> scopes = HashUtil.getHashSet();
		for (Topic t : themes) {
			if (scopes.isEmpty() || !matchAll) {
				scopes.addAll(getStore().getScopeStore().getScopes((ITopic) t));
			} else {
				scopes.retainAll(getStore().getScopeStore().getScopes((ITopic) t));
			}
		}
		return Collections.unmodifiableCollection(scopes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<Scoped> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScoped(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Scoped> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScoped(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (IScope scope : getAssociationScopes()) {
			set.addAll(scope.getThemes());
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return Collections.unmodifiableCollection(getStore().getScopeStore().getAssociationScopes());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScopedAssociations(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedAssociations(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedAssociations(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			return Collections.unmodifiableCollection(getAssociations(getStore().getScopeStore().getEmptyScope()));
		}
		return Collections.unmodifiableCollection(getAssociations(getScopes(theme)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		return Collections.unmodifiableCollection(getAssociations(getScopes(themes, matchAll)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScopedNames(scope));
		set.addAll(getStore().getScopeStore().getScopedOccurrences(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedNames(scope));
			set.addAll(getStore().getScopeStore().getScopedOccurrences(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (IScope scope : getNameScopes()) {
			set.addAll(scope.getThemes());
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return Collections.unmodifiableCollection(getStore().getScopeStore().getNameScopes());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScopedNames(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedNames(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedNames(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			return Collections.unmodifiableCollection(getNames(getStore().getScopeStore().getEmptyScope()));
		}
		return Collections.unmodifiableCollection(getNames(getScopes(theme)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}

		return Collections.unmodifiableCollection(getNames(getScopes(themes, matchAll)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (IScope scope : getOccurrenceScopes()) {
			set.addAll(scope.getThemes());
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return Collections.unmodifiableCollection(getStore().getScopeStore().getOccurrenceScopes());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScopedOccurrences(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedOccurrences(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedOccurrences(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			return Collections.unmodifiableCollection(getOccurrences(getStore().getScopeStore().getEmptyScope()));
		}
		return Collections.unmodifiableCollection(getOccurrences(getScopes(theme)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		return Collections.unmodifiableCollection(getOccurrences(getScopes(themes, matchAll)));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (IScope scope : getVariantScopes()) {
			set.addAll(scope.getThemes());
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return Collections.unmodifiableCollection(getStore().getScopeStore().getVariantScopes());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope cannot be null!");
		}
		Set<Variant> set = HashUtil.getHashSet();
		set.addAll(getStore().getScopeStore().getScopedVariants(scope));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Variant> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedVariants(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (scopes == null) {
			throw new IllegalArgumentException("Scopes cannot be null!");
		}
		Set<Variant> set = HashUtil.getHashSet();
		for (IScope scope : scopes) {
			set.addAll(getStore().getScopeStore().getScopedVariants(scope));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic theme) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (theme == null) {
			throw new IllegalArgumentException("Theme cannot be null!");
		}

		Set<IScope> scopes = HashUtil.getHashSet();
		for (IScope scope : getVariantScopes()) {
			if (scope.containsTheme((ITopic) theme)) {
				scopes.add(scope);
			}
		}
		return Collections.unmodifiableCollection(getVariants(scopes));
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (themes == null) {
			throw new IllegalArgumentException("Themes cannot be null!");
		}
		Set<ITopic> themes_ = HashUtil.getHashSet();
		for (Topic theme : themes) {
			themes_.add((ITopic) theme);
		}
		Set<IScope> scopes = HashUtil.getHashSet();
		for (IScope scope : getVariantScopes()) {
			if (matchAll && scope.getThemes().containsAll(themes_)) {
				scopes.add(scope);
			} else if (!matchAll) {
				for (ITopic theme : themes_) {
					if (scope.containsTheme(theme)) {
						scopes.add(scope);
						break;
					}
				}
			}
		}
		return Collections.unmodifiableCollection(getVariants(scopes));
	}

}
