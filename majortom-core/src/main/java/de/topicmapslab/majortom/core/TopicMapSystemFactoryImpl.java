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
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Map.Entry;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.store.TopicMapStoreFactory;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TopicMapSystemFactoryImpl extends TopicMapSystemFactory {
	/**
	 * the property file name
	 */
	private static final String propertyFile = "engine.properties";

	private Map<String, Object> features = null;
	private final Properties properties;

	private static final Set<String> SUPPORTED_FEATURES = HashUtil.getHashSet();
	static {
		SUPPORTED_FEATURES.add(FeatureStrings.READ_ONLY_SYSTEM);
		SUPPORTED_FEATURES.add(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME);
		SUPPORTED_FEATURES.add(FeatureStrings.AUTOMATIC_MERGING);
		SUPPORTED_FEATURES.add(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION);
		SUPPORTED_FEATURES.add(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION);
		SUPPORTED_FEATURES.add(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION);
		SUPPORTED_FEATURES.add(FeatureStrings.SUPPORT_HISTORY);
		SUPPORTED_FEATURES.add(FeatureStrings.SUPPORT_TRANSACTION);
		SUPPORTED_FEATURES.add(FeatureStrings.CONCURRENT_COLLECTIONS);
	}

	/**
	 * constructor
	 */
	public TopicMapSystemFactoryImpl() {
		features = HashUtil.getHashMap();
		features.put(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, true);
		features.put(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, true);
		features.put(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME, false);
		features.put(FeatureStrings.AUTOMATIC_MERGING, true);
		features.put(FeatureStrings.SUPPORT_HISTORY, false);
		features.put(FeatureStrings.SUPPORT_TRANSACTION, false);
		features.put(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION, true);
		features.put(FeatureStrings.READ_ONLY_SYSTEM, false);
		features.put(FeatureStrings.CONCURRENT_COLLECTIONS, false);
		properties = new Properties();
		loadPropertiesFromFile();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getFeature(String arg0) throws FeatureNotRecognizedException {
		return hasFeature(arg0) ? Boolean.parseBoolean(features.get(arg0).toString()) : false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getProperty(String arg0) {
		return properties.get(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasFeature(String arg0) {
		return features == null ? false : features.containsKey(arg0);
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMapSystem newTopicMapSystem() throws TMAPIException {
		final Object classname = getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS);
		ServiceLoader<ITopicMapSystem> loader = ServiceLoader.load(ITopicMapSystem.class, getClass().getClassLoader());
		for (ITopicMapSystem system : loader) {
			if ( classname == null || classname.equals(system.getHandledClass().getName())){
				system.setFactory(this);			
				return system;
			}
		}
		return new TopicMapSystemImpl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFeature(String arg0, boolean arg1) throws FeatureNotSupportedException,
			FeatureNotRecognizedException {
		if (!FeatureStrings.FEATURES.contains(arg0)) {
			throw new FeatureNotRecognizedException("Unknown feature string '" + arg0 + "'!");
		}
		if (!SUPPORTED_FEATURES.contains(arg0)) {
			throw new FeatureNotSupportedException("Feature not supported by the engine!");
		}
		features.put(arg0, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	/**
	 * Internal method to get all properties
	 * 
	 * @return the properties
	 */
	Properties getProperties() {
		return properties;
	}

	/**
	 * Internal method to get all features
	 * 
	 * @return the features
	 */
	Map<String, Object> getFeatures() {
		return features;
	}
	

	/**
	 * Hidden method to load optional properties of the current topic map system.
	 * 
	 * @throws TopicMapStoreException
	 *             thrown if the properties can not load
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
					this.properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
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
				this.properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
			}
		} catch (IOException e) {
			// NOTHING TO DO
		} catch (NullPointerException e) {
			// NOTHING TO DO
		}
	}

}
