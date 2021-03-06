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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.inmemory.store.InMemoryIdentity;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyAssociationStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyCharacteristicsStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyIdentityStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyReificationStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyScopeStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyTopicTypeStore;
import de.topicmapslab.majortom.inmemory.transaction.internal.LazyTypedStore;
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
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
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
public class InMemoryTransactionTopicMapStore extends VirtualTopicMapStore implements ITransactionTopicMapStore {

	private List<TransactionCommand> commands = new LinkedList<TransactionCommand>();
	private LinkedList<TransactionCommand> commited = new LinkedList<TransactionCommand>();
	private final ITransaction transaction;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the topic map system
	 * @param store
	 *            the real store
	 * @param transaction
	 *            the transaction
	 */
	public InMemoryTransactionTopicMapStore(ITopicMapSystem topicMapSystem, ITopicMapStore store, ITransaction transaction) {
		super(topicMapSystem, store);
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
			final Map<Object, Object> lazy = HashUtil.getHashMap();
			lazy.put(transaction, transaction.getTopicMap());
			ITopicMapListener listener = new ITopicMapListener() {
				public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
					if (event == TopicMapEventType.MERGE) {
						Object oldValue_ = null;
						/* find old value */
						// By subject-identifier
						for (Locator l : ((ITopic) oldValue).getSubjectIdentifiers()) {
							oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, l);
							if (oldValue_ != null) {
								break;
							}
						}
						// By subject-locator
						if (oldValue_ == null) {
							for (Locator l : ((ITopic) oldValue).getSubjectLocators()) {
								oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, l);
								if (oldValue_ != null) {
									break;
								}
							}
						}
						// By item-identifier
						if (oldValue_ == null) {
							for (Locator l : ((ITopic) oldValue).getItemIdentifiers()) {
								oldValue_ = doRead(getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, l);
								if (oldValue_ != null) {
									break;
								}
							}
						}
						// store mapping
						lazy.put(oldValue_, newValue);
					}
				}
			};
			getRealStore().addTopicMapListener(listener);
			for (TransactionCommand command : commands) {
				Object obj = command.commit(getRealStore(), lazy);
				if (obj != null && command.getResult() != null) {
					lazy.put(command.getResult(), obj);
				}
				commited.add(command);
			}
			commands.clear();
			getRealStore().removeTopicMapListener(listener);
		} catch (TransactionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void rollback() {
		Iterator<TransactionCommand> descendingIterator = commited.descendingIterator();
		while (descendingIterator.hasNext()) {
			descendingIterator.next().rollback();
		}
		commited.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		TransactionCommand command = new TransactionCommand(getTransaction(), null, TransactionOperation.MODIFY, context, paramType, params);
		super.doModify(context, paramType, params);
		commands.add(command);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		TransactionCommand command = new TransactionCommand(getTransaction(), null, TransactionOperation.REMOVE, context, paramType, params);
		super.doRemove(context, paramType, params);
		commands.add(command);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		TransactionCommand command = new TransactionCommand(getTransaction(), null, TransactionOperation.REMOVE, context, null, cascade);
		super.doRemove(context, cascade);
		commands.add(command);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		Object obj = super.doCreate(context, paramType, params);
		commands.add(new TransactionCommand(getTransaction(), obj, TransactionOperation.CREATE, context, paramType, params));
		return obj;
	}

	/**
	 * {@inheritDoc}
	 */
	public String doReadId(IConstruct c) throws TopicMapStoreException {
		try {
			return getRealStore().doRead(c, TopicMapStoreParameterType.ID).toString();
		} catch (TopicMapStoreException e) {
			return super.doReadId(c);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualIdentityStore<?> createIdentityStore(InMemoryTopicMapStore store) {
		return new LazyIdentityStore(this, getCapacityOfCollections());
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualAssociationStore<?> createAssociationStore(InMemoryTopicMapStore store) {
		return new LazyAssociationStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualReificationStore<?> createReificationStore(InMemoryTopicMapStore store) {
		return new LazyReificationStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualTypedStore<?> createTypedStore(InMemoryTopicMapStore store) {
		return new LazyTypedStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualCharacteristicsStore<?> createCharacteristicsStore(InMemoryTopicMapStore store, ILocator xsdString) {
		return new LazyCharacteristicsStore(this, xsdString, getCapacityOfCollections());
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualScopeStore<?> createScopeStore(InMemoryTopicMapStore store) {
		return new LazyScopeStore(this);
	}

	/**
	 * {@inheritDoc}
	 */
	protected VirtualTopicTypeStore<?> createTopicTypeStore(InMemoryTopicMapStore store) {
		return new LazyTopicTypeStore(this);
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
	public InMemoryIdentity getTopicMapIdentity() {
		return super.getTopicMapIdentity();
	}

}
