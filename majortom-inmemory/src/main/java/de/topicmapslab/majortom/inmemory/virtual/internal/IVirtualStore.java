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
package de.topicmapslab.majortom.inmemory.virtual.internal;

import de.topicmapslab.majortom.inmemory.store.model.IDataStore;
import de.topicmapslab.majortom.model.core.IConstruct;

/**
 * @author Sven Krosse
 * 
 */
public interface IVirtualStore extends IDataStore {

	/**
	 * Removes the virtual construct from the internal memory layer. But does not delete the construct in underlying
	 * data store.
	 * 
	 * @param construct
	 *            the construct to remove
	 * @param newConstruct
	 *            the new construct
	 */
	public void removeVirtualConstruct(IConstruct construct, IConstruct newConstruct);
}
