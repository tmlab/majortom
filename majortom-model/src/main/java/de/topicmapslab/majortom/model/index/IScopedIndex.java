/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.model.index;

import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;
import org.tmapi.index.ScopedIndex;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;

/**
 * Interface definition of an index of scopable constructs. The index contains a
 * set of method to identifier a scope instance by its themes or get scopable
 * objects by the scope instance.
 * 
 * @author Sven Krosse
 * 
 */
public interface IScopedIndex extends ScopedIndex, IIndex {

	/**
	 * Returns the scope object containing exactly the given themes.
	 * 
	 * @param themes an array of themes
	 * @return the scope object containing this themes or <code>null</code> if
	 *         there is no scope object.
	 */
	public IScope getScope(Topic... themes);

	/**
	 * Returns the scope object containing exactly the given themes.
	 * 
	 * @param themes the themes
	 * @return the scope object containing this themes or <code>null</code> if
	 *         there is no scope object.
	 */
	public IScope getScope(Collection<? extends Topic> themes);

	/**
	 * Returns a collection of scope object containing one combination of the
	 * given themes. The empty scope will never returned except the given array
	 * is empty.
	 * 
	 * @param themes the themes
	 * @return a collection of scope objects containing one combination of the
	 *         given themes
	 */
	public Collection<IScope> getScopes(Topic... themes);

	/**
	 * Returns a collection of scope object containing one combination of the
	 * given themes. The empty scope will never returned except the given array
	 * is empty.
	 * 
	 * @param themes the themes
	 * @param matchAll if value is <code>true</code> the scope object has to
	 *            contain at least each theme, otherwise at least one theme
	 * @return a collection of scope objects containing one combination of the
	 *         given themes
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll);

	/**
	 * Returns a collection of scope object containing one combination of the
	 * given themes. The empty scope will never returned except the given
	 * collection is empty.
	 * 
	 * @param themes the themes
	 * @param matchAll if value is <code>true</code> the scope object has to
	 *            contain at least each theme, otherwise at least one theme
	 * @return a collection of scope objects containing one combination of the
	 *         given themes
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll);

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope the scope
	 * @return a collection of all constructs scoped by the given scope
	 */
	public Collection<Scoped> getScopables(IScope scope);

	/**
	 * Returns all constructs scoped by one of the given scope objects.
	 * 
	 * @param scope the scopes
	 * @return a collection of all constructs scoped by one of the given scopes
	 */
	public Collection<Scoped> getScopables(IScope... scopes);

	/**
	 * Returns all scope objects used as scope of an association item.
	 * 
	 * @return a collection of scope objects
	 */
	public Collection<IScope> getAssociationScopes();

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope the scope
	 * @return a collection of all association items scoped by the given scope
	 */
	public Collection<Association> getAssociations(IScope scope);

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes the scopes
	 * @return a collection of all association items scoped by one of the given
	 *         scopes
	 */
	public Collection<Association> getAssociations(IScope... scopes);

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes the scopes
	 * @return a collection of all association items scoped by one of the given
	 *         scopes
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes);

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope the scope
	 * @return a collection of all characteristics scoped by the given scope
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope);

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope the scope
	 * @return a collection of all characteristics scoped by the given scope
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes);

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * @return a collection of scope objects
	 */
	public Collection<IScope> getOccurrenceScopes();

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope the scope object
	 * @return all occurrences scoped by the given scope object
	 */
	public Collection<Occurrence> getOccurrences(IScope scope);

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all occurrences scoped by one of the given scope objects
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes);

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all occurrences scoped by one of the given scope objects
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * @return a collection of scope objects
	 */
	public Collection<IScope> getNameScopes();

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope the scope object
	 * @return all names scoped by the given scope object
	 */
	public Collection<Name> getNames(IScope scope);

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all names scoped by one of the given scope objects
	 */
	public Collection<Name> getNames(IScope... scopes);

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all names scoped by one of the given scope objects
	 */
	public Collection<Name> getNames(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @return a collection of scope objects
	 */
	public Collection<IScope> getVariantScopes();

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope the scope object
	 * @return all variants scoped by the given scope object
	 */
	public Collection<Variant> getVariants(IScope scope);

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all variants scoped by one of the given scope objects
	 */
	public Collection<Variant> getVariants(IScope... scopes);

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes the scope objects
	 * @return all variants scoped by one of the given scope objects
	 */
	public Collection<Variant> getVariants(Collection<IScope> scopes);
}
