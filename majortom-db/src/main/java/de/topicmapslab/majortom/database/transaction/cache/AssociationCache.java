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
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of association and role informations
 * 
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

	private Set<IConstruct> removedConstructs;

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (associations != null) {
			associations.clear();
		}
		if (rolePlayers != null) {
			rolePlayers.clear();
		}
		if (playedRoles != null) {
			playedRoles.clear();
		}
		if (removedConstructs != null) {
			removedConstructs.clear();
		}
	}

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the underlying topic map store
	 */
	public AssociationCache(TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * Return all roles of the given association
	 * 
	 * @param association
	 *            the association
	 * @return the roles
	 */
	public Set<IAssociationRole> getRoles(Association association) {
		if (isRemovedConstruct((IAssociation) association)) {
			throw new TopicMapStoreException("Construct was removed!");
		}
		Set<IAssociationRole> set = null;
		if (associations == null || !associations.containsKey(association)) {
			set = redirectGetRoles(association);
		} else {
			set = associations.get(association);
		}
		Set<IAssociationRole> result = HashUtil.getHashSet();
		for (IAssociationRole r : set) {
			if (!isRemovedConstruct(r)) {
				result.add(r);
			}
		}
		return result;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the result
	 * 
	 * @param association
	 *            the association
	 * @return the roles
	 */
	@SuppressWarnings("unchecked")
	protected Set<IAssociationRole> redirectGetRoles(Association association) {
		Set<IAssociationRole> roles = (Set<IAssociationRole>) getTopicMapStore().doRead((IAssociation) association, TopicMapStoreParameterType.ROLE);
		if (associations == null) {
			associations = HashUtil.getHashMap();
		}
		this.associations.put((IAssociation) association, roles);
		for (IAssociationRole role : roles) {
			redirectGetPlayer(role);
		}
		return roles;
	}

	/**
	 * Return the played roles of the given role player.
	 * 
	 * @param player
	 *            the player
	 * @return the roles
	 */
	public Set<IAssociationRole> getRoles(ITopic player) {
		Set<IAssociationRole> roles = null;
		if (playedRoles == null || !playedRoles.containsKey(player)) {
			roles = redirectGetRoles(player);
		} else {
			roles = playedRoles.get(player);
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (IAssociationRole r : roles) {
			if (!isRemovedConstruct(r)) {
				set.add(r);
			}
		}
		return set;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the results for later call.
	 * 
	 * @param player
	 *            the playing topic
	 * @return the roles played
	 */
	@SuppressWarnings("unchecked")
	protected Set<IAssociationRole> redirectGetRoles(ITopic player) {
		Set<IAssociationRole> roles = (Set<IAssociationRole>) getTopicMapStore().doRead(player, TopicMapStoreParameterType.ROLE);
		if (playedRoles == null) {
			playedRoles = HashUtil.getHashMap();
		}
		playedRoles.put(player, roles);
		return roles;
	}

	/**
	 * Return the played associations of the given role player.
	 * 
	 * @param player
	 *            the player
	 * @return the associations
	 */
	public Set<IAssociation> getAssocaitionsPlayed(ITopic player) {
		Set<IAssociation> associations = HashUtil.getHashSet();
		for (IAssociationRole role : getRoles(player)) {
			associations.add(role.getParent());
		}
		return associations;
	}

	/**
	 * Return all stored associations
	 * 
	 * @return the associations
	 */
	public Set<IAssociation> getAssociations() {
		Set<IAssociation> associations = null;
		if (this.associations == null) {
			associations = redirectGetAssociations();
		} else {
			associations = this.associations.keySet();
		}
		Set<IAssociation> set = HashUtil.getHashSet();
		for (IAssociation association : associations) {
			if (!isRemovedConstruct(association)) {
				set.add(association);
			}
		}
		return set;
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the results for later reuse.
	 * 
	 * @return all associations of the topic map
	 */
	@SuppressWarnings("unchecked")
	protected Set<IAssociation> redirectGetAssociations() {
		Set<IAssociation> associations = (Set<IAssociation>) getTopicMapStore()
				.doRead(getTopicMapStore().getTopicMap(), TopicMapStoreParameterType.ASSOCIATION);

		if (this.associations == null) {
			this.associations = HashUtil.getHashMap();
		}
		for (IAssociation association : associations) {
			this.associations.put(association, redirectGetRoles(association));
		}
		return associations;
	}

	/**
	 * Return the player of a specific role
	 * 
	 * @param r
	 *            the role
	 * @return the player
	 */
	public ITopic getPlayer(Role r) {
		if (isRemovedConstruct((IAssociationRole) r)) {
			throw new TopicMapStoreException("Role is already marked as removed.");
		}
		if (rolePlayers == null || !rolePlayers.containsKey(r)) {
			return redirectGetPlayer(r);
		}
		return rolePlayers.get(r);
	}

	/**
	 * Internal method to redirect the call to the underlying topic map store
	 * and cache the result.
	 * 
	 * @param r
	 *            the role
	 * @return the player
	 */
	protected ITopic redirectGetPlayer(Role r) {
		ITopic player = (ITopic) getTopicMapStore().doRead((IAssociationRole) r, TopicMapStoreParameterType.PLAYER);
		if (rolePlayers == null) {
			rolePlayers = HashUtil.getHashMap();
		}
		rolePlayers.put((IAssociationRole) r, player);
		return player;
	}

	/**
	 * Remove an association item.
	 * 
	 * @param association
	 *            the association item
	 */
	public void removeAssociation(IAssociation association) {
		/*
		 * check if role is known by the store
		 */
		if (associations == null || !associations.containsKey(association)) {
			/*
			 * get associations
			 */
			redirectGetAssociations();
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

		if (removedConstructs == null) {
			removedConstructs = HashUtil.getHashSet();
		}
		removedConstructs.add(association);
	}

	/**
	 * Remove a role from the internal store.
	 * 
	 * @param role
	 *            the role
	 */
	public void removeRole(IAssociationRole role) {
		/*
		 * check if role is known by the store
		 */
		if (rolePlayers == null || !rolePlayers.containsKey(role)) {
			redirectGetPlayer(role);
		}

		/*
		 * get player
		 */
		ITopic player = rolePlayers.get(role);
		/*
		 * remove role from played roles
		 */
		if (playedRoles != null) {
			Set<IAssociationRole> set = playedRoles.get(player);
			if (set == null) {
				redirectGetRoles(player);
				set = playedRoles.get(player);
			}
			set.remove(role);
			playedRoles.put(player, set);
		}
		/*
		 * remove role
		 */
		rolePlayers.remove(role);

		/*
		 * remove role from parent association
		 */
		if ( associations == null){
			redirectGetAssociations();
		}
		Set<IAssociationRole> set = associations.get(role.getParent());
		if (set != null) {
			set.remove(role);
			associations.put(role.getParent(), set);
		}

		if (removedConstructs == null) {
			removedConstructs = HashUtil.getHashSet();
		}
		removedConstructs.add(role);
	}

	/**
	 * Register a new association item.
	 * 
	 * @param association
	 *            the association item
	 */
	public void addAssociation(IAssociation association) {
		if (associations == null) {
			redirectGetAssociations();
		}
		Set<IAssociationRole> set = HashUtil.getHashSet();
		associations.put(association, set);
	}

	/**
	 * Register a new association role item at the internal store.
	 * 
	 * @param association
	 *            the parent association
	 * @param role
	 *            the association role item
	 * @param player
	 *            the role player
	 */
	public void addRole(IAssociation association, IAssociationRole role, ITopic player) {
		/*
		 * redirect get roles played
		 */
		redirectGetRoles(player);
		/*
		 * check if association is known by the data store
		 */
		if (associations == null || !associations.containsKey(association)) {
			redirectGetAssociations();
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
			redirectGetPlayer(role);
		}
		rolePlayers.put(role, player);

		/*
		 * store backward reference of played role
		 */
		if (playedRoles == null) {
			redirectGetRoles(player);
		}
		set = playedRoles.get(player);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(role);
		playedRoles.put(player, set);
	}

	/**
	 * Modify the player of a specific role.
	 * 
	 * @param r
	 *            the role
	 * @param player
	 *            the new player
	 * @return the old player
	 */
	public ITopic setPlayer(IAssociationRole r, ITopic player) {
		if (rolePlayers == null) {
			redirectGetPlayer(r);
		}
		ITopic p = rolePlayers.get(r);
		rolePlayers.put(r, player);

		/*
		 * store backward reference of played role
		 */
		if (playedRoles == null) {
			redirectGetRoles(player);
			redirectGetRoles(p);
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
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		// NOTHING TO DO > DONE BY EXTERNAL MERGE UTILS
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
