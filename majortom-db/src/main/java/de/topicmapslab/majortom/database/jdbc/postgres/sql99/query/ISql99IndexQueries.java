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

import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;

/**
 * @author Sven Krosse
 * 
 */
public interface ISql99IndexQueries {

	/**
	 * Query definitions to realize methods of {@link ITypeInstanceIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QueryTypeInstanceIndex {

		interface NonPaged {

			public static final String QUERY_SELECT_ASSOCIATIONTYPES = "SELECT DISTINCT id_type FROM associations WHERE id_topicmap = ?  ;";

			public static final String QUERY_SELECT_NAMETYPES = "SELECT DISTINCT id_type FROM names WHERE id_topicmap = ?  ;";

			public static final String QUERY_SELECT_OCCURRENCETYPES = "SELECT DISTINCT id_type FROM occurrences WHERE id_topicmap = ?  ;";

			public static final String QUERY_SELECT_CHARACTERISTICTYPES = "SELECT DISTINCT id_type FROM ( SELECT DISTINCT id_type FROM occurrences WHERE id_topicmap = ? UNION SELECT DISTINCT id_type FROM names WHERE id_topicmap = ? ) AS u;";

			public static final String QUERY_SELECT_ROLETYPES = "SELECT DISTINCT id_type FROM roles WHERE id_topicmap = ?  ;";

			public static final String QUERY_SELECT_TOPICTYPES = "SELECT DISTINCT id_type FROM rel_instance_of, topics WHERE id_topicmap = ? AND id = id_type   AND id_instance IN ( SELECT id FROM topics AS t WHERE t.id = id_instance );";

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ?  ;";

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPES = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ;";

			public static final String QUERY_SELECT_CHARACTERISTICS_BY_TYPE = "SELECT id, id_parent, type FROM ( SELECT id, id_parent, 'o' AS type FROM occurrences WHERE id_type = ? UNION SELECT id, id_parent, 'n' AS type FROM names WHERE id_type = ? ) AS u;";

			public static final String QUERY_SELECT_CHARACTERISTICS_BY_TYPES = "SELECT id, id_parent, type FROM ( SELECT id, id_parent, 'o' AS type FROM occurrences WHERE id_type IN ( SELECT unnest( ? ) ) UNION SELECT id, id_parent, 'n' AS type FROM names WHERE id_type IN ( SELECT unnest( ? ) ) ) AS u;";

			public static final String QUERY_SELECT_ROLES_BY_TYPE = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id_type = ?  ;";

			public static final String QUERY_SELECT_ROLES_BY_TYPES = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ;";

			public static final String QUERY_SELECT_NAMES_BY_TYPE = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND  id_type = ?  ;";

			public static final String QUERY_SELECT_NAMES_BY_TYPES = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND  id_type IN ( SELECT unnest( ? ) )  ;";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id_type = ?  ;";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPES = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ;";

			public static final String QUERY_SELECT_TOPIC_WITHOUT_TYPE = "SELECT id FROM topics WHERE id_topicmap = ? AND NOT id IN ( SELECT id_instance FROM rel_instance_of )   ";

			public static final String QUERY_SELECT_TOPIC_BY_TYPE = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND id_topicmap = ? AND id_type = ?   ;";

			public static final String QUERY_SELECT_TOPIC_BY_TYPES = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND  id_topicmap = ? AND ( %SUBQUERY% )   ";

			public static final String QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL = "SELECT DISTINCT id_instance FROM rel_instance_of AS r, topics WHERE id = id_instance AND  id_topicmap = ?  AND  ARRAY ( SELECT id_type FROM rel_instance_of AS r2 WHERE r.id_instance = r2.id_instance ) @> CAST ( ARRAY[ %ARRAY%] AS bigint[] )   ";
		}

		interface Paged {

			public static final String QUERY_SELECT_ASSOCIATIONTYPES = "SELECT DISTINCT id_type FROM associations WHERE id_topicmap = ? ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_NAMETYPES = "SELECT DISTINCT id_type FROM names WHERE id_topicmap = ?   ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_OCCURRENCETYPES = "SELECT DISTINCT id_type FROM occurrences WHERE id_topicmap = ?   ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_CHARACTERISTICTYPES = "SELECT DISTINCT id_type FROM ( SELECT DISTINCT id_type FROM occurrences WHERE id_topicmap = ? UNION SELECT DISTINCT id_type FROM names WHERE id_topicmap = ? ) AS u ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_ROLETYPES = "SELECT DISTINCT id_type FROM roles WHERE id_topicmap = ?   ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_TOPICTYPES = "SELECT DISTINCT id_type FROM rel_instance_of, topics WHERE id_topicmap = ? AND id = id_type   AND id_instance IN ( SELECT id FROM topics AS t WHERE t.id = id_instance ) ORDER BY id_type OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type = ?  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_ASSOCIATIONS_BY_TYPES = "SELECT id FROM associations WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_CHARACTERISTICS_BY_TYPE = "SELECT id, id_parent, type FROM ( SELECT id, id_parent, 'o' AS type FROM occurrences WHERE id_type = ? UNION SELECT id, id_parent, 'n' AS type FROM names WHERE id_type = ? ) AS u ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_CHARACTERISTICS_BY_TYPES = "SELECT id, id_parent, type FROM ( SELECT id, id_parent, 'o' AS type FROM occurrences WHERE id_type IN ( SELECT unnest( ? ) ) UNION SELECT id, id_parent, 'n' AS type FROM names WHERE id_type IN ( SELECT unnest( ? ) ) ) AS u ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_ROLES_BY_TYPE = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id_type = ?  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_ROLES_BY_TYPES = "SELECT id, id_parent FROM roles WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_NAMES_BY_TYPE = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND  id_type = ?  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_NAMES_BY_TYPES = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND  id_type IN ( SELECT unnest( ? ) )  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id_type = ?  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_OCCURRENCES_BY_TYPES = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND id_type IN ( SELECT unnest( ? ) )  ORDER BY id OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_TOPIC_WITHOUT_TYPE = "SELECT id FROM topics WHERE id_topicmap = ? AND NOT id IN ( SELECT id_instance FROM rel_instance_of )  ORDER BY id OFFSET ? LIMIT ?  ";

			public static final String QUERY_SELECT_TOPIC_BY_TYPE = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND id_topicmap = ? AND id_type = ?   ORDER BY id_instance OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_TOPIC_BY_TYPES = "SELECT id_instance FROM rel_instance_of, topics WHERE id = id_instance AND  id_topicmap = ? AND ( %SUBQUERY% )  ORDER BY id_instance OFFSET ? LIMIT ?  ";

			public static final String QUERY_SELECT_TOPIC_BY_TYPES_MATCHES_ALL = "SELECT DISTINCT id_instance FROM rel_instance_of AS r, topics WHERE id = id_instance AND  id_topicmap = ?  AND  ARRAY ( SELECT id_type FROM rel_instance_of AS r2 WHERE r.id_instance = r2.id_instance ) @> CAST ( ARRAY[ %ARRAY%] AS bigint[] )   ORDER BY id_instance OFFSET ? LIMIT ? ";
		}

	}

	/**
	 * Query definitions to realize methods of {@link IScopedIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QueryScopeIndex {

		public static final String QUERY_SELECT_SCOPES_BY_THEMES_USED = "SELECT r.id_scope FROM rel_themes AS r, scopeables AS s, constructs AS c WHERE id_theme = ? AND r.id_scope = s.id_scope AND c.id = s.id;";

		public static final String QUERY_SELECT_SCOPES = "SELECT DISTINCT id_scope FROM rel_themes WHERE id_scope IN ( SELECT DISTINCT id_scope FROM scopeables ) AND ";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_EMPTYSCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND ( id_scope = NULL OR id_scope NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) )   ;";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_SCOPE = "SELECT id FROM associations WHERE id_topicmap = ? AND id_scope = ?  ;";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_SCOPES = "SELECT id FROM associations WHERE id_topicmap = ?    AND ";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_THEME = "SELECT id FROM associations WHERE id_topicmap = ? AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE id_theme = ? )   ;";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_THEMES = "SELECT id FROM associations WHERE id_topicmap = ? AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE %SUBQUERY% )   ; ";

		public static final String QUERY_SELECT_ASSOCIATIONS_BY_THEMES_MATCH_ALL = "SELECT id FROM associations AS a WHERE id_topicmap = ?   AND ARRAY ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = a.id_scope ) @> CAST ( ARRAY[%ARRAY%] AS bigint[] ); ";

		public static final String QUERY_SELECT_ASSOCIATION_SCOPES = "SELECT id_scope FROM associations WHERE id_topicmap = ?  ;";

		public static final String QUERY_SELECT_ASSOCIATION_THEMES = "SELECT id_theme FROM rel_themes WHERE id_scope IN ( SELECT id_scope FROM associations WHERE id_topicmap = ?    );";

		public static final String QUERY_SELECT_NAMES_BY_EMPTYSCOPE = "SELECT id, id_parent FROM names WHERE id_topicmap = ?    AND ( id_scope = NULL OR id_scope NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) );";

		public static final String QUERY_SELECT_NAMES_BY_SCOPE = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND id_scope = ?  ;";

		public static final String QUERY_SELECT_NAMES_BY_SCOPES = "SELECT id, id_parent FROM names WHERE id_topicmap = ?    AND ";

		public static final String QUERY_SELECT_NAMES_BY_THEME = "SELECT id, id_parent FROM names WHERE id_topicmap = ?    AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE id_theme = ? );";

		public static final String QUERY_SELECT_NAMES_BY_THEMES = "SELECT id, id_parent FROM names WHERE id_topicmap = ?    AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE %SUBQUERY% );";

		public static final String QUERY_SELECT_NAMES_BY_THEMES_MATCH_ALL = "SELECT id, id_parent FROM names AS n WHERE id_topicmap = ?    AND ARRAY ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = n.id_scope ) @> CAST ( ARRAY[%ARRAY%] AS bigint[] ); ";

		public static final String QUERY_SELECT_NAME_SCOPES = "SELECT id_scope FROM names WHERE id_topicmap = ?  ;";

		public static final String QUERY_SELECT_NAME_THEMES = "SELECT id_theme FROM rel_themes WHERE id_scope IN ( SELECT id_scope FROM names WHERE id_topicmap = ?   ) ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_EMPTYSCOPE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND ( id_scope = NULL OR id_scope NOT IN ( SELECT DISTINCT id_scope FROM rel_themes ) );";

		public static final String QUERY_SELECT_OCCURRENCES_BY_SCOPE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ?    AND id_scope = ?;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_SCOPES = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ?    AND ";

		public static final String QUERY_SELECT_OCCURRENCES_BY_THEME = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ?    AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE id_theme = ? );";

		public static final String QUERY_SELECT_OCCURRENCES_BY_THEMES = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ?    AND id_scope IN ( SELECT id_scope FROM rel_themes WHERE %SUBQUERY% ); ";

		public static final String QUERY_SELECT_OCCURRENCES_BY_THEMES_MATCH_ALL = "SELECT id, id_parent FROM occurrences AS o WHERE id_topicmap = ?   AND ARRAY ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = o.id_scope ) @> CAST ( ARRAY[%ARRAY%] AS bigint[] ); ";

		public static final String QUERY_SELECT_OCCURRENCE_SCOPES = "SELECT id_scope FROM occurrences WHERE id_topicmap = ?  ;";

		public static final String QUERY_SELECT_OCCURRENCE_THEMES = "SELECT id_theme FROM rel_themes WHERE id_scope IN ( SELECT id_scope FROM occurrences WHERE id_topicmap = ?  );";

		public static final String QUERY_SELECT_VARIANTS_BY_SCOPE = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.id_scope = ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_THEME = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ?    AND  ( v.id_scope IN ( SELECT id_scope FROM rel_themes WHERE id_theme = ? ) OR n.id_scope IN ( SELECT id_scope FROM rel_themes WHERE id_theme = ? ));";

		public static final String QUERY_SELECT_VARIANTS_BY_THEMES = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ?   AND ( v.id_scope IN ( SELECT id_scope FROM rel_themes WHERE %SUBQUERY% ) OR n.id_scope IN ( SELECT id_scope FROM rel_themes WHERE %SUBQUERY% )); ";

		public static final String QUERY_SELECT_VARIANTS_BY_THEMES_MATCH_ALL = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ?   AND ARRAY ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = n.id_scope ) || ARRAY ( SELECT id_theme FROM rel_themes AS r WHERE r.id_scope = v.id_scope ) @> CAST ( ARRAY[%ARRAY%] AS bigint[] )";

		public static final String QUERY_SELECT_VARIANT_SCOPES = "SELECT id_scope FROM variants WHERE id_topicmap = ?   ;";

		public static final String QUERY_SELECT_VARIANT_THEMES = "SELECT id_theme FROM rel_themes WHERE id_scope IN ( SELECT id_scope FROM variants WHERE id_topicmap = ?   ) OR id_scope IN ( SELECT id_scope FROM names WHERE id_topicmap = ? AND id IN ( SELECT id_parent FROM variants ));";
	}

	/**
	 * Query definitions to realize methods of {@link ILiteralIndex}
	 * 
	 * @author Sven Krosse
	 */
	interface QueryLiteralIndex {

