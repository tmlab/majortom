/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * A task definition for the modification tasks
 * 
 * @author Sven Krosse
 * 
 */
public class ModifyTask extends QueueTaskImpl {

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
	public ModifyTask(IConstruct context,
			TopicMapStoreParameterType parameterType, Object... parameters) {
		super(context, parameterType);
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doTask(ITopicMapStore topicMapStore)
			throws TopicMapStoreException {
		topicMapStore.doModify(getContext(), getParameterType(), parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return null;
	}

}
