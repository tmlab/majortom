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
package de.topicmapslab.majortom.index.paged;

import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedTransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * Implementation of the in-memory {@link IPagedTransitiveTypeInstanceIndex}
 * supporting paging
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedTransitiveTypeInstanceIndexImpl<T extends ITopicMapStore> extends PagedTypeInstanceIndexImpl<T> {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the topic map store
	 * @param parentIndex
	 *            the parent {@link ITransitiveTypeInstanceIndex}
	 */
	public PagedTransitiveTypeInstanceIndexImpl(T store, ITransitiveTypeInstanceIndex parentIndex) {
		super(store, parentIndex);
	}

}
