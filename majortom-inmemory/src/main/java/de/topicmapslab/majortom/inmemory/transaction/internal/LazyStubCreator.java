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
package de.topicmapslab.majortom.inmemory.transaction.internal;

import de.topicmapslab.majortom.core.AssociationImpl;
import de.topicmapslab.majortom.core.AssociationRoleImpl;
import de.topicmapslab.majortom.core.ConstructImpl;
import de.topicmapslab.majortom.core.NameImpl;
import de.topicmapslab.majortom.core.OccurrenceImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.core.VariantImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryIdentity;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.transaction.ITransaction;

/**
 * @author Sven Krosse
 * 
 */
public class LazyStubCreator {

	/**
	 * Creating a lazy stub of the given construct
	 * 
	 * @param <T>
	 *            the construct type
	 * @param construct
	 *            the construct
	 * @param transaction
	 *            the new topic map
	 * @return the lazy stub
	 */
	@SuppressWarnings("unchecked")
	public static final <T extends IConstruct> T createLazyStub(T construct, ITransaction transaction) {
		if (construct instanceof ITopic) {
			return (T) createLazyStub((ITopic) construct, transaction);
		} else if (construct instanceof IOccurrence) {
			return (T) createLazyStub((IOccurrence) construct, transaction);
		} else if (construct instanceof IName) {
			return (T) createLazyStub((IName) construct, transaction);
		} else if (construct instanceof IVariant) {
			return (T) createLazyStub((IVariant) construct, transaction);
		} else if (construct instanceof IAssociation) {
			return (T) createLazyStub((IAssociation) construct, transaction);
		} else if (construct instanceof IAssociationRole) {
			return (T) createLazyStub((IAssociationRole) construct, transaction);
		} else if (construct instanceof ITopicMap) {
			return (T) transaction;
		}
		throw new IllegalArgumentException("Unknown construct type.");
	}

	private static final ITopic createLazyStub(ITopic construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new TopicImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), transaction);
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static final IOccurrence createLazyStub(IOccurrence construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new OccurrenceImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), createLazyStub(construct.getParent(), transaction));
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static final IName createLazyStub(IName construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new NameImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), createLazyStub(construct.getParent(), transaction));
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static final IVariant createLazyStub(IVariant construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new VariantImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), createLazyStub(construct.getParent(), transaction));
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static final IAssociation createLazyStub(IAssociation construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new AssociationImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), transaction);
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static final IAssociationRole createLazyStub(IAssociationRole construct, final ITransaction transaction) {
		if (construct instanceof ConstructImpl) {
			return new AssociationRoleImpl(cloneIdentity(((ConstructImpl) construct).getIdentity()), createLazyStub(construct.getParent(), transaction));
		}
		throw new IllegalArgumentException("construct should be an instanceof ConstructImpl");
	}

	private static ITopicMapStoreIdentity cloneIdentity(ITopicMapStoreIdentity identity) {
		return new InMemoryIdentity(identity.getId());
	}

}
