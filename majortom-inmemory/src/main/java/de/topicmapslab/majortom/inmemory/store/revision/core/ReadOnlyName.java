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

import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyName extends ReadOnlyScopable implements IName {

	private Set<String> variantIds = HashUtil.getHashSet();
	private final String typeId;
	private final String value;

	/*
	 * cached values
	 */
	private Topic cachedType;
	private Set<Variant> cachedVariants;

	/**
	 * @param clone
	 */
	public ReadOnlyName(IName clone) {
		super(clone);
		typeId = clone.getType().getId();
		value = clone.getValue();

		for (Variant variant : clone.getVariants()) {
			variantIds.add(variant.getId());
		}
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
	public Set<Variant> getVariants(IScope scope) {
		throw new UnsupportedOperationException("Method not supported by the read only construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String arg0, Topic... arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String arg0, Collection<Topic> arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(Locator arg0, Topic... arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(Locator arg0, Collection<Topic> arg1) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String arg0, Locator arg1, Topic... arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Variant createVariant(String arg0, Locator arg1, Collection<Topic> arg2) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Variant> getVariants() {
		Set<Variant> variants = HashUtil.getHashSet();
		if (cachedVariants != null) {
			variants.addAll(cachedVariants);
		}

		if (!variantIds.isEmpty()) {
			Set<String> ids = HashUtil.getHashSet(variantIds);
			for (String id : ids) {
				Variant v = (Variant) getTopicMap().getConstructById(id);
				/*
				 * is read-only component -> cache access
				 */
				if (v instanceof ReadOnlyVariant) {
					if (cachedVariants == null) {
						cachedVariants = HashUtil.getHashSet();
					}
					cachedVariants.add(v);
					variantIds.remove(id);
				}
				variants.add(v);
			}
		}

		return variants;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValue(String arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only!");
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		if (cachedType != null) {
			return cachedType;
		}
		Topic type = (Topic) getTopicMap().getConstructById(typeId);
		if (type instanceof ReadOnlyTopic) {
			cachedType = type;
		}
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only!");
	}
}
