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
public interface IDumpQueries {

	public static final String QUERY_DUMP_ROLE = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_player, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, " 
			+ "?," //ii
			+ "id_type, id_reifier, id_player, 'r' "
			+ "FROM roles AS r WHERE id = ?";

	public static final String QUERY_DUMP_ASSOCIATION = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, roles, type) "
			+ "SELECT id_topicmap, ?, id, id_parent,"  
			+ "?," // ii
			+ "id_type, id_reifier, id_scope,"
			+ "?," // themes
			+ "?," // roles 
			+ "'a'"
			+ "FROM associations AS a WHERE id = ?;";

	public static final String QUERY_DUMP_VARIANT = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, id_reification, id_scope,themes, datatype, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, "  
			+ "?," // ii
			+ "id_reifier, id_scope, "
			+ "?," // themes
			+ "?," //datatype
			+ " value, 'v' "
			+ "FROM variants AS v WHERE id = ?";

	public static final String QUERY_DUMP_NAME = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, variants, id_reification, id_scope, themes, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, " 
			+ "?," //ii
			+ "id_type, "
			+ "?," // variants
			+ "id_reifier, id_scope,  "
			+ "?," // themes
			+ "value, 'n' "
			+ "FROM names AS n WHERE id = ?";

	public static final String QUERY_DUMP_OCCURRENCE = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, value, datatype, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, " 
			+ "?," //ii
			+ "id_type, id_reifier, id_scope,  "
			+ "?," // themes
			+ "value, "
			+ "?," //datatype
			+ "'o' "
			+ "FROM occurrences AS o WHERE id = ?";

	public static final String QUERY_DUMP_TOPIC = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, subjectidentifiers, subjectlocators, types, supertypes, names, occurrences, associations, id_reification, type, bestlabel) "
			+ "SELECT id_topicmap, ?, id, id_parent, "
			+ "?," //ii
			+ "?," //si
			+ "?," //sl
			+ "?," //types
			+ "?," //supertypes
			+ "?," //names
			+ "?," //occurrences
			+ "?," //associations
			+ "?," // reified
			+ "'t', ? FROM topics AS t WHERE id = ?";
}
