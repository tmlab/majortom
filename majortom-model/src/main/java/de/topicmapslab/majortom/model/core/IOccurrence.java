package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Occurrence;

/**
 * Interface definition representing an occurrence characteristics of a topic
 * item.
 * 
 * @author Sven Krosse
 * 
 */
public interface IOccurrence extends Occurrence, ICharacteristics, IDatatypeAware, IReifiable {
	/**
	 * {@inheritDoc}
	 */
	public ITopic getParent();
	
}
