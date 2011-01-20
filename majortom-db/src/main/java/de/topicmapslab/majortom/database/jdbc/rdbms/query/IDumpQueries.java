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
			+ "SELECT id_topicmap, ?, id, id_parent, ?, id_type, id_reifier, id_player, 'r' FROM roles AS r WHERE id = ?";
	
	public static final String QUERY_DUMP_ROLE_SELECT = "SELECT id_topicmap, id, id_parent, id_type, id_reifier, id_player FROM roles WHERE id = ?"; 
	
	public static final String QUERY_DUMP_ROLE_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_player, type) "
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//ts
			+ "?,"//if_rei
			+ "?,"//id_pl
			+ "'r')";//t

	public static final String QUERY_DUMP_ASSOCIATION = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, roles, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ?, id_type, id_reifier, id_scope, ?, ?, 'a' FROM associations AS a WHERE id = ?;";
	
	public static final String QUERY_DUMP_ASSOCIATION_SELECT = "SELECT id_topicmap, id, id_parent, id_type, id_reifier, id_scope FROM associations WHERE id = ?;";
	
	public static final String QUERY_DUMP_ASSOCIATION_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, roles, type) " 
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//ts
			+ "?,"//if_rei
			+ "?,"//id_sc
			+ "?,"//th
			+ "?,"//ro
			+ "'t')";//t

	public static final String QUERY_DUMP_VARIANT = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, id_reification, id_scope,themes, datatype, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ?, id_reifier, id_scope, ?, (SELECT reference FROM locators WHERE id = ?), value, 'v' FROM variants AS v WHERE id = ?";
	
	public static final String QUERY_DUMP_VARIANT_SELECT = "SELECT id_topicmap, id, id_parent, "  
			+ "id_reifier, id_scope, "
			+ " value "
			+ "FROM variants WHERE id = ?"; 

	public static final String QUERY_DUMP_VARIANT_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, id_reification, id_scope,themes, datatype, value, type) "
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//if_rei
			+ "?,"//id_sc
			+ "?,"//id_th
			+ "(SELECT reference FROM locators WHERE id = ?),"//dt
			+ "?,"//val
			+ "'v')";//t
	
	public static final String QUERY_DUMP_NAME = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, variants, id_reification, id_scope, themes, value, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, ?, id_type, ?, id_reifier, id_scope, ?, value, 'n' FROM names AS n WHERE id = ?";

	public static final String QUERY_DUMP_NAME_SELECT = "SELECT id_topicmap, id, id_parent, " 
			+ "id_type, "
			+ "id_reifier, id_scope,  "
			+ "value "
			+ "FROM names AS n WHERE id = ?"; 

	public static final String QUERY_DUMP_NAME_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, variants, id_reification, id_scope, themes, value, type) "
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//types
			+ "?,"//variants
			+ "?,"//id_rei
			+ "?,"//id_sco
			+ "?,"//themes
			+ "?,"//val
			+ "'n')";//t
		
	public static final String QUERY_DUMP_OCCURRENCE = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, value, datatype, type) "
			+ "SELECT id_topicmap, ?, id, id_parent, " 
			+ "?," //ii
			+ "id_type, id_reifier, id_scope,  "
			+ "?," // themes
			+ "value, "
			+ "(SELECT reference FROM locators WHERE id = ?)," //datatype
			+ "'o' "
			+ "FROM occurrences AS o WHERE id = ?";

	public static final String QUERY_DUMP_OCCURRENCE_SELECT = "SELECT id_topicmap, id, id_parent, " 
			+ "id_type, id_reifier, id_scope,  "
			+ "value "
			+ "FROM occurrences WHERE id = ?";
	
	public static final String QUERY_DUMP_OCCURRENCE_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, types, id_reification, id_scope, themes, value, datatype, type) "
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//types
			+ "?,"//id_rei
			+ "?,"//id_sco
			+ "?,"//themes
			+ "?,"//val
			+ "(SELECT reference FROM locators WHERE id = ?),"//dt
			+ "'o')";//t
	
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
	
	public static final String QUERY_DUMP_TOPIC_SELECT = "SELECT id_topicmap, id, id_parent FROM topics WHERE id = ?";
	
	public static final String QUERY_DUMP_TOPIC_INSERT_INTO_HISTORY = "INSERT INTO history(id_topicmap, id_revision, id, id_parent, itemidentifiers, subjectidentifiers, subjectlocators, types, supertypes, names, occurrences, associations, id_reification, type, bestlabel, bestIdentifier) "
			+ "VALUES(?,"//id_tm
			+ "?,"//id_rev
			+ "?,"//id
			+ "?,"//id_par
			+ "?,"//ii
			+ "?,"//si
			+ "?,"//sl
			+ "?,"//types
			+ "?,"//stypes
			+ "?,"//names
			+ "?,"//occs
			+ "?,"//assocs
			+ "?,"//id_rei
			+ "'t',"//t
			+ "?,"//bl
			+ "?)";//bi
}
