/**
 * 
 */
package de.topicmapslab.majortom.queued.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.index.Index;
import org.tmapi.index.LiteralIndex;
import org.tmapi.index.ScopedIndex;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.inmemory.transaction.InMemoryTransaction;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.queued.queue.IProcessingListener;
import de.topicmapslab.majortom.queued.queue.TopicMapStoreQueue;
import de.topicmapslab.majortom.queued.queue.task.CreateTask;
import de.topicmapslab.majortom.queued.queue.task.IQueueTask;
import de.topicmapslab.majortom.queued.queue.task.MergeTask;
import de.topicmapslab.majortom.queued.queue.task.ModifyTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveConstructTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveDuplicatesTask;
import de.topicmapslab.majortom.queued.queue.task.RemoveTask;
import de.topicmapslab.majortom.queued.store.index.ConcurrentIdentityIndex;
import de.topicmapslab.majortom.queued.store.index.ConcurrentLiteralIndex;
import de.topicmapslab.majortom.queued.store.index.ConcurrentScopedIndex;
import de.topicmapslab.majortom.queued.store.index.ConcurrentSupertypeSubtypeIndex;
import de.topicmapslab.majortom.queued.store.index.ConcurrentTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.queued.store.index.ConcurrentTypeInstanceIndex;
import de.topicmapslab.majortom.store.TopicMapStoreImpl;
import de.topicmapslab.majortom.util.HashUtil;

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
	Lock lock = new ReentrantLock(true);

	private ConcurrentScopedIndex scopedIndex;
	private ConcurrentIdentityIndex identityIndex;
	private ConcurrentTypeInstanceIndex typeInstanceIndex;
	private ConcurrentTransitiveTypeInstanceIndex transitiveTypeInstanceIndex;
	private ConcurrentSupertypeSubtypeIndex supertypeSubtypeIndex;
	private ConcurrentLiteralIndex literalIndex;

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

		/*
		 * overwrite set and map class for concurrent modification access
		 */
		HashUtil.overwriteMapImplementationClass(ConcurrentHashMap.class);
		HashUtil.overwriteSetImplementationClass(CopyOnWriteArraySet.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		super.connect();
		jdbcTopicMapStore.initialize(getTopicMapBaseLocator());
		jdbcTopicMapStore.connect();
		inMemoryTopicMapStore.initialize(getTopicMapBaseLocator());
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
	@SuppressWarnings("unchecked")
	public <I extends Index> I getIndex(Class<I> clazz) {
		/*
		 * revision is handled by the JDBC topic map store
		 */
		if (IRevisionIndex.class.isAssignableFrom(clazz)) {
			return jdbcTopicMapStore.getIndex(clazz);
		}
		/*
		 * get transitive type-instance index
		 */
		else if (ITransitiveTypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (transitiveTypeInstanceIndex == null) {
				transitiveTypeInstanceIndex = new ConcurrentTransitiveTypeInstanceIndex((ITransitiveTypeInstanceIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) transitiveTypeInstanceIndex;
		}
		/*
		 * get type-instance index
		 */
		else if (TypeInstanceIndex.class.isAssignableFrom(clazz)) {
			if (typeInstanceIndex == null) {
				typeInstanceIndex = new ConcurrentTypeInstanceIndex((ITypeInstanceIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) typeInstanceIndex;
		}
		/*
		 * get scoped index
		 */
		else if (ScopedIndex.class.isAssignableFrom(clazz)) {
			if (scopedIndex == null) {
				scopedIndex = new ConcurrentScopedIndex((IScopedIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) scopedIndex;
		}
		/*
		 * get supertype-subtype index
		 */
		else if (ISupertypeSubtypeIndex.class.isAssignableFrom(clazz)) {
			if (supertypeSubtypeIndex == null) {
				supertypeSubtypeIndex = new ConcurrentSupertypeSubtypeIndex((ISupertypeSubtypeIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) supertypeSubtypeIndex;
		}
		/*
		 * get literal index
		 */
		else if (LiteralIndex.class.isAssignableFrom(clazz)) {
			if (literalIndex == null) {
				literalIndex = new ConcurrentLiteralIndex((ILiteralIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) literalIndex;
		}
		/*
		 * get identity index
		 */
		else if (IIdentityIndex.class.isAssignableFrom(clazz)) {
			if (identityIndex == null) {
				identityIndex = new ConcurrentIdentityIndex((IIdentityIndex) inMemoryTopicMapStore.getIndex(clazz), lock);
			}
			return (I) identityIndex;
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
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			if (!isConnected()) {
				throw new TopicMapStoreException("Connection is not established");
			}

			switch (paramType) {
				case ID: {
					if (context instanceof ITopicMap) {
						return jdbcTopicMapStore.getTopicMapIdentity().getId();
					}
				}
			}
			/*
			 * redirect to virtual layer
			 */
			return inMemoryTopicMapStore.doRead(context, paramType, params);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T extends Construct> void doMerge(T context, T... others) throws TopicMapStoreException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
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
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
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
		} finally {
			lock.unlock();
		}

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
		try {
			while (!lock.tryLock()) {
				// WAIT
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
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
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
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
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
		} finally {
			lock.unlock();
		}
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
		while (queue.isBusy() && !queue.isInterrupted() && queue.isAlive()) {
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
		 * wait for finishing all tasks of the worker tread //
		 */
		// commit();
		/*
		 * remove duplicates from virtual store
		 */
		inMemoryTopicMapStore.removeDuplicates();

		/*
		 * add task to queue
		 */
		RemoveDuplicatesTask task = new RemoveDuplicatesTask(getTopicMap());
		queue.add(task);
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
		return jdbcTopicMapStore.isRevisionManagementSupported();
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableRevisionManagement(boolean enabled) throws TopicMapStoreException {
		if (!isRevisionManagementSupported() && enabled) {
			throw new TopicMapStoreException("Revision management not supported by the current store and cannot be enabled!");
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
		while (!lock.tryLock()) {
			// WAIT
		}
		try {
			if (task instanceof CreateTask) {
				updateVirtualLayer((CreateTask) task);
			}
		} finally {
			lock.unlock();
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

	/**
	 * {@inheritDoc}
	 */
	public ITopicMapStoreIdentity getTopicMapIdentity() {
		return jdbcTopicMapStore.getTopicMapIdentity();
	}

	private void updateVirtualLayer(CreateTask task) {
		Object result = task.getResult();
		if (result instanceof IConstruct) {
			ConstructImpl memory = (ConstructImpl) task.getInMemoryClone();
			inMemoryTopicMapStore.removeVirtualConstruct(memory, (IConstruct) result);
			long databaseId = ((ConstructImpl) result).getIdentity().longId();
			memory.getIdentity().setId(databaseId);

			if (task.getParameterType() == TopicMapStoreParameterType.NAME && !(task.getParameters()[0] instanceof ITopic)) {
				ILocator locator = new LocatorImpl(Namespaces.TMDM.TOPIC_NAME);
				ITopic topic = jdbcTopicMapStore.doReadTopicBySubjectIdentifier(getTopicMap(), locator);
				ITopic inMemory = inMemoryTopicMapStore.doReadTopicBySubjectIdentifier(getTopicMap(), locator);
				if (topic != null && inMemory != null) {
					inMemoryTopicMapStore.removeVirtualConstruct(inMemory, topic);
				}
			}
		}
	}

}
