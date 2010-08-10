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
package de.topicmapslab.majortom.inmemory.index.paged;

import de.topicmapslab.majortom.index.paged.PagedTypeInstanceIndexImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTypeInstanceIndex;

/**
 * Implementation of the in-memory {@link IPagedTypeInstanceIndex} supporting
 * paging
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedTypeInstanceIndex extends PagedTypeInstanceIndexImpl<InMemoryTopicMapStore> {

	/**
	 * @param store
	 * @param parentIndex
	 */
	public InMemoryPagedTypeInstanceIndex(InMemoryTopicMapStore store, ITypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

}
