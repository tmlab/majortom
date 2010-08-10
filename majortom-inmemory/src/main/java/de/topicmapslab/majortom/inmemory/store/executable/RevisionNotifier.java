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
package de.topicmapslab.majortom.inmemory.store.executable;

import de.topicmapslab.majortom.inmemory.store.revision.RevisionStore;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.revision.IRevision;

/**
 * @author Sven Krosse
 * 
 */
public class RevisionNotifier implements Runnable {

	private final RevisionStore store;
	private final IRevision revision;
	private final TopicMapEventType type;
	private final IConstruct context;
	private final Object newValue;
	private final Object oldValue;

	/**
	 * 
	 */
	public RevisionNotifier(final RevisionStore store, final IRevision revision, TopicMapEventType type, IConstruct context, Object newValue, Object oldValue) {
		this.store = store;
		this.revision = revision;
		this.type = type;
		this.context = context;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		store.addChange(revision, type, context, newValue, oldValue);
	}

}
