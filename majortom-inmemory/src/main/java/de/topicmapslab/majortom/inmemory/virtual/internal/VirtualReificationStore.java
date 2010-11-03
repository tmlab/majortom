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

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

import de.topicmapslab.majortom.inmemory.store.internal.ReificationStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualReificationStore<T extends VirtualTopicMapStore> extends ReificationStore {

	private BidiMap removedReifications;

	/**
	 * @param store
	 */
	public VirtualReificationStore(T store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		super.close();

		if (removedReifications != null) {
			removedReifications.clear();
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
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected T getStore() {
		return (T) super.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getReifier(IReifiable reifiable) {
		ITopic reifier = super.getReifier(reifiable);
		if (reifier == null && !getVirtualIdentityStore().isVirtual(reifiable)) {
			reifier = getVirtualIdentityStore().asVirtualConstruct(
					(ITopic) getStore().getRealStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION));
			if (getVirtualIdentityStore().isRemovedConstruct(reifier)) {
				return null;
			}
			if (reifier != null && removedReifications != null
					&& reifiable.getId().equals(removedReifications.get(reifier.getId()))) {
				return null;
			}
		}
		return reifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable getReified(ITopic reifier) {
		if (getVirtualIdentityStore().isRemovedConstruct(reifier)) {
			throw new ConstructRemovedException(reifier);
		}
		IReifiable reifiable = super.getReified(reifier);
		if (reifiable == null && !getVirtualIdentityStore().isVirtual(reifier)) {
			reifiable = getVirtualIdentityStore().asVirtualConstruct(
					(IReifiable) getStore().getRealStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION));
			if (getVirtualIdentityStore().isRemovedConstruct(reifiable)) {
				return null;
			}
			if (reifiable != null && removedReifications != null
					&& reifier.getId().equals(removedReifications.getKey(reifiable.getId()))) {
				return null;
			}
		}
		return reifiable;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic setReifier(IReifiable reifiable, ITopic reifier) {
		if (getVirtualIdentityStore().isRemovedConstruct(reifiable)) {
			throw new ConstructRemovedException(reifiable);
		}
		if (getVirtualIdentityStore().isRemovedConstruct(reifier)) {
			throw new ConstructRemovedException(reifier);
		}
		ITopic oldReifier = storeOldReification(reifiable);
		super.setReifier(reifiable, reifier);
		return oldReifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeReification(IReifiable reifiable) {
		storeOldReification(reifiable);
		super.removeReification(reifiable);
	}

	/**
	 * Internal method to store old reification
	 * 
	 * @param reifiable
	 *            the reifiable
	 * @return the old reifier
	 */
	private ITopic storeOldReification(IReifiable reifiable) {
		if (!getVirtualIdentityStore().isVirtual(reifiable)) {
			ITopic nonTransactionReifier = null;
			if (reifiable instanceof ITransaction) {
				nonTransactionReifier = getVirtualIdentityStore().asVirtualConstruct(
						(ITopic) getStore().getRealStore().doRead(reifiable.getTopicMap(),
								TopicMapStoreParameterType.REIFICATION));
			} else {
				nonTransactionReifier = getVirtualIdentityStore().asVirtualConstruct(
						(ITopic) getStore().getRealStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION));
			}

			if (nonTransactionReifier != null) {
				if (removedReifications == null || !removedReifications.containsKey(nonTransactionReifier.getId())) {
					if (removedReifications == null) {
						removedReifications = new TreeBidiMap();
					}
					removedReifications.put(nonTransactionReifier.getId(), reifiable.getId());
				}
			}
		}
		return getReifier(reifiable);
	}

	/**
	 * Internal method to store old reification
	 * 
	 * @param reifier
	 *            the reifier
	 * @return the old reifiable
	 */
	private IReifiable storeOldReification(ITopic reifier) {
		if (!getVirtualIdentityStore().isVirtual(reifier)) {
			IReifiable nonTransactionReifiable = getVirtualIdentityStore().asVirtualConstruct(
					(IReifiable) getStore().getRealStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION));
			if (nonTransactionReifiable != null) {
				if (removedReifications == null || !removedReifications.containsValue(nonTransactionReifiable.getId())) {
					if (removedReifications == null) {
						removedReifications = new TreeBidiMap();
					}
					removedReifications.put(reifier.getId(), nonTransactionReifiable.getId());
				}
			}
		}
		return getReified(reifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable removeReifier(ITopic reifier) {
		storeOldReification(reifier);
		return super.removeReifier(reifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		IReifiable reifiable = storeOldReification(topic);
		removeReifier(topic);
		if (reifiable != null) {
			setReifier(reifiable, replacement);
		}
	}

}
