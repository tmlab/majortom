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
package de.topicmapslab.majortom.inmemory.store.revision.core;

import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Read only implementation of {@link IScopable}
 * 
 * @author Sven Krosse
 * 
 */
public class ReadOnlyScopable extends ReadOnlyReifiable implements IScopable {

	private final Set<String> themeIds = HashUtil.getHashSet();

	/*
	 * cached values
	 */
	private Set<ITopic> cachedThemes;

	/**
	 * constructor
	 * 
	 * @param clone the construct to clone
	 */
	public ReadOnlyScopable(IScopable clone) {
		super((IReifiable) clone);

		for (Topic theme : clone.getScope()) {
			themeIds.add(theme.getId());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScopeObject() {
		Set<ITopic> themes = HashUtil.getHashSet();
		if (cachedThemes != null) {
			themes.addAll(cachedThemes);
		}
		if (!themeIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(themeIds);
			for (String id : ids) {
				ITopic theme = (ITopic) getTopicMap().getConstructById(id);
				if (theme instanceof ReadOnlyTopic) {
					if (cachedThemes == null) {
						cachedThemes = HashUtil.getHashSet();
					}
					themeIds.remove(id);
					cachedThemes.add(theme);
				}
				themes.add(theme);
			}
		}
		return new ScopeImpl(themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTheme(Topic arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getScope() {
		Set<Topic> themes = HashUtil.getHashSet();
		if (cachedThemes != null) {
			themes.addAll(cachedThemes);
		}
		if (!themeIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(themeIds);
			for (String id : ids) {
				ITopic theme = (ITopic) getTopicMap().getConstructById(id);
				if (theme instanceof ReadOnlyTopic) {
					if (cachedThemes == null) {
						cachedThemes = HashUtil.getHashSet();
					}
					themeIds.remove(id);
					cachedThemes.add(theme);
				}
				themes.add(theme);
			}
		}
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTheme(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

}
