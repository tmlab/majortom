/**
 * 
 */
package de.topicmapslab.majortom.inmemory.store;

import de.topicmapslab.majortom.model.core.ITopicMapSystem;
import de.topicmapslab.majortom.model.store.ITopicMapStore;
import de.topicmapslab.majortom.model.store.ITopicMapStoreFactory;

/**
 * @author Hannes Niederhausen
 *
 */
public class InMemoryTopicMapStoreFactory implements ITopicMapStoreFactory {

	public ITopicMapStore newTopicMapStore(ITopicMapSystem tmSystem) {
		return new InMemoryTopicMapStore(tmSystem);
	}
	
	public String getClassName() {
		return InMemoryTopicMapStore.class.getName();
	}

}
