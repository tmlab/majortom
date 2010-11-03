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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
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
import de.topicmapslab.majortom.util.FeatureStrings;
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
	private final Map<String, Locator> locators = HashUtil.getWeakHashMap();

	/**
	 * the topic map system properties
	 */
	private Properties properties;

	/**
	 * the topic map factory features
	 */
	private Map<String, Object> features = null;

	/**
	 * the parent factory
	 */
	private TopicMapSystemFactoryImpl factory;

	/**
	 * constructor for JAVA services
	 */
	public TopicMapSystemImpl() {
		// VOID
	}

	/**
	 * constructor
	 * 
	 * @param factory
	 *            the factory
	 */
	public TopicMapSystemImpl(TopicMapSystemFactoryImpl factory) {
		setFactory(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		for (TopicMap topicMap : HashUtil.getHashSet(topicMaps.values())) {
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
			throw new TopicMapExistsException("A topic map with the identifier '" + locator.getReference()
					+ "' already exists.");
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
			throw new TopicMapExistsException("A topic map with the identifier '" + locator.getReference()
					+ "' already exists.");
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
		return features.containsKey(arg0) ? Boolean.parseBoolean(features.get(arg0).toString()) : false;
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
		return properties.getProperty(arg0);
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
	 * {@inheritDoc}
	 */
	public ITopicMap removeTopicMap(Locator locator) {
		if (locator == null) {
			throw new IllegalArgumentException("Locator cannot be null.");
		}
		
//		ITopicMap topicMap = (ITopicMap) getTopicMap(locator);
//		if (topicMap == null) {
//			throw new IllegalArgumentException("No topic map contained for the given locator.");
//		}
		return this.topicMaps.remove(locator);
//		return topicMap;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void setFeature(String key, boolean value) throws FeatureNotSupportedException,
			FeatureNotRecognizedException {
		if (!FeatureStrings.FEATURES.contains(key)) {
			throw new FeatureNotRecognizedException("Unknown feature string '" + key + "'!");
		}
		if (!factory.hasFeature(key)) {
			throw new FeatureNotSupportedException("Feature not supported by the engine!");
		}
		features.put(key, value);
	}

	/**
	 * Internal method to add a topic map to internal storage
	 * 
	 * @param locator
	 *            the base locator
	 * @param topicMap
	 *            the topic map
	 */
	protected final void addTopicMap(Locator locator, ITopicMap topicMap) throws TopicMapExistsException {
		if (topicMaps.containsKey(locator)) {
			throw new TopicMapExistsException("A topic map with the identifier '" + locator.getReference()
					+ "' already exists.");
		}
		topicMaps.put(locator, topicMap);
	}

	/**
	 * Internal method to check if the locator is bound to a topic map instance
	 * 
	 * @param locator
	 *            the locator
	 * @return <code>true</code> if the locator is known as base locator for a topic map instance, <code>false</code>
	 *         otherwise.
	 */
	protected final boolean containsTopicMap(Locator locator) {
		return topicMaps.containsKey(locator);
	}

	/**
	 * Returns the topic map factory instance
	 */
	protected TopicMapSystemFactoryImpl getFactory() {
		return factory;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void setFactory(TopicMapSystemFactory factory) {
		setFactory((TopicMapSystemFactoryImpl) factory);
	}

	/**
	 * Internal method to set the factory instance
	 * 
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(TopicMapSystemFactoryImpl factory) {
		this.factory = factory;
		properties = new Properties();
		properties.putAll(((TopicMapSystemFactoryImpl) factory).getProperties());
		features = HashUtil.getHashMap(factory.getFeatures());
	}

}
