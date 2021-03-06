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
package de.topicmapslab.majortom.database.readonly;

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyTopic;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcReadOnlyTopic extends ReadOnlyTopic {

	private static final long serialVersionUID = 1L;
	private final IConnectionProvider provider;

	/**
	 * constructor
	 * 
	 * @param provider
	 *            the connection provider
	 * @param clone
	 *            the non-read-only clone
	 */
	public JdbcReadOnlyTopic(IConnectionProvider provider, ITopic clone) {
		super(clone);
		this.provider = provider;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Association> getAssociationsPlayed() {
		Set<IAssociation> set = doReadHistoryValue(TopicMapStoreParameterType.ASSOCIATION);
		Set<Association> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getSupertypes() {
		Set<ITopic> set = doReadHistoryValue(TopicMapStoreParameterType.SUPERTYPE);
		Set<Topic> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Name> getNames() {
		Set<IName> set = doReadHistoryValue(TopicMapStoreParameterType.NAME);
		Set<Name> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Occurrence> getOccurrences() {
		Set<IOccurrence> set = doReadHistoryValue(TopicMapStoreParameterType.OCCURRENCE);
		Set<Occurrence> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Reifiable getReified() {
		return doReadHistoryValue(TopicMapStoreParameterType.REIFICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRolesPlayed() {
		Set<Role> roles = HashUtil.getHashSet();
		for (Association a : getAssociationsPlayed()) {
			for (Role r : a.getRoles()) {
				if (r.getPlayer().equals(this)) {
					roles.add(r);
				}
			}
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getSubjectIdentifiers() {
		Set<ILocator> set = doReadHistoryValue(TopicMapStoreParameterType.SUBJECT_IDENTIFIER);
		Set<Locator> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getSubjectLocators() {
		Set<ILocator> set = doReadHistoryValue(TopicMapStoreParameterType.SUBJECT_LOCATOR);
		Set<Locator> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getTypes() {
		Set<ITopic> set = doReadHistoryValue(TopicMapStoreParameterType.TYPE);
		Set<Topic> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		Set<ILocator> set = doReadHistoryValue(TopicMapStoreParameterType.ITEM_IDENTIFIER);
		Set<Locator> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * Internal method to read the history values
	 * 
	 * @param <T>
	 *            the type of returned values
	 * @param type
	 *            the argument specifies the value to fetch
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	private <T extends Object> T doReadHistoryValue(TopicMapStoreParameterType type) {
		Object value = ReadOnlyUtils.doReadHistoryValue(provider, this, type);
		return (T)value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBestLabel() {
		return (String) doReadHistoryValue(TopicMapStoreParameterType.BEST_LABEL);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getBestIdentifier(boolean withPrefix) {		
		return (String) doReadHistoryValue(TopicMapStoreParameterType.BEST_IDENTIFIER);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBestLabel(Topic theme) {
		throw new UnsupportedOperationException("Read only topic does not support best label with theme");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getBestLabel(Topic theme, boolean strict) {
		throw new UnsupportedOperationException("Read only topic does not support best label with theme");
	}

}