		public static final String QUERY_SELECT_NAMES = "SELECT id, id_parent FROM names WHERE id_topicmap = ?  ;";

		public static final String QUERY_SELECT_NAMES_BY_VALUE = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND value ILIKE ?  ;";

		public static final String QUERY_SELECT_NAMES_BY_REGEXP = "SELECT id, id_parent FROM names WHERE id_topicmap = ? AND value ~* ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_DATATYPE = "SELECT o.id, id_parent FROM occurrences AS o, locators AS l WHERE id_topicmap = ? AND o.id_datatype = l.id AND l.reference = ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_DATERANGE = "SELECT o.id, id_parent FROM  occurrences AS o WHERE id_topicmap = ? AND ? <= ( SELECT CASE WHEN ( o2.id = o.id AND o2.id_datatype = l2.id AND l2.reference = 'http://www.w3.org/2001/XMLSchema#dateTime' ) THEN ( CAST( value AS timestamp with time zone )) ELSE NULL END FROM occurrences AS o2, locators AS l2 WHERE o.id = o2.id AND o2.id_datatype = l2.id ) AND ? >= ( SELECT CASE WHEN ( o2.id = o.id AND o2.id_datatype = l2.id AND l2.reference = 'http://www.w3.org/2001/XMLSchema#dateTime' ) THEN ( CAST( value AS timestamp with time zone )) ELSE NULL END FROM occurrences AS o2, locators AS l2 WHERE o.id = o2.id AND o2.id_datatype = l2.id );";

