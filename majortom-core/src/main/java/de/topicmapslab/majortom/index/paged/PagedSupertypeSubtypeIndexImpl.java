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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.index.core.BaseCachedSupertypeSubtypeIndexImpl;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.paging.IPagedSupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Implementation of {@link IPagedSupertypeSubtypeIndex}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class PagedSupertypeSubtypeIndexImpl<T extends ITopicMapStore> extends BaseCachedSupertypeSubtypeIndexImpl<T> implements IPagedSupertypeSubtypeIndex {

	private ISupertypeSubtypeIndex parentIndex;

	/**
	 * constructor
	 * 
	 * @param store
	 *            the internal topic map store
	 * @param parentIndex
	 *            the parent index
	 */
	public PagedSupertypeSubtypeIndexImpl(T store, ISupertypeSubtypeIndex parentIndex) {
		super(store);
		this.parentIndex = parentIndex;
	}

	/**
	 * @return the parentIndex
	 */
	public ISupertypeSubtypeIndex getParentIndex() {
		return parentIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.DIRECT_SUBTYPE, type, false, offset, limit, null);
		if (topics == null) {
			topics = doGetDirectSubtypes(type, offset, limit);
			cache(Type.DIRECT_SUBTYPE, type, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.DIRECT_SUBTYPE, type, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetDirectSubtypes(type, offset, limit, comparator);
			cache(Type.DIRECT_SUBTYPE, type, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.DIRECT_SUPERTYPE, type, false, offset, limit, null);
		if (topics == null) {
			topics = doGetDirectSupertypes(type, offset, limit);
			cache(Type.DIRECT_SUPERTYPE, type, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.DIRECT_SUPERTYPE, type, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetDirectSupertypes(type, offset, limit, comparator);
			cache(Type.DIRECT_SUPERTYPE, type, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, null, false, offset, limit, null);
		if (topics == null) {
			topics = doGetSubtypes(offset, limit);
			cache(Type.SUBTYPE, null, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, null, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSubtypes(offset, limit, comparator);
			cache(Type.SUBTYPE, null, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, type, false, offset, limit, null);
		if (topics == null) {
			topics = doGetSubtypes(type, offset, limit);
			cache(Type.SUBTYPE, type, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, type, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSubtypes(type, offset, limit, comparator);
			cache(Type.SUBTYPE, type, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSubtypes(types, false, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSubtypes(types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, types, all, offset, limit, null);
		if (topics == null) {
			topics = doGetSubtypes(types, all, offset, limit);
			cache(Type.SUBTYPE, types, all, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUBTYPE, types, all, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSubtypes(types, all, offset, limit, comparator);
			cache(Type.SUBTYPE, types, all, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, null, false, offset, limit, null);
		if (topics == null) {
			topics = doGetSupertypes(offset, limit);
			cache(Type.SUPERTYPE, null, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, null, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSupertypes(offset, limit, comparator);
			cache(Type.SUPERTYPE, null, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, type, false, offset, limit, null);
		if (topics == null) {
			topics = doGetSupertypes(type, offset, limit);
			cache(Type.SUPERTYPE, type, false, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, type, false, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSupertypes(type, offset, limit, comparator);
			cache(Type.SUPERTYPE, type, false, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSupertypes(types, false, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getSupertypes(types, false, offset, limit, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, types, all, offset, limit, null);
		if (topics == null) {
			topics = doGetSupertypes(types, all, offset, limit);
			cache(Type.SUPERTYPE, types, all, offset, limit, null, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Topic> getSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> topics = read(Type.SUPERTYPE, types, all, offset, limit, comparator);
		if (topics == null) {
			topics = doGetSupertypes(types, all, offset, limit, comparator);
			cache(Type.SUPERTYPE, types, all, offset, limit, comparator, topics);
		}
		return (List<Topic>) topics;
	}

	/**
	 * {@inheritDoc}
	 */
	public void open() {
		if (!parentIndex.isOpen()) {
			parentIndex.open();
		}
		super.open();
	}

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes within the given range
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes());
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of a topic type contained by
	 * the topic map.
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes within the given range
	 */
	protected List<Topic> doGetSupertypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes());
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(type));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of the given topic type.If the
	 * type is <code>null</code> the method returns all topics which have no
	 * super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(type));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSupertypes(type));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct supertype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which have
	 * no super-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all supertypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSupertypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSupertypes(type));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(types, all));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a supertype of at least one given type or
	 * of every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an supertype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all supertypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSupertypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSupertypes(types, all));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes within the given range
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes());
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of a topic type contained by the
	 * topic map.
	 * 
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a list of all subtypes within the given range
	 */
	protected List<Topic> doGetSubtypes(int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes());
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(type));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of the given topic type. If the
	 * type is <code>null</code> the method returns all topics which has no
	 * sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(type));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSubtypes(type));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a direct subtype of the given topic type.
	 * If the type is <code>null</code> the method returns all topics which has
	 * no sub-types.
	 * 
	 * @param type
	 *            the topic type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * 
	 * @return a list of all subtypes of the given type within the given range
	 */
	protected List<Topic> doGetDirectSubtypes(Topic type, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getDirectSubtypes(type));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @return a list of all subtypes of at least one of the given type within
	 *         the given range
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(types, all));
		return HashUtil.secureSubList(cache, offset, limit);
	}

	/**
	 * Returns all topic types being a subtype of at least one given type or of
	 * every given topic type.
	 * 
	 * @param types
	 *            the topic types
	 * @param all
	 *            flag indicates if the found topic types should be an subtype
	 *            of every given type
	 * 
	 * @param offset
	 *            the index of the first item
	 * @param limit
	 *            the maximum count of returned values
	 * @param comparator
	 *            the comparator
	 * @return a collection of all subtypes of at least one of the given type
	 */
	protected List<Topic> doGetSubtypes(Collection<? extends Topic> types, boolean all, int offset, int limit, Comparator<Topic> comparator) {
		List<Topic> cache = HashUtil.getList(getParentIndex().getSubtypes(types, all));
		Collections.sort(cache, comparator);
		return HashUtil.secureSubList(cache, offset, limit);
	}

}
