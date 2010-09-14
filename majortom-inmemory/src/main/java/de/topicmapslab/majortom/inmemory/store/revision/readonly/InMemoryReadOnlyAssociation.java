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
package de.topicmapslab.majortom.inmemory.store.revision.readonly;

import java.util.HashSet;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociation;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryReadOnlyAssociation extends ReadOnlyAssociation {

	private final String reifierId;
	private final String typeId;
	private final Set<String> roleIds = HashUtil.getHashSet();
	private final Set<String> themeIds = HashUtil.getHashSet();

	/*
	 * cached values
	 */
	private Topic cachedType;
	private Set<Role> cachedRoles;
	private Topic cachedReifier;
	private Set<Locator> itemIdentifiers = HashUtil.getHashSet();
	private Set<ITopic> cachedThemes;

	/**
	 * @param clone
	 */
	public InMemoryReadOnlyAssociation(IAssociation clone) {
		super(clone);

		for (Locator itemIdentifier : clone.getItemIdentifiers()) {
			itemIdentifiers.add(new LocatorImpl(itemIdentifier.getReference()));
		}
		typeId = clone.getType().getId();
		for (Role role : clone.getRoles()) {
			roleIds.add(role.getId());
		}

		if (clone.getReifier() != null) {
			reifierId = clone.getReifier().getId();
		} else {
			reifierId = null;
		}
		for (Topic theme : clone.getScope()) {
			themeIds.add(theme.getId());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		return itemIdentifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getParent() {
		return getTopicMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Role> getRoles() {
		Set<Role> roles = new HashSet<Role>();
		if (cachedRoles != null) {
			roles.addAll(cachedRoles);
		}
		if (!roleIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(roleIds);
			for (String id : ids) {
				Role r = (Role) getTopicMap().getConstructById(id);
				if (r instanceof InMemoryReadOnlyAssociationRole) {
					if (cachedRoles == null) {
						cachedRoles = HashUtil.getHashSet();
					}
					roleIds.remove(id);
					cachedRoles.add(r);
				}
				roles.add(r);
			}
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		if (cachedType != null) {
			return cachedType;
		}
		Topic type = (Topic) getTopicMap().getConstructById(typeId);
		if (type instanceof InMemoryReadOnlyTopic) {
			cachedType = type;
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		if (reifierId == null) {
			return null;
		}
		if (cachedReifier != null) {
			return cachedReifier;
		}
		Topic reifier = (Topic) getTopicMap().getStore().doRead(getTopicMap(), TopicMapStoreParameterType.BY_ID, reifierId);
		if (reifier instanceof InMemoryReadOnlyTopic) {
			cachedReifier = reifier;
		}
		return reifier;
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
				if (theme instanceof InMemoryReadOnlyTopic) {
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

}
