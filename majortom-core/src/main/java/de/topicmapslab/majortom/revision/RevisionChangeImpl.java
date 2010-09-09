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
package de.topicmapslab.majortom.revision;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.revision.IRevisionChange;

/**
 * Base implementation of {@link IRevisionChange}
 * 
 * @author Sven Krosse
 * 
 */
public class RevisionChangeImpl implements IRevisionChange {

	private final IRevision revision;
	private final Object oldValue;
	private final Object newValue;
	private final IConstruct context;
	private final TopicMapEventType type;

	/**
	 * constructor
	 * 
	 * @param revision the revision
	 * @param type the kind of change
	 * @param context the context of change
	 * @param newValue the new value
	 * @param oldValue the old value
	 */
	public RevisionChangeImpl(final IRevision revision, final TopicMapEventType type, final IConstruct context, final Object newValue, final Object oldValue) {
		this.revision = revision;
		this.type = type;
		this.context = context;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public IConstruct getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	public TopicMapEventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Type:\t" + type.name() + "\r\n");
		builder.append("Context:\t" + (context == null ? "null" : context.toString()) + "\r\n");
		builder.append("New value:\t" + (newValue == null ? "null" : newValue.toString()) + "\r\n");
		builder.append("Old value:\t" + (oldValue == null ? "null" : oldValue.toString()) + "\r\n");
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public Node toXml(Document doc) {
		Node change = doc.createElement("change");

		Node node = doc.createElement("type");
		node.appendChild(doc.createTextNode(type.name()));
		change.appendChild(node);

		node = doc.createElement("context");
		node.appendChild(doc.createTextNode((context == null ? "null" : context.toString())));
		change.appendChild(node);

		node = doc.createElement("newValue");
		node.appendChild(doc.createTextNode((newValue == null ? "null" : newValue.toString())));
		change.appendChild(node);

		node = doc.createElement("oldValue");
		node.appendChild(doc.createTextNode((oldValue == null ? "null" : oldValue.toString())));
		change.appendChild(node);

		return change;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision() {
		return revision;
	}
}
