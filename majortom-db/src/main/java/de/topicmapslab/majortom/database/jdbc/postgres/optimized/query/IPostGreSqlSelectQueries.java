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

	interface NonPaged {

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
		 * <b>parameters(4):</b> an array of theme-IDs, boolean-flag matching all, boolean flag exact match, topic map
		 * id
		 * </p>
		 */
		public static final String QUERY_READ_SCOPES_BY_THEMES = "SELECT r.id, ARRAY ( SELECT id_theme FROM rel_themes WHERE id_scope = r.id ) AS themes FROM ( SELECT unnest(scope_by_themes(?,?,?,?)) AS id ) AS r;";

		/**
		 * @since 1.1.2
		 */
		public static final String QUERY_READ_BEST_LABEL = "SELECT best_label(?,?);";

		/**
		 * @since 1.1.2
		 */
		public static final String QUERY_READ_BEST_LABEL_WITH_THEME = "SELECT best_label(?,?,?,?);";
	}

	interface Paged {
		/**
		 * Query to read the super types of a topic
		 * <p>
		 * <b>parameters(3):</b> topic id, offset, limit
		 * </p>
		 */
		public static final String QUERY_READ_SUPERTYPES = "SELECT unnest(transitive_supertypes(?)) AS id OFFSET ? LIMIT ?;";
	}
}
