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
package de.topicmapslab.majortom.database.jdbc.postgres.sql99;

import java.sql.Connection;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSSession;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public class SQL99Session extends RDBMSSession {

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
	public SQL99Session(final Sql99ConnectionProvider connectionProvider, String url, String user, String password) {
		super(connectionProvider, url, user, password);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Sql99QueryProcessor createProcessor(Connection connection) throws TopicMapStoreException {
		return new Sql99QueryProcessor(this, connection);
	}

	/**
	 * {@inheritDoc}
	 */
	public Sql99ConnectionProvider getConnectionProvider() {
		return (Sql99ConnectionProvider) super.getConnectionProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	protected Connection openConnection() throws SQLException {
		return getConnectionProvider().getConnection();
	}

}
