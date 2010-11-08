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
package de.topicmapslab.majortom.store;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.core.ConstructFactoryImpl;
import de.topicmapslab.majortom.core.TopicMapSystemImpl;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IConstructFactory;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.ITopicMapStoreMetaData;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of a read only {@link ITopicMapStore}.
 * 
 * 
 * @author Sven Krosse
 * 
 */
public abstract class TopicMapStoreImpl implements ITopicMapStore {

	public static boolean OUTPUT = false;

	private boolean connected = false;
	private Set<ITopicMapListener> listeners = null;
	private ITopicMapSystem topicMapSystem;
	private ITopicMap topicMap;
	private IConstructFactory factory;
	private ThreadPoolExecutor threadPool;
	private ITopicMapStoreMetaData metaData;
	private String topicMapBaseLocatorReference;
	/**
	 * the base locator of the topic map
	 */
	private ILocator baseLocator;

	/**
	 * feature {@link FeatureStrings#SUPPORT_HISTORY}
	 */
	private boolean featureRevisionManagement;
	/**
	 * feature {@link FeatureStrings#TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION}
	 */
	private boolean featureTypeInstanceAssociation;
	/**
	 * feature {@link FeatureStrings#TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION}
	 */
	private boolean featureSupertypeSubtypeAssociation;
	/**
	 * feature {@link FeatureStrings#READ_ONLY_SYSTEM}
	 */
	private boolean featureReadOnlyStore;
	/**
	 * feature {@link FeatureStrings#SUPPORT_TRANSACTION}
	 */
	private boolean featureSupportTransaction;
	/**
	 * feature {@link FeatureStrings#AUTOMATIC_MERGING}
	 */
	private boolean featureAutomaticMerging;
	/**
	 * feature {@link FeatureStrings#DELETION_CONSTRAINTS_REIFICATION}
	 */
	private boolean featureDeletionConstraintReification;
	/**
	 * feature {@link FeatureStrings#MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME}
	 */
	private boolean featureMergingByName;

	/**
	 * current state of revision management
	 */
	private boolean revisionManagementEnabled;

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the parent system
	 */
	public TopicMapStoreImpl(final ITopicMapSystem topicMapSystem) {
		setTopicMapSystem(topicMapSystem);
	}

