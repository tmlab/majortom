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
package de.topicmapslab.majortom.store;

import java.util.Calendar;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

import org.tmapi.core.Construct;
import org.tmapi.core.IdentityConstraintException;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicInUseException;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.OperationSignatureException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;

/**
 * Base implementation of {@link ITopicMapStore}.
 * 
 * 
 * @author Sven Krosse
 * 
 */
public abstract class TopicMapStoreImpl extends ReadOnlyTopicMapStoreImpl {

	/**
	 * constructor
	 */
	public TopicMapStoreImpl() {
	}

	/**
	 * constructor
	 * 
	 * @param topicMapSystem
	 *            the parent system
	 */
	public TopicMapStoreImpl(final ITopicMapSystem topicMapSystem) {
		super(topicMapSystem);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		if (isReadOnly() && paramType != TopicMapStoreParameterType.LOCATOR && paramType != TopicMapStoreParameterType.SCOPE) {
			throw new UnmodifyableStoreException("Creation not supported by read-only stores!");
		}
		((TopicMapStoreMetaDataImpl)getMetaData()).setModificationTime();
		switch (paramType) {
		case ASSOCIATION: {
			if (context instanceof ITopicMap) {
				/*
				 * createAssociation(ITopic)
				 */
				if (params.length == 1 && params[0] instanceof ITopic) {
					return doCreateAssociation((ITopicMap) context, (ITopic) params[0]);
				}
				/*
				 * createAssociation(ITopic, ITopic[])
				 */
				else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof Topic[]) {
					return doCreateAssociation((ITopicMap) context, (ITopic) params[0], convert((Topic[]) params[1]));
				}
				/*
				 * createAssociation(ITopic, Collection<ITopic>)
				 */
				else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof Collection<?>) {
					return doCreateAssociation((ITopicMap) context, (ITopic) params[0], (Collection<ITopic>) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_ITEM_IDENTIFER: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doCreateTopicByItemIdentifier((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_SUBJECT_IDENTIFER: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doCreateTopicBySubjectIdentifier((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_SUBJECT_LOCATOR: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doCreateTopicBySubjectLocator((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case LOCATOR: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof String) {
				return doCreateLocator((ITopicMap) context, (String) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case NAME: {
			if (context instanceof ITopic) {
				/*
				 * createName(String)
				 */
				if (params.length == 1 && params[0] instanceof String) {
					return doCreateName((ITopic) context, (String) params[0]);
				}
				/*
				 * createName(String, ITopic[])
				 */
				else if (params.length == 2 && params[0] instanceof String && params[1] instanceof Topic[]) {
					return doCreateName((ITopic) context, (String) params[0], convert((Topic[]) params[1]));
				}
				/*
				 * createName(String, Collection<ITopic>)
				 */
				else if (params.length == 2 && params[0] instanceof String && params[1] instanceof Collection<?>) {
					return doCreateName((ITopic) context, (String) params[0], (Collection<ITopic>) params[1]);
				}
				/*
				 * createName(ITopic, String)
				 */
				else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof String) {
					return doCreateName((ITopic) context, (ITopic) params[0], (String) params[1]);
				}

				/*
				 * createName(ITopic,String, ITopic[])
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof Topic[]) {
					return doCreateName((ITopic) context, (ITopic) params[0], (String) params[1], convert((Topic[]) params[2]));
				}
				/*
				 * createName(ITopic,String, Collection<ITopic>)
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof Collection<?>) {
					return doCreateName((ITopic) context, (ITopic) params[0], (String) params[1], (Collection<ITopic>) params[2]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case OCCURRENCE: {
			if (context instanceof ITopic) {
				/*
				 * createOccurrence(ITopic,String)
				 */
				if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof String) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1]);
				}
				/*
				 * createOccurrence(ITopic,String)
				 */
				else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof ILocator) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (ILocator) params[1]);
				}
				/*
				 * createOccurrence(ITopic,String, ILocator)
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof ILocator) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1], (ILocator) params[2]);
				}
				/*
				 * createOccurrence(ITopic,String, ITopic[])
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof Topic[]) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1], convert((Topic[]) params[2]));
				}
				/*
				 * createOccurrence(ITopic,String, Collection<?>)
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof Collection<?>) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1], (Collection<ITopic>) params[2]);
				}
				/*
				 * createOccurrence(ITopic,Locator, ITopic[])
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof ILocator && params[2] instanceof Topic[]) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (ILocator) params[1], convert((Topic[]) params[2]));
				}
				/*
				 * createOccurrence(ITopic,Locator, Collection<?>)
				 */
				else if (params.length == 3 && params[0] instanceof ITopic && params[1] instanceof ILocator && params[2] instanceof Collection<?>) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (ILocator) params[1], (Collection<ITopic>) params[2]);
				}
				/*
				 * createOccurrence(ITopic, String, ILocator, ITopic[])
				 */
				else if (params.length == 4 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof ILocator && params[3] instanceof Topic[]) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1], (ILocator) params[2], convert((Topic[]) params[3]));
				}
				/*
				 * createOccurrence(ITopic,String, ILocator, Collection<?>)
				 */
				else if (params.length == 4 && params[0] instanceof ITopic && params[1] instanceof String && params[2] instanceof ILocator && params[3] instanceof Collection<?>) {
					return doCreateOccurrence((ITopic) context, (ITopic) params[0], (String) params[1], (ILocator) params[2], (Collection<ITopic>) params[3]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case VARIANT: {
			if (context instanceof IName) {
				/*
				 * createVariant(String,ITopic[])
				 */
				if (params.length == 2 && params[0] instanceof String && params[1] instanceof Topic[]) {
					return doCreateVariant((IName) context, (String) params[0], convert((Topic[]) params[1]));
				}
				/*
				 * createVariant(String,Collection<?>)
				 */
				else if (params.length == 2 && params[0] instanceof String && params[1] instanceof Collection<?>) {
					return doCreateVariant((IName) context, (String) params[0], (Collection<ITopic>) params[1]);
				}
				/*
				 * createVariant(ILocator,ITopic[])
				 */
				else if (params.length == 2 && params[0] instanceof ILocator && params[1] instanceof Topic[]) {
					return doCreateVariant((IName) context, (ILocator) params[0], convert((Topic[]) params[1]));
				}
				/*
				 * createVariant(ILocator,Collection<?>)
				 */
				else if (params.length == 2 && params[0] instanceof ILocator && params[1] instanceof Collection<?>) {
					return doCreateVariant((IName) context, (ILocator) params[0], (Collection<ITopic>) params[1]);
				}
				/*
				 * createVariant(String, ILocator,ITopic[])
				 */
				else if (params.length == 3 && params[0] instanceof String && params[1] instanceof ILocator && params[2] instanceof Topic[]) {
					return doCreateVariant((IName) context, (String) params[0], (ILocator) params[1], convert((Topic[]) params[2]));
				}
				/*
				 * createVariant(String, ILocator,Collection<?>)
				 */
				else if (params.length == 3 && params[0] instanceof String && params[1] instanceof ILocator && params[2] instanceof Collection<?>) {
					return doCreateVariant((IName) context, (String) params[0], (ILocator) params[1], (Collection<ITopic>) params[2]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case ROLE: {
			if (context instanceof IAssociation && params.length == 2 && params[0] instanceof ITopic && params[1] instanceof ITopic) {
				return doCreateRole((IAssociation) context, (ITopic) params[0], (ITopic) params[1]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case TOPIC: {
			if (context instanceof ITopicMap) {
				return doCreateTopicByItemIdentifier((ITopicMap) context, doCreateItemIdentifier((ITopicMap) context));
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case SCOPE: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof Collection<?>) {
				return doCreateScope((ITopicMap) context, (Collection<ITopic>) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		}
		throw new OperationSignatureException(context, paramType, params);
	}

	/**
	 * Creates an internal item-identifier for a construct.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @return the created item-identifier
	 */
	protected abstract ILocator doCreateItemIdentifier(ITopicMap topicMap);

	/**
	 * Create a new association item.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param type
	 *            the type
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws TopicMapStoreException;

	/**
	 * Create a new association item.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param type
	 *            the type
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new name characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param value
	 *            the characteristics value
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IName doCreateName(ITopic topic, String value) throws TopicMapStoreException;

	/**
	 * Create a new name characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param value
	 *            the characteristics value
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new name characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IName doCreateName(ITopic topic, ITopic type, String value) throws TopicMapStoreException;

	/**
	 * Create a new name characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @param datatype
	 *            the data type
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws TopicMapStoreException;

	/**
	 * Create a new occurrence characteristics item.
	 * 
	 * @param topic
	 *            the parent topic
	 * @param type
	 *            the type
	 * @param value
	 *            the characteristics value
	 * @param datatype
	 *            the data type
	 * @param themes
	 *            the scoping themes
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new association role item.
	 * 
	 * @param association
	 *            the parent association
	 * @param type
	 *            the type
	 * @param player
	 *            the role player
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws TopicMapStoreException;

	

	/**
	 * Create a new topic item without any identifier.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws TopicMapStoreException;

	/**
	 * Create a new topic item.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param itemIdentifier
	 *            the item-identifier
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws TopicMapStoreException;

	/**
	 * Create a new topic item.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param subjectIdentifier
	 *            the subject-identifier
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws TopicMapStoreException;

	/**
	 * Create a new topic item.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param subjectLocator
	 *            the subject-locator
	 * @return the created construct
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws TopicMapStoreException;


	/**
	 * Create a new variant for the given name.
	 * 
	 * @param name
	 *            the parent name
	 * @param value
	 *            the value
	 * @param themes
	 *            the scoping themes
	 * @return the created variant
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new variant for the given name.
	 * 
	 * @param name
	 *            the parent name
	 * @param value
	 *            the value
	 * @param themes
	 *            the scoping themes
	 * @return the created variant
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IVariant doCreateVariant(IName name, ILocator value, Collection<ITopic> themes) throws TopicMapStoreException;

	/**
	 * Create a new variant for the given name.
	 * 
	 * @param name
	 *            the parent name
	 * @param value
	 *            the value
	 * @param datatype
	 *            the data type
	 * @param themes
	 *            the scoping themes
	 * @return the created variant
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws TopicMapStoreException;

	// *********************
	// * MODIFY OPERATIONS *
	// *********************

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		if (isReadOnly()) {
			throw new UnmodifyableStoreException("Modfication operation not supported by read-only stores!");
		}
		((TopicMapStoreMetaDataImpl)getMetaData()).setModificationTime();
		switch (paramType) {
		case ITEM_IDENTIFIER: {
			if (params.length == 1 && params[0] instanceof ILocator) {
				ITopic other = checkMergeConditionOfItemIdentifier(context, (ILocator) params[0]);
				if (other != null) {
					doMerge(context, other);
				}
				doModifyItemIdentifier(context, (ILocator) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case PLAYER: {
			if (context instanceof IAssociationRole && params.length == 1 && params[0] instanceof ITopic) {
				doModifyPlayer((IAssociationRole) context, (ITopic) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case REIFICATION: {
			if (context instanceof IReifiable && params.length == 1 && params[0] instanceof ITopic) {
				checkReificationConstraintBeforeModification((IReifiable) context, (ITopic) params[0]);
				doModifyReifier((IReifiable) context, (ITopic) params[0]);
			} else if (context instanceof IReifiable && params.length == 1 && params[0] == null) {
				doModifyReifier((IReifiable) context, null);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case SCOPE: {
			if (context instanceof IScopable && params.length == 1 && params[0] instanceof ITopic) {
				doModifyScope((IScopable) context, (ITopic) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case SUBJECT_IDENTIFIER: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ILocator) {
				ITopic other = checkMergeConditionOfSubjectIdentifier((ITopic) context, (ILocator) params[0]);
				if (other != null) {
					doMerge(context, other);
				}
				doModifySubjectIdentifier((ITopic) context, (ILocator) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case SUBJECT_LOCATOR: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ILocator) {
				ITopic other = checkMergeConditionOfSubjectLocator((ITopic) context, (ILocator) params[0]);
				if (other != null) {
					doMerge(context, other);
				}
				doModifySubjectLocator((ITopic) context, (ILocator) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case SUPERTYPE: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ITopic) {
				doModifySupertype((ITopic) context, (ITopic) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case TAG: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof String) {
				doModifyTag((ITopicMap) context, (String) params[0]);
			} else if (context instanceof ITopicMap && params.length == 2 && params[0] instanceof String && params[1] instanceof Calendar) {
				doModifyTag((ITopicMap) context, (String) params[0], (Calendar) params[1]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case TYPE: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ITopic) {
				doModifyTopicType((ITopic) context, (ITopic) params[0]);
			} else if (context instanceof ITypeable && params.length == 1 && params[0] instanceof ITopic) {
				doModifyType((ITypeable) context, (ITopic) params[0]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case VALUE: {
			if (context instanceof IName && params.length == 1 && params[0] instanceof String) {
				doModifyValue((IName) context, (String) params[0]);
			} else if (context instanceof IDatatypeAware && params.length == 1 && params[0] instanceof String) {
				doModifyValue((IDatatypeAware) context, (String) params[0]);
			} else if (context instanceof IDatatypeAware && params.length == 1) {
				doModifyValue((IDatatypeAware) context, params[0]);
			} else if (context instanceof IDatatypeAware && params.length == 2 && params[0] instanceof String && params[1] instanceof ILocator) {
				doModifyValue((IDatatypeAware) context, (String) params[0], (ILocator) params[1]);
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		case META_DATA: {
			if (context == null && params.length == 3 && params[0] instanceof IRevision && params[1] instanceof String && params[2] instanceof String) {
				doModifyMetaData((IRevision) params[0], params[1].toString(), params[2].toString());
			} else {
				throw new OperationSignatureException(context, paramType, params);
			}
		}
			break;
		default: {
			throw new OperationSignatureException(context, paramType, params);
		}
		}

	}

	/**
	 * Method checks if there is another with the item-identifier
	 * 
	 * @param c
	 *            the construct
	 * @param identifier
	 *            the identifier
	 * @return the topic to merge in the current one, <code>null</code> if no
	 *         topic has to be merged
	 * @throws IdentityConstraintException
	 *             thrown if merge candidate detected and automatic merging is
	 *             disabled or the constructs are not topics
	 */
	protected ITopic checkMergeConditionOfItemIdentifier(IConstruct c, ILocator identifier) {
		/*
		 * check if there is construct with the identifier as item-identifier
		 */
		IConstruct other = doReadConstruct(getTopicMap(), identifier);
		/*
		 * both items are equal
		 */
		if (c.equals(other)) {
			return null;
		}
		if (c instanceof ITopic) {
			if (other instanceof ITopic) {
				/*
				 * automatic merging enabled
				 */
				if (doAutomaticMerging()) {
					// doMerge(c,other);
					return (ITopic) other;
				}
				/*
				 * automatic merging disabled
				 */
				throw new IdentityConstraintException(c, other, identifier, "Item-Identifier in use but automatic merging is disabled!");
			} else if (other != null) {
				/*
				 * item-identifier in use
				 */
				throw new IdentityConstraintException(c, other, identifier, "Item-Identifier in use but construct is not a topic!");
			}

			ITopic t = doReadTopicBySubjectIdentifier(getTopicMap(), identifier);
			if (c.equals(t)) {
				return null;
			}
			if (t != null) {
				/*
				 * automatic merging enabled
				 */
				if (doAutomaticMerging()) {
					// doMerge(c,t);
					return t;
				}
				/*
				 * automatic merging disabled
				 */
				throw new IdentityConstraintException(c, other, identifier, "Identifier is used as subject-identifier but automatic merging is disabled!");
			}
			/*
			 * modification is allowed
			 */
			return null;
		}
		/*
		 * construct is not a topic
		 */
		else if (other != null) {
			/*
			 * item-identifier in use
			 */
			throw new IdentityConstraintException(c, other, identifier, "Item-Identifier in use but construct is not a topic!");
		}
		/*
		 * modification is allowed
		 */
		return null;
	}

	/**
	 * Add a new item-identifier to the given construct
	 * 
	 * @param c
	 *            the construct
	 * @param itemIdentifier
	 *            the item identifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException;

	/**
	 * Modify the player of the given role.
	 * 
	 * @param role
	 *            the role
	 * @param player
	 *            the new player
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyPlayer(IAssociationRole role, ITopic player) throws TopicMapStoreException;

	/**
	 * Checks the reification constraint before calling modification method.
	 * 
	 * @param r
	 *            the reified construct
	 * @param reifier
	 *            the reifier
	 * @throws ModelConstraintException
	 *             thrown if constraint check fails
	 */
	protected void checkReificationConstraintBeforeModification(IReifiable r, ITopic reifier) throws ModelConstraintException {
		/*
		 * check only if new reifier is not null
		 */
		if (reifier != null) {
			Reifiable r_ = reifier.getReified();
			if (r_ != null && !r.equals(r_)) {
				throw new ModelConstraintException(r, "Reifier is already in use!");
			}
		}
	}

	/**
	 * Modify the reifier of the given reified item.
	 * 
	 * @param r
	 *            the reified item
	 * @param reifier
	 *            the reifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyReifier(IReifiable r, ITopic reifier) throws TopicMapStoreException;

	/**
	 * Add a new theme to the given scoped item.
	 * 
	 * @param s
	 *            the scoped item
	 * @param theme
	 *            the theme
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyScope(IScopable s, ITopic theme) throws TopicMapStoreException;

	/**
	 * Method checks if there is another with the subject-identifier
	 * 
	 * @param t
	 *            the topic
	 * @param identifier
	 *            the identifier
	 * @return the topic to merge in the current one, <code>null</code> if no
	 *         topic has to be merged
	 * @throws TopicMapStoreException
	 *             thrown if merging fails
	 */
	protected ITopic checkMergeConditionOfSubjectIdentifier(ITopic t, ILocator identifier) throws TopicMapStoreException {
		/*
		 * check if there is topic with the identifier as subject-identifier
		 */
		ITopic other = doReadTopicBySubjectIdentifier(getTopicMap(), identifier);
		/*
		 * both items are equal
		 */
		if (t.equals(other)) {
			return null;
		}
		/*
		 * is there another topic?
		 */
		if (other != null) {
			/*
			 * automatic merging enabled
			 */
			if (doAutomaticMerging()) {
				// doMerge(t, other);
				return other;
			}
			/*
			 * automatic merging disabled
			 */
			throw new IdentityConstraintException(t, other, identifier, "Subject-Identifier in use but automatic merging is disabled!");
		}
		/*
		 * check if there is construct with the identifier as item-identifier
		 */
		IConstruct c = doReadConstruct(getTopicMap(), identifier);
		/*
		 * both items are equal
		 */
		if (t.equals(c)) {
			return null;
		}
		/*
		 * is there another construct?
		 */
		if (c instanceof ITopic) {
			/*
			 * automatic merging enabled
			 */
			if (doAutomaticMerging()) {
				// doMerge(t, c);
				return (ITopic) c;
			}
			/*
			 * automatic merging disabled
			 */
			throw new IdentityConstraintException(t, c, identifier, "Item-Identifier in use but automatic merging is disabled!");
		}
		/*
		 * modification is allowed
		 */
		return null;
	}

	/**
	 * Add a new subject-identifier to the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param subjectIdentifier
	 *            the subject-identifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException;

	/**
	 * Method checks if there is another with the subject-locator
	 * 
	 * @param t
	 *            the topic
	 * @param identifier
	 *            the identifier
	 * @return the topic to merge in the current one, <code>null</code> if no
	 *         topic has to be merged
	 * @throws TopicMapStoreException
	 *             thrown if merging fails
	 */
	protected ITopic checkMergeConditionOfSubjectLocator(ITopic t, ILocator identifier) throws TopicMapStoreException {
		/*
		 * check if there is topic with the identifier as subject-locator
		 */
		ITopic other = doReadTopicBySubjectLocator(getTopicMap(), identifier);
		/*
		 * both items are equal
		 */
		if (t.equals(other)) {
			return null;
		}
		/*
		 * automatic merging enabled
		 */
		if (other != null && doAutomaticMerging()) {
			// doMerge(t, other);
			return other;
		}
		/*
		 * automatic merging disabled
		 */
		else if (other != null) {
			throw new IdentityConstraintException(t, other, identifier, "Subject-locator in use but automatic merging is disabled!");
		}
		/*
		 * modification is allowed
		 */
		return null;
	}

	/**
	 * Add a new subject-locator to the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param subjectLocator
	 *            the subject-locator
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException;

	/**
	 * Add a new super type to the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the super type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifySupertype(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Add a new tag to the given topic map for the current time stamp
	 * 
	 * @param tm
	 *            the topic map
	 * @param tag
	 *            the tag
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyTag(ITopicMap tm, String tag) throws TopicMapStoreException;

	/**
	 * Add a new tag to the given topic map for the given time stamp
	 * 
	 * @param tm
	 *            the topic map
	 * @param tag
	 *            the tag
	 * @param timestamp
	 *            the time stamp
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws TopicMapStoreException;

	/**
	 * Modify the type of the given typed item.
	 * 
	 * @param t
	 *            the typed item
	 * @param type
	 *            the type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyType(ITypeable t, ITopic type) throws TopicMapStoreException;

	/**
	 * Add a new type to the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type to add
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyTopicType(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Modify the value of the given name.
	 * 
	 * @param n
	 *            the name
	 * @param value
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyValue(IName n, String value) throws TopicMapStoreException;

	/**
	 * Modify the value of the given data-type-aware.
	 * 
	 * @param t
	 *            data-type-aware
	 * @param value
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyValue(IDatatypeAware t, String value) throws TopicMapStoreException;

	/**
	 * Modify the value and the data type of the given data-type-aware.
	 * 
	 * @param t
	 *            data-type-aware
	 * @param value
	 *            the value
	 * @param datatype
	 *            the data type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws TopicMapStoreException;

	/**
	 * Modify the value and the data type of the given data-type-aware.
	 * 
	 * @param t
	 *            data-type-aware
	 * @param value
	 *            the value
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyValue(IDatatypeAware t, Object value) throws TopicMapStoreException;

	/**
	 * Add a new meta data set to the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @param key
	 *            the key of the meta data set
	 * @param value
	 *            the value of the meta data set
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doModifyMetaData(IRevision revision, final String key, final String value) throws TopicMapStoreException;

	// ********************
	// * MERGE OPERATIONS *
	// ********************

	/**
	 * {@inheritDoc}
	 */
	public <T extends Construct> void doMerge(T context, T... others) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		if (isReadOnly()) {
			throw new UnmodifyableStoreException("Merge operation not supported by read-only stores!");
		}
		((TopicMapStoreMetaDataImpl)getMetaData()).setModificationTime();
		if (context instanceof TopicMap && others.length > 0) {
			for (T other : others) {
				if (other instanceof TopicMap) {
					doMergeTopicMaps((TopicMap) context, (TopicMap) other);
				} else {
					throw new TopicMapStoreException("Cannot merge other constructs than topic maps into a topic map.");
				}
			}
		} else if (context instanceof ITopic && others.length > 0) {
			for (T other : others) {
				if (other instanceof ITopic) {
					checkMergeConstraint((ITopic) context, (ITopic) other);
					doMergeTopics((ITopic) context, (ITopic) other);
				} else {
					throw new TopicMapStoreException("Cannot merge other constructs than topics into a topic.");
				}
			}
		} else {
			throw new TopicMapStoreException("Cannot merge other constructs than topics or topic maps into a topic.");
		}
	}

	/**
	 * Merge a topic in the given topic
	 * 
	 * @param context
	 *            the topic to merge in
	 * @param other
	 *            the topic to merge in
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doMergeTopics(ITopic context, ITopic other) throws TopicMapStoreException;

	/**
	 * Method checks the merging constraints. At least one of the topics may not
	 * have a reified construct.
	 * 
	 * @param context
	 *            the context
	 * @param other
	 *            the other topic
	 * @throws TopicMapStoreException
	 */
	protected void checkMergeConstraint(ITopic context, ITopic other) throws TopicMapStoreException {
		/*
		 * check if at least one of the topics has no reified construct
		 */
		IReifiable reifiable = doReadReification(context);
		IReifiable otherReifiable = doReadReification(other);
		if (reifiable != null && otherReifiable != null) {
			throw new ModelConstraintException(context, "Merging topics not allowed because of reified clash!");
		}
	}

	/**
	 * Merge a topic map in the given topic map.
	 * 
	 * @param context
	 *            the topic map
	 * @param other
	 *            the topic map to merge in
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doMergeTopicMaps(TopicMap context, TopicMap other) throws TopicMapStoreException;

	// ********************
	// * REMOVE OPERATION *
	// ********************

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		if (isReadOnly()) {
			throw new UnmodifyableStoreException("Remove operation not supported by read-only stores!");
		}
		((TopicMapStoreMetaDataImpl)getMetaData()).setModificationTime();
		switch (paramType) {
		case ITEM_IDENTIFIER: {
			if (context instanceof IConstruct && params.length == 1 && params[0] instanceof ILocator) {
				doRemoveItemIdentifier(context, (ILocator) params[0]);
			}
		}
			break;
		case SCOPE: {
			if (context instanceof IScopable && params.length == 1 && params[0] instanceof ITopic) {
				doRemoveScope((IScopable) context, (ITopic) params[0]);
			}
		}
			break;
		case SUBJECT_IDENTIFIER: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ILocator) {
				doRemoveSubjectIdentifier((ITopic) context, (ILocator) params[0]);
			}
		}
			break;
		case SUBJECT_LOCATOR: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ILocator) {
				doRemoveSubjectLocator((ITopic) context, (ILocator) params[0]);
			}
		}
			break;
		case SUPERTYPE: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ITopic) {
				doRemoveSupertype((ITopic) context, (ITopic) params[0]);
			}
		}
			break;
		case TYPE: {
			if (context instanceof ITopic && params.length == 1 && params[0] instanceof ITopic) {
				doRemoveType((ITopic) context, (ITopic) params[0]);
			}
		}
			break;
		default: {
			throw new OperationSignatureException(context, paramType, params);
		}
		}
	}

	/**
	 * Remove the given item identifier from the given construct.
	 * 
	 * @param c
	 * @param itemIdentifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws TopicMapStoreException;

	/**
	 * Remove the theme from the given scoped item.
	 * 
	 * @param s
	 *            the scoped item
	 * @param theme
	 *            the theme
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveScope(IScopable s, ITopic theme) throws TopicMapStoreException;

	/**
	 * Remove the subject-identifier from the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param subjectIdentifier
	 *            the subject-identifier
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws TopicMapStoreException;

	/**
	 * Remove the subject-locator from the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param subjectLocator
	 *            the subject-locator
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws TopicMapStoreException;

	/**
	 * Remove the type from the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveSupertype(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Remove the super type from the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected abstract void doRemoveType(ITopic t, ITopic type) throws TopicMapStoreException;

	// ********************
	// * REMOVE CONSTRUCT *
	// ********************

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		if (isReadOnly()) {
			throw new UnmodifyableStoreException("Remove operation not supported by read-only stores!");
		}
		((TopicMapStoreMetaDataImpl)getMetaData()).setModificationTime();
		if (context instanceof ITopicMap) {
			doRemoveTopicMap((ITopicMap) context, cascade);
			close();
		} else if (context instanceof ITopic) {
			/*
			 * check if topic is in use if deletion is not marked as cascade
			 */
			if (!cascade && isTopicInUse((ITopic) context)) {
				throw new TopicInUseException((ITopic) context, "Topic is in use!");
			}
			doRemoveTopic((ITopic) context, cascade);
		} else if (context instanceof IName) {
			doRemoveName((IName) context, cascade);
		} else if (context instanceof IOccurrence) {
			doRemoveOccurrence((IOccurrence) context, cascade);
		} else if (context instanceof IAssociation) {
			doRemoveAssociation((IAssociation) context, cascade);
		} else if (context instanceof IAssociationRole) {
			/*
			 * delete role cascade means remove parent association too
			 */
			if (cascade) {
				doRemoveAssociation(((IAssociationRole) context).getParent(), cascade);
			} else {
				doRemoveRole((IAssociationRole) context, cascade);
			}
		} else if (context instanceof IVariant) {
			doRemoveVariant((IVariant) context, cascade);
		} else {
			throw new TopicMapStoreException("Unknown construct type to remove '" + context.getClass() + "'.");
		}
		((ConstructImpl) context).setRemoved(true);
	}

	/**
	 * Remove the topic map.
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws TopicMapStoreException;

	/**
	 * Method checks if the topic is used by any topic map relation.
	 * 
	 * @param topic
	 *            the topic to check
	 * @return <code>true</code> if the topic is used as type, reifier etc. ,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isTopicInUse(final ITopic topic) {
		if (isTopicUsedAsPlayer(topic) || isTopicUsedAsTheme(topic) || isTopicUsedAsType(topic) || isTopicUsedAsSupertype(topic) || isTopicUsedAsReifier(topic)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the topic is used as theme by any scoped item.
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if the topic is used as theme,
	 *         <code>false</code> otherwise
	 */
	protected boolean isTopicUsedAsTheme(final ITopic topic) {
		/*
		 * use as theme
		 */
		try {
			IScopedIndex scopeIndex = getIndex(IScopedIndex.class);
			if (!scopeIndex.isOpen()) {
				scopeIndex.open();
			}
			if (!scopeIndex.getScopes(topic).isEmpty()) {
				return true;
			}
		} catch (UnsupportedOperationException e) {
			// scope index not supported
		}
		return false;
	}

	/**
	 * Checks if the topic is used as type by any typed item.
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if the topic is used as type,
	 *         <code>false</code> otherwise
	 */
	protected boolean isTopicUsedAsType(final ITopic topic) {
		/*
		 * use as type
		 */
		try {
			ITypeInstanceIndex index = getIndex(ITypeInstanceIndex.class);
			if (!index.isOpen()) {
				index.open();
			}
			if (!index.getTopics(topic).isEmpty()) {
				return true;
			}
			if (!index.getAssociations(topic).isEmpty()) {
				return true;
			}
			if (!index.getOccurrences(topic).isEmpty()) {
				return true;
			}
			if (!index.getNames(topic).isEmpty()) {
				return true;
			}
			if (!index.getRoles(topic).isEmpty()) {
				return true;
			}
		} catch (UnsupportedOperationException e) {
			// index not supported
		}
		return false;
	}

	/**
	 * Checks if the topic is used as supertype by any topic item.
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if the topic is used as supertype,
	 *         <code>false</code> otherwise
	 */
	protected boolean isTopicUsedAsSupertype(final ITopic topic) {
		/*
		 * use as supertype
		 */
		try {
			ISupertypeSubtypeIndex index = getIndex(ISupertypeSubtypeIndex.class);
			if (!index.isOpen()) {
				index.open();
			}
			if (!index.getSubtypes(topic).isEmpty()) {
				return true;
			}
		} catch (UnsupportedOperationException e) {
			// index not supported
		}
		return false;
	}

	/**
	 * Checks if the topic is used as player by any association item.
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if the topic is used as player,
	 *         <code>false</code> otherwise
	 */
	protected boolean isTopicUsedAsPlayer(final ITopic topic) {
		/*
		 * use as role-player
		 */
		for (Role r : topic.getRolesPlayed()) {
			/*
			 * ignore instance role
			 */
			if (existsTmdmInstanceRoleType() && r.getType().equals(getTmdmInstanceRoleType())) {
				continue;
			}
			/*
			 * ignore subtype-role
			 */
			if (existsTmdmSubtypeRoleType() && r.getType().equals(getTmdmSubtypeRoleType())) {
				continue;
			}
			/*
			 * any other role
			 */
			return true;
		}

		return false;
	}

	/**
	 * Checks if the topic is used as reifier and reification is restricted for
	 * deletion
	 * 
	 * @param topic
	 *            the topic
	 * @return <code>true</code> if the topic is used as reifier and reification
	 *         is restricted for deletion, <code>false</code> otherwise
	 */
	protected boolean isTopicUsedAsReifier(final ITopic topic) {
		/*
		 * check if deletion constraints are defined and topic is used as
		 * reifier
		 */
		if (isReificationDeletionRestricted() && topic.getReified() != null) {
			return true;
		}
		return false;
	}

	/**
	 * Remove the topic.
	 * 
	 * @param topic
	 *            the topic
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveTopic(ITopic topic, boolean cascade) throws TopicMapStoreException;

	/**
	 * Remove the name.
	 * 
	 * @param name
	 *            the name
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveName(IName name, boolean cascade) throws TopicMapStoreException;

	/**
	 * Remove the occurrence.
	 * 
	 * @param occurrence
	 *            the occurrence
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws TopicMapStoreException;

	/**
	 * Remove the association.
	 * 
	 * @param association
	 *            the association
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveAssociation(IAssociation association, boolean cascade) throws TopicMapStoreException;

	/**
	 * Remove the association role.
	 * 
	 * @param role
	 *            the role
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveRole(IAssociationRole role, boolean cascade) throws TopicMapStoreException;

	/**
	 * Remove the variant.
	 * 
	 * @param variant
	 *            the variant
	 * @param cascade
	 *            flag indicates if the dependent construct should removed too
	 * @throws TopicMapStoreException
	 */
	protected abstract void doRemoveVariant(IVariant variant, boolean cascade) throws TopicMapStoreException;


	/**
	 * Transform the given array to a set of the specific type.
	 * 
	 * @param <T>
	 *            the type
	 * @param array
	 *            the array
	 * @return the transformed set
	 */
	private static Set<ITopic> convert(Topic[] array) {
		Set<ITopic> target = HashUtil.getHashSet();
		for (Topic o : array) {
			target.add((ITopic) o);
		}
		return target;
	}






	// *******************
	// * Utility methods *
	// *******************

	/**
	 * Create the specific association of the topic maps data model representing
	 * a type-instance relation between the given topics.
	 * 
	 * @param instance
	 *            the instance
	 * @param type
	 *            the type
	 */
	protected void createTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) {
		IAssociation association = doCreateAssociation(getTopicMap(), getTmdmTypeInstanceAssociationType());
		doCreateRole(association, getTmdmInstanceRoleType(), instance);
		doCreateRole(association, getTmdmTypeRoleType(), type);
	}

	/**
	 * Create the specific association of the topic maps data model representing
	 * a supertype-subtype relation between the given topics.
	 * 
	 * @param type
	 *            the type
	 * @param supertype
	 *            the supertype
	 */
	protected void createSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) {
		IAssociation association = doCreateAssociation(getTopicMap(), getTmdmSupertypeSubtypeAssociationType());
		doCreateRole(association, getTmdmSubtypeRoleType(), type);
		doCreateRole(association, getTmdmSupertypeRoleType(), supertype);
	}

	/**
	 * Removes the corresponding association of the supertype-subtype relation
	 * between the given topics.
	 * 
	 * @param type
	 *            the type
	 * @param supertype
	 *            the supertype
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected void removeSupertypeSubtypeAssociation(ITopic type, ITopic supertype, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmSupertypeSubtypeAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmSubtypeRoleType()).iterator().next().getPlayer().equals(type)
						&& association.getRoles(getTmdmSupertypeRoleType()).iterator().next().getPlayer().equals(supertype)) {
					doRemoveAssociation(association, false);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException("Invalid meta model! Missing supertype or subtype role!", e);
			}
		}
	}

	/**
	 * Removes the corresponding association of the type-instance relation
	 * between the given topics.
	 * 
	 * @param instance
	 *            the instance
	 * @param type
	 *            the type
	 * @return <code>true</code> if an association was removed,
	 *         <code>false</code> otherwise.
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	protected void removeTypeInstanceAssociation(ITopic instance, ITopic type, IRevision revision) throws TopicMapStoreException {
		Collection<IAssociation> associations = doReadAssociation(type, getTmdmTypeInstanceAssociationType());
		for (IAssociation association : associations) {
			try {
				if (association.getRoles(getTmdmInstanceRoleType()).iterator().next().getPlayer().equals(instance)
						&& association.getRoles(getTmdmTypeRoleType()).iterator().next().getPlayer().equals(type)) {
					doRemoveAssociation(association, false);
					break;
				}
			} catch (NoSuchElementException e) {
				throw new TopicMapStoreException("Invalid meta model! Missing type or instance role!", e);
			}
		}
	}

	/**
	 * Returns the topic type representing the type-instance association type of
	 * the topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmTypeInstanceAssociationType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the type association role type of the
	 * topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmTypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the instance association role type of
	 * the topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmInstanceRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the supertype-subtype association
	 * type of the topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmSupertypeSubtypeAssociationType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the supertype association role type
	 * of the topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the subtype association role type of
	 * the topic map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public ITopic getTmdmSubtypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Returns the topic type representing the default name type of the topic
	 * map data model.
	 * 
	 * @return the topic type
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 * @since 1.1.2
	 */
	public ITopic getTmdmDefaultNameType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		ITopic topic = doReadTopicBySubjectIdentifier(getTopicMap(), loc);
		if (topic == null) {
			topic = doCreateTopicBySubjectIdentifier(getTopicMap(), loc);
		}
		return topic;
	}

	/**
	 * Check if the topic type representing the type-instance association type
	 * of the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmTypeInstanceAssociationType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_TYPE_INSTANCE_ASSOCIATION);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the type association role type of
	 * the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmTypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_TYPE_ROLE_TYPE);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the instance association role type
	 * of the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmInstanceRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_INSTANCE_ROLE_TYPE);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the supertype-subtype association
	 * type of the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmSupertypeSubtypeAssociationType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the supertype association role type
	 * of the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmSupertypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUPERTYPE_ROLE_TYPE);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the subtype association role type of
	 * the topic map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public boolean existsTmdmSubtypeRoleType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Check if the topic type representing the default name type of the topic
	 * map data model exists.
	 * 
	 * @return <code>true</code> if this topic type exists, <code>false</code>
	 *         otherwise
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 * @since 1.1.2
	 */
	public boolean existsTmdmDefaultNameType() throws TopicMapStoreException {
		ILocator loc = doCreateLocator(getTopicMap(), TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE);
		return doReadTopicBySubjectIdentifier(getTopicMap(), loc) != null;
	}

	/**
	 * Store a change set for the given revision. The change set will be created
	 * by the given arguments.
	 * 
	 * @param revision
	 *            the revision the change set should add to
	 * @param type
	 *            the type of change
	 * @param context
	 *            the context of change
	 * @param newValue
	 *            the new value after change
	 * @param oldValue
	 *            the old value before change
	 */
	public abstract void storeRevision(final IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue);

	/**
	 * Store a change set for the given revision. The change set will be created
	 * by the given arguments.
	 * 
	 * @param revision
	 *            the revision the change set should add to
	 * @param type
	 *            the type of change
	 * @param context
	 *            the context of change
	 * @param newValue
	 *            the new value after change
	 * @param oldValue
	 *            the old value before change
	 */
	public void storeRevision(TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		storeRevision(createRevision(type), type, context, newValue, oldValue);
	}

	/**
	 * Creating a new revision object.
	 * @param the event type for the internal change set
	 * 
	 * @return the new revision object
	 */
	protected abstract IRevision createRevision(TopicMapEventType type);

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		super.removeDuplicates();
		MergeUtils.removeDuplicates(this, getTopicMap());
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		super.clear();
		doModifyReifier(getTopicMap(), null);
		for (ILocator ii : doReadItemIdentifiers(getTopicMap())) {
			doRemoveItemIdentifier(getTopicMap(), ii);
		}
		for (ITopic t : doReadTopics(getTopicMap())) {
			if (!t.isRemoved()) {
				try {
					t.remove(true);
				} catch (Exception e) {
					// NOTHING TO DO HERE
				}
			}
		}
	}


}
