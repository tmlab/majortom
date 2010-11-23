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

import java.util.concurrent.locks.Lock;

import de.topicmapslab.majortom.model.index.IIndex;

/**
 * Base class of a concurrent index implementation
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ConcurentIndexImpl<T extends IIndex> implements IIndex {

	private final T parentIndex;
	final Lock lock;

	public ConcurentIndexImpl(T parentIndex, Lock lock) {
		this.parentIndex = parentIndex;
		this.lock = lock;
	}

	/**
	 * @return the parentIndex
	 */
	public T getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		parentIndex.open();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		parentIndex.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOpen() {
		return parentIndex.isOpen();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoUpdated() {
		return parentIndex.isAutoUpdated();
	}

	/**
	 * {@inheritDoc}
	 */
	public void reindex() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			parentIndex.reindex();
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		try {
			while (!lock.tryLock()) {
				// WAIT
			}
			parentIndex.clear();
		} finally {
			lock.unlock();
		}
	}
	
}
