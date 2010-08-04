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

import java.util.Collections;
import java.util.List;

import org.tmapi.index.Index;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

/**
 * special index implementation supporting paging
 * 
 * @author Sven Krosse
 * 
 * @param <T>
 *            the topic map store class
 * @param <E>
 *            the type of the dependent parent index
 */
public abstract class PagedIndexImpl<T extends ITopicMapStore, E extends Index> extends IndexImpl<T> implements ITopicMapListener {

	/**
	 * the reference of the parent index
	 */
	private final E parentIndex;

	/**
	 * 
	 * @param store
	 * @param parentIndex
	 */
	public PagedIndexImpl(T store, E parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * Returning the reference of the parent index.
	 * 
	 * @return the parent index
	 */
	protected final E getParentIndex() {
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
	protected final <X> List<X> secureSubList(List<X> list, int offset, int limit) {
		int from = offset;
		if (from < 0) {
			from = 0;
		} else if (from >= list.size()) {
			if (!list.isEmpty()) {
				from = list.size() - 1;
			} else {
				from = 0;
			}
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
