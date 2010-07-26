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
public interface ISql99DeleteQueries {

	// ********************
	// * DELETE CONSTRUCT *
	// ********************

	public static final String QUERY_DELETE_ALL_ITEM_IDENTIFIERS = "DELETE FROM rel_item_identifiers WHERE id_construct IN ( SELECT id FROM constructs WHERE id_topicmap = ? OR id = ? );";
	
	public static final String QUERY_DELETE_HISTORY = "DELETE FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier IN ( SELECT id FROM constructs WHERE id_topicmap = ? ) OR id_notifier = ? );";
	
	public static final String QUERY_DELETE_TOPICMAP = QUERY_DELETE_ALL_ITEM_IDENTIFIERS + QUERY_DELETE_HISTORY + "DELETE FROM topicmaps WHERE id = ?;";

	public static final String QUERY_DELETE_TOPIC = "UPDATE topics SET removed = TRUE WHERE id = ?; UPDATE constructs SET removed = TRUE WHERE id_parent = ?; UPDATE variants SET removed = TRUE WHERE id_parent IN ( SELECT id FROM names WHERE id_parent = ? );";

	public static final String QUERY_DELETE_NAME = "UPDATE names SET removed = TRUE WHERE id = ?; UPDATE variants SET removed = TRUE WHERE id_parent = ?;";

	public static final String QUERY_DELETE_OCCURRENCE = "UPDATE occurrences SET removed = TRUE WHERE id = ?;";

	public static final String QUERY_DELETE_VARIANT = "UPDATE variants SET removed = TRUE WHERE id = ?;";

	public static final String QUERY_DELETE_ASSOCIATION = "UPDATE associations SET removed = TRUE WHERE id = ?; UPDATE roles SET removed = TRUE WHERE id_parent = ?;";

	public static final String QUERY_DELETE_ROLE = "UPDATE roles SET removed = TRUE WHERE id = ?;";

	// ***************
	// * DELETE DATA *
	// ***************

	public static final String QUERY_DELETE_TYPE = "DELETE FROM rel_instance_of WHERE id_instance = ? AND id_type = ?;";

	public static final String QUERY_DELETE_SUPERTYPE = "DELETE FROM rel_kind_of WHERE id_subtype = ? AND id_supertype = ?;";

	public static final String QUERY_DELETE_SUBJECT_IDENTIFIER = "DELETE FROM rel_subject_identifiers WHERE id_topic = ? and id_locator = ( SELECT id FROM locators WHERE reference LIKE ? )";

	public static final String QUERY_DELETE_SUBJECT_LOCATOR = "DELETE FROM rel_subject_locators WHERE id_topic = ? and id_locator = ( SELECT id FROM locators WHERE reference LIKE ? )";

	public static final String QUERY_DELETE_ITEM_IDENTIFIER = "DELETE FROM rel_item_identifiers WHERE id_construct = ? and id_locator = ( SELECT id FROM locators WHERE reference LIKE ? )";

}
