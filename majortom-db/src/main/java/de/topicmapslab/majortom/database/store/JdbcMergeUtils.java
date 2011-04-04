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
package de.topicmapslab.majortom.database.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import de.topicmapslab.majortom.database.store.TopicRef.Type;
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
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.store.NameMergeCandidate;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Utility class for merging process.
 * 
 * @author Sven Krosse
 * 
 */
public class JdbcMergeUtils {

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
	public static NameMergeCandidate detectMergeByNameCandidate(JdbcTopicMapStore store, ITopic topic, ITopic nameType, String value, Collection<ITopic> themes) {
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
	 * @param references
	 *            the references of the topic map
	 * @return the found topic or <code>null</code>
	 * @throws TopicMapStoreException
	 */
	public static ITopic getDuplette(JdbcTopicMapStore store, Topic topic, Map<TopicRef, ITopic> references) throws TopicMapStoreException {
		for (Locator locator : topic.getItemIdentifiers()) {
			/*
			 * no references given check topic map
			 */
			if (references == null) {
				ILocator loc = store.doCreateLocator(store.getTopicMap(), locator.getReference());
				IConstruct duplette = store.doReadConstruct(store.getTopicMap(), loc);
				if (duplette != null) {
					if (duplette instanceof ITopic) {
						return (ITopic) duplette;
					}
					throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
				}
				duplette = store.doReadTopicBySubjectIdentifier(store.getTopicMap(), loc);
				if (duplette != null) {
					return (ITopic) duplette;
				}
			}
			/*
			 * check references
			 */
			else {
				ITopic duplette = references.get(new TopicRef(locator.getReference(), Type.ITEM_IDENTIFIER));
				if (duplette == null) {
					throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
				}
				return duplette;
			}
		}

		for (Locator locator : topic.getSubjectIdentifiers()) {
			/*
			 * no references given check topic map
			 */
			if (references == null) {
				ILocator loc = store.doCreateLocator(store.getTopicMap(), locator.getReference());
				IConstruct duplette = store.doReadConstruct(store.getTopicMap(), loc);
				if (duplette != null) {
					if (duplette instanceof ITopic) {
						return (ITopic) duplette;
					}
					throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
				}
				duplette = store.doReadTopicBySubjectIdentifier(store.getTopicMap(), loc);
				if (duplette != null) {
					return (ITopic) duplette;
				}
			}
			/*
			 * check references
			 */
			else {
				ITopic duplette = references.get(new TopicRef(locator.getReference(), Type.SUBJECT_IDENTIFIER));
				if (duplette == null) {
					throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
				}
				return duplette;
			}
		}

		for (Locator locator : topic.getSubjectLocators()) {
			/*
			 * no references given check topic map
			 */
			if (references == null) {
				ILocator loc = store.doCreateLocator(store.getTopicMap(), locator.getReference());
				ITopic duplette = store.doReadTopicBySubjectLocator(store.getTopicMap(), loc);
				if (duplette != null) {
					return duplette;
				}
			}
			/*
			 * check references
			 */
			else {
				ITopic duplette = references.get(new TopicRef(locator.getReference(), Type.SUBJECT_LOCATOR));
				if (duplette == null) {
					throw new IdentityConstraintException(topic, duplette, locator, "Unresolveable identifier conflicts.");
				}
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
	public static IName getDuplette(JdbcTopicMapStore store, ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = store.doCreateScope(store.getTopicMap(), themes);
		/*
		 * check for name construct
		 */
		for (IName n : store.doReadNames(topic, type, scope)) {
			if (n.getValue().equals(value)) {
				return n;
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
	public static IVariant getDuplette(JdbcTopicMapStore store, IName name, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = store.doCreateScope(store.getTopicMap(), themes);
		/*
		 * check for variant value
		 */
		for (IVariant v : store.doReadVariants(name, scope)) {
			if (v.getValue().equals(value) && v.getDatatype().equals(locator)) {
				return v;
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
	public static IOccurrence getDuplette(JdbcTopicMapStore store, ITopic topic, ITopic type, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		/*
		 * get scope as filter
		 */
		IScope scope = store.doCreateScope(store.getTopicMap(), themes);
		/*
		 * check for occurrence value
		 */
		for (IOccurrence o : store.doReadOccurrences(topic, type, scope)) {
			if (o.getValue().equals(value) && o.getDatatype().equals(locator)) {
				return o;
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
	public static IAssociation getDuplette(JdbcTopicMapStore store, ITopic topic, ITopic other, IAssociation association) throws TopicMapStoreException {
		ITopic type = store.doReadType(association);
		IScope scope = store.doReadScope(association);
		/*
		 * iterate over all associations
		 */
		for (IAssociation a : store.doReadAssociation(store.getTopicMap(), type, scope)) {
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (IAssociationRole role : store.doReadRoles(a)) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<IAssociationRole> roles = store.doReadRoles(association, store.doReadType(role));
				/*
				 * look for duplicated role
				 */
				for (IAssociationRole r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					ITopic player = store.doReadPlayer(role);
					ITopic otherPlayer = store.doReadPlayer(r);
					if (otherPlayer.equals(player) || (player.equals(topic) && otherPlayer.equals(other))) {
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
	public static IAssociation getDuplette(JdbcTopicMapStore store, IAssociation association, Set<IAssociation> excluded) throws TopicMapStoreException {
		ITopic type = store.doReadType(association);
		IScope scope = store.doReadScope(association);
		/*
		 * iterate over all filtered associations
		 */
		for (IAssociation a : store.doReadAssociation(store.getTopicMap(), type, scope)) {
			if (a.equals(association) || excluded.contains(a)) {
				continue;
			}
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (IAssociationRole role : store.doReadRoles(a)) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<IAssociationRole> roles = store.doReadRoles(association, store.doReadType(role));
				/*
				 * look for duplicated role
				 */
				for (IAssociationRole r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (store.doReadPlayer(r).equals(store.doReadPlayer(role))) {
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
	public static void doMerge(JdbcTopicMapStore store, ITopic topic, ITopic other, IRevision revision) throws TopicMapStoreException {
		/*
		 * move names
		 */
		for (IName name : store.doReadNames(other)) {
			/*
			 * check if name already contained by the other topic
			 */
			IName duplette = getDuplette(store, topic, store.doReadType(name), store.doReadValue(name).toString(), store.doReadScope(name).getThemes());
			/*
			 * duplicated name found
			 */
			if (duplette != null) {
				/*
				 * copy variants
				 */
				for (IVariant v : store.doReadVariants(name)) {

					/*
					 * check if variant already contained by the other name
					 */
					IVariant dup = getDuplette(store, duplette, store.doReadValue(v).toString(), store.doReadDataType(v), store.doReadScope(v).getThemes());
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
						for (ILocator itemIdentifier : store.doReadItemIdentifiers(v)) {
							store.doRemoveItemIdentifier(v, itemIdentifier);
							store.doModifyItemIdentifier(dup, itemIdentifier);
						}
					}
					/*
					 * no duplicated variant found
					 */
					else {
						IVariant newVariant = store.doCreateVariant(duplette, store.doReadValue(v).toString(), store.doReadDataType(v), store.doReadScope(v).getThemes());
						/*
						 * copy all item identifiers
						 */
						for (ILocator itemIdentifier : store.doReadItemIdentifiers(v)) {
							store.doRemoveItemIdentifier(v, itemIdentifier);
							store.doModifyItemIdentifier(newVariant, itemIdentifier);
						}
						/*
						 * copy reifier
						 */
						ITopic reifier = store.doReadReification(v);
						if (reifier != null) {
							store.doModifyReifier(v, null);
							store.doModifyReifier(newVariant, reifier);
						}
					}
				}
				/*
				 * copy all item identifiers
				 */
				for (ILocator itemIdentifier : store.doReadItemIdentifiers(name)) {
					store.doRemoveItemIdentifier(name, itemIdentifier);
					store.doModifyItemIdentifier(duplette, itemIdentifier);
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
				IName newName = store.doCreateName(topic, store.doReadType(name), store.doReadValue(name).toString(), store.doReadScope(name).getThemes());
				/*
				 * copy all item identifiers
				 */
				for (ILocator itemIdentifier : store.doReadItemIdentifiers(name)) {
					store.doRemoveItemIdentifier(name, itemIdentifier);
					store.doModifyItemIdentifier(newName, itemIdentifier);
				}
				/*
				 * copy reifier
				 */
				ITopic reifier = store.doReadReification(name);
				if (reifier != null) {
					store.doModifyReifier(name, null);
					store.doModifyReifier(newName, reifier);
				}
				/*
				 * copy variants
				 */
				for (IVariant v : store.doReadVariants(name)) {
					IVariant newVariant = store.doCreateVariant(duplette, store.doReadValue(v).toString(), store.doReadDataType(v), store.doReadScope(v).getThemes());
					/*
					 * copy all item identifiers
					 */
					for (ILocator itemIdentifier : store.doReadItemIdentifiers(v)) {
						store.doRemoveItemIdentifier(v, itemIdentifier);
						store.doModifyItemIdentifier(newVariant, itemIdentifier);
					}
					/*
					 * copy reifier
					 */
					reifier = store.doReadReification(v);
					if (reifier != null) {
						store.doModifyReifier(v, null);
						store.doModifyReifier(newVariant, reifier);
					}
				}
			}
		}

		/*
		 * move occurrences
		 */
		for (IOccurrence occurrence : store.doReadOccurrences(other)) {
			ITopic type = store.doReadType(occurrence);
			Object value = store.doReadValue(occurrence);
			ILocator datatype = store.doReadDataType(occurrence);
			IScope scope = store.doReadScope(occurrence);
			/*
			 * check if occurrence already contained by the other topic
			 */
			IOccurrence duplette = getDuplette(store, topic, type, value.toString(), datatype, scope.getThemes());
			/*
			 * duplicated occurrence found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (ILocator itemIdentifier : store.doReadItemIdentifiers(occurrence)) {
					store.doRemoveItemIdentifier(occurrence, itemIdentifier);
					store.doModifyItemIdentifier(duplette, itemIdentifier);
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
				IOccurrence newOccurrence = store.doCreateOccurrence(topic, type, value.toString(), datatype, scope.getThemes());
				/*
				 * copy all item identifiers
				 */
				for (ILocator itemIdentifier : store.doReadItemIdentifiers(occurrence)) {
					store.doRemoveItemIdentifier(occurrence, itemIdentifier);
					store.doModifyItemIdentifier(newOccurrence, itemIdentifier);
				}
				/*
				 * copy reifier
				 */
				ITopic reifier = store.doReadReification(occurrence);
				if (reifier != null) {
					store.doModifyReifier(occurrence, null);
					store.doModifyReifier(newOccurrence, reifier);
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
			IAssociation duplette = getDuplette(store, topic, other, association);
			/*
			 * duplicated association found
			 */
			if (duplette != null) {
				/*
				 * copy all item identifiers
				 */
				for (ILocator itemIdentifier : store.doReadItemIdentifiers(association)) {
					store.doRemoveItemIdentifier(association, itemIdentifier);
					store.doModifyItemIdentifier(duplette, itemIdentifier);
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
					if (store.doReadPlayer(role).equals(other)) {
						store.doModifyPlayer(role, topic);
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
		IReifiable reifiable = store.doReadReification(other);
		if (reifiable != null) {
			store.doModifyReifier(reifiable, null);
			store.doModifyReifier(reifiable, topic);
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
	public static void replaceIdentity(JdbcTopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		/*
		 * item-identifier
		 */
		for (ILocator itemIdentifier : store.doReadItemIdentifiers(other)) {
			store.doRemoveItemIdentifier(other, itemIdentifier);
			store.doModifyItemIdentifier(topic, itemIdentifier);
		}
		/*
		 * subject-identifier
		 */
		for (ILocator loc : store.doReadSubjectIdentifiers(other)) {
			store.doRemoveSubjectIdentifier(other, loc);
			store.doModifySubjectIdentifier(topic, loc);
		}
		/*
		 * subject-locator
		 */
		for (ILocator loc : store.doReadSubjectLocators(other)) {
			store.doRemoveSubjectLocator(other, loc);
			store.doModifySubjectLocator(topic, loc);
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
	public static void replaceAsTypeOrSupertype(JdbcTopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		ITypeInstanceIndex typeIndex = store.getIndex(ITypeInstanceIndex.class);
		if (!typeIndex.isOpen()) {
			typeIndex.open();
		}
		/*
		 * replace as topic type
		 */
		for (Topic t : typeIndex.getTopics(other)) {
			store.doRemoveType((ITopic) t, other);
			store.doModifyTopicType((ITopic) t, topic);
		}
		/*
		 * replace as association type
		 */
		for (Association a : typeIndex.getAssociations(other)) {
			store.doModifyType((IAssociation) a, topic);
		}
		/*
		 * replace as role type
		 */
		for (Role r : typeIndex.getRoles(other)) {
			store.doModifyType((IAssociationRole) r, topic);
		}
		/*
		 * replace as occurrence type
		 */
		for (Occurrence o : typeIndex.getOccurrences(other)) {
			store.doModifyType((IOccurrence) o, topic);
		}
		/*
		 * replace as name type
		 */
		for (Name n : typeIndex.getNames(other)) {
			store.doModifyType((IName) n, topic);
		}

		ISupertypeSubtypeIndex supertypeSubtypeIndex = store.getIndex(ISupertypeSubtypeIndex.class);
		if (!supertypeSubtypeIndex.isOpen()) {
			supertypeSubtypeIndex.open();
		}
		/*
		 * replace as supertype
		 */
		for (Topic t : supertypeSubtypeIndex.getDirectSubtypes(other)) {
			store.doRemoveSupertype((ITopic) t, other);
			store.doModifySupertype((ITopic) t, topic);
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
	public static void replaceAsTheme(JdbcTopicMapStore store, ITopic topic, ITopic other) throws TopicMapStoreException {
		IScopedIndex scopeIndex = store.getIndex(IScopedIndex.class);
		if (!scopeIndex.isOpen()) {
			scopeIndex.open();
		}

		/*
		 * replace as association theme
		 */
		for (Association a : scopeIndex.getAssociations(other)) {
			store.doRemoveScope((IAssociation) a, other);
			store.doModifyScope((IAssociation) a, topic);
		}
		/*
		 * replace as name theme
		 */
		for (Name n : scopeIndex.getNames(other)) {
			store.doRemoveScope((IName) n, other);
			store.doModifyScope((IName) n, topic);
		}
		/*
		 * replace as variant theme
		 */
		for (Variant v : scopeIndex.getVariants(other)) {
			store.doRemoveScope((IVariant) v, other);
			store.doModifyScope((IVariant) v, topic);
		}
		/*
		 * replace as occurrence theme
		 */
		for (Occurrence o : scopeIndex.getOccurrences(other)) {
			store.doRemoveScope((IOccurrence) o, other);
			store.doModifyScope((IOccurrence) o, topic);
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
	public static void doMergeReifiable(JdbcTopicMapStore store, IReifiable reifiable, IReifiable other, IRevision revision) throws TopicMapStoreException {
		ITopic reifierOfOther = store.doReadReification(other);
		if (reifierOfOther != null) {
			ITopic reifier = store.doReadReification(reifiable);
			/*
			 * other construct has no reifier
			 */
			if (reifier == null) {
				/*
				 * move reifier
				 */
				store.doModifyReifier(other, null, revision);
				store.doModifyReifier(reifiable, reifierOfOther, revision);
			}
			/*
			 * both constructs are reified
			 */
			else {
				store.doModifyReifier(other, null, revision);
				/*
				 * merge both topics
				 */
				ITopic newReifier = store.doCreateTopicWithoutIdentifier(store.getTopicMap());
				doMerge(store, newReifier, reifier, revision);
				doMerge(store, newReifier, reifierOfOther, revision);
				((TopicImpl) reifier).getIdentity().setId(newReifier.longId());
				((TopicImpl) reifierOfOther).getIdentity().setId(newReifier.longId());
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
	private static void mergeTopicMaps(JdbcTopicMapStore store, ITopicMap topicMap, TopicMap other, boolean isCopyingOperation) throws TopicMapStoreException {
		Map<TopicRef, ITopic> references = new HashMap<TopicRef, ITopic>();
		IRevision revision = store.createRevision(TopicMapEventType.MERGE);
		Set<Topic> topics = other.getTopics();
		for (Topic topic : topics) {
			ITopic duplette = null;
			if (isCopyingOperation) {
				duplette = store.doCreateTopicWithoutIdentifier(topicMap, revision);
			} else {
				duplette = getDuplette(store, topic, null);
				if (duplette == null) {
					duplette = store.doCreateTopicWithoutIdentifier(topicMap, revision);
				}
			}

			Set<ILocator> iis = HashUtil.getHashSet(store.doReadItemIdentifiers(duplette));
			for (Locator loc : topic.getItemIdentifiers()) {
				if (isCopyingOperation || !iis.contains(loc)) {
					store.doModifyItemIdentifier(duplette, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}
				references.put(new TopicRef(loc.getReference(), Type.ITEM_IDENTIFIER), duplette);
			}

			Set<ILocator> sis = HashUtil.getHashSet(store.doReadSubjectIdentifiers(duplette));
			for (Locator loc : topic.getSubjectIdentifiers()) {
				if (isCopyingOperation || !sis.contains(loc)) {
					store.doModifySubjectIdentifier(duplette, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}
				references.put(new TopicRef(loc.getReference(), Type.SUBJECT_IDENTIFIER), duplette);
			}
			Set<ILocator> sls = HashUtil.getHashSet(store.doReadSubjectLocators(duplette));
			for (Locator loc : topic.getSubjectLocators()) {
				if (isCopyingOperation || !sls.contains(loc)) {
					store.doModifySubjectLocator(duplette, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}
				references.put(new TopicRef(loc.getReference(), Type.SUBJECT_LOCATOR), duplette);
			}
		}

		/*
		 * copy informations
		 */
		for (Topic topic : topics) {
			ITopic duplette = getDuplette(store, topic, references);
			if (duplette == null) {
				throw new TopicMapStoreException("Unknown topic!");
			}

			/*
			 * copy types
			 */
			for (Topic type : topic.getTypes()) {
				ITopic t = getDuplette(store, type, references);
				store.doModifyTopicType(duplette, t, revision);
			}

			/*
			 * copy super types
			 */
			if (topic instanceof ITopic) {
				for (Topic type : ((ITopic) topic).getSupertypes()) {
					ITopic t = getDuplette(store, type, references);
					store.doModifySupertype(duplette, t, revision);
				}
			}

			/*
			 * copy occurrences
			 */
			for (Occurrence occ : topic.getOccurrences()) {
				ITopic type = getDuplette(store, occ.getType(), references);
				IScope scope = getCorrespondingScope(store, occ.getScope(), references);
				ILocator datatype = (ILocator) store.getTopicMap().createLocator(occ.getDatatype().getReference());
				IOccurrence occurrence = getDuplette(store, duplette, type, occ.getValue(), datatype, scope.getThemes());
				if (occurrence == null) {
					occurrence = store.doCreateOccurrence(duplette, type, occ.getValue(), datatype, scope.getThemes(), revision);
				}
				/*
				 * copy item-identifiers of the occurrence
				 */
				for (Locator loc : occ.getItemIdentifiers()) {
					store.doModifyItemIdentifier(occurrence, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				Topic reifier = occ.getReifier();
				if (reifier != null) {
					store.doModifyReifier(occurrence, getDuplette(store, reifier, references));
				}

			}

			/*
			 * copy names
			 */
			for (Name name : topic.getNames()) {
				ITopic type = getDuplette(store, name.getType(), references);
				IScope scope = getCorrespondingScope(store, name.getScope(), references);
				IName n = getDuplette(store, duplette, type, name.getValue(), scope.getThemes());
				if (n == null) {
					n = store.doCreateName(duplette, type, name.getValue(), scope.getThemes(), revision);
				}

				/*
				 * copy item-identifiers of the name
				 */
				for (Locator loc : name.getItemIdentifiers()) {
					store.doModifyItemIdentifier(n, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				Topic reifier = name.getReifier();
				if (reifier != null) {
					store.doModifyReifier(n, getDuplette(store, reifier, references), revision);
				}

				/*
				 * copy variants
				 */
				for (Variant v : name.getVariants()) {
					scope = getCorrespondingScope(store, v.getScope(), references);
					ILocator datatype = (ILocator) store.getTopicMap().createLocator(v.getDatatype().getReference());
					IVariant variant = getDuplette(store, n, v.getValue(), datatype, scope.getThemes());
					if (variant == null) {
						variant = store.doCreateVariant(n, v.getValue(), datatype, scope.getThemes(), revision);
					}

					/*
					 * copy item-identifiers of the variant
					 */
					for (Locator loc : v.getItemIdentifiers()) {
						store.doModifyItemIdentifier(variant, store.doCreateLocator(topicMap, loc.getReference()), revision);
					}

					/*
					 * copy reification
					 */
					reifier = v.getReifier();
					if (reifier != null) {
						store.doModifyReifier(variant, getDuplette(store, reifier, references), revision);
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
			if (checkTmdmAssociation(store, ass, topicMap, other, references)) {
				continue;
			}

			ITopic type = getDuplette(store, ass.getType(), references);
			IAssociation association = store.doCreateAssociation(topicMap, type, getCorrespondingScope(store, ass.getScope(), references).getThemes(), revision);

			/*
			 * copy item-identifiers of the association
			 */
			for (Locator loc : ass.getItemIdentifiers()) {
				store.doModifyItemIdentifier(association, store.doCreateLocator(topicMap, loc.getReference()), revision);
			}

			/*
			 * copy reification
			 */
			Topic reifier = ass.getReifier();
			if (reifier != null) {
				store.doModifyReifier(association, getDuplette(store, reifier, references), revision);
			}

			/*
			 * copy roles
			 */
			for (Role r : ass.getRoles()) {
				type = getDuplette(store, r.getType(), references);
				ITopic player = getDuplette(store, r.getPlayer(), references);
				IAssociationRole role = (IAssociationRole) store.doCreateRole(association, type, player, revision);
				/*
				 * copy item-identifiers of the role
				 */
				for (Locator loc : r.getItemIdentifiers()) {
					store.doModifyItemIdentifier(role, store.doCreateLocator(topicMap, loc.getReference()), revision);
				}

				/*
				 * copy reification
				 */
				reifier = r.getReifier();
				if (reifier != null) {
					store.doModifyReifier(role, getDuplette(store, reifier, references), revision);
				}
			}
		}
	}

	// /**
	// * Copies the one topic map into the other
	// *
	// * @param store
	// * the store
	// * @param topicMap
	// * the topic map as target
	// * @param other
	// * the topic map as source
	// * @throws TopicMapStoreException
	// * thrown if operation fails
	// */
	// private static void copyTopicMaps(JdbcTopicMapStore store, ITopicMap topicMap, TopicMap other) throws
	// TopicMapStoreException {
	//
	// Set<Topic> topics = other.getTopics();
	//
	// Map<Topic, ITopic> newTopics = HashUtil.getHashMap(topics.size());
	//
	// // create all topics with all identifier
	// for (Topic topic : topics) {
	//
	// ITopic newTopic = store.doCreateTopicWithoutIdentifier(topicMap);
	// newTopics.put(topic, newTopic); // store relation for later use
	//
	// for (Locator loc : topic.getItemIdentifiers())
	// store.doModifyItemIdentifier(newTopic, store.doCreateLocator(topicMap, loc.getReference()));
	//
	// for (Locator loc : topic.getSubjectIdentifiers())
	// store.doModifySubjectIdentifier(newTopic, store.doCreateLocator(topicMap, loc.getReference()));
	//
	// for (Locator loc : topic.getSubjectLocators())
	// store.doModifySubjectLocator(newTopic, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * Copy information
	// */
	// for (Topic topic : topics) {
	//
	// ITopic newTopic = newTopics.get(topic);
	//
	// /*
	// * copy types
	// */
	// for (Topic type : topic.getTypes()) {
	// ITopic t = newTopics.get(type);
	// store.doModifyTopicType(newTopic, t);
	// }
	//
	// /*
	// * copy super types
	// */
	// if (topic instanceof ITopic) {
	// for (Topic type : ((ITopic) topic).getSupertypes()) {
	// ITopic t = newTopics.get(type);
	// store.doModifySupertype(newTopic, t);
	// }
	// }
	//
	// /*
	// * copy occurrences
	// */
	// for (Occurrence occ : topic.getOccurrences()) {
	//
	// ITopic type = newTopics.get(occ.getType());
	// Set<ITopic> themes = HashUtil.getHashSet();
	// for (Topic t : occ.getScope())
	// themes.add(newTopics.get(t));
	// // IScope scope = store.doCreateScope(topicMap, themes);
	// ILocator datatype = (ILocator) store.getTopicMap().createLocator(occ.getDatatype().getReference());
	//
	// IOccurrence newOccurrence = store.doCreateOccurrence(newTopic, type, occ.getValue(), datatype, themes);
	//
	// /*
	// * copy item-identifiers of the occurrence
	// */
	// for (Locator loc : occ.getItemIdentifiers()) {
	// store.doModifyItemIdentifier(newOccurrence, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * copy reification
	// */
	// Topic reifier = occ.getReifier();
	// if (reifier != null) {
	// store.doModifyReifier(newOccurrence, newTopics.get(reifier));
	// }
	//
	// }
	//
	// /*
	// * copy names
	// */
	// for (Name name : topic.getNames()) {
	// ITopic type = newTopics.get(name.getType());
	// Set<ITopic> themes = HashUtil.getHashSet();
	// for (Topic t : name.getScope())
	// themes.add(newTopics.get(t));
	//
	// IName newName = store.doCreateName(newTopic, type, name.getValue(), themes);
	//
	// /*
	// * copy item-identifiers of the name
	// */
	// for (Locator loc : name.getItemIdentifiers()) {
	// store.doModifyItemIdentifier(newName, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * copy reification
	// */
	// Topic reifier = name.getReifier();
	// if (reifier != null) {
	// store.doModifyReifier(newName, newTopics.get(reifier));
	// }
	//
	// /*
	// * copy variants
	// */
	// for (Variant v : name.getVariants()) {
	//
	// Set<ITopic> vthemes = HashUtil.getHashSet();
	// for (Topic t : v.getScope())
	// vthemes.add(newTopics.get(t));
	// ILocator datatype = (ILocator) store.getTopicMap().createLocator(v.getDatatype().getReference());
	//
	// IVariant variant = store.doCreateVariant(newName, v.getValue(), datatype, vthemes);
	//
	// /*
	// * copy item-identifiers of the variant
	// */
	// for (Locator loc : v.getItemIdentifiers()) {
	// store.doModifyItemIdentifier(variant, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * copy reification
	// */
	// reifier = v.getReifier();
	// if (reifier != null) {
	// store.doModifyReifier(variant, newTopics.get(reifier));
	// }
	// }
	// }
	// }
	//
	// /*
	// * copy associations
	// */
	// for (Association ass : other.getAssociations()) {
	//
	// /*
	// * filter TMDM associations
	// */
	// if (checkTmdmAssociation(store, ass, topicMap, other)) {
	// continue;
	// }
	//
	// ITopic type = newTopics.get(ass.getType());
	// Set<ITopic> themes = HashUtil.getHashSet();
	// for (Topic t : ass.getScope())
	// themes.add(newTopics.get(t));
	//
	// IAssociation association = store.doCreateAssociation(topicMap, type, themes);
	//
	// /*
	// * copy item-identifiers of the association
	// */
	// for (Locator loc : ass.getItemIdentifiers()) {
	// store.doModifyItemIdentifier(association, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * copy reification
	// */
	// Topic reifier = ass.getReifier();
	// if (reifier != null) {
	// store.doModifyReifier(association, newTopics.get(reifier));
	// }
	//
	// /*
	// * copy roles
	// */
	// for (Role r : ass.getRoles()) {
	// type = newTopics.get(r.getType());
	// ITopic player = newTopics.get(r.getPlayer());
	// IAssociationRole role = (IAssociationRole) association.createRole(type, player);
	// /*
	// * copy item-identifiers of the role
	// */
	// for (Locator loc : r.getItemIdentifiers()) {
	// store.doModifyItemIdentifier(role, store.doCreateLocator(topicMap, loc.getReference()));
	// }
	//
	// /*
	// * copy reification
	// */
	// reifier = r.getReifier();
	// if (reifier != null) {
	// store.doModifyReifier(role, newTopics.get(reifier));
	// }
	// }
	// }
	//
	// }

	/**
	 * Merge the one topic map into the other. If the source topic map is empty, the other topic map will simply be
	 * copied into the new one without checking if specific constructs have to be merged.
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
	public static void doMergeTopicMaps(JdbcTopicMapStore store, ITopicMap topicMap, TopicMap other) throws TopicMapStoreException {

		// if (store.isTopicMapEmpty(topicMap)) {
		// // use copy
		// copyTopicMaps(store, topicMap, other);
		// } else {
		// // use merge
		mergeTopicMaps(store, topicMap, other, store.isTopicMapEmpty(topicMap));
		// }
	}

	/**
	 * Returns the corresponding scope containing all themes of the origin
	 * 
	 * @param store
	 *            the store
	 * @param themes
	 *            the set of themes
	 * @param references
	 *            the references of the topic map
	 * @return the scope object
	 */
	public static IScope getCorrespondingScope(JdbcTopicMapStore store, final Set<Topic> themes, Map<TopicRef, ITopic> references) {
		Set<ITopic> set = HashUtil.getHashSet();
		for (Topic t : themes) {
			ITopic theme = getDuplette(store, t, references);
			set.add(theme);
		}
		return store.doCreateScope(store.getTopicMap(), set);
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
	private static boolean checkTmdmAssociation(JdbcTopicMapStore store, Association association, ITopicMap topicMap, TopicMap other, Map<TopicRef, ITopic> references) throws TopicMapStoreException {
		Locator typeInstanceLocator = store.doCreateLocator(topicMap, Namespaces.TMDM.TYPE_INSTANCE);
		Locator supertypeSubtypeLocator = store.doCreateLocator(topicMap, Namespaces.TMDM.SUPERTYPE_SUBTYPE);

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
			ITopic supertype = getDuplette(store, association.getRoles(supertypeRole).iterator().next().getPlayer(), references);
			ITopic subtype = getDuplette(store, association.getRoles(subtypeRole).iterator().next().getPlayer(), references);
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
			ITopic type = getDuplette(store, association.getRoles(typeRole).iterator().next().getPlayer(), references);
			ITopic instance = getDuplette(store, association.getRoles(instanceRole).iterator().next().getPlayer(), references);
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
	 *            the topic map itself
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public static void removeDuplicates(final JdbcTopicMapStore store, final ITopicMap topicMap) throws TopicMapStoreException {

		IRevision revision = store.createRevision(TopicMapEventType.REMOVE_DUPLICATES);

		for (ITopic topic : store.doReadTopics(store.getTopicMap())) {
			Set<Construct> removed = HashUtil.getHashSet();
			Collection<IName> names = HashUtil.getHashSet(store.doReadNames(topic));
			/*
			 * check name duplicates
			 */
			for (IName name : names) {
				if (removed.contains(name)) {
					continue;
				}
				for (IName duplicate : names) {
					if (duplicate.equals(name) || removed.contains(duplicate)) {
						continue;
					}
					/*
					 * names are equal if the value, the type and scope property are equal
					 */
					if (store.doReadType(name).equals(store.doReadType(duplicate)) && store.doReadValue(duplicate).equals(store.doReadValue(name))
							&& store.doReadScope(duplicate).equals(store.doReadScope(name))) {
						/*
						 * copy item-identifier
						 */
						for (ILocator ii : store.doReadItemIdentifiers(duplicate)) {
							store.doRemoveItemIdentifier(duplicate, ii, revision);
							store.doModifyItemIdentifier(name, ii, revision);
						}
						/*
						 * copy variants
						 */
						for (IVariant v : store.doReadVariants(duplicate)) {
							String value = store.doReadValue(v).toString();
							ILocator datatype = store.doReadDataType(v);
							IScope scope = store.doReadScope(v);
							boolean isNew = false;
							IVariant copy = getDuplette(store, name, value, datatype, scope.getThemes());
							if (copy == null) {
								isNew = true;
								copy = store.doCreateVariant(name, value, datatype, scope.getThemes(), revision);
							}
							/*
							 * copy item-identifier
							 */
							for (ILocator ii : store.doReadItemIdentifiers(v)) {
								if (!isNew) {
									store.doRemoveItemIdentifier(v, ii, revision);
								}
								store.doModifyItemIdentifier(copy, ii, revision);
							}
							/*
							 * check reification
							 */
							doMergeReifiable(store, copy, v, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, name, duplicate, revision);
						/*
						 * remove duplicate
						 */
						store.doRemoveName(duplicate, false, revision);
						removed.add(duplicate);
					}
				}
				/*
				 * check variants
				 */
				for (IVariant v : store.doReadVariants(name)) {
					if (removed.contains(v)) {
						continue;
					}
					for (IVariant dup : getDuplettes(store, name, store.doReadValue(v).toString(), store.doReadDataType(v), store.doReadScope(v).getThemes())) {
						if (v.equals(dup) || removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (ILocator ii : store.doReadItemIdentifiers(dup)) {
							store.doRemoveItemIdentifier(dup, ii, revision);
							store.doModifyItemIdentifier(v, ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, v, dup, revision);
						/*
						 * remove duplicate
						 */
						removed.add(dup);
						store.doRemoveVariant(dup, false, revision);
					}
				}
			}
			removed.clear();
			/*
			 * check occurrences
			 */
			Collection<IOccurrence> occurrences = HashUtil.getHashSet(store.doReadOccurrences(topic));
			for (IOccurrence occurrence : occurrences) {
				if (removed.contains(occurrence)) {
					continue;
				}
				for (IOccurrence duplicate : occurrences) {
					if (duplicate.equals(occurrence) || removed.contains(duplicate)) {
						continue;
					}
					/*
					 * occurrences are equal if the value, datatype, the type and scope property are equal
					 */
					if (store.doReadType(duplicate).equals(store.doReadType(occurrence)) && store.doReadValue(duplicate).equals(store.doReadValue(occurrence))
							&& store.doReadScope(duplicate).equals(store.doReadScope(occurrence)) && store.doReadDataType(duplicate).equals(store.doReadDataType(occurrence))) {
						/*
						 * copy item-identifier
						 */
						for (ILocator ii : store.doReadItemIdentifiers(duplicate)) {
							store.doRemoveItemIdentifier(duplicate, ii, revision);
							store.doModifyItemIdentifier(occurrence, ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, occurrence, duplicate, revision);
						/*
						 * remove duplicate
						 */
						store.doRemoveOccurrence(duplicate, false, revision);
						removed.add(duplicate);
					}
				}
			}
		}

		Set<Construct> removed = HashUtil.getHashSet();
		/*
		 * check associations
		 */
		for (IAssociation association : store.doReadAssociation(store.getTopicMap())) {
			if (removed.contains(association)) {
				continue;
			}
			Set<IAssociationRole> roles = HashUtil.getHashSet(store.doReadRoles(association));
			for (IAssociation duplicate : getDuplettes(store, association)) {
				if (removed.contains(duplicate)) {
					continue;
				}
				/*
				 * copy item-identifier
				 */
				for (ILocator ii : store.doReadItemIdentifiers(duplicate)) {
					store.doRemoveItemIdentifier(duplicate, ii, revision);
					store.doModifyItemIdentifier(association, ii, revision);
				}
				/*
				 * check roles
				 */
				for (IAssociationRole r : roles) {
					for (IAssociationRole dup : getDuplettes(store, duplicate, r)) {
						if (removed.contains(dup)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (ILocator ii : store.doReadItemIdentifiers(dup)) {
							store.doRemoveItemIdentifier(dup, ii, revision);
							store.doModifyItemIdentifier(r, ii, revision);
						}
						/*
						 * check reification
						 */
						doMergeReifiable(store, r, dup, revision);
						/*
						 * store removed
						 */
						removed.add(dup);
					}
				}
				/*
				 * check reification
				 */
				doMergeReifiable(store, association, duplicate, revision);
				/*
				 * remove duplicate
				 */
				store.doRemoveAssociation(duplicate, false, revision);
				removed.add(duplicate);
			}
			/*
			 * check roles
			 */
			for (IAssociationRole r : roles) {
				if (removed.contains(r)) {
					continue;
				}
				for (IAssociationRole dup : getDuplettes(store, association, r)) {
					if (dup.equals(r) || removed.contains(dup)) {
						continue;
					}
					/*
					 * copy item-identifier
					 */
					for (ILocator ii : store.doReadItemIdentifiers(dup)) {
						store.doRemoveItemIdentifier(dup, ii, revision);
						store.doModifyItemIdentifier(r, ii, revision);
					}
					/*
					 * check reification
					 */
					doMergeReifiable(store, r, dup, revision);
					/*
					 * remove duplicate
					 */
					store.doRemoveRole(dup, false, revision);
					removed.add(dup);
				}
			}
		}
	}

	/**
	 * Returns a set the duplicated role of the given association with the same type and player than the given one.
	 * 
	 * @param store
	 *            the store
	 * @param association
	 *            the association
	 * @param role
	 *            the role
	 * @return a set of duplicated roles
	 */
	public static Set<IAssociationRole> getDuplettes(JdbcTopicMapStore store, IAssociation association, IAssociationRole role) {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		for (IAssociationRole r : store.doReadRoles(association, store.doReadType(role))) {
			if (role.equals(r)) {
				continue;
			}
			if (store.doReadPlayer(r).equals(store.doReadPlayer(role))) {
				set.add(r);
			}
		}
		return set;
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
	public static Set<IAssociation> getDuplettes(JdbcTopicMapStore store, IAssociation association) throws TopicMapStoreException {
		Set<IAssociation> associations = HashUtil.getHashSet();
		/*
		 * iterate over all filtered associations
		 */
		for (IAssociation a : store.doReadAssociation(store.getTopicMap(), store.doReadType(association), store.doReadScope(association))) {
			if (a.equals(association)) {
				continue;
			}
			boolean duplette = true;
			/*
			 * iterate over all roles of an association
			 */
			for (IAssociationRole role : store.doReadRoles(a)) {
				boolean containsRole = false;
				/*
				 * get typed roles of the other association
				 */
				Set<IAssociationRole> roles = store.doReadRoles(association, store.doReadType(role));
				/*
				 * look for duplicated role
				 */
				for (IAssociationRole r : roles) {
					/*
					 * same player or players are the topics to merge
					 */
					if (store.doReadPlayer(r).equals(store.doReadPlayer(role))) {
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
				associations.add(a);
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
	public static Set<IVariant> getDuplettes(JdbcTopicMapStore store, IName name, String value, ILocator locator, Collection<ITopic> themes) throws TopicMapStoreException {
		Set<IVariant> variants = HashUtil.getHashSet();
		/*
		 * get scope as filter
		 */
		IScope scope = store.doCreateScope(store.getTopicMap(), themes);
		/*
		 * check for variant value
		 */
		for (IVariant v : store.doReadVariants(name, scope)) {
			if (store.doReadValue(v).toString().equals(value) && store.doReadDataType(v).equals(locator)) {
				variants.add(v);
			}
		}
		return variants;
	}

}
