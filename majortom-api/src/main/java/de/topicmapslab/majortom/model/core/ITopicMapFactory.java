package de.topicmapslab.majortom.model.core;

import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.ModelConstraintException;

/**
 * Interface definition of a topic map factory creating new topic map instances.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITopicMapFactory {

	/**
	 * Creates a new instance of {@link ILocator} with the given string
	 * reference.
	 * 
	 * @param reference
	 *            the string reference
	 * @return a locator instance
	 * @throws MalformedIriException
	 *             thrown if the given string reference is an invalid IRI
	 */
	public ILocator createLocator(final String reference)
			throws MalformedIRIException;

	/**
	 * Creates a new topic map instance with the given identifier reference.
	 * 
	 * @see #createTopicMap(ILocator)
	 * @param reference
	 *            the string representation of the locator
	 * @return the new topic map instance
	 * @throws MalformedIriException
	 *             thrown if the given string reference is an invalid IRI
	 * @throws MalformedIriException
	 *             thrown if the given locator is already used as locator
	 */
	public ITopicMap createTopicMap(final String reference)
			throws MalformedIRIException, ModelConstraintException;

	/**
	 * Creates a new topic map instance with the given identifier reference.
	 * 
	 * @param locator
	 *            the locator of the topic map
	 * @return the new topic map instance
	 * @throws ModelConstraintException
	 *             thrown if the given locator is already used as locator
	 */
	public ITopicMap createTopicMap(final ILocator locator)
			throws ModelConstraintException;

}
