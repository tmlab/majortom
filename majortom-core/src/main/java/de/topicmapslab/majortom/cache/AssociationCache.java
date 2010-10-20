/**
 * 
 */
package de.topicmapslab.majortom.cache;

import java.util.Map;

import org.tmapi.core.Construct;

import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class AssociationCache implements ITopicMapListener {

	private Map<IAssociationRole, ITopic> players;

	/**
	 * remove all contents from the internal cache
	 */
	public void clear() {
		if (players != null) {
			players.clear();
		}
	}

	/**
	 * Returns the player of the given role
	 * 
	 * @param role
	 *            the role
	 * @return the player or <code>null</code>
	 */
	public ITopic getPlayer(IAssociationRole role) {
		if (players == null) {
			return null;
		}
		return players.get(role);
	}

	/**
	 * Cache the player of the given role into internal store
	 * 
	 * @param role
	 *            the role
	 * @param player
	 *            the player
	 */
	public void cachePlayer(IAssociationRole role, ITopic player) {
		if (players == null) {
			players = HashUtil.getHashMap();
		}
		players.put(role, player);
	}

	/**
	 * {@inheritDoc}
	 */
	public void topicMapChanged(String id, TopicMapEventType event,
			Construct notifier, Object newValue, Object oldValue) {
		/*
		 * does cache contains any value and a role was removed
		 */
		if (event == TopicMapEventType.ROLE_REMOVED && players != null) {
			players.remove(oldValue);
		}
		/*
		 * player of a role was modified
		 */
		else if (event == TopicMapEventType.PLAYER_MODIFIED) {
			cachePlayer((IAssociationRole) notifier, (ITopic) newValue);
		}
	}
}
