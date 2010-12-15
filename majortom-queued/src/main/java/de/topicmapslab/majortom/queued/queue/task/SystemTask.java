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
package de.topicmapslab.majortom.queued.queue.task;

import de.topicmapslab.majortom.model.core.IConstruct;

/**
 * @author Sven Krosse
 * 
 */
public abstract class SystemTask extends QueueTaskImpl {

	/**
	 * @param context
	 * @param parameterType
	 */
	public SystemTask(IConstruct context) {
		super(context, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getResult() {
		return null;
	}

}
