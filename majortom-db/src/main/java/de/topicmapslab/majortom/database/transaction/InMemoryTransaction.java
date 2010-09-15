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
package de.topicmapslab.majortom.database.transaction;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.model.transaction.ITransactionTopicMapStore;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.transaction.TransactionImpl;

/**
 * In-memory implementation of the {@link ITransaction}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryTransaction extends TransactionImpl {

	private final TransactionTopicMapStore transactionTopicMapStore;

	/**
	 * constructor
	 * 
	 * @param parent
	 *            the parent topic map
	 */
	public InMemoryTransaction(ITopicMap parent) {
		super(parent);
		this.transactionTopicMapStore = new TransactionTopicMapStore(getTopicMap().getTopicMapSystem(), (TopicMapStoreImpl) parent.getStore(), this);
		this.transactionTopicMapStore.setTopicMap(this);
		this.transactionTopicMapStore.connect();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransactionTopicMapStore getStore() {
		if (isClose()) {
			throw new TransactionException("Transaction is already closed!");
		}
		return transactionTopicMapStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		rollback();
		this.transactionTopicMapStore.close();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends IConstruct> T moveToTransactionContext(T construct) {
		return transactionTopicMapStore.getIdentityStore().createLazyStub(construct);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void commit() throws TransactionException {
//		try {
//			((JdbcTopicMapStore)(getStore().getRealStore())).getProcessor().openTransaction();
			super.commit();
		// ((JdbcTopicMapStore)(getStore().getRealStore())).getProcessor().commit();
		// } catch (SQLException e) {
		// throw new TransactionException("Error occurred during transactio!");
		// }	
	}
	
}
