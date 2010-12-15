package de.topicmapslab.majortom.importer.helper;

import java.util.ArrayList;
import java.util.List;

import com.semagia.mio.IRef;

/**
 * Helper class to store associations temporarily
 * 
 * @author Hannes Niederhausen
 *
 */
public class Association {

	private long topicmapId;
	
	private List<Role> roles = new ArrayList<Role>();
	
	private IRef type;
	
	private IRef reifier;
	
	private List<IRef> themes = new ArrayList<IRef>();

	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @return the type
	 */
	public IRef getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(IRef type) {
		this.type = type;
	}

	/**
	 * @return the reifier
	 */
	public IRef getReifier() {
		return reifier;
	}

	/**
	 * @param reifier the reifier to set
	 */
	public void setReifier(IRef reifier) {
		this.reifier = reifier;
	}

	/**
	 * @return the themes
	 */
	public List<IRef> getThemes() {
		return themes;
	}

	/**
	 * @param themes the themes to set
	 */
	public void setThemes(List<IRef> themes) {
		this.themes = themes;
	}
	
	/**
	 * 
	 * @param topicmapId the topicmap id
	 */
	public void setTopicmapId(long topicmapId) {
		this.topicmapId = topicmapId;
	}

	/**
	 * @return the topicmapId
	 */
	public long getTopicmapId() {
		return topicmapId;
	}
	
	
}
