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
/**
 * 
 */
package de.topicmapslab.majortom.database.jdbc.index;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 *
 */
public class JdbcTypeInstanceIndex extends JdbcIndex implements ITypeInstanceIndex {

	/**
	 * @param store
	 */
	public JdbcTypeInstanceIndex(JdbcTopicMapStore store) {
		super(store);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Association> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationsByType((ITopic)type));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getAssociations(type));
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociations(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Association> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getAssociations(type));
		}
		return col;	
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getCharacteristicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Topic> col = HashUtil.getHashSet(getNameTypes());
		col.addAll(getOccurrenceTypes());
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> col = HashUtil.getHashSet();
		for ( Name n : getNames(type)){
			col.add((IName)n);
		}
		for ( Occurrence o : getOccurrences(type)){
			col.add((IOccurrence)o);
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> col = HashUtil.getHashSet();
		for ( Name n : getNames(types)){
			col.add((IName)n);
		}
		for ( Occurrence o : getOccurrences(types)){
			col.add((IOccurrence)o);
		}
		return col;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<ICharacteristics> getCharacteristics(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<ICharacteristics> col = HashUtil.getHashSet();
		for ( Name n : getNames(types)){
			col.add((IName)n);
		}
		for ( Occurrence o : getOccurrences(types)){
			col.add((IOccurrence)o);
		}
		return col;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Name> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNamesByType((ITopic)type));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getNames(type));
		}
		return col;	
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Name> getNames(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Name> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getNames(type));
		}
		return col;	
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Occurrence> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrencesByType((ITopic)type));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getOccurrences(type));
		}
		return col;	
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Occurrence> getOccurrences(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Occurrence> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getOccurrences(type));
		}
		return col;	
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Role> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getRolesByType((ITopic)type));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Role> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getRoles(type));
		}
		return col;	
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Role> getRoles(Collection<? extends Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		Collection<Role> col = HashUtil.getHashSet();
		for ( Topic type : types){
			col.addAll(getRoles(type));
		}
		return col;	
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic type) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByType(getStore().getTopicMap(),(ITopic)type));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic[] types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByTypes(Arrays.asList(types), all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Topic... types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByTypes(Arrays.asList(types), false));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByTypes(types, false));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopics(Collection<Topic> types, boolean all) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicsByTypes(types, all));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getAssociationTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getAssociationTypes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getNameTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getNameTypes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getOccurrenceTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getOccurrenceTypes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getRoleTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getRoleTypes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicTypes() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {			
			Collection<Topic> col = HashUtil.getHashSet();
			col.addAll(getStore().getProcessor().getTopicTypes(getStore().getTopicMap()));
			return col;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Cannot close connection to database!", e);
		}
	}

}
