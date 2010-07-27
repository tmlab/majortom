package de.topicmapslab.majortom.model.revision;

import java.util.Calendar;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Interface definition of a construct which can be have revisions. An instance
 * of this interface representing one revision in the history of the topic map
 * construct.
 * 
 * @author Sven Krosse
 * 
 */
public interface IRevision {

	/**
	 * Returns the internal revision number
	 * 
	 * @return the numeric value
	 */
	long getId();

	/**
	 * Returns the past revision of this construct.
	 * 
	 * <p>
	 * Special case:<br />
	 * If the current revision is the oldest revision of this construct,
	 * <code>null</code> will be returned.
	 * </p>
	 * 
	 * @return the past revision or <code>null</code>
	 */
	public IRevision getPast();

	/**
	 * Return the future revision of this revision.
	 * <p>
	 * Special case:<br />
	 * If the current revision is the newest revision of this construct,
	 * <code>null</code> will be returned.
	 * </p>
	 * 
	 * @return the future revision or <code>null</code>
	 */
	public IRevision getFuture();

	/**
	 * Returns the creation time of this revision
	 * 
	 * @return the creation time
	 */
	public Calendar getTimestamp();

	/**
	 * Returns all changes of the change set
	 * 
	 * @return a list containing all changes sorted by the happening time.
	 */
	public Changeset getChangeset();

	/**
	 * Exports the information of this history item as XML node.
	 * 
	 * @param doc
	 *            the parent document
	 * @return the node
	 */
	public Node toXml(Document doc);

	/**
	 * Add a new data set to the meta data of this revision.
	 * 
	 * @param key
	 *            the key of the meta data set
	 * @param value
	 *            the value of the meta data set
	 */
	public void addMetaData(final String key, final String value);

	/**
	 * Returns the value of the meta data set identified by the given key.
	 * 
	 * @param key
	 *            the key
	 * @return the value or <code>null</code> if the key is unknown
	 */
	public String getMetaData(final String key);

	/**
	 * Returns the whole meta data values
	 * 
	 * @return a map containing all meta data values
	 */
	public Map<String, String> getMetadata();
}
