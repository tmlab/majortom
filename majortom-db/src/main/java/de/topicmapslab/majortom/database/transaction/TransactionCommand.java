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
package de.topicmapslab.majortom.database.transaction;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.ConstructRemovedException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TransactionCommand {

	/**
	 * the operation type
	 */
	private final TransactionOperation operation;
	/**
	 * the context of operation
	 */
	private final IConstruct context;
	/**
	 * the parameter type
	 */
	private final TopicMapStoreParameterType paramterType;
	/**
	 * the parameters
	 */
	private final Object[] parameters;

	/**
	 * the result of operation
	 */
	private final Object result;

	public TransactionCommand(final ITransaction transaction, final Object result, final TransactionOperation operation, final IConstruct context,
			final TopicMapStoreParameterType parameterType, final Object... parameters) {
		this.operation = operation;
		this.context = LazyStubCreator.createLazyStub(context, transaction);
		this.paramterType = parameterType;
		this.parameters = parameters;
		this.result = result;
	}

	/**
	 * Commit the transaction store to the given topic map store.
	 * 
	 * @param store
	 *            the topic map store
	 * @param lazy
	 *            a lazy map containing the mapping between the transaction
	 *            stubs and the real construct
	 * @return the created item if operation is
	 *         {@link TransactionOperation#CREATE}, <code>null</code> otherwise.
	 * @throws TopicMapStoreException
	 *             thrown if operation fails
	 */
	// TODO scope objects
	public Object commit(ITopicMapStore store, Map<Object, Object> lazy) throws TopicMapStoreException {
		IConstruct context_ = (IConstruct) cleanParameter(store, context, lazy);
		Object[] parameters_ = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters_[i] = cleanParameter(store, parameters[i], lazy);
		}

		switch (operation) {
		case CREATE: {
			return store.doCreate(context_, paramterType, parameters_);
		}
		case MERGE: {
			store.doMerge(context_, Arrays.copyOfRange(parameters_, 0, parameters_.length, IConstruct[].class));
		}
			break;
		case MODIFY: {
			store.doModify(context_, paramterType, parameters_);
		}
			break;
		case REMOVE: {
			if (paramterType == null) {
				boolean cascade = parameters_.length == 1 && ((Boolean) parameters_[0]);
				store.doRemove(context_, cascade);
				if (context_ instanceof ConstructImpl) {
					((ConstructImpl) context_).setRemoved(true);
				}
			} else {
				store.doRemove(context_, paramterType, parameters_);
			}
		}
			break;
		}
		return null;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	private final Object cleanParameter(ITopicMapStore store, Object parameter, Map<Object, Object> lazy) {
		if (parameter == null) {
			return null;
		}
		if (lazy.containsKey(parameter)) {
			return lazy.get(parameter);
		}
		if (parameter instanceof IConstruct) {
			if (((IConstruct) parameter).getTopicMap() instanceof ITransaction) {
				IConstruct param = (IConstruct) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_ID, ((IConstruct) parameter).getId());
				if (param == null) {
					try {
						for (Locator loc : ((IConstruct) parameter).getItemIdentifiers()) {
							param = (IConstruct) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_ITEM_IDENTIFER, loc);
							if (param != null) {
								return param;
							}
						}
						if (parameter instanceof ITopic) {
							for (Locator loc : ((ITopic) parameter).getSubjectIdentifiers()) {
								param = (ITopic) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_IDENTIFER, loc);
								if (param != null) {
									return param;
								}
							}
							for (Locator loc : ((ITopic) parameter).getSubjectLocators()) {
								param = (ITopic) store.doRead(store.getTopicMap(), TopicMapStoreParameterType.BY_SUBJECT_LOCATOR, loc);
								if (param != null) {
									return param;
								}
							}
						}
					} catch (ConstructRemovedException e) {
						// NOTHING TO DO
					}
				}
				return param;
			}
		} else if (parameter.getClass().isArray()) {
			Set<Topic> topics = HashUtil.getHashSet();
			for (Object p : (Object[]) parameter) {
				if (p instanceof Topic) {
					topics.add((Topic) cleanParameter(store, p, lazy));
				}
			}
			return topics.toArray(new Topic[0]);
		} else if (parameter instanceof IScope) {
			return cleanParameter(store, ((IScope) parameter).getThemes(), lazy);
		}
		return parameter;
	}

}
