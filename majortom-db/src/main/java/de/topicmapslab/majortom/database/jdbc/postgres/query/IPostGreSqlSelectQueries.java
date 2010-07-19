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
package de.topicmapslab.majortom.database.jdbc.postgres.query;

/**
 * @author Sven Krosse
 * 
 */
public interface IPostGreSqlSelectQueries {

	// ******************
	// * READ TOPIC MAP *
	// ******************
	/**
	 * query to read the topic map
	 * <p>
	 * <b>parameters(1):</b> topic map base locator
	 * </p>
	 */
	public static String QUERY_READ_TOPICMAP = "SELECT tm.id FROM topicmaps AS tm, locators AS l WHERE l.reference = ? AND l.id = tm.id_base_locator;";

	// ********************
	// * READ ASSOCIATION *
	// ********************
	/**
	 * Query to read all associations
	 * <p>
	 * <b>parameters(1):</b> topic map id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS = "SELECT id FROM associations WHERE id_topicmap = ?";
	/**
	 * Query to read all associations of a specific type
	 * <p>
	 * <b>parameters(2):</b> topic map id, type id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ?";
	/**
	 * Query to read all associations within a specific type
	 * <p>
	 * <b>parameters(2):</b> topic map id, scope id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS_WITH_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_scope = ?";
	/**
	 * Query to read all associations of a specific type and within a specific
	 * scope
	 * <p>
	 * <b>parameters(3):</b> topic map id, type id, scope id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ? AND id_scope = ?";
	/**
	 * Query to read all played associations
	 * <p>
	 * <b>parameters(2):</b> topic map id, player id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id";

	/**
	 * Query to read all played associations of a specific type
	 * <p>
	 * <b>parameters(3):</b> topic map id, player id, type id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_type = ?";

	/**
	 * Query to read all played associations within a specific scope
	 * <p>
	 * <b>parameters(3):</b> topic map id, player id, scope id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_SCOPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_scope = ?";

	/**
	 * Query to read all played associations of a specific type and within a
	 * specific scope
	 * <p>
	 * <b>parameters(4):</b> topic map id, player id, type id, scope id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_type = ? AND a.id_scope = ?";

	// ********************
	// * READ CONSTRUCTS *
	// ********************

	/**
	 * Query to read a construct by id
	 * <p>
	 * <b>parameters(2):</b> topic map id and topic id
	 * </p>
	 */
	public static String QUERY_READ_CONSTRUCT = "SELECT id FROM constructs WHERE id_topicmap = ? AND id = ?";

	/**
	 * Query to read a topic by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, topic id
	 * </p>
	 */
	public static String QUERY_READ_TOPIC_BY_ID = "SELECT id FROM topics WHERE id_topicmap = ? AND id = ?";
	/**
	 * Query to read a name by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, name id
	 * </p>
	 */
	public static String QUERY_READ_NAME_BY_ID = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND id = ?";
	/**
	 * Query to read an occurrence by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, occurrence id
	 * </p>
	 */
	public static String QUERY_READ_OCCURRENCE_BY_ID = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id = ?";
	/**
	 * Query to read a variant by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, variant id
	 * </p>
	 */
	public static String QUERY_READ_VARIANT_BY_ID = "SELECT v.id, n.id, n.id_parent FROM variants AS v, names AS n WHERE v.id_topicmap = ? AND v.id = ? AND v.id_parent = n.id";
	/**
	 * Query to read an association by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, association id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONC_BY_ID = "SELECT id FROM associations WHERE id_topicmap = ? AND id = ?";
	/**
	 * Query to read a role by id
	 * <p>
	 * <b>parameters(2):</b> topic map id, role id
	 * </p>
	 */
	public static String QUERY_READ_ROLE_BY_ID = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id = ?";
	/**
	 * query to read a construct by item-identifier
	 * <p>
	 * <b>parameters(3):</b> topic map id,topic map id, the reference
	 * </p>
	 */
	public static final String QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER = " SELECT c.id FROM constructs AS c, rel_item_identifiers AS r, locators AS l WHERE ( c.id_topicmap = ? OR c.id = ? ) AND l.reference = ? AND l.id = r.id_locator AND r.id_construct = c.id;";

	// ********************
	// * READ DATATYPE *
	// ********************

	/**
	 * Query to read the data type of an occurrence or variant
	 * <p>
	 * <b>parameters(1):</b> construct id
	 * </p>
	 */
	public static final String QUERY_READ_DATATYPE = "SELECT reference FROM locators AS l, datatypeawares AS d WHERE d.id_datatype = l.id AND d.id = ?";

	// ***********************
	// * READ ITEMIDENTIFIER *
	// ***********************

