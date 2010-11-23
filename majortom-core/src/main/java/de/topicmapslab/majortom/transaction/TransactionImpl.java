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
package de.topicmapslab.majortom.transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.TopicMapImpl;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.exception.TransactionException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.model.transaction.ITransactionTopicMapStore;

/**
 * @author Sven Krosse
 * 
 */
public abstract class TransactionImpl extends TopicMapImpl implements ITransaction {

	private static final long serialVersionUID = 1L;

	/**
	 * the topic map
	 */
	private final ITopicMap topicMap;

	/**
	 * flag indicates if a transaction is close
	 */
	private boolean close = false;

	/**
	 * constructor
	 * 
	 * @param parent
	 *            the parent topic map
	 */
	public TransactionImpl(ITopicMap parent) {
		super(parent.getTopicMapSystem(), parent.getLocator());
		this.topicMap = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() throws TransactionException {
		getStore().commit();
		/*
		 * await termination of parent store queue
		 */
		getStore().getRealStore().commit();
		close = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void rollback() {
		getStore().rollback();
		close = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract ITransactionTopicMapStore getStore();

	/**
	 * {@inheritDoc}
	 */
	public ITopicMap getTopicMap() {
		return topicMap;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isClose() {
		return close;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReifier(Topic reifier) throws ModelConstraintException {
		if (reifier != null && !reifier.getTopicMap().equals(this)) {
			throw new ModelConstraintException(reifier, "Reifier has to be a topic of the same topic map.");
		}
		getStore().doModify(this, TopicMapStoreParameterType.REIFICATION, reifier);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		return (Topic) getStore().doRead(this, TopicMapStoreParameterType.REIFICATION);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addItemIdentifier(Locator identifier) throws ModelConstraintException {
		if (identifier == null) {
			throw new ModelConstraintException(this, "Item identifier cannot be null.");
		}
		if (!getItemIdentifiers().contains(identifier)) {
			getStore().doModify(this, TopicMapStoreParameterType.ITEM_IDENTIFIER, identifier);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeItemIdentifier(Locator identifier) {
		if (identifier != null && getItemIdentifiers().contains(identifier)) {
			getStore().doRemove(this, TopicMapStoreParameterType.ITEM_IDENTIFIER, identifier);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<Locator> getItemIdentifiers() {
		return Collections.unmodifiableSet((Set<Locator>) getStore().doRead(this, TopicMapStoreParameterType.ITEM_IDENTIFIER));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Association createAssociation(Topic type, Topic... themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null");
		}
		if (!type.getParent().equals(getTopicMap()) && !type.getParent().equals(this)) {
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (Association) getStore().doCreate(this, TopicMapStoreParameterType.ASSOCIATION, type, themes);
	}

	/**
	 * {@inheritDoc}
	 */
	public Association createAssociation(Topic type, Collection<Topic> themes) throws ModelConstraintException {
		if (type == null) {
			throw new ModelConstraintException(this, "Type cannot be null");
		}
		if (themes == null) {
			throw new ModelConstraintException(this, "Themes cannot be null");
		}
		if (!type.getParent().equals(getTopicMap())&& !type.getParent().equals(this)) {
			System.out.println(type.getParent().getId()+" = " + getTopicMap().getId());
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map!");
		}
		for (Topic theme : themes) {
			if (!theme.getTopicMap().equals(getTopicMap())&& !theme.getParent().equals(this)) {
				throw new ModelConstraintException(theme, "Theme has to be a topic of the same topic map!");
			}
		}
		return (Association) getStore().doCreate(this, TopicMapStoreParameterType.ASSOCIATION, type, themes);
	}
}
