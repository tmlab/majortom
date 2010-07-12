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
package de.topicmapslab.majortom.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of {@link IScope}
 * 
 * @author Sven Krosse
 * 
 */
public class ScopeImpl implements IScope {

	/**
	 * internal theme set
	 */
	private final Set<ITopic> themes;

	/**
	 * the internal id
	 */
	private final String id;

	/**
	 * constructor
	 * 
	 * @param id
	 *            the internal id
	 */
	public ScopeImpl(String id) {
		this.themes = HashUtil.getHashSet();
		this.id = id;
	}

	/**
	 * constructor
	 */
	public ScopeImpl() {
		this.themes = HashUtil.getHashSet();
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * constructor
	 * 
	 * @param id
	 *            the internal id
	 * @param themes
	 *            the themes
	 */
	public ScopeImpl(String id, Collection<ITopic> themes) {
		this.themes = HashUtil.getHashSet(themes);		
		this.id = id;
	}

	/**
	 * constructor
	 * 
	 * @param themes
	 *            the themes
	 */
	public ScopeImpl(Set<ITopic> themes) {
		this.themes = HashUtil.getHashSet(themes);
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsTheme(ITopic theme) {
		return themes.contains(theme);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public <T extends ITopic> Set<T> getThemes() {
		return Collections.unmodifiableSet((Set<T>) HashUtil.getHashSet(themes));
	}

	/**
	 * replace the given theme by the given replacement
	 * 
	 * @param theme
	 *            the theme
	 * @param replacement
	 *            the replacement
	 */
	public void replaceTheme(ITopic theme, ITopic replacement) {
		themes.remove(theme);
		themes.add(replacement);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Scope: " + themes.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof IScope) {
			return ((IScope) arg0).getThemes().equals(themes);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return themes.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}
}
