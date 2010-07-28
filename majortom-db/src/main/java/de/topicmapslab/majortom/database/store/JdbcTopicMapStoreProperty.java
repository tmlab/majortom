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
package de.topicmapslab.majortom.database.store;

import de.topicmapslab.majortom.store.TopicMapStoreProperty;

/**
 * @author Sven Krosse
 * 
 */
public final class JdbcTopicMapStoreProperty {

	private static final String JDBC_PREFIX = TopicMapStoreProperty.PREFIX + ".jdbc";

	public static final String DATABASE_HOST = JDBC_PREFIX + ".host";

	public static final String DATABASE_NAME = JDBC_PREFIX + ".database";

	public static final String DATABASE_PASSWORD = JDBC_PREFIX + ".password";

	public static final String DATABASE_USER = JDBC_PREFIX + ".user";

	public static final String SQL_DIALECT = JDBC_PREFIX + ".dialect";
}
