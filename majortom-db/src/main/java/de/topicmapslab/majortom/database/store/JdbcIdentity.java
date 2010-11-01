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
package de.topicmapslab.majortom.database.store;

import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;

/**
 * Implementation of {@link ITopicMapStoreIdentity} for a
 * {@link JdbcTopicMapStore}.
 * 
 * @author Sven Krosse
 * 
 */
public class JdbcIdentity implements ITopicMapStoreIdentity {

	/**
	 * the internal id
	 */
	private long id;

	/**
	 * constructor
	 * 
	 * @param id
	 *            the id
	 */
	public JdbcIdentity(final long id) {
		this.id = id;
	}

	/**
	 * Return the internal id of the construct
	 * 
	 * @return the id
	 */
	public String getId() {
		return String.valueOf(id);
	}
	
	/**
	 * Return the internal id of the constructn as long
	 * 
	 * @return the id
	 */
	public long longId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof JdbcIdentity) {
			return ((JdbcIdentity) obj).longId() == id;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}

	/**
	 * Modify the underlying id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {		
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setId(String id) {
		this.id = Long.parseLong(id);
	}

}
