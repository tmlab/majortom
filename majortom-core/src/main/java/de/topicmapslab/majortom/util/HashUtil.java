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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
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

	/**
	 * the found class used as {@link Set} implementation
	 */
	private static Class<?> setClass = null;
	/**
	 * the found class used as {@link Map} implementation
	 */
	private static Class<?> mapClass = null;

	/**
	 * hidden constructor
	 */
	private HashUtil() {
		// HIDDEN
	}

	/**
	 * Method try to initialize a gnu.trove.THashSet if the library is located
	 * in the class path
	 * 
	 * @param <T>
	 *            the type of elements
	 * @return the created set
	 */
	@SuppressWarnings("unchecked")
	public static <T> Set<T> getHashSet() {
		try {
			return (Set<T>) getSetClass().newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return new HashSet<T>();
	}

	/**
	 * Method try to initialize a gnu.trove.THashSet if the library is located
	 * in the class path
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
			return (Set<T>) getSetClass().getConstructor(Collection.class).newInstance(initial);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return new HashSet<T>(initial);
	}

	/**
	 * Returns the set class to use. If the method is called at the first time,
	 * the set class will be located in the class path.
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
	 * Method try to initialize a gnu.trove.THashMap if the library is located
	 * in the class path
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
			return (Map<K, V>) getMapClass().newInstance();
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		return new HashMap<K, V>();
	}

	/**
	 * Method try to initialize a gnu.trove.THashMap if the library is located
	 * in the class path
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
			return (Map<K, V>) getMapClass().getConstructor(Map.class).newInstance(initial);
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return new HashMap<K, V>(initial);
	}

	/**
	 * Returns the map class to use. If the method is called at the first time,
	 * the set class will be located in the class path.
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

}
