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

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TransactionException;

/**
 * Interface definition of a transaction.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITransaction extends ITopicMap {

	/**
	 * Commit all changes to the topic map store.
	 * 
	 * @throws TransactionException
	 *             thrown if commit fails
	 */
	public void commit() throws TransactionException;

	/**
	 * Rolling back all changes of the current transaction. After roll back the
	 * topic map state is the same like the time the transaction was created.
	 */
	public void rollback();

	/**
	 * Check if the transaction was already closed by calling {@link #commit()}
	 * or {@link #rollback()}.
	 * 
	 * @return the state
	 */
	public boolean isClose();

	/**
	 * Method move the current item to the transaction context.
	 * 
	 * @param <T>
	 *            the type of the construct
	 * @param construct
	 *            the construct to move
	 * @return the transaction item
	 */
	public <T extends IConstruct> T moveToTransactionContext(T construct);

}
