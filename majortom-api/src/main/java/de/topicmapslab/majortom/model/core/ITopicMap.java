package de.topicmapslab.majortom.model.core;

import java.util.Calendar;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.transaction.ITransaction;

/**
 * Interface definition of a topic map item.
 * <p>
 * A topic map is a set of topics and associations. Its purpose is to convey
 * information about subjects through statements about topics representing those
 * subjects. The topic map itself has no meaning or significance beyond its use
 * as a container for the information about those subjects.
 * </p>
 * 
 * @author Sven Krosse
 * 
 */
public interface ITopicMap extends TopicMap, IConstruct, IReifiable {

	/**
	 * Returns all topics being an instance of the given type.
	 * 
	 * @param type the topic type
	 * @return the topics
	 */
	public <T extends Topic> Collection<T> getTopics(Topic type);

	/**
	 * Returns all associations of the current topic map being an instance of
	 * the given type
	 * 
	 * @param type the association type
	 * @return the associations
	 */
	public <T extends Association> Collection<T> getAssociations(Topic type);

	/**
	 * Returns all associations of the current topic map being valid in the
	 * given scope
	 * 
	 * @param scope the scope
	 * @return the associations
	 */
	public <T extends Association> Collection<T> getAssociations(IScope scope);

	/**
	 * Returns all associations of the current topic map being an instance of
	 * the given type and being valid in the given scope.
	 * 
	 * @param type the association type
	 * @param scope the scope
	 * @return the associations
	 */
	public <T extends Association> Collection<T> getAssociations(Topic type, IScope scope);

	/**
	 * Add new tag name to the current time-stamp. The tag can be used instead
	 * of the time-stamp to access revisions.
	 * 
	 * @see #addTag(String, Calendar)
	 * @param name the tag name
	 */
	public void addTag(final String name);

	/**
	 * Add new tag name to the given time-stamp. The tag can be used instead of
	 * the time-stamp to access revisions.
	 * 
	 * @param name the tag name
	 * @param timestamp the timestamp
	 */
	public void addTag(final String name, final Calendar timestamp);

	/**
	 * Registers the listener to the topic map.
	 * 
	 * @param listener the listener to register
	 */
	public void addTopicMapListener(ITopicMapListener listener);

	/**
	 * Removes the listener to the topic map.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeTopicMapListener(ITopicMapListener listener);

	/**
	 * Returns the internal store instance handling data storage of topic map
	 * constructs.
	 * 
	 * @return the store instance
	 */
	public ITopicMapStore getStore();

	/**
	 * Creating a new transaction.
	 * 
	 * @return the created transaction
	 */
	public ITransaction createTransaction();

	/**
	 * Returns the topic map system of the current topic map.
	 * 
	 * @return the topic map system
	 */
	public ITopicMapSystem getTopicMapSystem();

	/**
	 * Return the scope object representing the scope containing all this
	 * themes.
	 * 
	 * @param themes the themes
	 * @return the scope object
	 */
	public IScope createScope(Topic... themes);
	
	/**
	 * Return the scope object representing the scope containing all this
	 * themes.
	 * 
	 * @param themes the themes
	 * @return the scope object
	 */
	public IScope createScope(Collection<Topic> themes);
}
