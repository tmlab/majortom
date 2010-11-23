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
package de.topicmapslab.majortom.queued.store;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.virtual.VirtualTopicMapStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualAssociationStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualCharacteristicsStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualIdentityStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualReificationStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualScopeStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualTopicTypeStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualTypedStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * @author Sven Krosse
 * 
 */
public class VirtualInMemoryTopicMapStore extends VirtualTopicMapStore {

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 * @param store
	 *            the real topic map store
	 */
	public VirtualInMemoryTopicMapStore(ITopicMapSystem topicMapSystem, JdbcTopicMapStore store) {
		super(topicMapSystem, store);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualIdentityStore<?> createIdentityStore(InMemoryTopicMapStore store) {
		return new VirtualIdentityStore<VirtualInMemoryTopicMapStore>(this, getCapacityOfCollections());
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualAssociationStore<?> createAssociationStore(InMemoryTopicMapStore store) {
		return new VirtualAssociationStore<VirtualInMemoryTopicMapStore>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualReificationStore<?> createReificationStore(InMemoryTopicMapStore store) {
		return new VirtualReificationStore<VirtualInMemoryTopicMapStore>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualTypedStore<?> createTypedStore(InMemoryTopicMapStore store) {
		return new VirtualTypedStore<VirtualInMemoryTopicMapStore>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualCharacteristicsStore<?> createCharacteristicsStore(InMemoryTopicMapStore store, ILocator xsdString) {
		return new VirtualCharacteristicsStore<VirtualInMemoryTopicMapStore>(this, xsdString);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualScopeStore<?> createScopeStore(InMemoryTopicMapStore store) {
		return new VirtualScopeStore<VirtualInMemoryTopicMapStore>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualTopicTypeStore<?> createTopicTypeStore(InMemoryTopicMapStore store) {
		return new VirtualTopicTypeStore<VirtualInMemoryTopicMapStore>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		switch (paramType) {
			/*
			 * revisions directly handled by the underlying topic map store
			 */
			case NEXT_REVISION:
			case PREVIOUS_REVISION:
			case TAG:
			case CHANGESET:
			case META_DATA:
			case REVISION_END:
			case REVISION_TIMESTAMP: {
				return getRealStore().doRead(context, paramType, params);
			}
			case TYPE: {
				if (context == null && params.length == 1 && params[0] instanceof IRevision) {
					return getRealStore().doRead(context, paramType, params);
				}
			}
		}
		return super.doRead(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		switch (paramType) {
			/*
			 * revisions directly handled by the underlying topic map store
			 */
			case TAG:
			case META_DATA: {
				getRealStore().doModify(context, paramType, params);
			}
				break;
			/*
			 * other modification handled by this topic map store
			 */
			default: {
				super.doModify(context, paramType, params);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementSupported() {
		return false;
	}

}