	/**
	 * constructor
	 */
	public TopicMapStoreImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTopicMapListener(ITopicMapListener listener) {
		if (listeners == null) {
			listeners = HashUtil.getHashSet();
		}
		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTopicMapListener(ITopicMapListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * @return the listeners
	 */
	public Set<ITopicMapListener> getListeners() {
		if (listeners == null) {
			return Collections.emptySet();
		}
		return listeners;
	}

	/**
	 * Method called by the topic map store to notify all topic map listeners registered for the given topic map
	 * 
	 * @param event
	 *            the event type
	 * @param notifier
	 *            the construct changed
	 * @param newValue
	 *            the new value
	 * @param oldValue
	 *            the old value
	 */
	public void notifyListeners(TopicMapEventType event, IConstruct notifier, Object newValue, Object oldValue) {
		String id = null;
		for (ITopicMapListener listener : getListeners()) {
			if (id == null) {
				id = generateId();
			}
			listener.topicMapChanged(id, event, notifier, newValue, oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTopicMapSystem(ITopicMapSystem topicMapSystem) {
		if (isConnected()) {
			throw new TopicMapStoreException("Store already connected, topic map system cannot changed!");
		}
		this.topicMapSystem = topicMapSystem;
		try {
			this.featureAutomaticMerging = topicMapSystem.getFeature(FeatureStrings.AUTOMATIC_MERGING);
			this.featureDeletionConstraintReification = topicMapSystem.getFeature(FeatureStrings.DELETION_CONSTRAINTS_REIFICATION);
			this.featureMergingByName = topicMapSystem.getFeature(FeatureStrings.MERGING_SUPPORT_FEATURE_BY_TOPIC_NAME);
			this.featureReadOnlyStore = topicMapSystem.getFeature(FeatureStrings.READ_ONLY_SYSTEM);
			this.featureRevisionManagement = topicMapSystem.getFeature(FeatureStrings.SUPPORT_HISTORY);
			this.featureSupertypeSubtypeAssociation = topicMapSystem.getFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION);
			this.featureSupportTransaction = topicMapSystem.getFeature(FeatureStrings.SUPPORT_TRANSACTION);
			this.featureTypeInstanceAssociation = topicMapSystem.getFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION);
		} catch (FeatureNotRecognizedException e) {
			throw new TopicMapStoreException("Feature is missing", e);
		}
		this.revisionManagementEnabled = featureRevisionManagement;
	}

	/**
	 * Returns the parent topic map system
	 * 
	 * @return the topicMapSystem
	 */
	public ITopicMapSystem getTopicMapSystem() {
		return topicMapSystem;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return this.featureReadOnlyStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementSupported() {
		return this.featureRevisionManagement;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementEnabled() {
		return isRevisionManagementSupported() && this.revisionManagementEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableRevisionManagement(boolean enabled) throws TopicMapStoreException {
		if (!isRevisionManagementSupported() && enabled) {
			throw new TopicMapStoreException("Revision management not supported by the current store and cannot be enabled!");
		}
		this.revisionManagementEnabled = enabled;
	}

	/**
	 * Method checks if the feature 'http://tmapi.org/features/automerge/' for automatic merging is set.
	 * 
	 * @return <code>true</code> if automatic merging is enabled, <code>false</code> otherwise.
	 */
	public boolean doAutomaticMerging() {
		return this.featureAutomaticMerging;
	}

	/**
	 * Method checks if the feature 'http://tmapi.org/features/merge/byTopicName/' for merging topics by name is set.
	 * 
	 * @return <code>true</code> if merging by name is enabled, <code>false</code> otherwise.
	 */
	public boolean doMergingByTopicName() {
		return this.featureMergingByName;
	}

	/**
	 * Method checks if the feature for supporting type-instance-associations as a type relation is set.
	 * 
	 * @return <code>true</code> if the associations should recognized, <code>false</code> otherwise.
	 */
	public boolean recognizingTypeInstanceAssociation() {
		return this.featureTypeInstanceAssociation;
	}

	/**
	 * Method checks if the feature for supporting supertype-subtype-associations as a supertype relation is set.
	 * 
	 * @return <code>true</code> if the associations should recognized, <code>false</code> otherwise.
	 */
	public boolean recognizingSupertypeSubtypeAssociation() {
		return this.featureSupertypeSubtypeAssociation;
	}

	/**
	 * Method returns checks if the deletion constraint contains the constraint, that topics used as reifier cannot be removed until the reification was
	 * destroyed.
	 * 
	 * @see TopicMapStoreProperty#DELETION_CONSTRAINTS_REIFICATION
	 * 
	 * @return <code>true</code> if a topic cannot be removed if it is used as reifier , <code>false</code> otherwise.
	 */
	public boolean isReificationDeletionRestricted() {
		return this.featureDeletionConstraintReification;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransactable() {
		return this.featureSupportTransaction;
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		if (this.getTopicMapSystem() == null) {
			throw new TopicMapStoreException("Store is not bind to any topic map system instance!");
		}
		if (this.topicMap == null) {
			throw new TopicMapStoreException("Store is not bind to any topic map instance!");
		}
		if (isConnected()) {
			return;
		}
		connected = true;

		Object maximum = getTopicMapSystem().getProperty(TopicMapStoreProperty.THREADPOOL_MAXIMUM);
		int max = Runtime.getRuntime().availableProcessors() + 1;
		if (maximum != null) {
			try {
				max = Integer.parseInt(maximum.toString());
			} catch (NumberFormatException e) {
				// NOTHING TO DO
			}
		}
		this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(max);
		this.factory = createConstructFactory();
		this.metaData = createMetaDataInstance();
	}

	/**
	 * {@inheritDoc}
	 */
	public void initialize(Locator topicMapBaseLocator) throws TopicMapStoreException {
		this.baseLocator = (ILocator) topicMapBaseLocator;
		this.topicMapBaseLocatorReference = topicMapBaseLocator.getReference();
		if (!topicMapBaseLocatorReference.endsWith("#") && !topicMapBaseLocatorReference.endsWith("/")) {
			this.topicMapBaseLocatorReference += "/";
		}
	}

	/**
	 * Creates a new construct factory instance
	 * 
	 * @return the construct factory
	 */
	protected IConstructFactory createConstructFactory() {
		return new ConstructFactoryImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		if (!isConnected()) {
			return;
		}
		((TopicMapSystemImpl) getTopicMapSystem()).removeTopicMap(getTopicMapBaseLocator());
		connected = false;
		this.factory = null;
		this.metaData = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Returns the topic map
	 * 
	 * @return the topicMap
	 */
	public ITopicMap getTopicMap() {
		return topicMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTopicMap(ITopicMap topicMap) throws TopicMapStoreException {
		if (this.topicMap != null) {
			throw new TopicMapStoreException("Store is already bind to an other topic map instance!");
		}
		this.topicMap = topicMap;
	}

	/**
	 * Adding the given task to the internal thread pool of the topic map store.
	 * 
	 * @param task
	 *            the task to add
	 */
	protected void addTaskToThreadPool(Runnable task) {
		getThreadPool().execute(task);
	}

	/**
	 * Returns the internal thread pool instance
	 * 
	 * @return the threadPool
	 */
	protected final ThreadPoolExecutor getThreadPool() {
		return threadPool;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstructFactory getConstructFactory() {
		return factory;
	}

	/**
	 * Internal called method to create a new meta data instance
	 * 
	 * @return the generated meta data instance
	 */
	protected ITopicMapStoreMetaData createMetaDataInstance() {
		return new TopicMapStoreMetaDataImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMapStoreMetaData getMetaData() {
		return metaData;
	}

	/**
	 * Returns the base locator of the topic map
	 * 
	 * @return the base locator
	 */
	public ILocator getTopicMapBaseLocator() {
		return baseLocator;
	}

	/**
	 * The topic map base locator reference
	 * 
	 * @return the topicMapBaseLocator
	 */
	public String getTopicMapBaseLocatorReference() {
		return topicMapBaseLocatorReference;
	}

	/**
	 * generates a new ID for a new construct
	 * 
	 * @return the id
	 */
	protected String generateId() {
		long t = System.currentTimeMillis();
		String s = Double.toString(Math.round(Math.random() * Double.MAX_VALUE)); // UUID.randomUUID().toString();
		if ( OUTPUT)
		System.out.println("Generate an id in " + (System.currentTimeMillis() - t) + " ms");
		return s;
	}
}
