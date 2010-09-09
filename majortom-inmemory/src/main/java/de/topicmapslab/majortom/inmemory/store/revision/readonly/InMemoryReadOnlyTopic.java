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
package de.topicmapslab.majortom.inmemory.store.revision.readonly;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.revision.core.ReadOnlyReifiable;
import de.topicmapslab.majortom.revision.core.ReadOnlyTopic;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryReadOnlyTopic extends ReadOnlyTopic {

	private Set<Locator> subjectIdentifiers = HashUtil.getHashSet();
	private Set<Locator> subjectLocators = HashUtil.getHashSet();
	private Set<String> typesIds = HashUtil.getHashSet();
	private Set<String> supertypeIds = HashUtil.getHashSet();
	private Set<String> nameIds = HashUtil.getHashSet();
	private Set<String> occurrenceIds = HashUtil.getHashSet();
	private Set<String> rolesPlayedIds = HashUtil.getHashSet();
	private Set<String> associationsPlayedIds = HashUtil.getHashSet();
	private final String reifiedId;
	private final String bestLabel;

	/*
	 * cached value
	 */
	private Set<Topic> cachedTypes, cachedSupertypes;
	private Set<Name> cachedNames;
	private Set<Occurrence> cachedOccurrences;
	private Reifiable cachedReified;
	private Set<Role> cachedRolesPlayed;
	private Set<Association> cachedAssociationsPlayed;
	private Set<Locator> itemIdentifiers = HashUtil.getHashSet();

	/**
	 * constructor
	 * 
	 * @param clone
	 *            the topic to clone
	 * @param parent
	 */
	public InMemoryReadOnlyTopic(ITopic clone) {
		super(clone);

		for (Locator itemIdentifier : clone.getItemIdentifiers()) {
			itemIdentifiers.add(new LocatorImpl(itemIdentifier.getReference()));
		}

		for (Locator subjectIdentifier : clone.getSubjectIdentifiers()) {
			subjectIdentifiers.add(new LocatorImpl(subjectIdentifier.getReference()));
		}

		for (Locator subjectLocator : clone.getSubjectLocators()) {
			subjectLocators.add(new LocatorImpl(subjectLocator.getReference()));
		}

		for (Topic type : clone.getTypes()) {
			typesIds.add(type.getId());
		}

		for (Topic type : clone.getSupertypes()) {
			supertypeIds.add(type.getId());
		}

		for (Name name : clone.getNames()) {
			nameIds.add(name.getId());
		}

		for (Occurrence occurrence : clone.getOccurrences()) {
			occurrenceIds.add(occurrence.getId());
		}

		if (clone.getReified() != null) {
			reifiedId = clone.getReified().getId();
		} else {
			reifiedId = null;
		}

		for (Role role : clone.getRolesPlayed()) {
			rolesPlayedIds.add(role.getId());
		}

		for (Association association : clone.getAssociationsPlayed()) {
			associationsPlayedIds.add(association.getId());
		}
		
		this.bestLabel = clone.getBestLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		return itemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getParent() {
		return getTopicMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociationsPlayed() {
		Collection<Association> associations = HashUtil.getHashSet();
		if (cachedAssociationsPlayed != null) {
			associations.addAll(cachedAssociationsPlayed);
		}
		if (!associationsPlayedIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(associationsPlayedIds);
			for (String id : ids) {
				Association association = (Association) getTopicMap().getConstructById(id);
				if (association instanceof InMemoryReadOnlyAssociation) {
					if (cachedAssociationsPlayed == null) {
						cachedAssociationsPlayed = HashUtil.getHashSet();
					}
					cachedAssociationsPlayed.add(association);
					cachedAssociationsPlayed.remove(id);
				}
				associations.add(association);
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		Collection<Topic> topics = HashUtil.getHashSet();
		if (cachedSupertypes != null) {
			topics.addAll(cachedSupertypes);
		}
		if (!supertypeIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(supertypeIds);
			for (String id : ids) {
				Topic topic = (Topic) getTopicMap().getConstructById(id);
				if (topic instanceof InMemoryReadOnlyTopic) {
					if (cachedSupertypes == null) {
						cachedSupertypes = HashUtil.getHashSet();
					}
					cachedSupertypes.add(topic);
					supertypeIds.remove(id);
				}
				topics.add(topic);
			}
		}
		return topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Name> getNames() {
		Set<Name> set = HashUtil.getHashSet();
		if (cachedNames != null) {
			set.addAll(cachedNames);
		}
		if (!nameIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(nameIds);
			for (String id : ids) {
				Name name = (Name) getTopicMap().getConstructById(id);
				if (name instanceof InMemoryReadOnlyName) {
					if (cachedNames == null) {
						cachedNames = HashUtil.getHashSet();
					}
					cachedNames.add(name);
					nameIds.remove(id);
				}
				set.add(name);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Occurrence> getOccurrences() {
		Set<Occurrence> set = HashUtil.getHashSet();
		if (cachedOccurrences != null) {
			set.addAll(cachedOccurrences);
		}
		if (!occurrenceIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(occurrenceIds);
			for (String id : ids) {
				Occurrence occ = (Occurrence) getTopicMap().getConstructById(id);
				if (occ instanceof InMemoryReadOnlyOccurrence) {
					if (cachedOccurrences == null) {
						cachedOccurrences = HashUtil.getHashSet();
					}
					cachedOccurrences.add(occ);
					occurrenceIds.remove(id);
				}
				set.add(occ);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Reifiable getReified() {
		if (reifiedId == null) {
			return null;
		}
		if (cachedReified != null) {
			return cachedReified;
		}
		Reifiable reified = (Reifiable) getTopicMap().getConstructById(reifiedId);
		if (reified instanceof ReadOnlyReifiable) {
			cachedReified = reified;
		}
		return reified;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRolesPlayed() {
		Set<Role> roles = HashUtil.getHashSet();
		if (cachedRolesPlayed != null) {
			roles.addAll(cachedRolesPlayed);
		}
		if (!rolesPlayedIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(rolesPlayedIds);
			for (String id : ids) {
				Role role = (Role) getTopicMap().getConstructById(id);
				if (role instanceof InMemoryReadOnlyAssociationRole) {
					if (cachedRolesPlayed == null) {
						cachedRolesPlayed = HashUtil.getHashSet();
					}
					cachedRolesPlayed.add(role);
					cachedRolesPlayed.remove(id);
				}
				roles.add(role);
			}
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getSubjectIdentifiers() {
		return subjectIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getSubjectLocators() {
		return subjectLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getTypes() {
		Set<Topic> topics = HashUtil.getHashSet();
		if (cachedTypes != null) {
			topics.addAll(cachedTypes);
		}
		if (!typesIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(typesIds);
			for (String id : ids) {
				Topic topic = (Topic) getTopicMap().getConstructById(id);
				if (topic instanceof InMemoryReadOnlyTopic) {
					if (cachedTypes == null) {
						cachedTypes = HashUtil.getHashSet();
					}
					cachedTypes.add(topic);
					typesIds.remove(id);
				}
				topics.add(topic);
			}
		}
		return topics;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getBestLabel() {
		return bestLabel;
	}
}
