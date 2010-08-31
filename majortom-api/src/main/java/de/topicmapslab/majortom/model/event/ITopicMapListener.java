package de.topicmapslab.majortom.model.event;

import org.tmapi.core.Construct;

/**
 * Interface for the topic map listener.
 * 
 * Listener may register to a topic map and get notified if the any construct in
 * the map changes.
 */
public interface ITopicMapListener {

	/**
	 * Every modification of the topic map calls this method.
	 * 
	 * @param id a unique id to identify the current event
	 * @param event the type of modification
	 * @param notifier the construct which is the container of the modification
	 * @param newValue the new value or <code>null</code> if non exists (e.g.
	 *            remove event)
	 * @param oldValue the old value or <code>null</code> if non exists (e.g.
	 *            add event)
	 */
	public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue);

}
