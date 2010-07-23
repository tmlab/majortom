package de.topicmapslab.majortom.model.revision;

import java.util.Calendar;

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
	public Calendar getBegin();

	/**
	 * Returns the end timestamp of this revision.
	 * 
	 * @return the end timestamp
	 */
	public Calendar getEnd();

	/**
	 * Returns all changes of the change set
	 * 
	 * @return a list containing all changes sorted by the happening time.
	 */
	public Changeset getChangeset();
	
	/**
	 * Exports the information of this history item as XML node.
	 * 
	 * @param doc the parent document
	 * @return the node
	 */
	public Node toXml(Document doc);
}
