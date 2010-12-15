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
package de.topicmapslab.majortom.database.jdbc.mysql;

import java.io.InputStream;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * Special connection provider for PostGreSQL.
 * 
 * @author Sven Krosse
 * 
 */
public class MySqlConnectionProvider extends RDBMSConnectionProvider {

	/**
	 * {@inheritDoc}
	 */
	protected String getDriverClassName() {
		return "com.mysql.jdbc.Driver";
	}

	/**
	 * {@inheritDoc}
	 */
	protected String getRdbmsName() {
		return "mysql";
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public MySqlSession openSession() {
		return new MySqlSession(this, getUrl(), getUser(), getPassword());
	}

	/**
	 * {@inheritDoc}
	 */
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
