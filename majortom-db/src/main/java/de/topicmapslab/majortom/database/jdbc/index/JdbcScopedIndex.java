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
/**
 * 
 */
package de.topicmapslab.majortom.database.jdbc.index;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcScopedIndex extends JdbcIndex implements IScopedIndex {

	/**
	 * @param store
	 */
	public JdbcScopedIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic arg0) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic[] arg0, boolean arg1) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic arg0) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic[] arg0, boolean arg1) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic arg0) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic[] arg0, boolean arg1) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<? extends Topic> themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic... themes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), Arrays.asList(themes), false);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), Arrays.asList(themes), matchAll);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getStore().getProcessor().getScopesByThemes(getStore().getTopicMap(), themes, matchAll);
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope scope) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope... scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getVariantThemes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic arg0) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic[] arg0, boolean arg1) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// TODO Auto-generated method stub
		return null;
	}

}
