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
package de.topicmapslab.majortom.index.nonpaged;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class CachedTransitiveTypeInstanceIndex<T extends ITopicMapStore> extends CachedTypeInstanceIndexImpl<T> implements ITransitiveTypeInstanceIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the parent i
	 */
	public CachedTransitiveTypeInstanceIndex(T store) {
		super(store);
	}

	/**
	 * Internal utility method to return the supertype-subtype index
	 * 
	 * @return the index
	 */
	private ISupertypeSubtypeIndex getSupertypeSubtypeIndex() {
		ISupertypeSubtypeIndex index = getTopicMapStore().getIndex(ISupertypeSubtypeIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index;
	}
	
	/**
	 * Internal utility method to return the type-instance index
	 * 
	 * @return the index
	 */
	private ITypeInstanceIndex getTypeInstanceIndex() {
		ITypeInstanceIndex index = getTopicMapStore().getIndex(ITypeInstanceIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getAssociations(st));
			}
			set.addAll(getTypeInstanceIndex().getAssociations(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getAssociations(st));
			}
			set.addAll(getTypeInstanceIndex().getAssociations(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> doGetAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Association> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getAssociations(st));
		}
		set.addAll(getTypeInstanceIndex().getAssociations(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getCharacteristics(st));
		}
		set.addAll(getTypeInstanceIndex().getCharacteristics(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getCharacteristics(st));
			}
			set.addAll(getTypeInstanceIndex().getCharacteristics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> doGetCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<ICharacteristics> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getCharacteristics(st));
			}
			set.addAll(getTypeInstanceIndex().getCharacteristics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> doGetRoles(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getRoles(st));
			}
			set.addAll(getTypeInstanceIndex().getRoles(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> doGetRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getRoles(st));
		}
		set.addAll(getTypeInstanceIndex().getRoles(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> doGetRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Role> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getRoles(st));
			}
			set.addAll(getTypeInstanceIndex().getRoles(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getNames(st));
		}
		set.addAll(getTypeInstanceIndex().getNames(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getNames(st));
			}
			set.addAll(getTypeInstanceIndex().getNames(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> doGetNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Name> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getNames(st));
			}
			set.addAll(getTypeInstanceIndex().getNames(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getOccurrences(st));
		}
		set.addAll(getTypeInstanceIndex().getOccurrences(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getOccurrences(st));
			}
			set.addAll(getTypeInstanceIndex().getOccurrences(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> doGetOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Occurrence> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getOccurrences(st));
			}
			set.addAll(getTypeInstanceIndex().getOccurrences(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getTopics(st));
			}
			set.addAll(getTypeInstanceIndex().getTopics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
			if ( !st.isEmpty()){
				set.addAll(getTypeInstanceIndex().getTopics(st));
			}
			set.addAll(getTypeInstanceIndex().getTopics(type));
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getTopics(type));
			} else {
				set.retainAll(getTopics(type));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (type == null) {
			return getTypeInstanceIndex().getTopics(type);
		}
		Set<Topic> set = HashUtil.getHashSet();
		Collection<Topic> st = getSupertypeSubtypeIndex().getSubtypes((ITopic) type);
		if ( !st.isEmpty()){
			set.addAll(getTypeInstanceIndex().getTopics(st));
		}
		set.addAll(getTypeInstanceIndex().getTopics(type));
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopics(Topic[] types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (types == null) {
			throw new IllegalArgumentException("Types cannot be null!");
		}
		Set<Topic> set = HashUtil.getHashSet();
		for (Topic type : types) {
			if (set.isEmpty() || !all) {
				set.addAll(getTopics(type));
			} else {
				set.retainAll(getTopics(type));
			}
		}
		return Collections.unmodifiableCollection(set);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetTopicTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getTopicTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetAssociationTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getAssociationTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetRoleTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getRoleTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetCharacteristicTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getCharacteristicTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetNameTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getNameTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

	/**
	 * {@inheritDoc}
	 */
	protected Collection<Topic> doGetOccurrenceTypes() {
		Set<Topic> types = HashUtil.getHashSet();
		Collection<Topic> ty = getTypeInstanceIndex().getOccurrenceTypes();
		types.addAll(ty);
		types.addAll(getSupertypeSubtypeIndex().getSubtypes(ty));
		return types; 
	}

}
