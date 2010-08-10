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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.paged.IPagedName;
import de.topicmapslab.majortom.model.index.paging.IPagedConstructIndex;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of the interface {@link IName}.
 * 
 * @author Sven Krosse
 * 
 * 
 */
public class NameImpl extends ScopeableImpl implements IName, IPagedName {

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param parent
	 *            the parent topic
	 */
	public NameImpl(ITopicMapStoreIdentity identity, ITopic parent) {
		super(identity, parent.getParent(), parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String value, Topic... themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> cleanedThemes = HashUtil.getHashSet();
		cleanedThemes.addAll(Arrays.asList(themes));
		cleanedThemes.removeAll(getScope());
		if (cleanedThemes.isEmpty()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String value, Collection<Topic> themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> cleanedThemes = HashUtil.getHashSet();
		cleanedThemes.addAll(themes);
		cleanedThemes.removeAll(getScope());
		if (cleanedThemes.isEmpty()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(Locator value, Topic... themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> cleanedThemes = HashUtil.getHashSet();
		cleanedThemes.addAll(Arrays.asList(themes));
		cleanedThemes.removeAll(getScope());
		if (cleanedThemes.isEmpty()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(Locator value, Collection<Topic> themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> cleanedThemes = HashUtil.getHashSet();
		cleanedThemes.addAll(themes);
		cleanedThemes.removeAll(getScope());
		if (cleanedThemes.isEmpty()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String value, Locator datatype, Topic... themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (datatype == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> cleanedThemes = HashUtil.getHashSet();
		cleanedThemes.addAll(Arrays.asList(themes));
		cleanedThemes.removeAll(getScope());
		if (cleanedThemes.isEmpty()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String value, Locator datatype, Collection<Topic> themes) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		if (datatype == null) {
			throw new ModelConstraintException(this, "Datatype cannot be null.");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null.");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		Set<Topic> parentThemes = getScope();
		if (!themes.containsAll(parentThemes) || parentThemes.size() >= themes.size()) {
			throw new ModelConstraintException(this, "Variant will be in the same scope than the variant!");
		}
		return (Variant) getTopicMap().getStore().doCreate(this, TopicMapStoreParameterType.VARIANT, value, datatype, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Variant> getVariants() {
		return (Set<Variant>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VARIANT);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Variant> getVariants(IScope scope) {
		if (scope == null) {
			throw new IllegalArgumentException("Scope filter cannot be null!");
		}
		return Collections.unmodifiableSet((Set<Variant>) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VARIANT, scope));
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(Topic type) {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null.");
		}
		if (!type.getTopicMap().equals(getTopicMap())) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.TYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		return (Topic) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic getParent() {
		return (ITopic) super.getParent();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String value) throws ModelConstraintException {
		if (value == null) {
			throw new ModelConstraintException(this, "Value cannot be null.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.VALUE, value);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return (String) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.VALUE);
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		Topic type = getType();
		return "Topic-Name{Parent:" + (getParent() == null ? "null" : getParent().toString()) + ";Type:" + (type == null ? "null" : type.toString())
				+ ";Value:" + getValue() + "}";
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(int offset, int limit) {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getVariants(this, offset, limit);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variant> getVariants(int offset, int limit, Comparator<Variant> comparator) {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getVariants(this, offset, limit, comparator);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long getNumberOfVariants() {
		IPagedConstructIndex index = getTopicMap().getIndex(IPagedConstructIndex.class);
		if (!index.isOpen()) {
			index.open();
		}
		return index.getNumberOfVariants(this);
	}
}
