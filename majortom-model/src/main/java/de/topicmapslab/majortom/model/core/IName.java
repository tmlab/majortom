package de.topicmapslab.majortom.model.core;

import java.util.Set;

import org.tmapi.core.Name;
import org.tmapi.core.Variant;

/**
 * Interface definition representing a name characteristics of a topic item.
 * 
 * @author Sven Krosse
 * 
 */
public interface IName extends Name, IConstruct, ICharacteristics, ITypeable, IReifiable, IScopable {

	/**
	 * Return all variants with the given scope.
	 * 
	 * @param scope the scope
	 * @return the variants
	 */
	public Set<Variant> getVariants(IScope scope);
	
	/**
	 * {@inheritDoc}
	 */
	public ITopic getParent();

}
