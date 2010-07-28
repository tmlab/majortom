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
package de.topicmapslab.majortom.revision.core;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyTopic extends ReadOnlyConstruct implements ITopic {

	private Set<Locator> subjectIdentifiers = HashUtil.getHashSet();
	private Set<Locator> subjectLocators = HashUtil.getHashSet();
	private Set<String> typesIds = HashUtil.getHashSet();
	private Set<String> supertypeIds = HashUtil.getHashSet();
	private Set<String> nameIds = HashUtil.getHashSet();
	private Set<String> occurrenceIds = HashUtil.getHashSet();
	private Set<String> rolesPlayedIds = HashUtil.getHashSet();
	private Set<String> associationsPlayedIds = HashUtil.getHashSet();
	private final String reifiedId;

	/*
	 * cached value
	 */
	private Set<Topic> cachedTypes, cachedSupertypes;
	private Set<Name> cachedNames;
	private Set<Occurrence> cachedOccurrences;
	private Reifiable cachedReified;
	private Set<Role> cachedRolesPlayed;
	private Set<Association> cachedAssociationsPlayed;

	/**
	 * constructor
	 * 
	 * @param clone
	 *            the topic to clone
	 * @param parent
	 */
	public ReadOnlyTopic(ITopic clone) {
		super(clone);

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
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getParent() {
		return (ITopicMap) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSupertype(Topic type) {
		throw new UnsupportedOperationException("Construct is read only!");
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
				if (association instanceof ReadOnlyAssociation) {
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
	public Collection<Association> getAssociationsPlayed(Topic type) {
		Collection<Association> associations = HashUtil.getHashSet();
		for (Association association : getAssociationsPlayed()) {
			if (association.getType().equals(type)) {
				associations.add(association);
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociationsPlayed(IScope scope) {
		Collection<Association> associations = HashUtil.getHashSet();
		for (Association association : getAssociationsPlayed()) {
			if (((IAssociation) association).getScopeObject().equals(scope)) {
				associations.add(association);
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociationsPlayed(Topic type, IScope scope) {
		Collection<Association> associations = HashUtil.getHashSet();
		for (Association association : getAssociationsPlayed()) {
			if (association.getType().equals(type) && ((IAssociation) association).getScopeObject().equals(scope)) {
				associations.add(association);
			}
		}
		return associations;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics() {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		for (Name n : getNames()) {
			set.add((IName) n);
		}
		for (Occurrence o : getOccurrences()) {
			set.add((IOccurrence) o);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		for (Name n : getNames(type)) {
			set.add((IName) n);
		}
		for (Occurrence o : getOccurrences(type)) {
			set.add((IOccurrence) o);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		for (Name n : getNames(scope)) {
			set.add((IName) n);
		}
		for (Occurrence o : getOccurrences(scope)) {
			set.add((IOccurrence) o);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope) {
		Collection<ICharacteristics> set = HashUtil.getHashSet();
		for (Name n : getNames(type, scope)) {
			set.add((IName) n);
		}
		for (Occurrence o : getOccurrences(type, scope)) {
			set.add((IOccurrence) o);
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type, IScope scope) {
		Set<Name> set = HashUtil.getHashSet();
		for (Name n : getNames()) {
			if (n.getType().equals(type) && ((IName) n).getScopeObject().equals(scope)) {
				set.add(n);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		Set<Name> set = HashUtil.getHashSet();
		for (Name n : getNames()) {
			if (((IName) n).getScopeObject().equals(scope)) {
				set.add(n);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type, IScope scope) {
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Occurrence o : getOccurrences()) {
			if (o.getType().equals(type) && ((IOccurrence) o).getScopeObject().equals(scope)) {
				set.add(o);
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Occurrence o : getOccurrences()) {
			if (((IOccurrence) o).getScopeObject().equals(scope)) {
				set.add(o);
			}
		}
		return set;
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
				if (topic instanceof ReadOnlyTopic) {
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
	public void removeSupertype(Topic type) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectIdentifier(Locator arg0) throws IdentityConstraintException, ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectLocator(Locator arg0) throws IdentityConstraintException, ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void addType(Topic arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(String arg0, Topic... arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(String arg0, Collection<Topic> arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(Topic arg0, String arg1, Topic... arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(Topic arg0, String arg1, Collection<Topic> arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, String arg1, Topic... arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, String arg1, Collection<Topic> arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, Locator arg1, Topic... arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, Locator arg1, Collection<Topic> arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, String arg1, Locator arg2, Topic... arg3) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic arg0, String arg1, Locator arg2, Collection<Topic> arg3) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
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
				if (name instanceof ReadOnlyName) {
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
	public Set<Name> getNames(Topic type) {
		Set<Name> set = HashUtil.getHashSet();
		for (Name n : getNames()) {
			if (n.getType().equals(type)) {
				set.add(n);
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
				if (occ instanceof ReadOnlyOccurrence) {
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
	public Set<Occurrence> getOccurrences(Topic type) {
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Occurrence o : getOccurrences()) {
			if (o.getType().equals(type)) {
				set.add(o);
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
		if (reified instanceof ReadOnlyConstruct) {
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
				if (role instanceof ReadOnlyAssociationRole) {
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
	public Set<Role> getRolesPlayed(Topic type) {
		Set<Role> roles = HashUtil.getHashSet();
		for (Role role : getRolesPlayed()) {
			if (role.getType().equals(type)) {
				roles.add(role);
			}
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRolesPlayed(Topic type, Topic assoType) {
		Set<Role> roles = HashUtil.getHashSet();
		for (Role role : getRolesPlayed()) {
			if (role.getType().equals(type) && role.getParent().getType().equals(assoType)) {
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
				if (topic instanceof ReadOnlyTopic) {
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
	public void mergeIn(Topic arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectIdentifier(Locator arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectLocator(Locator arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeType(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}

}
