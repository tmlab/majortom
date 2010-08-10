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
package de.topicmapslab.majortom.executable;

import java.util.Set;
import java.util.UUID;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;

/**
 * @author Sven Krosse
 * 
 */
public class EventNotifier implements Runnable {

	private final Set<ITopicMapListener> listeners;
	private final TopicMapEventType event;
	private final IConstruct notifier;
	private final Object newValue;
	private final Object oldValue;

	/**
	 * 
	 */
	public EventNotifier(final Set<ITopicMapListener> listeners, TopicMapEventType event, IConstruct notifier, Object newValue, Object oldValue) {
		this.listeners = listeners;
		this.event = event;
		this.notifier = notifier;
		this.newValue = newValue;
		this.oldValue = oldValue;		
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		if (listeners != null) {
			String id = UUID.randomUUID().toString();
			for (ITopicMapListener listener : listeners) {
				listener.topicMapChanged(id, event, notifier, newValue, oldValue);
			}
		}
	}

}