		public static final String QUERY_SELECT_OCCURRENCES_BY_RANGE = "SELECT o.id, id_parent FROM  occurrences AS o, locators AS l WHERE id_topicmap = ? AND o.id_datatype = l.id AND l.reference = ? AND CAST ( value AS double precision ) BETWEEN ? AND ?   ";

		public static final String QUERY_SELECT_OCCURRENCES_BY_VALUE = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND value ILIKE ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_REGEXP = "SELECT id, id_parent FROM occurrences WHERE id_topicmap = ? AND value ~* ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_VALUE_AND_DATATYPE = "SELECT o.id, id_parent FROM occurrences AS o, locators AS l WHERE id_topicmap = ? AND value ILIKE ? AND o.id_datatype = l.id AND l.reference = ?  ;";

		public static final String QUERY_SELECT_OCCURRENCES_BY_REGEXP_AND_DATATYPE = "SELECT o.id, id_parent FROM occurrences AS o, locators AS l WHERE id_topicmap = ? AND value ~* ? AND o.id_datatype = l.id AND l.reference = ?  ;";

		public static final String QUERY_SELECT_VARIANTS = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_DATATYPE = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n, locators AS l  WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.id_datatype = l.id AND l.reference = ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_VALUE = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.value ILIKE ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_REGEXP = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.value ~* ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_VALUE_AND_DATATYPE = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n, locators AS l  WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.value ILIKE ? AND v.id_datatype = l.id AND l.reference = ?  ;";

