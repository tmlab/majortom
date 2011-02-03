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

	// public void removeDuplicates(ITopic topic, IRevision revision) {
	// Set<Construct> removed = HashUtil.getHashSet();
	//
	// /*
	// * check name duplicates
	// */
	// for (IName name : getTopicMapStore().getCharacteristicsStore().getNames(topic)) {
	// if (removed.contains(name)) {
	// continue;
	// }
	// byte[] signature = signatures.get(name);
	// if (signature == null) {
	// throw new TopicMapStoreException("Signature is missing!");
	// }
	// Set<IConstruct> set = constructs.get(signature);
	// if (set.size() > 1) {
	// for (IConstruct c : set) {
	// /*
	// * ignore same association
	// */
	// if (name.equals(c)) {
	// continue;
	// }
	// IName duplicate = (IName) c;
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : getTopicMapStore().getIdentityStore().getItemIdentifiers(duplicate)) {
	// getTopicMapStore().removeItemIdentifier(duplicate, (ILocator) ii, revision);
	// getTopicMapStore().modifyItemIdentifier(name, (ILocator) ii, revision);
	// }
	// /*
	// * check variants
	// */
	// for (Variant duplicateVariant : getTopicMapStore().getCharacteristicsStore().getVariants(duplicate)) {
	// if ( removed.contains(duplicateVariant)){
	// continue;
	// }
	// byte[] variantSignature = generateSignature(name, duplicateVariant);
	// if (variantSignature == null) {
	// throw new TopicMapStoreException("Signature is missing!");
	// }
	// /*
	// * check duplicated variants
	// */
	// for (IConstruct r : duplicates) {
	// Variant variant = getDuplette(store, name, dup.getValue(), (ILocator) dup.getDatatype(), ((IVariant)
	// dup).getScopeObject().getThemes());
	// if (variant == null) {
	// variant = store.createVariant(name, dup.getValue(), (ILocator) dup.getDatatype(), ((IVariant)
	// dup).getScopeObject().getThemes(), revision);
	// }
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : dup.getItemIdentifiers()) {
	// store.removeItemIdentifier((IVariant) dup, (ILocator) ii, revision);
	// store.modifyItemIdentifier((IVariant) variant, (ILocator) ii, revision);
	// }
	// /*
	// * check reification
	// */
	// doMergeReifiable(store, (IVariant) variant, (IVariant) dup, revision);
	// }
	// /*
	// * check reification
	// */
	// doMergeReifiable(store, name, duplicate, revision);
	// /*
	// * remove duplicate
	// */
	// store.removeName(duplicate, true, revision);
	// removed.add(duplicate);
	//
	// }
	//
	// for (IName duplicate : store.doReadNames(topic)) {
	// if (duplicate.equals(name) || removed.contains(duplicate)) {
	// continue;
	// }
	// /*
	// * names are equal if the value, the type and scope property are equal
	// */
	// if (duplicate.getType().equals(name.getType()) && store.doReadValue(duplicate).equals(store.doReadValue(name)) &&
	// duplicate.getScopeObject().equals(name.getScopeObject())) {
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : duplicate.getItemIdentifiers()) {
	// store.removeItemIdentifier(duplicate, (ILocator) ii, revision);
	// store.modifyItemIdentifier(name, (ILocator) ii, revision);
	// }
	// /*
	// * copy variants
	// */
	// for (Variant dup : duplicate.getVariants()) {
	// Variant variant = getDuplette(store, name, dup.getValue(), (ILocator) dup.getDatatype(), ((IVariant)
	// dup).getScopeObject().getThemes());
	// if (variant == null) {
	// variant = store.createVariant(name, dup.getValue(), (ILocator) dup.getDatatype(), ((IVariant)
	// dup).getScopeObject().getThemes(), revision);
	// }
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : dup.getItemIdentifiers()) {
	// store.removeItemIdentifier((IVariant) dup, (ILocator) ii, revision);
	// store.modifyItemIdentifier((IVariant) variant, (ILocator) ii, revision);
	// }
	// /*
	// * check reification
	// */
	// doMergeReifiable(store, (IVariant) variant, (IVariant) dup, revision);
	// }
	// /*
	// * check reification
	// */
	// doMergeReifiable(store, name, duplicate, revision);
	// /*
	// * remove duplicate
	// */
	// store.removeName(duplicate, true, revision);
	// removed.add(duplicate);
	// }
	// }
	// /*
	// * check variants
	// */
	// for (Variant v : name.getVariants()) {
	// if (removed.contains(v)) {
	// continue;
	// }
	// for (IVariant dup : MergeUtils.getDuplettes(store, name, v.getValue(), (ILocator) v.getDatatype(), ((IVariant)
	// v).getScopeObject().getThemes())) {
	// if (v.equals(dup) || removed.contains(dup)) {
	// continue;
	// }
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : dup.getItemIdentifiers()) {
	// dup.removeItemIdentifier(ii);
	// v.addItemIdentifier(ii);
	// }
	// /*
	// * check reification
	// */
	// ITopic duplicateReifier = (ITopic) dup.getReifier();
	// ITopic reifier = (ITopic) v.getReifier();
	// if (duplicateReifier != null) {
	// dup.setReifier(null);
	// if (reifier != null) {
	// doMerge(store, reifier, duplicateReifier, revision);
	// } else {
	// v.setReifier(duplicateReifier);
	// }
	// }
	// /*
	// * remove duplicate
	// */
	// removed.add(dup);
	// dup.remove();
	// }
	// }
	// }
	// removed.clear();
	// /*
	// * check occurrences
	// */
	// for (Occurrence occurrence : topic.getOccurrences()) {
	// if (removed.contains(occurrence)) {
	// continue;
	// }
	// for (Occurrence duplicate : topic.getOccurrences()) {
	// if (duplicate.equals(occurrence) || removed.contains(duplicate)) {
	// continue;
	// }
	// /*
	// * occurrences are equal if the value, datatype, the type and scope property are equal
	// */
	// if (duplicate.getType().equals(occurrence.getType()) && duplicate.getValue().equals(occurrence.getValue())
	// && ((IOccurrence) occurrence).getScopeObject().equals(((IOccurrence) duplicate).getScopeObject()) &&
	// occurrence.getDatatype().equals(duplicate.getDatatype())) {
	// /*
	// * copy item-identifier
	// */
	// for (Locator ii : duplicate.getItemIdentifiers()) {
	// store.removeItemIdentifier((IOccurrence) duplicate, (ILocator) ii, revision);
	// store.modifyItemIdentifier((IOccurrence) occurrence, (ILocator) ii, revision);
	// }
	// /*
	// * check reification
	// */
	// doMergeReifiable(store, (IOccurrence) occurrence, (IOccurrence) duplicate, revision);
	// /*
	// * remove duplicate
	// */
	// store.removeOccurrence((IOccurrence) duplicate, true, revision);
	// removed.add(duplicate);
	// }
	// }
	// }
	//
	// removed.clear();
	// }

	/**
	 * Removes all duplicates associations
	 * 
	 * @param revision
	 *            the revision to store changes
	 */
	public void removeAssociationDuplicates(IRevision revision) {
		Set<IConstruct> removed = HashUtil.getHashSet();
		for (IAssociation association : HashUtil.getHashSet(getTopicMapStore().getAssociationStore().getAssociations())) {
			if (removed.contains(association)) {
				continue;
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
