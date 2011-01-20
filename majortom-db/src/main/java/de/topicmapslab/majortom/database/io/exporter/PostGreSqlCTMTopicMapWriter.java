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
package de.topicmapslab.majortom.database.io.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlCTMTopicMapWriter {
	/**
	 * 
	 */
	private static final String SELECT_TO_CTM = "SELECT to_ctm(?);";

	/**
	 * Export the content of the given input stream as CTM
	 * 
	 * @param outputStream
	 *            the output stream
	 * @param store
	 *            the topic map store
	 */
	public static void write(OutputStream outputStream, JdbcTopicMapStore store) {
		if (!SqlDialect.POSTGRESQL.name().equalsIgnoreCase(store.getDialect()) && !SqlDialect.POSTGRESQL99.name().equalsIgnoreCase(store.getDialect())) {
			throw new TopicMapStoreException("Importer instance only supports PostGreSQL database.");
		}

		ISession session = store.openSession();
		try {
			/*
			 * create statement and fill in arguments
			 */
			PreparedStatement stmt = session.getConnection().prepareStatement(SELECT_TO_CTM);
			stmt.setLong(1, store.getTopicMapIdentity().longId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				outputStream.write(rs.getBytes(0));
			}
			outputStream.flush();
		} catch (Exception e) {
			throw new TopicMapStoreException("Import of topic map failed!", e);
		} finally {
			try {
				session.close();
			} catch (SQLException e1) {
				// VOID
			}
		}

	}

	/**
	 * Export the content of the given input stream as CTM
	 * 
	 * @param file
	 *            the file
	 * @param store
	 *            the topic map store
	 */
	public static void write(File file, JdbcTopicMapStore store) {
		if (!SqlDialect.POSTGRESQL.name().equalsIgnoreCase(store.getDialect()) && !SqlDialect.POSTGRESQL99.name().equalsIgnoreCase(store.getDialect())) {
			throw new TopicMapStoreException("Importer instance only supports PostGreSQL database.");
		}
		try {
			write(new FileOutputStream(file), store);
		} catch (FileNotFoundException e) {
			throw new TopicMapStoreException("The file to read cannot be located.", e);
		}
	}
}
