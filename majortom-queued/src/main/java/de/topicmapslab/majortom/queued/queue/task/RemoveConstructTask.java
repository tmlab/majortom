/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * A task definition for the deletion tasks of constructs
 * 
 * @author Sven Krosse
 * 
 */
public class RemoveConstructTask extends QueueTaskImpl {

	/**
	 * the cascading flag
	 */
	private boolean cascade;

	/**
	 * constructor
	 * 
	 * @param context
	 *            the context
	 * @param parameters
	 *            the parameters
	 */
	public RemoveConstructTask(IConstruct context, boolean cascade) {
		super(context, null);
		this.cascade = cascade;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doTask(ITopicMapStore topicMapStore)
			throws TopicMapStoreException {
		topicMapStore.doRemove(getContext(), cascade);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return null;
	}

}
