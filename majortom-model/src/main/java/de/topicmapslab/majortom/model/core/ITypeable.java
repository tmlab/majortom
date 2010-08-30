package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Typed;

/**
 * Interface definition of a topic map construct which has a single type.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITypeable extends Typed, IConstruct {

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap();

}
