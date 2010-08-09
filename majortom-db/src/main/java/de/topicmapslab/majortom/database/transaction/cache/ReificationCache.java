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
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;

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
	private TreeBidiMap removedReification;

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
		if (removedReification != null) {
			removedReification.clear();
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
		if (reification == null || !reification.containsValue(reifiable)) {
			ITopic reifier = redirectGetReifier(reifiable);
			/*
			 * check if reification already removed
			 */
			if (removedReification != null && removedReification.containsKey(reifier) && removedReification.get(reifier).equals(reifiable)) {
				return null;
			}
		}
		return (ITopic) reification.getKey(reifiable);
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the results
	 * 
	 * @param reifiable
	 *            the reifiable
	 * @return the reifier or <code>null</code>
	 */
	protected ITopic redirectGetReifier(final IReifiable reifiable) {
		ITopic reifier = (ITopic) getTopicMapStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION);
		if (reification == null) {
			reification = new TreeBidiMap();
		}
		if (reifier != null) {
			reification.put(reifier, reifiable);
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
		if (reification == null || !reification.containsKey(reifier)) {
			IReifiable reified = redirectGetReified(reifier);
			/*
			 * check if reification already removed
			 */
			if (removedReification != null && removedReification.containsKey(reifier) && removedReification.get(reifier).equals(reified)) {
				return null;
			}
		}
		return (IReifiable) reification.get(reifier);
	}

	/**
	 * Internal method redirects the call to the underlying topic map store and
	 * cache the results
	 * 
	 * @param reifier
	 *            the reifier
	 * @return the reified construct or <code>null</code>
	 */
	protected IReifiable redirectGetReified(final ITopic reifier) {
		IReifiable reified = (IReifiable) getTopicMapStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION);
		if (reification == null) {
			reification = new TreeBidiMap();
		}
		if (reified != null) {
			reification.put(reifier, reified);
		}
		return reified;
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
		/*
		 * load values into cache
		 */
		ITopic r = getReifier(reifiable);
		if (reifier != null) {
			getReified(reifier);
			if (reification.containsKey(reifier)) {

				/*
				 * get old reification
				 */
				IReifiable old = (IReifiable) reification.get(reifier);
				/*
				 * check if old reifier is the same like new reifier
				 */
				if (!reifiable.equals(old)) {
					throw new ModelConstraintException(reifiable, "Reifier is already bound to another reified item.");
				}
				return reifier;
			}
		}

		/*
		 * remove old reification
		 */
		if (r != null) {
			reification.removeValue(reifiable);
			if (removedReification == null) {
				removedReification = new TreeBidiMap();
			}
			removedReification.put(r, reifiable);
		}
		/*
		 * set new reification
		 */
		if (reifier != null) {
			reification.put(reifier, reifiable);
		}
		return r;
	}

	/**
	 * Remove the reification from the internal store.
	 * 
	 * @param reifiable
	 *            reified item
	 */
	public void removeReification(final IReifiable reifiable) {
		/*
		 * check if reifiable is stored by the current map
		 */
		if (reification != null && reification.containsValue(reifiable)) {
			ITopic reifier = (ITopic) reification.removeValue(reifiable);
			if (removedReification == null) {
				removedReification = new TreeBidiMap();
			}
			removedReification.put(reifier, reifiable);
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
		/*
		 * check if reifiable is stored by the current map
		 */
		if (reification != null && reification.containsKey(reifier)) {
			IReifiable r = (IReifiable) reification.remove(reifier);
			if (removedReification == null) {
				removedReification = new TreeBidiMap();
			}
			removedReification.put(reifier, r);
			return r;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		IReifiable reifiable = getReified(topic);
		getReified(replacement);
		if (reification != null && reification.containsKey(topic)) {
			if (reification.containsKey(replacement)) {
				throw new ModelConstraintException((ITopic) reification.getKey(replacement), "Duplicated reifier!");
			}
			reification.put(replacement, reifiable);
		}
	}

	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	protected TopicMapStoreImpl getTopicMapStore() {
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
