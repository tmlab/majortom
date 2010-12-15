/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * A task definition for the creation tasks
 * 
 * @author Sven Krosse
 * 
 */
public class CreateTask extends QueueTaskImpl {

	/**
	 * array of all parameters
	 */
	private Object[] parameters;

	/**
	 * the result of execution
	 */
	private Object result;
	
	private final Object inMemoryClone;

	/**
	 * constructor
	 * 
	 * @param inMemoryClone
	 *            the corresponding construct created in memory 
	 * @param context
	 *            the context
	 * @param parameterType
	 *            the parameter type
	 * @param parameters
	 *            the parameters
	 */
	public CreateTask(Object inMemoryClone, IConstruct context, TopicMapStoreParameterType parameterType,
			Object... parameters) {
		super(context, parameterType);
		this.inMemoryClone = inMemoryClone;
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doTask(ITopicMapStore topicMapStore) throws TopicMapStoreException {
		result = topicMapStore.doCreate(getContext(), getParameterType(), parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return result;
	}
	
	/**
	 * Returns the corresponding construct created in the in memory topic map store as virtual clone
	 * @return the inMemoryClone
	 */
	public Object getInMemoryClone() {
		return inMemoryClone;
	}
	
	/**
	 * @return the parameters
	 */
	public Object[] getParameters() {
		return parameters;
	}

}
