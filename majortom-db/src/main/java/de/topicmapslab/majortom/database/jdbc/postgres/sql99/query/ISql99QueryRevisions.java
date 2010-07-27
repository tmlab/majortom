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
public interface ISql99QueryRevisions {

	public static final String QUERY_CREATE_REVISION = "INSER INTO revisions(time, id_topicmap) VALUES(now(),?)";

	public static final String QUERY_CREATE_CHANGESET = "INSER INTO changesets(id_revision,type,id_notifier, newValue, oldValue,time) VALUES(?,?,?,?,?,now())";

	public static final String QUERY_READ_FIRST_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ? ORDER BY id ASC LIMIT 1;";

	public static final String QUERY_READ_LAST_REVISION = "SELECT id FROM revisions WHERE id_topicmap = ? ORDER BY id DESC LIMIT 1;";

	public static final String QUERY_READ_PAST_REVISION = "SELECT id FROM revisions WHERE id < ? AND id_topicmap = ? ORDER BY id DESC LIMIT 1;";

	public static final String QUERY_READ_FUTURE_REVISION = "SELECT id FROM revisions WHERE id > ? AND id_topicmap = ? ORDER BY id ASC LIMIT 1;";

	public static final String QUERY_READ_LAST_MODIFICATION = "SELECT time FROM revisions WHERE id_topicmap = ? ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_LAST_MODIFICATION_OF_TOPIC = "SELECT time FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ) ORDER BY time DESC LIMIT 1";

	public static final String QUERY_READ_CHANGESET = "SELECT type, id_notifier, newValue, oldValue FROM changesets WHERE id_revision = ?;";

	public static final String QUERY_READ_TIMESTAMP = "SELECT time FROM revisions WHERE id = ?;";

	public static final String QUERY_READ_REVISIONS_BY_TOPIC = "SELECT id FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ) ORDER BY id ASC ;";

	public static final String QUERY_READ_REVISIONS_BY_ASSOCIATIONTYPE = "WITH ids AS ( SELECT id FROM associations WHERE id_type = ? ) SELECT id FROM revisions WHERE id IN ( SELECT id_revision FROM changesets WHERE id_notifier IN ( SELECT id FROM ids ) OR oldValue IN ( SELECT id FROM ids ) OR newValue IN ( SELECT id FROM ids ) ) ORDER BY id ASC ;";

	public static final String QUERY_READ_CHANGESETS_BY_TOPIC = "SELECT id_revision, type, id_notifier, newValue, oldValue FROM changesets WHERE id_notifier = ? OR oldValue = ? OR newValue = ? ORDER BY id ASC;";

	public static final String QUERY_READ_CHANGESETS_BY_ASSOCIATIONTYPE = "WITH ids AS ( SELECT id FROM associations WHERE id_type = ? ) SELECT id_revision, type, id_notifier, newValue, oldValue FROM changesets WHERE id_notifier IN ( SELECT id FROM ids ) OR oldValue IN ( SELECT id FROM ids ) OR newValue IN ( SELECT id FROM ids ) ORDER BY id ASC;";
}