		public static final String QUERY_SELECT_VARIANTS_BY_REGEXP_AND_DATATYPE = "SELECT v.id, v.id_parent, n.id_parent FROM variants AS v, names AS n, locators AS l  WHERE v.id_parent = n.id AND v.id_topicmap = ? AND v.value ~* ? AND v.id_datatype = l.id AND l.reference = ?  ;";
	}

	/**
	 * Query definitions to realize methods of {@link IIdentityIndex}
	 * 
	 * @author Sven Krosse
	 */
	interface QueryIdentityIndex {

		interface NonPaged {

			public static final String QUERY_SELECT_ITEM_IDENTIFIERS = "SELECT l.reference FROM locators AS l, rel_item_identifiers, constructs AS c WHERE ( c.id_topicmap = ? OR c.id = ? ) AND l.id = id_locator AND c.id = id_construct;";

			public static final String QUERY_SELECT_SUBJECT_IDENTIFIERS = "SELECT l.reference FROM locators AS l, rel_subject_identifiers, topics AS t WHERE t.id_topicmap = ? AND l.id = id_locator AND t.id = id_topic  ;";

			public static final String QUERY_SELECT_SUBJECT_LOCATORS = "SELECT l.reference FROM locators AS l, rel_subject_locators, topics AS t WHERE t.id_topicmap = ? AND l.id = id_locator AND t.id = id_topic  ;";

