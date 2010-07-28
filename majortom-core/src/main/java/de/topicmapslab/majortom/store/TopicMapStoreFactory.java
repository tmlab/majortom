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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.tmapi.core.Locator;
import org.tmapi.core.TopicMapSystemFactory;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;

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
	 * @param factory the factory containing the set properties
	 * @param topicMapSystem the topic map system
	 * @param topicMapBaseLocator TODO
	 * @return the generated topic map store
	 * @throws TopicMapStoreException thrown if the topic map store cannot
	 *             create
	 */
	@SuppressWarnings("unchecked")
	public static ITopicMapStore createTopicMapStore(final TopicMapSystemFactory factory, final ITopicMapSystem topicMapSystem, Locator topicMapBaseLocator) throws TopicMapStoreException {
		Object className = factory.getProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS);
		if (className == null) {
			ITopicMapStore store = loadWithJavaServices();
			factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, store.getClass().getName());
			store.setTopicMapSystem(topicMapSystem);
			store.initialize(topicMapBaseLocator);
			return store;
		}

		try {
			Class<? extends ITopicMapStore> clazz = (Class<? extends ITopicMapStore>) Class.forName(className.toString());
			Constructor<? extends ITopicMapStore> constructor = clazz.getConstructor(ITopicMapSystem.class);
			ITopicMapStore store = constructor.newInstance(topicMapSystem);
			store.initialize(topicMapBaseLocator);
			return store;
		} catch (ClassNotFoundException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (InstantiationException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (IllegalAccessException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (SecurityException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (NoSuchMethodException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (IllegalArgumentException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		} catch (InvocationTargetException e) {
			throw new TopicMapStoreException("Cannot load topic map store instance '" + className + "'", e);
		}
	}

	private final static List<String> defaultTopicMapStores = new LinkedList<String>();
	static{
		defaultTopicMapStores.add("de.topicmapslab.majortom.inMemory.store.InMemoryTopicMapStore");
	}
	
	private static ITopicMapStore loadWithJavaServices() {
		ServiceLoader<ITopicMapStore> loader = ServiceLoader.load(ITopicMapStore.class);
		loader.reload();
		Map<String, ITopicMapStore> stores = new HashMap<String, ITopicMapStore>();
		int count = 0;
		for (ITopicMapStore store : loader) {
			stores.put(store.getClass().getName(), store);
			count++;
		}
		switch (count) {
		case 0: {
			throw new TopicMapStoreException("Implementation class of topic map store not set.");
		}
		case 1: {
			return stores.values().iterator().next();
		}
		default: {
			for ( String defaultTopicMapStore : defaultTopicMapStores ){
				if ( stores.containsKey(defaultTopicMapStore)){
					return stores.get(defaultTopicMapStore);
				}
			}
			return stores.values().iterator().next();
		}
		}
	}
}
