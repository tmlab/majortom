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
package de.topicmapslab.majortom.database.jdbc.postgres.sql99;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.postgres.base.BasePostGreSqlConnectionProvider;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public class Sql99ConnectionProvider extends BasePostGreSqlConnectionProvider {

	/**
	 * constructor
	 */
	public Sql99ConnectionProvider() {
	}

	/**
	 * Constructor
	 * 
	 * @param host
	 *            the host
	 * @param database
	 *            database
	 * @param user
	 *            the user
	 * @param password
	 *            the password
	 */
	public Sql99ConnectionProvider(String host, String datatbase, String user, String password) {
		super(host, datatbase, user, password);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SQL99Session openSession() {
		return new SQL99Session(this, getUrl(), getUser(), getPassword());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void createSchema() throws SQLException {
		final String query = getSchemaQuery();
		Statement stmt = getGlobalSession().getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		stmt.executeUpdate(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSchemaQuery() {
		InputStream is = getClass().getResourceAsStream("script.sql");
		if (is == null) {
			throw new TopicMapStoreException("Cannot load database schema!");
		}
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner(is);
		while (scanner.hasNextLine()) {
			buffer.append(scanner.nextLine() + "\r\n");
		}
		scanner.close();
		return buffer.toString();
	}
}
