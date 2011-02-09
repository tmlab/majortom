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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.comparator.NameByValueComparator;
import de.topicmapslab.majortom.comparator.ScopeComparator;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;

/**
 * @author Sven Krosse
 */
public class BestLabelUtils {
	/**
	 * 
	 */
	private static final String ID_PREFIX = "id:";
	/**
	 * 
	 */
	protected static final String SUBJECTIDENTIFIER_PREFIX = "si:";
	/**
	 * 
	 */
	protected static final String SUBJECTLOCATOR_PREFIX = "sl:";
	/**
	 * 
	 */
	protected static final String ITEMIDENTIFIER_PREFIX = "ii:";

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @param topic
	 *            the topic
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 * @since 1.1.2
	 */
	public static String doReadBestLabel(ITopic topic) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<Name> names = HashUtil.getHashSet(topic.getNames());
		if (!names.isEmpty()) {
			return readBestName(topic, names);
		}
		return doReadBestIdentifier(topic, false);
	}

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @param topic
	 *            the topic
	 * @param theme
	 *            the theme
	 * @param strict
	 *            if there is no name with the given theme and strict is <code>true</code>, then <code>null</code> will
	 *            be returned.
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 * @since 1.1.2
	 */
	public static String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws TopicMapStoreException {
		/*
		 * get all names of the topic
		 */
		Set<Name> names = HashUtil.getHashSet(topic.getNames());
		if (!names.isEmpty()) {
			return readBestName(topic, theme, names, strict);
		}
		/*
		 * is strict mode
		 */
		if (strict) {
			return null;
		}
		return doReadBestIdentifier(topic, false);
	}

	/**
	 * Method filter the given names by the default name type
	 * 
	 * @param topic
	 *            the topic as parent of the names
	 * @param names
	 *            the names
	 * @return the filtered names
	 */
	private static Set<Name> filterByDefaultNameType(ITopic topic, Set<Name> names) {
		ITopicMap topicMap = topic.getTopicMap();
		Topic defaultNameType = topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(Namespaces.TMDM.TOPIC_NAME));
		/*
		 * check if default name type exists
		 */
		if (defaultNameType != null) {
			Set<Name> tmp = HashUtil.getHashSet(names);
			tmp.retainAll(topic.getNames(defaultNameType));
			/*
			 * more than one default name
			 */
			if (tmp.size() > 0) {
				return tmp;
			}
		}
		return names;
	}

	/**
	 * Internal best label method only check name attributes.
	 * 
	 * @param topic
	 *            the topic
	 * @param theme
	 *            the theme
	 * @param set
	 *            the non-empty set of names
	 * @param strict
	 *            if there is no name with the given theme and strict is <code>true</code>, then <code>null</code> will
	 *            be returned.
	 * @return the best name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private static String readBestName(ITopic topic, ITopic theme, Set<Name> names, boolean strict) throws TopicMapStoreException {
		IScopedIndex index = topic.getTopicMap().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		List<IScope> scopes = HashUtil.getList(index.getScopes(theme));
		/*
		 * sort scopes by number of themes
		 */
		Collections.sort(scopes, ScopeComparator.getInstance(true));
		boolean atLeastOneName = false;
		int numberOfThemes = -1;
		Set<Name> tmp = HashUtil.getHashSet();
		for (IScope s : scopes) {
			Collection<Name> scopedNames = topic.getNames(s);
			if (scopedNames.isEmpty()) {
				continue;
			}
			/*
			 * set number of themes
			 */
			if (numberOfThemes == -1) {
				numberOfThemes = s.getThemes().size();
			}
			/*
			 * current scope has more themes than expected
			 */
			if (numberOfThemes < s.getThemes().size()) {
				break;
			}
			/*
			 * get names of the scope and topic
			 */
			tmp.addAll(scopedNames);
			atLeastOneName = true;
		}
		/*
		 * is strict mode but no scoped name
		 */
		if (strict && !atLeastOneName) {
			return null;
		}
		if (!tmp.isEmpty()) {
			names.retainAll(tmp);
		}
		/*
		 * only one name of the current scope
		 */
		if (names.size() == 1) {
			return names.iterator().next().getValue();
		}
		/*
		 * check default name type
		 */
		names = filterByDefaultNameType(topic, names);
		/*
		 * sort by value
		 */
		List<Name> list = HashUtil.getList(names);
		Collections.sort(list, NameByValueComparator.getInstance(true));
		return list.get(0).getValue();
	}

	/**
	 * Internal best label method only check name attributes.
	 * 
	 * @param topic
	 *            the topic
	 * @param set
	 *            the non-empty set of names
	 * @return the best name
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	private static String readBestName(ITopic topic, Set<Name> names) throws TopicMapStoreException {
		IScopedIndex index = topic.getTopicMap().getIndex(IScopedIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		/*
		 * check if default name type exists
		 */
		names = filterByDefaultNameType(topic, names);
		if (names.size() == 1) {
			return names.iterator().next().getValue();
		}
		/*
		 * filter by scoping themes
		 */
		List<IScope> scopes = HashUtil.getList(index.getNameScopes());
		if (!scopes.isEmpty()) {
			/*
			 * sort scopes by number of themes
			 */
			Collections.sort(scopes, ScopeComparator.getInstance(true));
			Set<Name> tmp = HashUtil.getHashSet();
			int numberOfThemes = -1;
			for (IScope s : scopes) {
				Collection<Name> scopedNames = topic.getNames(s);
				if (scopedNames.isEmpty()) {
					continue;
				}
				/*
				 * set number of themes
				 */
				if (numberOfThemes == -1) {
					numberOfThemes = s.getThemes().size();
				}
				/*
				 * current scope has more themes than expected
				 */
				if (numberOfThemes < s.getThemes().size()) {
					break;
				}
				/*
				 * get names of the scope and topic
				 */
				tmp.addAll(scopedNames);
			}
			if (!tmp.isEmpty()) {
				names.retainAll(tmp);
			}
			/*
			 * only one name of the current scope
			 */
			if (names.size() == 1) {
				return names.iterator().next().getValue();
			}

		}
		/*
		 * sort by value
		 */
		List<Name> list = HashUtil.getList(names);
		Collections.sort(list, NameByValueComparator.getInstance(true));
		return list.get(0).getValue();
	}

	/**
	 * Returns the best and stable identifier of the topic. The best identifier will be extracted by following rules.
	 * 
	 * <p>
	 * 1. Identifiers are weighted by its types in the following order subject-identifier, subject-locator and
	 * item-identifier.
	 * </p>
	 * <p>
	 * 2. If there are more than one identifier of the same type, the shortest will be returned.
	 * </p>
	 * <p>
	 * 3. If there are more than one identifier with the same length, the lexicographically smallest will be returned.
	 * </p>
	 * 
	 * @param topic
	 *            the topic
	 * @param withPrefix
	 *            flag indicates if the returned identifier will be prefixed with its type. Subject-identifier(
	 *            <code>si:</code>), subject-locator(<code>sl:</code>) or item-identifier(<code>ii:</code>).
	 * @return the best identifier or the id if the topic has no identifiers
	 * @since 1.2.0
	 */
	public static String doReadBestIdentifier(ITopic topic, boolean withPrefix) {
		final String prefix;
		Set<Locator> locators;
		/*
		 * try subject-identifier
		 */
		locators = topic.getSubjectIdentifiers();
		if (locators.isEmpty()) {
			/*
			 * try subject-locator
			 */
			locators = topic.getSubjectLocators();
			if (locators.isEmpty()) {
				/*
				 * try item-identifier
				 */
				locators = topic.getItemIdentifiers();
				if (locators.isEmpty()) {
					String bestIdentifier = withPrefix ? ID_PREFIX : "";
					bestIdentifier += topic.getId();
					return bestIdentifier;
				}
				prefix = ITEMIDENTIFIER_PREFIX;
			} else {
				prefix = SUBJECTLOCATOR_PREFIX;
			}
		} else {
			prefix = SUBJECTIDENTIFIER_PREFIX;
		}

		if (locators.size() == 1) {
			String bestIdentifier = withPrefix ? prefix : "";
			bestIdentifier += locators.iterator().next().getReference();
			return bestIdentifier;
		}

		List<Locator> sorted = HashUtil.getList(locators);
		Collections.sort(sorted, new Comparator<Locator>() {
			/**
			 * {@inheritDoc}
			 */
			public int compare(Locator o1, Locator o2) {
				return o1.getReference().length() - o2.getReference().length();
			}
		});

		/*
		 * extract all references with the shortest length
		 */
		String first = sorted.get(0).getReference();
		List<String> references = HashUtil.getList();
		references.add(first);
		for (int i = 1; i < sorted.size(); i++) {
			String s = sorted.get(i).getReference();
			if (s.length() == first.length()) {
				references.add(s);
			} else {
				break;
			}
		}
		/*
		 * is only one
		 */
		if (references.size() == 1) {
			String bestIdentifier = withPrefix ? prefix : "";
			bestIdentifier += references.get(0);
			return bestIdentifier;
		}
		/*
		 * sort lexicographically
		 */
		Collections.sort(references);
		String bestIdentifier = withPrefix ? prefix : "";
		bestIdentifier += references.get(0);
		return bestIdentifier;
	}

}
