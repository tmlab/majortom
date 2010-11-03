/**
 * 
 */
package de.topicmapslab.majortom.queued.store;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransaction;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.queued.queue.IProcessingListener;
import de.topicmapslab.majortom.queued.queue.TopicMapStoreQueue;
import de.topicmapslab.majortom.queued.queue.task.CreateTask;
import de.topicmapslab.majortom.queued.queue.task.IQueueTask;
import de.topicmapslab.majortom.queued.queue.task.MergeTask;
import de.topicmapslab.majortom.queued.queue.task.ModifyTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveConstructTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveTask;
import de.topicmapslab.majortom.store.MergeUtils;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;

/**
 * A queued topic map store, which reads and writes any context to memory and creating a task to persist information to
 * database.
 * 
 * @author Sven Krosse
 * 
 */
public class QueuedTopicMapStore extends TopicMapStoreImpl implements IProcessingListener {

	private VirtualInMemoryTopicMapStore inMemoryTopicMapStore;
	private JdbcTopicMapStore jdbcTopicMapStore;
	private TopicMapStoreQueue queue;

	/**
	 * constructor
	 */
	public QueuedTopicMapStore() {
	}

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 */
	public QueuedTopicMapStore(ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTopicMap(ITopicMap topicMap) throws TopicMapStoreException {
		super.setTopicMap(topicMap);
		jdbcTopicMapStore.setTopicMap(topicMap);
		inMemoryTopicMapStore.setTopicMap(topicMap);
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		super.initialize(topicMapBaseLocator);
		/*
		 * initialize in database layer
		 */
		jdbcTopicMapStore = new JdbcTopicMapStore();
		jdbcTopicMapStore.setTopicMapSystem(getTopicMapSystem());
		/*
		 * initialize in memory layer
		 */
		inMemoryTopicMapStore = new VirtualInMemoryTopicMapStore(getTopicMapSystem(), jdbcTopicMapStore);
		inMemoryTopicMapStore.setTopicMapSystem(getTopicMapSystem());
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		super.connect();
		jdbcTopicMapStore.initialize(getBaseLocator());
		jdbcTopicMapStore.connect();
		inMemoryTopicMapStore.initialize(getBaseLocator());
		inMemoryTopicMapStore.connect();
		/*
		 * initialize queue
		 */
		queue = new TopicMapStoreQueue(jdbcTopicMapStore);
		queue.addProcessingListener(this);
		queue.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		doQueueShutdown();
		queue.removeProcessingListener(this);
		inMemoryTopicMapStore.close();
		jdbcTopicMapStore.close();
		super.close();
	}

	/**
	 * shutdown the worker thread queue
	 */
	private synchronized void doQueueShutdown() {
		queue.interrupt();
		while (queue.isAlive()) {
			// VOID
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <I extends Index> I getIndex(Class<I> clazz) {
		/*
		 * revision is handled by the JDBC topic map store
		 */
		if (IRevisionIndex.class.isAssignableFrom(clazz)) {
			return jdbcTopicMapStore.getIndex(clazz);
		}
		return inMemoryTopicMapStore.getIndex(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCachingEnabled() {
		return jdbcTopicMapStore.isCachingEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableCaching(boolean enable) {
		jdbcTopicMapStore.enableCaching(enable);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}

		switch (paramType) {
			case ID: {
				if (context instanceof ITopicMap) {
					return jdbcTopicMapStore.getTopicMapIdentity().getId();
				}
			}
				break;
		}
		/*
		 * redirect to virtual layer
		 */
		return inMemoryTopicMapStore.doRead(context, paramType, params);
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Construct> void doMerge(T context, T... others) throws TopicMapStoreException {
		/*
		 * merge in virtual layer
		 */
		inMemoryTopicMapStore.doMerge(context, others);
		/*
		 * create new merging task
		 */
		MergeTask task = new MergeTask(context, others);
		/*
		 * register task
		 */
		queue.add(task);
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		/*
		 * remove from virtual memory layer
		 */
		inMemoryTopicMapStore.doRemove(context, paramType, params);
		/*
		 * create new deletion task
		 */
		RemoveTask task = new RemoveTask(context, paramType, params);
		/*
		 * register task
		 */
		queue.add(task);

	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		/*
		 * is deletion of whole topic map
		 */
		if (context == getTopicMap()) {
			/*
			 * destroy queue
			 */
			doQueueShutdown();
			/*
			 * destroy topic map
			 */
			jdbcTopicMapStore.doRemove(context, cascade);
			return;
		}
		/*
		 * remove from virtual memory layer
		 */
		inMemoryTopicMapStore.doRemove(context, cascade);
		/*
		 * create new deletion task
		 */
		RemoveConstructTask task = new RemoveConstructTask(context, cascade);
		/*
		 * register task
		 */
		queue.add(task);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		/*
		 * create in the virtual memory layer
		 */
		Object object = inMemoryTopicMapStore.doCreate(context, paramType, params);
		/*
		 * create new creation task for worker thread
		 */
		CreateTask task = new CreateTask(object, context, paramType, params);
		/*
		 * register task
		 */
		queue.add(task);
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		/*
		 * modify in virtual memory layer
		 */
		inMemoryTopicMapStore.doModify(context, paramType, params);
		/*
		 * create new modification task for worker thread
		 */
		ModifyTask task = new ModifyTask(context, paramType, params);
		/*
		 * register task
		 */
		queue.add(task);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction createTransaction() {
		return new InMemoryTransaction(getTopicMap());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransactable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		while (queue.size() != 0 && !queue.isInterrupted() && queue.isAlive()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// VOID
			}
		}
		if (queue.isInterrupted() || !queue.isAlive()) {
			throw new TopicMapStoreException("Worker thread was shutdown!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		if (isReadOnly()) {
			throw new UnmodifyableStoreException("Read-only store does not support deletion of construct!");
		}
		/*
		 * wait for finishing all tasks of the worker tread
		 */
		commit();
		/*
		 * remove duplicates
		 */
		MergeUtils.removeDuplicates(this, getTopicMap());
		/*
		 * wait for finishing all tasks of the worker tread
		 */
		commit();
		/*
		 * reset virtual memory layer
		 */
		inMemoryTopicMapStore.close();
		inMemoryTopicMapStore  = new VirtualInMemoryTopicMapStore(getTopicMapSystem(), jdbcTopicMapStore);
		inMemoryTopicMapStore.setTopicMap(getTopicMap());
		inMemoryTopicMapStore.initialize(getBaseLocator());
		inMemoryTopicMapStore.connect();
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void clear() {
		/*
		 * wait for finishing all tasks of the worker tread
		 */
		commit();
		inMemoryTopicMapStore.clear();
		jdbcTopicMapStore.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementSupported() {
		return jdbcTopicMapStore.isRevisionManagementEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableRevisionManagement(boolean enabled) throws TopicMapStoreException {
		if (!isRevisionManagementSupported() && enabled) {
			throw new TopicMapStoreException(
					"Revision management not supported by the current store and cannot be enabled!");
		}
		jdbcTopicMapStore.enableRevisionManagement(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return jdbcTopicMapStore.isReadOnly();
	}

	/**
	 * {@inheritDoc}
	 */
	public void finished(IQueueTask task) {
		if (task instanceof CreateTask) {
			CreateTask ct = (CreateTask) task;
			Object result = task.getResult();
			if (result instanceof IConstruct) {
				String databaseId = ((ConstructImpl) result).getIdentity().getId();
				((ConstructImpl) ct.getInMemoryClone()).getIdentity().setId(databaseId);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTopicMapListener(ITopicMapListener listener) {
		jdbcTopicMapStore.addTopicMapListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTopicMapListener(ITopicMapListener listener) {
		jdbcTopicMapStore.removeTopicMapListener(listener);
	}

}
