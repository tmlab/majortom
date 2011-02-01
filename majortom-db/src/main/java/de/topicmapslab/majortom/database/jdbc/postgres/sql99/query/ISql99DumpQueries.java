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
package de.topicmapslab.majortom.database.jdbc.postgres.sql99.query;

/**
 * @author Sven Krosse
 * 
 */
public interface ISql99DumpQueries {

	public static final String QUERY_DUMP_ROLE = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_player, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_item_identifiers WHERE id_construct = r.id )), ARRAY[id_type], id_reifier, id_player, 'r' "
			+ "FROM roles AS r WHERE id = ?";

	public static final String QUERY_DUMP_ASSOCIATION = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, roles, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_item_identifiers WHERE id_construct = a.id )), ARRAY[id_type], id_reifier, id_scope, ARRAY( SELECT id_theme FROM rel_themes WHERE id_scope = a.id_scope ), ARRAY( SELECT id FROM roles WHERE id_parent = a.id ), 'a' "
			+ "FROM associations AS a WHERE id = ?;";

	public static final String QUERY_DUMP_VARIANT = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, id_reification, id_scope,themes, datatype, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_item_identifiers WHERE id_construct = v.id )), id_reifier, id_scope, ARRAY( SELECT id_theme FROM rel_themes WHERE id_scope = v.id_scope ),  ( SELECT reference FROM locators WHERE id = v.id_datatype), value, 'v' "
			+ "FROM variants AS v WHERE id = ?";

	public static final String QUERY_DUMP_NAME = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, variants, id_reification, id_scope, themes, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_item_identifiers WHERE id_construct = n.id )), ARRAY[id_type], ARRAY ( SELECT id FROM variants WHERE id_parent = n.id ), id_reifier, id_scope,  ARRAY( SELECT id_theme FROM rel_themes WHERE id_scope = n.id_scope ), value, 'n' "
			+ "FROM names AS n WHERE id = ?";

	public static final String QUERY_DUMP_OCCURRENCE = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, value, datatype, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_item_identifiers WHERE id_construct = o.id )), ARRAY[id_type], id_reifier, id_scope,  ARRAY( SELECT id_theme FROM rel_themes WHERE id_scope = o.id_scope ), value, ( SELECT reference FROM locators WHERE id = o.id_datatype), 'o' "
			+ "FROM occurrences AS o WHERE id = ?";

	public static final String QUERY_DUMP_TOPIC = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, subjectidentifiers, subjectlocators, types, supertypes, names, occurrences, associations, id_reification, type, bestlabel, bestidentifier) "
			+ "SELECT id_topicmap, ?, id, id_parent, "
			+ "ARRAY ( SELECT reference FROM locators WHERE id IN ( SELECT id_locator FROM rel_item_identifiers WHERE id_construct = t.id )), "
			+ "ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_subject_identifiers WHERE id_topic = t.id )), "
			+ "ARRAY ( SELECT reference FROM locators WHERE id IN (SELECT id_locator FROM rel_subject_locators WHERE id_topic = t.id )), "
			+ "ARRAY ( SELECT id_type FROM rel_instance_of WHERE id_instance = t.id ), "
			+ "ARRAY ( SELECT id_supertype FROM rel_kind_of WHERE id_subtype = t.id ), "
			+ "ARRAY ( SELECT id FROM names WHERE id_parent = t.id ), "
			+ "ARRAY ( SELECT id FROM occurrences WHERE id_parent = t.id ), "
			+ "ARRAY ( SELECT DISTINCT id FROM associations WHERE id IN ( SELECT id_parent FROM roles WHERE id_player = t.id )), "
			+ "(SELECT CASE WHEN ( t.id IN ( SELECT id_reifier FROM reifiables WHERE id_reifier = t.id)) THEN ( SELECT id FROM reifiables WHERE id_reifier = t.id ) ELSE NULL END),"
			+ "'t', ?, ? FROM topics AS t WHERE id = ?";
}
