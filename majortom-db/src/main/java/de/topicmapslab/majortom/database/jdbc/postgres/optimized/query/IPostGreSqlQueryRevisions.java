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
public interface IPostGreSqlQueryRevisions {

	public static final String QUERY_CREATE_REVISION = "INSER INTO revisions(time, id_topicmap) VALUES(now(),?)";
	
	public static final String QUERY_CREATE_CHANGESET = "INSER INTO changesets(id_revision,type,id_notifier, newValue, oldValue,time) VALUES(?,?,?,?,?,now())";

	
}
