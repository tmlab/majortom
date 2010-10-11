/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import java.util.Calendar;

import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * @author Sven
 *
 */
public interface IQueueTask {

	/**
	 * Method invoke by the writer queue to execute this task
	 * 
	 * @param topicMapStore
	 *            the topic map store, used to execute the task
	 * @throws TopicMapStoreException
	 * 
	 */
	public void doTask(ITopicMapStore topicMapStore)
			throws TopicMapStoreException;
	
	/**
	 * Returns the result of the task execution
	 */
	public Object getResult();
	
	/**
	 * Returns a calendar instance representing the definition time of this task
	 * @return the timestamp
	 */
	public Calendar getTimeStamp();
	
}
