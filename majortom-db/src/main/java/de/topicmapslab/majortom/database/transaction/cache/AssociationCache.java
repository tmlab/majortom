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

import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class AssociationCache implements IDataStore {

	/**
	 * internal storage map of association-roles relations
	 */
	private Map<IAssociation, Set<IAssociationRole>> associations;
	/**
	 * internal storage map of role-player relations
	 */
	private Map<IAssociationRole, ITopic> rolePlayers;
	/**
	 * internal storage map of player-roles relations
	 */
	private Map<ITopic, Set<IAssociationRole>> playedRoles;

	/**
	 * reference to the underlying topic map store
	 */
	private final TransactionTopicMapStore topicMapStore;

	private Map<IAssociationRole, ITopic> changedPlayers;

	/**
	 * 
	 */
	public AssociationCache(TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getPlayer(Role r) {
		if (isRemovedConstruct((IAssociationRole) r)) {
			throw new ConstructRemovedException(r);
		}
		/*
		 * read from internal cache
		 */
		ITopic player = null;
		if (rolePlayers != null && rolePlayers.containsKey(r)) {
			player = rolePlayers.get(r);
		}
		/*
		 * not cached yet
		 */
		if (player == null) {
			player = getTransactionStore().getIdentityStore().createLazyStub(
					(ITopic) getTopicMapStore().doRead((IAssociationRole) r, TopicMapStoreParameterType.PLAYER));
		}
		return player;
	}

	/**
	 * Register a new association item.
	 * 
	 * @param association the association item
	 */
	public void addAssociation(IAssociation association) {
		if (associations == null) {
			associations = HashUtil.getHashMap();
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
		associations.put(association, set);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addRole(IAssociation association, IAssociationRole role, ITopic player) {
		if (isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		} else if (isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		IAssociation a = getTransactionStore().getIdentityStore().createLazyStub(association);
		IAssociationRole r = getTransactionStore().getIdentityStore().createLazyStub(role);
		ITopic p = getTransactionStore().getIdentityStore().createLazyStub(player);
		if (!getAssociations().contains(association)) {
			addAssociation(a);
		}
		internalAddRole(a, r, p);
	}
	
	/**
	 * Internal method to register a new association role item at the internal store.
	 * 
	 * @param association the parent association
	 * @param role the association role item
	 * @param player the role player
	 */
	private void internalAddRole(IAssociation association, IAssociationRole role, ITopic player) {		
		/*
		 * check if association is known by the data store
		 */
		if (associations == null ) {
			associations = HashUtil.getHashMap();
		}
		/*
		 * add role to association store
		 */
		Set<IAssociationRole> set = associations.get(association);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(role);
		associations.put(association, set);

		/*
		 * store player of new role
		 */
		if (rolePlayers == null) {
			rolePlayers = HashUtil.getHashMap();
		}
		rolePlayers.put(role, player);

		/*
		 * store backward reference of played role
		 */
		if (playedRoles == null) {
			playedRoles = HashUtil.getHashMap();
		}
		set = playedRoles.get(player);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(role);
		playedRoles.put(player, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IAssociation> getAssociations() {
		Set<IAssociation> associations = HashUtil.getHashSet();
		if (this.associations != null) {
			associations.addAll(this.associations.keySet());
		}
		for (Association association : getTopicMapStore().getTopicMap().getAssociations()) {
			if (!isRemovedConstruct((IAssociation) association)) {
				associations.add(getTransactionStore().getIdentityStore().createLazyStub((IAssociation) association));
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(Association association) {
		if (isRemovedConstruct((IAssociation) association)) {
			throw new ConstructRemovedException(association);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		try {
			for (IAssociationRole role : (Set<IAssociationRole>) getTopicMapStore().doRead((IAssociation) association, TopicMapStoreParameterType.ROLE)) {
				if (!isRemovedConstruct(role)) {
					roles.add(getTransactionStore().getIdentityStore().createLazyStub(role));
				}
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		if (associations != null && associations.containsKey(association)) {
			roles.addAll(associations.get(association));
		}		
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IAssociationRole> getRoles(ITopic player) {
		if (isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		Set<IAssociationRole> roles = HashUtil.getHashSet();
		try {
			for (IAssociationRole role : (Set<IAssociationRole>) getTopicMapStore().doRead(player, TopicMapStoreParameterType.ROLE)) {
				/*
				 * old player relation
				 */
				if (changedPlayers != null && player.equals(changedPlayers.get(role))) {
					continue;
				}
				if (!isRemovedConstruct(role)) {
					roles.add(getTransactionStore().getIdentityStore().createLazyStub(role));
				}
			}
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
		if (playedRoles != null && playedRoles.containsKey(player)) {
			roles.addAll(playedRoles.get(player));
		}		
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic setPlayer(IAssociationRole r, ITopic player) {
		if (isRemovedConstruct(r)) {
			throw new ConstructRemovedException(r);
		}
		if (isRemovedConstruct(player)) {
			throw new ConstructRemovedException(player);
		}
		ITopic oldPlayer = getPlayer(r);
		if (changedPlayers == null) {
			changedPlayers = HashUtil.getHashMap();
		}
		if (!changedPlayers.containsKey(r)) {
			changedPlayers.put(r, oldPlayer);
		}
		internalSetPlayer(getTransactionStore().getIdentityStore().createLazyStub(r), getTransactionStore().getIdentityStore().createLazyStub(player));
		return oldPlayer;
	}
	
	/**
	 * Internal method to modify the player of a specific role.
	 * 
	 * @param r the role
	 * @param player the new player
	 * @return the old player
	 */
	private ITopic internalSetPlayer(IAssociationRole r, ITopic player) {
		if (rolePlayers == null) {
			rolePlayers = HashUtil.getHashMap();
		}
		ITopic p = rolePlayers.get(r);
		rolePlayers.put(r, player);

		/*
		 * store backward reference of played role
		 */
		if (playedRoles == null) {
			playedRoles = HashUtil.getHashMap();
		}
		/*
		 * remove old backward reference to player
		 */
		else if (p != null) {
			Set<IAssociationRole> set = playedRoles.get(p);
			set.remove(r);
			if (set.isEmpty()) {
				playedRoles.remove(p);
			} else {
				playedRoles.put(p, set);
			}
		}
		Set<IAssociationRole> set = playedRoles.get(player);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(r);
		playedRoles.put(player, set);
		return p;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRole(IAssociationRole role) {
		if (isRemovedConstruct(role)) {
			throw new ConstructRemovedException(role);
		}
		/*
		 * check if role is known by the store
		 */
		if (rolePlayers == null || !rolePlayers.containsKey(role)) {			
			return;
		}

		/*
		 * get player
		 */
		ITopic player = rolePlayers.get(role);
		/*
		 * remove role from played roles
		 */
		Set<IAssociationRole> set = playedRoles.get(player);
		if (set == null) {
			throw new TopicMapStoreException("Unknown association role item.");
		}
		set.remove(role);
		playedRoles.put(player, set);
		/*
		 * remove role
		 */
		rolePlayers.remove(role);		
		/*
		 * remove role from parent association
		 */
		set = associations.get(role.getParent());
		set.remove(role);
		associations.put(role.getParent(), set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeAssociation(IAssociation association) {
		if (isRemovedConstruct(association)) {
			throw new ConstructRemovedException(association);
		}
		/*
		 * check if role is known by the store
		 */
		if (associations == null || !associations.containsKey(association)) {
			return;
		}

		Set<IAssociationRole> roles = HashUtil.getHashSet();
		roles.addAll(associations.get(association));

		/*
		 * remove all roles
		 */
		for (IAssociationRole role : roles) {
			removeRole(role);
		}
		/*
		 * remove association
		 */
		associations.remove(association);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		if (isRemovedConstruct(topic)) {
			throw new ConstructRemovedException(topic);
		}
		if (isRemovedConstruct(replacement)) {
			throw new ConstructRemovedException(replacement);
		}
		ITopic replace = getTransactionStore().getIdentityStore().createLazyStub(replacement);

		try {
			for (IAssociationRole role : getRoles(topic)) {
				if (isRemovedConstruct(role)) {
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
		if (changedPlayers != null) {
			changedPlayers.clear();
		}
		if (associations != null) {
			associations.clear();
		}
		if (rolePlayers != null) {
			rolePlayers.clear();
		}
		if (playedRoles != null) {
			playedRoles.clear();
		}
	}

	/**
	 * @return the topicMapStore
	 */
	public TopicMapStoreImpl getTopicMapStore() {
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

}