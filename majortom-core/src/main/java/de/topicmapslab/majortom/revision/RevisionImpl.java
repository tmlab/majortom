/*******************************************************************************
 * Copyright 2010, Topic Maps Lab ( http://www.topicmapslab.de )
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * Base implementation of {@link IRevision}
 */
public abstract class RevisionImpl implements IRevision, Comparable<IRevision> {

	private final ITopicMapStore store;
	private final long id;

	/**
	 * constructor
	 * 
	 * @param store the parent store
	 * @param id the version number
	 */
	public RevisionImpl(ITopicMapStore store, long id) {
		this.store = store;
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getBegin() {
		return (Calendar) store.doRead(null, TopicMapStoreParameterType.REVISION_BEGIN, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getEnd() {
		return (Calendar) store.doRead(null, TopicMapStoreParameterType.REVISION_END, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getFuture() {
		return (IRevision) store.doRead(null, TopicMapStoreParameterType.NEXT_REVISION, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getPast() {
		return (IRevision) store.doRead(null, TopicMapStoreParameterType.PREVIOUS_REVISION, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset getChangeset() {
		return (Changeset) store.doRead(null, TopicMapStoreParameterType.CHANGESET, this);
	}

	/**
	 * {@inheritDoc}
	 */
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(IRevision o) {
		return (int) (getId() - o.getId());
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public Node toXml(Document doc) {
		Node revision = doc.createElement("revision");
		Node attId = doc.createAttribute("id");
		attId.setTextContent(Long.toString(getId()));
		revision.getAttributes().setNamedItem(attId);

		Node attTime = doc.createAttribute("timestamp");
		attTime.setTextContent(new SimpleDateFormat().format(getBegin().getTime()));
		revision.getAttributes().setNamedItem(attTime);

		revision.appendChild(getChangeset().toXml(doc));
		return revision;
	}

}
