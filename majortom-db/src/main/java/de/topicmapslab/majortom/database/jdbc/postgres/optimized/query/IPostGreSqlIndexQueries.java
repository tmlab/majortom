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

import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;

/**
 * @author Sven Krosse
 * 
 */
public interface IPostGreSqlIndexQueries {

	/**
	 * Query definitions to realize methods of
	 * {@link ITransitiveTypeInstanceIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QueryTransitiveTypeInstanceIndex {

		interface Paged {

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPE = "SELECT id FROM associations WHERE id_type IN ( SELECT unnest(types_and_subtypes(?))) ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_ROLES_BY_TYPE = "SELECT id, id_parent FROM roles WHERE id_type IN ( SELECT unnest(types_and_subtypes(?))) ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_NAMES_BY_TYPE = "SELECT id, id_parent FROM names WHERE  id_type IN ( SELECT unnest(types_and_subtypes(?))) ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPE = "SELECT id, id_parent FROM occurrences WHERE  id_type IN ( SELECT unnest(types_and_subtypes(?))) ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_TOPICS_BY_TYPE = "SELECT id_instance AS id FROM rel_instance_of, topics WHERE id = id_instance AND id_type IN ( SELECT unnest(types_and_subtypes(?))) ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_TOPICS_BY_TYPES = "SELECT unnest(topics_by_type_transitive(?,?)) AS id ORDER BY id OFFSET ? LIMIT ?;";

		}

		interface NonPaged {

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPE = "SELECT id FROM associations WHERE id_type IN ( SELECT unnest(types_and_subtypes(?))) ;";

			public static final String QUERY_SELECT_ROLES_BY_TYPE = "SELECT id, id_parent FROM roles WHERE id_type IN ( SELECT unnest(types_and_subtypes(?)));";

			public static final String QUERY_SELECT_NAMES_BY_TYPE = "SELECT id, id_parent FROM names WHERE id_type IN ( SELECT unnest(types_and_subtypes(?)));";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPE = "SELECT id, id_parent FROM occurrences WHERE id_type IN ( SELECT unnest(types_and_subtypes(?)));";

			public static final String QUERY_SELECT_TOPICS_BY_TYPE = "SELECT id_instance AS id FROM rel_instance_of, topics WHERE id = id_instance AND id_type IN ( SELECT unnest(types_and_subtypes(?)));";

			public static final String QUERY_SELECT_TOPICS_BY_TYPES = "SELECT unnest(topics_by_type_transitive(?,?)) AS id;";

		}

	}

	/**
	 * Query definitions to realize methods of {@link IScopedIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QueryScopeIndex {

		public static final String QUERY_SELECT_SCOPES_BY_THEMES_USED = "SELECT r.id, ARRAY ( SELECT id_theme FROM rel_themes WHERE id_scope = r.id ) AS themes FROM ( SELECT unnest(scope_by_themes(?,?,?,?)) AS id INTERSECT SELECT id_scope AS id FROM scopeables ) AS r;";

	}

	/**
	 * Query definitions to realize methods of {@link ISupertypeSubtypeIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QuerySupertypeSubtypeIndex {

		interface Paged {

			public static final String QUERY_SELECT_SUBTYPES_OF_TOPIC = "SELECT unnest(transitive_subtypes(?)) AS id ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_SUBTYPES_OF_TOPICS = "SELECT unnest(transitive_subtypes(?,?)) AS id ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_SUPERTYPES_OF_TOPIC = "SELECT unnest(transitive_supertypes(?)) AS id ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_SUPERTYPES_OF_TOPICS = "SELECT unnest(transitive_supertypes(?,?)) AS id ORDER BY id OFFSET ? LIMIT ?;";
		}

		interface NonPaged {
			
			public static final String QUERY_SELECT_SUBTYPES_OF_TOPIC = "SELECT unnest(transitive_subtypes(?)) AS id;";

			public static final String QUERY_SELECT_SUBTYPES_OF_TOPICS = "SELECT unnest(transitive_subtypes(?,?)) AS id;";

			public static final String QUERY_SELECT_SUPERTYPES_OF_TOPIC = "SELECT unnest(transitive_supertypes(?)) AS id;";

			public static final String QUERY_SELECT_SUPERTYPES_OF_TOPICS = "SELECT unnest(transitive_supertypes(?,?)) AS id;";
		}

	}
}
