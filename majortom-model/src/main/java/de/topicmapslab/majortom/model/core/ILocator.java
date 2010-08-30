package de.topicmapslab.majortom.model.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.tmapi.core.Locator;

/**
 * Interface definition representing an identifier of a topic map construct. A
 * locator can be used as subject-identifier, subject-locator or
 * item-identifier.
 * 
 * @author Sven Krosse
 * 
 */
public interface ILocator extends Locator {

	/**
	 * Returns the locator as instance of {@link URI}.
	 * 
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             thrown if the reference is not a valid IRI
	 */
	public URI toUri() throws URISyntaxException;

}
