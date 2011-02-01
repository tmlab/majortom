/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * A task definition for the deletion tasks
 * 
 * @author Sven Krosse
 * 
 */
public class RemoveTask extends QueueTaskImpl {

	/**
	 * array of all parameters
	 */
	private Object[] parameters;

	/**
	 * constructor
	 * 
	 * @param context
	 *            the context
	 * @param parameterType
	 *            the parameter type
	 * @param parameters
	 *            the parameters
	 */
	public RemoveTask(IConstruct context,
			TopicMapStoreParameterType parameterType, Object... parameters) {
		super(context, parameterType);
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doTask(ITopicMapStore topicMapStore)
			throws TopicMapStoreException {
		topicMapStore.doRemove(getContext(), getParameterType(), parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return null;
	}

}
