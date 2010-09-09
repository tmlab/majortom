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
public interface IConstraintsQueries {

	public static final String QUERY_DUPLICATE_NAME = "SELECT id, id_parent FROM names WHERE id_parent = ? AND id <> ? AND id_type = ? AND value = ? AND id_scope = ?";
	
	public static final String QUERY_MOVE_VARIANTS = "UPDATE variants SET id_parent = ? WHERE id_parent = ?;";
	
	public static final String QUERY_MOVE_ITEM_IDENTIFIERS = "UPDATE rel_item_identifiers SET id_construct = ? WHERE id_construct = ?;";

	public static final String QUERY_DUPLICATE_OCCURRENCE = "SELECT id, id_parent FROM occurrences WHERE id_parent = ? AND id <> ? AND id_type = ? AND value = ? AND id_datatype IN ( SELECT id FROM locators WHERE reference = ? ) AND id_scope = ?";

	public static final String QUERY_DUPLICATE_VARIANTS = "SELECT id, id_parent FROM variants WHERE id_parent = ? AND id <> ? AND value = ? AND id_datatype IN ( SELECT id FROM locators WHERE reference = ? ) AND id_scope = ?";
	
	public static final String QUERY_DUPLICATE_ASSOCIATIONS = "SELECT DISTINCT  a.id, a.id_reifier FROM roles  AS r, associations AS a WHERE 0 IN ( SELECT COUNT (r) FROM ( SELECT id_type , id_player FROM roles WHERE id_parent = r.id_parent EXCEPT SELECT id_type, id_player FROM roles WHERE id_parent = ? ) AS r ) AND r.id_parent <> ? AND r.id_parent = a.id AND a.id_type = ? AND a.id_scope = ?;";
	
	public static final String QUERY_DUPLICATE_ROLES = "SELECT id FROM roles WHERE id_parent = ? AND id_type = ? AND id_player = ?;";

}
