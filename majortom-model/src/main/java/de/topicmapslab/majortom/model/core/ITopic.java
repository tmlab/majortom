package de.topicmapslab.majortom.model.core;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

/**
 * Interface definition representing a topic item.
 * <p>
 * A topic is a symbol used within a topic map to represent one, and only one, subject, in order to allow statements to
 * be made about the subject. A statement is a claim or assertion about a subject (where the subject may be a topic map
 * construct).
 * </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface ITopic extends Topic, IConstruct {

	/**
	 * Method returns all super-types of the current topic item.
	 * 
	 * @return a collection containing the super types
	 */
	public Collection<Topic> getSupertypes();

	/**
	 * Method add a new super type to the topic item.
	 * 
	 * @param type
	 *            the new super type
	 */
	public void addSupertype(Topic type);

	/**
	 * Method removes a super type from the topic item.
	 * 
	 * @param type
	 *            the super type to remove
	 */
	public void removeSupertype(Topic type);

	/**
	 * Returns all associations using the current topic as acting player.
	 * 
	 * @return a collection of all associations using the current topic as acting player.
	 */
	public Collection<Association> getAssociationsPlayed();

	/**
	 * Returns all associations using the current topic as acting player and being a instance of the given type.
	 * 
	 * @param type
	 *            the association type
	 * 
	 * @return a collection of all associations of the given type using the current topic as acting player.
	 */
	public Collection<Association> getAssociationsPlayed(Topic type);

	/**
	 * Returns all associations using the current topic as acting player and being valid in the given scope.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @return a collection of all associations using the current topic as acting player and being valid in the given
	 *         scope.
	 */
	public Collection<Association> getAssociationsPlayed(IScope scope);

	/**
	 * Returns all associations using the current topic as acting player, being a instance of the given type and being
	 * valid in the given scope.
	 * 
	 * @param type
	 *            the association type
	 * @param scope
	 *            the scope
	 * 
	 * @return a collection of all associations of the given type using the current topic as acting player and being
	 *         valid in the given scope.
	 */
	public Collection<Association> getAssociationsPlayed(Topic type, IScope scope);

	/**
	 * Returns all characteristics of the current topic item.
	 * 
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics();

	/**
	 * Returns all characteristics of the current topic item being an instance of the given type.
	 * 
	 * @param type
	 *            the characteristics type
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type);

	/**
	 * Returns all characteristics of the current topic item being valid in the given scope.
	 * 
	 * @param scope
	 *            the scope
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope);

	/**
	 * Returns all name characteristics of the current topic item being an instance of the given type and being valid in
	 * the given scope.
	 * 
	 * @param type
	 *            the name type
	 * @param scope
	 *            the scope
	 * @return a collection of all name characteristics
	 */
	public Collection<Name> getNames(Topic type, IScope scope);

	/**
	 * Returns all name characteristics of the current topic item being valid in the given scope.
	 * 
	 * @param scope
	 *            the scope
	 * @return a collection of all name characteristics
	 */
	public Collection<Name> getNames(IScope scope);

	/**
	 * Returns all occurrence characteristics of the current topic item being an instance of the given type and being
	 * valid in the given scope.
	 * 
	 * @param type
	 *            the occurrence type
	 * @param scope
	 *            the scope
	 * @return a collection of all occurrence characteristics
	 */
	public Collection<Occurrence> getOccurrences(Topic type, IScope scope);

	/**
	 * Returns all occurrence characteristics of the current topic item being valid in the given scope.
	 * 
	 * @param scope
	 *            the scope
	 * @return a collection of all occurrence characteristics
	 */
	public Collection<Occurrence> getOccurrences(IScope scope);

	/**
	 * Returns all characteristics of the current topic item being an instance of the given type and being valid in the
	 * given scope.
	 * 
	 * @param type
	 *            the characteristics type
	 * @param scope
	 *            the scope
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope);

	/**
	 * {@inheritDoc}
	 */
	ITopicMap getParent();

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @since 1.1.2
	 */
	public String getBestLabel();

	/**
	 * <p>
	 * <b>Note:</b> Similar to {@link #getBestLabel(Topic, <code>false</code>)};
	 * </p>
	 * 
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the the smallest scope containing the given theme.
	 * </p>
	 * <p>
	 * 3. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 4. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 6. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 7. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 9. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @param theme
	 *            the theme
	 * @since 1.1.2
	 */
	public String getBestLabel(Topic theme);

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the the smallest scope containing the given theme.
	 * </p>
	 * <p>
	 * 3. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 4. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 6. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 7. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 9. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @param theme
	 *            the theme
	 * @param strict
	 *            if there is no name with the given theme and strict is <code>true</code>, then <code>null</code> will
	 *            be returned.
	 * @since 1.1.2
	 */
	public String getBestLabel(Topic theme, boolean strict);
}
