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
package de.topicmapslab.majortom.queued.store.index;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.index.IScopedIndex;

/**
 * @author Sven Krosse
 * 
 */
public class ConcurrentScopedIndex extends ConcurentIndexImpl<IScopedIndex> implements IScopedIndex {

	/**
	 * constructor
	 * 
	 * @param parentIndex
	 *            the parent index
	 * @param lock
	 *            the lock
	 */
	public ConcurrentScopedIndex(IScopedIndex parentIndex, Lock lock) {
		super(parentIndex, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic theme) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(theme);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic[] themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationThemes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociationThemes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic theme) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(theme);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic[] themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceThemes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrenceThemes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic theme) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(theme);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic[] themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameThemes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNameThemes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic theme) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(theme);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Topic[] themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getVariantThemes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariantThemes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Topic... themes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScope(themes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScope(Collection<? extends Topic> themes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScope(themes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic... themes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScopes(themes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Topic[] themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScopes(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getScopes(Collection<Topic> themes, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScopes(themes, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScopables(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Scoped> getScopables(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getScopables(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getAssociationScopes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociationScopes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<IScope> scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getOccurrenceScopes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrenceScopes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<IScope> scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getNameScopes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNameScopes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<IScope> scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<IScope> getVariantScopes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariantScopes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope scope) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(scope);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(IScope... scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(scopes);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Variant> getVariants(Collection<IScope> scopes) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getVariants(scopes);
		} finally {
			lock.unlock();
		}
	}

}
