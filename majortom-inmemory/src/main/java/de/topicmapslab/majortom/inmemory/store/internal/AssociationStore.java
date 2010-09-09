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
package de.topicmapslab.majortom.inmemory.store.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Role;

import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Internal data store of association and role informations
 * 
 * @author Sven Krosse
 * 
 */
public class AssociationStore implements IDataStore {

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
	}

	/**
	 * Return all roles of the given association
	 * 
	 * @param association the association
	 * @return the roles
	 */
	public Set<IAssociationRole> getRoles(Association association) {
		if (associations == null || !associations.containsKey(association)) {
			return Collections.emptySet();
		}
		return associations.get(association);
	}

	/**
	 * Return the played roles of the given role player.
	 * 
	 * @param player the player
	 * @return the roles
	 */
	public Set<IAssociationRole> getRoles(ITopic player) {
		if (playedRoles == null || !playedRoles.containsKey(player)) {
			return Collections.emptySet();
		}
		return playedRoles.get(player);
	}
	
	/**
	 * Return the played associations of the given role player.
	 * 
	 * @param player the player
	 * @return the associations
	 */
	public Set<IAssociation> getAssocaitionsPlayed(ITopic player){
		Set<IAssociation> associations = HashUtil.getHashSet();
		for ( IAssociationRole role : getRoles(player)){
			associations.add(role.getParent());
		}
		if ( associations.isEmpty()){
			return Collections.emptySet();
		}
		return associations;
	}

	/**
	 * Return all stored associations
	 * 
	 * @return the associations
	 */
	public Set<IAssociation> getAssociations() {
		if (associations == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(associations.keySet());
	}

	/**
	 * Return the player of a specific role
	 * 
	 * @param r the role
	 * @return the player
	 */
	public ITopic getPlayer(Role r) {
		if (rolePlayers == null || !rolePlayers.containsKey(r)) {
			throw new TopicMapStoreException("Unknown role instance.");
		}
		return rolePlayers.get(r);
	}

	/**
	 * Remove an association item.
	 * 
	 * @param association the association item
	 */
	public void removeAssociation(IAssociation association) {
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
	 * Remove a role from the internal store.
	 * 
	 * @param role the role
	 */
	public void removeRole(IAssociationRole role) {
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
			throw new TopicMapStoreException("Roles cache does not contains the given player.");
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
	 * Register a new association role item at the internal store.
	 * 
	 * @param association the parent association
	 * @param role the association role item
	 * @param player the role player
	 */
	public void addRole(IAssociation association, IAssociationRole role, ITopic player) {
		/*
		 * check if association is known by the data store
		 */
		if (associations == null || !associations.containsKey(association)) {
			throw new TopicMapStoreException("Associations cache does not contains the association item");
		}
		/*
		 * add role to association store
		 */
		Set<IAssociationRole> set = associations.get(association);
		if (set == null) {
			throw new TopicMapStoreException("Associations cache does not contains the association item");
		}
		set.add(role);

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
	 * Modify the player of a specific role.
	 * 
	 * @param r the role
	 * @param player the new player
	 * @return the old player
	 */
	public ITopic setPlayer(IAssociationRole r, ITopic player) {
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
}
