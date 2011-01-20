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
package de.topicmapslab.majortom.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Utility class to support other hash implementations, like gnu.trove
 * 
 * @author Sven Krosse
 * 
 */
public class HashUtil {

	private static int CAPACITY = 16;
	private static float LOAD_FACTORY = .75F;

	/**
	 * the found class used as {@link Set} implementation
	 */
	private static Class<?> setClass = HashSet.class;
	/**
	 * the found class used as {@link Map} implementation
	 */
	private static Class<?> mapClass = HashMap.class;

	/**
	 * hidden constructor
	 */
	private HashUtil() {
		// HIDDEN
	}

	/**
	 * Method try to initialize a gnu.trove.THashSet if the library is located in the class path
	 * 
	 * @param <T>
	 *            the type of elements
	 * @param capacity
	 *            the initial capacity
	 * @return the created set
	 */
	public static <T> Set<T> getHashSet(int capacity) {
		return new HashSet<T>(capacity);
	}

	/**
	 * Method try to initialize a gnu.trove.THashSet if the library is located in the class path
	 * 
	 * @param <T>
	 *            the type of elements
	 * @return the created set
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getHashSet() {

		try {
			Constructor<?> constructor = getSetClass().getConstructor(int.class, float.class);
			return (Set<T>) constructor.newInstance(CAPACITY, LOAD_FACTORY);
		} catch (SecurityException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		try {
			return (Set<T>) getSetClass().newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return new HashSet<T>(CAPACITY, LOAD_FACTORY);
	}

	/**
	 * Method try to initialize a gnu.trove.THashSet if the library is located in the class path
	 * 
	 * @param initial
	 *            the initial set
	 * @param <T>
	 *            the type of elements
	 * @return the created set
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getHashSet(Collection<? extends T> initial) {
		if (initial == null) {
			return getHashSet();
		}
		try {
			Constructor<?> constructor = getSetClass().getConstructor(int.class, float.class);
			Set<T> set = (Set<T>) constructor.newInstance(CAPACITY, LOAD_FACTORY);
			set.addAll(initial);
			return set;
		} catch (SecurityException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		try {
			return (Set<T>) getSetClass().getConstructor(Collection.class).newInstance(initial);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		Set<T> set = new HashSet<T>(CAPACITY, LOAD_FACTORY);
		set.addAll(initial);
		return set;
	}

	/**
	 * Returns the set class to use. If the method is called at the first time, the set class will be located in the
	 * class path.
	 * 
	 * @return the setClass the set class
	 */
	private static Class<?> getSetClass() {
		if (setClass == null) {
			try {
				setClass = Class.forName("gnu.trove.THashSet");
			} catch (ClassNotFoundException e) {
				setClass = HashSet.class;
			} catch (IllegalArgumentException e) {
				setClass = HashSet.class;
			} catch (SecurityException e) {
				setClass = HashSet.class;
			}
		}
		return setClass;
	}

	/**
	 * Method try to initialize a gnu.trove.THashMap if the library is located in the class path
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @param capacity
	 *            the initial capacity
	 * @return the created map
	 */
	public static <K, V> Map<K, V> getHashMap(int capacity) {
		return new HashMap<K, V>(capacity, LOAD_FACTORY);
	}

	/**
	 * Method try to initialize a gnu.trove.THashMap if the library is located in the class path
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @return the created map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> getHashMap() {
		try {
			Constructor<?> constructor = getMapClass().getConstructor(int.class, float.class);
			Map<K, V> map = (Map<K, V>) constructor.newInstance(CAPACITY, LOAD_FACTORY);
			return map;
		} catch (SecurityException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		try {
			return (Map<K, V>) getMapClass().newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return new HashMap<K, V>(CAPACITY, LOAD_FACTORY);
	}

	/**
	 * Method try to initialize a gnu.trove.THashMap if the library is located in the class path
	 * 
	 * @param initial
	 *            the initial map
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @return the created map
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> getHashMap(Map<? extends K, ? extends V> initial) {
		if (initial == null) {
			return getHashMap();
		}
		try {
			Constructor<?> constructor = getMapClass().getConstructor(int.class, float.class);
			Map<K, V> map = (Map<K, V>) constructor.newInstance(CAPACITY, LOAD_FACTORY);
			map.putAll(initial);
			return map;
		} catch (SecurityException e1) {
		} catch (NoSuchMethodException e1) {
		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		try {
			return (Map<K, V>) getMapClass().getConstructor(Map.class).newInstance(initial);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		Map<K, V> map = new HashMap<K, V>(CAPACITY, LOAD_FACTORY);
		map.putAll(initial);
		return map;
	}

	/**
	 * Returns the map class to use. If the method is called at the first time, the set class will be located in the
	 * class path.
	 * 
	 * @return the setClass the set class
	 */
	private static Class<?> getMapClass() {
		if (mapClass == null) {
			try {
				mapClass = Class.forName("gnu.trove.THashMap");
			} catch (ClassNotFoundException e) {
				mapClass = HashMap.class;
			} catch (IllegalArgumentException e) {
				mapClass = HashMap.class;
			} catch (SecurityException e) {
				mapClass = HashMap.class;
			}
		}
		return mapClass;
	}

	/**
	 * Method try to initialize a {@link WeakHashMap}.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @return the created map
	 */
	public static <K, V> Map<K, V> getWeakHashMap() {
		return new WeakHashMap<K, V>();
	}

	/**
	 * Method try to initialize a {@link WeakHashMap}.
	 * 
	 * @param initial
	 *            the initial map
	 * @param <K>
	 *            the key type
	 * @param <V>
	 *            the value type
	 * @return the created map
	 */
	public static <K, V> Map<K, V> getWeakHashMap(Map<? extends K, ? extends V> initial) {
		if (initial == null) {
			return getWeakHashMap();
		}
		return new WeakHashMap<K, V>(initial);
	}

	/**
	 * Method try to initialize a {@link List}.
	 * 
	 * @param <T>
	 *            the type of arguments
	 * @return the created list
	 */
	public static <T> List<T> getList() {
		return new ArrayList<T>();
	}

	/**
	 * Method try to initialize a List.
	 * 
	 * @param initial
	 *            the initial collection
	 * @param <T>
	 *            the type of arguments
	 * @return the created list
	 */
	public static <T> List<T> getList(Collection<? extends T> initial) {
		if (initial == null) {
			return getList();
		}
		return new ArrayList<T>(initial);
	}

	/**
	 * Clears the indexes in context to the given list, to avoid indexes out of range.
	 * 
	 * @param list
	 *            the list
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 * @return an two-
	 */
	public static final <X> List<X> secureSubList(List<X> list, int offset, int limit) {
		int from = offset;
		if (from < 0) {
			from = 0;
		} else if (from >= list.size()) {
			if (!list.isEmpty()) {
				from = list.size() - 1;
			} else {
				from = 0;
			}
		}
		int to = offset + limit;
		if (to < 0) {
			to = 0;
		} else if (to > list.size()) {
			to = list.size();
		}
		return Collections.unmodifiableList(list.subList(from, to));
	}

	/**
	 * External access method to overwrite internal set class.
	 * 
	 * @param clazz
	 *            the class
	 */
	public synchronized static <T extends Set<?>> void overwriteSetImplementationClass(Class<T> clazz) {
		setClass = clazz;
	}

	/**
	 * External access method to overwrite internal map class.
	 * 
	 * @param clazz
	 *            the class
	 */
	public synchronized static <T extends Map<?, ?>> void overwriteMapImplementationClass(Class<T> clazz) {
		mapClass = clazz;
	}

}
