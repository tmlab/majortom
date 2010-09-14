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
package de.topicmapslab.majortom.database.transaction.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
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
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class CharacteristicsCache implements IDataStore {

	/**
	 * storage map of topic-name mapping
	 */
	private Map<ITopic, Set<IName>> names;

	/**
	 * storage map of topic-occurrence mapping
	 */
	private Map<ITopic, Set<IOccurrence>> occurrences;

	/**
	 * storage map of name-variant mapping
	 */
	private Map<IName, Set<IVariant>> variants;

	/**
	 * storage map of datatype mapping
	 */
	private Map<IDatatypeAware, ILocator> dataTypes;

	/**
	 * storage map of datatype mapping
	 */
	private Map<ILocator, Set<IDatatypeAware>> dataTyped;

	/**
	 * storage map of characteristics-value mapping
	 */
	private Map<IConstruct, Object> values;

	/**
	 * the xsd:any locator
	 */
	private final ILocator xsdString;

	private final TransactionTopicMapStore topicMapStore;

	private Set<String> changedDatatypes;

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the transaction store
	 * @param xsdString
	 *            the locator of XSD string
	 */
	public CharacteristicsCache(TransactionTopicMapStore topicMapStore,
			ILocator xsdString) {
		this.topicMapStore = topicMapStore;
		this.xsdString = xsdString;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (changedDatatypes != null) {
			changedDatatypes.clear();
		}
		if (names != null) {
			names.clear();
		}
		if (occurrences != null) {
			occurrences.clear();
		}
		if (dataTypes != null) {
			dataTypes.clear();
		}
		if (dataTyped != null) {
			dataTyped.clear();
		}
		if (values != null) {
			values.clear();
		}
		if (variants != null) {
			variants.clear();
		}
	}

	/**
	 * Add the given name to the internal data store.
	 * 
	 * @param t
	 *            the parent topic
	 * @param n
	 *            the name
	 */
	public void addName(ITopic t, IName n) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		if (names == null) {
			names = HashUtil.getHashMap();
		}

		Set<IName> set = names.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(n);
		names.put(t, set);
	}

	/**
	 * Add the given occurrence to the internal data store.
	 * 
	 * @param t
	 *            the parent topic
	 * @param n
	 *            the occurrence
	 */
	public void addOccurrence(ITopic t, IOccurrence o) {
		if (isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (isRemovedConstruct(o)) {
			throw new ConstructRemovedException(o);
		}
		if (occurrences == null) {
			occurrences = HashUtil.getHashMap();
		}

		Set<IOccurrence> set = occurrences.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(o);
		occurrences.put(t, set);

		setDatatype(o, xsdString);
	}

	/**
	 * Add the given variant to the internal data store.
	 * 
	 * @param n
	 *            the name
	 * @param v
	 *            the variant
	 */
	public void addVariant(IName n, IVariant v) {
		if (isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		if (isRemovedConstruct(v)) {
			throw new ConstructRemovedException(v);
		}
		if (variants == null) {
			variants = HashUtil.getHashMap();
		}

		Set<IVariant> set = variants.get(n);
		if (set == null) {
			set = HashUtil.getHashSet();
		}
		set.add(v);
		variants.put(n, set);
		setDatatype(v, xsdString);
	}

	/**
	 * Returns the data type of the given data-type-aware
	 * 
	 * @param dataTypeAware
	 *            the data-type-aware
	 * @return the data type
	 */
	public ILocator getDatatype(IDatatypeAware dataTypeAware) {
		if (containsDatatype(dataTypeAware)) {
			return dataTypes.get(dataTypeAware);
		}
		return (ILocator) getTopicMapStore().doRead(dataTypeAware,
				TopicMapStoreParameterType.DATATYPE);
	}

	/**
	 * Checks if the store contains any locator of the given
	 * {@link IDatatypeAware}.
	 * 
	 * @param aware
	 *            the {@link IDatatypeAware}
	 * @return <code>true</code> if any locator of the given
	 *         {@link IDatatypeAware} is stored, <code>false</code> otherwise.
	 */
	protected final boolean containsDatatype(IDatatypeAware aware) {
		return dataTypes != null && dataTypes.containsKey(aware);
	}

	/**
	 * Returns all data-typed items with the given data type.
	 * 
	 * @param locator
	 *            the data type
	 * @return a set
	 */
	public Set<IDatatypeAware> getDatatypeAwares(ILocator locator) {
		ILiteralIndex index = getTopicMapStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IDatatypeAware> set = HashUtil.getHashSet();
		for (IDatatypeAware datatypeAware : index.getDatatypeAwares(locator)) {
			if (isRemovedConstruct(datatypeAware)) {
				continue;
			}
			if (changedDatatypes == null
					|| !changedDatatypes.contains(datatypeAware.getId())) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub(datatypeAware));
			}
		}
		if (dataTyped != null && dataTyped.containsKey(locator)) {
			set.addAll(dataTyped.get(locator));
		}
		return set;
	}

	/**
	 * Returns the names of all topics.
	 * 
	 * @return the names
	 */
	public Set<IName> getNames() {
		ILiteralIndex index = getTopicMapStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IName> set = HashUtil.getHashSet();
		for (Name name : index.getNames()) {
			if (!isRemovedConstruct((IName) name)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub((IName) name));
			}
		}
		if (names != null) {
			for (Entry<ITopic, Set<IName>> entry : names.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		return set;
	}

	/**
	 * Returns the names of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the names
	 */
	@SuppressWarnings("unchecked")
	public Set<IName> getNames(ITopic t) {
		Set<IName> set = HashUtil.getHashSet();
		for (IName name : (Set<IName>) getTopicMapStore().doRead(t,
				TopicMapStoreParameterType.NAME)) {
			if (!isRemovedConstruct(name)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub(name));
			}
		}
		if (names != null && names.containsKey(t)) {
			set.addAll(names.get(t));
		}
		return set;
	}

	/**
	 * Returns the occurrences of all topics.
	 * 
	 * @return the occurrences
	 */
	public Set<IOccurrence> getOccurrences() {
		ILiteralIndex index = getTopicMapStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		for (Occurrence occurrence : index.getOccurrences()) {
			if (!isRemovedConstruct((IOccurrence) occurrence)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub((IOccurrence) occurrence));
			}
		}
		if (occurrences != null) {
			for (Entry<ITopic, Set<IOccurrence>> entry : occurrences.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		return set;
	}

	/**
	 * Returns the occurrences of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the occurrences
	 */
	@SuppressWarnings("unchecked")
	public Set<IOccurrence> getOccurrences(ITopic t) {
		Set<IOccurrence> set = HashUtil.getHashSet();
		for (IOccurrence occurrence : (Set<IOccurrence>) getTopicMapStore()
				.doRead(t, TopicMapStoreParameterType.OCCURRENCE)) {
			if (!isRemovedConstruct(occurrence)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub(occurrence));
			}
		}
		if (occurrences != null && occurrences.containsKey(t)) {
			set.addAll(occurrences.get(t));
		}
		return set;
	}

	/**
	 * Returns the variants of the all names.
	 * 
	 * @return the variants
	 */
	public Set<IVariant> getVariants() {
		ILiteralIndex index = getTopicMapStore().getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IVariant> set = HashUtil.getHashSet();
		for (Variant variant : index.getVariants()) {
			if (!isRemovedConstruct((IVariant) variant)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub((IVariant) variant));
			}
		}
		if (variants != null) {
			for (Entry<IName, Set<IVariant>> entry : variants.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		return set;
	}

	/**
	 * Returns the variants of the given name.
	 * 
	 * @param n
	 *            the name
	 * @return the variants
	 */
	@SuppressWarnings("unchecked")
	public Set<IVariant> getVariants(IName n) {
		Set<IVariant> set = HashUtil.getHashSet();
		for (IVariant variant : (Set<IVariant>) getTopicMapStore().doRead(n,
				TopicMapStoreParameterType.VARIANT)) {
			if (!isRemovedConstruct(variant)) {
				set.add(getTransactionStore().getIdentityStore()
						.createLazyStub(variant));
			}
		}
		if (variants != null && variants.containsKey(n)) {
			set.addAll(variants.get(n));
		}
		return set;
	}

	/**
	 * Returns the value of the given object
	 * 
	 * @param obj
	 *            the object
	 * @return the value
	 */
	public Object getValue(IConstruct obj) {
		Object value = null;
		if (values != null && values.containsKey(obj)) {
			value = values.get(obj);
		}
		if (value == null) {
			value = getTopicMapStore().doRead(obj,
					TopicMapStoreParameterType.VALUE);
		}
		return value;
	}

	/**
	 * Modify the value of the given object
	 * 
	 * @param obj
	 *            the object
	 * @param value
	 *            the new value
	 * @return the old value
	 */
	public Object setValue(IConstruct obj, Object value) {
		if (isRemovedConstruct(obj)) {
			throw new ConstructRemovedException(obj);
		}
		Object oldValue = getValue(obj);
		if (values == null) {
			values = HashUtil.getHashMap();
		}
		values.put(obj, value);
		return oldValue;
	}

	/**
	 * Modify the data type of the given data-type-aware
	 * 
	 * @param dataTypeAware
	 *            the data-type-aware
	 * @param dataType
	 *            the new data type
	 * @return the old data type
	 */
	public ILocator setDatatype(IDatatypeAware dataTypeAware, ILocator dataType) {
		if (isRemovedConstruct(dataTypeAware)) {
			throw new ConstructRemovedException(dataTypeAware);
		}
		if (changedDatatypes == null) {
			changedDatatypes = HashUtil.getHashSet();
		}
		changedDatatypes.add(dataTypeAware.getId());
		ILocator oldDatatype = getDatatype(dataTypeAware);
		if (dataTypes == null) {
			dataTypes = HashUtil.getHashMap();
		}
		dataTypes.put(dataTypeAware, dataType);

		if (dataTyped == null) {
			dataTyped = HashUtil.getHashMap();
		}
		/*
		 * remove old data-type mapping
		 */
		if (oldDatatype != null) {
			Set<IDatatypeAware> datatypeAwares = dataTyped.get(oldDatatype);
			if (datatypeAwares != null) {
				datatypeAwares.remove(dataTypeAware);
				if (datatypeAwares.isEmpty()) {
					dataTyped.remove(oldDatatype);
				} else {
					dataTyped.put(oldDatatype, datatypeAwares);
				}
			}
		}

		/*
		 * set new data-type mapping
		 */
		Set<IDatatypeAware> datatypeAwares = dataTyped.get(dataType);
		if (datatypeAwares == null) {
			datatypeAwares = HashUtil.getHashSet();
		}
		datatypeAwares.add(dataTypeAware);
		dataTyped.put(dataType, datatypeAwares);
		return oldDatatype;
	}

	/**
	 * Remove the given topic as parent from the internal store
	 * 
	 * @param topic
	 *            the topic to remove
	 */
	public void removeTopic(ITopic topic) {
		if (names != null) {
			names.remove(topic);
		}
		if (occurrences != null) {
			occurrences.remove(topic);
		}
	}

	/**
	 * Remove the given name from the internal data store.
	 * 
	 * @param n
	 *            the name
	 */
	public void removeName(IName n) {
		if (names != null && names.containsKey(n.getParent())) {
			Set<IName> set = names.get(n.getParent());
			if (set == null) {
				throw new TopicMapStoreException("Unknown topic "
						+ n.toString());
			}
			set.remove(n);
			names.put(n.getParent(), set);

			/*
			 * remove all variants
			 */
			if (variants != null && variants.containsKey(n)) {
				variants.remove(n);
			}

			/*
			 * remove value
			 */
			if (values != null && values.containsKey(n)) {
				values.remove(n);
			}
		}
	}

	/**
	 * Remove the given occurrence from the internal data store.
	 * 
	 * @param v
	 *            the variant
	 */
	public void removeVariant(IVariant v) {
		if (variants != null && variants.containsKey(v.getParent())) {
			Set<IVariant> set = variants.get(v.getParent());
			if (set == null) {
				throw new TopicMapStoreException("Unknown variant "
						+ v.toString());
			}
			set.remove(v);
			variants.put(v.getParent(), set);

			/*
			 * remove data type
			 */
			if (dataTypes != null && dataTypes.containsKey(v)) {
				ILocator l = dataTypes.remove(v);
				Set<IDatatypeAware> datatypeAwares = dataTyped.get(l);
				datatypeAwares.remove(v);
				if (datatypeAwares.isEmpty()) {
					dataTyped.remove(l);
				} else {
					dataTyped.put(l, datatypeAwares);
				}
			}

			/*
			 * remove value
			 */
			if (values != null && values.containsKey(v)) {
				values.remove(v);
			}
		}
	}

	/**
	 * Remove the given occurrence from the internal data store.
	 * 
	 * @param o
	 *            the occurrence
	 */
	public void removeOccurrence(IOccurrence o) {
		if (occurrences != null && occurrences.containsKey(o.getParent())) {
			Set<IOccurrence> set = occurrences.get(o.getParent());
			if (set == null) {
				throw new TopicMapStoreException("Unknown occurrence "
						+ o.toString());
			}
			set.remove(o);
			occurrences.put(o.getParent(), set);

			/*
			 * remove data type
			 */
			if (dataTypes != null && dataTypes.containsKey(o)) {
				ILocator l = dataTypes.remove(o);
				Set<IDatatypeAware> datatypeAwares = dataTyped.get(l);
				datatypeAwares.remove(o);
				if (datatypeAwares.isEmpty()) {
					dataTyped.remove(l);
				} else {
					dataTyped.put(l, datatypeAwares);
				}
			}

			/*
			 * remove value
			 */
			if (values != null && values.containsKey(o)) {
				values.remove(o);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		// NOTHING TO DO
	}

	/**
	 * Returns the characteristics of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the characteristics
	 */
	public Set<ICharacteristics> getCharacteristics(ITopic t) {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getNames(t));
		set.addAll(getOccurrences(t));
		return set;
	}

	/**
	 * Returns the characteristics of all topics.
	 * 
	 * @return the characteristics
	 */
	public Set<ICharacteristics> getCharacteristics() {
		Set<ICharacteristics> set = HashUtil.getHashSet();
		set.addAll(getNames());
		set.addAll(getOccurrences());
		return set;
	}

	/**
	 * Returns the value of the given object
	 * 
	 * @param obj
	 *            the object
	 * @return the value
	 */
	public String getValueAsString(IConstruct obj) {
		Object value = getValue(obj);
		if (obj instanceof IName) {
			return value.toString();
		}
		return DatatypeAwareUtils.toString(value,
				getDatatype((IDatatypeAware) obj));
	}

	/**
	 * @return the topicMapStore
	 */
	public TopicMapStoreImpl getTopicMapStore() {
		return topicMapStore.getRealStore();
	}

	/**
	 * @return the topicMapStore
	 */
	public TransactionTopicMapStore getTransactionStore() {
		return topicMapStore;
	}

	/**
	 * Redirect method call to identity store and check if construct is marked
	 * as removed.
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if the construct was marked as removed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isRemovedConstruct(IConstruct c) {
		return getTransactionStore().getIdentityStore().isRemovedConstruct(c);
	}

}
