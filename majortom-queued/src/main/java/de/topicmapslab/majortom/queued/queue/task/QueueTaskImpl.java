/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import java.util.Calendar;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * @author Sven
 * 
 */
public abstract class QueueTaskImpl implements IQueueTask {

	/**
	 * the creation time
	 */
	private final Calendar timestamp;

	/**
	 * the context calling the task
	 */
	private final IConstruct context;
	/**
	 * the parameter type
	 */
	private final TopicMapStoreParameterType parameterType;

	/**
	 * constructor
	 * 
	 * @param context
	 *            the calling context or <code>null</code>
	 * @param parameterType
	 *            the parameter type
	 */
	public QueueTaskImpl(IConstruct context,
			TopicMapStoreParameterType parameterType) {
		this.context = context;
		this.parameterType = parameterType;
		this.timestamp = Calendar.getInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getTimeStamp() {
		return this.timestamp;
	}

	/**
	 * Returns the context of calling
	 * 
	 * @return the context
	 */
	public IConstruct getContext() {
		return context;
	}

	/**
	 * Returns the parameter type for the task
	 * 
	 * @return the parameter type
	 */
	public TopicMapStoreParameterType getParameterType() {
		return parameterType;
	}
	
	

}
