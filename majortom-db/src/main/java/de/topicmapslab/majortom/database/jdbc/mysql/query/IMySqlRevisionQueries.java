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
package de.topicmapslab.majortom.database.jdbc.mysql.query;

/**
 * @author Sven Krosse
 * 
 */
public interface IMySqlRevisionQueries {
	
	public static final String QUERY_CREATE_REVISION = "INSERT INTO revisions(`time`, id_topicmap) VALUES(now(),?)";

	public static final String QUERY_CREATE_CHANGESET = "INSERT INTO changesets(id_revision,type,id_notifier, newValue, oldValue,`time`) VALUES(?,?,?,?,?,now())";

	public static final String QUERY_CREATE_TAG = "INSERT INTO tags(tag, time) VALUES (?,?);";
		
	public static final String QUERY_CREATE_METADATA = "INSERT INTO metadata(id_revision, `key`, `value`) VALUES (?,?,?);";

	public static final String QUERY_MODIFY_METADATA = "UPDATE metadata SET value = ? WHERE id_revision = ? AND key = ?;";
	
	public static final String QUERY_MODIFY_TAG = "UPDATE tags SET time = ? WHERE tag = ?;";
	
	
	public static final String QUERY_READ_FIRST_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ? ORDER BY id ASC LIMIT 1;";

	public static final String QUERY_READ_LAST_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ? ORDER BY id DESC LIMIT 1;";

	public static final String QUERY_READ_PAST_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ?  AND id < ? ORDER BY id DESC LIMIT 1;";

	public static final String QUERY_READ_FUTURE_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ? AND id > ? ORDER BY id ASC LIMIT 1;";

	public static final String QUERY_READ_LAST_MODIFICATION = "SELECT time FROM revisions WHERE id_topicmap = ? ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_LAST_MODIFICATION_OF_TOPIC = "SELECT time FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ) ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_CHANGESET = "SELECT type, id_notifier, newValue, oldValue FROM changesets WHERE id_revision = ?;";

	public static final String QUERY_READ_TIMESTAMP = "SELECT time FROM revisions WHERE id = ?;";

	public static final String QUERY_READ_REVISIONS_BY_TOPIC = "SELECT id FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ) ORDER BY id ASC ;";

	public static final String QUERY_READ_REVISIONS_BY_ASSOCIATIONTYPE = "WITH ids AS ( SELECT id FROM associations WHERE id_type = ? ) SELECT id FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier IN ( SELECT id FROM ids ) OR oldValue IN ( SELECT CAST ( id AS character varying ) FROM ids ) OR newValue IN ( SELECT CAST ( id AS character varying ) FROM ids ) ) ORDER BY id ASC ;";

	public static final String QUERY_READ_CHANGESETS_BY_TOPIC = "SELECT id_revision, type, id_notifier, newValue, oldValue FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ORDER BY id ASC;";

	public static final String QUERY_READ_CHANGESETS_BY_ASSOCIATIONTYPE = "WITH ids AS ( SELECT id FROM associations WHERE id_type = ? ) SELECT id_revision, type, id_notifier, newValue, oldValue FROM changesets WHERE id_notifier IN ( SELECT id FROM ids ) OR oldValue IN ( SELECT CAST ( id AS character varying ) FROM ids ) OR newValue IN ( SELECT CAST ( id AS character varying ) FROM ids ) ORDER BY id ASC;";

	public static final String QUERY_READ_REVISION_BY_TIMESTAMP = "SELECT id FROM revisions WHERE id_topicmap = ? AND time <= ? ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_REVISION_BY_TAG = "SELECT id FROM revisions WHERE id_topicmap = ? AND time <= ( SELECT time FROM tags WHERE tag = ? ) ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_METADATA = "SELECT key, value FROM metadata WHERE id_revision = ?;";

	public static final String QUERY_READ_METADATA_BY_KEY = "SELECT value FROM metadata WHERE id_revision = ? AND key = ?;";
	
	
	public static final String QUERY_READ_HISTORY = "SELECT * FROM history WHERE id = ?;";
}
