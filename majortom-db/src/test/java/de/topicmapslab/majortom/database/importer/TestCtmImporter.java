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
package de.topicmapslab.majortom.database.importer;

import java.io.File;

import junit.framework.TestCase;

import org.tmapi.core.TopicMapSystemFactory;
import org.tmapix.io.CTMTopicMapReader;

import de.topicmapslab.majortom.database.jdbc.core.SqlDialect;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStore;
import de.topicmapslab.majortom.database.store.JdbcTopicMapStoreProperty;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.store.TopicMapStoreProperty;

/**
 * @author Sven Krosse
 * 
 */
public class TestCtmImporter extends TestCase {

	private ITopicMap tm;

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
		factory.setProperty(TopicMapStoreProperty.TOPICMAPSTORE_CLASS, JdbcTopicMapStore.class.getName());
		factory.setProperty(JdbcTopicMapStoreProperty.DATABASE_HOST, "localhost");
		factory.setProperty(JdbcTopicMapStoreProperty.DATABASE_NAME, "importer-test-db");
		factory.setProperty(JdbcTopicMapStoreProperty.DATABASE_PASSWORD, "postgres");
		factory.setProperty(JdbcTopicMapStoreProperty.DATABASE_USER, "postgres");
		factory.setProperty(JdbcTopicMapStoreProperty.SQL_DIALECT, SqlDialect.POSTGRESQL.name());

		tm = (ITopicMap) factory.newTopicMapSystem().createTopicMap("http://psi.freebase.com/");

	}

	public void testImportFromCtm() throws Exception {
		PostGreSqlCtmImporter.read(new File("src/test/resources/freebase.ctm"), (JdbcTopicMapStore)tm.getStore());
	}
	
	public void testImportFromCtmWithMio() throws Exception {
		CTMTopicMapReader reader = new CTMTopicMapReader(tm, new File("src/test/resources/pnd_small.ctm"));
		reader.read();
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void tearDown() throws Exception {
//		tm.remove();
	}
}
