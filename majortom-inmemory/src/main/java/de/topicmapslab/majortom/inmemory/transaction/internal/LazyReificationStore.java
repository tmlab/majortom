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

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.internal.ReificationStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * @author Sven Krosse
 * 
 */
public class LazyReificationStore extends ReificationStore {

	/**
	 * @param store
	 */
	public LazyReificationStore(InMemoryTopicMapStore store) {
		super(store);
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
	 * {@inheritDoc}
	 */
	protected InMemoryTransactionTopicMapStore getStore() {
		return (InMemoryTransactionTopicMapStore) super.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getReifier(IReifiable reifiable) {
		if ( getLazyIdentityStore().isRemovedConstruct(reifiable)){
			throw new ConstructRemovedException(reifiable);
		}
		ITopic reifier = super.getReifier(reifiable);
		if (reifier == null) {
			reifier = getLazyIdentityStore().createLazyStub((ITopic) getStore().getRealStore().doRead(reifiable, TopicMapStoreParameterType.REIFICATION));
		}
		return reifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public IReifiable getReified(ITopic reifier) {
		if ( getLazyIdentityStore().isRemovedConstruct(reifier)){
			throw new ConstructRemovedException(reifier);
		}
		IReifiable reifiable = super.getReified(reifier);
		if (reifiable == null) {
			reifiable = getLazyIdentityStore().createLazyStub((IReifiable) getStore().getRealStore().doRead(reifier, TopicMapStoreParameterType.REIFICATION));
		}
		return reifiable;
	}
	
}
