package de.topicmapslab.majortom.model.core;

import java.io.Serializable;

import org.tmapi.core.Construct;
import org.tmapi.core.TopicInUseException;

/**
 * Base Interface representing a topic map construct.
 * 
 * @author Sven Krosse
 * 
 */
public interface IConstruct extends Construct, Comparable<IConstruct>, Serializable {

	/**
	 * Removes the current construct from the underlying topic map.
	 * 
	 * @param cascade
	 *            flag indicates if all dependencies should be removed too
	 * @throws TopicInUseException
	 *             thrown if the construct will be used by another construct and
	 *             the argument cascade is <code>false</code>
	 */
	public void remove(final boolean cascade) throws TopicInUseException;

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap();

	/**
	 * Indicates if the construct is removed
	 * 
	 * @return the removed <code>true</code> if the construct was removed
	 */
	public boolean isRemoved();
}
