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
package de.topicmapslab.majortom.database.jdbc.rdbms;

import java.sql.Connection;

import de.topicmapslab.majortom.database.jdbc.core.SessionImpl;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public class RDBMSSession extends SessionImpl {

	/**
	 * Constructor
	 * 
	 * @param connectionProvider
	 *            the parent connection provider
	 * @param url
	 *            the database URL
	 * @param user
	 *            the user database property
	 * @param password
	 *            the password database property
	 */
	public RDBMSSession(final RDBMSConnectionProvider connectionProvider, String url, String user, String password) {
		super(connectionProvider, url, user, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected RDBMSQueryProcessor createProcessor(Connection connection) throws TopicMapStoreException {
		return new RDBMSQueryProcessor(this, connection);
	}

	/**
	 * {@inheritDoc}
	 */
	public RDBMSConnectionProvider getConnectionProvider() {
		return (RDBMSConnectionProvider) super.getConnectionProvider();
	}

}
