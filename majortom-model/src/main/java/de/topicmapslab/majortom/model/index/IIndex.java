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
package de.topicmapslab.majortom.model.index;

import org.tmapi.index.Index;

import de.topicmapslab.majortom.model.exception.IndexException;

/**
 * Interface definition of an index of the topic maps engine.
 * 
 * @author Sven Krosse
 * 
 */
public interface IIndex extends Index {

	/**
	 * Checks if the current index is current synchronous with the data stored
	 * in the data store.
	 * 
	 * @return <code>true</code> if the index is synchronous, <code>false</code>
	 *         otherwise.
	 */
	public boolean isSynchronous();

	/**
	 * Method synchronize the current index instance with the underlying data
	 * store.
	 * 
	 * @throws IndexException
	 *             thrown if the synchronization of the index fails
	 */
	public void synchronize() throws IndexException;

}
