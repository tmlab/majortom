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
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;

/**
 * @author Sven Krosse
 * 
 */
public class ConcurrentTypeInstanceIndex extends ConcurentIndexImpl<ITypeInstanceIndex> implements ITypeInstanceIndex {

	/**
	 * @param parentIndex
	 * @param lock
	 */
	public ConcurrentTypeInstanceIndex(ITypeInstanceIndex parentIndex, Lock lock) {
		super(parentIndex, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopics(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic[] types, boolean matchAll) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopics(types, matchAll);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopicTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociationTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getRoles(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getRoleTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getRoleTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrenceTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNameTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopics(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopics(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getTopics(types, all);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getAssociations(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getRoles(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getRoles(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getCharacteristicTypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristicTypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getCharacteristics(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getNames(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getOccurrences(types);
		} finally {
			lock.unlock();
		}
	}

}
