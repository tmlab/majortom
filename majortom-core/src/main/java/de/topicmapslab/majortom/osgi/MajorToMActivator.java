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
	 * {@inheritDoc}
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		MajorToMActivator.plugin = this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop(BundleContext context) throws Exception {
	}

	/**
	 * Hannes kommentiert das nachher @TODO, XXX
	 * @return
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
	 * Hannes kommentiert das nachher @TODO, XXX
	 */
	public static MajorToMActivator getDefault() {
		return plugin;
	}

	

}
