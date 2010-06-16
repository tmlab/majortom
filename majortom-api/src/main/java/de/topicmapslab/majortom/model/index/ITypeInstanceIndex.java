package de.topicmapslab.majortom.model.index;

import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.index.TypeInstanceIndex;

import de.topicmapslab.majortom.model.core.ICharacteristics;

/**
 * Interface definition of an index handling the type-instance-relation of topic
 * types of the current topic map.
 * 
 * @author Sven Krosse
 * 
 */
public interface ITypeInstanceIndex extends TypeInstanceIndex {

	/**
	 * Returns all instances being typed by at least of one given topic type.
	 * 
	 * @param types the topic types
	 * @return a collection of all instances typed by at least one of the given
	 *         types
	 */
	public Collection<Topic> getTopics(Topic... types);

	/**
	 * Returns all instances of at least one of given topic type.
	 * 
	 * @param types the topic types
	 * @return a collection of all instances typed by at least one of the given
	 *         types
	 */
	public Collection<Topic> getTopics(Collection<Topic> types);

	/**
	 * Returns all instances of at least one given type or of every given topic
	 * type.
	 * 
	 * @param types the topic types
	 * @param all flag indicates if the found instances should be typed by every
	 *            given type
	 * @return a collection of all instances typed by at least one or every of
	 *         the given types
	 */
	public Collection<Topic> getTopics(Collection<? extends Topic> types, boolean all);

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all association items typed by one of the given
	 *         types
	 */
	public Collection<Association> getAssociations(Topic... types);

	/**
	 * Returns all association items typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all association items typed by one of the given
	 *         types
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all association roles typed by one of the given
	 *         types
	 */
	public Collection<Role> getRoles(Topic... types);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all association roles typed by one of the given
	 *         types
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types);

	/**
	 * Returns all characteristic types contained by the topic map.
	 * 
	 * @return a collection of all types
	 */
	public Collection<Topic> getCharacteristicTypes();

	/**
	 * Returns all characteristics being typed by the given of topic type.
	 * 
	 * @param type the topic type
	 * 
	 * @return a collection of all characteristics typed by the given type
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type);

	/**
	 * Returns all characteristics typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all characteristics typed by one of the given
	 *         types
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types);

	/**
	 * Returns all characteristics typed by one of given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all characteristics typed by one of the given
	 *         types
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types);

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all names typed by one of the given types
	 */
	public Collection<Name> getNames(Topic... types);

	/**
	 * Returns all names typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all names typed by one of the given types
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types);

	/**
	 * Returns all association roles typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all association roles typed by one of the given
	 *         types
	 */
	public Collection<Occurrence> getOccurrences(Topic... types);

	/**
	 * Returns all occurrences typed by one of the given types.
	 * 
	 * @param types the topic types
	 * @return a collection of all occurrences typed by one of the given types
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types);
}
