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
package de.topicmapslab.majortom.inmemory.index;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.w3c.dom.Document;

import de.topicmapslab.majortom.index.IndexImpl;
import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStore;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;

/**
 * Implementation of {@link IRevisionIndex}
 * 
 * @author Sven Krosse
 * 
 */
public class InMemoryRevisionIndex extends IndexImpl<InMemoryTopicMapStore> implements IRevisionIndex {

	/**
	 * @param store
	 */
	public InMemoryRevisionIndex(InMemoryTopicMapStore store) {
		super(store);
	}

	/**
	 * {@inheritDoc}
	 */
	public Changeset getChangeset(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null || !getTopicMapStore().getTopicMap().equals(topic.getParent())) {
			throw new IndexException("Topic is not part of this topic map!");
		}
		return getTopicMapStore().getRevisionStore().getChangeset((ITopic) topic);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getFirstRevision() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicMapStore().getRevisionStore().getFirstRevision();
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getLastModification() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicMapStore().getRevisionStore().getLastModification();
	}

	/**
	 * {@inheritDoc}
	 */
	public Calendar getLastModification(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null || !getTopicMapStore().getTopicMap().equals(topic.getParent())) {
			throw new IndexException("Topic is not part of this topic map!");
		}
		return getTopicMapStore().getRevisionStore().getLastModification((ITopic) topic);
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getLastRevision() {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		return getTopicMapStore().getRevisionStore().getLastRevision();
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(Calendar timestamp) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (timestamp == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			return getTopicMapStore().getRevisionStore().getRevision(timestamp);
		} catch (TopicMapStoreException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(String tag) throws IndexException {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (tag == null) {
			throw new IllegalArgumentException("Argument cannot be null!");
		}
		try {
			return getTopicMapStore().getRevisionStore().getRevision(tag);
		} catch (TopicMapStoreException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRevision getRevision(long id) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		try {
			return getTopicMapStore().getRevisionStore().getRevision(id);
		} catch (TopicMapStoreException e) {
			throw new IndexException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> getRevisions(Topic topic) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (topic == null || !getTopicMapStore().getTopicMap().equals(topic.getParent())) {
			throw new IndexException("Topic is not part of this topic map!");
		}
		return getTopicMapStore().getRevisionStore().getRevisions((ITopic) topic);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void toXml(File file) throws IndexException {
		try {
			Document doc = getTopicMapStore().getRevisionStore().toXml();

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result result = new StreamResult(file);
			transformer.transform(new DOMSource(doc), result);

		} catch (Exception e) {
			throw new TopicMapStoreException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Changeset getAssociationChangeset(Topic associationType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (associationType == null || !getTopicMapStore().getTopicMap().equals(associationType.getParent())) {
			throw new IndexException("Topic is not part of this topic map!");
		}
		return getTopicMapStore().getRevisionStore().getAssociationChangeset((ITopic) associationType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<IRevision> getAssociationRevisions(Topic associationType) {
		if (!isOpen()) {
			throw new TMAPIRuntimeException("Index is closed!");
		}
		if (associationType == null || !getTopicMapStore().getTopicMap().equals(associationType.getParent())) {
			throw new IndexException("Topic is not part of this topic map!");
		}
		return getTopicMapStore().getRevisionStore().getAssociationRevisions((ITopic) associationType);
	}
}
