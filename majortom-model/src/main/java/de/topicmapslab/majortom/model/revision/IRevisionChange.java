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
package de.topicmapslab.majortom.model.revision;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.event.TopicMapEventType;

/**
 * Interface definition of one atomifc change.
 * 
 * @author Sven Krosse
 * 
 */
public interface IRevisionChange {

	/**
	 * Returns the old value before the changing was executed.
	 * 
	 * @return the old value
	 */
	Object getOldValue();

	/**
	 * Returns the new value after the changing was executed.
	 * 
	 * @return the new value
	 */
	Object getNewValue();

	/**
	 * Returns the context of the modified value. If a construct was created
	 * or removed the context is the parent of this construct.
	 * 
	 * @return the context
	 */
	IConstruct getContext();

	/**
	 * Return the kind of change.
	 * 
	 * @return the operation type
	 */
	TopicMapEventType getType();

	/**
	 * Exports the information of this history item as XML node.
	 * 
	 * @param doc
	 *            the parent document
	 * @return the node
	 */
	public Node toXml(Document doc);

	/**
	 * Returning the parent revision
	 * 
	 * @return the revision
	 */
	IRevision getRevision();
}
