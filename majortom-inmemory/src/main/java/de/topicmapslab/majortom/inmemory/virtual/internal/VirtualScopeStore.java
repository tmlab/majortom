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
package de.topicmapslab.majortom.inmemory.virtual.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.inmemory.store.internal.ScopeStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualScopeStore<T extends VirtualTopicMapStore> extends ScopeStore implements IVirtualStore {

	private Set<String> modifiedScopeables;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 */
	public VirtualScopeStore(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	protected IScope createEmptyScope() {
		IScope scope = new ScopeImpl();
		getVirtualIdentityStore().setVirtual(scope);
		return scope;
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
	 * Internal method to access the virtual-identity
	 * 
	 * @return the virtual identity store
	 */
	@SuppressWarnings("unchecked")
	protected VirtualIdentityStore<T> getVirtualIdentityStore() {
		return ((VirtualIdentityStore<T>) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	@SuppressWarnings("unchecked")
	protected T getStore() {
		return (T) super.getStore();
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
				if (getVirtualIdentityStore().isRemovedConstruct((IAssociation) association)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(association.getId())) {
					set.add(getVirtualIdentityStore().asVirtualScope(scope));
					break;
				}
			}
		}
		set.addAll(super.getAssociationScopes());
		if (set.isEmpty()) {
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
				if (getVirtualIdentityStore().isRemovedConstruct((IName) name)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(name.getId())) {
					set.add(getVirtualIdentityStore().asVirtualScope(scope));
					break;
				}
			}
		}
		set.addAll(super.getNameScopes());
		if (set.isEmpty()) {
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
				if (getVirtualIdentityStore().isRemovedConstruct((IOccurrence) occurrence)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(occurrence.getId())) {
					set.add(getVirtualIdentityStore().asVirtualScope(scope));
					break;
				}
			}
		}
		set.addAll(super.getOccurrenceScopes());
		if (set.isEmpty()) {
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
				if (getVirtualIdentityStore().isRemovedConstruct((IVariant) variant)) {
					continue;
				}
				if (modifiedScopeables == null || !modifiedScopeables.contains(variant.getId())) {
					set.add(getVirtualIdentityStore().asVirtualScope(scope));
					break;
				}
			}
		}
		set.addAll(super.getVariantScopes());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<ITopic> themes) {
		Set<ITopic> themes_ = HashUtil.getHashSet();
		boolean virtual = false;
		for (ITopic theme : themes) {
			if (getVirtualIdentityStore().isRemovedConstruct(theme)) {
				throw new ConstructRemovedException(theme);
			}
			if (getVirtualIdentityStore().isVirtual(theme)) {
				virtual = true;
			}
			themes_.add(getVirtualIdentityStore().asVirtualConstruct(theme));
		}

		IScope scope = super.getScope(themes_);
		if (virtual) {
			getVirtualIdentityStore().setVirtual(scope);
		}
		return scope;
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
		if (getVirtualIdentityStore().isRemovedConstruct(scoped)) {
			throw new ConstructRemovedException(scoped);
		}
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		if (!getVirtualIdentityStore().isVirtual(scoped)) {
			return getVirtualIdentityStore().asVirtualScope(
					(IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		}
		return getEmptyScope();
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IName scoped) {
		if (getVirtualIdentityStore().isRemovedConstruct(scoped)) {
			throw new ConstructRemovedException(scoped);
		}
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		if (!getVirtualIdentityStore().isVirtual(scoped)) {
			return getVirtualIdentityStore().asVirtualScope(
					(IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		}
		return getEmptyScope();
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IOccurrence scoped) {
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		if (!getVirtualIdentityStore().isVirtual(scoped)) {
			return getVirtualIdentityStore().asVirtualScope(
					(IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		}
		return getEmptyScope();
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(IVariant scoped) {
		if (containsScopeable(scoped)) {
			return super.getScope(scoped);
		}
		if (!getVirtualIdentityStore().isVirtual(scoped)) {
			return getVirtualIdentityStore().asVirtualScope(
					(IScope) getStore().getRealStore().doRead(scoped, TopicMapStoreParameterType.SCOPE));
		}
		return getEmptyScope();
	}

	/**
	 * Returns all scoped items of the given scope and given type.
	 * 
	 * @param scope
	 *            the scope
	 * @return the scoped construct
	 */
	@SuppressWarnings("unchecked")
	private <X extends IScopable> Set<X> internalGetScoped(IScope scope, Class<? extends X> clazz) {
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		IScope cleaned = scope;
		try {
			cleaned = (IScope) getStore().getRealStore().doCreate(getStore().getTopicMap(),
					TopicMapStoreParameterType.SCOPE, scope.getThemes());
		} catch (Exception e) {
			// VOID
		}

		Set<Scoped> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(cleaned)) {
			if (Name.class.isAssignableFrom(clazz)) {
				set.addAll(index.getNames(cleaned));
			} else if (Occurrence.class.isAssignableFrom(clazz)) {
				set.addAll(index.getOccurrences(cleaned));
			} else if (Association.class.isAssignableFrom(clazz)) {
				set.addAll(index.getAssociations(cleaned));
			} else if (Variant.class.isAssignableFrom(clazz)) {
				set.addAll(index.getVariants(cleaned));
			}
		}

		Set<X> scopables = HashUtil.getHashSet();
		for (Scoped scoped : set) {
			X scopable = (X) scoped;
			if (getVirtualIdentityStore().isRemovedConstruct(scopable)) {
				continue;
			}
			if (modifiedScopeables == null || !modifiedScopeables.contains(scopable.getId())) {
				scopables.add(getVirtualIdentityStore().asVirtualConstruct(scopable));
			}
		}
		if (Name.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<X>) super.getScopedNames(scope));
		} else if (Occurrence.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<X>) super.getScopedOccurrences(scope));
		} else if (Association.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<X>) super.getScopedAssociations(scope));
		} else if (Variant.class.isAssignableFrom(clazz)) {
			scopables.addAll((Set<X>) super.getScopedVariants(scope));
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
		if (getVirtualIdentityStore().isRemovedConstruct(theme)) {
			throw new ConstructRemovedException(theme);
		}
		IScopedIndex index = getStore().getRealStore().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IScope> scopes = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(theme)) {
			for (IScope scope : index.getScopes(theme)) {
				if (getVirtualIdentityStore().isRemovedScope(scope)) {
					continue;
				}
				if (getScoped(scope).isEmpty()) {
					continue;
				}
				scopes.add(getVirtualIdentityStore().asVirtualScope(scope));
			}
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
		super.setScope(getVirtualIdentityStore().asVirtualConstruct(scoped), getVirtualIdentityStore()
				.asVirtualScope(s));
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
		super.setScope(getVirtualIdentityStore().asVirtualConstruct(scoped), getVirtualIdentityStore()
				.asVirtualScope(s));
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
		super.setScope(getVirtualIdentityStore().asVirtualConstruct(scoped), getVirtualIdentityStore()
				.asVirtualScope(s));
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
		super.setScope(getVirtualIdentityStore().asVirtualConstruct(scoped), getVirtualIdentityStore()
				.asVirtualScope(s));
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

	/**
	 * {@inheritDoc}
	 */
	public void removeVirtualConstruct(IConstruct construct) {
		if (construct instanceof IScopable) {
			removeScoped((IScopable) construct);
			if (modifiedScopeables != null) {
				modifiedScopeables.remove(construct.getId());
			}
		}
	}

}
