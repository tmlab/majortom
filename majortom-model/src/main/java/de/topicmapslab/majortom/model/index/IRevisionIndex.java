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
package de.topicmapslab.majortom.model.index;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.exception.IndexException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;

/**
 * Interface definition of an index handling the access to the different
 * revisions of the topic map constructs.
 * 
 * @author Sven Krosse
 * 
 */
public interface IRevisionIndex extends IIndex {

	/**
	 * Method returns the last revision of the topic map.
	 * 
	 * @return the last revision
	 */
	public IRevision getLastRevision();

	/**
	 * Method returns the first revision of the topic map.
	 * 
	 * @return the first revision
	 */
	public IRevision getFirstRevision();

	/**
	 * Method returns the newest revision which was valid until the given
	 * time-stamp.
	 * 
	 * @param timestamp
	 *            the times-stamp
	 * @return the revision or <code>null</code>
	 */
	public IRevision getRevision(final Calendar timestamp);

	/**
	 * Method returns the newest revision which was valid until the given
	 * time-stamp symbolized by the given tag.
	 * 
	 * @param tag
	 *            the tag symbolizing the end of the validation range
	 * @return the revision or <code>null</code>
	 * @throws IndexException
	 *             thrown if the given tag is unknown
	 */
	public IRevision getRevision(final String tag) throws IndexException;

	/**
	 * Method returns the revision with the given id.
	 * 
	 * @param id
	 *            the id
	 * @return the revision or <code>null</code>
	 */
	public IRevision getRevision(final long id);

	/**
	 * Returns the last modification time stamp of the topic map construct
	 * 
	 * @return the time stamp
	 */
	public Calendar getLastModification();

	/**
	 * Returns the last modification time stamp of the given topic
	 * 
	 * @return the time stamp
	 */
	public Calendar getLastModification(Topic topic);

	/**
	 * Returns a list containing all revisions of the given topic
	 * 
	 * @param topic
	 *            the topic
	 * @return all revisions
	 */
	public List<IRevision> getRevisions(Topic topic);

	/**
	 * Returns the change set of the given topic
	 * 
	 * @param topic
	 *            the topic
	 * @return the change set
	 */
	public Changeset getChangeset(Topic topic);

	/**
	 * Returns a list containing all revisions of all association items of the
	 * specified type.
	 * 
	 * @param associationType
	 *            the association type
	 * @return all revisions
	 */
	public List<IRevision> getAssociationRevisions(Topic associationType);

	/**
	 * Returns the change set of all association items of the specified type
	 * 
	 * @param associationType
	 *            the association type
	 * @return the change set
	 */
	public Changeset getAssociationChangeset(Topic associationType);

	/**
	 * Converts the internal history to a XML document and store them to the
	 * given file.
	 * 
	 * @param file
	 *            the file
	 * @throws IndexException
	 *             thrown if write operation fails
	 */
	public void toXml(File file) throws IndexException;

}
