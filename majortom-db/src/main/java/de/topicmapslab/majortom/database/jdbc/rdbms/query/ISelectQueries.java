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
package de.topicmapslab.majortom.database.jdbc.rdbms.query;

/**
 * @author Sven Krosse
 * 
 */
public interface ISelectQueries {

	interface Paged {

		/**
		 * Query to read all associations
		 * <p>
		 * <b>parameters(3):</b> topic map id, offset, limit
		 * </p>
		 */
		public static String QUERY_READ_ASSOCIATIONS = "SELECT id FROM associations WHERE id_topicmap = ? ORDER BY id OFFSET ? LIMIT ?";

		/**
		 * Query to read the number of topics of a specific topic map.
		 * <p>
		 * <b>parameters(1):</b> topic map id
		 * </p>
		 */
		public static String QUERY_READ_NUMBER_OF_ASSOCIATIONS = "SELECT COUNT(id) FROM associations WHERE id_topicmap= ?;";
		/**
		 * Query to read all topics
		 * <p>
		 * <b>parameters(3):</b> topic map id, offset, limit
		 * </p>
		 */
		public static String QUERY_READ_TOPICS = "SELECT id FROM topics WHERE id_topicmap = ? ORDER BY id OFFSET ? LIMIT ?";
		/**
		 * Query to read the number of topics of a specific topic map.
		 * <p>
		 * <b>parameters(1):</b> topic map id
		 * </p>
		 */
		public static String QUERY_READ_NUMBER_OF_TOPICS = "SELECT COUNT(id) FROM topics WHERE id_topicmap= ?;";
		/**
		 * Query to read all played associations
		 * <p>
		 * <b>parameters(4):</b> topic map id, player id, offset, limit
		 * </p>
		 */
		public static String QUERY_READ_PLAYED_ASSOCIATIONS = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id ORDER BY a.id OFFSET ? LIMIT ?;";
		/**
		 * query to read all names of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id, offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_NAMES = "SELECT id FROM names WHERE id_parent = ? ORDER BY id OFFSET ? LIMIT ?;";
		/**
		 * query to read all occurrences of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id, offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCES = "SELECT id FROM occurrences WHERE id_parent = ? ORDER BY id OFFSET ? LIMIT ?;";
		/**
		 * Query to read all roles
		 * <p>
		 * <b>parameters(3):</b> association id,offset, limit
		 * </p>
		 */
		public static String QUERY_READ_ROLES = "SELECT id FROM roles WHERE id_parent = ? ORDER BY id OFFSET ? LIMIT ?;";
		/**
		 * Query to read all played roles
		 * <p>
		 * <b>parameters(3):</b> player id,offset, limit
		 * </p>
		 */
		public static String QUERY_READ_PLAYED_ROLES = "SELECT id, id_parent FROM roles WHERE id_player = ? ORDER BY id OFFSET ? LIMIT ?";
		/**
		 * Query to read the types of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id,offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_TYPES = "SELECT id_type FROM rel_instance_of WHERE id_instance = ? ORDER BY id_type OFFSET ? LIMIT ?;";

		/**
		 * query to read all variants of a name
		 * <p>
		 * <b>parameters(3):</b> name id,offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_VARIANTS = "SELECT id FROM variants WHERE id_parent = ? ORDER BY id OFFSET ? LIMIT ?";
		/**
		 * Query to read the number of played associations
		 * <p>
		 * <b>parameters(3):</b> player id, offset, limit
		 * </p>
		 */
		public static String QUERY_READ_NUMBER_OF_PLAYED_ASSOCIATIONS = "SELECT DISTINCT COUNT(a.id) AS number FROM associations AS a, roles AS r WHERE r.id_player = ? AND r.id_parent = a.id;";
		/**
		 * query to read the number of names of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id, offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_NUMBER_OF_NAMES = "SELECT COUNT(id) AS number FROM names WHERE id_parent = ?;";
		/**
		 * query to read the number of occurrences of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id, offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_NUMBER_OF_OCCURRENCES = "SELECT COUNT(id) AS number FROM occurrences WHERE id_parent = ?;";
		/**
		 * Query to read the number of roles
		 * <p>
		 * <b>parameters(3):</b> association id,offset, limit
		 * </p>
		 */
		public static String QUERY_READ_NUMBER_OF_ROLES = "SELECT COUNT(id) AS number FROM roles WHERE id_parent = ?;";
		/**
		 * Query to read the number of played roles
		 * <p>
		 * <b>parameters(3):</b> player id,offset, limit
		 * </p>
		 */
		public static String QUERY_READ_NUMBER_OF_PLAYED_ROLES = "SELECT COUNT(id) AS number FROM roles WHERE id_player = ?";
		/**
		 * Query to read the number of types of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id,offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_NUMBER_OF_TYPES = "SELECT COUNT(id_type) AS number FROM rel_instance_of WHERE id_instance = ?;";

		/**
		 * query to read the number of variants of a name
		 * <p>
		 * <b>parameters(3):</b> name id,offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_NUMBER_OF_VARIANTS = "SELECT COUNT(id) AS number FROM variants WHERE id_parent = ?;";
	}

	interface NonPaged {

		/**
		 * Load all locators of topic maps
		 * 
		 * @since 1.1.2
		 */
		public static String QUERY_READ_LOCATORS = "SELECT l.id , reference FROM topicmaps AS tm, locators AS l WHERE id_base_locator = l.id";
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
		public static String QUERY_READ_ASSOCIATIONS = "SELECT id FROM associations WHERE id_topicmap = ? ";
		/**
		 * Query to read all associations of a specific type
		 * <p>
		 * <b>parameters(2):</b> topic map id, type id
		 * </p>
		 */
		public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ? ";
		/**
		 * Query to read all associations within a specific type
		 * <p>
		 * <b>parameters(2):</b> topic map id, scope id
		 * </p>
		 */
		public static String QUERY_READ_ASSOCIATIONS_WITH_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_scope = ? ";

