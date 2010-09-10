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
package de.topicmapslab.majortom.database.cache;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
class CharacteristicsCache implements ITopicMapListener {

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
	 * {@inheritDoc}
	 */
	public void clear() {
		if (dataTypes != null) {
			dataTypes.clear();
		}
		if (dataTyped != null) {
			dataTyped.clear();
		}
		if (values != null) {
			values.clear();
		}
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
			return null;
		}
		return dataTypes.get(dataTypeAware);
	}

	/**
	 * Cache the datatype of the given construct to the internal store.
	 * 
	 * @param datatypeAware
	 *            the construct
	 * @param datatype
	 *            the datatype
	 */
	public void cacheDatatype(IDatatypeAware datatypeAware, ILocator datatype) {
		if (dataTypes == null) {
			dataTypes = HashUtil.getHashMap();
		}
		dataTypes.put(datatypeAware, datatype);
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
			return null;
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
		if (value == null) {
			return null;
		}
		if (obj instanceof IName) {
			return value.toString();
		}
		ILocator datatype = getDatatype((IDatatypeAware) obj);
		if (datatype == null) {
			return null;
		}
		return DatatypeAwareUtils.toString(value, datatype);
	}

	/**
	 * Cache the value to the given construct into internal cache
	 * 
	 * @param construct
	 *            the construct
	 * @param value
	 *            the value
	 */
	public void cacheValue(IConstruct construct, Object value) {
		if (values == null) {
			values = HashUtil.getHashMap();
		}
		Object value_ = value;
		if (value instanceof Calendar) {
			value_ = DatatypeAwareUtils.cloneCalendar((Calendar) value);
		}
		values.put(construct, value_);
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
			return null;
		}
		return dataTyped.get(locator);
	}

	/**
	 * Cache the given data-typed items with the given data type into internal
	 * cache.
	 * 
	 * @param locator
	 *            the data type
	 * @param set
	 *            all data-typed items
	 */
	public void cacheDatatypeAwares(ILocator locator, Set<IDatatypeAware> set) {
		if (dataTyped == null) {
			dataTyped = HashUtil.getHashMap();
		}
		dataTyped.put(locator, set);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * value was modified
		 */
		if (event == TopicMapEventType.VALUE_MODIFIED) {
			cacheValue((IConstruct) notifier, newValue);
		}
		/*
		 * datatype was modified
		 */
		else if (event == TopicMapEventType.DATATYPE_SET) {
			cacheDatatype((IDatatypeAware) notifier, (ILocator) newValue);
			if (dataTyped != null) {
				ILocator oldDt = (ILocator) oldValue;
				if (dataTyped.containsKey(oldDt)) {
					dataTyped.get(oldDt).remove(notifier);
				}
				ILocator newDt = (ILocator) newValue;
				if (dataTyped.containsKey(newDt)) {
					dataTyped.get(newDt).add((IDatatypeAware) notifier);
				}
			}
		}
		/*
		 * construct was removed
		 */
		else if (event == TopicMapEventType.NAME_REMOVED
				|| event == TopicMapEventType.OCCURRENCE_REMOVED
				|| event == TopicMapEventType.VARIANT_REMOVED) {
			/*
			 * clear value mapping
			 */
			if (values != null) {
				values.remove(oldValue);
			}
			/*
			 * clear datatype mapping
			 */
			if (oldValue instanceof IDatatypeAware) {
				ILocator locator = getDatatype((IDatatypeAware) oldValue);
				if (locator != null) {
					dataTypes.remove(notifier);
					if (dataTyped != null && dataTyped.containsKey(locator)) {
						dataTyped.get(locator).remove(oldValue);
					}
				} else if (dataTyped != null) {
					// clear whole cache -> because the datatype is unknown
					dataTyped.clear();
				}

			}
		}
	}
}
