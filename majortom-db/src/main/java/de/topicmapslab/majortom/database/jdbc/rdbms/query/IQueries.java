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
public interface IQueries {

	/**
	 * snippet to create a topic map with the base locator only if there is no
	 * topic map bound to this locator
	 * <p>
	 * <b>parameters(2):</b> the reference of the locator twice
	 * </p>
	 */
	public static final String SNIPPET_CREATE_TOPICMAP_IF_NOT_EXISTS = "INSERT INTO topicmaps (id_base_locator) SELECT id FROM locators WHERE reference = ? AND NOT EXISTS ( SELECT tm.id FROM topicmaps AS tm, locators AS l WHERE l.reference LIKE ? AND l.id = tm.id_base_locator); ";
		
	/**
	 * snippet to create a new topic only if there is not topic bound to the
	 * given subject-identifier
	 * <p>
	 * <b>parameters(3):</b> the topic map id, the parent id, the reference
	 * </p>
	 */
	public static final String SNIPPET_CREATE_TOPIC_IF_SUBJECT_IDENTIFIER_NOT_EXISTS = "INSERT INTO topics(id_topicmap,id_parent) VALUES( ?,? ) WHERE NOT EXISTS (SELECT t.id, l.id FROM topics AS t, locators AS l, rel_subject_identifiers as r WHERE reference = ? AND l.id = r.id_locator AND r.id_topic = t.id);";
}