		/**
		 * Query to read all associations of a specific type and within a specific scope
		 * <p>
		 * <b>parameters(3):</b> topic map id, type id, scope id
		 * </p>
		 */
		public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ? AND id_scope = ? ";
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
		 * Query to read all played associations of a specific type and within a specific scope
		 * <p>
		 * <b>parameters(4):</b> topic map id, player id, type id, scope id
		 * </p>
		 */
		// TODO
		public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_type = ? AND a.id_scope = ?";

		// ********************
		// * READ CONSTRUCTS *
		// ********************

		/**
		 * Query to read a construct by id
		 * <p>
		 * <b>parameters(1):</b> the construct id
		 * </p>
		 */
		public static String QUERY_READ_CONSTRUCT = "SELECT id, id_parent, 0 AS other, 't' AS type FROM topics WHERE id = ? " + "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'a' AS type FROM associations WHERE id  = ? " + "UNION " + "SELECT id, id_parent, 0 AS other, 'n' AS type FROM names WHERE id  = ? " + "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'o' AS type FROM occurrences WHERE id  = ? " + "UNION "
				+ "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id  = ? AND v.id_parent = n.id " + "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'r' AS type FROM roles WHERE id  = ? " + "UNION " + "SELECT id, 0 AS id_parent, 0 AS other, 'tm' AS type FROM topicmaps WHERE id  = ?;";

		public static String QUERY_READ_CONSTRUCT_STD = "SELECT id, id_parent, 0 AS other, '%TYPE%' AS type FROM %COLUMN% WHERE id = ?";

		public static String QUERY_READ_CONSTRUCT_VARIANT = "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id  = ? AND v.id_parent = n.id;";

