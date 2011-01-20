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
package de.topicmapslab.majortom.index.core;

import java.util.Collection;

import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.transaction.ITransaction;

/**
 * Base class of all cached indexes
 * 
 * @author Sven Krosse
 * 
 */
public abstract class BaseCachedIndexImpl<T extends ITopicMapStore> extends IndexImpl<T> implements ITopicMapListener {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store
	 */
	public BaseCachedIndexImpl(T store) {
		super(store);
	}

	/**
	 * clear all internal caches
	 */
	protected abstract void clearCache();

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		getTopicMapStore().addTopicMapListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		clearCache();
		getTopicMapStore().removeTopicMapListener(this);
		super.close();
	}

	/**
	 * Removed any cached content from internal cache
	 */
	public void clear() {
		clearCache();
	}

	/**
	 * Method checks if the given scope is on transaction context
	 * 
	 * @param scope
	 *            the scope
	 * @return <code>true</code> if scope is on transaction context,
	 *         <code>false</code> otherwise
	 */
	protected boolean isOnTransactionContext(IScope scope) {
		for (Topic t : scope.getThemes()) {
			if (t.getTopicMap() instanceof ITransaction) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method checks if the given context are on transaction context
	 * 
	 * @param context
	 *            the context
	 * @return <code>true</code> if context are on transaction context,
	 *         <code>false</code> otherwise
	 */
	protected boolean isOnTransactionContext(Collection<?> context) {
		for (Object o : context) {
			if (o instanceof IScope && isOnTransactionContext((IScope) o)) {
				return true;
			} else if (o instanceof ITopic && ((ITopic) o).getTopicMap() instanceof ITransaction) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method checks if the given scopes are on transaction context
	 * 
	 * @param scopes
	 *            the scopes
	 * @return <code>true</code> if scopes are on transaction context,
	 *         <code>false</code> otherwise
	 */
	protected boolean isOnTransactionContext(IScope... scopes) {
		for (IScope s : scopes) {
			if (isOnTransactionContext(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method checks if the given theme is on transaction context
	 * 
	 * @param c
	 *            the construct
	 * @return <code>true</code> if themes is on transaction context,
	 *         <code>false</code> otherwise
	 */
	protected boolean isOnTransactionContext(Construct c) {
		return c != null && (c instanceof ITransaction || c.getTopicMap() instanceof ITransaction);
	}

	/**
	 * Method checks if the given constructs are on transaction context
	 * 
	 * @param constructs
	 *            the constructs
	 * @return <code>true</code> if constructs are on transaction context,
	 *         <code>false</code> otherwise
	 */
	protected boolean isOnTransactionContext(Construct... constructs) {
		for (Construct c : constructs) {
			if (c instanceof ITransaction || c.getTopicMap() instanceof ITransaction) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the underlying topic map store supports caching
	 * 
	 * @return <code>true</code> if caching is supported and enabled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCachingEnabled() {
		return getTopicMapStore().isCachingEnabled();
	}
}
