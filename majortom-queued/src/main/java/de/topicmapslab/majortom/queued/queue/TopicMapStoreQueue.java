/**
 * 
 */
package de.topicmapslab.majortom.queued.queue;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.queued.queue.task.IQueueTask;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TopicMapStoreQueue extends Thread {

	private ITopicMapStore topicMapStore;

	private Queue<IQueueTask> tasks;

	private Set<IProcessingListener> listeners;

	/**
	 * constructor
	 * 
	 * @param topicMapStore
	 *            the topic map store
	 */
	public TopicMapStoreQueue(ITopicMapStore topicMapStore) {
		this.topicMapStore = topicMapStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void start() {
		this.tasks = new ConcurrentLinkedQueue<IQueueTask>();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		while (!isInterrupted()) {
			while (tasks.isEmpty()) {
				IQueueTask task = tasks.poll();
				task.doTask(topicMapStore);
				if (listeners != null) {
					for (IProcessingListener listener : listeners) {
						listener.finished(task);
					}
				}
			}
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * Returns the number of tasks in the internal queue
	 * 
	 * @return the number of tasks
	 */
	public int size() {
		return tasks.size();
	}

	/**
	 * removes all tasks from the internal queue
	 */
	public void clear() {
		tasks.clear();
	}

	/**
	 * Add the given task to internal queue
	 * 
	 * @param e
	 *            the new task
	 * @return <code>true</code> if the task could add to queue, <code>false</code> otherwise
	 */
	public boolean add(IQueueTask e) {
		return tasks.add(e);
	}

	/**
	 * Register a new processing listener for the current queue instance. Each listener will be notified if a task was
	 * finished.
	 * 
	 * @param listener
	 *            the new listener
	 */
	public void addProcessingListener(IProcessingListener listener) {
		if (listeners == null) {
			listeners = HashUtil.getHashSet();
		}
		listeners.add(listener);
	}

	/**
	 * Unregister a new processing listener for the current queue instance. Each listener will be notified if a task was
	 * finished.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeProcessingListener(IProcessingListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

}
