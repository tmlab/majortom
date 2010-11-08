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

import java.util.Map;
import java.util.Set;

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransactionTopicMapStore;
import de.topicmapslab.majortom.inmemory.virtual.internal.VirtualIdentityStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class LazyIdentityStore extends VirtualIdentityStore<InMemoryTransactionTopicMapStore> {

	private Map<String, IConstruct> lazyStubs;
	private Set<IScope> lazyScopes;

	/**
	 * @param store
	 */
	public LazyIdentityStore(InMemoryTransactionTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setId(IConstruct c, String id) {
		if (lazyStubs == null) {
			lazyStubs = HashUtil.getHashMap();
		}
		lazyStubs.put(id, c);
		super.setId(c, id);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <X extends IConstruct> X asVirtualConstruct(X c) throws ConstructRemovedException {
		if (c == null) {
			return null;
		}
		if (isRemovedConstruct(c)) {
			throw new ConstructRemovedException(c);
		}
		if (lazyStubs == null) {
			lazyStubs = HashUtil.getHashMap();
		}
		if (!lazyStubs.containsKey(c.getId())) {
			X construct = LazyStubCreator.createLazyStub(c, getStore().getTransaction());
			lazyStubs.put(c.getId(), construct);
			return construct;
		}
		return (X) lazyStubs.get(c.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope asVirtualScope(IScope scope) throws ConstructRemovedException {
		if (scope == null) {
			return null;
		}
		if (lazyScopes == null) {
			lazyScopes = HashUtil.getHashSet();
		}
		if (!lazyScopes.contains(scope)) {
			Set<ITopic> themes = HashUtil.getHashSet();
			for (ITopic theme : scope.getThemes()) {
				themes.add(asVirtualConstruct(theme));
			}
			return new ScopeImpl(themes);
		}
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (lazyStubs != null) {
			lazyStubs.clear();
		}
		if (lazyScopes != null) {
			lazyScopes.clear();
		}
		super.close();
	}


}
