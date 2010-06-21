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
package de.topicmapslab.majortom.inmemory.transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.internal.AssociationStore;
import de.topicmapslab.majortom.inmemory.store.internal.IdentityStore;
import de.topicmapslab.majortom.inmemory.store.internal.TypedStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyAssociationStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyIdentityStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyTypedStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.model.transaction.ITransactionTopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryTransactionTopicMapStore extends InMemoryTopicMapStore implements ITransactionTopicMapStore {

	private List<TransactionCommand> commands = new LinkedList<TransactionCommand>();
	private List<TransactionCommand> commited = new LinkedList<TransactionCommand>();
	private final ITopicMapStore store;
	private final ITransaction transaction;

	private LazyIdentityStore lazyIdentityStore;
	private LazyTypedStore lazyTypedStore;
	private LazyAssociationStore lazyAssociationStore;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 * @param store
	 *            the real store
	 */
	public InMemoryTransactionTopicMapStore(ITopicMapSystem topicMapSystem, ITopicMapStore store, ITransaction transaction) {
		super(topicMapSystem);
		this.store = store;
		this.transaction = transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction getTransaction() {
		return transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void commit() throws TransactionException {
		try {
			Map<Object, Object> lazy = HashUtil.getHashMap();
			lazy.put(transaction, transaction.getTopicMap());
			for (TransactionCommand command : commands) {
				Object obj = command.commit(getRealStore(), lazy);
				if (obj != null && command.getResult() != null) {
					lazy.put(command.getResult(), obj);
				}
				commited.add(command);
			}
			commands.clear();
		} catch (TransactionException e) {
			e.printStackTrace();
			System.out.println("Rollback " + commited.size() + " commands!");
		}
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
	public synchronized void rollback() {
		for (TransactionCommand command : commited) {
			// TODO undo command
			command.notify();
		}
		commited.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void connect() throws TopicMapStoreException {
		this.lazyIdentityStore = new LazyIdentityStore(this);
		this.lazyTypedStore = new LazyTypedStore(this);
		this.lazyAssociationStore = new LazyAssociationStore(this);
		super.connect();
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void close() throws TopicMapStoreException {
		this.lazyIdentityStore.close();
		this.lazyTypedStore.close();
		this.lazyAssociationStore.close();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		commands.add(new TransactionCommand(null, TransactionOperation.MODIFY, context, paramType, params));
		super.doModify(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		commands.add(new TransactionCommand(null, TransactionOperation.REMOVE, context, paramType, params));
		super.doRemove(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		commands.add(new TransactionCommand(null, TransactionOperation.REMOVE, context, null, cascade));
		super.doRemove(context, cascade);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		Object obj = super.doCreate(context, paramType, params);
		commands.add(new TransactionCommand(obj, TransactionOperation.CREATE, context, paramType, params));
		return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	public IdentityStore getIdentityStore() {
		return lazyIdentityStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public TypedStore getTypedStore() {
		return lazyTypedStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public AssociationStore getAssociationStore() {
		return lazyAssociationStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supportRevisions() {
		return false;
	}
}
