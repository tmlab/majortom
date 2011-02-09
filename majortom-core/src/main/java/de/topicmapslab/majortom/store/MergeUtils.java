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
package de.topicmapslab.majortom.store;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Utility class for merging process.
 * 
 * @author Sven Krosse
 * 
 */
public class MergeUtils {

	/**
	 * Method checks if there is a topic with the same name
	 * 
	 * @param store
	 *            the in-memory store
	 * @param topic
	 *            the topic, the name should added to
	 * @param nameType
	 *            the type of the name
	 * @param value
	 *            the name value
	 * @param themes
	 *            a set of themes
	 * @return the merging candidate-name combination or <code>null</code>
	 */
	public static NameMergeCandidate detectMergeByNameCandidate(ITopicMapStore store, ITopic topic, ITopic nameType, String value, Collection<ITopic> themes) {
		IName duplette = getDuplette(store, topic, nameType, value, themes);
		if (duplette != null) {
			return new NameMergeCandidate(topic, duplette);
		}
		return null;
	}

	/**
	 * Method try to find a topic with the same identity
	 * 
	 * @param store
	 *            the topic map store
	 * @param topic
	 *            the topic with the identities
	 * @return the found topic or <code>null</code>
	 * @throws TopicMapStoreException
	 */
	public static ITopic getDuplette(ITopicMapStore store, Topic topic) throws TopicMapStoreException {
		for (Locator locator : topic.getItemIdentifiers()) {
			ILocator loc = (ILocator) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.LOCATOR, locator.getReference());
			IConstruct duplette = (IConstruct) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, loc);
			if (duplette != null) {
				if (duplette instanceof ITopic) {
					return (ITopic) duplette;
				}
				throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
			}
			duplette = (ITopic) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, loc);
			if (duplette != null) {
				return (ITopic) duplette;
			}
		}

		for (Locator locator : topic.getSubjectIdentifiers()) {
			ILocator loc = (ILocator) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.LOCATOR, locator.getReference());
			IConstruct duplette = (IConstruct) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, loc);
			if (duplette != null) {
				if (duplette instanceof ITopic) {
					return (ITopic) duplette;
				}
				throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
			}
			duplette = (ITopic) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, loc);
			if (duplette != null) {
				return (ITopic) duplette;
			}
		}

		for (Locator locator : topic.getSubjectLocators()) {
			ILocator loc = (ILocator) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.LOCATOR, locator.getReference());
			ITopic duplette = (ITopic) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, loc);
			if (duplette != null) {
				return duplette;
			}
		}
		return null;
	}

	/**
	 * Method returns the duplicated name of the given topic with the given type, scope and value
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param themes
	 *            the themes of scope
	 * @return the duplicated name or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IName getDuplette(ITopicMapStore store, ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = (IScope) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.SCOPE, themes);
		/*
		 * check for name construct
		 */
		for (Name n : topic.getNames(type, scope)) {
			if (n.getValue().equals(value)) {
				return (IName) n;
			}
		}
		return null;
	}

	/**
	 * Method returns the duplicated variant of the given name with the scope, datatype and value
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic
	 * @param value
	 *            the value
	 * @param locator
	 *            the locator of datatype
	 * @param themes
	 *            the themes of scope
	 * @return the duplicated variant or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IVariant getDuplette(ITopicMapStore store, IName name, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = (IScope) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.SCOPE, themes);
		/*
		 * check for variant value
		 */
		for (Variant v : name.getVariants(scope)) {
			if (v.getValue().equals(value) && v.getDatatype().equals(locator)) {
				return (IVariant) v;
			}
		}
		return null;
	}

	/**
	 * Method returns the duplicated occurrence of the given topic with the given type, scope, datatype and value
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param locator
	 *            the locator of datatype
	 * @param themes
	 *            the themes of scope
	 * @return the duplicated occurrence or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IOccurrence getDuplette(ITopicMapStore store, ITopic topic, ITopic type, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = (IScope) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.SCOPE, themes);
		/*
		 * check for occurrence value
		 */
		for (Occurrence o : topic.getOccurrences(type, scope)) {
			if (o.getValue().equals(value) && o.getDatatype().equals(locator)) {
				return (IOccurrence) o;
			}
		}
		return null;
	}

	/**
	 * Method return a duplicated association only differs in the given role players
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the one player
	 * @param other
	 *            the other player
	 * @param association
	 *            the other association containing all information
	 * @return the duplicated association or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IAssociation getDuplette(ITopicMapStore store, ITopic topic, ITopic other, IAssociation association) throws TopicMapStoreException {
		/*
		 * iterate over all associations
		 */
		for (Association a : store.getTopicMap().getAssociations(association.getType(), association.getScopeObject())) {
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (Role role : a.getRoles()) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<Role> roles = association.getRoles(role.getType());
				/*
				 * look for duplicated role
				 */
				for (Role r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (r.getPlayer().equals(role.getPlayer()) || (role.getPlayer().equals(topic) && r.getPlayer().equals(other))) {
						containsRole = true;
						break;
					}
				}
				/*
				 * role is not a duplicated one
				 */
				if (!containsRole) {
					duplette = false;
					break;
				}
			}
			/*
			 * association is a duplicated one
			 */
			if (duplette) {
				return (IAssociation) a;
			}
		}

		/*
		 * no duplicated association found
		 */
		return null;
	}

	/**
	 * Method returns a duplicated association of the given one
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association to check
	 * @param excluded
	 *            a set of excluded associations
	 * @return the duplicated association or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IAssociation getDuplette(ITopicMapStore store, IAssociation association, Set<IAssociation> excluded) throws TopicMapStoreException {
		/*
		 * iterate over all filtered associations
		 */
		for (Association a : store.getTopicMap().getAssociations(association.getType(), association.getScopeObject())) {
			if (a.equals(association) || excluded.contains(a)) {
				continue;
			}
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (Role role : a.getRoles()) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<Role> roles = association.getRoles(role.getType());
				/*
				 * look for duplicated role
				 */
				for (Role r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (r.getPlayer().equals(role.getPlayer())) {
						containsRole = true;
						break;
					}
				}
				/*
				 * role is not a duplicated one
				 */
				if (!containsRole) {
					duplette = false;
					break;
				}
			}
			/*
			 * association is a duplicated one
			 */
			if (duplette) {
				return (IAssociation) a;
			}
		}

		/*
		 * no duplicated association found
		 */
		return null;
	}

	/**
	 * Method merge the given topics.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the one topic
	 * @param other
	 *            the other topic
	 * @param revision
	 *            the revision to store changes
	 * @throws TopicMapStoreException
	 */
	public static void doMerge(ITopicMapStore store, ITopic topic, ITopic other, IRevision revision) throws TopicMapStoreException {
		/*
		 * move names
		 */
		for (Name name : other.getNames()) {
			/*
			 * check if name already contained by the other topic
			 */
			IName duplette = getDuplette(store, topic, (ITopic) name.getType(), name.getValue(), ((IName) name).getScopeObject().getThemes());
			/*
			 * duplicated name found
			 */
			if (duplette != null) {
				/*
				 * copy variants
				 */
				for (Variant v : name.getVariants()) {

					/*
					 * check if variant already contained by the other name
					 */
					IVariant dup = getDuplette(store, duplette, v.getValue(), (ILocator) v.getDatatype(), ((IVariant) v).getScopeObject().getThemes());
					/*
					 * duplicated variant found
					 */
					if (dup != null) {
						/*
						 * merge them
						 */
						doMergeReifiable(store, dup, (IVariant) v, revision);
						/*
						 * copy all item identifiers
						 */
						for (Locator itemIdentifier : v.getItemIdentifiers()) {
							v.removeItemIdentifier(itemIdentifier);
							dup.addItemIdentifier(itemIdentifier);
						}
					}
					/*
					 * no duplicated variant found
					 */
					else {
						IVariant newVariant = (IVariant) duplette.createVariant(v.getValue(), (ILocator) v.getDatatype(), v.getScope());
						/*
						 * copy all item identifiers
						 */
						for (Locator itemIdentifier : v.getItemIdentifiers()) {
							v.removeItemIdentifier(itemIdentifier);
							newVariant.addItemIdentifier(itemIdentifier);
						}
						/*
						 * copy reifier
						 */
						Topic reifier = v.getReifier();
						if (reifier != null) {
							v.setReifier(null);
							newVariant.setReifier(reifier);
						}
					}
				}
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : name.getItemIdentifiers()) {
					name.removeItemIdentifier(itemIdentifier);
					duplette.addItemIdentifier(itemIdentifier);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, (IName) name, revision);
			}
			/*
			 * no duplicated name found
			 */
			else {
				IName newName = (IName) topic.createName((ITopic) name.getType(), name.getValue(), name.getScope());
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : name.getItemIdentifiers()) {
					name.removeItemIdentifier(itemIdentifier);
					newName.addItemIdentifier(itemIdentifier);
				}
				/*
				 * copy reifier
				 */
				Topic reifier = name.getReifier();
				if (reifier != null) {
					name.setReifier(null);
					newName.setReifier(reifier);
				}
				/*
				 * copy variants
				 */
				for (Variant v : name.getVariants()) {
					IVariant newVariant = (IVariant) newName.createVariant(v.getValue(), (ILocator) v.getDatatype(), v.getScope());

					/*
					 * copy all item identifiers
					 */
					for (Locator itemIdentifier : v.getItemIdentifiers()) {
						v.removeItemIdentifier(itemIdentifier);
						newVariant.addItemIdentifier(itemIdentifier);
					}
					/*
					 * copy reifier
					 */
					reifier = v.getReifier();
					if (reifier != null) {
						v.setReifier(null);
						newVariant.setReifier(reifier);
					}
				}
			}
		}

		/*
		 * move occurrences
		 */
		for (Occurrence occurrence : other.getOccurrences()) {
			/*
			 * check if occurrence already contained by the other topic
			 */
			IOccurrence duplette = getDuplette(store, topic, (ITopic) occurrence.getType(), occurrence.getValue(), (ILocator) occurrence.getDatatype(), ((IOccurrence) occurrence).getScopeObject()
					.getThemes());
			/*
			 * duplicated occurrence found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : occurrence.getItemIdentifiers()) {
					occurrence.removeItemIdentifier(itemIdentifier);
					duplette.addItemIdentifier(itemIdentifier);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, (IOccurrence) occurrence, revision);
			}
			/*
			 * no duplicated occurrence found
			 */
			else {
				IOccurrence newOccurrence = (IOccurrence) topic.createOccurrence((ITopic) occurrence.getType(), occurrence.getValue(), (ILocator) occurrence.getDatatype(), occurrence.getScope());
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : occurrence.getItemIdentifiers()) {
					occurrence.removeItemIdentifier(itemIdentifier);
					newOccurrence.addItemIdentifier(itemIdentifier);
				}
				/*
				 * copy reifier
				 */
				Topic reifier = occurrence.getReifier();
				if (reifier != null) {
					occurrence.setReifier(null);
					newOccurrence.setReifier(reifier);
				}
			}
		}

		/*
		 * move associations played
		 */
		for (Association association : other.getAssociationsPlayed()) {
			/*
			 * check if association is already played by the other topic
			 */
			IAssociation duplette = getDuplette(store, topic, other, (IAssociation) association);
			/*
			 * duplicated association found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : association.getItemIdentifiers()) {
					association.removeItemIdentifier(itemIdentifier);
					duplette.addItemIdentifier(itemIdentifier);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, (IAssociation) association, revision);

			}
			/*
			 * no duplicated association found
			 */
			else {
				for (Role role : association.getRoles()) {
					if (role.getPlayer().equals(other)) {
						role.setPlayer(topic);
					}
				}
			}
		}
		/*
		 * replace identity
		 */
		replaceIdentity(store, topic, other);
		/*
		 * replace as type or supertype
		 */
		replaceAsTypeOrSupertype(store, topic, other);
		/*
		 * replace as theme
		 */
		replaceAsTheme(store, topic, other);

		/*
		 * replace as reifier
		 */
		Reifiable reifiable = other.getReified();
		if (reifiable != null) {
			reifiable.setReifier(null);
			reifiable.setReifier(topic);
		}
		/*
		 * remove topic
		 */
		store.doRemove(other, true);
	}

	/**
	 * Method mode the whole identity of the one topic to the other topic
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic as target
	 * @param other
	 *            the topic as source
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void replaceIdentity(ITopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		/*
		 * item-identifier
		 */
		for (Locator loc : other.getItemIdentifiers()) {
			other.removeItemIdentifier(loc);
			topic.addItemIdentifier(loc);
		}
		/*
		 * subject-identifier
		 */
		for (Locator loc : other.getSubjectIdentifiers()) {
			other.removeSubjectIdentifier(loc);
			topic.addSubjectIdentifier(loc);
		}

		/*
		 * subject-locator
		 */
		for (Locator loc : other.getSubjectLocators()) {
			other.removeSubjectLocator(loc);
			topic.addSubjectLocator(loc);
		}
	}

	/**
	 * Method replace each type or supertype relation of the one topic by the other topic
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic as target
	 * @param other
	 *            the topic as source
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void replaceAsTypeOrSupertype(ITopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		ITypeInstanceIndex typeIndex = store.getIndex(ITypeInstanceIndex.class);
		if (!typeIndex.isOpen()) {
			typeIndex.open();
		}
		/*
		 * replace as topic type
		 */
		for (Topic t : typeIndex.getTopics(other)) {
			t.removeType(other);
			t.addType(topic);
		}
		/*
		 * replace as association type
		 */
		for (Association a : typeIndex.getAssociations(other)) {
			a.setType(topic);
		}
		/*
		 * replace as role type
		 */
		for (Role r : typeIndex.getRoles(other)) {
			r.setType(topic);
		}
		/*
		 * replace as occurrence type
		 */
		for (Occurrence o : typeIndex.getOccurrences(other)) {
			o.setType(topic);
		}
		/*
		 * replace as name type
		 */
		for (Name n : typeIndex.getNames(other)) {
			n.setType(topic);
		}

		ISupertypeSubtypeIndex supertypeSubtypeIndex = store.getIndex(ISupertypeSubtypeIndex.class);
		if (!supertypeSubtypeIndex.isOpen()) {
			supertypeSubtypeIndex.open();
		}
		/*
		 * replace as supertype
		 */
		for (Topic t : supertypeSubtypeIndex.getDirectSubtypes(other)) {
			((ITopic) t).removeSupertype(other);
			((ITopic) t).addSupertype(topic);
		}
	}

	/**
	 * Method replace the one topic by the other topic as theme
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic as target
	 * @param other
	 *            the topic as source
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void replaceAsTheme(ITopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		IScopedIndex scopeIndex = store.getIndex(IScopedIndex.class);
		if (!scopeIndex.isOpen()) {
			scopeIndex.open();
		}

		/*
		 * replace as association theme
		 */
		for (Association a : scopeIndex.getAssociations(other)) {
			a.removeTheme(other);
			a.addTheme(topic);
		}
		/*
		 * replace as name theme
		 */
		for (Name n : scopeIndex.getNames(other)) {
			n.removeTheme(other);
			n.addTheme(topic);
		}
		/*
		 * replace as variant theme
		 */
		for (Variant v : scopeIndex.getVariants(other)) {
			v.removeTheme(other);
			v.addTheme(topic);
		}
		/*
		 * replace as occurrence theme
		 */
		for (Occurrence o : scopeIndex.getOccurrences(other)) {
			o.removeTheme(other);
			o.addTheme(topic);
		}
	}

	/**
	 * Merge the reification of both reified constructs
	 * 
	 * @param store
	 *            the store
	 * @param reifiable
	 *            the one reified construct
	 * @param other
	 *            the other reified construct
	 * @param revision
	 *            the revision to store
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void doMergeReifiable(ITopicMapStore store, IReifiable reifiable, IReifiable other, IRevision revision) throws TopicMapStoreException {
		Topic reifierOfOther = other.getReifier();
		if (reifierOfOther != null) {
			Topic reifier = reifiable.getReifier();
			/*
			 * other construct has no reifier
			 */
			if (reifier == null) {
				/*
				 * move reifier
				 */
				other.setReifier(null);
				reifiable.setReifier(reifierOfOther);
			}
			/*
			 * both constructs are reified
			 */
			else {
				other.setReifier(null);
				/*
				 * merge both topics
				 */
				ITopic newReifier = (ITopic) store.getTopicMap().createTopic();
				doMerge(store, (ITopic) newReifier, (ITopic) reifier, revision);
				doMerge(store, (ITopic) newReifier, (ITopic) reifierOfOther, revision);
				ITopicMapStoreIdentity reifierIdent = ((TopicImpl) reifier).getIdentity();
				ITopicMapStoreIdentity reifierOfOtherIdent = ((TopicImpl) reifierOfOther).getIdentity();
				try {
					reifierIdent.setId(newReifier.longId());
					reifierOfOtherIdent.setId(newReifier.longId());
				} catch (UnsupportedOperationException e) {
					try {
						/*
						 * try to access a string method
						 */
						Method m = reifierIdent.getClass().getMethod("setId", String.class);
						m.invoke(reifierIdent, newReifier.getId());
						m.invoke(reifierOfOtherIdent, newReifier.getId());
					} catch (Exception e2) {
						throw new TopicMapStoreException("Cannot update topic identity", e);
					}
				}
			}
		}
	}

	/**
	 * Merge the one topic map into the other
	 * 
	 * @param store
	 *            the store
	 * @param topicMap
	 *            the topic map as target
	 * @param other
	 *            the topic map as source
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void doMergeTopicMaps(ITopicMapStore store, ITopicMap topicMap, TopicMap other) throws TopicMapStoreException {
		/*
		 * copy identifies
		 */
		for (Topic topic : other.getTopics()) {
			ITopic duplette = getDuplette(store, topic);
			if (duplette == null) {
				duplette = (ITopic) store.doCreate(topicMap, TopicMapStoreParameterType.TOPIC);
			}

			for (Locator loc : topic.getItemIdentifiers()) {
				if (!duplette.getItemIdentifiers().contains(loc)) {
					duplette.addItemIdentifier(store.getTopicMap().createLocator(loc.getReference()));
				}
			}

			for (Locator loc : topic.getSubjectIdentifiers()) {
				if (!duplette.getSubjectIdentifiers().contains(loc)) {
					duplette.addSubjectIdentifier(store.getTopicMap().createLocator(loc.getReference()));
				}
			}
			for (Locator loc : topic.getSubjectLocators()) {
				if (!duplette.getSubjectLocators().contains(loc)) {
					duplette.addSubjectLocator(store.getTopicMap().createLocator(loc.getReference()));
				}
			}
		}

		/*
		 * copy informations
		 */
		for (Topic topic : other.getTopics()) {
			ITopic duplette = getDuplette(store, topic);
			if (duplette == null) {
				throw new TopicMapStoreException("Unknown topic!");
			}

			/*
			 * copy types
			 */
			for (Topic type : topic.getTypes()) {
				ITopic t = getDuplette(store, type);
				duplette.addType(t);
			}

			/*
			 * copy super types
			 */
			if (topic instanceof ITopic) {
				for (Topic type : ((ITopic) topic).getSupertypes()) {
					ITopic t = getDuplette(store, type);
					duplette.addSupertype(t);
				}
			}

			/*
			 * copy occurrences
			 */
			for (Occurrence occ : topic.getOccurrences()) {
				ITopic type = getDuplette(store, occ.getType());
				IScope scope = getCorrespondingScope(store, occ.getScope());
				ILocator datatype = (ILocator) store.getTopicMap().createLocator(occ.getDatatype().getReference());
				IOccurrence occurrence = getDuplette(store, duplette, type, occ.getValue(), datatype, scope.getThemes());
				if (occurrence == null) {
					occurrence = (IOccurrence) duplette.createOccurrence(type, occ.getValue(), datatype, getCorrespondingScope(store, occ.getScope()).getThemes().toArray(new Topic[0]));
				}
				/*
				 * copy item-identifiers of the occurrence
				 */
				for (Locator loc : occ.getItemIdentifiers()) {
					occurrence.addItemIdentifier(store.getTopicMap().createLocator(loc.getReference()));
				}

				/*
				 * copy reification
				 */
				Topic reifier = occ.getReifier();
				if (reifier != null) {
					occurrence.setReifier(getDuplette(store, reifier));
				}

			}

			/*
			 * copy names
			 */
			for (Name name : topic.getNames()) {
				ITopic type = getDuplette(store, name.getType());
				IScope scope = getCorrespondingScope(store, name.getScope());
				IName n = getDuplette(store, duplette, type, name.getValue(), scope.getThemes());
				if (n == null) {
					n = (IName) duplette.createName(type, name.getValue(), scope.getThemes().toArray(new Topic[0]));
				}

				/*
				 * copy item-identifiers of the name
				 */
				for (Locator loc : name.getItemIdentifiers()) {
					n.addItemIdentifier(store.getTopicMap().createLocator(loc.getReference()));
				}

				/*
				 * copy reification
				 */
				Topic reifier = name.getReifier();
				if (reifier != null) {
					n.setReifier(getDuplette(store, reifier));
				}

				/*
				 * copy variants
				 */
				for (Variant v : name.getVariants()) {
					scope = getCorrespondingScope(store, v.getScope());
					ILocator datatype = (ILocator) store.getTopicMap().createLocator(v.getDatatype().getReference());
					IVariant variant = getDuplette(store, n, v.getValue(), datatype, scope.getThemes());
					if (variant == null) {
						variant = (IVariant) n.createVariant(v.getValue(), datatype, scope.getThemes().toArray(new Topic[0]));
					}

					/*
					 * copy item-identifiers of the variant
					 */
					for (Locator loc : v.getItemIdentifiers()) {
						variant.addItemIdentifier(store.getTopicMap().createLocator(loc.getReference()));
					}

					/*
					 * copy reification
					 */
					reifier = v.getReifier();
					if (reifier != null) {
						variant.setReifier(getDuplette(store, reifier));
					}
				}
			}
		}

		/*
		 * copy associations
		 */
		for (Association ass : other.getAssociations()) {

			/*
			 * filter TMDM associations
			 */
			if (checkTmdmAssociation(store, ass, topicMap, other)) {
				continue;
			}

			ITopic type = getDuplette(store, ass.getType());
			IAssociation association = (IAssociation) topicMap.createAssociation(type, getCorrespondingScope(store, ass.getScope()).getThemes().toArray(new Topic[0]));

			/*
			 * copy item-identifiers of the association
			 */
			for (Locator loc : ass.getItemIdentifiers()) {
				association.addItemIdentifier(topicMap.createLocator(loc.getReference()));
			}

			/*
			 * copy reification
			 */
			Topic reifier = ass.getReifier();
			if (reifier != null) {
				association.setReifier(getDuplette(store, reifier));
			}

			/*
			 * copy roles
			 */
			for (Role r : ass.getRoles()) {
				type = getDuplette(store, r.getType());
				ITopic player = getDuplette(store, r.getPlayer());
				IAssociationRole role = (IAssociationRole) association.createRole(type, player);
				/*
				 * copy item-identifiers of the role
				 */
				for (Locator loc : r.getItemIdentifiers()) {
					role.addItemIdentifier(topicMap.createLocator(loc.getReference()));
				}

				/*
				 * copy reification
				 */
				reifier = r.getReifier();
				if (reifier != null) {
					role.setReifier(getDuplette(store, reifier));
				}
			}
		}
	}

	/**
	 * Returns the corresponding scope containing all themes of the origin
	 * 
	 * @param store
	 *            the store
	 * @param themes
	 *            the set of themes
	 * @return the scope object
	 */
	public static IScope getCorrespondingScope(ITopicMapStore store, final Set<Topic> themes) {
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic t : themes) {
			ITopic theme = getDuplette(store, t);
			set.add(theme);
		}
		return store.getTopicMap().createScope(set);
	}

	/**
	 * Method checks if the association is a TMDM association. Such associations will not be copied into the new topic
	 * map to avoid duplicated entries.
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association
	 * @param topicMap
	 *            the topic map
	 * @param other
	 *            the other topic map
	 * @return <code>true</code> if the association is an TMDM association, <code>false</code> otherwise.
	 * @throws TopicMapStoreException
	 */
	private static boolean checkTmdmAssociation(ITopicMapStore store, Association association, ITopicMap topicMap, TopicMap other) throws TopicMapStoreException {
		Locator typeInstanceLocator = topicMap.createLocator(Namespaces.TMDM.TYPE_INSTANCE);
		Locator supertypeSubtypeLocator = topicMap.createLocator(Namespaces.TMDM.SUPERTYPE_SUBTYPE);

		/*
		 * is tmdm:supertype-subtype-association
		 */
		if (association.getType().getSubjectIdentifiers().contains(supertypeSubtypeLocator)) {
			/*
			 * get role-types of TMDM association
			 */
			Topic supertypeRole = other.getTopicBySubjectIdentifier(other.createLocator(Namespaces.TMDM.SUPERTYPE));
			Topic subtypeRole = other.getTopicBySubjectIdentifier(other.createLocator(Namespaces.TMDM.SUBTYPE));
			/*
			 * TMDM restricts that role-types has to exist if the association exists
			 */
			if (supertypeRole == null || subtypeRole == null) {
				throw new TopicMapStoreException("Invalid supertype-subtype-association, missing at least one role-type");
			}
			/*
			 * get equivalent players contained by the topic map, information merged in
			 */
			ITopic supertype = getDuplette(store, association.getRoles(supertypeRole).iterator().next().getPlayer());
			ITopic subtype = getDuplette(store, association.getRoles(subtypeRole).iterator().next().getPlayer());
			/*
			 * create association if players missing
			 */
			if (subtype == null || supertype == null) {
				return false;
			}
			/*
			 * check if super-type is known
			 */
			return subtype.getSupertypes().contains(supertype);
		} else if (association.getType().getSubjectIdentifiers().contains(typeInstanceLocator)) {
			/*
			 * get role-types of TMDM association
			 */
			Topic typeRole = other.getTopicBySubjectIdentifier(other.createLocator(Namespaces.TMDM.TYPE));
			Topic instanceRole = other.getTopicBySubjectIdentifier(other.createLocator(Namespaces.TMDM.INSTANCE));
			/*
			 * TMDM restricts that role-types has to exist if the association exists
			 */
			if (typeRole == null || instanceRole == null) {
				throw new TopicMapStoreException("Invalid type-instance-association, missing at least one role-type");
			}
			/*
			 * get equivalent players contained by the topic map, information merged in
			 */
			ITopic type = getDuplette(store, association.getRoles(typeRole).iterator().next().getPlayer());
			ITopic instance = getDuplette(store, association.getRoles(instanceRole).iterator().next().getPlayer());
			/*
			 * create association if players missing
			 */
			if (instance == null || type == null) {
				return false;
			}
			/*
			 * check if type is known
			 */
			return instance.getTypes().contains(type);
		}
		/*
		 * any other association
		 */
		return false;

	}

	/**
	 * Method removes all duplicates from the given topic map.
	 * 
	 * @param store
	 *            the topic map store
	 * @param topicMap
	 *            the topic map itselfs
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void removeDuplicates(final ITopicMapStore store, final ITopicMap topicMap) throws TopicMapStoreException {
		// ThreadPoolExecutor executor = (ThreadPoolExecutor)
		// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()
		// * 4);

		for (final Topic topic : topicMap.getTopics()) {
			// Thread thread = new Thread() {
			// /**
			// * {@inheritDoc}
			// */
			// public void run() {
			Set<Construct> removed = HashUtil.getHashSet();
			/*
			 * check name duplicates
			 */
			for (Name name : topic.getNames()) {
				if (removed.contains(name)) {
					continue;
				}
				for (Name duplicate : topic.getNames()) {
					if (duplicate.equals(name) || removed.contains(duplicate)) {
						continue;
					}
					/*
					 * names are equal if the value, the type and scope property are equal
					 */
					if (duplicate.getType().equals(name.getType()) && duplicate.getValue().equals(name.getValue()) && ((IName) duplicate).getScopeObject().equals(((IName) name).getScopeObject())) {
						/*
						 * copy item-identifier
						 */
						for (Locator ii : duplicate.getItemIdentifiers()) {
							duplicate.removeItemIdentifier(ii);
							name.addItemIdentifier(ii);
						}
						/*
						 * copy variants
						 */
						for (Variant v : duplicate.getVariants()) {
							Variant copy = getDuplette(store, (IName) name, v.getValue(), (ILocator) v.getDatatype(), ((IVariant) v).getScopeObject().getThemes());
							if (copy == null) {
								copy = name.createVariant(v.getValue(), v.getDatatype(), v.getScope());
							}
							/*
							 * copy item-identifier
							 */
							for (Locator ii : v.getItemIdentifiers()) {
								v.removeItemIdentifier(ii);
								copy.addItemIdentifier(ii);
							}
							/*
							 * check reification
							 */

							doMergeReifiable(store, (IVariant) copy, (IVariant) v, null);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IName) name, (IName) duplicate, null);
						/*
						 * remove duplicate
						 */
						duplicate.remove();
						removed.add(duplicate);
					}
				}
				/*
				 * check variants
				 */
				for (Variant v : name.getVariants()) {
					if (removed.contains(v)) {
						continue;
					}
					for (IVariant dup : getDuplettes(store, (IName) name, v.getValue(), (ILocator) v.getDatatype(), ((IVariant) v).getScopeObject().getThemes())) {
						if (v.equals(dup) || removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : dup.getItemIdentifiers()) {
							dup.removeItemIdentifier(ii);
							v.addItemIdentifier(ii);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IVariant) v, (IVariant) dup, null);
						/*
						 * remove duplicate
						 */
						removed.add(dup);
						dup.remove();
					}
				}
			}
			removed.clear();
			/*
			 * check occurrences
			 */
			for (Occurrence occurrence : topic.getOccurrences()) {
				if (removed.contains(occurrence)) {
					continue;
				}
				for (Occurrence duplicate : topic.getOccurrences()) {
					if (duplicate.equals(occurrence) || removed.contains(duplicate)) {
						continue;
					}
					/*
					 * occurrences are equal if the value, datatype, the type and scope property are equal
					 */
					if (duplicate.getType().equals(duplicate.getType()) && duplicate.getValue().equals(duplicate.getValue())
							&& ((IOccurrence) duplicate).getScopeObject().equals(((IOccurrence) duplicate).getScopeObject()) && occurrence.getDatatype().equals(duplicate.getDatatype())) {
						/*
						 * copy item-identifier
						 */
						for (Locator ii : duplicate.getItemIdentifiers()) {
							duplicate.removeItemIdentifier(ii);
							occurrence.addItemIdentifier(ii);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IOccurrence) occurrence, (IOccurrence) duplicate, null);
						/*
						 * remove duplicate
						 */
						duplicate.remove();
						removed.add(duplicate);
					}
				}
			}
			// }
			// };
			// executor.execute(thread);
		}
		// Thread thread = new Thread() {
		// /**
		// * {@inheritDoc}
		// */
		// public void run() {
		Set<Construct> removed = HashUtil.getHashSet();
		/*
		 * check associations
		 */
		for (final Association association : topicMap.getAssociations()) {
			if (removed.contains(association)) {
				continue;
			}
			for (IAssociation duplicate : getDuplettes(store, (IAssociation) association)) {
				if (removed.contains(duplicate)) {
					continue;
				}
				/*
				 * copy item-identifier
				 */
				for (Locator ii : duplicate.getItemIdentifiers()) {
					duplicate.removeItemIdentifier(ii);
					association.addItemIdentifier(ii);
				}
				/*
				 * check roles
				 */
				for (Role r : association.getRoles()) {
					for (IAssociationRole dup : getDuplettes(duplicate, (IAssociationRole) r)) {
						if (removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : dup.getItemIdentifiers()) {
							dup.removeItemIdentifier(ii);
							r.addItemIdentifier(ii);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IAssociationRole) r, dup, null);
						/*
						 * store removed
						 */
						removed.add(dup);
					}
				}
				/*
				 * check reification
				 */
				doMergeReifiable(store, (IAssociation) association, (IAssociation) duplicate, null);
				/*
				 * remove duplicate
				 */
				removed.add(duplicate);
				duplicate.remove();
			}
			/*
			 * check roles
			 */
			for (Role r : association.getRoles()) {
				if (removed.contains(r)) {
					continue;
				}
				for (IAssociationRole dup : getDuplettes((IAssociation) association, (IAssociationRole) r)) {
					if (dup.equals(r) || removed.contains(dup)) {
						continue;
					}
					/*
					 * copy item-identifier
					 */
					for (Locator ii : dup.getItemIdentifiers()) {
						dup.removeItemIdentifier(ii);
						r.addItemIdentifier(ii);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, (IAssociationRole) r, dup, null);
					/*
					 * remove duplicate
					 */
					removed.add(dup);
					dup.remove();
				}
			}
		}
		// }
		// };
		// executor.execute(thread);
		// /*
		// * shut-down thread pool
		// */
		// executor.shutdown();
		// /*
		// * wait
		// */
		// try {
		// executor.awaitTermination(10, TimeUnit.MINUTES);
		// } catch (InterruptedException e) {
		// throw new TopicMapStoreException(e);
		// }
	}

	/**
	 * Returns a set the duplicated role of the given association with the same type and player than the given one.
	 * 
	 * @param association
	 *            the association
	 * @param role
	 *            the role
	 * @return a set of duplicated roles
	 */
	public static Set<IAssociationRole> getDuplettes(IAssociation association, IAssociationRole role) {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (Role r : association.getRoles(role.getType())) {
			if (role.equals(r)) {
				continue;
			}
			if (r.getPlayer().equals(role.getPlayer())) {
				set.add((IAssociationRole) r);
			}
		}
		return set;
	}

	public static Set<IAssociation> getDuplettes2(ITopic topic, ITopicMapStore store, IAssociation association) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		/*
		 * iterate over all filtered associations
		 */
		for (Association a : topic.getAssociationsPlayed(association.getType(), association.getScopeObject())) {
			if (a.equals(association)) {
				continue;
			}
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (Role role : a.getRoles()) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<Role> roles = association.getRoles(role.getType());
				/*
				 * look for duplicated role
				 */
				for (Role r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (r.getPlayer().equals(role.getPlayer())) {
						containsRole = true;
						break;
					}
				}
				/*
				 * role is not a duplicated one
				 */
				if (!containsRole) {
					duplette = false;
					break;
				}
			}
			/*
			 * association is a duplicated one
			 */
			if (duplette) {
				associations.add((IAssociation) a);
			}
		}

		/*
		 * no duplicated association found
		 */
		return associations;
	}

	/**
	 * Method returns a set of duplicated association of the given one
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association to check
	 * @return a set of duplicated associations
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static Set<IAssociation> getDuplettes(ITopicMapStore store, IAssociation association) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		/*
		 * iterate over all filtered associations
		 */
		for (Association a : store.getTopicMap().getAssociations(association.getType(), association.getScopeObject())) {
			if (a.equals(association)) {
				continue;
			}
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (Role role : a.getRoles()) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<Role> roles = association.getRoles(role.getType());
				/*
				 * look for duplicated role
				 */
				for (Role r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (r.getPlayer().equals(role.getPlayer())) {
						containsRole = true;
						break;
					}
				}
				/*
				 * role is not a duplicated one
				 */
				if (!containsRole) {
					duplette = false;
					break;
				}
			}
			/*
			 * association is a duplicated one
			 */
			if (duplette) {
				associations.add((IAssociation) a);
			}
		}

		/*
		 * no duplicated association found
		 */
		return associations;
	}

	/**
	 * Method returns the duplicated variant of the given name with the scope, datatype and value
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the topic
	 * @param value
	 *            the value
	 * @param locator
	 *            the locator of datatype
	 * @param themes
	 *            the themes of scope
	 * @return the duplicated variant or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static Set<IVariant> getDuplettes(ITopicMapStore store, IName name, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		Set<IVariant> variants = HashUtil.getHashSet();
		/*
		 * get scope as filter
		 */
		IScope scope = (IScope) store.doCreate(store.getTopicMap(), TopicMapStoreParameterType.SCOPE, themes);
		/*
		 * check for variant value
		 */
		for (Variant v : name.getVariants(scope)) {
			if (v.getValue().equals(value) && v.getDatatype().equals(locator)) {
				variants.add((IVariant) v);
			}
		}
		return variants;
	}

}
