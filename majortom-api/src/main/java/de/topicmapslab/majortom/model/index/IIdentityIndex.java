package de.topicmapslab.majortom.model.index;

import java.util.Collection;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Topic;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.core.ITopicMap;

/**
 * Interface specification of special identity index. The index provides a set
 * of methods to get topic map constructs by identifiers or regular
 * expressisons.
 * 
 * @author Sven Krosse
 * 
 */
public interface IIdentityIndex extends Index {

	/**
	 * Returns all known item-identifiers of the current topic map.
	 * 
	 * @return a collection of all known item-identifiers
	 */
	public Collection<Locator> getItemIdentifiers();

	/**
	 * Returns all known subject-identifiers of the current topic map
	 * 
	 * @return a collection of all known subject-identifiers
	 */
	public Collection<Locator> getSubjectIdentifiers();

	/**
	 * Returns all known subject-locators of the current topic map
	 * 
	 * @return a collection of all known subject-locators
	 */
	public Collection<Locator> getSubjectLocators();

	/**
	 * The method try to identify a construct by the given string reference of
	 * its item-identifier. The given item-identifier will be transformed to a
	 * locator by the topic map function {@link ITopicMap#createLocator(String)}
	 * .
	 * 
	 * @param reference
	 *            the string reference of the item-identifier.
	 * @return the identified construct or <code>null</code>
	 * @throws MalformedIriException
	 *             thrown by {@link ITopicMap#createLocator(String)}
	 */
	public Construct getConstructByItemIdentifier(
			final String reference) throws MalformedIRIException;

	/**
	 * The method try to identify a construct by the given item-identifier.
	 * 
	 * @param locator
	 *            the identifier
	 * @return the identified construct or <code>null</code>
	 */
	public Construct getConstructByItemIdentifier(
			final Locator locator);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified constructs but never
	 *         <code>null</code>
	 */
	public Collection<Construct> getConstructsByItemIdentifier(
			final String regExp);

	/**
	 * The method try to identify all construct using an item-identifier
	 * matching the given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified constructs but never
	 *         <code>null</code>
	 */
	public Collection<Construct> getConstructsByItemIdentifier(
			final Pattern regExp);

	/**
	 * The method try to identify a topic by the given string reference of its
	 * subject-identifier. The given subject-identifier will be transformed to a
	 * locator by the topic map function {@link ITopicMap#createLocator(String)}
	 * .
	 * 
	 * @param reference
	 *            the string reference of the subject-identifier.
	 * @return the identified topic or <code>null</code>
	 * @throws MalformedIriException
	 *             thrown by {@link ITopicMap#createLocator(String)}
	 */
	public Topic getTopicBySubjectIdentifier(
			final String reference) throws MalformedIRIException;

	/**
	 * The method try to identify a topic by the given subject-identifier.
	 * 
	 * @param locator
	 *            the identifier
	 * @return the identified topic or <code>null</code>
	 */
	public Topic getTopicBySubjectIdentifier(final Locator locator);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified topics but never <code>null</code>
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(
			final String regExp);

	/**
	 * The method try to identify all construct using an subject-identifier
	 * matching the given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified topics but never <code>null</code>
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(
			final Pattern regExp);

	/**
	 * The method try to identify a topic by the given string reference of its
	 * subject-locator. The given subject-locator will be transformed to a
	 * locator by the topic map function {@link ITopicMap#createLocator(String)}
	 * .
	 * 
	 * @param reference
	 *            the string reference of the subject-locator
	 * @return the identified topic or <code>null</code>
	 * @throws MalformedIriException
	 *             thrown by {@link ITopicMap#createLocator(String)}
	 */
	public Topic getTopicBySubjectLocator(final String reference)
			throws MalformedIRIException;

	/**
	 * The method try to identify a topic by the given subject-locator.
	 * 
	 * @param locator
	 *            the identifier
	 * @return the identified topic or <code>null</code>
	 */
	public Topic getTopicBySubjectLocator(final Locator locator);

	/**
	 * The method try to identify all topic using a subject-locator matching the
	 * given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified topics but never <code>null</code>
	 */
	public Collection<Topic> getTopicsBySubjectLocator(
			final String regExp);

	/**
	 * The method try to identify all topic using a subject-locator matching the
	 * given regular expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified topics but never <code>null</code>
	 */
	public Collection<Topic> getTopicsBySubjectLocator(
			final Pattern regExp);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-locator matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified constructs but never
	 *         <code>null</code>
	 */
	public Collection<Construct> getConstructsByIdentifier(
			final String regExp);

	/**
	 * The method try to identify all construct using an item-identifier,
	 * subject-identifier or subject-locator matching the given regular
	 * expression.
	 * 
	 * @param regExp
	 *            the regular expression
	 * @return a collection of all identified constructs but never
	 *         <code>null</code>
	 */
	public Collection<Construct> getConstructsByIdentifier(
			final Pattern regExp);

	/**
	 * Method checks if the given string reference is known as
	 * subject-identifier.
	 * 
	 * @param reference
	 *            the reference
	 * @return <code>true</code> if there is a subject-identifier with this
	 *         reference, <code>false</code> otherwise.
	 */
	public boolean existsSubjectIdentifier(final String reference);

	/**
	 * Method checks if the given string reference is known as
	 * subject-identifier.
	 * 
	 * @param reference
	 *            the reference
	 * @return <code>true</code> if there is a subject-identifier with this
	 *         reference, <code>false</code> otherwise.
	 */
	public boolean existsSubjectIdentifier(final Locator locator);

	/**
	 * Method checks if the given string reference is known as subject-locator.
	 * 
	 * @param reference
	 *            the reference
	 * @return <code>true</code> if there is a subject-locator with this
	 *         reference, <code>false</code> otherwise.
	 */
	public boolean existsSubjectLocator(final String reference);

	/**
	 * Method checks if the given locator is known as subject-locator.
	 * 
	 * @param locator
	 *            the locator
	 * @return <code>true</code> if there is a subject-locator,
	 *         <code>false</code> otherwise.
	 */
	public boolean existsSubjectLocator(final Locator locator);

	/**
	 * Method checks if the given string reference is known as item-identifier.
	 * 
	 * @param reference
	 *            the reference
	 * @return <code>true</code> if there is a item-identifier with this
	 *         reference, <code>false</code> otherwise.
	 */
	public boolean existsItemIdentifier(final String reference);

	/**
	 * Method checks if the given locator is known as item-identifier.
	 * 
	 * @param locator
	 *            the locator
	 * @return <code>true</code> if there is a item-identifier,
	 *         <code>false</code> otherwise.
	 */
	public boolean existsItemIdentifier(final Locator locator);

	/**
	 * Method checks if the given string reference is known as
	 * subject-identifier, subject-locator or item-identifier
	 * 
	 * @param reference
	 *            the reference
	 * @return <code>true</code> if there is a subject-identifier,
	 *         subject-locator or item-identifier with this reference,
	 *         <code>false</code> otherwise.
	 */
	public boolean existsIdentifier(final String reference);

	/**
	 * Method checks if the given locator is known as subject-identifier,
	 * subject-locator or item-identifier.
	 * 
	 * @param locator
	 *            the locator
	 * @return <code>true</code> if there is a subject-identifier,
	 *         subject-locator or item-identifier, <code>false</code> otherwise.
	 */
	public boolean existsIdentifier(final Locator locator);

}
