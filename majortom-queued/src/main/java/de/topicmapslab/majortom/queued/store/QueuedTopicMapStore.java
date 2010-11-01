/**
 * 
 */
package de.topicmapslab.majortom.queued.store;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.queued.queue.TopicMapStoreQueue;
import de.topicmapslab.majortom.queued.queue.task.CreateTask;
import de.topicmapslab.majortom.queued.queue.task.MergeTask;
import de.topicmapslab.majortom.queued.queue.task.ModifyTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveConstructTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveTask;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;

/**
 * A queued topic map store, which reads and writes any context to memory and creating a task to persist information to
 * database.
 * 
 * @author Sven Krosse
 * 
 */
public class QueuedTopicMapStore extends TopicMapStoreImpl {

	private InMemoryTopicMapStore inMemoryTopicMapStore;
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
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		super.initialize(topicMapBaseLocator);
		/*
		 * initialize in memory layer
		 */
		inMemoryTopicMapStore = new InMemoryTopicMapStore();
		inMemoryTopicMapStore.initialize(topicMapBaseLocator);
		inMemoryTopicMapStore.setTopicMap(getTopicMap());
		inMemoryTopicMapStore.setTopicMapSystem(getTopicMapSystem());
		/*
		 * initialize in database layer
		 */
		jdbcTopicMapStore = new JdbcTopicMapStore();
		jdbcTopicMapStore.initialize(topicMapBaseLocator);
		jdbcTopicMapStore.setTopicMap(getTopicMap());
		jdbcTopicMapStore.setTopicMapSystem(getTopicMapSystem());
		/*
		 * initialize queue
		 */
		queue = new TopicMapStoreQueue(jdbcTopicMapStore);
		queue.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		super.connect();
		inMemoryTopicMapStore.connect();
		jdbcTopicMapStore.connect();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		inMemoryTopicMapStore.close();
		jdbcTopicMapStore.close();
		queue.interrupt();
		super.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public <I extends Index> I getIndex(Class<I> clazz) {
		throw new UnsupportedOperationException("Currently not supported!");
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
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params)
			throws TopicMapStoreException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		// TODO Auto-generated method stub

	}

}