	/**
	 * Query to read all item-identifiers of a construct
	 * <p>
	 * <b>parameters(1):</b> construct id
	 * </p>
	 */
	public static final String QUERY_READ_ITEM_IDENTIFIERS = "SELECT reference FROM locators AS l, rel_item_identifiers AS r WHERE r.id_construct = ? AND r.id_locator = l.id";

	// **************
	// * READ NAMES *
	// **************

	/**
	 * query to read all names of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES = "SELECT id FROM names WHERE id_parent = ?";
	/**
	 * query to read all names of a topic by type
	 * <p>
	 * <b>parameters(2):</b> topic id, type id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES_WITH_TYPE = "SELECT id FROM names WHERE id_parent = ? AND id_type = ?";
	/**
	 * query to read all names of topic by scope
	 * <p>
	 * <b>parameters(2):</b> topic id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES_WITH_SCOPE = "SELECT id FROM names WHERE id_parent = ? AND id_scope = ?";
	/**
	 * query to read all names of a topic by type and scope
	 * <p>
	 * <b>parameters(3):</b> topic id, type id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES_WITH_TYPE_AND_SCOPE = "SELECT id FROM names WHERE id_parent = ? AND id_type = ? AND id_scope = ?";

	// ********************
	// * READ OCCURRENCES *
	// ********************

	/**
	 * query to read all occurrences of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES = "SELECT id FROM occurrences WHERE id_parent = ?";
	/**
	 * query to read all occurrences of a topic by type
	 * <p>
	 * <b>parameters(2):</b> topic id, type id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES_WITH_TYPE = "SELECT id FROM occurrences WHERE id_parent = ? AND id_type = ?";
	/**
	 * query to read all occurrences of topic by scope
	 * <p>
	 * <b>parameters(2):</b> topic id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES_WITH_SCOPE = "SELECT id FROM occurrences WHERE id_parent = ? AND id_scope = ?";
	/**
	 * query to read all occurrences of a topic by type and scope
	 * <p>
	 * <b>parameters(3):</b> topic id, type id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES_WITH_TYPE_AND_SCOPE = "SELECT id FROM occurrences WHERE id_parent = ? AND id_type = ? AND id_scope = ?";

	// ********************
	// * READ ROLE PLAYER *
	// ********************

	/**
	 * query to read the player of a role
	 * <p>
	 * <b>parameters(1):</b> role id
	 * </p>
	 */
	public static final String QUERY_READ_PLAYER = "SELECT id_player FROM roles WHERE id = ?";

	// ********************
	// * READ REIFICATION *
	// ********************

	/**
	 * query to read the reifier of a construct
	 * <p>
	 * <b>parameters(1):</b> construct id
	 * </p>
	 */
	public static final String QUERY_READ_REIFIER = "SELECT id_reifier FROM reifiables WHERE id = ?";

	/**
	 * query to read the reified construct of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_REIFIED = "SELECT id FROM reifiables WHERE id_reifier = ?";

	// **************
	// * READ ROLES *
	// **************

	/**
	 * Query to read all roles
	 * <p>
	 * <b>parameters(1):</b> association id
	 * </p>
	 */
	public static String QUERY_READ_ROLES = "SELECT id FROM roles WHERE id_parent = ?";
	/**
	 * Query to read all roles by type
	 * <p>
	 * <b>parameters(2):</b> association id, type id
	 * </p>
	 */
	public static String QUERY_READ_ROLES_WITH_TYPE = "SELECT id FROM roles WHERE id_parent = ? AND id_type = ?";
	/**
	 * Query to read all played roles
	 * <p>
	 * <b>parameters(1):</b> player id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ROLES = "SELECT id, id_parent FROM roles WHERE id_player = ?";

	/**
	 * Query to read all played roles by type
	 * <p>
	 * <b>parameters(2):</b> player id, type id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE = "SELECT id, id_parent FROM roles WHERE id_player = ? AND id_type = ?";

	/**
	 * Query to read all played roles by types
	 * <p>
	 * <b>parameters(3):</b> player id, type id, asso_type
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE = "SELECT r.id, r.id_parent FROM roles AS r, associations AS a WHERE r.id_player = ? AND r.id_type = ? AND r.id_parent = a.id AND a.id_type = ?";

	/**
	 * Query to read all roles types
	 * <p>
	 * <b>parameters(1):</b> association id
	 * </p>
	 */
	public static String QUERY_READ_ROLESTYPES = "SELECT DISTINCT id_type FROM roles WHERE id_parent = ?";

	// *************************
	// * READ TOPIC IDENTIDIES *
	// *************************

