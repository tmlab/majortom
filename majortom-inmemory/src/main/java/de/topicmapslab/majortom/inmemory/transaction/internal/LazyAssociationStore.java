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

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.inmemory.store.internal.AssociationStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyAssociationStore extends AssociationStore {

	private final InMemoryTransactionTopicMapStore store;

	private Map<IAssociationRole, ITopic> changedPlayers;

	/**
	 * 
	 */
	public LazyAssociationStore(InMemoryTransactionTopicMapStore store) {
		this.store = store;
	}

	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	public InMemoryTransactionTopicMapStore getStore() {
		return store;
	}

	/**
	 * Internal method to access the lazy-identity store of this transaction
	 * context
	 * 
	 * @return the lazy identity store
	 */
	protected LazyIdentityStore getLazyIdentityStore() {
		return (LazyIdentityStore) getStore().getIdentityStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getPlayer(Role r) {
		if (getLazyIdentityStore().isRemovedConstruct((IAssociationRole) r)) {
			throw new ConstructRemovedException(r);
		}
		ITopic player = null;
		try {
			player = super.getPlayer(r);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		if (player == null) {
			player = getLazyIdentityStore().createLazyStub((ITopic) getStore().getRealStore().doRead((IAssociationRole) r, TopicMapStoreParameterType.PLAYER));
		}
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addRole(IAssociation association, IAssociationRole role, ITopic player) {
		if (getLazyIdentityStore().isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		} else if (getLazyIdentityStore().isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		if (!super.getAssociations().contains(association)) {
			addAssociation(getLazyIdentityStore().createLazyStub(association));
		}
		super.addRole(association, role, player);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> getAssociations() {
		Set<IAssociation> associations = HashUtil.getHashSet(super.getAssociations());
		for (Association association : getStore().getRealStore().getTopicMap().getAssociations()) {
			if (!getLazyIdentityStore().isRemovedConstruct((IAssociation) association)) {
				associations.add(getLazyIdentityStore().createLazyStub((IAssociation) association));
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(Association association) {
		if (getLazyIdentityStore().isRemovedConstruct((IAssociation) association)) {
			throw new ConstructRemovedException(association);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		try {
			for (IAssociationRole role : (Set<IAssociationRole>) getStore().getRealStore().doRead((IAssociation) association, TopicMapStoreParameterType.ROLE)) {
				if (!getLazyIdentityStore().isRemovedConstruct(role)) {
					roles.add(getLazyIdentityStore().createLazyStub(role));
				}
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		roles.addAll(super.getRoles(association));
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(ITopic player) {
		if (getLazyIdentityStore().isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		try {
			for (IAssociationRole role : (Set<IAssociationRole>) getStore().getRealStore().doRead(player, TopicMapStoreParameterType.ROLE)) {
				/*
				 * old player relation
				 */
				if (changedPlayers != null && player.equals(changedPlayers.get(role))) {
					continue;
				}
				if (!getLazyIdentityStore().isRemovedConstruct(role)) {
					roles.add(getLazyIdentityStore().createLazyStub(role));
				}
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		roles.addAll(super.getRoles(player));
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic setPlayer(IAssociationRole r, ITopic player) {
		if (getLazyIdentityStore().isRemovedConstruct(r)) {
			throw new ConstructRemovedException(r);
		}
		if (getLazyIdentityStore().isRemovedConstruct(player)) {
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
		if (getLazyIdentityStore().isRemovedConstruct(role)) {
			throw new ConstructRemovedException(role);
		}
		try {
			super.removeRole(role);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO HERE
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAssociation(IAssociation association) {
		if (getLazyIdentityStore().isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		}
		try {
			super.removeAssociation(association);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO HERE
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		if (getLazyIdentityStore().isRemovedConstruct(topic)) {
			throw new ConstructRemovedException(topic);
		}
		if (getLazyIdentityStore().isRemovedConstruct(replacement)) {
			throw new ConstructRemovedException(replacement);
		}
		ITopic replace = getLazyIdentityStore().createLazyStub(replacement);

		try {
			for (IAssociationRole role : getRoles(topic)) {
				if (getLazyIdentityStore().isRemovedConstruct(role)) {
					setPlayer(role, replace);
				}
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (changedPlayers != null ) {
			changedPlayers.clear();
		}
		super.close();
	}
	
}
