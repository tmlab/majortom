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
package de.topicmapslab.majortom.model.transaction;

import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * A topic map store a virtual layer between the application and the real topic
 * map store.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITransactionTopicMapStore extends ITopicMapStore {

	/**
	 * Commit all changes to the topic map store.
	 * @throws TransactionException
	 *             thrown if commit fails
	 * 
	 * @see ITransaction#commit()
	 */
	public void commit() throws TransactionException;

	/**
	 * Rolling back all changes of the current transaction. After roll back the
	 * topic map state is the same like the time the transaction was created.
	 * 
	 * @see ITransaction#rollback()
	 */
	public void rollback();

	/**
	 * Returns the underlying topic map store handle the real topic map
	 * instance.
	 * 
	 * @return the real topic map store
	 */
	public ITopicMapStore getRealStore();

	/**
	 * Returns the transaction reference which handled by this transaction topic
	 * map store.
	 * 
	 * @return the transaction
	 */
	public ITransaction getTransaction();

}
