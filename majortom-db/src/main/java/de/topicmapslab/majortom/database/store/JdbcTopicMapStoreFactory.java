/**
 * 
 */
package de.topicmapslab.majortom.database.store;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.ITopicMapStoreFactory;

/**
 * Factory for the topicmapstore service
 * 
 * @author Hannes Niederhausen
 *
 */
public class JdbcTopicMapStoreFactory implements ITopicMapStoreFactory{

	@Override
	public String getClassName() {
		return JdbcTopicMapStore.class.getName();
	}

	@Override
	public ITopicMapStore newTopicMapStore(ITopicMapSystem arg0) {
		return new JdbcTopicMapStore(arg0);
	}

	
}