		/**
		 * query to read a construct by item-identifier
		 * <p>
		 * <b>parameters(7):</b> the construct id, the topic map id 6x
		 * </p>
		 */
		// public static final String QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER =
		// "WITH ids(id) AS (SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ? ) "
		// +
		// "SELECT id, id_parent, 0 AS other, 't' AS type FROM topics WHERE id IN ( SELECT id FROM ids ) AND id_topicmap = ? "
		// + "UNION "
		// +
		// "SELECT id, id_parent, 0 AS other, 'a' AS type FROM associations WHERE id IN ( SELECT id FROM ids  ) AND id_topicmap = ? "
		// + "UNION "
		// +
		// "SELECT id, id_parent, 0 AS other, 'n' AS type FROM names WHERE id IN ( SELECT id FROM ids  ) AND id_topicmap = ? "
		// + "UNION "
		// +
		// "SELECT id, id_parent, 0 AS other, 'o' AS type FROM occurrences WHERE id IN ( SELECT id FROM ids ) AND id_topicmap = ? "
		// + "UNION "
		// +
		// "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id IN ( SELECT id FROM ids ) AND v.id_parent = n.id AND v.id_topicmap = ? "
		// + "UNION "
		// +
		// "SELECT id, id_parent, 0 AS other, 'r' AS type FROM roles WHERE id IN ( SELECT id FROM ids  ) AND id_topicmap = ? "
		// + "UNION "
		// + "SELECT id, 0 AS id_parent, 0 AS other, 'tm' AS type FROM topicmaps WHERE id IN ( SELECT id FROM ids );";
		//
		public static final String QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER = "SELECT id, id_parent, 0 AS other, 't' AS type FROM topics WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  ) AND id_topicmap = ? "
				+ "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'a' AS type FROM associations WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  ) AND id_topicmap = ? "
				+ "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'n' AS type FROM names WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  ) AND id_topicmap = ? "
				+ "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'o' AS type FROM occurrences WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ? ) AND id_topicmap = ? "
				+ "UNION "
				+ "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ? ) AND v.id_parent = n.id AND v.id_topicmap = ? "
				+ "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'r' AS type FROM roles WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  ) AND id_topicmap = ? "
				+ "UNION "
				+ "SELECT id, 0 AS id_parent, 0 AS other, 'tm' AS type FROM topicmaps WHERE id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  );";

		public static final String QUERY_READ_CONSTRUCT_ID_BY_ITEM_IDENTIFIER = "SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?;";

		public static final String QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER_STD = "SELECT id, id_parent, 0 AS other, '%TYPE%' AS type FROM %COLUMN% WHERE id_topicmap = ? AND id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ?  );";

		public static final String QUERY_READ_CONSTRUCT_BY_ITEM_IDENTIFIER_VARIANT = "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.id IN ( SELECT id_construct FROM rel_item_identifiers, locators WHERE id = id_locator AND reference = ? );";

		// ********************
		// * READ DATATYPE *
		// ********************

