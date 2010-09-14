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
package de.topicmapslab.majortom.index;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * @author Sven Krosse
 * 
 */
public abstract class IndexImpl<T extends ITopicMapStore> implements Index {

	/**
	 * the parent store
	 */
	private final T store;
	/**
	 * opening flag
	 */
	private boolean open = false;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the store instance
	 */
	public IndexImpl(T store) {
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		open = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoUpdated() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		open = true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		//NOTHING TO DO
	}

	/**
	 * {@inheritDoc}
	 */
	public void reindex() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		// NOTHING TO DO
	}

	/**
	 * Returns the internal store reference
	 * 
	 * @return the store
	 */
	protected T getStore() {
		return store;
	}

}
