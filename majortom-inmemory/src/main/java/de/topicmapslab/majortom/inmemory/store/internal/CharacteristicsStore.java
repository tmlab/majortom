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
package de.topicmapslab.majortom.inmemory.store.internal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class CharacteristicsStore implements IDataStore {

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

	/**
	 * constructor
	 * 
	 * @param xsdString
	 *            the xsd:string locator
	 */
	public CharacteristicsStore(ILocator xsdString) {
		this.xsdString = xsdString;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
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
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
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
		if (set.isEmpty()) {
			return Collections.emptySet();
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
	public Set<IName> getNames(ITopic t) {
		if (names == null || !names.containsKey(t)) {
			return Collections.emptySet();
		}
		return names.get(t);
	}

	/**
	 * Returns the names of all topics.
	 * 
	 * @return the names
	 */
	public Set<IName> getNames() {
		Set<IName> set = HashUtil.getHashSet();
		if (names != null) {
			for (Entry<ITopic, Set<IName>> entry : names.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
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
	public Set<IOccurrence> getOccurrences(ITopic t) {
		if (occurrences == null || !occurrences.containsKey(t)) {
			return Collections.emptySet();
		}
		return occurrences.get(t);
	}

	/**
	 * Returns the occurrences of all topics.
	 * 
	 * @return the occurrences
	 */
	public Set<IOccurrence> getOccurrences() {
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (occurrences != null) {
			for (Entry<ITopic, Set<IOccurrence>> entry : occurrences.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
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
	public Set<IVariant> getVariants(IName n) {
		if (variants == null || !variants.containsKey(n)) {
			return Collections.emptySet();
		}
		return variants.get(n);
	}

	/**
	 * Returns the variants of the all names.
	 * 
	 * @return the variants
	 */
	public Set<IVariant> getVariants() {
		Set<IVariant> set = HashUtil.getHashSet();
		if (variants != null) {
			for (Entry<IName, Set<IVariant>> entry : variants.entrySet()) {
				set.addAll(entry.getValue());
			}
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Remove the given name from the internal data store.
	 * 
	 * @param n
	 *            the name
	 */
	public void removeName(IName n) {
		if (names == null || !names.containsKey(n.getParent())) {
			return;
		}

		Set<IName> set = names.get(n.getParent());
		set.remove(n);
		if (set.isEmpty()) {
			names.remove(n.getParent());
		}

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

	/**
	 * Remove the given occurrence from the internal data store.
	 * 
	 * @param o
	 *            the occurrence
	 */
	public void removeOccurrence(IOccurrence o) {
		if (occurrences == null || !occurrences.containsKey(o.getParent())) {
			return;
		}

		Set<IOccurrence> set = occurrences.get(o.getParent());
		set.remove(o);
		if (set.isEmpty()) {
			occurrences.remove(o.getParent());
		}

		/*
		 * remove data type
		 */
		if (dataTypes != null && dataTypes.containsKey(o)) {
			ILocator l = dataTypes.remove(o);
			Set<IDatatypeAware> datatypeAwares = dataTyped.get(l);
			datatypeAwares.remove(o);
			if (datatypeAwares.isEmpty()) {
				dataTyped.remove(l);
			}
		}

		/*
		 * remove value
		 */
		if (values != null && values.containsKey(o)) {
			values.remove(o);
		}
	}

	/**
	 * Remove the given occurrence from the internal data store.
	 * 
	 * @param v
	 *            the variant
	 */
	public void removeVariant(IVariant v) {
		if (variants == null || !variants.containsKey(v.getParent())) {
			return;
		}

		/*
		 * remove variant
		 */
		Set<IVariant> set = variants.get(v.getParent());
		Set<IVariant> newSet = HashUtil.getHashSet();
		for ( IVariant var : set ){
			if ( !v.equals(var)){
				newSet.add(var);
			}
		}
		if (newSet.isEmpty()) {
			variants.remove(v.getParent());
		}else{
			variants.put(v.getParent(), newSet);
		}

		/*
		 * remove data type
		 */
		if (dataTypes != null && dataTypes.containsKey(v)) {
			ILocator l = dataTypes.remove(v);
			Set<IDatatypeAware> datatypeAwares = dataTyped.get(l);
			datatypeAwares.remove(v);
			if (datatypeAwares.isEmpty()) {
				dataTyped.remove(l);
			}
		}

		/*
		 * remove value
		 */
		if (values != null && values.containsKey(v)) {
			values.remove(v);
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
		if (names == null) {
			names = HashUtil.getHashMap();
		}

		Set<IName> set = names.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
			names.put(t, set);
		}
		set.add(n);
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
		if (occurrences == null) {
			occurrences = HashUtil.getHashMap();
		}

		Set<IOccurrence> set = occurrences.get(t);
		if (set == null) {
			set = HashUtil.getHashSet();
			occurrences.put(t, set);
		}
		set.add(o);
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
		if (variants == null) {
			variants = HashUtil.getHashMap();
		}

		Set<IVariant> set = variants.get(n);
		if (set == null) {
			set = HashUtil.getHashSet();
			variants.put(n, set);
		}
		set.add(v);
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
		if (dataTypes == null || !dataTypes.containsKey(dataTypeAware)) {
			return xsdString;
		}
		return dataTypes.get(dataTypeAware);
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
		ILocator oldDataType = getDatatype(dataTypeAware);
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
		if (oldDataType != null) {
			Set<IDatatypeAware> datatypeAwares = dataTyped.get(oldDataType);
			if (datatypeAwares != null) {
				datatypeAwares.remove(dataTypeAware);
				if (datatypeAwares.isEmpty()) {
					dataTyped.remove(oldDataType);
				}
			}
		}

		/*
		 * set new data-type mapping
		 */
		Set<IDatatypeAware> datatypeAwares = dataTyped.get(dataType);
		if (datatypeAwares == null) {
			datatypeAwares = HashUtil.getHashSet();
			dataTyped.put(dataType, datatypeAwares);
		}
		datatypeAwares.add(dataTypeAware);

		return oldDataType;
	}

	/**
	 * Returns the value of the given object
	 * 
	 * @param obj
	 *            the object
	 * @return the value
	 */
	public Object getValue(IConstruct obj) {
		if (values == null || !values.containsKey(obj)) {
			throw new TopicMapStoreException(
					"Value for construct does not exist!");
		}
		Object value = values.get(obj);
		if (value instanceof Calendar) {
			return DatatypeAwareUtils.cloneCalendar((Calendar) value);
		}
		return value;
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
	 * Modify the value of the given object
	 * 
	 * @param obj
	 *            the object
	 * @param value
	 *            the new value
	 * @return the old value
	 */
	public Object setValue(IConstruct obj, Object value) {
		Object oldValue = null;
		if (values == null) {
			values = HashUtil.getHashMap();
		} else {
			oldValue = values.get(obj);
		}
		Object value_ = value;
		if (value instanceof Calendar) {
			value_ = DatatypeAwareUtils.cloneCalendar((Calendar) value);
		}
		values.put(obj, value_);

		return oldValue;
	}

	/**
	 * Remove the given topic as parent from the internal store
	 * 
	 * @param topic
	 *            the topic to remove
	 */
	public void removeTopic(final ITopic topic) {
		if (names != null && names.containsKey(topic)) {
			for (IName n : HashUtil.getHashSet(names.get(topic))) {
				removeName(n);
			}
			this.names.remove(topic);
		}
		if (occurrences != null && occurrences.containsKey(topic)) {
			for (IOccurrence o : HashUtil.getHashSet(occurrences.get(topic))) {
				removeOccurrence(o);
			}
			this.occurrences.remove(topic);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		// NOTHING TO DO
	}

	/**
	 * Returns all data-typed items with the given data type.
	 * 
	 * @param locator
	 *            the data type
	 * @return a set
	 */
	public Set<IDatatypeAware> getDatatypeAwares(ILocator locator) {
		if (dataTyped == null || !dataTyped.containsKey(locator)) {
			return Collections.emptySet();
		}
		return dataTyped.get(locator);
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

}