		/**
		 * Query to read the data type of an occurrence or variant
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCE_DATATYPE = "SELECT l.id, l.reference FROM locators AS l, occurrences AS d WHERE d.id_datatype = l.id AND d.id = ?";

		/**
		 * Query to read the data type of an occurrence or variant
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_VARIANT_DATATYPE = "SELECT l.id,reference FROM locators AS l, variants AS d WHERE d.id_datatype = l.id AND d.id = ?";

		// ***********************
		// * READ ITEMIDENTIFIER *
		// ***********************

		/**
		 * Query to read all item-identifiers of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_ITEM_IDENTIFIERS = "SELECT l.id, l.reference FROM locators AS l, rel_item_identifiers AS r WHERE r.id_construct = ? AND r.id_locator = l.id";

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
		// TODO
		public static final String QUERY_READ_NAMES_WITH_TYPE = "SELECT id FROM names WHERE id_parent = ? AND id_type  = ?";
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
		// TODO
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
		// TODO
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
		// TODO
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
		public static final String QUERY_READ_NAME_REIFIER = "SELECT id_reifier FROM names WHERE id = ?";

		/**
		 * query to read the reifier of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCE_REIFIER = "SELECT id_reifier FROM occurrences WHERE id = ?";

		/**
		 * query to read the reifier of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_VARIANT_REIFIER = "SELECT id_reifier FROM variants WHERE id = ?";

		/**
		 * query to read the reifier of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_ROLE_REIFIER = "SELECT id_reifier FROM roles WHERE id = ?";

		/**
		 * query to read the reifier of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_ASSOCIATION_REIFIER = "SELECT id_reifier FROM associations WHERE id = ?";

		/**
		 * query to read the reifier of a construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_TOPICMAP_REIFIER = "SELECT id_reifier FROM topicmaps WHERE id = ?";

		/**
		 * query to read the reified construct of a topic
		 * <p>
		 * <b>parameters(1):</b> the reifier id
		 * </p>
		 */
		public static final String QUERY_READ_REIFIED = "SELECT id, id_parent, 0 AS other, 'a' AS type FROM associations WHERE id_reifier = ? " + "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'n' AS type FROM names WHERE id_reifier = ? " + "UNION " + "SELECT id, id_parent, 0 AS other, 'o' AS type FROM occurrences WHERE id_reifier = ? "
				+ "UNION " + "SELECT v.id, v.id_parent, n.id_parent, 'v' AS type FROM variants AS v, names AS n WHERE v.id_reifier = ? AND v.id_parent = n.id " + "UNION "
				+ "SELECT id, id_parent, 0 AS other, 'r' AS type FROM roles WHERE id_reifier = ? " + "UNION "
				+ "SELECT id, 0 AS id_parent, 0 AS other, 'tm' AS type FROM topicmaps WHERE id_reifier = ?;";

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
		// TODO
		public static String QUERY_READ_ROLES_WITH_TYPE = "SELECT id FROM roles WHERE id_parent = ? AND id_type = ? ";
		/**
		 * Query to read all played roles
		 * <p>
		 * <b>parameters(1):</b> player id
		 * </p>
		 */
		public static String QUERY_READ_PLAYED_ROLES = "SELECT id, id_parent FROM roles WHERE id_player = ? ";

		/**
		 * Query to read all played roles by type
		 * <p>
		 * <b>parameters(2):</b> player id, type id
		 * </p>
		 */
		// TODO
		public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE = "SELECT id, id_parent FROM roles WHERE id_player = ? AND id_type = ? ";

		/**
		 * Query to read all played roles by types
		 * <p>
		 * <b>parameters(3):</b> player id, type id, asso_type
		 * </p>
		 */
		// TODO
		public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE = "SELECT r.id, r.id_parent FROM roles AS r, associations AS a WHERE r.id_player = ? AND r.id_type = ? AND r.id_parent = a.id AND a.id_type = ?  ";

		/**
		 * Query to read all roles types
		 * <p>
		 * <b>parameters(1):</b> association id
		 * </p>
		 */
		public static String QUERY_READ_ROLESTYPES = "SELECT DISTINCT id_type FROM roles WHERE id_parent = ? ";

		// *************************
		// * READ TOPIC IDENTIDIES *
		// *************************

		/**
		 * Query to read all subject-identifiers of a topic
		 * <p>
		 * <b>parameters(1):</b> topic id
		 * </p>
		 */
		public static final String QUERY_READ_SUBJECT_IDENTIFIERS = "SELECT l.id,reference FROM locators AS l, rel_subject_identifiers AS r, topics AS t WHERE r.id_topic = ? AND r.id_locator = l.id AND t.id = r.id_topic";
		/**
		 * Query to read all subject-locators of a topic
		 * <p>
		 * <b>parameters(1):</b> topic id
		 * </p>
		 */
		public static final String QUERY_READ_SUBJECT_LOCATORS = "SELECT l.id,reference FROM locators AS l, rel_subject_locators AS r , topics AS t WHERE r.id_topic = ? AND r.id_locator = l.id  AND t.id = r.id_topic";

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
		public static final String QUERY_READ_TOPICS_WITH_TYPE = "SELECT id_instance FROM rel_instance_of AS r, topics AS t WHERE id_instance = t.id AND id_type IN ( SELECT id FROM topics WHERE id = ? )";

