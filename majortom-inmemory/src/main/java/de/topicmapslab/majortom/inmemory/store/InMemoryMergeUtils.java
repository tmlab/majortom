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
package de.topicmapslab.majortom.inmemory.store;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
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
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.store.MergeUtils;
import de.topicmapslab.majortom.store.NameMergeCandidate;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;

/**
 * Utility class for merging process.
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryMergeUtils {

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
	public static NameMergeCandidate detectMergeByNameCandidate(
			InMemoryTopicMapStore store, ITopic topic, ITopic nameType,
			String value, Collection<ITopic> themes) {
		IName duplette = getDuplette(store, topic, nameType, value, themes);
		if (duplette != null) {
			return new NameMergeCandidate(topic, duplette);
		}
		return null;
	}

	/**
	 * Returns the topic of the given store which is equal to the given one.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            a topic of another store
	 * @return the duplicated topic or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static ITopic getDuplette(InMemoryTopicMapStore store, Topic topic)
			throws TopicMapStoreException {
		for (Locator locator : topic.getItemIdentifiers()) {
			ILocator loc = store.getIdentityStore().createLocator(
					locator.getReference());
			IConstruct duplette = store.getIdentityStore()
					.byItemIdentifier(loc);
			if (duplette != null) {
				if (duplette instanceof ITopic) {
					return (ITopic) duplette;
				}
				throw new IdentityConstraintException(topic, duplette, locator,
						"Unresolveable identifier conflicts.");
			}
			duplette = store.getIdentityStore().bySubjectIdentifier(loc);
			if (duplette != null) {
				return (ITopic) duplette;
			}
		}

		for (Locator locator : topic.getSubjectIdentifiers()) {
			ILocator loc = store.getIdentityStore().createLocator(
					locator.getReference());
			IConstruct duplette = store.getIdentityStore()
					.byItemIdentifier(loc);
			if (duplette != null) {
				if (duplette instanceof ITopic) {
					return (ITopic) duplette;
				}
				throw new IdentityConstraintException(topic, duplette, locator,
						"Unresolveable identifier conflicts.");
			}
			duplette = store.getIdentityStore().bySubjectIdentifier(loc);
			if (duplette != null) {
				return (ITopic) duplette;
			}
		}

		for (Locator locator : topic.getSubjectLocators()) {
			ILocator loc = store.getIdentityStore().createLocator(
					locator.getReference());
			ITopic duplette = store.getIdentityStore().bySubjectLocator(loc);
			if (duplette != null) {
				return duplette;
			}
		}
		return null;
	}

	/**
	 * Returning the duplicated name with the given type, parent, value and
	 * scope.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the parent
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param themes
	 *            the scope
	 * @return the duplicated name or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IName getDuplette(InMemoryTopicMapStore store, ITopic topic,
			ITopic type, String value, Collection<ITopic> themes)
			throws TopicMapStoreException {
		Set<IName> names = HashUtil.getHashSet();
		/*
		 * get all names
		 */
		names.addAll(store.getCharacteristicsStore().getNames(topic));
		/*
		 * filter by same type
		 */
		names.retainAll(store.getTypedStore().getTyped(type));
		/*
		 * filter by same scope
		 */
		IScope scope = store.getScopeStore().getScope(themes);
		names.retainAll(store.getScopeStore().getScoped(scope));

		/*
		 * filter by value
		 */
		for (IName n : names) {
			if (n.getValue().equals(value)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Returning the duplicated variant with the given parent, value, datatype
	 * and scope.
	 * 
	 * @param store
	 *            the store
	 * @param name
	 *            the parent
	 * @param value
	 *            the value
	 * @param locator
	 *            the datatype
	 * @param themes
	 *            the scope
	 * @return the duplicated variant or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IVariant getDuplette(InMemoryTopicMapStore store, IName name,
			String value, ILocator locator, Collection<ITopic> themes)
			throws TopicMapStoreException {
		Set<IVariant> set = HashUtil.getHashSet();
		/*
		 * get all names
		 */
		set.addAll(store.getCharacteristicsStore().getVariants(name));
		/*
		 * filter by same scope
		 */
		IScope scope = store.getScopeStore().getScope(themes);
		set.retainAll(store.getScopeStore().getScoped(scope));

		/*
		 * filter by value
		 */
		for (IVariant v : set) {
			if (v.getValue().equals(value) && v.getDatatype().equals(locator)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * Returning the duplicated occurrence with the given type, parent, value,
	 * datatype and scope.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the parent
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @param locator
	 *            the datatype
	 * @param themes
	 *            the scope
	 * @return the duplicated occurrences or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IOccurrence getDuplette(InMemoryTopicMapStore store,
			ITopic topic, ITopic type, String value, ILocator locator,
			Collection<ITopic> themes) throws TopicMapStoreException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		/*
		 * get all names
		 */
		set.addAll(store.getCharacteristicsStore().getOccurrences(topic));
		/*
		 * filter by same type
		 */
		set.retainAll(store.getTypedStore().getTyped(type));
		/*
		 * filter by same scope
		 */
		IScope scope = store.getScopeStore().getScope(themes);
		set.retainAll(store.getScopeStore().getScoped(scope));

		/*
		 * filter by value
		 */
		for (IOccurrence o : set) {
			if (o.getValue().equals(value) && o.getDatatype().equals(locator)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Returning the duplicated association with the same type, roles and scope
	 * than the given one. All roles played by the source topic can be played by
	 * the target topic without changing the equality of associations.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the target topic
	 * @param other
	 *            the source topic
	 * @param association
	 *            the association
	 * @return the duplicated association or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IAssociation getDuplette(InMemoryTopicMapStore store,
			ITopic topic, ITopic other, IAssociation association)
			throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		/*
		 * get all played associations
		 */
		associations.addAll(store.doReadAssociation(topic));
		/*
		 * filter by type
		 */
		associations.retainAll(store.getTypedStore().getTyped(
				(ITopic) association.getType()));
		/*
		 * filter by scope
		 */
		associations.retainAll(store.getScopeStore().getScoped(
				association.getScopeObject()));

		/*
		 * iterate over all filtered associations
		 */
		for (IAssociation a : associations) {
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (IAssociationRole role : store.getAssociationStore()
					.getRoles(a)) {
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
					if (r.getPlayer().equals(role.getPlayer())
							|| (role.getPlayer().equals(topic) && r.getPlayer()
									.equals(other))) {
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
				return a;
			}
		}

		/*
		 * no duplicated association found
		 */
		return null;
	}

	/**
	 * Returning the duplicated association with the same type, roles and scope
	 * than the given one.
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association
	 * @return the duplicated association or <code>null</code>
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static IAssociation getDuplette(InMemoryTopicMapStore store,
			IAssociation association) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		/*
		 * get all played associations
		 */
		associations.addAll(store.getAssociationStore().getAssociations());
		/*
		 * filter by type
		 */
		associations.retainAll(store.getTypedStore().getTyped(
				(ITopic) association.getType()));
		/*
		 * filter by scope
		 */
		associations.retainAll(store.getScopeStore().getScoped(
				association.getScopeObject()));

		/*
		 * iterate over all filtered associations
		 */
		for (IAssociation a : associations) {
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (IAssociationRole role : store.getAssociationStore()
					.getRoles(a)) {
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
				return a;
			}
		}

		/*
		 * no duplicated association found
		 */
		return null;
	}

	/**
	 * Merging the given topics without creating duplicates.
	 * 
	 * @param store
	 *            the store
	 * @param topic
	 *            the target topic
	 * @param other
	 *            the source topic
	 * @param revision
	 *            the revision to store changes
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void doMerge(InMemoryTopicMapStore store, ITopic topic,
			ITopic other, IRevision revision) throws TopicMapStoreException {
		/*
		 * move names
		 */
		for (IName name : store.getCharacteristicsStore().getNames(other)) {
			/*
			 * check if name already contained by the other topic
			 */
			IName duplette = getDuplette(store, topic, (ITopic) name.getType(),
					name.getValue(), name.getScopeObject().getThemes());
			/*
			 * duplicated name found
			 */
			if (duplette != null) {
				/*
				 * copy variants
				 */
				for (IVariant v : store.getCharacteristicsStore().getVariants(
						name)) {
					/*
					 * check if variant already contained by the other name
					 */
					IVariant dup = getDuplette(store, duplette, v.getValue(),
							(ILocator) v.getDatatype(), v.getScopeObject()
									.getThemes());
					/*
					 * duplicated variant found
					 */
					if (dup != null) {
						/*
						 * merge them
						 */
						doMergeReifiable(store, dup, v, revision);
						/*
						 * copy all item identifiers
						 */
						for (Locator itemIdentifier : v.getItemIdentifiers()) {
							store.removeItemIdentifier(v,
									(ILocator) itemIdentifier, revision);
							store.modifyItemIdentifier(dup,
									(ILocator) itemIdentifier, revision);
						}
					}
					/*
					 * no duplicated variant found
					 */
					else {
						IVariant newVariant = store.createVariant(duplette, v
								.getValue(), (ILocator) v.getDatatype(), v
								.getScopeObject().getThemes(), revision);
						/*
						 * copy all item identifiers
						 */
						for (Locator itemIdentifier : v.getItemIdentifiers()) {
							store.removeItemIdentifier(v,
									(ILocator) itemIdentifier, revision);
							store.modifyItemIdentifier(newVariant,
									(ILocator) itemIdentifier, revision);
						}
						/*
						 * copy reifier
						 */
						ITopic reifier = store.doReadReification(v);
						if (reifier != null) {
							store.modifyReifier(v, null, revision);
							store.modifyReifier(newVariant, reifier, revision);
						}
					}
				}
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : name.getItemIdentifiers()) {
					store.removeItemIdentifier(name, (ILocator) itemIdentifier,
							revision);
					store.modifyItemIdentifier(duplette,
							(ILocator) itemIdentifier, revision);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, name, revision);
			}
			/*
			 * no duplicated name found
			 */
			else {
				IName newName = store.createName(topic,
						(ITopic) name.getType(), name.getValue(), name
								.getScopeObject().getThemes(), revision);
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : name.getItemIdentifiers()) {
					store.removeItemIdentifier(name, (ILocator) itemIdentifier,
							revision);
					store.modifyItemIdentifier(newName,
							(ILocator) itemIdentifier, revision);
				}
				/*
				 * copy reifier
				 */
				ITopic reifier = store.doReadReification(name);
				if (reifier != null) {
					store.modifyReifier(name, null, revision);
					store.modifyReifier(newName, reifier, revision);
				}
				/*
				 * copy variants
				 */
				for (IVariant v : store.getCharacteristicsStore().getVariants(
						name)) {
					IVariant newVariant = store.createVariant(newName, v
							.getValue(), (ILocator) v.getDatatype(), v
							.getScopeObject().getThemes(), revision);

					/*
					 * copy all item identifiers
					 */
					for (Locator itemIdentifier : v.getItemIdentifiers()) {
						store.removeItemIdentifier(name,
								(ILocator) itemIdentifier, revision);
						store.modifyItemIdentifier(newVariant,
								(ILocator) itemIdentifier, revision);
					}
					/*
					 * copy reifier
					 */
					reifier = store.doReadReification(v);
					if (reifier != null) {
						store.modifyReifier(v, null, revision);
						store.modifyReifier(newVariant, reifier, revision);
					}
				}
			}
		}

		/*
		 * move occurrences
		 */
		for (IOccurrence occurrence : store.getCharacteristicsStore()
				.getOccurrences(other)) {
			/*
			 * check if occurrence already contained by the other topic
			 */
			IOccurrence duplette = getDuplette(store, topic,
					(ITopic) occurrence.getType(), occurrence.getValue(),
					(ILocator) occurrence.getDatatype(), occurrence
							.getScopeObject().getThemes());
			/*
			 * duplicated occurrence found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : occurrence.getItemIdentifiers()) {
					store.removeItemIdentifier(occurrence,
							(ILocator) itemIdentifier, revision);
					store.modifyItemIdentifier(duplette,
							(ILocator) itemIdentifier, revision);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, occurrence, revision);
			}
			/*
			 * no duplicated occurrence found
			 */
			else {
				IOccurrence newOccurrence = store.createOccurrence(topic,
						(ITopic) occurrence.getType(), occurrence.getValue(),
						(ILocator) occurrence.getDatatype(), occurrence
								.getScopeObject().getThemes(), revision);
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : occurrence.getItemIdentifiers()) {
					store.removeItemIdentifier(occurrence,
							(ILocator) itemIdentifier, revision);
					store.modifyItemIdentifier(newOccurrence,
							(ILocator) itemIdentifier, revision);
				}
				/*
				 * copy reifier
				 */
				ITopic reifier = store.doReadReification(occurrence);
				if (reifier != null) {
					store.modifyReifier(occurrence, null, revision);
					store.modifyReifier(newOccurrence, reifier, revision);
				}
			}
		}

		/*
		 * move associations played
		 */
		for (IAssociation association : store.doReadAssociation(other)) {
			/*
			 * check if association is already played by the other topic
			 */
			IAssociation duplette = getDuplette(store, topic, other,
					association);
			/*
			 * duplicated association found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (Locator itemIdentifier : association.getItemIdentifiers()) {
					store.removeItemIdentifier(association,
							(ILocator) itemIdentifier, revision);
					store.modifyItemIdentifier(duplette,
							(ILocator) itemIdentifier, revision);
				}
				/*
				 * merge them
				 */
				doMergeReifiable(store, duplette, association, revision);

			}
			/*
			 * no duplicated association found
			 */
			else {
				for (IAssociationRole role : store.doReadRoles(association)) {
					if (role.getPlayer().equals(other)) {
						store.modifyPlayer(role, topic, revision);
					}
				}
			}
		}
		/*
		 * replace old topic
		 */
		store.getTopicTypeStore().replace(other, topic, revision);
		store.getTypedStore().replace(other, topic, revision);
		store.getScopeStore().replace(other, topic, revision);
		store.getReificationStore().replace(other, topic, revision);
		store.getAssociationStore().replace(other, topic, revision);
		store.getCharacteristicsStore().replace(other, topic, revision);
		store.getIdentityStore().replace(other, topic, revision);
		store.removeTopic(other, true, revision);
	}

	/**
	 * Handling reification after merging the given constructs. If both are
	 * reified, the reifier topics are merged. Otherwise the existing reifier
	 * are moved to the target construct.
	 * 
	 * @param store
	 *            the store
	 * @param reifiable
	 *            the target construct
	 * @param other
	 *            the source construct
	 * @param revision
	 *            the revision to store changes
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void doMergeReifiable(InMemoryTopicMapStore store,
			IReifiable reifiable, IReifiable other, IRevision revision)
			throws TopicMapStoreException {
		ITopic reifierOfOther = store.getReificationStore().getReifier(other);
		if (reifierOfOther != null) {
			ITopic reifier = store.getReificationStore().getReifier(reifiable);
			/*
			 * other construct has no reifier
			 */
			if (reifier == null) {
				/*
				 * move reifier
				 */
				store.modifyReifier(other, null, revision);
				store.modifyReifier(reifiable, reifierOfOther, revision);
			}
			/*
			 * both constructs are reified
			 */
			else {
				store.modifyReifier(other, null, revision);
				/*
				 * merge both topics
				 */
				ITopic newReifier = store.createTopic(store.getTopicMap(),
						revision);
				doMerge(store, newReifier, reifier, revision);
				doMerge(store, newReifier, reifierOfOther, revision);
				((InMemoryIdentity) ((TopicImpl) reifier).getIdentity())
						.setId(newReifier.longId());
				((InMemoryIdentity) ((TopicImpl) reifierOfOther).getIdentity())
						.setId(newReifier.longId());
				store.modifyReifier(other, null, null);
			}
		}
	}

	/**
	 * Merging all information items of the second topic map into the first
	 * topic map, without duplicates
	 * 
	 * @param store
	 *            the store
	 * @param topicMap
	 *            the target topic map
	 * @param other
	 *            the source topic map
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void doMergeTopicMaps(InMemoryTopicMapStore store,
			ITopicMap topicMap, TopicMap other) throws TopicMapStoreException {
		/*
		 * create revision
		 */
		IRevision revision = store.createRevision(TopicMapEventType.MERGE);
		/*
		 * copy identifies
		 */
		for (Topic topic : other.getTopics()) {
			ITopic duplette = getDuplette(store, topic);
			if (duplette == null) {
				duplette = store.createTopic(topicMap, revision);
			}

			for (Locator loc : topic.getItemIdentifiers()) {
				if (!duplette.getItemIdentifiers().contains(loc)) {
					store.modifyItemIdentifier(
							duplette,
							store.doCreateLocator(topicMap, loc.getReference()),
							revision);
				}
			}

			for (Locator loc : topic.getSubjectIdentifiers()) {
				if (!duplette.getSubjectIdentifiers().contains(loc)) {
					store.modifySubjectIdentifier(
							duplette,
							store.doCreateLocator(topicMap, loc.getReference()),
							revision);
				}
			}
			for (Locator loc : topic.getSubjectLocators()) {
				if (!duplette.getSubjectLocators().contains(loc)) {
					store.modifySubjectLocator(
							duplette,
							store.doCreateLocator(topicMap, loc.getReference()),
							revision);
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
				store.modifyTopicType(duplette, t, revision);
			}

			/*
			 * copy super types
			 */
			if (topic instanceof ITopic) {
				for (Topic type : ((ITopic) topic).getSupertypes()) {
					ITopic t = getDuplette(store, type);
					store.modifySupertype(duplette, t, revision);
				}
			}

			/*
			 * copy occurrences
			 */
			for (Occurrence occ : topic.getOccurrences()) {
				ITopic type = getDuplette(store, occ.getType());
				IScope scope = getCorrespondingScope(store, occ.getScope());
				ILocator datatype = store.getIdentityStore().createLocator(
						occ.getDatatype().getReference());
				IOccurrence occurrence = getDuplette(store, duplette, type,
						occ.getValue(), datatype, scope.getThemes());
				if (occurrence == null) {
					occurrence = store.createOccurrence(duplette, type, occ
							.getValue(), store.getIdentityStore()
							.createLocator(occ.getDatatype().getReference()),
							getCorrespondingScope(store, occ.getScope())
									.getThemes(), revision);
				}
				/*
				 * copy item-identifiers of the occurrence
				 */
				for (Locator loc : occ.getItemIdentifiers()) {
					store.modifyItemIdentifier(
							occurrence,
							store.getIdentityStore().createLocator(
									loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				Topic reifier = occ.getReifier();
				if (reifier != null) {
					store.modifyReifier(occurrence,
							getDuplette(store, reifier), revision);
				}

			}

			/*
			 * copy names
			 */
			for (Name name : topic.getNames()) {
				ITopic type = getDuplette(store, name.getType());
				IScope scope = getCorrespondingScope(store, name.getScope());
				IName n = getDuplette(store, duplette, type, name.getValue(),
						scope.getThemes());
				if (n == null) {
					n = store.createName(duplette, type, name.getValue(),
							scope.getThemes(), revision);
				}

				/*
				 * copy item-identifiers of the name
				 */
				for (Locator loc : name.getItemIdentifiers()) {
					store.modifyItemIdentifier(n, store.getIdentityStore()
							.createLocator(loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				Topic reifier = name.getReifier();
				if (reifier != null) {
					store.modifyReifier(n, getDuplette(store, reifier),
							revision);
				}

				/*
				 * copy variants
				 */
				for (Variant v : name.getVariants()) {
					scope = getCorrespondingScope(store, v.getScope());
					ILocator datatype = store.getIdentityStore().createLocator(
							v.getDatatype().getReference());
					IVariant variant = getDuplette(store, n, v.getValue(),
							datatype, scope.getThemes());
					if (variant == null) {
						variant = store.createVariant(
								n,
								v.getValue(),
								store.getIdentityStore().createLocator(
										v.getDatatype().getReference()),
								scope.getThemes(), revision);
					}

					/*
					 * copy item-identifiers of the variant
					 */
					for (Locator loc : v.getItemIdentifiers()) {
						store.modifyItemIdentifier(
								variant,
								store.getIdentityStore().createLocator(
										loc.getReference()), revision);
					}

					/*
					 * copy reification
					 */
					reifier = v.getReifier();
					if (reifier != null) {
						store.modifyReifier(variant,
								getDuplette(store, reifier), revision);
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
			IAssociation association = store.createAssociation(topicMap, type,
					getCorrespondingScope(store, ass.getScope()).getThemes(),
					revision);

			/*
			 * copy item-identifiers of the association
			 */
			for (Locator loc : ass.getItemIdentifiers()) {
				store.modifyItemIdentifier(association, store
						.getIdentityStore().createLocator(loc.getReference()),
						revision);
			}

			/*
			 * copy reification
			 */
			Topic reifier = ass.getReifier();
			if (reifier != null) {
				store.modifyReifier(association, getDuplette(store, reifier),
						revision);
			}

			/*
			 * copy roles
			 */
			for (Role r : ass.getRoles()) {
				type = getDuplette(store, r.getType());
				ITopic player = getDuplette(store, r.getPlayer());
				IAssociationRole role = store.createRole(association, type,
						player, revision);
				/*
				 * copy item-identifiers of the role
				 */
				for (Locator loc : r.getItemIdentifiers()) {
					store.modifyItemIdentifier(role, store.getIdentityStore()
							.createLocator(loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				reifier = r.getReifier();
				if (reifier != null) {
					store.modifyReifier(role, getDuplette(store, reifier),
							revision);
				}
			}
		}
	}

	/**
	 * Returns the IScope object representing all the given themes in the given
	 * topic map store.
	 * 
	 * @param store
	 *            the store.
	 * @param themes
	 *            the themes
	 * @return the scope object and never <code>null</code>
	 */
	public static IScope getCorrespondingScope(InMemoryTopicMapStore store,
			final Set<Topic> themes) {
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : themes) {
			ITopic theme = getDuplette(store, t);
			set.add(theme);
		}

		return store.getScopeStore().getScope(set);
	}

	/**
	 * Method checks if the given association is a TMDM type-instance or
	 * supertype-subtype association
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association to check
	 * @param topicMap
	 *            the topic map
	 * @param other
	 *            the other topic map
	 * @return <code>true</code> if the associations is a TMDM type-instance or
	 *         supertype-subtype association, <code>false</code> otherwise.
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private static boolean checkTmdmAssociation(InMemoryTopicMapStore store,
			Association association, ITopicMap topicMap, TopicMap other)
			throws TopicMapStoreException {
		Locator typeInstanceLocator = topicMap
				.createLocator(TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		Locator supertypeSubtypeLocator = topicMap
				.createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);

		/*
		 * is tmdm:supertype-subtype-association
		 */
		if (association.getType().getSubjectIdentifiers()
				.contains(supertypeSubtypeLocator)) {
			/*
			 * get role-types of TMDM association
			 */
			Topic supertypeRole = other
					.getTopicBySubjectIdentifier(other
							.createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE));
			Topic subtypeRole = other
					.getTopicBySubjectIdentifier(other
							.createLocator(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE));
			/*
			 * TMDM restricts that role-types has to exist if the association
			 * exists
			 */
			if (supertypeRole == null || subtypeRole == null) {
				throw new TopicMapStoreException(
						"Invalid supertype-subtype-association, missing at least one role-type");
			}
			/*
			 * get equivalent players contained by the topic map, information
			 * merged in
			 */
			ITopic supertype = getDuplette(store,
					association.getRoles(supertypeRole).iterator().next()
							.getPlayer());
			ITopic subtype = getDuplette(store,
					association.getRoles(subtypeRole).iterator().next()
							.getPlayer());
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
		} else if (association.getType().getSubjectIdentifiers()
				.contains(typeInstanceLocator)) {
			/*
			 * get role-types of TMDM association
			 */
			Topic typeRole = other.getTopicBySubjectIdentifier(other
					.createLocator(TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE));
			Topic instanceRole = other
					.getTopicBySubjectIdentifier(other
							.createLocator(TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE));
			/*
			 * TMDM restricts that role-types has to exist if the association
			 * exists
			 */
			if (typeRole == null || instanceRole == null) {
				throw new TopicMapStoreException(
						"Invalid type-instance-association, missing at least one role-type");
			}
			/*
			 * get equivalent players contained by the topic map, information
			 * merged in
			 */
			ITopic type = getDuplette(store, association.getRoles(typeRole)
					.iterator().next().getPlayer());
			ITopic instance = getDuplette(store,
					association.getRoles(instanceRole).iterator().next()
							.getPlayer());
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

	public static void removeDuplicates(final InMemoryTopicMapStore store,	final ITopic topic, boolean handleAssociations) throws TopicMapStoreException {
		
		final IRevision revision = store.createRevision(TopicMapEventType.REMOVE_DUPLICATES);
		Set<Construct> removed = HashUtil.getHashSet();
		
		/*
		 * check name duplicates
		 */
		for (IName name : store.doReadNames(topic)) {
			if (removed.contains(name)) {
				continue;
			}
			for (IName duplicate : store.doReadNames(topic)) {
				if (duplicate.equals(name) || removed.contains(duplicate)) {
					continue;
				}
				/*
				 * names are equal if the value, the type and scope property
				 * are equal
				 */
				if (duplicate.getType().equals(name.getType())
						&& store.doReadValue(duplicate).equals(store.doReadValue(name))
						&& duplicate.getScopeObject().equals(
								name.getScopeObject())) {
					/*
					 * copy item-identifier
					 */
					for (Locator ii : duplicate.getItemIdentifiers()) {
						store.removeItemIdentifier(duplicate,
								(ILocator) ii, revision);
						store.modifyItemIdentifier(name,
								(ILocator) ii, revision);
					}
					/*
					 * copy variants
					 */
					for (Variant v : duplicate.getVariants()) {
						Variant copy = getDuplette(store, name,
								v.getValue(), (ILocator) v.getDatatype(),
								((IVariant) v).getScopeObject().getThemes());
						if (copy == null) {
							copy = store.createVariant(name, v
									.getValue(),
									(ILocator) v.getDatatype(),
									((IVariant) v).getScopeObject()
											.getThemes(), revision);
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : v.getItemIdentifiers()) {
							store.removeItemIdentifier((IVariant) v,
									(ILocator) ii, revision);
							store.modifyItemIdentifier((IVariant) copy,
									(ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IVariant) v,
								(IVariant) copy, revision);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, name,
							duplicate, revision);
					/*
					 * remove duplicate
					 */
					store.removeName(duplicate, true, revision);
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
				for (IVariant dup : MergeUtils.getDuplettes(store,
						name, v.getValue(), (ILocator) v
								.getDatatype(), ((IVariant) v)
								.getScopeObject().getThemes())) {
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
					ITopic reifier = (ITopic) dup.getReifier();
					ITopic otherReifier = (ITopic) v.getReifier();
					if (reifier != null) {
						dup.setReifier(null);
						if (otherReifier != null) {
							doMerge(store, otherReifier, reifier, revision);
						} else {
							v.setReifier(reifier);
						}
					}
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
				if (duplicate.equals(occurrence)
						|| removed.contains(duplicate)) {
					continue;
				}
				/*
				 * occurrences are equal if the value, datatype, the type
				 * and scope property are equal
				 */
				if (duplicate.getType().equals(occurrence.getType())
						&& duplicate.getValue()
								.equals(occurrence.getValue())
						&& ((IOccurrence) occurrence).getScopeObject()
								.equals(((IOccurrence) duplicate)
										.getScopeObject())
						&& occurrence.getDatatype().equals(
								duplicate.getDatatype())) {
					/*
					 * copy item-identifier
					 */
					for (Locator ii : duplicate.getItemIdentifiers()) {
						store.removeItemIdentifier((IOccurrence) duplicate,
								(ILocator) ii, revision);
						store.modifyItemIdentifier(
								(IOccurrence) occurrence, (ILocator) ii,
								revision);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, (IOccurrence) occurrence,
							(IOccurrence) duplicate, revision);
					/*
					 * remove duplicate
					 */
					store.removeOccurrence((IOccurrence) duplicate, true,
							revision);
					removed.add(duplicate);
				}
			}
		}
		
		removed.clear();
		
		
		if(!handleAssociations)
			return;
		
		/*
		 * check associations
		 */
		
		for (final Association association : topic.getAssociationsPlayed()) {
			if (removed.contains(association)) {
				continue;
			}
			for (IAssociation duplicate : MergeUtils.getDuplettes2(topic, store, (IAssociation) association)) {
				if (duplicate.equals(association)
						|| removed.contains(duplicate)) {
					continue;
				}
				/*
				 * copy item-identifier
				 */
				for (Locator ii : duplicate.getItemIdentifiers()) {
					store.removeItemIdentifier(duplicate, (ILocator) ii,
							revision);
					store.modifyItemIdentifier((IAssociation) association,
							(ILocator) ii, revision);
				}
				/*
				 * check roles
				 */
				for (Role r : association.getRoles()) {
					for (IAssociationRole dup : MergeUtils.getDuplettes(
							duplicate, (IAssociationRole) r)) {
						if (removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : dup.getItemIdentifiers()) {
							store.removeItemIdentifier(dup, (ILocator) ii,
									revision);
							store.modifyItemIdentifier((IAssociationRole) r,
									(ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IAssociationRole) r, dup,
								revision);
					}
				}
				/*
				 * check reification
				 */
				doMergeReifiable(store, (IAssociation) association, duplicate,
						revision);
				/*
				 * remove duplicate
				 */
				store.removeAssociation(duplicate, true, revision);
				removed.add(duplicate);
			}
			/*
			 * check roles
			 */
			for (Role r : association.getRoles()) {
				if (removed.contains(r)) {
					continue;
				}
				for (IAssociationRole dup : MergeUtils.getDuplettes(
						(IAssociation) association, (IAssociationRole) r)) {
					if (dup.equals(r) || removed.contains(dup)) {
						continue;
					}
					/*
					 * copy item-identifier
					 */
					for (Locator ii : dup.getItemIdentifiers()) {
						store.removeItemIdentifier(dup, (ILocator) ii, revision);
						store.modifyItemIdentifier((IAssociationRole) r,
								(ILocator) ii, revision);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, (IAssociationRole) r, dup, revision);
					/*
					 * remove duplicate
					 */
					removed.add(dup);
					store.removeRole(dup, false, revision);
				}
			}
		}

		
	}
	
	// implementation on topic base
	public static void removeDuplicates2(final InMemoryTopicMapStore store,	final ITopicMap topicMap) throws TopicMapStoreException {
		
		final IRevision revision = store.createRevision(TopicMapEventType.REMOVE_DUPLICATES);
		Set<Construct> removed = HashUtil.getHashSet();
		
		for(final ITopic topic : store.doReadTopics(topicMap)) {
			removeDuplicates(store, topic, true);
		}
		
	}
	
	/**
	 * Method removes all duplicates from the given topic map.
	 * 
	 * @param store
	 *            the topic map store
	 * @param topicMap
	 *            the topic map itself
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void removeDuplicates(final InMemoryTopicMapStore store,
			final ITopicMap topicMap) throws TopicMapStoreException {
		final IRevision revision = store.createRevision(TopicMapEventType.REMOVE_DUPLICATES);

		for (final ITopic topic : store.doReadTopics(topicMap)) {
			Set<Construct> removed = HashUtil.getHashSet();
			
			/*
			 * check name duplicates
			 */
			for (IName name : store.doReadNames(topic)) {
				if (removed.contains(name)) {
					continue;
				}
				for (IName duplicate : store.doReadNames(topic)) {
					if (duplicate.equals(name) || removed.contains(duplicate)) {
						continue;
					}
					/*
					 * names are equal if the value, the type and scope property
					 * are equal
					 */
					if (duplicate.getType().equals(name.getType())
							&& store.doReadValue(duplicate).equals(store.doReadValue(name))
							&& duplicate.getScopeObject().equals(
									name.getScopeObject())) {
						/*
						 * copy item-identifier
						 */
						for (Locator ii : duplicate.getItemIdentifiers()) {
							store.removeItemIdentifier(duplicate,
									(ILocator) ii, revision);
							store.modifyItemIdentifier(name,
									(ILocator) ii, revision);
						}
						/*
						 * copy variants
						 */
						for (Variant v : duplicate.getVariants()) {
							Variant copy = getDuplette(store, name,
									v.getValue(), (ILocator) v.getDatatype(),
									((IVariant) v).getScopeObject().getThemes());
							if (copy == null) {
								copy = store.createVariant(name, v
										.getValue(),
										(ILocator) v.getDatatype(),
										((IVariant) v).getScopeObject()
												.getThemes(), revision);
							}
							/*
							 * copy item-identifier
							 */
							for (Locator ii : v.getItemIdentifiers()) {
								store.removeItemIdentifier((IVariant) v,
										(ILocator) ii, revision);
								store.modifyItemIdentifier((IVariant) copy,
										(ILocator) ii, revision);
							}
							/*
							 * check reification
							 */
							doMergeReifiable(store, (IVariant) v,
									(IVariant) copy, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, name,
								duplicate, revision);
						/*
						 * remove duplicate
						 */
						store.removeName(duplicate, true, revision);
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
					for (IVariant dup : MergeUtils.getDuplettes(store,
							name, v.getValue(), (ILocator) v
									.getDatatype(), ((IVariant) v)
									.getScopeObject().getThemes())) {
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
						ITopic reifier = (ITopic) dup.getReifier();
						ITopic otherReifier = (ITopic) v.getReifier();
						if (reifier != null) {
							dup.setReifier(null);
							if (otherReifier != null) {
								doMerge(store, otherReifier, reifier, revision);
							} else {
								v.setReifier(reifier);
							}
						}
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
					if (duplicate.equals(occurrence)
							|| removed.contains(duplicate)) {
						continue;
					}
					/*
					 * occurrences are equal if the value, datatype, the type
					 * and scope property are equal
					 */
					if (duplicate.getType().equals(occurrence.getType())
							&& duplicate.getValue()
									.equals(occurrence.getValue())
							&& ((IOccurrence) occurrence).getScopeObject()
									.equals(((IOccurrence) duplicate)
											.getScopeObject())
							&& occurrence.getDatatype().equals(
									duplicate.getDatatype())) {
						/*
						 * copy item-identifier
						 */
						for (Locator ii : duplicate.getItemIdentifiers()) {
							store.removeItemIdentifier((IOccurrence) duplicate,
									(ILocator) ii, revision);
							store.modifyItemIdentifier(
									(IOccurrence) occurrence, (ILocator) ii,
									revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IOccurrence) occurrence,
								(IOccurrence) duplicate, revision);
						/*
						 * remove duplicate
						 */
						store.removeOccurrence((IOccurrence) duplicate, true,
								revision);
						removed.add(duplicate);
					}
				}
			}
		}
		Set<Construct> removed = HashUtil.getHashSet();
		/*
		 * check associations
		 */
		for (final Association association : topicMap.getAssociations()) {
			if (removed.contains(association)) {
				continue;
			}
			for (IAssociation duplicate : MergeUtils.getDuplettes(store,
					(IAssociation) association)) {
				if (duplicate.equals(association)
						|| removed.contains(duplicate)) {
					continue;
				}
				/*
				 * copy item-identifier
				 */
				for (Locator ii : duplicate.getItemIdentifiers()) {
					store.removeItemIdentifier(duplicate, (ILocator) ii,
							revision);
					store.modifyItemIdentifier((IAssociation) association,
							(ILocator) ii, revision);
				}
				/*
				 * check roles
				 */
				for (Role r : association.getRoles()) {
					for (IAssociationRole dup : MergeUtils.getDuplettes(
							duplicate, (IAssociationRole) r)) {
						if (removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : dup.getItemIdentifiers()) {
							store.removeItemIdentifier(dup, (ILocator) ii,
									revision);
							store.modifyItemIdentifier((IAssociationRole) r,
									(ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, (IAssociationRole) r, dup,
								revision);
					}
				}
				/*
				 * check reification
				 */
				doMergeReifiable(store, (IAssociation) association, duplicate,
						revision);
				/*
				 * remove duplicate
				 */
				store.removeAssociation(duplicate, true, revision);
				removed.add(duplicate);
			}
			/*
			 * check roles
			 */
			for (Role r : association.getRoles()) {
				if (removed.contains(r)) {
					continue;
				}
				for (IAssociationRole dup : MergeUtils.getDuplettes(
						(IAssociation) association, (IAssociationRole) r)) {
					if (dup.equals(r) || removed.contains(dup)) {
						continue;
					}
					/*
					 * copy item-identifier
					 */
					for (Locator ii : dup.getItemIdentifiers()) {
						store.removeItemIdentifier(dup, (ILocator) ii, revision);
						store.modifyItemIdentifier((IAssociationRole) r,
								(ILocator) ii, revision);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, (IAssociationRole) r, dup, revision);
					/*
					 * remove duplicate
					 */
					removed.add(dup);
					store.removeRole(dup, false, revision);
				}
			}
		}
	}
	
}
