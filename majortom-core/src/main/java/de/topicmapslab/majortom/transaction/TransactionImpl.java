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
package de.topicmapslab.majortom.transaction;

import de.topicmapslab.majortom.core.TopicMapImpl;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.model.transaction.ITransactionTopicMapStore;

/**
 * @author Sven Krosse
 * 
 */
public abstract class TransactionImpl extends TopicMapImpl implements ITransaction {

	/**
	 * the topic map
	 */
	private final ITopicMap topicMap;

	/**
	 * flag indicates if a transaction is close
	 */
	private boolean close = false;

	/**
	 * constructor
	 * 
	 * @param parent the parent topic map
	 */
	public TransactionImpl(ITopicMap parent) {
		super(parent.getTopicMapSystem(), parent.getLocator());
		this.topicMap = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws TransactionException {		
		getStore().commit();
		close = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() {
		getStore().rollback();
		close = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract ITransactionTopicMapStore getStore();

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap() {
		return topicMap;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isClose() {
		return close;
	}

}