		// **********************
		// * READ TYPE HIERACHY *
		// **********************

		/**
		 * Query to read the type of a typed construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_NAME_TYPE = "SELECT id_type FROM names AS ty WHERE id = ?";
		/**
		 * Query to read the type of a typed construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCE_TYPE = "SELECT id_type FROM occurrences WHERE id = ?";
		/**
		 * Query to read the type of a typed construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_ROLE_TYPE = "SELECT id_type FROM roles WHERE id = ?";
		/**
		 * Query to read the type of a typed construct
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_ASSOCIATION_TYPE = "SELECT id_type FROM associations WHERE id = ?";

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
		public static final String QUERY_READ_SUPERTYPES = "SELECT id_supertype AS id FROM rel_kind_of WHERE id_subtype = ?";

		// **************
		// * READ SCOPE *
		// **************

		/**
		 * Query to read the scope of a construct
		 * <p>
		 * <b>parameters(1):</b> the construct id
		 * </p>
		 */
		public static final String QUERY_READ_NAME_SCOPE = "SELECT DISTINCT id_scope FROM names WHERE id = ?";
		/**
		 * Query to read the scope of a construct
		 * <p>
		 * <b>parameters(1):</b> the construct id
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCE_SCOPE = "SELECT DISTINCT id_scope FROM occurrences WHERE id = ?";
		/**
		 * Query to read the scope of a construct
		 * <p>
		 * <b>parameters(1):</b> the construct id
		 * </p>
		 */
		public static final String QUERY_READ_VARIANT_SCOPE = "SELECT DISTINCT id_scope FROM variants WHERE id = ?";
		/**
		 * Query to read the scope of a construct
		 * <p>
		 * <b>parameters(1):</b> the construct id
		 * </p>
		 */
		public static final String QUERY_READ_ASSOCIATION_SCOPE = "SELECT DISTINCT id_scope FROM associations WHERE id = ?";

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
		 * <b>parameters(4):</b> an array of theme-IDs, boolean-flag matching all, boolean flag exact match, topic map
		 * id
		 * </p>
		 */
		public static final String QUERY_READ_SCOPES_BY_THEME = "SELECT DISTINCT id_scope FROM rel_themes AS r WHERE id_theme = ? AND ? IN ( SELECT count ( id_theme ) FROM rel_themes WHERE id_scope = r.id_scope );";

		public static String QUERY_READ_USED_SCOPES_BY_THEME = "SELECT DISTINCT id_scope FROM rel_themes AS r WHERE id_theme = ? AND " + "id_scope IN (" + "SELECT id_scope FROM names UNION "
				+ "SELECT id_scope FROM variants UNION " + "SELECT id_scope FROM occurrences UNION " + "SELECT id_scope FROM associations) ;";

		public static final String QUERY_READ_EMPTY_SCOPE = "SELECT id FROM scopes WHERE id NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) AND id_topicmap = ?";

		// **************
		// * READ VALUE *
		// **************

		/**
		 * Query to read the value of a name, an occurrence or a variant
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_NAME_VALUE = "SELECT DISTINCT value FROM names WHERE id = ?";

		/**
		 * Query to read the value of a name, an occurrence or a variant
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_OCCURRENCE_VALUE = "SELECT DISTINCT value FROM occurrences WHERE id = ?";

		/**
		 * Query to read the value of a name, an occurrence or a variant
		 * <p>
		 * <b>parameters(1):</b> construct id
		 * </p>
		 */
		public static final String QUERY_READ_VARIANT_VALUE = "SELECT DISTINCT value FROM variants WHERE id = ?";

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

		public static final String QUERY_READ_LOCATOR = "SELECT id  FROM locators WHERE reference = ?; ";
	}

}
