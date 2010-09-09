package de.topicmapslab.majortom.database.cache;

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;

/**
 * Inner class handling the caching of reification
 * 
 * @author Sven Krosse
 * 
 */
class ReificationCache implements ITopicMapListener {

	/**
	 * internal storage map if reifier-reified relation
	 */
	private TreeBidiMap reification;

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		if (reification != null) {
			reification.clear();
		}
	}

	/***
	 * Returns the current stored reifier of the given reified item.
	 * 
	 * @param reifiable
	 *            the reified item
	 * @return the reifier or <code>null</code>
	 */
	public ITopic getReifier(final IReifiable reifiable) {
		if (reification == null || !reification.containsValue(reifiable)) {
			return null;
		}
		return (ITopic) reification.getKey(reifiable);
	}

	/**
	 * Returns the reified item of the given reifier
	 * 
	 * @param reifier
	 *            the reifier
	 * @return the reified item or <code>null</code>
	 */
	public IReifiable getReified(final ITopic reifier) {
		if (reification == null || !reification.containsKey(reifier)) {
			return null;
		}
		return (IReifiable) reification.get(reifier);
	}

	/**
	 * Cache the given reification into internal cache
	 * 
	 * @param reifiable
	 *            the reified construct
	 * @param reifier
	 *            the reifier
	 */
	public void cacheReification(IReifiable reifiable, ITopic reifier) {
		if (reification == null) {
			reification = new TreeBidiMap();
		}
		reification.put(reifier, reifiable);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * reification modified
		 */
		if (event == TopicMapEventType.REIFIER_SET) {
			ITopic newReifier = (ITopic) oldValue;
			IReifiable reifiable = (IReifiable) notifier;
			/*
			 * reification removed
			 */
			if (newReifier == null && reification != null) {
				reification.remove(newReifier);
			}
			/*
			 * reification set
			 */
			else if (newReifier != null) {
				cacheReification(reifiable, newReifier);
			}			
		}
		/*
		 * topic removed as potential reifier
		 */
		else if (event == TopicMapEventType.TOPIC_REMOVED
				&& reification != null) {
			ITopic topic = (ITopic) oldValue;
			reification.remove(topic);
		}
		/*
		 * reified construct removed
		 */
		else if (reification != null
				&& (event == TopicMapEventType.NAME_REMOVED
						|| event == TopicMapEventType.ASSOCIATION_REMOVED
						|| event == TopicMapEventType.OCCURRENCE_REMOVED
						|| event == TopicMapEventType.VARIANT_REMOVED || event == TopicMapEventType.ROLE_REMOVED)) {
			reification.removeValue(oldValue);
		}
	}
}
