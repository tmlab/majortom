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

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Changes set representing a set of changes.
 * 
 * @author Sven Krosse
 * 
 */
public class Changeset extends LinkedList<IRevisionChange> {

	private static final long serialVersionUID = 0L;

	/**
	 * Exports the information of this history item as XML node.
	 * 
	 * @param doc
	 *            the parent document
	 * @return the node
	 */
	public Node toXml(Document doc) {
		Node changeset = doc.createElement("changeset");

		for (IRevisionChange revisionChange : this) {
			changeset.appendChild(revisionChange.toXml(doc));
		}

		return changeset;
	}

}
