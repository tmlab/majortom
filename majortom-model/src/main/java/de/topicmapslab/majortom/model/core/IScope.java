package de.topicmapslab.majortom.model.core;

import java.util.Set;

/**
 * Interface definition of a scope of an {@link IScopable}.
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
public interface IScope {

	/**
	 * Returns a collection containing all themes of this scope object.
	 * 
	 * @return the themes
	 */
	public <T extends ITopic> Set<T> getThemes();

	/**
	 * Checks if the scope contains the given theme
	 * 
	 * @param theme the theme
	 * @return <code>true</code> if the theme is contained, <code>false</code>
	 *         otherwise.
	 */
	public boolean containsTheme(final ITopic theme);
	
	/**
	 * Returns the internal id of the scope object
	 * @return the id;
	 */
	public String getId();

}
