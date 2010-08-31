package de.topicmapslab.majortom.model.core;

import de.topicmapslab.majortom.model.store.ITopicMapStoreIdentity;

/**
 * Interface definition of a construct factory
 * 
 * @author Sven Krosse
 * 
 */
public interface IConstructFactory {

	/**
	 * Factory method to create a new topic instance.
	 * 
	 * @param identity
	 *            the identity of the new topic instance
	 * @param topicMap
	 *            the parent topic map
	 * @return the create topic instance
	 */
	public ITopic newTopic(ITopicMapStoreIdentity identity, ITopicMap topicMap);

	/**
	 * Factory method to create a new topic name instance.
	 * 
	 * @param identity
	 *            the identity of the new topic name instance
	 * @param parent
	 *            the parent topic
	 * @return the create topic name instance
	 */
	public IName newName(ITopicMapStoreIdentity identity, ITopic parent);

	/**
	 * Factory method to create a new occurrence instance.
	 * 
	 * @param identity
	 *            the identity of the new occurrence instance
	 * @param parent
	 *            the parent topic
	 * @return the create occurrence
	 */
	public IOccurrence newOccurrence(ITopicMapStoreIdentity identity,
			ITopic parent);

	/**
	 * Factory method to create a new topic name variant instance.
	 * 
	 * @param identity
	 *            the identity of the new topic name variant instance
	 * @param parent
	 *            the parent name
	 * @return the create topic name variant
	 */
	public IVariant newVariant(ITopicMapStoreIdentity identity, IName parent);

	/**
	 * Factory method to create a new association instance.
	 * 
	 * @param identity
	 *            the identity of the new association instance
	 * @param parent
	 *            the parent topic map
	 * @return the create association
	 */
	public IAssociation newAssociation(ITopicMapStoreIdentity identity,
			ITopicMap parent);

	/**
	 * Factory method to create a new association role instance.
	 * 
	 * @param identity
	 *            the identity of the new association role instance
	 * @param parent
	 *            the parent topic map
	 * @return the create association role
	 */
	public IAssociationRole newAssociationRole(ITopicMapStoreIdentity identity,
			IAssociation parent);

}
