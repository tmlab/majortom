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
package de.topicmapslab.majortom.database.jdbc.model;

import java.sql.SQLException;

import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public interface IDatabaseSession {

	/**
	 * Close an existing connection. If there is not an existing exception,
	 * nothing happens.
	 * 
	 * @throws SQLException
	 *             thrown if connection cannot be closed.
	 */
	public void closeConnections() throws SQLException;

	/**
	 * Returns the internal query processor instance, which should be used to
	 * execute queries.
	 * 
	 * @return the query processor
	 * @throws TopicMapStoreException
	 *             thrown if the connection is not established
	 */
	public <T extends IQueryProcessor> T getProcessor() throws TopicMapStoreException;

}
