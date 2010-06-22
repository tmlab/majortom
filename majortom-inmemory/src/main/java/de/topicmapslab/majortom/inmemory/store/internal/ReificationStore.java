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

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.tmapi.core.ModelConstraintException;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.revision.IRevision;

/**
 * Internal data store of reifier-reified relations.
 * 
 * @author Sven Krosse
 * 
 */
public class ReificationStore implements IDataStore {

	/**
	 * internal storage map if reifier-reified relation
	 */
	private TreeBidiMap reification;

	/**
	 * the parent store
	 */
	private final InMemoryTopicMapStore store;

	/**
	 * constructor
	 * 
	 * @param store the parent store
	 */
	public ReificationStore(final InMemoryTopicMapStore store) {
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (reification != null) {
			reification.clear();
		}
	}

	/***
	 * Returns the current stored reifier of the given reified item.
	 * 
	 * @param reifiable the reified item
	 * @return the reifier or <code>null</code>
	 */
	public ITopic getReifier(final IReifiable reifiable) {
		if (reification == null || !reification.containsValue(reifiable)) {
			return null;
		}
		return (ITopic) reification.getKey(reifiable);
	}

	/**
	 * Returns the reified item of the given reifier
	 * 
	 * @param reifier the reifier
	 * @return the reified item or <code>null</code>
	 */
	public IReifiable getReified(final ITopic reifier) {
		if (reification == null || !reification.containsKey(reifier)) {
			return null;
		}
		return (IReifiable) reification.get(reifier);
	}

	/**
	 * Create a new relation between the given reifier and the reified construct
	 * 
	 * @param reifiable the reified construct
	 * @param reifier the reifier
	 * @return the old reifier or <code>null</code>
	 */
	public ITopic setReifier(final IReifiable reifiable, final ITopic reifier) {
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
		return r;
	}

	/**
	 * Remove the reification from the internal store.
	 * 
	 * @param reifiable reified item
	 */
	public void removeReification(final IReifiable reifiable) {
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
	 * @param reifiable reified item
	 * @return the reified item
	 */
	public IReifiable removeReifier(final ITopic reifier) {
		/*
		 * check if reifiable is stored by the current map
		 */
		if (reification != null && reification.containsKey(reifier)) {
			return (IReifiable) reification.remove(reifier);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void replace(ITopic topic, ITopic replacement, IRevision revision) {
		if (reification != null && reification.containsKey(topic)) {
			if (reification.containsKey(replacement)) {
				throw new ModelConstraintException((ITopic) reification.getKey(replacement), "Duplicated reifier!");
			}
			IReifiable reifiable = getReified(topic);
			reification.put(replacement, reifiable);
			store.storeRevision(revision, TopicMapEventType.REIFIER_SET, reifiable, replacement, topic);
		}
	}
	
	/**
	 * Return the internal stored store instance.
	 * 
	 * @return the store the store instance
	 */
	protected InMemoryTopicMapStore getStore() {
		return store;
	}
}
