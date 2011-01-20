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

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link IReifiable}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ReifiableImpl extends ConstructImpl implements IReifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7359295275519843778L;

	/**
	 * constructor
	 * 
	 * @param identity
	 *            the {@link ITopicMapStoreIdentity}
	 * @param topicMap
	 *            the topic map
	 * @param parent
	 *            the parent construct
	 */
	protected ReifiableImpl(ITopicMapStoreIdentity identity, ITopicMap topicMap, IConstruct parent) {
		super(identity, topicMap, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		return (Topic) getTopicMap().getStore().doRead(this, TopicMapStoreParameterType.REIFICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReifier(Topic reifier) throws ModelConstraintException {
		if ( reifier != null && !reifier.getTopicMap().equals(getTopicMap())){
			throw new ModelConstraintException(reifier, "Reifier has to be a topic of the same topic map.");
		}		
		getTopicMap().getStore().doModify(this, TopicMapStoreParameterType.REIFICATION, reifier);
	}

}
