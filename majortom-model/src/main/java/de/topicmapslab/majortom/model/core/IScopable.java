package de.topicmapslab.majortom.model.core;

import org.tmapi.core.Scoped;

/**
 * Interface definition representing special topic map construct which are
 * scopable.
 * 
 * <p>
 * All statements have a scope. The scope represents the context within which a
 * statement is valid. Outside the context represented by the scope the
 * statement is not known to be valid. Formally, a scope is composed of a set of
 * topics that together define the context. That is, the statement is known to
 * be valid only in contexts where all the subjects in the scope apply.
 * </p>
 * 
 * </p> The unconstrained scope is the scope used to indicate that a statement
 * is considered to have unlimited validity. In the model this is represented by
 * the empty set. </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface IScopable extends Scoped, IConstruct{

	/**
	 * Returns the scope of this scopable construct.
	 * 
	 * @return the scope
	 */
	public IScope getScopeObject();
	
	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap();

}
