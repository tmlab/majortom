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
package de.topicmapslab.majortom.database.io.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlCTMTopicMapReader {

	/**
	 * 
	 */
	private static final String NEWLINE = "\n";
	/**
	 * 
	 */
	private static final String UTF_8 = "UTF-8";
	/**
	 * 
	 */
	private static final String SELECT_FROM_CTM = "SELECT from_ctm(?,?);";

	/**
	 * Read the content of the given input stream and call the database to read as CTM
	 * 
	 * @param io
	 *            the input stream
	 * @param store
	 *            the topic map store
	 */
	public static void read(InputStream io, JdbcTopicMapStore store) {
		if (!SqlDialect.POSTGRESQL.name().equalsIgnoreCase(store.getDialect()) && !SqlDialect.POSTGRESQL99.name().equalsIgnoreCase(store.getDialect())) {
			throw new TopicMapStoreException("Importer instance only supports PostGreSQL database.");
		}

		ISession session = store.openSession();
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			Scanner scanner = new Scanner(io);
			while (scanner.hasNextLine()) {
				buffer.write(scanner.nextLine().getBytes(UTF_8));
				buffer.write(NEWLINE.getBytes(UTF_8));
			}
			io.close();
			/*
			 * create statement and fill in arguments
			 */
			PreparedStatement stmt = session.getConnection().prepareStatement(SELECT_FROM_CTM);
			stmt.setLong(1, store.getTopicMapIdentity().longId());
			stmt.setString(2, buffer.toString(UTF_8));
			long t = System.currentTimeMillis();
			System.out.println("Sent to db @ " + new SimpleDateFormat().format(new Date(t)));
			stmt.execute();
			System.out.println("Imported after " + (System.currentTimeMillis() - t) + " ms.");
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
	 * Read the content of the given file and call the database to read as CTM
	 * 
	 * @param file
	 *            the file
	 * @param store
	 *            the topic map store
	 */
	public static void read(File file, JdbcTopicMapStore store) {
		if (!SqlDialect.POSTGRESQL.name().equalsIgnoreCase(store.getDialect()) && !SqlDialect.POSTGRESQL99.name().equalsIgnoreCase(store.getDialect())) {
			throw new TopicMapStoreException("Importer instance only supports PostGreSQL database.");
		}
		try {
			read(new FileInputStream(file), store);
		} catch (FileNotFoundException e) {
			throw new TopicMapStoreException("The file to read cannot be located.", e);
		}
	}
}
