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

import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;
import de.topicmapslab.majortom.model.store.TopicMapStoreParamterType;

/**
 * Base implementation of {@link IOccurrence}
 * 
 * @author Sven Krosse
 * 
 */
public class OccurrenceImpl extends DataTypeAwareImpl implements IOccurrence {

	/**
	 * constructor
	 * 
	 * @param identity the {@link ITopicMapStoreIdentity}
	 * @param parent the parent topic
	 */
	public OccurrenceImpl(ITopicMapStoreIdentity identity, ITopic parent) {
		super(identity, parent.getTopicMap(), parent);
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
	public void setType(Topic type) {
		if ( type == null ){
			throw new ModelConstraintException(this,"Type cannot be null.");
		}
		if ( !type.getTopicMap().equals(getTopicMap())){
			throw new ModelConstraintException(type, "Type has to be a topic of the same topic map.");
		}
		getTopicMap().getStore().doModify(this, TopicMapStoreParamterType.TYPE, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		return (Topic) getTopicMap().getStore().doRead(this, TopicMapStoreParamterType.TYPE);
	}
}