			public static final String QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN = "SELECT id_topic AS id, 't' AS type FROM topics AS t, rel_subject_identifiers, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id    AND l.id = id_locator AND reference ~* ? UNION SELECT id_topic AS id, 't' AS type FROM topics AS t, rel_subject_locators, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ?   UNION SELECT id_construct AS id, 'c' AS type FROM constructs AS c, rel_item_identifiers, locators AS l WHERE ( c.id_topicmap = ? OR c.id = ? ) AND id_construct = c.id AND l.id = id_locator AND reference ~* ?;";

			public static final String QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN = "SELECT id_construct FROM constructs AS c, rel_item_identifiers, locators AS l WHERE ( c.id_topicmap = ? OR c.id = ? ) AND id_construct = c.id AND l.id = id_locator AND reference ~* ? ;";

			public static final String QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN = "SELECT id_topic FROM topics AS t, rel_subject_identifiers, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ?  ;";

			public static final String QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN = "SELECT id_topic FROM topics AS t, rel_subject_locators, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ?  ;";
		}

		interface Paged {

			public static final String QUERY_SELECT_ITEM_IDENTIFIERS = "SELECT l.reference FROM locators AS l, rel_item_identifiers, constructs AS c WHERE ( c.id_topicmap = ? OR c.id = ? ) AND l.id = id_locator AND c.id = id_construct ORDER BY l.reference OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_SUBJECT_IDENTIFIERS = "SELECT l.reference FROM locators AS l, rel_subject_identifiers, topics AS t WHERE t.id_topicmap = ? AND l.id = id_locator AND t.id = id_topic ORDER BY l.reference OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_SUBJECT_LOCATORS = "SELECT l.reference FROM locators AS l, rel_subject_locators, topics AS t WHERE t.id_topicmap = ? AND l.id = id_locator AND t.id = id_topic ORDER BY l.reference OFFSET ? LIMIT ? ;";

