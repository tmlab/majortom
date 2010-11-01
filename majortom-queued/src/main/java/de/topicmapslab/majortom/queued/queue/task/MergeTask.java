/**
 * 
 */
package de.topicmapslab.majortom.queued.queue.task;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * A task definition for the merging tasks
 * 
 * @author Sven Krosse
 * 
 */
public class MergeTask extends QueueTaskImpl {

	/**
	 * array of all parameters
	 */
	private Construct[] constructs;

	/**
	 * constructor
	 * 
	 * @param context
	 *            the context
	 * @param constructs
	 */
	public <T extends Construct> MergeTask(T context, T... constructs) {
		super((IConstruct)context, null);
		this.constructs = constructs;
	}

	/**
	 * {@inheritDoc}
	 */
	public void doTask(ITopicMapStore topicMapStore) throws TopicMapStoreException {
		topicMapStore.doMerge(getContext(), constructs);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return null;
	}

}
