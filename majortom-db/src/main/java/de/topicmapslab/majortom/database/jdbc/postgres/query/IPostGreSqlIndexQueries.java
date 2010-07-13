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
public interface IPostGreSqlIndexQueries {

	interface QueryTypeInstanceIndex {
		
		public static final String QUERY_SELECT_ASSOCIATIONTYPES = "SELECT DISTINCT id_type FROM associations WHERE id_topicmap = ?;";
		
		public static final String QUERY_SELECT_NAMETYPES = "SELECT DISTINCT id_type FROM names WHERE id_topicmap = ?;";
		
		public static final String QUERY_SELECT_OCCURRENCETYPES = "SELECT DISTINCT id_type FROM occurrences WHERE id_topicmap = ?;";
		
		public static final String QUERY_SELECT_ROLETYPES = "SELECT DISTINCT id_type FROM roles WHERE id_topicmap = ?;";
		
		public static final String QUERY_SELECT_TOPICTYPES = "SELECT DISTINCT id_type FROM rel_instance_of, topics WHERE id_topicmap = ? AND id = id_type;";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ?;";

		public static final String QUERY_SELECT_ROLES_BY_TYPE = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id_type = ?;";

		public static final String QUERY_SELECT_NAMES_BY_TYPE = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND id_type = ?;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_TYPE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id_type = ?;";

		public static final String QUERY_SELECT_TOPIC_WITHOUT_TYPE = "SELECT id FROM topics WHERE id_topicmap = ? AND NOT id IN ( SELECT id_instance FROM rel_instance_of )";

		public static final String QUERY_SELECT_TOPIC_BY_TYPE = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND id_topicmap = ? AND id_type = ?;";

		public static final String QUERY_SELECT_TOPIC_BY_TYPES = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND  id_topicmap = ? ";

	}
	
	interface QueryScopeIndex{
				
		public static final String QUERY_READ_SCOPES = "SELECT DISTINCT id_scope FROM rel_themes WHERE ";
	}

}
