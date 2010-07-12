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
package de.topicmapslab.majortom.revision.core;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Read only implementation of {@link IReifiable}
 * 
 * @author Sven Krosse
 * 
 */
public class ReadOnlyReifiable extends ReadOnlyConstruct implements IReifiable {

	private final String reifierId;

	/*
	 * cached values
	 */
	private Topic cachedReifier;

	/**
	 * constructor
	 * 
	 * @param clone the construct to clone
	 */
	public ReadOnlyReifiable(IReifiable clone) {
		super(clone);
		if (clone.getReifier() != null) {
			reifierId = clone.getReifier().getId();
		} else {
			reifierId = null;
		}

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
		if (reifier instanceof ReadOnlyTopic) {
			cachedReifier = reifier;
		}
		return reifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReifier(Topic arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

}
