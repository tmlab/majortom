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

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;

/**
 * @author Sven Krosse
 * 
 */
public class ConcurrentSupertypeSubtypeIndex extends ConcurentIndexImpl<ISupertypeSubtypeIndex> implements ISupertypeSubtypeIndex {

	/**
	 * @param parentIndex
	 * @param lock
	 */
	public ConcurrentSupertypeSubtypeIndex(ISupertypeSubtypeIndex parentIndex, Lock lock) {
		super(parentIndex, lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSupertypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSupertypes(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSupertypes(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDirectSupertypes(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSupertypes(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSupertypes(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes(Collection<? extends Topic> types, boolean all) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSupertypes(types, all);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubtypes();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubtypes(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getDirectSubtypes(Topic type) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getDirectSubtypes(type);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Topic... types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubtypes(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubtypes(types);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSubtypes(Collection<? extends Topic> types, boolean all) {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			return getParentIndex().getSubtypes(types, all);
		} finally {
			lock.unlock();
		}
	}

}
