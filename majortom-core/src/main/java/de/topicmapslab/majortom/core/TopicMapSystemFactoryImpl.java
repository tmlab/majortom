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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.FeatureNotSupportedException;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TopicMapSystemFactoryImpl extends TopicMapSystemFactory {

	private Map<String, Object> features = null;
	private final Properties properties = new Properties(); 

	private static final Set<String> SUPPORTED_FEATURES = HashUtil.getHashSet();
	static {
		SUPPORTED_FEATURES.add(FeatureStrings.READ_ONLY_SYSTEM);
		SUPPORTED_FEATURES.add(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME);
		SUPPORTED_FEATURES.add(FeatureStrings.AUTOMATIC_MERGING);
		SUPPORTED_FEATURES.add(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION);
		SUPPORTED_FEATURES.add(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION);
		SUPPORTED_FEATURES.add(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION);
		SUPPORTED_FEATURES.add(FeatureStrings.SUPPORT_HISTORY);
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
		features.put(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION, true);
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
		return properties == null ? null : properties.get(arg0);
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
		return new TopicMapSystemImpl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFeature(String arg0, boolean arg1) throws FeatureNotSupportedException, FeatureNotRecognizedException {
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
	public void setProperty(String arg0, Object arg1) {		
		properties.put(arg0, arg1);
	}
	
	/**
	 * @return the properties
	 */
	Properties getProperties() {
		return properties;
	}

}
