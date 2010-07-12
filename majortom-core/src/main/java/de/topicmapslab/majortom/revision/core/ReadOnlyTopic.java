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
import de.topicmapslab.majortom.model.core.ICharacteristics;
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
	private final String reifiedId;

	/*
	 * cached value
	 */
	private Set<Topic> cachedTypes, cachedSupertypes;
	private Set<Name> cachedNames;
	private Set<Occurrence> cachedOccurrences;
	private Reifiable cachedReified;

	/**
	 * constructor
	 * 
	 * @param clone the topic to clone
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
	public <T extends Association> Collection<T> getAssociationsPlayed() {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type, IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics() {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Name> Collection<T> getNames(Topic type, IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Name> Collection<T> getNames(IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Occurrence> Collection<T> getOccurrences(Topic type, IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Occurrence> Collection<T> getOccurrences(IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
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
	public Set<Name> getNames(Topic arg0) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
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
	public Set<Occurrence> getOccurrences(Topic arg0) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
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
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRolesPlayed(Topic arg0) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRolesPlayed(Topic arg0, Topic arg1) {
		throw new UnsupportedOperationException();
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
