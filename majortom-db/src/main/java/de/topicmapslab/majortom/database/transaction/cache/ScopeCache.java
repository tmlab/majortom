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
package de.topicmapslab.majortom.database.transaction.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.ModifableTopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class ScopeCache implements IDataStore {

	private Set<String> modifiedScopeables;
	private final TransactionTopicMapStore topicMapStore;

	/**
	 * storage map of scope-themes mapping
	 */
	private Map<IScope, Set<ITopic>> scopes;
	/**
	 * storage map of scope-name relation
	 */
	private Map<IScope, Set<IName>> scopedNames;
	/**
	 * storage map of scope-occurrence relation
	 */
	private Map<IScope, Set<IOccurrence>> scopedOccurrences;
	/**
	 * storage map of scope-variant relation
	 */
	private Map<IScope, Set<IVariant>> scopedVariants;
	/**
	 * storage map of scope-association relation
	 */
	private Map<IScope, Set<IAssociation>> scopedAssociations;
	/**
	 * storage map of name-scope relation
	 */
	private Map<IName, IScope> nameScopes;
	/**
	 * storage map of occurrence-scope relation
	 */
	private Map<IOccurrence, IScope> occurrenceScopes;
	/**
	 * storage map of variant-scope relation
	 */
	private Map<IVariant, IScope> variantScopes;
	/**
	 * storage map of association-scope relation
	 */
	private Map<IAssociation, IScope> associationScopes;
	/**
	 * empty scope
	 */
	private IScope emptyScope;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public ScopeCache(TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
		emptyScope = new ScopeImpl(getTransactionStore().generateIdentity().getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (scopes != null) {
			scopes.clear();
		}
		if (scopedNames != null) {
			scopedNames.clear();
		}
		if (scopedOccurrences != null) {
			scopedOccurrences.clear();
		}
		if (scopedVariants != null) {
			scopedVariants.clear();
		}
		if (scopedAssociations != null) {
			scopedAssociations.clear();
		}
		if (nameScopes != null) {
			nameScopes.clear();
		}
		if (occurrenceScopes != null) {
			occurrenceScopes.clear();
		}
		if (variantScopes != null) {
			variantScopes.clear();
		}
		if (associationScopes != null) {
			associationScopes.clear();
		}
		if (modifiedScopeables != null) {
			modifiedScopeables.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getAssociationScopes() {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		scopes: for (IScope scope : index.getAssociationScopes()) {
			for (Association association : index.getAssociations(scope)) {
				if (isRemovedConstruct((IAssociation) association)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(association.getId())) {
					set.add(getTransactionStore().getIdentityStore().createLazyStub(scope));
					continue scopes;
				}
			}
		}
		if (scopedAssociations != null) {
			set.addAll(scopedAssociations.keySet());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getNameScopes() {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		scopes: for (IScope scope : index.getNameScopes()) {
			for (Name name : index.getNames(scope)) {
				if (isRemovedConstruct((IName) name)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(name.getId())) {
					set.add(getTransactionStore().getIdentityStore().createLazyStub(scope));
					continue scopes;
				}
			}
		}
		if (scopedNames != null) {
			set.addAll(scopedNames.keySet());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getOccurrenceScopes() {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		scopes: for (IScope scope : index.getOccurrenceScopes()) {
			for (Occurrence occurrence : index.getOccurrences(scope)) {
				if (isRemovedConstruct((IOccurrence) occurrence)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(occurrence.getId())) {
					set.add(getTransactionStore().getIdentityStore().createLazyStub(scope));
					continue scopes;
				}
			}
		}
		if (scopedOccurrences != null) {
			set.addAll(scopedOccurrences.keySet());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getVariantScopes() {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		scopes: for (IScope scope : index.getVariantScopes()) {
			for (Variant variant : index.getVariants(scope)) {
				if (isRemovedConstruct((IVariant) variant)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(variant.getId())) {
					set.add(getTransactionStore().getIdentityStore().createLazyStub(scope));
					continue scopes;
				}
			}
		}
		if (scopedVariants != null) {
			set.addAll(scopedVariants.keySet());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<ITopic> themes) {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> themes_ = HashUtil.getHashSet();
		for (ITopic theme : themes) {
			if (isRemovedConstruct(theme)) {
				throw new ConstructRemovedException(theme);
			}
			themes_.add(getTransactionStore().getIdentityStore().createLazyStub(theme));
		}

		/*
		 * create scope
		 */
		if (themes_.isEmpty()) {
			return emptyScope;
		}
		if (scopes == null) {
			scopes = HashUtil.getHashMap();
		}
		for (Entry<IScope, Set<ITopic>> entry : scopes.entrySet()) {
			if (entry.getValue().size() == themes_.size() && entry.getValue().containsAll(themes_)) {
				return entry.getKey();
			}
		}
		Set<ITopic> set = HashUtil.getHashSet();
		set.addAll(themes_);
		IScope scope = new ScopeImpl(getTransactionStore().generateIdentity().getId(), set);
		scopes.put(scope, set);
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(ITopic... themes) {
		return getScope(Arrays.asList(themes));
	}

	/**
	 * Returns the scope of the scoped construct
	 * 
	 * @param scoped
	 *            the scoped construct
	 * @return the scope and never <code>null</code>
	 */
	public IScope getScope(IScopable scoped) {
		if (scoped instanceof IAssociation) {
			return getScope((IAssociation) scoped);
		} else if (scoped instanceof IOccurrence) {
			return getScope((IOccurrence) scoped);
		} else if (scoped instanceof IName) {
			return getScope((IName) scoped);
		} else if (scoped instanceof IVariant) {
			return getScope((IVariant) scoped);
		} else {
			throw new TopicMapStoreException("Type of scoped item is unknown '" + scoped.getClass() + "'.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IAssociation scoped) {
		if (isRemovedConstruct(scoped)) {
			throw new ConstructRemovedException(scoped);
		}

		if (containsScopeable(scoped)) {
			return associationScopes.get(scoped);
		}
		/*
		 * read from underlying topic map store
		 */
		IScope s = getTransactionStore().getIdentityStore().createLazyStub((IScope) getTopicMapStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		if (associationScopes == null) {
			associationScopes = HashUtil.getHashMap();
		}
		associationScopes.put(scoped, s);
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IName scoped) {
		if (containsScopeable(scoped)) {
			return nameScopes.get(scoped);
		}
		/*
		 * read from underlying topic map store
		 */
		IScope s = getTransactionStore().getIdentityStore().createLazyStub((IScope) getTopicMapStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		if (nameScopes == null) {
			nameScopes = HashUtil.getHashMap();
		}
		nameScopes.put(scoped, s);
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IOccurrence scoped) {
		if (containsScopeable(scoped)) {
			return occurrenceScopes.get(scoped);
		}
		/*
		 * read from underlying topic map store
		 */
		IScope s = getTransactionStore().getIdentityStore().createLazyStub((IScope) getTopicMapStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		if (occurrenceScopes == null) {
			occurrenceScopes = HashUtil.getHashMap();
		}
		occurrenceScopes.put(scoped, s);
		return s;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IVariant scoped) {
		if (containsScopeable(scoped)) {
			return variantScopes.get(scoped);
		}
		/*
		 * read from underlying topic map store
		 */
		IScope s = getTransactionStore().getIdentityStore().createLazyStub((IScope) getTopicMapStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		if (variantScopes == null) {
			variantScopes = HashUtil.getHashMap();
		}
		variantScopes.put(scoped, s);
		return s;
	}

	/**
	 * Returns all scoped items of the given scope and given type.
	 * 
	 * @param scope
	 *            the scope
	 * @return the scoped construct
	 */
	@SuppressWarnings("unchecked")
	private <T extends IScopable> Set<T> internalGetScoped(IScope scope, Class<? extends T> clazz) {
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<Scoped> set = HashUtil.getHashSet();
		if (Name.class.isAssignableFrom(clazz)) {
			set.addAll(index.getNames(scope));
		} else if (Occurrence.class.isAssignableFrom(clazz)) {
			set.addAll(index.getOccurrences(scope));
		} else if (Association.class.isAssignableFrom(clazz)) {
			set.addAll(index.getAssociations(scope));
		} else if (Variant.class.isAssignableFrom(clazz)) {
			set.addAll(index.getVariants(scope));
		}

		Set<T> scopables = HashUtil.getHashSet();
		for (Scoped scoped : set) {
			T scopable = (T) scoped;
			if (isRemovedConstruct(scopable)) {
				continue;
			}
			if (modifiedScopeables == null || !modifiedScopeables.contains(scopable.getId())) {
				scopables.add(getTransactionStore().getIdentityStore().createLazyStub(scopable));
			}
		}
		if (Name.class.isAssignableFrom(clazz)) {
			if (scopedNames != null && scopedNames.containsKey(scope)) {
				scopables.addAll((Set<T>) scopedNames.get(scope));
			}
		} else if (Occurrence.class.isAssignableFrom(clazz)) {
			if (scopedOccurrences != null && scopedOccurrences.containsKey(scope)) {
				scopables.addAll((Set<T>) scopedOccurrences.get(scope));
			}
		} else if (Association.class.isAssignableFrom(clazz)) {
			if (scopedAssociations != null && scopedAssociations.containsKey(scope)) {
				scopables.addAll((Set<T>) scopedAssociations.get(scope));
			}
		} else if (Variant.class.isAssignableFrom(clazz)) {
			if (scopedVariants != null && scopedVariants.containsKey(scope)) {
				scopables.addAll((Set<T>) scopedVariants.get(scope));
			}
		}
		return scopables;
	}

	/**
	 * Returns all scoped items of the given scope.
	 * 
	 * @param scope
	 *            the scope
	 * @return the scoped construct
	 */
	public Set<IScopable> getScoped(IScope scope) {
		Set<IScopable> scoped = HashUtil.getHashSet();
		scoped.addAll(getScopedAssociations(scope));
		scoped.addAll(getScopedOccurrences(scope));
		scoped.addAll(getScopedNames(scope));
		scoped.addAll(getScopedVariants(scope));
		return scoped;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> getScopedAssociations(IScope scope) {
		return internalGetScoped(scope, IAssociation.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> getScopedNames(IScope scope) {
		return internalGetScoped(scope, IName.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> getScopedOccurrences(IScope scope) {
		return internalGetScoped(scope, IOccurrence.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> getScopedVariants(IScope scope) {
		return internalGetScoped(scope, IVariant.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getScopes(ITopic theme) {
		if (isRemovedConstruct(theme)) {
			throw new ConstructRemovedException(theme);
		}
		IScopedIndex index = getTopicMapStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> scopes = HashUtil.getHashSet();
		for (IScope scope : index.getScopes(theme)) {
			if (getTransactionStore().getIdentityStore().isRemovedScope(scope)) {
				continue;
			}
			if (getScoped(scope).isEmpty()) {
				continue;
			}
			scopes.add(getTransactionStore().getIdentityStore().createLazyStub(scope));
		}
		if (this.scopes != null) {
			for (IScope s : this.scopes.keySet()) {
				if (s instanceof ScopeImpl && s.containsTheme(theme)) {
					scopes.add(s);
				}
			}
		}
		return scopes;
	}

	/**
	 * Store the relation between the given scoped item and the scope.
	 * 
	 * @param scoped
	 *            the scoped construct
	 * @param s
	 *            the scope
	 */
	public void setScope(IScopable scoped, IScope s) {
		if (scoped instanceof IAssociation) {
			setScope((IAssociation) scoped, s);
		} else if (scoped instanceof IOccurrence) {
			setScope((IOccurrence) scoped, s);
		} else if (scoped instanceof IName) {
			setScope((IName) scoped, s);
		} else if (scoped instanceof IVariant) {
			setScope((IVariant) scoped, s);
		} else {
			throw new TopicMapStoreException("Type of scoped item is unknown '" + scoped.getClass() + "'.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IAssociation scoped_, IScope s) {
		internalSetScope(scoped_, s);
		IAssociation scoped = getTransactionStore().getIdentityStore().createLazyStub(scoped_);
		IScope scope = getTransactionStore().getIdentityStore().createLazyStub(s);
		if (associationScopes == null) {
			associationScopes = HashUtil.getHashMap();
		}
		associationScopes.put(scoped, scope);
		if (scopedAssociations == null) {
			scopedAssociations = HashUtil.getHashMap();
		}
		Set<IAssociation> set = scopedAssociations.get(scope);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(scoped);
		scopedAssociations.put(scope, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IName scoped_, IScope s) {
		internalSetScope(scoped_, s);
		IName scoped = getTransactionStore().getIdentityStore().createLazyStub(scoped_);
		IScope scope = getTransactionStore().getIdentityStore().createLazyStub(s);
		if (nameScopes == null) {
			nameScopes = HashUtil.getHashMap();
		}
		nameScopes.put(scoped, scope);
		if (scopedNames == null) {
			scopedNames = HashUtil.getHashMap();
		}
		Set<IName> set = scopedNames.get(scope);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(scoped);
		scopedNames.put(scope, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IOccurrence scoped_, IScope s) {
		internalSetScope(scoped_, s);
		IOccurrence scoped = getTransactionStore().getIdentityStore().createLazyStub(scoped_);
		IScope scope = getTransactionStore().getIdentityStore().createLazyStub(s);
		if (occurrenceScopes == null) {
			occurrenceScopes = HashUtil.getHashMap();
		}
		occurrenceScopes.put(scoped, scope);
		if (scopedOccurrences == null) {
			scopedOccurrences = HashUtil.getHashMap();
		}
		Set<IOccurrence> set = scopedOccurrences.get(scope);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(scoped);
		scopedOccurrences.put(scope, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IVariant scoped_, IScope s) {
		internalSetScope(scoped_, s);
		IVariant scoped = getTransactionStore().getIdentityStore().createLazyStub(scoped_);
		IScope scope = getTransactionStore().getIdentityStore().createLazyStub(s);
		if (variantScopes == null) {
			variantScopes = HashUtil.getHashMap();
		}
		variantScopes.put(scoped, scope);
		if (scopedVariants == null) {
			scopedVariants = HashUtil.getHashMap();
		}
		Set<IVariant> set = scopedVariants.get(scope);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(scoped);
		scopedVariants.put(scope, set);
	}

	/**
	 * Store the relation between the given scoped item and the scope.
	 * 
	 * @param scoped
	 *            the scoped construct
	 * @param s
	 *            the scope
	 */
	private final void internalSetScope(IScopable scoped, IScope s) {
		/*
		 * old scope is out of transaction context
		 */
		if (!containsScopeable(scoped)) {
			if (modifiedScopeables == null) {
				modifiedScopeables = HashUtil.getHashSet();
			}
			modifiedScopeables.add(scoped.getId());
		}
	}

	/**
	 * Remove the relation between the scoped construct and the stored scope.
	 * 
	 * @param scoped
	 *            the scoped construct
	 * @return the old scope
	 */
	public IScope removeScope(IScopable scoped) {
		if (scoped instanceof IAssociation) {
			return removeScope((IAssociation) scoped);
		} else if (scoped instanceof IOccurrence) {
			return removeScope((IOccurrence) scoped);
		} else if (scoped instanceof IName) {
			return removeScope((IName) scoped);
		} else if (scoped instanceof IVariant) {
			return removeScope((IVariant) scoped);
		} else {
			throw new TopicMapStoreException("Type of scoped item is unknown '" + scoped.getClass() + "'.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScope(IAssociation scoped) {
		IScope scope = getScope(scoped);
		if (associationScopes != null && associationScopes.containsKey(scoped)) {
			IScope s = associationScopes.remove(scoped);
			if (scopedAssociations != null && scopedAssociations.containsKey(s)) {
				Set<IAssociation> set = scopedAssociations.get(s);
				set.remove(scoped);
				if (set.isEmpty()) {
					scopedAssociations.remove(s);
				} else {
					scopedAssociations.put(s, set);
				}
			}
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScope(IName scoped) {
		IScope scope = getScope(scoped);
		if (nameScopes != null && nameScopes.containsKey(scoped)) {
			IScope s = nameScopes.remove(scoped);
			if (scopedNames != null && scopedNames.containsKey(s)) {
				Set<IName> set = scopedNames.get(s);
				set.remove(scoped);
				if (set.isEmpty()) {
					scopedNames.remove(s);
				} else {
					scopedNames.put(s, set);
				}
			}
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScope(IOccurrence scoped) {
		IScope scope = getScope(scoped);
		if (occurrenceScopes != null && occurrenceScopes.containsKey(scoped)) {
			IScope s = occurrenceScopes.remove(scoped);
			if (scopedOccurrences != null && scopedOccurrences.containsKey(s)) {
				Set<IOccurrence> set = scopedOccurrences.get(s);
				set.remove(scoped);
				if (set.isEmpty()) {
					scopedOccurrences.remove(s);
				} else {
					scopedOccurrences.put(s, set);
				}
			}
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScope(IVariant scoped) {
		IScope scope = getScope(scoped);
		if (variantScopes != null && variantScopes.containsKey(scoped)) {
			IScope s = variantScopes.remove(scoped);
			if (scopedVariants != null && scopedVariants.containsKey(s)) {
				Set<IVariant> set = scopedVariants.get(s);
				set.remove(scoped);
				if (set.isEmpty()) {
					scopedVariants.remove(s);
				} else {
					scopedVariants.put(s, set);
				}
			}
		}
		return scope;
	}

	/**
	 * @return the topicMapStore
	 */
	public ModifableTopicMapStoreImpl getTopicMapStore() {
		return topicMapStore.getRealStore();
	}

	/**
	 * @return the topicMapStore
	 */
	public TransactionTopicMapStore getTransactionStore() {
		return topicMapStore;
	}

	/**
	 * Redirect method call to identity store and check if construct is marked
	 * as removed.
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if the construct was marked as removed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isRemovedConstruct(IConstruct c) {
		return getTransactionStore().getIdentityStore().isRemovedConstruct(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		Set<IScope> scopes = getScopes(topic);
		for (IScope s : scopes) {
			if (s instanceof ScopeImpl && s.containsTheme(topic)) {
				Set<ITopic> themes = HashUtil.getHashSet();
				themes.addAll(s.getThemes());
				themes.remove(topic);
				themes.add(replacement);
				IScope newScope = getScope(themes);
				/*
				 * replace as association scope
				 */
				Set<IAssociation> associations = HashUtil.getHashSet(getScopedAssociations(s));
				for (IAssociation a : associations) {
					removeScope(a);
					setScope(a, newScope);
				}
				/*
				 * replace as name scope
				 */
				Set<IName> names = HashUtil.getHashSet(getScopedNames(s));
				for (IName n : names) {
					removeScope(n);
					setScope(n, newScope);
				}
				/*
				 * replace as occurrence scope
				 */
				Set<IOccurrence> occurrences = HashUtil.getHashSet(getScopedOccurrences(s));
				for (IOccurrence o : occurrences) {
					removeScope(o);
					setScope(o, newScope);
				}
				/*
				 * replace as variant scope
				 */
				Set<IVariant> variants = HashUtil.getHashSet(getScopedVariants(s));
				for (IVariant v : variants) {
					removeScope(v);
					setScope(v, newScope);
				}
			}
		}
	}

	/**
	 * Method checks if the given object is stored by the current data store.
	 * 
	 * @param scopable
	 *            the object to check
	 * @return <code>true</code> if the data store contains a scope relation for
	 *         the given object, <code>false</code> otherwise.
	 */
	protected final boolean containsScopeable(IScopable scopable) {
		if (scopable instanceof IAssociation) {
			return associationScopes != null && associationScopes.containsKey(scopable);
		} else if (scopable instanceof IName) {
			return nameScopes != null && nameScopes.containsKey(scopable);
		} else if (scopable instanceof IOccurrence) {
			return occurrenceScopes != null && occurrenceScopes.containsKey(scopable);
		}
		return variantScopes != null && variantScopes.containsKey(scopable);
	}

	/**
	 * Method checks if the given topic is used as theme.
	 * 
	 * @param theme
	 *            the theme
	 * @return <code>true</code> if the topic is used as theme,
	 *         <code>false</code> otherwise.
	 */
	public boolean usedAsTheme(ITopic theme) {
		for (IScope scope : getScopes(theme)) {
			if (!getScoped(scope).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the empty scope object.
	 * 
	 * @return the empty scope object
	 */
	public IScope getEmptyScope() {
		return emptyScope;
	}

	/**
	 * Removing the theme and all dependent scopes and scoped objects.
	 * 
	 * @param theme
	 *            the theme
	 * @return the removed scoped objects
	 */
	public Set<IScopable> removeScopes(ITopic theme) {
		Set<IScopable> removed = HashUtil.getHashSet();
		for (IScope scope : getScopes(theme)) {
			/*
			 * remove scoped associations
			 */
			for (IAssociation a : getScopedAssociations(scope)) {
				if (containsScopeable(a)) {
					removeScope(a);
				}
				removed.add(a);
			}
			if (scopedAssociations != null) {
				scopedAssociations.remove(scope);
			}
			/*
			 * remove scoped occurrences
			 */
			for (IOccurrence o : getScopedOccurrences(scope)) {
				if (containsScopeable(o)) {
					removeScope(o);
				}
				removed.add(o);
			}
			if (scopedOccurrences != null) {
				scopedOccurrences.remove(scope);
			}
			/*
			 * remove scoped names
			 */
			for (IName n : getScopedNames(scope)) {
				if (containsScopeable(n)) {
					removeScope(n);
				}
				removed.add(n);
			}
			if (scopedNames != null) {
				scopedNames.remove(scope);
			}
			/*
			 * remove scoped variants
			 */
			for (IVariant v : getScopedVariants(scope)) {
				if (containsScopeable(v)) {
					removeScope(v);
				}
				removed.add(v);
			}
			if (scopedVariants != null) {
				scopedVariants.remove(scope);
			}

			/*
			 * remove scope
			 */
			if (scopes != null) {
				scopes.remove(scope);
			}
		}
		return removed;
	}

}
