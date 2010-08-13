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
package de.topicmapslab.majortom.database.jdbc.postgres.optimized.query;

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
	 * Query to read all associations of a specific type
	 * <p>
	 * <b>parameters(2):</b> topic map id, type id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?)))";
	/**
	 * Query to read all associations of a specific type and within a specific
	 * scope
	 * <p>
	 * <b>parameters(3):</b> topic map id, type id, scope id
	 * </p>
	 */
	public static String QUERY_READ_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?))) AND id_scope = ?";

	/**
	 * Query to read all played associations of a specific type
	 * <p>
	 * <b>parameters(3):</b> topic map id, player id, type id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_type IN ( SELECT unnest(types_and_subtypes(?)))";

	/**
	 * Query to read all played associations of a specific type and within a
	 * specific scope
	 * <p>
	 * <b>parameters(4):</b> topic map id, player id, type id, scope id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ASSOCIATIONS_WITH_TYPE_AND_SCOPE = "SELECT DISTINCT a.id FROM associations AS a, roles AS r WHERE a.id_topicmap = ? AND r.id_player = ? AND r.id_parent = a.id AND a.id_type IN ( SELECT unnest(types_and_subtypes(?))) AND a.id_scope = ?";


	// **************
	// * READ NAMES *
	// **************
	
	/**
	 * query to read all names of a topic by type
	 * <p>
	 * <b>parameters(2):</b> topic id, type id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES_WITH_TYPE = "SELECT id FROM names WHERE id_parent = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?)))";
	
	/**
	 * query to read all names of a topic by type and scope
	 * <p>
	 * <b>parameters(3):</b> topic id, type id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_NAMES_WITH_TYPE_AND_SCOPE = "SELECT id FROM names WHERE id_parent = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?))) AND id_scope = ?";

	// ********************
	// * READ OCCURRENCES *
	// ********************

	/**
	 * query to read all occurrences of a topic by type
	 * <p>
	 * <b>parameters(2):</b> topic id, type id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES_WITH_TYPE = "SELECT id FROM occurrences WHERE id_parent = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?)))";
	
	/**
	 * query to read all occurrences of a topic by type and scope
	 * <p>
	 * <b>parameters(3):</b> topic id, type id, scope id
	 * </p>
	 */
	public static final String QUERY_READ_OCCURRENCES_WITH_TYPE_AND_SCOPE = "SELECT id FROM occurrences WHERE id_parent = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?))) AND id_scope = ?";

	// **************
	// * READ ROLES *
	// **************

	/**
	 * Query to read all roles by type
	 * <p>
	 * <b>parameters(2):</b> association id, type id
	 * </p>
	 */
	public static String QUERY_READ_ROLES_WITH_TYPE = "SELECT id FROM roles WHERE id_parent = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?)))";

	/**
	 * Query to read all played roles by type
	 * <p>
	 * <b>parameters(2):</b> player id, type id
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE = "SELECT id, id_parent FROM roles WHERE id_player = ? AND id_type IN ( SELECT unnest(types_and_subtypes(?)))";

	/**
	 * Query to read all played roles by types
	 * <p>
	 * <b>parameters(3):</b> player id, type id, asso_type
	 * </p>
	 */
	public static String QUERY_READ_PLAYED_ROLES_WITH_TYPE_AND_ASSOTYPE = "SELECT r.id, r.id_parent FROM roles AS r, associations AS a WHERE r.id_player = ? AND r.id_type IN ( SELECT unnest(types_and_subtypes(?))) AND r.id_parent = a.id AND a.id_type IN ( SELECT unnest(types_and_subtypes(?)))";

	// **********************
	// * READ TYPE HIERACHY *
	// **********************

	/**
	 * Query to read the super types of a topic
	 * <p>
	 * <b>parameters(1):</b> topic id
	 * </p>
	 */
	public static final String QUERY_READ_SUPERTYPES = "SELECT unnest(transitive_supertypes(?)) AS id;"; 

	// **************
	// * READ SCOPE *
	// **************
	
	/**
	 * Query to read the scope object by a collection of themes
	 * <p>
	 * <b>parameters(4):</b> an array of theme-IDs, boolean-flag matching all, boolean flag exact match, topic map id 
	 * </p>
	 */
	public static final String QUERY_READ_SCOPES_BY_THEMES = "SELECT unnest(scope_by_themes(?,?,?,?)) AS id;";
}
