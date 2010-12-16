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
package de.topicmapslab.majortom.model.index.paging;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.index.IIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;

/**
 * Special {@link IScopedIndex} to support paging.
 * 
 * @author Sven Krosse
 * 
 */
public interface IPagedScopedIndex extends IIndex {

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all constructs within the given range scoped by the
	 *         given scope
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit);

	/**
	 * Returns all constructs scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all constructs within the given range scoped by the
	 *         given scope
	 */
	public List<Scoped> getScopables(IScope scope, int offset, int limit,
			Comparator<Scoped> comparator);
	
	/**
	 * Returns the number of constructs scoped by the given scope
	 * @param scope the scope
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfScopables(IScope scope);

	/**
	 * Returns all scope objects used as scope of an association item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of scope objects within the given range
	 */
	public List<IScope> getAssociationScopes(int offset, int limit);

	/**
	 * Returns all scope objects used as scope of an association item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of scope objects within the given range
	 */
	public List<IScope> getAssociationScopes(int offset, int limit,
			Comparator<IScope> comparator);
	
	/**
	 * Returns the number of association scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociationScopes();

	/**
	 * Returning all themes contained by at least one association scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         association scope.
	 */
	public List<Topic> getAssociationThemes(int offset, int limit);

	/**
	 * Returning all themes contained by at least one association scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one
	 *         association scope.
	 */
	public List<Topic> getAssociationThemes(int offset, int limit,
			Comparator<Topic> comparator);
	
	/**
	 * Returns the number of association themes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociationThemes();

	/**
	 * Returning all associations in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations within the given range
	 */
	public List<Association> getAssociations(Topic theme, int offset, int limit);

	/**
	 * Returning all associations in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations within the given range
	 */
	public List<Association> getAssociations(Topic theme, int offset,
			int limit, Comparator<Association> comparator);

	/**
	 * Returns the number of association scoped by a scope containing the theme
	 * @param theme the theme
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(Topic theme);
	
	/**
	 * Returning all associations in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all associations within the given range
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all,
			int offset, int limit);
	
	/**
	 * Returns the number of all associations in the scope containing the given themes.
	 * @param themes the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(Topic[] themes, boolean all);

	/**
	 * Returning all associations in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all associations within the given range
	 */
	public List<Association> getAssociations(Topic[] themes, boolean all,
			int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items within the given range scoped by
	 *         the given scope
	 */
	public List<Association> getAssociations(IScope scope, int offset, int limit);

	/**
	 * Returns all association items scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association items within the given range scoped by
	 *         the given scope
	 */
	public List<Association> getAssociations(IScope scope, int offset,
			int limit, Comparator<Association> comparator);
	
	/**
	 * Returns the number of association scoped by the scope 
	 * @param scope the scope
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(IScope scope);

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all association items within the given range scoped by
	 *         one of the given scopes
	 */
	public List<Association> getAssociations(Collection<IScope> scopes,
			int offset, int limit);
	
	/**
	 * Returns the number of association scoped by one of the scopes 
	 * @param scopes the scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfAssociations(Collection<IScope> scopes);

	/**
	 * Returns all association items scoped by one of the given scope objects.
	 * 
	 * @param scopes
	 *            the scopes
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all association items within the given range scoped by
	 *         one of the given scopes
	 */
	public List<Association> getAssociations(Collection<IScope> scopes,
			int offset, int limit, Comparator<Association> comparator);

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all characteristics within the given range scoped by
	 *         the given scope
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset,
			int limit);

	/**
	 * Returns all characteristics scoped by the given scope object.
	 * 
	 * @param scope
	 *            the scope
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all characteristics within the given range scoped by
	 *         the given scope
	 */
	public List<ICharacteristics> getCharacteristics(IScope scope, int offset,
			int limit, Comparator<ICharacteristics> comparator);

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of scope objects within the given range
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit);

	/**
	 * Returns all scope objects used as scope of an occurrence item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of scope objects within the given range
	 */
	public List<IScope> getOccurrenceScopes(int offset, int limit,
			Comparator<IScope> comparator);
	

	/**
	 * Returns the number of occurrence scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrenceScopes();

	/**
	 * Returning all themes contained by at least one occurrence scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         occurrence scope.
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit);

	/**
	 * Returning all themes contained by at least one occurrence scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one
	 *         occurrence scope.
	 */
	public List<Topic> getOccurrenceThemes(int offset, int limit,
			Comparator<Topic> comparator);
	
	/**
	 * Returns the number of occurrence themes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrenceThemes();

	/**
	 * Returning all occurrences in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit);

	/**
	 * Returning all occurrences in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range
	 */
	public List<Occurrence> getOccurrences(Topic theme, int offset, int limit,
			Comparator<Occurrence> comparator);
	
	/**
	 * Returns the number of occurrences scoped by a scope containing the theme
	 * @param theme the theme
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(Topic theme);

	/**
	 * Returning all occurrences in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all,
			int offset, int limit);

	/**
	 * Returning all occurrences in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range
	 */
	public List<Occurrence> getOccurrences(Topic[] themes, boolean all,
			int offset, int limit, Comparator<Occurrence> comparator);
	

	/**
	 * Returns the number of all occurrences in the scope containing the given themes.
	 * @param themes the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(Topic[] themes, boolean all);

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences scoped by the given scope object within the given
	 *         range
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit);

	/**
	 * Return all occurrences scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences scoped by the given scope object within the given
	 *         range
	 */
	public List<Occurrence> getOccurrences(IScope scope, int offset, int limit,
			Comparator<Occurrence> comparator);
	
	/**
	 * Returns the number of occurrences scoped by the scope 
	 * @param scope the scope
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(IScope scope);

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all occurrences within the given range scoped by one of the given
	 *         scope objects
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes,
			int offset, int limit);

	/**
	 * Return all occurrences scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all occurrences within the given range scoped by one of the given
	 *         scope objects
	 */
	public List<Occurrence> getOccurrences(Collection<IScope> scopes,
			int offset, int limit, Comparator<Occurrence> comparator);
	
	/**
	 * Returns the number of occurrences scoped by one of the scopes 
	 * @param scopes the scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfOccurrences(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a collection of scope objects within the given range
	 */
	public List<IScope> getNameScopes(int offset, int limit);

	/**
	 * Returns all scope objects used as scope of a name item.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of scope objects within the given range
	 */
	public List<IScope> getNameScopes(int offset, int limit,
			Comparator<IScope> comparator);
	

	/**
	 * Returns the number of name scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNameScopes();

	/**
	 * Returning all themes contained by at least one name scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one name
	 *         scope.
	 */
	public List<Topic> getNameThemes(int offset, int limit);

	/**
	 * Returning all themes contained by at least one name scope.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all themes within the given range contained by at least one name
	 *         scope.
	 */
	public List<Topic> getNameThemes(int offset, int limit,
			Comparator<Topic> comparator);
	
	/**
	 * Returns the number of name themes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNameThemes();

	/**
	 * Returning all names in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range
	 */
	public List<Name> getNames(Topic theme, int offset, int limit);

	/**
	 * Returning all names in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range
	 */
	public List<Name> getNames(Topic theme, int offset, int limit,
			Comparator<Name> comparator);
	
	/**
	 * Returns the number of names scoped by a scope containing the theme
	 * @param theme the theme
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(Topic theme);

	/**
	 * Returning all names in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset,
			int limit);

	/**
	 * Returning all names in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range
	 */
	public List<Name> getNames(Topic[] themes, boolean all, int offset,
			int limit, Comparator<Name> comparator);
	

	/**
	 * Returns the number of all names in the scope containing the given themes.
	 * @param themes the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(Topic[] themes, boolean all);

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names within the given range scoped by the given scope object
	 */
	public List<Name> getNames(IScope scope, int offset, int limit);
	
	/**
	 * Returns the number of names scoped by the scope 
	 * @param scope the scope
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(IScope scope);

	/**
	 * Return all names scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range scoped by the given scope object
	 */
	public List<Name> getNames(IScope scope, int offset, int limit,
			Comparator<Name> comparator);

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all names scoped by one of the given scope objects
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset, int limit);

	/**
	 * Return all names scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all names within the given range scoped by one of the given scope
	 *         objects
	 */
	public List<Name> getNames(Collection<IScope> scopes, int offset,
			int limit, Comparator<Name> comparator);
	
	/**
	 * Returns the number of names scoped by one of the scopes 
	 * @param scopes the scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfNames(Collection<IScope> scopes);

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a collection of scope objects within the given range
	 */
	public List<IScope> getVariantScopes(int offset, int limit);

	/**
	 * Returns all scope objects used as scope of a variant item.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a collection of scope objects within the given range
	 */
	public List<IScope> getVariantScopes(int offset, int limit,
			Comparator<IScope> comparator);
	

	/**
	 * Returns the number of variant scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariantScopes();

	/**
	 * Returning all themes contained by at least one variant scope.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all themes within the given range contained by at least one
	 *         variant scope.
	 */
	public List<Topic> getVariantThemes(int offset, int limit);

	/**
	 * Returning all themes contained by at least one variant scope.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return all themes within the given range contained by at least one
	 *         variant scope.
	 */
	public List<Topic> getVariantThemes(int offset, int limit,
			Comparator<Topic> comparator);
	
	/**
	 * Returns the number of variant themes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariantThemes();

	/**
	 * Returning all variants in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit);

	/**
	 * Returning all variants in the scope containing the given theme.
	 * 
	 * @param theme
	 *            the theme
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range
	 */
	public List<Variant> getVariants(Topic theme, int offset, int limit,
			Comparator<Variant> comparator);

	/**
	 * Returns the number of variants scoped by a scope containing the theme
	 * @param theme the theme
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariants(Topic theme);
	
	/**
	 * Returning all variants in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset,
			int limit);

	/**
	 * Returning all variants in the scope containing the given themes.
	 * 
	 * @param themes
	 *            the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range
	 */
	public List<Variant> getVariants(Topic[] themes, boolean all, int offset,
			int limit, Comparator<Variant> comparator);
	

	/**
	 * Returns the number of all variants in the scope containing the given themes.
	 * @param themes the themes
	 * @param all
	 *            flag indicates if all themes have to be contained
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariants(Topic[] themes, boolean all);

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range scoped by the given scope
	 *         object
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit);

	/**
	 * Return all variants scoped by the given scope object
	 * 
	 * @param scope
	 *            the scope object
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range scoped by the given scope
	 *         object
	 */
	public List<Variant> getVariants(IScope scope, int offset, int limit,
			Comparator<Variant> comparator);
	
	/**
	 * Returns the number of variants scoped by the scope 
	 * @param scope the scope
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariants(IScope scope);

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return all variants within the given range scoped by one of the given
	 *         scope objects
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset,
			int limit);

	/**
	 * Return all variants scoped by one of the given scope objects
	 * 
	 * @param scopes
	 *            the scope objects
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return all variants within the given range scoped by one of the given
	 *         scope objects
	 */
	public List<Variant> getVariants(Collection<IScope> scopes, int offset,
			int limit, Comparator<Variant> comparator);
	
	/**
	 * Returns the number of variants scoped by one of the scopes 
	 * @param scopes the scopes
	 * @return the number
	 * @since 1.2.0
	 */
	public long getNumberOfVariants(Collection<IScope> scopes);

}
