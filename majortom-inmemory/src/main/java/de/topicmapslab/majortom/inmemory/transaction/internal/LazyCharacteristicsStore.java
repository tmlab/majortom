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
package de.topicmapslab.majortom.inmemory.transaction.internal;

import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.inmemory.store.internal.CharacteristicsStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
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
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyCharacteristicsStore extends CharacteristicsStore {

	private final ITopicMapStore store;

	private Set<String> changedDatatypes;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the transaction store
	 * @param xsdString
	 *            the locator of XSD string
	 */
	public LazyCharacteristicsStore(ITopicMapStore store, ILocator xsdString) {
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
	 * Internal method to access the lazy-identity store of this transaction
	 * context
	 * 
	 * @return the lazy identity store
	 */
	protected LazyIdentityStore getLazyIdentityStore() {
		return ((LazyIdentityStore) getStore().getIdentityStore());
	}

	/**
	 * Returns the internal reference of the topic map store.
	 * 
	 * @return the topic map store
	 */
	protected InMemoryTransactionTopicMapStore getStore() {
		return (InMemoryTransactionTopicMapStore) store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addName(ITopic t, IName n) {
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getLazyIdentityStore().isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		super.addName(t, n);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addOccurrence(ITopic t, IOccurrence o) {
		if (getLazyIdentityStore().isRemovedConstruct(t)) {
			throw new ConstructRemovedException(t);
		}
		if (getLazyIdentityStore().isRemovedConstruct(o)) {
			throw new ConstructRemovedException(o);
		}
		super.addOccurrence(t, o);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addVariant(IName n, IVariant v) {
		if (getLazyIdentityStore().isRemovedConstruct(n)) {
			throw new ConstructRemovedException(n);
		}
		if (getLazyIdentityStore().isRemovedConstruct(v)) {
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
		return (ILocator) getStore().getRealStore().doRead(dataTypeAware,
				TopicMapStoreParameterType.DATATYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<IDatatypeAware> getDatatypeAwares(ILocator locator) {
		ILiteralIndex index = getStore().getRealStore().getIndex(
				ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IDatatypeAware> set = HashUtil.getHashSet();
		for (IDatatypeAware datatypeAware : index.getDatatypeAwares(locator)) {
			if (getLazyIdentityStore().isRemovedConstruct(datatypeAware)) {
				continue;
			}
			if (changedDatatypes == null
					|| !changedDatatypes.contains(datatypeAware.getId())) {
				set.add(getLazyIdentityStore().createLazyStub(datatypeAware));
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
		ILiteralIndex index = getStore().getRealStore().getIndex(
				ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IName> set = HashUtil.getHashSet();
		for (Name name : index.getNames()) {
			if (!getLazyIdentityStore().isRemovedConstruct((IName)name)) {
				set.add(getLazyIdentityStore().createLazyStub((IName)name));
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
		for (IName name : (Set<IName>) getStore().getRealStore().doRead(t,
				TopicMapStoreParameterType.NAME)) {
			if (!getLazyIdentityStore().isRemovedConstruct(name)) {
				set.add(getLazyIdentityStore().createLazyStub(name));
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
		ILiteralIndex index = getStore().getRealStore().getIndex(
				ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IOccurrence> set = HashUtil.getHashSet();
		for ( Occurrence occurrence : index.getOccurrences()) {
			if (!getLazyIdentityStore().isRemovedConstruct((IOccurrence)occurrence)) {
				set.add(getLazyIdentityStore().createLazyStub((IOccurrence)occurrence));
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
		for (IOccurrence occurrence : (Set<IOccurrence>) getStore()
				.getRealStore()
				.doRead(t, TopicMapStoreParameterType.OCCURRENCE)) {
			if (!getLazyIdentityStore().isRemovedConstruct(occurrence)) {
				set.add(getLazyIdentityStore().createLazyStub(occurrence));
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
		ILiteralIndex index = getStore().getRealStore().getIndex(
				ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		Set<IVariant> set = HashUtil.getHashSet();
		for (Variant variant : index.getVariants()) {
			if (!getLazyIdentityStore().isRemovedConstruct((IVariant)variant)) {
				set.add(getLazyIdentityStore().createLazyStub((IVariant)variant));
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
		for (IVariant variant : (Set<IVariant>) getStore().getRealStore()
				.doRead(n, TopicMapStoreParameterType.VARIANT)) {
			if (!getLazyIdentityStore().isRemovedConstruct(variant)) {
				set.add(getLazyIdentityStore().createLazyStub(variant));
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
		if (getLazyIdentityStore().isRemovedConstruct(obj)) {
			throw new ConstructRemovedException(obj);
		}
		try {
			return super.getValue(obj);
		} catch (TopicMapStoreException e) {
			try {
				return getStore().getRealStore().doRead(obj,
						TopicMapStoreParameterType.VALUE);
			} catch (TopicMapStoreException ex) {
				// THROWN IF CONSTRUCT IS NOT CREATED YET
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object setValue(IConstruct obj, Object value) {
		if (getLazyIdentityStore().isRemovedConstruct(obj)) {
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
		if (getLazyIdentityStore().isRemovedConstruct(dataTypeAware)) {
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

}
