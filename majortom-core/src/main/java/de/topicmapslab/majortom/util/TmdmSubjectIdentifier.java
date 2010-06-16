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
	 * subject-identifier of the topic-type of the topic maps data model
	 */
	public static final String TMDM_TOPIC_TYPE = "http://psi.topicmaps.org/iso13250/glossary/topic-type";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_NAME_TYPE = "http://psi.topicmaps.org/iso13250/glossary/topic-name-type";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_OCCURRENCE_TYPE = "http://psi.topicmaps.org/iso13250/glossary/occurrence-type";
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
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_ASSOCIATION_TYPE = "http://psi.topicmaps.org/iso13250/glossary/association-type";
	/**
	 * subject-identifier of the name-type of the topic maps data model
	 */
	public static final String TMDM_ASSOCIATION_ROLE_TYPE = "http://psi.topicmaps.org/iso13250/glossary/association-role-type";
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
		return identifier.equalsIgnoreCase(TMDM_ASSOCIATION_ROLE_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_ASSOCIATION_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_DEFAULT_NAME_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_INSTANCE_ROLE_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_NAME_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_OCCURRENCE_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_SUBTYPE_ROLE_TYPE)
				|| identifier.equalsIgnoreCase(TMDM_SUPERTYPE_ROLE_TYPE)
				|| identifier
						.equalsIgnoreCase(TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION)
				|| identifier.equalsIgnoreCase(TMDM_TOPIC_TYPE)
				|| identifier
						.equalsIgnoreCase(TMDM_TYPE_INSTANCE_ASSOCIATION)
				|| identifier.equalsIgnoreCase(TMDM_TYPE_ROLE_TYPE);
	}

}
