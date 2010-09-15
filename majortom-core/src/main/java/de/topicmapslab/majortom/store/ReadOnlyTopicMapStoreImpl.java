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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IConstructFactory;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.OperationSignatureException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Base implementation of a read only {@link ITopicMapStore}.
 * 
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyTopicMapStoreImpl implements ITopicMapStore {

	private boolean connected = false;
	private Set<ITopicMapListener> listeners = null;

	/**
	 * {@inheritDoc}
	 */
	public void addTopicMapListener(ITopicMapListener listener) {
		if (listeners == null) {
			listeners = HashUtil.getHashSet();
		}
		listeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTopicMapListener(ITopicMapListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	/**
	 * @return the listeners
	 */
	public Set<ITopicMapListener> getListeners() {
		if (listeners == null) {
			return Collections.emptySet();
		}
		return listeners;
	}

	/**
	 * Method called by the topic map store to notify all topic map listeners
	 * registered for the given topic map
	 * 
	 * @param event
	 *            the event type
	 * @param notifier
	 *            the construct changed
	 * @param newValue
	 *            the new value
	 * @param oldValue
	 *            the old value
	 */
	public void notifyListeners(TopicMapEventType event, IConstruct notifier, Object newValue, Object oldValue) {
		String id = UUID.randomUUID().toString();
		for (ITopicMapListener listener : getListeners()) {
			listener.topicMapChanged(id, event, notifier, newValue, oldValue);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object doCreate(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support construct creation!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doModify(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support construct modification!");
	}

	// ********************
	// * MERGE OPERATIONS *
	// ********************

	/**
	 * {@inheritDoc}
	 */
	public <T extends Construct> void doMerge(T context, T... others) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support construct merging!");
	}

	// *******************
	// * READ OPERATIONS *
	// *******************

	/**
	 * {@inheritDoc}
	 */
	public Object doRead(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		if (!isConnected()) {
			throw new TopicMapStoreException("Connection is not established");
		}
		switch (paramType) {
		case ROLE_TYPES: {
			if (context instanceof IAssociation) {
				return doReadRoleTypes((IAssociation) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case ROLE: {
			if (context instanceof IAssociation) {
				if (params.length == 0) {
					return doReadRoles((IAssociation) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadRoles((IAssociation) context, (ITopic) params[0]);
				}
			} else if (context instanceof ITopic) {
				if (params.length == 0) {
					return doReadRoles((ITopic) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadRoles((ITopic) context, (ITopic) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof ITopic) {
					return doReadRoles((ITopic) context, (ITopic) params[0], (ITopic) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case PLAYER: {
			if (context instanceof IAssociationRole) {
				return doReadPlayer((IAssociationRole) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case VALUE: {
			if (context instanceof IDatatypeAware) {
				if (params.length == 0) {
					return doReadValue((IDatatypeAware) context);
				} else if (params.length == 1 && params[0] instanceof Class<?>) {
					return doReadValue((IDatatypeAware) context, (Class<?>) params[0]);
				}
			} else if (context instanceof IName) {
				return doReadValue((IName) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case DATATYPE: {
			if (context instanceof IDatatypeAware) {
				return doReadDataType((IDatatypeAware) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case ID: {
			return doReadId(context);
		}
		case ITEM_IDENTIFIER: {
			return doReadItemIdentifiers(context);
		}
		case REIFICATION: {
			if (context instanceof ITopic) {
				return doReadReification((ITopic) context);
			} else if (context instanceof IReifiable) {
				return doReadReification((IReifiable) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case REVISION_TIMESTAMP: {
			if (context == null && params.length == 1 && params[0] instanceof IRevision) {
				return doReadRevisionTimestamp((IRevision) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case NEXT_REVISION: {
			if (context == null && params.length == 1 && params[0] instanceof IRevision) {
				return doReadFutureRevision((IRevision) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case PREVIOUS_REVISION: {
			if (context == null && params.length == 1 && params[0] instanceof IRevision) {
				return doReadPastRevision((IRevision) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case CHANGESET: {
			if (context == null && params.length == 1 && params[0] instanceof IRevision) {
				return doReadChangeSet((IRevision) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case SCOPE: {
			if (context instanceof IScopable) {
				return doReadScope((IScopable) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case ASSOCIATION: {
			if (context instanceof ITopic) {
				if (params.length == 0) {
					return doReadAssociation((ITopic) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadAssociation((ITopic) context, (ITopic) params[0]);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadAssociation((ITopic) context, (IScope) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof IScope) {
					return doReadAssociation((ITopic) context, (ITopic) params[0], (IScope) params[1]);
				}
			} else if (context instanceof ITopicMap) {
				if (params.length == 0) {
					return doReadAssociation((ITopicMap) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadAssociation((ITopicMap) context, (ITopic) params[0]);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadAssociation((ITopicMap) context, (IScope) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof IScope) {
					return doReadAssociation((ITopicMap) context, (ITopic) params[0], (IScope) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case CHARACTERISTICS: {
			if (context instanceof ITopic) {
				if (params.length == 0) {
					return doReadCharacteristics((ITopic) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadCharacteristics((ITopic) context, (ITopic) params[0]);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadCharacteristics((ITopic) context, (IScope) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof IScope) {
					return doReadCharacteristics((ITopic) context, (ITopic) params[0], (IScope) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case NAME: {
			if (context instanceof ITopic) {
				if (params.length == 0) {
					return doReadNames((ITopic) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadNames((ITopic) context, (ITopic) params[0]);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadNames((ITopic) context, (IScope) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof IScope) {
					return doReadNames((ITopic) context, (ITopic) params[0], (IScope) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case OCCURRENCE: {
			if (context instanceof ITopic) {
				if (params.length == 0) {
					return doReadOccurrences((ITopic) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadOccurrences((ITopic) context, (ITopic) params[0]);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadOccurrences((ITopic) context, (IScope) params[0]);
				} else if (params.length == 2 && params[0] instanceof ITopic && params[1] instanceof IScope) {
					return doReadOccurrences((ITopic) context, (ITopic) params[0], (IScope) params[1]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case VARIANT: {
			if (context instanceof IName) {
				if (params.length == 0) {
					return doReadVariants((IName) context);
				} else if (params.length == 1 && params[0] instanceof IScope) {
					return doReadVariants((IName) context, (IScope) params[0]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case TYPE: {
			if (context instanceof ITopic) {
				return doReadTypes((ITopic) context);
			} else if (context instanceof ITypeable) {
				return doReadType((ITypeable) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case SUPERTYPE: {
			if (context instanceof ITopic) {
				return doReadSuptertypes((ITopic) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case SUBJECT_IDENTIFIER: {
			if (context instanceof ITopic) {
				return doReadSubjectIdentifiers((ITopic) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case SUBJECT_LOCATOR: {
			if (context instanceof ITopic) {
				return doReadSubjectLocators((ITopic) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case TOPIC: {
			if (context instanceof ITopicMap) {
				if (params.length == 0) {
					return doReadTopics((ITopicMap) context);
				} else if (params.length == 1 && params[0] instanceof ITopic) {
					return doReadTopics((ITopicMap) context, (ITopic) params[0]);
				}
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_ID: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof String) {
				return doReadConstruct((ITopicMap) context, (String) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_ITEM_IDENTIFER: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doReadConstruct((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_SUBJECT_IDENTIFER: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doReadTopicBySubjectIdentifier((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BY_SUBJECT_LOCATOR: {
			if (context instanceof ITopicMap && params.length == 1 && params[0] instanceof ILocator) {
				return doReadTopicBySubjectLocator((ITopicMap) context, (ILocator) params[0]);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case LOCATOR: {
			if (context instanceof ITopicMap) {
				return doReadLocator((ITopicMap) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case META_DATA: {
			if (context == null && params.length == 1 && params[0] instanceof IRevision) {
				return doReadMetaData((IRevision) params[0]);
			} else if (context == null && params.length == 2 && params[0] instanceof IRevision && params[1] instanceof String) {
				return doReadMetaData((IRevision) params[0], params[1].toString());
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		case BEST_LABEL: {
			if (context instanceof ITopic && params.length == 0) {
				return doReadBestLabel((ITopic) context);
			}
			throw new OperationSignatureException(context, paramType, params);
		}
		}
		throw new OperationSignatureException(context, paramType, params);
	}

	/**
	 * Read the association played by the given topic
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopic t) throws TopicMapStoreException;

	/**
	 * Read the associations played by the given topic and being typed by the
	 * given type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Read all scoped associations played by the given topic and being typed by
	 * the given type
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all scoped associations played by the given topic
	 * 
	 * @param t
	 *            the topic
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all associations of the topic map.
	 * 
	 * @param tm
	 *            the topic map
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopicMap tm) throws TopicMapStoreException;

	/**
	 * Read all associations of the topic map being typed by the given type.
	 * 
	 * @param tm
	 *            the topic map
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws TopicMapStoreException;

	/**
	 * Read all scoped associations of the topic map being typed by the given
	 * type.
	 * 
	 * @param tm
	 *            the topic map
	 * @param type
	 *            the type
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all scoped associations of the topic map.
	 * 
	 * @param tm
	 *            the topic map
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all characteristics if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ICharacteristics> doReadCharacteristics(ITopic t) throws TopicMapStoreException;

	/**
	 * Read all characteristics if the given topic being typed by the given
	 * type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Read all scoped characteristics if the given topic being typed by the
	 * given type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all scoped characteristics if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws TopicMapStoreException;

	/**
	 * Read the construct identified by the given id.
	 * 
	 * @param t
	 *            the topic map
	 * @param id
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IConstruct doReadConstruct(ITopicMap t, String id) throws TopicMapStoreException;

	/**
	 * Read the construct identified by the given item-identifier.
	 * 
	 * @param t
	 *            the topic map
	 * @param itemIdentifier
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws TopicMapStoreException;

	/**
	 * Read the data type of the given data-type-aware
	 * 
	 * @param d
	 *            the data-type-aware
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ILocator doReadDataType(IDatatypeAware d) throws TopicMapStoreException;

	/**
	 * Read the id of the given construct
	 * 
	 * @param c
	 *            the construct
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract String doReadId(IConstruct c) throws TopicMapStoreException;

	/**
	 * Read all item-identifiers of the given construct
	 * 
	 * @param c
	 *            the construct
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ILocator> doReadItemIdentifiers(IConstruct c) throws TopicMapStoreException;

	/**
	 * Read the base locator of the given topic map.
	 * 
	 * @param t
	 *            the topic map
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ILocator doReadLocator(ITopicMap t) throws TopicMapStoreException;

	/**
	 * Read all names if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IName> doReadNames(ITopic t) throws TopicMapStoreException;

	/**
	 * Read all names if the given topic being typed by the given type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IName> doReadNames(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Read all scoped names if the given topic being typed by the given type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all scoped names if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IName> doReadNames(ITopic t, IScope scope) throws TopicMapStoreException;

	/**
	 * Read the previous revision of the revision.
	 * 
	 * @param r
	 *            the revision
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IRevision doReadFutureRevision(IRevision r) throws TopicMapStoreException;

	/**
	 * Read all occurrences if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IOccurrence> doReadOccurrences(ITopic t) throws TopicMapStoreException;

	/**
	 * Read all occurrence if the given topic being typed by the given type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws TopicMapStoreException;

	/**
	 * Read all scoped occurrence if the given topic being typed by the given
	 * type.
	 * 
	 * @param t
	 *            the topic
	 * @param type
	 *            the type
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws TopicMapStoreException;

	/**
	 * Read all scoped occurrence if the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws TopicMapStoreException;

	/**
	 * Read the player of the given role
	 * 
	 * @param role
	 *            the role
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ITopic doReadPlayer(IAssociationRole role) throws TopicMapStoreException;

	/**
	 * Read the past revision of the revision.
	 * 
	 * @param r
	 *            the revision
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IRevision doReadPastRevision(IRevision r) throws TopicMapStoreException;

	/**
	 * Read the reified item of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IReifiable doReadReification(ITopic t) throws TopicMapStoreException;

	/**
	 * Read the reifier of the reified construct
	 * 
	 * @param r
	 *            the reified item
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ITopic doReadReification(IReifiable r) throws TopicMapStoreException;

	/**
	 * Read the time-stamp of the revision begin.
	 * 
	 * @param r
	 *            the revision
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Calendar doReadRevisionTimestamp(IRevision r) throws TopicMapStoreException;

	/**
	 * Read the change set of a revision.
	 * 
	 * @param r
	 *            the revision
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Changeset doReadChangeSet(IRevision r) throws TopicMapStoreException;

	/**
	 * Read all association role items of the given association
	 * 
	 * @param association
	 *            the association
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociationRole> doReadRoles(IAssociation association) throws TopicMapStoreException;

	/**
	 * Read all association role items of the given association and the given
	 * type.
	 * 
	 * @param association
	 *            the association
	 * @param type
	 *            the role type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws TopicMapStoreException;

	/**
	 * Read the played roles of the given topic.
	 * 
	 * @param player
	 *            the role player
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociationRole> doReadRoles(ITopic player) throws TopicMapStoreException;

	/**
	 * Read the played roles of the given topic and being typed by the given
	 * type
	 * 
	 * @param player
	 *            the player
	 * @param type
	 *            the role type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws TopicMapStoreException;

	/**
	 * Read the played roles of the given topic and being typed by the given
	 * type
	 * 
	 * @param player
	 *            the player
	 * @param type
	 *            the role type
	 * @param assocType
	 *            the association type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws TopicMapStoreException;

	/**
	 * Read all role types of the given association
	 * 
	 * @param association
	 *            the association
	 * @return a set containing all role types
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ITopic> doReadRoleTypes(IAssociation association) throws TopicMapStoreException;

	/**
	 * Read all subject-identifiers of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws TopicMapStoreException;

	/**
	 * Read all subject-locators of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ILocator> doReadSubjectLocators(ITopic t) throws TopicMapStoreException;

	/**
	 * Read all super types of the given topic.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Collection<ITopic> doReadSuptertypes(ITopic t) throws TopicMapStoreException;

	/**
	 * Read the topic identified by the given subject-identifier.
	 * 
	 * @param t
	 *            the topic map
	 * @param subjectIdentifier
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws TopicMapStoreException;

	/**
	 * Read the topic identified by the given subject-locator.
	 * 
	 * @param t
	 *            the topic map
	 * @param subjectLocator
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws TopicMapStoreException;

	/**
	 * Return all topics of the topic map.
	 * 
	 * @param t
	 *            the topic map
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ITopic> doReadTopics(ITopicMap t) throws TopicMapStoreException;

	/**
	 * Return all topics of the topic map being typed by the given type.
	 * 
	 * @param t
	 *            the topic map
	 * @param type
	 *            the type
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws TopicMapStoreException;

	/**
	 * Read the type of the given typed item.
	 * 
	 * @param typed
	 *            the typed
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract ITopic doReadType(ITypeable typed) throws TopicMapStoreException;

	/**
	 * Read all types of the given type.
	 * 
	 * @param t
	 *            the topic
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<ITopic> doReadTypes(ITopic t) throws TopicMapStoreException;

	/**
	 * Read the scope of the given scoped item
	 * 
	 * @param s
	 *            the scoped item
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract IScope doReadScope(IScopable s) throws TopicMapStoreException;

	/**
	 * Read the value of the topic name.
	 * 
	 * @param n
	 *            the name
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Object doReadValue(IName n) throws TopicMapStoreException;

	/**
	 * Read the value of the data-type-aware.
	 * 
	 * @param t
	 *            the data-type-aware
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Object doReadValue(IDatatypeAware t) throws TopicMapStoreException;

	/**
	 * Read the value of the data-type-aware and cast them to the given type
	 * 
	 * @param <T>
	 *            the type of the value
	 * @param t
	 *            the data-type-aware
	 * @param type
	 *            the type of the value
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract <T extends Object> T doReadValue(IDatatypeAware t, Class<T> type) throws TopicMapStoreException;

	/**
	 * Read the variants of the given name.
	 * 
	 * @param n
	 *            the name
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IVariant> doReadVariants(IName n) throws TopicMapStoreException;

	/**
	 * Read the variants of the given name in the given scope.
	 * 
	 * @param n
	 *            the name
	 * @param scope
	 *            the scope
	 * @return the read information
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Set<IVariant> doReadVariants(IName n, IScope scope) throws TopicMapStoreException;

	/**
	 * Read the whole meta data sets of the given revision
	 * 
	 * @param revision
	 *            the revision
	 * @return a map containing all key-value-pairs of the revisions meta data
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract Map<String, String> doReadMetaData(IRevision revision) throws TopicMapStoreException;

	/**
	 * Read the value of the meta set of the given revision identified by the
	 * given key.
	 * 
	 * @param revision
	 *            the revision
	 * @param key
	 *            the key
	 * @return the value of the revision
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	public abstract String doReadMetaData(IRevision revision, final String key) throws TopicMapStoreException;

	/**
	 * Returns the best label for the current topic instance. The best label
	 * will be identified satisfying the following rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other
	 * types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other
	 * scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than
	 * others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than
	 * others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the
	 * lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the
	 * lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the
	 * lexicographically smallest reference are returned.
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
	public abstract String doReadBestLabel(ITopic topic) throws TopicMapStoreException;

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, TopicMapStoreParameterType paramType, Object... params) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support construct deletion!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void doRemove(IConstruct context, boolean cascade) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support deletion of construct content!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeDuplicates() {
		throw new UnsupportedOperationException("Read-only store does not support deletion of construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		throw new UnsupportedOperationException("Read-only store does not support deletion of construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstructFactory getConstructFactory() {
		throw new UnsupportedOperationException("Read-only store does not support creation of construct!");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransactable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementEnabled() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRevisionManagementSupported() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commit() {
		throw new UnsupportedOperationException("Read-only store does not support transactions!");
	}

	/**
	 * {@inheritDoc}
	 */
	public ITransaction createTransaction() {
		throw new UnsupportedOperationException("Read-only store does not support transactions!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void enableRevisionManagement(boolean enabled) throws TopicMapStoreException {
		throw new UnsupportedOperationException("Read-only store does not support revision management!");
	}

	/**
	 * {@inheritDoc}
	 */
	public void connect() throws TopicMapStoreException {
		this.connected = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws TopicMapStoreException {
		this.connected = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isConnected() {
		return connected;
	}
}