			public static final String QUERY_SELECT_CONSTRUCTS_BY_IDENTIFIER_PATTERN = "SELECT id, type FROM ( SELECT id_topic AS id, 't' AS type FROM topics AS t, rel_subject_identifiers, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id    AND l.id = id_locator AND reference ~* ? UNION SELECT id_topic AS id, 't' AS type FROM topics AS t, rel_subject_locators, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ?   UNION SELECT id_construct AS id, 'c' AS type FROM constructs AS c, rel_item_identifiers, locators AS l WHERE ( c.id_topicmap = ? OR c.id = ? ) AND id_construct = c.id AND l.id = id_locator AND reference ~* ? ) AS u ORDER BY id OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_CONSTRUCTS_BY_ITEM_IDENTIFIER_PATTERN = "SELECT id_construct FROM constructs AS c, rel_item_identifiers, locators AS l WHERE ( c.id_topicmap = ? OR c.id = ? ) AND id_construct = c.id AND l.id = id_locator AND reference ~* ? ORDER BY id_construct OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_TOPICS_BY_SUBJECT_IDENTIFIER_PATTERN = "SELECT id_topic FROM topics AS t, rel_subject_identifiers, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ?  ORDER BY id_topic OFFSET ? LIMIT ?;";

			public static final String QUERY_SELECT_TOPICS_BY_SUBJECT_LOCATOR_PATTERN = "SELECT id_topic FROM topics AS t, rel_subject_locators, locators AS l WHERE t.id_topicmap = ? AND id_topic = t.id AND l.id = id_locator AND reference ~* ? ORDER BY id_topic OFFSET ? LIMIT ?;";
		}

	}

	/**
	 * Query definitions to realize methods of {@link ISupertypeSubtypeIndex}
	 * 
	 * @author Sven Krosse
	 * 
	 */
	interface QuerySupertypeSubtypeIndex {

		interface NonPaged {

			public static final String QUERY_SELECT_DIRECT_SUBTYPES = "SELECT id_subtype AS id FROM rel_kind_of WHERE id_supertype = ?;";

			public static final String QUERY_SELECT_TOPICS_WITHOUT_SUBTYPES = "SELECT id FROM topics WHERE id NOT IN ( SELECT id_supertype FROM rel_kind_of ) AND id_parent = ?;";

			public static final String QUERY_SELECT_SUBTYPES_OF_TOPIC = "SELECT id_subtype AS id FROM rel_kind_of WHERE id_supertype = ?;";

			public static final String QUERY_SELECT_SUBTYPES = "SELECT id_subtype FROM rel_kind_of, topics WHERE id = id_subtype AND id_topicmap = ?;";

			public static final String QUERY_SELECT_DIRECT_SUPERTYPES = "SELECT id_supertype AS id FROM rel_kind_of WHERE id_subtype = ?;";

			public static final String QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES = "SELECT id FROM topics WHERE id NOT IN ( SELECT id_subtype FROM rel_kind_of ) AND id_parent = ?;";

			public static final String QUERY_SELECT_SUPERTYPES_OF_TOPIC = "SELECT id_supertype AS id FROM rel_kind_of WHERE id_subtype = ?";

			public static final String QUERY_SELECT_SUPERTYPES = "SELECT id_supertype AS id  FROM rel_kind_of, topics WHERE id = id_subtype AND id_topicmap = ?;";

		}

		interface Paged {
			
			public static final String QUERY_SELECT_TOPICS_WITHOUT_SUPERTYPES = "SELECT id FROM topics WHERE id NOT IN ( SELECT id_subtype FROM rel_kind_of ) AND id_parent = ? ORDER BY id OFFSET ? LIMIT ?;";
			
			public static final String QUERY_SELECT_NUMBER_OF_TOPICS_WITHOUT_SUPERTYPES = "SELECT COUNT(id) AS number FROM topics WHERE id NOT IN ( SELECT id_subtype FROM rel_kind_of ) AND id_parent = ?;";
			
		}

	}
}
