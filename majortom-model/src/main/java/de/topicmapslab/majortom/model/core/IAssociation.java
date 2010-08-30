package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Association;

/**
 * Interface definition of an association item.
 * <p>
 * An association is a representation of a relationship between one or more
 * subjects. Associations have an association type, a subject describing the
 * nature of the relationship represented by associations of that type.
 * </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface IAssociation extends Association, IConstruct, ITypeable, IReifiable, IScopable {

	/**
	 * {@inheritDoc}
	 */
	ITopicMap getParent();

}
