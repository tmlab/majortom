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

import static de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlQueries.SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS;
import static de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlQueries.SNIPPET_CREATE_TOPICMAP_IF_NOT_EXISTS;

/**
 * @author Sven Krosse
 * 
 */
public interface IPostGreSqlInsertQueries {

	/**
	 * query to create a topic map by base locator if there is not a topic map
	 * bound to the locator.
	 * <p>
	 * <b>parameters(4):</b>base locator 4x
	 * </p>
	 */
	public static final String QUERY_CREATE_TOPICMAP = SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS + SNIPPET_CREATE_TOPICMAP_IF_NOT_EXISTS;

	/**
	 * query to create an association
	 * <p>
	 * <b>parameters(3):</b> topic map id, parent id, type id
	 * </p>
	 */
	public static final String QUERY_CREATE_ASSOCIATION = "INSERT INTO associations(id_topicmap, id_parent, id_type) VALUES (?,?,?);";
	/**
	 * query to create an association with scope
	 * <p>
	 * <b>parameters(4):</b> topic map id, parent id, type id, scope id
	 * </p>
	 */
	public static final String QUERY_CREATE_ASSOCIATION_WITH_SCOPE = "INSERT INTO associations(id_topicmap, id_parent, id_type, id_scope) VALUES (?,?,?,?);";
	/**
	 * query to create a locator by reference if not exists and return the id
	 * <p>
	 * <b>parameters(2):</b> reference twice
	 * </p>
	 */
	public static final String QUERY_CREATE_LOCATOR = SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS;
	/**
	 * query to create a topic name with the name type
	 * <p>
	 * <b>parameters(4):</b> topic map id, topic id, type id, value
	 * </p>
	 */
	public static final String QUERY_CREATE_NAME = "INSERT INTO names (id_topicmap, id_parent, id_type, value) VALUES (?,?,?,?);";
	/**
	 * query to create a topic name with the name type and scope
	 * <p>
	 * <b>parameters(5):</b> topic map id, topic id, type id, value, scope id
	 * </p>
	 */
	public static final String QUERY_CREATE_NAME_WITH_SCOPE = "INSERT INTO names (id_topicmap, id_parent, id_type, value, id_scope) VALUES (?,?,?,?,?);";
	/**
	 * query to create an occurrence
	 * <p>
	 * <b>parameters(7):</b> reference twice, topic map id, topic id,type id,
	 * value, reference
	 * </p>
	 */
	public static final String QUERY_CREATE_OCCURRENCE = SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO occurrences (id_topicmap, id_parent, id_type, value, id_datatype) SELECT ?,?,?,?, l.id FROM locators AS l WHERE l.reference LIKE ?;";
	/**
	 * query to create an occurrence
	 * <p>
	 * <b>parameters(8):</b> reference twice, topic map id, parent id,type id,
	 * value, id_scope, reference
	 * </p>
	 */
	public static final String QUERY_CREATE_OCCURRENCE_WITH_SCOPE = SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO occurrences (id_topicmap, id_parent, id_type, value, id_datatype, id_scope) SELECT ?,?,?,?,id,? FROM locators WHERE reference LIKE ?;";
	/**
	 * query to create an association role
	 * <p>
	 * <b>parameters(4):</b> topic map id, association id,type id, player id
	 * </p>
	 */
	public static final String QUERY_CREATE_ROLE = "INSERT INTO roles(id_topicmap, id_parent, id_type, id_player) VALUES (?,?,?,?)";
	/**
	 * query to create a scope object
	 * <p>
	 * <b>parameters(1):</b> topic map id
	 * </p>
	 */
	public static final String QUERY_CREATE_SCOPE = "INSERT INTO scopes(id_topicmap) VALUES (?)";
	/**
	 * query to create a topic with two parameters
	 * <p>
	 * <b>parameters(2):</b> topic map id, topic map id
	 * </p>
	 */
	public static final String QUERY_CREATE_TOPIC = "INSERT INTO topics(id_topicmap, id_parent) VALUES (?,?);";
	/**
	 * query to create a variant
	 * <p>
	 * <b>parameters(7):</b> reference, reference, topic map id, parent id,
	 * value, id_scope, reference
	 * </p>
	 */
	public static final String QUERY_CREATE_VARIANT = SNIPPET_CREATE_LOCATOR_IF_NOT_EXISTS
			+ "INSERT INTO variants (id_topicmap, id_parent, value, id_datatype, id_scope) SELECT ?,?,?,id,? FROM locators WHERE reference LIKE ?;";
}
