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

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyName;
import de.topicmapslab.majortom.revision.core.ReadOnlyVariant;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class InMemoryReadOnlyVariant extends ReadOnlyVariant {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String reifierId;
	private String parentId;
	private final Set<String> themeIds = HashUtil.getHashSet();
	private Object value;
	private final Locator datatype;

	private Topic cachedReifier;
	private Set<Locator> itemIdentifiers = HashUtil.getHashSet();
	private IName cachedParent;
	private Set<ITopic> cachedThemes;

	/**
	 * @param clone
	 */
	public InMemoryReadOnlyVariant(IVariant clone) {
		super(clone);

		this.parentId = clone.getParent().getId();

		for (Locator itemIdentifier : clone.getItemIdentifiers()) {
			itemIdentifiers.add(new LocatorImpl(itemIdentifier.getReference()));
		}
		if (clone.getReifier() != null) {
			reifierId = clone.getReifier().getId();
		} else {
			reifierId = null;
		}
		for (Topic theme : clone.getScope()) {
			themeIds.add(theme.getId());
		}
		try {
			this.value = DatatypeAwareUtils.toValue(clone);
		} catch (Exception e) {
			this.value = clone.getValue();
		}
		this.datatype = new LocatorImpl(clone.getDatatype().getReference());
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator getDatatype() {
		return datatype;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object objectValue() {
		return value;
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
	public IName getParent() {
		if (cachedParent != null) {
			return cachedParent;
		}
		IName parent = (IName) getTopicMap().getStore().doRead(getTopicMap(), TopicMapStoreParameterType.BY_ID, parentId);
		if (parent instanceof ReadOnlyName) {
			cachedParent = parent;
		}
		return parent;
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
