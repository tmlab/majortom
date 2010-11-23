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
package de.topicmapslab.majortom.inmemory.virtual.internal;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.inmemory.store.internal.CharacteristicsStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualCharacteristicsStore<T extends VirtualTopicMapStore> extends CharacteristicsStore implements IVirtualStore {

	private final T store;

	private Set<String> changedDatatypes;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the transaction store
	 * @param xsdString
	 *            the locator of XSD string
	 */
	public VirtualCharacteristicsStore(T store, ILocator xsdString) {
		super(xsdString);
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		super.close();
		if (changedDatatypes != null) {
			changedDatatypes.clear();
		}
	}

	/**
	 * Internal method to access the virtual-identity store
	 * 
	 * @return the virtual identity store
	 */
	@SuppressWarnings("unchecked")
	protected VirtualIdentityStore<T> getVirtualIdentityStore() {
		return ((VirtualIdentityStore<T>) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected T getStore() {
		return store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addName(ITopic t, IName n) {
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		super.addName(t, n);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOccurrence(ITopic t, IOccurrence o) {
		if (getVirtualIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(o)) {
			throw new ConstructRemovedException(o);
		}
		super.addOccurrence(t, o);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addVariant(IName n, IVariant v) {
		if (getVirtualIdentityStore().isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(v)) {
			throw new ConstructRemovedException(v);
		}
		super.addVariant(n, v);
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator getDatatype(IDatatypeAware dataTypeAware) {
		if (containsDatatype(dataTypeAware)) {
			return super.getDatatype(dataTypeAware);
		}
		if (!getVirtualIdentityStore().isVirtual(dataTypeAware)) {
			return (ILocator) getStore().getRealStore().doRead(dataTypeAware, TopicMapStoreParameterType.DATATYPE);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IDatatypeAware> getDatatypeAwares(ILocator locator) {
		ILiteralIndex index = getStore().getRealStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IDatatypeAware> set = HashUtil.getHashSet();
		for (IDatatypeAware datatypeAware : index.getDatatypeAwares(locator)) {
			if (getVirtualIdentityStore().isRemovedConstruct(datatypeAware)) {
				continue;
			}
			if (changedDatatypes == null || !changedDatatypes.contains(datatypeAware.getId())) {
				set.add(getVirtualIdentityStore().asVirtualConstruct(datatypeAware));
			}
		}
		set.addAll(super.getDatatypeAwares(locator));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IName> getNames() {
		ILiteralIndex index = getStore().getRealStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IName> set = HashUtil.getHashSet();
		for (Name name : index.getNames()) {
			if (!getVirtualIdentityStore().isRemovedConstruct((IName) name)) {
				set.add(getVirtualIdentityStore().asVirtualConstruct((IName) name));
			}
		}
		set.addAll(super.getNames());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IName> getNames(ITopic t) {
		Set<IName> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			for (IName name : (Set<IName>) getStore().getRealStore().doRead(t, TopicMapStoreParameterType.NAME)) {
				if (!getVirtualIdentityStore().isRemovedConstruct(name)) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(name));
				}
			}
		}
		set.addAll(super.getNames(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IOccurrence> getOccurrences() {
		ILiteralIndex index = getStore().getRealStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		for (Occurrence occurrence : index.getOccurrences()) {
			if (!getVirtualIdentityStore().isRemovedConstruct((IOccurrence) occurrence)) {
				set.add(getVirtualIdentityStore().asVirtualConstruct((IOccurrence) occurrence));
			}
		}
		set.addAll(super.getOccurrences());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IOccurrence> getOccurrences(ITopic t) {
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(t)) {
			for (IOccurrence occurrence : (Set<IOccurrence>) getStore().getRealStore().doRead(t, TopicMapStoreParameterType.OCCURRENCE)) {
				if (!getVirtualIdentityStore().isRemovedConstruct(occurrence)) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(occurrence));
				}
			}
		}
		set.addAll(super.getOccurrences(t));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IVariant> getVariants() {
		ILiteralIndex index = getStore().getRealStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IVariant> set = HashUtil.getHashSet();
		for (Variant variant : index.getVariants()) {
			if (!getVirtualIdentityStore().isRemovedConstruct((IVariant) variant)) {
				set.add(getVirtualIdentityStore().asVirtualConstruct((IVariant) variant));
			}
		}
		set.addAll(super.getVariants());
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<IVariant> getVariants(IName n) {
		Set<IVariant> set = HashUtil.getHashSet();
		if (!getVirtualIdentityStore().isVirtual(n)) {
			for (IVariant variant : (Set<IVariant>) getStore().getRealStore().doRead(n, TopicMapStoreParameterType.VARIANT)) {
				if (!getVirtualIdentityStore().isRemovedConstruct(variant)) {
					set.add(getVirtualIdentityStore().asVirtualConstruct(variant));
				}
			}
		}
		set.addAll(super.getVariants(n));
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValue(IConstruct obj) {
		if (getVirtualIdentityStore().isRemovedConstruct(obj)) {
			throw new ConstructRemovedException(obj);
		}
		try {
			return super.getValue(obj);
		} catch (TopicMapStoreException e) {
			if (!getVirtualIdentityStore().isVirtual(obj)) {
				try {
					return getStore().getRealStore().doRead(obj, TopicMapStoreParameterType.VALUE);
				} catch (TopicMapStoreException ex) {
					// THROWN IF CONSTRUCT IS NOT CREATED YET
				}
			} 
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object setValue(IConstruct obj, Object value) {
		if (getVirtualIdentityStore().isRemovedConstruct(obj)) {
			throw new ConstructRemovedException(obj);
		}
		Object oldValue = getValue(obj);
		super.setValue(obj, value);
		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public ILocator setDatatype(IDatatypeAware dataTypeAware, ILocator dataType) {
		if (getVirtualIdentityStore().isRemovedConstruct(dataTypeAware)) {
			throw new ConstructRemovedException(dataTypeAware);
		}
		if (changedDatatypes == null) {
			changedDatatypes = HashUtil.getHashSet();
		}
		changedDatatypes.add(dataTypeAware.getId());
		ILocator oldDatatype = getDatatype(dataTypeAware);
		super.setDatatype(dataTypeAware, dataType);
		return oldDatatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTopic(ITopic topic) {
		try {
			super.removeTopic(topic);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeName(IName n) {
		try {
			super.removeName(n);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeVariant(IVariant v) {
		try {
			super.removeVariant(v);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeOccurrence(IOccurrence o) {
		try {
			super.removeOccurrence(o);
		} catch (TopicMapStoreException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeVirtualConstruct(IConstruct construct, IConstruct newConstruct) {
		/*
		 * construct is an occurrence
		 */
		if (construct instanceof IOccurrence) {
			IOccurrence newOccurrence = (IOccurrence) construct;
			/*
			 * replace occurrences as children of its parent
			 */
			Map<ITopic, Set<IOccurrence>> occurrences = getOccurrencesMap();
			if (occurrences != null && occurrences.containsKey(construct.getParent())) {
				occurrences.get(construct.getParent()).remove(construct);
				occurrences.get(construct.getParent()).add(newOccurrence);
			}
			replaceValueBinding(construct, newConstruct);
			replaceDatatypeBinding((IOccurrence) construct, newOccurrence);
		}
		/*
		 * construct is an occurrence
		 */
		else if (construct instanceof IName) {
			IName newName = (IName) construct;
			/*
			 * replace name as children of its parent
			 */
			Map<ITopic, Set<IName>> names = getNamesMap();
			if (names != null && names.containsKey(construct.getParent())) {
				names.get(construct.getParent()).remove(construct);
				names.get(construct.getParent()).add(newName);
			}
			replaceValueBinding(construct, newConstruct);
			/*
			 * replace as variant parent
			 */
			Map<IName, Set<IVariant>> variants = getVariantsMap();
			if (variants != null && variants.containsKey(construct)) {
				Set<IVariant> set = variants.remove(construct);
				variants.put(newName, set);
			}
		}
		/*
		 * construct is a variant
		 */
		else if (construct instanceof IVariant) {
			IVariant newVariant = (IVariant) construct;
			/*
			 * replace as child of its name
			 */
			Map<IName, Set<IVariant>> variants = getVariantsMap();
			if (variants != null && variants.containsKey(construct.getParent())) {
				variants.get(construct.getParent()).remove(construct);
				variants.get(construct.getParent()).add(newVariant);
			}
			replaceValueBinding(construct, newConstruct);
			replaceDatatypeBinding((IVariant) construct, newVariant);
		}
		/*
		 * else if construct is a ITopic
		 */
		else if (construct instanceof ITopic) {
			ITopic newTopic = (ITopic) newConstruct;
			/*
			 * copy names to new parent
			 */
			Map<ITopic, Set<IName>> names = getNamesMap();
			if (names != null && names.containsKey(construct)) {
				Set<IName> set = names.remove(construct);
				names.put(newTopic, set);
			}
			/*
			 * copy occurrences to new parent
			 */
			Map<ITopic, Set<IOccurrence>> occurrences = getOccurrencesMap();
			if (occurrences != null && occurrences.containsKey(construct)) {
				Set<IOccurrence> set = occurrences.remove(construct);
				occurrences.put(newTopic, set);
			}
		}
	}

	/**
	 * Internal method to replace value mappings
	 * 
	 * @param construct
	 *            the old construct
	 * @param newConstruct
	 *            the new construct
	 */
	private void replaceValueBinding(IConstruct construct, IConstruct newConstruct) {
		/*
		 * replace the value of the construct
		 */
		Map<IConstruct, Object> values = getValuesMap();
		if (values != null && values.containsKey(construct)) {
			Object value = values.remove(construct);
			values.put(newConstruct, value);
			/*
			 * replace the construct as value holder
			 */
			if (construct instanceof IOccurrence) {
				Map<String, Set<IOccurrence>> occurrencesByValue = getOccurrencesByValueMap();
				if (occurrencesByValue != null && occurrencesByValue.containsKey(value.toString())) {
					occurrencesByValue.get(value.toString()).remove(construct);
					occurrencesByValue.get(value.toString()).add((IOccurrence) newConstruct);
				}
			} else if (construct instanceof IName) {
				Map<String, Set<IName>> namesByValue = getNamesByValueMap();
				if (namesByValue != null && namesByValue.containsKey(value.toString())) {
					namesByValue.get(value.toString()).remove(construct);
					namesByValue.get(value.toString()).add((IName) newConstruct);
				}
			} else if (construct instanceof IVariant) {
				Map<String, Set<IVariant>> variantsByValue = getVariantsByValueMap();
				if (variantsByValue != null && variantsByValue.containsKey(value.toString())) {
					variantsByValue.get(value.toString()).remove(construct);
					variantsByValue.get(value.toString()).add((IVariant) newConstruct);
				}
			}
		}
	}

	/**
	 * Internal method to replace datatype mappings
	 * 
	 * @param construct
	 *            the old construct
	 * @param newConstruct
	 *            the new construct
	 */
	private void replaceDatatypeBinding(IDatatypeAware construct, IDatatypeAware newConstruct) {
		Map<IDatatypeAware, ILocator> datatypes = getDataTypesMap();
		if (datatypes != null && datatypes.containsKey(construct)) {
			/*
			 * copy datatype to new reference
			 */
			ILocator datatype = datatypes.remove(construct);
			datatypes.put(newConstruct, datatype);
			/*
			 * replace datatype-construct mapping
			 */
			Map<ILocator, Set<IDatatypeAware>> datatyped = getDataTypedMap();
			if (datatyped != null && datatyped.containsKey(datatype)) {
				datatyped.get(datatype).remove(construct);
				datatyped.get(datatype).add(newConstruct);
			}
			/*
			 * replace change datatypes mapping
			 */
			if (changedDatatypes != null && changedDatatypes.contains(construct.getId())) {
				changedDatatypes.remove(construct.getId());
				changedDatatypes.add(newConstruct.getId());
			}
		}
	}
}
