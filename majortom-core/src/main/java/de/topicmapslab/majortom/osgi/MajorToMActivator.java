/**
 * 
 */
package de.topicmapslab.majortom.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.Bundle;
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
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		MajorToMActivator.plugin = this;
		for (Bundle b : context.getBundles()) {
			// this is a dirty hack for our current topicmapstores to register their services
			// this will not work with sotres using another symbolic name then de.topicmapslab.majortom*
			String name = (String) b.getHeaders().get("Bundle-SymbolicName");
			if (name.startsWith("de.topicmapslab.majortom")) {
				if ((b.getState()!=Bundle.STARTING)&&(b.getState()!=Bundle.ACTIVE)) {
					b.start();
				}
			}
				
		}
	}


	@Override
	public void stop(BundleContext context) throws Exception {
	}

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

	public static MajorToMActivator getDefault() {
		return plugin;
	}

	

}
