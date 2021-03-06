package de.topicmapslab.majortom.model.event;

import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Typed;

import de.topicmapslab.majortom.model.core.ICharacteristics;

/**
 * The enumeration represents the type of events a topic map construct may throw.
 */
public enum TopicMapEventType {
	
	 TOPIC_MAP_CREATED,
	
	 TOPIC_ADDED,
	 
	 TYPE_ADDED,
	 
	 SUPERTYPE_ADDED,
	
	 SUBJECT_IDENTIFIER_ADDED,
	 
	 SUBJECT_LOCATOR_ADDED,
	 
	 ITEM_IDENTIFIER_ADDED,
	 
	 SCOPE_MODIFIED,
	 
	 ROLE_ADDED,
	 
	 ASSOCIATION_ADDED,
	 
	 NAME_ADDED,
	 
	 OCCURRENCE_ADDED,
	 
	 VARIANT_ADDED,
	 
	 PLAYER_MODIFIED,
	 	 
	 SUBJECT_IDENTIFIER_REMOVED,
	 
	 SUBJECT_LOCATOR_REMOVED,	 
	 
	 ITEM_IDENTIFIER_REMOVED,
	 
	 TYPE_REMOVED,
	 
	 SUPERTYPE_REMOVED,
	 
	 ASSOCIATION_REMOVED,
	 
	 ROLE_REMOVED,
	 
	 OCCURRENCE_REMOVED,
	 
	 VARIANT_REMOVED,
	 
	 NAME_REMOVED,
	 
	 TOPIC_REMOVED,
	 /**
	  * The type of {@link Typed} was set
	  */
	 TYPE_SET,
	 /**
	  * The reifier of {@link Reifiable} was set
	  */
	 REIFIER_SET,
	 /**
	  * The value of {@link ICharacteristics} was modified or the role player of a {@link Role} was changes
	  */
	 VALUE_MODIFIED,
	 /**
	  * the datatype of {@link ICharacteristics} was modified
	  */
	 DATATYPE_SET,	 
	 /**
	  * two constructs merged
	  */
	 MERGE,
	 
	 /**
	  * the internal id of a construct was modified
	  */
	 ID_MODIFIED,
	 
	 /**
	  * duplicates removed
	  */
	 REMOVE_DUPLICATES,
	 
	 /**
	  *@XXX Remove this, if external dependencies are cleaned (Maiana Meta Map) 
	  */	 
	 UNKNOWN;
	 
}
