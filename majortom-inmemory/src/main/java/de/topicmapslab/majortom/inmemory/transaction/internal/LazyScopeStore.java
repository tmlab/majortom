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
package de.topicmapslab.majortom.inmemory.transaction.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.inmemory.store.internal.ScopeStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyScopeStore extends ScopeStore {

	private Set<String> modifiedScopeables;
	private final ITopicMapStore store;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public LazyScopeStore(ITopicMapStore store) {
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		super.close();
		if (modifiedScopeables != null) {
			modifiedScopeables.clear();
		}
	}

	/**
	 * Internal method to access the lazy-identity store of this transaction
	 * context
	 * 
	 * @return the lazy identity store
	 */
	protected LazyIdentityStore getLazyIdentityStore() {
		return ((LazyIdentityStore) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected InMemoryTransactionTopicMapStore getStore() {
		return (InMemoryTransactionTopicMapStore) store;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getAssociationScopes() {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		for (IScope scope : index.getAssociationScopes()) {
			for (Association association : index.getAssociations(scope)) {
				if (getLazyIdentityStore().isRemovedConstruct((IAssociation) association)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(association.getId())) {
					set.add(getLazyIdentityStore().createLazyStub(scope));
					break;
				}
			}
		}
		set.addAll(super.getAssociationScopes());
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getNameScopes() {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		for (IScope scope : index.getNameScopes()) {
			for (Name name : index.getNames(scope)) {
				if (getLazyIdentityStore().isRemovedConstruct((IName) name)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(name.getId())) {
					set.add(getLazyIdentityStore().createLazyStub(scope));
					break;
				}
			}
		}
		set.addAll(super.getNameScopes());
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getOccurrenceScopes() {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		for (IScope scope : index.getOccurrenceScopes()) {
			for (Occurrence occurrence : index.getOccurrences(scope)) {
				if (getLazyIdentityStore().isRemovedConstruct((IOccurrence) occurrence)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(occurrence.getId())) {
					set.add(getLazyIdentityStore().createLazyStub(scope));
					break;
				}
			}
		}
		set.addAll(super.getOccurrenceScopes());
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IScope> getVariantScopes() {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> set = HashUtil.getHashSet();
		for (IScope scope : index.getVariantScopes()) {
			for (Variant variant : index.getVariants(scope)) {
				if (getLazyIdentityStore().isRemovedConstruct((IVariant) variant)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(variant.getId())) {
					set.add(getLazyIdentityStore().createLazyStub(scope));
					break;
				}
			}
		}
		set.addAll(super.getVariantScopes());
		if ( set.isEmpty()){
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<ITopic> themes) {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<ITopic> themes_ = HashUtil.getHashSet();
		for (ITopic theme : themes) {
			if (getLazyIdentityStore().isRemovedConstruct(theme)) {
				throw new ConstructRemovedException(theme);
			}
			themes_.add(getLazyIdentityStore().createLazyStub(theme));
		}

		return super.getScope(themes_);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(ITopic... themes) {
		return getScope(Arrays.asList(themes));
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IAssociation scoped) {
		if (getLazyIdentityStore().isRemovedConstruct(scoped)) {
			throw new ConstructRemovedException(scoped);
		}
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		return getLazyIdentityStore().createLazyStub((IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IName scoped) {
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		return getLazyIdentityStore().createLazyStub((IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IOccurrence scoped) {
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		return getLazyIdentityStore().createLazyStub((IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IVariant scoped) {
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		return getLazyIdentityStore().createLazyStub((IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
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
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
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
			if (getLazyIdentityStore().isRemovedConstruct(scopable)) {
				continue;
			}
			if (modifiedScopeables == null || !modifiedScopeables.contains(scopable.getId())) {
				scopables.add(getLazyIdentityStore().createLazyStub(scopable));
			}
		}
		if (Name.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<T>) super.getScopedNames(scope));
		} else if (Occurrence.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<T>) super.getScopedOccurrences(scope));
		} else if (Association.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<T>) super.getScopedAssociations(scope));
		} else if (Variant.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<T>) super.getScopedVariants(scope));
		}
		return scopables;
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
		if (getLazyIdentityStore().isRemovedConstruct(theme)) {
			throw new ConstructRemovedException(theme);
		}
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		
		Set<IScope> scopes = HashUtil.getHashSet();
		for (IScope scope : index.getScopes(theme)) {
			if (getLazyIdentityStore().isRemovedScope(scope)) {
				continue;
			}
			if (getScoped(scope).isEmpty()) {
				continue;
			}
			scopes.add(getLazyIdentityStore().createLazyStub(scope));
		}
		scopes.addAll(super.getScopes(theme));
		return scopes;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IAssociation scoped, IScope s) {
		/*
		 * old scope is out of transaction context
		 */
		if (!containsScopeable(scoped)) {
			if (modifiedScopeables == null) {
				modifiedScopeables = HashUtil.getHashSet();
			}
			modifiedScopeables.add(scoped.getId());
		}
		super.setScope(getLazyIdentityStore().createLazyStub(scoped), getLazyIdentityStore().createLazyStub(s));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IName scoped, IScope s) {
		/*
		 * old scope is out of transaction context
		 */
		if (!containsScopeable(scoped)) {
			if (modifiedScopeables == null) {
				modifiedScopeables = HashUtil.getHashSet();
			}
			modifiedScopeables.add(scoped.getId());
		}
		super.setScope(getLazyIdentityStore().createLazyStub(scoped), getLazyIdentityStore().createLazyStub(s));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IOccurrence scoped, IScope s) {
		/*
		 * old scope is out of transaction context
		 */
		if (!containsScopeable(scoped)) {
			if (modifiedScopeables == null) {
				modifiedScopeables = HashUtil.getHashSet();
			}
			modifiedScopeables.add(scoped.getId());
		}
		super.setScope(getLazyIdentityStore().createLazyStub(scoped), getLazyIdentityStore().createLazyStub(s));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setScope(IVariant scoped, IScope s) {
		/*
		 * old scope is out of transaction context
		 */
		if (!containsScopeable(scoped)) {
			if (modifiedScopeables == null) {
				modifiedScopeables = HashUtil.getHashSet();
			}
			modifiedScopeables.add(scoped.getId());
		}
		super.setScope(getLazyIdentityStore().createLazyStub(scoped), getLazyIdentityStore().createLazyStub(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IScope removeScoped(IAssociation scoped) {
		IScope scope = getScope(scoped);
		super.removeScoped(scoped);
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScoped(IName scoped) {
		IScope scope = getScope(scoped);
		super.removeScoped(scoped);
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScoped(IOccurrence scoped) {
		IScope scope = getScope(scoped);
		super.removeScoped(scoped);
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope removeScoped(IVariant scoped) {
		IScope scope = getScope(scoped);
		super.removeScoped(scoped);
		return scope;
	}

}
