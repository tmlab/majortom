/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.topicmapslab.majortom.database.jdbc.postgresql99.query;

/**
 * @author Sven Krosse
 * 
 */
public interface ISql99UpdateQueries {
	/**
	 * query to add a subject-locator for a topic
	 * <p>
	 * <b>parameters(4):</b> the reference twice, the topic id and the reference
	 * of the locator
	 * </p>
	 */
	public static final String QUERY_ADD_SUBJECT_LOCATOR = ISql99Queries.SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO rel_subject_locators(id_topic, id_locator) SELECT ? , id FROM locators WHERE reference LIKE ?;";
	/**
	 * query to add a item-identifier for a construct
	 * <p>
	 * <b>parameters(4):</b> the reference twice, the construct id and the
	 * reference of the locator
	 * </p>
	 */
	public static final String QUERY_ADD_ITEM_IDENTIFIER = ISql99Queries.SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO rel_item_identifiers(id_construct, id_locator) SELECT ? , id FROM locators WHERE reference LIKE ?;";
	/**
	 * query to add a subject-identifier for a topic
	 * <p>
	 * <b>parameters(4):</b> the reference twice, the topic id and the reference
	 * of the locator
	 * </p>
	 */
	public static final String QUERY_ADD_SUBJECT_IDENTIFIER = ISql99Queries.SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO rel_subject_identifiers(id_topic, id_locator) SELECT ? , id FROM locators WHERE reference LIKE ?;";

	/**
	 * query to add a theme to an existing scope
	 * <p>
	 * <b>parameters(2):</b> scope id, theme id
	 * </p>
	 */
	public static final String QUERY_ADD_THEME = "INSERT INTO rel_themes(id_scope, id_theme) VALUES (?,?);";

	/**
	 * query to modify the type of a typed construct
	 * <p>
	 * <b>parameters(2):</b> type id, construct id
	 * </p>
	 */
	public static final String QUERY_MODIFY_TYPE = "UPDATE typeables SET id_type = ? WHERE id = ?;";

	/**
	 * query to modify the player of a role
	 * <p>
	 * <b>parameters(2):</b> role id, player id
	 * </p>
	 */
	public static final String QUERY_MODIFY_PLAYER = "UPDATE roles SET id_player = ? WHERE id = ?;";

	/**
	 * query to modify the reifier of a construct
	 * <p>
	 * <b>parameters(2):</b> reifier id, topic id
	 * </p>
	 */
	public static final String QUERY_MODIFY_REIFIER = "UPDATE reifiables SET id_reifier = ? WHERE id = ?;";

	/**
	 * query to modify the scope of a construct
	 * <p>
	 * <b>parameters(2):</b> scope id, scoped id
	 * </p>
	 */
	public static final String QUERY_MODIFY_SCOPE = "UPDATE scopeables SET id_scope = ? WHERE id = ?;";

	/**
	 * query to add a type to a topic
	 * <p>
	 * <b>parameters(4):</b> instance id, type id, instance id, type id
	 * </p>
	 */
	public static final String QUERY_MODIFY_TYPES = "INSERT INTO rel_instance_of(id_instance, id_type) SELECT ?,? WHERE NOT EXISTS ( SELECT id_instance, id_type  FROM rel_instance_of WHERE id_instance = ? AND id_type = ? );";
	/**
	 * query to add a supertype to a topic
	 * <p>
	 * <b>parameters(4):</b> subtype id, supertype id, subtype id, supertype id
	 * </p>
	 */
	public static final String QUERY_MODIFY_SUPERTYPES = "INSERT INTO rel_kind_of(id_subtype, id_supertype) SELECT ?,? WHERE NOT EXISTS ( SELECT id_subtype, id_supertype  FROM rel_kind_of WHERE id_subtype = ? AND id_supertype = ? );";

	/**
	 * query to modify the value of a name, occurrence or variant
	 * <p>
	 * <b>parameters(2):</b> value, construct id
	 * </p>
	 */
	public static final String QUERY_MODIFY_VALUE = "UPDATE literals SET value = ? WHERE id = ?;";

	/**
	 * query to modify the value of an occurrence or variant and the datatype
	 * <p>
	 * <b>parameters(6):</b> value, construct id, the reference (3x), the
	 * construct id
	 * </p>
	 */
	public static final String QUERY_MODIFY_VALUE_WITH_DATATYPE = QUERY_MODIFY_VALUE + ISql99Queries.SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "UPDATE datatypeawares SET id_datatype = ( SELECT id FROM locators WHERE reference LIKE ? ) WHERE id = ?;";

	interface QueryMerge{
		
		/**
		 * query to replace each reference of a topic by another topic
		 * <p>
		 * <b>parameters(22):</b> 11x(new topic id, old topic id)
		 * </p>
		 */
		public static final String QUERY_MERGE_TOPIC 	= 	"UPDATE rel_kind_of SET id_subtype = ? WHERE id_subtype = ?;"
														+	"UPDATE rel_kind_of SET id_supertype = ? WHERE id_supertype = ?;"
														+	"UPDATE rel_instance_of SET id_instance = ? WHERE id_instance = ?;"
														+	"UPDATE rel_instance_of SET id_type = ? WHERE id_type = ?;"
														+	"UPDATE rel_themes SET id_theme = ? WHERE id_theme = ?;"
														+	"UPDATE typeables SET id_type = ? WHERE id_type = ?;"
														+	"UPDATE reifiables SET id_reifier = ? WHERE id_reifier = ?;"
														+	"UPDATE rel_item_identifiers SET id_construct = ? WHERE id_construct = ?;"
														+	"UPDATE rel_subject_identifiers SET id_topic = ? WHERE id_topic = ?;"
														+	"UPDATE rel_subject_locators SET id_topic = ? WHERE id_topic = ?;"
														+	"UPDATE roles SET id_player = ? WHERE id_player = ?;"
														+	"UPDATE constructs SET id_parent = ? WHERE id_parent = ?;"
														+	"UPDATE topics SET removed = TRUE WHERE id = ?;";
		
	}
	
}
