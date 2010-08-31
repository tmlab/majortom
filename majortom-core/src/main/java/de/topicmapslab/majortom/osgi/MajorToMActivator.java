/**
 * 
 */
package de.topicmapslab.majortom.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import de.topicmapslab.majortom.model.store.ITopicMapStoreFactory;

/**
 * Activator which loads the store servie informations
 * 
 * @author Hannes Niederhausen
 *
 */
public class MajorToMActivator implements BundleActivator {

	private static MajorToMActivator plugin;
	private BundleContext context;
	
	/**
	 * This method is called by the OSGi framework on activating the bundle.
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		MajorToMActivator.plugin = this;
	}


	/**
	 * This method is called when the bundle is stopped.
	 */
	public void stop(BundleContext context) throws Exception {
		MajorToMActivator.plugin = null;
	}

	/**
	 * This method retrieves all services which implement the {@link ITopicMapStoreFactory} interface and
	 * returns the list of the found implementations.
	 * 
	 * @return a list of {@link ITopicMapStoreFactory} implementations
	 * @throws InvalidSyntaxException
	 */
	public List<ITopicMapStoreFactory> getTopicMapStoreFactories() throws InvalidSyntaxException {
		List<ITopicMapStoreFactory> result = new ArrayList<ITopicMapStoreFactory>();
		ServiceReference[] refs = context.getServiceReferences(ITopicMapStoreFactory.class.getName(), null);
		if (refs==null)
			return Collections.emptyList();
		for (ServiceReference ref : refs) {
			result.add((ITopicMapStoreFactory) context.getService(ref));
		}
		
		return result;
	}

	/**
	 * Returns the instance of this activator
	 * @return the activator of the bundle ot <code>null</code> if the bundle is not active
	 */
	public static MajorToMActivator getDefault() {
		return plugin;
	}

	

}
