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

import java.util.Collections;
import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of {@link IScopable}.
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ScopeableImpl extends ReifiableImpl implements IScopable {

	/**
	 * constructor
	 * 
	 * @param identity the {@link ITopicMapStoreIdentity}
	 * @param topicMap the topic map
	 * @param parent the parent construct
	 */
	protected ScopeableImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap, IConstruct parent) {
		super(identity, topicMap, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScopeObject() {
		return (IScope) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.SCOPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTheme(Topic theme) throws ModelConstraintException {
		if (theme == null) {
			throw new ModelConstraintException(this, "Theme cannot be null.");
		}
		if (!theme.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.SCOPE, theme);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getScope() {
		Set<Topic> themes = HashUtil.getHashSet();
		IScope scope = getScopeObject();
		if (scope != null) {
			themes.addAll(scope.getThemes());
		}
		return Collections.unmodifiableSet(themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTheme(Topic theme) {
		if (theme == null) {
			throw new ModelConstraintException(this, "Theme cannot be null.");
		}
		getTopicMap().getStore().doRemove(this, TopicMapStoreParameterType.SCOPE, theme);
	}
}
