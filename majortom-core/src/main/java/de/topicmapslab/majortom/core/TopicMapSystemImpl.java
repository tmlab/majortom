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

package de.topicmapslab.majortom.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.store.TopicMapStoreFactory;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of {@link TopicMapSystem}
 * 
 * @author Sven Krosse
 * 
 */
public class TopicMapSystemImpl implements ITopicMapSystem {

	/**
	 * a map of all contained topic maps
	 */
	private Map<Locator, ITopicMap> topicMaps = HashUtil.getHashMap();
	/**
	 * a map of all locators created by the topic map system
	 */
	private final Map<String, Locator> locators = HashUtil.getHashMap();
	/**
	 * the property file name
	 */
	private static final String propertyFile = "engine.properties";

	/**
	 * the parent factory
	 */
	private final TopicMapSystemFactory factory;

	/**
	 * constructor
	 * 
	 * @param factory the factory
	 */
	public TopicMapSystemImpl(TopicMapSystemFactory factory) {
		this.factory = factory;
		loadPropertiesFromFile();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		for (TopicMap topicMap : topicMaps.values()) {
			topicMap.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator createLocator(String arg0) throws MalformedIRIException {
		if (locators.containsKey(arg0)) {
			return locators.get(arg0);
		}
		Locator l = new LocatorImpl(arg0);
		locators.put(arg0, l);
		return l;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap createTopicMap(Locator locator) throws TopicMapExistsException {
		if (topicMaps.containsKey(locator)) {
			throw new TopicMapExistsException("A topic map with the identifier '" + locator.getReference() + "' already exists.");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		TopicMapImpl topicMap = new TopicMapImpl(this, locator);
		ITopicMapStore store = TopicMapStoreFactory.createTopicMapStore(factory, this, locator);
		topicMap.setStore(store);
		topicMaps.put(locator, topicMap);
		return topicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap createTopicMap(String arg0) throws TopicMapExistsException {
		if (arg0 == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return createTopicMap(createLocator(arg0));
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap createTopicMap(Locator locator, ITopicMapStore store) throws TopicMapExistsException {
		if (topicMaps.containsKey(locator)) {
			throw new TopicMapExistsException("A topic map with the identifier '" + locator.getReference() + "' already exists.");
		}
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		if (store == null) {
			throw new IllegalArgumentException("Store cannot be null");
		}
		TopicMapImpl topicMap = new TopicMapImpl(this, locator);
		topicMap.setStore(store);
		topicMaps.put(locator, topicMap);
		return topicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap createTopicMap(String ref, ITopicMapStore store) throws TopicMapExistsException {
		if (ref == null) {
			throw new IllegalArgumentException("Locator cannot be null");
		}
		return createTopicMap(createLocator(ref), store);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getFeature(String arg0) throws FeatureNotRecognizedException {
		return factory.getFeature(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getLocators() {
		return Collections.unmodifiableSet(topicMaps.keySet());
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getProperty(String arg0) {
		return factory.getProperty(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap getTopicMap(String arg0) {
		if (locators.containsKey(arg0)) {
			return getTopicMap(locators.get(arg0));
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMap getTopicMap(Locator arg0) {
		return topicMaps.get(arg0);
	}

	/**
	 * Hidden method to load optional properties of the current topic map
	 * system.
	 * 
	 * @throws TopicMapStoreException thrown if the properties can not load
	 */
	private void loadPropertiesFromFile() throws TopicMapStoreException {
		/*
		 * load from file
		 */
		File file = new File(propertyFile);
		if (file.exists()) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
				for (Entry<Object, Object> entry : properties.entrySet()) {
					factory.setProperty(entry.getKey().toString(), entry.getValue().toString());
				}
			} catch (FileNotFoundException e) {
				// NOTHING TO DO
			} catch (IOException e) {
				// NOTHING TO DO
			}
		}
		/*
		 * load from resources if exists
		 */
		Properties properties = new Properties();
		try {
			properties.load(TopicMapStoreFactory.class.getResourceAsStream(propertyFile));
			for (Entry<Object, Object> entry : properties.entrySet()) {
				factory.setProperty(entry.getKey().toString(), entry.getValue().toString());
			}
		} catch (IOException e) {
			// NOTHING TO DO
		} catch (NullPointerException e) {
			// NOTHING TO DO
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap removeTopicMap(Locator locator) {
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null.");
		}
		ITopicMap topicMap = (ITopicMap) getTopicMap(locator);
		if (topicMap == null) {
			throw new IllegalArgumentException("No topic map contained for the given locator.");
		}
		this.topicMaps.remove(locator);
		return topicMap;
	}

}
