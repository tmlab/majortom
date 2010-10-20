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
import de.topicmapslab.majortom.index.nonpaged.CachedIdentityIndexImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcIdentityIndex extends
		CachedIdentityIndexImpl<JdbcTopicMapStore> {

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
		return existsIdentifier(getTopicMapStore().getTopicMap().createLocator(
				reference));
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
		return existsItemIdentifier(locator)
				|| existsSubjectIdentifier(locator)
				|| existsSubjectLocator(locator);
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
		return existsItemIdentifier(getTopicMapStore().getTopicMap().createLocator(
				reference));
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
		return existsSubjectIdentifier(getTopicMapStore().getTopicMap().createLocator(
				reference));
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
		return existsSubjectLocator(getTopicMapStore().getTopicMap().createLocator(
				reference));
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
	public Construct getConstructByItemIdentifier(String reference)
			throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getConstructByItemIdentifier(getTopicMapStore().getTopicMap()
				.createLocator(reference));
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
		return getTopicMapStore().getTopicMap().getConstructByItemIdentifier(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> doGetConstructsByIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression  cannot be null");
		}
		try {
			Set<Construct> constructs = HashUtil.getHashSet();
			constructs.addAll(getTopicMapStore().getProcessor()
					.getConstructsByIdentitifer(getTopicMapStore().getTopicMap(),
							regExp, -1, -1));
			return constructs;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> doGetConstructsByIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression  cannot be null");
		}
		return doGetConstructsByIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> doGetConstructsByItemIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression  cannot be null");
		}

		try {
			Set<Construct> constructs = HashUtil.getHashSet();
			constructs.addAll(getTopicMapStore().getProcessor()
					.getConstructsByItemIdentitifer(getTopicMapStore().getTopicMap(),
							regExp, -1, -1));
			return constructs;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Construct> doGetConstructsByItemIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression  cannot be null");
		}
		return doGetConstructsByItemIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> doGetItemIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getTopicMapStore().getProcessor().getItemIdentifiers(
					getTopicMapStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> doGetSubjectIdentifiers() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getTopicMapStore().getProcessor().getSubjectIdentifiers(
					getTopicMapStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Locator> doGetSubjectLocators() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			Set<Locator> locators = HashUtil.getHashSet();
			locators.addAll(getTopicMapStore().getProcessor().getSubjectLocators(
					getTopicMapStore().getTopicMap(), -1, -1));
			return locators;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectIdentifier(String reference)
			throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getTopicBySubjectIdentifier(getTopicMapStore().getTopicMap()
				.createLocator(reference));
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
		return getTopicMapStore().getTopicMap().getTopicBySubjectIdentifier(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getTopicBySubjectLocator(String reference)
			throws MalformedIRIException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (reference == null) {
			throw new IllegalArgumentException("Reference cannot be null");
		}
		return getTopicBySubjectLocator(getTopicMapStore().getTopicMap().createLocator(
				reference));
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
		return getTopicMapStore().getTopicMap().getTopicBySubjectLocator(locator);
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopicsBySubjectIdentifier(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor()
					.getTopicsBySubjectIdentitifer(getTopicMapStore().getTopicMap(),
							regExp, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopicsBySubjectIdentifier(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		return doGetTopicsBySubjectIdentifier(regExp.pattern());
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopicsBySubjectLocator(String regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		try {
			Set<Topic> topics = HashUtil.getHashSet();
			topics.addAll(getTopicMapStore().getProcessor().getTopicsBySubjectLocator(
					getTopicMapStore().getTopicMap(), regExp, -1, -1));
			return topics;
		} catch (SQLException e) {
			throw new TopicMapStoreException("Internal database error!", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<Topic> doGetTopicsBySubjectLocator(Pattern regExp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (regExp == null) {
			throw new IllegalArgumentException(
					"Regular expression cannot be null");
		}
		return doGetTopicsBySubjectLocator(regExp.pattern());
	}

}
