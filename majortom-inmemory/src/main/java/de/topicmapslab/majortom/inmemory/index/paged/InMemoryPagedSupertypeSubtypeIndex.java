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

import de.topicmapslab.majortom.index.paged.PagedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryPagedSupertypeSubtypeIndex extends PagedSupertypeSubtypeIndexImpl<InMemoryTopicMapStore> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the internal topic map store
	 * @param parentIndex
	 *            the parent index
	 */
	public InMemoryPagedSupertypeSubtypeIndex(InMemoryTopicMapStore store, ISupertypeSubtypeIndex parentIndex) {
		super(store, parentIndex);
	}

}
