package de.topicmapslab.majortom.core;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstructFactory;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;

/**
 * Base implementation of a construct factory only calls the internal package
 * private constructors.
 * 
 * @author Sven Krosse
 * 
 */
public class ConstructFactoryImpl implements IConstructFactory {

	/**
	 * {@inheritDoc}
	 */
	public IAssociation newAssociation(ITopicMapStoreIdentity identity, ITopicMap parent) {
		return new AssociationImpl(identity, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociationRole newAssociationRole(ITopicMapStoreIdentity identity, IAssociation parent) {
		return new AssociationRoleImpl(identity, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public IName newName(ITopicMapStoreIdentity identity, ITopic parent) {
		return new NameImpl(identity, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public IOccurrence newOccurrence(ITopicMapStoreIdentity identity, ITopic parent) {
		return new OccurrenceImpl(identity, parent);
	}

	/**
	 * {@inheritDoc}
	 */
	public ITopic newTopic(ITopicMapStoreIdentity identity, ITopicMap topicMap) {
		return new TopicImpl(identity, topicMap);
	}

	/**
	 * {@inheritDoc}
	 */
	public IVariant newVariant(ITopicMapStoreIdentity identity, IName parent) {
		return new VariantImpl(identity, parent);
	}

}
