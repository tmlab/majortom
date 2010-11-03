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
package de.topicmapslab.majortom.inmemory.virtual;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualAssociationStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualCharacteristicsStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualIdentityStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualReificationStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualScopeStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualTopicTypeStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualTypedStore;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * @author Sven Krosse
 * 
 */
public abstract class VirtualTopicMapStore extends InMemoryTopicMapStore {

	private final ITopicMapStore store;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 * @param store
	 *            the real store
	 */
	public VirtualTopicMapStore(ITopicMapSystem topicMapSystem, ITopicMapStore store) {
		super(topicMapSystem);
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMapStore getRealStore() {
		return store;
	}

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualIdentityStore<?> createIdentityStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualAssociationStore<?> createAssociationStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualReificationStore<?> createReificationStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualTypedStore<?> createTypedStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualCharacteristicsStore<?> createCharacteristicsStore(InMemoryTopicMapStore store,
			ILocator xsdString);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualScopeStore<?> createScopeStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	protected abstract VirtualTopicTypeStore<?> createTopicTypeStore(InMemoryTopicMapStore store);

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementEnabled() {
		return false;
	}

}
