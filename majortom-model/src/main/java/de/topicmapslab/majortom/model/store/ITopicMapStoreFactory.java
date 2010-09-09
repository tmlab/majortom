/**
 * 
 */
package de.topicmapslab.majortom.model.store;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;

/**
 * Specifies a factory interface for {@link ITopicMapStore}implementations.
 * 
 * @author Hannes Niederhausen
 *
 */
public interface ITopicMapStoreFactory {

	/**
	 * Creates a new {@link ITopicMapStore} instance.
	 * 
	 * @param tmSystem the topic map system which uses the store
	 * @return a new instance of {@link ITopicMapStore}
	 */
	public ITopicMapStore newTopicMapStore(ITopicMapSystem tmSystem);
	
	/**
	 * 
	 * @return the qualified name of the class which will be created
	 */
	public String getClassName();
}
