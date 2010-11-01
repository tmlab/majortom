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
package de.topicmapslab.majortom.database.core;

import java.sql.SQLException;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;

import de.topicmapslab.majortom.core.TopicMapImpl;
import de.topicmapslab.majortom.core.TopicMapSystemFactoryImpl;
import de.topicmapslab.majortom.core.TopicMapSystemImpl;
import de.topicmapslab.majortom.database.jdbc.core.ConnectionProviderFactory;
import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.store.TopicMapStoreFactory;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcTopicMapSystem extends TopicMapSystemImpl {

	/**
	 * constructor for JAVA services
	 */
	public JdbcTopicMapSystem() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param factory
	 *            the factory
	 */
	public JdbcTopicMapSystem(TopicMapSystemFactoryImpl factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getLocators() {
		return loadLocatorsFromDatabase();
	}

	/**
	 * Internal method to load all locators from database
	 */
	private final Set<Locator> loadLocatorsFromDatabase() {
		Set<Locator> storedTopicMapLocators = HashUtil.getHashSet();
		/*
		 * try to load connection parameters
		 */
		Object host = getProperty(JdbcTopicMapStoreProperty.DATABASE_HOST);
		Object database = getProperty(JdbcTopicMapStoreProperty.DATABASE_NAME);
		Object user = getProperty(JdbcTopicMapStoreProperty.DATABASE_USER);
		Object password = getProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD);
		Object dialect = getProperty(JdbcTopicMapStoreProperty.SQL_DIALECT);

		/*
		 * check required parameters
		 */
		if (database == null || host == null || user == null || dialect == null) {
			throw new TMAPIRuntimeException("Missing connection properties!");
		}

		try {
			/*
			 * load locators
			 */
			IConnectionProvider provider = ConnectionProviderFactory.getFactory().newConnectionProvider(
					dialect.toString());
			provider.openConnections(host.toString(), database.toString(), user.toString(), password == null ? ""
					: password.toString());
			for (ILocator loc : provider.getProcessor().getLocators()) {
				storedTopicMapLocators.add(loc);
			}
		} catch (SQLException e) {
			throw new TMAPIRuntimeException("Connection or communiation with database failed!", e);
		}
		return storedTopicMapLocators;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap getTopicMap(Locator locator) {
		/*
		 * instance already known for given base locator
		 */
		if (containsTopicMap(locator)) {
			return super.getTopicMap(locator);
		}
		/*
		 * get all locators of the database
		 */
		if (getLocators().contains(locator)) {
			TopicMapImpl topicMap = new TopicMapImpl(this, locator);
			ITopicMapStore store = TopicMapStoreFactory.createTopicMapStore(getFactory(), this, locator);
			topicMap.setStore(store);
			try {
				addTopicMap(locator, topicMap);
			} catch (TopicMapExistsException e) {
				throw new TMAPIRuntimeException("Internal error.!", e);
			}
			return topicMap;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap getTopicMap(String reference) {
		return getTopicMap(createLocator(reference));
	}
}
