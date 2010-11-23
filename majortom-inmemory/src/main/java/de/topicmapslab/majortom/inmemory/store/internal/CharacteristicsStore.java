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
import java.util.regex.Pattern;

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
	 * reverse map to speed up the literal index
	 */
	private Map<String, Set<IName>> namesByValue;

	/**
	 * reverse map to speed up the literal index
	 */
	private Map<String, Set<IOccurrence>> occurrencesByValue;

	/**
	 * reverse map to speed up the literal index
	 */
	private Map<String, Set<IVariant>> variantsByValue;

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
		if (namesByValue != null) {
			namesByValue.clear();
		}
		if (occurrencesByValue != null) {
			occurrencesByValue.clear();
		}
		if (variantsByValue != null) {
			variantsByValue.clear();
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
	 * Returns the names matching the given value.
	 * 
	 * @param value
	 *            the value
	 * @param isRegExp
	 *            Flag if the value is a regular expression
	 * @return the names
	 */
	public Set<IName> getNamesByValue(final String value, boolean isRegExp) {
		/*
		 * redirect if value is regular expression
		 */
		if (isRegExp) {
			return getNamesByValue(Pattern.compile(value));
		}
		Set<IName> set = HashUtil.getHashSet();
		if (namesByValue != null && namesByValue.containsKey(value)) {
			set.addAll(namesByValue.get(value));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Returns the names matching the given regular expression.
	 * 
	 * @param regExp
	 *            a regular expression
	 * @return the names
	 */
	public Set<IName> getNamesByValue(final Pattern regExp) {
		Set<IName> set = HashUtil.getHashSet();
		if (namesByValue != null) {
			for (Entry<String, Set<IName>> entry : namesByValue.entrySet()) {
				if (regExp.matcher(entry.getKey()).matches()) {
					set.addAll(entry.getValue());
				}
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
	 * Returns the occurrence matching the given value.
	 * 
	 * @param value
	 *            the value
	 * @param isRegExp
	 *            Flag if the value is a regular expression
	 * @return the occurrences
	 */
	public Set<IOccurrence> getOccurrencesByValue(final String value, boolean isRegExp) {
		/*
		 * redirect if value is regular expression
		 */
		if (isRegExp) {
			return getOccurrencesByValue(Pattern.compile(value));
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (occurrencesByValue != null && occurrencesByValue.containsKey(value)) {
			set.addAll(occurrencesByValue.get(value));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Returns the occurrences matching the given regular expression.
	 * 
	 * @param regExp
	 *            a regular expression
	 * @return the occurrences
	 */
	public Set<IOccurrence> getOccurrencesByValue(final Pattern regExp) {
		Set<IOccurrence> set = HashUtil.getHashSet();
		if (occurrencesByValue != null) {
			for (Entry<String, Set<IOccurrence>> entry : occurrencesByValue.entrySet()) {
				if (regExp.matcher(entry.getKey()).matches()) {
					set.addAll(entry.getValue());
				}
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
	 * Returns the variants matching the given value.
	 * 
	 * @param value
	 *            the value
	 * @param isRegExp
	 *            Flag if the value is a regular expression
	 * @return the variants
	 */
	public Set<IVariant> getVariantsByValue(final String value, boolean isRegExp) {
		/*
		 * redirect if value is regular expression
		 */
		if (isRegExp) {
			return getVariantsByValue(Pattern.compile(value));
		}
		Set<IVariant> set = HashUtil.getHashSet();
		if (variantsByValue != null && variantsByValue.containsKey(value)) {
			set.addAll(variantsByValue.get(value));
		}
		if (set.isEmpty()) {
			return Collections.emptySet();
		}
		return set;
	}

	/**
	 * Returns the variants matching the given regular expression.
	 * 
	 * @param regExp
	 *            a regular expression
	 * @return the variants
	 */
	public Set<IVariant> getVariantsByValue(final Pattern regExp) {
		Set<IVariant> set = HashUtil.getHashSet();
		if (variantsByValue != null) {
			for (Entry<String, Set<IVariant>> entry : variantsByValue.entrySet()) {
				if (regExp.matcher(entry.getKey()).matches()) {
					set.addAll(entry.getValue());
				}
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
		if (names != null && names.containsKey(n.getParent())) {
			Set<IName> set = names.get(n.getParent());
			set.remove(n);
			if (set.isEmpty()) {
				names.remove(n.getParent());
			}
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
			Object value = values.remove(n);
			if (namesByValue != null && namesByValue.containsKey(value.toString())) {
				Set<IName> constructs = namesByValue.get(value.toString());
				constructs.remove(n);
				if (constructs.isEmpty()) {
					namesByValue.remove(value.toString());
				}
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
			Object value = values.remove(o);
			if (occurrencesByValue != null && occurrencesByValue.containsKey(value.toString())) {
				Set<IOccurrence> constructs = occurrencesByValue.get(value.toString());
				constructs.remove(o);
				if (constructs.isEmpty()) {
					occurrencesByValue.remove(value.toString());
				}
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
		if (variants == null || !variants.containsKey(v.getParent())) {
			return;
		}

		/*
		 * remove variant
		 */
		Set<IVariant> set = variants.get(v.getParent());
		Set<IVariant> newSet = HashUtil.getHashSet();
		for (IVariant var : set) {
			if (!v.equals(var)) {
				newSet.add(var);
			}
		}
		if (newSet.isEmpty()) {
			variants.remove(v.getParent());
		} else {
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
			Object value = values.remove(v);
			if (variantsByValue != null && variantsByValue.containsKey(value.toString())) {
				Set<IVariant> constructs = variantsByValue.get(value.toString());
				constructs.remove(v);
				if (constructs.isEmpty()) {
					variantsByValue.remove(value.toString());
				}
			}
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
			throw new TopicMapStoreException("Value for construct does not exist!");
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
		return DatatypeAwareUtils.toString(value, getDatatype((IDatatypeAware) obj));
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

		/*
		 * update the reverse value-constructs entry
		 */
		if (obj instanceof IName) {
			updateReverseValueMapping((IName) obj, oldValue == null ? null : oldValue.toString(), value.toString());
		} else if (obj instanceof IOccurrence) {
			updateReverseValueMapping((IOccurrence) obj, oldValue == null ? null : oldValue.toString(), value.toString());
		} else if (obj instanceof IVariant) {
			updateReverseValueMapping((IVariant) obj, oldValue == null ? null : oldValue.toString(), value.toString());
		}

		return oldValue;
	}

	/**
	 * Internal method to update the reverse mapping of a value and the constructs
	 * 
	 * @param name
	 *            the name
	 * @param oldValue
	 *            the old value
	 * @param value
	 *            the new value
	 */
	private void updateReverseValueMapping(IName name, String oldValue, String value) {
		/*
		 * initialize construct-value mapping
		 */
		if (namesByValue == null) {
			namesByValue = HashUtil.getHashMap();
		}
		/*
		 * remove old value if exists
		 */
		if (oldValue != null && namesByValue.containsKey(oldValue)) {
			Set<IName> constructs = namesByValue.get(oldValue);
			constructs.remove(name);
			if (constructs.isEmpty()) {
				namesByValue.remove(oldValue.toString());
			}
		}
		/*
		 * set new value
		 */
		Set<IName> constructs = namesByValue.get(value);
		if (constructs == null) {
			constructs = HashUtil.getHashSet();
			namesByValue.put(value, constructs);
		}
		constructs.add(name);
	}

	/**
	 * Internal method to update the reverse mapping of a value and the constructs
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param oldValue
	 *            the old value
	 * @param value
	 *            the new value
	 */
	private void updateReverseValueMapping(IOccurrence occurrence, String oldValue, String value) {
		/*
		 * initialize construct-value mapping
		 */
		if (occurrencesByValue == null) {
			occurrencesByValue = HashUtil.getHashMap();
		}
		/*
		 * remove old value if exists
		 */
		if (oldValue != null && occurrencesByValue.containsKey(oldValue)) {
			Set<IOccurrence> constructs = occurrencesByValue.get(oldValue);
			constructs.remove(occurrence);
			if (constructs.isEmpty()) {
				occurrencesByValue.remove(oldValue.toString());
			}
		}
		/*
		 * set new value
		 */
		Set<IOccurrence> constructs = occurrencesByValue.get(value);
		if (constructs == null) {
			constructs = HashUtil.getHashSet();
			occurrencesByValue.put(value, constructs);
		}
		constructs.add(occurrence);
	}

	/**
	 * Internal method to update the reverse mapping of a value and the constructs
	 * 
	 * @param variant
	 *            the variant
	 * @param oldValue
	 *            the old value
	 * @param value
	 *            the new value
	 */
	private void updateReverseValueMapping(IVariant variant, String oldValue, String value) {
		/*
		 * initialize construct-value mapping
		 */
		if (variantsByValue == null) {
			variantsByValue = HashUtil.getHashMap();
		}
		/*
		 * remove old value if exists
		 */
		if (oldValue != null && variantsByValue.containsKey(oldValue)) {
			Set<IVariant> constructs = variantsByValue.get(oldValue);
			constructs.remove(variant);
			if (constructs.isEmpty()) {
				variantsByValue.remove(oldValue.toString());
			}
		}
		/*
		 * set new value
		 */
		Set<IVariant> constructs = variantsByValue.get(value);
		if (constructs == null) {
			constructs = HashUtil.getHashSet();
			variantsByValue.put(value, constructs);
		}
		constructs.add(variant);
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
	 * Checks if the store contains any locator of the given {@link IDatatypeAware}.
	 * 
	 * @param aware
	 *            the {@link IDatatypeAware}
	 * @return <code>true</code> if any locator of the given {@link IDatatypeAware} is stored, <code>false</code>
	 *         otherwise.
	 */
	protected final boolean containsDatatype(IDatatypeAware aware) {
		return dataTypes != null && dataTypes.containsKey(aware);
	}

	/**
	 * @return the dataTyped
	 */
	protected Map<ILocator, Set<IDatatypeAware>> getDataTypedMap() {
		return dataTyped;
	}
	
	/**
	 * @return the dataTypes
	 */
	protected Map<IDatatypeAware, ILocator> getDataTypesMap() {
		return dataTypes;
	}
		
	protected Map<IConstruct, Object> getValuesMap() {
		return values;
	}
	
	protected Map<ITopic, Set<IName>> getNamesMap(){
		return names;
	}
	
	protected Map<ITopic, Set<IOccurrence>> getOccurrencesMap(){
		return occurrences;
	}
	
	protected Map<IName, Set<IVariant>> getVariantsMap(){
		return variants;
	}
	
	protected Map<String, Set<IName>> getNamesByValueMap(){
		return namesByValue;
	}
	
	protected Map<String, Set<IOccurrence>> getOccurrencesByValueMap(){
		return occurrencesByValue;
	}
	
	protected Map<String, Set<IVariant>> getVariantsByValueMap(){
		return variantsByValue;
	}
}

