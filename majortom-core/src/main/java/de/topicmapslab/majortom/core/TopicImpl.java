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

package de.topicmapslab.majortom.core;

import java.util.Collection;
import java.util.Collections;
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

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link ITopic}
 * 
 * @author Sven Krosse
 * 
 */
public class TopicImpl extends ConstructImpl implements ITopic {

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param topicMap
	 *            the topic map
	 */
	public TopicImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap) {
		super(identity, topicMap, topicMap);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociationsPlayed() {
		return (Collection<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Association type filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, type));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociationsPlayed(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Association scope filter cannot be null!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type, IScope scope) {
		if (type == null) {
			throw new IllegalArgumentException("Association type filter cannot be null!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Association scope filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ASSOCIATION, type, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Collection<ICharacteristics> getCharacteristics() {
		return Collections.unmodifiableSet((Set<ICharacteristics>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.CHARACTERISTICS));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Characteristic type filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<ICharacteristics>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.CHARACTERISTICS, type));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Characteristic scope filter cannot be null!");
		}
		return Collections.unmodifiableSet((Set<ICharacteristics>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.CHARACTERISTICS, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope) {
		if (type == null) {
			throw new IllegalArgumentException("Characteristic type filter cannot be null!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Characteristic scope filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<ICharacteristics>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.CHARACTERISTICS, type,
				scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Name> Collection<T> getNames(Topic type, IScope scope) {
		if (type == null) {
			throw new IllegalArgumentException("Name type filter cannot be null!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Name scope filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.NAME, type, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Name> Collection<T> getNames(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Name scope filter cannot be null!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.NAME, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Occurrence> Collection<T> getOccurrences(Topic type, IScope scope) {
		if (type == null) {
			throw new IllegalArgumentException("Occurrence type filter cannot be null!");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Occurrence scope filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.OCCURRENCE, type, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends Occurrence> Collection<T> getOccurrences(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Occurrence scope filter cannot be null!");
		}
		return Collections.unmodifiableSet((Set<T>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.OCCURRENCE, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Collection<Topic> getSupertypes() {
		return Collections.unmodifiableSet((Set<Topic>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.SUPERTYPE));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSupertype(Topic type) {
		if (type == null) {
			throw new ModelConstraintException(this, "Supertype cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.SUPERTYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSupertype(Topic type) {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (getSupertypes().contains(type)) {
			getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.SUPERTYPE, type);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectIdentifier(Locator identifier) throws IdentityConstraintException, ModelConstraintException {
		if (identifier == null) {
			throw new ModelConstraintException(this, "Subject-identifier cannot be null!");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.SUBJECT_IDENTIFIER, identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSubjectLocator(Locator locator) throws IdentityConstraintException, ModelConstraintException {
		if (locator == null) {
			throw new ModelConstraintException(this, "Subject-locator cannot be null!");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.SUBJECT_LOCATOR, locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(String value, Topic... themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IName) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.NAME, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(String value, Collection<Topic> themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IName) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.NAME, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(Topic type, String value, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IName) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.NAME, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Name createName(Topic type, String value, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IName) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.NAME, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, String value, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, String value, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, Locator value, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, Locator value, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, String value, Locator datatype, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (datatype == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Occurrence createOccurrence(Topic type, String value, Locator datatype, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (datatype == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (IOccurrence) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.OCCURRENCE, type, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Name> getNames() {
		return Collections.unmodifiableSet((Set<Name>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Name> getNames(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Name type filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<Name>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.NAME, type));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Occurrence> getOccurrences() {
		return Collections.unmodifiableSet((Set<Occurrence>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.OCCURRENCE));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Occurrence> getOccurrences(Topic type) {
		if (type == null) {
			throw new IllegalArgumentException("Occurrence type filter cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<Occurrence>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.OCCURRENCE, type));
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
	public Reifiable getReified() {
		return (Reifiable) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.REIFICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesPlayed() {
		return Collections.unmodifiableSet((Set<Role>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesPlayed(Topic roleType) {
		if (roleType == null) {
			throw new IllegalArgumentException("Association role type filter cannot be null!");
		}
		if (!roleType.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<Role>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE, roleType));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Role> getRolesPlayed(Topic roleType, Topic associtaionType) {
		if (associtaionType == null) {
			throw new IllegalArgumentException("Association type filter cannot be null!");
		}
		if (roleType == null) {
			throw new IllegalArgumentException("Association role type filter cannot be null!");
		}
		if (!roleType.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		if (!associtaionType.getParent().equals(getTopicMap())) {
			throw new IllegalArgumentException("Type has to be a topic of the same topic map!");
		}
		return Collections.unmodifiableSet((Set<Role>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.ROLE, roleType, associtaionType));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Locator> getSubjectIdentifiers() {
		return Collections.unmodifiableSet((Set<Locator>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.SUBJECT_IDENTIFIER));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Locator> getSubjectLocators() {
		return Collections.unmodifiableSet((Set<Locator>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.SUBJECT_LOCATOR));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Topic> getTypes() {
		return Collections.unmodifiableSet((Set<Topic>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.TYPE));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addType(Topic type) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null!");
		}
		if (!type.getParent().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.TYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeType(Topic type) {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null!");
		}
		getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.TYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public void mergeIn(Topic topic) throws ModelConstraintException {
		if (topic == null) {
			throw new ModelConstraintException(this, "Other topic cannot be null!");
		}
		if (!topic.equals(this)) {
			getTopicMap().getStore().doMerge(this, (ITopic) topic);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectIdentifier(Locator identifier) {
		if (identifier == null) {
			throw new ModelConstraintException(this, "Subject-identifier cannot be null!");
		}
		if (getSubjectIdentifiers().contains(identifier)) {
			getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.SUBJECT_IDENTIFIER, identifier);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeSubjectLocator(Locator locator) {
		if (locator == null) {
			throw new ModelConstraintException(this, "Subject-locator cannot be null!");
		}
		if (getSubjectLocators().contains(locator)) {
			getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.SUBJECT_LOCATOR, locator);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		String out = "Topic{";
		Set<Locator> set = getSubjectIdentifiers();
		if (!set.isEmpty()) {
			out += "si:" + set.iterator().next().toExternalForm();
		} else {
			set = getSubjectLocators();
			if (!set.isEmpty()) {
				out += "sl:" + set.iterator().next().toExternalForm();
			} else {
				set = getItemIdentifiers();
				if (!set.isEmpty()) {
					out += "ii:" + set.iterator().next().toExternalForm();
				} else {
					Set<Name> names = getNames();
					if (!names.isEmpty()) {
						out += "Name:" + names.iterator().next().getValue();
					} else {
						out += "id:" + getId();
					}
				}
			}
		}
		return out + "}";
	}

}
