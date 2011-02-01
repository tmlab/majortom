package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Role;

/**
 * Interface definition of an association role item.
 * 
 * <p>
 * An association role is a representation of the involvement of a subject in a
 * relationship represented by an association. An association role connects two
 * pieces of information within an association: the association role player,
 * that is, the topic participating in the association, and the association role
 * type, that is, a subject describing the nature of the participation of an
 * association role player in an association.
 * </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface IAssociationRole extends Role, IReifiable, ITypeable {

	/**
	 * {@inheritDoc}
	 */
	IAssociation getParent();
	
}
