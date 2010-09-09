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
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcIdentityIndex extends IndexImpl<JdbcTopicMapStore> implements IIdentityIndex {

	/**
	 * constructor
	 * 
	 * @param store
	 *            the parent store
	 */
	public JdbcIdentityIndex(JdbcTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return existsIdentifier(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return existsItemIdentifier(locator) || existsSubjectIdentifier(locator) || existsSubjectLocator(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return existsItemIdentifier(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsItemIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getConstructByItemIdentifier(locator) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return existsSubjectIdentifier(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getTopicBySubjectIdentifier(locator) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(String reference) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return existsSubjectLocator(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean existsSubjectLocator(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getTopicBySubjectLocator(locator) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getConstructByItemIdentifier(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Construct getConstructByItemIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getStore().getTopicMap().getConstructByItemIdentifier(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression  cannot be null");
		}
		try {
			Set<Construct> constructs = HashUtil.getHashSet();
			constructs.addAll(getStore().getProcessor().getConstructsByIdentitifer(getStore().getTopicMap(), regExp, -1, -1));
			return constructs;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression  cannot be null");
		}
		return getConstructsByIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression  cannot be null");
		}

		try {
			Set<Construct> constructs = HashUtil.getHashSet();
			constructs.addAll(getStore().getProcessor().getConstructsByItemIdentitifer(getStore().getTopicMap(), regExp, -1, -1));
			return constructs;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> getConstructsByItemIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression  cannot be null");
		}
		return getConstructsByItemIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getItemIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getStore().getProcessor().getItemIdentifiers(getStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getStore().getProcessor().getSubjectIdentifiers(getStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> getSubjectLocators() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getStore().getProcessor().getSubjectLocators(getStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getTopicBySubjectIdentifier(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getStore().getTopicMap().getTopicBySubjectIdentifier(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(String reference) throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getTopicBySubjectLocator(getStore().getTopicMap().createLocator(reference));
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(Locator locator) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return getStore().getTopicMap().getTopicBySubjectLocator(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getTopicsBySubjectIdentitifer(getStore().getTopicMap(), regExp, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		return getTopicsBySubjectIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getStore().getProcessor().getTopicsBySubjectLocator(getStore().getTopicMap(), regExp, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> getTopicsBySubjectLocator(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException("Regular expression cannot be null");
		}
		return getTopicsBySubjectLocator(regExp.pattern());
	}

}
