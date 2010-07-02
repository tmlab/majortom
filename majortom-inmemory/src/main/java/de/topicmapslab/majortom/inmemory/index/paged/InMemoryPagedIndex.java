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

import java.util.Collections;
import java.util.List;

import org.tmapi.index.Index;

import de.topicmapslab.majortom.inmemory.index.InMemoryIndex;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.event.ITopicMapListener;

/**
 * special in-memory index supporting paging
 * 
 * @author Sven Krosse
 * 
 * @param <T>
 *            the type of the dependent parent index
 */
public abstract class InMemoryPagedIndex<T extends Index> extends InMemoryIndex implements ITopicMapListener {

	/**
	 * the reference of the parent index
	 */
	private final T parentIndex;

	/**
	 * 
	 * @param store
	 * @param parentIndex
	 */
	public InMemoryPagedIndex(InMemoryTopicMapStore store, T parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * Returning the reference of the parent index.
	 * 
	 * @return the parent index
	 */
	protected final T getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		super.open();
		if (!parentIndex.isOpen()) {
			parentIndex.open();
		}
		getStore().addTopicMapListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		getStore().removeTopicMapListener(this);
		super.close();
	}

	/**
	 * Clears the indexes in context to the given list, to avoid indexes out of
	 * range.
	 * 
	 * @param list
	 *            the list
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return an two-
	 */
	protected final <E> List<E> secureSubList(List<E> list, int offset, int limit) {
		int from = offset;
		if (from < 0) {
			from = 0;
		} else if (from >= list.size()) {
			from = list.size() - 1;
		}
		int to = offset + limit;
		if (to < 0) {
			to = 0;
		} else if (to > list.size()) {
			to = list.size();
		}
		return Collections.unmodifiableList(list.subList(from, to));
	}
}
