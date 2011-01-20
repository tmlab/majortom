package de.topicmapslab.majortom.importer.helper;

import java.util.ArrayList;
import java.util.List;

import com.semagia.mio.IRef;

/**
 * 
 * supertype for the name and occurence helper class
 * 
 * @author Hannes Niederhausen
 * 
 */
public abstract class Characteristic {

	private long parentId;
	private long topicmapId;
	private IRef typeRef;
	private String value;
	private IRef reifier;
	private List<IRef> themeRefs = new ArrayList<IRef>();

	/**
	 * 
	 */
	public Characteristic() {
		super();
	}

	/**
	 * @return
	 */
	public long getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 */
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return
	 */
	public long getTopicMapId() {
		return topicmapId;
	}

	/**
	 * @param topicmapId
	 */
	public void setTopicmapId(long topicmapId) {
		this.topicmapId = topicmapId;
	}

	/**
	 * @return
	 */
	public IRef getTypeRef() {
		return typeRef;
	}

	/**
	 * @param typeRef
	 */
	public void setTypeRef(IRef typeRef) {
		this.typeRef = typeRef;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return
	 */
	public List<IRef> getThemeRefs() {
		return themeRefs;
	}

	/**
	 * @param ref
	 */
	public void addTheme(IRef ref) {
		themeRefs.add(ref);
	}

	/**
	 * @return
	 */
	public IRef getReifier() {
		return reifier;
	}
	
	/**
	 * @param reifier
	 */
	public void setReifier(IRef reifier) {
		this.reifier = reifier;
	}
}