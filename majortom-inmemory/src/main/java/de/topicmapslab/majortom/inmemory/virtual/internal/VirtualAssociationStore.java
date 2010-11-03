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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.inmemory.store.internal.AssociationStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualAssociationStore<T extends VirtualTopicMapStore> extends AssociationStore {

	private final T store;

	private Map<IAssociationRole, ITopic> changedPlayers;

	/**
	 * constructor
	 */
	public VirtualAssociationStore(T store) {
		this.store = store;
	}

	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	public T getStore() {
		return store;
	}

	/**
	 * Internal method to access the virtual-identity store
	 * 
	 * @return the virtual identity store
	 */
	@SuppressWarnings("unchecked")
	protected VirtualIdentityStore<T> getVirtualIdentityStore() {
		return (VirtualIdentityStore<T>) getStore().getIdentityStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getPlayer(Role r) {
		if (getVirtualIdentityStore().isRemovedConstruct((IAssociationRole) r)) {
			throw new ConstructRemovedException(r);
		}
		ITopic player = null;
		try {
			player = super.getPlayer(r);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		if (player == null) {
			player = getVirtualIdentityStore().asVirtualConstruct(
					(ITopic) getStore().getRealStore().doRead((IAssociationRole) r, TopicMapStoreParameterType.PLAYER));
		}
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRole(IAssociation association, IAssociationRole role, ITopic player) {
		if (getVirtualIdentityStore().isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		} else if (getVirtualIdentityStore().isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		if (!super.getAssociations().contains(association)) {
			addAssociation(getVirtualIdentityStore().asVirtualConstruct(association));
		}
		super.addRole(association, role, player);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociation> getAssociations() {
		Set<IAssociation> associations = HashUtil.getHashSet(super.getAssociations());
		ITopicMapStore realStore = getStore().getRealStore();
		for (Association association : (Collection<Association>) realStore.doRead(realStore.getTopicMap(),
				TopicMapStoreParameterType.ASSOCIATION)) {
			if (!getVirtualIdentityStore().isRemovedConstruct((IAssociation) association)) {
				associations.add(getVirtualIdentityStore().asVirtualConstruct((IAssociation) association));
			}
		}
		if (associations.isEmpty()) {
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(Association association) {
		if (getVirtualIdentityStore().isRemovedConstruct((IAssociation) association)) {
			throw new ConstructRemovedException(association);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(association)) {
			for (IAssociationRole role : (Set<IAssociationRole>) getStore().getRealStore().doRead(
					(IAssociation) association, TopicMapStoreParameterType.ROLE)) {
				if (!getVirtualIdentityStore().isRemovedConstruct(role)) {
					roles.add(getVirtualIdentityStore().asVirtualConstruct(role));
				}
			}
		}
		roles.addAll(super.getRoles(association));
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(ITopic player) {
		if (getVirtualIdentityStore().isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(player)) {
			for (IAssociationRole role : (Set<IAssociationRole>) getStore().getRealStore().doRead(player,
					TopicMapStoreParameterType.ROLE)) {
				/*
				 * old player relation
				 */
				if (changedPlayers != null && player.equals(changedPlayers.get(role))) {
					continue;
				}
				if (!getVirtualIdentityStore().isRemovedConstruct(role)) {
					roles.add(getVirtualIdentityStore().asVirtualConstruct(role));
				}
			}
		}
		roles.addAll(super.getRoles(player));
		if (roles.isEmpty()) {
			return Collections.emptySet();
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic setPlayer(IAssociationRole r, ITopic player) {
		if (getVirtualIdentityStore().isRemovedConstruct(r)) {
			throw new ConstructRemovedException(r);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		ITopic oldPlayer = getPlayer(r);
		if (changedPlayers == null) {
			changedPlayers = HashUtil.getHashMap();
		}
		if (!changedPlayers.containsKey(r)) {
			changedPlayers.put(r, oldPlayer);
		}
		super.setPlayer(r, player);
		return oldPlayer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRole(IAssociationRole role) {
		if (getVirtualIdentityStore().isRemovedConstruct(role)) {
			throw new ConstructRemovedException(role);
		}
		super.removeRole(role);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAssociation(IAssociation association) {
		if (getVirtualIdentityStore().isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		}
		super.removeAssociation(association);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		if (getVirtualIdentityStore().isRemovedConstruct(topic)) {
			throw new ConstructRemovedException(topic);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(replacement)) {
			throw new ConstructRemovedException(replacement);
		}
		ITopic replace = getVirtualIdentityStore().asVirtualConstruct(replacement);

		for (IAssociationRole role : getRoles(topic)) {
			if (getVirtualIdentityStore().isRemovedConstruct(role)) {
				setPlayer(role, replace);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (changedPlayers != null) {
			changedPlayers.clear();
		}
		super.close();
	}
}