	/**
	 * Query to read all subject-identifiers of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_SUBJECT_IDENTIFIERS = "SELECT reference FROM locators AS l, rel_subject_identifiers AS r WHERE r.id_topic = ? AND r.id_locator = l.id";
	/**
	 * Query to read all subject-locators of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_SUBJECT_LOCATORS = "SELECT reference FROM locators AS l, rel_subject_locators AS r WHERE r.id_topic = ? AND r.id_locator = l.id";

	// **************
	// * READ TOPICS *
	// **************

	/**
	 * query to read a topic by subject-identifier
	 * <p>
	 * <b>parameters(2):</b>topic map id, the reference
	 * </p>
	 */
	public static final String QUERY_READ_TOPIC_BY_SUBJECT_IDENTIFIER = " SELECT t.id FROM topics AS t, locators AS l, rel_subject_identifiers as r WHERE  id_topicmap = ? AND reference = ? AND l.id = r.id_locator AND r.id_topic = t.id;";
	/**
	 * query to read a topic by subject-identifier
	 * <p>
	 * <b>parameters(2):</b>topic map id, the reference
	 * </p>
	 */
	public static final String QUERY_READ_TOPIC_BY_SUBJECT_LOCATOR = " SELECT t.id FROM topics AS t, locators AS l, rel_subject_locators as r WHERE  id_topicmap = ? AND reference = ? AND l.id = r.id_locator AND r.id_topic = t.id;";
	/**
	 * Query to read all topics
	 * <p>
	 * <b>parameters(1):</b> topic map id
	 * </p>
	 */
	public static final String QUERY_READ_TOPICS = "SELECT id FROM topics WHERE id_topicmap = ?";
	/**
	 * Query to read all topics by type
	 * <p>
	 * <b>parameters(2):</b> topic map id, type id
	 * </p>
	 */
	public static final String QUERY_READ_TOPICS_WITH_TYPE = "SELECT id_instance FROM rel_instance_of WHERE id_type = ?";

	// **********************
	// * READ TYPE HIERACHY *
	// **********************

	/**
	 * Query to read the type of a typed construct
	 * <p>
	 * <b>parameters(1):</b> construct id
	 * </p>
	 */
	public static final String QUERY_READ_TYPE = "SELECT id_type FROM typeables WHERE id = ?";

	/**
	 * Query to read the types of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_TYPES = "SELECT id_type FROM rel_instance_of WHERE id_instance = ?";

	/**
	 * Query to read the super types of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_SUPERTYPES = "SELECT id_supertype FROM rel_kind_of WHERE id_subtype = ?";

	// **************
	// * READ SCOPE *
	// **************

	/**
	 * Query to read the empty scope of the topic map
	 * <p>
	 * <b>parameters(1):</b> the topic map id
	 * </p>
	 */
	public static final String QUERY_READ_EMPTY_SCOPE = "SELECT id FROM scopes WHERE id_topicmap = ? AND id NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) ";

	/**
	 * Query to read the scope of a construct
	 * <p>
	 * <b>parameters(1):</b> the construct id
	 * </p>
	 */
	public static final String QUERY_READ_SCOPE = "SELECT DISTINCT id_scope FROM scopeables WHERE id = ?";

	/**
	 * Query to read the themes of a scope
	 * <p>
	 * <b>parameters(1):</b> the scope id
	 * </p>
	 */
	public static final String QUERY_READ_THEMES = "SELECT DISTINCT id_theme FROM rel_themes WHERE id_scope = ?";

	/**
	 * Query to read the scope object by a collection of themes
	 * <p>
	 * <b>parameters(1):</b> the topic map id and an array of theme IDs ( ASC )
	 * </p>
	 */
	public static final String QUERY_READ_SCOPES_BY_THEMES = "SELECT DISTINCT id_scope FROM rel_themes AS r WHERE ARRAY(SELECT id_theme FROM rel_themes AS r2 WHERE r2.id_scope = r.id_scope ORDER BY r2.id_theme ASC ) %OPERATOR% CAST ( ARRAY[%ARRAY%] AS bigint[])";
	

	// **************
	// * READ VALUE *
	// **************

	/**
	 * Query to read the value of a name, an occurrence or a variant
	 * <p>
	 * <b>parameters(1):</b> construct id
	 * </p>
	 */
	public static final String QUERY_READ_VALUE = "SELECT DISTINCT value FROM literals WHERE id = ?";

	// *****************
	// * READ VARIANTS *
	// *****************

	/**
	 * query to read all variants of a name
	 * <p>
	 * <b>parameters(1):</b> name id
	 * </p>
	 */
	public static final String QUERY_READ_VARIANTS = "SELECT id FROM variants WHERE id_parent = ?";
	/**
	 * query to read all variants of a name by scope
	 * <p>
	 * <b>parameters(2):</b> name id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_VARIANTS_WITH_SCOPE = "SELECT id FROM variants WHERE id_parent = ? AND id_scope = ?";
}
