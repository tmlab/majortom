/*
 * Copyright: Copyright 2010 Topic Maps Lab, University of Leipzig. http://www.topicmapslab.de/    
 * License:   Apache License, Version 2.0 http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 *
 */
package de.topicmapslab.majortom.util;

/**
 * Utility class for TMDM default subject-identifier.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class TmdmSubjectIdentifier {
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_TYPE_ROLE_TYPE = "http://psi.topicmaps.org/iso13250/model/type";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_INSTANCE_ROLE_TYPE = "http://psi.topicmaps.org/iso13250/model/instance";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_SUBTYPE_ROLE_TYPE = "http://psi.topicmaps.org/iso13250/model/subtype";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_SUPERTYPE_ROLE_TYPE = "http://psi.topicmaps.org/iso13250/model/supertype";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_TYPE_INSTANCE_ASSOCIATION = "http://psi.topicmaps.org/iso13250/model/type-instance";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION = "http://psi.topicmaps.org/iso13250/model/supertype-subtype";
	/**
	 * subject-identifier of the default name-type of the topic maps data model
	 */
	public static final String TMDM_DEFAULT_NAME_TYPE = "http://psi.topicmaps.org/iso13250/model/topic-name";

	/**
	 * Checks if the given identifier is known as subject-identifier of the
	 * topic maps meta model
	 * 
	 * @param identifier
	 *            the identifier
	 * @return <code>true</code> if the given identifier is a predefined
	 *         subject-identifier of the topic maps meta model,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isTmdmSubjectIdentifier(final String identifier) {
		return identifier.equals(TMDM_DEFAULT_NAME_TYPE) || identifier.equals(TMDM_INSTANCE_ROLE_TYPE) || identifier.equals(TMDM_SUBTYPE_ROLE_TYPE)
				|| identifier.equals(TMDM_SUPERTYPE_ROLE_TYPE) || identifier.equals(TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION)
				|| identifier.equals(TMDM_TYPE_INSTANCE_ASSOCIATION) || identifier.equals(TMDM_TYPE_ROLE_TYPE);
	}

}
