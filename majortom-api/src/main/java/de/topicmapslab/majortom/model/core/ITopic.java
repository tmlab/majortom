package de.topicmapslab.majortom.model.core;

import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Topic;

/**
 * Interface definition representing a topic item.
 * <p>
 * A topic is a symbol used within a topic map to represent one, and only one,
 * subject, in order to allow statements to be made about the subject. A
 * statement is a claim or assertion about a subject (where the subject may be a
 * topic map construct).
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
	 * @param type the new super type
	 */
	public void addSupertype(Topic type);

	/**
	 * Method removes a super type from the topic item.
	 * 
	 * @param type the super type to remove
	 */
	public void removeSupertype(Topic type);

	/**
	 * Returns all associations using the current topic as acting player.
	 * 
	 * @return a collection of all associations using the current topic as
	 *         acting player.
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed();

	/**
	 * Returns all associations using the current topic as acting player and
	 * being a instance of the given type.
	 * 
	 * @param type the association type
	 * 
	 * @return a collection of all associations of the given type using the
	 *         current topic as acting player.
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type);

	/**
	 * Returns all associations using the current topic as acting player and
	 * being valid in the given scope.
	 * 
	 * @param scope the scope
	 * 
	 * @return a collection of all associations using the current topic as
	 *         acting player and being valid in the given scope.
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(IScope scope);

	/**
	 * Returns all associations using the current topic as acting player, being
	 * a instance of the given type and being valid in the given scope.
	 * 
	 * @param type the association type
	 * @param scope the scope
	 * 
	 * @return a collection of all associations of the given type using the
	 *         current topic as acting player and being valid in the given
	 *         scope.
	 */
	public <T extends Association> Collection<T> getAssociationsPlayed(Topic type, IScope scope);

	/**
	 * Returns all characteristics of the current topic item.
	 * 
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics();

	/**
	 * Returns all characteristics of the current topic item being an instance
	 * of the given type.
	 * 
	 * @param type the characteristics type
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type);

	/**
	 * Returns all characteristics of the current topic item being valid in the
	 * given scope.
	 * 
	 * @param scope the scope
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope);

	/**
	 * Returns all name characteristics of the current topic item being an
	 * instance of the given type and being valid in the given scope.
	 * 
	 * @param type the name type
	 * @param scope the scope
	 * @return a collection of all name characteristics
	 */
	public <T extends Name> Collection<T> getNames(Topic type, IScope scope);

	/**
	 * Returns all name characteristics of the current topic item being valid in
	 * the given scope.
	 * 
	 * @param scope the scope
	 * @return a collection of all name characteristics
	 */
	public <T extends Name> Collection<T> getNames(IScope scope);

	/**
	 * Returns all occurrence characteristics of the current topic item being an
	 * instance of the given type and being valid in the given scope.
	 * 
	 * @param type the occurrence type
	 * @param scope the scope
	 * @return a collection of all occurrence characteristics
	 */
	public <T extends Occurrence> Collection<T> getOccurrences(Topic type, IScope scope);

	/**
	 * Returns all occurrence characteristics of the current topic item being
	 * valid in the given scope.
	 * 
	 * @param scope the scope
	 * @return a collection of all occurrence characteristics
	 */
	public <T extends Occurrence> Collection<T> getOccurrences(IScope scope);

	/**
	 * Returns all characteristics of the current topic item being an instance
	 * of the given type and being valid in the given scope.
	 * 
	 * @param type the characteristics type
	 * @param scope the scope
	 * @return a collection of all characteristics
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type, IScope scope);

	/**
	 * {@inheritDoc}
	 */
	ITopicMap getParent();
}
