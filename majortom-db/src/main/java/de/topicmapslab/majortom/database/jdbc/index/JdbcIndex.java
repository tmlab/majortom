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

import org.tmapi.index.Index;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcIndex implements Index {

	private boolean open = false;
	private final JdbcTopicMapStore store;

	/**
	 * 
	 */
	public JdbcIndex(final JdbcTopicMapStore store) {
		this.store = store;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		this.open = false;
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
		this.open = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void reindex() {
		// NOTHING TO DO
	}

	/**
	 * Returns the internal store reference
	 * 
	 * @return the store the store reference
	 */
	public JdbcTopicMapStore getStore() {
		return store;
	}

}
