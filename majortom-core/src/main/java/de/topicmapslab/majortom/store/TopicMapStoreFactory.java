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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.ITopicMapStoreFactory;
import de.topicmapslab.majortom.osgi.MajorToMActivator;

/**
 * Factory implementation creating a topic map store object from given runtime
 * properties.
 * 
 * @author Sven Krosse
 * 
 */
public class TopicMapStoreFactory {

	/**
	 * Create a new topic map store object using the properties given by the
	 * first argument.
	 * 
	 * @param factory
	 *            the factory containing the set properties
	 * @param topicMapSystem
	 *            the topic map system
	 * @param topicMapBaseLocator
	 *            the base locator of the topic map
	 *            {@link TopicMap#getLocator()}
	 * @return the generated topic map store
	 * @throws TopicMapStoreException
	 *             thrown if the topic map store cannot create
	 */
	public static ITopicMapStore createTopicMapStore(final TopicMapSystemFactory factory, final ITopicMapSystem topicMapSystem, Locator topicMapBaseLocator) throws TopicMapStoreException {
		Object className = factory.getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS);
		if (className == null) {
			ITopicMapStoreFactory storeFac = loadWithJavaServices();
			factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, storeFac.getClassName());
			ITopicMapStore store = storeFac.newTopicMapStore(topicMapSystem);
			store.setTopicMapSystem(topicMapSystem);
			store.initialize(topicMapBaseLocator);
			return store;
		}

		try {
			ITopicMapStoreFactory storeFac = getStoreFactories().get(className.toString().trim());

			ITopicMapStore store = storeFac.newTopicMapStore(topicMapSystem);
			store.initialize(topicMapBaseLocator);
			return store;
		} catch (TopicMapStoreException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		}
	}

	private final static List<String> defaultTopicMapStores = new LinkedList<String>();
	private static Map<String, ITopicMapStoreFactory> storeFactories;

	static {
		defaultTopicMapStores.add("de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore");
	}

	private static ITopicMapStoreFactory loadWithJavaServices() {
		initStoreFactories();
		switch (getStoreFactories().size()) {
		case 0: {
			throw new TopicMapStoreException("Implementation class of topic map store not set.");
		}
		case 1: {
			return getStoreFactories().values().iterator().next();
		}
		default: {
			for (String defaultTopicMapStore : defaultTopicMapStores) {
				if (getStoreFactories().containsKey(defaultTopicMapStore)) {
					return getStoreFactories().get(defaultTopicMapStore);
				}
			}
			return getStoreFactories().values().iterator().next();
		}
		}
	}

	/**
	 * Returns a map of store factories initialized by OSGi or Java services.
	 * 
	 * @return a map of store factories initialized by OSGi or Java services
	 */
	public static Map<String, ITopicMapStoreFactory> getStoreFactories() {
		if (storeFactories == null) {
			initStoreFactories();
		}
		return storeFactories;
	}

	/**
	 * loads the store list either from the bundle activator or from the
	 * services
	 */
	private static void initStoreFactories() {
		Iterable<ITopicMapStoreFactory> list = null;
		// try to load the extension points via OSGi
		try {
			// check if we are in an OSGi environment if not an exception is
			// thrown
			if (MajorToMActivator.getDefault() != null) {
				list = MajorToMActivator.getDefault().getTopicMapStoreFactories();
				storeFactories = new HashMap<String, ITopicMapStoreFactory>();

				for (ITopicMapStoreFactory fac : list) {
					storeFactories.put(fac.getClassName(), fac);
				}
			}
		} catch (Throwable e) {
			// we do nothing, cause we are not in an OSGi environment
		}

		if (list == null) {
			ServiceLoader<ITopicMapStore> loader = ServiceLoader.load(ITopicMapStore.class, TopicMapStoreFactory.class.getClassLoader());
			storeFactories = new HashMap<String, ITopicMapStoreFactory>();

			for (ITopicMapStore store : loader) {
				storeFactories.put(store.getClass().getName(), new TMStoreFactory(store));
			}
		}

	}

	private static class TMStoreFactory implements ITopicMapStoreFactory {
		private final ITopicMapStore store;

		public TMStoreFactory(ITopicMapStore store) {
			this.store = store;
		}

		@Override
		public ITopicMapStore newTopicMapStore(ITopicMapSystem tmSystem) {
			ITopicMapStore newStore;
			try {
				newStore = store.getClass().getConstructor().newInstance();
				((TopicMapStoreImpl) newStore).setTopicMapSystem(tmSystem);
				return newStore;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String getClassName() {
			return store.getClass().getName();
		}
	}
}
