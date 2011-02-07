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

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class SignatureStore implements ITopicMapListener {

	private static final String ASSOCIATION_SIGNATURE = "{0}({1})@{2}";
	private static final String ROLE_SIGNATURE = "{0}:{1}";
	private static final String OCCURRENCE_SIGNATURE = "{0}:{1}:{2}{3}{4}";
	private static final String NAME_SIGNATURE = "{0}:-{1}:{2}{3}";
	private static final String VARIANT_SIGNATURE = "{0}:{1}{2}{3}";

	private Map<IConstruct, String> signatures = HashUtil.getHashMap();
	private Map<String, Set<IConstruct>> constructs = HashUtil.getHashMap();

	private final MessageDigest digest;

	private final InMemoryTopicMapStore topicMapStore;

	/**
	 * 
	 */
	public SignatureStore(InMemoryTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			throw new TopicMapStoreException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
		/*
		 * is association creation
		 */
		if (newValue instanceof IAssociation) {
			updateSignature((IAssociation) newValue);
		}
		/*
		 * is role creation
		 */
		else if (newValue instanceof IAssociationRole) {
			updateSignature((IAssociationRole) newValue);
		}
		/*
		 * is name creation
		 */
		else if (newValue instanceof IName) {
			updateSignature((IName) newValue);
		}
		/*
		 * is occurrence creation
		 */
		else if (newValue instanceof IOccurrence) {
			updateSignature((IOccurrence) newValue);
		}
		/*
		 * is variant creation
		 */
		else if (newValue instanceof IVariant) {
			updateSignature((IVariant) newValue);
		}
		/*
		 * is association deletion
		 */
		else if (oldValue instanceof IAssociation) {
			removeSignature(((IAssociation) oldValue));
		}
		/*
		 * is role deletion
		 */
		else if (oldValue instanceof IAssociationRole) {
			removeSignature(((IAssociationRole) oldValue));
		}
		/*
		 * is name deletion
		 */
		else if (oldValue instanceof IName) {
			removeSignature(((IName) oldValue));
		}
		/*
		 * is occurrence deletion
		 */
		else if (oldValue instanceof IOccurrence) {
			removeSignature(((IOccurrence) oldValue));
		}
		/*
		 * is variant deletion
		 */
		else if (oldValue instanceof IVariant) {
			removeSignature(((IVariant) oldValue));
		}
		/*
		 * is modification of association context ( type or scope )
		 */
		else if (notifier instanceof IAssociation) {
			/*
			 * type modified
			 */
			if (event == TopicMapEventType.TYPE_SET) {
				updateSignature((IAssociation) notifier);
			}
			/*
			 * scope modified
			 */
			else if (event == TopicMapEventType.SCOPE_MODIFIED) {
				updateSignature((IAssociation) notifier);
			}
		}
		/*
		 * is modification of role context ( type or player )
		 */
		else if (notifier instanceof IAssociationRole) {
			/*
			 * type modified
			 */
			if (event == TopicMapEventType.TYPE_SET) {
				updateSignature((IAssociationRole) notifier);
			}
			/*
			 * player modified
			 */
			else if (event == TopicMapEventType.PLAYER_MODIFIED) {
				updateSignature((IAssociationRole) notifier);
			}
		}
		/*
		 * is modification of name context ( type, value or scope )
		 */
		else if (notifier instanceof IName) {
			IName construct = (IName) notifier;
			/*
			 * type modified
			 */
			if (event == TopicMapEventType.TYPE_SET) {
				updateSignature(construct);
			}
			/*
			 * scope modified
			 */
			else if (event == TopicMapEventType.SCOPE_MODIFIED) {
				updateSignature(construct);
			}
			/*
			 * value modified
			 */
			else if (event == TopicMapEventType.VALUE_MODIFIED) {
				updateSignature(construct);
			}
		}
		/*
		 * is modification of occurrence context ( type, value or scope ) Ignore datatype: will be changed over value
		 * change
		 */
		else if (notifier instanceof IOccurrence) {
			IOccurrence construct = (IOccurrence) notifier;
			/*
			 * type modified
			 */
			if (event == TopicMapEventType.TYPE_SET) {
				updateSignature(construct);
			}
			/*
			 * scope modified
			 */
			else if (event == TopicMapEventType.SCOPE_MODIFIED) {
				updateSignature(construct);
			}
			/*
			 * value modified
			 */
			else if (event == TopicMapEventType.VALUE_MODIFIED) {
				updateSignature(construct);
			}
		}
		/*
		 * is modification of variant context ( value or scope ) Ignore datatype: will be changed over value change
		 */
		else if (notifier instanceof IVariant) {
			IVariant construct = (IVariant) notifier;
			/*
			 * scope modified
			 */
			if (event == TopicMapEventType.SCOPE_MODIFIED) {
				updateSignature(construct);
			}
			/*
			 * value modified
			 */
			else if (event == TopicMapEventType.VALUE_MODIFIED) {
				updateSignature(construct);
			}
		}
	}

	/**
	 * Method to remove the signature
	 * 
	 * @param construct
	 *            the construct
	 */
	private void removeSignature(IConstruct construct) {
		String oldSignature = signatures.get(construct);
		/*
		 * remove old link
		 */
		if (oldSignature != null && constructs.containsKey(oldSignature)) {
			Set<IConstruct> set = constructs.get(oldSignature);
			set.remove(construct);
			if (set.isEmpty()) {
				constructs.remove(oldSignature);
			}
			signatures.remove(construct);
		}

	}

	/**
	 * Internal method to update the signature for given id
	 * 
	 * @param construct
	 *            the construct
	 * @param signature
	 *            the signature
	 */
	private void updateSignature(IConstruct construct, String signature) {
		removeSignature(construct);
		signatures.put(construct, signature);

		Set<IConstruct> set = constructs.get(signature);
		if (set == null) {
			set = HashUtil.getHashSet();
			constructs.put(signature, set);
		}
		set.add(construct);
	}

	/**
	 * Internal method delicates to update association signature
	 * 
	 * @param association
	 *            the association
	 */
	private void updateSignature(IAssociation association) {
		final String signature = toHash(generateSignature(association));
		updateSignature(association, signature);
	}

	/**
	 * Internal method delicates to update the role signatue
	 * 
	 * @param role
	 *            the role
	 */
	private void updateSignature(IAssociationRole role) {
		updateSignature(role.getParent());
		final String signature = toHash(generateSignature(role));
		updateSignature(role, signature);
	}

	/**
	 * Internal method delicates to update name signature
	 * 
	 * @param name
	 *            the name
	 */
	private void updateSignature(IName name) {
		final String signature = toHash(generateSignature(name));
		updateSignature(name, signature);
	}

	/**
	 * Internal method delicates to update occurrence signature
	 * 
	 * @param occurrence
	 *            the occurrence
	 */
	private void updateSignature(IOccurrence occurrence) {
		final String signature = toHash(generateSignature(occurrence));
		updateSignature(occurrence, signature);
	}

	/**
	 * Internal method delicates to update variant signature
	 * 
	 * @param variant
	 *            the variant
	 */
	private void updateSignature(IVariant variant) {
		final String signature = toHash(generateSignature(variant));
		updateSignature(variant, signature);
	}

	/**
	 * Internal method to generate new association signature
	 * 
	 * @param association
	 *            the association
	 * @return the generated signature
	 */
	private String generateSignature(IAssociation association) {
		final String typeId = getTopicMapStore().getTypedStore().getType(association).getId();
		final String scopeId = getTopicMapStore().getScopeStore().getScope(association).getId();
		final Set<String> roleSignatures = HashUtil.getHashSet();
		for (IAssociationRole role : getTopicMapStore().getAssociationStore().getRoles(association)) {
			roleSignatures.add(generateSignature(role));
		}

		List<String> sortedSignatures = new ArrayList<String>(roleSignatures);
		Collections.sort(sortedSignatures);
		return MessageFormat.format(ASSOCIATION_SIGNATURE, typeId, sortedSignatures.toString(), scopeId);
	}

	/**
	 * Internal method to generate new role signature
	 * 
	 * @param role
	 *            the role
	 * @return the generated signature
	 */
	private String generateSignature(IAssociationRole role) {
		final String typeId = getTopicMapStore().getTypedStore().getType(role).getId();
		final String playerId = getTopicMapStore().getAssociationStore().getPlayer(role).getId();
		return MessageFormat.format(ROLE_SIGNATURE, typeId, playerId);
	}

	/**
	 * Internal method to generate new name signature
	 * 
	 * @param name
	 *            the name
	 * @return the generated signature
	 */
	private String generateSignature(IName name) {
		final String typeId = getTopicMapStore().getTypedStore().getType(name).getId();
		final String scopeId = getTopicMapStore().getScopeStore().getScope(name).getId();
		final String value = getTopicMapStore().getCharacteristicsStore().getValueAsString(name);
		final String parentId = name.getParent().getId();
		return MessageFormat.format(NAME_SIGNATURE, parentId, typeId, value, scopeId);
	}

	/**
	 * Internal method to generate new occurrence signature
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @return the generated signature
	 */
	private String generateSignature(IOccurrence occurrence) {
		final String typeId = getTopicMapStore().getTypedStore().getType(occurrence).getId();
		final String scopeId = getTopicMapStore().getScopeStore().getScope(occurrence).getId();
		final String value = getTopicMapStore().getCharacteristicsStore().getValueAsString(occurrence);
		final String datatype = getTopicMapStore().getCharacteristicsStore().getDatatype(occurrence).getReference();
		final String parentId = occurrence.getParent().getId();
		return MessageFormat.format(OCCURRENCE_SIGNATURE, parentId, typeId, value, datatype, scopeId);
	}

	/**
	 * Internal method to generate new variant signature
	 * 
	 * @param parent
	 *            the name used as parent
	 * @param variant
	 *            the variant
	 * @return the generated signature
	 */
	private String generateSignature(IName parent, IVariant variant) {
		final String scopeId = getTopicMapStore().getScopeStore().getScope(variant).getId();
		final String value = getTopicMapStore().getCharacteristicsStore().getValueAsString(variant);
		final String datatype = getTopicMapStore().getCharacteristicsStore().getDatatype(variant).getReference();
		final String parentId = parent.getId();
		return MessageFormat.format(VARIANT_SIGNATURE, parentId, value, datatype, scopeId);
	}

	/**
	 * Internal method to generate new variant signature
	 * 
	 * @param variant
	 *            the variant
	 * @return the generated signature
	 */
	private String generateSignature(IVariant variant) {
		return generateSignature(variant.getParent(), variant);
	}

	/**
	 * @return the topicMapStore
	 */
	public InMemoryTopicMapStore getTopicMapStore() {
		return topicMapStore;
	}

	/**
	 * Internal method to remove duplicates in context to the topic
	 * 
	 * @param topic
	 *            the topic
	 * @param revision
	 *            the revision to store changes
	 */
	private void internalRemoveDuplicates(ITopic topic, IRevision revision) {
		Set<Construct> removed = HashUtil.getHashSet();
		/*
		 * check name duplicates
		 */
		for (IName name : HashUtil.getHashSet(getTopicMapStore().getCharacteristicsStore().getNames(topic))) {
			if (removed.contains(name)) {
				continue;
			}
			String signature = signatures.get(name);
			if (signature == null) {
				throw new TopicMapStoreException("Signature is missing!");
			}
			Set<IConstruct> set = HashUtil.getHashSet(constructs.get(signature));
			if (set.size() > 1) {
				for (IConstruct c : set) {
					/*
					 * ignore same association
					 */
					if (name.equals(c)) {
						continue;
					}
					IName duplicate = (IName) c;
					/*
					 * copy item-identifier
					 */
					for (Locator ii : HashUtil.getHashSet(getTopicMapStore().getIdentityStore().getItemIdentifiers(duplicate))) {
						getTopicMapStore().removeItemIdentifier(duplicate, (ILocator) ii, revision);
						getTopicMapStore().modifyItemIdentifier(name, (ILocator) ii, revision);
					}
					/*
					 * check variants
					 */
					for (IVariant duplicateVariant : HashUtil.getHashSet(getTopicMapStore().getCharacteristicsStore().getVariants(duplicate))) {
						if (removed.contains(duplicateVariant)) {
							continue;
						}
						String variantSignature = toHash(generateSignature(name, duplicateVariant));
						if (variantSignature == null) {
							throw new TopicMapStoreException("Signature is missing!");
						}
						Set<IConstruct> duplicates = constructs.get(variantSignature);
						IVariant variant = null;
						/*
						 * copy new variant
						 */
						if (duplicates == null || duplicates.isEmpty()) {
							String value = getTopicMapStore().getCharacteristicsStore().getValueAsString(duplicateVariant);
							ILocator datatype = getTopicMapStore().getCharacteristicsStore().getDatatype(duplicateVariant);
							IScope scope = getTopicMapStore().getScopeStore().getScope(duplicateVariant);
							variant = getTopicMapStore().createVariant(name, value, datatype, scope.getThemes(), revision);
						}
						/*
						 * merge in variant
						 */
						else {
							variant = (IVariant) duplicates.iterator().next();
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : HashUtil.getHashSet(getTopicMapStore().getIdentityStore().getItemIdentifiers(duplicateVariant))) {
							getTopicMapStore().removeItemIdentifier(duplicateVariant, (ILocator) ii, revision);
							getTopicMapStore().modifyItemIdentifier(variant, (ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), variant, duplicateVariant, revision);
						removed.add(duplicateVariant);
					}
					/*
					 * check reification
					 */
					InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), name, duplicate, revision);
					/*
					 * remove duplicate
					 */
					getTopicMapStore().removeName(duplicate, true, revision);
					removed.add(duplicate);
				}
			}

			/*
			 * check variants
			 */
			for (IVariant variant : HashUtil.getHashSet(getTopicMapStore().getCharacteristicsStore().getVariants(name))) {
				if (removed.contains(variant)) {
					continue;
				}
				String variantSignature = signatures.get(variant);
				if (variantSignature == null) {
					throw new TopicMapStoreException("Signature is missing!");
				}
				Set<IConstruct> duplicates = HashUtil.getHashSet(constructs.get(variantSignature));
				if (duplicates.size() > 1) {
					for (IConstruct c2 : duplicates) {
						if (c2.equals(variant) || removed.contains(c2)) {
							continue;
						}
						IVariant duplicateVariant = (IVariant) c2;
						/*
						 * copy item-identifier
						 */
						for (Locator ii : HashUtil.getHashSet(getTopicMapStore().getIdentityStore().getItemIdentifiers(duplicateVariant))) {
							getTopicMapStore().removeItemIdentifier(duplicateVariant, (ILocator) ii, revision);
							getTopicMapStore().modifyItemIdentifier(variant, (ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), variant, duplicateVariant, revision);
						removed.add(duplicateVariant);
						getTopicMapStore().removeVariant(duplicateVariant, false, revision);
					}
				}
			}
		}

		removed.clear();
		/*
		 * check occurrences
		 */
		for (IOccurrence occurrence : HashUtil.getHashSet(getTopicMapStore().getCharacteristicsStore().getOccurrences(topic))) {
			if (removed.contains(occurrence)) {
				continue;
			}
			String signature = signatures.get(occurrence);
			if (signature == null) {
				throw new TopicMapStoreException("Signature is missing!");
			}
			Set<IConstruct> set = HashUtil.getHashSet(constructs.get(signature));
			if (set.size() > 1) {
				for (IConstruct c : set) {
					/*
					 * ignore same association
					 */
					if (occurrence.equals(c)) {
						continue;
					}
					IOccurrence duplicate = (IOccurrence) c;
					/*
					 * copy item-identifier
					 */
					for (Locator ii : HashUtil.getHashSet(getTopicMapStore().getIdentityStore().getItemIdentifiers(duplicate))) {
						getTopicMapStore().removeItemIdentifier(duplicate, (ILocator) ii, revision);
						getTopicMapStore().modifyItemIdentifier(occurrence, (ILocator) ii, revision);
					}
					/*
					 * check reification
					 */
					InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), occurrence, duplicate, revision);
					/*
					 * remove duplicate
					 */
					getTopicMapStore().removeOccurrence(duplicate, true, revision);
					removed.add(duplicate);
				}
			}
		}
		removed.clear();
	}

	/**
	 * Internal method to remove duplicates from topic map store
	 * 
	 * @param association
	 *            the association
	 * @param removed
	 *            the removed constructs
	 * @param revision
	 *            the revision to store the changes
	 */
	private void internalRemoveDuplicates(IAssociation association, Set<IConstruct> removed, IRevision revision) {
		if (removed.contains(association)) {
			return;
		}
		String signature = signatures.get(association);
		if (signature == null) {
			throw new TopicMapStoreException("Signature is missing!");
		}
		Set<IConstruct> set = HashUtil.getHashSet(constructs.get(signature));
		if (set.size() > 1) {
			for (IConstruct c : set) {
				/*
				 * ignore same association
				 */
				if (association.equals(c)) {
					continue;
				}
				IAssociation duplicate = (IAssociation) c;
				/*
				 * copy item-identifier
				 */
				for (Locator ii : duplicate.getItemIdentifiers()) {
					getTopicMapStore().removeItemIdentifier(duplicate, (ILocator) ii, revision);
					getTopicMapStore().modifyItemIdentifier(association, (ILocator) ii, revision);
				}
				/*
				 * check contained roles of duplicate
				 */
				for (IAssociationRole dup : HashUtil.getHashSet(getTopicMapStore().getAssociationStore().getRoles(duplicate))) {
					if (removed.contains(dup)) {
						continue;
					}
					/*
					 * generate signature
					 */
					final String sig = toHash(generateSignature(dup));
					Set<IConstruct> duplicates = HashUtil.getHashSet(constructs.get(sig));
					/*
					 * check duplicated roles
					 */
					for (IConstruct r : duplicates) {
						if (removed.contains(r) || !r.getParent().equals(association)) {
							continue;
						}
						/*
						 * copy item-identifier
						 */
						for (Locator ii : dup.getItemIdentifiers()) {
							getTopicMapStore().removeItemIdentifier(dup, (ILocator) ii, revision);
							getTopicMapStore().modifyItemIdentifier((IAssociationRole) r, (ILocator) ii, revision);
						}
						/*
						 * check reification
						 */
						InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), (IAssociationRole) r, dup, revision);
						break;
					}
				}
				/*
				 * check reification
				 */
				InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), association, duplicate, revision);
				/*
				 * remove duplicate
				 */
				getTopicMapStore().removeAssociation(duplicate, true, revision);
				removed.add(c);
			}
		}
		/*
		 * check roles
		 */
		for (IAssociationRole r : HashUtil.getHashSet(getTopicMapStore().getAssociationStore().getRoles(association))) {
			if (removed.contains(r)) {
				continue;
			}
			String roleSignature = signatures.get(r);
			if (roleSignature == null) {
				throw new TopicMapStoreException("Signature is missing!");
			}
			Set<IConstruct> duplicates = HashUtil.getHashSet(constructs.get(roleSignature));
			if (duplicates.size() == 1) {
				continue;
			}
			for (IConstruct c2 : duplicates) {
				if (removed.contains(c2) || r.equals(c2) || !c2.getParent().equals(association)) {
					continue;
				}
				IAssociationRole dup = (IAssociationRole) c2;
				/*
				 * copy item-identifier
				 */
				for (Locator ii : dup.getItemIdentifiers()) {
					getTopicMapStore().removeItemIdentifier(dup, (ILocator) ii, revision);
					getTopicMapStore().modifyItemIdentifier(r, (ILocator) ii, revision);
				}
				/*
				 * check reification
				 */
				InMemoryMergeUtils.doMergeReifiable(getTopicMapStore(), r, dup, revision);
				getTopicMapStore().removeRole(dup, false, revision);
				removed.add(c2);
			}
		}
	}

	/**
	 * Removes all duplicates of the topic map
	 * 
	 * @param revision
	 *            the revision to store changes
	 */
	public void removeDuplicates(IRevision revision) {
		for (ITopic topic : HashUtil.getHashSet(getTopicMapStore().getIdentityStore().getTopics())) {
			if (topic.isRemoved()) {
				continue;
			}
			internalRemoveDuplicates(topic, revision);
		}
		Set<IConstruct> removed = HashUtil.getHashSet();
		for (IAssociation association : HashUtil.getHashSet(getTopicMapStore().getAssociationStore().getAssociations())) {
			if (removed.contains(association)) {
				continue;
			}
			internalRemoveDuplicates(association, removed, revision);
		}
	}

	/**
	 * Removes all duplicates in relation to the given topic. In other words, all duplicate names, occurrences and
	 * played associations will be removed.
	 * 
	 * @param topic
	 *            the topic
	 * @param revision
	 *            the revision to store changes
	 */
	public void removeDuplicates(ITopic topic, IRevision revision) {
		internalRemoveDuplicates(topic, revision);
		Set<IConstruct> removed = HashUtil.getHashSet();
		for (IAssociation association : HashUtil.getHashSet(getTopicMapStore().doReadAssociation(topic))) {
			if (removed.contains(association)) {
				continue;
			}
			internalRemoveDuplicates(association, removed, revision);
		}
	}

	/**
	 * Utility method to generate a hash
	 * 
	 * @param string
	 *            the string
	 * @return the byte array as hash
	 */
	private String toHash(final String string) {
		return new String(digest.digest(string.getBytes()));
	}

}
