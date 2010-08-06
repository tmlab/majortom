/**
 * 
 */
package de.topicmapslab.majortom.inmemory.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.topicmapslab.majortom.inmemory.store.InMemoryTopicMapStoreFactory;
import de.topicmapslab.majortom.model.store.ITopicMapStoreFactory;

/**
 * Activator which registers the topic map store as service.
 * 
 * @author Hannes Niederhausen
 *
 */
public class InMemoryActivator implements BundleActivator {

	private InMemoryTopicMapStoreFactory storeFactory;
	private ServiceRegistration registerService;

	@Override
	public void start(BundleContext context) throws Exception {
		storeFactory = new InMemoryTopicMapStoreFactory();
		registerService = context.registerService(ITopicMapStoreFactory.class.getName(), storeFactory, null);
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		registerService.unregister();
	}

}
