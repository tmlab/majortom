package de.topicmapslab.majortom.importer.helper;

import com.semagia.mio.IRef;

/**
 * helper class to store roles temporarily
 * 
 * @author Hannes Niederhausen
 *
 */
public class Role {

	private IRef roleType;
	private IRef rolePlayer;
	/**
	 * @return the roleType
	 */
	public IRef getRoleType() {
		return roleType;
	}
	/**
	 * @param roleType the roleType to set
	 */
	public void setRoleType(IRef roleType) {
		this.roleType = roleType;
	}
	/**
	 * @return the rolePlayer
	 */
	public IRef getRolePlayer() {
		return rolePlayer;
	}
	/**
	 * @param rolePlayer the rolePlayer to set
	 */
	public void setRolePlayer(IRef rolePlayer) {
		this.rolePlayer = rolePlayer;
	}
	
	
	
	
	
}
