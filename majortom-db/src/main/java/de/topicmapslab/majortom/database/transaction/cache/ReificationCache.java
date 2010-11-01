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

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.tmapi.core.ModelConstraintException;

import de.topicmapslab.majortom.database.transaction.TransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.store.ModifableTopicMapStoreImpl;

/**
 * Internal data store of reifier-reified relations.
 * 
 * @author Sven Krosse
 * 
 */
public class ReificationCache implements IDataStore {

	/**
	 * internal storage map if reifier-reified relation
	 */
	private TreeBidiMap reification;

	/**
	 * internal storage map if reifier-reified relation
	 */
	private TreeBidiMap removedReifications;

	/**
	 * the parent store
	 */
	private final TransactionTopicMapStore topicMapStore;

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the transaction store
	 */
	public ReificationCache(final TransactionTopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (reification != null) {
			reification.clear();
		}
		if (removedReifications != null) {
			removedReifications.clear();
		}
	}

	/***
	 * Returns the current stored reifier of the given reified item.
	 * 
	 * @param reifiable
	 *            the reified item
	 * @return the reifier or <code>null</code>
	 */
	public ITopic getReifier(final IReifiable reifiable) {
		if (isRemovedConstruct(reifiable)) {
			throw new TopicMapStoreException("Construct is already marked as removed!");
		}
		ITopic reifier = null;
		if ( reification != null && reification.containsValue(reifiable)){
			reifier = (ITopic) reification.getKey(reifiable);
		}
		if (reifier == null) {
			reifier = getTransactionStore().getIdentityStore().createLazyStub((ITopic) getTopicMapStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION));
			if (isRemovedConstruct(reifier)) {
				return null;
			}
			if (reifier != null && removedReifications != null && reifiable.getId().equals(removedReifications.get(reifier.getId()))) {
				return null;
			}
		}
		return reifier;
	}

	/**
	 * Returns the reified item of the given reifier
	 * 
	 * @param reifier
	 *            the reifier
	 * @return the reified item or <code>null</code>
	 */
	public IReifiable getReified(final ITopic reifier) {
		if (isRemovedConstruct(reifier)) {
			throw new ConstructRemovedException(reifier);
		}
		IReifiable reifiable = null;
		if ( reification != null && reification.containsKey(reifier)){
			reifiable = (IReifiable) reification.get(reifier);
		}
		if (reifiable == null) {
			reifiable =  getTransactionStore().getIdentityStore().createLazyStub((IReifiable) getTopicMapStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION));
			if (isRemovedConstruct(reifiable)) {
				return null;
			}
			if (reifiable != null && removedReifications != null && reifier.getId().equals(removedReifications.getKey(reifiable.getId()))) {
				return null;
			}
		}
		return reifiable;
	}

	/**
	 * Create a new relation between the given reifier and the reified construct
	 * 
	 * @param reifiable
	 *            the reified construct
	 * @param reifier
	 *            the reifier
	 * @return the old reifier or <code>null</code>
	 */
	public ITopic setReifier(final IReifiable reifiable, final ITopic reifier) {
		if (isRemovedConstruct(reifiable)) {
			throw new ConstructRemovedException(reifiable);
		}
		if (isRemovedConstruct(reifier)) {
			throw new ConstructRemovedException(reifier);
		}
		ITopic oldReifier = storeOldReification(reifiable);
		if (reification == null) {
			reification = new TreeBidiMap();
		}

		if (reifier != null && reification.containsKey(reifier)) {
			/*
			 * get old reification
			 */
			IReifiable old = (IReifiable) reification.get(reifier);
			/*
			 * check if old reifier is the same like new reifier
			 */
			if (!old.equals(reifiable)) {
				throw new ModelConstraintException(reifiable, "Reifier is already bound to another reified item.");
			}
			return reifier;
		}

		/*
		 * remove old reification
		 */
		ITopic r = getReifier(reifiable);
		if (r != null) {
			reification.removeValue(reifiable);
		}
		/*
		 * set new reification
		 */
		if (reifier != null) {
			reification.put(reifier, reifiable);
		}
		return oldReifier;
	}

	/**
	 * Remove the reification from the internal store.
	 * 
	 * @param reifiable
	 *            reified item
	 */
	public void removeReification(final IReifiable reifiable) {
		storeOldReification(reifiable);
		/*
		 * check if reifiable is stored by the current map
		 */
		if (reification != null && reification.containsValue(reifiable)) {
			reification.removeValue(reifiable);
		}
	}

	/**
	 * Remove the reifier from the internal store.
	 * 
	 * @param reifiable
	 *            reified item
	 * @return the reified item
	 */
	public IReifiable removeReifier(final ITopic reifier) {
		storeOldReification(reifier);
		/*
		 * check if reifiable is stored by the current map
		 */
		if (reification != null && reification.containsKey(reifier)) {
			return (IReifiable) reification.remove(reifier);
		}
		return null;
	}
	
	/**
	 * Internal method to store old reification
	 * 
	 * @param reifiable
	 *            the reifiable
	 * @return the old reifier
	 */
	private ITopic storeOldReification(IReifiable reifiable) {
		ITopic nonTransactionReifier = null;
		if (reifiable instanceof ITransaction) {
			nonTransactionReifier = getTransactionStore().getIdentityStore().createLazyStub(
					(ITopic) getTopicMapStore().doRead(reifiable.getTopicMap(), TopicMapStoreParameterType.REIFICATION));
		} else {
			nonTransactionReifier = getTransactionStore().getIdentityStore().createLazyStub(
					(ITopic) getTopicMapStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION));
		}

		if (nonTransactionReifier != null) {
			if (removedReifications == null || !removedReifications.containsKey(nonTransactionReifier.getId())) {
				if (removedReifications == null) {
					removedReifications = new TreeBidiMap();
				}
				removedReifications.put(nonTransactionReifier.getId(), reifiable.getId());
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
		IReifiable nonTransactionReifiable = getTransactionStore().getIdentityStore().createLazyStub(
				(IReifiable) getTopicMapStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION));
		if (nonTransactionReifiable != null) {
			if (removedReifications == null || !removedReifications.containsValue(nonTransactionReifiable.getId())) {
				if (removedReifications == null) {
					removedReifications = new TreeBidiMap();
				}
				removedReifications.put(reifier.getId(), nonTransactionReifiable.getId());
			}
		}
		return getReified(reifier);
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

	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	protected ModifableTopicMapStoreImpl getTopicMapStore() {
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
